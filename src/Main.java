/*
    This is my first "real" program in Java
    I learnt to program in java on my own so there might (there are) be some problems with my code
    but this code should work with whatever (correct thing) we throw at it

    My english is not the best either sorry :) 
    + I tend to not comment my code (thought I tried to comment as much as I could)
*/

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;

import java.util.regex.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

import translation.Translator;


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
                for (int i = 1; i < args.length; i++) {
                    String arg = args[i];
                    Matcher m;
                    if ((m = pMode.matcher(arg)).find()) { //-mode=
                        mode = m.group(1);
                    } else if ((m = pOut.matcher(arg)).find()) { //-out=
                        output = m.group(1);
                    }
                }
            }

            //read src
            File f = new File(args[0]);
            byte[] src = readFile(f);

            //gets the extention, will crash if there is no extention
            String ext = f.getName().split(".*\\.")[1];

            //get the Translator
            Translator trans = new Translator(src, ext);

            if (mode.compareTo("interpret") == 0) {
                
                
            } else if (mode.compareTo("verify") == 0) {
                
                
                System.exit(0);
            } else if (mode.compareTo("compile") == 0) {

            }

        }
    }

    /**
     * Prints the help to the console (yes, help is README.md)
     */
    private static void showHelp() {
        try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(new File("README.md")))) {
            System.out.println(new String(stream.readAllBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function will read the given file
     * 
     * @param f - the file to read from
     * @return the content of the file
     */
    private static byte[] readFile(File f) {
        //GZIP compressed file
        try (BufferedInputStream stream = new BufferedInputStream(new GZIPInputStream(new FileInputStream(f)))) {
            return stream.readAllBytes();
            //return new String(stream.readAllBytes(), "UTF-8");
        } catch (FileNotFoundException e) {
            System.err.println("The file " + f.getAbsolutePath() + " does not exist");
            System.err.println("if you need help try: mc64 --");
        } catch (ZipException e) {
            //the file is not a GZIP compressed file so continue
        } catch (IOException e) {
            e.printStackTrace();
        }
        //read the normal file
        try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(f))) {
            return stream.readAllBytes();
            //return new String(stream.readAllBytes(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //should never get here (I think) so whatever
        return null;
    }

}