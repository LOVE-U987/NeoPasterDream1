# -*- coding: utf-8 -*-
"""对比原版树叶纹理与染梦树叶纹理的饱和度，验证"灰色纹理"机制。"""
import io
import statistics
import zipfile

from PIL import Image

JAR = r"C:\Users\97128\.gradle\caches\neoformruntime\artifacts\minecraft_1.21.1_client.jar"
TARGETS = [
    "assets/minecraft/textures/block/oak_leaves.png",
    "assets/minecraft/textures/block/jungle_leaves.png",
    "assets/minecraft/textures/block/spruce_leaves.png",
    "assets/minecraft/textures/block/birch_leaves.png",
]

jar = zipfile.ZipFile(JAR)
for name in TARGETS:
    img = Image.open(io.BytesIO(jar.read(name))).convert("RGB")
    px = list(img.getdata())
    r = [p[0] for p in px]
    g = [p[1] for p in px]
    b = [p[2] for p in px]
    sat = statistics.mean([max(p) - min(p) for p in px])
    fname = name.split("/")[-1]
    print(
        f"{fname:22s} avgRGB=({statistics.mean(r):6.1f},{statistics.mean(g):6.1f},{statistics.mean(b):6.1f}) sat={sat:5.1f}"
    )
jar.close()
