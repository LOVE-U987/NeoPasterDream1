package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.api.item.base.AbstractGeoDisplayItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;

import java.util.List;

/**
 * 通用玩偶显示物品
 * <p>
 * 所有 API 玩偶的物品共用此类，客户端通过 {@link DollItemRenderer} 渲染。
 */
public class DollDisplayItem extends AbstractGeoDisplayItem {

    /**
     * 构造通用玩偶显示物品
     *
     * @param block      对应的方块
     * @param properties 物品属性
     */
    public DollDisplayItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    protected String getControllerName() {
        return "doll_controller";
    }

    @Override
    protected int getTransitionTicks() {
        return 0;
    }

    @Override
    protected PlayState predicate(AnimationState<?> state) {
        return PlayState.STOP;
    }

    /**
     * 添加玩偶悬浮描述
     * <p>
     * 读取语言键 {@code item.pasterdream.<name>.desc}，统一显示为灰色提示行。
     *
     * @param stack    当前物品栈
     * @param context  提示上下文
     * @param tooltip  提示组件列表
     * @param flag     提示标志
     */
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable(this.getDescriptionId(stack) + ".desc").withStyle(ChatFormatting.GRAY));
    }
}
