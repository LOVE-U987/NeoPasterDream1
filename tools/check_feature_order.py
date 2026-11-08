# 原 mod 的生物群系修饰器按注册 ID 字母序应用；同一 decoration step 内跨群系共享的
# placed_feature 相对顺序必须全局一致，否则原版 FeatureSorter 在区块生成时抛出
# "Feature order cycle found" 并导致创建世界卡死/崩溃（见 B2 热修，2026-11-07）。
# 本脚本静态模拟应用顺序并检测排序环，供 biome_modifier / worldgen 挂载类改动必跑。
#
# 用法（项目根目录）:
#   python tools/check_feature_order.py
# 退出码: 0 = 无环; 1 = 检出排序环或读取失败
#
# 已知局限:
# - 原版群系的静态 features 数组不在项目资源内，按空列表处理（不影响本项目引入的环检测）;
# - 跨维度群系对被合并到同一张图，理论上可能产生永不同图的群系间误报;
# - "#minecraft:*" 群系标签无法在项目内展开，跳过并提示。
# - 模拟按注册 ID 字母序应用 add_features，与 NeoForge 数据包注册加载顺序一致。

import glob
import json
import os
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATA = os.path.join(ROOT, "PasterDream", "src", "main", "resources", "data", "pasterdream")

STEPS = [
    "raw_generation", "lakes", "local_modifications", "underground_structures",
    "surface_structures", "strongholds", "underground_ores", "underground_decoration",
    "fluid_springs", "vegetal_decoration", "top_layer_modification",
]


def load_tags():
    """读取 tags/worldgen/biome/*.json，返回 {tag_name: set(biome_name)}。"""
    tags = {}
    tag_dir = os.path.join(DATA, "tags", "worldgen", "biome")
    for f in glob.glob(os.path.join(tag_dir, "*.json")):
        name = os.path.basename(f)[:-5]
        try:
            d = json.load(open(f, encoding="utf-8"))
        except (OSError, ValueError) as e:
            print("WARN: 标签解析失败 %s: %s" % (f, e))
            continue
        values = set()
        for v in d.get("values", []):
            if isinstance(v, str):
                values.add(v)
            elif isinstance(v, dict) and isinstance(v.get("id"), str):
                values.add(v["id"])
        tags[name] = values
    return tags


def load_static_biomes():
    """读取 worldgen/biome/*.json 静态 features 数组（1.21 格式: 11 个按 step 序排列的数组）。"""
    biomes = {}
    biome_dir = os.path.join(DATA, "worldgen", "biome")
    for f in glob.glob(os.path.join(biome_dir, "*.json")):
        name = os.path.basename(f)[:-5]
        try:
            d = json.load(open(f, encoding="utf-8"))
        except (OSError, ValueError) as e:
            print("WARN: 群系解析失败 %s: %s" % (f, e))
            continue
        eff = {s: [] for s in STEPS}
        for i, step_list in enumerate(d.get("features", [])):
            if i < len(STEPS) and isinstance(step_list, list):
                eff[STEPS[i]] = [
                    x.split(":")[-1] for x in step_list
                    if isinstance(x, str) and ":" in x
                ]
        biomes[name] = eff
    return biomes


def resolve_biome_targets(entry, tags):
    """展开 biome_modifier 的 biomes 字段为 pasterdream 群系名集合。"""
    entries = entry if isinstance(entry, list) else [entry]
    out = set()
    for b in entries:
        if not isinstance(b, str):
            continue
        if b.startswith("#"):
            tag_id = b[1:]
            ns, _, tag_name = tag_id.partition(":")
            if ns != "pasterdream":
                print("WARN: 跳过非 pasterdream 标签 %s（无法在项目内展开）" % b)
                continue
            for v in tags.get(tag_name, ()):
                if v.startswith("pasterdream:"):
                    out.add(v.split(":", 1)[1])
        elif b.startswith("pasterdream:"):
            out.add(b.split(":", 1)[1])
        # minecraft:* 群系无静态数据，跳过（其静态 features 由原版提供）
    return out


def apply_modifiers(biomes, tags):
    """按注册 ID 字母序模拟 neoforge:add_features 的应用。"""
    mods = []
    mod_dir = os.path.join(DATA, "neoforge", "biome_modifier")
    for f in glob.glob(os.path.join(mod_dir, "*.json")):
        rid = "pasterdream:" + os.path.basename(f)[:-5]
        try:
            d = json.load(open(f, encoding="utf-8"))
        except (OSError, ValueError) as e:
            print("WARN: biome_modifier 解析失败 %s: %s" % (f, e))
            continue
        if d.get("type") != "neoforge:add_features":
            continue
        mods.append((rid, d))
    mods.sort(key=lambda x: x[0])
    for rid, d in mods:
        step = d.get("step")
        if step not in STEPS:
            print("WARN: %s 使用未知 step %r" % (rid, step))
            continue
        feats = d.get("features", [])
        feats = feats if isinstance(feats, list) else [feats]
        feats = [x.split(":")[-1] for x in feats if isinstance(x, str) and ":" in x]
        for t in resolve_biome_targets(d.get("biomes"), tags):
            if t in biomes:
                biomes[t][step].extend(feats)


def find_cycles(biomes):
    """对每个 step 跨全部项目群系建排序图（相邻特征对为边），DFS 检测环。"""
    cycles = []
    for step in STEPS:
        adj = {}
        for lst in biomes.values():
            for i in range(len(lst[step]) - 1):
                adj.setdefault(lst[step][i], set()).add(lst[step][i + 1])
        color, stack = {}, []

        def dfs(u):
            color[u] = 1
            stack.append(u)
            for v in adj.get(u, ()):
                if color.get(v, 0) == 1:
                    cycles.append((step, stack[stack.index(v):] + [v]))
                elif color.get(v, 0) == 0:
                    dfs(v)
            stack.pop()
            color[u] = 2

        for u in list(adj):
            if color.get(u, 0) == 0:
                dfs(u)
    return cycles


def main():
    tags = load_tags()
    biomes = load_static_biomes()
    if not biomes:
        print("ERROR: 未读取到任何群系文件，检查路径 %s" % DATA)
        return 1
    apply_modifiers(biomes, tags)
    cycles = find_cycles(biomes)
    if not cycles:
        print("OK: 全部 %d 个 step 无 feature order cycle（%d 个群系）" % (len(STEPS), len(biomes)))
        return 0
    print("FAIL: 检出 %d 个排序环，区块生成将抛出 Feature order cycle:" % len(cycles))
    for step, cyc in cycles:
        print("  step=%s cycle: %s" % (step, " -> ".join(cyc)))
    print("修复方向: 调整 biome_modifier 文件名（注册 ID）字母序，或拆分/合并挂载，使共享特征相对顺序全局一致。")
    return 1


if __name__ == "__main__":
    sys.exit(main())
