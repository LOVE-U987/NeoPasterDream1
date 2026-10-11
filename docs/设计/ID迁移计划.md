# ID 迁移计划

> 本文档由 `tools/scan_numeric_registrations.py` 自动生成，
> 列出项目内所有注册名带数字后缀（`_0`/`_1`/`_2`/`_3`…）的内容，
> 作为 ID 重命名 / 迁移的基线清单。
> 重新生成: `python tools/scan_numeric_registrations.py`

## 判定口径

| 项 | 说明 |
|----|------|
| 命中规则 | 名称含 `_<数字>` 段：结尾（`flower_1`）或后接 `_`（`crack_0_particle`） |
| 代码侧范围 | 5 模块 `src/main/java/**/registry/**` 的字符串字面量 |
| 数据包侧范围 | `src/main/resources/data/**` 下的注册目录文件名 |
| 已排除 | `libs/`、`build/`、`docs/deprecated/`、`packs/`（原版 UI 覆盖包） |
| 引用排除 | `DebugStructureWandItem` / `DebugDecorWandItem` / `LootstableCreateItem` 的结构 ID 参数不算注册名 |

---

## 一、代码侧注册

### 方块（85 个）

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDBlocks.java`（8）:

- `dream_spawner_0`
- `dyedream_bud_0`
- `dyedream_bud_1`
- `dyedream_bud_2`
- `ice_bud_0`
- `little_purple_doll_0`
- `qin_doll_0`
- `shadow_light_0`

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/blocks/PDBlocksDolls.java`（2）:

- `little_purple_doll_0`
- `qin_doll_0`

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/blocks/PDBlocksDungeon.java`（24）:

- `shadow_arena_block_0`
- `shadow_dungeon_block_0`
- `shadow_dungeon_block_1`
- `shadow_dungeon_block_2`
- `shadow_dungeon_block_3`
- `shadow_dungeon_block_4`
- `shadow_dungeon_block_5`
- `shadow_dungeon_block_6`
- `shadow_dungeon_door_0`
- `shadow_dungeon_door_1`
- `shadow_dungeon_key_0`
- `shadow_dungeon_key_1`
- `shadow_fissure_0`
- `shadow_fissure_1`
- `shadow_fissure_2`
- `shadow_fissure_3`
- `shadow_fissure_4`
- `shadow_fissure_5`
- `shadowdungeondoor_2`
- `shadowdungeondoor_3`
- `shadowshelf_0`
- `shadowshelf_1`
- `shadowshelf_2`
- `shadowshelf_3`

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/blocks/PDBlocksFurniture.java`（2）:

- `claypan_1`
- `shadow_trap_0`

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/blocks/PDBlocksMisc.java`（5）:

- `clay_pot_0`
- `claypan_0`
- `claypan_2`
- `dream_spawner_1`
- `memento_item_11`

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/blocks/PDBlocksResearch.java`（1）:

- `dream_spawner_0`

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/blocks/PDBlocksVegetation.java`（43）:

- `dyedream_bud_0`
- `dyedream_bud_1`
- `dyedream_bud_2`
- `flower_1`
- `flower_2`
- `flower_3`
- `flower_5`
- `flower_6`
- `flower_7`
- `flower_8`
- `flower_9`
- `flower_10`
- `flower_11`
- `flower_12`
- `flower_13`
- `flower_14`
- `flower_15`
- `flower_16`
- `flower_17`
- `flower_18`
- `grass_1`
- `grass_2`
- `grass_3`
- `grass_4`
- `grass_5`
- `grass_6`
- `grass_7`
- `grass_8`
- `grass_9`
- `grass_10`
- `grass_11`
- `grass_12`
- `grass_13`
- `grass_14`
- `grass_15`
- `ice_bud_0`
- `pebble_0`
- `pinkagaric_0`
- `pinkagaric_1`
- `pinkagaric_2`
- `pinkagaric_3`
- `shadow_light_0`
- `vine_0`

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/blocks/PDBlocksWindJourney.java`（8）:

- `armor_wreck_block_0`
- `armor_wreck_block_1`
- `armor_wreck_block_2`
- `armor_wreck_block_3`
- `armor_wreck_block_4`
- `windmoor_leaves_0`
- `windmoor_leaves_1`
- `windmoor_leaves_2`

### 方块实体（7 个）

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDBlockEntities.java`（5）:

- `dream_spawner_0`
- `flower_17`
- `grass_14`
- `little_purple_doll_0`
- `qin_doll_0`

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDBlockEntitiesFurniture.java`（2）:

- `claypan_1`
- `shadow_trap_0`

### 物品（186 个）

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/items/PDItemsBlocks.java`（81）:

- `armor_wreck_block_0`
- `armor_wreck_block_1`
- `armor_wreck_block_2`
- `armor_wreck_block_3`
- `armor_wreck_block_4`
- `clay_pot_0`
- `claypan_0`
- `claypan_2`
- `dream_spawner_0`
- `dream_spawner_1`
- `dyedream_bud_0`
- `dyedream_bud_1`
- `dyedream_bud_2`
- `flower_1`
- `flower_2`
- `flower_3`
- `flower_5`
- `flower_6`
- `flower_7`
- `flower_8`
- `flower_9`
- `flower_10`
- `flower_11`
- `flower_12`
- `flower_13`
- `flower_14`
- `flower_15`
- `flower_16`
- `flower_17`
- `flower_18`
- `grass_1`
- `grass_2`
- `grass_3`
- `grass_4`
- `grass_5`
- `grass_6`
- `grass_7`
- `grass_8`
- `grass_9`
- `grass_10`
- `grass_11`
- `grass_12`
- `grass_13`
- `grass_14`
- `grass_15`
- `ice_bud_0`
- `memento_item_11`
- `pebble_0`
- `pinkagaric_0`
- `pinkagaric_1`
- `pinkagaric_2`
- `pinkagaric_3`
- `shadow_arena_block_0`
- `shadow_dungeon_block_0`
- `shadow_dungeon_block_1`
- `shadow_dungeon_block_2`
- `shadow_dungeon_block_3`
- `shadow_dungeon_block_4`
- `shadow_dungeon_block_5`
- `shadow_dungeon_block_6`
- `shadow_dungeon_door_0`
- `shadow_dungeon_door_1`
- `shadow_dungeon_key_0`
- `shadow_dungeon_key_1`
- `shadow_fissure_0`
- `shadow_fissure_1`
- `shadow_fissure_2`
- `shadow_fissure_3`
- `shadow_fissure_4`
- `shadow_fissure_5`
- `shadow_light_0`
- `shadowdungeondoor_2`
- `shadowdungeondoor_3`
- `shadowshelf_0`
- `shadowshelf_1`
- `shadowshelf_2`
- `shadowshelf_3`
- `vine_0`
- `windmoor_leaves_0`
- `windmoor_leaves_1`
- `windmoor_leaves_2`

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/items/PDItemsCurios.java`（10）:

