package NBT;

import java.nio.ByteBuffer;

public class TagInt extends NbtTag {
	
	private static final byte ID = 0b0011;
	private int value;
	
	public TagInt(int value) {
		this.name = "";
		this.value = value;
	}
	
	public TagInt(String name, int value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public Integer getValue() {
		return this.value;
	}
	
	@Override
	public void set(String path, Object value) {
		throw new NbtException("Cannot set value with path for a TagInt");
	}

	@Override
	public NbtTag get(String path) {
		throw new NbtException("Cannot get value with path for a TagInt");
	}

	@Override
	public void set(Object value) {
		if (value instanceof Integer) {
			this.value = (Integer)value;
		} else {
			throw new InvalidDataType(value.getClass(), Integer.class);
		}
	}
	
	@Override
	public byte getId() {
		return ID;
	}

	@Override
	public int getLength() {
		return 3 + this.name.length() + Integer.BYTES;
	}

	@Override
	public String toString() {
		return ((this.name.length() > 0)? this.name + ":" : "") + this.getValueAsString();
	}

	@Override
	public byte[] toNBTFormat() {
		ByteBuffer b = ByteBuffer.allocate(this.getLength());
		b.put(ID).putShort((short) this.name.length()).put(this.name.getBytes()).putInt(this.value);
		return b.array();
	}
	
	@Override
	byte[] getValueAsNBT() {
		return ByteBuffer.allocate(Integer.BYTES).putInt(this.value).array();
	}
	
	@Override
	int getNBTValueLength() {
		return Integer.BYTES;
	}
	
	@Override
	String getValueAsString() {
		return this.value + "";
	}
}
