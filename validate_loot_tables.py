#!/usr/bin/env python3
"""全面验证 PasterDream 方块战利品表 JSON 的正确性"""

import json
import os
import re
import sys

MOD_ID = "pasterdream"
BASE_DIR = "src/main/resources"
LOOT_DIR = os.path.join(BASE_DIR, "data", MOD_ID, "loot_tables", "blocks")
BLOCKS_FILE = "src/main/java/com/pasterdream/pasterdreammod/registry/PDBlocks.java"
ITEMS_FILE = "src/main/java/com/pasterdream/pasterdreammod/registry/PDItems.java"

errors = []
warnings = []

def report_error(msg):
    errors.append(f"  ❌ {msg}")
    print(f"    {msg}")

def report_warning(msg):
    warnings.append(f"  ⚠️ {msg}")
    print(f"    {msg}")

# ============================================================
# 1. 从 PDBlocks.java 提取所有注册的方块名
# ============================================================
print("🔍 [1/5] 提取 PDBlocks.java 注册方块名...")
block_names_from_java = set()
# 匹配模式：public static final DeferredBlock<...> NAME = BLOCKS.register*("block_name"
patterns = [
    r'BLOCKS\.register(?:Simple)?Block\s*\(\s*"([a-z_0-9]+)"',   # registerBlock / registerSimpleBlock
    r'BLOCKS\.register(?:Simple)?Block\s*\(\s*//[^"]*"([a-z_0-9]+)"',  # 带注释的（基本用不上）
]

with open(BLOCKS_FILE, "r", encoding="utf-8") as f:
    content = f.read()

for p in patterns:
    for m in re.finditer(p, content):
        block_names_from_java.add(m.group(1))

# 额外：register("name", ...) 但排除 registerSimpleBlock 和 registerBlock 已经匹配过的
for m in re.finditer(r'BLOCKS\.register\s*\(\s*"([a-z_0-9]+)"', content):
    block_names_from_java.add(m.group(1))

print(f"    PDBlocks.java 中共发现 {len(block_names_from_java)} 个方块注册")
# 打印出来方便核对
for b in sorted(block_names_from_java):
    print(f"      - {b}")

# ============================================================
# 2. 从 PDItems.java 提取所有注册的物品名
# ============================================================
print("\n🔍 [2/5] 提取 PDItems.java 注册物品名...")
item_names_from_java = set()
with open(ITEMS_FILE, "r", encoding="utf-8") as f:
    items_content = f.read()

for m in re.finditer(r'ITEMS\.register(?:Simple)?(?:Block)?Item\s*\(\s*"([a-z_0-9]+)"', items_content):
    item_names_from_java.add(m.group(1))
for m in re.finditer(r'ITEMS\.register\s*\(\s*"([a-z_0-9]+)"', items_content):
    item_names_from_java.add(m.group(1))

print(f"    PDItems.java 中共发现 {len(item_names_from_java)} 个物品注册")

# ============================================================
# 3. 检查所有生成的 JSON 文件
# ============================================================
print(f"\n🔍 [3/5] 扫描 {LOOT_DIR} 目录下的 JSON 文件...")

if not os.path.exists(LOOT_DIR):
    report_error(f"目录不存在: {LOOT_DIR}")
    sys.exit(1)

json_files = [f for f in os.listdir(LOOT_DIR) if f.endswith(".json")]
json_block_names = {f.replace(".json", "") for f in json_files}
print(f"    共找到 {len(json_files)} 个 JSON 文件")

# ============================================================
# 4. 逐文件验证 JSON 内容
# ============================================================
print("\n🔍 [4/5] 逐文件验证 JSON 内容正确性...")

valid_json_count = 0
invalid_json_count = 0
item_ref_issues = []

