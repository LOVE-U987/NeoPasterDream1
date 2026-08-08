execute at @a unless predicate base:end unless block ~ 0 ~ minecraft:barrier run summon minecraft:area_effect_cloud ~ 0 ~ {Tags:["ChunkGenerator"],Duration:2147483647}
execute at @a if predicate base:end unless block ~ 0 ~ minecraft:moving_piston run summon minecraft:area_effect_cloud ~ 0 ~ {Tags:["ChunkGenerator"],Duration:2147483647}

execute at @a unless predicate base:end positioned ~-8 0 ~-8 as @e[distance=0..,type=area_effect_cloud,tag=ChunkGenerator,sort=nearest,limit=1] at @s run function base:generate/overworld
execute as @a at @s if predicate base:end positioned ~-8 0 ~-8 as @e[distance=0..,type=area_effect_cloud,tag=ChunkGenerator,sort=nearest,limit=1] at @s run function base:generate/end

schedule function base:player 5t replace
