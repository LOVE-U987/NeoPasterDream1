# -*- coding: utf-8 -*-
"""批量读取染梦结构 JSON 的 start_height / 投影 / 地形适应配置"""
import json
import os

BASE = r"c:\Users\97128\Documents\GitHub\NeoPasterDream1\PasterDream\src\main\resources\data\pasterdream\worldgen\structure"

# 染梦相关结构（从 PDRuinsRegistration 注释中提取）
NAMES = [
    "dream_train", "dyedream_worldtree_0", "dyedream_worldtree_1",
    "pinkagaric_house_0", "pinkagaric_house_1", "pinkagaric_house_2", "pinkagaric_house_3",
    "struct_dyedream_crack_1", "desert_cottage_0", "aaroncos_arena_portals",
    "dyedream_floating_temple",
    "dream_church_0", "dream_church_1", "dream_church_2", "dream_church_3",
    "dream_church_4", "dream_church_5", "dream_church_6", "dream_church_7",
    "dream_church_8", "dream_church_9", "dream_church_10",
    "desert_fortress_0",
    "dyedream_tower_0", "dyedream_tower_1", "dyedream_laboratory_0", "dyedream_tavern",
    "dyedream_pavilion_0", "dyedream_pavilion_1", "dyedream_pavilion_2",
    "dyedream_campsite_0", "dream_wishingtree_0", "dream_wishingtree_1",
    "traveler_house_0", "traveler_house_1", "traveler_house_2",
    "garden_decryption_0", "garden_decryption_1", "garden_decryption_2",
    "picnic_basket_structure", "meltdream_liquid_well_0", "meltdream_liquid_well_1",
]

for name in NAMES:
    p = os.path.join(BASE, name + ".json")
    if not os.path.exists(p):
        print(f"{name}: MISSING")
        continue
    with open(p, encoding="utf-8") as f:
        d = json.load(f)
    sh = d.get("start_height", {})
    abs_h = sh.get("absolute", sh.get("height", "?"))
    print(f"{name:28s} start_height={str(abs_h):>5} "
          f"proj={str(d.get('project_start_to_heightmap', '-')):18s} "
          f"terrain_adapt={d.get('terrain_adaptation', '-')}")
