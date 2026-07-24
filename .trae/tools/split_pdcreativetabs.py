#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
将 PDCreativeTabs.java 按标签页拆分为多个子文件，采用「分文件注册 + 原类聚合引用」模式。

子文件只负责通过 PDCreativeTabs.TABS 注册标签页；PDCreativeTabs.java 保留 TABS 注册器，
并聚合暴露所有常量，保持外部引用不变。
"""

import re
from pathlib import Path

BASE_DIR = Path(r'C:\Users\97128\Documents\GitHub\NeoPasterDream1')
SRC = BASE_DIR / 'PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDCreativeTabs.java'
OUT_DIR = BASE_DIR / 'PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/creativetabs'
PACKAGE = 'com.pasterdream.pasterdreammod.registry.creativetabs'

COMMON_IMPORTS = '''package {package};

import com.pasterdream.pasterdreammod.registry.PDCreativeTabs;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
'''

SECTION_MAP = {
    '1. 生物实体': 'PDCreativeTabsEntity',
    '2. 染梦维度': 'PDCreativeTabsDyedream',
    '3. 阴影维度': 'PDCreativeTabsShadow',
    '4. 风之旅途维度': 'PDCreativeTabsWind',
    '5. 纪念品': 'PDCreativeTabsSouvenir',
    '6. 盔甲装备': 'PDCreativeTabsArmor',
    '7. 武器工具': 'PDCreativeTabsWeapon',
    '8. 食物饮品': 'PDCreativeTabsFood',
    '9. 饰品装备': 'PDCreativeTabsCurio',
    '9. 音乐唱片': 'PDCreativeTabsDisc',
    '10. 调试功能': 'PDCreativeTabsDebug',
}

CLASS_DOC = {
    'PDCreativeTabsEntity': '生物实体相关创造模式标签页注册。',
    'PDCreativeTabsDyedream': '染梦维度创造模式标签页注册。',
    'PDCreativeTabsShadow': '阴影维度创造模式标签页注册。',
    'PDCreativeTabsWind': '风之旅途维度创造模式标签页注册。',
    'PDCreativeTabsSouvenir': '纪念品创造模式标签页注册。',
    'PDCreativeTabsArmor': '盔甲装备创造模式标签页注册。',
    'PDCreativeTabsWeapon': '武器工具创造模式标签页注册。',
    'PDCreativeTabsFood': '食物饮品创造模式标签页注册。',
    'PDCreativeTabsCurio': '饰品装备创造模式标签页注册。',
    'PDCreativeTabsDisc': '音乐唱片创造模式标签页注册。',
    'PDCreativeTabsDebug': '调试功能创造模式标签页注册。',
}

# 所有标签页常量名，用于跨文件引用替换
TAB_CONSTANTS = [
    'ENTITY_TAB', 'DYEDREAM_TAB', 'SHADOW_TAB', 'WIND_TAB', 'SOUVENIR_TAB',
    'ARMOR_TAB', 'WEAPON_TAB', 'FOOD_TAB', 'CURIO_TAB', 'DISC_TAB', 'DEBUG_TAB',
]


def split_sections(content: str):
    """按 // =============== 标题 =============== 主分隔线分隔。"""
    pattern = re.compile(r'^\s*//\s*={15,}\s*(.*?)\s*={15,}\s*$', re.MULTILINE)
    matches = list(pattern.finditer(content))
    if not matches:
        return []

    sections = []
    first_start = matches[0].start()
    lead = content[:first_start]
    sections.append(('LEAD', lead))

    for i, m in enumerate(matches):
        title = m.group(1).strip()
        start = m.start()
        end = matches[i + 1].start() if i + 1 < len(matches) else len(content)
        body = content[start:end]
        sections.append((title, body))

    return sections


def get_class_name(title: str) -> str:
    return SECTION_MAP.get(title, 'PDCreativeTabsMisc')


