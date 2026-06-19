package com.pasterdream.pasterdreammod.mixin;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.client.PDClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.Music;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;
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

    /**
     * 在 getSituationalMusic 方法开始处注入
     * <p>
     * 决策逻辑：
     * <ol>
     *   <li>如果在 ModMusicManager 管理的自定义维度中 → 返回 null（交给 ModMusicManager）</li>
     *   <li>如果在原版维度且玩家处于创造/旁观模式 → 返回群系BGM（原逻辑）</li>
     * </ol>
     */
    @Inject(method = "getSituationalMusic", at = @At("HEAD"), cancellable = true)
    private void pasterdream$overrideCreativeMusic(CallbackInfoReturnable<Music> cir) {
        Minecraft self = (Minecraft) (Object) this;
        LocalPlayer player = self.player;
        if (player == null || player.level() == null) return;

        // 在 ModMusicManager 管理的自定义维度中，返回 null 让其全权处理
        if (PDClientEvents.getBiomeMusicRegistry().isCustomDimension(player.level())) {
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
