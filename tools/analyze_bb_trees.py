# -*- coding: utf-8 -*-
"""
解析 Better Biomes 数据包的树木结构 NBT 文件
用法: python analyze_bb_trees.py
输出: 每棵树的尺寸、palette(方块列表)、各方块使用次数
"""
import gzip
import os
import struct
from collections import Counter

BASE = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "示例", "data", "trees", "structures")


def read_root(path):
    """极简 NBT 解析器: 返回根 compound dict (支持 1.16.2+ 结构格式)"""
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
            if tt == 8:  # string
                sl = struct.unpack(">H", data[pos:pos + 2])[0]
                pos += 2
                d[name] = data[pos:pos + sl].decode("utf-8", errors="replace")
                pos += sl
            elif tt == 3:  # int
                d[name] = struct.unpack(">i", data[pos:pos + 4])[0]
                pos += 4
            elif tt == 1:  # byte
                d[name] = struct.unpack(">b", data[pos:pos + 1])[0]
                pos += 1
            elif tt == 9:  # list
                et = data[pos]
                pos += 1
                l = struct.unpack(">i", data[pos:pos + 4])[0]
                pos += 4
                if et == 0:  # 空列表
                    d[name] = []
                elif et == 10:  # compound list (palette / blocks)
                    d[name] = [parse_compound() for _ in range(l)]
                elif et == 3:  # int list
                    vals = []
                    for _ in range(l):
                        vals.append(struct.unpack(">i", data[pos:pos + 4])[0])
                        pos += 4
                    d[name] = vals
                elif et == 8:  # string list
                    vals = []
                    for _ in range(l):
                        sl2 = struct.unpack(">H", data[pos:pos + 2])[0]
                        pos += 2
                        vals.append(data[pos:pos + sl2].decode("utf-8", errors="replace"))
                        pos += sl2
                    d[name] = vals
                else:
                    d[name] = "list<" + str(et) + ">"
                    break
            elif tt == 10:  # compound
                d[name] = parse_compound()
            elif tt == 2:  # short
                d[name] = struct.unpack(">h", data[pos:pos + 2])[0]
                pos += 2
            elif tt == 4:  # long
                d[name] = struct.unpack(">q", data[pos:pos + 8])[0]
                pos += 8
            elif tt == 7:  # byte array
                ll = struct.unpack(">i", data[pos:pos + 4])[0]
                pos += 4 + ll
                d[name] = "bytes[%d]" % ll
            else:
                d[name] = "unknown<" + str(tt) + ">"
                break
        return d

    # 根 tag 头
    root_type = data[pos]
    pos += 1
    nl = struct.unpack(">H", data[pos:pos + 2])[0]
    pos += 2 + nl
    if root_type == 10:
        return parse_compound()
    return None


def block_label(blk, palette_names):
    """返回方块的显示名: 优先 palette 索引, 否则字符串"""
    if isinstance(blk, dict):
        st = blk.get("state")
        if isinstance(st, int) and st < len(palette_names):
            return palette_names[st]
        return str(st)
    return "?"


def main():
    files = [
        "aspen/small.nbt", "aspen/mid.nbt", "aspen/big.nbt",
        "blossom.nbt", "tallbirch.nbt", "smallpalm1.nbt",
        "plaintree.nbt", "poplar.nbt", "snowtree.nbt",
        "cherrybush.nbt", "bush.nbt", "prismarinespike.nbt",
        "conifers/big1-0.nbt",
    ]
    print("=" * 100)
    print("Better Biomes 树木结构 NBT 分析")
    print("=" * 100)
    for rel in files:
        p = os.path.join(BASE, rel)
        if not os.path.exists(p):
            print("%-24s MISSING" % rel)
            continue
        try:
            root = read_root(p)
        except Exception as e:
            print("%-24s ERR %s" % (rel, e))
            continue
        if not isinstance(root, dict):
            print("%-24s NOT A STRUCTURE" % rel)
            continue
        size = root.get("size", "?")
        blocks = root.get("blocks", [])
        palette = root.get("palette", [])
        names = [b.get("Name", "?") for b in palette] if isinstance(palette, list) else []
        cnt = Counter()
        for blk in blocks:
            if isinstance(blk, dict):
                cnt[block_label(blk, names)] += 1
        usage = ", ".join("%s x%d" % (k, v) for k, v in cnt.most_common())
        print("%-24s size=%s blocks=%d palette=%s" % (rel, size, len(blocks), names))
        print("    使用: %s" % usage)


if __name__ == "__main__":
    main()
