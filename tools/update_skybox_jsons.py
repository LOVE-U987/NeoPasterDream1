#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
天空盒 JSON 批量升级脚本
========================
1. fade_speed 0.06 → 0.12（群星更快显现）
2. star_field 改用群系专属彩色星星纹理（2 色 × 7 帧 = 14 帧），count 增大、尺寸增大、color 置白
3. planet_system 追加第 3 颗行星（参考 Stellara 多行星排版）
运行: python tools/update_skybox_jsons.py
"""

import json
import os

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SKYBOX_DIR = os.path.join(ROOT, "PasterDream", "src", "main", "resources",
                          "data", "pasterdream", "skyboxes")

# 文件 → 主题 映射
THEME_MAP = {
    "warm_plains.json": "warm",
    "hot_forest.json": "forest",
    "frozen.json": "frozen",
    "ocean.json": "ocean",
    "mushroom.json": "mushroom",
    "dense_forest.json": "dense",
}


def star_textures(theme):
    """群系主题星星纹理列表（主色 7 帧 + 次色 7 帧）"""
    return (
        [f"pasterdream:textures/sky/{theme}/{theme}_star_{i}.png" for i in range(1, 8)]
        + [f"pasterdream:textures/sky/{theme}/{theme}_star2_{i}.png" for i in range(1, 8)]
    )


def third_planet(theme):
    """第 3 颗行星配置（小号、不同方位，参考 Stellara 布局）"""
    return {
        "texture": f"pasterdream:textures/sky/{theme}/{theme}_planet_2.png",
        "yaw": 0.35,
        "pitch": -0.32,
        "size": 3.2,
        "roll": 1.4,
        "roll_speed": 0.0016,
        "color": [0.95, 0.9, 1.0],
        "opacity": 0.72,
    }


def process(file_name):
    path = os.path.join(SKYBOX_DIR, file_name)
    with open(path, encoding="utf-8") as fh:
        data = json.load(fh)
    theme = THEME_MAP[file_name]

    # 1. 加速淡入
    data["fade_speed"] = 0.12

    changed_star = False
    changed_planet = False
    for layer in data.get("layers", []):
        ltype = layer.get("type", "")
        # 2. 群系专属彩色星星
        if ltype == "pasterdream:star_field":
            layer["textures"] = star_textures(theme)
            layer["count"] = 1400
            layer["min_size"] = 0.6
            layer["max_size"] = 2.0
            layer["color"] = [1.0, 1.0, 1.0]
            changed_star = True
        # 3. 追加第 3 颗行星
        if ltype == "pasterdream:planet_system":
            planets = layer.setdefault("planets", [])
            if len(planets) < 3:
                planets.append(third_planet(theme))
                changed_planet = True

    with open(path, "w", encoding="utf-8") as fh:
        json.dump(data, fh, ensure_ascii=False, indent=2)
        fh.write("\n")
    print(f"  {file_name}: fade=0.12 star_field更新={changed_star} 追加行星={changed_planet}")


def main():
    for name in THEME_MAP:
        process(name)
    print("完成!")


if __name__ == "__main__":
    main()
