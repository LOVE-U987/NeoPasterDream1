# -*- coding: utf-8 -*-
"""设置染梦群系 foliage_color：除冰雪群系外全部回退为「最开始的」基础粉紫 0x92557F。

- 非冰雪群系: 固定 = 最开始的颜色(原纹理粉紫 0x92557F = (146,85,127))，不随群系变化
- 冰雪群系(biome_dyedream_2): 保持蓝色覆盖(亮冰蓝紫)，体现寒冷群系特点

所有写入值强制为 0xFFRRGGBB 负值形式（alpha=0xFF），避免 alpha=0 透明。
"""
import json
import glob
import os

# 最开始的颜色：原纹理平均粉紫 0x92557F（用户指定的"最开始没有灰化的颜色"）
BASE_ORIGINAL = (146, 85, 127)

# 冰雪群系：蓝色覆盖（寒冷群系树叶偏蓝）
COLD_OVERRIDE = {
    "biome_dyedream_2": (150, 180, 240),          # 冰雪：亮冰蓝紫
}
BASE_DIR = r"PasterDream/src/main/resources/data/pasterdream/worldgen/biome"


def to_mc_int(rgb: tuple) -> int:
    """0xRRGGBB -> 带 alpha 0xFF 的 signed int（0xFFRRGGBB），保证 alpha=255 不透明"""
    v = 0xFF000000 | (rgb[0] << 16) | (rgb[1] << 8) | rgb[2]
    return v - 0x100000000 if v >= 0x80000000 else v


print("=== 设置群系 foliage_color: 非冰雪=基础粉紫(最开始) / 冰雪=蓝 ===")
plan = {}
for f in sorted(glob.glob(os.path.join(BASE_DIR, "biome_dyedream*.json"))):
    name = os.path.basename(f).replace(".json", "")
    if name in COLD_OVERRIDE:
        # 冰雪群系：蓝色覆盖（寒冷群系树叶偏蓝）
        m = COLD_OVERRIDE[name]
        print(f"{name:38s} [冰雪覆盖] -> {m} (#{m[0]:02X}{m[1]:02X}{m[2]:02X})")
    else:
        # 非冰雪群系：固定为「最开始」的基础粉紫 0x92557F
        m = BASE_ORIGINAL
        print(f"{name:38s} [基础粉紫]  -> {m} (#{m[0]:02X}{m[1]:02X}{m[2]:02X})")
    val = to_mc_int(m)
    plan[name] = (val, m)

print()
print("=== 写入 JSON ===")
for f in sorted(glob.glob(os.path.join(BASE_DIR, "biome_dyedream*.json"))):
    name = os.path.basename(f).replace(".json", "")
    if name not in plan:
        print(f"SKIP (无映射): {name}")
        continue
    with open(f, "r", encoding="utf-8") as fp:
        data = json.load(fp)
    old = data["effects"].get("foliage_color")
    data["effects"]["foliage_color"] = plan[name][0]
    with open(f, "w", encoding="utf-8") as fp:
        json.dump(data, fp, indent=2, ensure_ascii=False)
        fp.write("\n")
    print(f"UPDATED {name}: foliage_color {old} -> {plan[name][0]}")
