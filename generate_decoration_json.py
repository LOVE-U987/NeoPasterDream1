import json
import os

MOD_ID = "pasterdream"
FEATURE_TYPE = f"{MOD_ID}:generic_decor"

DECORATIONS = [
    {
        "name": "dyedream_crystal_cluster",
        "biome": "pasterdream:biome_dyedream_0",
        "step": "top_layer_modification",
        "rarity": 2,
        "config": {
            "type": "scatter",
            "body_block": {
                "type": "weighted_state_provider",
                "entries": [
                    {"data": {"Name": "pasterdream:meltdream_crystal_lamp"}, "weight": 40},
                    {"data": {"Name": "pasterdream:dyedream_bud_0"}, "weight": 30},
                    {"data": {"Name": "pasterdream:dyedream_bud_1"}, "weight": 20},
                    {"data": {"Name": "pasterdream:dyedream_bud_2"}, "weight": 10}
                ]
            },
            "top_block": {"type": "simple_state_provider", "state": {"Name": "pasterdream:meltdream_crystal_lamp"}},
            "min_height": 1,
            "max_height": 1,
            "cluster_size": 5,
            "check_hang": True
        }
    },
    {
        "name": "meltdream_crystal_pillar",
        "biome": "pasterdream:biome_dyedream_0",
        "step": "top_layer_modification",
        "rarity": 5,
        "config": {
            "type": "pillar",
            "body_block": {"type": "simple_state_provider", "state": {"Name": "pasterdream:dyedreamquartz_block"}},
            "top_block": {"type": "simple_state_provider", "state": {"Name": "pasterdream:meltdream_crystal_lamp"}},
            "min_height": 6,
            "max_height": 12,
            "base_width": 2,
            "top_width": 1,
            "crystal_chance": 0.2,
            "crystal_block": {"type": "simple_state_provider", "state": {"Name": "pasterdream:dyedream_bud_0"}},
            "debris_block": {"type": "simple_state_provider", "state": {"Name": "pasterdream:dyedreamquartz_block"}},
            "debris_count": 4,
            "debris_radius": 2,
            "region_check": True,
            "region_threshold": 0.3,
            "check_hang": True
        }
    },
    {
        "name": "floating_cloud_island",
        "biome": "pasterdream:biome_dyedream_0",
        "step": "top_layer_modification",
        "rarity": 4,
        "config": {
            "type": "blob",
            "body_block": {
                "type": "weighted_state_provider",
                "entries": [
                    {"data": {"Name": "pasterdream:cloud"}, "weight": 70},
                    {"data": {"Name": "pasterdream:thick_cloud"}, "weight": 25},
                    {"data": {"Name": "pasterdream:meltdream_crystal_lamp"}, "weight": 5}
                ]
            },
            "top_block": {"type": "simple_state_provider", "state": {"Name": "pasterdream:cloud"}},
            "cluster_size": 80,
            "base_radius": 5,
            "top_radius": 0,
            "y_radius": 2,
            "irregularity": 0.25,
            "fill_hang": False,
            "region_check": True,
            "region_threshold": 0.3
        }
    },
    {
        "name": "calcite_crystal_garden",
        "biome": "pasterdream:biome_dyedream_1",
        "step": "top_layer_modification",
        "rarity": 2,
        "config": {
            "type": "scatter",
            "body_block": {
                "type": "weighted_state_provider",
                "entries": [
                    {"data": {"Name": "minecraft:calcite"}, "weight": 35},
                    {"data": {"Name": "pasterdream:polished_calcite"}, "weight": 25},
                    {"data": {"Name": "pasterdream:pinkagaric_0"}, "weight": 20},
                    {"data": {"Name": "pasterdream:pinkagaric_1"}, "weight": 15},
                    {"data": {"Name": "pasterdream:dyedream_bud_0"}, "weight": 5}
                ]
            },
            "top_block": {"type": "simple_state_provider", "state": {"Name": "minecraft:calcite"}},
            "min_height": 1,
            "max_height": 1,
            "cluster_size": 8,
            "check_hang": True
        }
    },
    {
        "name": "warm_crystal_spike",
        "biome": "pasterdream:biome_dyedream_1",
        "step": "top_layer_modification",
        "rarity": 4,
        "config": {
            "type": "spike",
            "body_block": {
                "type": "weighted_state_provider",
                "entries": [
                    {"data": {"Name": "minecraft:calcite"}, "weight": 50},
                    {"data": {"Name": "pasterdream:dyedreamquartz_block"}, "weight": 35},
                    {"data": {"Name": "pasterdream:smooth_dyedreamquartz_block"}, "weight": 15}
                ]
            },
            "top_block": {"type": "simple_state_provider", "state": {"Name": "pasterdream:meltdream_crystal_lamp"}},
            "min_height": 10,
            "max_height": 20,
            "base_radius": 3,
            "top_radius": 0,
            "crystal_chance": 0.25,
            "crystal_block": {
                "type": "weighted_state_provider",
                "entries": [
                    {"data": {"Name": "pasterdream:dyedream_bud_0"}, "weight": 40},
                    {"data": {"Name": "pasterdream:dyedream_bud_1"}, "weight": 30},
                    {"data": {"Name": "pasterdream:pinkagaric_3"}, "weight": 30}
                ]
            },
            "region_check": True,
            "region_threshold": 0.3,
            "check_hang": True
        }
    },
    {
        "name": "pinkagaric_forest",
        "biome": "pasterdream:biome_dyedream_mushroom_plains",
        "step": "vegetal_decoration",
        "rarity": 1,
        "config": {
            "type": "scatter",
            "body_block": {
                "type": "weighted_state_provider",
                "entries": [
                    {"data": {"Name": "pasterdream:pinkagaric_0"}, "weight": 30},
                    {"data": {"Name": "pasterdream:pinkagaric_1"}, "weight": 25},
                    {"data": {"Name": "pasterdream:pinkagaric_2"}, "weight": 20},
                    {"data": {"Name": "pasterdream:pinkagaric_3"}, "weight": 15},
                    {"data": {"Name": "pasterdream:dyedream_bud_0"}, "weight": 10}
                ]
            },
            "top_block": {"type": "simple_state_provider", "state": {"Name": "pasterdream:pinkagaric_0"}},
            "min_height": 1,
            "max_height": 1,
            "cluster_size": 10,
            "check_hang": True
        }
    }
]


