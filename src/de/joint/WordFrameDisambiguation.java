// under development
package de.joint;

import de.joint.frames.wfd.data.ProfileEvaluation;
import java.io.IOException;

public class WordFrameDisambiguation {

    public static void main(String[] args) throws IOException {
        printHeader();
        if (args == null || args.length < 3) {
            printUsage();
            System.exit(0);
        }
        String profilename = args[0];
        String method = args[1];
        String norm = args[2];
        ProfileEvaluation.WFD(profilename, method, norm);
    }

    public static void printHeader() {
        System.out.println("LOaDing: WordFrameDisambiguation v 1.0");
        System.out.println("http://web.informatik.uni-mannheim.de/joint/");
    }

    public static void printUsage() {

        System.out.println("wfd.sh <profilename> <method> <norm>");
        System.out.println("<profilename> one from: ");
        System.out.println("      - Framester: fn2bnBase, fn2bnDirectX, fn2bnFprofile, fn2bnFrameBase, fn2bnTransX, fn2bnXWFN");
        System.out.println("      - LOaDing: ddt-wiki-n30-1400k-Base, ddt-wiki-n30-1400k-DirectX, ddt-wiki-n30-1400k-Fprofile, ddt-wiki-n30-1400k-Base, ddt-wiki-n30-1400k-TransX, ddt-wiki-n30-1400k-XWFN");
        System.out.println("<method> one from:");
        System.out.println("      - oracle");
        System.out.println("      - top-1");
        System.out.println("<norm> one from:");
        System.out.println("      - cond");
        System.out.println("      - invnorm");
    }
}
