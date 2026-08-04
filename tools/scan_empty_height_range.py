# -*- coding: utf-8 -*-
"""扫描 structure JSON 的 start_height 配置，找出 min_inclusive > max_inclusive 的空高度范围问题。"""
import json
import os
import glob

ROOT = r"c:\Users\97128\Documents\GitHub\NeoPasterDream1"
STRUCT_DIRS = [
    os.path.join(ROOT, "PasterDream", "src", "main", "resources", "data", "pasterdream", "worldgen", "structure"),
    os.path.join(ROOT, "PasterDreamSanity", "src", "main", "resources", "data", "pasterdreamsanity", "worldgen", "structure"),
    os.path.join(ROOT, "PasterDreamSpells", "src", "main", "resources", "data", "pasterdreamspells", "worldgen", "structure"),
]

def resolve(value):
    """将 height provider 值解析为数值，返回 (value, kind)。"""
    if isinstance(value, dict):
        kind = value.get("type", "absolute")
        v = value.get("value", value.get(kind, 0))
        return v, kind
    return value, "int"

def scan_height(file, h, label="start_height"):
    if not isinstance(h, dict):
        return
    htype = h.get("type", "")
    if "biased" not in htype:
        return
    mi = h.get("min_inclusive")
    ma = h.get("max_inclusive")
    if mi is None or ma is None:
        return
    mi_v, mi_k = resolve(mi)
    ma_v, ma_k = resolve(ma)
    # absolute 与 int 都是绝对数值，可比较；above_bottom 依赖 world，跳过
    if mi_k in ("absolute", "int") and ma_k in ("absolute", "int"):
        if mi_v > ma_v:
            inner = h.get("inner", 0)
            print(f"[INVALID] {os.path.basename(file)} [{label}] {htype}: min={mi_v}({mi_k}) max={ma_v}({ma_k}) inner={inner}  => 空范围")

def scan_file(file):
    try:
        with open(file, "r", encoding="utf-8") as f:
            data = json.load(f)
    except Exception as e:
        print(f"[ERROR] {os.path.basename(file)}: {e}")
        return
    sh = data.get("start_height")
    if sh is not None:
        scan_height(file, sh, "start_height")
    for k in ("start_peak", "start_perimeter", "start_height_inclusive"):
        v = data.get(k)
        if v is not None:
            scan_height(file, v, k)
    # configured_feature 里也可能有 height_range / height 配置
    cf = data.get("config")
    if isinstance(cf, dict):
        for k in ("height", "height_range", "min_inclusive", "max_inclusive"):
            v = cf.get(k)
            if isinstance(v, dict) and "biased" in str(v.get("type", "")):
                scan_height(file, v, f"config.{k}")

def main():
    files = []
    for d in STRUCT_DIRS:
        if os.path.isdir(d):
            files += glob.glob(os.path.join(d, "**", "*.json"), recursive=True)
    if not files:
        print("未找到 structure JSON 目录，尝试全库扫描...")
        files = glob.glob(os.path.join(ROOT, "**", "worldgen", "structure", "*.json"), recursive=True)
    print(f"扫描 {len(files)} 个 structure JSON...")
    for f in sorted(files):
        scan_file(f)
    # 也扫描 configured_feature 的 biased_to_bottom
    cf_dirs = glob.glob(os.path.join(ROOT, "**", "worldgen", "configured_feature", "*.json"), recursive=True)
    print(f"扫描 {len(cf_dirs)} 个 configured_feature JSON...")
    for f in sorted(cf_dirs):
        scan_file(f)
    # 扫描 placed_feature 的 placement 中 height_range
    pf_dirs = glob.glob(os.path.join(ROOT, "**", "worldgen", "placed_feature", "*.json"), recursive=True)
    print(f"扫描 {len(pf_dirs)} 个 placed_feature JSON...")
    for f in sorted(pf_dirs):
        try:
            with open(f, "r", encoding="utf-8") as fp:
                data = json.load(fp)
        except Exception:
            continue
        for p in data.get("placement", []):
            if isinstance(p, dict) and p.get("type") == "minecraft:height_range":
                h = p.get("height")
                if isinstance(h, dict):
                    scan_height(f, h, "placement.height_range")
    print("完成。")

if __name__ == "__main__":
    main()
