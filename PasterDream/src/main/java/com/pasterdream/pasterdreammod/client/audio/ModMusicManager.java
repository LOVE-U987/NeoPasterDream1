package com.pasterdream.pasterdreammod.client.audio;

import com.pasterdream.pasterdreammod.config.PDClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import net.minecraft.util.RandomSource;

/**
 * 模组背景音乐管理器 —— 自定义维度的群系BGM交叉淡化过渡
 * <p>
 * 核心职责：
 * <ul>
 *   <li>检测玩家所在群系变化</li>
 *   <li>在群系切换时执行交叉淡化过渡</li>
 *   <li>仅在 DimensionAPI 注册的自定义维度中生效</li>
 * </ul>
 * <p>
 * 过渡策略（交叉淡化）：
 * <ol>
 *   <li>检测到群系变化且新音乐与当前音乐不同 → 进入切换冷却期</li>
 *   <li>冷却结束后触发交叉淡化：新音乐从零音量开始逐 tick 渐强至 {@link #TARGET_VOLUME}，
 *       旧音乐同步逐 tick 渐弱，静音后自动停止，全程约 {@link #CROSSFADE_STEPS} 个游戏 tick</li>
 *   <li>交叉淡化期间继续检测群系变化，必要时重定向淡入目标，避免切换滞后</li>
 *   <li>冷却期间玩家回到原群系则取消切换，进入新群系则重置冷却</li>
 * </ol>
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
 * 依赖关系通过构造函数注入，由 {@link MusicSystemFactory} 负责组装。
 */
public class ModMusicManager {

    // ==================== 常量 ====================

    /** BGM 目标音量（与 sounds.json 中的 volume 一致） */
    public static final float TARGET_VOLUME = 0.3f;

    /**
     * 获取实际生效的 BGM 音量。
     * <p>
     * 由 {@link com.pasterdream.pasterdreammod.config.PDClientConfig#BGM_MASTER_VOLUME}
     * 作为总音量倍率与目标音量相乘得到；配置实时生效，不需要重启游戏。
     *
     * @return 实际播放音量（0.0 ~ 1.0）
     */
    public static float getEffectiveVolume() {
        return TARGET_VOLUME * PDClientConfig.BGM_MASTER_VOLUME.get().floatValue();
    }

    /**
     * 获取指定曲目的实际生效音量。
     * <p>
     * 计算方式：目标音量 × 主音量 × 曲目独立音量倍率。
     * 未配置独立音量的曲目默认倍率为 1.0。
     *
     * @param musicName 音乐名称
     * @return 实际播放音量（0.0 ~ 1.0）
     */
    public static float getEffectiveVolume(String musicName) {
        float trackVolume = 1.0f;
        Supplier<Double> volumeSupplier = PDClientConfig.getBgmVolume(musicName);
        if (volumeSupplier != null) {
            trackVolume = volumeSupplier.get().floatValue();
        }
        return TARGET_VOLUME * PDClientConfig.BGM_MASTER_VOLUME.get().floatValue() * trackVolume;
    }

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

    /** 每个群系当前选中的曲目（用于多曲目随机时避免每 tick 重新抽选） */
    private final Map<ResourceLocation, String> biomeTrackSelection = new HashMap<>();

    /** 曲目随机选择器 */
    private final RandomSource trackRandom = RandomSource.create();

    /**
     * 构造函数 —— 通过依赖注入接收所有子系统
     *
     * @param biomeMusicRegistry    群系音乐注册表
     * @param soundEventLookup      声音事件查找器
     * @param playbackController    音乐播放控制器
     * @param crossfadeManager      交叉淡化管理器
     * @param cooldownManager       冷却管理器
     * @param loopRestartManager    循环重播管理器
     * @param deduplication         BGM 去重检测器
     */
    public ModMusicManager(
            BiomeMusicRegistry biomeMusicRegistry,
            SoundEventLookup soundEventLookup,
            MusicPlaybackController playbackController,
            CrossfadeManager crossfadeManager,
            CooldownManager cooldownManager,
            LoopRestartManager loopRestartManager,
            BgmDeduplication deduplication) {
        this.biomeMusicRegistry = Objects.requireNonNull(biomeMusicRegistry, "[ModMusicManager] biomeMusicRegistry 不能为空");
        this.soundEventLookup = Objects.requireNonNull(soundEventLookup, "[ModMusicManager] soundEventLookup 不能为空");
        this.playbackController = Objects.requireNonNull(playbackController, "[ModMusicManager] playbackController 不能为空");
        this.crossfadeManager = Objects.requireNonNull(crossfadeManager, "[ModMusicManager] crossfadeManager 不能为空");
        this.cooldownManager = Objects.requireNonNull(cooldownManager, "[ModMusicManager] cooldownManager 不能为空");
        this.loopRestartManager = Objects.requireNonNull(loopRestartManager, "[ModMusicManager] loopRestartManager 不能为空");
        this.deduplication = Objects.requireNonNull(deduplication, "[ModMusicManager] deduplication 不能为空");
    }