- `fire_0_necklace`
- `health_0_necklace`
- `hithard_0_ring`
- `hithard_1_ring`
- `meltdream_energy_0_ring`
- `rabbit_0_necklace`
- `red_dew_0_ring`
- `red_dew_1_ring`
- `red_dew_2_ring`
- `red_dew_3_ring`

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/items/PDItemsDolls.java`（2）:

- `little_purple_doll_0`
- `qin_doll_0`

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/items/PDItemsFoods.java`（1）:

- `rage_elixir_0`

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/items/PDItemsFunctional.java`（77）:

- `blueprint_0`
- `blueprint_1`
- `calle_card_0`
- `calle_card_1`
- `calle_card_2`
- `calle_card_3`
- `calle_card_4`
- `calle_card_5`
- `calle_card_6`
- `calle_card_7`
- `calle_card_8`
- `calle_card_9`
- `debug_wand_desert_fortress_0`
- `debug_wand_dream_church_0`
- `debug_wand_dream_church_1`
- `debug_wand_dream_church_2`
- `debug_wand_dream_church_3`
- `debug_wand_dream_church_4`
- `debug_wand_dream_church_5`
- `debug_wand_dream_church_6`
- `debug_wand_dream_church_7`
- `debug_wand_dream_church_8`
- `debug_wand_dream_church_9`
- `debug_wand_dream_church_10`
- `debug_wand_dream_wishingtree_0`
- `debug_wand_dream_wishingtree_1`
- `debug_wand_dyedream_campsite_0`
- `debug_wand_dyedream_laboratory_0`
- `debug_wand_dyedream_pavilion_0`
- `debug_wand_dyedream_pavilion_1`
- `debug_wand_dyedream_pavilion_2`
- `debug_wand_dyedream_tower_0`
- `debug_wand_dyedream_tower_1`
- `debug_wand_garden_decryption_0`
- `debug_wand_garden_decryption_1`
- `debug_wand_garden_decryption_2`
- `debug_wand_meltdream_liquid_well_0`
- `debug_wand_meltdream_liquid_well_1`
- `debug_wand_pinkagaric_0`
- `debug_wand_pinkagaric_1`
- `debug_wand_pinkagaric_2`
- `debug_wand_pinkagaric_3`
- `debug_wand_traveler_house_0`
- `debug_wand_traveler_house_1`
- `debug_wand_traveler_house_2`
- `debug_wand_worldtree_0`
- `deep_treasure_0`
- `deep_treasure_1`
- `dream_coin_0`
- `dream_coin_1`
- `heart_chocolate_0`
- `heart_chocolate_1`
- `heart_chocolate_2`
- `lootstable_create_0`
- `lootstable_create_1`
- `lootstable_create_2`
- `lootstable_create_3`
- `lootstable_create_4`
- `lootstable_create_5`
- `lootstable_create_6`
- `lootstable_create_7`
- `lootstable_create_8`
- `lootstable_create_9`
- `meltdream_crystal_0`
- `memento_item_01`
- `memento_item_02`
- `memento_item_03`
- `memento_item_04`
- `memento_item_05`
- `memento_item_06`
- `memento_item_07`
- `memento_item_08`
- `memento_item_09`
- `memento_item_10`
- `memory_gem_0`
- `red_dew_0`
- `storage_bag_0`

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/items/PDItemsFurniture.java`（2）:

- `claypan_1`
- `shadow_trap_0`

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/items/PDItemsMaterials.java`（7）:

- `brokennotes_0`
- `enhance_stone_0`
- `enhance_stone_1`
- `sword_embryo_0`
- `tabitem_1`
- `tabitem_2`
- `unknownnotes_0`

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/items/PDItemsMusic.java`（1）:

- `wind_journey_1_disc`

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/items/PDItemsSpawnEggs.java`（4）:

- `aaroncos_lefthand_0`
- `aaroncos_righthand_0`
- `shadow_npc_0`
- `shadow_squeal_ghost_0`

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/items/PDItemsTools.java`（1）:

- `dyedream_sword_0`

### 实体（4 个）

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDEntities.java`（4）:

- `aaroncos_lefthand_0`
- `aaroncos_righthand_0`
- `shadow_npc_0`
- `shadow_squeal_ghost_0`

### 状态效果（33 个）

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDEffects.java`（15）:

- `cook_buff_0`
- `counterattack_buff_0`
- `counterattack_buff_1`
- `deadwind_buff_0`
- `deadwind_buff_1`
- `deadwind_buff_2`
- `dreamharp_of_wanderer_buff_0`
- `dreamharp_of_wanderer_buff_1`
- `dreamharp_of_wanderer_buff_2`
- `oppression_buff_0`
- `rest_buff_0`
- `rest_buff_in_dark_0`
- `tailwind_buff_0`
- `tailwind_buff_1`
- `tailwind_buff_2`

`PasterDreamSanity/src/main/java/com/pasterdream/pasterdreammod/pasterdreamsanity/registry/PDSanityEffects.java`（11）:

- `cheerup_buff_0`
- `cheerup_buff_1`
- `cheerup_buff_2`
- `cheerup_buff_3`
- `insand_buff_0`
- `insand_buff_1`
- `insand_buff_2`
- `insand_buff_3`
- `insand_buff_4`
- `insand_buff_5`
- `insand_buff_6`

