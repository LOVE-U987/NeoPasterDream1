import os
import re
import shutil

root = r'c:\Users\97128\Documents\GitHub\NeoPasterDream1\PasterDream'

# 1. bobo_plume -> boboji_plume
old_bobo = 'bobo_plume'
new_bobo = 'boboji_plume'

# 文件重命名
for base in [
    os.path.join(root, r'src\main\resources\assets\pasterdream\textures\item'),
    os.path.join(root, r'src\main\resources\assets\pasterdream\models\item'),
]:
    old_path = os.path.join(base, f'{old_bobo}.png' if 'textures' in base else f'{old_bobo}.json')
    new_path = os.path.join(base, f'{new_bobo}.png' if 'textures' in base else f'{new_bobo}.json')
    if os.path.exists(old_path):
        shutil.move(old_path, new_path)
        print(f'RENAMED {old_path} -> {new_path}')

# 更新模型 JSON 中的纹理引用
model_path = os.path.join(root, r'src\main\resources\assets\pasterdream\models\item', f'{new_bobo}.json')
if os.path.exists(model_path):
    with open(model_path, 'r', encoding='utf-8') as f:
        text = f.read()
    text = text.replace(f'pasterdream:item/{old_bobo}', f'pasterdream:item/{new_bobo}')
    with open(model_path, 'w', encoding='utf-8') as f:
        f.write(text)
    print(f'UPDATED model {model_path}')

# 2. feiai_butterfly_bow -> crimsonlove_butterfly_bow
old_feiai = 'feiai_butterfly_bow'
new_feiai = 'crimsonlove_butterfly_bow'

old_tex = os.path.join(root, r'src\main\resources\assets\pasterdream\textures\item', f'{old_feiai}.png')
new_tex = os.path.join(root, r'src\main\resources\assets\pasterdream\textures\item', f'{new_feiai}.png')
if os.path.exists(old_tex):
    shutil.move(old_tex, new_tex)
    print(f'RENAMED {old_tex} -> {new_tex}')

# 更新 hiyori_head.json 中的纹理引用
hiyori_path = os.path.join(root, r'src\main\resources\assets\pasterdream\models\item\hiyori_head.json')
if os.path.exists(hiyori_path):
    with open(hiyori_path, 'r', encoding='utf-8') as f:
        text = f.read()
    if old_feiai in text:
        text = text.replace(f'pasterdream:item/{old_feiai}', f'pasterdream:item/{new_feiai}')
        with open(hiyori_path, 'w', encoding='utf-8') as f:
            f.write(text)
        print(f'UPDATED {hiyori_path}')

# 3. 更新 PDItems.java 注册名
pditems_path = os.path.join(root, r'src\main\java\com\pasterdream\pasterdreammod\registry\PDItems.java')
if os.path.exists(pditems_path):
    with open(pditems_path, 'r', encoding='utf-8') as f:
        text = f.read()
    if f'"{old_bobo}"' in text:
        text = text.replace(f'"{old_bobo}"', f'"{new_bobo}"')
        with open(pditems_path, 'w', encoding='utf-8') as f:
            f.write(text)
        print(f'UPDATED {pditems_path}')

# 4. 更新语言文件中的 key
for lang in ['en_us.json', 'zh_cn.json']:
    lang_path = os.path.join(root, r'src\main\resources\assets\pasterdream\lang', lang)
    if not os.path.exists(lang_path):
        continue
    with open(lang_path, 'r', encoding='utf-8') as f:
        text = f.read()
    if old_bobo in text:
        text = text.replace(f'block.pasterdream.{old_bobo}', f'block.pasterdream.{new_bobo}')
        text = text.replace(f'item.pasterdream.{old_bobo}', f'item.pasterdream.{new_bobo}')
        # 英文翻译同步改为 Boboji Plume
        if lang == 'en_us.json':
            text = text.replace(f'"{new_bobo}": "Bobo Plume"', f'"{new_bobo}": "Boboji Plume"')
        with open(lang_path, 'w', encoding='utf-8') as f:
            f.write(text)
        print(f'UPDATED lang {lang_path}')

# 5. 更新 curios tag
charm_tag_path = os.path.join(root, r'src\main\resources\data\curios\tags\item\charm.json')
if os.path.exists(charm_tag_path):
    with open(charm_tag_path, 'r', encoding='utf-8') as f:
        text = f.read()
    if old_bobo in text:
        text = text.replace(f'pasterdream:{old_bobo}', f'pasterdream:{new_bobo}')
        with open(charm_tag_path, 'w', encoding='utf-8') as f:
            f.write(text)
        print(f'UPDATED {charm_tag_path}')

print('DONE')
