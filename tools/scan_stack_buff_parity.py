# -*- coding: utf-8 -*-
"""
扫盘：物品堆叠数量 / BUFF 数值（原模组 libs/FixPasterDream-main vs 新模组 PasterDream）
- 物品堆叠: 解析两边物品注册源码中的 stacksTo / durability(自动=1) / 默认 64
- BUFF 数值: 解析两边 MobEffect 的 addAttributeModifier(属性, uuid, 数值, 操作) 元组
- 排除项: cheerup_buff(振奋) / strawberry_heart(草莓甜心) / cradle_in_ones_arms(怀中御守)
用法: python tools/scan_stack_buff_parity.py
"""
import io, json, os, re, sys

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8")
ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OLD_SRC = os.path.join(ROOT, "libs", "FixPasterDream-main", "src", "main", "java")
NEW_SRC = os.path.join(ROOT, "PasterDream", "src", "main", "java")

EXCLUDE = {"cheerup_buff", "strawberry_heart", "cradle_in_ones_arms"}

# 1.20.1 Forge -> 1.21.1 NeoForge 操作名映射（同一操作）
OP_MAP = {"ADDITION": "ADD_VALUE", "MULTIPLY_BASE": "ADD_MULTIPLIED_BASE", "MULTIPLY_TOTAL": "MULTIPLY_TOTAL"}

# ---------- 通用工具 ----------

def read(path):
    with open(path, encoding="utf-8", errors="replace") as f:
        return f.read()


def find_class_files(src, class_name):
    """在 src 树中查找定义 class <class_name> 的 java 文件路径列表。"""
    hits = []
    for dirpath, _dirs, files in os.walk(src):
        for fn in files:
            if fn.endswith(".java") and fn[:-5] == class_name:
                hits.append(os.path.join(dirpath, fn))
    return hits


def extract_balanced(text, start_idx):
    """从 text[start_idx] 开始提取括号配对内的内容（假设当前位置是 '('）。返回内部文本。"""
    depth, i = 0, start_idx
    while i < len(text):
        ch = text[i]
        if ch == "(":
            depth += 1
        elif ch == ")":
            depth -= 1
            if depth == 0:
                return text[start_idx + 1:i]
        i += 1
    return ""


TOOL_RE = re.compile(
    r"extends\s+(\w*(?:Sword|Pickaxe|Axe|Shovel|Hoe|Tiered|Armor|Bow|Crossbow|Trident|"
    r"ProjectileWeapon|Mace|Shield|FishingRod|Shears|FlintAndSteel|Brush|FireworkRocket|"
    r"Snowball|Egg|EnderPearl|SpawnEgg|Boat|Minecart|Instrument|GoatHorn)\w*)"
)


def item_stack_from_class(src, class_name):
    """解析自定义 Item 类的堆叠: 显式 stacksTo(N) > durability/工具(1) > 默认 64。"""
    for fp in find_class_files(src, class_name):
        text = read(fp)
        m = re.search(r"stacksTo\(\s*(\d+)\s*\)", text)
        if m:
            return int(m.group(1))
        # 无显式 stacksTo: 有 durability 或继承工具/盔甲 → 堆叠 1
        if "durability(" in text or TOOL_RE.search(text):
            return 1
        return 64
    return None  # 类文件未找到


# ---------- Part A: 物品堆叠 ----------

def parse_supplier_stack(src, body):
    """从注册 supplier body 提取堆叠: stacksTo > new XxxItem / XxxItem::new 类解析 > 默认 64。"""
    sm = re.search(r"stacksTo\(\s*(\d+)\s*\)", body)
    if sm:
        return int(sm.group(1))
    cm = re.search(r"new\s+([\w.]+Item)(?:\.\w+)?\s*\(", body) or re.search(r"([\w.]+Item)\s*::new", body)
    if cm:
        cls = cm.group(1).split(".")[-1]
        val = item_stack_from_class(src, cls)
        return val if val is not None else 64
    return 64


def scan_stacks_old():
    """原模组: PasterdreamModItems.java 的 REGISTRY.register("name", ...) / block(...)"""
    items = {}
    fp = os.path.join(OLD_SRC, "net", "pasterdream", "init", "PasterdreamModItems.java")
    text = read(fp)
    reg_re = re.compile(r'REGISTRY\.register\("(\w+)"\s*,\s*\(\)\s*->\s*(.*?)\);', re.S)
    for m in reg_re.finditer(text):
        items[m.group(1)] = parse_supplier_stack(OLD_SRC, m.group(2))
    # block(...) 单独处理（无 lambda）
    for m in re.finditer(r"= block\(PasterdreamModBlocks\.(\w+)\)", text):
        items[m.group(1).lower()] = 64
    return items


