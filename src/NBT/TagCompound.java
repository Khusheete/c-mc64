package NBT;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.ArrayList;

public class TagCompound extends NbtTag {
	
	private static final byte ID = 0b1010;
	private List<NbtTag> value;
	
	public TagCompound() {
		this.name = "";
		this.value = new ArrayList<NbtTag>();
	}

	public TagCompound(String name) {
		this.name = name;
		this.value = new ArrayList<NbtTag>();
	}

	public TagCompound(List<NbtTag> value) {
		this.name = "";
		this.value = value;
	}
	
	public TagCompound(String name, List<NbtTag> value) {
		this.name = name;
		this.value = value;
	}
	
	
	@Override
	public List<NbtTag> getValue() {
		return this.value;
	}
	
	public void setValue(List<NbtTag> value) {
		this.value = value;
	}
	
	public NbtTag get(String name) {
		for (NbtTag nbt : this.value)
			if (nbt.getName().equals(name))
				return nbt;
		return null;
	}
	
	public boolean hasValue(String name) {
		for (NbtTag nbt : this.value)
			if (nbt.getName().equals(name))
				return true;
		return false;
	}
	
	public void add(NbtTag nbt) {
		if (!this.hasValue(nbt.getName()))
			this.value.add(nbt);
	}
	
	public void set(NbtTag nbt) {
		for (int i = 0; i < this.value.size(); i++) {
			NbtTag tag = this.value.get(i);
			if (tag.getName().equals(nbt.getName())) {
				this.value.set(i, nbt);
				break;
			}
		}
	}
	
	public void remove(String name) {
		for (int i = 0; i < this.value.size(); i++) {
			NbtTag tag = this.value.get(i);
			if (tag.getName().equals(name)) {
				this.value.remove(i);
				break;
			}
		}
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
		for (NbtTag nbt : this.value)
			b.put(nbt.toNBTFormat());
		b.put((byte) 0b0000);
		return b.array();
	}

	@Override
	int getNBTValueLength() {
		int length = 1;
		for (NbtTag nbt : this.value)
			length += nbt.getLength();
		return length;
	}

	@Override
	String getValueAsString() {
		String result = "{";
		for (int i = 0; i < this.value.size(); i++) {
			result += this.value.get(i).toString();
			if (i + 1 != this.value.size()) result += ",";
		}
		result += "}";
		return result;
	}

}
