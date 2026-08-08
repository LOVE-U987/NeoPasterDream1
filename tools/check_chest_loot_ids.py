# -*- coding: utf-8 -*-
"""
核对融梦水晶箱物品池中所有物品的注册 ID 是否存在于语言文件中，
确保配置默认值使用的 ID 均有效。
"""
import json
import sys
import io

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8")

LANG_PATH = r"c:\Users\97128\Documents\GitHub\NeoPasterDream1\PasterDream\src\main\resources\assets\pasterdream\lang\zh_cn.json"

# 融梦水晶箱三个品质物品池的物品 ID（不含数量与权重）
pools = {
    "common": [
        "fried_egg", "candy_cane", "bubble_gum", "chocolate", "berry_buncake",
        "cream_buncake", "dyedream_popsicle", "gingerbread_man", "potato_buncake",
        "pumpkin_buncake", "jellyfish_jello", "ricecake", "swiss_roll",
        "bread_slice", "fig", "strawberry_heart", "wafer_biscuit",
    ],
    "rare": [
        "dyedream_ingot", "titanium_ingot", "blackmetal_ingot", "white_crystal",
        "dreamwish", "soul_essence", "charged_amethyst", "wind_iron_ingot",
        "moltengold_ingot", "dream_aurorian_steel", "dyedream_sword",
        "titanium_sword", "pinkegg", "nightmare_fuel", "memento_item_03",
        "memento_item_08",
    ],
    "legendary": [
        "meltdream_crystal_0", "shadow_erosion_sword", "allkinds_ring",
        "bobo_plume", "dyedream_upgrade", "titanium_upgrade", "sculk_upgrade",
        "dyedream_teleport_crystal", "sweetdream_disc", "dyedream_world_disc",
        "memento_item_03", "memento_item_08",
    ],
}

with open(LANG_PATH, encoding="utf-8") as f:
    lang = json.load(f)

missing = []
for pool_name, ids in pools.items():
    for item_id in ids:
        item_key = f"item.pasterdream.{item_id}"
        block_key = f"block.pasterdream.{item_id}"
        if item_key not in lang and block_key not in lang:
            missing.append((pool_name, item_id))

if missing:
    print("=== 语言文件中缺失的物品键 ===")
    for pool_name, item_id in missing:
        print(f"  [{pool_name}] {item_id}")
else:
    print("OK: 全部物品 ID 均存在于语言文件中")
