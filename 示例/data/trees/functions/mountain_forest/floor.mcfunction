#execute if block ~ 3 ~ minecraft:bedrock unless block ~ 1 ~ minecraft:bedrock run setblock ~ ~ ~ minecraft:cobblestone_slab
execute unless block ~ 2 ~ bedrock run setblock ~ ~-1 ~ podzol
execute if block ~ 3 ~ bedrock run setblock ~ ~-1 ~ coarse_dirt
execute if block ~ 3 ~ bedrock unless block ~ 3 ~ bedrock run setblock ~ ~-1 ~ gravel