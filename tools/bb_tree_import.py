# -*- coding: utf-8 -*-
"""
Better Biomes 树木结构 NBT 提取工具 —— 原封不动提取 + 方块名替换
用法: python bb_tree_import.py <源nbt相对路径> <目标文件名> [--keep-map]
例:   python bb_tree_import.py aspen/big.nbt bb_aspen_big.nbt
      python bb_tree_import.py tallbirch.nbt bb_tallbirch.nbt
"""
import gzip
import os
import struct
import sys

# ---------------------------------------------------------------- NBT 常量
TAG_END = 0
TAG_BYTE = 1
TAG_SHORT = 2
TAG_INT = 3
TAG_LONG = 4
TAG_FLOAT = 5
TAG_DOUBLE = 6
TAG_BYTE_ARRAY = 7
TAG_STRING = 8
TAG_LIST = 9
TAG_COMPOUND = 10
TAG_INT_ARRAY = 11
TAG_LONG_ARRAY = 12

# ---------------------------------------------------------------- 读取
class Reader:
    def __init__(self, data):
        self.data = data
        self.pos = 0

    def read_byte(self):
        v = self.data[self.pos]
        self.pos += 1
        return v

    def read_short(self):
        v = struct.unpack(">h", self.data[self.pos:self.pos + 2])[0]
        self.pos += 2
        return v

    def read_int(self):
        v = struct.unpack(">i", self.data[self.pos:self.pos + 4])[0]
        self.pos += 4
        return v

    def read_long(self):
        v = struct.unpack(">q", self.data[self.pos:self.pos + 8])[0]
        self.pos += 8
        return v

    def read_float(self):
        v = struct.unpack(">f", self.data[self.pos:self.pos + 4])[0]
        self.pos += 4
        return v

    def read_double(self):
        v = struct.unpack(">d", self.data[self.pos:self.pos + 8])[0]
        self.pos += 8
        return v

    def read_string(self):
        l = self.read_short()
        v = self.data[self.pos:self.pos + l].decode("utf-8")
        self.pos += l
        return v

    def read_payload(self, t):
        if t == TAG_BYTE:
            return self.read_byte()
        if t == TAG_SHORT:
            return self.read_short()
        if t == TAG_INT:
            return self.read_int()
        if t == TAG_LONG:
            return self.read_long()
        if t == TAG_FLOAT:
            return self.read_float()
        if t == TAG_DOUBLE:
            return self.read_double()
        if t == TAG_STRING:
            return self.read_string()
        if t == TAG_BYTE_ARRAY:
            l = self.read_int()
            v = self.data[self.pos:self.pos + l]
            self.pos += l
            return v
        if t == TAG_LIST:
            et = self.read_byte()
            l = self.read_int()
            return [self.read_payload(et) for _ in range(l)]
        if t == TAG_COMPOUND:
            return self.read_compound()
        if t == TAG_INT_ARRAY:
            l = self.read_int()
            v = struct.unpack(">%di" % l, self.data[self.pos:self.pos + 4 * l])
            self.pos += 4 * l
            return list(v)
        if t == TAG_LONG_ARRAY:
            l = self.read_int()
            v = struct.unpack(">%dq" % l, self.data[self.pos:self.pos + 8 * l])
            self.pos += 8 * l
            return list(v)
        raise ValueError("未知 tag 类型: %d @%d" % (t, self.pos))

    def read_compound(self):
        d = {}
        while True:
            t = self.read_byte()
            if t == TAG_END:
                break
            name = self.read_string()
            d[name] = self.read_payload(t)
        return d


def read_nbt_file(path):
    with open(path, "rb") as f:
        raw = f.read()
    try:
        data = gzip.decompress(raw)
        gzipped = True
    except Exception:
        data = raw
        gzipped = False
    r = Reader(data)
    root_t = r.read_byte()
    root_name = r.read_string()
    root = r.read_payload(root_t)
    return root, gzipped


# ---------------------------------------------------------------- 写入
def write_payload(t, v):
    if t == TAG_BYTE:
        return struct.pack(">b", v)
    if t == TAG_SHORT:
        return struct.pack(">h", v)
    if t == TAG_INT:
        return struct.pack(">i", v)
    if t == TAG_LONG:
        return struct.pack(">q", v)
    if t == TAG_FLOAT:
        return struct.pack(">f", v)
    if t == TAG_DOUBLE:
        return struct.pack(">d", v)
    if t == TAG_STRING:
        b = v.encode("utf-8")
        return struct.pack(">H", len(b)) + b
    if t == TAG_BYTE_ARRAY:
        return struct.pack(">i", len(v)) + bytes(v)
    if t == TAG_LIST:
        if not v:
            # 空列表：元素类型 0（结束）
            return struct.pack(">bi", TAG_END, 0)
        # 推断元素类型（compound 优先，int 其次，string 第三）
        first = v[0]
        if isinstance(first, dict):
            et = TAG_COMPOUND
        elif isinstance(first, str):
            et = TAG_STRING
        elif isinstance(first, bool) or isinstance(first, int):
            et = TAG_INT
        else:
            raise ValueError("无法推断 list 元素类型: %r" % first)
        out = struct.pack(">bi", et, len(v))
        for item in v:
            out += write_payload(et, item)
        return out
    if t == TAG_COMPOUND:
        out = b""
        for k, val in v.items():
            kt = _infer_type(val)
            kb = k.encode("utf-8")
            out += struct.pack(">bH", kt, len(kb)) + kb
            out += write_payload(kt, val)
        return out + b"\x00"
    if t == TAG_INT_ARRAY:
        return struct.pack(">i", len(v)) + struct.pack(">%di" % len(v), *v)
    if t == TAG_LONG_ARRAY:
        return struct.pack(">i", len(v)) + struct.pack(">%dq" % len(v), *v)
    raise ValueError("未知 tag 类型: %d" % t)


