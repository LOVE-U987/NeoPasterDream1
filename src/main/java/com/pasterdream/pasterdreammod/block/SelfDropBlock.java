package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.List;

/**
 * 矿物方块 —— 挖掘时掉落对应的粗矿物品而非自身
 * <p>
 * 用于替代数据包战利品表，避免因战利品表加载异常导致矿物不掉落。
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
        PasterDreamMod.LOGGER.debug("[SelfDropBlock] 方块ID和状态: block={}, state={}",
                BuiltInRegistries.BLOCK.getKey(this), state);
        Item item = BuiltInRegistries.ITEM.get(BuiltInRegistries.BLOCK.getKey(this));
        if (item != null && item != Items.AIR) {
            return List.of(new ItemStack(item));
        }
        PasterDreamMod.LOGGER.warn("[SelfDropBlock] BuiltInRegistries 未找到对应的物品，回退到 new ItemStack(this): block={}",
                BuiltInRegistries.BLOCK.getKey(this));
        return List.of(new ItemStack(this));
    }
}