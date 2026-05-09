package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModParticleTypes;
import net.pasterdream.init.PasterdreamModMobEffects;
import net.pasterdream.entity.AaroncosLefthand0Entity;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.TagKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Comparator;

public class AaroncosLefthandSkillHitProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof AaroncosLefthand0Entity) {
			((AaroncosLefthand0Entity) entity).setAnimation("empty");
		}
		if (entity instanceof AaroncosLefthand0Entity) {
			((AaroncosLefthand0Entity) entity).setAnimation("skill_hit");
		}
		PasterdreamMod.queueServerWork(10, () -> {
			entity.setDeltaMovement(new Vec3(0, 2, 0));
			PasterdreamMod.queueServerWork(5, () -> {
				entity.setDeltaMovement(new Vec3(0, (-10), 0));
			});
			PasterdreamMod.queueServerWork(9, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.SHADOW_STONE_PARTICLE.get()), (entity.getX()), (entity.getY()), (entity.getZ()), 128, 2, 1, 2, 0.1);
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.SMOKE, (entity.getX()), (entity.getY()), (entity.getZ()), 64, 2, 1, 2, 0.5);
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.EXPLOSION, (entity.getX()), (entity.getY()), (entity.getZ()), 12, 1, 1, 1, 0.5);
				{
					final Vec3 _center = new Vec3((entity.getX()), (entity.getY() - 3), (entity.getZ()));
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(15 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("pasterdream:special_entity_tag")))
								&& !entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("pasterdream:shadow_mob")))) {
							if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
								_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.CONFUSION_BUFF.get(), 10, 1, false, false));
							entityiterator.setDeltaMovement(new Vec3(0, 0.5, 0));
							entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC)), 6);
						}
					}
				}
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.explode")), SoundSource.MASTER, 1, 1);
					} else {
						_level.playLocalSound((entity.getX()), (entity.getY()), (entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.explode")), SoundSource.MASTER, 1, 1, false);
					}
				}
			});
		});
		PasterdreamMod.queueServerWork(21, () -> {
			entity.setDeltaMovement(new Vec3(0, 3, 0));
			PasterdreamMod.queueServerWork(6, () -> {
				entity.setDeltaMovement(new Vec3(0, (-10), 0));
			});
			PasterdreamMod.queueServerWork(9, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.SHADOW_STONE_PARTICLE.get()), (entity.getX()), (entity.getY()), (entity.getZ()), 256, 3, 1, 3, 0.1);
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.SMOKE, (entity.getX()), (entity.getY()), (entity.getZ()), 128, 3, 1, 3, 0.5);
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.EXPLOSION, (entity.getX()), (entity.getY()), (entity.getZ()), 16, 2, 1, 2, 0.5);
				{
					final Vec3 _center = new Vec3((entity.getX()), (entity.getY() - 5), (entity.getZ()));
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(19 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("pasterdream:special_entity_tag")))
								&& !entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("pasterdream:shadow_mob")))) {
							if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
								_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.CONFUSION_BUFF.get(), 10, 1, false, false));
							entityiterator.setDeltaMovement(new Vec3(0, 1, 0));
							entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC)), 7);
						}
					}
				}
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.explode")), SoundSource.MASTER, (float) 1.1, 1);
					} else {
						_level.playLocalSound((entity.getX()), (entity.getY()), (entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.explode")), SoundSource.MASTER, (float) 1.1, 1, false);
					}
				}
			});
		});
		PasterdreamMod.queueServerWork(42, () -> {
			entity.setDeltaMovement(new Vec3(0, 4, 0));
			PasterdreamMod.queueServerWork(6, () -> {
				entity.setDeltaMovement(new Vec3(0, (-10), 0));
			});
			PasterdreamMod.queueServerWork(11, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.SHADOW_STONE_PARTICLE.get()), (entity.getX()), (entity.getY()), (entity.getZ()), 512, 4, 1, 4, 0.1);
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.SMOKE, (entity.getX()), (entity.getY()), (entity.getZ()), 192, 4, 1, 4, 0.5);
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.EXPLOSION, (entity.getX()), (entity.getY()), (entity.getZ()), 24, 3, 1, 3, 0.5);
				{
					final Vec3 _center = new Vec3((entity.getX()), (entity.getY() - 5), (entity.getZ()));
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(23 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("pasterdream:special_entity_tag")))
								&& !entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("pasterdream:shadow_mob")))) {
							if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
								_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.CONFUSION_BUFF.get(), 10, 1, false, false));
							entityiterator.setDeltaMovement(new Vec3(0, 1.5, 0));
							entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC)), 8);
						}
					}
				}
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.explode")), SoundSource.MASTER, (float) 1.2, 1);
					} else {
						_level.playLocalSound((entity.getX()), (entity.getY()), (entity.getZ()), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.explode")), SoundSource.MASTER, (float) 1.2, 1, false);
					}
				}
			});
		});
	}
}
