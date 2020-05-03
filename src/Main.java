/*
    This is my first "real" program in Java
    I learnt to program in java on my own so there might (there are) be some problems with my code
    but this code should work with whatever (correct thing) we throw at it

    My english is not the best either sorry :) 
    + I tend to not comment my code (thought I tried to comment as much as I could)
*/

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;

import java.util.regex.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import NBT.*;


class Main {

    private static String output = "prog.mcfunction";
    private static String mode = "interpret";
    private static String version = "1.1.2";

    public static void main(String args[]) {
        //System.out.println(java.util.stream.Stream.of(args).reduce("mc64", (x, y) -> x + " " + y));
        if (args.length == 0) {
            showHelp();
            return;
        }
        else if (args[0].compareTo("help") == 0 || args[0].compareTo("-h") == 0 || args[0].compareTo("--help") == 0) {
            showHelp();
            return;
        } else {
            //read arguments
            {
                Pattern pMode = Pattern.compile("-mode=(interpret|compile|verify)");
                Pattern pOut = Pattern.compile("-out=(.+)");
                Pattern pVersion = Pattern.compile("-v(\\p{Digit}+\\.\\p{Digit}+\\.\\p{Digit}+)");
                for (int i = 1; i < args.length; i++) {
                    String arg = args[i];
                    Matcher m;
                    if ((m = pMode.matcher(arg)).find()) {
                        mode = m.group(1);
                    } else if ((m = pOut.matcher(arg)).find()) {
                        output = m.group(1);
                    }
                    else if ((m = pVersion.matcher(arg)).find()) {
                        version = m.group(1);
                    }
                }
            }

            //read src
            byte[] src = new byte[0];
            File f = new File(args[0]);

            try (BufferedInputStream stream = new BufferedInputStream(new GZIPInputStream(new FileInputStream(f)))) {
                src = stream.readAllBytes();
            } catch (FileNotFoundException e) {
                System.err.println("The file " + args[0] + " does not exist");
                System.err.println("if you need help try: mc64 --");
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(NbtTag.parse(src));

            if (mode.compareTo("interpret") == 0) {
                
                
            } else if (mode.compareTo("verify") == 0) {
                
                
                System.exit(0);
            } else if (mode.compareTo("compile") == 0) {

            }

        }
    }

    private static void showHelp() {
        try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(new File("README.md")))) {
            System.out.println(new String(stream.readAllBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}