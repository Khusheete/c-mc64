package NBT;

public class InvalidDataType extends NbtException {
    private static final long serialVersionUID = 7641670086530537616L;
    
    public InvalidDataType(Class<?> dataType, Class<?> targetType) {
        super("Data type is: " + dataType.getName() + ", but should be: " + targetType.getName());
    }
}