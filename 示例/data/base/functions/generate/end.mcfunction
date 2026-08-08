execute store result entity @s Pos[0] double 16 run data get entity @s Pos[0] 0.0625
execute store result entity @s Pos[2] double 16 run data get entity @s Pos[2] 0.0625
execute store result score OldX p run data get entity @s Pos[0]
execute store result score OldZ p run data get entity @s Pos[2]
execute at @s run fill ~ 0 ~ ~15 0 ~15 moving_piston
execute at @s run kill @e[type=area_effect_cloud,distance=..1,tag=ChunkGenerator]

execute at @s positioned ~16 ~ ~ unless block ~ ~ ~ minecraft:moving_piston run summon minecraft:area_effect_cloud ~ ~ ~ {Tags:["ChunkGenerator"],Duration:2147483647}
execute at @s positioned ~-16 ~ ~ unless block ~ ~ ~ minecraft:moving_piston run summon minecraft:area_effect_cloud ~ ~ ~ {Tags:["ChunkGenerator"],Duration:2147483647}
execute at @s positioned ~ ~ ~16 unless block ~ ~ ~ minecraft:moving_piston run summon minecraft:area_effect_cloud ~ ~ ~ {Tags:["ChunkGenerator"],Duration:2147483647}
execute at @s positioned ~ ~ ~-16 unless block ~ ~ ~ minecraft:moving_piston run summon minecraft:area_effect_cloud ~ ~ ~ {Tags:["ChunkGenerator"],Duration:2147483647}
execute as @s at @s run function #base:end