package interpreter;

import java.util.List;
import java.util.Stack;
import java.util.HashMap;
import java.util.Map;

class Memory {
    private Stack<Map<String, Integer>> stack;
    private int[][][] globalMem;
    private String[] strings;

    private static final String[] PARAMETER_NAME = new String[]{"*a", "*b", "*c", "*d", "*e", "*f"};

    private static final char[] chars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', ' ', '.', ':', ',', ';', '-', '_', '!', '?', '\'', '$', '%', '&', '/', '(', ')', '=', '+', '*', '~', '#', '<', '>', '°'};

    public Memory() {
        stack = new Stack<Map<String, Integer>>();
        globalMem = new int[16][128][128];
        strings = new String[128];
    }

    public void add(List<String> variables, int[] parameters) {
        Map<String, Integer> newVars = new HashMap<String, Integer>();
        for (String var : variables) {
            newVars.put(var, 0);
        }
        for (int i = 0; i < parameters.length; i++) {
            newVars.put(PARAMETER_NAME[i], parameters[i]);
        }
        newVars.put("*return", 0);
        stack.push(newVars);
    }

    public int getGlobal(int i0, int i1, int i2) {
        if (i0 < 0 || i0 > 15 || i1 < 0 || i1 > 127 || i2 < 0 || i2 > 127)
            throw new InterpreterException("Index out of bounds [" + i0 + "][" + i1 + "][" + i2 + "]");
        return this.globalMem[i0][i1][i2];
    }

    public int getGlobal(int i) {
        if (i < 0 || i > 262143)
            throw new InterpreterException("Index out of bounds [" + i + "]");
        int i0 = i / 16384;
        int i1 = i % 16384 / 128;
        int i2 = i % 16384 % 128;
        i1 %= 128;
        return this.globalMem[i0][i1][i2];
    }

    public int getLocal(String name) {
        Map<String, Integer> top = stack.peek();
        if (!top.containsKey(name)) {  //mainly for debugging
            throw new InterpreterException("Undefined variable " + name);
        }
        return top.get(name);
    }

    public void setGlobal(int i0, int i1, int i2, int value) {
        if (i0 < 0 || i0 > 15 || i1 < 0 || i1 > 127 || i2 < 0 || i2 > 127)
            throw new InterpreterException("Index out of bounds [" + i0 + "][" + i1 + "][" + i2 + "]");
        this.globalMem[i0][i1][i2] = value;
    }

    public void setGlobal(int i, int value) {
        if (i < 0 || i > 262143)
            throw new InterpreterException("Index out of bounds [" + i + "]");
        int i0 = i / 16384;
        int i1 = i % 16384 / 128;
        int i2 = i % 16384 % 128;
        i1 %= 128;
        this.globalMem[i0][i1][i2] = value;
    }

    public void setLocal(String name, int value) {
        Map<String, Integer> top = stack.peek();
        if (!top.containsKey(name)) {  //mainly for debugging
            throw new InterpreterException("Undefined variable " + name);
        }
        top.put(name, value);
    }

    public void setReturn(int value) {
        setLocal("*return", value);
    }

    public String getString(int i) {
        if (i < 0 || i > 127)
            throw new InterpreterException("Index out of bounds [" + i + "]");
        if (this.strings[i] == null)
            throw new InterpreterException("string [" + i + "] is not defined");
        return this.strings[i];
    }

    public void setString(int i, String value) {
        if (i < 0 || i > 127)
            throw new InterpreterException("Index out of bounds [" + i + "]");
        for (char c : value.toCharArray())
            if (getChar(c) == -1)
                throw new InterpreterException("Unknown char '" + c + "'");
        this.strings[i] = value;
    }

    public void createString(String variable) {
        for (int i = 0; i < 127; i++) {
            if (this.strings[i] == null) {
                this.setLocal(variable, i);
                this.strings[i] = "";
                this.setReturn(i);
                return;
            }
        }
        this.setReturn(-1);
    }

    public Character getChar(int c) {
        if (c < 0 || c >= chars.length)
            return null;
        return chars[c];
    }

    public int getChar(char c) {
        for (int i = 0; i < chars.length; i++) {
            if (c == chars[i]) {
                return i;
            }
        }
        return -1;
    }

    public void pop() {
        this.stack.pop();
    }
}