    // ==================== 配置 API（实例方法） ====================

    /**
     * 初始化默认群系音乐映射
     * <p>
     * 注册染梦维度的默认群系音乐配置。
     */
    public void initializeDefaultBiomeMusic() {
        registerBiomeMusic("biome_dyedream_0", "dyedream_world");
        registerBiomeMusic("biome_dyedream_1", "dream_heath");
        registerBiomeMusic("biome_dyedream_2", "dream_delta");
        registerBiomeMusic("biome_dyedream_3", "dream_taiga");
        registerBiomeMusic("biome_dyedream_deep_ocean", "sweetdream_music");
        registerBiomeMusic("biome_dyedream_mushroom_plains", "snowfall_dream_music");
        registerBiomeMusic("biome_dyedream_dense_forest", "dream_meadow_daisy");
        registerBiomeMusic("wind_journey_biome_0", "wind_journey_departure", "wind_journey_midsummer");
        registerBiomeMusic("wind_journey_biome_1", "wind_journey_departure", "wind_journey_midsummer");
    }

    /**
     * 注册群系音乐映射（单首曲目）
     *
     * @param biomeId   群系 ID（相对于模组命名空间）
     * @param musicName 音乐注册名称（如 "dream_meadow"）
     */
    public void registerBiomeMusic(String biomeId, String musicName) {
        biomeMusicRegistry.registerBiomeMusic(biomeId, musicName);
    }

    /**
     * 注册群系音乐映射（多首曲目随机播放）
     *
     * @param biomeId    群系 ID（相对于模组命名空间）
     * @param musicNames 音乐注册名称列表
     */
    public void registerBiomeMusic(String biomeId, String... musicNames) {
        biomeMusicRegistry.registerBiomeMusic(biomeId, musicNames);
    }

    /**
     * 注册自定义维度（启用 ModMusicManager 的维度）
     *
     * @param dimensionId 维度 ID
     */
    public void registerCustomDimension(ResourceLocation dimensionId) {
        biomeMusicRegistry.registerCustomDimension(dimensionId);
    }

    /**
     * 获取群系音乐注册表
     *
     * @return BiomeMusicRegistry 实例
     */
    public BiomeMusicRegistry getBiomeMusicRegistry() {
        return biomeMusicRegistry;
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
        // 传递给 CooldownManager 真正生效
        cooldownManager.setSwitchCooldownTicks(ticks);
    }

    // ==================== 核心 Tick 逻辑 ====================

