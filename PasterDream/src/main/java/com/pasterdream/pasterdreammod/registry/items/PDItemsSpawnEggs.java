package com.pasterdream.pasterdreammod.registry.items;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.curio.CurioAPI;
import com.pasterdream.pasterdreammod.api.curio.model.CurioSlot;
import com.pasterdream.pasterdreammod.api.effect.MobEffectAPI;
import com.pasterdream.pasterdreammod.api.entity.EntityAPI;
import com.pasterdream.pasterdreammod.api.item.ItemAPI;
import com.pasterdream.pasterdreammod.api.item.model.MigrationCategory;
import com.pasterdream.pasterdreammod.api.item.model.ToolSpec.ToolType;
import com.pasterdream.pasterdreammod.item.*;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.food.FoodProperties;
import net.neoforged.neoforge.registries.DeferredItem;


/**
 * 刷怪蛋注册。
 *
 * @see PDItems
 */
public class PDItemsSpawnEggs {


    // ==================== 刷怪蛋（通过 EntityAPI 统一注册） ====================

    /**
     * 暗影魔像刷怪蛋 (shadow_golem_spawn_egg)
     * 颜色配置于 PDEntities.SHADOW_GOLEM_RESULT 的 .spawnEgg()
     */
    public static final DeferredItem<Item> SHADOW_GOLEM_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "shadow_golem", PDEntities.SHADOW_GOLEM);

    /**
     * 粉色史莱姆刷怪蛋 (pink_slime_spawn_egg)
     * 颜色配置于 PDEntities.PINK_SLIME_RESULT 的 .spawnEgg()
     */
    public static final DeferredItem<Item> PINK_SLIME_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "pink_slime", PDEntities.PINK_SLIME);

    /**
     * 粉红鸡刷怪蛋 (pink_chicken_spawn_egg)
     */
    public static final DeferredItem<Item> PINK_CHICKEN_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "pink_chicken", PDEntities.PINK_CHICKEN);

    /**
     * 水母刷怪蛋 (jellyfish_spawn_egg)
     */
    public static final DeferredItem<Item> JELLYFISH_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "jellyfish", PDEntities.JELLYFISH);

    /**
     * 怨魂刷怪蛋 (friendly_ghost_spawn_egg)
     */
    public static final DeferredItem<Item> FRIENDLY_GHOST_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "friendly_ghost", PDEntities.FRIENDLY_GHOST);

    /**
     * 萤火虫刷怪蛋 (firefly_spawn_egg)
     */
    public static final DeferredItem<Item> FIREFLY_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "firefly", PDEntities.FIREFLY);

    /**
     * 金色狐狸刷怪蛋 (golden_fox_spawn_egg)
     */
    public static final DeferredItem<Item> GOLDEN_FOX_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "golden_fox", PDEntities.GOLDEN_FOX);

    /**
     * 融梦水晶刷怪蛋 (meltdream_crystal_spawn_egg)
     */
    public static final DeferredItem<Item> MELTDREAM_CRYSTAL_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "meltdream_crystal", PDEntities.MELTDREAM_CRYSTAL);


    // ==================== 阴影系列刷怪蛋 ====================

    /**
     * 暗影幽灵刷怪蛋 (shadow_ghost_spawn_egg)
     */
    public static final DeferredItem<Item> SHADOW_GHOST_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "shadow_ghost", PDEntities.SHADOW_GHOST);

    /**
     * 暗影尖啸幽灵刷怪蛋 (shadow_squeal_ghost_spawn_egg)
     */
    public static final DeferredItem<Item> SHADOW_SQUEAL_GHOST_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "shadow_squeal_ghost", PDEntities.SHADOW_SQUEAL_GHOST);

    /**
     * 暗影尖啸幽灵0刷怪蛋 (shadow_squeal_ghost_0_spawn_egg)
     */
    public static final DeferredItem<Item> SHADOW_SQUEAL_GHOST_0_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "shadow_squeal_ghost_0", PDEntities.SHADOW_SQUEAL_GHOST_0);

    /**
     * 暗影之手刷怪蛋 (shadow_hand_spawn_egg)
     */
    public static final DeferredItem<Item> SHADOW_HAND_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "shadow_hand", PDEntities.SHADOW_HAND);


    // ==================== 雷云系列刷怪蛋 ====================

    /**
     * 雷云刷怪蛋 (thundercloud_spawn_egg)
     */
    public static final DeferredItem<Item> THUNDERCLOUD_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "thundercloud", PDEntities.THUNDERCLOUD);

    /**
     * 高压雷云刷怪蛋 (highvoltage_spawn_egg)
     */
    public static final DeferredItem<Item> HIGHVOLTAGE_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "highvoltage", PDEntities.HIGHVOLTAGE);


    // ==================== 其他敌对生物刷怪蛋 ====================

    /**
     * 风之骑士刷怪蛋 (wind_knight_spawn_egg)
     */
    public static final DeferredItem<Item> WIND_KNIGHT_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "wind_knight", PDEntities.WIND_KNIGHT);

    /**
     * 震动水晶刷怪蛋 (shaking_crystal_spawn_egg)
     */
    public static final DeferredItem<Item> SHAKING_CRYSTAL_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "shaking_crystal", PDEntities.SHAKING_CRYSTAL);

    /**
     * 暗影调和图腾刷怪蛋 (shadow_tune_totem_spawn_egg)
     */
    public static final DeferredItem<Item> SHADOW_TUNE_TOTEM_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "shadow_tune_totem", PDEntities.SHADOW_TUNE_TOTEM);

    /**
     * 小石灵刷怪蛋 (small_stone_spirit_spawn_egg)
     */
    public static final DeferredItem<Item> SMALL_STONE_SPIRIT_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "small_stone_spirit", PDEntities.SMALL_STONE_SPIRIT);

    /**
     * 黑甲虫刷怪蛋 (black_beetle_spawn_egg)
     */
    public static final DeferredItem<Item> BLACK_BEETLE_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "black_beetle", PDEntities.BLACK_BEETLE);

    /**
     * 黑甲虫母体刷怪蛋 (black_beetle_mother_spawn_egg)
     */
    public static final DeferredItem<Item> BLACK_BEETLE_MOTHER_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "black_beetle_mother", PDEntities.BLACK_BEETLE_MOTHER);


    // ==================== 恐怖尖喙系列刷怪蛋 ====================

    /**
     * 恐怖尖喙刷怪蛋 (terrorbeak_spawn_egg)
     */
    public static final DeferredItem<Item> TERRORBEAK_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "terrorbeak", PDEntities.TERRORBEAK);

    /**
     * 疯狂恐怖尖喙刷怪蛋 (crazy_terrorbeak_spawn_egg)
     */
    public static final DeferredItem<Item> CRAZY_TERRORBEAK_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "crazy_terrorbeak", PDEntities.CRAZY_TERRORBEAK);

    /**
     * 虚弱恐怖尖喙刷怪蛋 (weakeness_terrorbeak_spawn_egg)
     */
    public static final DeferredItem<Item> WEAKENESS_TERRORBEAK_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "weakeness_terrorbeak", PDEntities.WEAKENESS_TERRORBEAK);


    // ==================== 骨翼系列刷怪蛋 ====================

    /**
     * 骨翼刷怪蛋 (bone_wing_spawn_egg)
     */
    public static final DeferredItem<Item> BONE_WING_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "bone_wing", PDEntities.BONE_WING);

    /**
     * 灰烬骨翼刷怪蛋 (ash_bone_wing_spawn_egg)
     */
    public static final DeferredItem<Item> ASH_BONE_WING_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "ash_bone_wing", PDEntities.ASH_BONE_WING);


    // ==================== 染梦新生物刷怪蛋 ====================

    /**
     * 玄武岩蜗牛刷怪蛋 (basalt_snail_spawn_egg)
     */
    public static final DeferredItem<Item> BASALT_SNAIL_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "basalt_snail", PDEntities.BASALT_SNAIL);

    /**
     * 狐火刷怪蛋 (fox_fire_spawn_egg)
     */
    public static final DeferredItem<Item> FOX_FIRE_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "fox_fire", PDEntities.FOX_FIRE);

    /**
     * 暗影 ??? NPC 刷怪蛋 (shadow_npc_0_spawn_egg)
     */
    public static final DeferredItem<Item> SHADOW_NPC_0_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "shadow_npc_0", PDEntities.SHADOW_NPC_0);

    /**
     * 孢子实体刷怪蛋 (spore_entity_spawn_egg)
     */
    public static final DeferredItem<Item> SPORE_ENTITY_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "spore_entity", PDEntities.SPORE_ENTITY);


    // ==================== BOSS 刷怪蛋 ====================

    /**
     * 亚伦柯斯之触 - 左刷怪蛋 (aaroncos_lefthand_0_spawn_egg)
     * 颜色配置于 PDEntities.AARONCOS_LEFTHAND_0_RESULT 的 .spawnEgg()
     */
    public static final DeferredItem<Item> AARONCOS_LEFTHAND_0_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "aaroncos_lefthand_0", PDEntities.AARONCOS_LEFTHAND_0);

    /**
     * 亚伦柯斯之触 - 右刷怪蛋 (aaroncos_righthand_0_spawn_egg)
     * 颜色配置于 PDEntities.AARONCOS_RIGHTHAND_0_RESULT 的 .spawnEgg()
     */
    public static final DeferredItem<Item> AARONCOS_RIGHTHAND_0_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "aaroncos_righthand_0", PDEntities.AARONCOS_RIGHTHAND_0);


    // ==================== 补充刷怪蛋（原版名称对齐） ====================

    /** 大地之刃剑气刷怪蛋 */
    public static final DeferredItem<Item> TERRASWORD_WAVE_SPAWN_EGG =
            EntityAPI.createSpawnEggItem(PDItems.ITEMS, "terrasword_wave", PDEntities.TERRASWORD_WAVE);

    /** 治疗法术立场刷怪蛋（原版 healing_spell_entity_spawn_egg） */
    public static final DeferredItem<Item> HEALING_SPELL_ENTITY_SPAWN_EGG = PDItems.ITEMS.register(
            "healing_spell_entity_spawn_egg",
            () -> new SpawnEggItem(PDEntities.HEALING_SPELL_ENTITY.get(), 0xFEF670, 0xFFC03D, new Item.Properties()));

}
