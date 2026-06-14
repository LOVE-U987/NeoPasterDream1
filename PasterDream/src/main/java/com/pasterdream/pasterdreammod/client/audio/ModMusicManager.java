package com.pasterdream.pasterdreammod.client.audio;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

/**
 * 模组背景音乐管理器 —— 自定义维度的群系BGM交叉淡化过渡
 * <p>
 * 核心职责：
 * <ul>
 *   <li>检测玩家所在群系变化</li>
 *   <li>在群系切换时执行交叉淡化过渡（旧音乐渐弱 + 新音乐渐强同时进行）</li>
 *   <li>仅在 DimensionAPI 注册的自定义维度中生效</li>
 * </ul>
 * <p>
 * 本类为协调器，实际逻辑委托给以下子系统：
 * <ul>
 *   <li>{@link BiomeMusicRegistry} — 群系音乐映射与自定义维度注册</li>
 *   <li>{@link MusicPlaybackController} — 音乐播放控制</li>
 *   <li>{@link CrossfadeManager} — 交叉淡化状态管理</li>
 *   <li>{@link CooldownManager} — 切换冷却系统</li>
 *   <li>{@link LoopRestartManager} — 循环重播管理</li>
 *   <li>{@link BgmDeduplication} — 去重检测与修复</li>
 * </ul>
 * <p>
 * 过渡策略（交叉淡化）：
 * <ol>
 *   <li>检测到群系变化 → 新音乐的 SoundEvent 与旧音乐不同</li>
 *   <li>进入 FADING 状态：旧音乐音量从 TARGET_VOLUME 逐渐降至 0，
 *       新音乐音量从 0 逐渐升至 TARGET_VOLUME</li>
 *   <li>两个声音实例同时播放，经过 CROSSFADE_STEPS 步后完成过渡</li>
 *   <li>步进间隔为 3 个游戏 tick（~150ms），总过渡时长约 3 秒</li>
 * </ol>
 */
public class ModMusicManager {

    private static ModMusicManager instance;

    // ==================== 常量 ====================

    /** BGM 目标音量（与 sounds.json 中的 volume 一致） */
    public static final float TARGET_VOLUME = 0.3f;

    /** 交叉淡化步数（每步 = 1 个游戏 tick ≈ 50ms，60步 ≈ 3秒） */
    public static final int CROSSFADE_STEPS = 60;

    /** 默认切换冷却 tick 数（100 tick ≈ 5 秒） */
    public static final int DEFAULT_SWITCH_COOLDOWN_TICKS = 100;

    // ==================== 子系统 ====================

    private final BiomeMusicRegistry biomeMusicRegistry;
    private final SoundEventLookup soundEventLookup;
    private final MusicPlaybackController playbackController;
    private final CrossfadeManager crossfadeManager;
    private final CooldownManager cooldownManager;
    private final LoopRestartManager loopRestartManager;
    private final BgmDeduplication deduplication;

    // ==================== 运行时状态 ====================

    /** 上一个 tick 的群系 ID */
    private ResourceLocation previousBiomeId;

    // ==================== 静态初始化 ====================

    static {
        getInstance().biomeMusicRegistry.registerBiomeMusic("biome_dyedream_0", "dyedream_world");
        getInstance().biomeMusicRegistry.registerBiomeMusic("biome_dyedream_1", "dream_heath");
        getInstance().biomeMusicRegistry.registerBiomeMusic("biome_dyedream_2", "dream_delta");
        getInstance().biomeMusicRegistry.registerBiomeMusic("biome_dyedream_3", "dream_taiga");
        getInstance().biomeMusicRegistry.registerBiomeMusic("biome_dyedream_deep_ocean", "sweetdream_music");
        getInstance().biomeMusicRegistry.registerBiomeMusic("biome_dyedream_mushroom_plains", "snowfall_dream_music");
    }

    private ModMusicManager() {
        this.biomeMusicRegistry = new BiomeMusicRegistry();
        this.soundEventLookup = new SoundEventLookup();
        this.playbackController = new MusicPlaybackController(soundEventLookup);
        this.crossfadeManager = new CrossfadeManager(playbackController, soundEventLookup);
        this.deduplication = new BgmDeduplication(playbackController, crossfadeManager);
        this.cooldownManager = new CooldownManager(DEFAULT_SWITCH_COOLDOWN_TICKS);
        this.loopRestartManager = new LoopRestartManager(
                1200, 1800, 600, 1200
        );
    }

    /**
     * 获取 ModMusicManager 单例
     *
     * @return 单例实例
     */
    public static ModMusicManager getInstance() {
        if (instance == null) {
            instance = new ModMusicManager();
        }
        return instance;
    }

    // ==================== 配置 API（静态委托） ====================

    /**
     * 注册群系音乐映射
     *
     * @param biomeId   群系 ID（相对于模组命名空间）
     * @param musicName 音乐注册名称（如 "dream_meadow"）
     */
    public static void registerBiomeMusic(String biomeId, String musicName) {
        getInstance().biomeMusicRegistry.registerBiomeMusic(biomeId, musicName);
    }

    /**
     * 注册自定义维度（启用 ModMusicManager 的维度）
     *
     * @param dimensionId 维度 ID
     */
    public static void registerCustomDimension(ResourceLocation dimensionId) {
        getInstance().biomeMusicRegistry.registerCustomDimension(dimensionId);
    }

