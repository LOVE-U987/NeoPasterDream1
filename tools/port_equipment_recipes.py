"""
原模组装备配方移植脚本
========================
从 FixPasterDream-main (1.20.1 Forge) 读取装备类配方，
转换为 1.21.1 NeoForge 格式后写入当前模组的 recipe/ 目录。

转换规则：
  result.item → result.id（仅 output/result 区段）
  路径：recipes/ → recipe/
  原料引用保持 item/tag 不变（1.21.1 兼容）
"""

import json, os, re, shutil

# ==================== 路径配置 ====================
ORIGINAL_RECIPE_DIR = os.path.join(
    os.path.dirname(__file__),
    "..", "libs", "FixPasterDream-main",
    "src", "main", "resources", "data", "pasterdream", "recipes"
)
TARGET_RECIPE_DIR = os.path.join(
    os.path.dirname(__file__),
    "..", "PasterDream", "src", "main",
    "resources", "data", "pasterdream", "recipe"
)

# ==================== 装备物品 ID 列表 ====================
# 这些是原模组中有的装备配方对应的输出物品（items 命名空间 = pasterdream）
# 只处理这些物品的配方，其他跳过
EQUIPMENT_ITEMS = set([
    # ---- 铜工具 ----
    "copper_sword", "copper_shovel", "copper_hoe", "copper_pickaxe", "copper_axe",
    # ---- 铜盔甲 ----
    "copper_armor_helmet", "copper_armor_chestplate",
    "copper_armor_leggings", "copper_armor_boots",
    # ---- 钛工具 ----
    "titanium_sword", "titanium_pickaxe", "titanium_axe",
    "titanium_shovel", "titanium_hoe",
    # ---- 钛盔甲 ----
    "titanium_armor_helmet", "titanium_armor_chestplate",
    "titanium_armor_leggings", "titanium_armor_boots",
    # ---- 梦境工具 ----
    "dyedream_sword", "dyedream_sword_0", "dyedream_pickaxe",
    "dyedream_axe", "dyedream_shovel", "dyedream_hoe", "dyedream_hammer",
    # ---- 梦境盔甲 ----
    "dyedream_armor_helmet", "dyedream_armor_chestplate",
    "dyedream_armor_leggings", "dyedream_armor_boots",
    # ---- 熔金工具 ----
    "moltengold_sword", "moltengold_pickaxe", "moltengold_axe",
    "moltengold_shovel", "moltengold_hoe", "moltengold_wand",
    # ---- 真·熔金 ----
    "true_moltengold_sword", "true_moltengold_pickaxe", "true_moltengold_wand",
    # ---- 至真·熔金 ----
    "truest_moltengold_sword", "truest_moltengold_wand",
    # ---- Sculk 盔甲 ----
    "sculk_armor_helmet", "sculk_armor_chestplate",
    "sculk_armor_leggings", "sculk_armor_boots",
    # ---- 熔梦工具 ----
    "meltdream_pickaxe", "meltdream_axe", "meltdream_shovel", "meltdream_hoe",
    # ---- 特殊武器 ----
    "grass_sword", "true_grass_sword", "tide_sword", "true_tide_sword",
    # ---- 法杖 ----
    "mana_wand",
])


def is_equipment_recipe(recipe: dict) -> bool:
    """判断配方是否产出装备物品"""
    result = recipe.get("result", {})
    if isinstance(result, dict):
        item_id = result.get("id") or result.get("item", "")
    elif isinstance(result, str):
        item_id = result
    else:
        return False

    # 提取物品短ID（去掉命名空间前缀）
    if ":" in item_id:
        ns, path = item_id.split(":", 1)
    else:
        path = item_id

    return path in EQUIPMENT_ITEMS


def convert_recipe(recipe: dict) -> dict:
    """将 1.20 配方转换为 1.21 格式"""
    recipe = json.loads(json.dumps(recipe))  # deep copy

    # 转换 result 字段
    result = recipe.get("result", {})
    if isinstance(result, dict) and "item" in result:
        result["id"] = result.pop("item")
        result.setdefault("count", 1)

    # 转换 type 字段（如果需要）
    # minecraft:crafting_shaped / shapeless / smithing_transform / smelting 等保持原样

    return recipe


def get_output_item_id(recipe: dict) -> str:
    """获取配方的输出物品 ID"""
    result = recipe.get("result", {})
    if isinstance(result, dict):
        return result.get("id") or result.get("item", "")
    elif isinstance(result, str):
        return result
    return ""


def make_filename(item_id: str, recipe_type: str, existing: set) -> str:
    """
    为输出物品生成唯一的配方文件名。
    优先使用 item_id，若已存在则编号后缀。
    """
    # 提取短ID
    if ":" in item_id:
        path = item_id.split(":", 1)[1]
    else:
        path = item_id

    # 判断配方类型后缀
    type_suffix = {
        "minecraft:crafting_shaped": "",
        "minecraft:crafting_shapeless": "",
        "minecraft:smithing_transform": "_smithing",
        "minecraft:smelting": "_smelting",
        "minecraft:blasting": "_blasting",
        "minecraft:campfire_cooking": "_campfire",
        "minecraft:smoking": "_smoking",
        "minecraft:stonecutting": "_stonecutting",
    }
    suffix = type_suffix.get(recipe_type, "")

    # 针对已有多配方的物品，尝试 _1, _2, _smithing 等
    base_name = f"{path}{suffix}.json"

    # 如果 base_name 已经存在，且该物品已经有一个配方了，则加后缀 _2, _3 等
    # 但尽量使用带类型区分的名字

    return base_name


