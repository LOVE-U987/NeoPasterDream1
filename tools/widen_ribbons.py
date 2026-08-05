#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
光带加宽脚本
============
将各 skybox 中 ribbons 的 thickness 翻倍（光带更宽更明显），
仅修改 thickness 字段，不重写整个 JSON（尊重手动改动）。
运行: python tools/widen_ribbons.py
"""

import glob
import json
import os

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SKYBOX_DIR = os.path.join(ROOT, "PasterDream", "src", "main", "resources",
                          "data", "pasterdream", "skyboxes")


def main():
    for f in sorted(glob.glob(os.path.join(SKYBOX_DIR, "*.json"))):
        with open(f, encoding="utf-8") as fh:
            data = json.load(fh)
        changed = False
        for layer in data.get("layers", []):
            if layer.get("type") == "pasterdream:ribbons":
                old = layer.get("thickness", 0.1)
                layer["thickness"] = round(old * 2.0, 3)   # 翻倍加宽
                changed = True
        if changed:
            with open(f, "w", encoding="utf-8") as fh:
                json.dump(data, fh, ensure_ascii=False, indent=2)
                fh.write("\n")
            print(f"  {os.path.basename(f)}: ribbons thickness 已加宽")
    print("完成!")


if __name__ == "__main__":
    main()
