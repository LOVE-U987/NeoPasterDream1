setblock ~ ~-1 ~ minecraft:grass_block
#execute if block ~ 4 ~ minecraft:bedrock run setblock ~ ~ ~ minecraft:grass
#execute if block ~ 4 ~ minecraft:bedrock if block ~ 3 ~ minecraft:bedrock run setblock ~ ~ ~ minecraft:large_fern[half=lower]
#execute if block ~ 4 ~ minecraft:bedrock if block ~ 3 ~ minecraft:bedrock run setblock ~ ~1 ~ minecraft:large_fern[half=upper]
#execute if block ~ 2 ~ minecraft:bedrock if block ~ 3 ~ minecraft:bedrock run setblock ~ ~ ~ minecraft:red_mushroom
#execute if block ~ 2 ~ minecraft:bedrock if block ~ 3 ~ minecraft:bedrock run setblock ~ ~-1 ~ minecraft:podzol
#execute if block ~ 3 ~ minecraft:bedrock if block ~ 1 ~ minecraft:bedrock run setblock ~ ~ ~ minecraft:fern
#execute if block ~ 3 ~ minecraft:bedrock if block ~ 2 ~ minecraft:bedrock if block ~ 1 ~ minecraft:bedrock run setblock ~ ~-1 ~ minecraft:coarse_dirt
#execute if block ~ 4 ~ minecraft:bedrock unless block ~ 1 ~ minecraft:bedrock run setblock ~ ~ ~ minecraft:cobblestone_slab
#execute if block ~ 4 ~ minecraft:bedrock if block ~ 1 ~ minecraft:bedrock run setblock ~ ~-1 ~ minecraft:podzol
#execute unless block ~ 3 ~ minecraft:bedrock unless block ~ 1 ~ minecraft:bedrock run setblock ~ ~ ~ minecraft:sweet_berry_bush[age=3]
#execute if block ~ 20 ~ stone unless block ~ 4 ~ minecraft:bedrock unless block ~ 3 ~ minecraft:bedrock if block ~ 1 ~ minecraft:bedrock if block ~ 2 ~ minecraft:bedrock unless block ~1 2 ~ minecraft:bedrock unless block ~-1 2 ~ minecraft:bedrock unless block ~ 2 ~-1 minecraft:bedrock unless block ~ 2 ~1 minecraft:bedrock run function trees:conifer/bigtree
#execute unless block ~ 20 ~ stone unless block ~ 4 ~ minecraft:bedrock unless block ~ 3 ~ minecraft:bedrock if block ~ 1 ~ minecraft:bedrock if block ~ 2 ~ minecraft:bedrock unless block ~1 2 ~ minecraft:bedrock unless block ~-1 2 ~ minecraft:bedrock unless block ~ 2 ~-1 minecraft:bedrock unless block ~ 2 ~1 minecraft:bedrock run function trees:conifer/midtree
#execute if block ~ 4 ~ minecraft:bedrock if block ~ 1 ~ minecraft:bedrock if block ~ 2 ~ minecraft:bedrock unless block ~1 2 ~ minecraft:bedrock unless block ~-1 2 ~ minecraft:bedrock unless block ~ 2 ~-1 minecraft:bedrock unless block ~ 2 ~1 minecraft:bedrock run function trees:conifer/midtree
#execute unless block ~ 20 ~ stone run setblock ~ ~-1 ~ podzol
#execute if block ~ 20 ~ stone if block ~ 30 ~ stone run setblock ~ ~-1 ~ coarse_dirt
#execute if block ~ 20 ~ granite run setblock ~ ~-1 ~ podzol
#execute if block ~ 20 ~ andesite run setblock ~ ~-1 ~ coarse_dirt
#execute if block ~ 20 ~ dirt run setblock ~ ~-1 ~ coarse_dirt