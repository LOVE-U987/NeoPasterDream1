
package net.pasterdream.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.pasterdream.capability.MeltDreamEnergyCapability;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;

public class MeltdreamEnergy0RingItem extends Item implements ICurioItem {
	public MeltdreamEnergy0RingItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7b\u7CBE\u826F \u2605\u2605\u2605"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u8EAB\u5904\u68A6\u5883\u65F6 \u878D\u68A6\u80FD\u91CF+0.15/min"));
	}

	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if(entity instanceof Player pl && pl.tickCount % 20 == 0)
        {
            if ((pl.level().dimension()) == (ResourceKey.create(Registries.DIMENSION, new ResourceLocation("pasterdream:dyedream_world")))
                    || (pl.level().dimension()) == (ResourceKey.create(Registries.DIMENSION, new ResourceLocation("pasterdream:lamp_shadow_world")))) {
                MeltDreamEnergyCapability.addPlayerMeltDreamEnergy(pl,0.0025);
            }
        }

	}
}
