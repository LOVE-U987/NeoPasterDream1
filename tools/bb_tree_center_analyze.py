# -*- coding: utf-8 -*-
"""
分析 Better Biomes 树结构的树干中心偏移 —— 计算每棵树 log 方块的包围盒中心 vs 结构 size 中心
用法: python bb_tree_center_analyze.py
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
    palette = root["palette"]

    # 统计 log 方块（dyedream_log）
    log_idx = [i for i, pal in enumerate(palette) if isinstance(pal, dict) and pal.get("Name") == "pasterdream:dyedream_log"]
    log_idx = set(log_idx)
    logs = [b for b in blocks if b["state"] in log_idx]
    if not logs:
        # 可能 state 字段类型不同，直接按 Name 找
        print("%-16s 无 log 方块" % name)
        return
    xs = [b["pos"][0] for b in logs]
    ys = [b["pos"][1] for b in logs]
    zs = [b["pos"][2] for b in logs]
    # log 包围盒中心（向下取整）
    min_x, max_x = min(xs), max(xs)
    min_y, max_y = min(ys), max(ys)
    min_z, max_z = min(zs), max(zs)
    trunk_cx = (min_x + max_x) / 2.0
    trunk_cy = (min_y + max_y) / 2.0
    trunk_cz = (min_z + max_z) / 2.0
    # size 中心
    size_cx = (size[0] - 1) / 2.0
    size_cy = (size[1] - 1) / 2.0
    size_cz = (size[2] - 1) / 2.0
    dx = round(size_cx - trunk_cx)
    dy = round(size_cy - trunk_cy)
    dz = round(size_cz - trunk_cz)
    print("%-16s size=%s log包围盒=[%d..%d,%d..%d,%d..%d] log中心=(%s,%s,%s) size中心=(%s,%s,%s) 平移=(%d,%d,%d)"
          % (name, size, min_x, max_x, min_y, max_y, min_z, max_z,
             trunk_cx, trunk_cy, trunk_cz, size_cx, size_cy, size_cz, dx, dy, dz))


def main():
    names = ["bb_tallbirch", "bb_blossom", "bb_aspen_big", "bb_aspen_mid", "bb_aspen_small", "bb_poplar"]
    print("=" * 100)
    print("树结构树干中心偏移分析（平移量 = size中心 - log包围盒中心，四舍五入）")
    print("=" * 100)
    for n in names:
        analyze(n)


if __name__ == "__main__":
    main()
