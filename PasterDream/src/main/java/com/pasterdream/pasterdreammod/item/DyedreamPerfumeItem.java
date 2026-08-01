package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDItems;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/**
 * dyedream_perfume 物品类
 * 原版稀有度: COMMON，使用后返还玻璃罐并播放饮用动画
 */
public class DyedreamPerfumeItem extends Item {

    /**
     * 构造方法
     *
     * @param properties 物品属性，应包含已配置效果的 FoodProperties
     */
    public DyedreamPerfumeItem(Item.Properties properties) {
        super(properties);
    }

    /**
     * 返回饮用动画，使玩家使用香水时播放饮用音效
     *
     * @param stack 物品栈
     * @return {@link UseAnim#DRINK} 饮用动画
     */
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    /**
     * 完成使用后返还玻璃罐
     *
     * @param stack  当前物品栈
     * @param level  所处世界
     * @param entity 使用者
     * @return 剩余的物品栈
     */
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        super.finishUsingItem(stack, level, entity);
        if (stack.isEmpty()) {
            return new ItemStack(PDItems.GLASSJAR.get());
        }
        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            ItemStack retval = new ItemStack(PDItems.GLASSJAR.get());
            if (!player.getInventory().add(retval)) {
                player.drop(retval, false);
            }
        }
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.dyedream_perfume.on_drink"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.dyedream_perfume.effect.spider"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.dyedream_perfume.effect.sleep"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.dyedream_perfume.desc"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
