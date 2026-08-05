# -*- coding: utf-8 -*-
"""检查染梦树叶灰化纹理的 alpha 分布与不透明像素亮度，验证"纹理太暗"假设。"""
from PIL import Image

PATHS = [
    r"PasterDream/src/main/resources/assets/pasterdream/textures/block/dyedream_leaves.png",
]


def analyze(path: str) -> None:
    img = Image.open(path).convert("RGBA")
    px = list(img.getdata())
    n = len(px)
    opaque = [p for p in px if p[3] > 0]
    semi = [p for p in px if 0 < p[3] < 255]
    transparent = [p for p in px if p[3] == 0]
    print(f"=== {path} ===")
    print(f"总像素: {n}, 不透明: {len(opaque)}, 半透明: {len(semi)}, 全透明: {len(transparent)}")
    if opaque:
        rs = [p[0] for p in opaque]
        gs = [p[1] for p in opaque]
        bs = [p[2] for p in opaque]
        lum = [int(0.299 * p[0] + 0.587 * p[1] + 0.114 * p[2]) for p in opaque]
        print(f"不透明像素: avgRGB=({sum(rs)//len(rs)},{sum(gs)//len(gs)},{sum(bs)//len(bs)}) avgLum={sum(lum)//len(lum)}")
    print()


for p in PATHS:
    analyze(p)
