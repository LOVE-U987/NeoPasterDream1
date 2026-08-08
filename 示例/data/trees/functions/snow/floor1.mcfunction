#execute if block ~ 4 ~ minecraft:bedrock run setblock ~ ~ ~ snow[layers=4]
#execute if block ~ 3 ~ minecraft:bedrock run setblock ~ ~ ~ snow[layers=3]
execute if block ~ 2 ~ minecraft:bedrock run setblock ~ ~ ~ snow[layers=6]
execute if block ~ 1 ~ minecraft:bedrock run setblock ~ ~ ~ snow[layers=7]
execute unless block ~ 1 ~ minecraft:bedrock run setblock ~ ~-1 ~ snow_block