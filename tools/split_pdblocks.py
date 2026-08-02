#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
将 PDBlocks.java 按主题拆分为多个子文件，采用「分文件注册 + 原类聚合引用」模式。

子文件只负责通过 PDBlocks.BLOCKS 注册方块；PDBlocks.java 保留 BLOCKS 注册器、
BlockConfig 静态初始化与私有辅助方法，并聚合暴露所有常量，保持外部引用不变。
"""

import re
from pathlib import Path

BASE_DIR = Path(r'C:\Users\97128\Documents\GitHub\NeoPasterDream1')
SRC = BASE_DIR / 'PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDBlocks.java'
OUT_DIR = BASE_DIR / 'PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/blocks'
PACKAGE = 'com.pasterdream.pasterdreammod.registry.blocks'

COMMON_IMPORTS = '''package {package};

import com.pasterdream.pasterdreammod.api.block.BlockAPI;
import com.pasterdream.pasterdreammod.api.block.BlockConfig;
import com.pasterdream.pasterdreammod.api.block.builder.VariantSetResult;
import com.pasterdream.pasterdreammod.block.*;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Map;
'''

SECTION_MAP = {
    # 自定义交互方块
    '自定义方块（保持手动注册）': 'PDBlocksCustom',
    # 玩偶/雕像类装饰方块
    '玩偶/雕像方块': 'PDBlocksDolls',
    # 功能性方块（书卷、炼药锅、水晶箱）
    '寻梦者的永恒书卷': 'PDBlocksFunctional',
    '梦境炼药锅（Dream Factory/Cauldron）': 'PDBlocksFunctional',
    '融梦水晶箱（GeckoLib 动画）': 'PDBlocksFunctional',
    # 简单换皮方块及其所有依赖变体（必须同文件以避免跨文件引用）
    '简单换皮方块（API 批量注册）': 'PDBlocksSimple',
    '简单方块公开引用': 'PDBlocksSimple',
    '特殊方块（保持手动注册）': 'PDBlocksSimple',
    '建筑变体族（API 批量注册）': 'PDBlocksSimple',
    '其他变体（手动注册）': 'PDBlocksSimple',
    '钙华变体系列（缺失方块补全）': 'PDBlocksSimple',
    '玻璃面板和灯笼': 'PDBlocksSimple',
    # 植被/植物/云朵/Phase1 作物（共享 flowerProps 等辅助方法）
    '自定义模型方块': 'PDBlocksVegetation',
    '云朵方块': 'PDBlocksVegetation',
    '染梦花草（移植自原版模组）': 'PDBlocksVegetation',
    'Phase 1: 移植物块材料': 'PDBlocksVegetation',
    # 流体方块
    '流体方块': 'PDBlocksMaterials',
    # BOSS 相关方块
    'BOSS 相关方块': 'PDBlocksBoss',
    # 阴影维度基础方块与变体
    '阴影维度基础方块': 'PDBlocksShadow',
    '阴影石砖变体族（API 批量注册）': 'PDBlocksShadow',
    '阴影木板变体族（API 批量注册）': 'PDBlocksShadow',
    # 暗影地牢/竞技场方块
    '暗影地牢方块（BOSS 竞技场场地）': 'PDBlocksDungeon',
    '暗影地牢功能性方块': 'PDBlocksDungeon',
    '暗影书架系列（4种样式）': 'PDBlocksDungeon',
    '暗影裂隙系列（6种发光等级）': 'PDBlocksDungeon',
    # 染梦维度 Phase 2 剩余方块
    '染梦维度剩余方块（Phase 2）': 'PDBlocksDyedreamPhase2',
}

CLASS_DOC = {
    'PDBlocksCustom': '自定义交互方块注册（蓄梦池、书桌、列车结构、生命水晶）。',
    'PDBlocksDolls': '玩偶/雕像类装饰方块注册。',
    'PDBlocksFunctional': '功能性方块注册（书卷、炼药锅、水晶箱）。',
    'PDBlocksSimple': '简单换皮方块及建筑变体注册（泥土、沙、木板、矿石、楼梯/台阶/墙等）。',
    'PDBlocksVegetation': '植被/植物/云朵/Phase1 作物类方块注册。',
    'PDBlocksMaterials': '流体方块注册。',
    'PDBlocksBoss': 'BOSS 相关方块注册。',
    'PDBlocksShadow': '阴影维度基础方块与变体注册。',
    'PDBlocksDungeon': '暗影地牢/竞技场方块注册。',
    'PDBlocksDyedreamPhase2': '染梦维度 Phase 2 剩余方块注册。',
}


def extract_static_initializer(content: str) -> tuple[str, str, str]:
    """提取 static { ... } 块，返回 (之前的内容, static块, 之后的内容)。"""
    match = re.search(r'\n    static \{.*?\n    \}', content, re.DOTALL)
    if not match:
        return content, '', ''
    before = content[:match.start()]
    static_block = match.group(0)
    after = content[match.end():]
    return before, static_block, after


def split_sections(content: str):
    """按 // =============== 标题 =============== 主分隔线分隔，忽略 // ===== 子分隔线。"""
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
    return SECTION_MAP.get(title, 'PDBlocksMisc')


