package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.client.particle.CrystalSnowflakeParticle;
import com.pasterdream.pasterdreammod.client.particle.DreamSporeParticle;
import com.pasterdream.pasterdreammod.client.particle.StardustParticle;
import com.pasterdream.pasterdreammod.registry.PDBiomes;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * 染梦世界动态环境粒子渲染器 (Dyedream Environment Renderer)
 * <p>
 * 作为客户端独立模块，与 {@link PDClientEvents} 并行运行，专用于在染梦维度
 * 生成新增的动态环境粒子系统（TASK 4）。
 * <p>
 * 功能概述：
 * <ul>
 *   <li>监听 {@link ClientTickEvent.Post}，在客户端 tick 后触发</li>
 *   <li>仅在染梦维度（dyedream_world）中生效</li>
 *   <li>根据玩家所在生物群系 ID，生成对应的环境粒子效果</li>
 *   <li>与 {@link PDClientEvents} 的粒子系统互补互不冲突</li>
 * </ul>
 * <p>
 * 群系-粒子映射关系：
 * <pre>
 * {@code
 * biome_dyedream_0 (梦幻平原)         → DREAM_SPORE     概率 0.003
 * biome_dyedream_1 (温暖森林)         → STARDUST         概率 0.004
 * biome_dyedream_2 (寒冷染梦)         → CRYSTAL_SNOWFLAKE 概率 0.005
 * biome_dyedream_3 (暖色海岸/海洋)    → STARDUST          概率 0.003
 * biome_dyedream_deep_ocean (晶莹深海) → STARDUST         概率 0.004
 * biome_dyedream_mushroom_plains (蘑菇平原) → DREAM_SPORE 概率 0.004 (绿色/蓝色变体)
 * biome_dyedream_shore (染梦海岸)     → DREAM_SPORE + STARDUST  各 0.003
 * biome_dyedream_river (染梦河流)     → STARDUST                  概率 0.005
 * biome_dyedream_dense_forest (染梦密林) → DREAM_SPORE + STARDUST 各 0.002
 * }
 * </pre>
 * <p>
 * 设计要点：
 * <ul>
 *   <li>使用 {@link ResourceKey#create} 引用群系，避免直接硬编码字符串比较</li>
 *   <li>所有粒子生成使用 {@link Minecraft#level} 的 {@code addParticle()} 方法，
 *       确保服务端兼容和安全</li>
 *   <li>粒子生成频率通过概率控制，避免性能问题</li>
 *   <li>暂停时跳过粒子生成，防止暂停解冻瞬间爆出大量粒子</li>
 * </ul>
 *
 * @see PDClientEvents 原有的环境粒子系统（本类为其补充，而非替代）
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public class DyedreamEnvironmentRenderer {

    // ======================== 漂移常量 ========================

    /** 水平漂移速度系数 */
    private static final double DRIFT_SPEED = 0.0008;
    /** 水平漂移半径（方块） */
    private static final double DRIFT_RADIUS = 6.0;
    /** 粒子生成中心前移距离（沿玩家视线方向，让粒子更多出现在前方视野内） */
    private static final double FORWARD_OFFSET = 6.0;
    /** 粒子水平生成范围（最小） */
    private static final double SPAWN_RADIUS_MIN = 2.0;
    /** 粒子水平生成范围（最大） */
    private static final double SPAWN_RADIUS_MAX = 12.0;

    // ======================== 事件处理 ========================

    /**
     * 客户端 Tick 后处理
     * <p>
     * 在染梦维度中根据当前生物群系生成对应的动态环境粒子。
     * 暂停时不执行，避免解冻瞬间粒子爆炸。
     *
     * @param event 客户端 Tick 事件（Post 阶段）
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        if (mc.isPaused()) return;

        // 仅在染梦维度中生效
        if (!PDDimensions.isDyedreamWorld(mc.player.level())) return;

        var biomeKey = mc.level.getBiome(mc.player.blockPosition()).unwrapKey();
        if (biomeKey.isEmpty()) return;

        ResourceKey<Biome> currentBiome = biomeKey.get();

        // ===== 按群系分发粒子效果 =====
        if (PDBiomes.DYEDREAM_PLAINS.equals(currentBiome)) {
            spawnDreamSpore(mc, 0.003f);
        } else if (PDBiomes.DYEDREAM_FOREST.equals(currentBiome)) {
            spawnStardust(mc, 0.004f);
        } else if (PDBiomes.DYEDREAM_FROZEN_TUNDRA.equals(currentBiome)) {
            spawnCrystalSnowflake(mc, 0.005f);
        } else if (PDBiomes.DYEDREAM_COLD_OCEAN.equals(currentBiome)) {
            // 暖色海岸/海洋：星尘（密度对齐原极光粒子）
            spawnStardust(mc, 0.003f);
        } else if (PDBiomes.DYEDREAM_DEEP_OCEAN.equals(currentBiome)) {
            // 晶莹深海：星尘（密度对齐原极光粒子）
            spawnStardust(mc, 0.004f);
        } else if (PDBiomes.DYEDREAM_MUSHROOM_PLAINS.equals(currentBiome)) {
            // 蘑菇平原：梦幻孢子（绿色/蓝色变体），用自定义颜色速度
            spawnMushroomSporeVariant(mc, 0.004f);
        } else if (PDBiomes.DYEDREAM_SHORE.equals(currentBiome)) {
            // 海岸：梦幻孢子 + 星尘组合
            spawnDreamSpore(mc, 0.003f);
            spawnStardust(mc, 0.003f);
        } else if (PDBiomes.DYEDREAM_RIVER.equals(currentBiome)) {
            spawnStardust(mc, 0.005f);
        } else if (PDBiomes.DYEDREAM_DENSE_FOREST.equals(currentBiome)) {
            // 密林：梦幻孢子 + 星尘组合
            spawnDreamSpore(mc, 0.002f);
            spawnStardust(mc, 0.002f);
        } else {
            // 未知群系：生成通用星尘
            spawnStardust(mc, 0.002f);
        }
    }

    // ======================== 粒子生成方法 ========================

    /**
     * 生成梦幻孢子粒子（默认粉紫色系）
     * <p>
     * 从玩家前方偏移中心周围 2~12 方块范围内、头顶 3~8 格高度生成，
     * 使用玩家视线方向偏移生成中心，使粒子更多出现在前方视野中。
     * 速度分量大幅降低以延长粒子在视野中的停留时间。
     *
     * @param mc         Minecraft 客户端实例
     * @param probability 每 tick 的生成概率（0.0 ~ 1.0）
     */
    private static void spawnDreamSpore(Minecraft mc, float probability) {
        var random = mc.player.getRandom();
        if (random.nextFloat() >= probability) return;

        long gameTime = mc.level.getGameTime();
        double driftX = Math.sin(gameTime * DRIFT_SPEED) * DRIFT_RADIUS;
        double driftZ = Math.cos(gameTime * DRIFT_SPEED * 0.7 + 1.5) * DRIFT_RADIUS;

        SimpleParticleType type = (SimpleParticleType) PDParticles.DREAM_SPORE.particleType();

        // 计算沿玩家视线方向的偏移生成中心
        double[] forward = getForwardSpawnCenter(mc);
        double cx = forward[0] + driftX;
        double cy = forward[1];
        double cz = forward[2] + driftZ;

        int count = 1 + random.nextInt(2);
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = SPAWN_RADIUS_MIN + random.nextDouble() * (SPAWN_RADIUS_MAX - SPAWN_RADIUS_MIN);

            mc.level.addParticle(
                    type,
                    cx + Math.cos(angle) * dist,
                    cy + 3.0 + random.nextDouble() * 5.0,
                    cz + Math.sin(angle) * dist,
                    (random.nextDouble() - 0.5) * 0.002,
                    -0.002 - random.nextDouble() * 0.004,
                    (random.nextDouble() - 0.5) * 0.002
            );
        }
    }

    /**
     * 生成蘑菇平原变体孢子粒子（绿色/蓝色系）
     * <p>
     * 从地面附近向前偏移区域内生成，速度分量减半以延长显示时长。
     *
     * @param mc         Minecraft 客户端实例
     * @param probability 每 tick 的生成概率
     */
    private static void spawnMushroomSporeVariant(Minecraft mc, float probability) {
        var random = mc.player.getRandom();
        if (random.nextFloat() >= probability) return;

        long gameTime = mc.level.getGameTime();
        double driftX = Math.sin(gameTime * DRIFT_SPEED * 0.7) * DRIFT_RADIUS;
        double driftZ = Math.cos(gameTime * DRIFT_SPEED * 0.9 + 1.2) * DRIFT_RADIUS;

        SimpleParticleType type = (SimpleParticleType) PDParticles.DREAM_SPORE.particleType();

        // 计算沿玩家视线方向的偏移生成中心（贴近地面）
        double[] forward = getForwardSpawnCenter(mc);
        double cx = forward[0] + driftX;
        double cz = forward[2] + driftZ;
        double playerFloorY = mc.player.getY() - 2.0;

        int count = 1 + random.nextInt(2);
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = SPAWN_RADIUS_MIN + random.nextDouble() * (SPAWN_RADIUS_MAX - SPAWN_RADIUS_MIN);

            mc.level.addParticle(
                    type,
                    cx + Math.cos(angle) * dist,
                    playerFloorY + 0.5 + random.nextDouble() * 4.0,
                    cz + Math.sin(angle) * dist,
                    (random.nextDouble() - 0.5) * 0.002,
                    -0.003 - random.nextDouble() * 0.004,
                    (random.nextDouble() - 0.5) * 0.002
            );
        }
    }

    /**
     * 生成水晶雪花粒子
     * <p>
     * 从玩家前方上方 5~12 格高度生成，缓慢飘落，
     * 带有横向微风扰动，落地后自动消失。
     *
     * @param mc         Minecraft 客户端实例
     * @param probability 每 tick 的生成概率
     */
    private static void spawnCrystalSnowflake(Minecraft mc, float probability) {
        var random = mc.player.getRandom();
        if (random.nextFloat() >= probability) return;

        long gameTime = mc.level.getGameTime();
        double driftX = Math.sin(gameTime * DRIFT_SPEED * 0.8) * DRIFT_RADIUS;
        double driftZ = Math.cos(gameTime * DRIFT_SPEED * 1.1 + 1.0) * DRIFT_RADIUS;

        SimpleParticleType type = (SimpleParticleType) PDParticles.CRYSTAL_SNOWFLAKE.particleType();

        // 计算沿玩家视线方向的偏移生成中心
        double[] forward = getForwardSpawnCenter(mc);
        double cx = forward[0] + driftX;
        double cz = forward[2] + driftZ;

        int count = 1 + random.nextInt(2);
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = SPAWN_RADIUS_MIN + random.nextDouble() * (SPAWN_RADIUS_MAX - SPAWN_RADIUS_MIN);

            mc.level.addParticle(
                    type,
                    cx + Math.cos(angle) * dist,
                    forward[1] + 5.0 + random.nextDouble() * 7.0,
                    cz + Math.sin(angle) * dist,
                    (random.nextDouble() - 0.5) * 0.002,
                    -0.005 - random.nextDouble() * 0.008,
                    (random.nextDouble() - 0.5) * 0.002
            );
        }
    }

    /**
     * 生成星尘粒子
     * <p>
     * 从玩家前方偏移区域生成，速度分量减半，延长显示时长。
     *
     * @param mc         Minecraft 客户端实例
     * @param probability 每 tick 的生成概率
     */
    private static void spawnStardust(Minecraft mc, float probability) {
        var random = mc.player.getRandom();
        if (random.nextFloat() >= probability) return;

        long gameTime = mc.level.getGameTime();
        double driftX = Math.sin(gameTime * DRIFT_SPEED * 1.2) * DRIFT_RADIUS;
        double driftZ = Math.cos(gameTime * DRIFT_SPEED * 0.9 + 2.0) * DRIFT_RADIUS;

        SimpleParticleType type = (SimpleParticleType) PDParticles.STARDUST.particleType();

        // 计算沿玩家视线方向的偏移生成中心
        double[] forward = getForwardSpawnCenter(mc);
        double cx = forward[0] + driftX;
        double cy = forward[1];
        double cz = forward[2] + driftZ;

        int count = 1 + random.nextInt(2);
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = SPAWN_RADIUS_MIN + random.nextDouble() * (SPAWN_RADIUS_MAX - SPAWN_RADIUS_MIN);

            mc.level.addParticle(
                    type,
                    cx + Math.cos(angle) * dist,
                    cy + 1.0 + random.nextDouble() * 7.0,
                    cz + Math.sin(angle) * dist,
                    (random.nextDouble() - 0.5) * 0.002,
                    0.0,
                    (random.nextDouble() - 0.5) * 0.002
            );
        }
    }

    /**
     * 计算沿玩家视线方向的粒子生成中心偏移坐标
     * <p>
     * 使用玩家的偏航角（水平视线方向）将生成中心前移，
     * 使粒子更多出现在前方视野内，避免视角转动时粒子飞向身后的现象。
     *
     * @param mc Minecraft 客户端实例
     * @return [x, y, z] 生成中心坐标
     */
    private static double[] getForwardSpawnCenter(Minecraft mc) {
        float yaw = mc.player.getYRot();
        float pitch = mc.player.getXRot();
        double radYaw = Math.toRadians(yaw);
        double radPitch = Math.toRadians(pitch);
        // 水平方向分量（忽略俯仰的垂直影响，保持粒子在玩家水平附近）
        double forwardX = -Math.sin(radYaw) * FORWARD_OFFSET;
        double forwardZ = Math.cos(radYaw) * FORWARD_OFFSET;
        // 垂直方向：轻微俯仰补偿，但限制在合理范围内
        double forwardY = -Math.sin(radPitch) * FORWARD_OFFSET * 0.3;
        return new double[]{
                mc.player.getX() + forwardX,
                mc.player.getY() + forwardY,
                mc.player.getZ() + forwardZ
        };
    }

    // 工具类防止实例化
    private DyedreamEnvironmentRenderer() {
        throw new UnsupportedOperationException("事件订阅器工具类不可实例化");
    }
}
