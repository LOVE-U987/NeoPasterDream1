function structure:minercamp/checkchunk
execute if predicate base:plain if block ~ 30 ~ iron_ore unless block ~ 1 ~ bedrock if block ~ 4 ~ minecraft:bedrock if block ~ 3 ~ minecraft:bedrock if block ~ 2 ~ minecraft:bedrock positioned ~-8 ~10 ~-8 run function structure:tipi/checkchunk
execute if predicate base:savanna run function structure:termite/spawnnest
execute if predicate base:savanna unless block ~ 1 ~ bedrock if block ~ 4 ~ minecraft:bedrock if block ~ 3 ~ minecraft:bedrock run function structure:termite/checkchunk