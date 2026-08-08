setblock ~ ~-1 ~ minecraft:red_sand
execute if block ~ 3 ~ minecraft:bedrock if block ~ 2 ~ minecraft:bedrock if block ~ 1 ~ minecraft:bedrock run setblock ~ ~-1 ~ minecraft:coarse_dirt
execute unless block ~ ~-1 ~ red_sand if block ~ 3 ~ minecraft:bedrock if block ~ 1 ~ minecraft:bedrock run setblock ~ ~ ~ minecraft:fern
execute unless block ~ ~-1 ~ red_sand if block ~ 4 ~ minecraft:bedrock run setblock ~ ~ ~ minecraft:grass
execute if block ~ 4 ~ minecraft:bedrock unless block ~ 1 ~ minecraft:bedrock run setblock ~ ~ ~ minecraft:dead_bush
#execute unless block ~ 20 ~ stone if block ~ 3 ~ minecraft:bedrock if block ~ 2 ~ minecraft:bedrock unless block ~1 2 ~ minecraft:bedrock unless block ~-1 2 ~ minecraft:bedrock unless block ~ 2 ~-1 minecraft:bedrock unless block ~ 2 ~1 minecraft:bedrock run function trees:outback/tall_bush
#execute if block ~ 20 ~ stone if block ~ 3 ~ minecraft:bedrock if block ~ 2 ~ minecraft:bedrock unless block ~1 2 ~ minecraft:bedrock unless block ~-1 2 ~ minecraft:bedrock unless block ~ 2 ~-1 minecraft:bedrock unless block ~ 2 ~1 minecraft:bedrock run function trees:outback/tall_bush
