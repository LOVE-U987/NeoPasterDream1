execute if block ~ ~ ~ minecraft:air if block ~ ~-1 ~ #trees:dirt run function trees:tagia/floor
execute if block ~ ~ ~ minecraft:spruce_log[axis=y] if block ~ ~-1 ~ #trees:undertree unless block ~1 ~ ~ cobblestone unless block ~-1 ~ ~ cobblestone unless block ~ ~ ~1 cobblestone unless block ~ ~ ~-1 cobblestone run function trees:tagia/tree
