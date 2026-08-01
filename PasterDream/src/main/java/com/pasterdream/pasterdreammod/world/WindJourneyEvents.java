package com.pasterdream.pasterdreammod.world;

import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.registry.PDGameRules;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import top.theillusivec4.curios.api.CuriosApi;

/**
 * 风之旅途：日更风向、面朝顺/逆风、进维提示与主题曲。
 * <p>
 * 对应原版 {@code WindDirectionPr0/1/2}、{@code WindJourneyWorldPr0}、
 * {@code PlayerTotalTickUpdateProcedure} 中的风向分支。
 */
public final class WindJourneyEvents {

    private static final String[] WIND_DIR_NAMES = {
            "北方", "东北方", "东方", "东南方", "南方", "西南方", "西方", "西北方"
    };

    /** 上次已处理的日序号（dayTime/24000），避免 dayTime==0 或卡在日界时每 tick 重roll */
    private static long lastWindDayIndex = Long.MIN_VALUE;

    private WindJourneyEvents() {
    }

    /**
     * 跨维度：进入风维提示 + 主题曲；离开风维清掉顺/逆风，避免 permanent modifier 残留。
     * <p>
     * 不在离开时清 force NBT（纸飞机仍可能装备，回维应保留 amp）。
     *
     * @param event 跨维度事件
     */
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (event.getFrom().equals(PDDimensions.WIND_JOURNEY_WORLD_LEVEL_KEY)) {
            player.removeEffect(PDEffects.DEADWIND_BUFF.holder());
            player.removeEffect(PDEffects.TAILWIND_BUFF.holder());
        }
        if (!event.getTo().equals(PDDimensions.WIND_JOURNEY_WORLD_LEVEL_KEY)) {
            return;
        }
        player.displayClientMessage(Component.translatable("message.pasterdream.wind_journey.not_finished"), false);
    }

    /**
     * 世界 tick：风维日更风向、广播、音效与羽毛粒子（Pr0）。
     *
     * @param event 维度 tick 后
     */
    public static void onLevelTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!PDDimensions.isWindJourneyWorld(serverLevel)) {
            return;
        }

        long dayTime = serverLevel.getDayTime();
        // 原版用 dayTime % 24000 == 0，但 VERIFY 快进 / dayTime 停在 0 时会每 tick 命中。
        // 改为「跨过新的一天序号」才 roll 一次（仍含 0 点日界）。
        long dayIndex = Math.floorDiv(dayTime, 24000L);
        if (dayIndex != lastWindDayIndex && dayTime % 24000L == 0L) {
            lastWindDayIndex = dayIndex;
            int dir = Mth.nextInt(RandomSource.create(), 0, 7);
            GameRules.IntegerValue rule = serverLevel.getGameRules().getRule(PDGameRules.WIND_DIRECTION);
            rule.set(dir, serverLevel.getServer());

            for (ServerPlayer player : serverLevel.players()) {
                serverLevel.playSound(
                        null,
                        player.blockPosition(),
                        PDSounds.WIND_CHIME.get(),
                        SoundSource.WEATHER,
                        1.0F,
                        1.0F);
                serverLevel.sendParticles(
                        (SimpleParticleType) PDParticles.FEATHER_WHITE_PARTICLE.holder().get(),
                        player.getX(),
                        player.getY() + 2.0D,
                        player.getZ(),
                        48,
                        3.0D,
                        3.0D,
                        3.0D,
                        0.05D);
            }

            final int broadcastDir = dir;
            ServerScheduler.schedule(2, () -> {
                if (!serverLevel.getServer().isRunning()) {
                    return;
                }
                String name = WIND_DIR_NAMES[Mth.clamp(broadcastDir, 0, 7)];
                Component msg = Component.translatable("message.pasterdream.wind_journey.wind_blows", name);
                for (ServerPlayer player : serverLevel.players()) {
                    player.displayClientMessage(msg, false);
                }
            });

            ServerScheduler.schedule(79, () -> {
                if (!serverLevel.getServer().isRunning()) {
                    return;
                }
                for (ServerPlayer player : serverLevel.players()) {
                    serverLevel.playSound(
                            null,
                            player.blockPosition(),
                            PDSounds.BREEZE_WIND.get(),
                            SoundSource.WEATHER,
                            1.0F,
                            1.0F);
                }
            });
        }

        if (dayTime == 1L || dayTime == 5L) {
            for (ServerPlayer player : serverLevel.players()) {
                serverLevel.sendParticles(
                        (SimpleParticleType) PDParticles.FEATHER_WHITE_PARTICLE.holder().get(),
                        player.getX(),
                        player.getY() + 2.0D,
                        player.getZ(),
                        48,
                        3.0D,
                        3.0D,
                        3.0D,
                        0.05D);
            }
        }
    }

    /**
     * 风维虚空保护：玩家掉出世界底部时传送回主世界上空。
     */
    private static void voidFallbackToOverworld(ServerPlayer player) {
        ServerLevel overworld = player.server.getLevel(Level.OVERWORLD);
        if (overworld == null) return;
        double tx, tz;
        BlockPos respawn = player.getRespawnPosition();
        if (player.getRespawnDimension().equals(Level.OVERWORLD) && respawn != null) {
            tx = respawn.getX();
            tz = respawn.getZ();
        } else {
            BlockPos spawn = overworld.getSharedSpawnPos();
            tx = spawn.getX();
            tz = spawn.getZ();
        }
        player.teleportTo(overworld, tx, 304, tz, player.getYRot(), player.getXRot());
    }

    /**
     * 玩家 tick：风维虚空保护 + 无防风时按朝向施加顺风 / 逆风（Pr1 + Pr2）。
     *
     * @param event 玩家 tick 后
     */
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide() || !(player instanceof ServerPlayer sp)) {
            return;
        }
        if (!PDDimensions.isWindJourneyWorld(sp.level())) {
            return;
        }
        // 虚空保护：掉到世界底部以下 → 传回主世界上空
        if (sp.getY() < -10) {
            voidFallbackToOverworld(sp);
            return;
        }
        int interval = Math.max(1, PDCommonConfig.PLAYER_TOTAL_TICK_UPDATE.get());
        if (sp.tickCount % interval != 0) {
            return;
        }
        if (sp.hasEffect(PDEffects.WINDPROOF_BUFF.holder())) {
            return;
        }

        int windDir = sp.level().getGameRules().getInt(PDGameRules.WIND_DIRECTION);
        float yRot = sp.getYRot();
        int tailwindAmp = (int) sp.getPersistentData().getDouble("player_tailwind_force");
        int deadwindAmp = (int) sp.getPersistentData().getDouble("player_deadwind_force");

        // Pr1：面朝风向 → 顺风
        if (facingWindCone(yRot, windDir)) {
            sp.addEffect(new MobEffectInstance(PDEffects.TAILWIND_BUFF.holder(), 20, tailwindAmp, false, false));
        }

        // Pr2：背风锥；戴破风旗帜则逆当顺，否则逆风
        boolean hasFlag = CuriosApi.getCuriosInventory(sp)
                .map(handler -> handler.findFirstCurio(PDItems.WIND_KNIGHT_FLAG.get()).isPresent())
                .orElse(false);
        int opposite = (windDir + 4) % 8;
        if (facingWindCone(yRot, opposite)) {
            if (hasFlag) {
                sp.addEffect(new MobEffectInstance(PDEffects.TAILWIND_BUFF.holder(), 20, tailwindAmp, false, false));
            } else {
                sp.addEffect(new MobEffectInstance(PDEffects.DEADWIND_BUFF.holder(), 20, deadwindAmp, false, false));
            }
        }
    }

    /**
     * 原版各风向对应的 YRot 锥（与 WindDirectionPr1 表一致）。
     *
     * @param yRot 玩家偏航
     * @param dir  风向 0–7
     * @return 是否面朝该风向锥
     */
    private static boolean facingWindCone(float yRot, int dir) {
        // 归一到 [-180, 180)
        while (yRot >= 180.0F) {
            yRot -= 360.0F;
        }
        while (yRot < -180.0F) {
            yRot += 360.0F;
        }
        return switch (dir & 7) {
            case 0 -> yRot <= 35.0F && yRot >= -35.0F;
            case 1 -> yRot <= 70.0F && yRot >= 10.0F;
            case 2 -> yRot <= 125.0F && yRot >= 55.0F;
            case 3 -> yRot <= 170.0F && yRot >= 100.0F;
            case 4 -> (yRot <= 180.0F && yRot >= 145.0F) || (yRot <= -145.0F && yRot >= -180.0F);
            case 5 -> yRot <= -100.0F && yRot >= -170.0F;
            case 6 -> yRot <= -55.0F && yRot >= -125.0F;
            case 7 -> yRot <= -10.0F && yRot >= -80.0F;
            default -> false;
        };
    }
}
