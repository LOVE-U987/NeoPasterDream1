package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.List;

/**
 * 染梦花/草通用方块
 * 用于所有单格的小花和草丛（flower_1~18 中的单格品种 + grass_1~15 中的单格品种）
 * 支持自定义药水效果，必须种在染梦土壤上
 */
public class DyedreamFlowerBlock extends FlowerBlock {
    /**
     * @param effect 触碰时获得的药水效果持有者
     * @param duration 效果持续时间（tick）
     * @param properties 方块属性
     */
    public DyedreamFlowerBlock(Holder<MobEffect> effect, int duration, Properties properties) {
        super(effect, duration, properties);
    }

    /**
     * 使用默认属性的快捷构造
     */
    public DyedreamFlowerBlock(Holder<MobEffect> effect, int duration) {
        this(effect, duration,
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .sound(SoundType.GRASS)
                .instabreak()
                .noCollission()
                .offsetType(BlockBehaviour.OffsetType.XZ)
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