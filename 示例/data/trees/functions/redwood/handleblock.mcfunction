execute if block ~ ~ ~ minecraft:air if block ~ ~-1 ~ #trees:dirt run function trees:redwood/floor
execute if block ~ ~ ~ minecraft:spruce_log[axis=y] if block ~ ~15 ~ spruce_log[axis=y] if block ~ ~-1 ~ #trees:undertree run function trees:redwood/bigtree
execute if block ~ ~ ~ minecraft:spruce_log[axis=y] unless entity @e[type=area_effect_cloud,distance=..50] unless block ~ ~15 ~ spruce_log[axis=y] if block ~ ~-1 ~ #trees:undertree unless block ~1 ~ ~ cobblestone unless block ~-1 ~ ~ cobblestone unless block ~ ~ ~1 cobblestone unless block ~ ~ ~-1 cobblestone run function trees:redwood/trunk
