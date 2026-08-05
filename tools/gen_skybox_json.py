#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
染梦天空盒 JSON 生成器（Stellara 比例镜像 v2）
============================================
严格参考仿照模组 Stellara 各群系的比例参数生成染梦天空盒。

比例基准（从 Stellara JSON 实测提取）:
  star_field : count=1000~1200, min_size=0.45~0.5, max_size=1.5~1.6
  aurora     : frozen → width=7 min_pitch=0.25 max_pitch=0.55 opacity=0.75 (高空窄带)
               mushroom → width=20 min_pitch=-0.25 max_pitch=0 opacity=0.1 bands=1
               swamp/tropic → width=20 min_pitch=-0.25 max_pitch=-0.1 opacity=0.05 bands=3
  planet     : 每颗不同纹理(7张循环), pitch 含高轨 0.9/0.98, 尺寸 4.8~11 梯度
  constellation : 每群系 5~6 个, scale=0.5~0.62 多样, line_width=0.14~0.16
  ribbons    : aquatic 细 tilt=1.25 thick=0.08 blur=0.25 opacity=0.22

运行: python tools/gen_skybox_json.py
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


def tint(rgb, opacity):
    return {"type": "pasterdream:sky_tint", "priority": -100,
            "color": col(*rgb), "opacity": opacity}


def star_field(theme, count=1000, min_size=0.5, max_size=1.5):
    """Stellara 比例：count 1000~1200, min 0.45~0.5, max 1.5~1.6"""
    return {
        "type": "pasterdream:star_field", "priority": -5,
        "textures": [f"pasterdream:textures/sky/{theme}/{theme}_star_{i}.png" for i in range(1, 8)]
                   + [f"pasterdream:textures/sky/{theme}/{theme}_star2_{i}.png" for i in range(1, 8)],
        "count": count, "min_size": min_size, "max_size": max_size,
        "color": [1.0, 1.0, 1.0], "seed": hash(theme) % 100000,
    }


def aurora(colors, opacity, width, min_pitch, max_pitch, bands, speed=0.005):
    """Stellara 比例极光：width/pitch 决定高空窄带或低空宽带"""
    return {
        "type": "pasterdream:aurora", "priority": 20,
        "bands": bands, "segments": 200, "gradient_steps": 16,
        "center_yaw": 3.14, "sphere_radius": 2, "width": width,
        "min_pitch": min_pitch, "max_pitch": max_pitch,
        "wave_amplitude": 0.05, "wave_frequency": 5.0,
        "depth_amplitude": 10, "ray_strength": 0.5, "edge_softness": 0.5,
        "speed": speed, "opacity": opacity,
        "colors": [col(*c) for c in colors],
    }


def ribbons(colors, opacity, tilt, thickness, blur, segments=180, steps=10):
    """Stellara 比例光带：aquatic 细(tilt1.25) / the_end 粗(tilt0.72)"""
    return {
        "type": "pasterdream:ribbons", "priority": -12,
        "segments": segments, "gradient_steps": steps, "center_yaw": 2.95,
        "base_pitch": 0.18, "spacing": 0, "thickness": thickness,
        "arc": 6.28318, "tilt": tilt,
        "wave_amplitude": 0.032, "wave_frequency": 3.0, "wobble_amplitude": 0.035,
        "speed": 0.01, "edge_softness": 0.2, "blur": blur,
        "opacity": opacity, "seed": 114902,
        "colors": [col(*c) for c in colors],
    }


def shooting_stars(count=2, interval=115, duration=18):
    """Stellara 比例：count 2~3, interval 100~125, dur 18"""
    return {
        "type": "pasterdream:shooting_stars", "priority": 24,
        "count": count, "interval_ticks": interval, "duration_ticks": duration,
        "color": [0.66, 0.92, 1.0], "seed": 114904,
    }


def planet(theme, index):
    """行星：7 张纹理循环 + Stellara aquatic 布局（含高轨 pitch 0.9/0.98）"""
    tex = index % 7 + 1
    layout = [
        (-2.70, -0.26, 7.6), (-1.92, 0.06, 10.2), (-1.05, 0.36, 6.4),
        (-0.18, -0.14, 8.7), (0.72, 0.24, 6.8), (1.54, -0.04, 7.0),
        (2.48, 0.42, 6.6), (-2.26, 0.90, 5.8), (1.02, 0.98, 4.8),
        (-0.86, -0.64, 6.2), (2.92, -0.50, 5.4),
    ]
    yaw, pitch, size = layout[min(index, len(layout) - 1)]
    return {
        "texture": f"pasterdream:textures/sky/{theme}/{theme}_planet_{tex}.png",
        "yaw": yaw, "pitch": pitch, "size": size,
        "roll": 0.1 + index * 0.25,
        "roll_speed": (1 if index % 2 == 0 else -1) * (0.0008 + index * 0.0001),
        "color": [1.0, 1.0, 1.0],
        "opacity": round(0.86 - index * 0.03, 2),
    }


