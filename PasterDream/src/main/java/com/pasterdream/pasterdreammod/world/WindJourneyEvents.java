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
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
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

    /** 风维缓降保护：玩家高于脚下地表该格数时视为高空状态，开始补刷缓降 */
    private static final double SLOW_FALLING_TRIGGER_ABOVE_GROUND = 8.0D;

    /** 风维缓降保护：补刷时长（tick），接近耗尽时才重挂，避免每 tick 刷同步包 */
    private static final int SLOW_FALLING_REFRESH_TICKS = 60;

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
     * 传回主世界安全落点：优先玩家重生点（重生维为主世界时），否则世界出生点，
     * 用主世界 heightmap 取地表上方 Y。
     * <p>
     * 替代原版固定 {@code y=304} 的高空传送——原版返回后需坠落约 270 格，
     * 无缓降/鞘翅必摔死（原版缺陷，移植版改为 heightmap 安全落点，属有意差异）。
     *
     * @param player 触发返回的玩家
     */
    public static void returnToOverworldSafe(ServerPlayer player) {
        ServerLevel overworld = player.server.getLevel(Level.OVERWORLD);
        if (overworld == null) {
            return;
        }
        BlockPos target;
        BlockPos respawn = player.getRespawnPosition();
        if (player.getRespawnDimension().equals(Level.OVERWORLD) && respawn != null) {
            target = respawn;
        } else {
            target = overworld.getSharedSpawnPos();
        }
        int surfaceY = overworld.getHeight(Heightmap.Types.MOTION_BLOCKING, target.getX(), target.getZ());
        // 项目惯例（对齐 PDCustomSpawnEvents）：getHeight 取地表后 +1 作为落脚 Y，避免嵌进地表
        player.teleportTo(overworld, target.getX() + 0.5D,
                Math.max(surfaceY + 1, overworld.getMinBuildHeight() + 1), target.getZ() + 0.5D,
                player.getYRot(), player.getXRot());
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
        // 虚空保护：掉到世界底部以下 → 传回主世界安全落点
        if (sp.getY() < -10) {
            returnToOverworldSafe(sp);
            return;
        }
        // 缓降保护：高于脚下地表 8 格以上时补刷缓降，覆盖进维 254 高空降落与岛间失足
        //（原版无此保护，进维/失足摔死，属有意差异；接近耗尽才重挂，避免每 tick 刷同步包）
        if (!sp.isCreative() && !sp.isSpectator()) {
            MobEffectInstance slowFalling = sp.getEffect(MobEffects.SLOW_FALLING);
            if (slowFalling == null || slowFalling.getDuration() < 20) {
                int groundY = sp.level().getHeight(Heightmap.Types.MOTION_BLOCKING,
                        sp.getBlockX(), sp.getBlockZ());
                if (sp.getY() - groundY > SLOW_FALLING_TRIGGER_ABOVE_GROUND) {
                    sp.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,
                            SLOW_FALLING_REFRESH_TICKS, 0, true, false));
                }
            }
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
