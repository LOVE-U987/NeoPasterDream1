#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
将 PDItems.java 按主题拆分为多个子文件，采用「分文件注册 + 原类聚合引用」模式。

子文件只负责通过 PDItems.ITEMS 注册物品；PDItems.java 保留 ITEMS 注册器并聚合
暴露所有常量，保持外部引用不变。
"""

import re
from pathlib import Path

BASE_DIR = Path(r'C:\Users\97128\Documents\GitHub\NeoPasterDream1')
SRC = BASE_DIR / 'PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDItems.java'
OUT_DIR = BASE_DIR / 'PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/items'
PACKAGE = 'com.pasterdream.pasterdreammod.registry.items'

# 原始 import 块（所有子文件复用同一套 import，避免逐个分析依赖）
COMMON_IMPORTS = '''package {package};

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.curio.CurioAPI;
import com.pasterdream.pasterdreammod.api.curio.model.CurioSlot;
import com.pasterdream.pasterdreammod.api.effect.MobEffectAPI;
import com.pasterdream.pasterdreammod.api.entity.EntityAPI;
import com.pasterdream.pasterdreammod.api.item.ItemAPI;
import com.pasterdream.pasterdreammod.api.item.model.MigrationCategory;
import com.pasterdream.pasterdreammod.api.item.model.ToolSpec.ToolType;
import com.pasterdream.pasterdreammod.item.*;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.food.FoodProperties;
import net.neoforged.neoforge.registries.DeferredItem;
'''

# 注释中的中文主题到英文文件名的映射
SECTION_MAP = {
    '默认注册项': 'PDItemsFunctional',
    '玩偶/雕像物品': 'PDItemsDolls',
    '梦境炼药锅物品': 'PDItemsFunctional',
    '融梦水晶箱物品': 'PDItemsFunctional',
    '染梦世界方块物品': 'PDItemsBlocks',
    'Phase 1: 移植方块物品': 'PDItemsBlocks',
    '钙华变体补充方块物品': 'PDItemsBlocks',
    '刷怪蛋（通过 EntityAPI 统一注册）': 'PDItemsSpawnEggs',
    '阴影系列刷怪蛋': 'PDItemsSpawnEggs',
    '雷云系列刷怪蛋': 'PDItemsSpawnEggs',
    '其他敌对生物刷怪蛋': 'PDItemsSpawnEggs',
    '恐怖尖喙系列刷怪蛋': 'PDItemsSpawnEggs',
    '骨翼系列刷怪蛋': 'PDItemsSpawnEggs',
    '染梦新生物刷怪蛋': 'PDItemsSpawnEggs',
    '测试材料物品': 'PDItemsMaterials',
    '批量移植的材料物品': 'PDItemsMaterials',
    'Phase 2: 移植特殊物品': 'PDItemsMaterials',
    'API物品移植测试': 'PDItemsMaterials',
    '剑类武器': 'PDItemsTools',
    '镐类/锤类工具': 'PDItemsTools',
    '食物类物品': 'PDItemsFoods',
    '需要自定义类的物品（tooltip/交互）': 'PDItemsFunctional',
    'Curio饰品/特殊物品': 'PDItemsCurios',
    '音乐唱片（使用 API registerCustom 注册）': 'PDItemsMusic',
    '染梦群系背景音乐唱片（使用 API registerCustom 注册）': 'PDItemsMusic',
    '自定义模型方块 BlockItem': 'PDItemsBlocks',
    '云朵方块 BlockItem': 'PDItemsBlocks',
    '染梦花草 BlockItem': 'PDItemsBlocks',
    '调试结构法杖': 'PDItemsFunctional',
    'P0 移植遗迹调试水晶': 'PDItemsFunctional',
    'P1 移植遗迹调试水晶': 'PDItemsFunctional',
    '染梦世界装饰物调试水晶': 'PDItemsFunctional',
    '染梦世界树木调试水晶': 'PDItemsFunctional',
    'BOSS 相关物品': 'PDItemsFunctional',
    '阴影维度方块物品': 'PDItemsBlocks',
    'BOSS 刷怪蛋': 'PDItemsSpawnEggs',
    '盔甲套装': 'PDItemsArmor',
}

CLASS_DOC = {
    'PDItemsBlocks': '方块物品注册（BlockItem）。',
    'PDItemsDolls': '玩偶/雕像物品注册。',
    'PDItemsFunctional': '功能性物品注册（含展示方块、箱子、法杖、调试水晶、BOSS 物品）。',
    'PDItemsMaterials': '原材料与杂物注册。',
    'PDItemsTools': '工具与武器注册。',
    'PDItemsFoods': '食物类物品注册。',
    'PDItemsCurios': 'Curio 饰品与特殊物品注册。',
    'PDItemsSpawnEggs': '刷怪蛋注册。',
    'PDItemsMusic': '音乐唱片注册。',
    'PDItemsArmor': '盔甲套装注册。',
}


def extract_header(content: str) -> str:
    """提取 package 声明之上的所有内容（package 语句本身保留在原文件）。"""
    lines = content.splitlines()
    for i, line in enumerate(lines):
        if line.strip().startswith('package '):
            return '\n'.join(lines[:i]) + '\n'
    return ''


def split_sections(content: str):
    """按 // === ... === 或 // --- ... --- 或 // ======== ... ======== 分隔为 (标题, 主体) 列表。"""
    # 匹配形如 // ==================== 标题 ==================== 的分隔线
    pattern = re.compile(r'^\s*//\s*(={5,}|\-{5,})\s*(.*?)\s*\1\s*$', re.MULTILINE)
    matches = list(pattern.finditer(content))
    if not matches:
        return []

    sections = []
    # 第一个分隔线之前的领头内容（package/import/类声明/ITEMS/最前面几个无注释物品）
    first_start = matches[0].start()
    lead = content[:first_start]
    sections.append(('默认注册项', lead))

    for i, m in enumerate(matches):
        title = m.group(2).strip()
        start = m.start()
        end = matches[i + 1].start() if i + 1 < len(matches) else len(content)
        body = content[start:end]
        sections.append((title, body))

    return sections


