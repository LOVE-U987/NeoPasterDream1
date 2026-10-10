# -*- coding: utf-8 -*-
"""校验 PDIdAliases 别名表并生成独立产物报告。

校验项:
    1. 每个别名目标 (to) 必须在移植版 Java 源码中作为注册名出现。
    2. 别名来源 (from) 不应仍被注册（否则别名被遮蔽、实际 no-op），仅告警。
    3. 已确认的「移植版内部改名」manifest 项必须被别名表覆盖。

识别的调用形状:
    - aliasBlockAndItem(table, "from", "to")            旧形状（历史兼容）
    - table.add(Registries.X, id("from"), id("to"))     旧形状（历史兼容）
    - table.addBlockAndItem("from", "to")               便捷方法（BlockItem 双注册表）
    - table.addBlockAndItemFamily("to", "from0", ...)   便捷方法（同族合并 + 双注册表）
    - table.addFamily(Registries.X, "to", "from0", ...) 便捷方法（同族合并）

产物:
    build/reports/pd_id_aliases.md

用法:
    python tools/verify_id_aliases.py
"""
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ALIASES_JAVA = (ROOT / "PasterDream/src/main/java/com/pasterdream/pasterdreammod/"
                "compat/PDIdAliases.java")
MANIFEST = ROOT / "PasterDream/src/main/resources/pd_porting_manifest.json"
REPORT = ROOT / "build/reports/pd_id_aliases.md"

REG_DIRS = [
    ROOT / "PasterDream/src/main/java",
    ROOT / "PasterDreamSpells/src/main/java",
    ROOT / "PasterDreamAPI/src/main/java",
]

# 经 Phase 0 取证确认的「移植版内部改名」manifest 项（必须被别名表覆盖）
PORT_INTERNAL_MANIFEST_RENAMES = ["wind_knight_spawnblock_0"]

PAIR_RE = re.compile(
    r'aliasBlockAndItem\(\s*table\s*,\s*"([a-z0-9_]+)"\s*,\s*"([a-z0-9_]+)"\s*\)')
ADD_RE = re.compile(
    r'table\.add\(Registries\.(\w+)\s*,\s*id\("([a-z0-9_]+)"\)\s*,\s*id\("([a-z0-9_]+)"\)\s*\)')
# IdAliasTable 便捷方法调用形状（0.11.x 起支持）：
#   table.addBlockAndItem("from", "to")
#   table.addBlockAndItemFamily("to", "from0", "from1", ...)
#   table.addFamily(Registries.X, "to", "from0", "from1", ...)
# 支持跨行与行内注释；字符串字面量按出现顺序解析。
CALL_RE = re.compile(
    r'table\s*\.\s*(addBlockAndItemFamily|addBlockAndItem|addFamily)\b\s*\(([^)]*)\)')
REG_IN_CALL_RE = re.compile(r'Registries\.(\w+)')
QUOTED_RE = re.compile(r'"([a-z0-9_]+)"')

REG_NAME_RE = re.compile(
    r'register[A-Za-z]*\(\s*"([a-z0-9_]+)"')
REG_SIMPLE_RE = re.compile(
    r'registerSimple[A-Za-z]*\(\s*"([a-z0-9_]+)"')

REGISTRY_LABEL = {"BLOCK": "block", "ITEM": "item"}


def collect_aliases():
    text = ALIASES_JAVA.read_text(encoding="utf-8")
    aliases = []
    # 旧形状：aliasBlockAndItem(table, "from", "to")（历史调用，保持兼容）
    for frm, to in PAIR_RE.findall(text):
        aliases.append(("block", frm, to))
        aliases.append(("item", frm, to))
    # 新形状：IdAliasTable 便捷方法（addBlockAndItem / addBlockAndItemFamily / addFamily）
    for match in CALL_RE.finditer(text):
        call, args_text = match.group(1), match.group(2)
        # 剥离行内注释，避免注释中的引号字符串污染参数解析
        args_code = re.sub(r"//[^\n]*", "", args_text)
        names = QUOTED_RE.findall(args_code)
        if call == "addBlockAndItem" and len(names) == 2:
            frm, to = names
            aliases.append(("block", frm, to))
            aliases.append(("item", frm, to))
        elif call == "addFamily":
            reg = REG_IN_CALL_RE.search(args_code)
            if reg is None or len(names) < 2:
                continue
            registry = REGISTRY_LABEL.get(reg.group(1), reg.group(1).lower())
            to, froms = names[0], names[1:]
            for frm in froms:
                aliases.append((registry, frm, to))
        elif call == "addBlockAndItemFamily" and len(names) >= 2:
            to, froms = names[0], names[1:]
            for frm in froms:
                aliases.append(("block", frm, to))
                aliases.append(("item", frm, to))
    for reg, frm, to in ADD_RE.findall(text):
        aliases.append((REGISTRY_LABEL.get(reg, reg.lower()), frm, to))
    return aliases


