package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.List;

/**
 * 染梦原木方块
 * 继承 RotatedPillarBlock 并覆写 getDrops() 确保挖掘时掉落自身
 */
public class DyedreamLogBlock extends RotatedPillarBlock {
    /**
     * @param properties 方块属性
     */
    public DyedreamLogBlock(Properties properties) {
        super(properties);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        String blockName = BuiltInRegistries.BLOCK.getKey(this).toString();
        PasterDreamMod.LOGGER.info("[DyedreamLogBlock] ===== getDrops() 被调用 =====");
        PasterDreamMod.LOGGER.info("[DyedreamLogBlock] 方块: {}", blockName);
        PasterDreamMod.LOGGER.info("[DyedreamLogBlock] 方块状态: {}", state);
        PasterDreamMod.LOGGER.info("[DyedreamLogBlock] 方块类: {}", this.getClass().getName());
        PasterDreamMod.LOGGER.info("[DyedreamLogBlock] 父类: RotatedPillarBlock");
        PasterDreamMod.LOGGER.info("[DyedreamLogBlock] 掉落策略: 掉落自身 (this={})", this);
        List<ItemStack> drops = List.of(new ItemStack(this));
        PasterDreamMod.LOGGER.info("[DyedreamLogBlock] 掉落列表: {} (数量={})", drops, drops.size());
        if (!drops.isEmpty()) {
            PasterDreamMod.LOGGER.info("[DyedreamLogBlock] 掉落物品[0]: {} (空={})",
                drops.get(0).getItem().toString(), drops.get(0).isEmpty());
        }
        PasterDreamMod.LOGGER.info("[DyedreamLogBlock] ===== getDrops() 结束 =====");
        return drops;
    }
}
