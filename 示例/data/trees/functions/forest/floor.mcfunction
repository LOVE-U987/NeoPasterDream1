execute if block ~ 1 ~ minecraft:bedrock run setblock ~ ~ ~ minecraft:grass
execute if block ~ 3 ~ minecraft:bedrock if block ~ 1 ~ minecraft:bedrock run setblock ~ ~ ~ minecraft:fern
execute if block ~ 3 ~ minecraft:bedrock if block ~ 2 ~ minecraft:bedrock if block ~ 1 ~ minecraft:bedrock run setblock ~ ~-1 ~ minecraft:coarse_dirt
execute if block ~ 4 ~ minecraft:bedrock unless block ~ 1 ~ minecraft:bedrock run setblock ~ ~ ~ minecraft:cobblestone_slab
execute if block ~ 4 ~ minecraft:bedrock if block ~ 1 ~ minecraft:bedrock run setblock ~ ~-1 ~ minecraft:podzol
execute if block ~ 4 ~ minecraft:bedrock if block ~ 1 ~ minecraft:bedrock if block ~ 2 ~ bedrock run setblock ~ ~ ~ minecraft:red_mushroom
execute unless block ~ 1 ~ minecraft:bedrock if block ~ 3 ~ minecraft:bedrock run setblock ~ ~ ~ minecraft:large_fern[half=lower]
execute unless block ~ 1 ~ minecraft:bedrock if block ~ 3 ~ minecraft:bedrock run setblock ~ ~1 ~ minecraft:large_fern[half=upper]

execute if block ~ 20 ~ stone if block ~ 4 ~ minecraft:bedrock unless block ~ 1 ~ bedrock unless entity @e[type=minecraft:area_effect_cloud,tag=forest,distance=..8] run function trees:forest/bigtree
execute unless block ~ 20 ~ stone if block ~ 4 ~ minecraft:bedrock unless block ~ 1 ~ bedrock unless entity @e[type=minecraft:area_effect_cloud,tag=forest,distance=..8] run function trees:forest/midtree
execute if block ~ 1 ~ bedrock if block ~ 3 ~ bedrock unless entity @e[type=minecraft:area_effect_cloud,tag=forest,distance=..8] run function trees:forest/smalltree