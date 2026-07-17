import json
import os

RECIPE_DIR = "PasterDream/src/main/resources/data/pasterdream/recipe"

def ensure_dir():
    os.makedirs(RECIPE_DIR, exist_ok=True)

def write_recipe(name, data):
    path = os.path.join(RECIPE_DIR, f"{name}.json")
    with open(path, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2)
    print(f"Generated: {name}.json")

def gen_tool_recipe(modid, tool_name, material_name, base_tool, ingredient_item, ingredient_count=2):
    ingredients = [
        {"item": f"minecraft:{base_tool}"},
    ] + [{"item": f"{modid}:{ingredient_item}"}] * ingredient_count + [
        {"item": f"{modid}:blackstick"}
    ]
    
    data = {
        "type": "minecraft:crafting_shapeless",
        "category": "equipment",
        "ingredients": ingredients,
        "result": {
            "id": f"{modid}:{tool_name}",
            "count": 1
        }
    }
    write_recipe(f"{material_name}_{tool_name.split('_')[-1]}", data)

def gen_armor_recipe(modid, armor_name, material_name, base_armor, ingredient_item, ingredient_count=2):
    ingredients = [
        {"item": f"minecraft:{base_armor}"},
    ] + [{"item": f"{modid}:{ingredient_item}"}] * ingredient_count + [
        {"item": f"{modid}:blackstick"}
    ]
    
    data = {
        "type": "minecraft:crafting_shapeless",
        "category": "equipment",
        "ingredients": ingredients,
        "result": {
            "id": f"{modid}:{armor_name}",
            "count": 1
        }
    }
    write_recipe(f"{material_name}_{armor_name.split('_')[-1]}", data)

def main():
    ensure_dir()
    modid = "pasterdream"
    
    tools_to_gen = [
        ("dyedream_axe", "dyedream", "diamond_axe", "dyedream_ingots"),
        ("dyedream_shovel", "dyedream", "diamond_shovel", "dyedream_ingots"),
        ("dyedream_hoe", "dyedream", "diamond_hoe", "dyedream_ingots"),
        ("moltengold_axe", "moltengold", "stone_axe", "moltengold_ingot"),
        ("moltengold_shovel", "moltengold", "stone_shovel", "moltengold_ingot"),
        ("moltengold_hoe", "moltengold", "stone_hoe", "moltengold_ingot"),
        ("meltdream_axe", "meltdream", "iron_axe", "meltdream_crystal"),
        ("meltdream_shovel", "meltdream", "iron_shovel", "meltdream_crystal"),
        ("meltdream_hoe", "meltdream", "iron_hoe", "meltdream_crystal"),
        ("shadow_erosion_axe", "shadow_erosion", "diamond_axe", "shadow_erosion"),
        ("shadow_erosion_shovel", "shadow_erosion", "diamond_shovel", "shadow_erosion"),
        ("shadow_erosion_hoe", "shadow_erosion", "diamond_hoe", "shadow_erosion"),
    ]
    
    for tool_name, material_name, base_tool, ingredient in tools_to_gen:
        gen_tool_recipe(modid, tool_name, material_name, base_tool, ingredient)
    
    armor_sets = [
        ("copper", "copper_ingot", "iron"),
        ("titanium", "titanium_ingot", "diamond"),
        ("sculk", "sculk_catalyst", "diamond"),
        ("dyedream", "dyedream_ingots", "diamond"),
        ("qym", "qym", "diamond"),
    ]
    
    armor_parts = ["helmet", "chestplate", "leggings", "boots"]
    
    for material_name, ingredient, base_material in armor_sets:
        for part in armor_parts:
            armor_name = f"{material_name}_armor_{part}"
            gen_armor_recipe(modid, armor_name, material_name, f"{base_material}_{part}", ingredient)
    
    print("\nDone! Generated all tool and armor recipes.")

if __name__ == "__main__":
    main()
