package NBT;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.*;

public abstract class NbtTag {
	
	protected String name;
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public abstract Object getValue();
	public abstract byte getId();
	public abstract int getLength();

	public abstract NbtTag get(String path);
	public abstract void set(String path, Object value);
	public abstract void set(Object value);
	public abstract NbtTag copy();

	protected abstract Class<?> getType();
	
	/**SNBT String*/
	public abstract String toString();
	public abstract byte[] toNBTFormat();
	abstract byte[] getValueAsNBT();
	abstract int getNBTValueLength();
	abstract String getValueAsString();
	
	
	public static NbtTag parse(byte[] tag) {
		switch (tag[0]) {
		case 1: //byte
			{
				int nameLength = getNameLength(tag);
				return new TagByte(getTagName(tag, nameLength), tag[3 + nameLength]);
			}
		case 2: //short
			{
				int nameLength = getNameLength(tag);
				return new TagShort(getTagName(tag, nameLength), ByteBuffer.wrap(tag, 3 + nameLength, Short.BYTES).getShort());
			}
		case 3: //int
			{
				int nameLength = getNameLength(tag);
				return new TagInt(getTagName(tag, nameLength), ByteBuffer.wrap(tag, 3 + nameLength, Integer.BYTES).getInt());
			}
		case 4: //long
			{
				int nameLength = getNameLength(tag);
				return new TagLong(getTagName(tag, nameLength), ByteBuffer.wrap(tag, 3 + nameLength, Long.BYTES).getLong());
			}
		case 5: //float
			{
				int nameLength = getNameLength(tag);
				return new TagFloat(getTagName(tag, nameLength), ByteBuffer.wrap(tag, 3 + nameLength, Float.BYTES).getFloat());
			}
		case 6: //double
			{
				int nameLength = getNameLength(tag);
				return new TagDouble(getTagName(tag, nameLength), ByteBuffer.wrap(tag, 3 + nameLength, Double.BYTES).getDouble());
			}
		case 7: //byte array
			{
				int nameLength = getNameLength(tag);
				int arrayLength = ByteBuffer.wrap(tag, 3 + nameLength, Integer.BYTES).getInt();
				byte[] array = Arrays.copyOfRange(tag, Integer.BYTES + 3 + nameLength, Integer.BYTES + 3 + nameLength + arrayLength);
				return new TagByteArray(getTagName(tag, nameLength), array);
			}
		case 8: //string
			{
				int nameLength = getNameLength(tag);
				short stringLength = ByteBuffer.wrap(tag, 3 + nameLength, Short.BYTES).getShort();
				String content = "";
				try { content = new String(Arrays.copyOfRange(tag, 5 + nameLength, 5 + nameLength + stringLength), "UTF-8"); } catch (Exception e) { throw new InvalidNbtException("Strings in nbt data must be encoded in UTF-8"); }
				return new TagString(getTagName(tag, nameLength), content);
			}
		case 9: //list
			{
				int nameLength = getNameLength(tag);
				int i = 4 + nameLength + Integer.BYTES;
				byte typeId = tag[3 + nameLength];
				if (typeId == 0) 
					return new TagList(getTagName(tag, nameLength), new NbtTag[0]);
				int length = ByteBuffer.wrap(tag, i - Integer.BYTES, Integer.BYTES).getInt();
				NbtTag[] list = new NbtTag[length];
				
				int off = 0;
				for (int j = 0; j < length; j++) {
					int pos = i + off;
					NbtTag newTag = parse(ByteBuffer.allocate(3 + tag.length - pos).put(new byte[] {typeId, 0, 0}).put(Arrays.copyOfRange(tag, pos, tag.length)).array());
					off += newTag.getNBTValueLength();
					list[j] = newTag;
				}

				return new TagList(getTagName(tag, nameLength), list);
			}
		case 10: //compound
			{
				int nameLength = getNameLength(tag);
				List<NbtTag> list = new ArrayList<NbtTag>();

				int i = 3 + nameLength;
				while (i < tag.length) {
					if (tag[i] == 0) break;
					NbtTag newTag = parse(Arrays.copyOfRange(tag, i, tag.length));
					i += newTag.getLength();
					list.add(newTag);
				}

				return new TagCompound(getTagName(tag, nameLength), list);
			}
		case 11: //int array
			{
				int nameLength = getNameLength(tag);
				int arrayLength = ByteBuffer.wrap(tag, 3 + nameLength, Integer.BYTES).getInt();
				int[] array = new int[arrayLength];
				for (int i = 0; i < arrayLength; i++) {
					int k = 3 + nameLength + Integer.BYTES + (i * Integer.BYTES);
					array[i] = ByteBuffer.wrap(tag, k, Integer.BYTES).getInt();
				}
				return new TagIntArray(getTagName(tag, nameLength), array);
			}
		case 12: //long array
			{
				int nameLength = getNameLength(tag);
				int arrayLength = ByteBuffer.wrap(tag, 3 + nameLength, Integer.BYTES).getInt();
				long[] array = new long[arrayLength];
				for (int i = 0; i < arrayLength; i++) {
					int k = 3 + nameLength + Integer.BYTES + (i * Long.BYTES);
					array[i] = ByteBuffer.wrap(tag, k, Long.BYTES).getLong();
				}
				return new TagLongArray(getTagName(tag, nameLength), array);
			}
		default:
			throw new InvalidNbtException("nbt type " + tag[0] + " does not exist");
		}
	}
	
