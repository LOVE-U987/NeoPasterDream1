execute as @e[type=item,nbt={Item:{id:"minecraft:player_head",tag:{SkullOwner:{Id:[I;257820640,175983470,-1220806382,-1360720541],Properties:{textures:[{Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzA2NTVkNjM4ZTRiNjIxOWZlNjU1ZjdiZWEzNGY5OTQ5NTQxZGQyMDc3NjRiMzYwODhlZDFiZmZkMWNjM2UxNiJ9fX0="}]}}}}}] run data merge entity @s {Item:{id:"minecraft:chorus_fruit"},tag:{}}

execute at @a if block ~ 254 ~ void_air run effect give @a minecraft:blindness 2 0 true
execute at @a if block ~ 254 ~ void_air run effect give @a minecraft:night_vision 1 0 true
execute at @a as @e[type=minecraft:enderman,tag=!a,distance=..340] at @s run function end:handlemob

