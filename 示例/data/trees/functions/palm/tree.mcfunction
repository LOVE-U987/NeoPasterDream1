setblock ~ ~ ~ minecraft:structure_block{name:"trees:smallpalm1",posX:-2,posY:0,posZ:-1,rotation:"NONE",mirror:"NONE",mode:"LOAD"} replace
setblock ~ ~1 ~ minecraft:redstone_block
summon minecraft:area_effect_cloud ~ ~ ~ {Duration:2,Tags:["palm"]}