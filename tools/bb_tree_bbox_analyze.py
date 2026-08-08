# -*- coding: utf-8 -*-
"""
分析 Better Biomes 树结构整体内容（非空气方块）的中心偏移
用法: python bb_tree_bbox_analyze.py
"""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import bb_tree_import as bt


def analyze(name):
    p = os.path.join(bt.DST_BASE, name + ".nbt")
    root, gz = bt.read_nbt_file(p)
    size = root["size"]
    blocks = root["blocks"]

    xs = [b["pos"][0] for b in blocks]
    ys = [b["pos"][1] for b in blocks]
    zs = [b["pos"][2] for b in blocks]
    min_x, max_x = min(xs), max(xs)
    min_y, max_y = min(ys), max(ys)
    min_z, max_z = min(zs), max(zs)
    cx = (min_x + max_x) / 2.0
    cy = (min_y + max_y) / 2.0
    cz = (min_z + max_z) / 2.0
    scx = (size[0] - 1) / 2.0
    scy = (size[1] - 1) / 2.0
    scz = (size[2] - 1) / 2.0
    dx = round(scx - cx)
    dy = round(scy - cy)
    dz = round(scz - cz)
    print("%-16s size=%s 内容包围盒=[%d..%d,%d..%d,%d..%d] 内容中心=(%s,%s,%s) size中心=(%s,%s,%s) 平移=(%d,%d,%d)"
          % (name, size, min_x, max_x, min_y, max_y, min_z, max_z,
             cx, cy, cz, scx, scy, scz, dx, dy, dz))


def main():
    names = ["bb_tallbirch", "bb_blossom", "bb_aspen_big", "bb_aspen_mid", "bb_aspen_small", "bb_poplar"]
    print("=" * 100)
    print("树结构整体内容中心偏移分析（平移量 = size中心 - 内容包围盒中心）")
    print("=" * 100)
    for n in names:
        analyze(n)


if __name__ == "__main__":
    main()
