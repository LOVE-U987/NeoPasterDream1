package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.util.PasterItemData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * 占星者的祈愿 (star_wish_rod)
 * <p>
 * 还原自原版 StarWishRodItem（特殊钓鱼竿）：
 * <ul>
 *   <li>耐久 0（不可损耗）、防火、附魔能力 10</li>
 *   <li>使用时切换自定义数据 cast 开关（驱动 pasterdream:cast 模型谓词切换收/抛竿贴图）</li>
 *   <li>不在任一手持有时自动复位 cast（StarWishRodPr1Procedure）</li>
 *   <li>钓鱼幸运加成（220% 特殊幸运值乘区）由战利品表体系承载，物品端不做额外逻辑</li>
 * </ul>
 */
public class StarWishRodItem extends FishingRodItem {

    public StarWishRodItem(Item.Properties properties) {
        super(properties.durability(0).fireResistant());
    }

    @Override
    public int getEnchantmentValue() {
        return 10;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        super.use(level, player, hand);
        ItemStack stack = player.getItemInHand(hand);
        // 原版 StarWishRodPr0Procedure：切换 cast 开关
        PasterItemData.putBoolean(stack, "cast", !PasterItemData.getBoolean(stack, "cast"));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        // 原版 StarWishRodPr1Procedure：不在任一手上时复位 cast
        if (entity instanceof Player player
                && player.getMainHandItem() != stack && player.getOffhandItem() != stack
                && PasterItemData.getBoolean(stack, "cast")) {
            PasterItemData.putBoolean(stack, "cast", false);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.literal("§7▪ §9钓鱼特殊幸运值乘区：220%"));
        tooltip.add(Component.literal("§7▪ §9将可能钓到更有价值的深海秘宝"));
    }
}