	private static int getNameLength(byte[] tag) {
		return ByteBuffer.wrap(Arrays.copyOfRange(tag, 1, 3)).getShort();
	}
	
	private static String getTagName(byte[] tag, int nameLength) {
		return new String(Arrays.copyOfRange(tag, 3, 3 + nameLength));
	}


	private final static Pattern pCompound = Pattern.compile("\\{(.*)\\}");

	private final static Pattern pInteger = Pattern.compile("(-?\\p{Digit}+)i?");
	private final static Pattern pLong = Pattern.compile("(-?\\p{Digit}+)(?:l|L)");
	private final static Pattern pByte = Pattern.compile("(-?\\p{Digit}+)(?:b|B)");
	private final static Pattern pShort = Pattern.compile("(-?\\p{Digit}+)(?:s|S)");

	private final static Pattern pFloat = Pattern.compile("(-?(?:\\p{Digit}+(?:\\.\\p{Digit}+)?|\\.\\p{Digit}+)(?:(?:e|E)\\p{Digit}+)?)f?");
	private final static Pattern pDouble = Pattern.compile("(-?(?:\\p{Digit}+(?:\\.\\p{Digit}+)?|\\.\\p{Digit}+)(?:(?:e|E)\\p{Digit}+)?)(?:d|D)");

	private final static Pattern pString = Pattern.compile("(?:\"|')(\\p{Print}*)(?:\"|')");
	
	private final static Pattern pByteArray = Pattern.compile("\\[B;(.*)\\]");
	private final static Pattern pIntArray = Pattern.compile("\\[I;(.*)\\]");
	private final static Pattern pLongArray = Pattern.compile("\\[L;(.*)\\]");
	private final static Pattern pArray = Pattern.compile("\\[(.*)\\]");



