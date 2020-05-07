package NBT;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class TagList extends NbtTag {
	
	private static final byte ID = 0b1001;
	private List<NbtTag> value;
	
	public TagList() {
		this.name = "";
		this.value = new ArrayList<NbtTag>();
	}

	public TagList(String name) {
		this.name = name;
		this.value = new ArrayList<NbtTag>();
	}

	public TagList(List<NbtTag> value) {
		this.name = "";
		this.value = value;
	}
	
	public TagList(String name, List<NbtTag> value) {
		this.name = name;
		this.value = value;
	}

	public TagList(NbtTag[] value) {
		this.name = "";
		this.value = Arrays.asList(value);
	}
	
	public TagList(String name, NbtTag[] value) {
		this.name = name;
		this.value = Arrays.asList(value);
	}

	public NbtTag get(int index) {
		return this.value.get(index);
	}

	public void set(int index, NbtTag tag) {
		this.value.set(index, tag);
	}

	public void append(NbtTag tag) {
		this.value.add(tag);
	}

	public void prepend(NbtTag tag) {
		this.value.add(0, tag);
	}

	public void remove(int index) {
		this.value.remove(index);
	}
	
	@Override
	public List<NbtTag> getValue() {
		return this.value;
	}
	
	@Override
	public NbtTag get(String path) {
		String[] pth = path.split("\\.", 2);
		if (pth.length == 2)
			return this.value.get(Integer.parseInt(pth[0])).get(pth[1]);
		else
			return this.value.get(Integer.parseInt(pth[0]));
	}

	@Override
	public void set(String path, Object value) {
		String[] pth = path.split("\\.", 2);
		int index = Integer.parseInt(pth[0]);
		if (pth.length == 2)
			this.value.get(index).set(pth[1], value);
		else {
			this.value.get(index).set(value);
		}
	}

	@Override
	public void set(Object value) {
		throw new NbtException("Cannot set value of a TagList");
	}

	@Override
	public NbtTag copy() {
		return new TagList(this.name, new ArrayList<NbtTag>(this.value));
	}

	@Override
	protected Class<?> getType() {
		return List.class;
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
		if (this.value.size() == 0)
			return new byte[] {0};
		ByteBuffer b = ByteBuffer.allocate(this.getNBTValueLength());
		b.put(this.value.get(0).getId());
		b.putInt(this.value.size());
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
		for (int i = 0; i < this.value.size(); i++) {
			result += this.value.get(i).getValueAsString();
			if (i + 1 != this.value.size()) result += ",";
		}
		result += "]";
		return result;
	}

}
