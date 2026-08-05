#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Stellara 纹理换色工具（仅本项目内参考复用）
============================================
参考仿照模组 Stellara 的星星/行星纹理结构（小尺寸 + 透明通道 + 色相渐变），
将其换色为染梦群系主题色后输出到本模组资源目录。

换色原理（保持明度层次）：
  - 星星：原色是"深蓝→中蓝→亮蓝→白"的明度渐变。把每个非透明像素的
    RGB 按"最亮分量归一化"得到饱和度因子，再乘染梦主题色 → 保留形状与
    明度层次，色相变成染梦主题。
  - 行星：同理，把原行星色相映射到染梦主题行星色。

输出:
  PasterDream/src/main/resources/assets/pasterdream/textures/sky/<theme>/
    <theme>_star_1..7.png      (主题色星星)
    <theme>_star2_1..7.png     (次色星星)
    <theme>_planet_1..7.png    (主题行星, 尺寸放大到 64)

运行: python tools/recolor_stellara_textures.py
"""

import math
import os
import shutil

from PIL import Image

STELLARA = r"C:\Users\97128\AppData\Local\Temp\stellara\assets\stellara\textures\sky"
OUT = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
                   "PasterDream", "src", "main", "resources",
                   "assets", "pasterdream", "textures", "sky")

# 染梦群系主题色 (主色, 次色, 行星色)
THEMES = {
    "warm": {
        "star": (255, 226, 208), "star2": (255, 186, 204),
        "planet": (255, 200, 150),
    },
    "forest": {
        "star": (186, 255, 208), "star2": (208, 255, 178),
        "planet": (150, 230, 160),
    },
    "frozen": {
        "star": (196, 228, 255), "star2": (150, 200, 255),
        "planet": (200, 225, 255),
    },
    "ocean": {
        "star": (176, 232, 255), "star2": (122, 198, 242),
        "planet": (130, 200, 255),
    },
    "mushroom": {
        "star": (224, 198, 255), "star2": (188, 152, 255),
        "planet": (220, 180, 255),
    },
    "dense": {
        "star": (255, 198, 224), "star2": (255, 166, 196),
        "planet": (255, 190, 220),
    },
}

# Stellara 主题 → 本模组主题 映射（用哪个 Stellara 主题的星星作为底子）
# 实际从同名主题取，找不到则用 frozen（冷色调底子最通用）
STELLARA_THEME = {
    "warm": "cherry",      # 樱花 → 暖
    "forest": "forest",
    "frozen": "frozen",
    "ocean": "aquatic",
    "mushroom": "mushroom",
    "dense": "cherry",     # 樱花 → 粉
}


def recolor_pixel(r, g, b, a, target_rgb):
    """保持明度层次换色：按最亮分量归一化得到强度，乘目标色"""
    if a == 0:
        return (r, g, b, a)
    lum = max(r, g, b)
    if lum == 0:
        lum = 1
    # 强度 = 原亮度 / 255（0~1），目标色乘强度 + 原白色像素保持纯白
    # 但 Stellara 星星中心是纯白 255,255,255 → 应保留白（星核）
    if r > 240 and g > 240 and b > 240:
        return (255, 255, 255, a)   # 纯白核心保留
    scale = lum / 255.0
    return (
        int(target_rgb[0] * scale),
        int(target_rgb[1] * scale),
        int(target_rgb[2] * scale),
        a,
    )


def recolor_image(src_path, target_rgb):
    img = Image.open(src_path).convert("RGBA")
    px = img.load()
    w, h = img.size
    for y in range(h):
        for x in range(w):
            r, g, b, a = px[x, y]
            px[x, y] = recolor_pixel(r, g, b, a, target_rgb)
    return img


def upscale(img, size):
    """最近邻放大到指定尺寸（MC 会自动放大，这里主动放大保持清晰）"""
    return img.resize((size, size), Image.NEAREST)


def main():
    for theme, cfg in THEMES.items():
        st_theme = STELLARA_THEME.get(theme, "frozen")
        st_dir = os.path.join(STELLARA, st_theme)
        out_dir = os.path.join(OUT, theme)
        os.makedirs(out_dir, exist_ok=True)

        # 星星：找 Stellara 主题的 *star_*.png（两套色）
        star_files = sorted(f for f in os.listdir(st_dir) if "star" in f and f.endswith(".png"))
        if not star_files:
            print(f"  [警告] {st_theme} 无星星纹理，跳过 {theme}")
            continue

        # 按文件名分组：主色星 7 张 + 次色星 7 张
        # Stellara 主题星星命名如 blue_star_1.png / cyan_star_2.png（两套色）
        groups = {}
        for f in star_files:
            prefix = f.rsplit("_", 1)[0]   # blue_star
            groups.setdefault(prefix, []).append(f)
        prefixes = sorted(groups.keys())
        main_prefix = prefixes[0] if prefixes else None
        second_prefix = prefixes[1] if len(prefixes) > 1 else main_prefix

        # 主色星星（7 张）
        for i in range(1, 8):
            src = os.path.join(st_dir, f"{main_prefix}_{i}.png") if main_prefix else None
            if src and os.path.exists(src):
                img = recolor_image(src, cfg["star"])
                img = upscale(img, 32)
                img.save(os.path.join(out_dir, f"{theme}_star_{i}.png"))
        # 次色星星（7 张）
        for i in range(1, 8):
            src = os.path.join(st_dir, f"{second_prefix}_{i}.png") if second_prefix else None
            if src and os.path.exists(src):
                img = recolor_image(src, cfg["star2"])
                img = upscale(img, 32)
                img.save(os.path.join(out_dir, f"{theme}_star2_{i}.png"))

        # 行星：找 *planet_*.png（每主题 5~7 张），换色放大到 64
        planet_files = sorted(f for f in os.listdir(st_dir) if "planet" in f and f.endswith(".png"))
        for idx, f in enumerate(planet_files[:7], start=1):
            img = recolor_image(os.path.join(st_dir, f), cfg["planet"])
            img = upscale(img, 64)
            img.save(os.path.join(out_dir, f"{theme}_planet_{idx}.png"))

        print(f"  {theme}: 星星×{len(star_files)} 行星×{min(len(planet_files), 7)} 已换色")

    print("完成!")


if __name__ == "__main__":
    main()
