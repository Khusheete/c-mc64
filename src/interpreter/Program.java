package interpreter;

import java.util.Map;
import java.util.Stack;

public class Program {
    
    private Screen surface;

    private Map<String, Function> funcs;
    private Memory mem;
    private Stack<String> stack;

    public Program(String src, String version) {
        //verifyes and parses the program
        surface = new Screen();
        switch (version) {
        case "1.1.2":
            this.funcs = interpreter.v1_1_2.Parser.parse(src);
            this.mem = new interpreter.v1_1_2.Memory();
            this.stack = new Stack<String>();
            break;
        default:
            System.err.println("Unknown version \"" + version + "\"");
            System.exit(1);
            break;
        }
    }

    /**
     * runs the first function in the program (which is the main function)
     */
    public void run() {
        //run the main function
        surface.setVisible(true);
        stack.push(funcs.keySet().iterator().next());
        funcs.values().iterator().next().call(this);
        stack.pop();
    }

    /**
     * calls the named function
     * set the arguments in the memory (yes sloppy but working and should keep working)
     * 
     * @param func the function name
     * @param line the line inside the calling function, for debugging
     */
    public void runFunction(String func, int line) {
        try { //TODO add line to stack to debug
            stack.push(func);
            funcs.get(func).call(this);
            stack.pop();
        } catch (NullPointerException e) {
            printError(line, "The function \"" + func + "\" does not exist");
        }
    }

    /**
     * @return the memory of the program
     */
    public Memory getMemory() {
        return this.mem;
    }

    /**
     * prints the given error along with the the function that threw it
     */
    public void printError(int line, String error) {
        System.err.println("\u001B[31mIn function \"" + this.stack.peek() + "\" (line " + line + "):");
        System.err.println(error + "\u001B[0m");
        System.exit(1);
    }
}