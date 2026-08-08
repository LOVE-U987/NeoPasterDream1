execute if block ~ 1 ~ minecraft:bedrock run setblock ~ ~ ~ snow[layers=1]
execute if block ~ 2 ~ minecraft:bedrock run setblock ~ ~ ~ snow[layers=2]
execute if block ~ 4 ~ minecraft:bedrock run setblock ~ ~ ~ snow[layers=4]
execute if block ~ 3 ~ minecraft:bedrock run setblock ~ ~ ~ snow[layers=3]
fill ~ ~-1 ~ ~ ~-2 ~ minecraft:snow_block
execute unless block ~ 4 ~ minecraft:bedrock run fill ~ ~-1 ~ ~ ~-4 ~ minecraft:snow_block
execute unless block ~ 3 ~ minecraft:bedrock run fill ~ ~-1 ~ ~ ~-3 ~ minecraft:snow_block
fill ~ ~-1 ~ ~ ~-5 ~ minecraft:cyan_terracotta replace #trees:snowcap
execute unless block ~ 4 ~ minecraft:bedrock run fill ~ ~-1 ~ ~ ~-7 ~ minecraft:cyan_terracotta replace #trees:snowcap	
execute unless block ~ 3 ~ minecraft:bedrock run fill ~ ~-1 ~ ~ ~-6 ~ minecraft:cyan_terracotta replace #trees:snowcap
