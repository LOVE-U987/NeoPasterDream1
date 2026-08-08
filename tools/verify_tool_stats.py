# -*- coding: utf-8 -*-
"""校验新模组 PDItemsTools/PDItemsMaterials 工具数值与原模组一致"""
import re
import pathlib

root = pathlib.Path(r'c:\Users\97128\Documents\GitHub\NeoPasterDream1')
# 原模组数据: name -> (uses, speed, finalDmg, spd, ench)
ORIG = {
    # 剑: finalDmg = ctor + bonus
    'broken_hero_sword': (100, 1, 6.0, -2.4, 0),
    'copper_sword': (225, 1, 4.5, -2.4, 12),
    'creative_sword': (100, 4, 9.0, 6.0, 2),
    'desert_sword': (1561, 1, 10.0, -3.1, 8),
    'dyedream_sword_0': (1314, 1, 8.0, -2.4, 22),
    'dyedream_sword': (1314, 1, 7.0, -2.4, 22),
    'grass_sword': (874, 1, 6.0, -2.5, 16),
    'iceshadow_hammer': (835, 4, 12.0, -3.3, 2),
    'moltengold_sword': (251, 1, 5.0, -2.3, 23),
    'shadow_erosion_sword': (1725, 4, 5.5, -1.0, 2),
    'shadow_sword': (1771, 0, 11.0, -2.4, 10),
    'thermal_dagger': (1721, 1, 5.5, -2.3, 2),
    'tide_sword': (1561, 4, 7.5, -2.8, 11),
    'titanium_sword': (1721, 1, 6.5, -2.4, 17),
    'true_desert_sword': (1561, 1, 11.0, -3.1, 8),
    'true_grass_sword': (1311, 1, 6.5, -2.5, 16),
    'true_moltengold_sword': (1255, 1, 6.0, -2.2, 23),
    'true_tide_sword': (1561, 4, 8.0, -2.8, 11),
    'truest_moltengold_sword': (1255, 1, 6.0, -2.15, 23),
    'white_sword': (1771, 0, 8.0, -2.4, 10),  # 自定义类，跳过（挖速0→2无影响）
    # 镐/斧/锹/锄
    'copper_pickaxe': (225, 4, 2.5, -2.8, 12),
    'copper_axe': (225, 5, 8.0, -3.15, 12),
    'copper_shovel': (225, 5, 3.0, -3.0, 12),
    'copper_hoe': (225, 5, 0.0, -1.5, 12),
    'titanium_pickaxe': (1721, 9, 4.5, -2.8, 17),
    'titanium_axe': (1721, 9, 9.0, -3.0, 17),
    'titanium_shovel': (1721, 9, 5.5, -3.0, 17),
    'titanium_hoe': (1721, 9, 0.5, 0.0, 17),
    'dyedream_pickaxe': (1314, 11, 5.0, -2.8, 22),
    'dyedream_hammer': (6570, 3, 10.0, -3.3, 22),
    'dyedream_axe': (1314, 11, 9.5, -3.0, 22),
    'dyedream_shovel': (1314, 11, 5.5, -3.0, 22),
    'dyedream_hoe': (1314, 11, 1.0, 0.0, 22),
    'moltengold_pickaxe': (251, 14, 3.0, -2.7, 23),
    'moltengold_axe': (251, 14, 8.0, -3.0, 23),
    'moltengold_shovel': (251, 14, 3.5, -2.9, 23),
    'moltengold_hoe': (251, 14, 0.0, -0.5, 23),
    'true_moltengold_pickaxe': (1255, 16, 4.0, -2.6, 23),
    'shadow_erosion_pickaxe': (1725, 13, 5.0, -2.8, 16),
    'shadow_erosion_axe': (1725, 13, 10.0, -3.0, 16),
    'shadow_erosion_shovel': (1725, 13, 6.0, -3.0, 16),
    'shadow_erosion_hoe': (1725, 13, 1.5, 0.0, 16),
    # 融梦系列：故意修改（250/6/附魔5/铁级），不参与校验
}

files = [
    root / 'PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/items/PDItemsTools.java',
    root / 'PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/items/PDItemsMaterials.java',
]
texts = {f.name: f.read_text(encoding='utf-8', errors='ignore') for f in files}
combined = '\n'.join(texts.values())

# 解析新模组注册块: name -> (durability, miningSpeed, attackDamage, attackSpeed, enchantment)
pattern = re.compile(
    r'toolItem\("(?P<name>[\w_]+)"\)\s*'
    r'\.type\(ToolType\.(?P<type>\w+)\)\s*\.durability\((?P<dur>\d+)\)'
    r'(?:\s*\.miningSpeed\((?P<speed>[0-9.]+)f?\))?'
    r'\s*\.attackDamage\((?P<dmg>-?[0-9.]+)f?\)\.attackSpeed\((?P<spd>-?[0-9.]+)f?\)'
    r'(?:\s*\.enchantment\((?P<ench>\d+)\))?',
    re.S,
)

new_vals = {}
for m in pattern.finditer(combined):
    name = m.group('name')
    new_vals[name] = (
        int(m.group('dur')),
        float(m.group('speed').rstrip('f')) if m.group('speed') else None,
        float(m.group('dmg').rstrip('f')),
        float(m.group('spd').rstrip('f')),
        int(m.group('ench')) if m.group('ench') else 5,  # 默认附魔 5
    )

mismatch = 0
for name, orig in ORIG.items():
    if name == 'white_sword':
        continue
    if name not in new_vals:
        print(f'MISSING in new: {name}')
        mismatch += 1
        continue
    nv = new_vals[name]
    ou, os_, od, osp, oe = orig
    nu, ns, nd, nsp, ne = nv
    issues = []
    if nu != ou:
        issues.append(f'耐久 {ou}->{nu}')
    if ns is not None and os_ is not None and abs(ns - os_) > 0.01:
        issues.append(f'挖速 {os_}->{ns}')
    if abs(nd - od) > 0.01:
        issues.append(f'伤害 {od}->{nd}')
    if abs(nsp - osp) > 0.01:
        issues.append(f'攻速 {osp}->{nsp}')
    if ne != oe:
        issues.append(f'附魔 {oe}->{ne}')
    if issues:
        print(f'DIFF {name}: ' + '; '.join(issues))
        mismatch += 1

# 检查新模组中是否有 toolItem 注册但不在 ORIG 中的（多出来的）
for name in new_vals:
    if name not in ORIG and 'meltdream' not in name:
        print(f'EXTRA in new (未在原模组比对表): {name} {new_vals[name]}')

print(f'\n总校验 {len(ORIG)} 项, 差异 {mismatch} 项')
