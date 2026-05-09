package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModEntities;
import net.pasterdream.init.PasterdreamModBlocks;
import net.pasterdream.init.PasterdreamModAttributes;
import net.pasterdream.entity.MoltengoldWandProjectileEntity;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

public class TruestMoltengoldWandPr1Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null)
			return;
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.dragon_fireball.explode")), SoundSource.NEUTRAL, (float) 0.5, 1);
			} else {
				_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.dragon_fireball.explode")), SoundSource.NEUTRAL, (float) 0.5, 1, false);
			}
		}
		if ((world.getBlockState(BlockPos.containing(x, y + 1, z))).getBlock() == Blocks.AIR && !((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.AIR)) {
			world.setBlock(BlockPos.containing(x, y + 1, z), Blocks.FIRE.defaultBlockState(), 3);
		}
		if ((world.getBlockState(BlockPos.containing(x, y + 2, z))).getBlock() == Blocks.AIR) {
			world.setBlock(BlockPos.containing(x, y + 2, z), PasterdreamModBlocks.STARCALL_BLOCK.get().defaultBlockState(), 3);
		}
		if (world.canSeeSkyFromBelowWater(BlockPos.containing(x, y + 2, z)) && Math.random() <= 0.5) {
			world.setBlock(BlockPos.containing(x, y + 11, z), PasterdreamModBlocks.STARCALL_CRACK.get().defaultBlockState(), 3);
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y + 1, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:starcall_crack")), SoundSource.NEUTRAL, (float) 1.5, 1);
				} else {
					_level.playLocalSound(x, (y + 1), z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:starcall_crack")), SoundSource.NEUTRAL, (float) 1.5, 1, false);
				}
			}
		}
		if (sourceentity instanceof Player) {
			if (((LivingEntity) sourceentity).getAttribute(PasterdreamModAttributes.MAGICPOWER.get()).getBaseValue() >= 1) {
				for (int index0 = 0; index0 < (int) (2 + ((LivingEntity) sourceentity).getAttribute(PasterdreamModAttributes.MAGICPOWER.get()).getBaseValue()); index0++) {
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
						_entityToSpawn.setPos((entity.getX()), (entity.getY() + 0.5), (entity.getZ()));
						_entityToSpawn.shoot((Mth.nextDouble(RandomSource.create(), -1, 1)), 1, (Mth.nextDouble(RandomSource.create(), -1, 1)), (float) 0.5, (float) 0.5);
						projectileLevel.addFreshEntity(_entityToSpawn);
					}
				}
			}
		}
	}
}
