execute if block ~ 3 ~ bedrock run setblock ~ ~-1 ~ grass_path
execute unless block ~ 2 ~ minecraft:bedrock unless block ~ 4 ~ bedrock unless block ~ 3 ~ bedrock run setblock ~ ~-1 ~ coarse_dirt

execute if block ~ 4 ~ minecraft:bedrock if block ~ 1 ~ minecraft:bedrock run setblock ~ ~-1 ~ minecraft:farmland
execute if block ~ 4 ~ minecraft:bedrock if block ~ 1 ~ minecraft:bedrock run setblock ~ ~ ~ wheat[age=7]
execute if block ~ 4 ~ minecraft:bedrock if block ~ 1 ~ minecraft:bedrock run setblock ~ ~1 ~ air

#execute if block ~ 2 ~ minecraft:bedrock run setblock ~ ~ ~ grass
#execute if block ~ 2 ~ minecraft:bedrock run setblock ~ ~1 ~ air
#execute if block ~ 2 ~ minecraft:bedrock if block ~ 3 ~ minecraft:bedrock run setblock ~ ~ ~ minecraft:tall_grass[half=lower]
#execute if block ~ 2 ~ minecraft:bedrock if block ~ 3 ~ minecraft:bedrock run setblock ~ ~1 ~ minecraft:tall_grass[half=upper]
