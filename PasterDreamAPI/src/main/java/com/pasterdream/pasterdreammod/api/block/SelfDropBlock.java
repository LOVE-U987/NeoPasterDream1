package com.pasterdream.pasterdreammod.api.block;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.List;

/**
 * 基础方块类 —— 所有通过 API 批量注册的方块均使用此类
 * <p>
 * 掉落逻辑采用混合策略：
 * <ol>
 *   <li>优先使用战利品表系统（适用于已配置战利品表的矿石等方块）</li>
 *   <li>战利品表返回空时回退为掉落方块自身（适用于无战利品表的简单装饰方块）</li>
 * </ol>
 */
public class SelfDropBlock extends Block {

    /**
     * @param properties 方块属性
     */
    public SelfDropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = super.getDrops(state, params);
        if (drops.isEmpty()) {
            return List.of(new ItemStack(this));
        }
        return drops;
    }
}