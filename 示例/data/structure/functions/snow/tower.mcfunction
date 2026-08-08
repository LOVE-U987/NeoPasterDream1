setblock ~ ~ ~ minecraft:structure_block{name:"structure:snow/tower",posX:-5,posY:-3,posZ:-5,rotation:"NONE",mirror:"NONE",mode:"LOAD"} replace
setblock ~ ~1 ~ minecraft:redstone_block
#summon minecraft:area_effect_cloud ~ ~ ~ {Duration:1000,Tags:["trunk"]}