# -*- coding: utf-8 -*-
"""扫描项目内使用数字后缀的注册 ID，生成《ID 迁移计划》文档。

判定口径:
    - 代码侧: 5 个模块 src/main/java/**/registry/** 中的字符串字面量
    - 数据包侧: src/main/resources/data 下的注册目录文件名
    - 命中条件: 名称含 `_<数字>` 段（结尾或后接 `_`，如 `flower_1`、`crack_0_particle`）

用法:
    python tools/scan_numeric_registrations.py

输出:
    docs/设计/ID迁移计划.md
"""
import os
import re
from collections import OrderedDict, defaultdict

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
MODS = [
    "PasterDream",
    "PasterDreamAPI",
    "PasterDreamSpells",
    "PasterDreamSanity",
    "PasterDreamMeltDream",
]
SKIP_DIRS = {
    "libs", "build", ".gradle", ".git", ".idea", ".vscode",
    ".opencode", "deprecated", "__pycache__", "scratchpad", "packs",
}
OUT = os.path.join(ROOT, "docs", "设计", "ID迁移计划.md")

NUM_RE = re.compile(r"_\d+($|_)")
LIT_RE = re.compile(r'"([a-z0-9_]+)"')
REF_MARKERS = ("DebugStructureWandItem", "DebugDecorWandItem", "LootstableCreateItem")
REF_KEEP_PREFIX = ("debug_wand_", "lootstable_create_")


def norm(p):
    """统一路径分隔符。"""
    return p.replace("\\", "/")


def natkey(name):
    """自然排序键: 按字母段 + 数字段排序。"""
    parts = re.split(r"(\d+)", name)
    return [int(p) if p.isdigit() else p for p in parts]


def is_numeric(name):
    """名称是否含 `_<数字>` 段。"""
    return bool(NUM_RE.search(name))


# ==================== 代码侧 ====================

CODE_CATEGORIES = [
    ("方块", lambda r: "/registry/blocks/" in r or r.endswith("registry/PDBlocks.java")),
    ("方块实体", lambda r: os.path.basename(r).startswith("PDBlockEntities")),
    ("物品", lambda r: "/registry/items/" in r),
    ("实体", lambda r: os.path.basename(r) == "PDEntities.java"),
    ("状态效果", lambda r: os.path.basename(r) in (
        "PDEffects.java", "PDSpellsEffects.java", "PDSanityEffects.java",
        "PDMeltDreamEffects.java")),
    ("音效", lambda r: os.path.basename(r) in ("PDSounds.java", "PDSpellsSounds.java")),
    ("粒子", lambda r: os.path.basename(r) in ("PDParticles.java", "PDSpellsParticles.java")),
    ("菜单", lambda r: os.path.basename(r).startswith("PDMenus")),
    ("进度", lambda r: os.path.basename(r) == "PDAdvancements.java"),
    ("结构/遗迹", lambda r: os.path.basename(r) in (
        "PDRuinsRegistration.java", "PDStructurePlacements.java",
        "PDFeatures.java", "PDPlacedFeatures.java", "PDWorldgenRegistries.java",
        "ModDecorations.java", "DyedreamDecorations.java", "IceDecorations.java",
        "OceanDecorations.java")),
]


def collect_code():
    """返回 {分类: {相对路径: [名称...]}}。"""
    result = OrderedDict((c, OrderedDict()) for c, _ in CODE_CATEGORIES)
    for mod in MODS:
        java_root = os.path.join(ROOT, mod, "src", "main", "java")
        if not os.path.isdir(java_root):
            continue
        for dp, dns, fns in os.walk(java_root):
            dns[:] = [d for d in dns if d not in SKIP_DIRS]
            if "registry" not in dp.split(os.sep):
                continue
            for fn in fns:
                if not fn.endswith(".java"):
                    continue
                path = os.path.join(dp, fn)
                rel = norm(os.path.relpath(path, ROOT))
                try:
                    with open(path, encoding="utf-8", errors="replace") as fh:
                        lines = fh.readlines()
                except OSError:
                    continue
                names = set()
                for line in lines:
                    is_ref = any(m in line for m in REF_MARKERS)
                    for lit in LIT_RE.findall(line):
                        if not is_numeric(lit):
                            continue
                        if is_ref and not lit.startswith(REF_KEEP_PREFIX):
                            continue
                        names.add(lit)
                if not names:
                    continue
                for cat, rule in CODE_CATEGORIES:
                    if rule(rel):
                        result[cat].setdefault(rel, set()).update(names)
                        break
    return result


