#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
行星/卫星 opacity 提升脚本（对齐 Stellara 基准）
=============================================
Stellara 行星 opacity 0.62~0.96（主行星≥0.9），我们的 0.56~0.86 偏低，
标准混合下顶点 alpha = alpha×opacity 导致行星偏透明。
修复：行星 opacity 提升到 0.9~0.97（主行星 0.95+），卫星 0.9+。
仅修改 opacity 字段，不重写整个 JSON。
运行: python tools/boost_planet_opacity.py
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
        changed = 0
        for layer in data.get("layers", []):
            if layer.get("type") != "pasterdream:planet_system":
                continue
            for i, p in enumerate(layer.get("planets", [])):
                # 主行星(前2颗) opacity ≥0.95，其余 ≥0.9
                target = 0.95 if i < 2 else 0.9
                old = p.get("opacity", 1.0)
                if old < target:
                    p["opacity"] = target
                    changed += 1
                for s in p.get("satellites", []):
                    old_s = s.get("opacity", 1.0)
                    if old_s < 0.9:
                        s["opacity"] = 0.9
                        changed += 1
        if changed:
            with open(f, "w", encoding="utf-8") as fh:
                json.dump(data, fh, ensure_ascii=False, indent=2)
                fh.write("\n")
            print(f"  {os.path.basename(f)}: 更新 {changed} 处 opacity")
    print("完成!")


if __name__ == "__main__":
    main()
