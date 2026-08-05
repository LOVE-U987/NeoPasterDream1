#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
染梦天空盒"每晚随机"变体生成器
==============================
为每个染梦群系生成 3 套天空变体 JSON（同 biomes、同 weight），
SkyboxRenderer 每晚从候选池随机选一套 → 每个夜晚不同随机天空。

保留原有风格：每套都是 tint + 星域 + 行星 + 星座 + (极光/光带/流星 之一)，
但组合与参数略有变化（种子、极光高度、光带厚度、流星频率等）。

⚠️ 极光修正：所有极光 min_pitch/max_pitch 改为高空（天上），不贴地。
   - 强极光（frozen）: min 0.15 max 0.5
   - 中极光: min 0.1 max 0.45
   - 弱极光: min 0.05 max 0.4

运行: python tools/gen_skybox_variants.py
"""

import json
import os

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SKYBOX_DIR = os.path.join(ROOT, "PasterDream", "src", "main", "resources",
                          "data", "pasterdream", "skyboxes")

DIM = "pasterdream:dyedream_world"
FADE = 0.12
WEIGHT = 100


def col(*rgb):
    return list(rgb)


def star_textures(theme):
    return ([f"pasterdream:textures/sky/{theme}/{theme}_star_{i}.png" for i in range(1, 8)]
            + [f"pasterdream:textures/sky/{theme}/{theme}_star2_{i}.png" for i in range(1, 8)])


def planet_texture(theme, idx):
    return f"pasterdream:textures/sky/{theme}/{theme}_planet_{idx % 7 + 1}.png"


def tint(rgb, opacity):
    return {"type": "pasterdream:sky_tint", "priority": -100, "color": col(*rgb), "opacity": opacity}


def star_field(theme, count=1000, seed=1):
    return {
        "type": "pasterdream:star_field", "priority": -5,
        "textures": star_textures(theme),
        "count": count, "min_size": 0.5, "max_size": 1.5,
        "color": [1.0, 1.0, 1.0], "seed": seed,
    }


def planet_system(theme, count=7, seed_offset=0):
    layout = [
        (-2.70, -0.26, 7.6), (-1.92, 0.06, 10.2), (-1.05, 0.36, 6.4),
        (-0.18, -0.14, 8.7), (0.72, 0.24, 6.8), (1.54, -0.04, 7.0),
        (2.48, 0.42, 6.6), (-2.26, 0.90, 5.8), (1.02, 0.98, 4.8),
        (-0.86, -0.64, 6.2), (2.92, -0.50, 5.4),
    ]
    planets = []
    for i in range(count):
        yaw, pitch, size = layout[min(i, len(layout) - 1)]
        # 变体：微调 yaw/pitch/size
        yaw += (seed_offset * 0.07) % 0.3 - 0.15
        pitch += (seed_offset * 0.05) % 0.15 - 0.07
        planets.append({
            "texture": planet_texture(theme, i + seed_offset),
            "yaw": round(yaw, 2), "pitch": round(pitch, 2), "size": round(size, 1),
            "roll": round(0.1 + i * 0.25, 2),
            "roll_speed": round((1 if i % 2 == 0 else -1) * (0.0008 + i * 0.0001), 4),
            "color": [1.0, 1.0, 1.0],
            "opacity": 0.95 if i < 2 else 0.9,
        })
    return {"type": "pasterdream:planet_system", "priority": 8, "planets": planets}


def aurora(colors, opacity, min_pitch, max_pitch, bands=3, seed=1):
    """极光：pitch 全部为高空（天上），不贴地"""
    return {
        "type": "pasterdream:aurora", "priority": 20,
        "bands": bands, "segments": 200, "gradient_steps": 16,
        "center_yaw": 3.14, "sphere_radius": 2, "width": 18,
        "min_pitch": min_pitch, "max_pitch": max_pitch,
        "wave_amplitude": 0.05, "wave_frequency": 5.0,
        "depth_amplitude": 10, "ray_strength": 0.5, "edge_softness": 0.5,
        "speed": 0.005, "opacity": opacity, "seed": seed,
        "colors": [col(*c) for c in colors],
    }


def ribbons(colors, opacity, tilt=1.25, thickness=0.16, seed=1):
    return {
        "type": "pasterdream:ribbons", "priority": -12,
        "segments": 180, "gradient_steps": 10, "center_yaw": 2.95,
        "base_pitch": 0.18, "spacing": 0, "thickness": thickness,
        "arc": 6.28318, "tilt": tilt,
        "wave_amplitude": 0.032, "wave_frequency": 3.0, "wobble_amplitude": 0.035,
        "speed": 0.01, "edge_softness": 0.2, "blur": 0.35,
        "opacity": opacity, "seed": seed,
        "colors": [col(*c) for c in colors],
    }


def shooting_stars(count=2, interval=110, seed=1):
    return {
        "type": "pasterdream:shooting_stars", "priority": 24,
        "count": count, "interval_ticks": interval, "duration_ticks": 18,
        "color": [0.66, 0.92, 1.0], "seed": seed,
    }


def constellations(seed=1):
    shapes = [
        ([(-0.48, -0.24, 0.9), (-0.24, -0.02, 1.02), (0.02, 0.18, 1.28), (0.34, 0.32, 0.95), (0.5, 0.06, 0.86)],
         [[0, 1], [1, 2], [2, 3], [3, 4]], (0.68, 0.92, 1.0), -1.28, -0.46, 0.56),
        ([(-0.56, -0.06, 0.92), (-0.34, 0.18, 1.08), (-0.08, 0.04, 1.22), (0.18, 0.2, 0.98), (0.46, 0.08, 0.92), (0.58, -0.18, 0.86), (-0.2, -0.28, 0.9), (0.1, -0.18, 0.86)],
         [[0, 1], [1, 2], [2, 3], [3, 4], [1, 5], [5, 6], [6, 2]], (0.58, 0.84, 1.0), 2.06, -0.34, 0.62),
        ([(-0.38, -0.34, 0.86), (-0.18, -0.06, 0.98), (0.02, 0.24, 1.28), (0.26, 0.02, 0.94), (0.5, -0.22, 0.86), (-0.42, 0.18, 0.88), (0.22, -0.42, 0.9)],
         [[0, 1], [1, 2], [2, 3], [3, 4], [1, 5], [3, 6]], (0.86, 0.92, 1.0), 0.18, 0.82, 0.54),
    ]
    result = []
    for i, (stars, lines, c, yaw, pitch, scale) in enumerate(shapes):
        result.append({
            "type": "pasterdream:constellation", "priority": 15,
            "yaw": yaw + seed * 0.05, "pitch": pitch + seed * 0.03,
            "scale": scale, "twinkle_speed": 0.024, "line_width": 0.15,
            "color": col(*c), "seed": seed * 100 + i,
            "stars": [{"u": s[0], "v": s[1], "size": s[2]} for s in stars],
            "lines": [list(l) for l in lines],
        })
    return result


# ==================== 各群系 3 套变体 ====================
# 每套保留群系风格，但组合/参数不同

def build_warm():
    """温暖平原：暖紫调 + 光带 + 彩虹(白天)"""
    biomes = ["pasterdream:biome_dyedream_0"]
    variants = [
        # 变体 1：光带 + 强流星
        [tint((0.30, 0.18, 0.42), 0.05),
         ribbons([(0.92, 0.60, 0.42)], 0.45, seed=11),
         star_field("warm", seed=101), planet_system("warm", 7, seed_offset=0),
         shooting_stars(3, 100, seed=1001)] + constellations(seed=1),
        # 变体 2：弱极光(天上) + 光带
        [tint((0.30, 0.18, 0.42), 0.06),
         ribbons([(0.92, 0.60, 0.42)], 0.35, seed=12),
         aurora([(1.0, 0.75, 0.6), (1.0, 0.6, 0.8)], 0.25, 0.05, 0.4, bands=2, seed=201),
         star_field("warm", 1100, seed=102), planet_system("warm", 7, seed_offset=1),
         shooting_stars(2, 120, seed=1002)] + constellations(seed=2),
        # 变体 3：光带 + 多星座
        [tint((0.30, 0.18, 0.42), 0.05),
         ribbons([(0.92, 0.60, 0.42), (0.9, 0.4, 0.6)], 0.4, seed=13),
         star_field("warm", 900, seed=103), planet_system("warm", 7, seed_offset=2),
         shooting_stars(2, 130, seed=1003)] + constellations(seed=3),
    ]
    return [("warm_plains_v%d.json" % (i + 1), biomes, v) for i, v in enumerate(variants)]


def build_forest():
    """炎热森林：绿调 + 光带"""
    biomes = ["pasterdream:biome_dyedream_1"]
    variants = [
        [tint((0.16, 0.28, 0.18), 0.05),
         ribbons([(0.25, 0.75, 0.40), (0.50, 1.0, 0.60)], 0.45, seed=21),
         star_field("forest", seed=201), planet_system("forest", 7, seed_offset=0),
         shooting_stars(3, 100, seed=2001)] + constellations(seed=1),
        [tint((0.16, 0.28, 0.18), 0.06),
         ribbons([(0.25, 0.75, 0.40)], 0.35, seed=22),
         aurora([(0.4, 1.0, 0.5), (0.3, 0.9, 0.7)], 0.2, 0.05, 0.4, bands=2, seed=202),
         star_field("forest", 1100, seed=202), planet_system("forest", 7, seed_offset=1),
         shooting_stars(2, 120, seed=2002)] + constellations(seed=2),
        [tint((0.16, 0.28, 0.18), 0.05),
         ribbons([(0.25, 0.75, 0.40), (0.3, 1.0, 0.5)], 0.4, seed=23),
         star_field("forest", 900, seed=203), planet_system("forest", 7, seed_offset=2),
         shooting_stars(4, 90, seed=2003)] + constellations(seed=3),
    ]
    return [("hot_forest_v%d.json" % (i + 1), biomes, v) for i, v in enumerate(variants)]


def build_frozen():
    """寒冷冰雪：冷蓝调 + 强极光(天上)"""
    biomes = ["pasterdream:biome_dyedream_2"]
    variants = [
        [tint((0.18, 0.24, 0.42), 0.06),
         aurora([(0.25, 0.75, 1.0), (0.45, 0.50, 1.0), (0.35, 0.95, 0.9)], 0.55, 0.15, 0.5, bands=3, seed=301),
         star_field("frozen", seed=301), planet_system("frozen", 9, seed_offset=0),
         shooting_stars(2, 110, seed=3001)] + constellations(seed=1),
        [tint((0.18, 0.24, 0.42), 0.07),
         aurora([(0.5, 0.8, 1.0), (0.6, 0.6, 1.0)], 0.4, 0.1, 0.45, bands=2, seed=302),
         star_field("frozen", 1100, seed=302), planet_system("frozen", 9, seed_offset=1),
         shooting_stars(3, 100, seed=3002)] + constellations(seed=2),
        [tint((0.18, 0.24, 0.42), 0.06),
         aurora([(0.25, 0.75, 1.0), (0.45, 0.50, 1.0), (0.35, 0.95, 0.9), (0.6, 0.8, 1.0)], 0.6, 0.2, 0.55, bands=4, seed=303),
         ribbons([(0.5, 0.7, 1.0)], 0.3, seed=33),
         star_field("frozen", 900, seed=303), planet_system("frozen", 9, seed_offset=2),
         shooting_stars(2, 130, seed=3003)] + constellations(seed=3),
    ]
    return [("frozen_v%d.json" % (i + 1), biomes, v) for i, v in enumerate(variants)]


def build_ocean():
    """海洋系：深海蓝调 + 光带 + 多行星"""
    biomes = ["pasterdream:biome_dyedream_3", "pasterdream:biome_dyedream_deep_ocean",
              "pasterdream:biome_dyedream_shore", "pasterdream:biome_dyedream_river"]
    variants = [
        [tint((0.10, 0.22, 0.36), 0.05),
         ribbons([(0.12, 0.50, 0.80), (0.30, 0.75, 1.0)], 0.45, seed=41),
         star_field("ocean", seed=401), planet_system("ocean", 11, seed_offset=0),
         shooting_stars(3, 100, seed=4001)] + constellations(seed=1),
        [tint((0.10, 0.22, 0.36), 0.06),
         ribbons([(0.12, 0.50, 0.80)], 0.35, seed=42),
         aurora([(0.2, 0.6, 1.0), (0.3, 0.8, 1.0)], 0.2, 0.05, 0.4, bands=2, seed=402),
         star_field("ocean", 1100, seed=402), planet_system("ocean", 11, seed_offset=1),
         shooting_stars(2, 120, seed=4002)] + constellations(seed=2),
        [tint((0.10, 0.22, 0.36), 0.05),
         ribbons([(0.12, 0.50, 0.80), (0.3, 0.7, 1.0)], 0.4, seed=43),
         star_field("ocean", 900, seed=403), planet_system("ocean", 11, seed_offset=2),
         shooting_stars(4, 90, seed=4003)] + constellations(seed=3),
    ]
    return [("ocean_v%d.json" % (i + 1), biomes, v) for i, v in enumerate(variants)]


def build_mushroom():
    """蘑菇平原：菌紫调 + 弱极光(天上)"""
    biomes = ["pasterdream:biome_dyedream_mushroom_plains"]
    variants = [
        [tint((0.24, 0.16, 0.34), 0.05),
         aurora([(0.75, 0.55, 1.0), (0.55, 0.75, 1.0)], 0.3, 0.05, 0.4, bands=2, seed=501),
         star_field("mushroom", seed=501), planet_system("mushroom", 7, seed_offset=0),
         shooting_stars(2, 120, seed=5001)] + constellations(seed=1),
        [tint((0.24, 0.16, 0.34), 0.06),
         ribbons([(0.6, 0.5, 1.0), (0.7, 0.6, 1.0)], 0.4, seed=51),
         star_field("mushroom", 1100, seed=502), planet_system("mushroom", 7, seed_offset=1),
         shooting_stars(3, 100, seed=5002)] + constellations(seed=2),
        [tint((0.24, 0.16, 0.34), 0.05),
         aurora([(0.75, 0.55, 1.0)], 0.2, 0.05, 0.35, bands=1, seed=503),
         ribbons([(0.6, 0.5, 1.0)], 0.3, seed=52),
         star_field("mushroom", 900, seed=503), planet_system("mushroom", 7, seed_offset=2),
         shooting_stars(2, 130, seed=5003)] + constellations(seed=3),
    ]
    return [("mushroom_v%d.json" % (i + 1), biomes, v) for i, v in enumerate(variants)]


def build_dense():
    """染梦密林：粉紫调 + 光带 + 弱极光(天上)"""
    biomes = ["pasterdream:biome_dyedream_dense_forest"]
    variants = [
        [tint((0.30, 0.16, 0.28), 0.05),
         ribbons([(0.90, 0.45, 0.70), (0.70, 0.50, 1.0), (0.45, 0.80, 1.0)], 0.45, seed=61),
         star_field("dense", seed=601), planet_system("dense", 7, seed_offset=0),
         shooting_stars(3, 100, seed=6001)] + constellations(seed=1),
        [tint((0.30, 0.16, 0.28), 0.06),
         aurora([(0.9, 0.55, 0.75), (0.6, 0.7, 1.0)], 0.25, 0.05, 0.4, bands=2, seed=602),
         ribbons([(0.90, 0.45, 0.70)], 0.35, seed=62),
         star_field("dense", 1100, seed=602), planet_system("dense", 7, seed_offset=1),
         shooting_stars(2, 120, seed=6002)] + constellations(seed=2),
        [tint((0.30, 0.16, 0.28), 0.05),
         ribbons([(0.90, 0.45, 0.70), (0.7, 0.5, 1.0)], 0.4, seed=63),
         star_field("dense", 900, seed=603), planet_system("dense", 7, seed_offset=2),
         shooting_stars(4, 90, seed=6003)] + constellations(seed=3),
    ]
    return [("dense_forest_v%d.json" % (i + 1), biomes, v) for i, v in enumerate(variants)]


def main():
    builders = [build_warm(), build_forest(), build_frozen(), build_ocean(), build_mushroom(), build_dense()]
    for builder in builders:
        for file_name, biomes, layers in builder:
            skybox = {
                "fade_speed": FADE,
                "weight": WEIGHT,
                "dimensions": [DIM],
                "biomes": biomes,
                "layers": layers,
            }
            path = os.path.join(SKYBOX_DIR, file_name)
            with open(path, "w", encoding="utf-8") as fh:
                json.dump(skybox, fh, ensure_ascii=False, indent=2)
                fh.write("\n")
            print(f"  {file_name}: biomes={biomes} layers={len(layers)}")
    print("完成! 共 18 套变体")


if __name__ == "__main__":
    main()
