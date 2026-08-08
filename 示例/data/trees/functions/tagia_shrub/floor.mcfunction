#execute if block ~ 3 ~ minecraft:bedrock unless block ~ 1 ~ minecraft:bedrock run setblock ~ ~ ~ minecraft:cobblestone_slab
setblock ~ ~-1 ~ podzol
execute if block ~ 2 ~ bedrock run setblock ~ ~-1 ~ coarse_dirt
execute if block ~ 4 ~ bedrock unless block ~ 1 ~ bedrock run setblock ~ ~-1 ~ gravel
execute if block ~ 1 ~ bedrock if block ~ 3 ~ bedrock if block ~ 4 ~ bedrock run setblock ~ ~ ~ fern