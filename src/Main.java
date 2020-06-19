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

import java.io.FileOutputStream;
import java.io.BufferedOutputStream;

import java.util.regex.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipException;

import interpreter.Program;
import translation.Translator;


class Main {

    private static String output = "prog.mcfunction";
    private static String mode = "interpret";

    public static void main(String args[]) {
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

            if (trans.isCodeOk()) {
                Program prog = new Program(trans.getProgramAsNbt());
                if (mode.compareTo("interpret") == 0) {
                    //run the program
                    prog.run();
                } else if (mode.compareTo("verify") == 0) {
                    //we already verifyed the program (when creating the object "prog") so exit (just in case we add something after)
                    System.exit(0);
                } else if (mode.compareTo("compile") == 0) {
                    //compile the program
                    File out = new File(output);
                    ext = output.split(".*\\.")[1];
                    if (ext.compareTo("nbt") == 0 || ext.compareTo("mc64b") == 0)
                        try (BufferedOutputStream stream = new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(out)))) {
                            stream.write(trans.convert(ext));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    else
                        try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(out))) {
                            stream.write(trans.convert(ext));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
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