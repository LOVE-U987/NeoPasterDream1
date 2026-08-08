# -*- coding: utf-8 -*-
"""
逐层分析 Better Biomes 树木结构 NBT —— 输出每 y 层的方块布局，用于复刻树形
用法: python analyze_bb_tree_layers.py <nbt相对路径> [--show]
例:   python analyze_bb_tree_layers.py aspen/small.nbt
      python analyze_bb_tree_layers.py blossom.nbt --show
"""
import gzip
import os
import struct
import sys
from collections import Counter, defaultdict

BASE = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "示例", "data", "trees", "structures")


def read_root(path):
    with open(path, "rb") as f:
        data = f.read()
    try:
        data = gzip.decompress(data)
    except Exception:
        pass
    pos = 0

    def parse_compound():
        nonlocal pos
        d = {}
        while pos < len(data):
            tt = data[pos]
            if tt == 0:
                pos += 1
                break
            nll = struct.unpack(">H", data[pos + 1:pos + 3])[0]
            name = data[pos + 3:pos + 3 + nll].decode("utf-8", errors="replace")
            pos += 3 + nll
            if tt == 8:
                sl = struct.unpack(">H", data[pos:pos + 2])[0]
                pos += 2
                d[name] = data[pos:pos + sl].decode("utf-8", errors="replace")
                pos += sl
            elif tt == 3:
                d[name] = struct.unpack(">i", data[pos:pos + 4])[0]
                pos += 4
            elif tt == 1:
                d[name] = struct.unpack(">b", data[pos:pos + 1])[0]
                pos += 1
            elif tt == 9:
                et = data[pos]
                pos += 1
                l = struct.unpack(">i", data[pos:pos + 4])[0]
                pos += 4
                if et == 0:
                    d[name] = []
                elif et == 10:
                    d[name] = [parse_compound() for _ in range(l)]
                elif et == 3:
                    vals = []
                    for _ in range(l):
                        vals.append(struct.unpack(">i", data[pos:pos + 4])[0])
                        pos += 4
                    d[name] = vals
                elif et == 8:
                    vals = []
                    for _ in range(l):
                        sl2 = struct.unpack(">H", data[pos:pos + 2])[0]
                        pos += 2
                        vals.append(data[pos:pos + sl2].decode())
                        pos += sl2
                    d[name] = vals
                else:
                    d[name] = "list<" + str(et) + ">"
                    break
            elif tt == 10:
                d[name] = parse_compound()
            elif tt == 2:
                d[name] = struct.unpack(">h", data[pos:pos + 2])[0]
                pos += 2
            elif tt == 7:
                ll = struct.unpack(">i", data[pos:pos + 4])[0]
                pos += 4 + ll
                d[name] = "bytes[%d]" % ll
            else:
                d[name] = "unknown<" + str(tt) + ">"
                break
        return d

    root_type = data[pos]
    pos += 1
    nl = struct.unpack(">H", data[pos:pos + 2])[0]
    pos += 2 + nl
    if root_type == 10:
        return parse_compound()
    return None


def layer_label(blk, names):
    st = blk.get("state")
    if isinstance(st, int) and st < len(names):
        n = names[st]
        return "LOG" if "log" in n or "wood" in n else ("LEAF" if "leaves" in n else n.split(":")[-1].upper())
    return "?"


def main():
    rel = sys.argv[1] if len(sys.argv) > 1 else "aspen/small.nbt"
    show = "--show" in sys.argv
    p = os.path.join(BASE, rel)
    root = read_root(p)
    size = root["size"]
    blocks = root["blocks"]
    names = [b.get("Name", "?") for b in root.get("palette", [])]

    # 按 y 分组
    by_y = defaultdict(list)
    for blk in blocks:
        y = blk["pos"][1]
        by_y[y].append(blk)

    print("=" * 70)
    print("%s  size=%s  blocks=%d" % (rel, size, len(blocks)))
    print("-" * 70)
    for y in sorted(by_y.keys()):
        layer = by_y[y]
        cnt = Counter(layer_label(b, names) for b in layer)
        desc = "  ".join("%s:%d" % (k, v) for k, v in sorted(cnt.items()))
        print("y=%2d  %s" % (y, desc))
        if show:
            # ASCII 图：X-Z 平面
            xs = [b["pos"][0] for b in layer]
            zs = [b["pos"][2] for b in layer]
            if xs:
                x0, x1 = min(xs), max(xs)
                z0, z1 = min(zs), max(zs)
                grid = {}
                for b in layer:
                    grid[(b["pos"][0], b["pos"][2])] = layer_label(b, names)
                for z in range(z0, z1 + 1):
                    row = "".join(grid.get((x, z), ".")[0] for x in range(x0, x1 + 1))
                    print("      " + row)
    # 树干统计：每 y 的 LOG 数量（判断是单柱还是 3x3）
    print("-" * 70)
    for y in sorted(by_y.keys()):
        logs = [b for b in by_y[y] if layer_label(b, names) == "LOG"]
        if logs:
            xs = sorted(set(b["pos"][0] for b in logs))
            zs = sorted(set(b["pos"][2] for b in logs))
            print("y=%2d LOG: x=%s z=%s count=%d" % (y, xs, zs, len(logs)))


if __name__ == "__main__":
    main()