for filename in sorted(json_files):
    block_name = filename.replace(".json", "")
    filepath = os.path.join(LOOT_DIR, filename)

    try:
        with open(filepath, "r", encoding="utf-8") as f:
            data = json.load(f)
        valid_json_count += 1
    except json.JSONDecodeError as e:
        report_error(f"{filename}: JSON 语法错误 - {e}")
        invalid_json_count += 1
        continue

    # 检查顶级结构
    if data.get("type") != "minecraft:block":
        report_error(f"{filename}: type 应为 'minecraft:block'，实际为 '{data.get('type')}'")

    pools = data.get("pools", [])
    if not pools:
        report_error(f"{filename}: 缺少 pools 数组")
        continue

    for i, pool in enumerate(pools):
        if pool.get("rolls") != 1:
            report_warning(f"{filename}: pool[{i}] rolls 不为 1")

        entries = pool.get("entries", [])
        if not entries:
            report_error(f"{filename}: pool[{i}] 缺少 entries")
            continue

        for j, entry in enumerate(entries):
            if entry.get("type") != "minecraft:item":
                report_error(f"{filename}: entry[{j}] type 应为 'minecraft:item'")
                continue

            item_name = entry.get("name", "")
            if not item_name:
                report_error(f"{filename}: entry[{j}] 缺少 name")
                continue

            # 检查物品名格式
            if not item_name.startswith(f"{MOD_ID}:"):
                report_warning(f"{filename}: 物品引用 '{item_name}' 不属于本模组命名空间")

            # 检查引用的物品是否在 items 中注册
            ref_item = item_name.split(":")[-1] if ":" in item_name else item_name
            if ref_item not in item_names_from_java:
                # 但 block item 可能没有直接的物品注册，检查它是不是一个方块名
                if ref_item != block_name:
                    item_ref_issues.append((filename, ref_item))

        # 检查 conditions
        conditions = pool.get("conditions", [])
        has_survives = any(c.get("condition") == "minecraft:survives_explosion" for c in conditions)
        if not has_survives:
            report_warning(f"{filename}: pool[{i}] 缺少 survives_explosion 条件")

print(f"\n    JSON 语法正确: {valid_json_count}")
print(f"    JSON 语法错误: {invalid_json_count}")

if item_ref_issues:
    print(f"\n    ⚠️ 引用的物品可能未在 PDItems 中注册（可能是 BlockItem 自动生成，需人工确认）:")
    for fname, ref in item_ref_issues:
        print(f"      {fname}: 引用物品 '{ref}'")

# ============================================================
# 5. 检查覆盖完整性：是否有方块遗漏了战利品表？
# ============================================================
print("\n🔍 [5/5] 检查方块战利品表覆盖完整性...")

# 已生成 JSON 的方快的集合
generated_blocks = json_block_names

# 缺失的方块
missing = block_names_from_java - generated_blocks
extra = generated_blocks - block_names_from_java

if missing:
    report_error(f"以下 {len(missing)} 个方块缺少战利品表文件:")
    for b in sorted(missing):
        print(f"      - {b}")
else:
    print(f"    ✅ 所有 {len(block_names_from_java)} 个已注册方块都有对应的战利品表")

if extra:
    report_warning(f"以下 {len(extra)} 个战利品表文件找不到对应的 PDBlocks 注册（可能是无害的残留文件）:")
    for b in sorted(extra):
        print(f"      - {b}")
else:
    print(f"    ✅ 没有多余的战利品表文件")

# ============================================================
# 总结
# ============================================================
print("\n" + "=" * 50)
print("📊 验证总结")
print("=" * 50)
print(f"  战利品表文件数:      {len(json_files)}")
print(f"  PDBlocks 注册方块:   {len(block_names_from_java)}")
print(f"  JSON 语法错误:       {invalid_json_count}")
print(f"  遗漏方块:            {len(missing)}")
print(f"  多余文件:            {len(extra)}")
print(f"  物品引用待确认:      {len(item_ref_issues)}")

if errors:
    print(f"\n  ❌ {len(errors)} 个错误待修复")
    for e in errors:
        print(e)
else:
    print("\n  ✅ 零错误！")

if warnings:
    print(f"\n  ⚠️ {len(warnings)} 个警告（建议复查）")
    for w in warnings:
        print(w)

print("\n✅ 验证完成！")