#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
行星纹理"实心化"脚本
======================
行星本应完全不透明（实心球体），但换色自 Stellara 的纹理保留了柔和半透明边缘，
导致原版渲染为半透明、光影下黑色。本脚本将 alpha>0 的像素全部提升到 255（不透明），
圆外仍全透明。
仅处理 planet 纹理，不影响星星（星星需要柔和光晕）。
运行: python tools/solidify_planets.py
"""

import glob
import os

from PIL import Image

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SKY_DIR = os.path.join(ROOT, "PasterDream", "src", "main", "resources",
                       "assets", "pasterdream", "textures", "sky")


def solidify(path):
    img = Image.open(path).convert("RGBA")
    px = img.load()
    w, h = img.size
    changed = 0
    for y in range(h):
        for x in range(w):
            r, g, b, a = px[x, y]
            if 0 < a < 255:
                px[x, y] = (r, g, b, 255)   # 半透明 → 不透明
                changed += 1
    if changed:
        img.save(path)   # 写回文件！
    return changed


def main():
    total = 0
    for f in sorted(glob.glob(os.path.join(SKY_DIR, "*", "*_planet_*.png"))):
        changed = solidify(f)
        if changed:
            total += changed
            print(f"  {os.path.relpath(f, SKY_DIR)}: {changed} 半透明像素 → 不透明")
    print(f"完成! 共修正 {total} 像素")


if __name__ == "__main__":
    main()
