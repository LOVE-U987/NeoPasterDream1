fill ~-3 ~ ~-3 ~3 ~15 ~3 air replace minecraft:spruce_leaves[persistent=false]
fill ~ ~ ~ ~ ~15 ~ air replace spruce_log
execute if block ~ 1 ~ bedrock run setblock ~ ~ ~ minecraft:structure_block{name:"trees:taiga/shrub2",posX:-3,posY:0,posZ:-3,rotation:"NONE",mirror:"NONE",mode:"LOAD"} replace
execute if block ~ 2 ~ bedrock run setblock ~ ~ ~ minecraft:structure_block{name:"trees:taiga/shrub2",posX:3,posY:0,posZ:-3,rotation:"CLOCKWISE_90",mirror:"NONE",mode:"LOAD"} replace
execute if block ~ 3 ~ bedrock run setblock ~ ~ ~ minecraft:structure_block{name:"trees:taiga/shrub2",posX:3,posY:0,posZ:3,rotation:"CLOCKWISE_180",mirror:"NONE",mode:"LOAD"} replace
execute if block ~ 4 ~ bedrock run setblock ~ ~ ~ air
execute unless block ~ 4 ~ bedrock run setblock ~ ~1 ~ minecraft:redstone_block
execute unless block ~ 1 ~ bedrock unless block ~ 2 ~ bedrock unless block ~ 3 ~ bedrock unless block ~ 4 ~ bedrock run setblock ~ ~1 ~ minecraft:air