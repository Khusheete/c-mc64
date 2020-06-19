#loads from a structure block


#replace the blocks so we can load the structure
fill -64 1 67 -64 1 68 air replace
setblock -64 1 68 minecraft:structure_block[mode=load]{metadata:"",mirror:"NONE",ignoreEntities:1b,powered:0b,seed:0L,author:"Siandfrance",rotation:"NONE",posX:0,mode:"LOAD",posY:0,sizeX:1,posZ:-1,integrity:1.0f,showair:0b,name:"c-mc64:prog",sizeY:1,sizeZ:1,showboundingbox:1b}

#load the program
setblock -64 2 68 redstone_block
setblock -64 2 68 air

#copy the program into sys:program.cmd
data modify storage sys program.cmd set from block -64 1 67 Items[0].tag.program

function sys:io/console/program/tick