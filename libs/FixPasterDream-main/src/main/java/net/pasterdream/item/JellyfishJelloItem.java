
package net.pasterdream.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.pasterdream.init.PasterdreamModMobEffects;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.network.chat.Component;

import java.util.List;

public class JellyfishJelloItem extends Item {
	public JellyfishJelloItem() {
		super(new Item.Properties()
                .stacksTo(64)
                .rarity(Rarity.COMMON)
                .food((new FoodProperties.Builder())
                        .nutrition(4)
                        .saturationMod(0.5f)
                        .alwaysEat()
                        .effect(()-> new MobEffectInstance(PasterdreamModMobEffects.WINDPROOF_BUFF.get(), 12000, 0),1)
                        .build()));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u00A77\u98DF\u7528\u540E\u83B7\u5F97\u6548\u679C:"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u9632\u98CEI (10:00)"));
		list.add(Component.literal("\u00A77\u00A7o\u8BA9\u4F60\u4E5F\u50CF\u90A3\u4E9B\u6C34\u6BCD\u4E00\u6837\u65E0\u60E7\u4E8E\u98CE"));
	}

}
