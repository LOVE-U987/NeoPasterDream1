#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
天空盒 JSON 与纹理资源校验脚本
==============================
1. 校验 data/pasterdream/skyboxes/*.json 语法合法
2. 校验 JSON 中引用的纹理文件实际存在
运行: python tools/verify_skyboxes.py
"""

import glob
import json
import os
import re

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
BASE = os.path.join(ROOT, "PasterDream", "src", "main", "resources")
SKYBOX_DIR = os.path.join(BASE, "data", "pasterdream", "skyboxes")
TEX_DIR = os.path.join(BASE, "assets", "pasterdream", "textures", "sky")


def main():
    skyboxes = sorted(glob.glob(os.path.join(SKYBOX_DIR, "*.json")))
    print("== JSON 语法校验 ==")
    for f in skyboxes:
        with open(f, encoding="utf-8") as fh:
            data = json.load(fh)
        layers = data.get("layers", [])
        biomes = data.get("biomes", [])
        dims = data.get("dimensions", [])
        print(f"  OK {os.path.basename(f)}  layers={len(layers)}  biomes={biomes}  dims={dims}")

    print("\n== 纹理引用校验 ==")
    missing = []
    for f in skyboxes:
        with open(f, encoding="utf-8") as fh:
            raw = fh.read()
        for m in re.finditer(r"pasterdream:textures/sky/([^\"']+)\.png", raw):
            rel = m.group(1) + ".png"
            path = os.path.join(TEX_DIR, *rel.split("/"))
            if not os.path.exists(path):
                missing.append((os.path.basename(f), rel))
    if missing:
        for f, t in missing:
            print(f"  缺失: {f} -> {t}")
        print("  校验失败!")
        return 1
    print("  全部纹理引用存在 OK")

    print("\n== 群系覆盖检查 ==")
    expected = {
        "biome_dyedream_0", "biome_dyedream_1", "biome_dyedream_2",
        "biome_dyedream_3", "biome_dyedream_deep_ocean", "biome_dyedream_mushroom_plains",
        "biome_dyedream_shore", "biome_dyedream_river", "biome_dyedream_dense_forest",
    }
    covered = set()
    for f in skyboxes:
        with open(f, encoding="utf-8") as fh:
            data = json.load(fh)
        for b in data.get("biomes", []):
            covered.add(b.split(":")[-1])
    uncovered = expected - covered
    if uncovered:
        print(f"  未覆盖群系: {sorted(uncovered)}")
    else:
        print("  全部 9 个染梦群系均被覆盖 OK")
    print("\n完成!")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
