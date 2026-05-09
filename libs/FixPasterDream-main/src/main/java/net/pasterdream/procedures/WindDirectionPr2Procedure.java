package net.pasterdream.procedures;

import top.theillusivec4.curios.api.CuriosApi;

import net.pasterdream.init.PasterdreamModMobEffects;
import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModGameRules;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;

public class WindDirectionPr2Procedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if ((world instanceof Level _lvl ? _lvl.dimension() : Level.OVERWORLD) == (ResourceKey.create(Registries.DIMENSION, new ResourceLocation("pasterdream:wind_journey_world")))) {
			if (entity instanceof LivingEntity lv ? CuriosApi.getCuriosHelper().findEquippedCurio(PasterdreamModItems.WIND_KNIGHT_FLAG.get(), lv).isPresent() : false == false) {
				if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 4) {
					if (entity.getYRot() <= 35 && entity.getYRot() >= -35) {
						if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.TAILWIND_BUFF.get(), 20, (int) (0 + entity.getPersistentData().getDouble("player_tailwind_force"))));
					}
				} else if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 5) {
					if (entity.getYRot() <= 70 && entity.getYRot() >= 10) {
						if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.TAILWIND_BUFF.get(), 20, (int) (0 + entity.getPersistentData().getDouble("player_tailwind_force"))));
					}
				}
				if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 6) {
					if (entity.getYRot() <= 125 && entity.getYRot() >= 55) {
						if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.TAILWIND_BUFF.get(), 20, (int) (0 + entity.getPersistentData().getDouble("player_tailwind_force"))));
					}
				} else if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 7) {
					if (entity.getYRot() <= 170 && entity.getYRot() >= 100) {
						if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.TAILWIND_BUFF.get(), 20, (int) (0 + entity.getPersistentData().getDouble("player_tailwind_force"))));
					}
				}
				if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 0) {
					if (entity.getYRot() <= 180 && entity.getYRot() >= 145 || entity.getYRot() <= -145 && entity.getYRot() >= -180) {
						if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.TAILWIND_BUFF.get(), 20, (int) (0 + entity.getPersistentData().getDouble("player_tailwind_force"))));
					}
				} else if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 1) {
					if (entity.getYRot() <= -100 && entity.getYRot() >= -170) {
						if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.TAILWIND_BUFF.get(), 20, (int) (0 + entity.getPersistentData().getDouble("player_tailwind_force"))));
					}
				}
				if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 2) {
					if (entity.getYRot() <= -55 && entity.getYRot() >= -125) {
						if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.TAILWIND_BUFF.get(), 20, (int) (0 + entity.getPersistentData().getDouble("player_tailwind_force"))));
					}
				} else if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 3) {
					if (entity.getYRot() <= -10 && entity.getYRot() >= -80) {
						if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.TAILWIND_BUFF.get(), 20, (int) (0 + entity.getPersistentData().getDouble("player_tailwind_force"))));
					}
				}
			} else {
				if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 4) {
					if (entity.getYRot() <= 35 && entity.getYRot() >= -35) {
						if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.DEADWIND_BUFF.get(), 20, (int) (0 + entity.getPersistentData().getDouble("player_deadwind_force"))));
					}
				} else if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 5) {
					if (entity.getYRot() <= 70 && entity.getYRot() >= 10) {
						if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.DEADWIND_BUFF.get(), 20, (int) (0 + entity.getPersistentData().getDouble("player_deadwind_force"))));
					}
				}
				if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 6) {
					if (entity.getYRot() <= 125 && entity.getYRot() >= 55) {
						if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.DEADWIND_BUFF.get(), 20, (int) (0 + entity.getPersistentData().getDouble("player_deadwind_force"))));
					}
				} else if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 7) {
					if (entity.getYRot() <= 170 && entity.getYRot() >= 100) {
						if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.DEADWIND_BUFF.get(), 20, (int) (0 + entity.getPersistentData().getDouble("player_deadwind_force"))));
					}
				}
				if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 0) {
					if (entity.getYRot() <= 180 && entity.getYRot() >= 145 || entity.getYRot() <= -145 && entity.getYRot() >= -180) {
						if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.DEADWIND_BUFF.get(), 20, (int) (0 + entity.getPersistentData().getDouble("player_deadwind_force"))));
					}
				} else if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 1) {
					if (entity.getYRot() <= -100 && entity.getYRot() >= -170) {
						if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.DEADWIND_BUFF.get(), 20, (int) (0 + entity.getPersistentData().getDouble("player_deadwind_force"))));
					}
				}
				if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 2) {
					if (entity.getYRot() <= -55 && entity.getYRot() >= -125) {
						if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.DEADWIND_BUFF.get(), 20, (int) (0 + entity.getPersistentData().getDouble("player_deadwind_force"))));
					}
				} else if ((world.getLevelData().getGameRules().getInt(PasterdreamModGameRules.PASTERDREAM_WIND_DIRECTION)) == 3) {
					if (entity.getYRot() <= -10 && entity.getYRot() >= -80) {
						if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.DEADWIND_BUFF.get(), 20, (int) (0 + entity.getPersistentData().getDouble("player_deadwind_force"))));
					}
				}
			}
		}
	}
}
