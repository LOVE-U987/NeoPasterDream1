package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModParticleTypes;
import net.pasterdream.init.PasterdreamModMobEffects;
import net.pasterdream.entity.AaroncosLefthand0Entity;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.tags.TagKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Comparator;

public class AaroncosLefthandSkillSwordProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (!(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(PasterdreamModMobEffects.SHADOW_SILENCE_BUFF.get()))) {
			if (entity instanceof AaroncosLefthand0Entity) {
				((AaroncosLefthand0Entity) entity).setAnimation("empty");
			}
			if (new Object() {
				public int getScore(String score, Entity _ent) {
					Scoreboard _sc = _ent.level().getScoreboard();
					Objective _so = _sc.getObjective(score);
					if (_so != null)
						return _sc.getOrCreatePlayerScore(_ent.getScoreboardName(), _so).getScore();
					return 0;
				}
			}.getScore("skill", entity) == 0 && new Object() {
				public int getScore(String score, Entity _ent) {
					Scoreboard _sc = _ent.level().getScoreboard();
					Objective _so = _sc.getObjective(score);
					if (_so != null)
						return _sc.getOrCreatePlayerScore(_ent.getScoreboardName(), _so).getScore();
					return 0;
				}
			}.getScore("switch", entity) == 1) {
				if (new Object() {
					public int getScore(String score, Entity _ent) {
						Scoreboard _sc = _ent.level().getScoreboard();
						Objective _so = _sc.getObjective(score);
						if (_so != null)
							return _sc.getOrCreatePlayerScore(_ent.getScoreboardName(), _so).getScore();
						return 0;
					}
				}.getScore("skill_sword", entity) != 1) {
					if ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) > 1) {
						{
							Entity _ent = entity;
							Scoreboard _sc = _ent.level().getScoreboard();
							Objective _so = _sc.getObjective("skill");
							if (_so == null)
								_so = _sc.addObjective("skill", ObjectiveCriteria.DUMMY, Component.literal("skill"), ObjectiveCriteria.RenderType.INTEGER);
							_sc.getOrCreatePlayerScore(_ent.getScoreboardName(), _so).setScore(1);
						}
						{
							Entity _ent = entity;
							Scoreboard _sc = _ent.level().getScoreboard();
							Objective _so = _sc.getObjective("skill_sword");
							if (_so == null)
								_so = _sc.addObjective("skill_sword", ObjectiveCriteria.DUMMY, Component.literal("skill_sword"), ObjectiveCriteria.RenderType.INTEGER);
							_sc.getOrCreatePlayerScore(_ent.getScoreboardName(), _so).setScore(1);
						}
						PasterdreamMod.queueServerWork(140, () -> {
							{
								Entity _ent = entity;
								Scoreboard _sc = _ent.level().getScoreboard();
								Objective _so = _sc.getObjective("skill");
								if (_so == null)
									_so = _sc.addObjective("skill", ObjectiveCriteria.DUMMY, Component.literal("skill"), ObjectiveCriteria.RenderType.INTEGER);
								_sc.getOrCreatePlayerScore(_ent.getScoreboardName(), _so).setScore(0);
							}
						});
						PasterdreamMod.queueServerWork(420, () -> {
							{
								Entity _ent = entity;
								Scoreboard _sc = _ent.level().getScoreboard();
								Objective _so = _sc.getObjective("skill_sword");
								if (_so == null)
									_so = _sc.addObjective("skill_sword", ObjectiveCriteria.DUMMY, Component.literal("skill_sword"), ObjectiveCriteria.RenderType.INTEGER);
								_sc.getOrCreatePlayerScore(_ent.getScoreboardName(), _so).setScore(0);
							}
						});
						if (entity instanceof AaroncosLefthand0Entity) {
							((AaroncosLefthand0Entity) entity).setAnimation("empty");
						}
						if (entity instanceof AaroncosLefthand0Entity) {
							((AaroncosLefthand0Entity) entity).setAnimation("skill_sword");
						}
						if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 120, 1, false, false));
						entity.setDeltaMovement(new Vec3(0, (-2), 0));
						if (world instanceof Level _level) {
							if (!_level.isClientSide()) {
								_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:stone_break")), SoundSource.MASTER, 1, 1);
							} else {
								_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:stone_break")), SoundSource.MASTER, 1, 1, false);
							}
						}
						{
							final Vec3 _center = new Vec3(x, y, z);
							List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(30 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
							for (Entity entityiterator : _entfound) {
								if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("pasterdream:special_entity_tag")))
										&& !entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("pasterdream:shadow_mob")))) {
									if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
										_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.CONFUSION_BUFF.get(), 60, 1, false, false));
								}
							}
						}
						PasterdreamMod.queueServerWork(15, () -> {
							if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
								_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 4, false, false));
							if (world instanceof ServerLevel _level)
								_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.SHADOW_STONE_PARTICLE.get()), x, y, z, 128, 1, 2, 1, 0.5);
						});
						PasterdreamMod.queueServerWork(25, () -> {
							if (world instanceof ServerLevel _level)
								_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.SHADOW_STONE_PARTICLE.get()), x, y, z, 128, 1, 2, 1, 0.5);
						});
						PasterdreamMod.queueServerWork(57, () -> {
							AaroncosLefthandSkillSword0Procedure.execute(world, x, y, z, entity);
						});
						PasterdreamMod.queueServerWork(70, () -> {
							AaroncosLefthandSkillSword0Procedure.execute(world, x, y, z, entity);
						});
						PasterdreamMod.queueServerWork(83, () -> {
							AaroncosLefthandSkillSword0Procedure.execute(world, x, y, z, entity);
						});
						PasterdreamMod.queueServerWork(88, () -> {
							AaroncosLefthandSkillSword0Procedure.execute(world, x, y, z, entity);
						});
						PasterdreamMod.queueServerWork(95, () -> {
							AaroncosLefthandSkillSword0Procedure.execute(world, x, y, z, entity);
						});
						PasterdreamMod.queueServerWork(105, () -> {
							AaroncosLefthandSkillSword0Procedure.execute(world, x, y, z, entity);
						});
						PasterdreamMod.queueServerWork(112, () -> {
							AaroncosLefthandSkillSword0Procedure.execute(world, x, y, z, entity);
						});
					}
				}
			}
			if (!world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), 64, 64, 64), e -> true).isEmpty()) {
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(64 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof Player) {
							entityiterator.getPersistentData().putString("aaroncos_lefthand_0_name", (entity.getDisplayName().getString()));
							entityiterator.getPersistentData().putDouble("aaroncos_lefthand_0_hp", (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1));
							if (entity.isAlive()) {
								entityiterator.getPersistentData().putBoolean("aaroncos_lefthand_0_life", true);
							} else {
								entityiterator.getPersistentData().putBoolean("aaroncos_lefthand_0_life", false);
							}
						}
					}
				}
			}
		}
		AaroncosHandBloodlockProcedure.execute(world, x, y, z, entity);
	}
}