def planet_system(theme, count=7):
    return {"type": "pasterdream:planet_system", "priority": 8,
            "planets": [planet(theme, i) for i in range(count)]}


def constellation(stars, lines, color, yaw, pitch, scale=0.55, line_width=0.15, twinkle=0.024):
    """星座：scale/lw/tw 可多样（Stellara scale 0.5~0.62）"""
    return {
        "type": "pasterdream:constellation", "priority": 15,
        "yaw": yaw, "pitch": pitch, "scale": scale,
        "twinkle_speed": twinkle, "line_width": line_width,
        "color": col(*color),
        "stars": [{"u": s[0], "v": s[1], "size": s[2] if len(s) > 2 else 1.0} for s in stars],
        "lines": [list(l) for l in lines],
    }


def rainbow():
    return {
        "type": "pasterdream:rainbow", "priority": 6,
        "yaw": 3.14, "base_pitch": -0.16, "radius": 1.25, "thickness": 0.35,
        "segments": 96, "arc": 2.8, "opacity": 0.45, "blur": 1.0,
        "colors": [[1.0, 0.12, 0.10], [1.0, 0.48, 0.08], [1.0, 0.88, 0.12],
                   [0.18, 0.78, 0.22], [0.18, 0.86, 1.0], [0.10, 0.48, 1.0],
                   [0.42, 0.18, 0.88]],
    }


# ==================== 星座形状（5 个，参考 Stellara the_end 5 星座） ====================
SHAPE_W = [[-0.48, -0.24, 0.9], [-0.24, -0.02, 1.02], [0.02, 0.18, 1.28],
           [0.34, 0.32, 0.95], [0.5, 0.06, 0.86]]
SHAPE_UMBRELLA = [[-0.56, -0.06, 0.92], [-0.34, 0.18, 1.08], [-0.08, 0.04, 1.22],
                  [0.18, 0.2, 0.98], [0.46, 0.08, 0.92], [0.58, -0.18, 0.86],
                  [-0.2, -0.28, 0.9], [0.1, -0.18, 0.86]]
SHAPE_CROSS = [[-0.38, -0.34, 0.86], [-0.18, -0.06, 0.98], [0.02, 0.24, 1.28],
               [0.26, 0.02, 0.94], [0.5, -0.22, 0.86], [-0.42, 0.18, 0.88],
               [0.22, -0.42, 0.9]]
SHAPE_V = [[-0.5, 0.22, 0.9], [-0.24, 0.08, 1.04], [0.02, -0.08, 1.3],
           [0.28, 0.08, 0.96], [0.5, 0.28, 0.86], [-0.12, -0.36, 0.9], [0.22, -0.34, 0.86]]
SHAPE_DIAMOND = [[-0.36, 0.02, 0.94], [-0.1, 0.16, 1.14], [0.16, 0.18, 1.05],
                 [0.32, -0.1, 0.9], [0.0, -0.28, 0.94]]


def constellations_5():
    """5 个星座（scale 0.5~0.62 多样化，参考 Stellara the_end）"""
    return [
        constellation(SHAPE_W, [[0, 1], [1, 2], [2, 3], [3, 4]], (0.68, 0.92, 1.0),
                      -1.28, -0.46, scale=0.56, line_width=0.15, twinkle=0.024),
        constellation(SHAPE_UMBRELLA, [[0, 1], [1, 2], [2, 3], [3, 4], [1, 5], [5, 6], [6, 2]],
                      (0.58, 0.84, 1.0), 2.06, -0.34, scale=0.62, line_width=0.14, twinkle=0.022),
        constellation(SHAPE_CROSS, [[0, 1], [1, 2], [2, 3], [3, 4], [1, 5], [3, 6]],
                      (0.86, 0.92, 1.0), 0.18, 0.82, scale=0.54, line_width=0.14, twinkle=0.025),
        constellation(SHAPE_V, [[0, 1], [1, 2], [2, 3], [3, 4], [2, 5], [5, 6], [6, 3]],
                      (0.72, 0.78, 1.0), -2.58, 0.58, scale=0.55, line_width=0.14, twinkle=0.024),
        constellation(SHAPE_DIAMOND, [[0, 1], [1, 2], [2, 3], [3, 4], [2, 5]],
                      (0.74, 1.0, 0.96), 2.7, 1.02, scale=0.5, line_width=0.15, twinkle=0.022),
    ]


def constellations_3():
    """3 星座（紧凑版，用于有极光的群系避免过密）"""
    return [
        constellation(SHAPE_W, [[0, 1], [1, 2], [2, 3], [3, 4]], (0.68, 0.92, 1.0),
                      -1.28, -0.46, scale=0.56, line_width=0.15, twinkle=0.024),
        constellation(SHAPE_UMBRELLA, [[0, 1], [1, 2], [2, 3], [3, 4], [1, 5], [5, 6], [6, 2]],
                      (0.58, 0.84, 1.0), 2.06, -0.34, scale=0.62, line_width=0.14, twinkle=0.022),
        constellation(SHAPE_CROSS, [[0, 1], [1, 2], [2, 3], [3, 4], [1, 5], [3, 6]],
                      (0.86, 0.92, 1.0), 0.18, 0.82, scale=0.54, line_width=0.14, twinkle=0.025),
    ]


