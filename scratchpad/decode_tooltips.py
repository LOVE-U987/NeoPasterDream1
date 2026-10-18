# -*- coding: utf-8 -*-
"""临时脚本：解码原模组武器类 appendHoverText 的 unicode 转义文本"""
import re
import pathlib

base = pathlib.Path(r'libs/FixPasterDream-main/src/main/java/net/pasterdream/item')
files = [
    'TruestMoltengoldSwordItem.java', 'TrueTideSwordItem.java', 'DyedreamSword0Item.java',
    'ShadowSwordItem.java',
]
pattern = re.compile(r'Component\.literal\("((?:[^"\\]|\\.)*)"\)')
for fn in files:
    t = (base / fn).read_text(encoding='utf-8', errors='ignore')
    if 'appendHoverText' not in t:
        print(f'== {fn}: no tooltip')
        continue
    body = t[t.index('appendHoverText'):]
    print(f'== {fn}:')
    for m in pattern.finditer(body):
        raw = m.group(1)
        try:
            print('   |' + raw.encode('latin-1', 'backslashreplace').decode('unicode_escape'))
        except Exception as e:
            print(f'   |RAW {raw} ({e})')
