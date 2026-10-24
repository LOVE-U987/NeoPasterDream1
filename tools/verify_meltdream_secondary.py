# -*- coding: utf-8 -*-
"""校验融梦水晶箱二级奖励表。

覆盖原版 MeltdreamChestLootsPr0/Pr1 的 4 张表：
  - 染梦稀有：19 权重（水晶 4 + 15 项各 1）
  - 染梦传说：8 项各 1
  - 风旅稀有：8 权重（水晶 4 + 4 项各 1）
  - 风旅传说：3 项各 1
"""
import json
import os
import sys
import io

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8")

REPO_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CHEST_DIR = os.path.join(
    REPO_ROOT, "PasterDream", "src", "main", "resources",
    "data", "pasterdream", "loot_table", "chests")

EXPECTED = {
    "loots_meltdream_chest_secondary_dyedream_rare.json": {
        "pasterdream:meltdream_crystal_0": 4,
        "pasterdream:feather_necklace": 1,
        "pasterdream:cross_necklace": 1,
        "pasterdream:dyedream_upgrade": 1,
        "pasterdream:time_hourglass": 1,
        "pasterdream:meltdream_liquid_bucket": 1,
        "pasterdream:duke_coin_curio": 1,
        "pasterdream:light_butterfly_curio": 1,
        "pasterdream:sculk_upgrade": 1,
        "pasterdream:rage_elixir_0": 1,
        "pasterdream:silver_fox_cotton_candy": 1,
        "pasterdream:snow_vow_head": 1,
        "pasterdream:sweetdream_disc": 1,
        "pasterdream:snowfalldream_disc": 1,
        "pasterdream:counter_ring": 1,
        "pasterdream:silver_bell": 1,
    },
    "loots_meltdream_chest_secondary_dyedream_legendary.json": {
        "pasterdream:ceciliacare_charm": 1,
        "pasterdream:broken_hero_sword": 1,
        "pasterdream:terra_charm": 1,
        "pasterdream:qin_doll_0": 1,
        "pasterdream:allkinds_ring": 1,
        "pasterdream:hiyori_head": 1,
        "pasterdream:boboji_plume": 1,
        "pasterdream:machine_wing": 1,
    },
    "loots_meltdream_chest_secondary_wind_rare.json": {
        "pasterdream:meltdream_crystal_0": 4,
        "pasterdream:wind_journey_disc": 1,
        "pasterdream:calle_card_0": 1,
        "pasterdream:feather_necklace": 1,
        "pasterdream:time_hourglass": 1,
    },
    "loots_meltdream_chest_secondary_wind_legendary.json": {
        "pasterdream:boboji_plume": 1,
        "pasterdream:turnback_cloak": 1,
        "pasterdream:evasion_cloak": 1,
    },
}


def read_weights(path):
    """读取单表并返回 {物品 ID: 权重}；重复条目权重累加。"""
    with open(path, encoding="utf-8") as f:
        data = json.load(f)
    weights = {}
    for pool in data.get("pools", []):
        for entry in pool.get("entries", []):
            if entry.get("type") != "minecraft:item":
                continue
            name = entry.get("name", "")
            weights[name] = weights.get(name, 0) + entry.get("weight", 1)
    return data, weights


def main():
    failed = False
    for filename, expected in EXPECTED.items():
        path = os.path.join(CHEST_DIR, filename)
        if not os.path.isfile(path):
            print("FAIL 缺失文件: %s" % filename)
            failed = True
            continue
        try:
            data, actual = read_weights(path)
        except Exception as exc:  # noqa: BLE001 - 校验脚本需要汇总解析错误
            print("FAIL 解析失败: %s (%s)" % (filename, exc))
            failed = True
            continue

        expected_sequence = "pasterdream:chests/" + filename[:-5]
        if data.get("random_sequence") != expected_sequence:
            print("FAIL random_sequence: %s -> %r" % (filename, data.get("random_sequence")))
            failed = True

        if actual != expected:
            print("FAIL 条目/权重不一致: %s" % filename)
            missing = {k: v for k, v in expected.items() if actual.get(k) != v}
            extra = {k: v for k, v in actual.items() if k not in expected}
            if missing:
                print("  期望缺失或权重不符: %s" % missing)
            if extra:
                print("  多余条目: %s" % extra)
            failed = True
        else:
            print("OK %s (权重总和 %d)" % (filename, sum(actual.values())))

    if failed:
        return 1
    print("OK: 融梦水晶箱二级奖励表全部通过")
    return 0


if __name__ == "__main__":
    sys.exit(main())
