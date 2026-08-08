summon minecraft:armor_stand ~ ~ ~ {Invisible:1b,Tags:["StartTipi"]}
execute as @e[tag=StartTipi] at @s run spreadplayers ~ ~ 0 1 false @s
execute at @e[tag=StartTipi] run function structure:tipi/createvillage
kill @e[tag=StartTipi]
