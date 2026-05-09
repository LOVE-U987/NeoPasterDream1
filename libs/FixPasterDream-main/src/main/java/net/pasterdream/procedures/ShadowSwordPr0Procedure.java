package net.pasterdream.procedures;

import net.pasterdream.capability.SanCapability;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;

import java.util.concurrent.atomic.AtomicReference;

public class ShadowSwordPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (entity instanceof Player) {
			if (entity instanceof ServerPlayer sp && sp.level() instanceof ServerLevel && sp.getAdvancements().getOrStartProgress(sp.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_talent_shadow"))).isDone() || (entity instanceof Player _plr && _plr.getAbilities().instabuild)) {
                if (!entity.level().isClientSide()) {
                    entity.level().playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:shadow_sword")), SoundSource.PLAYERS, 1, 1);
                } else {
                    entity.level().playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:shadow_sword")), SoundSource.PLAYERS, 1, 1, false);
                }
			} else {
				if (entity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("\u672A\u9009\u5219<\u6697\u5F71> \u65E0\u6CD5\u4F7F\u7528\u6B64\u5251"), false);
				entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.FELL_OUT_OF_WORLD)), (float) ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) - 1));
				if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
					_entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0));
			}
			{
				AtomicReference<IItemHandler> _iitemhandlerref = new AtomicReference<>();
				entity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(_iitemhandlerref::set);
				if (_iitemhandlerref.get() != null) {
					for (int _idx = 0; _idx < _iitemhandlerref.get().getSlots(); _idx++) {
						ItemStack itemstackiterator = _iitemhandlerref.get().getStackInSlot(_idx).copy();
						if (itemstackiterator.is(ItemTags.create(new ResourceLocation("pasterdream:skill")))) {
							if (entity instanceof Player _player)
								_player.getCooldowns().addCooldown(itemstackiterator.getItem(), (int) (40 * ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.SKILLCD.get()).getValue()));
						}
					}
				}
			}
            if(entity instanceof ServerPlayer sp && SanCapability.IsSanCheckSystem())
            {
                if(entity.getCapability(SanCapability.Provider.PLAYER_SAN_CAPABILITY).map(cap -> cap.getSanValue() <= 0).orElse(true))
                {
                    entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MAGIC)), Math.max(0,sp.getHealth() - 1));
                }
            }
		}
	}
}
