#!/usr/bin/env python3
"""深度检查战利品表，看看哪些地方还有优化空间"""

import json
import os
import re

MOD_ID = "pasterdream"
LOOT_DIR = "src/main/resources/data/pasterdream/loot_tables/blocks"
BLOCKS_FILE = "src/main/java/com/pasterdream/pasterdreammod/registry/PDBlocks.java"

# ==================== 从 Java 提取方块注册名和注册方式 ====================
print("=" * 60)
print("🔍 深度战利品表分析报告")
print("=" * 60)

with open(BLOCKS_FILE, "r", encoding="utf-8") as f:
    content = f.read()

# 提取所有注册方式（registerSimpleBlock vs registerBlock）
simple_blocks = set()
custom_blocks = set()

for m in re.finditer(r'BLOCKS\.registerSimpleBlock\s*\(\s*"([a-z_0-9]+)"', content):
    simple_blocks.add(m.group(1))
for m in re.finditer(r'BLOCKS\.registerBlock\s*\(\s*"([a-z_0-9]+)"', content):
    custom_blocks.add(m.group(1))
for m in re.finditer(r'BLOCKS\.register\s*\(\s*"([a-z_0-9]+)"', content):
    name = m.group(1)
    # 排除上面已经匹配过的
    if name not in simple_blocks and name not in custom_blocks:
        custom_blocks.add(name)

all_blocks = simple_blocks | custom_blocks

print(f"\n📦 方块注册方式分布:")
print(f"  registerSimpleBlock (基础方块): {len(simple_blocks)} 个")
print(f"  registerBlock (自定义方块): {len(custom_blocks)} 个")

# ==================== 逐个检查 JSON 内容 ====================
print(f"\n📋 逐方块检查结果:")
print(f"  {'方块名':<35} {'注册方式':<20} {'掉落物':<30} {'是否需优化'}")
print(f"  {'-'*35} {'-'*20} {'-'*30} {'-'*10}")

needs_attention = []

for block_name in sorted(all_blocks):
    is_simple = block_name in simple_blocks
    reg_type = "registerSimpleBlock" if is_simple else "registerBlock"

    json_path = os.path.join(LOOT_DIR, f"{block_name}.json")
    if not os.path.exists(json_path):
        print(f"  ❌ {block_name:<33} {reg_type:<20} {'文件缺失！':<30} {'❌'}")
        needs_attention.append(block_name)
        continue

    with open(json_path, "r", encoding="utf-8") as f:
        data = json.load(f)

    item_name = data["pools"][0]["entries"][0]["name"]
    
    # 检查潜在问题
    issues = []
    
    # 1. 矿石检查 - 是否有时运效果？
    if "ore" in block_name:
        # 检查是否有数量范围或时运
        entry = data["pools"][0]["entries"][0]
        if "functions" not in entry:
            issues.append("缺少时运(Fortune)支持")
    
    # 2. 树叶检查 - 是否需要 Silk Touch 逻辑？
    if "leaves" in block_name:
        issues.append("无Silk Touch逻辑(需要时可加)")
    
    # 3. 玻璃/冰检查
    if "glass" in block_name or "ice" in block_name or "glaspane" in block_name:
        issues.append("无Silk Touch逻辑(需要时可加)")
    
    # 4. 双层花/草检查
    if block_name in [f"flower_{i}" for i in [7, 10, 11, 12, 18]] + [f"grass_{i}" for i in [4, 10, 15]]:
        issues.append("双高层方块(当前简化:破坏掉落自身)")

    if issues:
        print(f"  ⚠️  {block_name:<33} {reg_type:<20} {item_name:<30} {'⚠️  ' + issues[0][:20]}")
        for iss in issues[1:]:
            print(f"  {'':<35} {'':<20} {'':<30} {'⚠️  ' + iss[:20]}")
        needs_attention.append((block_name, issues))
    else:
        print(f"  ✅  {block_name:<33} {reg_type:<20} {item_name:<30} {'✅'}")

# ==================== 优化建议汇总 ====================
print(f"\n{'='*60}")
print("💡 优化建议汇总")
print(f"{'='*60}")

optimization_items = []

# 检查矿石时运
ore_blocks = [b for b in all_blocks if "ore" in b]
if ore_blocks:
    optimization_items.append(f"🔧 时运(Fortune)支持: {len(ore_blocks)} 个矿石可以添加时运效果")
    for b in ore_blocks:
        optimization_items.append(f"    - {b} (当前固定掉落1个)")

# 检查 Silk Touch
st_blocks = [b for b in all_blocks if "glass" in b or "ice" in b or "leaves" in b]
if st_blocks:
    optimization_items.append(f"🔧 精准采集(Silk Touch)支持: {len(st_blocks)} 个方块可以添加")
    for b in st_blocks[:5]:
        optimization_items.append(f"    - {b}")
    if len(st_blocks) > 5:
        optimization_items.append(f"    - ...等 {len(st_blocks)} 个")

if optimization_items:
    for item in optimization_items:
        print(f"  {item}")
else:
    print(f"  ✅ 所有方块战利品表配置已经完善")

# ==================== 检查实体战利品表 ====================
print(f"\n{'='*60}")
print("👾 实体战利品表状态")
print(f"{'='*60}")

entity_loot_dir = "src/main/resources/data/pasterdream/loot_tables/entities"
if os.path.exists(entity_loot_dir):
    entity_files = [f for f in os.listdir(entity_loot_dir) if f.endswith(".json")]
    for f in entity_files:
        print(f"  ✅ {f}")
    print(f"\n  共 {len(entity_files)} 个实体战利品表")
else:
    print(f"  ⚠️ 实体战利品表目录不存在")

# ==================== 最终结论 ====================
print(f"\n{'='*60}")
print("📊 最终结论")
print(f"{'='*60}")

if needs_attention:
    print(f"\n❌ {len([x for x in needs_attention if isinstance(x, str)])} 个方块完全缺失战利品表文件")
    print(f"⚠️  {len([x for x in needs_attention if isinstance(x, tuple)])} 个方块有优化空间")
else:
    print(f"\n✅ 所有方块均已配置战利品表")
    print(f"✅ 全量覆盖无遗漏!")

print(f"\n💬 需要我帮你处理以上优化项吗？比如:")
print(f"   1. 给矿石添加时运效果(提高掉落数量)")
print(f"   2. 给玻璃/冰/树叶添加精准采集逻辑")
print(f"   3. 其他你有想到的需求")

print(f"\n{'='*60}")
print("✅ 深度分析完成！")
print(f"{'='*60}")