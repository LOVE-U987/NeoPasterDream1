execute if block ~ 66 ~ #base:caveblock if block ~1 66 ~ air run function trees:cliff/generate
execute if block ~ 66 ~ #base:caveblock if block ~-1 66 ~ air run function trees:cliff/generate
execute if block ~ 66 ~ #base:caveblock if block ~ 66 ~1 air run function trees:cliff/generate
execute if block ~ 66 ~ #base:caveblock if block ~ 66 ~-1 air run function trees:cliff/generate
execute unless block ~-1 66 ~ air unless block ~1 66 ~ air unless block ~ 66 ~-1 air unless block ~ 66 ~1 air if block ~ 74 ~1 air run function trees:cliff/inside