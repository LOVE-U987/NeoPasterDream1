package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.DreamAccumulatorDisplayItem;
import com.pasterdream.pasterdreammod.item.LifeCrystalDisplayItem;
import com.pasterdream.pasterdreammod.item.MagicStoneItem;
import com.pasterdream.pasterdreammod.item.ShadowChestDisplayItem;
import com.pasterdream.pasterdreammod.item.TitaniumIngotItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 物品注册类
 * 使用 DeferredRegister 模式注册所有物品
 */
public class PDItems {

    /**
     * 物品注册器
     */
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PasterDreamMod.MOD_ID);

    /**
     * 蓄梦池物品 (dream_accumulator)
     * 使用 DreamAccumulatorDisplayItem 实现手持 GeckoLib 动画
     */
    public static final DeferredItem<BlockItem> DREAM_ACCUMULATOR = ITEMS.register("dream_accumulator",
            () -> new DreamAccumulatorDisplayItem(PDBlocks.DREAM_ACCUMULATOR.get(), new Item.Properties()));

    /**
     * 染梦书桌物品 (dyedream_desk)
     * 对应 PDBlocks.DYEDREAM_DESK 方块
     */
    public static final DeferredItem<BlockItem> DYEDREAM_DESK = ITEMS.registerSimpleBlockItem("dyedream_desk",
            PDBlocks.DYEDREAM_DESK);

    /**
     * 生命水晶物品 (life_crystal)
     * 对应 PDBlocks.LIFE_CRYSTAL 方块
     * 使用 LifeCrystalDisplayItem 实现手持 3D 渲染
     * 站在附近可以缓慢恢复生命值
     */
    public static final DeferredItem<LifeCrystalDisplayItem> LIFE_CRYSTAL = ITEMS.register("life_crystal",
            () -> new LifeCrystalDisplayItem(new Item.Properties()));

    /**
     * 影之箱子物品 (shadow_chest)
     * 对应 PDBlocks.SHADOW_CHEST 方块
     * 使用 ShadowChestDisplayItem 实现手持 3D 渲染
     * 装饰性方块，无存储功能
     */
    public static final DeferredItem<ShadowChestDisplayItem> SHADOW_CHEST = ITEMS.register("shadow_chest",
            () -> new ShadowChestDisplayItem(new Item.Properties()));

    // ==================== 刷怪蛋 ====================

    /**
     * 暗影魔像刷怪蛋 (shadow_golem_spawn_egg)
     * 主色: 深灰色 (0x171717), 副色: 暗紫色 (0x7A7A7A)
     */
    public static final DeferredItem<SpawnEggItem> SHADOW_GOLEM_SPAWN_EGG = ITEMS.register("shadow_golem_spawn_egg",
            () -> new SpawnEggItem(PDEntities.SHADOW_GOLEM.get(), 0x171717, 0x7A7A7A, new Item.Properties()));

    /**
     * 粉色史莱姆刷怪蛋 (pink_slime_spawn_egg)
     * 主色: 粉色 (0xFFB6C1), 副色: 深粉色 (0xFF69B4)
     */
    public static final DeferredItem<SpawnEggItem> PINK_SLIME_SPAWN_EGG = ITEMS.register("pink_slime_spawn_egg",
            () -> new SpawnEggItem(PDEntities.PINK_SLIME.get(), 0xFFB6C1, 0xFF69B4, new Item.Properties()));

    // ==================== 测试材料物品 ====================

    /**
     * 钛锭 (titanium_ingot)
     * 基础材料，稀有度为 UNCOMMON
     */
    public static final DeferredItem<Item> TITANIUM_INGOT = ITEMS.register("titanium_ingot",
            () -> new TitaniumIngotItem());

    /**
     * 染梦粉 (dyedream_dust)
     * 基础材料
     */
    public static final DeferredItem<Item> DYEDREAM_DUST = ITEMS.registerSimpleItem("dyedream_dust");

    /**
     * 魔法石 (magic_stone)
     * 基础材料，带有特殊描述文本
     */
    public static final DeferredItem<Item> MAGIC_STONE = ITEMS.register("magic_stone",
            () -> new MagicStoneItem());

    /**
     * 粉色粘液球 (pink_slimeball)
     */
    public static final DeferredItem<Item> PINK_SLIMEBALL = ITEMS.registerSimpleItem("pink_slimeball");

    /**
     * 染梦石英 (dyedreamquartz)
     */
    public static final DeferredItem<Item> DYEDREAMQUARTZ = ITEMS.registerSimpleItem("dyedreamquartz");
}
