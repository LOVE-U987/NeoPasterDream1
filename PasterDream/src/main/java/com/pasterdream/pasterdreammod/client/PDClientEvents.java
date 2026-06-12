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
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

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
    private static final ResourceKey<Biome> BIOME_DYEDREAM_DEEP_OCEAN = ResourceKey.create(
            net.minecraft.core.registries.Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_deep_ocean")
    );
    private static final ResourceKey<Biome> BIOME_DYEDREAM_MUSHROOM_PLAINS = ResourceKey.create(
            net.minecraft.core.registries.Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "biome_dyedream_mushroom_plains")
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

        // 暂停时不生成环境粒子，避免解冻时一瞬间爆出大量粒子
        if (mc.isPaused()) return;

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
        } else if (BIOME_DYEDREAM_DEEP_OCEAN.equals(currentBiome)) {
            spawnDeepOceanBioluminescence(mc);
        } else if (BIOME_DYEDREAM_MUSHROOM_PLAINS.equals(currentBiome)) {
            spawnMushroomSpores(mc);
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
     * 优化说明：使用玩家 Y 坐标 - 2 作为粗略地面位置，避免昂贵的 getHeight 查询。
     */
    private static void spawnSnowflakeGround(Minecraft mc) {
        var random = mc.player.getRandom();
        if (random.nextFloat() >= 0.07f) return;

        long gameTime = mc.level.getGameTime();
        double driftX = Math.sin(gameTime * DRIFT_SPEED * 0.6) * DRIFT_RADIUS;
        double driftZ = Math.cos(gameTime * DRIFT_SPEED * 1.3 + 0.5) * DRIFT_RADIUS;

        SimpleParticleType type = (SimpleParticleType) PDParticles.SNOWFLAKE_0_PARTICLE.holder().get();

        double playerFloorY = mc.player.getY() - 2.0;

        int count = 1 + random.nextInt(3);
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = 2.0 + random.nextDouble() * 14.0;

            double spawnX = mc.player.getX() + driftX + Math.cos(angle) * dist;
            double spawnZ = mc.player.getZ() + driftZ + Math.sin(angle) * dist;

            mc.level.addParticle(
                    type,
                    spawnX,
                    playerFloorY + 0.5 + random.nextDouble() * 1.5,
                    spawnZ,
                    (random.nextDouble() - 0.5) * 0.006,
                    0.005 + random.nextDouble() * 0.01,
                    (random.nextDouble() - 0.5) * 0.006
            );
        }
    }

    /**
     * 生成深海荧光羽毛（晶莹深海）
     * <p>
     * 白色荧光羽毛粒子从海面之上缓缓上浮，模拟发光浮游生物/深海羽毛水母
     * 在海面释放荧光孢子的效果。粒子使用 feather_white_particle 类型，
     * 12帧动画呈现羽毛飘逸感，夜晚自动切换为发光渲染。
     */
    private static void spawnDeepOceanBioluminescence(Minecraft mc) {
        var random = mc.player.getRandom();
        if (random.nextFloat() >= 0.06f) return;

        long gameTime = mc.level.getGameTime();
        double driftX = Math.sin(gameTime * DRIFT_SPEED * 0.5) * DRIFT_RADIUS;
        double driftZ = Math.cos(gameTime * DRIFT_SPEED * 1.1 + 1.8) * DRIFT_RADIUS;

        SimpleParticleType type = (SimpleParticleType) PDParticles.FEATHER_WHITE_PARTICLE.holder().get();

        int seaLevel = mc.level.getSeaLevel();

        int count = 1 + random.nextInt(2);
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = 3.0 + random.nextDouble() * 12.0;

            mc.level.addParticle(
                    type,
                    mc.player.getX() + driftX + Math.cos(angle) * dist,
                    seaLevel - 1.0 + random.nextDouble() * 7.0,
                    mc.player.getZ() + driftZ + Math.sin(angle) * dist,
                    (random.nextDouble() - 0.5) * 0.004,
                    0.008 + random.nextDouble() * 0.012,
                    (random.nextDouble() - 0.5) * 0.004
            );
        }
    }

    /**
     * 生成蘑菇孢子粉尘（蘑菇平原）
     * <p>
     * 暖金色孢子粒子从地面缓缓飘散，模拟夜晚发光的魔法孢子粉尘效果。
     * 粒子使用 dyedream_0_particle 类型，夜晚自动切换为发光渲染，
     * 伴随大小脉冲呼吸效果和横向风漂运动。
     */
    private static void spawnMushroomSpores(Minecraft mc) {
        var random = mc.player.getRandom();
        if (random.nextFloat() >= 0.07f) return;

        long gameTime = mc.level.getGameTime();
        double driftX = Math.sin(gameTime * DRIFT_SPEED * 0.7) * DRIFT_RADIUS;
        double driftZ = Math.cos(gameTime * DRIFT_SPEED * 0.9 + 1.2) * DRIFT_RADIUS;

        SimpleParticleType type = (SimpleParticleType) PDParticles.DYEDREAM_0_PARTICLE.holder().get();

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
     * 树冠落叶系统
     * <p>
     * 每 4 tick 在玩家周围扫描树叶方块，检测到后生成飘落的叶片粒子。
     * 优化说明：减少每次扫描数量，添加已加载区块检测以避免触发 chunk 加载。
     */
    private static void spawnTreeLeaves(Minecraft mc) {
        var random = mc.player.getRandom();
        long gameTime = mc.level.getGameTime();
        if (gameTime % 4 != 0) return;

        int playerY = mc.player.blockPosition().getY();

        for (int i = 0; i < 5; i++) {
            double scanX = mc.player.getX() + (random.nextDouble() - 0.5) * 24.0;
            double scanZ = mc.player.getZ() + (random.nextDouble() - 0.5) * 24.0;
            int scanY = playerY + 3 + random.nextInt(10);

            BlockPos checkPos = BlockPos.containing(scanX, scanY, scanZ);

            if (!isChunkLoaded(mc, checkPos)) continue;

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
     * 检查指定位置的区块是否已加载，避免触发 chunk 加载导致的卡顿
     */
    private static boolean isChunkLoaded(Minecraft mc, BlockPos pos) {
        return mc.level.getChunkSource().hasChunk(pos.getX() >> 4, pos.getZ() >> 4);
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