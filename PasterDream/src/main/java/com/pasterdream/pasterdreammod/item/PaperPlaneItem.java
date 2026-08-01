package com.pasterdream.pasterdreammod.item;

import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import java.util.List;

/**
 * 纸飞机饰品（Curio）。
 * <p>
 * 对齐原版语义：装备时提升顺/逆风效果 amp（写入 force NBT=1），卸下清零。
 * 具体移速数值由 {@code PDEffects} 顺/逆风 onApply 按 amp 施加，本类不直接改属性。
 */
public class PaperPlaneItem extends Item implements ICurioItem {

    /** 顺风 amp NBT 键，由 WindJourneyEvents 读取 */
    private static final String TAILWIND_FORCE_KEY = "player_tailwind_force";
    /** 逆风 amp NBT 键，由 WindJourneyEvents 读取 */
    private static final String DEADWIND_FORCE_KEY = "player_deadwind_force";

    public PaperPlaneItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
    }

    @Override
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.translatable("tooltip.pasterdream.paper_plane.quality"));
        list.add(Component.translatable("tooltip.pasterdream.paper_plane.effect_1"));
        list.add(Component.translatable("tooltip.pasterdream.paper_plane.flavor_1"));
    }

    /**
     * 装备纸飞机：将顺/逆风 force 置 1，使风向 buff 使用更高 amp。
     *
     * @param slotContext 饰品槽上下文
     * @param prevStack   槽内原物品
     * @param stack       当前装备的纸飞机
     */
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity == null) {
            return;
        }
        entity.getPersistentData().putDouble(TAILWIND_FORCE_KEY, 1);
        entity.getPersistentData().putDouble(DEADWIND_FORCE_KEY, 1);
    }

    /**
     * 卸下纸飞机：将顺/逆风 force 置 0，恢复默认 amp。
     *
     * @param slotContext 饰品槽上下文
     * @param newStack    卸下后槽内物品
     * @param stack       被卸下的纸飞机
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity == null) {
            return;
        }
        entity.getPersistentData().putDouble(TAILWIND_FORCE_KEY, 0);
        entity.getPersistentData().putDouble(DEADWIND_FORCE_KEY, 0);
    }
}