# ==================== 各群系天空盒组装 ====================

def build_warm():
    """参考 plains + aquatic：白天彩虹 + 细光带 + 5 星座"""
    layers = [
        tint((0.75, 0.55, 0.80), 0.05),
        ribbons([(0.92, 0.60, 0.42)], 0.22, tilt=1.25, thickness=0.08, blur=0.25),
        star_field("warm", count=1000, min_size=0.5, max_size=1.5),
        shooting_stars(2, interval=115, duration=18),
        planet_system("warm", 7),
    ]
    layers += constellations_5()
    layers.append(rainbow())
    return {"warm_plains.json": ("pasterdream:biome_dyedream_0", layers)}


def build_forest():
    """参考 forest：无极光，5 星座"""
    layers = [
        tint((0.10, 0.46, 0.60), 0.06),
        ribbons([(0.25, 0.75, 0.40), (0.50, 1.0, 0.60)], 0.18, tilt=0.9, thickness=0.09, blur=0.3),
        star_field("forest", count=1000, min_size=0.5, max_size=1.5),
        shooting_stars(2, interval=115, duration=18),
        planet_system("forest", 7),
    ]
    layers += constellations_5()
    return {"hot_forest.json": ("pasterdream:biome_dyedream_1", layers)}


def build_frozen():
    """参考 frozen：高空窄带强极光 + 9 行星"""
    layers = [
        tint((0.86, 0.58, 0.32), 0.055),
        aurora([(0.25, 0.75, 1.0), (0.45, 0.50, 1.0), (0.35, 0.95, 0.9), (0.6, 0.8, 1.0)],
               opacity=0.75, width=7, min_pitch=0.25, max_pitch=0.55, bands=3),
        star_field("frozen", count=1000, min_size=0.5, max_size=1.5),
        shooting_stars(2, interval=110, duration=18),
        planet_system("frozen", 9),
    ]
    layers += constellations_3()
    return {"frozen.json": ("pasterdream:biome_dyedream_2", layers)}


def build_ocean():
    """参考 aquatic：11 行星 + 细光带 + 5 星座"""
    layers = [
        tint((0.18, 0.72, 0.46), 0.05),
        ribbons([(0.12, 0.50, 0.80), (0.30, 0.75, 1.0)], 0.22, tilt=1.25, thickness=0.08, blur=0.25),
        star_field("ocean", count=1000, min_size=0.5, max_size=1.5),
        shooting_stars(2, interval=110, duration=18),
        planet_system("ocean", 11),
    ]
    layers += constellations_5()
    return {"ocean.json": ("pasterdream:biome_dyedream_3,pasterdream:biome_dyedream_deep_ocean,"
                           "pasterdream:biome_dyedream_shore,pasterdream:biome_dyedream_river", layers)}


def build_mushroom():
    """参考 mushroom：微弱极光 + 7 行星 + 5 星座"""
    layers = [
        tint((0.82, 0.50, 1.0), 0.05),
        aurora([(0.75, 0.55, 1.0), (0.55, 0.75, 1.0), (0.85, 0.65, 1.0), (0.6, 0.7, 1.0), (0.9, 0.8, 1.0), (0.7, 0.6, 1.0)],
               opacity=0.1, width=20, min_pitch=-0.25, max_pitch=0, bands=1),
        star_field("mushroom", count=1000, min_size=0.5, max_size=1.5),
        shooting_stars(2, interval=120, duration=18),
        planet_system("mushroom", 7),
    ]
    layers += constellations_5()
    return {"mushroom.json": ("pasterdream:biome_dyedream_mushroom_plains", layers)}


def build_dense():
    """参考 swamp：细光带 + 微弱极光 + 5 星座"""
    layers = [
        tint((0.16, 0.42, 0.24), 0.07),
        ribbons([(0.90, 0.45, 0.70), (0.70, 0.50, 1.0), (0.45, 0.80, 1.0)], 0.14, tilt=1.1, thickness=0.1, blur=0.35),
        aurora([(0.90, 0.55, 0.75)], opacity=0.05, width=20, min_pitch=-0.25, max_pitch=-0.1, bands=3),
        star_field("dense", count=1000, min_size=0.5, max_size=1.5),
        shooting_stars(2, interval=125, duration=18),
        planet_system("dense", 7),
    ]
    layers += constellations_5()
    return {"dense_forest.json": ("pasterdream:biome_dyedream_dense_forest", layers)}


def main():
    builders = [build_warm(), build_forest(), build_frozen(), build_ocean(), build_mushroom(), build_dense()]
    for builder in builders:
        for file_name, (biomes_str, layers) in builder.items():
            biomes = [b.strip() for b in biomes_str.split(",") if b.strip()]
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
    print("完成!")


if __name__ == "__main__":
    main()
