#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""将 build/dist 下的打包产物移动到项目根目录的 打包产物/ 文件夹。

供 Gradle 根任务 packageMod / packageAll 在 doLast 阶段调用，
实现「打包完成 → 产物自动归档」的集成。
用法:
    python tools/dist_to_folder.py
    python tools/dist_to_folder.py --source build/dist --target 打包产物
"""

from __future__ import annotations

import argparse
import shutil
import sys
from pathlib import Path

# 脚本位于 <项目根>/tools/ 下：parents[0]=tools, parents[1]=项目根
ROOT = Path(__file__).resolve().parents[1]


def main() -> int:
    parser = argparse.ArgumentParser(description="移动打包产物到指定目录")
    parser.add_argument("--source", type=Path, default=ROOT / "build/dist", help="产物源目录（默认 build/dist）")
    parser.add_argument("--target", type=Path, default=ROOT / "打包产物", help="归档目标目录（默认 打包产物/）")
    args = parser.parse_args()

    source = args.source.resolve()
    target = args.target.resolve()

    if not source.is_dir():
        print(f"[dist_to_folder] 源目录不存在: {source}")
        return 0

    target.mkdir(parents=True, exist_ok=True)
    moved = []
    for jar in sorted(source.glob("*.jar")):
        dest = target / jar.name
        # 目标已存在同名文件时先删除，避免 shutil.move 在 Windows 上报错
        if dest.exists():
            dest.unlink()
        shutil.move(str(jar), str(dest))
        moved.append(dest.name)

    if moved:
        print(f"[dist_to_folder] 已归档 {len(moved)} 个产物: {', '.join(moved)}")
        print(f"[dist_to_folder] 目标目录: {target}")
    else:
        print(f"[dist_to_folder] 未发现 jar 产物（{source}）")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
