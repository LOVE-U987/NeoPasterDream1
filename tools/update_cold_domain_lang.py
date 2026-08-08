# -*- coding: utf-8 -*-
"""为冷域方块与标签页添加中英文语言条目"""
import json
import os

LANG_DIR = r'c:\Users\97128\Documents\GitHub\NeoPasterDream1\PasterDream\src\main\resources\assets\pasterdream\lang'

ZH = {
    'block.pasterdream.cold_domain_dirt': '冷域泥土',
    'block.pasterdream.cold_domain_log': '冷域木头',
    'block.pasterdream.stripped_cold_domain_log': '去皮冷域木头',
    'block.pasterdream.cold_domain_leaves': '冷域树叶',
    'block.pasterdream.snowy_cold_domain_grass': '雪地草坪',
    'block.pasterdream.dyedream_farmland': '染梦耕地',
    'item.pasterdream.cold_domain_dirt': '冷域泥土',
    'item.pasterdream.cold_domain_log': '冷域木头',
    'item.pasterdream.stripped_cold_domain_log': '去皮冷域木头',
    'item.pasterdream.cold_domain_leaves': '冷域树叶',
    'item.pasterdream.snowy_cold_domain_grass': '雪地草坪',
    'item.pasterdream.dyedream_farmland': '染梦耕地',
    'itemGroup.pasterdream.cold_domain_tab': '冷域',
}

EN = {
    'block.pasterdream.cold_domain_dirt': 'Cold Domain Dirt',
    'block.pasterdream.cold_domain_log': 'Cold Domain Log',
    'block.pasterdream.stripped_cold_domain_log': 'Stripped Cold Domain Log',
    'block.pasterdream.cold_domain_leaves': 'Cold Domain Leaves',
    'block.pasterdream.snowy_cold_domain_grass': 'Snowy Cold Domain Grass',
    'block.pasterdream.dyedream_farmland': 'Dyedream Farmland',
    'item.pasterdream.cold_domain_dirt': 'Cold Domain Dirt',
    'item.pasterdream.cold_domain_log': 'Cold Domain Log',
    'item.pasterdream.stripped_cold_domain_log': 'Stripped Cold Domain Log',
    'item.pasterdream.cold_domain_leaves': 'Cold Domain Leaves',
    'item.pasterdream.snowy_cold_domain_grass': 'Snowy Cold Domain Grass',
    'item.pasterdream.dyedream_farmland': 'Dyedream Farmland',
    'itemGroup.pasterdream.cold_domain_tab': 'Cold Domain',
}


def merge(lang_file, entries):
    p = os.path.join(LANG_DIR, lang_file)
    with open(p, 'r', encoding='utf-8') as f:
        data = json.load(f)
    added = []
    for k, v in entries.items():
        if k in data:
            print(f'  [覆盖] {k}: {data[k]} -> {v}')
        else:
            added.append(k)
        data[k] = v
    with open(p, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)
    print(f'{lang_file}: 新增 {len(added)} 条（已存在 {len(entries) - len(added)} 条）')


merge('zh_cn.json', ZH)
merge('en_us.json', EN)
print('语言文件更新完成')
