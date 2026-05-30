package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.loot.LootParams;
import java.util.List;

/**
 * 染梦树苗 (Dyedream Sapling)
 * 简化版，继承 FlowerBlock，无 EntityBlock
 * 注意：这是简化实现，原版有复杂的生长逻辑和 BlockEntity
 */
public class DyedreamSaplingBlock extends FlowerBlock {
    /**
     * 构造一个染梦树苗方块
     */
    public DyedreamSaplingBlock() {
        super(MobEffects.LUCK, 80,
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .randomTicks()
                .sound(SoundType.GRASS)
                .instabreak()
                .noCollission()
                .offsetType(BlockBehaviour.OffsetType.NONE)
                .pushReaction(PushReaction.DESTROY));
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(PDBlocks.DYEDREAM_GRASS.get())
            || state.is(PDBlocks.DYEDREAM_DIRT.get())
            || state.is(PDBlocks.DYEDREAM_SAND.get())
            || state.is(PDBlocks.DYEDREAM_BLOCK.get());
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of(new ItemStack(this));
    }
}