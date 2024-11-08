package translation;

import java.io.UnsupportedEncodingException;

import NBT.*;
import java.util.List;
import java.util.ArrayList;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.File;

import java.util.regex.*;

public class Translator {

    /**
     * The nbt data of the program, stored the same way as in the mc64 map (storage:
     * sys PATH: program.cmd)
     */
    private TagList program;
    private boolean ok = true;

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
        //setting up the parser
        if (!setupDone)
            setup();

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
        switch (format) { //TODO add other read
            case "nbt": // reading structure file
                this.program = (TagList)NbtTag.parse(src).get("blocks.0.nbt.Items.0.tag.program");
                break;
            case "mc64b": // reading from the mc64 program in nbt format
                try {
                    this.program = (TagList)NbtTag.parse(src);
                } catch (Exception e) {
                    System.err.println("Program in mc64b format must be a TagList");
                    System.exit(1);
                }
                break;
            case "mc64": //reading from mc64 code
                {
                    //create a code string so we can use regex
                    String code = "";
                    try {code = new String(src, "UTF-8");} catch (UnsupportedEncodingException e) { System.err.println(".mc64 files must be encoded in UTF-8"); System.exit(1); }
                    String[] lines = code.split("(\r\n|\n|\r)");
                    if (lines.length > 1024) throw new RuntimeException("The mc64 computer can have 1024 lines of code max");
                    for (int i = 0; i < lines.length; i++) {
                        lines[i] = lines[i].trim();
                        if (lines[i].isBlank()) continue;
                        try {
                            program.set("" + i, parseLine(lines[i]));
                        } catch (Exception e) {
                            System.err.println("Error line " + (i + 1) + ":\n" + "\"" + lines[i] + "\"");
                            System.err.println(e.getMessage());
                            ok = false;
                        }
                    }
                }
                break;
            case "dat": //reading from command_storage_minecraft.dat (directly from storage memory)
                System.out.println(".mcfunction");
                break;
            default:
                throw new RuntimeException(format + " file extension is not supported \n(supported file ext are: .nbt, .mc64b, .mc64 and .mcfunction)");
        }
    }


    /**
     * parses a line of code from a .mc64 file and creates a TagCompound that the mc64 computer or this program can understaind
     * 
     * @param line - the line to parse
     * @return the command as an TagCompound
     */
    private static TagCompound parseLine(String line) {
        for (LineParser l : parsers) {
            TagCompound result = l.parse(line);
            if (result != null) {
                return result;
            }
        }
        throw new RuntimeException("Syntax error");
    }

    private static Pattern lineParser = Pattern.compile("(\\p{Alnum}+:.*?)(?=\\p{Alnum}+:|\\z)", Pattern.DOTALL);
    private static boolean setupDone = false;
    private static List<LineParser> parsers;

    private static void setup() {
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


        setupDone = true;
        parsers = new ArrayList<LineParser>();
        String src = "";

        try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(new File("src/translation/commands.pattern")))) {
            src = new String(stream.readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Matcher m = lineParser.matcher(src);
        
        while (m.find()) {
            parsers.add(new LineParser(m.group(1), cmd));
        }
    }


    public TagList getProgramAsNbt() {
        return this.program;
    }

    public boolean isCodeOk() {
        return this.ok;
    }


    public byte[] convert(String format) {
        switch (format) {
        case "nbt":
            //initialize the structure block data
            TagCompound struct = (TagCompound)NbtTag.parse(
                "{" + 
                    "DataVersion:2230," +
                    "size: [1, 1, 1]," +
                    "palette: [" +
                        "{" +
                            "Name:\"minecraft:chest\"," +
                            "Properties: {" +
                                "waterlogged: \"false\"," +
                                "facing: \"west\"," + 
                                "type: \"single\"" +
                            "}" +
                        "}" +
                    "]," +
                    "blocks: [" +
                        "{" +
                            "state: 0," +
                            "pos: [0, 0, 0]," +
                            "nbt: {" +
                                "Items: [" +
                                    "{" +
                                        "Count: 1b," +
                                        "Slot: 13b," +
                                        "id: \"minecraft:book\"," +
                                        "tag: {" +
                                            "program: []" +
                                        "}" +
                                    "}" +
                                "]" +
                            "}" +
                        "}" +
                    "]," +
                    "entities: []" +
                "}"
            );
            //add the program data
            struct.set("blocks.0.nbt.Items.0.tag.program", this.program.getValue());
            return struct.toNBTFormat();
        case "mcfunction":
            String result = "data modify storage sys program.cmd set value ";
            result += this.getProgramAsNbt();
            result += "\n" + "function sys:io/console/program/tick";
            return result.getBytes();
        case "mc64":
            break;
        case "mc64b":
            return this.program.toNBTFormat();
        }
        return null;
    }
}