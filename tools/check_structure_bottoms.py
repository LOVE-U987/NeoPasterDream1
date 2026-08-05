# -*- coding: utf-8 -*-
"""检查染梦结构 NBT 底部设计意图"""
import gzip
import io
import struct
from collections import Counter
from pathlib import Path

BASE = Path(r"c:\Users\97128\Documents\GitHub\NeoPasterDream1\PasterDream\src\main\resources\data\pasterdream\structure")


def read_nbt(path):
    raw = path.read_bytes()
    buf = io.BytesIO(gzip.decompress(raw))
    buf.read(1)
    n = struct.unpack(">H", buf.read(2))[0]
    buf.read(n)

    def read_compound():
        tag = {}
        while True:
            tid = struct.unpack(">b", buf.read(1))[0]
            if tid == 0:
                break
            ln = struct.unpack(">H", buf.read(2))[0]
            name = buf.read(ln).decode("utf-8")
            tag[name] = read_payload(tid)
        return tag

    def read_payload(tid):
        if tid == 1:
            return struct.unpack(">b", buf.read(1))[0]
        if tid == 2:
            return struct.unpack(">h", buf.read(2))[0]
        if tid == 3:
            return struct.unpack(">i", buf.read(4))[0]
        if tid == 4:
            return struct.unpack(">q", buf.read(8))[0]
        if tid == 5:
            return struct.unpack(">f", buf.read(4))[0]
        if tid == 6:
            return struct.unpack(">d", buf.read(8))[0]
        if tid == 7:
            n = struct.unpack(">i", buf.read(4))[0]
            return buf.read(n)
        if tid == 8:
            n = struct.unpack(">H", buf.read(2))[0]
            return buf.read(n).decode("utf-8")
        if tid == 9:
            et = struct.unpack(">b", buf.read(1))[0]
            n = struct.unpack(">i", buf.read(4))[0]
            return [read_payload(et) for _ in range(n)]
        if tid == 10:
            return read_compound()
        if tid == 11:
            n = struct.unpack(">i", buf.read(4))[0]
            return list(struct.unpack(">" + "i" * n, buf.read(4 * n)))
        if tid == 12:
            n = struct.unpack(">i", buf.read(4))[0]
            return list(struct.unpack(">" + "q" * n, buf.read(8 * n)))
    return read_compound()


# 查找 worldtree 实际文件名
for p in BASE.glob("*worldtree*"):
    print("找到:", p.name)

NAMES = [
    "dyedream_laboratory_0", "dream_wishingtree_0", "dream_church_0",
    "dyedream_tavern", "dyedream_pavilion_0", "dyedream_campsite_0",
    "picnic_basket_structure",
]
for name in NAMES:
    p = BASE / (name + ".nbt")
    if not p.exists():
        print(f"== {name}: NOT FOUND ==")
        continue
    tag = read_nbt(p)
    size = tag.get("size", [0, 0, 0])
    print(f"== {name} == size={size}")
    pal = tag.get("palette", [])
    blocks = tag.get("blocks", [])
    for layer in range(min(6, size[1])):
        cnt = Counter()
        for b in blocks:
            if b["pos"][1] == layer:
                cnt[pal[b["state"]].get("Name", "?")] += 1
        print(f"  Y={layer}: {dict(cnt)}")
