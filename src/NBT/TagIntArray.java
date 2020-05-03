package NBT;

import java.nio.ByteBuffer;

public class TagIntArray extends NbtTag {
	
	private final static byte ID = 0b1011;
	private int[] value;
	
	public TagIntArray(int[] value) {
		this.name = "";
		this.value = value;
	}
	
	public TagIntArray(String name, int[] value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public int[] getValue() {
		return this.value;
	}
	
	public void setValue(int[] value) {
		this.value = value;
	}
	
	public void set(int index, int value) {
		this.value[index] = value;
	}
	
	public int get(int index) {
		return this.value[index];
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
		b.put(ID).putShort((short) this.name.length()).put(this.name.getBytes()).put(this.getValueAsNBT());
		return b.array();
	}

	@Override
	byte[] getValueAsNBT() {
		ByteBuffer b = ByteBuffer.allocate(this.getNBTValueLength());
		b.putInt(this.value.length);
		for (int i : this.value)
			b.putInt(i);
		return b.array();
	}

	@Override
	int getNBTValueLength() {
		return Integer.BYTES + (Integer.BYTES * this.value.length);
	}

	@Override
	String getValueAsString() {
		String result = "[I;";
		for (int i = 0; i < this.value.length; i++) {
			result += this.value[i];
			if (i + 1 != this.value.length) result += ",";
		}
		result += "]";
		return result;
	}

}
