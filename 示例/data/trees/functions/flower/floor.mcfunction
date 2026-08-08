execute if block ~ 3 ~ bedrock if block ~ 1 ~ bedrock run setblock ~ ~-1 ~ podzol
execute unless block ~ 2 ~ bedrock if block ~ 1 ~ bedrock run setblock ~ ~-1 ~ coarse_dirt
execute unless block ~ 1 ~ bedrock if block ~ 2 ~ bedrock run setblock ~ ~-1 ~ minecraft:brown_concrete_powder
execute if block ~ 4 ~ bedrock unless block ~ 1 ~ bedrock run setblock ~ ~ ~ cobblestone_slab


execute if block ~ 20 ~ dirt if block ~ 1 ~ minecraft:bedrock run setblock ~ ~-1 ~ minecraft:honey_block
execute if block ~ 20 ~ dirt if block ~ 1 ~ minecraft:bedrock if block ~ 4 ~ minecraft:bedrock if block ~ 2 ~ minecraft:bedrock run setblock ~ ~-1 ~ minecraft:honeycomb_block

execute if block ~ 4 ~ minecraft:bedrock if block ~ 2 ~ minecraft:bedrock if block ~ ~-1 ~ #trees:dirt run setblock ~ ~ ~ minecraft:rose_bush[half=lower]
execute if block ~ 4 ~ minecraft:bedrock if block ~ 2 ~ minecraft:bedrock if block ~ ~-1 ~ #trees:dirt run setblock ~ ~1 ~ minecraft:rose_bush[half=upper]

execute if block ~ 4 ~ minecraft:bedrock if block ~ 3 ~ minecraft:bedrock if block ~ ~-1 ~ #trees:dirt run setblock ~ ~ ~ minecraft:peony[half=lower]
execute if block ~ 4 ~ minecraft:bedrock if block ~ 3 ~ minecraft:bedrock if block ~ ~-1 ~ #trees:dirt run setblock ~ ~1 ~ minecraft:peony[half=upper]

execute unless block ~ 1 ~ minecraft:bedrock if block ~ 2 ~ minecraft:bedrock if block ~ ~-1 ~ #trees:dirt run setblock ~ ~ ~ minecraft:lilac[half=lower]
execute unless block ~ 1 ~ minecraft:bedrock if block ~ 2 ~ minecraft:bedrock if block ~ ~-1 ~ #trees:dirt run setblock ~ ~1 ~ minecraft:lilac[half=upper]


#----------Big Flowers----------
execute unless entity @e[type=minecraft:area_effect_cloud,tag=flower,distance=..15] if block ~ 4 ~ minecraft:bedrock run function trees:flower/trees/1
execute unless entity @e[type=minecraft:area_effect_cloud,tag=flower,distance=..15] if block ~ 3 ~ minecraft:bedrock run function trees:flower/trees/2
execute unless entity @e[type=minecraft:area_effect_cloud,tag=flower,distance=..15] unless block ~ 2 ~ minecraft:bedrock run function trees:flower/trees/3
execute unless entity @e[type=minecraft:area_effect_cloud,tag=flower,distance=..15] unless block ~ 1 ~ minecraft:bedrock run function trees:flower/trees/4
execute unless entity @e[type=minecraft:area_effect_cloud,tag=flower,distance=..10] if block ~ 2 ~ minecraft:bedrock if block ~ 3 ~ minecraft:bedrock run function trees:flower/trees/small