summon minecraft:armor_stand ~ ~ ~ {Invisible:1b,Tags:["StartMound"]}
execute as @e[tag=StartMound] at @s run spreadplayers ~ ~ 0 1 false @s
execute at @e[tag=StartMound] run function structure:termite/mound
kill @e[tag=StartMound]