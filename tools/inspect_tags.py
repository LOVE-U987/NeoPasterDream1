# -*- coding: utf-8 -*-
"""查看现有 minecraft 标签文件内容，用于规划冷域方块标签追加"""
import json
import os

BASE = r'c:\Users\97128\Documents\GitHub\NeoPasterDream1\PasterDream\src\main\resources\data'
FILES = [
    'minecraft/tags/block/logs.json',
    'minecraft/tags/block/logs_that_burn.json',
    'minecraft/tags/block/leaves.json',
    'minecraft/tags/block/dirt.json',
    'minecraft/tags/item/logs.json',
    'minecraft/tags/item/logs_that_burn.json',
    'minecraft/tags/item/leaves.json',
    'minecraft/tags/item/dirt.json',
]
for f in FILES:
    p = os.path.join(BASE, f)
    if os.path.exists(p):
        d = json.load(open(p, encoding='utf-8'))
        print(f, '=>', d.get('values'))
    else:
        print(f, '=> 不存在')
