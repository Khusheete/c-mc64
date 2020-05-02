package interpreter.v1_1_2;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import java.util.regex.*;

public class Parser {
    
    private static final Pattern DECL = Pattern.compile("decl (\\p{Lower}[-\\p{Lower}\\p{Digit}\\._]*)");
    private static final Pattern FUNCTION = Pattern.compile("(\\p{Lower}[-\\p{Lower}\\p{Digit}\\._]*)\\(((?:\\*a)?(?:, \\*b)?(?:, \\*c)?(?:, \\*d)?(?:, \\*e)?(?:, \\*f)?)\\)");
    private static final Pattern RETURN_F = Pattern.compile("RETURN (\\p{Digit}+|\\p{Lower}[-\\p{Lower}\\p{Digit}\\._]*)");

    private static final Pattern LOOP = Pattern.compile("\\b(?:WHILE|IF)\\b");
    private static final Pattern END_LOOP = Pattern.compile("\\b(?:WEND|IFEND)\\b");

    public static Map<String, interpreter.Function> parse(String src) {
        Map<String, interpreter.Function> funcs = new HashMap<String,interpreter.Function>();
        List<String> vars = new ArrayList<String>();

        Iterator<String> i = src.lines().iterator();
        int line = 1;


        while (i.hasNext()) {
            String l = i.next().trim();
            line++;
            if (l.isEmpty()) continue;
            Matcher m;
            if ((m = DECL.matcher(l)).matches()) {
                //add local variables
                vars.add(m.group(1));
            } else if ((m = FUNCTION.matcher(l)).matches()) {
                //finds a function
                String funcName = m.group(1);
                int args = (m.group(2).isEmpty())? 0 : m.group(2).split(",").length;
                List<String> funcSrc = new ArrayList<String>();
                //stores the source of the function
                //and stops at the "RETURN" command
                int loopCount = 0;
                boolean hasReturn = false;
                while (true) {
                    if (!i.hasNext() && (!hasReturn || loopCount == 0)) {
                        System.err.println("\u001B[31mFunction \"" + funcName + "\" has no \"RETURN\" point\u001B[0m");
                        System.exit(1);
                    }
                    if (!i.hasNext() && hasReturn) {
                        break; //the class "Function" will handle the error
                    }
                    l = i.next().trim();
                    line++;
                    //do not ignore empty lines so we can keep track of the line count inside the function
                    //if (l.isEmpty()) continue;

                    if (LOOP.matcher(l).find())
                        loopCount++;
                    else if (END_LOOP.matcher(l).find() && loopCount > 0)
                        loopCount--;

                    funcSrc.add(l);

                    if ((m = RETURN_F.matcher(l)).find() && loopCount == 0) break;
                    if ((m = RETURN_F.matcher(l)).find() && loopCount != 0) hasReturn = true;
                }

                funcs.put(funcName, new Function(funcSrc, vars, args, line - funcSrc.size()));
                
                //reset variables
                vars = new ArrayList<String>();
            } else {
                System.err.println("\u001B[31mSyntax error at line " + line + ": \"" + l + "\"\u001B[0m");
            }
        }
        return funcs;
    }
}