#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
天空盒 JSON tint 强度提升脚本
==============================
将各 skybox 的 sky_tint opacity 从 0.04~0.07 提升至 0.09~0.12，
配合 SkyTintContent 的夜晚权重公式，使半夜(time 18000)天空色彩最明显。
运行: python tools/boost_sky_tint.py
"""

import json
import os

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SKYBOX_DIR = os.path.join(ROOT, "PasterDream", "src", "main", "resources",
                          "data", "pasterdream", "skyboxes")

# 各文件目标 opacity（按群系氛围强弱调整）
OPACITY_MAP = {
    "warm_plains.json": 0.10,
    "hot_forest.json": 0.09,
    "frozen.json": 0.11,
    "ocean.json": 0.10,
    "mushroom.json": 0.09,
    "dense_forest.json": 0.10,
}


def main():
    for name, target in OPACITY_MAP.items():
        path = os.path.join(SKYBOX_DIR, name)
        with open(path, encoding="utf-8") as fh:
            data = json.load(fh)
        updated = False
        for layer in data.get("layers", []):
            if layer.get("type") == "pasterdream:sky_tint":
                layer["opacity"] = target
                updated = True
        with open(path, "w", encoding="utf-8") as fh:
            json.dump(data, fh, ensure_ascii=False, indent=2)
            fh.write("\n")
        print(f"  {name}: tint opacity -> {target} (更新={updated})")
    print("完成!")


if __name__ == "__main__":
    main()
