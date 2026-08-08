execute if block ~ 66 ~ #base:caveblock run fill ~ 72 ~ ~ 62 ~ stone

execute if block ~ 66 ~ #base:caveblock if block ~ 4 ~ bedrock run fill ~ 72 ~ ~ 70 ~ coarse_dirt
execute if block ~ 66 ~ #base:caveblock if block ~ 2 ~ bedrock run fill ~ 72 ~ ~ 71 ~ coarse_dirt
execute if block ~ 66 ~ #base:caveblock run fill ~ 72 ~ ~ 72 ~ coarse_dirt
execute if block ~ 66 ~ #base:caveblock run setblock ~ 73 ~ minecraft:grass_block
execute positioned ~1 ~ ~ unless block ~ 66 ~ #base:caveblock run function trees:cliff/side
execute positioned ~-1 ~ ~ unless block ~ 66 ~ #base:caveblock run function trees:cliff/side
execute positioned ~ ~ ~1 unless block ~ 66 ~ #base:caveblock run function trees:cliff/side
execute positioned ~ ~ ~-1 unless block ~ 66 ~ #base:caveblock run function trees:cliff/side