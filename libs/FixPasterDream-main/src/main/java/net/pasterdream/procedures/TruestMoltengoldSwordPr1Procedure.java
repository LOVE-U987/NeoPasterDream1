package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModParticleTypes;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.tags.ItemTags;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.BlockPos;

import java.util.concurrent.atomic.AtomicReference;

public class TruestMoltengoldSwordPr1Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (itemstack.getOrCreateTag().getBoolean("skill") == false) {
			itemstack.getOrCreateTag().putBoolean("skill", true);
			AtkPr0Procedure.execute(entity, itemstack);
			if (world instanceof ServerLevel _level)
				_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.BUFF_0_PARTICLE.get()), x, (y - 0.5), z, 20, 0.5, 1, 0.5, 1);
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:sword1")), SoundSource.PLAYERS, (float) 0.8, 1);
				} else {
					_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:sword1")), SoundSource.PLAYERS, (float) 0.8, 1, false);
				}
			}
			if (entity instanceof LivingEntity _entity)
				_entity.swing(InteractionHand.MAIN_HAND, true);
			{
				AtomicReference<IItemHandler> _iitemhandlerref = new AtomicReference<>();
				entity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(_iitemhandlerref::set);
				if (_iitemhandlerref.get() != null) {
					for (int _idx = 0; _idx < _iitemhandlerref.get().getSlots(); _idx++) {
						ItemStack itemstackiterator = _iitemhandlerref.get().getStackInSlot(_idx).copy();
						if (itemstackiterator.is(ItemTags.create(new ResourceLocation("pasterdream:skill")))) {
							if (entity instanceof Player _player)
								_player.getCooldowns().addCooldown(itemstackiterator.getItem(), (int) (200 * ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.SKILLCD.get()).getValue()));
						}
					}
				}
			}
		}
	}
}
