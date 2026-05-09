package net.pasterdream.procedures;

import net.pasterdream.capability.MeltDreamEnergyCapability;
import top.theillusivec4.curios.api.CuriosApi;

import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.tags.ItemTags;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import java.util.concurrent.atomic.AtomicReference;

public class TerraSwordPr1Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (entity instanceof Player pl && MeltDreamEnergyCapability.consumePlayerMeltDreamEnergy(pl,0.3)) {
			AtkPr0Procedure.execute(entity, itemstack);
			itemstack.getOrCreateTag().putDouble("skill", 1);
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.enchantment_table.use")), SoundSource.PLAYERS, 1, 1);
				} else {
					_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.enchantment_table.use")), SoundSource.PLAYERS, 1, 1, false);
				}
			}
			if (CuriosApi.getCuriosInventory(pl).map( date -> date.findFirstCurio(PasterdreamModItems.TERRA_CHARM.get()).isPresent()).orElse(false)) {
				itemstack.getOrCreateTag().putDouble("paster_atk", (itemstack.getOrCreateTag().getDouble("paster_atk") * 1.3));
                MeltDreamEnergyCapability.addPlayerMeltDreamEnergy(pl,0.2);
				{
					AtomicReference<IItemHandler> _iitemhandlerref = new AtomicReference<>();
					entity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(_iitemhandlerref::set);
					if (_iitemhandlerref.get() != null) {
						for (int _idx = 0; _idx < _iitemhandlerref.get().getSlots(); _idx++) {
							ItemStack itemstackiterator = _iitemhandlerref.get().getStackInSlot(_idx).copy();
							if (itemstackiterator.is(ItemTags.create(new ResourceLocation("pasterdream:skill")))) {
								if (entity instanceof Player _player)
									_player.getCooldowns().addCooldown(itemstackiterator.getItem(), (int) (40 * ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.SKILLCD.get()).getBaseValue()));
							}
						}
					}
				}
			} else {
				{
					AtomicReference<IItemHandler> _iitemhandlerref = new AtomicReference<>();
					entity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(_iitemhandlerref::set);
					if (_iitemhandlerref.get() != null) {
						for (int _idx = 0; _idx < _iitemhandlerref.get().getSlots(); _idx++) {
							ItemStack itemstackiterator = _iitemhandlerref.get().getStackInSlot(_idx).copy();
							if (itemstackiterator.is(ItemTags.create(new ResourceLocation("pasterdream:skill")))) {
								if (entity instanceof Player _player)
									_player.getCooldowns().addCooldown(itemstackiterator.getItem(), (int) (100 * ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.SKILLCD.get()).getBaseValue()));
							}
						}
					}
				}
			}
		} else {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u878D\u68A6\u80FD\u91CF\u4E0D\u8DB3"), true);
		}
	}
}
