setblock ~ ~ ~ minecraft:structure_block{name:"structure:snow/pumpkin",posX:-4,posY:0,posZ:-4,rotation:"NONE",mirror:"NONE",mode:"LOAD"} replace
setblock ~ ~1 ~ minecraft:redstone_block
#summon minecraft:area_effect_cloud ~ ~ ~ {Duration:1000,Tags:["trunk"]}