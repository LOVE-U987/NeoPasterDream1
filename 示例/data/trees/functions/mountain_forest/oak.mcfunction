fill ~-3 ~ ~-3 ~3 ~15 ~3 air replace minecraft:spruce_leaves[persistent=false]
fill ~ ~ ~ ~ ~15 ~ air replace spruce_log
execute if block ~ 1 ~ bedrock run setblock ~ ~ ~ minecraft:structure_block{name:"trees:poplar",posX:-2,posY:0,posZ:-2,rotation:"NONE",mirror:"NONE",mode:"LOAD"} replace
execute if block ~ 2 ~ bedrock run setblock ~ ~ ~ minecraft:structure_block{name:"trees:poplar",posX:2,posY:0,posZ:-2,rotation:"CLOCKWISE_90",mirror:"NONE",mode:"LOAD"} replace
execute if block ~ 3 ~ bedrock run setblock ~ ~ ~ minecraft:structure_block{name:"trees:poplar",posX:2,posY:0,posZ:2,rotation:"CLOCKWISE_180",mirror:"NONE",mode:"LOAD"} replace
execute if block ~ 4 ~ bedrock run setblock ~ ~ ~ minecraft:structure_block{name:"trees:poplar",posX:-2,posY:0,posZ:2,rotation:"COUNTERCLOCKWISE_90",mirror:"NONE",mode:"LOAD"} replace
execute unless block ~ 1 ~ bedrock unless block ~ 2 ~ bedrock unless block ~ 3 ~ bedrock unless block ~ 4 ~ bedrock run setblock ~ ~ ~ minecraft:structure_block{name:"trees:poplar",posX:-2,posY:0,posZ:2,rotation:"COUNTERCLOCKWISE_90",mirror:"NONE",mode:"LOAD"} replace
setblock ~ ~1 ~ minecraft:redstone_block