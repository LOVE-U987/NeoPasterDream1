# -*- coding: utf-8 -*-
"""批量给染梦宝箱战利品表添加 memento_item_03/08 条目。

在每个宝箱的每个 pool 的 entries 开头插入两个低权重条目(weight 1)，
使"羽星占卜图录"与"星空枕"可通过染梦世界宝箱开出。
已存在的文件跳过，不重复插入。
"""
import json
import os

CHEST_DIR = r"c:\Users\97128\Documents\GitHub\NeoPasterDream1\PasterDream\src\main\resources\data\pasterdream\loot_table\chests"
TARGET_ITEMS = ["pasterdream:memento_item_03", "pasterdream:memento_item_08"]

def main():
    files = [f for f in os.listdir(CHEST_DIR) if f.endswith(".json")]
    changed = 0
    for name in files:
        path = os.path.join(CHEST_DIR, name)
        with open(path, "r", encoding="utf-8") as f:
            data = json.load(f)
        pools = data.get("pools", [])
        if not pools:
            continue
        modified = False
        for pool in pools:
            entries = pool.get("entries", [])
            existing = {e.get("name") for e in entries if e.get("type") == "minecraft:item"}
            to_add = [t for t in TARGET_ITEMS if t not in existing]
            if not to_add:
                continue
            new_entries = []
            for item in to_add:
                new_entries.append({"type": "minecraft:item", "name": item, "weight": 1})
            new_entries.extend(entries)
            pool["entries"] = new_entries
            modified = True
        if modified:
            with open(path, "w", encoding="utf-8") as f:
                json.dump(data, f, ensure_ascii=False, indent=2)
                f.write("\n")
            changed += 1
            print(f"updated: {name}")
    print(f"done, {changed}/{len(files)} files updated")

if __name__ == "__main__":
    main()
