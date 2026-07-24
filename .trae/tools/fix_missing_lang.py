#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""自动补全 zh_cn.json 中缺失的 item 翻译键。

策略：
1. 如果存在对应的 block.pasterdream.<name>，直接复用 block 翻译。
2. 如果不存在，使用预定义映射表；映射表中没有的，输出占位符供人工检查。
"""

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent.parent
LANG_FILE = ROOT / "PasterDream/src/main/resources/assets/pasterdream/lang/zh_cn.json"

# 预定义翻译：没有 block 翻译的纯物品
CUSTOM_TRANSLATIONS = {
    "aaroncos_hand_chest": "亚伦柯斯之触战利品箱",
    "aaroncoshandspawnblock": "亚伦柯斯之眼",
    "chiseled_shadow_stone_brick": "雕凿阴影石砖",
    "cracked_shadow_stone_brick": "裂纹阴影石砖",
    "loose_shadow_dungeon_block": "松动暗影地牢砖",
    "shadow_arena_block_0": "暗影竞技场地砖",
    "shadow_blast_furnace_core": "暗影高炉核心",
    "shadow_block": "阴影方块",
    "shadow_dungeon_block_0": "暗影地牢砖",
    "shadow_dungeon_block_1": "暗影地牢顶砖",
    "shadow_dungeon_block_2": "暗影地牢花纹砖",
    "shadow_dungeon_block_3": "暗影地牢雕刻砖",
    "shadow_dungeon_block_4": "暗影地牢发光砖",
    "shadow_dungeon_block_5": "暗影地牢砖楼梯",
    "shadow_dungeon_block_6": "暗影地牢砖台阶",
    "shadow_dungeon_door_0": "暗影地牢薄板门",
    "shadow_dungeon_door_1": "暗影地牢薄板门·静",
    "shadow_dungeon_key_0": "暗影地牢墙钥",
    "shadow_dungeon_key_1": "暗影地牢地钥",
    "shadow_fissure_0": "暗影裂隙",
    "shadow_fissure_1": "暗影裂隙·透",
    "shadow_fissure_2": "暗影裂隙·明",
    "shadow_fissure_3": "暗影裂隙·明透",
    "shadow_fissure_4": "暗影裂隙·辉",
    "shadow_fissure_5": "暗影裂隙·辉透",
    "shadow_hyphae": "阴影菌核",
    "shadow_nylium": "阴影菌丝",
    "shadow_planks": "阴影木板",
    "shadow_shroomlight": "阴影菌光体",
    "shadow_stem": "阴影菌柄",
    "shadow_stone": "阴影石",
    "shadow_stone_brick": "阴影石砖",
    "shadow_stone_bricks": "阴影石砖（旧）",
    "shadow_stone_tiles": "阴影石瓦",
    "shadow_wart_block": "阴影疣块",
    "shadowcandle": "暗影蜡烛",
    "shadowdungeondoor_2": "暗影地牢门 II",
    "shadowdungeondoor_3": "暗影地牢门 III",
    "shadowshelf_0": "暗影书架",
    "shadowshelf_1": "暗影书架·暗",
    "shadowshelf_2": "暗影书架·蚀",
    "shadowshelf_3": "暗影书架·烬",
    "stripped_shadow_hyphae": "去皮阴影菌核",
    "stripped_shadow_stem": "去皮阴影菌柄",
    "thick_shadow_block": "致密阴影方块",
}


def load_lang():
    with LANG_FILE.open("r", encoding="utf-8") as f:
        return json.load(f)


def save_lang(data):
    with LANG_FILE.open("w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)
        f.write("\n")


def get_missing_items():
    import check_lang as cl
    item_names = cl.extract_names(cl.PD_ITEMS)
    block_names = cl.extract_names(cl.PD_BLOCKS)
    block_names.update(cl.extract_batch_names(cl.PD_BLOCKS))
    block_names.update(cl.extract_variant_names(cl.PD_BLOCKS))

    lang = load_lang()
    missing = set()
    for name in item_names:
        if f"item.pasterdream.{name}" not in lang:
            missing.add(name)
    for name in block_names:
        if f"item.pasterdream.{name}" not in lang:
            missing.add(name)
    return missing, block_names


def translate_name(name, lang):
    block_key = f"block.pasterdream.{name}"
    if block_key in lang:
        return lang[block_key]
    if name in CUSTOM_TRANSLATIONS:
        return CUSTOM_TRANSLATIONS[name]
    return None


def main():
    lang = load_lang()
    missing, block_names = get_missing_items()

    added = []
    unresolved = []
    for name in sorted(missing):
        translation = translate_name(name, lang)
        if translation is None:
            unresolved.append(name)
            continue
        lang[f"item.pasterdream.{name}"] = translation
        added.append((name, translation))

    save_lang(lang)

    print(f"已补充 {len(added)} 个 item 翻译键")
    if unresolved:
        print(f"\n仍有 {len(unresolved)} 个无法自动翻译：")
        for name in unresolved:
            print(f"  - item.pasterdream.{name}")
    return 1 if unresolved else 0


if __name__ == "__main__":
    raise SystemExit(main())
