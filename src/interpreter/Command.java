package interpreter;

import NBT.TagCompound;
import NBT.TagList;
import NBT.NbtTag;

import java.util.List;

class Command {
    
    private int command;
    private int mod;
    private Parameter[] parameters;

    public Command(TagCompound cmd) {
        this.command = (Integer)cmd.get("cmd").getValue();
        this.mod = (Integer)cmd.get("mod").getValue();
        this.parameters = new Parameter[7];
        List<NbtTag> parameters = ((TagList)cmd.get("parameters")).getValue();
        for (int i = 0; i < parameters.size(); i++) {
            this.parameters[i] = new Parameter((TagCompound)parameters.get(i));
        }
    }

    public int getCommand() {
        return command;
    }

    public int getMod() {
        return mod;
    }

    public Parameter[] getParameters() {
        return this.parameters;
    }
}