    /**
     * 判断当前维度是否为已注册的自定义维度
     *
     * @param level 当前维度
     * @return 如果是自定义维度返回 true
     */
    public static boolean isCustomDimension(Level level) {
        return getInstance().biomeMusicRegistry.isCustomDimension(level);
    }

    // ==================== 实例 API ====================

    /**
     * 查询当前是否正在播放 BGM
     *
     * @return 如果有 BGM 正在播放返回 true
     */
    public boolean isPlayingBgm() {
        return playbackController.isPlaying() || crossfadeManager.isCrossfading();
    }

    /**
     * 设置群系切换冷却 tick 数
     * <p>
     * 玩家进入新群系后，需等待冷却结束后才开始交叉淡化。
     * 冷却期间原 BGM 持续播放，可有效防止群系边界反复横跳导致的 BGM 错乱。
     *
     * @param ticks 冷却 tick 数（20 tick ≈ 1 秒），至少 1 tick
     */
    public void setSwitchCooldownTicks(int ticks) {
        // CooldownManager 的冷却时间在构造时固定，此处无法动态修改
        // 如需支持动态修改需扩展 CooldownManager
        PasterDreamMod.LOGGER.warn("[ModMusicManager] setSwitchCooldownTicks 暂不支持动态修改");
    }

    // ==================== 核心 Tick 逻辑 ====================

    /**
     * 客户端每 tick 调用一次
     * <p>
     * 执行流程：
     * <ol>
     *   <li>检查玩家和世界状态，判断是否在自定义维度中</li>
     *   <li>如果正在进行交叉淡化 → 执行一步淡化</li>
     *   <li>执行 BGM 去重检测</li>
     *   <li>如果处于切换冷却期 → 更新冷却状态</li>
     *   <li>如果群系变化且未在冷却中 → 进入冷却期</li>
     *   <li>如果空闲且没有音乐 → 直接播放当前群系音乐</li>
     *   <li>循环重播检测</li>
     * </ol>
     */
    public void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        // 不在自定义维度中 → 停止所有音乐
        if (!biomeMusicRegistry.isCustomDimension(mc.player.level())) {
            if (playbackController.isPlaying() || crossfadeManager.isCrossfading()) {
                stopAllMusic();
            }
            return;
        }

        // 获取当前群系
        var biomeKeyOptional = mc.level.getBiome(mc.player.blockPosition()).unwrapKey();
        if (biomeKeyOptional.isEmpty()) return;
        ResourceLocation currentBiomeId = biomeKeyOptional.get().location();

        // 处理已经在进行中的交叉淡化步进
        if (crossfadeManager.isCrossfading()) {
            crossfadeManager.updateStep();
            return;
        }

        // 执行 BGM 去重检测
        deduplication.deduplicate();

        String musicName = biomeMusicRegistry.getMusicForBiome(currentBiomeId);
        long gameTick = mc.level.getGameTime();

        // ==================== 切换冷却期逻辑 ====================
        if (cooldownManager.isInCooldown()) {
            cooldownManager.setPendingMusicName(musicName);
            if (cooldownManager.updateCooldown(currentBiomeId, previousBiomeId, gameTick)) {
                // 冷却结束 → 开始交叉淡化
                crossfadeManager.startCrossfade(cooldownManager.getPendingMusicName());
            }
            previousBiomeId = currentBiomeId;
            return;
        }

        // ==================== 群系变化检测 ====================
        boolean biomeChanged = previousBiomeId != null && !currentBiomeId.equals(previousBiomeId);
        previousBiomeId = currentBiomeId;

        if (biomeChanged) {
            String currentMusicName = playbackController.getCurrentMusicName();
            if (musicName != null && musicName.equals(currentMusicName)) {
                // 音乐相同 → 不切换也不进入冷却，但标记群系已变化
                loopRestartManager.markBiomeChanged();
                return;
            }
            // 进入切换冷却期 + 标记群系已变化
            loopRestartManager.markBiomeChanged();
            cooldownManager.enterCooldown(currentBiomeId, musicName, gameTick);
            return;
        }

        // 空闲状态但没有播放音乐 → 直接播放（首次进入维度时触发）
        if (!playbackController.isPlaying()
                && !crossfadeManager.isCrossfading()
                && musicName != null) {
            playbackController.play(musicName);
        }

        // ==================== 循环重播检测 ====================
        if (playbackController.isPlaying()
                && playbackController.getCurrentSound() != null
                && !cooldownManager.isInCooldown()) {
            boolean isMusicActive = Minecraft.getInstance().getSoundManager()
                    .isActive(playbackController.getCurrentSound());
            if (loopRestartManager.update(isMusicActive, gameTick)) {
                // 去重检测：已在播放中 → 跳过
                if (!deduplication.isBgmActive(playbackController.getCurrentMusicName())) {
                    playbackController.restart();
                }
            }
        }
    }

    // ==================== 内部工具 ====================

    /**
     * 停止所有音乐并重置所有状态
     */
    private void stopAllMusic() {
        playbackController.stop();
        crossfadeManager.stopCrossfade();
        cooldownManager.cancelCooldown();
        loopRestartManager.reset();
    }
}