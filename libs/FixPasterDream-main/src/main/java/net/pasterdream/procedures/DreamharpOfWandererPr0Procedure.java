package net.pasterdream.procedures;

import net.pasterdream.capability.MeltDreamEnergyCapability;
import net.pasterdream.init.PasterdreamModMobEffects;
import net.pasterdream.init.PasterdreamModAttributes;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Comparator;

public class DreamharpOfWandererPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (entity instanceof Player pl) {
			if (MeltDreamEnergyCapability.consumePlayerMeltDreamEnergy(pl,2)) {
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.HEART, x, y, z, 7, 0.45, 0.8, 0.45, 0.5);
                PasterdreamMod.queueServerWork(5, () -> {
					if (world instanceof ServerLevel _level)
						_level.sendParticles(ParticleTypes.HEART, x, y, z, 7, 0.45, 0.8, 0.45, 0.5);
                    PasterdreamMod.queueServerWork(5, () -> {
						if (world instanceof ServerLevel _level)
							_level.sendParticles(ParticleTypes.HEART, x, y, z, 7, 0.45, 0.8, 0.45, 0.5);
					});
				});
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:dreamharp_of_wanderer")), SoundSource.PLAYERS, (float) 0.7, 1);
					} 
				}
				if (entity instanceof Player _player)
					_player.getCooldowns().addCooldown(itemstack.getItem(), 200);
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof Player) {
							if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
								_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.DREAMHARP_OF_WANDERER_BUFF.get(), 1200, 0));
						}
					}
				}
			} else {
				if (entity instanceof Player _player && _player.level().isClientSide())
					_player.displayClientMessage(Component.literal("\u878D\u68A6\u80FD\u91CF\u4E0D\u8DB3"), true);
			}
		}
	}
}
