package NBT;

import java.nio.ByteBuffer;

public class TagString extends NbtTag {
	
	private static final byte ID = 0b1000;
	private String value;
	
	public TagString(String value) {
		this.name = "";
		this.value = value;
	}
	
	public TagString(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public String getValue() {
		return this.value.replace("\\\\", "\\").replace("\\n", "\n").replace("\\t", "\t").replace("\\b", "\b").replace("\\r", "\r").replace("\\f", "\f").replace("\\\"", "\"").replace("\\\'", "\'");
	}
	
	@Override
	public void set(String path, Object value) {
		throw new NbtException("Cannot set value with path for a TagLong");
	}

	@Override
	public NbtTag get(String path) {
		throw new NbtException("Cannot get value with path for a TagLong");
	}

	@Override
	public void set(Object value) {
		if (value instanceof String) {
			this.value = (String)value;
		} else {
			throw new InvalidDataType(value.getClass(), String.class);
		}
	}

	@Override
	protected Class<?> getType() {
		return String.class;
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
	public String toString() {
		return ((this.name.length() > 0)? this.name + ":" : "") + this.getValueAsString();
	}

	@Override
	public byte[] toNBTFormat() {
		ByteBuffer b = ByteBuffer.allocate(this.getLength());
		b.put(ID).putShort((short) this.name.length()).put(this.name.getBytes()).putShort((short) this.value.length()).put(this.value.getBytes());
		return b.array();
	}
	
	@Override
	byte[] getValueAsNBT() {
		return ByteBuffer.allocate(this.getNBTValueLength()).putShort((short) this.value.length()).put(this.value.getBytes()).array();
	}
	
	@Override
	int getNBTValueLength() {
		return Short.BYTES + this.value.length();
	}
	
	@Override
	String getValueAsString() {
		return "\"" + this.value + "\"";
	}

}