def get_class_name(title: str) -> str:
    return SECTION_MAP.get(title, 'PDItemsMisc')


def build_sub_file(class_name: str, doc: str, bodies: list[str]) -> str:
    parts = [COMMON_IMPORTS.format(package=PACKAGE)]
    parts.append(f'''
/**
 * {doc}
 *
 * @see PDItems
 */
public class {class_name} {{
''')
    parts.extend(bodies)
    parts.append('}\n')
    return '\n'.join(parts)


def extract_constant_names(content: str) -> list[str]:
    """提取 public static final DeferredItem<...> NAME = ... 中的 NAME。"""
    return re.findall(r'public\s+static\s+final\s+DeferredItem<[^>]+>\s+(\w+)\s*=', content)


def build_pditems_new(lead: str, constants: dict[str, list[str]]) -> str:
    """生成新的 PDItems.java：保留 ITEMS 注册器，仅聚合引用各子类常量。"""
    # 删除原 lead 中的具体注册项，保留到 ITEMS 声明为止
    lead = re.sub(
        r'(public\s+static\s+final\s+DeferredRegister\.Items\s+ITEMS\s*=\s*DeferredRegister\.createItems\(PasterDreamMod\.MOD_ID\);)',
        r'\1\n',
        lead,
        count=1,
        flags=re.DOTALL
    )

    # 去掉 lead 末尾可能残留的空白和注册项
    lead = lead.rstrip()

    # 构造聚合引用
    agg_lines = ['\n    // ==================== 子文件聚合引用 ====================\n']
    for class_name, names in constants.items():
        agg_lines.append(f'    // --- {class_name} ---')
        for name in names:
            agg_lines.append(f'    public static final DeferredItem<?> {name} = {class_name}.{name};')
        agg_lines.append('')

    # 添加 import items.*
    lead = lead.replace(
        'import com.pasterdream.pasterdreammod.registry.PDBlocks;',
        'import com.pasterdream.pasterdreammod.registry.PDBlocks;\nimport com.pasterdream.pasterdreammod.registry.items.*;'
    )

    # 清理原 lead 中所有 public static final DeferredItem 注册项（保留 ITEMS）
    lead_lines = lead.splitlines()
    cleaned = []
    skip_until_next_blank = False
    in_multiline_register = False
    for line in lead_lines:
        stripped = line.strip()
        if stripped.startswith('public static final DeferredItem<'):
            in_multiline_register = True
            continue
        if in_multiline_register:
            if stripped.endswith(');'):
                in_multiline_register = False
            continue
        cleaned.append(line)

    return '\n'.join(cleaned) + '\n' + '\n'.join(agg_lines) + '\n}\n'


def main():
    content = SRC.read_text(encoding='utf-8')
    sections = split_sections(content)
    if not sections:
        print('未找到分区标记，取消拆分。')
        return

    OUT_DIR.mkdir(parents=True, exist_ok=True)

    # 分组
    groups: dict[str, list[str]] = {}
    lead_title, lead_body = sections[0]
    # 第一个分区之前的 body 包含类声明和 ITEMS，需要单独处理
    for title, body in sections[1:]:
        class_name = get_class_name(title)
        groups.setdefault(class_name, []).append(body)

    # 写入子文件
    all_constants: dict[str, list[str]] = {}
    for class_name, bodies in groups.items():
        doc = CLASS_DOC.get(class_name, f'{class_name} 注册。')
        file_content = build_sub_file(class_name, doc, bodies)
        out_path = OUT_DIR / f'{class_name}.java'
        out_path.write_text(file_content, encoding='utf-8')
        print(f'Generated: {out_path}')
        # 收集该文件暴露的常量
        all_constants[class_name] = extract_constant_names(file_content)

    # 生成新的 PDItems.java
    new_pditems = build_pditems_new(lead_body, all_constants)
    SRC.write_text(new_pditems, encoding='utf-8')
    print(f'Rewritten: {SRC}')


if __name__ == '__main__':
    main()