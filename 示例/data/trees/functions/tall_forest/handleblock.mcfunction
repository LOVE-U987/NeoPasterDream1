#execute if block ~ ~ ~ minecraft:air if block ~ ~-1 ~ #trees:dirt run function trees:tall_forest/floor
execute if block ~ ~ ~ minecraft:birch_log unless block ~ ~15 ~ minecraft:birch_log if block ~ ~-1 ~ #trees:dirt run function trees:tall_forest/tree
execute if block ~ ~ ~ minecraft:oak_log unless block ~ ~15 ~ minecraft:oak_log if block ~ ~-1 ~ #trees:dirt run function trees:tall_forest/oak