setblock ~ ~ ~ minecraft:structure_block[mode=load]{name:"minecraft:termitenest",posX:-5,posY:0,posZ:-5,rotation:"NONE",mirror:"NONE",mode:"LOAD",powered:0b} replace
setblock ~ ~1 ~ redstone_block
execute positioned ~-8 ~ ~-8 run function caves:savanna/checkchunk