package com.pasterdream.pasterdreammod.block;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;

/**
 * 烈焰花（flower_6）方块
 * <p>
 * 掉落逻辑：
 * <ul>
 *   <li><b>剪刀采集</b>：掉落自身（flower_6），与其它染梦花行为一致</li>
 *   <li><b>正常采集（空手/其它工具）</b>：掉落 1 个烈焰粉（minecraft:blaze_powder）</li>
 * </ul>
 */
public class BlazeFlowerBlock extends DyedreamFlowerBlock {

    /**
     * @param effect     触碰时获得的药水效果持有者
     * @param duration   效果持续时间（tick）
     * @param properties 方块属性
     */
    public BlazeFlowerBlock(Holder<MobEffect> effect, int duration, Properties properties) {
        super(effect, duration, properties);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        // 剪刀采集：掉落自身（不变）
        ItemStack tool = params.getParameter(LootContextParams.TOOL);
        if (tool != null && !tool.isEmpty() && tool.getItem() instanceof ShearsItem) {
            return List.of(new ItemStack(this));
        }
        // 正常采集：掉落烈焰粉
        return List.of(new ItemStack(Items.BLAZE_POWDER));
    }
}