`PasterDreamSpells/src/main/java/com/pasterdream/pasterdreammod/pasterdreamspells/registry/PDSpellsEffects.java`（7）:

- `fury_spell_buff_0`
- `fury_spell_buff_1`
- `fury_spell_buff_2`
- `fury_spell_buff_3`
- `fury_spell_buff_4`
- `ice_spell_buff_0`
- `ice_spell_buff_1`

### 音效（8 个）

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDSounds.java`（7）:

- `ding_0`
- `meltdream_chest_0`
- `shadow_biome_0`
- `shadow_music_0`
- `shadow_trap_0`
- `stone_break_0`
- `wind_knight_skill_0`

`PasterDreamSpells/src/main/java/com/pasterdream/pasterdreammod/pasterdreamspells/registry/PDSpellsSounds.java`（1）:

- `fury_spell_0`

### 粒子（10 个）

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDParticles.java`（8）:

- `attack_0_particle`
- `buff_0_particle`
- `crack_0_particle`
- `dust_0_particle`
- `dyedream_0_particle`
- `fox_fire_0_particle`
- `fox_fire_1_particle`
- `snowflake_0_particle`

`PasterDreamSpells/src/main/java/com/pasterdream/pasterdreammod/pasterdreamspells/registry/PDSpellsParticles.java`（2）:

- `snowflake_1_particle`
- `spell_snowflake_0_particle`

### 菜单（3 个）

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDMenus.java`（1）:

- `storage_bag_0`

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDMenusBlueprint.java`（1）:

- `blueprint_gui_0`

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDMenusDreamnotes.java`（1）:

- `dreamnotes_gui_0`

### 进度（45 个）

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDAdvancements.java`（45）:

- `achievement_a_0`
- `achievement_a_1`
- `achievement_adventure_0`
- `achievement_b_0`
- `achievement_b_1`
- `achievement_b_2`
- `achievement_b_3`
- `achievement_c_0`
- `achievement_c_1`
- `achievement_c_2`
- `achievement_c_3`
- `achievement_c_4`
- `achievement_d_0`
- `achievement_end_0`
- `achievement_hide_0`
- `achievement_hide_1`
- `achievement_hide_2`
- `achievement_hide_3`
- `achievement_hide_4`
- `achievement_hide_5`
- `achievement_hide_6`
- `achievement_hide_7`
- `achievement_hide_8`
- `achievement_hide_9`
- `achievement_hide_10`
- `achievement_hide_11`
- `achievement_hide_12`
- `achievement_hide_13`
- `achievement_hide_14`
- `achievement_hide_15`
- `achievement_hide_16`
- `achievement_nether_0`
- `achievement_shadow_a_0`
- `achievement_shadow_a_1`
- `achievement_shadow_b_0`
- `achievement_shadow_c_0`
- `achievement_shadow_d_0`
- `achievement_shadow_e_0`
- `achievement_shadow_npc_0`
- `achievement_shadow_npc_1`
- `achievement_shadow_npc_2`
- `achievement_shadow_npc_3`
- `achievement_shadow_npc_4`
- `achievement_shadow_npc_5`
- `achievement_special_0`

### 结构/遗迹（46 个）

`PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDRuinsRegistration.java`（46）:

- `desert_cottage_0`
- `desert_cottage_0_set`
- `desert_fortress_0`
- `desert_fortress_0_set`
- `dream_wishingtree_0`
- `dream_wishingtree_0_set`
- `dream_wishingtree_1`
- `dream_wishingtree_1_set`
- `dyedream_campsite_0`
- `dyedream_campsite_0_set`
- `dyedream_laboratory_0`
- `dyedream_laboratory_0_set`
- `dyedream_pavilion_0`
- `dyedream_pavilion_0_set`
- `dyedream_pavilion_1`
- `dyedream_pavilion_1_set`
- `dyedream_pavilion_2`
- `dyedream_pavilion_2_set`
- `dyedream_tower_0`
- `dyedream_tower_0_set`
- `dyedream_tower_1`
- `dyedream_tower_1_set`
- `dyedream_worldtree_0`
- `dyedream_worldtree_0_set`
- `dyedream_worldtree_1`
- `dyedream_worldtree_1_set`
- `garden_decryption_0`
- `garden_decryption_0_set`
- `garden_decryption_1`
- `garden_decryption_1_set`
- `garden_decryption_2`
- `garden_decryption_2_set`
- `meltdream_liquid_well_0`
- `meltdream_liquid_well_0_set`
- `meltdream_liquid_well_1`
- `meltdream_liquid_well_1_set`
- `struct_dyedream_crack_0`
- `struct_dyedream_crack_1`
- `struct_dyedream_crack_1_set`
- `traveler_house_0`
- `traveler_house_0_set`
- `traveler_house_1`
- `traveler_house_1_set`
- `traveler_house_2`
- `traveler_house_2_set`
- `windmoor_tree_0`

> 代码侧命中总计: **427** 个（跨文件去重前按分类分别统计）

---

## 二、数据包侧注册

### 群系（9 个）

- `biome_dyedream_0`
- `biome_dyedream_1`
- `biome_dyedream_2`
- `biome_dyedream_3`
- `biome_shadow_0`
- `biome_shadow_1`
- `biome_shadow_2`
- `wind_journey_biome_0`
- `wind_journey_biome_1`

### 进度（55 个）

