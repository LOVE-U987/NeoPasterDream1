import os
import json

SRC_DIR = "libs/FixPasterDream-main/src/main/resources/data/pasterdream/loot_tables/chests"
DST_DIR = "PasterDream/src/main/resources/data/pasterdream/loot_table/chests"

os.makedirs(DST_DIR, exist_ok=True)

for filename in os.listdir(SRC_DIR):
    if not filename.endswith('.json'):
        continue
    
    src_path = os.path.join(SRC_DIR, filename)
    dst_path = os.path.join(DST_DIR, filename)
    
    with open(src_path, 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    for pool in data.get('pools', []):
        for entry in pool.get('entries', []):
            for func in entry.get('functions', []):
                if 'function' in func and not func['function'].startswith('minecraft:'):
                    func['function'] = 'minecraft:' + func['function']
    
    data['random_sequence'] = data.get('random_sequence', '').replace('loot_tables', 'loot_table')
    
    with open(dst_path, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
    
    print(f"Migrated: {filename}")

print(f"\nSuccessfully migrated {len(os.listdir(SRC_DIR))} chest loot tables!")