def build_sub_file(class_name: str, doc: str, bodies: list[str]) -> str:
    parts = [COMMON_IMPORTS.format(package=PACKAGE)]
    parts.append(f'''
/**
 * {doc}
 *
 * @see PDBlocks
 */
public class {class_name} {{
''')
    # 子文件统一通过 PDBlocks.BLOCKS 注册；仅替换注册器调用，避免误伤 SIMPLE_BLOCKS 等字段名
    for body in bodies:
        cleaned = body.replace('BLOCKS.registerBlock(', 'PDBlocks.BLOCKS.registerBlock(')
        cleaned = cleaned.replace('BLOCKS.register(', 'PDBlocks.BLOCKS.register(')
        # 移除 body 末尾可能存在的类结束花括号，避免与子文件自己的 } 重复
        cleaned = cleaned.rstrip()
        while cleaned.endswith('}'):
            cleaned = cleaned[:-1].rstrip()
        parts.append(cleaned)
    parts.append('}\n')
    return '\n'.join(parts)


def extract_constant_names(content: str) -> list[str]:
    """提取 public static final DeferredBlock<...> NAME = ... 中的 NAME。"""
    return re.findall(r'public\s+static\s+final\s+DeferredBlock<[^>]+>\s+(\w+)\s*=', content)


def build_pdblocks_new(lead: str, static_block: str, constants: dict[str, list[str]]) -> str:
    """生成新的 PDBlocks.java：保留 BLOCKS、静态初始化、辅助方法，仅聚合引用子类常量。"""
    # 清理 lead 中的具体注册项，仅保留到 BLOCKS 声明
    lead_lines = lead.splitlines()
    cleaned = []
    in_multiline_register = False
    for line in lead_lines:
        stripped = line.strip()
        if stripped.startswith('public static final DeferredBlock<'):
            in_multiline_register = True
            continue
        if in_multiline_register:
            if stripped.endswith(');'):
                in_multiline_register = False
            continue
        cleaned.append(line)

    # 添加子包聚合 import
    lead_text = '\n'.join(cleaned)
    lead_text = lead_text.replace(
        'import net.neoforged.neoforge.registries.DeferredRegister;',
        'import net.neoforged.neoforge.registries.DeferredRegister;\n\nimport com.pasterdream.pasterdreammod.registry.blocks.*;'
    )

    agg_lines = ['\n    // ==================== 子文件聚合引用 ====================\n']
    for class_name, names in constants.items():
        agg_lines.append(f'    // --- {class_name} ---')
        for name in names:
            agg_lines.append(f'    public static final DeferredBlock<?> {name} = {class_name}.{name};')
        agg_lines.append('')

    return lead_text.rstrip() + '\n' + static_block + '\n' + '\n'.join(agg_lines) + '\n}\n'


def main():
    content = SRC.read_text(encoding='utf-8')
    before, static_block, after = extract_static_initializer(content)
    if not static_block:
        print('未找到 static 初始化块，取消拆分。')
        return

    # 将 static 块替换为占位符，使前后 section 能一起被解析
    placeholder = '\n    // __STATIC_INITIALIZER_PLACEHOLDER__\n'
    content_without_static = before + placeholder + after

    sections = split_sections(content_without_static)
    if not sections:
        print('未找到分区标记，取消拆分。')
        return

    OUT_DIR.mkdir(parents=True, exist_ok=True)

    groups: dict[str, list[str]] = {}
    lead_body = sections[0][1]

    for title, body in sections[1:]:
        # 跳过 static 初始化占位符 section
        if '__STATIC_INITIALIZER_PLACEHOLDER__' in title:
            continue
        class_name = get_class_name(title)
        groups.setdefault(class_name, []).append(body)

    all_constants: dict[str, list[str]] = {}
    for class_name, bodies in groups.items():
        doc = CLASS_DOC.get(class_name, f'{class_name} 注册。')
        file_content = build_sub_file(class_name, doc, bodies)
        names = extract_constant_names(file_content)
        if not names:
            print(f'Skipped empty: {class_name}')
            continue
        out_path = OUT_DIR / f'{class_name}.java'
        out_path.write_text(file_content, encoding='utf-8')
        print(f'Generated: {out_path}')
        all_constants[class_name] = names

    new_pdblocks = build_pdblocks_new(lead_body, static_block, all_constants)
    SRC.write_text(new_pdblocks, encoding='utf-8')
    print(f'Rewritten: {SRC}')


if __name__ == '__main__':
    main()
