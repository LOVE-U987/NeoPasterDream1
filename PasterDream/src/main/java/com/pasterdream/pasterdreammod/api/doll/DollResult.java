package com.pasterdream.pasterdreammod.api.doll;

import com.pasterdream.pasterdreammod.block.DollBlock;
import com.pasterdream.pasterdreammod.block.entity.DollBlockEntity;
import com.pasterdream.pasterdreammod.item.DollDisplayItem;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * 玩偶注册结果
 * <p>
 * 保存一次 {@link DollBuilder#register()} 产生的所有 DeferredHolder，
 * 方便调用者后续引用。
 *
 * @param name            注册名
 * @param block           方块 DeferredHolder
 * @param item            物品 DeferredHolder
 * @param blockEntityType 方块实体类型 DeferredHolder
 * @param config          玩偶配置
 */
public record DollResult(
        String name,
        DeferredBlock<DollBlock> block,
        DeferredItem<DollDisplayItem> item,
        DeferredHolder<BlockEntityType<?>, BlockEntityType<DollBlockEntity>> blockEntityType,
        DollConfig config
) {
}
