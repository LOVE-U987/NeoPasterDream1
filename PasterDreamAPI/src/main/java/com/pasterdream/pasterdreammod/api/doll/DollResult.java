package com.pasterdream.pasterdreammod.api.doll;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * 玩偶注册结果
 * <p>
 * 保存一次 {@link DollBuilder#register()} 产生的所有 DeferredHolder，
 * 方便调用者后续引用。
 * <p>
 * 方块/物品/方块实体类型使用通配泛型：API 层不命名具体实现类（实现类由
 * {@code PasterDream} 主模组经 {@link DollImplementations} 注入），调用方仅以
 * Block/Item/BlockEntityType 基类型消费。
 *
 * @param name            注册名
 * @param block           方块 DeferredHolder
 * @param item            物品 DeferredHolder
 * @param blockEntityType 方块实体类型 DeferredHolder
 * @param config          玩偶配置
 */
public record DollResult(
        String name,
        DeferredBlock<? extends Block> block,
        DeferredItem<? extends Item> item,
        DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<?>> blockEntityType,
        DollConfig config
) {
}
