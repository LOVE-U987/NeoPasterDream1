package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModParticleTypes;
import net.pasterdream.init.PasterdreamModMobEffects;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.tags.TagKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.List;
import java.util.Comparator;

public class FoxFirePr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity.getPersistentData().getDouble("time") >= 400) {
			if (!entity.level().isClientSide())
				entity.discard();
		} else {
			entity.getPersistentData().putDouble("time", (entity.getPersistentData().getDouble("time") + 1));
			if (world instanceof ServerLevel _level)
				_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.FOX_FIRE_0_PARTICLE.get()), x, y, z, 5, 5, 0.15, 5, 1);
			if (world instanceof ServerLevel _level)
				_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.FOX_FIRE_1_PARTICLE.get()), x, y, z, 5, 5, 0.15, 5, 1);
			{
				final Vec3 _center = new Vec3(x, y, z);
				List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(6 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
				for (Entity entityiterator : _entfound) {
					if (entityiterator instanceof Mob && !entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("pasterdream:special_entity_tag")))) {
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.VULNERABILITY_BUFF.get(), 20, 1));
						if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 0));
					} else {
						if (entityiterator instanceof Player) {
							if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
								_entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));
						}
					}
				}
			}
		}
	}
}
