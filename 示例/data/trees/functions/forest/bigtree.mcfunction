setblock ~ ~0 ~ minecraft:structure_block{name:"trees:forest/big",posX:-8,posY:0,posZ:-8,rotation:"NONE",mirror:"NONE",mode:"LOAD"} replace
setblock ~ ~1 ~ minecraft:redstone_block
summon minecraft:area_effect_cloud ~ ~ ~ {Duration:2,Tags:["forest"]}