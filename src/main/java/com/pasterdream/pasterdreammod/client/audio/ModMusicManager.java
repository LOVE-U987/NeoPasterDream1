package com.pasterdream.pasterdreammod.client.audio;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;

import java.util.*;

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

    // ==================== 群系音乐映射 ====================

    private static final Map<ResourceLocation, String> BIOME_MUSIC_MAP = new LinkedHashMap<>();
    private static final Set<ResourceLocation> CUSTOM_DIMENSIONS = new HashSet<>();

    // ==================== 运行时状态 ====================

    private FadeState fadeState = FadeState.IDLE;

    /** 新音乐声音实例（过渡完成后保留此实例继续播放） */
    private SoundInstance currentSound;

    /** 旧音乐声音实例（过渡中逐渐降低音量，过渡完成后停止） */
    private SoundInstance fadingOutSound;

    /** 当前音乐名称 */
    private String currentMusicName;

    /** 正在淡出的旧音乐名称 */
    private String fadingOutMusicName;

    /** 上一个 tick 的群系 ID */
    private ResourceLocation previousBiomeId;

    /** 当前交叉淡化步数（0 ~ CROSSFADE_STEPS） */
    private int crossfadeStep;

    /** 交叉淡化期间的新音乐名称（淡化完成后赋值给 currentMusicName） */
    private String crossfadeTargetMusicName;

    /** 群系切换冷却 tick 数（可配置，默认 5 秒） */
    private int switchCooldownTicks = DEFAULT_SWITCH_COOLDOWN_TICKS;

    // ==================== 切换冷却状态 ====================

    /** 是否处于切换冷却期 */
    private boolean isInCooldown = false;

    /** 冷却期记录的目标群系 ID（冷却结束后要切换到该群系） */
    private ResourceLocation pendingBiomeId;

    /** 冷却期记录的目标音乐名称 */
    private String pendingMusicName;

    /** 冷却期开始的游戏 tick 数 */
    private long cooldownStartTick;

    // ==================== 静态初始化 ====================

    static {
        registerBiomeMusic("biome_dyedream_0", "dream_meadow");
        registerBiomeMusic("biome_dyedream_1", "dream_heath");
        registerBiomeMusic("biome_dyedream_2", "dream_taiga");
        registerBiomeMusic("biome_dyedream_3", "dream_delta");
    }

    private ModMusicManager() {
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

    // ==================== 配置 API ====================

    /**
     * 注册群系音乐映射
     *
     * @param biomeId   群系 ID（相对于模组命名空间）
     * @param musicName 音乐注册名称（如 "dream_meadow"）
     */
    public static void registerBiomeMusic(String biomeId, String musicName) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, biomeId);
        BIOME_MUSIC_MAP.put(id, musicName);
    }

    /**
     * 注册自定义维度（启用 ModMusicManager 的维度）
     *
     * @param dimensionId 维度 ID
     */
    public static void registerCustomDimension(ResourceLocation dimensionId) {
        CUSTOM_DIMENSIONS.add(dimensionId);
    }

    /**
     * 判断当前维度是否为已注册的自定义维度
     *
     * @param level 当前维度
     * @return 如果是自定义维度返回 true
     */
    public static boolean isCustomDimension(Level level) {
        return CUSTOM_DIMENSIONS.contains(level.dimension().location());
    }

    /**
     * 查询当前是否正在播放 BGM
     *
     * @return 如果有 BGM 正在播放返回 true
     */
    public boolean isPlayingBgm() {
        return currentSound != null || fadingOutSound != null;
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
        this.switchCooldownTicks = Math.max(1, ticks);
    }

    // ==================== 核心 Tick 逻辑 ====================

    /**
     * 客户端每 tick 调用一次
     * <p>
     * 执行流程：
     * <ol>
     *   <li>检查玩家和世界状态，判断是否在自定义维度中</li>
     *   <li>如果正在进行交叉淡化 → 执行一步音量更新</li>
     *   <li>如果处于切换冷却期 → 冷却期间原 BGM 持续播放，
     *       冷却结束后才开始交叉淡化</li>
     *   <li>如果群系变化且未在冷却中 → 进入冷却期</li>
     *   <li>如果空闲且没有音乐 → 直接播放当前群系音乐</li>
     * </ol>
     */
    public void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        // 不在自定义维度中 → 停止所有音乐
        if (!isCustomDimension(mc.player.level())) {
            if (currentSound != null || fadingOutSound != null) {
                stopAllMusic();
            }
            return;
        }

        // 获取当前群系
        var biomeKeyOptional = mc.level.getBiome(mc.player.blockPosition()).unwrapKey();
        if (biomeKeyOptional.isEmpty()) return;
        ResourceLocation currentBiomeId = biomeKeyOptional.get().location();

        // 处理已经在进行中的交叉淡化步进
        if (fadeState == FadeState.FADING) {
            processCrossfadeStep();
            return;
        }

        String musicName = getMusicForBiome(currentBiomeId);
        long gameTick = mc.level.getGameTime();

        // ==================== 切换冷却期逻辑 ====================
        if (isInCooldown) {
            if (currentBiomeId.equals(pendingBiomeId)) {
                // 仍在目标群系中 → 检查冷却是否结束
                if (gameTick - cooldownStartTick >= switchCooldownTicks) {
                    // 冷却结束 → 开始交叉淡化
                    isInCooldown = false;
                    handleCrossfade(pendingMusicName);
                    pendingBiomeId = null;
                }
                // 冷却未结束 → 原 BGM 继续播放，不切换
            } else if (previousBiomeId != null && currentBiomeId.equals(previousBiomeId)) {
                // 回到了原群系 → 取消冷却
                isInCooldown = false;
                pendingBiomeId = null;
                PasterDreamMod.LOGGER.debug("[ModMusicManager] 切换冷却已取消，回到原群系");
            } else {
                // 又进入了另一个新群系 → 重置冷却
                pendingBiomeId = currentBiomeId;
                pendingMusicName = musicName;
                cooldownStartTick = gameTick;
                PasterDreamMod.LOGGER.debug("[ModMusicManager] 冷却期间进入新群系，冷却重置");
            }
            previousBiomeId = currentBiomeId;
            return;
        }

        // ==================== 群系变化检测 ====================
        boolean biomeChanged = previousBiomeId != null && !currentBiomeId.equals(previousBiomeId);
        previousBiomeId = currentBiomeId;

        if (biomeChanged) {
            if (musicName != null && musicName.equals(currentMusicName)) {
                // 音乐相同 → 不切换也不进入冷却
                return;
            }
            // 进入切换冷却期
            isInCooldown = true;
            pendingBiomeId = currentBiomeId;
            pendingMusicName = musicName;
            cooldownStartTick = gameTick;
            PasterDreamMod.LOGGER.debug("[ModMusicManager] 群系变化，进入切换冷却 ({} ticks)",
                    switchCooldownTicks);
            return;
        }

        // 空闲状态但没有播放音乐 → 直接播放（首次进入维度时触发）
        if (fadeState == FadeState.IDLE && currentSound == null
                && fadingOutSound == null && musicName != null) {
            startMusicDirect(musicName);
        }
    }

    /**
     * 获取群系对应的音乐名称
     *
     * @param biomeId 群系 ID
     * @return 音乐名称，无映射时返回 null
     */
    private String getMusicForBiome(ResourceLocation biomeId) {
        return BIOME_MUSIC_MAP.get(biomeId);
    }

    // ==================== 交叉淡化（Crossfade）核心 ====================

    /**
     * 触发交叉淡化
     * <p>
     * 由冷却系统在冷却结束后调用。会检查新音乐名称是否不等于当前音乐。
     *
     * @param newMusicName 目标音乐名称
     */
    private void handleCrossfade(String newMusicName) {
        // 相同音乐 → 不切换
        if (newMusicName != null && newMusicName.equals(currentMusicName)) {
            return;
        }

        // 没有旧音乐播放 → 直接淡入新音乐
        if (currentSound == null) {
            startMusicDirect(newMusicName);
            return;
        }

        // 新音乐为 null → 停止当前音乐
        if (newMusicName == null) {
            stopAllMusic();
            return;
        }

        // 开始交叉淡化
        fadingOutSound = currentSound;
        fadingOutMusicName = currentMusicName;
        crossfadeTargetMusicName = newMusicName;
        currentSound = null;
        currentMusicName = null;

        fadeState = FadeState.FADING;
        crossfadeStep = 0;

        PasterDreamMod.LOGGER.debug("[ModMusicManager] 开始交叉淡化: {} → {}",
                fadingOutMusicName, newMusicName);
    }

    /**
     * 执行一步交叉淡化
     * <p>
     * 每步同时更新旧音乐和新音乐的音量：
     * <ul>
     *   <li>旧音乐音量 = TARGET_VOLUME * (剩余步数 / 总步数)</li>
     *   <li>新音乐音量 = TARGET_VOLUME * (当前步数 / 总步数)</li>
     * </ul>
     */
    private void processCrossfadeStep() {
        if (crossfadeStep >= CROSSFADE_STEPS) {
            // 交叉淡化完成
            if (fadingOutSound != null) {
                Minecraft.getInstance().getSoundManager().stop(fadingOutSound);
            }
            fadingOutSound = null;
            fadingOutMusicName = null;
            currentMusicName = crossfadeTargetMusicName;
            crossfadeTargetMusicName = null;
            fadeState = FadeState.IDLE;
            PasterDreamMod.LOGGER.debug("[ModMusicManager] 交叉淡化完成: {}", currentMusicName);
            return;
        }

        crossfadeStep++;
        float progress = (float) crossfadeStep / CROSSFADE_STEPS;

        // 旧音乐音量递减
        float oldVolume = TARGET_VOLUME * (1.0f - progress);
        if (oldVolume > 0.001f && fadingOutMusicName != null) {
            recreateFadingSound(fadingOutMusicName, oldVolume);
        } else {
            if (fadingOutSound != null) {
                Minecraft.getInstance().getSoundManager().stop(fadingOutSound);
                fadingOutSound = null;
            }
        }

        // 新音乐音量递增
        float newVolume = TARGET_VOLUME * progress;
        recreateCurrentSound(crossfadeTargetMusicName, newVolume);
    }

    /**
     * 直接播放音乐（无过渡）
     */
    private void startMusicDirect(String musicName) {
        if (musicName == null) return;
        stopAllMusic();
        currentMusicName = musicName;
        fadeState = FadeState.FADING;
        crossfadeStep = CROSSFADE_STEPS;
        recreateCurrentSound(musicName, TARGET_VOLUME);
        fadeState = FadeState.IDLE;
        PasterDreamMod.LOGGER.debug("[ModMusicManager] 直接播放: music.{}", musicName);
    }

    // ==================== 声音实例管理 ====================

    /**
     * 重新创建正在淡出的旧音乐声音实例
     */
    private void recreateFadingSound(String musicName, float volume) {
        SoundEvent soundEvent = lookupSoundEvent(musicName);
        if (soundEvent == null) {
            if (fadingOutSound != null) {
                Minecraft.getInstance().getSoundManager().stop(fadingOutSound);
                fadingOutSound = null;
            }
            return;
        }

        if (fadingOutSound != null) {
            Minecraft.getInstance().getSoundManager().stop(fadingOutSound);
        }

        fadingOutSound = VolumeSoundInstance.forMusic(soundEvent, volume);
        Minecraft.getInstance().getSoundManager().play(fadingOutSound);
    }

    /**
     * 重新创建当前音乐声音实例
     */
    private void recreateCurrentSound(String musicName, float volume) {
        SoundEvent soundEvent = lookupSoundEvent(musicName);
        if (soundEvent == null) return;

        if (currentSound != null) {
            Minecraft.getInstance().getSoundManager().stop(currentSound);
        }

        currentMusicName = musicName;
        currentSound = VolumeSoundInstance.forMusic(soundEvent, volume);
        Minecraft.getInstance().getSoundManager().play(currentSound);
    }

    /**
     * 查找 SoundEvent
     *
     * @param musicName 音乐名称
     * @return SoundEvent，未找到时返回 null
     */
    private SoundEvent lookupSoundEvent(String musicName) {
        ResourceLocation soundId = ResourceLocation.fromNamespaceAndPath(
                PasterDreamMod.MOD_ID, "music." + musicName);
        SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.get(soundId);
        if (soundEvent == null) {
            PasterDreamMod.LOGGER.warn("[ModMusicManager] 未找到声音事件: {}", soundId);
        }
        return soundEvent;
    }

    /**
     * 停止所有音乐并重置冷却状态
     */
    private void stopAllMusic() {
        if (currentSound != null) {
            Minecraft.getInstance().getSoundManager().stop(currentSound);
            currentSound = null;
        }
        if (fadingOutSound != null) {
            Minecraft.getInstance().getSoundManager().stop(fadingOutSound);
            fadingOutSound = null;
        }
        currentMusicName = null;
        fadingOutMusicName = null;
        crossfadeTargetMusicName = null;
        fadeState = FadeState.IDLE;
        isInCooldown = false;
        pendingBiomeId = null;
        pendingMusicName = null;
    }
}
