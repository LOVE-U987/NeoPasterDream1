# -*- coding: utf-8 -*-
"""为冷域方块批量追加/创建标签（block + item + common farmlands）"""
import json
import os

BASE = r'c:\Users\97128\Documents\GitHub\NeoPasterDream1\PasterDream\src\main\resources\data'

# 需要追加的标签：路径 -> 追加值列表（存在则去重追加，不存在则创建）
APPEND = {
    'minecraft/tags/block/logs.json': [
        'pasterdream:cold_domain_log',
        'pasterdream:stripped_cold_domain_log',
    ],
    'minecraft/tags/block/logs_that_burn.json': [
        'pasterdream:cold_domain_log',
        'pasterdream:stripped_cold_domain_log',
    ],
    'minecraft/tags/block/leaves.json': [
        'pasterdream:cold_domain_leaves',
    ],
    'minecraft/tags/block/dirt.json': [
        'pasterdream:cold_domain_dirt',
        'pasterdream:snowy_cold_domain_grass',
    ],
    'minecraft/tags/item/logs.json': [
        'pasterdream:cold_domain_log',
        'pasterdream:stripped_cold_domain_log',
    ],
    'minecraft/tags/item/logs_that_burn.json': [
        'pasterdream:cold_domain_log',
        'pasterdream:stripped_cold_domain_log',
    ],
    'minecraft/tags/item/leaves.json': [
        'pasterdream:cold_domain_leaves',
    ],
    'minecraft/tags/item/dirt.json': [
        'pasterdream:cold_domain_dirt',
        'pasterdream:snowy_cold_domain_grass',
    ],
}

# 需要新建的标签：路径 -> values
CREATE = {
    'c/tags/block/farmlands.json': [
        'pasterdream:dyedream_farmland',
    ],
    'c/tags/item/farmlands.json': [
        'pasterdream:dyedream_farmland',
    ],
}

for rel, add_values in APPEND.items():
    p = os.path.join(BASE, rel)
    if os.path.exists(p):
        d = json.load(open(p, encoding='utf-8'))
        values = d.setdefault('values', [])
        for v in add_values:
            if v not in values:
                values.append(v)
        d['replace'] = d.get('replace', False)
    else:
        d = {'replace': False, 'values': add_values}
    with open(p, 'w', encoding='utf-8') as f:
        json.dump(d, f, ensure_ascii=False, indent=2)
    print('更新:', rel, '->', d['values'])

for rel, values in CREATE.items():
    p = os.path.join(BASE, rel)
    if os.path.exists(p):
        print('已存在（跳过创建）:', rel)
        continue
    os.makedirs(os.path.dirname(p), exist_ok=True)
    d = {'replace': False, 'values': values}
    with open(p, 'w', encoding='utf-8') as f:
        json.dump(d, f, ensure_ascii=False, indent=2)
    print('创建:', rel, '->', values)

print('标签处理完成')
