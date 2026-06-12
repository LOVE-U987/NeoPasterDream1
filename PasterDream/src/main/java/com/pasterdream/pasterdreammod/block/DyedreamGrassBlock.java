package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.List;

/**
 * 染梦草方块
 * 用于 dyedream_grass，确保挖掘时掉落自身
 */
public class DyedreamGrassBlock extends Block {
    /**
     * @param properties 方块属性
     */
    public DyedreamGrassBlock(Properties properties) {
        super(properties);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of(new ItemStack(this));
    }
}