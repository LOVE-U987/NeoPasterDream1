function base:seed/getbiome
execute if predicate base:birch positioned ~-8 ~ ~-8 run function trees:aspen/checkchunk

#execute if predicate base:jungle positioned ~-8 ~ ~-8 run function trees:jungle/checkchunk
#execute if predicate base:mushroom positioned ~-8 ~ ~-8 run function trees:mushroom/checkchunk
execute if predicate base:swamp positioned ~-8 ~ ~-8 run function trees:willow/checkchunk

execute if predicate trees:beach positioned ~-8 ~ ~-8 run function trees:palm/checkchunk
execute if predicate trees:cliffs positioned ~-8 ~ ~-8 run function trees:cliff/handlelayer
execute if predicate trees:conifer positioned ~-8 ~ ~-8 run function trees:conifer/checkchunk
execute if predicate trees:redwood positioned ~-8 ~ ~-8 run function trees:redwood/checkchunk
execute if score total p matches 10..20 if predicate trees:outback positioned ~-8 ~ ~-8 run function trees:outback/checkchunk
execute if score total p matches 10..20 if predicate trees:outback run tag @s add outback
execute if predicate base:savanna unless entity @s[tag=outback] positioned ~-8 ~ ~-8 run function trees:savanna/checkchunk
execute if predicate trees:mountain_forest positioned ~-8 ~ ~-8 run function trees:mountain_forest/checkchunk

execute if predicate base:snow positioned ~-8 ~ ~-8 run function trees:snow/checkchunk
#execute if predicate base:snow_taiga positioned ~-8 ~ ~-8 run function trees:snow_tagia/checkchunk
execute positioned ~-8 ~ ~-8 run function trees:cliff/handlelayer
#execute if predicate base:mountain positioned ~-8 ~ ~-8 run function trees:snowcap/checkchunk
execute if predicate base:plain unless score total p matches 10..20 positioned ~-8 ~ ~-8 run function trees:plains/checkchunk
execute if predicate base:plain if score total p matches 10..20 positioned ~-8 ~ ~-8 run function trees:wetlands/checkchunk
execute if score total p matches 10..20 if predicate base:taiga unless predicate trees:conifer unless predicate trees:redwood positioned ~-8 ~ ~-8 run function trees:tagia_shrub/checkchunk
execute unless score total p matches 10..20 if predicate base:taiga unless predicate trees:conifer unless predicate trees:redwood positioned ~-8 ~ ~-8 run function trees:tagia/checkchunk
execute if score total p matches 10..20 if predicate base:forest unless predicate base:flower positioned ~-8 ~ ~-8 run function trees:tall_forest/checkchunk
execute if score total p matches 50..60 if predicate base:forest unless predicate base:flower positioned ~-8 ~ ~-8 run function trees:forest/checkchunk
execute if score total p matches 30..50 if predicate base:flower positioned ~-8 ~ ~-8 run function trees:blossom/checkchunk 
execute unless score total p matches 30..50 if predicate base:flower positioned ~-8 ~ ~-8 run function trees:flower/checkchunk


execute if predicate base:desert if score total p matches 10..20 positioned ~-8 ~ ~-8 run function trees:painted_desert/checkchunk
execute unless score total p matches 10..20 if block ~ 4 ~ bedrock unless block ~ 2 ~ bedrock if predicate base:desert positioned ~-8 ~ ~-8 run function trees:desert/checkchunk
#execute if predicate base:prismarine if score total p matches 10..20 positioned ~-8 ~ ~-8 run function trees:prismarine/checkchunk