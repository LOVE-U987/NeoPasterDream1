import re
from pathlib import Path

ROOT = Path(r'c:\Users\97128\Documents\GitHub\NeoPasterDream1')

# 读取原模组配置键
ORIG_CLIENT = ROOT / 'libs/FixPasterDream-main/src/main/java/net/pasterdream/configuration/PasterdreamConfigClientConfiguration.java'
ORIG_COMMON = ROOT / 'libs/FixPasterDream-main/src/main/java/net/pasterdream/configuration/PasterdreamConfigCommonConfiguration.java'
CUR_CLIENT = ROOT / 'PasterDream/src/main/java/com/pasterdream/pasterdreammod/config/PDClientConfig.java'
CUR_COMMON = ROOT / 'PasterDream/src/main/java/com/pasterdream/pasterdreammod/config/PDCommonConfig.java'
UI_SCREEN = ROOT / 'PasterDream/src/main/java/com/pasterdream/pasterdreammod/client/gui/config/PDConfigScreen.java'
LANG_FILE = ROOT / 'PasterDream/src/main/resources/assets/pasterdream/lang/zh_cn.json'


def parse_config_keys(path):
    text = path.read_text(encoding='utf-8')
    # 匹配 .define("key", ...)
    keys = re.findall(r'\.define\("([^"]+)"', text)
    return keys


def parse_current_fields(path):
    text = path.read_text(encoding='utf-8')
    fields = re.findall(r'public static final ModConfigSpec\.ConfigValue<\w+>\s+(\w+);', text)
    return fields


def parse_ui_entries(path):
    text = path.read_text(encoding='utf-8')
    # 匹配 PDClientConfig.XXX 或 PDCommonConfig.XXX
    entries = re.findall(r'(PDClientConfig|PDCommonConfig)\.(\w+)', text)
    return entries


def parse_lang_keys(path):
    text = path.read_text(encoding='utf-8')
    keys = re.findall(r'"gui\.pasterdream\.config\.([\w_]+)"', text)
    return keys


orig_client_keys = parse_config_keys(ORIG_CLIENT)
orig_common_keys = parse_config_keys(ORIG_COMMON)
cur_client_fields = parse_current_fields(CUR_CLIENT)
cur_common_fields = parse_current_fields(CUR_COMMON)
ui_entries = parse_ui_entries(UI_SCREEN)
lang_keys = set(parse_lang_keys(LANG_FILE))

print("=" * 60)
print("原模组配置键统计")
print(f"  Client: {len(orig_client_keys)}")
print(f"  Common: {len(orig_common_keys)}")
print(f"  合计: {len(orig_client_keys) + len(orig_common_keys)}")

print("\n当前项目配置字段统计")
print(f"  Client: {len(cur_client_fields)}")
print(f"  Common: {len(cur_common_fields)}")
print(f"  合计: {len(cur_client_fields) + len(cur_common_fields)}")

# 比较键名（将空格替换为下划线，忽略大小写）
orig_keys_normalized = {k.strip().replace(' ', '_').lower() for k in orig_client_keys + orig_common_keys}
cur_keys_normalized = {f.lower() for f in cur_client_fields + cur_common_fields}

missing_in_current = orig_keys_normalized - cur_keys_normalized
missing_in_original = cur_keys_normalized - orig_keys_normalized

print("\n原模组有但当前项目缺失的键:")
if missing_in_current:
    for k in sorted(missing_in_current):
        print(f"  - {k}")
else:
    print("  无")

print("\n当前项目有但原模组没有的键:")
if missing_in_original:
    for k in sorted(missing_in_original):
        print(f"  - {k}")
else:
    print("  无")

# UI 包含检查
ui_fields = {f.lower() for _, f in ui_entries}
print("\nUI 中引用的配置字段数:", len(ui_fields))
print("UI 未包含的配置字段:")
missing_in_ui = cur_keys_normalized - ui_fields
if missing_in_ui:
    for k in sorted(missing_in_ui):
        print(f"  - {k}")
else:
    print("  无")

# 语言键检查（需要去掉末尾下划线差异）
print("\n语言键缺失检查:")
needed_lang_keys = {k.strip().replace(' ', '_').rstrip('_').lower() for k in orig_client_keys + orig_common_keys}
missing_lang = needed_lang_keys - {k.rstrip('_').lower() for k in lang_keys}
if missing_lang:
    for k in sorted(missing_lang):
        print(f"  - gui.pasterdream.config.{k}")
else:
    print("  无")
