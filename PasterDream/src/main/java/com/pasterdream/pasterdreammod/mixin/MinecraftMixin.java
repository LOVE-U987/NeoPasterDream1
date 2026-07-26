package com.pasterdream.pasterdreammod.mixin;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.client.PDClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Music;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Minecraft 类混合注入
 * <p>
 * 修改背景音乐选择逻辑，与 {@link com.pasterdream.pasterdreammod.client.audio.ModMusicManager} 协同工作：
 * <ul>
 *   <li>在 ModMusicManager 管理的自定义维度中 → 直接返回 null，由 ModMusicManager 全权管理 BGM</li>
 *   <li>在原版维度且玩家处于创造/旁观模式 → 返回群系BGM（当 replace_current_music=true）</li>
 * </ul>
 * <p>
 * 防止 {@link net.minecraft.client.sounds.MusicManager} 与 ModMusicManager 同时播放BGM，
 * 避免"双倍BGM"问题。
 */
@Mixin(Minecraft.class)
public class MinecraftMixin {

    /** 缓存的维度 Key —— 避免每 tick 重复执行完整的自定义维度注册表判断 */
    @Unique
    private ResourceKey<Level> pasterdream$cachedDimension;

    /** 缓存的自定义维度判断结果（与 pasterdream$cachedDimension 配套，维度变化时刷新） */
    @Unique
    private boolean pasterdream$cachedIsCustomDimension;

    /**
     * 在 getSituationalMusic 方法开始处注入
     * <p>
     * 决策逻辑：
     * <ol>
     *   <li>客户端未完全初始化（player/level 未就绪）→ 直接返回，不访问注册表</li>
     *   <li>如果在 ModMusicManager 管理的自定义维度中 → 返回 null（交给 ModMusicManager）</li>
     *   <li>如果在原版维度且玩家处于创造/旁观模式 → 返回群系BGM（原逻辑）</li>
     * </ol>
     * 自定义维度判断结果按维度 Key 缓存，仅在维度变化时重新查询。
     */
    @Inject(method = "getSituationalMusic", at = @At("HEAD"), cancellable = true)
    private void pasterdream$overrideCreativeMusic(CallbackInfoReturnable<Music> cir) {
        Minecraft self = (Minecraft) (Object) this;
        LocalPlayer player = self.player;
        // 客户端未完全初始化（主菜单、维度切换过渡等）时提前返回，避免过早触发音频系统初始化
        if (player == null || self.level == null || player.level() == null) return;

        // 按维度 Key 缓存 isCustomDimension 判断结果，维度变化时自动失效
        ResourceKey<Level> dimension = player.level().dimension();
        if (!dimension.equals(pasterdream$cachedDimension)) {
            pasterdream$cachedIsCustomDimension =
                    PDClientEvents.getBiomeMusicRegistry().isCustomDimension(player.level());
            pasterdream$cachedDimension = dimension;
        }

        // 在 ModMusicManager 管理的自定义维度中，返回 null 让其全权处理
        if (pasterdream$cachedIsCustomDimension) {
            cir.setReturnValue(null);
            return;
        }

        // 原版维度：创造/旁观模式时优先返回群系BGM（原逻辑）
        if (player.isCreative() || player.isSpectator()) {
            Holder<Biome> biomeHolder = player.level().getBiome(player.blockPosition());
            biomeHolder.value().getBackgroundMusic().ifPresent(music -> {
                if (music.replaceCurrentMusic()) {
                    PasterDreamMod.LOGGER.debug("[MixinMusic] 使用群系BGM替代创造模式音乐: {}",
                            music.getEvent().value().getLocation());
                    cir.setReturnValue(music);
                }
            });
        }
    }
}
