# -*- coding: utf-8 -*-
"""校验 PDIdAliases 别名表并生成独立产物报告。

校验项:
    1. 每个别名目标 (to) 必须在移植版 Java 源码中作为注册名出现。
    2. 别名来源 (from) 不应仍被注册（否则别名被遮蔽、实际 no-op），仅告警。
    3. 已确认的「移植版内部改名」manifest 项必须被别名表覆盖。

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

REG_NAME_RE = re.compile(
    r'register[A-Za-z]*\(\s*"([a-z0-9_]+)"')
REG_SIMPLE_RE = re.compile(
    r'registerSimple[A-Za-z]*\(\s*"([a-z0-9_]+)"')

REGISTRY_LABEL = {"BLOCK": "block", "ITEM": "item"}


def collect_aliases():
    text = ALIASES_JAVA.read_text(encoding="utf-8")
    aliases = []
    for frm, to in PAIR_RE.findall(text):
        aliases.append(("block", frm, to))
        aliases.append(("item", frm, to))
    for reg, frm, to in ADD_RE.findall(text):
        aliases.append((REGISTRY_LABEL.get(reg, reg.lower()), frm, to))
    return aliases


def collect_registered_names():
    names = set()
    for base in REG_DIRS:
        if not base.is_dir():
            continue
        for path in base.rglob("*.java"):
            try:
                text = path.read_text(encoding="utf-8", errors="replace")
            except OSError:
                continue
            names.update(REG_NAME_RE.findall(text))
            names.update(REG_SIMPLE_RE.findall(text))
    return names


def main():
    aliases = collect_aliases()
    registered = collect_registered_names()
    manifest_renames = json.loads(MANIFEST.read_text(encoding="utf-8")).get("renames", {})

    broken = []
    shadowed = []
    for registry, frm, to in aliases:
        if to not in registered:
            broken.append((registry, frm, to))
        if frm in registered:
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
