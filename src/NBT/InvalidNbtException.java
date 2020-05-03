package NBT;

class InvalidNbtException  extends RuntimeException {
    private static final long serialVersionUID = 4002194584666738517L;
    
    public InvalidNbtException(String message) {
        super(message);
    }
}