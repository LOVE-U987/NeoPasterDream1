#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""染梦维度岩浆生成链路静态校验器。

对应修复计划 .opencode/plans/2026-09-30-dyedream-lava-restore.md 的 4 个断点，
逐条断言岩浆生成链路已恢复。biome_modifier 不经 DataGen 生成，其加载错误只在
运行时暴露，故本脚本作为 runData 之外的静态兜底。

用法::

    python tools/verify_dyedream_lava.py

全部通过退出码 0，任一失败退出码 1。
"""

from __future__ import annotations

import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "PasterDream/src/main/resources"
DATA = RES / "data/pasterdream"
MIXINS_JSON = RES / "pasterdream.mixins.json"
MIXIN_SOURCE = (
    ROOT
    / "PasterDream/src/main/java/com/pasterdream/pasterdreammod/mixin"
    / "NoiseBasedChunkGeneratorMixin.java"
)

BIOME_MODIFIER = DATA / "neoforge/biome_modifier/dyedream_lava_lake.json"
NOISE_SETTINGS = DATA / "worldgen/noise_settings/dyedream_world.json"
CARVERS = (
    DATA / "worldgen/configured_carver/dyedream_cave.json",
    DATA / "worldgen/configured_carver/dyedream_cave_extra_underground.json",
    DATA / "worldgen/configured_carver/dyedream_canyon.json",
)

LAVA_LAKE_FEATURE = "minecraft:lake_lava_underground"
BIOMES_SELECTOR = "#pasterdream:is_dyedream"
AQUIFER_LAVA_NOISE = "minecraft:aquifer_lava"
MIXIN_NAME = "NoiseBasedChunkGeneratorMixin"


def load_json(path: Path):
    with path.open("r", encoding="utf-8-sig") as handle:
        return json.load(handle)


def check_biome_modifier() -> list[str]:
    """断点 1：地下岩浆湖 feature 挂到 LAKES 步。"""
    errors: list[str] = []
    if not BIOME_MODIFIER.exists():
        return [f"缺少 {BIOME_MODIFIER.relative_to(ROOT)}"]
    try:
        data = load_json(BIOME_MODIFIER)
    except ValueError as exc:
        return [f"JSON 解析失败 {BIOME_MODIFIER.name}: {exc}"]

    if data.get("type") != "neoforge:add_features":
        errors.append(f"type 应为 neoforge:add_features，实际 {data.get('type')!r}")

    biomes = data.get("biomes")
    if isinstance(biomes, str):
        if biomes != BIOMES_SELECTOR:
            errors.append(f"biomes 应为 {BIOMES_SELECTOR}，实际 {biomes!r}")
    elif isinstance(biomes, list):
        if BIOMES_SELECTOR not in biomes and not any(
            isinstance(item, str) and item.startswith("#pasterdream:is_dyedream") for item in biomes
        ):
            errors.append(f"biomes 数组未包含 {BIOMES_SELECTOR}")
    else:
        errors.append(f"biomes 字段缺失或类型异常: {biomes!r}")

    features = data.get("features")
    features = features if isinstance(features, list) else [features]
    if LAVA_LAKE_FEATURE not in features:
        errors.append(f"features 应包含 {LAVA_LAKE_FEATURE}，实际 {features!r}")

    if data.get("step") != "lakes":
        errors.append(f"step 应为 lakes（GenerationStep.Decoration.LAKES），实际 {data.get('step')!r}")

    return errors


def check_noise_router_lava() -> list[str]:
    """断点 2：含水层岩浆噪声恢复为 aquifer_lava。"""
    if not NOISE_SETTINGS.exists():
        return [f"缺少 {NOISE_SETTINGS.relative_to(ROOT)}"]
    try:
        data = load_json(NOISE_SETTINGS)
    except ValueError as exc:
        return [f"JSON 解析失败 {NOISE_SETTINGS.name}: {exc}"]

    lava = (data.get("noise_router") or {}).get("lava")
    if not isinstance(lava, dict):
        return [f"noise_router.lava 应为对象（噪声定义），实际 {lava!r}，岩浆含水层未启用"]

    noise = lava.get("noise")
    if noise != AQUIFER_LAVA_NOISE:
        return [f"noise_router.lava.noise 应为 {AQUIFER_LAVA_NOISE}，实际 {noise!r}"]
    return []


def check_carver_lava_level() -> list[str]:
    """断点 4：carver 洞穴岩浆水位恢复为 above_bottom 8（Y=-56）。"""
    errors: list[str] = []
    for path in CARVERS:
        if not path.exists():
            errors.append(f"缺少 {path.relative_to(ROOT)}")
            continue
        try:
            data = load_json(path)
        except ValueError as exc:
            errors.append(f"JSON 解析失败 {path.name}: {exc}")
            continue

        level = (data.get("config") or {}).get("lava_level")
        if not isinstance(level, dict):
            errors.append(f"{path.name}: config.lava_level 缺失或类型异常 {level!r}")
            continue
        if "above_bottom" not in level:
            errors.append(
                f"{path.name}: lava_level 应含 above_bottom（原版口径），实际 {level!r}，洞穴岩浆被禁用"
            )
        elif "absolute" in level and level.get("absolute", 0) < -1000:
            errors.append(f"{path.name}: lava_level 仍为禁用哨兵值 {level!r}")
    return errors


def check_mixin_removed() -> list[str]:
    """断点 3：底部岩浆层 Mixin 已彻底移除。"""
    errors: list[str] = []
    if MIXIN_SOURCE.exists():
        errors.append(f"Mixin 源文件仍存在: {MIXIN_SOURCE.relative_to(ROOT)}")
    if not MIXINS_JSON.exists():
        errors.append(f"缺少 {MIXINS_JSON.relative_to(ROOT)}")
        return errors
    try:
        data = load_json(MIXINS_JSON)
    except ValueError as exc:
        return [f"JSON 解析失败 {MIXINS_JSON.name}: {exc}"]

    registered = list(data.get("mixins") or []) + list(data.get("client") or []) + list(
        data.get("server") or []
    )
    if MIXIN_NAME in registered:
        errors.append(f"{MIXINS_JSON.name} 仍注册 {MIXIN_NAME}")
    if "StructureTemplateAccessor" not in registered:
        errors.append(f"{MIXINS_JSON.name} 误删 StructureTemplateAccessor")
    return errors


def main() -> int:
    checks = (
        ("断点1 地下岩浆湖 feature", check_biome_modifier),
        ("断点2 含水层岩浆噪声", check_noise_router_lava),
        ("断点3 底部岩浆层 Mixin", check_mixin_removed),
        ("断点4 carver 洞穴岩浆", check_carver_lava_level),
    )

    failed = 0
    for label, func in checks:
        errors = func()
        if errors:
            failed += 1
            print(f"[FAIL] {label}")
            for err in errors:
                print(f"       - {err}")
        else:
            print(f"[PASS] {label}")

    total = len(checks)
    print(f"\n{total - failed}/{total} 通过")
    return 1 if failed else 0


if __name__ == "__main__":
    sys.exit(main())
