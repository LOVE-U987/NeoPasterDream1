fill ~-8 ~ ~-8 ~8 ~15 ~8 air replace minecraft:spruce_leaves[persistent=false]
fill ~-5 ~ ~-5 ~5 ~15 ~5 air replace spruce_log
execute if block ~ 1 ~ bedrock run setblock ~ ~ ~ minecraft:structure_block{name:"trees:conifers/small1",posX:-6,posY:-1,posZ:-6,rotation:"NONE",mirror:"NONE",mode:"LOAD"} replace
execute if block ~ 2 ~ bedrock run setblock ~ ~ ~ minecraft:structure_block{name:"trees:conifers/small1",posX:6,posY:-1,posZ:-6,rotation:"CLOCKWISE_90",mirror:"NONE",mode:"LOAD"} replace
execute if block ~ 3 ~ bedrock run setblock ~ ~ ~ minecraft:structure_block{name:"trees:conifers/small1",posX:6,posY:-1,posZ:6,rotation:"CLOCKWISE_180",mirror:"NONE",mode:"LOAD"} replace
execute if block ~ 4 ~ bedrock run setblock ~ ~ ~ minecraft:structure_block{name:"trees:conifers/small1",posX:-6,posY:-1,posZ:6,rotation:"COUNTERCLOCKWISE_90",mirror:"NONE",mode:"LOAD"} replace
setblock ~ ~1 ~ minecraft:redstone_block
execute unless block ~ 1 ~ bedrock unless block ~ 2 ~ bedrock unless block ~ 3 ~ bedrock unless block ~ 4 ~ bedrock run setblock ~ ~1 ~ minecraft:air