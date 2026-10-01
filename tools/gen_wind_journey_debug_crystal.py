"""生成风之旅途调试水晶纹理。

风格对齐现有调试水晶共享纹理 item/life_crystal.png：菱形晶体、柔和渐变、
中心白色高光、外围微弱光晕；配色改为风之旅途的青蓝系，便于与其它调试水晶区分，
同时保持同一套视觉语言。该纹理供所有风之旅途地物与相关结构的调试水晶共用。

用法：python tools/gen_wind_journey_debug_crystal.py
输出：PasterDream/src/main/resources/assets/pasterdream/textures/item/wind_journey_debug_crystal.png
"""

from pathlib import Path

from PIL import Image

# 画布尺寸：与 life_crystal.png 一致
SIZE = 16

# 输出路径
OUT = (
    Path(__file__).resolve().parents[1]
    / "PasterDream/src/main/resources/assets/pasterdream/textures/item/wind_journey_debug_crystal.png"
)

# 风之旅途配色：深青 -> 中青 -> 浅青白，中心高光为纯白
DEEP = (0x1E, 0x6E, 0x9C)
MID = (0x4F, 0xB8, 0xE0)
LIGHT = (0xC8, 0xF2, 0xFF)
CORE = (0xFF, 0xFF, 0xFF)


def lerp(a, b, t):
    """按权重 t 在两个 RGB 三元组之间线性插值。"""
    return tuple(round(a[i] + (b[i] - a[i]) * t) for i in range(3))


def build() -> Image.Image:
    """构建 16x16 菱形调试水晶纹理。"""
    img = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    px = img.load()

    center = (SIZE - 1) / 2.0
    radius = 8.0  # 曼哈顿半径：菱形恰好横跨整格

    for y in range(SIZE):
        for x in range(SIZE):
            dx = x - center
            dy = y - center
            manhattan = abs(dx) + abs(dy)

            if manhattan <= radius:
                # 纵向渐变：上浅下深
                t = max(0.0, min(1.0, (dy + radius) / (2 * radius)))
                if t < 0.5:
                    base = lerp(LIGHT, MID, t * 2)
                else:
                    base = lerp(MID, DEEP, (t - 0.5) * 2)

                # 中心高光：越靠中心越接近纯白
                euclid = (dx * dx + dy * dy) ** 0.5
                glow = max(0.0, 1.0 - euclid / 4.2) ** 1.4
                color = lerp(base, CORE, glow * 0.85)

                # 边缘柔化，避免生硬锯齿
                edge = max(0.0, min(1.0, (radius - manhattan) / 1.4))
                px[x, y] = (*color, int(255 * edge))
            elif manhattan <= radius + 2.0:
                # 外围光晕
                halo = max(0.0, 1.0 - (manhattan - radius) / 2.0)
                px[x, y] = (*MID, int(70 * halo))

    return img


if __name__ == "__main__":
    OUT.parent.mkdir(parents=True, exist_ok=True)
    build().save(OUT)
    print(f"written: {OUT}")
