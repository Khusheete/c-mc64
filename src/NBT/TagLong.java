package NBT;

import java.nio.ByteBuffer;

public class TagLong extends NbtTag {
	
	private final static byte ID = 0b0100;
	private long value;
	
	public TagLong(long value) {
		this.name = "";
		this.value = value;
	}
	
	public TagLong(String name, long value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public Long getValue() {
		return this.value;
	}
	
	public void setValue(long value) {
		this.value = value;
	}

	@Override
	public byte getId() {
		return ID;
	}

	@Override
	public int getLength() {
		return 3 + this.name.length() + Long.BYTES;
	}

	@Override
	public String toString() {
		return ((this.name.length() > 0)? this.name + ":" : "") + this.getValueAsString();
	}

	@Override
	public byte[] toNBTFormat() {
		ByteBuffer b = ByteBuffer.allocate(this.getLength());
		b.put(ID).putShort((short) this.name.length()).put(this.name.getBytes()).putLong(this.value);
		return b.array();
	}
	
	@Override
	byte[] getValueAsNBT() {
		return ByteBuffer.allocate(Long.BYTES).putLong(this.value).array();
	}
	
	@Override
	int getNBTValueLength() {
		return Long.BYTES;
	}
	
	@Override
	String getValueAsString() {
		return this.value + "l";
	}

}
