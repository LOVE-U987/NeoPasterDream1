package com.pasterdream.pasterdreammod.client.audio;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.audio.IMusicEventLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

/**
 * PD 主模 {@link IMusicEventLookup}：{@code pasterdream:music.&lt;name&gt;}。
 */
public class SoundEventLookup implements IMusicEventLookup {

    /**
     * 根据音乐名称查找 SoundEvent。
     * <p>
     * 查找规则：{@code <modid>:music.<musicName>}
     *
     * @param musicName 音乐注册名称（如 "dream_meadow"）
     * @return SoundEvent，未找到时返回 null
     */
    @Override
    @Nullable
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
