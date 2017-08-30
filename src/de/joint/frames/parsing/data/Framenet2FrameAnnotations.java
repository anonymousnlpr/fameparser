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

public class Framenet2FrameAnnotations
{
    private final static String framenetFullAnnotatedTextsFolder="/home/sfaralli/Scrivania/progetti/JOINT-T/FRAMES/FRAMENET/fndata-1.7/fulltext/";
    private final static String framenetFullAnnotatedFlatTextsFolder="/home/sfaralli/Scrivania/progetti/JOINT-T/FRAMES/FRAMENET/fndata-1.7/fulltextflat/";
    public static void main(String args[]) throws IOException, Exception
    {
        File sourcedir=new File(framenetFullAnnotatedTextsFolder);
        for (String filename:sourcedir.list())
        {
            if (!filename.endsWith("xml")) continue;
            System.out.println("Processing: "+filename);
            System.out.println("Creating: "+filename+".tsv");
            
            BufferedReader br = Files.getBufferedReader(framenetFullAnnotatedTextsFolder+filename);
            BufferedWriter bw = Files.getBufferedWriter(framenetFullAnnotatedFlatTextsFolder+"/"+filename+".tsv");
            /*******/
            // sentence
            /******/            
            String corpID="";
            String docID="";
            String sentNo="";
            String paragNo="";
            String aPos="";
            String ID="";
            String text="";
            /*******/
            // annotations
            /******/            
            String luID="";
            String luName="";
            String frameID="";
            String frameName="";
            String status="";
            String aID="";
            /*******/
            // target
            /******/            
            String cBy="";
            String end="";
            String start="";
            String target="";
            bw.write("corpID\tdocID\tsentNo\tparagNo\taPos\tID\ttext\tluID\tluName\tframeID\tframeName\tstatus\taID\tstart\tend\tcBy\ttarget\n");
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
                          default:break;
                       }
                       
                   }
                   
               }else
               if (line.startsWith("<text"))
               {
                   text=line.replace("<text>","").replace("</text>","");

               }
               else
               if (line.startsWith("<annotationSet"))
               {
              // luID="11606" luName="chemical.n" frameID="1321" frameName="Substance" status="MANUAL" ID="6531972"
                   
                   luID="null";
                   luName="null";
                   frameID="null";
                   frameName="null";
                line=line.replace("<annotationSet","").replace(">","").replace("\"","").trim();
                   String[] arguments=line.split(" ");
                   for (String ar:arguments)
                   {
                       String[] ars=ar.split("=");
                       switch (ars[0]) {
                           case "luID":
                               luID=ars[1];
                               break;
                           case "luName":
                               luName=ars[1];
                               break;
                           case "frameID":
                               frameID=ars[1];
                               break;
                           case "frameName":
                               frameName=ars[1];
                               break;
                           case "status":
                               status=ars[1];
                               break;
                           case "ID":
                               aID=ars[1];
                               break;
                          default:break;
                       }
                       
                   }
                   if (status.equals("UNANN"))
                   {
                   bw.write(corpID+"\t"+docID+"\t"+sentNo+"\t"+paragNo+"\t"+aPos+"\t"+ID+"\t"+text+"\t"+luID+"\t"+luName+"\t"+frameID+"\t"+frameName+"\t"+status+"\t"+aID+"\tnull\tnull\tnull\tnull\n");
                   
                   }
               
               }else
                 if (line.startsWith("<label"))
                    {
                        
                    if (!line.contains("name=\"Target\"")) continue;   
                   line=line.replace("<label","").replace("/>","").replace("\"","").trim();
                   String[] arguments=line.split(" ");
                   for (String ar:arguments)
                   {
                       String[] ars=ar.split("=");
                       switch (ars[0]) {
                           case "cBy":
                               cBy=ars[1];
                               break;
                           case "end":
                               end=ars[1];
                               break;
                           case "start":
                               start=ars[1];
                               break;
                          default:break;
                       }
                   }
                   target=text.substring(new Integer(start),new Integer(end)+1);
                   bw.write(corpID+"\t"+docID+"\t"+sentNo+"\t"+paragNo+"\t"+aPos+"\t"+ID+"\t"+text+"\t"+luID+"\t"+luName+"\t"+frameID+"\t"+frameName+"\t"+status+"\t"+aID+"\t"+start+"\t"+end+"\t"+cBy+"\t"+target+"\n");
                     
              }
            }
            br.close();
            bw.flush();
            bw.close();
           // System.exit(0);
        }
    
    
    }
}