    /**
     * 客户端每 tick 调用一次
     * <p>
     * 执行流程：
     * <ol>
     *   <li>检查玩家和世界状态，判断是否在自定义维度中</li>
     *   <li>如果正在进行过渡 → 步进过渡，并继续检测群系变化，必要时重定向淡入目标</li>
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

        // BGM 总开关关闭 → 淡出并停止所有模组 BGM（不影响唱片等其它音乐）
        if (!PDClientConfig.BGM_MASTER_ENABLED.get()) {
            if (playbackController.getCurrentSound() != null
                    || playbackController.getCurrentMusicName() != null
                    || crossfadeManager.isCrossfading()) {
                stopAllMusic();
            }
            return;
        }

        // 不在自定义维度中 → 淡出并停止所有音乐（含播完待重播的残留状态）
        if (!biomeMusicRegistry.isCustomDimension(mc.player.level())) {
            if (playbackController.getCurrentSound() != null
                    || playbackController.getCurrentMusicName() != null
                    || crossfadeManager.isCrossfading()) {
                stopAllMusic();
            }
            return;
        }

        // 获取当前群系
        var biomeKeyOptional = mc.level.getBiome(mc.player.blockPosition()).unwrapKey();
        if (biomeKeyOptional.isEmpty()) return;
        ResourceLocation currentBiomeId = biomeKeyOptional.get().location();

        List<String> candidates = biomeMusicRegistry.getMusicForBiome(currentBiomeId);
        String musicName = selectTrack(currentBiomeId, candidates);
        long gameTick = mc.level.getGameTime();

        // 当前正在播放的音乐被单独禁用 → 淡出停止
        String currentMusicName = playbackController.getCurrentMusicName();
        if (currentMusicName != null && !isBgmEnabled(currentMusicName)) {
            crossfadeManager.beginFadeOutAll();
            previousBiomeId = currentBiomeId;
            return;
        }

        // 处理已经在进行中的过渡步进
        // 过渡期间不再跳过群系变化检测，避免跨群系切换滞后整个淡化周期
        if (crossfadeManager.isCrossfading()) {
            boolean stillFading = crossfadeManager.updateStep();

            boolean biomeChangedDuringFade = previousBiomeId != null && !currentBiomeId.equals(previousBiomeId);
            previousBiomeId = currentBiomeId;
            if (biomeChangedDuringFade) {
                loopRestartManager.markBiomeChanged();
                // 新群系无音乐映射时保持原过渡目标，不做重定向
                if (musicName != null) {
                    if (stillFading) {
                        // 过渡尚未结束 → 直接重定向淡入目标
                        crossfadeManager.redirectCrossfade(musicName);
                    } else if (!musicName.equals(playbackController.getCurrentMusicName())) {
                        // 本 tick 恰好过渡结束 → 按常规流程进入切换冷却
                        cooldownManager.enterCooldown(currentBiomeId, musicName, gameTick);
                    }
                }
            }
            return;
        }

        // 执行 BGM 去重检测
        deduplication.deduplicate();

        // ==================== 切换冷却期逻辑 ====================
        if (cooldownManager.isInCooldown()) {
            cooldownManager.setPendingMusicName(musicName);
            if (cooldownManager.updateCooldown(currentBiomeId, previousBiomeId, gameTick)) {
                // 冷却结束 → 开始交叉淡化
                // 目标音乐为 null（群系无映射）时不触发切换，保持当前 BGM 继续播放
                String pendingMusicName = cooldownManager.getPendingMusicName();
                if (pendingMusicName != null) {
                    crossfadeManager.startCrossfade(pendingMusicName);
                }
            }
            previousBiomeId = currentBiomeId;
            return;
        }

        // ==================== 群系变化检测 ====================
        boolean biomeChanged = previousBiomeId != null && !currentBiomeId.equals(previousBiomeId);
        previousBiomeId = currentBiomeId;

        if (biomeChanged) {
            String playingName = playbackController.getCurrentMusicName();
            // 新群系无音乐映射或被单独禁用 → 淡出停止当前 BGM；
            // 音乐相同 → 同样不切换也不进入冷却；两种情况都只标记群系已变化
            if (musicName == null) {
                if (playbackController.getCurrentSound() != null || crossfadeManager.isCrossfading()) {
                    crossfadeManager.beginFadeOutAll();
                }
                loopRestartManager.markBiomeChanged();
                return;
            }
            if (musicName.equals(playingName)) {
                loopRestartManager.markBiomeChanged();
                return;
            }
            // 进入切换冷却期 + 标记群系已变化
            loopRestartManager.markBiomeChanged();
            cooldownManager.enterCooldown(currentBiomeId, musicName, gameTick);
            return;
        }

        // 空闲状态且当前没有任何 BGM → 直接播放（首次进入维度时触发）
        // 以 currentSound/currentMusicName 判空而非 isPlaying()，
        // 避免非循环 BGM 播完后绕过循环重播间隔立即重播
        if (playbackController.getCurrentSound() == null
                && playbackController.getCurrentMusicName() == null
                && !crossfadeManager.isCrossfading()
                && musicName != null) {
            playbackController.play(musicName);
        }

        // ==================== 循环重播检测 ====================
        if (playbackController.getCurrentSound() != null
                && !cooldownManager.isInCooldown()) {
            // 复用方法顶部的 mc 局部变量，避免重复调用 Minecraft.getInstance()
            boolean isMusicActive = mc.getSoundManager()
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
     * 停止所有音乐并重置所有状态（带淡出过渡）
     * <p>
     * 首次调用触发全体淡出（{@link FadeState#FADING_OUT}），
     * 之后每 tick 重复调用推进淡出，音量归零后由声音实例自行停止，
     * 全部结束后状态机自动回到空闲。
     */
    private void stopAllMusic() {
        crossfadeManager.beginFadeOutAll(); // 幂等：已在全体淡出中则无操作
        crossfadeManager.updateStep();      // 推进淡出，检测是否全部结束
        cooldownManager.cancelCooldown();
        loopRestartManager.reset();
    }

    /**
     * 为指定群系选择一首可播放的曲目。
     * <p>
     * 若群系已缓存选中曲目且该曲目仍可用，则直接复用；否则从候选列表中随机挑选一首。
     * 多曲目群系（如风之旅途）会在进入群系时随机选定一首，直到离开该群系前保持不变，
     * 避免每 tick 重新抽选导致 BGM 频繁切换。
     *
     * @param biomeId   群系 ID
     * @param candidates 候选曲目列表
     * @return 可播放的音乐名称；无可用曲目时返回 null
     */
    private String selectTrack(ResourceLocation biomeId, List<String> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }
        String selected = biomeTrackSelection.get(biomeId);
        if (selected != null && candidates.contains(selected) && isBgmEnabled(selected)) {
            return selected;
        }
        List<String> enabled = candidates.stream()
                .filter(this::isBgmEnabled)
                .toList();
        if (enabled.isEmpty()) {
            return null;
        }
        selected = enabled.get(trackRandom.nextInt(enabled.size()));
        biomeTrackSelection.put(biomeId, selected);
        return selected;
    }

    /**
     * 检查指定音乐是否被配置启用。
     * <p>
     * 未在配置表中注册的音乐默认视为启用，以保持向后兼容。
     *
     * @param musicName 音乐名称
     * @return 如果启用返回 true
     */
    private boolean isBgmEnabled(String musicName) {
        Supplier<Boolean> switchSupplier = PDClientConfig.getBgmSwitch(musicName);
        return switchSupplier == null || Boolean.TRUE.equals(switchSupplier.get());
    }
}
