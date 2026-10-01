# -*- coding: utf-8 -*-
"""结构 NBT 检查工具 —— 解析指定结构的 palette / blocks / block_entities。

用法:
    python tools/inspect_structure_nbt.py <结构NBT路径> [关注方块ID]

参数:
    <结构NBT路径>   必填。结构 NBT 文件路径（支持 gzip 压缩），
                    例如 PasterDream/src/main/resources/data/pasterdream/structure/hot_air_balloon_0.nbt
    [关注方块ID]    可选。需要重点检查的方块 ID（默认 twilight_lantern），
                    会统计其在 palette 中的索引、blocks 中的出现次数，以及是否带 NBT 数据。

说明:
    本脚本原先将路径写死在 NBT_PATH，传入命令行参数不生效，容易误读其它结构
    （曾有排查被误导）。现改为路径必填，缺参直接打印用法退出。
"""
import gzip
import io
import struct
import sys
from pathlib import Path

# 未指定关注方块时的默认值
DEFAULT_FOCUS_BLOCK = "twilight_lantern"

# palette 列表最多打印多少条，避免大结构刷屏
MAX_PALETTE_PRINT = 40


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
            # 空列表的 elem_type 为 0，此时不读取元素
            if elem_type == 0 or n <= 0:
                return []
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


def parse_root(path: Path):
    """读取并解析结构 NBT，返回根 Compound。"""
    raw = path.read_bytes()
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
    return reader.read_payload(root_tag)


def describe(root: dict, focus_block: str):
    """打印结构概览与关注方块的检查结果。"""
    print("\n顶层键:", list(root.keys()))
    print("size:", root.get("size"))

    palette = root.get("palette", [])
    print(f"\npalette 方块数: {len(palette)}")
    for i, p in enumerate(palette[:MAX_PALETTE_PRINT]):
        name = p.get("Name", "?")
        props = p.get("Properties", {})
        mark = " <<<" if focus_block in str(name) else ""
        print(f"  [{i}] {name} {props}{mark}")
    if len(palette) > MAX_PALETTE_PRINT:
        print(f"  ...（其余 {len(palette) - MAX_PALETTE_PRINT} 条省略）")

    blocks = root.get("blocks", [])
    print(f"\nblocks 数: {len(blocks)}")

    focus_indexes = [i for i, p in enumerate(palette) if focus_block in str(p.get("Name", ""))]
    print(f"关注方块 '{focus_block}' 的 palette 索引: {focus_indexes}")

    be_list = root.get("block_entities", [])
    print(f"\nblock_entities 数: {len(be_list)}")
    for be in be_list:
        if focus_block.split(":")[-1] in str(be.get("id", "")):
            print("  关注方块 BE:", be)

    # blocks 中带自定义 nbt 的关注方块条目
    if focus_indexes:
        with_nbt = [b for b in blocks
                    if "nbt" in b and b.get("state", -1) in focus_indexes]
        print(f"\nblocks 中带 nbt 的关注方块条目数: {len(with_nbt)}")
        for b in with_nbt[:5]:
            print("  pos:", b["pos"], "state:", b["state"], "nbt:", b["nbt"])

        focus_blocks = [b for b in blocks if b.get("state", -1) in focus_indexes]
        print(f"\nblocks 中关注方块出现: {len(focus_blocks)} 次")
    else:
        print(f"\n（本结构未使用关注方块 '{focus_block}'，跳过专项统计）")

    print(f"block_entities 全部 id: {[be.get('id') for be in be_list][:20]}")


def main(argv):
    if len(argv) < 2:
        print(__doc__)
        print("错误: 缺少结构 NBT 路径参数（本脚本不再使用写死的默认路径）")
        return 1

    path = Path(argv[1])
    if not path.is_file():
        print(f"错误: 文件不存在 -> {path}")
        return 1

    focus_block = argv[2] if len(argv) > 2 else DEFAULT_FOCUS_BLOCK
    print(f"结构文件: {path}\n关注方块: {focus_block}\n")

    root = parse_root(path)
    describe(root, focus_block)
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv))
