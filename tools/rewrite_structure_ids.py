# -*- coding: utf-8 -*-
"""重写结构 NBT 文件中的注册 ID（通用化自 rewrite_structure_spawnblock_id.py）。

将 data/**/structure/*.nbt（gzip 压缩）中的旧注册 ID 字符串改写为新 ID，
原地修正 NBT TAG_String 的 2 字节大端长度前缀。新 ID 必须短于旧 ID
（去后缀/缩写场景）；加长场景会破坏切片左移假设，脚本会拒绝执行。

用法:
    python tools/rewrite_structure_ids.py --dry-run pasterdream:shadow_npc_0_spawn_egg pasterdream:shadow_npc_spawn_egg
    python tools/rewrite_structure_ids.py pasterdream:aaroncos_lefthand_0 pasterdream:aaroncos_lefthand
可一次传多对 (旧, 新)。
"""
import argparse
import gzip
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SEARCH_DIRS = [Path(mod) / "src" / "main" / "resources" for mod in
               ("PasterDream", "PasterDreamAPI", "PasterDreamSpells", "PasterDreamSanity")]


def find_nbt_files():
    """收集全部模块资源目录下的 .nbt 文件。"""
    files = []
    for d in SEARCH_DIRS:
        if d.is_dir():
            files.extend(d.rglob("*.nbt"))
    return files


def rewrite_file(path, pairs):
    """对单个 .nbt 执行全部 (旧, 新) 替换，返回 {旧ID: 替换处数}。

    @param path  NBT 文件路径
    @param pairs (旧全名, 新全名) 列表，旧名必须长于新名
    @return 各旧 ID 的替换计数
    """
    raw = path.read_bytes()
    data = bytearray(gzip.decompress(raw) if raw[:2] == b"\x1f\x8b" else raw)
    counts = {}
    for old, new in pairs:
        old_b = old.encode("ascii")
        new_b = new.encode("ascii")
        if len(new_b) >= len(old_b):
            raise SystemExit(f"FAIL: 新 ID 必须短于旧 ID（{old} -> {new}），"
                             "加长会破坏 NBT 切片左移假设")
        count = 0
        idx = data.find(old_b)
        while idx != -1:
            # NBT TAG_String: 0x08 + 2 字节大端长度 + utf8，长度前缀紧贴字符串前
            old_len = (data[idx - 2] << 8) | data[idx - 1]
            if old_len != len(old_b):
                # 长度前缀不匹配：说明该命中是更长字符串的子串（如成就 ID），
                # 跳过并由调用方人工复核
                idx = data.find(old_b, idx + 1)
                continue
            data[idx - 2] = (len(new_b) >> 8) & 0xFF
            data[idx - 1] = len(new_b) & 0xFF
            data[idx:idx + len(old_b)] = new_b
            count += 1
            idx = data.find(old_b, idx)
        if count:
            counts[old] = count
            path.write_bytes(gzip.compress(bytes(data), mtime=0))
    return counts


def main():
    parser = argparse.ArgumentParser(description="重写结构 NBT 中的注册 ID")
    parser.add_argument("pairs", nargs="+", metavar="OLD_NEW",
                        help="旧ID 新ID 成对给出（可多对）")
    parser.add_argument("--dry-run", action="store_true",
                        help="只统计命中，不写回")
    args = parser.parse_args()
    if len(args.pairs) % 2 != 0:
        raise SystemExit("FAIL: 参数必须成对（旧ID 新ID）")
    pairs = [(args.pairs[i], args.pairs[i + 1]) for i in range(0, len(args.pairs), 2)]

    total = {}
    for path in find_nbt_files():
        if args.dry_run:
            raw = path.read_bytes()
            data = gzip.decompress(raw) if raw[:2] == b"\x1f\x8b" else raw
            for old, _ in pairs:
                n = data.count(old.encode("ascii"))
                if n:
                    print(f"[dry] {path}: {old} x{n}")
                    total[old] = total.get(old, 0) + n
        else:
            for old, n in rewrite_file(path, pairs).items():
                print(f"[done] {path}: {old} x{n}")
                total[old] = total.get(old, 0) + n
    if not total:
        print("无命中")
    return 0


if __name__ == "__main__":
    sys.exit(main())