```
achievement_a_0
achievement_a_1
achievement_adventure_0
achievement_b_0
achievement_b_1
achievement_b_2
achievement_b_3
achievement_c_0
achievement_c_1
achievement_c_2
achievement_c_3
achievement_c_4
achievement_d_0
achievement_end_0
achievement_hide_0
achievement_hide_1
achievement_hide_2
achievement_hide_3
achievement_hide_4
achievement_hide_5
achievement_hide_6
achievement_hide_7
achievement_hide_8
achievement_hide_9
achievement_hide_10
achievement_hide_11
achievement_hide_12
achievement_hide_13
achievement_hide_14
achievement_hide_15
achievement_hide_16
achievement_nether_0
achievement_shadow_a_0
achievement_shadow_a_1
achievement_shadow_b_0
achievement_shadow_c_0
achievement_shadow_d_0
achievement_shadow_e_0
achievement_shadow_npc_0
achievement_shadow_npc_1
achievement_shadow_npc_2
achievement_shadow_npc_3
achievement_shadow_npc_4
achievement_shadow_npc_5
achievement_special_0
achievement_treasure_a_0
achievement_treasure_a_1
achievement_treasure_a_2
achievement_treasure_a_3
achievement_treasure_a_4
achievement_treasure_a_5
achievement_treasure_a_6
achievement_treasure_a_7
achievement_treasure_a_8
achievement_treasure_b_0
```

### 结构（103 个）

```
big_bubbles_0
big_bubbles_1
big_bubbles_2
big_bubbles_3
big_bubbles_4
big_bubbles_5
big_bubbles_6
big_bubbles_7
bocchi_0
bocchi_1
breakwind_curtain_0
crystal_ball_0
crystal_ball_1
desert_cottage_0
desert_fortress_0
dream_church_0
dream_church_1
dream_church_2
dream_church_3
dream_church_4
dream_church_5
dream_church_6
dream_church_7
dream_church_8
dream_church_9
dream_church_10
dream_wishingtree_0
dream_wishingtree_1
dyedream_campsite_0
dyedream_laboratory_0
dyedream_pavilion_0
dyedream_pavilion_1
dyedream_pavilion_2
dyedream_tower_0
dyedream_tower_1
dyedream_worldtree_0
dyedream_worldtree_1
fisherman_hut_0
fisherman_hut_1
garden_decryption_0
garden_decryption_1
garden_decryption_2
hot_air_balloon_0
hot_air_balloon_1
hot_air_balloon_2
hot_air_balloon_3
hot_air_balloon_4
hot_air_balloon_5
hot_air_balloon_6
hot_air_balloon_7
lifecrystal_cave_0
lost_sword_0
meltdream_liquid_well_0
meltdream_liquid_well_1
pinkagaric_house_0
pinkagaric_house_1
pinkagaric_house_2
pinkagaric_house_3
shadow_chain_0
shadow_chain_1
shadow_chain_2
shadow_chain_3
shadow_chain_4
shadow_foundry_0
shadow_foundry_1
shadow_foundry_2
shadow_fungus_house_0
shadow_fungus_house_1
shadow_fungus_nest_0
shadow_fungus_nest_1
shadow_fungus_nest_2
shadow_hand_0
shadow_shelter_0
shadow_shelter_1
shadow_underground_workroom_0
shadow_underground_workroom_1
small_ballon_0
small_ballon_1
small_ballon_2
small_ballon_3
small_ballon_4
small_ballon_5
small_ballon_6
small_ballon_7
small_ballon_8
small_ballon_9
small_ballon_10
stone_pillar_0
stone_pillar_1
stone_pillar_sky_0
stone_pillar_sky_1
stone_pillar_sky_2
struct_dyedream_crack_0
struct_dyedream_crack_1
traveler_house_0
traveler_house_1
traveler_house_2
warped_relic_0
wind_infested_stone_0
wind_infested_stone_1
wind_island_0
wind_pond_0
windmoor_tree_0
```

### 结构集（26 个）

- `bocchi_0`
- `bocchi_1`
- `breakwind_curtain_0`
- `crystal_ball_0`
- `crystal_ball_1`
- `desert_cottage_0_set`
- `desert_fortress_0_set`
- `dyedream_campsite_0_set`
- `dyedream_laboratory_0_set`
- `dyedream_worldtree_0_set`
- `dyedream_worldtree_1_set`
- `garden_decryption_0_set`
- `garden_decryption_1_set`
- `garden_decryption_2_set`
- `lifecrystal_cave_0`
- `lost_sword_0`
- `shadow_hand_0`
- `stone_pillar_sky_0`
- `stone_pillar_sky_1`
- `stone_pillar_sky_2`
- `struct_dyedream_crack_0`
- `struct_dyedream_crack_1_set`
- `warped_relic_0`
- `wind_island_0`
- `wind_pond_0`
- `windmoor_tree_0`

### 模板池（103 个）

```
big_bubbles_0
big_bubbles_1
big_bubbles_2
big_bubbles_3
big_bubbles_4
big_bubbles_5
big_bubbles_6
big_bubbles_7
bocchi_0
bocchi_1
breakwind_curtain_0
crystal_ball_0
crystal_ball_1
desert_cottage_0
desert_fortress_0
dream_church_0
dream_church_1
dream_church_2
dream_church_3
dream_church_4
dream_church_5
dream_church_6
dream_church_7
dream_church_8
dream_church_9
dream_church_10
dream_wishingtree_0
dream_wishingtree_1
dyedream_campsite_0
dyedream_laboratory_0
dyedream_pavilion_0
dyedream_pavilion_1
dyedream_pavilion_2
dyedream_tower_0
dyedream_tower_1
dyedream_worldtree_0
dyedream_worldtree_1
fisherman_hut_0
fisherman_hut_1
garden_decryption_0
garden_decryption_1
garden_decryption_2
hot_air_balloon_0
hot_air_balloon_1
hot_air_balloon_2
hot_air_balloon_3
hot_air_balloon_4
hot_air_balloon_5
hot_air_balloon_6
hot_air_balloon_7
lifecrystal_cave_0
lost_sword_0
meltdream_liquid_well_0
meltdream_liquid_well_1
pinkagaric_house_0
pinkagaric_house_1
pinkagaric_house_2
pinkagaric_house_3
shadow_chain_0
shadow_chain_1
shadow_chain_2
shadow_chain_3
shadow_chain_4
shadow_foundry_0
shadow_foundry_1
shadow_foundry_2
shadow_fungus_house_0
shadow_fungus_house_1
shadow_fungus_nest_0
shadow_fungus_nest_1
shadow_fungus_nest_2
shadow_hand_0
shadow_shelter_0
shadow_shelter_1
shadow_underground_workroom_0
shadow_underground_workroom_1
small_ballon_0
small_ballon_1
small_ballon_2
small_ballon_3
small_ballon_4
small_ballon_5
small_ballon_6
small_ballon_7
small_ballon_8
small_ballon_9
small_ballon_10
stone_pillar_0
stone_pillar_1
stone_pillar_sky_0
stone_pillar_sky_1
stone_pillar_sky_2
struct_dyedream_crack_0
struct_dyedream_crack_1
traveler_house_0
traveler_house_1
traveler_house_2
warped_relic_0
wind_infested_stone_0
wind_infested_stone_1
wind_island_0
wind_pond_0
windmoor_tree_0
```

