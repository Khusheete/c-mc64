package interpreter.v1_1_2;

public class Memory extends interpreter.Memory {
    private int returnv;
    private int[] args;

    public Memory() {
        returnv = 0;
        args = new int[6];
    }

    /**
     * @return the last returned value
     */
    public int getReturn() {
        return returnv;
    }

    /**
     * @param returnv - set the return value of the function
     */
    public void setReturn(int returnv) {
        this.returnv = returnv;
    }

    /**
     * @param c - a character from a to f that defines the argument
     * @return the value of the argument
     */
    public int getArg(char c) {
        int i = (int)c - 97;
        return args[i];
    }

    /**
     * Set the arguments to call a function
     * @param args - the arguments
     */
    public void setArgs(int[] args) {
        for (int i = 0; i < 6; i++)
            this.args[i] = args[i];
    }
}