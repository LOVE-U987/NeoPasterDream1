package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModEntities;
import net.pasterdream.entity.MoltengoldWandProjectileEntity;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

public class StarcallCrackPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world instanceof ServerLevel _level)
			_level.sendParticles(ParticleTypes.LAVA, (x + 0.5), (y - 0.1), (z + 0.5), 3, 1, 0.1, 1, 0.1);
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.dragon_fireball.explode")), SoundSource.NEUTRAL, (float) 0.6, 1);
			} else {
				_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.dragon_fireball.explode")), SoundSource.NEUTRAL, (float) 0.6, 1, false);
			}
		}
		if (!((world.getFluidState(BlockPos.containing(x, y - 10, z)).createLegacyBlock()).getBlock() instanceof LiquidBlock)) {
			if (world instanceof ServerLevel projectileLevel) {
				Projectile _entityToSpawn = new Object() {
					public Projectile getArrow(Level level, float damage, int knockback) {
						AbstractArrow entityToSpawn = new MoltengoldWandProjectileEntity(PasterdreamModEntities.MOLTENGOLD_WAND_PROJECTILE.get(), level);
						entityToSpawn.setBaseDamage(damage);
						entityToSpawn.setKnockback(knockback);
						entityToSpawn.setSilent(true);
						return entityToSpawn;
					}
				}.getArrow(projectileLevel, 5, 1);
				_entityToSpawn.setPos((x + 0.5), (y + 0.5), (z + 0.5));
				_entityToSpawn.shoot(0, (-1), 0, 1, 8);
				projectileLevel.addFreshEntity(_entityToSpawn);
			}
		}
	}
}
