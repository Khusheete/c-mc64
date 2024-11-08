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

	@Override
	public void set(String path, Object value) {
		String[] pth = path.split("\\.", 2);
		for (NbtTag tag : this.value) {
			if (tag.getName().equals(pth[0])) {
				if (pth.length == 2)
					tag.set(pth[1], value);
				else
					tag.set(value);
				return;
			}
		}
		throw new NbtException("No value named " + pth[0] + " has been found");
	}

	@Override
	public void set(Object value) {
		if (value instanceof NbtTag) {
			NbtTag tag = ((NbtTag)value);
			for (int i = 0; i < this.value.size(); i++) {
				NbtTag val = this.value.get(i);
				if (tag.getName().equals(val.getName())) {
					if (val.getId() != tag.getId() && this.getId() == tag.getId())
						break;
					if (val.getId() != tag.getId())
						throw new InvalidDataType(tag.getClass(), val.getClass());
					this.value.set(i, tag);
					return;
				}
			}
			if (tag instanceof TagCompound && this.name == tag.name) {
				TagCompound t = (TagCompound)tag;
				this.value = new ArrayList<NbtTag>(t.getValue());
			}
		} else if (value instanceof List) {
			List<?> v = (List<?>)value;
			this.value = new ArrayList<NbtTag>();
			if (v.size() > 0) {
				if (!(v.get(0) instanceof NbtTag))
					throw new InvalidDataType(v.get(0).getClass(), NbtTag.class);
				for (int i = 0; i < v.size(); i++) {
					this.value.add((NbtTag)v.get(i));
				}
			}
		} else {
			throw new InvalidDataType(value.getClass(), List.class);
		}
	}
	
	@Override
	public NbtTag get(String path) {
		String[] pth = path.split("\\.", 2);
		for (NbtTag tag : this.value) {
			if (tag.getName().compareTo(pth[0]) == 0) {
				if (pth.length == 2)
					return (NbtTag)tag.get(pth[1]);
				else
					return tag;
			}
		}
		return null;
	}

	@Override
	public NbtTag copy() {
		List<NbtTag> val = new ArrayList<NbtTag>();
		for (NbtTag tag : this.value) {
			val.add(tag.copy());
		}
		return new TagCompound(this.name, val);

	}

	@Override
	protected Class<?> getType() {
		return List.class;
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
