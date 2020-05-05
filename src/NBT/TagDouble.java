package NBT;

import java.nio.ByteBuffer;

public class TagDouble extends NbtTag {
	
	private static final byte ID = 0b0110;
	private double value;
	
	public TagDouble(double value) {
		this.name = "";
		this.value = value;
	}
	
	public TagDouble(String name, double value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public Double getValue() {
		return this.value;
	}
	
	@Override
	public void set(String path, Object value) {
		throw new NbtException("Cannot set value with path for a TagDouble");
	}

	@Override
	public NbtTag get(String path) {
		throw new NbtException("Cannot get value with path for a TagDouble");
	}

	@Override
	public void set(Object value) {
		if (value instanceof Double) {
			this.value = (Double)value;
		} else {
			throw new InvalidDataType(value.getClass(), Double.class);
		}
	}

	@Override
	public byte getId() {
		return ID;
	}

	@Override
	public int getLength() {
		return 3 + this.name.length() + Double.BYTES;
	}

	@Override
	public String toString() {
		return ((this.name.length() > 0)? this.name + ":" : "") + this.getValueAsString();
	}

	@Override
	public byte[] toNBTFormat() {
		ByteBuffer b = ByteBuffer.allocate(this.getLength());
		b.put(ID).putShort((short) this.name.length()).put(this.name.getBytes()).putDouble(this.value);
		return b.array();
	}
	
	@Override
	byte[] getValueAsNBT() {
		return ByteBuffer.allocate(Double.BYTES).putDouble(this.value).array();
	}
	
	@Override
	int getNBTValueLength() {
		return Double.BYTES;
	}
	
	@Override
	String getValueAsString() {
		return this.value + ((Double.isInfinite(value))? "" : "d");
	}

}
