package NBT;

import java.nio.ByteBuffer;

public class TagByteArray extends NbtTag {
	
	private static final byte ID = 0b0111;
	private byte[] value;
	
	public TagByteArray(byte[] value) {
		this.name = "";
		this.value = value;
	}
	
	public TagByteArray(String name, byte[] value) {
		this.name = name;
		this.value = value;
	}
	
	public byte get(int index) {
		return this.value[index];
	}
	
	public void set(int index, byte value) {
		this.value[index] = value;
	}
	
	@Override
	public byte[] getValue() {
		return value;
	}
	
	public void setValue(byte[] value) {
	this.value = value;
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
		b.put(ID).putShort((short) this.name.length()).put(this.name.getBytes()).putInt(this.value.length).put(this.value);
		return b.array();
	}
	
	@Override
	byte[] getValueAsNBT() {
		return ByteBuffer.allocate(this.getNBTValueLength()).putInt(this.value.length).put(this.value).array();
	}
	
	@Override
	int getNBTValueLength() {
		return Integer.BYTES + (Byte.BYTES * this.value.length);
	}
	
	@Override
	String getValueAsString() {
		String result = "[B;";
		for (int i = 0; i < this.value.length; i++) {
			result += this.value[i];
			if (i + 1 != this.value.length) result += ",";
		}
		result += "]";
		return result;
	}

}