### 配置地物（70 个）

```
biome_dyedream_0_tree
biome_dyedream_1_tree
biome_dyedream_2_tree
flower_1
flower_2
flower_3
flower_5
flower_6
flower_7
flower_8
flower_9
flower_10
flower_11
flower_12
flower_13
flower_14
flower_15
flower_16
flower_17
flower_18
grass_1
grass_2
grass_3
grass_4
grass_5
grass_6
grass_7
grass_8
grass_9
grass_10
grass_11
grass_12
grass_13
grass_14
grass_15
ground_feature_dyedream_0
ground_feature_dyedream_1
ground_feature_dyedream_2
ground_feature_dyedream_3
ground_feature_dyedream_4
ground_feature_dyedream_5
ground_feature_dyedream_6
ground_feature_dyedream_7
ground_feature_dyedream_8
ground_feature_dyedream_9
ground_feature_dyedream_10
ground_feature_dyedream_11
ground_feature_dyedream_12
ground_feature_dyedream_13
ground_feature_dyedream_14
ground_feature_dyedream_15
ground_feature_shadow_0
ground_feature_shadow_1
ground_feature_shadow_2
ground_feature_shadow_3
ground_feature_shadow_4
ground_feature_shadow_5
ground_feature_shadow_6
ground_feature_shadow_7
ground_feature_wind_journey_0
ground_feature_wind_journey_1
ground_feature_wind_journey_2
ground_feature_wind_journey_3
ground_feature_wind_journey_4
ground_feature_wind_journey_5
ground_feature_wind_journey_6
ground_overworld_0
patch_pinkagaric_0
patch_pinkagaric_1
vine_0
```

### 放置地物（68 个）

```
biome_dyedream_0_tree
biome_dyedream_1_tree
biome_dyedream_2_tree
flower_1
flower_2
flower_3
flower_5
flower_6
flower_7
flower_8
flower_9
flower_10
flower_11
flower_12
flower_13
flower_14
flower_15
flower_16
flower_17
flower_18
grass_1
grass_2
grass_3
grass_4
grass_5
grass_6
grass_7
grass_8
grass_9
grass_10
grass_11
grass_12
grass_13
grass_14
grass_15
ground_feature_dyedream_0
ground_feature_dyedream_1
ground_feature_dyedream_2
ground_feature_dyedream_3
ground_feature_dyedream_4
ground_feature_dyedream_5
ground_feature_dyedream_6
ground_feature_dyedream_7
ground_feature_dyedream_8
ground_feature_dyedream_9
ground_feature_dyedream_10
ground_feature_dyedream_11
ground_feature_dyedream_12
ground_feature_dyedream_13
ground_feature_dyedream_14
ground_feature_dyedream_15
ground_feature_shadow_0
ground_feature_shadow_1
ground_feature_shadow_2
ground_feature_shadow_3
ground_feature_shadow_4
ground_feature_shadow_5
ground_feature_shadow_6
ground_feature_shadow_7
ground_feature_wind_journey_0
ground_feature_wind_journey_1
ground_feature_wind_journey_2
ground_feature_wind_journey_3
ground_feature_wind_journey_4
ground_feature_wind_journey_5
ground_feature_wind_journey_6
ground_overworld_0
vine_0
```

### 战利品表（105 个）

```
aaroncos_lefthand_0
aaroncos_righthand_0
achievement_hide_1
achievement_hide_2
achievement_hide_4
armor_wreck_block_0
armor_wreck_block_1
armor_wreck_block_2
armor_wreck_block_3
armor_wreck_block_4
clay_pot_0
claypan_0
claypan_1
claypan_2
dream_spawner_0
dream_spawner_1
dyedream_bud_0
dyedream_bud_1
dyedream_bud_2
flower_1
flower_2
flower_3
flower_5
flower_6
flower_7
flower_8
flower_9
flower_10
flower_11
flower_12
flower_13
flower_14
flower_15
flower_16
flower_17
flower_18
grass_1
grass_2
grass_3
grass_4
grass_5
grass_6
grass_7
grass_8
grass_9
grass_10
grass_11
grass_12
grass_13
grass_14
grass_15
ice_bud_0
little_purple_doll_0
loots_deep_treasure_0
loots_deep_treasure_0_super
loots_deep_treasure_1
loots_deep_treasure_1_super
loots_relic_0
loots_relic_1
loots_relic_2
loots_relic_3
loots_relic_4
loots_relic_5
loots_relic_6
loots_relic_7
loots_relic_8
loots_relic_9
memento_item_11
pinkagaric_0
pinkagaric_1
pinkagaric_2
pinkagaric_3
qin_doll_0
shadow_light_0
shadow_trap_0
shadowshelf_0
shadowshelf_1
shadowshelf_2
structure_block_0
structure_block_1
structure_block_2
structure_block_3
structure_block_4
structure_block_5
structure_block_6
structure_block_7
structure_block_8
structure_block_9
structure_block_10
structure_block_11
structure_block_12
structure_block_13
structure_block_14
structure_block_15
structure_block_16
structure_block_17
structure_block_18
structure_block_19
structure_block_20
structure_block_21
structure_block_22
structure_block_23
windmoor_leaves_0
windmoor_leaves_1
windmoor_leaves_2
```

### 配方（201 个）

