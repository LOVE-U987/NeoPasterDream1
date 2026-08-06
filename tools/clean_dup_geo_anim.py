#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""清理 geo/animations 根目录冗余副本：删除与子目录内容相同的文件。

安全策略：
1. 仅处理根目录 `geo/*.geo.json` / `animations/*.animation.json`
2. 仅当子目录（block/entity/item）存在同名文件且 MD5 完全一致时才删除
3. 仅当根目录文件未被任何 Java / JSON 显式引用时才删除
4. 打印每个删除动作与统计
"""

from pathlib import Path

ASSETS = Path(__file__).resolve().parents[1] / "PasterDream/src/main/resources/assets/pasterdream"
JAVA = Path(__file__).resolve().parents[1] / "PasterDream/src/main/java/com/pasterdream/pasterdreammod"

import hashlib

def md5(p: Path) -> str:
    return hashlib.md5(p.read_bytes()).hexdigest()

def main() -> int:
    all_java = "\n".join(j.read_text(encoding="utf-8", errors="ignore") for j in JAVA.rglob("*.java"))
    all_json = "\n".join(f.read_text(encoding="utf-8", errors="ignore") for f in ASSETS.rglob("*.json"))

    deleted = 0
    skipped_ref = 0
    skipped_diff = 0

    for sub in ("geo", "animations"):
        for f in sorted((ASSETS / sub).glob("*")):
            if not f.is_file():
                continue
            rel = f"{sub}/{f.name}"
            # 找同名子目录文件
            subs = [
                f"{sub}/block/{f.name}",
                f"{sub}/entity/{f.name}",
                f"{sub}/item/{f.name}",
            ]
            subs = [s for s in subs if (ASSETS / s).exists()]
            if not subs:
                continue
            # 内容全部一致
            if not all(md5(f) == md5(ASSETS / s) for s in subs):
                skipped_diff += 1
                continue
            # 根目录未被引用
            if ('"' + rel + '"') in all_java or ('"' + rel) in all_java or rel in all_json:
                skipped_ref += 1
                continue
            f.unlink()
            deleted += 1
            print(f"  DEL {rel}")

    print(f"\n===== 清理完成 =====")
    print(f"删除: {deleted}")
    print(f"跳过(被引用): {skipped_ref}")
    print(f"跳过(子目录内容不同): {skipped_diff}")
    return 0

if __name__ == "__main__":
    raise SystemExit(main())
