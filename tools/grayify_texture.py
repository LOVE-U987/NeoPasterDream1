# -*- coding: utf-8 -*-
"""将彩色方块纹理灰化为中性灰纹理（保留明暗细节与 Alpha 通道）。

用途：配合 Minecraft foliage 染色机制（模型 tintindex + BlockColors），
让方块纹理像原版树叶一样随生物群系 foliage_color 变色。
原理：染色为乘法混合，灰色纹理乘任意颜色即可得到该颜色的明暗版本。

可选 --gamma 参数对灰度做幂次提亮（gamma>1 提亮），
让灰纹理更亮，乘 tint 后颜色更鲜丽不显灰暗。

用法:
    python tools/grayify_texture.py <png路径> [<png路径>...] [--gamma 1.35]
"""
import sys

from PIL import Image


def grayify(path: str, gamma: float = 1.0) -> None:
    """把单个 PNG 纹理灰化并可选提亮：R=G=B=亮度(Luma)，保留 Alpha。"""
    img = Image.open(path).convert("RGBA")
    w, h = img.size
    px = img.load()
    for y in range(h):
        for x in range(w):
            r, g, b, a = px[x, y]
            lum = int(0.299 * r + 0.587 * g + 0.114 * b)
            if gamma != 1.0:
                lum = round(255 * (lum / 255) ** (1 / gamma))
            px[x, y] = (lum, lum, lum, a)
    img.save(path)
    print(f"grayified(gamma={gamma}): {path}")


def main() -> None:
    args = sys.argv[1:]
    gamma = 1.0
    if "--gamma" in args:
        idx = args.index("--gamma")
        gamma = float(args[idx + 1])
        del args[idx:idx + 2]
    if not args:
        print(__doc__)
        sys.exit(1)
    for p in args:
        grayify(p, gamma)


if __name__ == "__main__":
    main()
