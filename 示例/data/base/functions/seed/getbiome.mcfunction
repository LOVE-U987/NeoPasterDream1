execute store result score x p run data get entity @p Pos[0] 0.0078125
scoreboard players operation x p %= 100 number
execute store result score z p run data get entity @p Pos[2] 0.0078125
scoreboard players operation z p %= 100 number
scoreboard players operation x p *= z p
scoreboard players operation total p = x p
scoreboard players operation total p %= 100 number