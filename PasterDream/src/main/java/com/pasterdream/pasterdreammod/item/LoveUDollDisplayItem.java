package com.pasterdream.pasterdreammod.item;
import com.pasterdream.pasterdreammod.api.item.base.AbstractGeoDisplayItem;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;

import java.util.List;

/**
 * LOVE_U拉乌酱玩偶显示物品 (Love U Doll Display Item)
 * <p>
 * 使用 {@link AbstractGeoDisplayItem} 实现 3D 手持渲染，对应方块 {@link PDBlocks#LOVE_U_DOLL}。
 * 客户端渲染器通过 {@code PDClientItemExtensions} 单独注册。
 */
public class LoveUDollDisplayItem extends AbstractGeoDisplayItem {

    /**
     * 构造LOVE_U拉乌酱玩偶显示物品
     * <p>
     * 默认稀有度为史诗且不可被火焰烧毁。
     */
    public LoveUDollDisplayItem() {
        this(new Item.Properties().rarity(Rarity.EPIC).fireResistant());
    }

    /**
     * 构造LOVE_U拉乌酱玩偶显示物品
     *
     * @param properties 物品属性
     */
    public LoveUDollDisplayItem(Item.Properties properties) {
        super(PDBlocks.LOVE_U_DOLL.get(), properties);
    }

    @Override
    protected String getControllerName() {
        return "display";
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
     * 添加物品提示信息
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
