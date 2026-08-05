# -*- coding: utf-8 -*-
"""解析 shadow_world_door.nbt，检查 twilight_lantern 是否有 BlockEntity 数据。
用法: python tools/inspect_structure_nbt.py
"""
import gzip
import io
import struct
import sys
from pathlib import Path

NBT_PATH = Path(r"c:\Users\97128\Documents\GitHub\NeoPasterDream1\PasterDream\src\main\resources\data\pasterdream\structure\shadow_world_door.nbt")


class NBTReader:
    def __init__(self, buf):
        self.buf = buf

    def read(self, fmt):
        size = struct.calcsize(fmt)
        data = self.buf.read(size)
        if len(data) < size:
            raise EOFError("NBT 截断")
        return struct.unpack(fmt, data)[0]

    def read_tag_name(self):
        length = self.read(">H")
        return self.buf.read(length).decode("utf-8", errors="replace")

    def read_payload(self, tag_id):
        if tag_id == 0:
            return None
        if tag_id == 1:
            return self.read(">b")
        if tag_id == 2:
            return self.read(">h")
        if tag_id == 3:
            return self.read(">i")
        if tag_id == 4:
            return self.read(">q")
        if tag_id == 5:
            return self.read(">f")
        if tag_id == 6:
            return self.read(">d")
        if tag_id == 7:
            n = self.read(">i")
            return self.buf.read(n)
        if tag_id == 8:
            length = self.read(">H")
            return self.buf.read(length).decode("utf-8", errors="replace")
        if tag_id == 9:
            elem_type = self.read(">b")
            n = self.read(">i")
            return [self.read_payload(elem_type) for _ in range(n)]
        if tag_id == 10:
            return self.read_compound()
        if tag_id == 11:
            n = self.read(">i")
            return list(struct.unpack(f">{n}i", self.buf.read(4 * n)))
        if tag_id == 12:
            n = self.read(">i")
            return list(struct.unpack(f">{n}q", self.buf.read(8 * n)))
        raise ValueError(f"未知 TAG id: {tag_id}")

    def read_compound(self):
        tag = {}
        while True:
            tag_id = self.read(">b")
            if tag_id == 0:
                break
            name = self.read_tag_name()
            tag[name] = self.read_payload(tag_id)
        return tag


def main():
    raw = NBT_PATH.read_bytes()
    if raw[:2] == b"\x1f\x8b":
        buf = io.BytesIO(gzip.decompress(raw))
        print("检测到 gzip 压缩，已解压")
    else:
        buf = io.BytesIO(raw)
        print("无压缩")
    reader = NBTReader(buf)
    root_tag = reader.read(">b")
    print(f"根 TAG id: {root_tag} (10=Compound)")
    root_name = reader.read_tag_name()
    print(f"根 TAG 名: {root_name}")
    root = reader.read_payload(root_tag)

    print("\n顶层键:", list(root.keys()))
    size = root.get("size")
    print("size:", size)

    palette = root.get("palette", [])
    print(f"\npalette 方块数: {len(palette)}")
    for i, p in enumerate(palette):
        name = p.get("Name", "?")
        props = p.get("Properties", {})
        mark = " <<<" if "twilight_lantern" in str(name) or "shadow_bed" in str(name) else ""
        print(f"  [{i}] {name} {props}{mark}")

    blocks = root.get("blocks", [])
    print(f"\nblocks 数: {len(blocks)}")
    # 找 twilight_lantern 在 palette 中的索引
    lantern_idx = [i for i, p in enumerate(palette)
                   if "twilight_lantern" in str(p.get("Name", ""))]
    print("twilight_lantern palette 索引:", lantern_idx)

    be_list = root.get("block_entities", [])
    print(f"\nblock_entities 数: {len(be_list)}")
    for be in be_list:
        be_id = be.get("id", "?")
        if "twilight" in str(be_id) or "lantern" in str(be_id):
            print("  lantern BE:", be)

    # blocks 中有 nbt 的条目（带自定义数据）
    with_nbt = [b for b in blocks if "nbt" in b and "twilight_lantern" in str(palette[b["state"]].get("Name", ""))]
    print(f"\nblocks 中带 nbt 的 twilight_lantern 条目数: {len(with_nbt)}")
    for b in with_nbt[:5]:
        print("  pos:", b["pos"], "state:", b["state"], "nbt:", b["nbt"])

    # 统计：palette 里 twilight_lantern 出现次数 vs block_entities 里 lantern 出现次数
    lantern_blocks = [b for b in blocks if "twilight_lantern" in str(palette[b["state"]].get("Name", ""))]
    print(f"\nblocks 中 twilight_lantern 方块出现: {len(lantern_blocks)} 次")
    print(f"block_entities 中 lantern: {len(be_list)} 个 (全部: {[be.get('id') for be in be_list][:20]})")


if __name__ == "__main__":
    main()
