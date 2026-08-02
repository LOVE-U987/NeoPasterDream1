"""
资源文件中文拼音命名扫描器
遍历 PasterDream / PasterDreamAPI 的 resources，找出文件名或语言键中疑似拼音的 token。
策略：
1. 使用 toneperfect.app 的完整拼音表生成有效拼音音节集合；
2. 将 token 切分为拼音音节（DP）；
3. 排除 Google 10000 英文常用词表中的词；
4. 输出候选清单及上下文。
"""
import json
import re
from collections import defaultdict
from pathlib import Path

BASE = Path(r"C:\Users\97128\Documents\GitHub\NeoPasterDream1")
RESOURCE_DIRS = [
    BASE / "PasterDream" / "src" / "main" / "resources",
    BASE / "PasterDreamAPI" / "src" / "main" / "resources",
]
TOOLS = BASE / "tools"

# 额外项目术语白名单（避免误报）
PROJECT_WHITELIST = {
    "minecraft", "pasterdream", "item", "block", "entity", "model", "models",
    "texture", "textures", "sound", "sounds", "animation", "animations", "geo",
    "recipe", "recipes", "loot", "table", "tables", "tag", "tags", "advancement",
    "advancements", "dimension", "type", "feature", "placed", "configured",
    "biome", "modifier", "modifiers", "structure", "worldgen", "world",
    "generator", "source", "noise", "router", "settings", "codec", "configured",
    "placed", "jukebox", "song", "songs", "particle", "particles",
}


def load_pinyin_syllables() -> set:
    """加载从 toneperfect.app 完整拼音表解析出的有效音节。"""
    path = TOOLS / "pinyin_syllables.txt"
    if not path.exists():
        raise FileNotFoundError(f"请先运行 parse_pinyin_chart.py 生成 {path}")
    return set(path.read_text(encoding="utf-8").splitlines())


def load_common_english() -> set:
    """加载 Google 10000 英文常用词表。"""
    path = TOOLS / "common_english.txt"
    if not path.exists():
        raise FileNotFoundError(f"缺少常用英文词表 {path}")
    return {line.strip().lower() for line in path.read_text(encoding="utf-8").splitlines() if line.strip()}


PY_SYLLABLES = load_pinyin_syllables()
COMMON_ENGLISH = load_common_english()


def can_segment_pinyin(token: str) -> bool:
    """DP 判断 token 能否被完全切成有效拼音音节。"""
    n = len(token)
    dp = [False] * (n + 1)
    dp[0] = True
    for i in range(n):
        if not dp[i]:
            continue
        # 音节长度 1~4
        for j in range(i + 1, min(n, i + 4) + 1):
            if token[i:j] in PY_SYLLABLES:
                dp[j] = True
    return dp[n]


def is_pinyin_candidate(token: str) -> bool:
    """判断一个 token 是否疑似中文拼音。"""
    token = token.lower()
    if not token.isalpha():
        return False
    if len(token) < 2 or len(token) > 10:
        return False
    if token in PROJECT_WHITELIST:
        return False
    # 整词是常见英文词 => 排除
    if token in COMMON_ENGLISH:
        return False
    return can_segment_pinyin(token)


def extract_tokens(text: str):
    """从路径或键中切分出候选 token。"""
    return [t for t in re.split(r"[_.\-\d\/]+", text) if t]


def main():
    hits = defaultdict(list)  # token -> [(context_type, context_value)]
    file_count = 0

    for res_dir in RESOURCE_DIRS:
        if not res_dir.exists():
            continue
        for path in res_dir.rglob("*"):
            if path.is_file():
                file_count += 1
                rel = path.relative_to(BASE).as_posix()
                for token in extract_tokens(path.stem):
                    if is_pinyin_candidate(token):
                        hits[token].append(("file", rel))

                # 扫描语言文件键
                if path.name.endswith(".json") and "lang" in rel:
                    try:
                        data = json.loads(path.read_text(encoding="utf-8"))
                    except Exception:
                        continue
                    if isinstance(data, dict):
                        for key in data.keys():
                            for token in extract_tokens(key):
                                if is_pinyin_candidate(token):
                                    hits[token].append(("lang_key", f"{rel}:{key}"))

    print(f"扫描文件总数: {file_count}")
    print(f"疑似拼音 token 数: {len(hits)}")
    print()
    if not hits:
        print("未发现明显中文拼音命名。")
        return

    print("| Token | 出现次数 | 上下文样例 |")
    print("|-------|----------|------------|")
    for token in sorted(hits, key=lambda t: -len(hits[t])):
        contexts = hits[token]
        sample = contexts[0]
        sample_str = f"{sample[0]}={sample[1]}"
        if len(sample_str) > 70:
            sample_str = sample_str[:67] + "..."
        print(f"| {token} | {len(contexts)} | {sample_str} |")

    # 输出详细清单到报告目录
    out = BASE / "docs" / "archive" / "pinyin-resource-scan.md"
    out.parent.mkdir(parents=True, exist_ok=True)
    with out.open("w", encoding="utf-8") as f:
        f.write("# 资源文件中文拼音命名扫描结果\n\n")
        f.write(f"扫描文件总数: {file_count}\n\n")
        for token in sorted(hits):
            f.write(f"## `{token}`\n")
            for ctx_type, ctx_value in hits[token]:
                f.write(f"- {ctx_type}: `{ctx_value}`\n")
            f.write("\n")
    print(f"\n详细清单已保存: {out}")


if __name__ == "__main__":
    main()
