package de.joint.frames.wfd.data;

import com.google.common.collect.HashMultimap;
import it.uniroma1.lcl.jlt.util.DoubleCounter;
import it.uniroma1.lcl.jlt.util.Files;
import it.uniroma1.lcl.jlt.util.IntegerCounter;
import java.io.*;
import java.util.*;

/**
 *
 * @author sfaralli
 */
public class ProfileEvaluation {

    private final static String framenetFullAnnotatedTextsFolder = "data/testset/fulltext/";
    private final static String framenetFullAnnotatedFlatTextsFolder = "data/testset/fulltextflat/";
    private final static String framenetFullAnnotatedDisambiguatedTextsFolder = "data/testset/fulltextdisambiguated/";
    private final static String framenetFullAnnotatedEvaluationsFolder = "data/testset/fulltextresults/";
    private final static String profilesFolder = "data/profiles/";

    private final static String disambiguationSystem = "BFy";

    public static void WFD(String profilename, String method, String norm) throws IOException {

        String profilefullname = profilesFolder + profilename + ".ttl.txt";

        //load profile
        HashMap<String, DoubleCounter<String>> profile = new HashMap<>();
        HashMap<String, DoubleCounter<String>> pprofile = new HashMap<>();

        //load profile
        BufferedReader pr = Files.getBufferedReader(profilefullname);
        while (pr.ready()) {
            String line = pr.readLine();
            if (line.startsWith("frame:")) {
                String[] lines = line.split("skos:closeMatch");
                String framename = lines[0].replace("frame:", "").trim();
                pprofile.put(framename, new DoubleCounter<String>());
                if (lines.length == 2 && !lines[1].trim().isEmpty()) {
                    String[] syns = lines[1].replace(".", "").trim().split(",");
                    for (String syn : syns) {
                        String[] synparts = syn.split(":");
                        Double score = 1.0;
                        if (synparts.length == 3) {
                            score = new Double(synparts[2]);
                        }
                        if (!synparts[1].startsWith("s")) {
                            pprofile.get(framename).count(synparts[0].trim() + ":s" + synparts[1].trim(), score);
                        } else {
                            pprofile.get(framename).count(synparts[0].trim() + ":" + synparts[1].trim(), score);
                        }
                    }

                }
            }

        }
        pr.close();

        //use probabilities
        for (String framename : pprofile.keySet()) {
            DoubleCounter<String> dc = pprofile.get(framename);
            DoubleCounter<String> ndc = new DoubleCounter<String>();
            for (String s : dc.keySet()) {
                Double probability = dc.getProbability(s);
                {
                    ndc.count(s, probability);
                }
            }
            profile.put(framename, ndc);
        }

        File dir = new File(framenetFullAnnotatedTextsFolder);
        //  global counters
        int totAnnotations = 0;
        int totTP = 0;
        int totTN = 0;
        int totFP = 0;
        int totFN = 0;
        int totCandidates = 0;
        IntegerCounter<Integer> polysemydistribution = new IntegerCounter<Integer>();
        for (String filename : dir.list()) {
            if (!filename.endsWith(".xml")) {
                continue;
            }
            System.out.println("Processing annotated document:" + filename);
            //read the disambiguated document
            String disambiguatedfilename = framenetFullAnnotatedDisambiguatedTextsFolder + disambiguationSystem + "/" + filename + ".tsv";
            HashMultimap<String, BFyAnnotation> sentenceid2senses = HashMultimap.create();

            if (!new File(disambiguatedfilename).exists()) {
                continue;
            }
            System.out.println("Processing disambuguated document:" + disambiguatedfilename);
            // read the disambiguations
            BufferedReader dr = Files.getBufferedReader(disambiguatedfilename);
            dr.readLine();
            while (dr.ready()) {
                //corpID	docID	sentNo	paragNo	aPos	ID	text	tokenFragment_start	tokenFragment_end	charSegment_start	charSegment_end	babelSynsetID	DBpediaURL	BabelNetURL	score	coherenceScore	globalScore	source
                String line = dr.readLine();
                String[] fields = line.split("\t");
                String sentNo = fields[2];
                String parNo = fields[3];
                String tokenFragment_start = fields[7];
                String tokenFragment_end = fields[8];
                String charSegment_start = fields[9];
                String charSegment_end = fields[10];
                String babelSynsetID = fields[11];
                String DBpediaURL = fields[12];
                String BabelNetURL = fields[13];
                String score = fields[14];
                String coherenceScore = fields[15];
                String globalScore = fields[16];
                String source = fields[17];
                BFyAnnotation ba = new BFyAnnotation(
                        new Integer(tokenFragment_start),
                        new Integer(tokenFragment_end),
                        new Integer(charSegment_start),
                        new Integer(charSegment_end),
                        babelSynsetID,
                        DBpediaURL,
                        BabelNetURL,
                        new Double(score),
                        new Double(coherenceScore),
                        new Double(globalScore),
                        source
                );
                sentenceid2senses.put(parNo + "-" + sentNo, ba);
            }
            dr.close();
            //  global counters
            int docAnnotations = 0;
            int docTP = 0;
            int docTN = 0;
            int docFP = 0;
            int docFN = 0;

            BufferedWriter bw = Files.getBufferedWriter(framenetFullAnnotatedEvaluationsFolder + disambiguationSystem + "/" + method + "/" + filename + "_" + profilename + "_" + norm + ".tsv");

            // open the annotated gold standard
            BufferedReader br = Files.getBufferedReader(framenetFullAnnotatedFlatTextsFolder + filename + ".tsv");
            br.readLine();
            Set<String> sentencIDs = new HashSet<>();
            Set<String> paragraphIDS = new HashSet<>();
            while (br.ready()) {
                //format
                //corpID	docID	sentNo	paragNo	aPos	ID	text	luID	luName	frameID	frameName	status	aID	start	end	cBy	target            
                String line = br.readLine();
                String[] fields = line.split("\t");
                docAnnotations++;
                totAnnotations++;
                sentencIDs.add(fields[3] + "-" + fields[2]);
                paragraphIDS.add(fields[3]);
                String frameName = fields[10]; //gold standard annotated frame
                if (!fields[11].equals("UNANN")) {
                    int start = 0;
                    int end = 0;

                    start = new Integer(fields[13]);
                    end = new Integer(fields[14]);
                    // devo prendere la disambiguazione che matcha start end char dal documento disambiguato
                    Collection<BFyAnnotation> linked = sentenceid2senses.get(fields[3] + "-" + fields[2]);
                    if (linked.isEmpty()) {
                        docFN++;
                    } else {
                        boolean found = false;
                        Set<BFyAnnotation> slinks = new HashSet<>();
                        for (BFyAnnotation annot : linked) {
                            if (annot.getCharSegment_start() == start && annot.getCharSegment_end() == end) {
                                slinks.add(annot);
                                String disambiguatedtarget = annot.getBabelSynsetID().replace("bn:", "bn:s");

                                // rank of entities
                                DoubleCounter<String> rank = new DoubleCounter<>();

                                for (String fn : profile.keySet()) {
                                    if (profile.get(fn).keySet().contains(disambiguatedtarget)) {
                                        switch (norm) {

                                            case "invnorm":
                                                rank.count(fn, (double) profile.get(fn).keySet().size() / (double) profile.get(fn).get(disambiguatedtarget));
                                                break;
                                            case "conditional":
                                                Double totalW = profile.get(fn).getTotal();
                                                rank.count(fn, (double) profile.get(fn).get(disambiguatedtarget) / totalW);
                                                break;
                                            default:
                                                break;
                                        }
                                    }

                                }

                                totCandidates += rank.keySet().size();
                                polysemydistribution.count(rank.keySet().size());
                                if (!rank.keySet().isEmpty()) {
                                    found = true;
                                }
                                Set<String> selected = new HashSet<>();
                                if (!rank.keySet().isEmpty()) {
                                    switch (method) {
                                        case "top_1":
                                            selected.addAll(rank.getTopK(1));
                                            break;
                                        case "oracle":
                                            if (rank.keySet().contains(frameName)) {
                                                selected.add(frameName);
                                            }
                                            break;

                                        default:
                                            break;
                                    }
                                    for (String s : selected) {
                                        if (s.equals(frameName)) {
                                            docTP++;
                                        } else {
                                            docFP++;
                                        }
                                    }
                                }
                                bw.write("SILVER:" + line + "\n");
                                bw.write("LINKED:" + slinks + "\n");
                                bw.write("RANK:" + rank + "\n");
                                bw.write("SELECTED:" + selected + "\n");
                                boolean ok = false;
                                for (String s : selected) {
                                    if (s.equals(frameName)) {
                                        ok = true;
                                    }

                                }
                                if (ok) {
                                    bw.write("MATCH!\n");
                                } else {
                                    bw.write("WRONG!\n");
                                }
                            }

                        }
                        if (found == false) {
                            docFN++;
                        } else {
                            //docDisambiguated++;
                        }
                    }

                } else {
                    docTN++;
                    //docUnannotated++;
                }
            }
            br.close();
            bw.write("filename\t" + filename + "\n");
            bw.write("TP\t" + docTP + "\n");
            bw.write("TN\t" + docTN + "\n");
            bw.write("FP\t" + docFP + "\n");
            bw.write("FN\t" + docFN + "\n");
            bw.write("P\t" + precision(docTP, docTN, docFP, docFN) + "\n");
            bw.write("R\t" + recall(docTP, docTN, docFP, docFN) + "\n");
            bw.write("F\t" + f1_measure(docTP, docTN, docFP, docFN) + "\n");
            bw.write("A\t" + accuracy(docTP, docTN, docFP, docFN) + "\n");
            bw.close();
            totAnnotations += docAnnotations;
            totTP += docTP;
            totTN += docTN;
            totFP += docFP;
            totFN += docFN;
            System.out.println("TP:" + totTP);
            System.out.println("TN:" + totTN);
            System.out.println("FP:" + totFP);
            System.out.println("FN:" + totFN);
            System.out.println("TOTCANDIDATES:" + totCandidates);
            System.out.println("0\t1\t2\t3\t4\t5\t6\t7\t8\t9");
            for (int i = 0; i < 10; i++) {
                int value = 0;
                if (polysemydistribution.keySet().contains(i)) {
                    value = polysemydistribution.get(i);
                }
                System.out.print(value + "\t");

            }
            System.out.print("\n");
            System.out.print("Ovwerlapping annotations:" + totAnnotations);
        }

    }

    public static double precision(int TP, int TN, int FP, int FN) {
        if (TP + FP == 0) {
            return 0.0;
        }
        double result = (double) TP / (double) (TP + FP);
        return result;
    }

    public static double recall(int TP, int TN, int FP, int FN) {
        if (TP + FN == 0) {
            return 0.0;
        }
        double result = (double) TP / (double) (TP + FN);
        return result;
    }

    public static double accuracy(int TP, int TN, int FP, int FN) {
        if (TP + TN + FP + FN == 0) {
            return 0.0;
        }
        double result = (double) (TP + TN) / (double) (TP + TN + FP + FN);
        return result;
    }

    public static double f1_measure(int TP, int TN, int FP, int FN) {
        double P = precision(TP, TN, FP, FN);
        double R = recall(TP, TN, FP, FN);
        if (P + R == 0.0) {
            return 0.0;
        }
        double result = 2.0 * ((double) (P * R) / (double) (P + R));
        return result;
    }
}
