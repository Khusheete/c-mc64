package interpreter;

import NBT.TagCompound;
import NBT.TagList;


class Parameter {
    
    private int type;
    private String name;

    public Parameter(TagCompound parameter) {
        this.type = (Integer)parameter.get("type").getValue();
        this.name = ((TagList)parameter.get("name")).getValue().stream().map((x) -> (String)x.getValue()).reduce("", (x, y) -> x + y);
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return this.name;
    }
}