def generate_configured_feature(decor):
    return {
        "type": FEATURE_TYPE,
        "config": decor["config"]
    }


def generate_placed_feature(decor):
    return {
        "feature": f"{MOD_ID}:{decor['name']}",
        "placement": [
            {"type": "minecraft:rarity_filter", "chance": decor["rarity"]},
            {"type": "minecraft:in_square"},
            {"type": "minecraft:heightmap", "heightmap": "MOTION_BLOCKING"},
            {"type": "minecraft:biome"}
        ]
    }


def generate_biome_modifier(decor):
    biome = decor["biome"]
    step = decor["step"]
    
    if biome.startswith("#"):
        simple_name = f"tag_{biome.split(':')[1]}"
    elif ":" in biome:
        simple_name = biome.split(":")[1]
    else:
        simple_name = biome
    
    filename = f"{simple_name}_{step}.json"
    return {
        "filename": filename,
        "content": {
            "type": "neoforge:add_features",
            "biomes": biome,
            "features": [f"{MOD_ID}:{decor['name']}"],
            "step": step
        }
    }


def main():
    base_path = os.path.join("PasterDream", "src", "main", "resources", "data", MOD_ID)
    
    configured_path = os.path.join(base_path, "worldgen", "configured_feature")
    placed_path = os.path.join(base_path, "worldgen", "placed_feature")
    biome_modifier_path = os.path.join(base_path, "neoforge", "biome_modifier")
    
    os.makedirs(configured_path, exist_ok=True)
    os.makedirs(placed_path, exist_ok=True)
    os.makedirs(biome_modifier_path, exist_ok=True)
    
    biome_modifiers = {}
    
    for decor in DECORATIONS:
        name = decor["name"]
        
        configured_json = generate_configured_feature(decor)
        with open(os.path.join(configured_path, f"{name}.json"), "w", encoding="utf-8") as f:
            json.dump(configured_json, f, indent=2, ensure_ascii=False)
        print(f"✅ Generated configured_feature: {name}.json")
        
        placed_json = generate_placed_feature(decor)
        with open(os.path.join(placed_path, f"{name}.json"), "w", encoding="utf-8") as f:
            json.dump(placed_json, f, indent=2, ensure_ascii=False)
        print(f"✅ Generated placed_feature: {name}.json")
        
        modifier = generate_biome_modifier(decor)
        filename = modifier["filename"]
        if filename not in biome_modifiers:
            biome_modifiers[filename] = {
                "type": "neoforge:add_features",
                "biomes": decor["biome"],
                "features": [],
                "step": decor["step"]
            }
        biome_modifiers[filename]["features"].append(f"{MOD_ID}:{name}")
    
    for filename, content in biome_modifiers.items():
        with open(os.path.join(biome_modifier_path, filename), "w", encoding="utf-8") as f:
            json.dump(content, f, indent=2, ensure_ascii=False)
        print(f"✅ Generated biome_modifier: {filename}")
    
    print(f"\n🎉 完成! 共生成 {len(DECORATIONS)} 个装饰物定义")


if __name__ == "__main__":
    main()