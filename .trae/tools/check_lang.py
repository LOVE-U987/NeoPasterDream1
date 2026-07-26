#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""语言文件完整性检查脚本。"""

import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent.parent
PD_ITEMS_DIR = ROOT / "PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry"
PD_ITEMS = PD_ITEMS_DIR / "PDItems.java"
PD_BLOCKS = PD_ITEMS_DIR / "PDBlocks.java"
PD_ITEMS_SUBDIR = PD_ITEMS_DIR / "items"
PD_BLOCKS_SUBDIR = PD_ITEMS_DIR / "blocks"
LANG_FILE = ROOT / "PasterDream/src/main/resources/assets/pasterdream/lang/zh_cn.json"

# 只匹配字面量完整注册名；排除 "foo_" + i 这类拼接前缀（否则会误报 structure_block_/dreamnotes_）
REGISTER_PATTERNS = [
    re.compile(r'\bregister\s*\(\s*"([a-z0-9_]+)"\s*[,)]'),
    re.compile(r'\bregisterSimple(?:Item|BlockItem)\s*\(\s*"([a-z0-9_]+)"\s*[,)]'),
    re.compile(r'\bregisterItem\s*\(\s*"([a-z0-9_]+)"\s*[,)]'),
    re.compile(r'\bregisterBlock\s*\(\s*"([a-z0-9_]+)"\s*[,)]'),
    re.compile(r'\bCurioAPI\.create\s*\(\s*"([a-z0-9_]+)"\s*[,)]'),
    re.compile(r'\bregisterCustom\s*\(\s*"([a-z0-9_]+)"\s*[,)]'),
]


def extract_names(path):
    text = path.read_text(encoding="utf-8")
    names = set()
    for pattern in REGISTER_PATTERNS:
        names.update(pattern.findall(text))
    return names


def extract_batch_names(path):
    text = path.read_text(encoding="utf-8")
    names = set()
    pattern = re.compile(r'BlockAPI\.batchRegister\s*\(\s*"([a-z0-9_]+)"\s*\)(.*?)\.build\s*\(\s*\)', re.DOTALL)
    for match in pattern.finditer(text):
        base = match.group(1)
        body = match.group(2)
        indices = set()
        range_match = re.search(r'\.range\s*\(\s*(\d+)\s*,\s*(\d+)\s*\)', body)
        if range_match:
            indices.update(range(int(range_match.group(1)), int(range_match.group(2)) + 1))
        list_match = re.search(r'\.indexList\s*\(([^)]+)\)', body)
        if list_match:
            for num in re.findall(r'\d+', list_match.group(1)):
                indices.add(int(num))
        exclude_match = re.search(r'\.exclude\s*\(([^)]+)\)', body)
        if exclude_match:
            for num in re.findall(r'\d+', exclude_match.group(1)):
                indices.discard(int(num))
        for idx in indices:
            names.add(f"{base}_{idx}")
    return names


def extract_variant_names(path):
    text = path.read_text(encoding="utf-8")
    names = set()
    suffixes = {
        "withStairs": "_stairs", "withSlab": "_slab", "withWall": "_wall",
        "withFence": "_fence", "withFenceGate": "_fencegate", "withDoor": "_door",
        "withTrapdoor": "_trapdoor", "withPressurePlate": "_pressure_plate", "withButton": "_button",
    }
    pattern = re.compile(r'BlockAPI\.createVariantSet\s*\(\s*"([a-z0-9_]+)"\s*,(.*?)\.build\s*\(\s*\)', re.DOTALL)
    for match in pattern.finditer(text):
        base = match.group(1)
        body = match.group(2)
        for method, suffix in suffixes.items():
            if f".{method}(" in body:
                names.add(f"{base}{suffix}")
    return names


def collect_names_from_files(files):
    """从多个 Java 文件中汇总注册名（支持主文件 + 拆分后的子文件）。"""
    names = set()
    batch_names = set()
    variant_names = set()
    for path in files:
        if not path.exists():
            continue
        names.update(extract_names(path))
        batch_names.update(extract_batch_names(path))
        variant_names.update(extract_variant_names(path))
    names.update(batch_names)
    names.update(variant_names)
    return names


def main():
    item_files = [PD_ITEMS] + sorted(PD_ITEMS_SUBDIR.glob("PDItems*.java"))
    block_files = [PD_BLOCKS] + sorted(PD_BLOCKS_SUBDIR.glob("PDBlocks*.java"))

    item_names = collect_names_from_files(item_files)
    block_names = collect_names_from_files(block_files)
    # BlockItem（registerSimpleBlockItem 等注册的方块物品）在运行时通过
    # block.pasterdream.* 键取显示名（BlockItem.getDescriptionId 委托方块），
    # 不需要 item.pasterdream.* 键——物品侧检查需排除方块名，避免误报。
    item_names -= block_names

    with LANG_FILE.open("r", encoding="utf-8") as f:
        lang_keys = set(json.load(f).keys())

    missing_blocks = sorted(n for n in block_names if f"block.pasterdream.{n}" not in lang_keys)
    missing_items = sorted(n for n in item_names if f"item.pasterdream.{n}" not in lang_keys)
    has_missing = missing_blocks or missing_items

    print("=" * 60)
    print("语言文件完整性检查结果")
    print("=" * 60)
    print(f"PDItems 注册数: {len(item_names)}")
    print(f"PDBlocks 注册数: {len(block_names)}")
    print(f"zh_cn.json 键总数: {len(lang_keys)}")
    print("-" * 60)

    if missing_blocks:
        print(f"\n[缺失] block.pasterdream.* 翻译键（共 {len(missing_blocks)} 个）:")
        for name in missing_blocks:
            print(f"  - block.pasterdream.{name}")

    if missing_items:
        print(f"\n[缺失] item.pasterdream.* 翻译键（PDItems 直接注册，共 {len(missing_items)} 个）:")
        for name in missing_items:
            print(f"  - item.pasterdream.{name}")

    if not has_missing:
        print("\n✅ 所有注册项均已找到对应的翻译键。")

    print("-" * 60)
    return 1 if has_missing else 0


if __name__ == "__main__":
    raise SystemExit(main())
