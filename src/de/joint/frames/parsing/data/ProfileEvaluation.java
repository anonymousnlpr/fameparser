/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.joint.frames.parsing.data;
import com.google.common.collect.HashMultimap;
import it.uniroma1.lcl.jlt.util.DoubleCounter;
import it.uniroma1.lcl.jlt.util.Files;
import java.io.*;
import java.util.*;
/**
 *
 * @author sfaralli
 */
public class ProfileEvaluation 
{
    
    private final static String framenetFullAnnotatedTextsFolder="/home/sfaralli/Scrivania/progetti/JOINT-T/FRAMES/FRAMENET/fndata-1.7/fulltext/";
    private final static String framenetFullAnnotatedDisambiguatedTextsFolder="/home/sfaralli/Scrivania/progetti/JOINT-T/FRAMES/FRAMENET/fndata-1.7/fulltextdisambiguated/";
    private final static String framenetFullAnnotatedFlatTextsFolder="/home/sfaralli/Scrivania/progetti/JOINT-T/FRAMES/FRAMENET/fndata-1.7/fulltextflat/";    
    private final static String framenetFullAnnotatedEvaluationsFolder="/home/sfaralli/Scrivania/progetti/JOINT-T/FRAMES/FRAMENET/fndata-1.7/fulltextresults/";    
    private final static String profilesFolder="/home/sfaralli/Scrivania/progetti/JOINT-T/FRAMES/FRAMESTER/";    
    private final static String disambiguationSystem="BFy";
    //private final static String disambiguationSystem="UKB";
    private static String keyword="Base";
  
    //private static String keyword="XWFN";
    //private static String keyword="TransX";
    //private static String keyword="DirectX";
    //private static String keyword="Fprofile";
    //private static String keyword="FrameBase";
    
    
    
 //  public static final String dd ="ddt-wiki-n30-1400k-closure";
 //public static final String dd ="ddt-wiki-n200-380k-closure";
 public static final String dd="ddt-news-n50-485k-closure";
 //public static final String dd="ddt-news-n200-345k-closure";
    
    
     //private static String method="all";
     
 private static String method="top_1";
  //private static String method="rnd";
   
  //private static String norm="norm";
   //private static String norm="nonorm";
   private static String norm="invnorm";
   
   // all va ripesato 1/k
   // vediamo se facendo finta che gli FP si incrementano solo di uno
   
   
    private static HashMap<String,DoubleCounter<String>> profile;
     