def main():
    if not os.path.isdir(ORIGINAL_RECIPE_DIR):
        print(f"[ERROR] 原配方目录不存在: {ORIGINAL_RECIPE_DIR}")
        return

    if not os.path.isdir(TARGET_RECIPE_DIR):
        print(f"[ERROR] 目标配方目录不存在: {TARGET_RECIPE_DIR}")
        return

    # 收集目标目录已有文件名
    existing_files = set(os.listdir(TARGET_RECIPE_DIR))

    # 收集原模组装备配方
    original_files = sorted(os.listdir(ORIGINAL_RECIPE_DIR))
    ported_count = 0
    skipped_non_equipment = 0
    skipped_parse_error = 0
    equip_recipes = []  # [(output_item, recipe_dict, original_filename)]

    for fname in original_files:
        if not fname.endswith(".json"):
            continue
        fpath = os.path.join(ORIGINAL_RECIPE_DIR, fname)
        try:
            with open(fpath, "r", encoding="utf-8") as f:
                recipe = json.load(f)
        except Exception as e:
            print(f"  [SKIP] 解析失败: {fname} → {e}")
            skipped_parse_error += 1
            continue

        if not is_equipment_recipe(recipe):
            skipped_non_equipment += 1
            continue

        # 收集到装备配方列表
        item_id = get_output_item_id(recipe)
        equip_recipes.append((item_id, recipe, fname))

    if not equip_recipes:
        print("[INFO] 未找到任何装备配方，任务结束。")
        return

    # 按输出物品分组，以确定编号
    from collections import defaultdict
    item_groups = defaultdict(list)
    for item_id, recipe, fname in equip_recipes:
        item_groups[item_id].append((recipe, fname))

    for item_id, recipes in sorted(item_groups.items()):
        # 提取短ID
        if ":" in item_id:
            short_id = item_id.split(":", 1)[1]
        else:
            short_id = item_id

        # ====== 清理阶段：删除目标目录中该物品的所有旧配方文件 ======
        for existing_fname in list(existing_files):
            # 匹配以 short_id 开头的 .json 文件
            if existing_fname.startswith(f"{short_id}.") or existing_fname.startswith(f"{short_id}_"):
                old_path = os.path.join(TARGET_RECIPE_DIR, existing_fname)
                try:
                    os.remove(old_path)
                    print(f"  [DEL] 删除旧配方: {existing_fname}")
                    existing_files.discard(existing_fname)
                except Exception as e:
                    print(f"  [WARN] 删除失败: {existing_fname} → {e}")

        for idx, (recipe, original_fname) in enumerate(recipes):
            try:
                converted = convert_recipe(recipe)

                # 确定目标文件名：用类型后缀区分配方
                rtype = recipe.get("type", "")
                if "smithing" in rtype:
                    suffix = "_smithing"
                elif "smelting" in rtype:
                    suffix = "_smelting"
                elif "blasting" in rtype:
                    suffix = "_blasting"
                elif "smoking" in rtype:
                    suffix = "_smoking"
                elif "campfire_cooking" in rtype:
                    suffix = "_campfire"
                elif "stonecutting" in rtype:
                    suffix = "_stonecutting"
                else:
                    suffix = ""

                # 文件名策略：
                # - smithing 配方 → {item}_smithing.json
                # - 若仅有 1 个 crafting 配方 → {item}.json
                # - 若有多个 crafting 配方 → {item}_1.json, {item}_2.json ...
                is_smithing = "smithing" in rtype
                if is_smithing:
                    fname_out = f"{short_id}_smithing.json"
                else:
                    # 统计该物品有多少个非 smithing 配方
                    crafting_recipes = [(r, f) for r, f in recipes if "smithing" not in r.get("type", "")]
                    if len(crafting_recipes) == 1:
                        fname_out = f"{short_id}.json"
                    else:
                        # 非 smithing 配方有多个，按出现顺序 _1, _2 ...
                        craft_index = [cr for cr in enumerate(recipes)
                                       if "smithing" not in cr[1][0].get("type", "")]
                        pos_in_craft = next(i for i, (ri, (r, f)) in enumerate(craft_index)
                                            if f == original_fname)
                        fname_out = f"{short_id}_{pos_in_craft + 1}.json"

                target_path = os.path.join(TARGET_RECIPE_DIR, fname_out)

                # 写入
                with open(target_path, "w", encoding="utf-8") as f:
                    json.dump(converted, f, indent=2, ensure_ascii=False)

                print(f"  [OK] {original_fname:30s} → {fname_out:40s} ({short_id})")
                ported_count += 1
                existing_files.add(fname_out)

            except Exception as e:
                print(f"  [ERR] {original_fname}: {e}")

    print(f"\n{'='*60}")
    print(f"  装备配方移植完成！")
    print(f"  总处理: {len(original_files)} 个文件")
    print(f"  装备配方: {len(equip_recipes)} 个 → 写出 {ported_count} 个")
    print(f"  跳过(非装备): {skipped_non_equipment} 个")
    print(f"  跳过(解析失败): {skipped_parse_error} 个")
    print(f"  目标目录: {TARGET_RECIPE_DIR}")
    print(f"{'='*60}")


if __name__ == "__main__":
    main()
