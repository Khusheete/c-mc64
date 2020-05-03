package NBT;

import java.nio.ByteBuffer;
// import java.util.Arrays;
// import java.util.List;

public class TagList extends NbtTag {
	
	private static final byte ID = 0b1001;
	private NbtTag[] value;
	
	// public TagList(List<? extends NbtTag> value) {
	// 	this.name = "";
	// 	this.value = value;
	// }
	
	// public TagList(String name, List<? extends NbtTag> value) {
	// 	this.name = name;
	// 	this.value = value;
	// }

	public TagList(NbtTag[] value) {
		this.name = "";
		this.value = value;
	}
	
	public TagList(String name, NbtTag[] value) {
		this.name = name;
		this.value = value;
	}

	public NbtTag get(int index) {
		return this.value[index];
	}

	public void set(int index, NbtTag tag) {
		this.value[index] = tag;
	}
	
	@Override
	public NbtTag[] getValue() {
		return this.value;
	}
	
	public void setValue(NbtTag[] value) {
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
		b.put(ID).putShort((short) this.name.length()).put(this.name.getBytes()).put(this.getValueAsNBT());
		return b.array();
	}
	
	@Override
	byte[] getValueAsNBT() {
		if (this.value.length == 0)
			return new byte[] {0};
		ByteBuffer b = ByteBuffer.allocate(this.getNBTValueLength());
		b.put(this.value[0].getId());
		b.putInt(this.value.length);
		for (NbtTag nbt : this.value)
			b.put(nbt.getValueAsNBT());
		return b.array();
	}
	
	@Override 
	int getNBTValueLength() {
		int length = Byte.BYTES + Integer.BYTES;
		for (NbtTag nbt : this.value)
			length += nbt.getNBTValueLength();
		return length;
	}
	
	@Override 
	String getValueAsString() {
		String result = "[";
		for (int i = 0; i < this.value.length; i++) {
			result += this.value[i].getValueAsString();
			if (i + 1 != this.value.length) result += ",";
		}
		result += "]";
		return result;
	}

}
