#execute if block ~ 4 ~ minecraft:bedrock run setblock ~ ~ ~ minecraft:grass
#execute if block ~ 4 ~ minecraft:bedrock if block ~ 3 ~ minecraft:bedrock run setblock ~ ~ ~ minecraft:large_fern[half=lower]
#execute if block ~ 4 ~ minecraft:bedrock if block ~ 3 ~ minecraft:bedrock run setblock ~ ~1 ~ minecraft:large_fern[half=upper]
#execute if block ~ 2 ~ minecraft:bedrock if block ~ 3 ~ minecraft:bedrock run setblock ~ ~ ~ minecraft:red_mushroom
#execute if block ~ 2 ~ minecraft:bedrock if block ~ 3 ~ minecraft:bedrock run setblock ~ ~-1 ~ minecraft:podzol
#execute if block ~ 3 ~ minecraft:bedrock if block ~ 1 ~ minecraft:bedrock run setblock ~ ~ ~ minecraft:fern
execute if predicate trees:beach if block ~ 3 ~ minecraft:bedrock if block ~ 2 ~ minecraft:bedrock if block ~ 1 ~ minecraft:bedrock if block ~ 1 ~ minecraft:bedrock run setblock ~ ~-1 ~ minecraft:coarse_dirt
execute if predicate trees:beach if block ~ 3 ~ minecraft:bedrock unless block ~ 1 ~ minecraft:bedrock run setblock ~ ~ ~ minecraft:cobblestone_slab
#execute if block ~ 4 ~ minecraft:bedrock if block ~ 1 ~ minecraft:bedrock run setblock ~ ~-1 ~ minecraft:podzol
execute unless block ~ 3 ~ minecraft:bedrock unless block ~ 1 ~ minecraft:bedrock if predicate trees:beach run setblock ~ ~ ~ minecraft:sea_pickle[waterlogged=false]
execute if predicate trees:beach if block ~ 4 ~ minecraft:bedrock unless block ~ 4 ~-1 minecraft:bedrock unless block ~ 4 ~1 minecraft:bedrock unless block ~-1 4 ~ minecraft:bedrock unless block ~1 4 ~ minecraft:bedrock unless block ~1 4 ~-1 minecraft:bedrock unless block ~1 4 ~1 minecraft:bedrock unless block ~-1 4 ~-1 minecraft:bedrock unless block ~-1 4 ~1 minecraft:bedrock unless entity @e[type=minecraft:area_effect_cloud,tag=palm,distance=..7] run function trees:palm/handletree