    public static void main(String[] args) throws IOException
    {
   String profilename=dd+"_"+keyword;
     //String profilename="fn2bn"+keyword;
        
        String profilefullname=profilesFolder+profilename+".ttl.txt";
        
        //load profile
        profile=new HashMap<String,DoubleCounter<String>>();
        BufferedReader pr=Files.getBufferedReader(profilefullname);
        while (pr.ready())
        {
            String line=pr.readLine();
            if (line.startsWith("frame:"))
            {
                String[] lines=line.split("skos:closeMatch");
                String framename=lines[0].replace("frame:", "").trim();
                profile.put(framename, new DoubleCounter<String>());
                if (lines.length==2&&!lines[1].trim().isEmpty())
                {
                    String[] syns=lines[1].trim().split(",");
                    for (String syn:syns)
                    {
                        String[] synparts=syn.split(":");
                        Double score=1.0;
                        if (synparts.length==3)
                            score=new Double(synparts[2]);
                        if (!synparts[1].startsWith("s"))
                            profile.get(framename).count(synparts[0].trim()+":s"+synparts[1].trim(),score);
                        else
                            profile.get(framename).count(synparts[0].trim()+":"+synparts[1].trim(),score);
                     }
                
                }
            }
        
        }
        pr.close();
        File dir=new File(framenetFullAnnotatedTextsFolder);
        //  global counters
            int totAnnotations=0;
            int totManual=0;
            int totUnannotated=0;
            int totSentences=0;
            int totParagraphs=0;
            int totDisambiguated=0;
            int totDoc=0;
        
            int totTP=0;
            int totTN=0;
            int totFP=0;
            int totFN=0;
            
            
        for (String filename:dir.list())    
        {
            if (!filename.endsWith(".xml")) continue;
            System.out.println("Processing annotated document:"+filename);
            //read the disambiguated document
            String disambiguatedfilename=framenetFullAnnotatedDisambiguatedTextsFolder+disambiguationSystem+"/"+filename+".tsv";
            HashMultimap<String,BFyAnnotation> sentenceid2senses;
            sentenceid2senses = HashMultimap.create();
            
            if (!new File(disambiguatedfilename).exists()) continue;
            System.out.println("Processing disambuguated document:"+disambiguatedfilename);
            // read the disambiguations
            BufferedReader dr=Files.getBufferedReader(disambiguatedfilename);
            dr.readLine();
            while(dr.ready())
            {
            //corpID	docID	sentNo	paragNo	aPos	ID	text	tokenFragment_start	tokenFragment_end	charSegment_start	charSegment_end	babelSynsetID	DBpediaURL	BabelNetURL	score	coherenceScore	globalScore	source
            String line=dr.readLine();
            String[] fields=line.split("\t");
            String sentNo=fields[2];
            String parNo=fields[3];
            String tokenFragment_start=fields[7];	
            String tokenFragment_end=fields[8];	
            String charSegment_start=fields[9];	
            String charSegment_end=fields[10];
            String babelSynsetID=fields[11];	
            String DBpediaURL=fields[12];
            String BabelNetURL=fields[13];	
            String score=fields[14];
            String coherenceScore=fields[15];
            String globalScore=fields[16];
            String source=fields[17];    
            BFyAnnotation ba=new BFyAnnotation(
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
            sentenceid2senses.put(parNo+"-"+sentNo, ba);
            }
            dr.close();
            //  global counters
            int docAnnotations=0;
            int docManual=0;
            int docUnannotated=0;
            int docDisambiguated=0;
            int docOverlapping=0;
            int docTP=0;
            int docTN=0;
            int docFP=0;
            int docFN=0;

            BufferedWriter bw=Files.getBufferedWriter(framenetFullAnnotatedEvaluationsFolder+disambiguationSystem+"/"+method+"/"+filename+"_"+profilename+"_"+norm+".tsv");

            
            // open the annotated gold standard
            BufferedReader br = Files.getBufferedReader(framenetFullAnnotatedFlatTextsFolder+filename+".tsv");
            br.readLine();
            Set<String> sentencIDs=new HashSet<String>();
            Set<String> paragraphIDS=new HashSet<String>();
            while (br.ready())
            {
                //format
                //corpID	docID	sentNo	paragNo	aPos	ID	text	luID	luName	frameID	frameName	status	aID	start	end	cBy	target            
                String line=br.readLine();
                String[] fields = line.split("\t");
                docAnnotations++;
                
                sentencIDs.add(fields[3]+"-"+fields[2]);
                paragraphIDS.add(fields[3]);
                String frameName=fields[10]; //gold standard annotated frame
                if (!fields[11].equals("UNANN")) 
                {
                    int start=0;
                    int end=0;
                    
                    start=new Integer(fields[13]);
                    end=new Integer(fields[14]);
                    docManual++;
                    
                    
                    // devo prendere la disambiguazione che matcha start end char dal documento disambiguato
                    Collection<BFyAnnotation> linked=sentenceid2senses.get(fields[3]+"-"+fields[2]);
                    if (linked.isEmpty())
                    {
                        docFN++;
                    }
                    else
                    {
                         boolean found=false;
                        // devo verificare che ci sia l'overlap
                        for (BFyAnnotation annot:linked)
                        {
                         if (annot.getCharSegment_start()==start && annot.getCharSegment_end()==end)
                         { 
                             docOverlapping++;
                             String disambiguatedtarget=annot.getBabelSynsetID().replace("bn:","bn:s");
                             // rank of entity
                             DoubleCounter<String> rank=new DoubleCounter<String>();
                             
                             for (String fn:profile.keySet())
                             {
                                if (profile.get(fn).keySet().contains(disambiguatedtarget))
                                    if (!norm.equals("nonorm"))
                                    {
                                        if (norm.equals("norm"))
                                            rank.count(fn,profile.get(fn).get(disambiguatedtarget)/profile.get(fn).keySet().size());
                                        else
                                            rank.count(fn,profile.get(fn).keySet().size()/profile.get(fn).get(disambiguatedtarget));
                                    }
                                else
                                  rank.count(fn,profile.get(fn).get(disambiguatedtarget));
                             }
                             if (rank.keySet().size()!=0) found=true;
                             Set<String> selected=new HashSet<String>();
                             if (rank.keySet().size()!=0)
                             {
                             switch(method)
                             {
                                 case "top_1" :  
                                     selected.addAll(rank.getTopK(1));
                                     break;
                                 case "all"  :selected.addAll(rank.keySet()); break;
                                 case "rnd"  : 
                                     
                                     ArrayList<String> chiavi=new ArrayList<String>();
                                     for (String k:rank.keySet()) chiavi.add(k);
                                     int index=(int)((double)Math.random()*(double)(chiavi.size()-1)); 
                                     try
                                     {
                                     selected.add(chiavi.get(index));
                                     }catch(Exception e)
                                     {
                                     
                                     System.out.println("e");
                                     }
                                     break;
                                 default: break;
                             }
                               for (String s:selected)
                               {
                                    if (s.equals(frameName)) docTP++;
                                    else docFP++;
                               }
                             }
                         }
                         
                        }
                        if (found==false) docFN++;
                        else docDisambiguated++;
                    }
                    
                    
                }
                else  
                {
                    docTN++;
                    docUnannotated++;
                }
                
                
                
                
                // valutare tp,fp,tn,fn
                
                
                
                
                
                
                
            }
            br.close();
            bw.write("filename\t"+filename+"\n");
            bw.write("docAnnotations\t"+docAnnotations+"\n");
            bw.write("docManual\t"+docManual+"\n");
            bw.write("docUnannotated\t"+docUnannotated+"\n");
            bw.write("sentences\t"+sentencIDs.size()+"\n");
            bw.write("paragraphs\t"+paragraphIDS.size()+"\n");
            bw.write("Overlapping\t"+docOverlapping+"\n");
            bw.write("Disambiguated\t"+docDisambiguated+"\n");
            bw.write("TP\t"+docTP+"\n");
            bw.write("TN\t"+docTN+"\n");
            bw.write("FP\t"+docFP+"\n");
            bw.write("FN\t"+docFN+"\n");
            bw.write("P\t"+precision(docTP,docTN,docFP,docFN)+"\n");
            bw.write("R\t"+recall(docTP,docTN,docFP,docFN)+"\n");
            bw.write("F\t"+f1_measure(docTP,docTN,docFP,docFN)+"\n");
            bw.write("A\t"+accuracy(docTP,docTN,docFP,docFN)+"\n");
            bw.close();
            totAnnotations+=docAnnotations;
            totManual+=docManual;
            totUnannotated+=docUnannotated;
            totSentences+=sentencIDs.size();
            totParagraphs+=paragraphIDS.size();
            totDisambiguated+=docDisambiguated;
            totDoc++;
            totTP+=docTP;
            totTN+=docTN;
            totFP+=docFP;
            totFN+=docFN;
            System.out.println("TP:"+totTP);
            System.out.println("TN:"+totTN);
            System.out.println("FP:"+totFP);
            System.out.println("FN:"+totFN);
           // System.exit(0);
        }
                
    }
    public static double precision(int TP,int TN,int FP,int FN)
    {
        if (TP+FP==0) return 0.0;
        double result=(double)TP/(double)(TP+FP);
        return result;
    }
    public static double recall(int TP,int TN,int FP,int FN)
    {
        if (TP+FN==0) return 0.0;
        double result=(double)TP/(double)(TP+FN);
        return result;
    }
    public static double accuracy(int TP,int TN,int FP,int FN)
    {
        if (TP+TN+FP+FN==0) return 0.0;
        double result=(double)(TP+TN)/(double)(TP+TN+FP+FN);
        return result;
    }
    public static double f1_measure(int TP,int TN,int FP,int FN)
    {
        double P=precision(TP,TN,FP,FN);
        double R=recall(TP,TN,FP,FN);
        if (P+R==0.0) return 0.0;
        double result=2.0*((double)(P*R)/(double)(P+R));
        return result;
    }
}
