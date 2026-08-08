execute if block ~ 4 ~ minecraft:bedrock if block ~ 3 ~ minecraft:bedrock run setblock ~ ~-1 ~ coarse_dirt
execute unless block ~ 1 ~ minecraft:bedrock if block ~ 3 ~ minecraft:bedrock run setblock ~ ~-1 ~ cobblestone
execute if block ~ 2 ~ minecraft:bedrock unless block ~ 3 ~ minecraft:bedrock if block ~ 1 ~ bedrock run setblock ~ ~-1 ~ minecraft:podzol

execute unless block ~ 1 ~ minecraft:bedrock unless block ~ 2 ~ minecraft:bedrock run setblock ~ ~-1 ~ minecraft:farmland
execute unless block ~ 1 ~ minecraft:bedrock unless block ~ 2 ~ minecraft:bedrock run setblock ~ ~ ~ wheat[age=7]
execute unless block ~ 1 ~ minecraft:bedrock unless block ~ 2 ~ minecraft:bedrock run setblock ~ ~1 ~ air

execute if block ~ 2 ~ minecraft:bedrock run setblock ~ ~ ~ grass
execute if block ~ 2 ~ minecraft:bedrock run setblock ~ ~1 ~ air
execute if block ~ 2 ~ minecraft:bedrock if block ~ 3 ~ minecraft:bedrock run setblock ~ ~ ~ minecraft:tall_grass[half=lower]
execute if block ~ 2 ~ minecraft:bedrock if block ~ 3 ~ minecraft:bedrock run setblock ~ ~1 ~ minecraft:tall_grass[half=upper]
execute unless block ~ 20 ~ cave_air unless block ~ 20 ~ stone unless block ~1 ~-1 ~ #trees:jungle unless block ~-1 ~-1 ~ #trees:jungle unless block ~ ~-1 ~1 #trees:jungle unless block ~ ~-1 ~-1 #trees:jungle run setblock ~ ~-1 ~ water
#execute if block ~ 20 ~ andesite run setblock ~ ~-1 ~ water