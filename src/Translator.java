
import java.io.UnsupportedEncodingException;

import NBT.*;

import java.util.regex.*;

public class Translator {

    /**
     * The nbt data of the program, stored the same way as in the mc64 map (storage:
     * sys PATH: program.cmd)
     */
    TagList program;

    /**
     * 
     * @param src    - the byte version of the source
     * @param format - the extention of the file supported ext are: .nbt - a
     *               structure save of a program .mc64b - a compressed program of
     *               mc64 (using this program, not Zip compression) .mc64 - a
     *               standard mc64 program .mcfunction - an exported program
     *               (exported using this program)
     */
    public Translator(byte[] src, String format) {
        // preparing the program
        program = new TagList();
        // there can be 1024 lines of code max in mc64
        for (int i = 0; i < 1024; i++) {
            TagCompound cmd = new TagCompound();
            cmd.add(new TagInt("cmd", 0));
            cmd.add(new TagInt("mod", 0));
            TagList parameters = new TagList("parameters");
            for (int j = 0; j < 7; j++) {
                TagCompound parameter = new TagCompound();
                parameter.add(new TagInt("type", 0));
                parameter.add(new TagInt("value", 0));
                parameter.add(new TagList("name"));
                parameters.append(parameter);
            }
            cmd.add(parameters);
            program.append(cmd);
        }

        // reading the source
        switch (format) {
            case "nbt":
                System.out.println(".nbt");
                break;
            case "mc64b":
                System.out.println(".mc64b");
                break;
            case "mc64":
                {
                    //create a code string so we can use regex
                    String code = "";
                    try {code = new String(src, "UTF-8");} catch (UnsupportedEncodingException e) { System.err.println(".mc64 files must be encoded in UTF-8"); System.exit(1); }
                    String[] lines = code.split("[\n|\r|\r\n]");
                    if (lines.length > 1024) throw new RuntimeException("The mc64 computer can have 1024 lines of code max");

                    for (int i = 0; i < lines.length; i++) {
                        if (lines[i].isBlank()) continue;
                        try {
                        System.out.println(parseLine(lines[i]));
                        } catch (Exception e) {
                            System.err.println("Error line " + i + ":\n" + "\"" + lines[i] + "\"");
                            System.err.println(e.getMessage());
                        }
                    }
                }
                break;
            case "mcfunction":
                System.out.println(".mcfunction");
                break;
            default:
                throw new RuntimeException(format + " file extension is not supported \n(supported file ext are: .nbt, .mc64b, .mc64 and .mcfunction)");
        }
    }

    private static Pattern pComment = Pattern.compile("//(.*)");

    /**
     * parses a line of code from a .mc64 file and creates a TagCompound that the mc64 computer or this program can understaind
     * 
     * @param line - the line to parse
     * @return the command as an TagCompound
     */
    private static TagCompound parseLine(String line) {
        // setting up the command
        TagCompound cmd = new TagCompound();
        {
            cmd.add(new TagInt("cmd", 0));
            cmd.add(new TagInt("mod", 0));
            TagList parameters = new TagList("parameters");
            for (int j = 0; j < 7; j++) {
                TagCompound parameter = new TagCompound();
                parameter.add(new TagInt("type", 0));
                parameter.add(new TagInt("value", 0));
                parameter.add(new TagList("name"));
                parameters.append(parameter);
            }
            cmd.add(parameters);
        }

        // matcher to use to get the groups of the regex
        Matcher m;

        if ((m = pComment.matcher(line)).matches()) {
            String comment = m.group(1);
            if (comment.length() > 100) throw new RuntimeException("Comments cannot be more than 100 characters");
            cmd.set(new TagInt("cmd", 1));
            cmd.set(new TagInt("type", 2)); //TODO change to parameters.type
            cmd.set(stringToList(comment, "name", 100));
        }
        //TODO else throw exception
        return null;
    }

    private static TagList stringToList(String str, String name, int length) {
        TagList result = new TagList(name);
        for (char c : name.toCharArray()) {
            result.append(new TagString("" + c));
        }
        for (int i = str.length(); i < length; i++) {
            result.append(new TagString(""));
        }
        return result;
    }

}