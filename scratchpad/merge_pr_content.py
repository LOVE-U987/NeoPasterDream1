# -*- coding: utf-8 -*-
"""临时脚本：为 PR worktree 合并"仅我负责"的 lang 键与 Changelog 条目
- lang：以 worktree（main）版为基，从 7603ed7d 版提取我的键追加
- Changelog：提取我的 4 个条目块（剔除工坊条目——代码不随本 PR），插入 v0.11.0 头后
"""
import json
import pathlib
import subprocess

REPO = pathlib.Path(r'C:\Users\97128\Documents\GitHub\NeoPasterDream1')
WT = REPO / r'.claude\worktrees\pr-weapon'
LANG = r'PasterDream/src/main/resources/assets/pasterdream/lang'

MY_KEY_PREFIXES = [
    'tooltip.pasterdream.grass_sword.',
    'tooltip.pasterdream.true_grass_sword.',
    'tooltip.pasterdream.moltengold_sword.',
    'tooltip.pasterdream.true_moltengold_sword.',
    'tooltip.pasterdream.truest_moltengold_sword.',
    'tooltip.pasterdream.tide_sword.',
    'tooltip.pasterdream.desert_sword.',
    'tooltip.pasterdream.thermal_dagger.',
    'tooltip.pasterdream.creative_sword.',
    'tooltip.pasterdream.moltengold_tool.',
    'tooltip.pasterdream.true_moltengold_pickaxe.',
    'tooltip.pasterdream.dyedream_sword_0.',
    'tooltip.pasterdream.true_tide_sword.',
    'tooltip.pasterdream.true_desert_sword.',
]
MY_EXACT_KEYS = [
    'message.pasterdream.grass_sword.growth',
    'message.pasterdream.shadow_sword.no_talent',
    'item.pasterdream.shadow_sword.hovertext.is_san',
    'item.pasterdream.shadow_sword.hovertext.describe',
]

MY_ENTRY_TITLES = [
    '### 新增：草薙武器行为移植——中毒/击杀成长/天丛云战技',
    '### 新增：简单类武器行为批量移植（10 件，武器行为补全第一批）',
    '### 新增：战技类武器行为批量移植（5 件，武器行为补全第二批 · 收官）',
    '### 修复：北海若冲刺无效 + 暗影沉默体系怪物侧缺失（实机反馈两项）',
]


def show(commit: str, path: str) -> str:
    """读取主仓库中指定提交的文件内容（UTF-8）"""
    return subprocess.run(
        ['git', 'show', f'{commit}:{path}'],
        cwd=REPO, capture_output=True, check=True,
    ).stdout.decode('utf-8')


def merge_lang(name: str) -> None:
    base = json.loads((WT / LANG / name).read_text(encoding='utf-8'))
    src = json.loads(show('7603ed7d', f'{LANG}/{name}'))
    added = []
    for key, val in src.items():
        if key in base:
            continue
        if any(key.startswith(p) for p in MY_KEY_PREFIXES) or key in MY_EXACT_KEYS:
            base[key] = val
            added.append(key)
    out = json.dumps(base, ensure_ascii=False, indent=2) + '\n'
    (WT / LANG / name).write_bytes(out.encode('utf-8'))
    print(f'{name}: +{len(added)} keys')


def merge_changelog() -> None:
    base = (WT / 'Changelog.md').read_text(encoding='utf-8')
    src = show('7603ed7d', 'Changelog.md')
    # 按 "### " 切条目块，取我的条目（到下一个 "### " 或 "## " 为止）
    blocks = {}
    for title in MY_ENTRY_TITLES:
        start = src.find(title)
        if start < 0:
            raise SystemExit(f'条目未找到: {title}')
        nxt = len(src)
        for marker in ('\n### ', '\n## '):
            idx = src.find(marker, start + len(title))
            if idx >= 0:
                nxt = min(nxt, idx)
        blocks[title] = src[start:nxt].rstrip() + '\n'
    anchor = '## 未发布\n'
    if anchor not in base:
        raise SystemExit('未发布 锚点未找到')
    insertion = '\n' + ''.join(blocks[t] for t in MY_ENTRY_TITLES)
    base = base.replace(anchor, anchor + insertion, 1)
    (WT / 'Changelog.md').write_bytes(base.encode('utf-8'))
    print(f'Changelog: +{len(MY_ENTRY_TITLES)} entries')


merge_lang('zh_cn.json')
merge_lang('en_us.json')
merge_changelog()
print('DONE')