def replace_tab_refs(body: str, own_names: list[str]) -> str:
    """将其他标签页常量引用替换为 PDCreativeTabs.XXX_TAB，避免跨文件引用错误；
    保留本类定义的常量名不变，避免破坏常量定义左侧。"""
    for name in TAB_CONSTANTS:
        if name in own_names:
            continue
        # 使用单词边界，避免误替换含这些子串的标识符
        body = re.sub(rf'\b{name}\b', f'PDCreativeTabs.{name}', body)
    return body


def build_sub_file(class_name: str, doc: str, bodies: list[str], own_names: list[str]) -> str:
    parts = [COMMON_IMPORTS.format(package=PACKAGE)]
    parts.append(f'''
/**
 * {doc}
 *
 * @see PDCreativeTabs
 */
public class {class_name} {{
''')
    for body in bodies:
        cleaned = body.replace('TABS.register(', 'PDCreativeTabs.TABS.register(')
        cleaned = replace_tab_refs(cleaned, own_names)
        # 移除 body 末尾可能存在的类结束花括号
        cleaned = cleaned.rstrip()
        while cleaned.endswith('}'):
            cleaned = cleaned[:-1].rstrip()
        parts.append(cleaned)
    parts.append('}\n')
    return '\n'.join(parts)


def extract_constant_names(content: str) -> list[str]:
    """提取 public static final DeferredHolder<...> NAME = ... 中的 NAME。"""
    return re.findall(r'public\s+static\s+final\s+DeferredHolder<[^>]+>\s+(\w+)\s*=', content)


def build_pdcreativetabs_new(lead: str, constants: dict[str, list[str]]) -> str:
    """生成新的 PDCreativeTabs.java：保留 TABS 注册器，仅聚合引用子类常量。"""
    lead_lines = lead.splitlines()
    cleaned = []
    in_multiline_register = False
    for line in lead_lines:
        stripped = line.strip()
        if stripped.startswith('public static final DeferredHolder<'):
            in_multiline_register = True
            continue
        if in_multiline_register:
            if stripped.endswith(');') or stripped.endswith('.build());'):
                in_multiline_register = False
            continue
        cleaned.append(line)

    lead_text = '\n'.join(cleaned)
    lead_text = lead_text.replace(
        'import net.neoforged.neoforge.registries.DeferredRegister;',
        'import net.neoforged.neoforge.registries.DeferredRegister;\n\nimport com.pasterdream.pasterdreammod.registry.creativetabs.*;'
    )

    agg_lines = ['\n    // ==================== 子文件聚合引用 ====================\n']
    for class_name, names in constants.items():
        agg_lines.append(f'    // --- {class_name} ---')
        for name in names:
            agg_lines.append(f'    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> {name} = {class_name}.{name};')
        agg_lines.append('')

    return lead_text.rstrip() + '\n' + '\n'.join(agg_lines) + '\n}\n'


def main():
    content = SRC.read_text(encoding='utf-8')
    sections = split_sections(content)
    if not sections:
        print('未找到分区标记，取消拆分。')
        return

    OUT_DIR.mkdir(parents=True, exist_ok=True)

    groups: dict[str, list[str]] = {}
    lead_body = sections[0][1]

    for title, body in sections[1:]:
        class_name = get_class_name(title)
        groups.setdefault(class_name, []).append(body)

    all_constants: dict[str, list[str]] = {}
    for class_name, bodies in groups.items():
        doc = CLASS_DOC.get(class_name, f'{class_name} 注册。')
        # 先提取本类定义的常量名，用于后续跨文件引用替换时保护自身常量名
        raw_content = '\n'.join(bodies)
        names = extract_constant_names(raw_content)
        if not names:
            print(f'Skipped empty: {class_name}')
            continue
        file_content = build_sub_file(class_name, doc, bodies, names)
        out_path = OUT_DIR / f'{class_name}.java'
        out_path.write_text(file_content, encoding='utf-8')
        print(f'Generated: {out_path}')
        all_constants[class_name] = names

    new_pdcreativetabs = build_pdcreativetabs_new(lead_body, all_constants)
    SRC.write_text(new_pdcreativetabs, encoding='utf-8')
    print(f'Rewritten: {SRC}')


if __name__ == '__main__':
    main()
