#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
染梦天空系统纹理生成器
=====================
程序化生成天空盒所需的星星/行星/卫星纹理，输出到:
  PasterDream/src/main/resources/assets/pasterdream/textures/sky/<theme>/

设计:
  - common/star_1~7.png : 32x32 白色星星 7 帧(不同形状/光芒), 供星座着色复用
  - <theme>_star_1~7.png : 32x32 群系主题主色星星 7 帧
  - <theme>_star2_1~7.png : 32x32 群系主题次色星星 7 帧(与主色混合, 星空层次更丰富)
  - <theme>_planet_1/2.png : 128x128 渐变行星(每主题配色不同)
  - <theme>_satellite_1.png : 64x64 卫星
主题: warm(暖橙) / forest(翠绿) / frozen(冰蓝) / ocean(深海蓝) / mushroom(菌紫) / dense(粉紫)

运行: python tools/gen_sky_textures.py
依赖: Pillow
"""

import math
import os
import random

from PIL import Image, ImageDraw

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUT_DIR = os.path.join(ROOT, "PasterDream", "src", "main", "resources", "assets",
                       "pasterdream", "textures", "sky")

# ============ 星星纹理(7 帧, 支持颜色) ============

def _star_core(draw, cx, cy, radius, color, max_alpha=255):
    """径向渐变光晕核：中心不透明、边缘渐隐到透明（Alpha 方向正确）"""
    steps = 16
    for i in range(steps, 0, -1):
        t = i / steps          # 1(外) → 0.0625(中心)
        r = radius * t
        # 中心(t小)alpha 高，边缘(t大)alpha 渐隐到 0
        alpha = int(max_alpha * (1.0 - max(0.0, (t - 0.7) / 0.3)))
        alpha = max(0, min(max_alpha, alpha))
        draw.ellipse([cx - r, cy - r, cx + r, cy + r], fill=(color[0], color[1], color[2], alpha))


def _star_spikes(draw, cx, cy, radius, color, count, max_alpha=255):
    """光芒射线"""
    for k in range(count):
        angle = k * (2 * math.pi / count)
        length = radius * (0.9 + 0.3 * ((k * 37) % 5) / 5.0)
        x1 = cx + math.cos(angle) * radius * 0.25
        y1 = cy + math.sin(angle) * radius * 0.25
        x2 = cx + math.cos(angle) * length
        y2 = cy + math.sin(angle) * length
        width = max(1, int(radius * 0.12))
        draw.line([x1, y1, x2, y2], fill=(color[0], color[1], color[2], max_alpha), width=width)


def make_star_frame(index, size=32, color=(255, 255, 255)):
    """生成第 index 帧星星(不同形状), color 为 RGB 颜色"""
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx = cy = size / 2.0
    base = size * 0.5

    if index == 0:      # 纯圆点光晕
        _star_core(draw, cx, cy, base * 0.5, color)
    elif index == 1:    # 四芒星
        _star_core(draw, cx, cy, base * 0.42, color)
        _star_spikes(draw, cx, cy, base * 0.75, color, 4)
    elif index == 2:    # 六芒星
        _star_core(draw, cx, cy, base * 0.4, color)
        _star_spikes(draw, cx, cy, base * 0.8, color, 6)
    elif index == 3:    # 八芒星
        _star_core(draw, cx, cy, base * 0.38, color)
        _star_spikes(draw, cx, cy, base * 0.85, color, 8)
    elif index == 4:    # 菱形
        r = base * 0.45
        points = [(cx, cy - r), (cx + r * 0.5, cy), (cx, cy + r), (cx - r * 0.5, cy)]
        draw.polygon(points, fill=(color[0], color[1], color[2], 235))
        _star_core(draw, cx, cy, base * 0.3, color, 200)
    elif index == 5:    # 带光环
        _star_core(draw, cx, cy, base * 0.3, color)
        ring_r = base * 0.55
        ring_w = max(1, int(size * 0.05))
        draw.ellipse([cx - ring_r, cy - ring_r, cx + ring_r, cy + ring_r],
                     outline=(color[0], color[1], color[2], 160), width=ring_w)
    else:               # 十字光芒
        _star_core(draw, cx, cy, base * 0.4, color)
        for angle in (0, math.pi / 2):
            x1 = cx + math.cos(angle) * base * 0.2
            y1 = cy + math.sin(angle) * base * 0.2
            x2 = cx + math.cos(angle) * base * 0.9
            y2 = cy + math.sin(angle) * base * 0.9
            draw.line([x1, y1, x2, y2], fill=(color[0], color[1], color[2], 220), width=max(1, int(size * 0.08)))
    return img


# ============ 行星/卫星纹理 ============

def _radial_gradient(size, center_color, edge_color):
    """径向渐变球体：中心不透明、边缘渐隐到透明（Alpha 方向正确）"""
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx = cy = size / 2.0
    r = size / 2.0
    steps = 64
    for i in range(steps, 0, -1):
        t = i / steps          # 1(外) → 0.0156(中心)
        rr = r * t
        col = tuple(int(center_color[j] + (edge_color[j] - center_color[j]) * (1 - t)) for j in range(3))
        # 中心(t小)alpha 255 不透明，边缘(t大)最后 18% 半径内渐隐到 0
        alpha = int(255 * (1.0 - max(0.0, (t - 0.82) / 0.18)))
        alpha = max(0, min(255, alpha))
        draw.ellipse([cx - rr, cy - rr, cx + rr, cy + rr], fill=(col[0], col[1], col[2], alpha))
    return img


def _add_surface_noise(img, rng, density=0.06, radius_scale=0.85):
    """叠加随机表面斑纹(模拟大陆/云带)，确保不超出圆形边界"""
    size = img.size[0]
    cx = cy = size / 2.0
    draw = ImageDraw.Draw(img)
    rr = size * radius_scale / 2.0
    max_dist = rr - 1.0       # 斑纹中心不超过圆内，避免画出边界
    count = int(size * size * density / 100)
    for _ in range(count):
        angle = rng.uniform(0, 2 * math.pi)
        dist = rng.uniform(0, max_dist)
        x = cx + math.cos(angle) * dist
        y = cy + math.sin(angle) * dist
        w = rng.uniform(1.5, size * 0.08)
        h = rng.uniform(1.5, size * 0.08)
        shade = rng.uniform(0.75, 1.25)
        base = img.getpixel((int(x), int(y)))
        if base[3] > 0:
            col = tuple(max(0, min(255, int(c * shade))) for c in base[:3])
            # 斑纹仅在原像素不透明处绘制，且限制在圆内
            draw.ellipse([x - w / 2, y - h / 2, x + w / 2, y + h / 2],
                         fill=(col[0], col[1], col[2], base[3]))
    # 用圆形 mask 裁剪，确保圆外绝对透明
    mask = Image.new("L", (size, size), 0)
    md = ImageDraw.Draw(mask)
    md.ellipse([0, 0, size - 1, size - 1], fill=255)
    img.putalpha(Image.composite(img.split()[3], Image.new("L", (size, size), 0), mask))


def _add_band(img, color, y_offset, thickness, alpha=70):
    """水平色带(模拟气态行星纹理)"""
    size = img.size[0]
    cx = cy = size / 2.0
    r = size * 0.48
    draw = ImageDraw.Draw(img)
    y1 = cy + y_offset - thickness / 2
    y2 = cy + y_offset + thickness / 2
    draw.ellipse([cx - r, y1, cx + r, y2], fill=(color[0], color[1], color[2], alpha))


def make_planet(size, top_color, bottom_color, rng, band_color=None, band_offset=0.0):
    """生成行星纹理(垂直渐变 + 噪声 + 可选色带)"""
    img = _radial_gradient(size, top_color, bottom_color)
    if band_color:
        _add_band(img, band_color, size * band_offset, size * 0.08)
    _add_surface_noise(img, rng)
    return img


def make_satellite(size, color, rng):
    """生成卫星纹理(单一配色小球)"""
    darker = tuple(int(c * 0.55) for c in color)
    img = _radial_gradient(size, color, darker)
    _add_surface_noise(img, rng, density=0.15)
    return img


# ============ 主题配色 ============

THEMES = {
    "warm": {       # 温暖平原: 暖橙红 + 粉白星星
        "star1": (255, 226, 208),   # 暖白
        "star2": (255, 186, 204),   # 粉
        # 7 张行星配色（参考 Stellara planet_1..7 多纹理）
        "planets": [
            ((255, 210, 150), (200, 90, 60), (255, 240, 200)),
            ((250, 180, 120), (160, 70, 50), None),
            ((255, 200, 170), (140, 55, 80), (255, 230, 210)),
            ((230, 160, 100), (120, 45, 40), None),
            ((255, 220, 180), (180, 80, 70), (255, 245, 220)),
            ((245, 170, 130), (110, 40, 60), None),
            ((255, 190, 145), (150, 60, 45), (255, 235, 205)),
        ],
        "satellite": (200, 140, 100),
    },
    "forest": {     # 炎热森林: 翠绿 + 绿星星
        "star1": (186, 255, 208),   # 翠绿
        "star2": (208, 255, 178),   # 嫩绿
        "planets": [
            ((170, 240, 170), (40, 130, 70), (220, 255, 220)),
            ((150, 220, 130), (30, 100, 60), None),
            ((120, 200, 150), (25, 85, 50), (200, 255, 200)),
            ((90, 180, 130), (20, 70, 45), None),
            ((160, 235, 185), (50, 120, 75), (230, 255, 230)),
            ((130, 210, 120), (35, 90, 55), None),
            ((110, 190, 160), (28, 78, 52), (210, 250, 215)),
        ],
        "satellite": (120, 190, 120),
    },
    "frozen": {     # 寒冷冰雪: 冰蓝 + 蓝白星星
        "star1": (196, 228, 255),   # 冰蓝
        "star2": (150, 200, 255),   # 亮蓝
        "planets": [
            ((220, 240, 255), (90, 150, 230), (255, 255, 255)),
            ((190, 220, 255), (60, 110, 200), None),
            ((170, 210, 250), (50, 95, 185), (240, 250, 255)),
            ((205, 230, 255), (75, 130, 215), None),
            ((180, 215, 245), (65, 105, 195), (255, 255, 255)),
            ((160, 200, 240), (45, 85, 175), None),
            ((195, 225, 255), (80, 140, 225), (245, 252, 255)),
        ],
        "satellite": (170, 200, 250),
    },
    "ocean": {      # 海洋: 深海蓝 + 青蓝星星
        "star1": (176, 232, 255),   # 天青
        "star2": (122, 198, 242),   # 深青
        "planets": [
            ((130, 200, 255), (15, 60, 140), (180, 230, 255)),
            ((100, 180, 240), (10, 45, 110), None),
            ((80, 160, 230), (8, 38, 95), (160, 215, 250)),
            ((115, 190, 250), (20, 55, 130), None),
            ((140, 210, 255), (12, 50, 120), (190, 235, 255)),
            ((70, 150, 220), (6, 32, 85), None),
            ((125, 195, 245), (18, 48, 115), (170, 225, 255)),
        ],
        "satellite": (90, 160, 220),
    },
    "mushroom": {   # 蘑菇平原: 菌紫 + 紫星星
        "star1": (224, 198, 255),   # 淡紫
        "star2": (188, 152, 255),   # 亮紫
        "planets": [
            ((230, 190, 255), (120, 60, 190), (250, 230, 255)),
            ((200, 160, 240), (90, 45, 160), None),
            ((180, 140, 230), (75, 35, 145), (240, 215, 255)),
            ((215, 175, 250), (105, 52, 175), None),
            ((235, 205, 255), (130, 68, 200), (255, 240, 255)),
            ((170, 125, 220), (65, 30, 130), None),
            ((210, 165, 245), (100, 48, 168), (245, 225, 255)),
        ],
        "satellite": (180, 130, 230),
    },
    "dense": {      # 密林: 粉紫 + 粉红星星
        "star1": (255, 198, 224),   # 粉
        "star2": (255, 166, 196),   # 玫粉
        "planets": [
            ((255, 190, 220), (160, 60, 130), (255, 230, 240)),
            ((240, 160, 200), (130, 50, 110), None),
            ((220, 140, 185), (110, 40, 95), (250, 210, 230)),
            ((250, 175, 215), (145, 55, 120), None),
            ((255, 205, 230), (170, 68, 140), (255, 240, 248)),
            ((210, 130, 175), (100, 35, 85), None),
            ((245, 165, 210), (140, 52, 115), (252, 220, 238)),
        ],
        "satellite": (220, 140, 180),
    },
}


def main():
    print(f"输出目录: {OUT_DIR}")
    os.makedirs(os.path.join(OUT_DIR, "common"), exist_ok=True)

    # 1. 白色星星 7 帧(通用, 星座用)
    for i in range(7):
        frame = make_star_frame(i)
        path = os.path.join(OUT_DIR, "common", f"star_{i + 1}.png")
        frame.save(path)
        print(f"  [星星] {path}")

    # 2. 群系主题星星 + 7 张行星 + 卫星
    for theme, cfg in THEMES.items():
        theme_dir = os.path.join(OUT_DIR, theme)
        os.makedirs(theme_dir, exist_ok=True)
        rng = random.Random(f"pasterdream-{theme}")

        # 主题主色/次色星星各 7 帧
        for i in range(7):
            s1 = make_star_frame(i, color=cfg["star1"])
            s1.save(os.path.join(theme_dir, f"{theme}_star_{i + 1}.png"))
            s2 = make_star_frame(i, color=cfg["star2"])
            s2.save(os.path.join(theme_dir, f"{theme}_star2_{i + 1}.png"))

        # 7 张不同配色的行星（参考 Stellara planet_1..7 多纹理）
        for idx, (top, bottom, band) in enumerate(cfg["planets"], start=1):
            band_offset = 0.12 if idx % 2 == 1 else -0.1
            p = make_planet(128, top, bottom, rng, band_color=band, band_offset=band_offset)
            p.save(os.path.join(theme_dir, f"{theme}_planet_{idx}.png"))

        sat = make_satellite(64, cfg["satellite"], rng)
        sat.save(os.path.join(theme_dir, f"{theme}_satellite_1.png"))

        print(f"  [主题] {theme}: 星星×14 / 行星×7 / 卫星×1 已生成")

    print("完成!")


if __name__ == "__main__":
    main()
