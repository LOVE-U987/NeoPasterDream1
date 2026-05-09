package net.pasterdream.procedures;

import top.theillusivec4.curios.api.CuriosApi;

import net.pasterdream.init.PasterdreamModParticleTypes;
import net.pasterdream.init.PasterdreamModMobEffects;
import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

public class EvasionBuffPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:evasion")), SoundSource.PLAYERS, (float) 0.7, 1);
			} else {
				_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:evasion")), SoundSource.PLAYERS, (float) 0.7, 1, false);
			}
		}
		if (world instanceof ServerLevel _level)
			_level.sendParticles(ParticleTypes.CLOUD, x, (y + 1.5), z, 12, 0.1, 0.4, 0.1, 0.05);
		if (world instanceof ServerLevel _level)
			_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.DUST_0_PARTICLE.get()), x, (y + 0.8), z, 32, 0.3, 0.5, 0.3, 0.1);
		if (world instanceof ServerLevel _level)
			_level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, (y + 0.8), z, 24, 0.3, 0.5, 0.3, 0.1);
		entity.getPersistentData().putBoolean("evasion", true);
		if (entity instanceof LivingEntity lv ? CuriosApi.getCuriosHelper().findEquippedCurio(PasterdreamModItems.COUNTER_RING.get(), lv).isPresent() : false == false) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.COUNTERATTACK_BUFF.get(), 40, 0));
		}
		if (entity instanceof LivingEntity lv ? CuriosApi.getCuriosHelper().findEquippedCurio(PasterdreamModItems.TURNBACK_CLOAK.get(), lv).isPresent() : false == false) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.COUNTERATTACK_BUFF.get(), 40, 0));
		}
		if (entity instanceof LivingEntity lv ? CuriosApi.getCuriosHelper().findEquippedCurio(PasterdreamModItems.EVASION_CLOAK.get(), lv).isPresent() : false == false) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.COUNTERATTACK_BUFF.get(), 40, 0));
		}
		entity.setSprinting(true);
		entity.setDeltaMovement(new Vec3((entity.getLookAngle().x * 0.4), (entity.getLookAngle().y), (entity.getLookAngle().z * 0.4)));
		if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
			_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 7, 1, false, false));
		PasterdreamMod.queueServerWork(7, () -> {
			entity.setSprinting(false);
		});
		PasterdreamMod.queueServerWork(2, () -> {
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.CLOUD, (entity.getX()), (entity.getY() + 1.5), (entity.getZ()), 6, 0.1, 0.4, 0.1, 0.05);
			if (world instanceof ServerLevel _level)
				_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.DUST_0_PARTICLE.get()), (entity.getX()), (entity.getY() + 0.8), (entity.getZ()), 16, 0.3, 0.5, 0.3, 0.1);
			if (world instanceof ServerLevel _level)
				_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.DUST_0_PARTICLE.get()), (entity.getX()), (entity.getY() + 0.8), (entity.getZ()), 12, 0.3, 0.5, 0.3, 0.1);
			PasterdreamMod.queueServerWork(2, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.CLOUD, (entity.getX()), (entity.getY() + 1.5), (entity.getZ()), 6, 0.1, 0.4, 0.1, 0.05);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.DUST_0_PARTICLE.get()), (entity.getX()), (entity.getY() + 0.8), (entity.getZ()), 16, 0.3, 0.5, 0.3, 0.1);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.DUST_0_PARTICLE.get()), (entity.getX()), (entity.getY() + 0.8), (entity.getZ()), 12, 0.3, 0.5, 0.3, 0.1);
				PasterdreamMod.queueServerWork(2, () -> {
					if (world instanceof ServerLevel _level)
						_level.sendParticles(ParticleTypes.CLOUD, (entity.getX()), (entity.getY() + 1.5), (entity.getZ()), 6, 0.1, 0.4, 0.1, 0.05);
					if (world instanceof ServerLevel _level)
						_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.DUST_0_PARTICLE.get()), (entity.getX()), (entity.getY() + 0.8), (entity.getZ()), 16, 0.3, 0.5, 0.3, 0.1);
					if (world instanceof ServerLevel _level)
						_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.DUST_0_PARTICLE.get()), (entity.getX()), (entity.getY() + 0.8), (entity.getZ()), 12, 0.3, 0.5, 0.3, 0.1);
					PasterdreamMod.queueServerWork(2, () -> {
						if (world instanceof ServerLevel _level)
							_level.sendParticles(ParticleTypes.CLOUD, (entity.getX()), (entity.getY() + 1.5), (entity.getZ()), 6, 0.1, 0.4, 0.1, 0.05);
						if (world instanceof ServerLevel _level)
							_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.DUST_0_PARTICLE.get()), (entity.getX()), (entity.getY() + 0.8), (entity.getZ()), 16, 0.3, 0.5, 0.3, 0.1);
						if (world instanceof ServerLevel _level)
							_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.DUST_0_PARTICLE.get()), (entity.getX()), (entity.getY() + 0.8), (entity.getZ()), 12, 0.3, 0.5, 0.3, 0.1);
						PasterdreamMod.queueServerWork(2, () -> {
							if (world instanceof ServerLevel _level)
								_level.sendParticles(ParticleTypes.CLOUD, (entity.getX()), (entity.getY() + 1.5), (entity.getZ()), 6, 0.1, 0.4, 0.1, 0.05);
							if (world instanceof ServerLevel _level)
								_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.DUST_0_PARTICLE.get()), (entity.getX()), (entity.getY() + 0.8), (entity.getZ()), 16, 0.3, 0.5, 0.3, 0.1);
							if (world instanceof ServerLevel _level)
								_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.DUST_0_PARTICLE.get()), (entity.getX()), (entity.getY() + 0.8), (entity.getZ()), 12, 0.3, 0.5, 0.3, 0.1);
						});
					});
				});
			});
		});
	}
}
