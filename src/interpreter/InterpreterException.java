package interpreter;

class InterpreterException extends RuntimeException {
    private static final long serialVersionUID = 3339193110793720687L;

    public InterpreterException(String message) {
        super(message);
    }
}