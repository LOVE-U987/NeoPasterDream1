# -*- coding: utf-8 -*-
"""基于染梦 noise_settings 生成冷域维度 noise_settings（替换 default_block 与 surface_rule）"""
import json
import os

ROOT = r'c:\Users\97128\Documents\GitHub\NeoPasterDream1'
SRC = os.path.join(ROOT, 'PasterDream', 'src', 'main', 'resources', 'data', 'pasterdream',
                   'worldgen', 'noise_settings', 'dyedream_world.json')
DST = os.path.join(ROOT, 'PasterDream', 'src', 'main', 'resources', 'data', 'pasterdream',
                   'worldgen', 'noise_settings', 'cold_domain_world.json')

with open(SRC, 'r', encoding='utf-8') as f:
    text = f.read()

# 1. default_block: calcite -> stone
text = text.replace('"Name": "minecraft:calcite"', '"Name": "minecraft:stone"')

# 2. 替换 surface_rule（从 surface_rule 键到文件尾）
idx = text.find('"surface_rule"')
assert idx != -1, 'surface_rule not found'
prefix = text[:idx]

surface = '''"surface_rule": {
    "type": "minecraft:sequence",
    "sequence": [
      {
        "type": "minecraft:condition",
        "if_true": {
          "type": "minecraft:vertical_gradient",
          "random_name": "minecraft:bedrock_floor",
          "true_at_and_below": {
            "above_bottom": 0
          },
          "false_at_and_above": {
            "above_bottom": 5
          }
        },
        "then_run": {
          "type": "minecraft:block",
          "result_state": {
            "Name": "minecraft:bedrock"
          }
        }
      },
      {
        "type": "minecraft:condition",
        "if_true": {
          "type": "minecraft:above_preliminary_surface"
        },
        "then_run": {
          "type": "minecraft:sequence",
          "sequence": [
            {
              "type": "minecraft:condition",
              "if_true": {
                "type": "minecraft:stone_depth",
                "surface_type": "floor",
                "add_surface_depth": false,
                "secondary_depth_range": 0,
                "offset": 0
              },
              "then_run": {
                "type": "minecraft:block",
                "result_state": {
                  "Name": "pasterdream:snowy_cold_domain_grass"
                }
              }
            },
            {
              "type": "minecraft:condition",
              "if_true": {
                "type": "minecraft:stone_depth",
                "surface_type": "floor",
                "add_surface_depth": true,
                "secondary_depth_range": 0,
                "offset": 0
              },
              "then_run": {
                "type": "minecraft:block",
                "result_state": {
                  "Name": "pasterdream:cold_domain_dirt"
                }
              }
            }
          ]
        }
      }
    ]
  }
}'''

new_text = prefix + surface
with open(DST, 'w', encoding='utf-8') as f:
    f.write(new_text)

# 校验 JSON 可解析
json.loads(new_text)
print('OK: cold_domain_world.json 生成并可解析')
print('default_block 已改 stone:', '"Name": "minecraft:stone"' in new_text)
print('surface_rule 引用: snowy_cold_domain_grass =', 'snowy_cold_domain_grass' in new_text,
      '| cold_domain_dirt =', 'cold_domain_dirt' in new_text)
