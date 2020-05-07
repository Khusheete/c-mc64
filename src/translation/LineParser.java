package translation;

import java.util.regex.*;
import java.util.HashMap;
import NBT.*;

class LineParser {
    
    private static Pattern argument = Pattern.compile("(\\p{Alpha}\\p{Alnum}*) *= *(.*)");
    private static Pattern string = Pattern.compile("<string=(\\d)>");
    private static Pattern var = Pattern.compile("<var=(\\d)>");

    private String name;
    private Pattern pattern;
    private TagCompound template;

    private HashMap<String, String> instr;
    
    public LineParser(String pattern, TagCompound template) {
        this.template = template;
        instr = new HashMap<String, String>();
        String[] lines = pattern.split("[\n|\r|\r\n]");
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].trim();
            Matcher m = argument.matcher(lines[i]);
            if (m.matches()) {
                String variable = m.group(1);
                String value = m.group(2);
                if (variable.compareTo("pattern") == 0) { //TODO maybe debug
                    m = string.matcher(value);
                    while (m.find()) {
                        value = m.replaceFirst("\\p{Alnum}{1," + m.group(1) + "}");
                    }
                    m = var.matcher(value);
                    while (m.find()) {
                        value = m.replaceFirst("\\p{Alpha}\\p{Alnum}{0," + (Integer.parseInt(m.group(1)) - 1) + "}");
                    }
                    System.out.println("hello: " + value);
                    this.pattern = Pattern.compile(value);
                } else {
                    instr.put(variable, value);
                }
            } else if (lines[i].endsWith(".")) {
                name = lines[i].substring(0, lines[i].length() - 1);
            }
        }
    }

    public TagCompound parse(String line) {
        TagCompound result = (TagCompound)this.template.copy();
        //TODO do the parsing

        return result;
    }


    private static TagList stringToList(String str, String name, int length) {
        TagList result = new TagList(name);
        for (char c : str.toCharArray()) {
            result.append(new TagString("" + c));
        }
        for (int i = str.length(); i < length; i++) {
            result.append(new TagString(""));
        }
        return result;
    }
}