# 按源文件名归属注册表（用于按注册表精确做 broken/shadowed 校验；
# 同名字符串跨注册表不算遮蔽，如音效 shadow_trap_0 与方块 shadow_trap_0 并存）。
# 无法归类的文件进入 unknown 桶，保守地对所有注册表生效。
FILE_REGISTRY_RULES = [
    ("sound_event", lambda n: n in ("PDSounds.java", "PDSpellsSounds.java")),
    ("mob_effect", lambda n: n in ("PDEffects.java", "PDSpellsEffects.java",
                                   "PDSanityEffects.java", "PDMeltDreamEffects.java")),
    ("particle", lambda n: "Particle" in n),
    ("menu", lambda n: n.startswith("PDMenus")),
    ("block_entity", lambda n: n.startswith("PDBlockEntities")),
    ("entity", lambda n: n == "PDEntities.java"),
    ("item", lambda n: "Items" in n),
    ("block", lambda n: "Blocks" in n),
    ("advancement", lambda n: n.startswith("PDAdvancements")),
]


def registry_of_file(filename):
    """按文件名推断其注册名所属的注册表标签。"""
    for registry, rule in FILE_REGISTRY_RULES:
        if rule(filename):
            return registry
    return "unknown"


def collect_registered_names():
    """按注册表收集注册名：{registry 标签: {name, ...}}，未归类文件进 unknown 桶。"""
    by_registry = {}
    for base in REG_DIRS:
        if not base.is_dir():
            continue
        for path in base.rglob("*.java"):
            try:
                text = path.read_text(encoding="utf-8", errors="replace")
            except OSError:
                continue
            bucket = by_registry.setdefault(registry_of_file(path.name), set())
            bucket.update(REG_NAME_RE.findall(text))
            bucket.update(REG_SIMPLE_RE.findall(text))
    return by_registry


def main():
    aliases = collect_aliases()
    registered_by_reg = collect_registered_names()
    manifest_renames = json.loads(MANIFEST.read_text(encoding="utf-8")).get("renames", {})

    def known(registry):
        """别名所属注册表的已注册名集合（含未归类文件的保守并集）。"""
        return registered_by_reg.get(registry, set()) | registered_by_reg.get("unknown", set())

    broken = []
    shadowed = []
    for registry, frm, to in aliases:
        if to not in known(registry):
            broken.append((registry, frm, to))
        if frm in known(registry):
            shadowed.append((registry, frm, to))

    covered_from = {frm for _, frm, _ in aliases}
    missing_coverage = [n for n in PORT_INTERNAL_MANIFEST_RENAMES if n not in covered_from]

    lines = []
    lines.append("# ID 别名表校验报告")
    lines.append("")
    lines.append(f"- 别名条数：{len(aliases)}")
    lines.append(f"- 目标已注册校验失败：{len(broken)}")
    lines.append(f"- 来源仍注册（遮蔽告警）：{len(shadowed)}")
    lines.append(f"- 移植内部 manifest 改名未覆盖：{len(missing_coverage)}")
    lines.append("")
    lines.append("## 别名清单")
    lines.append("")
    lines.append("| 注册表 | from | to |")
    lines.append("|--------|------|----|")
    for registry, frm, to in aliases:
        lines.append(f"| {registry} | `{frm}` | `{to}` |")
    lines.append("")
    if broken:
        lines.append("## 失败：目标未注册")
        lines.append("")
        for registry, frm, to in broken:
            lines.append(f"- [{registry}] `{frm}` -> `{to}`")
        lines.append("")
    if shadowed:
        lines.append("## 告警：来源仍注册（别名被遮蔽）")
        lines.append("")
        for registry, frm, to in shadowed:
            lines.append(f"- [{registry}] `{frm}` -> `{to}`")
        lines.append("")
    if missing_coverage:
        lines.append("## 失败：移植内部 manifest 改名未覆盖")
        lines.append("")
        for name in missing_coverage:
            target = manifest_renames.get(name, "?")
            lines.append(f"- `{name}` -> `{target}`")
        lines.append("")

    REPORT.parent.mkdir(parents=True, exist_ok=True)
    REPORT.write_text("\n".join(lines) + "\n", encoding="utf-8", newline="\n")

    print(f"aliases={len(aliases)} broken={len(broken)} shadowed={len(shadowed)} "
          f"missing_coverage={len(missing_coverage)}")
    print(f"report: {REPORT}")
    return 1 if (broken or missing_coverage) else 0


if __name__ == "__main__":
    raise SystemExit(main())
