package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModAttributes;
import net.pasterdream.entity.AaroncosRighthand0Entity;
import net.pasterdream.entity.AaroncosLefthand0Entity;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.pasterdream.init.PasterdreamModMobEffects;

import java.util.List;
import java.util.Comparator;

public class ShadowTuneTotemPr1Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		{
			final Vec3 _center = new Vec3(x, y, z);
			List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(64 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
			for (Entity entityiterator : _entfound) {
				if (entityiterator instanceof AaroncosLefthand0Entity || entityiterator instanceof AaroncosRighthand0Entity) {
					if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
						_entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 0, false, false));
				}
				if (entityiterator instanceof Player entity) {
                    entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.OPPRESSION_BUFF.get(),20,0));
                    entity.removeEffect(MobEffects.DAMAGE_RESISTANCE);
				}
			}
		}
	}
}
