package com.pasterdream.pasterdreammod.entity;

import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.util.ServerScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

/**
 * 法术命中效果 (Spell Impact Effects)
 * <p>
 * 完整还原原版 PasterDream 五种法术投射物的命中效果时序
 * （对应原版 LightningSpellPr0 / PoisonSpellPr0 / HealingSpellPr0 / FurySpellPr0 / IceSpellPr0-Pr1 procedure），
 * 所有粒子数量、作用范围、效果强度、延迟节奏均与原版一致。
 * <p>
 * 延迟任务通过 {@link ServerScheduler} 真定时派发（等价原版 queueServerWork；
 * 不能用 vanilla TickTask 队列——它在服务器空闲时立即执行而非定时）。
 */
public final class SpellEffects {

    private SpellEffects() {
    }

    /** 闪电法术落雷时刻（tick，相对命中） */
    private static final int[] LIGHTNING_STRIKE_TICKS = {55, 65, 75, 85};
    /** 闪电法术火花脉冲时刻 */
    private static final int[] LIGHTNING_SPARK_TICKS = {7, 12, 17, 22, 27, 37, 42, 47};
    /** 剧毒法术三波攻势的起始时刻 */
    private static final int[] POISON_WAVE_BASE_TICKS = {1, 81, 161};
    /** 剧毒法术每波内施加效果的脉冲时刻（相对波次起始） */
    private static final int[] POISON_EFFECT_PULSE_TICKS = {2, 22, 42, 62, 82};
    /** 剧毒法术每波内纯粒子脉冲时刻（相对波次起始） */
    private static final int[] POISON_PARTICLE_PULSE_TICKS = {12, 32, 52, 72};
    /** 狂暴法术增益脉冲时刻 */
    private static final int[] FURY_PULSE_TICKS = {10, 20, 30, 40, 50};
    /** 冰冻法术冻结波时刻 */
    private static final int[] ICE_WAVE_TICKS = {5, 10, 15, 20};

