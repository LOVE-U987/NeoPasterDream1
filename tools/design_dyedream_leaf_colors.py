# -*- coding: utf-8 -*-
"""设计染梦群系树叶专属目标色方案，计算混色(0.5)后实际 tint 与最终显示色。"""
from __future__ import annotations

BASE = (146, 85, 127)  # 染梦树叶基础色（原纹理平均）

# 目标色设计：粉紫系渐变，每群系有辨识度（RGB）
TARGETS = {
    "平原 0":        (235, 135, 195),  # 亮粉
    "森林 1":        (175, 105, 195),  # 紫
    "冰雪 2":        (170, 150, 235),  # 蓝紫(冷)
    "高原 3":        (215, 140, 175),  # 粉紫
    "深海":          (150, 120, 210),  # 深蓝紫(暗)
    "密林":          (155, 90, 175),   # 深紫(暗)
    "蘑菇平原":      (240, 155, 220),  # 亮粉紫
    "河流":          (235, 135, 195),  # 同平原
    "海岸":          (185, 165, 225),  # 淡蓝紫
}


def to_hex(rgb: tuple) -> str:
    return "0x%02X%02X%02X" % rgb


def to_int(rgb: tuple) -> int:
    """转成 Minecraft 的带符号 int 颜色值（0xRRGGBB，>=0x800000 时符号位为负）。"""
    v = (rgb[0] << 16) | (rgb[1] << 8) | rgb[2]
    return v - 0x1000000 if v >= 0x800000 else v


def mix(base: tuple, target: tuple, ratio: float) -> tuple:
    return tuple(round(base[i] + (target[i] - base[i]) * ratio) for i in range(3))


print("=== 方案：代码硬编码群系专属目标色（不依赖 JSON 杂色） ===")
for name, t in TARGETS.items():
    m = mix(BASE, t, 0.5)
    # 最终显示色 = 灰纹理亮度(约107) * tint / 255
    final = tuple(round(107 * c / 255) for c in m)
    print(f"{name:10s}: target={t} {to_hex(t):10s} ({to_int(t):>9d}) | mix={m} {to_hex(m):10s} ({to_int(m):>9d}) | 最终显示色 {final}")
