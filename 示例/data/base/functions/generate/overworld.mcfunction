execute store result entity @s Pos[0] double 16 run data get entity @s Pos[0] 0.0625
execute store result entity @s Pos[2] double 16 run data get entity @s Pos[2] 0.0625

execute at @s run fill ~ 0 ~ ~15 0 ~15 barrier replace bedrock
execute at @s run kill @e[type=area_effect_cloud,distance=..1,tag=ChunkGenerator]

execute at @s positioned ~16 ~ ~ if block ~ ~ ~ minecraft:bedrock run summon minecraft:area_effect_cloud ~ ~ ~ {Tags:["ChunkGenerator"],Duration:2147483647}
execute at @s positioned ~-16 ~ ~ if block ~ ~ ~ minecraft:bedrock run summon minecraft:area_effect_cloud ~ ~ ~ {Tags:["ChunkGenerator"],Duration:2147483647}
execute at @s positioned ~ ~ ~16 if block ~ ~ ~ minecraft:bedrock run summon minecraft:area_effect_cloud ~ ~ ~ {Tags:["ChunkGenerator"],Duration:2147483647}
execute at @s positioned ~ ~ ~-16 if block ~ ~ ~ minecraft:bedrock run summon minecraft:area_effect_cloud ~ ~ ~ {Tags:["ChunkGenerator"],Duration:2147483647}

execute at @s positioned ~8 ~ ~8 run function #base:overworld
execute at @s positioned ~-8 ~ ~-8 run kill @e[type=item,dx=16,dz=16]
kill @e[dx=16,dz=16,dy=255,type=item]