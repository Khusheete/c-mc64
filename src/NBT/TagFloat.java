package NBT;

import java.nio.ByteBuffer;

public class TagFloat extends NbtTag {
	
	private static final byte ID = 0b0101;
	private float value;
	
	public TagFloat(float value) {
		this.name = "";
		this.value = value;
	}
	
	public TagFloat(String name, float value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public Float getValue() {
		return this.value;
	}
	
	@Override
	public void set(String path, Object value) {
		throw new NbtException("Cannot set value with path for a TagFloat");
	}

	@Override
	public NbtTag get(String path) {
		throw new NbtException("Cannot get value with path for a TagFloat");
	}

	@Override
	public void set(Object value) {
		if (value instanceof Float) {
			this.value = (Float)value;
		} else {
			throw new InvalidDataType(value.getClass(), Float.class);
		}
	}

	@Override
	protected Class<?> getType() {
		return Float.class;
	}

	@Override
	public byte getId() {
		return ID;
	}

	@Override
	public int getLength() {
		return 3 + this.name.length() + Float.BYTES;
	}

	@Override
	public String toString() {
		return ((this.name.length() > 0)? this.name + ":" : "") + this.getValueAsString();
	}

	@Override
	public byte[] toNBTFormat() {
		ByteBuffer b = ByteBuffer.allocate(this.getLength());
		b.put(ID).putShort((short) this.name.length()).put(this.name.getBytes()).putFloat(this.value);
		return b.array();
	}
	
	@Override
	byte[] getValueAsNBT() {
		return ByteBuffer.allocate(Float.BYTES).putFloat(this.value).array();
	}
	
	@Override
	int getNBTValueLength() {
		return Float.BYTES;
	}
	
	@Override
	String getValueAsString() {
		return this.value + ((Float.isInfinite(value))? "" : "f");
	}

}