```
apple_juice_1
apple_juice_2
berry_buncake_1
berry_buncake_2
blackmetal_ingot_2
blackmetal_ingot_3
blackstick_1
blackstick_2
blackstick_4
bread_1
bread_2
bread_slice_1
bread_slice_2
bread_slice_3
bread_slice_4
bread_slice_5
bread_slice_6
bricks_dyedreamquartz_block_1
bricks_dyedreamquartz_block_2
bricks_dyedreamquartz_block_3
charged_amethyst_1
charged_amethyst_2
crafting_6
crafting_7
crafting_62
crafting_73
crafting_93
crafting_130
crafting_134
crafting_141
crafting_150
crafting_155
crafting_156
crafting_158
crafting_202
crafting_206
crafting_211
crafting_212
crafting_213
crafting_215
crafting_216
crafting_217
crafting_219
crafting_221
crafting_223
crafting_225
crafting_232
crafting_233
crafting_241
crafting_271
crafting_274
crafting_275
crafting_279
crafting_280
crafting_281
crafting_282
crafting_283
crafting_284
crafting_285
crafting_287
crafting_288
crafting_289
crafting_290
crafting_291
crafting_292
crafting_293
crafting_294
crafting_295
crafting_296
crafting_297
crafting_298
crafting_299
crafting_300
crafting_301
crafting_302
crafting_303
crafting_304
crafting_305
crafting_306
crafting_307
crafting_308
crafting_311
crafting_316
crafting_321
crafting_323
crafting_324
crafting_325
crafting_326
cream_buncake_1
cream_buncake_2
dough_1
dough_2
dyedream_bud_slab_1
dyedream_bud_slab_2
dyedream_bud_stairs_1
dyedream_bud_stairs_2
dyedream_bud_wall_1
dyedream_bud_wall_2
dyedream_dust_1
dyedream_dust_2
dyedream_dust_piece_1
dyedream_dust_piece_2
dyedream_dye_1
dyedream_dye_2
dyedream_dye_3
dyedream_fruit_buncake_1
dyedream_fruit_buncake_2
dyedream_glass_1
dyedream_glass_2
dyedream_ingot_1
dyedream_ingot_2
dyedream_ingot_3
dyedream_planks_1
dyedream_planks_2
dyedream_planks_3
dyedream_sword_0_smithing
dyedreamquartz_1
dyedreamquartz_2
dyedreamquartz_block_1
dyedreamquartz_block_2
dyedreamquartz_block_3
dyedreamquartz_block_slab_1
dyedreamquartz_block_slab_2
dyedreamquartz_block_slab_3
dyedreamquartz_block_stairs_1
dyedreamquartz_block_stairs_2
dyedreamquartz_block_stairs_3
dyedreamquartz_block_wall_1
dyedreamquartz_block_wall_2
dyedreamquartz_block_wall_3
fire_0_necklace
flour_1
flour_2
flour_3
fried_egg_1
fried_egg_2
glass_cup_1
glow_berry_buncake_1
glow_berry_buncake_2
health_0_necklace
heart_chocolate_0
heart_chocolate_1
heart_chocolate_2
hithard_0_ring
hithard_1_ring
honey_juice_1
honey_juice_2
magic_stone_1
magic_stone_2
melon_buncake_1
melon_buncake_2
milk_glassjar_1
milk_glassjar_2
milk_glassjar_3
moltengold_ingot_1
moltengold_ingot_2
pillar_dyedreamquartz_block_1
pillar_dyedreamquartz_block_2
pillar_dyedreamquartz_block_3
pink_dye_1
pink_dye_2
pinkagaric_3
potato_buncake_1
potato_buncake_2
pumpkin_buncake_1
pumpkin_buncake_2
rabbit_0_necklace
raw_titanium_1
red_dew_0
red_dew_0_ring
red_dew_1_ring
red_dew_2_ring
red_dew_3_ring
sandwich_1
sandwich_2
smelting_14
smithing_23
smooth_dyedreamquartz_block_1
smooth_dyedreamquartz_block_2
smooth_dyedreamquartz_block_3
string_1
string_2
titanium_armor_boots_1
titanium_armor_boots_2
titanium_armor_chestplate_1
titanium_armor_chestplate_2
titanium_armor_helmet_1
titanium_armor_helmet_2
titanium_armor_leggings_1
titanium_armor_leggings_2
titanium_ingot_1
titanium_ingot_2
unknownnotes_0_1
unknownnotes_0_2
water_glassjar_1
water_glassjar_2
water_glassjar_3
watermelon_juice_1
watermelon_juice_2
wind_iron_ingot_1
wind_iron_ingot_2
```

### 画（1 个）

- `pasterdream_draw_0`

> 数据包侧命中总计: **741** 个

---

## 三、迁移分类参考

### 3.1 同族编号（多编号并存，多为 MCreator 变体拆分）

> 同一前缀拆成多个编号（如 `calle_card_0~9`）。改名需成组处理，
> 建议改成语义名（`calle_card_fire` 等）或保留编号但补齐文档。

