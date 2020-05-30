package interpreter;

import NBT.TagCompound;
import NBT.TagList;


class Parameter {
    
    private int type;
    private String name;
    private int val;

    public Parameter(TagCompound parameter) {
        this.type = (Integer)parameter.get("type").getValue();
        this.val = (Integer)parameter.get("value").getValue();
        this.name = ((TagList)parameter.get("name")).getValue().stream().map((x) -> (String)x.getValue()).reduce("", (x, y) -> x + y);
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return this.name;
    }

    public void setValue(int value) {
        this.val = value;
    }

    public int getValue() {
        return val;
    }
}