def _infer_type(v):
    if isinstance(v, dict):
        return TAG_COMPOUND
    if isinstance(v, str):
        return TAG_STRING
    if isinstance(v, bool):
        return TAG_BYTE
    if isinstance(v, int):
        return TAG_INT
    if isinstance(v, float):
        return TAG_DOUBLE
    if isinstance(v, (bytes, bytearray)):
        return TAG_BYTE_ARRAY
    if isinstance(v, list):
        if v and isinstance(v[0], dict):
            return TAG_LIST
        if v and isinstance(v[0], str):
            return TAG_LIST
        if v and isinstance(v[0], bool):
            return TAG_BYTE_ARRAY if False else TAG_LIST
        if v and isinstance(v[0], int):
            return TAG_LIST
        return TAG_LIST
    raise ValueError("无法推断类型: %r" % v)


def write_nbt_file(root, path, gzipped=True):
    out = b"\x0a\x00\x00"  # TAG_Compound + 空名
    for k, val in root.items():
        kt = _infer_type(val)
        kb = k.encode("utf-8")
        out += struct.pack(">bH", kt, len(kb)) + kb
        out += write_payload(kt, val)
    out += b"\x00"
    if gzipped:
        out = gzip.compress(out)
    with open(path, "wb") as f:
        f.write(out)


# ---------------------------------------------------------------- 方块映射
# Better Biomes 原版方块 → 染梦方块（树材质替换）
BLOCK_MAP = {
    "minecraft:birch_leaves": "pasterdream:dyedream_leaves",
    "minecraft:oak_leaves": "pasterdream:dyedream_leaves",
    "minecraft:dark_oak_leaves": "pasterdream:dyedream_leaves",
    "minecraft:spruce_leaves": "pasterdream:dyedream_leaves",
    "minecraft:jungle_leaves": "pasterdream:dyedream_leaves",
    "minecraft:acacia_leaves": "pasterdream:dyedream_leaves",
    "minecraft:birch_wood": "pasterdream:dyedream_log",
    "minecraft:oak_wood": "pasterdream:dyedream_log",
    "minecraft:dark_oak_wood": "pasterdream:dyedream_log",
    "minecraft:spruce_wood": "pasterdream:dyedream_log",
    "minecraft:jungle_wood": "pasterdream:dyedream_log",
    "minecraft:acacia_wood": "pasterdream:dyedream_log",
    "minecraft:birch_log": "pasterdream:dyedream_log",
    "minecraft:oak_log": "pasterdream:dyedream_log",
    "minecraft:dark_oak_log": "pasterdream:dyedream_log",
    "minecraft:spruce_log": "pasterdream:dyedream_log",
    "minecraft:jungle_log": "pasterdream:dyedream_log",
    "minecraft:acacia_log": "pasterdream:dyedream_log",
    "minecraft:mangrove_log": "pasterdream:dyedream_log",
    "minecraft:mangrove_wood": "pasterdream:dyedream_log",
}

# ---------------------------------------------------------------- 主流程
SRC_BASE = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "示例", "data", "trees", "structures")
DST_BASE = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "PasterDream", "src", "main", "resources", "data", "pasterdream", "structure")


def main():
    if len(sys.argv) < 3:
        print(__doc__)
        sys.exit(1)
    rel = sys.argv[1]
    dst_name = sys.argv[2]
    src = os.path.join(SRC_BASE, rel)
    if not os.path.exists(src):
        print("源文件不存在: %s" % src)
        sys.exit(1)
    os.makedirs(DST_BASE, exist_ok=True)
    dst = os.path.join(DST_BASE, dst_name)

    root, gzipped = read_nbt_file(src)
    if "palette" not in root:
        print("不是结构 NBT（无 palette）: %s" % rel)
        sys.exit(1)

    replaced = set()
    for pal in root["palette"]:
        if isinstance(pal, dict) and "Name" in pal:
            old = pal["Name"]
            if old in BLOCK_MAP:
                pal["Name"] = BLOCK_MAP[old]
                replaced.add("%s -> %s" % (old, BLOCK_MAP[old]))

    write_nbt_file(root, dst, gzipped=gzipped)
    size = root.get("size", "?")
    blocks = len(root.get("blocks", []))
    print("提取完成: %s -> %s" % (rel, dst))
    print("  size=%s blocks=%d" % (size, blocks))
    if replaced:
        print("  方块替换:")
        for r in sorted(replaced):
            print("    " + r)
    else:
        print("  无方块替换（未命中映射表）")
    print("  palette 方块: " + ", ".join(sorted(set(p.get("Name", "?") for p in root["palette"] if isinstance(p, dict)))))


if __name__ == "__main__":
    main()
