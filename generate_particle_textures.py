"""
PasterDream 粒子纹理生成脚本
用于生成 Task 4 所需的 4 种染梦世界环境粒子 PNG 纹理占位图

用法: python generate_particle_textures.py
输出: PasterDream/src/main/resources/assets/pasterdream/textures/particle/

每个纹理为 16x16 像素的 PNG，中心为带渐变光晕的圆形图案。
"""

import struct
import zlib
import os

def create_png(width, height, pixels):
    """
    创建一个简单的 RGBA PNG 文件
    pixels: 长度为 width * height * 4 的字节数组 (RGBA)
    """
    def write_chunk(chunk_type, data):
        chunk = chunk_type + data
        crc = struct.pack('>I', zlib.crc32(chunk) & 0xFFFFFFFF)
        return struct.pack('>I', len(data)) + chunk + crc

    # PNG signature
    signature = b'\x89PNG\r\n\x1a\n'

    # IHDR chunk
    ihdr_data = struct.pack('>IIBBBBB', width, height, 8, 6, 0, 0, 0)  # 8bit RGBA
    ihdr = write_chunk(b'IHDR', ihdr_data)

    # IDAT chunk (image data)
    raw_data = b''
    for y in range(height):
        raw_data += b'\x00'  # filter byte (none)
        for x in range(width):
            idx = (y * width + x) * 4
            raw_data += bytes(pixels[idx:idx+4])

    compressed = zlib.compress(raw_data)
    idat = write_chunk(b'IDAT', compressed)

    # IEND chunk
    iend = write_chunk(b'IEND', b'')

    return signature + ihdr + idat + iend


def make_circle_gradient_16px(r, g, b, a=255, glow_radius=7):
    """
    创建 16x16 的圆形渐变像素数据
    中心最亮，向外渐变透明
    """
    pixels = []
    center = 7.5
    max_dist = glow_radius
    for y in range(16):
        for x in range(16):
            dx = x - center
            dy = y - center
            dist = (dx * dx + dy * dy) ** 0.5

            if dist <= max_dist:
                # 中心不透明到边缘透明
                alpha_factor = max(0, 1.0 - dist / max_dist)
                alpha_val = int(a * alpha_factor)
                # 中心亮度稍微提升
                brightness = 1.0 - (dist / max_dist) * 0.3
                pixels.extend([
                    min(255, int(r * brightness)),
                    min(255, int(g * brightness)),
                    min(255, int(b * brightness)),
                    alpha_val
                ])
            else:
                pixels.extend([0, 0, 0, 0])  # 完全透明
    return pixels


def make_star_16px():
    """
    创建 16x16 星形像素数据
    中心亮白点 + 四角星芒
    """
    pixels = []
    center = 7.5
    for y in range(16):
        for x in range(16):
            dx = x - center
            dy = y - center
            dist = (dx * dx + dy * dy) ** 0.5

            # 星芒条件：轴上或者对角线上的像素
            is_axis = abs(dx) < 1.0 or abs(dy) < 1.0
            is_diagonal = abs(abs(dx) - abs(dy)) < 1.0
            is_core = dist < 2.0

            angle = abs(dx) if abs(dx) > abs(dy) else abs(dy)

            if is_core:
                alpha = 255
                r, g, b = 255, 255, 240
            elif (is_axis or is_diagonal) and dist < 6:
                alpha = int(max(0, 255 * (1.0 - dist / 6.0)))
                r, g, b = 255, 255, 230
            else:
                alpha = 0
                r, g, b = 0, 0, 0

            pixels.extend([r, g, b, alpha])
    return pixels


def make_snowflake_16px():
    """
    创建 16x16 雪花状像素数据
    六角形冰晶结构，白色半透明
    """
    pixels = []
    center = 7.5
    for y in range(16):
        for x in range(16):
            dx = x - center
            dy = y - center
            dist = (dx * dx + dy * dy) ** 0.5

            if dist < 1.0:
                # 中心核心
                alpha = 200
                r, g, b = 255, 255, 255
            elif dist < 7:
                # 六角形分支
                angle = abs(dx) if abs(dx) > abs(dy) else abs(dy)
                is_vertical = abs(dx) < 0.8
                is_horizontal = abs(dy) < 0.8
                is_diag1 = abs(dx - dy) < 0.8
                is_diag2 = abs(dx + dy - 15) < 0.8

                if (is_vertical or is_horizontal or is_diag1 or is_diag2):
                    alpha = int(max(0, 180 * (1.0 - dist / 7.0)))
                    r, g, b = 255, 255, 255
                else:
                    alpha = 0
                    r, g, b = 0, 0, 0
            else:
                alpha = 0
                r, g, b = 0, 0, 0

            pixels.extend([r, g, b, alpha])
    return pixels


def make_aurora_16px():
    """
    创建 16x16 极光带状像素数据
    水平方向的渐变光带，青/蓝/紫
    """
    pixels = []
    for y in range(16):
        for x in range(16):
            # 水平方向颜色渐变
            t = x / 15.0
            if t < 0.33:
                r_factor = 0.3 + t * 1.2
                g_factor = 0.5 + t * 1.0
                b_factor = 0.8 + t * 0.6
            elif t < 0.66:
                r_factor = 0.5 + (t - 0.33) * 0.6
                g_factor = 0.7 + (t - 0.33) * 0.3
                b_factor = 1.0 - (t - 0.33) * 0.6
            else:
                r_factor = 0.7 + (t - 0.66) * 0.6
                g_factor = 0.8 - (t - 0.66) * 0.5
                b_factor = 0.6 - (t - 0.66) * 0.4

            # 垂直方向渐变 - 中间亮，上下淡
            vy = abs(y - 7.5) / 7.5
            v_factor = max(0, 1.0 - vy * 1.5)

            alpha = int(180 * v_factor)
            r = min(255, int(200 * r_factor))
            g = min(255, int(200 * g_factor))
            b = min(255, int(200 * b_factor))

            pixels.extend([r, g, b, alpha])
    return pixels


def main():
    output_dir = os.path.join(
        os.path.dirname(os.path.abspath(__file__)),
        "PasterDream", "src", "main", "resources",
        "assets", "pasterdream", "textures", "particle"
    )
    os.makedirs(output_dir, exist_ok=True)

    textures = {
        "dream_spore.png": make_circle_gradient_16px(220, 120, 200, 220, 6),  # 粉紫色孢子
        "crystal_snowflake.png": make_snowflake_16px(),  # 白色雪花
        "aurora_glow.png": make_aurora_16px(),  # 极光渐变带
        "stardust.png": make_star_16px(),  # 星芒
    }

    for filename, pixels in textures.items():
        filepath = os.path.join(output_dir, filename)
        png_data = create_png(16, 16, pixels)
        with open(filepath, 'wb') as f:
            f.write(png_data)
        print(f"[OK] 已生成: {filepath} ({len(png_data)} bytes)")


if __name__ == "__main__":
    main()
    print("\n完成! 4 个粒子纹理 PNG 已生成到 textures/particle/ 目录")
    print("提示: 如果需要更精美的纹理，请用图像编辑软件手动替换这些占位图。")
