from pathlib import Path

content = r'''#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
语言文件完整性检查脚本。

扫描 PDItems.java 与 PDBlocks.java 中的注册名，
与 assets/pasterdream/lang/zh_cn.json 进行对比，
输出缺失的 block.pasterdream.<name> 与 item.pasterdream.<name> 翻译键。

支持:
- 普通 DeferredRegister 注册
- CurioAPI.create 注册
- BlockAPI.batchRegister 批量编号注册
- BlockAPI.createVariantSet 建筑变体族注册

用法:
    python .trae/tools/check_lang.py
"""

import json
import re
from pathlib import Path

# 项目根目录（脚本位于 .trae/tools/）
ROOT = Path(__file__).resolve().parent.parent.parent

PD_ITEMS = ROOT / "PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDItems.java"
PD_BLOCKS = ROOT / "PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDBlocks.java"
LANG_FILE = ROOT / "PasterDream/src/main/resources/assets/pasterdream/lang/zh_cn.json"

# 普通注册名匹配
REGISTER_PATTERNS = [
    re.compile(r'\bregister\s*\(\s*"([a-z0-9_]+)"'),
    re.compile(r'\bregisterSimple(?:Item|BlockItem)\s*\(\s*"([a-z0-9_]+)"'),
    re.compile(r'\bregisterItem\s*\(\s*"([a-z0-9_]+)"'),
    re.compile(r'\bCurioAPI\.create\s*\(\s*"([a-z0-9_]+)"'),
    re.compile(r'\bregisterCustom\s*\(\s*"([a-z0-9_]+)"'),
]


def extract_names(file_path: Path) -> set[str]:
    """从 Java 文件中提取普通注册名。"""
    if not file_path.exists():
        print(f"[警告] 文件不存在: {file_path}")
        return set()

    text = file_path.read_text(encoding="utf-8")
    names: set[str] = set()
    for pattern in REGISTER_PATTERNS:
        names.update(pattern.findall(text))
    return names


def extract_batch_register_names(file_path: Path) -> set[str]:
    """解析 BlockAPI.batchRegister(...) 调用，生成 {base}_{index} 名称。"""
    text = file_path.read_text(encoding="utf-8")
    names: set[str] = set()

    batch_pattern = re.compile(
        r'BlockAPI\.batchRegister\s*\(\s*"([a-z0-9_]+)"\s*\)(.*?)\.build\s*\(\s*\)',
        re.DOTALL,
    )

    for match in batch_pattern.finditer(text):
        base_name = match.group(1)
        body = match.group(2)

        indices: set[int] = set()

        range_match = re.search(r'\.range\s*\(\s*(\d+)\s*,\s*(\d+)\s*\)', body)
        if range_match:
            start, end = int(range_match.group(1)), int(range_match.group(2))
            indices.update(range(start, end + 1))

        index_list_match = re.search(r'\.indexList\s*\(([^)]+)\)', body)
        if index_list_match:
            for num_str in re.findall(r'\d+', index_list_match.group(1)):
                indices.add(int(num_str))

        exclude_match = re.search(r'\.exclude\s*\(([^)]+)\)', body)
        if exclude_match:
            for num_str in re.findall(r'\d+', exclude_match.group(1)):
                indices.discard(int(num_str))

        for index in sorted(indices):
            names.add(f"{base_name}_{index}")

    return names


VARIANT_SUFFIXES = {
    "withStairs": "_stairs",
    "withSlab": "_slab",
    "withWall": "_wall",
    "withFence": "_fence",
    "withFenceGate": "_fencegate",
    "withDoor": "_door",
    "withTrapdoor": "_trapdoor",
    "withPressurePlate": "_pressure_plate",
    "withButton": "_button",
}


def extract_variant_set_names(file_path: Path) -> set[str]:
    """解析 BlockAPI.createVariantSet(...) 调用，生成变体名称。"""
    text = file_path.read_text(encoding="utf-8")
    names: set[str] = set()

    variant_pattern = re.compile(
        r'BlockAPI\.createVariantSet\s*\(\s*"([a-z0-9_]+)"\s*,(.*?)\.build\s*\(\s*\)',
        re.DOTALL,
    )

    for match in variant_pattern.finditer(text):
        base_name = match.group(1)
        body = match.group(2)

        for method, suffix in VARIANT_SUFFIXES.items():
            if f".{method}(" in body or f".{method} " in body:
                names.add(f"{base_name}{suffix}")

    return names


def load_lang_keys(file_path: Path) -> set[str]:
    """加载语言文件中的所有键。"""
    if not file_path.exists():
        print(f"[错误] 语言文件不存在: {file_path}")
        return set()

    with file_path.open("r", encoding="utf-8") as f:
        data = json.load(f)
    return set(data.keys())


def main() -> int:
    """主入口。"""
    item_names = extract_names(PD_ITEMS)
    block_names = extract_names(PD_BLOCKS)
    block_names.update(extract_batch_register_names(PD_BLOCKS))
    block_names.update(extract_variant_set_names(PD_BLOCKS))

    block_item_names = block_names.copy()

    lang_keys = load_lang_keys(LANG_FILE)

    missing_blocks = sorted(name for name in block_names if f"block.pasterdream.{name}" not in lang_keys)
    missing_items = sorted(name for name in item_names if f"item.pasterdream.{name}" not in lang_keys)
    missing_block_items = sorted(
        name for name in block_item_names
        if f"item.pasterdream.{name}" not in lang_keys
    )

    has_missing = missing_blocks or missing_items or missing_block_items

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

    if missing_block_items:
        print(f"\n[缺失] item.pasterdream.* 翻译键（BlockItem 形态，共 {len(missing_block_items)} 个）:")
        for name in missing_block_items:
            print(f"  - item.pasterdream.{name}")

    if not has_missing:
        print("\n✅ 所有注册项均已找到对应的翻译键。")

    print("-" * 60)
    return 1 if has_missing else 0


if __name__ == "__main__":
    raise SystemExit(main())
'''

path = Path(r'c:\Users\97128\Documents\GitHub\NeoPasterDream1\.trae\tools\check_lang.py')
path.write_text(content, encoding='utf-8')
print(f"Written {len(content)} chars to {