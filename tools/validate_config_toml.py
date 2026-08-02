import tomllib
import sys
from pathlib import Path

files = [
    Path(r'c:\Users\97128\Documents\GitHub\NeoPasterDream1\PasterDream\run\config\PasterDream-Client.toml'),
    Path(r'c:\Users\97128\Documents\GitHub\NeoPasterDream1\PasterDream\run\config\PasterDream-Common.toml'),
]

all_ok = True
for f in files:
    if not f.exists():
        print(f'[FAIL] {f} 不存在')
        all_ok = False
        continue
    try:
        with open(f, 'rb') as fh:
            data = tomllib.load(fh)
        print(f'[OK] {f.name}')
        print(f'  路径: {f}')
        print(f'  顶层分类: {list(data.keys())}')
        for sec, vals in data.items():
            print(f'  [{sec}] 配置项数量: {len(vals)}')
    except Exception as e:
        print(f'[FAIL] {f.name}: {e}')
        all_ok = False

sys.exit(0 if all_ok else 1)
