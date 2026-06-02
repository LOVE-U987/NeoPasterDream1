package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.client.audio.ModMusicManager;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * 客户端事件处理类
 * <p>
 * 处理客户端专属的周期性事件，包括染梦维度的群系专属环境粒子和树冠落叶系统。
 * 每个生物群系拥有独特的粒子效果，同时染梦树叶和樱花树周围会飘落叶片。
 * <p>
 * 同时管理 {@link ModMusicManager} 的 tick 驱动。
 * 通过 {@link EventBusSubscriber} 自动注册到游戏事件总线，仅在客户端生效。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class PDClientEvents {

    private static final ResourceKey<Biome> BIOME_DYEDREAM_0 = ResourceKey.create(
            net.minecraft.core.registries.Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_0")
    );
    private static final ResourceKey<Biome> BIOME_DYEDREAM_1 = ResourceKey.create(
            net.minecraft.core.registries.Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_1")
    );
    private static final ResourceKey<Biome> BIOME_DYEDREAM_2 = ResourceKey.create(
            net.minecraft.core.registries.Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_2")
    );
    private static final ResourceKey<Biome> BIOME_DYEDREAM_3 = ResourceKey.create(
            net.minecraft.core.registries.Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_3")
    );

    private static final ResourceLocation DYEDREAM_LEAVES_ID = ResourceLocation.fromNamespaceAndPath(
            PasterDreamMod.MOD_ID, "dyedream_leaves");

    /**
     * 当前玩家所在的染梦维度生物群系Key。
     * 供雾色/天空渲染器读取，实现群系专属雾色效果。
     */
    public static ResourceKey<Biome> currentBiomeKey = null;

    private static final double DRIFT_SPEED = 0.0008;
    private static final double DRIFT_RADIUS = 6.0;

    /** ModMusicManager 是否已初始化（注册自定义维度等） */
    private static boolean musicManagerInitialized = false;

    /**
     * 客户端 Tick 后处理
     * <p>
     * 执行以下任务：
     * <ol>
     *   <li>初次运行时初始化 ModMusicManager（注册自定义维度）</li>
     *   <li>驱动 ModMusicManager 的 tick（群系BGM切换、淡入淡出等）</li>
     *   <li>在染梦维度中生成群系专属环境粒子和落叶</li>
     * </ol>
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        // 首次 tick 时初始化 ModMusicManager
        if (!musicManagerInitialized) {
            initMusicManager();
        }

        // 驱动 ModMusicManager tick（BGM 切换、淡入淡出、玩家状态检测）
        ModMusicManager.getInstance().tick();

        // 仅在染梦维度中处理环境粒子
        if (!PDDimensions.isDyedreamWorld(mc.player.level())) return;

        var biomeKey = mc.level.getBiome(mc.player.blockPosition()).unwrapKey();
        if (biomeKey.isEmpty()) return;

        currentBiomeKey = biomeKey.get();
        ResourceKey<Biome> currentBiome = currentBiomeKey;

        if (BIOME_DYEDREAM_0.equals(currentBiome)) {
            spawnDreamfertiliter(mc);
        } else if (BIOME_DYEDREAM_1.equals(currentBiome)) {
            spawnWhiteStar(mc);
        } else if (BIOME_DYEDREAM_2.equals(currentBiome)) {
            spawnSilver(mc);
        } else if (BIOME_DYEDREAM_3.equals(currentBiome)) {
            spawnSnowflakeGround(mc);
        }

        spawnTreeLeaves(mc);
    }

    /**
     * 初始化 ModMusicManager
     * <p>
     * 注册自定义维度，启用 BGM 交叉淡化系统。
     */
    private static void initMusicManager() {
        ModMusicManager.registerCustomDimension(
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dyedream_world"));
        musicManagerInitialized = true;
        PasterDreamMod.LOGGER.info("[PDClientEvents] ModMusicManager 初始化完成");
    }

    /**
     * 生成衍梦粉尘（温暖平原）
     * <p>
     * 粉色染梦粉尘从空中缓缓飘落。
     */
    private static void spawnDreamfertiliter(Minecraft mc) {
        var random = mc.player.getRandom();
        if (random.nextFloat() >= 0.06f) return;

        long gameTime = mc.level.getGameTime();
        double driftX = Math.sin(gameTime * DRIFT_SPEED) * DRIFT_RADIUS;
        double driftZ = Math.cos(gameTime * DRIFT_SPEED * 0.7 + 1.5) * DRIFT_RADIUS;

        double windAngle = Math.sin(gameTime * 0.0001) * 0.5;
        double windX = Math.cos(windAngle) * 0.003;
        double windZ = Math.sin(windAngle) * 0.003;

        SimpleParticleType type = (SimpleParticleType) PDParticles.DREAMFERTILITER_PARTICLE.get();

        int count = 1 + random.nextInt(2);
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = 4.0 + random.nextDouble() * 14.0;

            mc.level.addParticle(
                    type,
                    mc.player.getX() + driftX + Math.cos(angle) * dist,
                    mc.player.getY() + 4.0 + random.nextDouble() * 6.0,
                    mc.player.getZ() + driftZ + Math.sin(angle) * dist,
                    windX,
                    -0.005 - random.nextDouble() * 0.01,
                    windZ
            );
        }
    }

    /**
     * 生成白色星光（炎热森林）
     * <p>
     * 4帧白色星光粒子在林间缓慢漂浮闪烁。
     */
    private static void spawnWhiteStar(Minecraft mc) {
        var random = mc.player.getRandom();
        if (random.nextFloat() >= 0.07f) return;

        long gameTime = mc.level.getGameTime();
        double driftX = Math.sin(gameTime * DRIFT_SPEED * 1.2) * DRIFT_RADIUS;
        double driftZ = Math.cos(gameTime * DRIFT_SPEED * 0.9 + 2.0) * DRIFT_RADIUS;

        SimpleParticleType type = (SimpleParticleType) PDParticles.WHITE_STAR_PARTICLE.holder().get();

        int count = 1 + random.nextInt(3);
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = 2.0 + random.nextDouble() * 16.0;

            mc.level.addParticle(
                    type,
                    mc.player.getX() + driftX + Math.cos(angle) * dist,
                    mc.player.getY() + random.nextDouble() * 8.0,
                    mc.player.getZ() + driftZ + Math.sin(angle) * dist,
                    (random.nextDouble() - 0.5) * 0.004,
                    0.0,
                    (random.nextDouble() - 0.5) * 0.004
            );
        }
    }

    /**
     * 生成银色冰晶粒子（寒冷冰雪）
     * <p>
     * 3帧冰晶银色粒子旋转上浮。
     */
    private static void spawnSilver(Minecraft mc) {
        var random = mc.player.getRandom();
        if (random.nextFloat() >= 0.07f) return;

        long gameTime = mc.level.getGameTime();
        double driftX = Math.sin(gameTime * DRIFT_SPEED * 0.8) * DRIFT_RADIUS;
        double driftZ = Math.cos(gameTime * DRIFT_SPEED * 1.1 + 1.0) * DRIFT_RADIUS;

        SimpleParticleType type = (SimpleParticleType) PDParticles.SILVER_PARTICLE.holder().get();

        int count = 1 + random.nextInt(3);
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = 2.0 + random.nextDouble() * 16.0;

            mc.level.addParticle(
                    type,
                    mc.player.getX() + driftX + Math.cos(angle) * dist,
                    mc.player.getY() + 1.0 + random.nextDouble() * 8.0,
                    mc.player.getZ() + driftZ + Math.sin(angle) * dist,
                    (random.nextDouble() - 0.5) * 0.003,
                    0.01 + random.nextDouble() * 0.015,
                    (random.nextDouble() - 0.5) * 0.003
            );
        }
    }

    /**
     * 生成地面雪花粒子（温暖海洋）
     * <p>
     * 蓝色雪花星芒在地面/水面附近生成，向上飘散。
     */
    private static void spawnSnowflakeGround(Minecraft mc) {
        var random = mc.player.getRandom();
        if (random.nextFloat() >= 0.07f) return;

        long gameTime = mc.level.getGameTime();
        double driftX = Math.sin(gameTime * DRIFT_SPEED * 0.6) * DRIFT_RADIUS;
        double driftZ = Math.cos(gameTime * DRIFT_SPEED * 1.3 + 0.5) * DRIFT_RADIUS;

        SimpleParticleType type = (SimpleParticleType) PDParticles.SNOWFLAKE_0_PARTICLE.holder().get();

        int count = 1 + random.nextInt(3);
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = 2.0 + random.nextDouble() * 14.0;

            double spawnX = mc.player.getX() + driftX + Math.cos(angle) * dist;
            double spawnZ = mc.player.getZ() + driftZ + Math.sin(angle) * dist;
            int floorY = mc.level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE,
                    (int) spawnX, (int) spawnZ);

            mc.level.addParticle(
                    type,
                    spawnX,
                    floorY + 0.5 + random.nextDouble() * 1.5,
                    spawnZ,
                    (random.nextDouble() - 0.5) * 0.006,
                    0.005 + random.nextDouble() * 0.01,
                    (random.nextDouble() - 0.5) * 0.006
            );
        }
    }

    /**
     * 树冠落叶系统
     * <p>
     * 每 4 tick 扫描玩家周围 10 个随机位置，检测到染梦树叶或樱花树
     * 后在其下方生成飘落的叶片粒子。
     */
    private static void spawnTreeLeaves(Minecraft mc) {
        var random = mc.player.getRandom();
        long gameTime = mc.level.getGameTime();
        if (gameTime % 4 != 0) return;

        int playerY = mc.player.blockPosition().getY();

        for (int i = 0; i < 10; i++) {
            double scanX = mc.player.getX() + (random.nextDouble() - 0.5) * 24.0;
            double scanZ = mc.player.getZ() + (random.nextDouble() - 0.5) * 24.0;
            int scanY = playerY + 3 + random.nextInt(10);

            BlockPos checkPos = BlockPos.containing(scanX, scanY, scanZ);
            BlockState blockState = mc.level.getBlockState(checkPos);

            if (isLeafBlock(blockState)) {
                double leafX = checkPos.getX() + random.nextDouble();
                double leafZ = checkPos.getZ() + random.nextDouble();

                SimpleParticleType type = (SimpleParticleType) PDParticles.LEAVES_PARTICLE.holder().get();

                mc.level.addParticle(
                        type,
                        leafX,
                        checkPos.getY() - 0.5,
                        leafZ,
                        (random.nextDouble() - 0.5) * 0.005,
                        -0.01 - random.nextDouble() * 0.015,
                        (random.nextDouble() - 0.5) * 0.005
                );
            }
        }
    }

    /**
     * 判断方块是否为可生成落叶的树叶方块
     *
     * @param state 方块状态
     * @return 如果是染梦树叶则返回 true
     */
    private static boolean isLeafBlock(BlockState state) {
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return DYEDREAM_LEAVES_ID.equals(blockId);
    }
}