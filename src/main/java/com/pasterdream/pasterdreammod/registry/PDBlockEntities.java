package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.DreamAccumulatorBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.LifeCrystalBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.ShadowChestBlockEntity;
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
}