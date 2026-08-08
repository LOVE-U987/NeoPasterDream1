execute if block ~ ~-1 ~ dirt run fill ~ ~-1 ~ ~ ~-5 ~ minecraft:prismarine replace dirt
execute if block ~ ~-1 ~ sand run fill ~ ~-1 ~ ~ ~-5 ~ minecraft:prismarine_bricks replace sand
execute if block ~ ~-1 ~ clay run fill ~ ~-1 ~ ~ ~-5 ~ minecraft:dark_prismarine replace clay
execute unless entity @e[type=minecraft:area_effect_cloud,tag=spike,distance=..7] if block ~ 4 ~ minecraft:bedrock unless block ~ 1 ~ minecraft:bedrock unless block ~ 2 ~ bedrock if block ~ ~3 ~ water if block ~ ~ ~ water run function trees:prismarine/spike
execute if block ~ 4 ~ minecraft:bedrock if block ~ 3 ~ minecraft:bedrock if block ~ 2 ~ minecraft:bedrock unless block ~ 1 ~ minecraft:bedrock run setblock ~ ~ ~ minecraft:sea_lantern
fill ~ 0 ~ ~15 60 ~15 minecraft:prismarine replace stone
fill ~ 0 ~ ~15 60 ~15 minecraft:prismarine_bricks replace andesite
fill ~ 0 ~ ~15 60 ~15 minecraft:dark_prismarine replace diorite
fill ~ 0 ~ ~15 60 ~15 minecraft:dark_prismarine replace granite
fill ~ 0 ~ ~15 60 ~15 minecraft:prismarine replace dirt