def scan_stacks_new():
    """新模组: PDItems.java + registry/items/PDItems*.java 的 ITEMS.register* / ItemAPI builder"""
    items = {}
    reg_re = re.compile(r'ITEMS\.register[A-Za-z]*\("(\w+)"\s*,\s*(.*?)\);', re.S)
    api_re = re.compile(r'ItemAPI\.(\w+)\("(\w+)"(.*?)\);', re.S)
    for dirpath, _dirs, files in os.walk(NEW_SRC):
        for fn in files:
            if not (fn.startswith("PDItems") and fn.endswith(".java")):
                continue
            text = read(os.path.join(dirpath, fn))
            for m in reg_re.finditer(text):
                name, body = m.group(1), m.group(2)
                if "registerSimpleBlockItem" in m.group(0):
                    items[name] = 64
                    continue
                items[name] = parse_supplier_stack(NEW_SRC, body)
            for m in api_re.finditer(text):
                meth, name, rest = m.group(1), m.group(2), m.group(3)
                if meth in ("simpleItem", "foodItem", "curioItem"):
                    sm = re.search(r"stacksTo\(\s*(\d+)\s*\)", rest)
                    items[name] = int(sm.group(1)) if sm else 64
                elif meth == "registerCustom":
                    items[name] = parse_supplier_stack(NEW_SRC, rest)
    return items


# ---------- Part B: BUFF 数值 ----------

def norm_attr(expr):
    """属性归一化: PasterdreamModAttributes.SAN_VARIABILITY.get() -> SAN_VARIABILITY"""
    e = expr.strip()
    e = re.sub(r"\.get\(\)$", "", e)
    m = re.search(r"(\w+)$", e)
    return m.group(1) if m else e


def eval_amount(amount, fix_val=None):
    """数值归一化: '0.6 * fix' + fix=2 -> 1.2; 纯数字原样保留。"""
    a = amount.strip()
    m = re.match(r"^(-?[\d.]+)\s*\*\s*fix$", a)
    if m and fix_val is not None:
        v = float(m.group(1)) * fix_val
        return str(int(v)) if v == int(v) else str(v)
    return a


def parse_addattr_block(text, fix_val=None):
    """提取 addAttributeModifier(attr, uuid, amount, op) 归一化元组列表。"""
    out = []
    for m in re.finditer(r"addAttributeModifier\s*\(\s*([^,]+)\s*,\s*([^,]+)\s*,\s*([^,)]+)\s*,\s*([^)]+)\)", text):
        attr = norm_attr(m.group(1))
        amount = eval_amount(m.group(3), fix_val)
        opm = re.search(r"(\w+)$", m.group(4).strip())
        op_raw = opm.group(1) if opm else ""
        # 已是新名(ADD_VALUE/ADD_MULTIPLIED_BASE/MULTIPLY_TOTAL)直接用; 否则查旧名映射
        op = op_raw if op_raw in OP_MAP.values() else OP_MAP.get(op_raw, m.group(4).strip())
        out.append((attr, amount, op))
    return sorted(out)


def scan_buffs_old():
    """原模组: 注册名 -> potion 类 -> addAttributeModifier 元组(fix 由注册参数求值)"""
    buffs = {}
    fp = os.path.join(OLD_SRC, "net", "pasterdream", "init", "PasterdreamModMobEffects.java")
    text = read(fp)
    for m in re.finditer(r'REGISTRY\.register\("(\w+)"\s*,\s*\(\)\s*->\s*new\s+(\w+)\s*\(([^)]*)\)', text):
        name, cls, args = m.group(1), m.group(2), m.group(3).strip()
        fix_m = re.match(r"(\d+)", args)
        fix_val = int(fix_m.group(1)) if fix_m else None
        for cfile in find_class_files(OLD_SRC, cls):
            buffs[name] = parse_addattr_block(read(cfile), fix_val)
            break
    return buffs


