import os
import re
import shutil

root = r'c:\Users\97128\Documents\GitHub\NeoPasterDream1'

moves = []

src_decor = os.path.join(root, r'PasterDream\src\main\java\com\pasterdream\pasterdreammod\worldgen\decor')
tgt_decor = os.path.join(root, r'PasterDreamAPI\src\main\java\com\pasterdream\pasterdreammod\api\worldgen\decor')

files_to_move = [
    'DecorationBuilder.java',
    'DecorationConfig.java',
    'DecorationPlacer.java',
    'DecorationRegistry.java',
    'DecorationType.java',
    'GenericDecorationFeature.java',
    'ICustomDecorationGenerator.java',
    'TreeRegistry.java',
]
for fn in files_to_move:
    moves.append((os.path.join(src_decor, fn), os.path.join(tgt_decor, fn)))

src_utils = os.path.join(root, r'PasterDream\src\main\java\com\pasterdream\pasterdreammod\worldgen\WorldGenUtils.java')
tgt_utils = os.path.join(root, r'PasterDreamAPI\src\main\java\com\pasterdream\pasterdreammod\api\worldgen\WorldGenUtils.java')
moves.append((src_utils, tgt_utils))

src_test = os.path.join(root, r'PasterDream\src\test\java\com\pasterdream\pasterdreammod\worldgen\decor\DecorationJsonGenerator.java')
tgt_test = os.path.join(root, r'PasterDreamAPI\src\test\java\com\pasterdream\pasterdreammod\api\worldgen\decor\DecorationJsonGenerator.java')
moves.append((src_test, tgt_test))

os.makedirs(tgt_decor, exist_ok=True)
os.makedirs(os.path.dirname(tgt_utils), exist_ok=True)
os.makedirs(os.path.dirname(tgt_test), exist_ok=True)

for src, tgt in moves:
    if not os.path.exists(src):
        print(f'SOURCE MISSING: {src}')
        continue
    shutil.move(src, tgt)
    print(f'MOVED {src} -> {tgt}')

old_pkg_decor = 'com.pasterdream.pasterdreammod.worldgen.decor'
new_pkg_decor = 'com.pasterdream.pasterdreammod.api.worldgen.decor'
old_pkg_worldgen = 'com.pasterdream.pasterdreammod.worldgen'
new_pkg_worldgen = 'com.pasterdream.pasterdreammod.api.worldgen'

moved_targets = [t for s, t in moves]


def rewrite_moved(path):
    with open(path, 'r', encoding='utf-8') as f:
        text = f.read()
    original = text
    text = text.replace(f'package {old_pkg_decor};', f'package {new_pkg_decor};')
    text = text.replace(f'package {old_pkg_worldgen};', f'package {new_pkg_worldgen};')
    text = text.replace(f'import {old_pkg_decor}.', f'import {new_pkg_decor}.')
    text = text.replace(f'import {old_pkg_worldgen}.WorldGenUtils;', f'import {new_pkg_worldgen}.WorldGenUtils;')
    text = text.replace('PasterDreamMod.MOD_ID', 'PasterDreamAPI.MOD_ID')
    text = text.replace('PasterDreamMod.LOGGER', 'PasterDreamAPI.LOGGER')
    if 'PasterDreamAPI' in text and 'import com.pasterdream.pasterdreammod.api.PasterDreamAPI;' not in text:
        text = re.sub(r'(package [^;]+;\n)', r'\1\nimport com.pasterdream.pasterdreammod.api.PasterDreamAPI;', text, count=1)
    if text != original:
        with open(path, 'w', encoding='utf-8') as f:
            f.write(text)
        print(f'REWROTE {path}')


for p in moved_targets:
    rewrite_moved(p)

old_import_decor = f'import {old_pkg_decor}'
new_import_decor = f'import {new_pkg_decor}'
old_import_utils = f'import {old_pkg_worldgen}.WorldGenUtils'
new_import_utils = f'import {new_pkg_worldgen}.WorldGenUtils'

moved_abs = {os.path.abspath(p) for p in moved_targets}

for dirpath, dirnames, filenames in os.walk(root):
    for fn in filenames:
        if not fn.endswith('.java'):
            continue
        path = os.path.join(dirpath, fn)
        if os.path.abspath(path) in moved_abs:
            continue
        with open(path, 'r', encoding='utf-8') as f:
            text = f.read()
        if old_import_decor in text or old_import_utils in text:
            text = text.replace(old_import_decor, new_import_decor)
            text = text.replace(old_import_utils, new_import_utils)
            with open(path, 'w', encoding='utf-8') as f:
                f.write(text)
            print(f'UPDATED {path}')

print('DONE')
