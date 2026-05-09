package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModParticleTypes;
import net.pasterdream.entity.ShadowMagicballEntity;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.TagKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;

import java.util.List;
import java.util.Comparator;

public class ShadowMagicballPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity.getPersistentData().getBoolean("switch") != true) {
			if (world instanceof ServerLevel _level)
				_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.SHADOW_STONE_PARTICLE.get()), x, y, z, 4, 0.2, 0.2, 0.2, 0.1);
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.SMOKE, x, y, z, 4, 0.2, 0.2, 0.2, 0.1);
			{
				final Vec3 _center = new Vec3(x, y, z);
				List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(3 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
				for (Entity entityiterator : _entfound) {
					if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("pasterdream:special_entity_tag")))
							&& !entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("pasterdream:shadow_mob")))) {
						entity.getPersistentData().putBoolean("switch", true);
						entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MAGIC)), 4);
						ShadowMagicballPr1Procedure.execute(world, x, y, z, entity);
						if (entity instanceof ShadowMagicballEntity) {
							((ShadowMagicballEntity) entity).setAnimation("empty");
						}
						if (entity instanceof ShadowMagicballEntity) {
							((ShadowMagicballEntity) entity).setAnimation("death");
						}
					}
				}
			}
		}
	}
}