    /**
     * 闪电法术命中：烟雾/电火花脉冲后，在 5*5 区域内生成 4 次随机落雷
     * （与原版 LightningSpellPr0Procedure 一致）
     *
     * @param level 服务端世界
     * @param x     命中点 X
     * @param y     命中点 Y
     * @param z     命中点 Z
     */
    public static void lightning(ServerLevel level, double x, double y, double z) {
        playSound(level, x, y, z, SoundEvents.SPLASH_POTION_BREAK, 1.0f);
        level.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, x, y + 1, z, 64, 2, 1, 2, 0.01);
        level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y + 1, z, 48, 2, 1, 2, 0.01);
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y + 1, z, 32, 2, 1, 2, 0.02);

        schedule(level, 2, () -> {
            playSound(level, x, y, z, PDSounds.LIGHTNING_SPELL.get(), 1.0f);
            level.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, x, y + 1, z, 32, 2, 1, 2, 0.02);
            level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y + 1, z, 32, 2, 1, 2, 0.02);
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y + 1, z, 32, 2, 1, 2, 0.02);
        });
        // 周期性电火花（t=32 时额外补一轮烟雾）
        for (int t : LIGHTNING_SPARK_TICKS) {
            schedule(level, t, () ->
                    level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y + 1, z, 32, 2, 1, 2, 0.02));
        }
        schedule(level, 32, () -> {
            level.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, x, y, z, 32, 2, 1, 2, 0.02);
            level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, 32, 2, 1, 2, 0.02);
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y + 1, z, 32, 2, 1, 2, 0.02);
        });
        // 4 次随机落雷（±2 格 = 5*5 区域）
        for (int t : LIGHTNING_STRIKE_TICKS) {
            schedule(level, t, () -> {
                level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y + 1, z, 128, 2, 1, 2, 0.5);
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
                if (bolt != null) {
                    RandomSource random = level.getRandom();
                    bolt.moveTo(Vec3.atBottomCenterOf(BlockPos.containing(
                            x + Mth.nextDouble(random, -2, 2), y, z + Mth.nextDouble(random, -2, 2))));
                    level.addFreshEntity(bolt);
                }
            });
        }
    }

    /**
     * 剧毒法术命中：三波毒雾攻势，对 7*7 区域内所有生物反复施加剧毒 IV/虚弱/缓慢
     * （与原版 PoisonSpellPr0Procedure 一致，总时长约 12 秒）
     *
     * @param level 服务端世界
     * @param x     命中点 X
     * @param y     命中点 Y
     * @param z     命中点 Z
     */
    public static void poison(ServerLevel level, double x, double y, double z) {
        playSound(level, x, y, z, SoundEvents.SPLASH_POTION_BREAK, 1.0f);
        level.sendParticles(PDParticles.POISON_GAS_PARTICLE.holder().get(), x, y + 1, z, 48, 2, 1, 2, 0.01);
        level.sendParticles(PDParticles.POISON_SOUL_PARTICLE.holder().get(), x, y + 1, z, 24, 2, 1, 2, 0.02);

        for (int base : POISON_WAVE_BASE_TICKS) {
            // 效果脉冲：粒子 + 施加剧毒/虚弱/缓慢
            for (int pulse : POISON_EFFECT_PULSE_TICKS) {
                schedule(level, base + pulse, () -> {
                    level.sendParticles(PDParticles.POISON_GAS_PARTICLE.holder().get(),
                            x, y + 1, z, 64, 2, 1, 2, 0.01);
                    level.sendParticles(PDParticles.POISON_SOUL_PARTICLE.holder().get(),
                            x, y + 1, z, 32, 2, 1, 2, 0.02);
                    applyPoisonEffects(level, x, y, z);
                });
            }
            // 纯粒子脉冲
            for (int pulse : POISON_PARTICLE_PULSE_TICKS) {
                schedule(level, base + pulse, () -> {
                    level.sendParticles(PDParticles.POISON_GAS_PARTICLE.holder().get(),
                            x, y + 1, z, 32, 2, 1, 2, 0.01);
                    level.sendParticles(PDParticles.POISON_SOUL_PARTICLE.holder().get(),
                            x, y + 1, z, 16, 2, 1, 2, 0.02);
                });
            }
        }
    }

    /**
     * 对 7*7 区域内所有生物施加剧毒 IV / 虚弱 / 缓慢（各 20 tick，与原版一致——含施法者自身）
     */
    private static void applyPoisonEffects(ServerLevel level, double x, double y, double z) {
        Vec3 center = new Vec3(x, y, z);
        List<Entity> targets = level.getEntitiesOfClass(Entity.class,
                new AABB(center, center).inflate(7 / 2d), e -> true);
        for (Entity target : targets) {
            if (target instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.POISON, 20, 3));
                living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 0));
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 0));
            }
        }
    }

    /**
     * 治疗法术命中：生成治疗立场实体（持续 20 秒，每秒回复立场内玩家/和平生物 5% 最大生命值）
     * （与原版 HealingSpellPr0Procedure 一致）
     *
     * @param level 服务端世界
     * @param x     命中点 X
     * @param y     命中点 Y
     * @param z     命中点 Z
     */
    public static void healing(ServerLevel level, double x, double y, double z) {
        playSound(level, x, y, z, SoundEvents.SPLASH_POTION_BREAK, 1.0f);
        level.sendParticles(PDParticles.HEALING_SPELL_PARTICLE.holder().get(), x, y + 1, z, 64, 2, 1, 2, 0.05);
        level.sendParticles(PDParticles.YELLOW_SMOKE_PARTICLE.holder().get(), x, y + 0.5, z, 32, 2, 1, 2, 0.05);
        spawnField(level, PDEntities.HEALING_SPELL_ENTITY.get(), x, y, z);
        schedule(level, 2, () -> playSound(level, x, y, z, PDSounds.HEALING_SPELL.get(), 0.2f));
    }

    /**
     * 狂暴法术命中：生成狂暴立场实体，6 次脉冲对 8*8 区域内玩家施加狂暴增益
     * （与原版 FurySpellPr0/Pr1Procedure 一致）
     *
     * @param level 服务端世界
     * @param x     命中点 X
     * @param y     命中点 Y
     * @param z     命中点 Z
     */
    public static void fury(ServerLevel level, double x, double y, double z) {
        playSound(level, x, y, z, SoundEvents.SPLASH_POTION_BREAK, 1.2f);
        playSound(level, x, y, z, PDSounds.FURY_SPELL_0.get(), 1.0f);
        spawnField(level, PDEntities.FURY_SPELL_ENTITY.get(), x, y, z);
        furyPulse(level, x, y, z);
        for (int t : FURY_PULSE_TICKS) {
            schedule(level, t, () -> furyPulse(level, x, y, z));
        }
    }

    /**
     * 狂暴增益脉冲：附魔/龙息/狂暴/末地烛粒子 + 8*8 区域内玩家获得 60 tick 狂暴增益
     */
    private static void furyPulse(ServerLevel level, double x, double y, double z) {
        level.sendParticles(ParticleTypes.ENCHANT, x, y + 3, z, 100, 2.5, 1, 2.5, 0.02);
        level.sendParticles(ParticleTypes.DRAGON_BREATH, x, y + 0.8, z, 100, 2.5, 0.3, 2.5, 0.01);
        level.sendParticles(PDParticles.FURY_SPELL_PARTICLE.holder().get(), x, y + 2, z, 12, 2.5, 1, 2.5, 0.02);
        level.sendParticles(ParticleTypes.END_ROD, x, y + 2, z, 6, 2.5, 1, 2.5, 0.02);

        Vec3 center = new Vec3(x, y, z);
        List<Player> players = level.getEntitiesOfClass(Player.class,
                new AABB(center, center).inflate(8 / 2d), e -> true);
        for (Player player : players) {
            player.addEffect(new MobEffectInstance(PDEffects.FURY_SPELL_BUFF, 60, 0));
        }
    }

    /**
     * 冰冻法术命中：雪花爆发 + 5 波冻结（7*7 区域内实体冻结 140 tick 并获得冰冻减益）
     * （与原版 IceSpellPr0/Pr1Procedure 一致）
     *
     * @param level 服务端世界
     * @param x     命中点 X
     * @param y     命中点 Y
     * @param z     命中点 Z
     */
    public static void ice(ServerLevel level, double x, double y, double z) {
        playSound(level, x, y, z, SoundEvents.SPLASH_POTION_BREAK, 1.2f);
        playSound(level, x, y, z, PDSounds.ICE_SPELL.get(), 1.0f);
        level.sendParticles(ParticleTypes.SNOWFLAKE, x + 0.5, y + 1.5, z + 0.5, 240, 2.5, 1.5, 2.5, 0.05);
        level.sendParticles(PDParticles.SNOWFLAKE_0_PARTICLE.holder().get(),
                x + 0.5, y + 2, z + 0.5, 128, 2.5, 1.5, 2.5, 0.1);
        level.sendParticles(PDParticles.SNOWFLAKE_1_PARTICLE.holder().get(),
                x + 0.5, y + 2, z + 0.5, 128, 2.5, 1.5, 2.5, 0.1);
        iceFreezeWave(level, x, y, z);
        for (int t : ICE_WAVE_TICKS) {
            schedule(level, t, () -> iceFreezeWave(level, x, y, z));
        }
    }

    /**
     * 冻结波：雪花粒子 + 7*7 区域内实体 setTicksFrozen(140)，生物额外获得 40 tick 冰冻减益
     */
    private static void iceFreezeWave(ServerLevel level, double x, double y, double z) {
        level.sendParticles(ParticleTypes.SNOWFLAKE, x + 0.5, y + 1.5, z + 0.5, 48, 2.5, 1.5, 2.5, 0.1);
        level.sendParticles(PDParticles.SNOWFLAKE_0_PARTICLE.holder().get(),
                x + 0.5, y + 2, z + 0.5, 32, 2.5, 1.5, 2.5, 0.1);
        level.sendParticles(PDParticles.SNOWFLAKE_1_PARTICLE.holder().get(),
                x + 0.5, y + 2, z + 0.5, 32, 2.5, 1.5, 2.5, 0.1);

        Vec3 center = new Vec3(x, y, z);
        List<Entity> targets = level.getEntitiesOfClass(Entity.class,
                new AABB(center, center).inflate(7 / 2d), e -> true);
        for (Entity target : targets) {
            target.setTicksFrozen(140);
            if (target instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(PDEffects.ICE_SPELL_BUFF, 40, 0));
            }
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 在命中点上方 1 格生成立场实体（随机朝向，与原版一致）
     */
    private static void spawnField(ServerLevel level, EntityType<? extends Entity> type,
                                   double x, double y, double z) {
        Entity field = type.spawn(level, BlockPos.containing(x, y + 1, z), MobSpawnType.MOB_SUMMONED);
        if (field != null) {
            field.setYRot(level.getRandom().nextFloat() * 360F);
        }
    }

    /**
     * 服务端广播播放音效（NEUTRAL 声道，音调 1.0）
     */
    private static void playSound(ServerLevel level, double x, double y, double z,
                                  SoundEvent sound, float volume) {
        level.playSound(null, BlockPos.containing(x, y, z), sound, SoundSource.NEUTRAL, volume, 1.0f);
    }

    /**
     * 延迟指定 tick 后在服务端主线程执行任务（等价于原版 MCreator 的 queueServerWork）
     * <p>
     * 注意：不可用 {@code server.tell(new TickTask(...))} —— vanilla 该队列在服务器
     * 空闲时立即执行任务而非等到目标 tick（会把全部法术时序压缩到同一 tick，
     * 已由客户端实测取证），改用 {@link ServerScheduler} 真定时派发。
     *
     * @param level 服务端世界（保留参数以维持调用点签名）
     * @param delay 延迟 tick 数
     * @param task  要执行的任务
     */
    private static void schedule(ServerLevel level, int delay, Runnable task) {
        ServerScheduler.schedule(delay, task);
    }
}