def scan_buffs_new():
    """新模组: 主模 PDEffects.java + Sanity/MeltDream/Spells 附属模块的 PD*Effects.java"""
    buffs = {}
    targets = []
    # 主模
    fp = os.path.join(NEW_SRC, "com", "pasterdream", "pasterdreammod", "registry", "PDEffects.java")
    if os.path.exists(fp):
        targets.append(fp)
    # 附属模块
    for mod in ("PasterDreamSanity", "PasterDreamMeltDream", "PasterDreamSpells"):
        base = os.path.join(ROOT, mod, "src", "main", "java")
        for dirpath, _dirs, files in os.walk(base):
            for fn in files:
                if fn.endswith("Effects.java"):
                    targets.append(os.path.join(dirpath, fn))
    for path in targets:
        text = read(path)
        for m in re.finditer(r'\w+\.register\("(\w+)"\s*,\s*\(\)\s*->\s*new (?:MobEffect|InstantenousMobEffect)', text):
            name = m.group(1)
            open_idx = text.find("(", m.start())
            buffs[name] = parse_addattr_block(extract_balanced(text, open_idx))
        for m in re.finditer(r'createEffect\("(\w+)"\)(.*?)\.build\(\)', text, re.S):
            name = m.group(1)
            if name not in buffs:
                buffs[name] = parse_addattr_block(m.group(2))
    return buffs


# ---------- 主流程 ----------

def main():
    old_items = scan_stacks_old()
    new_items = scan_stacks_new()
    old_buffs = scan_buffs_old()
    new_buffs = scan_buffs_new()

    report = {"stack_diff": [], "buff_diff": [], "stack_old_only": [], "stack_new_only": []}

    # 堆叠对比
    for name, ost in sorted(old_items.items()):
        if name in EXCLUDE:
            continue
        nst = new_items.get(name)
        if nst is None:
            report["stack_old_only"].append(name)
        elif ost != nst:
            report["stack_diff"].append({"item": name, "old": ost, "new": nst})
    for name in sorted(new_items):
        if name not in old_items and name not in EXCLUDE:
            report["stack_new_only"].append(name)

    # BUFF 对比
    for name, oattr in sorted(old_buffs.items()):
        if name in EXCLUDE:
            continue
        nattr = new_buffs.get(name)
        if nattr is None:
            report["buff_diff"].append({"effect": name, "detail": "新模组未找到"})
        elif sorted(oattr) != sorted(nattr):
            report["buff_diff"].append({
                "effect": name,
                "old": sorted(oattr),
                "new": sorted(nattr),
            })
    for name in sorted(new_buffs):
        if name not in old_buffs and name not in EXCLUDE:
            report["buff_diff"].append({"effect": name, "detail": "仅新模组存在"})

    # 终端输出
    print(f"=== 物品堆叠对比: 原模组 {len(old_items)} / 新模组 {len(new_items)} (排除 {sorted(EXCLUDE)}) ===")
    if report["stack_diff"]:
        print(f"\n[差异] {len(report['stack_diff'])} 项:")
        for d in report["stack_diff"]:
            print(f"  {d['item']}: 原 {d['old']} -> 新 {d['new']}")
    else:
        print("\n[堆叠] 无差异 ✅")
    if report["stack_old_only"]:
        print(f"\n[仅原模组] {len(report['stack_old_only'])} 项(新模组未注册):")
        print("  " + ", ".join(report["stack_old_only"]))
    if report["stack_new_only"]:
        print(f"\n[仅新模组] {len(report['stack_new_only'])} 项(原模组无):")
        print("  " + ", ".join(report["stack_new_only"]))

    print(f"\n=== BUFF 数值对比: 原模组 {len(old_buffs)} / 新模组 {len(new_buffs)} (排除 {sorted(EXCLUDE)}) ===")
    if report["buff_diff"]:
        print(f"\n[差异] {len(report['buff_diff'])} 项:")
        for d in report["buff_diff"]:
            if "detail" in d:
                print(f"  {d['effect']}: {d['detail']}")
            else:
                print(f"  {d['effect']}:")
                print(f"    原: {d['old']}")
                print(f"    新: {d['new']}")
    else:
        print("\n[BUFF] 无差异 ✅")

    out = os.path.join(ROOT, "scratchpad", "scan_stack_buff_report.json")
    os.makedirs(os.path.dirname(out), exist_ok=True)
    with open(out, "w", encoding="utf-8") as f:
        json.dump(report, f, ensure_ascii=False, indent=2)
    print(f"\n报告已保存: {out}")


if __name__ == "__main__":
    main()
