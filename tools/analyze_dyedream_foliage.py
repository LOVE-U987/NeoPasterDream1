# -*- coding: utf-8 -*-
"""分析染梦群系 foliage_color 实际值，诊断树叶染色颜色异常。"""
import json
import glob
import os


def to_rgb(v: int) -> tuple:
    return ((v >> 16) & 0xFF, (v >> 8) & 0xFF, v & 0xFF)


BASE = (146, 85, 127)  # 原染梦树叶纹理平均色（深粉紫）
BASE_DIR = r"PasterDream/src/main/resources/data/pasterdream/worldgen/biome"

print("=== 当前群系 foliage_color 实际 RGB ===")
data = []
for f in sorted(glob.glob(os.path.join(BASE_DIR, "biome_dyedream*.json"))):
    d = json.load(open(f, encoding="utf-8"))
    fx = d.get("effects", {})
    fc = fx.get("foliage_color")
    name = os.path.basename(f).replace("biome_dyedream", "").replace(".json", "").lstrip("_")
    if fc is None:
        print(f"{name:22s}: foliage_color MISSING! getFoliageColor() 返回默认值")
        continue
    data.append((name, fc))
    print(f"{name:22s}: {fc:>10d} -> RGB {to_rgb(fc)}")

print()
print("=== 当前混色(0.5)后的 tint 值（灰纹理 x tint = 最终显示色） ===")
for name, fc in data:
    t = to_rgb(fc)
    m = tuple(round(BASE[i] + (t[i] - BASE[i]) * 0.5) for i in range(3))
    # 最终显示色 = 灰纹理亮度(约107) x tint / 255
    final = tuple(round(107 * c / 255) for c in m)
    print(f"{name:22s}: mix={m} -> 最终显示色 {final}")
