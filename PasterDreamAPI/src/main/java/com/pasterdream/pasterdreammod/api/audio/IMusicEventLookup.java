package com.pasterdream.pasterdreammod.api.audio;

import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

/**
 * 音乐名 → {@link SoundEvent} 查找契约。
 * <p>
 * 实现可硬编码 modid 前缀（如 {@code pasterdream:music.<name>}）或走自定义注册表。
 * 无客户端类型依赖，API 可安全引用。
 */
@FunctionalInterface
public interface IMusicEventLookup {

    /**
     * @param musicName 逻辑音乐名（非完整 ResourceLocation）
     * @return SoundEvent；未找到时返回 null
     */
    @Nullable
    SoundEvent lookup(String musicName);
}
