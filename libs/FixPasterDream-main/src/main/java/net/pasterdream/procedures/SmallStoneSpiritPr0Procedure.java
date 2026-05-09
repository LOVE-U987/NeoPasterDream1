package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModBlocks;
import net.pasterdream.entity.SmallStoneSpiritEntity;
import net.pasterdream.PasterdreamMod;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Comparator;

public class SmallStoneSpiritPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		{
			final Vec3 _center = new Vec3(x, y, z);
			List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(10 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
			for (Entity entityiterator : _entfound) {
				if (entityiterator instanceof SmallStoneSpiritEntity) {
					entityiterator.getPersistentData().putDouble("size", (entityiterator.getPersistentData().getDouble("size") + 0.1));
					if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
						_entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 6, false, false));
					if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
						_entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 0, false, false));
					if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
						_entity.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 400, (int) (entityiterator.getPersistentData().getDouble("size") * 10 - 1), false, false));
				}
			}
		}
		if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.AIR && !((world.getBlockState(BlockPos.containing(x, y - 1, z))).getBlock() == Blocks.AIR)) {
			if (Math.random() > 0.5) {
				PasterdreamMod.queueServerWork(22, () -> {
					world.setBlock(BlockPos.containing(x, y, z), PasterdreamModBlocks.SMALL_STONE_SPIRIT_BLOCK.get().defaultBlockState(), 3);
				});
			}
		}
	}
}
