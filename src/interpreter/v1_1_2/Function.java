package interpreter.v1_1_2;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.*;
import java.util.Stack;

public class Function implements interpreter.Function {

    private static final Pattern IF = Pattern.compile("IF *\\( *(\\p{Digit}+|\\p{Lower}[-\\p{Lower}\\p{Digit}\\._]*) *((?:<=?|>=?|==?|!=)) *(\\p{Digit}+|\\p{Lower}[-\\p{Lower}\\p{Digit}\\._]*) *\\)");
    private static final Pattern ELSE = Pattern.compile("ELSE");
    private static final Pattern IFEND = Pattern.compile("IFEND");
    private static final Pattern WHILE = Pattern.compile("WHILE *\\( *(\\p{Digit}+|\\p{Lower}[-\\p{Lower}\\p{Digit}\\._]*) *((?:<=?|>=?|==?|!=)) *(\\p{Digit}+|\\p{Lower}[-\\p{Lower}\\p{Digit}\\._]*) *\\)");
    private static final Pattern WEND = Pattern.compile("WEND");

    private static final Pattern RETURN_F = Pattern.compile("RETURN (\\p{Digit}+|\\p{Lower}[-\\p{Lower}\\p{Digit}\\._]*)");

    private List<String> vars;
    private int startingLine;
    private List<String> code;

    public Function(List<String> src, List<String> localVar, int argCount, int startingLine) {
        this.startingLine = startingLine;
        vars = localVar;
        this.code = src;

        this.verify();
    }

    @Override
    public void call(interpreter.Program prg) {
        //setup the local variables
        Map<String, Integer> variables = new HashMap<String, Integer>();
        vars.stream().forEach((x) -> variables.put(x, 0));
        //get the program memory
        Memory mem = (Memory)prg.getMemory();


        //run the program
        for (int line = 0; line < this.code.size(); line++) {
            String l = this.code.get(line);
            if (l.isEmpty()) continue;
            System.out.println((line + startingLine) + ": " + l);

            Matcher m;
            if ((m = RETURN_F.matcher(l)).matches()) {
                String returnv = m.group(1);
                mem.setReturn(getValue(variables, returnv, line + startingLine));
                break;
            }
        }
    }

    /**
     * verifyes the program, if it fails prints the error and kill this program
     */
    public void verify() {
        //setup the local variables
        Map<String, Integer> variables = new HashMap<String, Integer>();
        vars.stream().forEach((x) -> variables.put(x, 0));

        //setup the test variables
        Stack<String> loops = new Stack<String>();
        
        boolean foundError = false;

        //run the syntax test for each line
        for (int line = 0; line < this.code.size(); line++) {
            String l = this.code.get(line);
            if (l.isEmpty()) continue;
            System.out.println((line + startingLine) + ": " + l);

            Matcher m;
            if ((m = IF.matcher(l)).matches()) {
                loops.push("IF");
                if (getValue(variables, m.group(1), line + startingLine) == null) foundError = true;
                if (getValue(variables, m.group(3), line + startingLine) == null) foundError = true;
            } else if ((m = WHILE.matcher(l)).matches()) {
                loops.push("WHILE");
                if (getValue(variables, m.group(1), line + startingLine) == null) foundError = true;
                if (getValue(variables, m.group(3), line + startingLine) == null) foundError = true;
            } else if ((m = WEND.matcher(l)).matches()) {
                if (loops.size() == 0) {
                    printError("WEND without WHILE loop", line + startingLine);
                    foundError = true;
                } else {
                    String lastLoop = loops.pop();
                    if (lastLoop.startsWith("IF")) {
                        printError("missing IFEND before WEND", line + startingLine);
                        foundError = true;
                    }
                }
            } else if ((m = IFEND.matcher(l)).matches()) {
                if (loops.size() == 0) {
                    printError("IFEND without IF condition", line + startingLine);
                    foundError = true;
                } else {
                    String lastLoop = loops.pop();
                    if (lastLoop.compareTo("WHILE") == 0) {
                        printError("missing WEND before IFEND", line + startingLine);
                        foundError = true;
                    }
                }
            } else if ((m = RETURN_F.matcher(l)).matches()) {
                if (getValue(variables, m.group(1), line + startingLine) == null) foundError = true;
            } else if ((m = ELSE.matcher(l)).matches()) {
                if (loops.size() == 0) {
                    printError("ELSE without IF condition", line + startingLine);
                    foundError = true;
                } else {
                    String lastLoop = loops.peek();
                    if (lastLoop.compareTo("WHILE") == 0) {
                        printError("missing WEND before ELSE", line + startingLine);
                        foundError = true;
                    } else if (lastLoop.compareTo("IF-ELSE") == 0) {
                        printError("only one ELSE allowed by IF condition", line + startingLine);
                        foundError = true;
                    } else if (lastLoop.compareTo("IF") == 0) {
                        loops.pop();
                        loops.push("IF-ELSE");
                    }
                }
            }
            /*TODO:
            CALL
            WAIT

            Commands (variables)
            Commands (IO)
            Commands (strings)
            Commands (gpu)

            error for unknown command*/
        }

        if (loops.size() > 0) {
            foundError = true;
            String error = "";
            while (!loops.isEmpty()) {
                String loop = loops.pop();
                if (loop.compareTo("IF") == 0 || loop.compareTo("IF-ELSE") == 0) {
                    error += "missing IFEND\n";
                } else if (loop.compareTo("WHILE") == 0) {
                    error += "missing WEND\n";
                }
            }
            printError(error, startingLine + this.code.size() - 1);
        }

        if (foundError) {
            System.exit(1);
        }
    }

    /**
     * 
     * @param variables - the set of variables to use
     * @param value - the name of a variable or an int
     * @param line - the line at wich the code is executed
     * @return the given value
     */
    private static Integer getValue(Map<String, Integer> variables, String value, int line) {
        try {
            //if the return value is an int
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            //otherwise return a variable value
            try {
                return variables.get(value);
            } catch (NullPointerException e0) {
                printError("The variabe \"" + value + "\" does not exist", line);
            }
        }
        return null;
    }

    private static void printError(String error, int line) {
        System.out.println("\u001B[31mError line " + line + ":");
        System.out.println(error + "\u001B[0m");
    }
    
}