# ==================== 数据包侧 ====================

DATA_SECTIONS = [
    ("群系", "data/pasterdream/worldgen/biome", False),
    ("进度", "data/pasterdream/advancement", False),
    ("结构", "data/pasterdream/worldgen/structure", False),
    ("结构集", "data/pasterdream/worldgen/structure_set", False),
    ("模板池", "data/pasterdream/worldgen/template_pool", False),
    ("配置地物", "data/pasterdream/worldgen/configured_feature", False),
    ("放置地物", "data/pasterdream/worldgen/placed_feature", False),
    ("战利品表", "data/pasterdream/loot_table", True),
    ("配方", "data/pasterdream/recipe", False),
    ("画", "data/pasterdream/painting_variant", False),
]


def collect_data():
    """返回 {分类: [名称...]}。"""
    result = OrderedDict((name, set()) for name, _, _ in DATA_SECTIONS)
    for mod in MODS:
        res_root = os.path.join(ROOT, mod, "src", "main", "resources")
        if not os.path.isdir(res_root):
            continue
        for name, sub, recursive in DATA_SECTIONS:
            target = os.path.join(res_root, *sub.split("/"))
            if not os.path.isdir(target):
                continue
            if recursive:
                for dp, dns, fns in os.walk(target):
                    dns[:] = [d for d in dns if d not in SKIP_DIRS]
                    for fn in fns:
                        stem = os.path.splitext(fn)[0]
                        if is_numeric(stem):
                            result[name].add(stem)
            else:
                for fn in os.listdir(target):
                    if not os.path.isfile(os.path.join(target, fn)):
                        continue
                    stem = os.path.splitext(fn)[0]
                    if is_numeric(stem):
                        result[name].add(stem)
    return {k: sorted(v, key=natkey) for k, v in result.items()}


# ==================== 分类 ====================

def classify(names):
    """按「同族编号 / 裸 _0 / 单编号」分类。"""
    groups = defaultdict(list)
    for n in names:
        m = re.search(r"_\d+$", n)
        if m:
            groups[n[: m.start()]].append(n)
        else:
            # 数字在中间段（如 crack_0_particle）单独归为单编号
            groups[n].append(n)
    same_family, bare_zero, single = [], [], []
    for base, members in groups.items():
        if len(members) >= 2:
            same_family.append((base, sorted(members, key=natkey)))
        elif members[0].endswith("_0"):
            bare_zero.append(members[0])
        else:
            single.append(members[0])
    same_family.sort(key=lambda kv: kv[0])
    bare_zero.sort(key=natkey)
    single.sort(key=natkey)
    return same_family, bare_zero, single


def render_list(names, bullet=True):
    """渲染名称列表。"""
    if bullet:
        return "\n".join("- `%s`" % n for n in names)
    return "```\n" + "\n".join(names) + "\n```"


