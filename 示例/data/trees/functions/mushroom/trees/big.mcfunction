setblock ~ ~2 ~ minecraft:structure_block{name:"trees:mushroom/tallred0",posX:-11,posY:30,posZ:-11,rotation:"NONE",mirror:"NONE",mode:"LOAD"} replace
setblock ~ ~3 ~ minecraft:redstone_block

setblock ~ ~ ~ minecraft:structure_block{name:"trees:mushroom/tallred",posX:-11,posY:0,posZ:-11,rotation:"NONE",mirror:"NONE",mode:"LOAD"} replace
setblock ~ ~1 ~ minecraft:redstone_block
summon minecraft:area_effect_cloud ~ ~ ~ {Duration:2,Tags:["mushroom"]}