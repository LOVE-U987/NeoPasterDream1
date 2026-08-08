setblock ~ ~ ~ minecraft:structure_block{name:"trees:redwood/trunk",posX:-11,posY:-7,posZ:-14,rotation:"NONE",mirror:"NONE",mode:"LOAD"} replace
setblock ~ ~1 ~ minecraft:redstone_block
execute unless block ~ 1 ~ bedrock unless block ~ 2 ~ bedrock unless block ~ 3 ~ bedrock unless block ~ 4 ~ bedrock run setblock ~ ~1 ~ minecraft:air
summon minecraft:area_effect_cloud ~ ~ ~ {Duration:1000,Tags:["trunk"]}