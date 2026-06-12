package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDItems;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
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
     * @param properties 物品属性
     */
    public DyedreamPerfumeItem(Item.Properties properties) {
        super(properties.stacksTo(64).food(new FoodProperties.Builder()
                    .nutrition(0).saturationModifier(0f).build()));
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
        tooltipComponents.add(Component.literal("\u00A77\u996E\u7528\u540E\u83B7\u5F97\u6548\u679C:"));
        tooltipComponents.add(Component.literal("\u00A77\u25AA \u00A79\u8BA9\u8718\u871B\u8FDC\u79BB\u4F60 (1:00)"));
        tooltipComponents.add(Component.literal("\u00A77\u25AA \u00A79\u6E05\u7A7A\u672A\u7761\u7720\u7D2F\u8BA1\u91CF\u8868"));
        tooltipComponents.add(Component.literal("\u00A77\u00A7o\u5F53\u4F60\u5728\u601D\u8003\u4E3A\u4EC0\u4E48\u9999\u6C34\u8981\u7528\u6765\u559D\u65F6 \u6216\u8BB8\u5E94\u8BE5\u5148\u8003\u8651\u4F60\u73B0\u5728\u6B63\u5728\u505A\u68A6\uFF1F"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