def build_doc():
    code = collect_code()
    data = collect_data()

    lines = []
    lines.append("# ID 迁移计划")
    lines.append("")
    lines.append("> 本文档由 `tools/scan_numeric_registrations.py` 自动生成，")
    lines.append("> 列出项目内所有注册名带数字后缀（`_0`/`_1`/`_2`/`_3`…）的内容，")
    lines.append("> 作为 ID 重命名 / 迁移的基线清单。")
    lines.append("> 重新生成: `python tools/scan_numeric_registrations.py`")
    lines.append("")
    lines.append("## 判定口径")
    lines.append("")
    lines.append("| 项 | 说明 |")
    lines.append("|----|------|")
    lines.append("| 命中规则 | 名称含 `_<数字>` 段：结尾（`flower_1`）或后接 `_`（`crack_0_particle`） |")
    lines.append("| 代码侧范围 | 5 模块 `src/main/java/**/registry/**` 的字符串字面量 |")
    lines.append("| 数据包侧范围 | `src/main/resources/data/**` 下的注册目录文件名 |")
    lines.append("| 已排除 | `libs/`、`build/`、`docs/deprecated/`、`packs/`（原版 UI 覆盖包） |")
    lines.append("| 引用排除 | `DebugStructureWandItem` / `DebugDecorWandItem` / `LootstableCreateItem` 的结构 ID 参数不算注册名 |")
    lines.append("")
    lines.append("---")
    lines.append("")

    # 代码侧
    lines.append("## 一、代码侧注册")
    lines.append("")
    code_total = 0
    for cat, _ in CODE_CATEGORIES:
        files = code.get(cat, {})
        if not files:
            continue
        all_names = set()
        for names in files.values():
            all_names.update(names)
        code_total += len(all_names)
        lines.append("### %s（%d 个）" % (cat, len(all_names)))
        lines.append("")
        for rel in sorted(files):
            names = sorted(files[rel], key=natkey)
            lines.append("`%s`（%d）:" % (rel, len(names)))
            lines.append("")
            lines.append(render_list(names))
            lines.append("")
    lines.append("> 代码侧命中总计: **%d** 个（跨文件去重前按分类分别统计）" % code_total)
    lines.append("")
    lines.append("---")
    lines.append("")

    # 数据包侧
    lines.append("## 二、数据包侧注册")
    lines.append("")
    data_total = 0
    for name, _, _ in DATA_SECTIONS:
        names = data.get(name, [])
        if not names:
            continue
        data_total += len(names)
        lines.append("### %s（%d 个）" % (name, len(names)))
        lines.append("")
        lines.append(render_list(names, bullet=(len(names) <= 40)))
        lines.append("")
    lines.append("> 数据包侧命中总计: **%d** 个" % data_total)
    lines.append("")
    lines.append("---")
    lines.append("")

    # 分类
    code_all = set()
    for files in code.values():
        for names in files.values():
            code_all.update(names)

    lines.append("## 三、迁移分类参考")
    lines.append("")
    lines.append("### 3.1 同族编号（多编号并存，多为 MCreator 变体拆分）")
    lines.append("")
    lines.append("> 同一前缀拆成多个编号（如 `calle_card_0~9`）。改名需成组处理，")
    lines.append("> 建议改成语义名（`calle_card_fire` 等）或保留编号但补齐文档。")
    lines.append("")
    same, bare, single = classify(code_all)
    for base, members in same:
        lines.append("- `%s`: %s" % (base, ", ".join("`%s`" % m for m in members)))
    lines.append("")
    lines.append("### 3.2 裸 `_0`（无同族，数字无意义，优先改名）")
    lines.append("")
    lines.append(render_list(bare))
    lines.append("")
    lines.append("### 3.3 单编号（数字在中间段，如粒子 `*_0_particle`）")
    lines.append("")
    lines.append(render_list(single))
    lines.append("")
    lines.append("---")
    lines.append("")

    lines.append("## 四、迁移建议（草稿）")
    lines.append("")
    lines.append("| 优先级 | 对象 | 处理方式 |")
    lines.append("|--------|------|----------|")
    lines.append("| P0 | 3.2 裸 `_0`（无同族） | 直接去掉 `_0`：`meltdream_crystal_0` → `meltdream_crystal`、`red_dew_0` → `red_dew`、`shadow_npc_0` → `shadow_npc` |")
    lines.append("| P1 | 3.1 同族编号 | 语义化命名（需成组同步），或保留编号仅补文档 |")
    lines.append("| P2 | 群系旧名 | 已有新描述名，按 `PDBiomes` 的 `@Deprecated(forRemoval)` 时间表移除旧别名 |")
    lines.append("| 保留 | 结构 / 结构集 / 模板池 / 地物 / 配方 / 战利品表编号 | 编号即变体 ID，无明确语义名时保留 |")
    lines.append("")
    lines.append("**迁移同步清单**（每个 ID 改名需同步以下位置）:")
    lines.append("")
    lines.append("1. Java 注册名与常量（`registry/**`）")
    lines.append("2. 资源文件: blockstate / model / 纹理 / 动画 / sounds.json")
    lines.append("3. 语言文件 key（`assets/*/lang/*.json`）")
    lines.append("4. 引用: 配方、战利品表、标签、进度、结构引用、DataGen")
    lines.append("5. 快照: `PasterDream/src/main/resources/pd_porting_manifest.json`（PDPortingVerifyTest 校验基线）")
    lines.append("")
    lines.append("**模板**: 群系已采用「新描述名 + 旧数字别名」双轨（见 `PDBiomes.java`），可作为其他类别的迁移范式。")
    lines.append("")

    return "\n".join(lines) + "\n"


def main():
    doc = build_doc()
    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    with open(OUT, "w", encoding="utf-8", newline="\n") as fh:
        fh.write(doc)
    print("written:", OUT)


if __name__ == "__main__":
    main()
