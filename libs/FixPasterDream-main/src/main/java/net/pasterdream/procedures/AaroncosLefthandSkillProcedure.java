package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModMobEffects;
import net.pasterdream.PasterdreamMod;

import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;

public class AaroncosLefthandSkillProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (!(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(PasterdreamModMobEffects.SHADOW_SILENCE_BUFF.get()))) {
			if (new Object() {
				public int getScore(String score, Entity _ent) {
					Scoreboard _sc = _ent.level().getScoreboard();
					Objective _so = _sc.getObjective(score);
					if (_so != null)
						return _sc.getOrCreatePlayerScore(_ent.getScoreboardName(), _so).getScore();
					return 0;
				}
			}.getScore("skill", entity) != 1 && new Object() {
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
				}.getScore("skill_sprint", entity) != 1 && new Object() {
					public int getScore(String score, Entity _ent) {
						Scoreboard _sc = _ent.level().getScoreboard();
						Objective _so = _sc.getObjective(score);
						if (_so != null)
							return _sc.getOrCreatePlayerScore(_ent.getScoreboardName(), _so).getScore();
						return 0;
					}
				}.getScore("skill_sprint", entity) != 3) {
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
						Objective _so = _sc.getObjective("skill_sprint");
						if (_so == null)
							_so = _sc.addObjective("skill_sprint", ObjectiveCriteria.DUMMY, Component.literal("skill_sprint"), ObjectiveCriteria.RenderType.INTEGER);
						_sc.getOrCreatePlayerScore(_ent.getScoreboardName(), _so).setScore(1);
					}
					AaroncosLefthandSkillSprintProcedure.execute(world, x, y, z, entity);
					PasterdreamMod.queueServerWork(20, () -> {
						{
							Entity _ent = entity;
							Scoreboard _sc = _ent.level().getScoreboard();
							Objective _so = _sc.getObjective("skill_hit");
							if (_so == null)
								_so = _sc.addObjective("skill_hit", ObjectiveCriteria.DUMMY, Component.literal("skill_hit"), ObjectiveCriteria.RenderType.INTEGER);
							_sc.getOrCreatePlayerScore(_ent.getScoreboardName(), _so).setScore((int) (new Object() {
								public int getScore(String score, Entity _ent) {
									Scoreboard _sc = _ent.level().getScoreboard();
									Objective _so = _sc.getObjective(score);
									if (_so != null)
										return _sc.getOrCreatePlayerScore(_ent.getScoreboardName(), _so).getScore();
									return 0;
								}
							}.getScore("skill_hit", entity) + 1));
						}
					});
					PasterdreamMod.queueServerWork(40, () -> {
						{
							Entity _ent = entity;
							Scoreboard _sc = _ent.level().getScoreboard();
							Objective _so = _sc.getObjective("skill");
							if (_so == null)
								_so = _sc.addObjective("skill", ObjectiveCriteria.DUMMY, Component.literal("skill"), ObjectiveCriteria.RenderType.INTEGER);
							_sc.getOrCreatePlayerScore(_ent.getScoreboardName(), _so).setScore(0);
						}
					});
					PasterdreamMod.queueServerWork(120, () -> {
						{
							Entity _ent = entity;
							Scoreboard _sc = _ent.level().getScoreboard();
							Objective _so = _sc.getObjective("skill_sprint");
							if (_so == null)
								_so = _sc.addObjective("skill_sprint", ObjectiveCriteria.DUMMY, Component.literal("skill_sprint"), ObjectiveCriteria.RenderType.INTEGER);
							_sc.getOrCreatePlayerScore(_ent.getScoreboardName(), _so).setScore(0);
						}
					});
				}
				if (new Object() {
					public int getScore(String score, Entity _ent) {
						Scoreboard _sc = _ent.level().getScoreboard();
						Objective _so = _sc.getObjective(score);
						if (_so != null)
							return _sc.getOrCreatePlayerScore(_ent.getScoreboardName(), _so).getScore();
						return 0;
					}
				}.getScore("skill_hit", entity) == 3) {
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
						Objective _so = _sc.getObjective("skill_hit");
						if (_so == null)
							_so = _sc.addObjective("skill_hit", ObjectiveCriteria.DUMMY, Component.literal("skill_hit"), ObjectiveCriteria.RenderType.INTEGER);
						_sc.getOrCreatePlayerScore(_ent.getScoreboardName(), _so).setScore(4);
					}
					AaroncosLefthandSkillHitProcedure.execute(world, entity);
					PasterdreamMod.queueServerWork(100, () -> {
						{
							Entity _ent = entity;
							Scoreboard _sc = _ent.level().getScoreboard();
							Objective _so = _sc.getObjective("skill");
							if (_so == null)
								_so = _sc.addObjective("skill", ObjectiveCriteria.DUMMY, Component.literal("skill"), ObjectiveCriteria.RenderType.INTEGER);
							_sc.getOrCreatePlayerScore(_ent.getScoreboardName(), _so).setScore(0);
						}
						{
							Entity _ent = entity;
							Scoreboard _sc = _ent.level().getScoreboard();
							Objective _so = _sc.getObjective("skill_hit");
							if (_so == null)
								_so = _sc.addObjective("skill_hit", ObjectiveCriteria.DUMMY, Component.literal("skill_hit"), ObjectiveCriteria.RenderType.INTEGER);
							_sc.getOrCreatePlayerScore(_ent.getScoreboardName(), _so).setScore(0);
						}
					});
				}
			}
			AaroncosLefthandTickBossbarProcedure.execute(world, x, y, z, entity);
		}
	}
}