- `achievement_a`: `achievement_a_0`, `achievement_a_1`
- `achievement_b`: `achievement_b_0`, `achievement_b_1`, `achievement_b_2`, `achievement_b_3`
- `achievement_c`: `achievement_c_0`, `achievement_c_1`, `achievement_c_2`, `achievement_c_3`, `achievement_c_4`
- `achievement_hide`: `achievement_hide_0`, `achievement_hide_1`, `achievement_hide_2`, `achievement_hide_3`, `achievement_hide_4`, `achievement_hide_5`, `achievement_hide_6`, `achievement_hide_7`, `achievement_hide_8`, `achievement_hide_9`, `achievement_hide_10`, `achievement_hide_11`, `achievement_hide_12`, `achievement_hide_13`, `achievement_hide_14`, `achievement_hide_15`, `achievement_hide_16`
- `achievement_shadow_a`: `achievement_shadow_a_0`, `achievement_shadow_a_1`
- `achievement_shadow_npc`: `achievement_shadow_npc_0`, `achievement_shadow_npc_1`, `achievement_shadow_npc_2`, `achievement_shadow_npc_3`, `achievement_shadow_npc_4`, `achievement_shadow_npc_5`
- `armor_wreck_block`: `armor_wreck_block_0`, `armor_wreck_block_1`, `armor_wreck_block_2`, `armor_wreck_block_3`, `armor_wreck_block_4`
- `blueprint`: `blueprint_0`, `blueprint_1`
- `calle_card`: `calle_card_0`, `calle_card_1`, `calle_card_2`, `calle_card_3`, `calle_card_4`, `calle_card_5`, `calle_card_6`, `calle_card_7`, `calle_card_8`, `calle_card_9`
- `cheerup_buff`: `cheerup_buff_0`, `cheerup_buff_1`, `cheerup_buff_2`, `cheerup_buff_3`
- `claypan`: `claypan_0`, `claypan_1`, `claypan_2`
- `counterattack_buff`: `counterattack_buff_0`, `counterattack_buff_1`
- `deadwind_buff`: `deadwind_buff_0`, `deadwind_buff_1`, `deadwind_buff_2`
- `debug_wand_dream_church`: `debug_wand_dream_church_0`, `debug_wand_dream_church_1`, `debug_wand_dream_church_2`, `debug_wand_dream_church_3`, `debug_wand_dream_church_4`, `debug_wand_dream_church_5`, `debug_wand_dream_church_6`, `debug_wand_dream_church_7`, `debug_wand_dream_church_8`, `debug_wand_dream_church_9`, `debug_wand_dream_church_10`
- `debug_wand_dream_wishingtree`: `debug_wand_dream_wishingtree_0`, `debug_wand_dream_wishingtree_1`
- `debug_wand_dyedream_pavilion`: `debug_wand_dyedream_pavilion_0`, `debug_wand_dyedream_pavilion_1`, `debug_wand_dyedream_pavilion_2`
- `debug_wand_dyedream_tower`: `debug_wand_dyedream_tower_0`, `debug_wand_dyedream_tower_1`
- `debug_wand_garden_decryption`: `debug_wand_garden_decryption_0`, `debug_wand_garden_decryption_1`, `debug_wand_garden_decryption_2`
- `debug_wand_meltdream_liquid_well`: `debug_wand_meltdream_liquid_well_0`, `debug_wand_meltdream_liquid_well_1`
- `debug_wand_pinkagaric`: `debug_wand_pinkagaric_0`, `debug_wand_pinkagaric_1`, `debug_wand_pinkagaric_2`, `debug_wand_pinkagaric_3`
- `debug_wand_traveler_house`: `debug_wand_traveler_house_0`, `debug_wand_traveler_house_1`, `debug_wand_traveler_house_2`
- `deep_treasure`: `deep_treasure_0`, `deep_treasure_1`
- `dream_coin`: `dream_coin_0`, `dream_coin_1`
- `dream_spawner`: `dream_spawner_0`, `dream_spawner_1`
- `dream_wishingtree`: `dream_wishingtree_0`, `dream_wishingtree_1`
- `dreamharp_of_wanderer_buff`: `dreamharp_of_wanderer_buff_0`, `dreamharp_of_wanderer_buff_1`, `dreamharp_of_wanderer_buff_2`
- `dyedream_bud`: `dyedream_bud_0`, `dyedream_bud_1`, `dyedream_bud_2`
- `dyedream_pavilion`: `dyedream_pavilion_0`, `dyedream_pavilion_1`, `dyedream_pavilion_2`
- `dyedream_tower`: `dyedream_tower_0`, `dyedream_tower_1`
- `dyedream_worldtree`: `dyedream_worldtree_0`, `dyedream_worldtree_1`
- `enhance_stone`: `enhance_stone_0`, `enhance_stone_1`
- `flower`: `flower_1`, `flower_2`, `flower_3`, `flower_5`, `flower_6`, `flower_7`, `flower_8`, `flower_9`, `flower_10`, `flower_11`, `flower_12`, `flower_13`, `flower_14`, `flower_15`, `flower_16`, `flower_17`, `flower_18`
- `fury_spell_buff`: `fury_spell_buff_0`, `fury_spell_buff_1`, `fury_spell_buff_2`, `fury_spell_buff_3`, `fury_spell_buff_4`
- `garden_decryption`: `garden_decryption_0`, `garden_decryption_1`, `garden_decryption_2`
- `grass`: `grass_1`, `grass_2`, `grass_3`, `grass_4`, `grass_5`, `grass_6`, `grass_7`, `grass_8`, `grass_9`, `grass_10`, `grass_11`, `grass_12`, `grass_13`, `grass_14`, `grass_15`
- `heart_chocolate`: `heart_chocolate_0`, `heart_chocolate_1`, `heart_chocolate_2`
- `ice_spell_buff`: `ice_spell_buff_0`, `ice_spell_buff_1`
- `insand_buff`: `insand_buff_0`, `insand_buff_1`, `insand_buff_2`, `insand_buff_3`, `insand_buff_4`, `insand_buff_5`, `insand_buff_6`
- `lootstable_create`: `lootstable_create_0`, `lootstable_create_1`, `lootstable_create_2`, `lootstable_create_3`, `lootstable_create_4`, `lootstable_create_5`, `lootstable_create_6`, `lootstable_create_7`, `lootstable_create_8`, `lootstable_create_9`
- `meltdream_liquid_well`: `meltdream_liquid_well_0`, `meltdream_liquid_well_1`
- `memento_item`: `memento_item_01`, `memento_item_02`, `memento_item_03`, `memento_item_04`, `memento_item_05`, `memento_item_06`, `memento_item_07`, `memento_item_08`, `memento_item_09`, `memento_item_10`, `memento_item_11`
- `pinkagaric`: `pinkagaric_0`, `pinkagaric_1`, `pinkagaric_2`, `pinkagaric_3`
- `shadow_dungeon_block`: `shadow_dungeon_block_0`, `shadow_dungeon_block_1`, `shadow_dungeon_block_2`, `shadow_dungeon_block_3`, `shadow_dungeon_block_4`, `shadow_dungeon_block_5`, `shadow_dungeon_block_6`
- `shadow_dungeon_door`: `shadow_dungeon_door_0`, `shadow_dungeon_door_1`
- `shadow_dungeon_key`: `shadow_dungeon_key_0`, `shadow_dungeon_key_1`
- `shadow_fissure`: `shadow_fissure_0`, `shadow_fissure_1`, `shadow_fissure_2`, `shadow_fissure_3`, `shadow_fissure_4`, `shadow_fissure_5`
- `shadowdungeondoor`: `shadowdungeondoor_2`, `shadowdungeondoor_3`
- `shadowshelf`: `shadowshelf_0`, `shadowshelf_1`, `shadowshelf_2`, `shadowshelf_3`
- `struct_dyedream_crack`: `struct_dyedream_crack_0`, `struct_dyedream_crack_1`
- `tabitem`: `tabitem_1`, `tabitem_2`
- `tailwind_buff`: `tailwind_buff_0`, `tailwind_buff_1`, `tailwind_buff_2`
- `traveler_house`: `traveler_house_0`, `traveler_house_1`, `traveler_house_2`
- `windmoor_leaves`: `windmoor_leaves_0`, `windmoor_leaves_1`, `windmoor_leaves_2`

