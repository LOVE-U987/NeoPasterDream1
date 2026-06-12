package com.pasterdream.pasterdreammod.block;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.List;

/**
 * 染梦原木方块，也用于染梦木头、去皮变种
 * 继承 RotatedPillarBlock 并覆写 getDrops()，确保无需战利品表也能正确掉落自身
 */
public class DyedreamLogBlock extends RotatedPillarBlock {
    /**
     * @param properties 方块属性
     */
    public DyedreamLogBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of(new ItemStack(this));
    }
}
