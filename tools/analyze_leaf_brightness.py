# -*- coding: utf-8 -*-
"""分析染梦树叶灰化纹理的亮度直方图，确定提亮参数。"""
from PIL import Image

PATH = r"PasterDream/src/main/resources/assets/pasterdream/textures/block/dyedream_leaves.png"

img = Image.open(PATH).convert("RGBA")
px = list(img.getdata())
opaque = [p for p in px if p[3] > 0]
lums = sorted(int(0.299 * p[0] + 0.587 * p[1] + 0.114 * p[2]) for p in opaque)

n = len(lums)
print(f"不透明像素: {n}")
print(f"亮度: min={lums[0]} p10={lums[n//10]} p25={lums[n//4]} 中位={lums[n//2]} p75={lums[n*3//4]} p90={lums[n*9//10]} max={lums[-1]}")
print(f"平均亮度: {sum(lums)//n}")

# 模拟 gamma 提亮后的平均亮度
for gamma in (0.9, 0.85, 0.8, 0.75, 0.7):
    new = [round(255 * (l / 255) ** (1 / gamma)) for l in lums]
    print(f"gamma={gamma}: 新平均亮度={sum(new)//n} 中位={new[n//2]} p10={new[n//10]} p90={new[n*9//10]}")
