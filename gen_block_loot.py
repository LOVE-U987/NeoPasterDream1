#!/usr/bin/env python3
"""批量生成 PasterDream 方块战利品表 JSON 文件（v2 优化版）"""

import json
import os

MOD_ID = "pasterdream"
LOOT_DIR = os.path.join("src", "main", "resources", "data", MOD_ID, "loot_tables", "blocks")

# ==================== 方块战利品定义 ====================

# 掉落自身的方块（registerBlock 注册但使用 vanilla Block 类，需要 loot table）
self_drop_blocks = [
    # 核心功能方块
    "dream_accumulator", "dyedream_desk", "life_crystal", "shadow_chest",
    # 染梦方块
    "dyedream_block", "dyedream_dirt", "dyedream_sand", "dyedream_planks",
    "dyedreamquartz_block", "smooth_dyedreamquartz_block",
    "bricks_dyedreamquartz_block", "chiseled_dyedreamquartz_block",
    "dyedream_bud_block", "pinkslime_block", "icestone",
    "dyedream_grass",
    # 原木/木头
    "dyedream_log", "dyedream_wood", "pillar_dyedreamquartz_block",
    # 台阶/楼梯/墙
    "dyedream_planks_stairs", "dyedream_bud_stairs",
    "dyedreamquartz_block_stairs",
    "dyedream_planks_slab", "dyedream_bud_slab", "dyedreamquartz_block_slab",
    "dyedream_bud_wall", "dyedreamquartz_block_wall",
    # 栅栏/门/压力板/按钮
    "dyedream_planks_fence", "dyedream_planks_fencegate",
    "dyedream_planks_door", "dyedream_planks_trapdoor",
    "dyedream_planks_pressure_plate", "dyedream_planks_button",
    # 灯笼
    "dyedream_lartern",
    # 自定义模型方块
    "dyedream_planks_pane",
    # 粉丁菇 4 变种
    "pinkagaric_0", "pinkagaric_1", "pinkagaric_2", "pinkagaric_3",
    # 花蕾 3 变种
    "dyedream_bud_0", "dyedream_bud_1", "dyedream_bud_2",
    # 冰蕾
    "ice_bud_0",
    # 水面植物
    "dyedream_lily_pad", "dyedream_lotus", "dyedream_seagrass",
    # 树苗与裂纹
    "dyedream_sapling", "dyedream_crack",
    # 云朵方块
    "cloud", "dark_cloud", "thick_cloud",
]

# 单格花（flower_1 ~ flower_18，跳过双层的 flower_7/10/11/12/18）
single_flowers = [f"flower_{i}" for i in [1, 2, 3, 5, 6, 8, 9, 13, 14, 15, 16, 17]]
# 双层花（Java 层面 DyedreamDoublePlantBlock 已控制只有底部掉落）
double_flowers = [f"flower_{i}" for i in [7, 10, 11, 12, 18]]

# 单格草（grass_1 ~ grass_15，跳过双层的 grass_4/10/15）
single_grasses = [f"grass_{i}" for i in [1, 2, 3, 5, 6, 7, 8, 9, 11, 12, 13, 14]]
# 双层草（Java 层面 DyedreamDoublePlantBlock 已控制只有底部掉落）
double_grasses = [f"grass_{i}" for i in [4, 10, 15]]

# ==================== 特殊方块分类 ====================

# 带时运效果的矿石（key: 方块名, value: 掉落物品 ID）
ore_drops = {
    "dyedreamquartz_ore": "pasterdream:dyedreamquartz",
    "dyedreamdust_ore": "pasterdream:dyedream_dust",
    "amber_candy_ore": "pasterdream:amber_candy",
}

# 需要精准采集的方块（玻璃/冰/树叶，生存空手不掉落）
silk_touch_blocks = [
    "dyedream_glass", "carve_dyedream_glass", "gold_carve_dyedream_glass",
    "dyedream_glasspane", "carve_dyedream_glasspane", "gold_carve_dyedream_glasspane",
    "dyedream_ice", "dyedream_packed_ice",
    "dyedream_leaves", "dyedream_worldtree_leaves",
]

all_blocks = (self_drop_blocks + single_flowers + double_flowers
              + single_grasses + double_grasses + silk_touch_blocks
              + list(ore_drops.keys()))

# ==================== 战利品表生成函数 ====================

def make_self_drop_loot(block_name):
    """普通方块：掉落自身"""
    return {
        "type": "minecraft:block",
        "pools": [
            {
                "rolls": 1,
                "entries": [
                    {
                        "type": "minecraft:item",
                        "name": f"pasterdream:{block_name}"
                    }
                ],
                "conditions": [
                    {
                        "condition": "minecraft:survives_explosion"
                    }
                ]
            }
        ]
    }


def make_ore_loot(block_name, item_id):
    """矿石：掉落对应物品 + 时运效果 + 爆炸衰减"""
    return {
        "type": "minecraft:block",
        "pools": [
            {
                "rolls": 1,
                "entries": [
                    {
                        "type": "minecraft:item",
                        "name": item_id,
                        "functions": [
                            {
                                "function": "minecraft:apply_bonus",
                                "enchantment": "minecraft:fortune",
                                "formula": "minecraft:ore_drops"
                            },
                            {
                                "function": "minecraft:explosion_decay"
                            }
                        ]
                    }
                ],
                "conditions": [
                    {
                        "condition": "minecraft:survives_explosion"
                    }
                ]
            }
        ]
    }


def make_silk_touch_loot(block_name):
    """易碎方块：需要精准采集才掉落自身"""
    return {
        "type": "minecraft:block",
        "pools": [
            {
                "rolls": 1,
                "entries": [
                    {
                        "type": "minecraft:item",
                        "name": f"pasterdream:{block_name}"
                    }
                ],
                "conditions": [
                    {
                        "condition": "minecraft:match_tool",
                        "predicate": {
                            "enchantments": [
                                {
                                    "enchantment": "minecraft:silk_touch",
                                    "levels": {
                                        "min": 1
                                    }
                                }
                            ]
                        }
                    },
                    {
                        "condition": "minecraft:survives_explosion"
                    }
                ]
            }
        ]
    }


# ==================== 主流程 ====================

def main():
    os.makedirs(LOOT_DIR, exist_ok=True)

    stats = {"普通掉落": 0, "时运矿石": 0, "精准采集": 0}

    for block in all_blocks:
        if block in ore_drops:
            loot_data = make_ore_loot(block, ore_drops[block])
            stats["时运矿石"] += 1
        elif block in silk_touch_blocks:
            loot_data = make_silk_touch_loot(block)
            stats["精准采集"] += 1
        else:
            loot_data = make_self_drop_loot(block)
            stats["普通掉落"] += 1

        file_path = os.path.join(LOOT_DIR, f"{block}.json")
        with open(file_path, "w", encoding="utf-8") as f:
            json.dump(loot_data, f, indent=2)

    total = sum(stats.values())
    print(f"✅ 共生成 {total} 个方块战利品表 JSON 文件：")
    print(f"   📦 普通掉落自身: {stats['普通掉落']} 个")
    print(f"   ⛏️  矿石+时运效果: {stats['时运矿石']} 个")
    print(f"   🧤 需要精准采集: {stats['精准采集']} 个")


if __name__ == "__main__":
    main()