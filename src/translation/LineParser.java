package translation;

import java.util.regex.*;
import java.util.List;
import java.util.ArrayList;

import NBT.*;

class LineParser {
    
    private static Pattern argument = Pattern.compile("((?:\\p{Alpha}\\p{Alnum}+)(?:\\.\\p{Alnum}+)*) *= *(.*)");
    private static Pattern string = Pattern.compile("<string=(\\d+)>");
    private static Pattern val = Pattern.compile("<value=(\\d+)>");
    private static Pattern varName = Pattern.compile("<variable>");

    private String name;
    private Pattern pattern;
    private TagCompound template;

    private List<InstrPair> instr;
    
    public LineParser(String pattern, TagCompound template) {
        this.template = template;
        instr = new ArrayList<InstrPair>();
        String[] lines = pattern.lines().toArray((size) -> new String[size]);
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].trim();
            Matcher m = argument.matcher(lines[i]);
            if (m.matches()) {
                String variable = m.group(1);
                String value = m.group(2);
                if (variable.compareTo("pattern") == 0) {
                    Matcher m1 = string.matcher(value);
                    value = m1.replaceAll("\\\\p{Print}{0,$1}");
                    m1 = val.matcher(value);
                    value = m1.replaceAll("-?\\\\p{Alnum}{1,$1}");
                    m1 = varName.matcher(value);
                    value = m1.replaceAll("\\\\p{Alpha}\\\\p{Alnum}{0,9}");
                    this.pattern = Pattern.compile(value);
                } else {
                    instr.add(new InstrPair(variable, value));
                }
            } else if (lines[i].endsWith(":")) {
                name = lines[i].substring(0, lines[i].length() - 1);
            }
        }
    }

    private static final Pattern integer = Pattern.compile("\\d+");
    private static final Pattern intGroup = Pattern.compile("g(\\d+)\\[(\\d+)\\]");
    private static final Pattern intGroupArray = Pattern.compile("g(\\d+)\\{(.*)\\}");

    private static final Pattern groupType = Pattern.compile("g(\\d+)\\.type");
    private static final Pattern groupValue = Pattern.compile("g(\\d+)\\.value");
    private static final Pattern ifGroup = Pattern.compile("g(\\d+) *-> *(\\d+)");

    public TagCompound parse(String line) {
        TagCompound result = (TagCompound)this.template.copy();
        Matcher m = this.pattern.matcher(line);
        if (!m.matches()) {
            return null;
        }
        for (int i = 0; i < instr.size(); i++) {
            String value = this.instr.get(i).value;
            String action = this.instr.get(i).action;

            Matcher act;

            if (integer.matcher(action).matches()) {
                result.set(value, Integer.parseInt(action));
            } else if ((act = intGroup.matcher(action)).matches()) {
                int g = Integer.parseInt(act.group(1));
                if (m.groupCount() >= g) if (m.group(g) != null) {
                    result.set(value, stringToList(m.group(g), Integer.parseInt(act.group(2))));
                }
            } else if ((act = intGroupArray.matcher(action)).matches()) {
                int g = Integer.parseInt(act.group(1));
                String[] strs = act.group(2).split(" *, *");
                if (m.groupCount() >= g) if (m.group(g) != null) {
                    String gr = m.group(g);
                    boolean ok = false;
                    int k = 0;
                    for (int j = 0; j < strs.length; j++) {
                        if (strs[j].matches("s\\d+")) {
                            k = Integer.parseInt(strs[j], 1, strs[j].length(), 10);
                            continue;
                        }
                        if (gr.equals(strs[j]) && !strs[j].equals("NONE")) {
                            result.set(value, k);
                            ok = true;
                            break;
                        }
                        k++;
                    }
                    if (!ok) {
                        throw new RuntimeException("Syntax error is " + gr + " but should be {" + act.group(2) + "}\n in " + name);
                    }
                }
            } else if ((act = groupType.matcher(action)).matches()) {
                int g = Integer.parseInt(act.group(1));
                if (m.groupCount() >= g) if (m.group(g) != null) {
                    if (m.group(g).matches("-?\\d+"))
                        result.set(value, 0);
                    else if (m.group(g).matches("\\p{Alpha}\\p{Alnum}*"))
                        result.set(value, 1);
                    else
                        result.set(value, 2);
                }
            } else if ((act = ifGroup.matcher(action)).matches()) {
                int g = Integer.parseInt(act.group(1));
                if (m.groupCount() >= g) if (m.group(g) != null) {
                    result.set(value, Integer.parseInt(act.group(2)));
                }
            } else if ((act = groupValue.matcher(action)).matches()) {
                int g = Integer.parseInt(act.group(1));
                if (m.groupCount() >= g) if (m.group(g) != null) {
                    if (m.group(g).matches("-?\\d+"))
                        result.set(value, Integer.parseInt(m.group(g)));
                }
            }
        }
        return result;
    }


    private List<TagString> stringToList(String str, int length) {
        if (str.length() >= length)
            throw new RuntimeException("length of " + str.length() + "but max length is " + length + " in " + name);
        List<TagString> result = new ArrayList<TagString>();
        for (char c : str.toCharArray()) {
            result.add(new TagString("" + c));
        }
        for (int i = str.length(); i < length; i++) {
            result.add(new TagString(""));
        }
        return result;
    }


    private class InstrPair {
        public String value;
        public String action;

        public InstrPair(String value, String action) {
            this.value = value;
            this.action = action;
        }
    }
}