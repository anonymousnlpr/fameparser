/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.joint.frames.parsing.data;


import it.uniroma1.lcl.jlt.util.Files;
import java.io.*;
/**
 *
 * @author sfaralli
 */

public class Framenet2WSD
{
    private final static String framenetFullAnnotatedTextsFolder="/home/sfaralli/Scrivania/progetti/JOINT-T/FRAMES/FRAMENET/fndata-1.7/fulltext/";
    private final static String framenetFullAnnotatedDisambiguatedTextsFolder="/home/sfaralli/Scrivania/progetti/JOINT-T/FRAMES/FRAMENET/fndata-1.7/fulltextdisambiguated/";
    private final static String disambiguationSystem="BFy";
    //private final static String disambiguationSystem="UKB";
    public static void main(String args[]) throws IOException, Exception
    {
        File sourcedir=new File(framenetFullAnnotatedTextsFolder);
        for (String filename:sourcedir.list())
        {
            if (!filename.endsWith("xml")) continue;
            System.out.println("Processing: "+filename);
            System.out.println("Creating: "+disambiguationSystem+"/"+filename+".tsv");
            if (new File(framenetFullAnnotatedDisambiguatedTextsFolder+disambiguationSystem+"/"+filename+".tsv").exists()) continue;
            
            BufferedReader br = Files.getBufferedReader(framenetFullAnnotatedTextsFolder+filename);
            BufferedWriter bw = Files.getBufferedWriter(framenetFullAnnotatedDisambiguatedTextsFolder+disambiguationSystem+"/"+filename+".tsv");
            bw.write("corpID\tdocID\tsentNo\tparagNo\taPos\tID\ttext\ttokenFragment\tstart\ttokenFragment\tend\tcharSegment\tstart\tcharSegment\tend\tbabelSynsetID\tDBpediaURL\tBabelNetURL\tscore\tcoherenceScore\tglobalScore\tsource\n");
            String corpID="";
            String docID="";
            String sentNo="";
            String paragNo="";
            String aPos="";
            String ID="";
            while (br.ready())
            {
               String line=br.readLine().trim();
               
               if (line.startsWith("<sentence"))
               {
                // <sentence corpID="195" docID="23791" sentNo="1" paragNo="1" aPos="0" ID="4106338">
                   line=line.replace("<sentence","").replace(">","").replace("\"","").trim();
                   String[] arguments=line.split(" ");
                   for (String ar:arguments)
                   {
                       String[] ars=ar.split("=");
                       switch (ars[0]) {
                           case "corpID":
                               corpID=ars[1];
                               break;
                           case "docID":
                               docID=ars[1];
                               break;
                           case "sentNo":
                               sentNo=ars[1];
                               break;
                           case "paragNo":
                               paragNo=ars[1];
                               break;
                           case "aPos":
                               aPos=ars[1];
                               break;
                           case "ID":
                               ID=ars[1];
                               break;
                       }
                       
                   }
                   
               }else
               if (line.startsWith("<text"))
               {
                   line=line.replace("<text>","").replace("</text>","");
                   for (BFyAnnotation bfa:BabelFy.parse(BabelFy.babelfy(line)))
                    bw.write(corpID+"\t"+docID+"\t"+sentNo+"\t"+paragNo+"\t"+aPos+"\t"+ID+"\t"+line+"\t"+bfa.toString()+"\n");
               }
            }
            br.close();
            bw.close();
            System.exit(0);
        }
    
    }
    
}
