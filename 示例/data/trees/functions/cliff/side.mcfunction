#execute if block ~ 66 ~ #caves:caveblock if block ~ 4 ~ bedrock run fill ~ 72 ~ ~ 70 ~ coarse_dirt
#execute if block ~ 66 ~ #caves:caveblock if block ~ 2 ~ bedrock run fill ~ 72 ~ ~ 71 ~ coarse_dirt
#execute if block ~ 66 ~ #caves:caveblock run fill ~ 72 ~ ~ 72 ~ coarse_dirt
execute if block ~ 3 ~ bedrock run setblock ~ 73 ~ minecraft:grass_block
execute unless block ~ 3 ~ bedrock run setblock ~ 72 ~ minecraft:grass_block
execute if block ~ 4 ~ bedrock run fill ~ 72 ~ ~ 70 ~ coarse_dirt keep
execute unless block ~ 2 ~ bedrock run fill ~ 72 ~ ~ 71 ~ coarse_dirt keep
execute if block ~ 4 ~ bedrock if block ~ 1 ~ bedrock run setblock ~ 69 ~ minecraft:cobblestone_slab[type=top]
execute unless block ~ 2 ~ bedrock if block ~ 1 ~ bedrock run setblock ~ 70 ~ minecraft:cobblestone_slab[type=top]
execute unless block ~ 3 ~ bedrock if block ~ 2 ~ bedrock unless block ~ 4 ~ bedrock if block ~ 1 ~ bedrock run setblock ~ 71 ~ minecraft:cobblestone_slab[type=top]
execute if block ~ 3 ~ bedrock if block ~ 2 ~ bedrock unless block ~ 4 ~ bedrock if block ~ 1 ~ bedrock run setblock ~ 72 ~ minecraft:cobblestone_slab[type=top]