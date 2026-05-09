package net.pasterdream.procedures;

import top.theillusivec4.curios.api.CuriosApi;

import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.atomic.AtomicReference;

public class MoltengoldWandPr1Procedure {
	public static void execute(LevelAccessor world, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity lv ? CuriosApi.getCuriosHelper().findEquippedCurio(PasterdreamModItems.QYM_HEAD.get(), lv).isPresent() : false == true) {
			if (entity instanceof Player _player)
				_player.getCooldowns().addCooldown(itemstack.getItem(), 0);
		} else {
			{
				AtomicReference<IItemHandler> _iitemhandlerref = new AtomicReference<>();
				entity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(_iitemhandlerref::set);
				if (_iitemhandlerref.get() != null) {
					for (int _idx = 0; _idx < _iitemhandlerref.get().getSlots(); _idx++) {
						ItemStack itemstackiterator = _iitemhandlerref.get().getStackInSlot(_idx).copy();
						if (itemstackiterator.is(ItemTags.create(new ResourceLocation("pasterdream:magic")))) {
							if (entity instanceof Player _player)
								_player.getCooldowns().addCooldown(itemstackiterator.getItem(), (int) (20 + ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.MAGICCD.get()).getValue()));
						}
					}
				}
			}
		}
	}
}
