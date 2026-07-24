import os
import re

root = r'c:\Users\97128\Documents\GitHub\NeoPasterDream1\PasterDreamAPI'
pat = re.compile(r'^import com\.pasterdream\.pasterdreammod\.PasterDreamMod;\n', re.MULTILINE)

for dirpath, dirnames, filenames in os.walk(root):
    for fn in filenames:
        if not fn.endswith('.java'):
            continue
        path = os.path.join(dirpath, fn)
        with open(path, 'r', encoding='utf-8') as f:
            text = f.read()
        if pat.search(text):
            new_text = pat.sub('', text)
            with open(path, 'w', encoding='utf-8') as f:
                f.write(new_text)
            print(f'REMOVED stale import from {path}')

print('DONE')
