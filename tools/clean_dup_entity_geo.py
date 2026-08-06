#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""清理 10 个实体 geo 根目录孤儿副本（entity 子目录版本存在且为实际解析路径）。

安全策略：
1. 仅处理这 10 个已知差异文件：ash_bone_wing, bone_wing, crazy_terrorbeak,
   highvoltage_thundercloud, shadow_ghost, shadow_hand, shadow_rune_totem,
   shaking_crystal, terrorbeak, thundercloud
2. 仅当 geo/entity/{name}.geo.json 存在（有效文件）时删除根目录副本
3. 仅当根目录文件未被任何 Java / JSON 显式引用时删除
"""

from pathlib import Path

ASSETS = Path(__file__).resolve().parents[1] / "PasterDream/src/main/resources/assets/pasterdream"
JAVA = Path(__file__).resolve().parents[1] / "PasterDream/src/main/java/com/pasterdream/pasterdreammod"

DIFF_NAMES = [
    "ash_bone_wing", "bone_wing", "crazy_terrorbeak", "highvoltage_thundercloud",
    "shadow_ghost", "shadow_hand", "shadow_rune_totem", "shaking_crystal",
    "terrorbeak", "thundercloud",
]


def main() -> int:
    all_java = "\n".join(j.read_text(encoding="utf-8", errors="ignore") for j in JAVA.rglob("*.java"))
    all_json = "\n".join(f.read_text(encoding="utf-8", errors="ignore") for f in ASSETS.rglob("*.json"))

    deleted = 0
    skipped = 0
    for name in DIFF_NAMES:
        root_p = ASSETS / f"geo/{name}.geo.json"
        entity_p = ASSETS / f"geo/entity/{name}.geo.json"
        if not root_p.exists():
            print(f"  已不存在: geo/{name}.geo.json")
            continue
        if not entity_p.exists():
            print(f"  跳过(entity子目录缺文件): {name}")
            skipped += 1
            continue
        rel = f"geo/{name}.geo.json"
        if ('"' + rel + '"') in all_java or ('"' + rel) in all_java or rel in all_json:
            print(f"  跳过(被引用): {name}")
            skipped += 1
            continue
        root_p.unlink()
        deleted += 1
        print(f"  DEL geo/{name}.geo.json")

    print(f"\n===== 清理完成 =====")
    print(f"删除: {deleted}")
    print(f"跳过: {skipped}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
