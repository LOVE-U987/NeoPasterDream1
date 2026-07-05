package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.client.particle.AuroraGlowParticle;
import com.pasterdream.pasterdreammod.client.particle.CrystalSnowflakeParticle;
import com.pasterdream.pasterdreammod.client.particle.DreamSporeParticle;
import com.pasterdream.pasterdreammod.client.particle.StardustParticle;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
 * biome_dyedream_3 (暖色海岸/海洋)    → AURORA_GLOW      概率 0.003
 * biome_dyedream_deep_ocean (晶莹深海) → AURORA_GLOW     概率 0.004
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

    // ======================== 群系 ResourceKey ========================

    private static final ResourceKey<Biome> BIOME_DYEDREAM_0 = ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_0")
    );
    private static final ResourceKey<Biome> BIOME_DYEDREAM_1 = ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_1")
    );
    private static final ResourceKey<Biome> BIOME_DYEDREAM_2 = ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_2")
    );
    private static final ResourceKey<Biome> BIOME_DYEDREAM_3 = ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_3")
    );
    private static final ResourceKey<Biome> BIOME_DYEDREAM_DEEP_OCEAN = ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_deep_ocean")
    );
    private static final ResourceKey<Biome> BIOME_DYEDREAM_MUSHROOM_PLAINS = ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_mushroom_plains")
    );
    private static final ResourceKey<Biome> BIOME_DYEDREAM_SHORE = ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_shore")
    );
    private static final ResourceKey<Biome> BIOME_DYEDREAM_RIVER = ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_river")
    );
    private static final ResourceKey<Biome> BIOME_DYEDREAM_DENSE_FOREST = ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_dense_forest")
    );

    // ======================== 漂移常量 ========================

    /** 水平漂移速度系数 */
    private static final double DRIFT_SPEED = 0.0008;
    /** 水平漂移半径（方块） */
    private static final double DRIFT_RADIUS = 6.0;

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
        if (BIOME_DYEDREAM_0.equals(currentBiome)) {
            spawnDreamSpore(mc, 0.003f);
        } else if (BIOME_DYEDREAM_1.equals(currentBiome)) {
            spawnStardust(mc, 0.004f);
        } else if (BIOME_DYEDREAM_2.equals(currentBiome)) {
            spawnCrystalSnowflake(mc, 0.005f);
        } else if (BIOME_DYEDREAM_3.equals(currentBiome)) {
            spawnAuroraGlow(mc, 0.003f);
        } else if (BIOME_DYEDREAM_DEEP_OCEAN.equals(currentBiome)) {
            spawnAuroraGlow(mc, 0.004f);
        } else if (BIOME_DYEDREAM_MUSHROOM_PLAINS.equals(currentBiome)) {
            // 蘑菇平原：梦幻孢子（绿色/蓝色变体），用自定义颜色速度
            spawnMushroomSporeVariant(mc, 0.004f);
        } else if (BIOME_DYEDREAM_SHORE.equals(currentBiome)) {
            // 海岸：梦幻孢子 + 星尘组合
            spawnDreamSpore(mc, 0.003f);
            spawnStardust(mc, 0.003f);
        } else if (BIOME_DYEDREAM_RIVER.equals(currentBiome)) {
            spawnStardust(mc, 0.005f);
        } else if (BIOME_DYEDREAM_DENSE_FOREST.equals(currentBiome)) {
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
     * 从玩家周围 4~16 方块范围内、头顶 3~8 格高度生成，
     * 缓慢上浮并带有周期性水平漂移。
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

        int count = 1 + random.nextInt(2);
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = 4.0 + random.nextDouble() * 12.0;

            mc.level.addParticle(
                    type,
                    mc.player.getX() + driftX + Math.cos(angle) * dist,
                    mc.player.getY() + 3.0 + random.nextDouble() * 5.0,
                    mc.player.getZ() + driftZ + Math.sin(angle) * dist,
                    (random.nextDouble() - 0.5) * 0.004,
                    -0.003 - random.nextDouble() * 0.008,
                    (random.nextDouble() - 0.5) * 0.004
            );
        }
    }

    /**
     * 生成蘑菇平原变体孢子粒子（绿色/蓝色系）
     * <p>
     * 从地面附近向上生成，使用与默认梦幻孢子相同的粒子类型，
     * 但通过不同的运动参数模拟绿色/蓝色菌丝孢子的飘散效果。
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

        double playerFloorY = mc.player.getY() - 2.0;

        int count = 1 + random.nextInt(2);
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = 2.0 + random.nextDouble() * 14.0;

            mc.level.addParticle(
                    type,
                    mc.player.getX() + driftX + Math.cos(angle) * dist,
                    playerFloorY + 0.5 + random.nextDouble() * 4.0,
                    mc.player.getZ() + driftZ + Math.sin(angle) * dist,
                    (random.nextDouble() - 0.5) * 0.003,
                    -0.005 - random.nextDouble() * 0.008,
                    (random.nextDouble() - 0.5) * 0.003
            );
        }
    }

    /**
     * 生成水晶雪花粒子
     * <p>
     * 从玩家上方 5~12 格高度生成，缓慢飘落，
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

        int count = 1 + random.nextInt(2);
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = 2.0 + random.nextDouble() * 16.0;

            mc.level.addParticle(
                    type,
                    mc.player.getX() + driftX + Math.cos(angle) * dist,
                    mc.player.getY() + 5.0 + random.nextDouble() * 7.0,
                    mc.player.getZ() + driftZ + Math.sin(angle) * dist,
                    (random.nextDouble() - 0.5) * 0.003,
                    -0.01 - random.nextDouble() * 0.015,
                    (random.nextDouble() - 0.5) * 0.003
            );
        }
    }

    /**
     * 生成极光粒子
     * <p>
     * 从玩家周围较高位置生成，水平缓慢漂移，
     * 颜色在青/蓝/紫之间变化。
     *
     * @param mc         Minecraft 客户端实例
     * @param probability 每 tick 的生成概率
     */
    private static void spawnAuroraGlow(Minecraft mc, float probability) {
        var random = mc.player.getRandom();
        if (random.nextFloat() >= probability) return;

        long gameTime = mc.level.getGameTime();
        double driftX = Math.sin(gameTime * DRIFT_SPEED * 0.5) * DRIFT_RADIUS;
        double driftZ = Math.cos(gameTime * DRIFT_SPEED * 1.2 + 0.8) * DRIFT_RADIUS;

        SimpleParticleType type = (SimpleParticleType) PDParticles.AURORA_GLOW.particleType();

        int count = 1 + random.nextInt(2);
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = 3.0 + random.nextDouble() * 12.0;

            mc.level.addParticle(
                    type,
                    mc.player.getX() + driftX + Math.cos(angle) * dist,
                    mc.player.getY() + 6.0 + random.nextDouble() * 8.0,
                    mc.player.getZ() + driftZ + Math.sin(angle) * dist,
                    (random.nextDouble() - 0.5) * 0.002,
                    (random.nextDouble() - 0.5) * 0.002,
                    (random.nextDouble() - 0.5) * 0.002
            );
        }
    }

    /**
     * 生成星尘粒子
     * <p>
     * 从玩家周围大范围轻微上浮飘散，
     * 尺寸极小但发光明显，产生星芒闪烁效果。
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

        int count = 1 + random.nextInt(2);
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = 2.0 + random.nextDouble() * 16.0;

            mc.level.addParticle(
                    type,
                    mc.player.getX() + driftX + Math.cos(angle) * dist,
                    mc.player.getY() + 1.0 + random.nextDouble() * 7.0,
                    mc.player.getZ() + driftZ + Math.sin(angle) * dist,
                    (random.nextDouble() - 0.5) * 0.004,
                    0.0,
                    (random.nextDouble() - 0.5) * 0.004
            );
        }
    }

    // 工具类防止实例化
    private DyedreamEnvironmentRenderer() {
        throw new UnsupportedOperationException("事件订阅器工具类不可实例化");
    }
}
