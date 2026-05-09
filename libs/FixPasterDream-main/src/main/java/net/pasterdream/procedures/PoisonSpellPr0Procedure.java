package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModParticleTypes;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
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
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Comparator;

public class PoisonSpellPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.splash_potion.break")), SoundSource.NEUTRAL, (float) 0.7, 1);
			} else {
				_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.splash_potion.break")), SoundSource.NEUTRAL, (float) 0.7, 1, false);
			}
		}
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:poison_spell")), SoundSource.NEUTRAL, (float) 0.7, 1);
			} else {
				_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:poison_spell")), SoundSource.NEUTRAL, (float) 0.7, 1, false);
			}
		}
		if (world instanceof ServerLevel _level)
			_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 48, 2, 1, 2, 0.01);
		if (world instanceof ServerLevel _level)
			_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 24, 2, 1, 2, 0.02);
		PasterdreamMod.queueServerWork(1, () -> {
			PasterdreamMod.queueServerWork(2, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 64, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.02);
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.POISON, 20, 3));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 0));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 0));
					}
				}
			});
			PasterdreamMod.queueServerWork(22, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 16, 2, 1, 2, 0.02);
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.POISON, 20, 3));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 0));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 0));
					}
				}
			});
			PasterdreamMod.queueServerWork(42, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 16, 2, 1, 2, 0.02);
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.POISON, 20, 3));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 0));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 0));
					}
				}
			});
			PasterdreamMod.queueServerWork(62, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 16, 2, 1, 2, 0.02);
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.POISON, 20, 3));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 0));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 0));
					}
				}
			});
			PasterdreamMod.queueServerWork(82, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 16, 2, 1, 2, 0.02);
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.POISON, 20, 3));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 0));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 0));
					}
				}
			});
			PasterdreamMod.queueServerWork(12, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 16, 2, 1, 2, 0.02);
			});
			PasterdreamMod.queueServerWork(32, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 16, 2, 1, 2, 0.02);
			});
			PasterdreamMod.queueServerWork(52, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 16, 2, 1, 2, 0.02);
			});
			PasterdreamMod.queueServerWork(72, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 16, 2, 1, 2, 0.02);
			});
		});
		PasterdreamMod.queueServerWork(81, () -> {
			PasterdreamMod.queueServerWork(2, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 64, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.02);
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.POISON, 20, 3));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 0));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 0));
					}
				}
			});
			PasterdreamMod.queueServerWork(22, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 16, 2, 1, 2, 0.02);
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.POISON, 20, 3));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 0));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 0));
					}
				}
			});
			PasterdreamMod.queueServerWork(42, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 16, 2, 1, 2, 0.02);
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.POISON, 20, 3));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 0));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 0));
					}
				}
			});
			PasterdreamMod.queueServerWork(62, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 16, 2, 1, 2, 0.02);
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.POISON, 20, 3));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 0));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 0));
					}
				}
			});
			PasterdreamMod.queueServerWork(82, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 16, 2, 1, 2, 0.02);
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.POISON, 20, 3));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 0));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 0));
					}
				}
			});
			PasterdreamMod.queueServerWork(12, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 16, 2, 1, 2, 0.02);
			});
			PasterdreamMod.queueServerWork(32, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 16, 2, 1, 2, 0.02);
			});
			PasterdreamMod.queueServerWork(52, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 16, 2, 1, 2, 0.02);
			});
			PasterdreamMod.queueServerWork(72, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 16, 2, 1, 2, 0.02);
			});
		});
		PasterdreamMod.queueServerWork(161, () -> {
			PasterdreamMod.queueServerWork(2, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 64, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.02);
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.POISON, 20, 3));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 0));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 0));
					}
				}
			});
			PasterdreamMod.queueServerWork(22, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 16, 2, 1, 2, 0.02);
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.POISON, 20, 3));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 0));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 0));
					}
				}
			});
			PasterdreamMod.queueServerWork(42, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 16, 2, 1, 2, 0.02);
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.POISON, 20, 3));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 0));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 0));
					}
				}
			});
			PasterdreamMod.queueServerWork(62, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 16, 2, 1, 2, 0.02);
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.POISON, 20, 3));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 0));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 0));
					}
				}
			});
			PasterdreamMod.queueServerWork(82, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 16, 2, 1, 2, 0.02);
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(7 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.POISON, 20, 3));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 0));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 0));
					}
				}
			});
			PasterdreamMod.queueServerWork(12, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 16, 2, 1, 2, 0.02);
			});
			PasterdreamMod.queueServerWork(32, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 16, 2, 1, 2, 0.02);
			});
			PasterdreamMod.queueServerWork(52, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 16, 2, 1, 2, 0.02);
			});
			PasterdreamMod.queueServerWork(72, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get()), x, (y + 1), z, 32, 2, 1, 2, 0.01);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get()), x, (y + 1), z, 16, 2, 1, 2, 0.02);
			});
		});
	}
}
