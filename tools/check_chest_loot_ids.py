# -*- coding: utf-8 -*-
"""
校验融梦水晶箱战利品表中所有 pasterdream 物品的注册 ID 是否存在于语言文件中。

覆盖新增的原版战利品表：
  - data/pasterdream/loot_table/chests/loots_meltdream_chest_0.json
  - data/pasterdream/loot_table/chests/loots_meltdream_chest_1.json
"""
import json
import os
import sys
import io

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8")

REPO_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
LANG_PATH = os.path.join(
    REPO_ROOT, "PasterDream", "src", "main", "resources",
    "assets", "pasterdream", "lang", "zh_cn.json")
LOOT_TABLES = [
    os.path.join(REPO_ROOT, "PasterDream", "src", "main", "resources",
                 "data", "pasterdream", "loot_table", "chests", "loots_meltdream_chest_0.json"),
    os.path.join(REPO_ROOT, "PasterDream", "src", "main", "resources",
                 "data", "pasterdream", "loot_table", "chests", "loots_meltdream_chest_1.json"),
]


def collect_pasterdream_ids():
    """从战利品表中收集所有 pasterdream: 命名空间的物品 ID。"""
    ids = set()
    for path in LOOT_TABLES:
        with open(path, encoding="utf-8") as f:
            data = json.load(f)
        for pool in data.get("pools", []):
            for entry in pool.get("entries", []):
                name = entry.get("name", "")
                if name.startswith("pasterdream:"):
                    ids.add(name.split(":", 1)[1])
    return sorted(ids)


def main():
    with open(LANG_PATH, encoding="utf-8") as f:
        lang = json.load(f)

    missing = []
    for item_id in collect_pasterdream_ids():
        item_key = "item.pasterdream.%s" % item_id
        block_key = "block.pasterdream.%s" % item_id
        if item_key not in lang and block_key not in lang:
            missing.append(item_id)

    if missing:
        print("=== 语言文件中缺失的物品键 ===")
        for item_id in missing:
            print("  %s" % item_id)
        return 1
    print("OK: 融梦水晶箱战利品表全部物品 ID 均存在于语言文件中")
    return 0


if __name__ == "__main__":
    sys.exit(main())
