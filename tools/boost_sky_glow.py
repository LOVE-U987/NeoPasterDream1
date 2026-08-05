#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
极光/光带 opacity 提升脚本
============================
加法混合下低 opacity 内容叠加到暗天空仍偏暗，提升 opacity 保证亮度：
- aurora : dense 0.05→0.3, mushroom 0.1→0.35, frozen 0.75→0.55
- ribbons: 全部 →0.45
仅修改 opacity 字段，不重写整个 JSON（尊重手动改动）。
运行: python tools/boost_sky_glow.py
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
        changed = []
        for layer in data.get("layers", []):
            t = layer.get("type", "")
            if t == "pasterdream:aurora":
                old = layer.get("opacity", 0.1)
                new = min(0.55, old * 4.0)   # 提升 4 倍，上限 0.55 防钳白
                layer["opacity"] = round(new, 3)
                changed.append(f"aurora {old}->{new:.2f}")
            elif t == "pasterdream:ribbons":
                old = layer.get("opacity", 0.2)
                new = min(0.45, old * 2.5)   # 提升 2.5 倍，上限 0.45
                layer["opacity"] = round(new, 3)
                changed.append(f"ribbons {old}->{new:.2f}")
        if changed:
            with open(f, "w", encoding="utf-8") as fh:
                json.dump(data, fh, ensure_ascii=False, indent=2)
                fh.write("\n")
            print(f"  {os.path.basename(f)}: " + "; ".join(changed))
    print("完成!")


if __name__ == "__main__":
    main()
