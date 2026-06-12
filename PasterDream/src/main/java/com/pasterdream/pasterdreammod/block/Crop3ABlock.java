package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.List;

/**
 * 作物3a - 再生效果，仅可种植在染梦泥土/草方块上
 */
public class Crop3ABlock extends FlowerBlock {
    public Crop3ABlock(BlockBehaviour.Properties properties) {
        super(MobEffects.REGENERATION, 100, properties);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity living && !level.isClientSide) {
            living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0, true, false));
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = super.getDrops(state, params);
        if (drops.isEmpty()) {
            return List.of(new ItemStack(this));
        }
        return drops;
    }

    @Override
    public boolean mayPlaceOn(BlockState groundState, BlockGetter level, BlockPos pos) {
        return groundState.is(PDBlocks.DYEDREAM_DIRT.get()) || groundState.is(PDBlocks.DYEDREAM_GRASS.get());
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        return this.mayPlaceOn(level.getBlockState(below), level, below);
    }
}