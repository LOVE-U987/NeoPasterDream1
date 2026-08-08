fill ~-8 ~ ~-8 ~8 ~15 ~8 air replace minecraft:oak_leaves[persistent=false]
fill ~-5 ~ ~-5 ~5 ~15 ~5 air replace minecraft:oak_log[axis=x]
fill ~-5 ~ ~-5 ~5 ~15 ~5 air replace minecraft:oak_log[axis=z]
fill ~ ~ ~ ~ ~15 ~ air replace oak_log
execute if block ~ 1 ~ bedrock run setblock ~ ~ ~ minecraft:structure_block{name:"trees:blossom",posX:-7,posY:0,posZ:-9,rotation:"NONE",mirror:"NONE",mode:"LOAD"} replace
execute if block ~ 2 ~ bedrock run setblock ~ ~ ~ minecraft:structure_block{name:"trees:blossom",posX:9,posY:0,posZ:-7,rotation:"CLOCKWISE_90",mirror:"NONE",mode:"LOAD"} replace
execute if block ~ 3 ~ bedrock run setblock ~ ~ ~ minecraft:structure_block{name:"trees:blossom",posX:7,posY:0,posZ:9,rotation:"CLOCKWISE_180",mirror:"NONE",mode:"LOAD"} replace
execute if block ~ 4 ~ bedrock run setblock ~ ~ ~ minecraft:structure_block{name:"trees:blossom",posX:-9,posY:0,posZ:7,rotation:"COUNTERCLOCKWISE_90",mirror:"NONE",mode:"LOAD"} replace
setblock ~ ~1 ~ minecraft:redstone_block 
summon minecraft:area_effect_cloud ~ ~ ~ {Duration:1000,Tags:["tree"]}
execute unless block ~ 1 ~ bedrock unless block ~ 2 ~ bedrock unless block ~ 3 ~ bedrock unless block ~ 4 ~ bedrock run setblock ~ ~1 ~ minecraft:air
