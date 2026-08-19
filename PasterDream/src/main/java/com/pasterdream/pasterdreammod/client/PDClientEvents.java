package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.client.audio.BiomeMusicRegistry;
import com.pasterdream.pasterdreammod.client.audio.ModMusicManager;
import com.pasterdream.pasterdreammod.client.audio.MusicSystemFactory;
import com.pasterdream.pasterdreammod.registry.PDBiomes;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 客户端事件处理类
 * <p>
 * 处理客户端专属的周期性事件，包括染梦维度的群系专属环境粒子和树冠落叶系统。
 * 每个生物群系拥有独特的粒子效果，同时染梦树叶和樱花树周围会飘落叶片。
 * <p>
 * 同时管理 {@link ModMusicManager} 的 tick 驱动。
 * 通过 {@link EventBusSubscriber} 自动注册到游戏事件总线，仅在客户端生效。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public class PDClientEvents {

    private static final ResourceLocation DYEDREAM_LEAVES_ID = ResourceLocation.fromNamespaceAndPath(
            PasterDreamMod.MOD_ID, "dyedream_leaves");

    /**
     * 当前玩家所在的染梦维度生物群系Key。
     * 供雾色/天空渲染器读取，实现群系专属雾色效果。
     */
    public static ResourceKey<Biome> currentBiomeKey = null;

    private static final double DRIFT_SPEED = 0.0008;
    private static final double DRIFT_RADIUS = 6.0;

    /**
     * 音频系统管理器实例。
     * volatile 保证双重检查锁定下的安全发布，避免其他线程读到部分构造的对象。
     */
    private static volatile ModMusicManager musicManager;

    /** ModMusicManager 是否已初始化（注册自定义维度等），volatile 保证跨线程可见性 */
    private static volatile boolean musicManagerInitialized = false;

    /** 上一次 tick 玩家是否处于竞技场遗迹群系，用于群系进入检测 */
    private static boolean wasInArenaBiome = false;

    /** 竞技场自言自语文本的冷却结束时间（游戏刻） */
    private static long arenaWhisperCooldownUntil = 0;

    /** 竞技场自言自语冷却时长：30 秒（600 tick） */
    private static final long ARENA_WHISPER_COOLDOWN_TICKS = 600;

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
        if (musicManager != null) {
            musicManager.tick();
        }

        // 暂停时不生成环境粒子，避免解冻时一瞬间爆出大量粒子
        if (mc.isPaused()) return;

        boolean isDyedream = PDDimensions.isDyedreamWorld(mc.player.level());
        boolean isArena = PDDimensions.isAaroncosArenaWorld(mc.player.level());

        // 仅在染梦维度或竞技场维度中处理环境粒子
        if (!isDyedream && !isArena) return;

        var biomeKey = mc.level.getBiome(mc.player.blockPosition()).unwrapKey();
        if (biomeKey.isEmpty()) return;

        currentBiomeKey = biomeKey.get();
        ResourceKey<Biome> currentBiome = currentBiomeKey;

        if (isDyedream) {
            if (PDBiomes.DYEDREAM_PLAINS.equals(currentBiome)) {
                spawnDreamfertiliter(mc);
            } else if (PDBiomes.DYEDREAM_FOREST.equals(currentBiome)) {
                spawnWhiteStar(mc);
            } else if (PDBiomes.DYEDREAM_FROZEN_TUNDRA.equals(currentBiome)) {
                spawnSilver(mc);
            } else if (PDBiomes.DYEDREAM_COLD_OCEAN.equals(currentBiome)) {
                spawnSnowflakeGround(mc);
            } else if (PDBiomes.DYEDREAM_DEEP_OCEAN.equals(currentBiome)) {
                spawnDeepOceanBioluminescence(mc);
            } else if (PDBiomes.DYEDREAM_MUSHROOM_PLAINS.equals(currentBiome)) {
                spawnMushroomSpores(mc);
            } else if (PDBiomes.DYEDREAM_SHORE.equals(currentBiome)) {
                spawnShoreSpray(mc);
            } else if (PDBiomes.DYEDREAM_RIVER.equals(currentBiome)) {
                spawnRiverGlow(mc);
            } else if (PDBiomes.DYEDREAM_DENSE_FOREST.equals(currentBiome)) {
                spawnForestFireflies(mc);
            }

            spawnTreeLeaves(mc);
        } else if (isArena) {
            boolean inArenaBiome = PDBiomes.BIOME_AARONCOS_ARENA.equals(currentBiome);
            tryShowArenaWhisper(mc, inArenaBiome);
            if (inArenaBiome) {
                spawnArenaShadowMist(mc);
            }
        }
    }

    /**
     * 获取音频系统管理器实例
     * <p>
     * 如果尚未初始化，则先调用 {@link #initMusicManager()} 完成初始化。
     * 使用双重检查锁定（double-checked locking）保证多线程环境下的单例创建。
     *
     * @return ModMusicManager 实例
     */
    public static ModMusicManager getMusicManager() {
        if (musicManager == null) {
            synchronized (PDClientEvents.class) {
                if (musicManager == null) {
                    initMusicManager();
                }
            }
        }
        return musicManager;
    }

    /**
     * 获取群系音乐注册表
     * <p>
     * 如果尚未初始化，则先调用 {@link #initMusicManager()} 完成初始化。
     * 供 {@link com.pasterdream.pasterdreammod.mixin.MinecraftMixin} 等外部类查询
     * ModMusicManager 管理的自定义维度。
     *
     * @return BiomeMusicRegistry 实例
     */
    public static BiomeMusicRegistry getBiomeMusicRegistry() {
        return getMusicManager().getBiomeMusicRegistry();
    }

    /**
     * 初始化 ModMusicManager
     * <p>
     * 使用工厂创建音频系统实例，注册自定义维度与默认群系音乐映射，
     * 启用 BGM 交叉淡化系统。
     * <p>
     * 方法级 synchronized（与 {@link #getMusicManager()} 的双重检查锁定共用类锁）
     * 加内部初始化标记防护，保证并发调用下只初始化一次；
     * 先在局部变量上完成全部配置、最后才发布到 volatile 字段，
     * 确保其他线程拿到的一定是完整初始化的实例。
     */
    private static synchronized void initMusicManager() {
        // 并发/重复调用防护：已初始化则直接返回
        if (musicManagerInitialized) return;

        // 使用工厂创建音频系统（先在局部变量上配置，避免发布部分构造对象）
        ModMusicManager manager = MusicSystemFactory.createMusicSystem();

        // 注册自定义维度
        manager.registerCustomDimension(
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dyedream_world"));
        manager.registerCustomDimension(
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "wind_journey_world"));

        // 注册默认群系音乐映射
        manager.initializeDefaultBiomeMusic();

        // 配置完成后再发布
        musicManager = manager;
        musicManagerInitialized = true;
        PDDebugLogger.mainInfo("[PDClientEvents] ModMusicManager 初始化完成");
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

        SimpleParticleType type = (SimpleParticleType) PDParticles.DREAMFERTILITER_PARTICLE.particleType();

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

    /**
     * 生成海岸浪花泡沫粒子（染梦海岸）
     * <p>
     * 模拟海浪拍打岸边时溅起的泡沫和盐雾效果，使用白色气泡粒子从水面附近生成，
     * 向上飘散后逐渐消失。粒子带有轻微的横向漂移，模拟海风效果。
     */
    private static void spawnShoreSpray(Minecraft mc) {
        var random = mc.player.getRandom();
        if (random.nextFloat() >= 0.08f) return;

        long gameTime = mc.level.getGameTime();
        double driftX = Math.sin(gameTime * DRIFT_SPEED * 0.6) * DRIFT_RADIUS;
        double driftZ = Math.cos(gameTime * DRIFT_SPEED * 1.2 + 0.8) * DRIFT_RADIUS;

        SimpleParticleType type = (SimpleParticleType) PDParticles.SNOWFLAKE_0_PARTICLE.holder().get();

        double playerFloorY = mc.player.getY() - 1.0;

        int count = 1 + random.nextInt(2);
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = 2.0 + random.nextDouble() * 12.0;

            mc.level.addParticle(
                    type,
                    mc.player.getX() + driftX + Math.cos(angle) * dist,
                    playerFloorY + 0.2 + random.nextDouble() * 1.0,
                    mc.player.getZ() + driftZ + Math.sin(angle) * dist,
                    (random.nextDouble() - 0.5) * 0.008,
                    0.01 + random.nextDouble() * 0.015,
                    (random.nextDouble() - 0.5) * 0.008
            );
        }
    }

    /**
     * 生成河流发光粒子（染梦河流）
     * <p>
     * 模拟河流中漂浮的发光生物和花瓣效果，使用白色星光粒子在水面上方缓慢漂浮，
     * 带有柔和的垂直上下运动，营造梦幻般的河流氛围。
     */
    private static void spawnRiverGlow(Minecraft mc) {
        var random = mc.player.getRandom();
        if (random.nextFloat() >= 0.07f) return;

        long gameTime = mc.level.getGameTime();
        double driftX = Math.sin(gameTime * DRIFT_SPEED * 0.4) * DRIFT_RADIUS;
        double driftZ = Math.cos(gameTime * DRIFT_SPEED * 0.8 + 1.5) * DRIFT_RADIUS;

        SimpleParticleType type = (SimpleParticleType) PDParticles.WHITE_STAR_PARTICLE.holder().get();

        int seaLevel = mc.level.getSeaLevel();

        int count = 1 + random.nextInt(2);
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = 1.5 + random.nextDouble() * 10.0;

            mc.level.addParticle(
                    type,
                    mc.player.getX() + driftX + Math.cos(angle) * dist,
                    seaLevel + 0.3 + random.nextDouble() * 2.0,
                    mc.player.getZ() + driftZ + Math.sin(angle) * dist,
                    (random.nextDouble() - 0.5) * 0.003,
                    (Math.sin(gameTime * 0.01 + angle) * 0.005),
                    (random.nextDouble() - 0.5) * 0.003
            );
        }
    }

    /**
     * 生成森林萤火虫粒子（染梦密林）
     * <p>
     * 模拟森林中漂浮的萤火虫效果，使用粉色粉尘粒子在地面上方漂浮，
     * 带有随机闪烁和轻微的上下移动，营造神秘的魔法森林氛围。
     */
    private static void spawnForestFireflies(Minecraft mc) {
        var random = mc.player.getRandom();
        if (random.nextFloat() >= 0.09f) return;

        long gameTime = mc.level.getGameTime();
        double driftX = Math.sin(gameTime * DRIFT_SPEED * 0.5) * DRIFT_RADIUS;
        double driftZ = Math.cos(gameTime * DRIFT_SPEED * 0.7 + 2.0) * DRIFT_RADIUS;

        SimpleParticleType type = (SimpleParticleType) PDParticles.DREAMFERTILITER_PARTICLE.particleType();

        double playerFloorY = mc.player.getY() - 1.0;

        int count = 1 + random.nextInt(3);
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = 1.0 + random.nextDouble() * 14.0;

            mc.level.addParticle(
                    type,
                    mc.player.getX() + driftX + Math.cos(angle) * dist,
                    playerFloorY + 1.0 + random.nextDouble() * 6.0,
                    mc.player.getZ() + driftZ + Math.sin(angle) * dist,
                    (random.nextDouble() - 0.5) * 0.004,
                    (Math.sin(gameTime * 0.015 + angle * 2) * 0.008),
                    (random.nextDouble() - 0.5) * 0.004
            );
        }
    }

    /**
     * 尝试显示竞技场遗迹群系的自言自语文本。
     * <p>
     * 当玩家刚进入 {@link PDBiomes#BIOME_AARONCOS_ARENA} 且冷却结束时，
     * 在聊天栏显示“我觉得这里不太对劲...”，使其比 action bar 更持久、更易察觉。
     * 冷却机制避免反复刷屏；离开群系后重新进入可再次触发。
     *
     * @param mc           Minecraft 客户端实例
     * @param inArenaBiome 当前是否处于竞技场遗迹群系
     */
    private static void tryShowArenaWhisper(Minecraft mc, boolean inArenaBiome) {
        long gameTime = mc.level.getGameTime();

        if (inArenaBiome && !wasInArenaBiome && gameTime >= arenaWhisperCooldownUntil) {
            mc.player.displayClientMessage(
                    Component.translatable("message.pasterdream.aaroncos_arena.whisper"),
                    false);
            arenaWhisperCooldownUntil = gameTime + ARENA_WHISPER_COOLDOWN_TICKS;
        }

        wasInArenaBiome = inArenaBiome;
    }

    /**
     * 生成竞技场灯影雾气粒子。
     * <p>
     * 紫色发光孢子从玩家周围缓缓上浮，模拟灯影能量从 portal 废墟中渗出的效果。
     * 粒子密度较低，保持“点缀式”氛围，避免像感染类模组一样过度张扬。
     *
     * @param mc Minecraft 客户端实例
     */
    private static void spawnArenaShadowMist(Minecraft mc) {
        var random = mc.player.getRandom();
        if (random.nextFloat() >= 0.07f) return;

        long gameTime = mc.level.getGameTime();
        double driftX = Math.sin(gameTime * DRIFT_SPEED * 0.4) * DRIFT_RADIUS;
        double driftZ = Math.cos(gameTime * DRIFT_SPEED * 0.6 + 1.5) * DRIFT_RADIUS;

        SimpleParticleType type = (SimpleParticleType) PDParticles.DREAM_SPORE.holder().get();

        int count = 1 + random.nextInt(2);
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = 2.0 + random.nextDouble() * 16.0;

            mc.level.addParticle(
                    type,
                    mc.player.getX() + driftX + Math.cos(angle) * dist,
                    mc.player.getY() + 0.5 + random.nextDouble() * 4.0,
                    mc.player.getZ() + driftZ + Math.sin(angle) * dist,
                    (random.nextDouble() - 0.5) * 0.002,
                    0.006 + random.nextDouble() * 0.01,
                    (random.nextDouble() - 0.5) * 0.002
            );
        }
    }
}