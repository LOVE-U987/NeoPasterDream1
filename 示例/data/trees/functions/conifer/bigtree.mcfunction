fill ~-12 ~ ~-12 ~12 ~15 ~12 air replace minecraft:spruce_leaves[persistent=false]
fill ~-11 ~-10 ~-11 ~11 ~30 ~11 air replace spruce_log
execute if block ~ 1 ~ bedrock run setblock ~ ~2 ~ minecraft:structure_block{name:"trees:conifers/conifer_b",posX:-10,posY:45,posZ:-10,rotation:"NONE",mirror:"NONE",mode:"LOAD"} replace
execute if block ~ 2 ~ bedrock run setblock ~ ~2 ~ minecraft:structure_block{name:"trees:conifers/conifer_b",posX:10,posY:45,posZ:-10,rotation:"CLOCKWISE_90",mirror:"NONE",mode:"LOAD"} replace
execute if block ~ 3 ~ bedrock run setblock ~ ~2 ~ minecraft:structure_block{name:"trees:conifers/conifer_b",posX:10,posY:45,posZ:10,rotation:"CLOCKWISE_180",mirror:"NONE",mode:"LOAD"} replace
execute if block ~ 4 ~ bedrock run setblock ~ ~2 ~ minecraft:structure_block{name:"trees:conifers/conifer_b",posX:-10,posY:45,posZ:10,rotation:"COUNTERCLOCKWISE_90",mirror:"NONE",mode:"LOAD"} replace
setblock ~ ~3 ~ minecraft:redstone_block
execute if block ~ 1 ~ bedrock run setblock ~ ~ ~ minecraft:structure_block{name:"trees:conifers/conifer_a",posX:-10,posY:-1,posZ:-10,rotation:"NONE",mirror:"NONE",mode:"LOAD"} replace
execute if block ~ 2 ~ bedrock run setblock ~ ~ ~ minecraft:structure_block{name:"trees:conifers/conifer_a",posX:10,posY:-1,posZ:-10,rotation:"CLOCKWISE_90",mirror:"NONE",mode:"LOAD"} replace
execute if block ~ 3 ~ bedrock run setblock ~ ~ ~ minecraft:structure_block{name:"trees:conifers/conifer_a",posX:10,posY:-1,posZ:10,rotation:"CLOCKWISE_180",mirror:"NONE",mode:"LOAD"} replace
execute if block ~ 4 ~ bedrock run setblock ~ ~ ~ minecraft:structure_block{name:"trees:conifers/conifer_a",posX:-10,posY:-1,posZ:10,rotation:"COUNTERCLOCKWISE_90",mirror:"NONE",mode:"LOAD"} replace
setblock ~ ~1 ~ minecraft:redstone_block
execute unless block ~ 1 ~ bedrock unless block ~ 2 ~ bedrock unless block ~ 3 ~ bedrock unless block ~ 4 ~ bedrock run setblock ~ ~1 ~ minecraft:air
execute unless block ~ 1 ~ bedrock unless block ~ 2 ~ bedrock unless block ~ 3 ~ bedrock unless block ~ 4 ~ bedrock run setblock ~ ~3 ~ minecraft:air