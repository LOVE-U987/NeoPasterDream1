# -*- coding: utf-8 -*-
"""
扫描所有配置类中的 ConfigValue 字段，检查每个字段是否在代码库中被实际使用（.get() 读取）。
输出：
  1. 从未被引用的配置项（除定义处外 0 次引用）——即无效配置
  2. 仅被配置界面引用（无业务读取）——可能无效
"""
import os
import re
import json

ROOT = r"c:\Users\97128\Documents\GitHub\NeoPasterDream1"
MODULES = ["PasterDream", "PasterDreamAPI", "PasterDreamMeltDream",
           "PasterDreamSanity", "PasterDreamSpells"]

CONFIG_FILES = [
    r"PasterDream\src\main\java\com\pasterdream\pasterdreammod\config\PDCommonConfig.java",
    r"PasterDream\src\main\java\com\pasterdream\pasterdreammod\config\PDClientConfig.java",
    r"PasterDreamMeltDream\src\main\java\com\pasterdream\pasterdreammod\pasterdreammeltdream\config\PDMeltDreamConfig.java",
    r"PasterDreamSanity\src\main\java\com\pasterdream\pasterdreammod\pasterdreamsanity\config\PDSanityConfig.java",
    r"PasterDreamSpells\src\main\java\com\pasterdream\pasterdreammod\pasterdreamspells\config\PDSpellsConfig.java",
]


def collect_all_java_texts():
    """收集所有模块 src 下的 java 源码文本（路径 → 文本）"""
    texts = {}
    for mod in MODULES:
        base = os.path.join(ROOT, mod, "src", "main", "java")
        if not os.path.isdir(base):
            continue
        for dirpath, _, files in os.walk(base):
            for fn in files:
                if fn.endswith(".java"):
                    p = os.path.join(dirpath, fn)
                    try:
                        with open(p, "r", encoding="utf-8") as f:
                            texts[p] = f.read()
                    except Exception:
                        pass
    return texts


def parse_config_fields(path):
    """从配置类中提取字段名与注释"""
    with open(path, "r", encoding="utf-8") as f:
        text = f.read()
    fields = []
    # 匹配: public static final ModConfigSpec.ConfigValue<Type> NAME;
    pattern = re.compile(
        r"public\s+static\s+final\s+ModConfigSpec\.ConfigValue<[^>]+>\s+([A-Z][A-Z0-9_]+)\s*;"
    )
    for m in pattern.finditer(text):
        fields.append(m.group(1))
    return fields


def main():
    java_texts = collect_all_java_texts()
    print(f"扫描到 Java 文件: {len(java_texts)}")

    results = []
    for cfg_path in CONFIG_FILES:
        full = os.path.join(ROOT, cfg_path)
        if not os.path.exists(full):
            print(f"  !! 配置类不存在: {cfg_path}")
            continue
        fields = parse_config_fields(full)
        cls_name = os.path.basename(cfg_path).replace(".java", "")

        # 定义处文件文本（用于判断哪些引用属于定义处自身）
        cfg_text = java_texts.get(full, "")

        for field in fields:
            total_refs = 0
            ref_files = []
            for path, text in java_texts.items():
                count = len(re.findall(r"\b" + field + r"\b", text))
                if count > 0:
                    total_refs += count
                    ref_files.append((path, count))
            # 定义处自身的出现次数
            self_refs = len(re.findall(r"\b" + field + r"\b", cfg_text))
            # 计算“定义处的字段声明 + 初始化赋值”（config 类里 define(...) 赋给该字段 = 2 处/字段 典型）
            # 引用次数阈值：config 类自身 2 次（声明+赋值），加外部业务引用
            external = total_refs - self_refs
            # 分类
            if external == 0:
                status = "❌ 完全未使用"
            else:
                # 检查外部引用是否全部在配置界面类（路径统一转正斜杠匹配）
                norm = lambda p: p.replace(os.sep, "/")
                screen_only = all(
                    "gui/config" in norm(p) or "AddonConfigRegistry" in norm(p)
                    for p, _ in ref_files if norm(p) != norm(full)
                )
                status = "⚠️ 仅配置界面引用" if screen_only else "✅ 有业务读取"
            results.append({
                "class": cls_name,
                "field": field,
                "status": status,
                "self_refs": self_refs,
                "external_refs": external,
                "ref_files": [os.path.relpath(p, ROOT) for p, _ in ref_files if p != full],
            })

    # 输出
    print("\n" + "=" * 80)
    print("【无效配置扫描结果】")
    print("=" * 80)
    for r in sorted(results, key=lambda x: (x["status"].startswith("❌"), x["status"])):
        print(f"\n{r['status']}  {r['class']}.{r['field']}")
        print(f"   外部引用 {r['external_refs']} 处 → {r['ref_files'] if r['ref_files'] else '（无）'}")

    with open(os.path.join(ROOT, "scratchpad", "config_usage_scan.json"), "w", encoding="utf-8") as f:
        json.dump(results, f, ensure_ascii=False, indent=2)
    print(f"\n结果已保存: scratchpad/config_usage_scan.json")


if __name__ == "__main__":
    main()
