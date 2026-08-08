summon minecraft:armor_stand ~ ~ ~ {Invisible:1b,Tags:["Tipi"]}
summon minecraft:armor_stand ~ ~ ~ {Invisible:1b,Tags:["Tipi"]}
summon minecraft:armor_stand ~ ~ ~ {Invisible:1b,Tags:["Tipi"]}
summon minecraft:armor_stand ~ ~ ~ {Invisible:1b,Tags:["Tipi"]}
spreadplayers ~ ~ 14 15 false @e[type=minecraft:armor_stand,tag=Tipi]
execute as @e[type=minecraft:armor_stand,tag=Tipi] at @s if block ~ ~-1 ~ minecraft:grass run tp @s ~ ~-1 ~
execute as @e[type=minecraft:armor_stand,tag=Tipi] at @s if entity @e[type=minecraft:armor_stand,distance=1..5] run kill @s
execute as @e[type=minecraft:armor_stand,tag=Tipi] at @s facing entity @p feet run tp ~ ~ ~
execute at @e[type=minecraft:armor_stand,y_rotation=-45..44,tag=Tipi] run setblock ~ ~ ~ minecraft:structure_block[mode=load]{powered:0b,rotation:"NONE",posX:-3,mode:"LOAD",posY:-1,sizeX:7,posZ:-3,integrity:1.0f,name:"structure:tipi",sizeY:10,sizeZ:7,showboundingbox:0b}
execute at @e[type=minecraft:armor_stand,y_rotation=45..134,tag=Tipi] run setblock ~ ~ ~ minecraft:structure_block[mode=load]{powered:0b,rotation:"CLOCKWISE_90",posX:3,mode:"LOAD",posY:-1,sizeX:7,posZ:-3,integrity:1.0f,name:"structure:tipi",sizeY:10,sizeZ:7,showboundingbox:0b}
execute at @e[type=minecraft:armor_stand,y_rotation=135..224,tag=Tipi] run setblock ~ ~ ~ minecraft:structure_block[mode=load]{powered:0b,rotation:"CLOCKWISE_180",posX:3,mode:"LOAD",posY:-1,sizeX:7,posZ:3,integrity:1.0f,name:"structure:tipi",sizeY:10,sizeZ:7,showboundingbox:0b}
execute at @e[type=minecraft:armor_stand,y_rotation=225..314,tag=Tipi] run setblock ~ ~ ~ minecraft:structure_block[mode=load]{powered:0b,rotation:"CLOCKWISE_270",posX:-3,mode:"LOAD",posY:-1,sizeX:7,posZ:-3,integrity:1.0f,name:"structure:tipi",sizeY:10,sizeZ:7,showboundingbox:0b}

execute at @e[type=minecraft:armor_stand,tag=Tipi] run setblock ~ ~1 ~ redstone_block
kill @e[type=armor_stand,tag=Tipi]