### 3.2 裸 `_0`（无同族，数字无意义，优先改名）

- `aaroncos_lefthand_0`
- `aaroncos_righthand_0`
- `achievement_adventure_0`
- `achievement_d_0`
- `achievement_end_0`
- `achievement_nether_0`
- `achievement_shadow_b_0`
- `achievement_shadow_c_0`
- `achievement_shadow_d_0`
- `achievement_shadow_e_0`
- `achievement_special_0`
- `blueprint_gui_0`
- `brokennotes_0`
- `clay_pot_0`
- `cook_buff_0`
- `debug_wand_desert_fortress_0`
- `debug_wand_dyedream_campsite_0`
- `debug_wand_dyedream_laboratory_0`
- `debug_wand_worldtree_0`
- `desert_cottage_0`
- `desert_fortress_0`
- `ding_0`
- `dreamnotes_gui_0`
- `dyedream_campsite_0`
- `dyedream_laboratory_0`
- `dyedream_sword_0`
- `fury_spell_0`
- `ice_bud_0`
- `little_purple_doll_0`
- `meltdream_chest_0`
- `meltdream_crystal_0`
- `memory_gem_0`
- `oppression_buff_0`
- `pebble_0`
- `qin_doll_0`
- `rage_elixir_0`
- `red_dew_0`
- `rest_buff_0`
- `rest_buff_in_dark_0`
- `shadow_arena_block_0`
- `shadow_biome_0`
- `shadow_light_0`
- `shadow_music_0`
- `shadow_npc_0`
- `shadow_squeal_ghost_0`
- `shadow_trap_0`
- `stone_break_0`
- `storage_bag_0`
- `sword_embryo_0`
- `unknownnotes_0`
- `vine_0`
- `wind_knight_skill_0`
- `windmoor_tree_0`

### 3.3 单编号（数字在中间段，如粒子 `*_0_particle`）

- `attack_0_particle`
- `buff_0_particle`
- `crack_0_particle`
- `desert_cottage_0_set`
- `desert_fortress_0_set`
- `dream_wishingtree_0_set`
- `dream_wishingtree_1_set`
- `dust_0_particle`
- `dyedream_0_particle`
- `dyedream_campsite_0_set`
- `dyedream_laboratory_0_set`
- `dyedream_pavilion_0_set`
- `dyedream_pavilion_1_set`
- `dyedream_pavilion_2_set`
- `dyedream_tower_0_set`
- `dyedream_tower_1_set`
- `dyedream_worldtree_0_set`
- `dyedream_worldtree_1_set`
- `fire_0_necklace`
- `fox_fire_0_particle`
- `fox_fire_1_particle`
- `garden_decryption_0_set`
- `garden_decryption_1_set`
- `garden_decryption_2_set`
- `health_0_necklace`
- `hithard_0_ring`
- `hithard_1_ring`
- `meltdream_energy_0_ring`
- `meltdream_liquid_well_0_set`
- `meltdream_liquid_well_1_set`
- `rabbit_0_necklace`
- `red_dew_0_ring`
- `red_dew_1_ring`
- `red_dew_2_ring`
- `red_dew_3_ring`
- `snowflake_0_particle`
- `snowflake_1_particle`
- `spell_snowflake_0_particle`
- `struct_dyedream_crack_1_set`
- `traveler_house_0_set`
- `traveler_house_1_set`
- `traveler_house_2_set`
- `wind_journey_1_disc`

---

## 四、迁移建议（草稿）

| 优先级 | 对象 | 处理方式 |
|--------|------|----------|
| P0 | 3.2 裸 `_0`（无同族） | 直接去掉 `_0`：`meltdream_crystal_0` → `meltdream_crystal`、`red_dew_0` → `red_dew`、`shadow_npc_0` → `shadow_npc` |
| P1 | 3.1 同族编号 | 语义化命名（需成组同步），或保留编号仅补文档 |
| P2 | 群系旧名 | 已有新描述名，按 `PDBiomes` 的 `@Deprecated(forRemoval)` 时间表移除旧别名 |
| 保留 | 结构 / 结构集 / 模板池 / 地物 / 配方 / 战利品表编号 | 编号即变体 ID，无明确语义名时保留 |

**迁移同步清单**（每个 ID 改名需同步以下位置）:

1. Java 注册名与常量（`registry/**`）
2. 资源文件: blockstate / model / 纹理 / 动画 / sounds.json
3. 语言文件 key（`assets/*/lang/*.json`）
4. 引用: 配方、战利品表、标签、进度、结构引用、DataGen
5. 快照: `PasterDream/src/main/resources/pd_porting_manifest.json`（PDPortingVerifyTest 校验基线）

**模板**: 群系已采用「新描述名 + 旧数字别名」双轨（见 `PDBiomes.java`），可作为其他类别的迁移范式。

