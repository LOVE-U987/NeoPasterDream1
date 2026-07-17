import os

LOOT_DIR = "PasterDream/src/main/resources/data/pasterdream/loot_table/blocks"

blocks = [
    "big_bubble",
    "windrunner_crystal_block",
    "congeal_wind_block",
    "starcall_block",
    "starcall_crack",
    "cyan_stone",
    "cyan_stone_bricks",
    "cyan_stone_mossy_bricks",
    "cyan_stone_chiseled_bricks",
    "cyan_stone_pillar",
    "cyan_stone_smooth",
    "white_sand",
    "salt_block",
    "clarity_glass",
    "carved_clarity_glass",
    "framed_clarity_glass",
    "clarity_glass_pane",
    "framed_clarity_glass_pane",
    "windiron_bars",
    "breakwind_curtain",
    "cyan_stone_stairs",
    "cyan_stone_slab",
    "cyan_stone_wall",
    "cyan_stone_pressure_plate",
    "cyan_stone_button"
]

def generate_loot_table(block_name):
    template = f'''{{
  "type": "minecraft:block",
  "pools": [
    {{
      "rolls": 1,
      "entries": [
        {{
          "type": "minecraft:item",
          "name": "pasterdream:{block_name}",
          "weight": 1,
          "functions": [
            {{
              "function": "minecraft:set_count",
              "count": {{
                "min": 1,
                "max": 1
              }}
            }},
            {{
              "function": "minecraft:explosion_decay"
            }}
          ]
        }}
      ]
    }}
  ],
  "random_sequence": "pasterdream:blocks/{block_name}"
}}'''
    return template

os.makedirs(LOOT_DIR, exist_ok=True)

for block in blocks:
    filepath = os.path.join(LOOT_DIR, f"{block}.json")
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(generate_loot_table(block))
    print(f"Generated: {filepath}")

print(f"\nGenerated {len(blocks)} loot tables!")
