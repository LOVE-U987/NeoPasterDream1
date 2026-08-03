import json
from pathlib import Path

LANG_FILE = Path(r'c:\Users\97128\Documents\GitHub\NeoPasterDream1\PasterDream\src\main\resources\assets\pasterdream\lang\zh_cn.json')

new_entries = {
    "gui.pasterdream.config.current_category": "当前分类：%s",
    "gui.pasterdream.config.item_count": "%s 项",
    "gui.pasterdream.config.hint.category_switch": "点击左侧分类浏览全部配置",
    "gui.pasterdream.config.total": "共 %s 项配置",
}

with open(LANG_FILE, 'r', encoding='utf-8') as f:
    data = json.load(f)

for k, v in new_entries.items():
    data[k] = v

with open(LANG_FILE, 'w', encoding='utf-8') as f:
    json.dump(data, f, ensure_ascii=False, indent=2)
    f.write('\n')

print("已添加 UI 提示语言键")
