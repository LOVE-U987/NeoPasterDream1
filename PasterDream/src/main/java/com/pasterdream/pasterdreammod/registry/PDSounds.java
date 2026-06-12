package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 声音事件注册类 —— 管理模组中所有自定义声音、背景音乐的注册
 * <p>
 * 通过 {@link DeferredRegister} 注册所有 {@link SoundEvent}，
 * 并提供便捷的维度背景音乐注册方法。
 * <p>
 * 注意：注册 SoundEvent 后还需要在 {@code sounds.json} 中声明对应的声音条目，
 * 详见 {@link com.pasterdream.pasterdreammod.api.dimension.gen.SoundsJsonGenerator}。
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 在 PasterDreamMod 构造函数中：
 * PDSounds.SOUND_EVENTS.register(modEventBus);
 *
 * // 获取已注册的音乐事件：
 * Supplier<SoundEvent> music = PDSounds.getDimensionMusic("dyedream_world");
 * }</pre>
 */
public class PDSounds {

    private PDSounds() {
        throw new UnsupportedOperationException("PDSounds 是不可实例化的注册类");
    }

    /**
     * 声音事件延迟注册器
     */
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, PasterDreamMod.MOD_ID);

    /** 缓存已注册的维度音乐事件 */
    private static final Map<String, Supplier<SoundEvent>> DIMENSION_MUSIC_CACHE = new HashMap<>();

    // ==================== 融梦水晶箱 SoundEvent ====================

    /**
     * 融梦水晶箱 —— 普通/稀有品质音效
     */
    public static final Supplier<SoundEvent> MELTDREAM_CHEST_0 = SOUND_EVENTS.register("meltdream_chest_0",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "meltdream_chest_0")));

    /**
     * 融梦水晶箱 —— 传说品质音效
     */
    public static final Supplier<SoundEvent> MELTDREAM_CHEST = SOUND_EVENTS.register("meltdream_chest",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "meltdream_chest")));

    // ==================== 唱片音乐 SoundEvent ====================

    /**
     * 甜蜜的梦 唱片音乐 SoundEvent
     */
    public static final Supplier<SoundEvent> SWEETDREAM_MUSIC = SOUND_EVENTS.register("sweetdream",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "sweetdream_music")));

    /**
     * 落雪之梦 唱片音乐 SoundEvent
     */
    public static final Supplier<SoundEvent> SNOWFALL_DREAM_MUSIC = SOUND_EVENTS.register("snowfall_dream_music",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "snowfall_dream_music")));

    /**
     * 亚伦柯斯之触 唱片音乐 SoundEvent
     */
    public static final Supplier<SoundEvent> AARONCOS_MUSIC = SOUND_EVENTS.register("aaroncos_music",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "aaroncos_music")));

    /**
     * 风之旅途 唱片音乐 SoundEvent
     */
    public static final Supplier<SoundEvent> WIND_JOURNEY_MUSIC = SOUND_EVENTS.register("wind_journey",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "wind_journey")));

    // ==================== 染梦群系背景音乐 唱片 SoundEvent ====================

    /**
     * 梦幻草原 唱片音乐 SoundEvent
     */
    public static final Supplier<SoundEvent> DREAM_MEADOW_MUSIC = SOUND_EVENTS.register("dream_meadow_music",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "music/dream_meadow")));

    /**
     * 梦幻荒原 唱片音乐 SoundEvent
     */
    public static final Supplier<SoundEvent> DREAM_HEATH_MUSIC = SOUND_EVENTS.register("dream_heath_music",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "music/dream_heath")));

    /**
     * 梦幻雪林 唱片音乐 SoundEvent
     */
    public static final Supplier<SoundEvent> DREAM_TAIGA_MUSIC = SOUND_EVENTS.register("dream_taiga_music",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "music/dream_taiga")));

    /**
     * 梦幻三角洲 唱片音乐 SoundEvent
     */
    public static final Supplier<SoundEvent> DREAM_DELTA_MUSIC = SOUND_EVENTS.register("dream_delta_music",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "music/dream_delta")));

    /**
     * 静态初始化：在注册阶段前预注册所有已知维度的背景音乐
     * <p>
     * 防止 {@link DimensionBuilder#build()} 在运行时被延迟触发（如通过 {@code PDDimensions} 的静态块懒加载）
     * 时调用 {@link #registerDimensionMusic}，而此时 RegisterEvent 已过、DeferredRegister 已锁定无法注册新条目。
     */
    static {
        // === 预注册所有已知维度的背景音乐 ===
        registerDimensionMusic("dyedream_world");
        // === 预注册染梦维度各群系的自定义背景音乐（用于群系BGM关联） ===
        registerDimensionMusic("dream_meadow");
        registerDimensionMusic("dream_heath");
        registerDimensionMusic("dream_taiga");
        registerDimensionMusic("dream_delta");
        registerDimensionMusic("sweetdream_music");
        registerDimensionMusic("snowfall_dream_music");
    }

    /**
     * 注册一个维度背景音乐 SoundEvent
     * <p>
     * 注册 ID 格式为 {@code music.{musicName}}（遵循 Minecraft 原版惯例，
     * 如 {@code music.dyedream_world}），声音文件对应路径为
     * {@code assets/pasterdream/sounds/music/{musicName}.ogg}。
     *
     * @param musicName 音乐名称（如 "dyedream_world"）
     * @return 已注册的 SoundEvent Supplier
     */
    public static synchronized Supplier<SoundEvent> registerDimensionMusic(String musicName) {
        // 如果已缓存，直接返回
        Supplier<SoundEvent> cached = DIMENSION_MUSIC_CACHE.get(musicName);
        if (cached != null) {
            return cached;
        }

        String soundId = "music." + musicName;
        Supplier<SoundEvent> supplier = SOUND_EVENTS.register(soundId,
                () -> SoundEvent.createVariableRangeEvent(
                        ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, soundId)
                ));

        DIMENSION_MUSIC_CACHE.put(musicName, supplier);
        PasterDreamMod.LOGGER.info("[PDSounds] ✅ 已注册背景音乐 SoundEvent: {} (assets/{}/sounds/music/{}.ogg)",
                soundId, PasterDreamMod.MOD_ID, musicName);
        return supplier;
    }

    /**
     * 获取已注册的维度背景音乐
     *
     * @param musicName 音乐名称（与注册时一致）
     * @return SoundEvent Supplier，如果未注册返回 null
     */
    public static Supplier<SoundEvent> getDimensionMusic(String musicName) {
        return DIMENSION_MUSIC_CACHE.get(musicName);
    }
}