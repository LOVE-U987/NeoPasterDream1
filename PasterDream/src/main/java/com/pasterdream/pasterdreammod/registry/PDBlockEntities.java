package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.DreamAccumulatorBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.DreamCauldronBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.DyedreamDeskBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.LifeCrystalBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.ShadowChestBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.TheEndlessBookOfDreamSeekersBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 方块实体注册类
 * 使用 DeferredRegister 模式注册所有 BlockEntityType
 */
public class PDBlockEntities {

    /**
     * 方块实体类型注册器
     */
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, PasterDreamMod.MOD_ID);

    /**
     * 蓄梦池方块实体类型
     * 用于渲染 GeckoLib 动画
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DreamAccumulatorBlockEntity>> DREAM_ACCUMULATOR =
            BLOCK_ENTITIES.register("dream_accumulator",
                    () -> BlockEntityType.Builder.of(
                            DreamAccumulatorBlockEntity::new,
                            PDBlocks.DREAM_ACCUMULATOR.get()
                    ).build(null));

    /**
     * 生命水晶方块实体类型
     * 用于渲染 GeckoLib 浮动和旋转动画
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LifeCrystalBlockEntity>> LIFE_CRYSTAL =
            BLOCK_ENTITIES.register("life_crystal",
                    () -> BlockEntityType.Builder.of(
                            LifeCrystalBlockEntity::new,
                            PDBlocks.LIFE_CRYSTAL.get()
                    ).build(null));

    /**
     * 影之箱方块实体类型
     * 用于渲染 GeckoLib 开盖动画
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ShadowChestBlockEntity>> SHADOW_CHEST =
            BLOCK_ENTITIES.register("shadow_chest",
                    () -> BlockEntityType.Builder.of(
                            ShadowChestBlockEntity::new,
                            PDBlocks.SHADOW_CHEST.get()
                    ).build(null));

    /**
     * 寻梦者的永恒书卷方块实体类型
     * 1 格库存，用于渲染 GeckoLib 动画和 GUI
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TheEndlessBookOfDreamSeekersBlockEntity>> THE_ENDLESS_BOOK_OF_DREAM_SEEKERS =
            BLOCK_ENTITIES.register("the_endless_book_of_dream_seekers",
                    () -> BlockEntityType.Builder.of(
                            TheEndlessBookOfDreamSeekersBlockEntity::new,
                            PDBlocks.THE_ENDLESS_BOOK_OF_DREAM_SEEKERS.get()
                    ).build(null));

    /**
     * 染梦书桌方块实体类型
     * 1 格库存（最大堆叠 1），支持 GUI 菜单和物品展示
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DyedreamDeskBlockEntity>> DYEDREAM_DESK =
            BLOCK_ENTITIES.register("dyedream_desk",
                    () -> BlockEntityType.Builder.of(
                            DyedreamDeskBlockEntity::new,
                            PDBlocks.DYEDREAM_DESK.get()
                    ).build(null));

    /**
     * 融梦水晶箱方块实体类型（关闭状态）
     * 用于渲染 GeckoLib 开启动画，9 格库存
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.pasterdream.pasterdreammod.block.entity.MeltdreamChestBlockEntity>> MELTDREAM_CHEST =
            BLOCK_ENTITIES.register("meltdream_chest",
                    () -> BlockEntityType.Builder.of(
                            com.pasterdream.pasterdreammod.block.entity.MeltdreamChestBlockEntity::new,
                            PDBlocks.MELTDREAM_CHEST.get()
                    ).build(null));

    /**
     * 融梦水晶箱方块实体类型（打开状态）
     * 9 格库存，支持 GUI，无动画
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.pasterdream.pasterdreammod.block.entity.MeltdreamChestOpenBlockEntity>> MELTDREAM_CHEST_OPEN =
            BLOCK_ENTITIES.register("meltdream_chest_open",
                    () -> BlockEntityType.Builder.of(
                            com.pasterdream.pasterdreammod.block.entity.MeltdreamChestOpenBlockEntity::new,
                            PDBlocks.MELTDREAM_CHEST_OPEN.get()
                    ).build(null));

    /**
     * 梦境炼药锅方块实体类型
     * GeckoLib 动画 + 4 格库存（3 输入 + 1 输出），支持 GUI 菜单
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DreamCauldronBlockEntity>> DREAM_CAULDRON =
            BLOCK_ENTITIES.register("dream_cauldron",
                    () -> BlockEntityType.Builder.of(
                            DreamCauldronBlockEntity::new,
                            PDBlocks.DREAM_CAULDRON.get()
                    ).build(null));
}