"""
复制云朵方块资源文件（纹理/模型/blockstate）从 libs 到主项目
"""
import os
import shutil
import glob

LIBS = r"c:\Users\97128\Documents\GitHub\NeoPasterDream1\libs\FixPasterDream-main\src\main\resources\assets\pasterdream"
MAIN = r"c:\Users\97128\Documents\GitHub\NeoPasterDream1\src\main\resources\assets\pasterdream"

# 需要复制的文件
FILES = [
    ("textures/block/fluffy_cloud.png", "textures/block/fluffy_cloud.png"),
    ("textures/block/storm_cloud.png", "textures/block/storm_cloud.png"),
    ("textures/block/dense_cloud.png", "textures/block/dense_cloud.png"),
    ("models/block/cloud.json", "models/block/cloud.json"),
    ("models/block/dark_cloud.json", "models/block/dark_cloud.json"),
    ("models/block/thick_cloud.json", "models/block/thick_cloud.json"),
    ("models/item/cloud.json", "models/item/cloud.json"),
    ("models/item/dark_cloud.json", "models/item/dark_cloud.json"),
    ("models/item/thick_cloud.json", "models/item/thick_cloud.json"),
    ("blockstates/cloud.json", "blockstates/cloud.json"),
    ("blockstates/dark_cloud.json", "blockstates/dark_cloud.json"),
    ("blockstates/thick_cloud.json", "blockstates/thick_cloud.json"),
]

copied = 0
skipped = 0
for rel_path in [f[0] for f in FILES]:
    src = os.path.join(LIBS, rel_path)
    dst = os.path.join(MAIN, rel_path)
    os.makedirs(os.path.dirname(dst), exist_ok=True)
    if os.path.exists(src):
        if not os.path.exists(dst):
            shutil.copy2(src, dst)
            print(f"  [COPY] {rel_path}")
            copied += 1
        else:
            print(f"  [SKIP] {rel_path} (已存在)")
            skipped += 1
    else:
        print(f"  [MISS] {rel_path}")

print(f"\n复制了 {copied} 个文件，跳过了 {skipped} 个")