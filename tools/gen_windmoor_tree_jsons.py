"""生成风之树 18 变体的装饰数据 JSON。

风之树为程序化 CUSTOM 装饰（生成器 WindMoorTreeGenerator），其 ConfiguredFeature /
PlacedFeature 需以数据包 JSON 形式存在，才能被世界生成与调试水晶按名查找到。
本脚本按与 WindMoorTrees.key() 完全一致的命名规则批量产出：

  - worldgen/configured_feature/windmoor_tree_<尺寸>_<高度>_<枝节>.json
  - worldgen/placed_feature/windmoor_tree_<尺寸>_<高度>_<枝节>.json
  - neoforge/biome_modifier/wind_journey_trees.json（把 18 个变体绑到风之旅途群系）

JSON 结构对齐既有 CUSTOM 装饰 pasterdream:cloud_bubble（generic_decor + type=custom），
避免自创字段导致编解码失败。

用法：python tools/gen_windmoor_tree_jsons.py
"""

import json
from pathlib import Path

# 数据包根目录
DATA = (
    Path(__file__).resolve().parents[1]
    / "PasterDream/src/main/resources/data/pasterdream"
)

# 与 WindMoorTrees 保持一致的参数表（名称必须逐字相同）
SIZES = [("small", 5, 7, 2), ("medium", 8, 11, 3), ("large", 13, 17, 4)]
HEIGHTS = [("low", 0.75), ("mid", 1.0), ("high", 1.3)]
BRANCHES = [("sparse", 3), ("bushy", 7)]

# 风之旅途群系（与 tags/worldgen/biome/is_wind_journey.json 一致）
BIOMES = ["pasterdream:wind_journey_islands", "pasterdream:wind_journey_desert"]

# 生成阶段
STEP = "vegetal_decoration"


def variant_names():
    """按 WindMoorTrees 的顺序生成 18 个变体名。"""
    return [
        f"windmoor_tree_{size[0]}_{height[0]}_{branch[0]}"
        for size in SIZES
        for height in HEIGHTS
        for branch in BRANCHES
    ]


def configured_feature(name: str) -> dict:
    """构造 ConfiguredFeature JSON（CUSTOM 装饰，对齐 cloud_bubble 结构）。"""
    return {
        "type": "pasterdream:generic_decor",
        "config": {
            "type": "custom",
            "body_block": {
                "type": "minecraft:simple_state_provider",
                "state": {"Name": "pasterdream:windmoor_log"},
            },
            "top_block": {
                "type": "minecraft:simple_state_provider",
                "state": {"Name": "pasterdream:windmoor_leaves"},
            },
            "custom_generator_key": name,
            "replaceable": {
                "type": "minecraft:any_of",
                "predicates": [
                    {
                        "type": "minecraft:matching_blocks",
                        "blocks": [
                            "minecraft:air",
                            "minecraft:cave_air",
                            "pasterdream:windmoor_leaves",
                        ],
                    },
                    {"type": "minecraft:replaceable"},
                ],
            },
        },
    }


def placed_feature(name: str) -> dict:
    """构造 PlacedFeature JSON（地表定点 + 群系过滤）。"""
    return {
        "feature": f"pasterdream:{name}",
        "placement": [
            {"type": "minecraft:rarity_filter", "chance": 6},
            {"type": "minecraft:in_square"},
            {"type": "minecraft:heightmap", "heightmap": "MOTION_BLOCKING_NO_LEAVES"},
            {"type": "minecraft:biome"},
        ],
    }


def biome_modifier(names) -> dict:
    """构造群系修饰器 —— 把全部变体注入风之旅途群系的植被阶段。"""
    return {
        "type": "neoforge:add_features",
        "biomes": BIOMES,
        "features": [f"pasterdream:{name}" for name in names],
        "step": STEP,
    }


def dump(path: Path, data: dict):
    """以 UTF-8 + LF 写出缩进 JSON。"""
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n",
                    encoding="utf-8", newline="\n")


def main():
    names = variant_names()
    for name in names:
        dump(DATA / "worldgen/configured_feature" / f"{name}.json", configured_feature(name))
        dump(DATA / "worldgen/placed_feature" / f"{name}.json", placed_feature(name))
    dump(DATA / "neoforge/biome_modifier/wind_journey_trees.json", biome_modifier(names))
    print(f"已生成 {len(names)} 个 configured_feature + {len(names)} 个 placed_feature "
          f"+ 1 个 biome_modifier")
    print("变体:", ", ".join(names))


if __name__ == "__main__":
    main()
