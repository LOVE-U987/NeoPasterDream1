# -*- coding: utf-8 -*-
"""读取染梦群系 grass/foliage/water 颜色，分析新配色公式输入。"""
import json
import glob
import os

BASE = r"PasterDream/src/main/resources/data/pasterdream/worldgen/biome"


def rgb(v: int) -> tuple:
    v &= 0xFFFFFF
    return ((v >> 16) & 0xFF, (v >> 8) & 0xFF, v & 0xFF)


for f in sorted(glob.glob(os.path.join(BASE, "biome_dyedream*.json"))):
    d = json.load(open(f, encoding="utf-8"))
    fx = d["effects"]
    name = os.path.basename(f).replace("biome_dyedream", "").replace(".json", "").lstrip("_")
    g = fx.get("grass_color")
    fo = fx.get("foliage_color")
    w = fx.get("water_color")
    print(
        f"{name:16s} grass={g} rgb{rgb(g)} | foliage={fo} rgb{rgb(fo)} | water={w} rgb{rgb(w)}"
    )
