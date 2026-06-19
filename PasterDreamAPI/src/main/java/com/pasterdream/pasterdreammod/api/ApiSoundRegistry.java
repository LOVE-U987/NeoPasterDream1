package com.pasterdream.pasterdreammod.api;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * API 层的声音事件注册器。
 * 提供维度背景音乐的动态注册能力，供 DimensionBuilder / DimensionAPI 使用。
 * <p>
 * 主模组需在 {@code PasterDreamMod} 构造器中注册：
 * <pre>{@code
 * ApiSoundRegistry.DIMENSION_SOUNDS.register(modEventBus);
 * }</pre>
 */
public final class ApiSoundRegistry {

    private ApiSoundRegistry() {
        throw new UnsupportedOperationException("ApiSoundRegistry 是不可实例化的注册类");
    }

    /** API 专属的 SoundEvent 注册器 */
    public static final DeferredRegister<SoundEvent> DIMENSION_SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, PasterDreamAPI.MOD_ID);

    /** 缓存已注册的维度音乐事件 */
    private static final Map<String, Supplier<SoundEvent>> DIMENSION_MUSIC_CACHE = new HashMap<>();

    static {
        registerDimensionMusic("dyedream_world");
        registerDimensionMusic("dream_meadow");
        registerDimensionMusic("dream_heath");
        registerDimensionMusic("dream_taiga");
        registerDimensionMusic("dream_delta");
        registerDimensionMusic("sweetdream_music");
        registerDimensionMusic("snowfall_dream_music");
    }

    /**
     * 注册一个维度背景音乐 SoundEvent。
     * ID 格式为 {@code music.{musicName}}，声音文件对应
     * {@code assets/pasterdream/sounds/music/{musicName}.ogg}。
     * <p>
     * 注册前会检查对应 .ogg 文件是否存在于类路径中；若缺失则跳过注册并记录警告。
     *
     * @param musicName 音乐名称（如 "dyedream_world"）
     * @return 已注册的 SoundEvent Supplier；若文件缺失则返回 {@code null}
     */
    public static synchronized Supplier<SoundEvent> registerDimensionMusic(String musicName) {
        if (DIMENSION_MUSIC_CACHE.containsKey(musicName)) {
            return DIMENSION_MUSIC_CACHE.get(musicName);
        }

        String resourcePath = "/assets/" + PasterDreamAPI.MOD_ID + "/sounds/music/" + musicName + ".ogg";
        if (ApiSoundRegistry.class.getResource(resourcePath) == null) {
            PasterDreamAPI.LOGGER.warn("[ApiSoundRegistry] 背景音乐文件缺失: assets/{}/sounds/music/{}.ogg，跳过注册",
                    PasterDreamAPI.MOD_ID, musicName);
            DIMENSION_MUSIC_CACHE.put(musicName, null);
            return null;
        }

        String soundId = "music." + musicName;
        Supplier<SoundEvent> supplier = DIMENSION_SOUNDS.register(soundId,
                () -> SoundEvent.createVariableRangeEvent(
                        ResourceLocation.fromNamespaceAndPath(PasterDreamAPI.MOD_ID, soundId)
                ));
        DIMENSION_MUSIC_CACHE.put(musicName, supplier);
        PasterDreamAPI.LOGGER.info("[ApiSoundRegistry] 已注册背景音乐 SoundEvent: {} (assets/{}/sounds/music/{}.ogg)",
                soundId, PasterDreamAPI.MOD_ID, musicName);
        return supplier;
    }

    public static Supplier<SoundEvent> getDimensionMusic(String musicName) {
        return DIMENSION_MUSIC_CACHE.get(musicName);
    }
}
