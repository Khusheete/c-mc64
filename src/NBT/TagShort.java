package NBT;

import java.nio.ByteBuffer;

public class TagShort extends NbtTag {
	
	private static final byte ID = 0b0010;
	private short value;
	
	public TagShort(short value) {
		this.name = "";
		this.value = value;
	}
	
	public TagShort(String name, short value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public Short getValue() {
		return value;
	}
	
	public void setValue(short value) {
		this.value = value;
	}

	@Override
	public byte getId() {
		return ID;
	}

	@Override
	public String toString() {
		return ((this.name.length() > 0)? this.name + ":" : "") + this.getValueAsString();
	}
	
	@Override
	public int getLength() {
		return 3 + this.name.length() + Short.BYTES;
	}

	@Override
	public byte[] toNBTFormat() {
		ByteBuffer b = ByteBuffer.allocate(this.getLength());
		b.put(ID).putShort((short) this.name.length()).put(this.name.getBytes()).putShort(this.value);
		return b.array();
	}
	
	@Override
	byte[] getValueAsNBT() {
		return ByteBuffer.allocate(Short.BYTES).putShort(this.value).array();
	}
	
	@Override
	int getNBTValueLength() {
		return Short.BYTES;
	}
	
	@Override
	String getValueAsString() {
		return this.value + "s";
	}
}
