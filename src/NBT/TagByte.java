package NBT;

import java.nio.ByteBuffer;

public class TagByte extends NbtTag {
	
	private static final byte ID = 0b0001;
	private byte value;
	
	public TagByte(byte value) {
		this.name = "";
		this.value = value;
	}
	
	public TagByte(String name, byte value) {
		this.name = name;
		this.value = value;
	}
	

	@Override
	public Byte getValue() {
		return value;
	}

	@Override
	public String toString() {
		return ((this.name.length() > 0)? this.name + ":" : "") + this.getValueAsString();
	}
	
	@Override
	public byte getId() {
		return ID;
	}
	
	@Override
	public int getLength() {
		return 3 + this.name.length() + this.getNBTValueLength();
	}

	@Override
	public void set(String path, Object value) {
		throw new NbtException("Cannot set value with path for a TagByte");
	}

	@Override
	public NbtTag get(String path) {
		throw new NbtException("Cannot get value with path for a TagByte");
	}

	@Override
	public void set(Object value) {
		if (value instanceof Byte) {
			this.value = (Byte)value;
		} else {
			throw new InvalidDataType(value.getClass(), Byte.class);
		}
	}

	@Override
	protected Class<?> getType() {
		return Byte.class;
	}

	@Override
	public byte[] toNBTFormat() {
		ByteBuffer b = ByteBuffer.allocate(this.getLength());
		b.put(ID).putShort((short) this.name.length()).put(this.name.getBytes()).put(this.value);
		return b.array();
	}
	
	@Override
	byte[] getValueAsNBT() {
		return ByteBuffer.allocate(this.getNBTValueLength()).put(this.value).array();
	}
	
	@Override
	int getNBTValueLength() {
		return Byte.BYTES;
	}
	
	@Override
	String getValueAsString() {
		return this.value + "b";
	}

}