	public static NbtTag parse(String tag) throws InvalidNbtException {
		//creating a new String we can modify
		StringBuilder nbt = new StringBuilder(tag);

		//suppressing the spaces and the newlines
		boolean inString = false;
		for (int i = 0; i < nbt.length(); i++) {
			if (nbt.charAt(i) == '\"' || nbt.charAt(i) == '\'') {
				if (i > 0) {
					if (nbt.charAt(i-1) != '\\')
						inString = !inString;
				} else {
					inString = !inString;
				}
			}
			if ((nbt.charAt(i) == ' ' || nbt.charAt(i) == '\t' || nbt.charAt(i) == '\n' || nbt.charAt(i) == '\r') && !inString) {
				nbt.deleteCharAt(i--);
			}
		}

		//finding what type it is
		Matcher m;

		if ((m = pCompound.matcher(nbt)).matches()) {
			TagCompound result = new TagCompound(new ArrayList<NbtTag>());
			//splitting the string by values
			String[] values = NbtTag.splitValues(m.group(1));
			for (String s : values) {
				String[] vals = s.split(":", 2);
				if (vals.length != 2)
					throw new InvalidNbtException("Missing tag value for \"" + s + "\"");
				NbtTag t = NbtTag.parse(vals[1]);
				t.setName(vals[0]);
				result.add(t);
			}
			return result;
		} else if ((m = pInteger.matcher(nbt)).matches()) {
			return new TagInt(Integer.parseInt(m.group(1)));
		} else if ((m = pLong.matcher(nbt)).matches()) {
			return new TagLong(Long.parseLong(m.group(1)));
		} else if ((m = pByte.matcher(nbt)).matches()) {
			return new TagByte(Byte.parseByte(m.group(1)));
		} else if ((m = pShort.matcher(nbt)).matches()) {
			return new TagShort(Short.parseShort(m.group(1)));
		} else if ((m = pFloat.matcher(nbt)).matches()) {
			return new TagFloat(Float.parseFloat(m.group(1)));
		} else if ((m = pDouble.matcher(nbt)).matches()) {
			return new TagDouble(Double.parseDouble(m.group(1)));
		} else if (nbt.compareTo(new StringBuilder("Infinity")) == 0 || nbt.compareTo(new StringBuilder("infinity")) == 0) {
			return new TagDouble(Double.POSITIVE_INFINITY);
		} else if (nbt.compareTo(new StringBuilder("-Infinity")) == 0 || nbt.compareTo(new StringBuilder("-infinity")) == 0) {
			return new TagDouble(Double.NEGATIVE_INFINITY);
		} else if ((m = pString.matcher(nbt)).matches()) {
			return new TagString(m.group(1));
		} else if ((m = pByteArray.matcher(nbt)).matches()) {
			String[] vals = m.group(1).split(",");
			byte[] bytes = new byte[vals.length];
			for (int i = 0; i < vals.length; i++)
				bytes[i] = Byte.parseByte(vals[i]);
			return new TagByteArray(bytes);
		} else if ((m = pIntArray.matcher(nbt)).matches()) {
			String[] vals = m.group(1).split(",");
			int[] ints = new int[vals.length];
			for (int i = 0; i < vals.length; i++)
				ints[i] = Integer.parseInt(vals[i]);
			return new TagIntArray(ints);
		} else if ((m = pLongArray.matcher(nbt)).matches()) {
			String[] vals = m.group(1).split(",");
			long[] longs = new long[vals.length];
			for (int i = 0; i < vals.length; i++)
				longs[i] = Long.parseLong(vals[i]);
			return new TagLongArray(longs);
		} else if ((m = pArray.matcher(nbt)).matches()) {
			String[] vals = NbtTag.splitValues(m.group(1));
			NbtTag[] tags = new NbtTag[vals.length];
			for (int i = 0; i < vals.length; i++) {
				tags[i] = NbtTag.parse(vals[i]);
				if (tags[0].getId() != tags[i].getId())
					throw new InvalidNbtException("All element of a list must be of the same type, at \"" + nbt + "\"");
			}
			return new TagList(tags);
		} else {
			throw new InvalidNbtException("Invalid nbt data");
		}
	}

	private static String[] splitValues(String nbt) {
		if (nbt.isBlank())
			return new String[0];
		
		Stack<Character> skipUntil = new Stack<Character>();
		int lastIndex = 0;
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < nbt.length();) {
			if (nbt.charAt(i) == ',') {
				result.add(nbt.substring(lastIndex, i));
				lastIndex = i + 1;
			}
			do {
				if (nbt.charAt(i) == '\"') {
					if (i > 0) {
						if (nbt.charAt(i-1) != '\\')
							skipUntil.push('\"');
					} else skipUntil.push('\"');
				}
				if (nbt.charAt(i) == '\'') {
					if (i > 0) {
						if (nbt.charAt(i-1) != '\\')
							skipUntil.push('\'');
					} else skipUntil.push('\'');
				}
				if (nbt.charAt(i) == '[')
					skipUntil.push(']');
				if (nbt.charAt(i) == '{')
					skipUntil.push('}');
				if (skipUntil.size() > 0) {
					if (nbt.charAt(i) == skipUntil.peek()) {
						if (i > 0) {
							if (nbt.charAt(i-1) != '\\')
								skipUntil.pop();
						} else {
							skipUntil.pop();
						}
					}
				}
				if (++i >= nbt.length() && skipUntil.size() > 0)
						throw new InvalidNbtException("Missing enclosing " + skipUntil);
			} while (skipUntil.size() != 0);
		}
		result.add(nbt.substring(lastIndex, nbt.length()));
		String[] r = new String[result.size()];
		result.toArray(r);
		return r;
	}
}
