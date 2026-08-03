#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""PasterDream 静态资源闭包验证器。

验证注册项与客户端/数据包资源之间的引用闭包，作为运行时 PDPortingVerifyTest
的静态补充。输出人类可读摘要，并写入 scratchpad/pd_resource_verify_report.json。
"""

from __future__ import annotations

import argparse
import json
import re
import sys
from collections import defaultdict
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
MOD = ROOT / "PasterDream"
RES = MOD / "src/main/resources"
ASSETS = RES / "assets/pasterdream"
DATA = RES / "data/pasterdream"
JAVA = MOD / "src/main/java/com/pasterdream/pasterdreammod"
MANIFEST = RES / "pd_porting_manifest.json"
REPORT_DEFAULT = ROOT / "scratchpad/pd_resource_verify_report.json"

REGISTER_PATTERNS = (
    # 要求字面量后紧跟 , 或 )，避免把 "dreamnotes_" + i 当成完整注册名
    re.compile(r'\bregister(?:Block|Item)?\s*\(\s*"([a-z0-9_]+)"\s*[,)]'),
    re.compile(r'\bregisterSimple(?:Item|BlockItem)\s*\(\s*"([a-z0-9_]+)"\s*[,)]'),
    re.compile(r'\bregisterCustom\s*\(\s*"([a-z0-9_]+)"\s*[,)]'),
)


def load_json(path: Path):
    with path.open("r", encoding="utf-8-sig") as handle:
        return json.load(handle)


def add(failures, category: str, source: Path | str, target: str, detail: str = ""):
    src = str(source.relative_to(ROOT)) if isinstance(source, Path) and source.is_relative_to(ROOT) else str(source)
    failures[category].append({"source": src, "target": target, "detail": detail})


def namespace_path(value: str, default_ns: str = "pasterdream") -> tuple[str, str]:
    if ":" in value:
        return tuple(value.split(":", 1))
    return default_ns, value


def local_model_path(value: str) -> Path | None:
    # 模型 parent 未带命名空间时由 Minecraft 按 minecraft: 解析；只有显式
    # pasterdream: 引用才落到本模组 models 目录。
    ns, path = namespace_path(value, default_ns="minecraft")
    if ns != "pasterdream":
        return None
    return ASSETS / "models" / f"{path}.json"


def local_texture_path(value: str, particle: bool = False) -> Path | None:
    # 模型纹理同样是未命名空间默认 minecraft:；粒子 JSON 通常显式写命名空间。
    ns, path = namespace_path(value, default_ns="minecraft")
    if ns != "pasterdream":
        return None
    if particle and "/" not in path:
        path = "particle/" + path
    return ASSETS / "textures" / f"{path}.png"


def iter_json():
    yield from RES.rglob("*.json")
    for pack in (RES / "packs").glob("*") if (RES / "packs").exists() else ():
        yield from pack.rglob("*.json")


def registered_names(files) -> set[str]:
    names: set[str] = set()
    for path in files:
        if not path.exists():
            continue
        text = path.read_text(encoding="utf-8")
        for pattern in REGISTER_PATTERNS:
            names.update(pattern.findall(text))
        for match in re.finditer(r'BlockAPI\.batchRegister\s*\(\s*"([a-z0-9_]+)"\s*\)(.*?)\.build\s*\(\s*\)', text, re.S):
            base, body = match.groups()
            ranges = re.findall(r'\.range\s*\(\s*(\d+)\s*,\s*(\d+)\s*\)', body)
            for lo, hi in ranges:
                names.update(f"{base}_{i}" for i in range(int(lo), int(hi) + 1))
    return names


def scan_json_and_bom(failures):
    count = 0
    for path in iter_json():
        count += 1
        raw = path.read_bytes()
        if raw.startswith(b"\xef\xbb\xbf"):
            add(failures, "json_bom", path, str(path), "UTF-8 BOM")
        try:
            json.loads(raw.decode("utf-8-sig"))
        except Exception as exc:
            add(failures, "json_parse", path, str(path), str(exc))
    return count


def scan_model(model: Path, failures, visiting: set[Path], checked: set[Path]):
    if model in checked or model in visiting:
        return
    visiting.add(model)
    try:
        obj = load_json(model)
    except Exception:
        visiting.remove(model)
        return
    parent = obj.get("parent")
    if isinstance(parent, str):
        target = local_model_path(parent)
        if target is not None:
            if not target.exists():
                add(failures, "model_parent", model, parent)
            else:
                scan_model(target, failures, visiting, checked)
    textures = obj.get("textures", {})
    if isinstance(textures, dict):
        for key, value in textures.items():
            if not isinstance(value, str) or value.startswith("#"):
                continue
            target = local_texture_path(value)
            if target is not None and not target.exists():
                add(failures, "model_texture", model, value, f"texture key {key}")
    visiting.remove(model)
    checked.add(model)


def scan_models_and_blockstates(failures):
    checked: set[Path] = set()
    for model in (ASSETS / "models").rglob("*.json"):
        scan_model(model, failures, set(), checked)

    for state in (ASSETS / "blockstates").glob("*.json"):
        try:
            obj = load_json(state)
        except Exception:
            continue
        refs: list[str] = []
        def walk(value):
            if isinstance(value, dict):
                for k, v in value.items():
                    if k == "model" and isinstance(v, str):
                        refs.append(v)
                    else:
                        walk(v)
            elif isinstance(value, list):
                for v in value:
                    walk(v)
        walk(obj)
        for ref in refs:
            target = local_model_path(ref)
            if target is not None and not target.exists():
                add(failures, "blockstate_model", state, ref)


def scan_particles(failures):
    for path in (ASSETS / "particles").glob("*.json"):
        try:
            obj = load_json(path)
        except Exception:
            continue
        for texture in obj.get("textures", []):
            if not isinstance(texture, str):
                continue
            target = local_texture_path(texture, particle=True)
            if target is not None and not target.exists():
                add(failures, "particle_texture", path, texture)


def scan_sounds(failures):
    path = ASSETS / "sounds.json"
    if not path.exists():
        add(failures, "sound_definition", path, "sounds.json")
        return
    try:
        sounds = load_json(path)
    except Exception:
        return
    for event, obj in sounds.items():
        for value in obj.get("sounds", []):
            name = value.get("name") if isinstance(value, dict) else value
            if not isinstance(name, str):
                continue
            ns, target = namespace_path(name)
            if ns == "pasterdream" and not (ASSETS / "sounds" / f"{target}.ogg").exists():
                add(failures, "sound_ogg", path, name, f"event {event}")


def scan_registered_resources(failures):
    block_files = list((JAVA / "registry/blocks").glob("PDBlocks*.java")) + [JAVA / "registry/PDBlocks.java"]
    item_files = list((JAVA / "registry/items").glob("PDItems*.java")) + [JAVA / "registry/PDItems.java"]
    blocks = registered_names(block_files)
    items = registered_names(item_files)
    manifest = load_json(MANIFEST)
    renames = manifest.get("renames", {})
    excluded = manifest.get("excluded", {})
    expected_blocks = {renames.get(n, n) for n in manifest.get("blocks", [])} - set(excluded.get("blocks", []))

    for name in sorted(blocks & expected_blocks):
        state = ASSETS / "blockstates" / f"{name}.json"
        if not state.exists():
            add(failures, "registered_blockstate", "registry", name)
        item_model = ASSETS / "models/item" / f"{name}.json"
        if name in items and not item_model.exists():
            add(failures, "registered_block_item_model", "registry", name)

    for name in sorted(items - blocks):
        model = ASSETS / "models/item" / f"{name}.json"
        if not model.exists():
            add(failures, "registered_item_model", "registry", name)

    return len(blocks), len(items)


def scan_original_loot_parity(failures):
    original = ROOT / "libs/FixPasterDream-main/src/main/resources/data/pasterdream/loot_tables/blocks"
    current = DATA / "loot_table/blocks"
    if not original.exists():
        return
    for path in original.glob("*.json"):
        if not (current / path.name).exists():
            add(failures, "original_loot_missing", path, path.stem)


def scan_literal_resource_locations(failures):
    # 只处理 Java 中可静态证明的完整字面路径；动态拼接由运行时资源加载检查覆盖。
    pattern = re.compile(r'ResourceLocation\.(?:fromNamespaceAndPath|parse)\(\s*(?:PasterDreamMod\.MOD_ID\s*,\s*)?"((?:geo|animations|textures)/[a-z0-9_./-]+)"\s*\)')
    suffix = {"geo": "", "animations": "", "textures": ""}
    for java in JAVA.rglob("*.java"):
        text = java.read_text(encoding="utf-8")
        for ref in pattern.findall(text):
            target = ASSETS / ref
            if not target.exists():
                add(failures, "java_resource_literal", java, ref)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--report", type=Path, default=REPORT_DEFAULT)
    parser.add_argument("--allow-original-loot-gap", action="store_true")
    args = parser.parse_args()

    failures: dict[str, list[dict]] = defaultdict(list)
    json_count = scan_json_and_bom(failures)
    scan_models_and_blockstates(failures)
    scan_particles(failures)
    scan_sounds(failures)
    blocks, items = scan_registered_resources(failures)
    scan_original_loot_parity(failures)
    scan_literal_resource_locations(failures)

    if args.allow_original_loot_gap:
        failures.pop("original_loot_missing", None)

    total = sum(len(v) for v in failures.values())
    report = {
        "generated_by": "tools/verify_resource_closure.py",
        "json_files": json_count,
        "registered_blocks_scanned": blocks,
        "registered_items_scanned": items,
        "failure_count": total,
        "failures": dict(sorted(failures.items())),
    }
    args.report.parent.mkdir(parents=True, exist_ok=True)
    args.report.write_text(json.dumps(report, ensure_ascii=False, indent=2), encoding="utf-8")

    print("=" * 64)
    print("PasterDream 资源闭包验证")
    print("=" * 64)
    print(f"JSON: {json_count} | 注册方块扫描: {blocks} | 注册物品扫描: {items}")
    if total:
        print(f"FAIL: {total} 个闭包错误（{len(failures)} 类）")
        for category, rows in sorted(failures.items()):
            print(f"\n[{category}] {len(rows)}")
            for row in rows[:20]:
                detail = f" ({row['detail']})" if row["detail"] else ""
                print(f"  {row['source']} -> {row['target']}{detail}")
            if len(rows) > 20:
                print(f"  ... 另 {len(rows) - 20} 项，见报告")
    else:
        print("PASS: JSON/模型/纹理/粒子/音效/注册资源/loot 闭包全部完整")
    print(f"报告: {args.report}")
    return 1 if total else 0


if __name__ == "__main__":
    raise SystemExit(main())
