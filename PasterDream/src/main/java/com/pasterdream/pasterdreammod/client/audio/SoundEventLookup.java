package com.pasterdream.pasterdreammod.client.audio;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

/**
 * SoundEvent 查找工具 —— 根据音乐名称查找对应的 SoundEvent
 * <p>
 * 职责：
 * <ul>
 *   <li>封装音乐名称到 SoundEvent 的查找逻辑</li>
 *   <li>统一处理查找失败时的日志记录</li>
 * </ul>
 * <p>
 * 命名约定：音乐名称对应的 SoundEvent 注册名为 {@code <modid>:music.<musicName>}。
 */
public class SoundEventLookup {

    /**
     * 根据音乐名称查找 SoundEvent
     * <p>
     * 查找规则：{@code <modid>:music.<musicName>}
     *
     * @param musicName 音乐注册名称（如 "dream_meadow"）
     * @return SoundEvent，未找到时返回 null
     */
    public SoundEvent lookup(String musicName) {
        ResourceLocation soundId = ResourceLocation.fromNamespaceAndPath(
                PasterDreamMod.MOD_ID, "music." + musicName);
        SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.get(soundId);
        if (soundEvent == null) {
            PasterDreamMod.LOGGER.warn("[SoundEventLookup] 未找到声音事件: {}", soundId);
        }
        return soundEvent;
    }
}