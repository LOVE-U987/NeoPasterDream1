package com.pasterdream.pasterdreammod.mixin;

import com.pasterdream.pasterdreammod.api.client.effect.cutscene.CutsceneCameraHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * KeyboardInput 混合注入 —— 过场时清空移动输入
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code KeyboardInputMixin} 设计思路
 * （独立实现，非复制）。过场激活时取消输入 tick 并清空玩家输入，避免
 * 玩家移动/跳跃打断过场。
 */
@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {

    /**
     * 过场时取消移动输入
     *
     * @param slowDown 潜行降速标志
     * @param slowDown2 浮空降速标志
     * @param ci        回调信息
     */
    @Inject(method = "tick", at = @At("TAIL"), cancellable = true)
    private void pasterdream$clearInput(boolean slowDown, float slowDown2, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (CutsceneCameraHandler.isActive() && player != null) {
            CutsceneCameraHandler.nullifyInput(player);
            ci.cancel();
        }
    }
}
