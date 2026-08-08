execute if block ~ 1 ~ minecraft:bedrock run setblock ~ ~ ~ snow[layers=1]
execute if block ~ 2 ~ minecraft:bedrock run setblock ~ ~ ~ snow[layers=2]
execute if block ~ 4 ~ minecraft:bedrock run setblock ~ ~ ~ snow[layers=4]
execute if block ~ 3 ~ minecraft:bedrock run setblock ~ ~ ~ snow[layers=3]
execute if block ~ 3 ~ minecraft:bedrock unless block ~ 1 ~ minecraft:bedrock run setblock ~ ~ ~ minecraft:cobblestone_slab
execute if block ~ 4 ~ minecraft:bedrock unless block ~ 1 ~ bedrock unless entity @e[type=minecraft:area_effect_cloud,tag=snow_tagia,distance=..3] run function trees:snow_tagia/tree