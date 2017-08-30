// under development

package de.joint;
import java.io.IOException;


public class FrameParser {

    public static void main(String[] args) throws IOException {
        printHeader();
        /*if (args == null || args.length < 3) {
            printUsage();
            System.exit(0);
        }
        String dataFolder = args[0];
        String csvFilename = args[1];
        int iterations = new Integer(args[2]);
        System.out.println("Mapping the monosemous senses");
        MonosemousMappingToBabelNet.map(dataFolder, csvFilename);
        System.out.println("Mapping the polysemous senses");
        for (int iteration = 1; iteration < iterations; iteration++) {
            IterativeMappingToBabelNet.map(dataFolder, csvFilename, iteration);
        }*/

    }

    public static void printHeader() {

        System.out.println("DDTs FrameParser v 1.0");
    }

    public static void printUsage() 
    {

/*        System.out.println("linker.sh <datafolder> <csvfilename> <numofiterations>");
        System.out.println("<datafolder> the path containing the ddt in the csv format;");
        System.out.println("<csvfilename> the filename of the ddt in the csv format;");
        System.out.println("<numofiterations> the number of iterations (integer value);");*/
    }
}
