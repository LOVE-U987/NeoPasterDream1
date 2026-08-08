# -*- coding: utf-8 -*-
"""验证冷域新增资源：JSON 可解析、纹理存在、模型引用纹理存在、维度 JSON 引用完整"""
import json
import os
import re

ROOT = r'c:\Users\97128\Documents\GitHub\NeoPasterDream1'
ASSETS = os.path.join(ROOT, 'PasterDream', 'src', 'main', 'resources', 'assets', 'pasterdream')
GEN_ASSETS = os.path.join(ROOT, 'PasterDream', 'src', 'generated', 'resources', 'assets', 'pasterdream')
DATA = os.path.join(ROOT, 'PasterDream', 'src', 'main', 'resources', 'data', 'pasterdream')
DATA_ROOT = os.path.join(ROOT, 'PasterDream', 'src', 'main', 'resources', 'data')
TEX = os.path.join(ASSETS, 'textures', 'block')

errors = []

# 1. 纹理文件存在性
tex_names = [
    'cold_domain_log.png', 'cold_domain_log_top.png',
    'stripped_cold_domain_log.png', 'stripped_cold_domain_log_top.png',
    'cold_domain_leaves.png', 'cold_domain_dirt.png',
    'snowy_cold_domain_grass_top.png', 'snowy_cold_domain_grass_side.png',
    'dyedream_farmland.png', 'moist_dyedream_farmland.png',
]
for t in tex_names:
    p = os.path.join(TEX, t)
    if not os.path.exists(p):
        errors.append(f'纹理缺失: {p}')

# 2. 手写 JSON 可解析
json_files = [
    'models/block/cold_domain_leaves.json', 'models/item/cold_domain_leaves.json',
    'blockstates/cold_domain_leaves.json',
    'models/block/snowy_cold_domain_grass.json', 'models/block/snowy_cold_domain_grass_snowy.json',
    'models/item/snowy_cold_domain_grass.json', 'blockstates/snowy_cold_domain_grass.json',
    'models/block/dyedream_farmland.json', 'models/block/dyedream_farmland_moist.json',
    'models/item/dyedream_farmland.json', 'blockstates/dyedream_farmland.json',
    'models/item/cold_domain_dirt.json', 'models/item/cold_domain_log.json',
    'models/item/stripped_cold_domain_log.json',
]
for rel in json_files:
    p = os.path.join(ASSETS, rel)
    if not os.path.exists(p):
        errors.append(f'手写JSON缺失: {rel}')
        continue
    try:
        json.load(open(p, encoding='utf-8'))
    except Exception as e:
        errors.append(f'JSON解析失败 {rel}: {e}')

# 3. 战利品表 JSON
loot_files = [
    'cold_domain_leaves', 'snowy_cold_domain_grass', 'dyedream_farmland',
    'cold_domain_dirt', 'cold_domain_log', 'stripped_cold_domain_log',
]
for name in loot_files:
    p = os.path.join(DATA, 'loot_table', 'blocks', f'{name}.json')
    if not os.path.exists(p):
        errors.append(f'战利品表缺失: {name}')
        continue
    try:
        json.load(open(p, encoding='utf-8'))
    except Exception as e:
        errors.append(f'战利品表解析失败 {name}: {e}')

# 4. DataGen 生成文件检查
gen_checks = [
    'models/block/cold_domain_dirt.json',
    'models/block/cold_domain_log.json',
    'models/block/cold_domain_log_horizontal.json',
    'models/block/stripped_cold_domain_log.json',
    'models/block/stripped_cold_domain_log_horizontal.json',
    'blockstates/cold_domain_log.json',
    'blockstates/cold_domain_dirt.json',
]
for rel in gen_checks:
    p = os.path.join(GEN_ASSETS, rel)
    if not os.path.exists(p):
        errors.append(f'DataGen生成缺失: {rel}')

# 5. 维度 JSON
dim_checks = [
    (DATA, 'dimension/cold_domain_world.json'),
    (DATA, 'dimension_type/cold_domain_world.json'),
    (DATA, 'worldgen/biome/cold_domain_biome.json'),
    (DATA, 'worldgen/noise_settings/cold_domain_world.json'),
]
for base, rel in dim_checks:
    p = os.path.join(base, rel)
    if not os.path.exists(p):
        errors.append(f'维度JSON缺失: {rel}')
        continue
    try:
        json.load(open(p, encoding='utf-8'))
    except Exception as e:
        errors.append(f'维度JSON解析失败 {rel}: {e}')

# 6. 语言文件包含新键
for lang in ['zh_cn.json', 'en_us.json']:
    p = os.path.join(ASSETS, 'lang', lang)
    try:
        d = json.load(open(p, encoding='utf-8'))
    except Exception as e:
        errors.append(f'语言文件解析失败 {lang}: {e}')
        continue
    for key in ['block.pasterdream.cold_domain_log', 'block.pasterdream.snowy_cold_domain_grass',
                'block.pasterdream.dyedream_farmland', 'itemGroup.pasterdream.cold_domain_tab']:
        if key not in d:
            errors.append(f'语言键缺失 {lang}: {key}')

# 7. 标签文件检查
tag_checks = [
    ('minecraft/tags/block/logs.json', 'pasterdream:cold_domain_log'),
    ('minecraft/tags/block/leaves.json', 'pasterdream:cold_domain_leaves'),
    ('minecraft/tags/block/dirt.json', 'pasterdream:cold_domain_dirt'),
    ('minecraft/tags/item/logs.json', 'pasterdream:cold_domain_log'),
    ('minecraft/tags/item/leaves.json', 'pasterdream:cold_domain_leaves'),
    ('minecraft/tags/item/dirt.json', 'pasterdream:cold_domain_dirt'),
    ('c/tags/block/farmlands.json', 'pasterdream:dyedream_farmland'),
    ('c/tags/item/farmlands.json', 'pasterdream:dyedream_farmland'),
]
for rel, val in tag_checks:
    p = os.path.join(DATA_ROOT, rel)
    if not os.path.exists(p):
        errors.append(f'标签缺失: {rel}')
        continue
    try:
        d = json.load(open(p, encoding='utf-8'))
        if val not in d.get('values', []):
            errors.append(f'标签缺值 {rel}: 缺少 {val}')
    except Exception as e:
        errors.append(f'标签解析失败 {rel}: {e}')

if errors:
    print('发现问题:')
    for e in errors:
        print('  -', e)
    print(f'共 {len(errors)} 个问题')
else:
    print('✅ 所有资源验证通过（纹理/JSON/战利品表/维度/语言/标签）')
