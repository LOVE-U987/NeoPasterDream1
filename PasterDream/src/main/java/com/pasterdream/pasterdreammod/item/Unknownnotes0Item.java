package com.pasterdream.pasterdreammod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 未解析的笔记 (unknownnotes_0)
 * <p>
 * 原版仅 tooltip；研究台槽位过滤已按注册名/物品引用。
 * 若 {@code PDItemsMaterials.UNKNOWNNOTES_0} 仍为 simpleItem，可按
 * {@code dreamnotes_registry_staging.md} 替换为本类。
 */
public class Unknownnotes0Item extends Item {

    public Unknownnotes0Item() {
        super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
    }

    public Unknownnotes0Item(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("未知内容"));
        tooltipComponents.add(Component.literal("§7需要使用§e研究台§7解析笔记"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
