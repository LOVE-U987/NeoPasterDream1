"""检查染梦地下笋、云团形状和冰柱群系的资源约束。"""

import json
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
DATA = ROOT / "PasterDream/src/main/resources/data/pasterdream"


def load(relative):
    """读取一个世界生成资源。"""
    return json.loads((DATA / relative).read_text(encoding="utf-8"))


def check():
    """断言关键资源行为，配置回退时以非零退出。"""
    for number in (5, 6):
        patch = load(f"worldgen/configured_feature/grass_{number}.json")["config"]
        inner = patch["feature"]["feature"]
        assert inner["type"] == "pasterdream:cave_calcite_spike"
        assert inner["config"]["to_place"]["state"]["Name"] == f"pasterdream:grass_{number}"
        assert inner["config"]["floor_search_range"] == 16
        assert inner["config"]["ceiling_search_range"] == 32
        placement = load(f"worldgen/placed_feature/grass_{number}.json")["placement"]
        types = [p["type"] for p in placement]
        assert "minecraft:heightmap" not in types
        assert "minecraft:height_range" in types and types[-1] == "minecraft:biome"
        assert next(p["count"] for p in placement if p["type"] == "minecraft:count") == 2
        assert next(p["chance"] for p in placement if p["type"] == "minecraft:rarity_filter") == 10
    spikes = {"pasterdream:grass_5", "pasterdream:grass_6"}
    cave = load("neoforge/biome_modifier/dyedream_cave_calcite_spikes.json")
    assert set(cave["features"]) == spikes and cave["step"] == "underground_decoration"
    for path in (DATA / "neoforge/biome_modifier").glob("*.json"):
        modifier = json.loads(path.read_text(encoding="utf-8"))
        features = modifier.get("features", [])
        features = [features] if isinstance(features, str) else features
        if spikes.intersection(features):
            assert modifier["step"] == "underground_decoration", path
    for number in (2, 4):
        cloud = load(f"worldgen/configured_feature/ground_feature_dyedream_{number}.json")["config"]
        assert cloud["y_spread"] <= 3 < cloud["xz_spread"]
        assert cloud["tries"] <= 96
        placed = load(f"worldgen/placed_feature/ground_feature_dyedream_{number}.json")
        assert placed["placement"][-1]["type"] == "minecraft:biome"
    ice = {"pasterdream:ground_feature_dyedream_0", "pasterdream:ground_feature_dyedream_3"}
    for path in (DATA / "neoforge/biome_modifier").glob("*.json"):
        modifier = json.loads(path.read_text(encoding="utf-8"))
        features = modifier.get("features", [])
        features = [features] if isinstance(features, str) else features
        if ice.intersection(features):
            assert modifier["biomes"] == "pasterdream:dyedream_frozen_tundra", path
            assert modifier["step"] == "surface_structures", path
    cold = load("neoforge/biome_modifier/dyedream_surface_ice.json")
    assert set(cold["features"]) == ice
    print("PASS: underground calcite, compact clouds, cold-only ice columns")


if __name__ == "__main__":
    check()
