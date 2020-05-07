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
	
	@Override
	public void set(Object value) {
		if (value instanceof Integer[]) {
			Integer[] vals = (Integer[])value;
			this.value = new int[vals.length];
			for (int i = 0; i < vals.length; i++) {
				this.value[i] = vals[i];
			}
		} else {
			throw new InvalidDataType(value.getClass(), Integer[].class);
		}
	}

	@Override
	public void set(String path, Object value) {
		if (value instanceof Integer) {
			int index = Integer.parseInt(path);
			this.set(index, (Integer)value);
		} else {
			throw new InvalidDataType(value.getClass(), Integer.class);
		}
	}

	@Override
	public NbtTag copy() {
		return new TagIntArray(this.name, this.value.clone());
	}

	public int get(int index) {
		return this.value[index];
	}
	
	public void set(int index, int value) {
		this.value[index] = value;
	}

	@Override
	public NbtTag get(String path) {
		throw new NbtException("Cannot get value with path for a TagIntArray");
	}

	@Override
	protected Class<?> getType() {
		return Integer[].class;
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
