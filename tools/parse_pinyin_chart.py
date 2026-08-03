import re
from pathlib import Path

text = Path(r"C:\Users\97128\AppData\Local\Temp\trae\toolcall-output\de423f62-03c9-42d0-a6db-18bcf2585b09.txt").read_text(encoding="utf-8")

tone_map = str.maketrans(
    "āáǎàēéěèīíǐìōóǒòūúǔùǖǘǚǜĀÁǍÀĒÉĚÈĪÍǏÌŌÓǑÒŪÚǓÙǕǗǙǛ",
    "aaaaeeeeiiiioooouuuuvvvvAAAAEEEEIIIIOOOOUUUUVVVV",
)

syllables = set()
for line in text.splitlines():
    if not line.startswith("|"):
        continue
    parts = [p.strip() for p in line.split("|")[1:-1]]
    if not parts:
        continue
    first = parts[0].strip()
    if first in ("声/韵", "", " "):
        continue
    for cell in parts:
        if not cell or cell == "∅":
            continue
        s = cell.translate(tone_map).lower()
        s = re.sub(r"<[^>]+>", "", s)
        # 去掉像 "b · p · m · f" 这种含非字母的单元
        if s.isalpha() and 1 <= len(s) <= 4:
            syllables.add(s)

syllables = sorted(syllables)
print(f"count={len(syllables)}")
print(syllables)

# 同时保存为文本，方便其它脚本引用
out = Path(__file__).resolve().parent / "pinyin_syllables.txt"
out.write_text("\n".join(syllables), encoding="utf-8")
print(f"saved to {out}")