fill ~-20 ~ ~-20 ~20 ~5 ~20 air replace minecraft:spruce_leaves[persistent=false]
fill ~-20 ~5 ~-20 ~20 ~10 ~20 air replace minecraft:spruce_leaves[persistent=false]
fill ~-20 ~10 ~-20 ~20 ~20 ~20 air replace minecraft:spruce_leaves[persistent=false]

fill ~-15 ~-10 ~-15 ~15 ~10 ~15 air replace spruce_log
fill ~-15 ~10 ~-15 ~15 ~30 ~15 air replace spruce_log

execute run setblock ~ ~2 ~ minecraft:structure_block{name:"trees:redwood/redwood1b",posX:-11,posY:39,posZ:-14,rotation:"NONE",mirror:"NONE",mode:"LOAD"} replace
execute if block ~ 2 ~ bedrock run setblock ~ ~2 ~ minecraft:structure_block{name:"trees:redwood/redwood1b",posX:14,posY:39,posZ:-11,rotation:"CLOCKWISE_90",mirror:"NONE",mode:"LOAD"} replace
execute if block ~ 3 ~ bedrock run setblock ~ ~2 ~ minecraft:structure_block{name:"trees:redwood/redwood1b",posX:11,posY:39,posZ:14,rotation:"CLOCKWISE_180",mirror:"NONE",mode:"LOAD"} replace
execute if block ~ 4 ~ bedrock run setblock ~ ~2 ~ minecraft:structure_block{name:"trees:redwood/redwood1b",posX:-14,posY:39,posZ:11,rotation:"COUNTERCLOCKWISE_90",mirror:"NONE",mode:"LOAD"} replace
setblock ~ ~3 ~ minecraft:redstone_block
execute run setblock ~ ~ ~ minecraft:structure_block{name:"trees:redwood/redwood1a",posX:-11,posY:-7,posZ:-14,rotation:"NONE",mirror:"NONE",mode:"LOAD"} replace
execute if block ~ 2 ~ bedrock run setblock ~ ~ ~ minecraft:structure_block{name:"trees:redwood/redwood1a",posX:14,posY:-7,posZ:-11,rotation:"CLOCKWISE_90",mirror:"NONE",mode:"LOAD"} replace
execute if block ~ 3 ~ bedrock run setblock ~ ~ ~ minecraft:structure_block{name:"trees:redwood/redwood1a",posX:11,posY:-7,posZ:14,rotation:"CLOCKWISE_180",mirror:"NONE",mode:"LOAD"} replace
execute if block ~ 4 ~ bedrock run setblock ~ ~ ~ minecraft:structure_block{name:"trees:redwood/redwood1a",posX:-14,posY:-7,posZ:11,rotation:"COUNTERCLOCKWISE_90",mirror:"NONE",mode:"LOAD"} replace
setblock ~ ~1 ~ minecraft:redstone_block
execute unless block ~ 1 ~ bedrock unless block ~ 2 ~ bedrock unless block ~ 3 ~ bedrock unless block ~ 4 ~ bedrock run setblock ~ ~1 ~ minecraft:air
execute unless block ~ 1 ~ bedrock unless block ~ 2 ~ bedrock unless block ~ 3 ~ bedrock unless block ~ 4 ~ bedrock run setblock ~ ~3 ~ minecraft:air