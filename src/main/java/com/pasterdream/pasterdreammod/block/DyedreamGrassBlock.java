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
        String blockName = BuiltInRegistries.BLOCK.getKey(this).toString();
        PasterDreamMod.LOGGER.info("[DyedreamGrassBlock] ===== getDrops() 被调用 =====");
        PasterDreamMod.LOGGER.info("[DyedreamGrassBlock] 方块: {}", blockName);
        PasterDreamMod.LOGGER.info("[DyedreamGrassBlock] 方块状态: {}", state);
        PasterDreamMod.LOGGER.info("[DyedreamGrassBlock] 方块类: {}", this.getClass().getName());
        PasterDreamMod.LOGGER.info("[DyedreamGrassBlock] 掉落策略: 掉落自身 (this={})", this);
        List<ItemStack> drops = List.of(new ItemStack(this));
        PasterDreamMod.LOGGER.info("[DyedreamGrassBlock] 掉落列表: {} (数量={})", drops, drops.size());
        if (!drops.isEmpty()) {
            PasterDreamMod.LOGGER.info("[DyedreamGrassBlock] 掉落物品[0]: {} (空={})",
                drops.get(0).getItem().toString(), drops.get(0).isEmpty());
        }
        PasterDreamMod.LOGGER.info("[DyedreamGrassBlock] ===== getDrops() 结束 =====");
        return drops;
    }
}