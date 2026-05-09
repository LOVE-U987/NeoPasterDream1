package net.pasterdream.procedures;

import top.theillusivec4.curios.api.CuriosApi;

import net.pasterdream.init.PasterdreamModMobEffects;
import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModEntities;
import net.pasterdream.configuration.PasterdreamConfigCommonConfiguration;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;

public class InsandBuffPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity.isAlive() && entity instanceof Player) {
			if ((entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(PasterdreamModMobEffects.INSAND_BUFF.get()) ? _livEnt.getEffect(PasterdreamModMobEffects.INSAND_BUFF.get()).getAmplifier() : 0) == 0) {
				if (PasterdreamConfigCommonConfiguration.LOW_SAN_PICTURE_JITTER.get()) {
					{
						Entity _ent = entity;
						_ent.setYRot((float) (entity.getYRot() + Mth.nextDouble(RandomSource.create(), -0.5, 0.5)));
						_ent.setXRot((float) (entity.getXRot() + Mth.nextDouble(RandomSource.create(), -0.5, 0.5)));
						_ent.setYBodyRot(_ent.getYRot());
						_ent.setYHeadRot(_ent.getYRot());
						_ent.yRotO = _ent.getYRot();
						_ent.xRotO = _ent.getXRot();
						if (_ent instanceof LivingEntity _entity) {
							_entity.yBodyRotO = _entity.getYRot();
							_entity.yHeadRotO = _entity.getYRot();
						}
					}
				}
				if (Math.random() < 0.005) {
					//if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
					//	_entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 25, 0, false, false));
					if (Math.random() < 0.3) {
						if ((world.getBlockState(BlockPos.containing(x, y + 2, z))).getBlock() == Blocks.AIR) {
							if (world instanceof ServerLevel _level) {
								Entity entityToSpawn = PasterdreamModEntities.TERRORBEAK.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
								if (entityToSpawn != null) {
									entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
								}
							}
						}
					}
					if (Math.random() < 0.5) {
						if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.AIR) {
							if (world instanceof ServerLevel _level) {
								Entity entityToSpawn = PasterdreamModEntities.SHADOW_HAND.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
								if (entityToSpawn != null) {
									entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
								}
							}
						}
					}
				}
			}
			if ((entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(PasterdreamModMobEffects.INSAND_BUFF.get()) ? _livEnt.getEffect(PasterdreamModMobEffects.INSAND_BUFF.get()).getAmplifier() : 0) == 1) {
				if (PasterdreamConfigCommonConfiguration.LOW_SAN_PICTURE_JITTER.get()) {
					{
						Entity _ent = entity;
						_ent.setYRot((float) (entity.getYRot() + Mth.nextDouble(RandomSource.create(), -1, 1)));
						_ent.setXRot((float) (entity.getXRot() + Mth.nextDouble(RandomSource.create(), -1, 1)));
						_ent.setYBodyRot(_ent.getYRot());
						_ent.setYHeadRot(_ent.getYRot());
						_ent.yRotO = _ent.getYRot();
						_ent.xRotO = _ent.getXRot();
						if (_ent instanceof LivingEntity _entity) {
							_entity.yBodyRotO = _entity.getYRot();
							_entity.yHeadRotO = _entity.getYRot();
						}
					}
				}
				if (Math.random() < 0.01) {
					//if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
					//	_entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 28, 0, false, false));
					if (Math.random() < 0.25) {
						if ((world.getBlockState(BlockPos.containing(x, y + 2, z))).getBlock() == Blocks.AIR) {
							if (world instanceof ServerLevel _level) {
								Entity entityToSpawn = PasterdreamModEntities.TERRORBEAK.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
								if (entityToSpawn != null) {
									entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
								}
							}
						}
					}
					if (Math.random() < 0.5) {
						if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.AIR) {
							if (world instanceof ServerLevel _level) {
								Entity entityToSpawn = PasterdreamModEntities.SHADOW_HAND.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
								if (entityToSpawn != null) {
									entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
								}
							}
						}
					}
				}
			}
			if ((entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(PasterdreamModMobEffects.INSAND_BUFF.get()) ? _livEnt.getEffect(PasterdreamModMobEffects.INSAND_BUFF.get()).getAmplifier() : 0) == 2) {
				if (PasterdreamConfigCommonConfiguration.LOW_SAN_PICTURE_JITTER.get()) {
					{
						Entity _ent = entity;
						_ent.setYRot((float) (entity.getYRot() + Mth.nextDouble(RandomSource.create(), -3, 3)));
						_ent.setXRot((float) (entity.getXRot() + Mth.nextDouble(RandomSource.create(), -2, 2)));
						_ent.setYBodyRot(_ent.getYRot());
						_ent.setYHeadRot(_ent.getYRot());
						_ent.yRotO = _ent.getYRot();
						_ent.xRotO = _ent.getXRot();
						if (_ent instanceof LivingEntity _entity) {
							_entity.yBodyRotO = _entity.getYRot();
							_entity.yHeadRotO = _entity.getYRot();
						}
					}
				}
				if (Math.random() < 0.025) {
					//if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
						//_entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 30, 0, false, false));
					if (entity instanceof LivingEntity lv ? CuriosApi.getCuriosHelper().findEquippedCurio(PasterdreamModItems.DEGENERATE_BODYS.get(), lv).isPresent() : false == false) {
						if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) > 1) {
							entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.FELL_OUT_OF_WORLD)), 1);
						}
					}
					if (Math.random() < 0.05) {
						if ((world.getBlockState(BlockPos.containing(x, y + 2, z))).getBlock() == Blocks.AIR) {
							if (world instanceof ServerLevel _level) {
								Entity entityToSpawn = PasterdreamModEntities.TERRORBEAK.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
								if (entityToSpawn != null) {
									entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
								}
							}
						}
					}
					if (Math.random() < 0.03) {
						if ((world.getBlockState(BlockPos.containing(x, y + 2, z))).getBlock() == Blocks.AIR) {
							if (world instanceof ServerLevel _level) {
								Entity entityToSpawn = PasterdreamModEntities.CRAZY_TERRORBEAK.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
								if (entityToSpawn != null) {
									entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
								}
							}
						}
					}
					if (Math.random() < 0.5) {
						if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.AIR) {
							if (world instanceof ServerLevel _level) {
								Entity entityToSpawn = PasterdreamModEntities.SHADOW_HAND.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
								if (entityToSpawn != null) {
									entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
								}
							}
						}
					}
				}
			}
		}
	}
}
