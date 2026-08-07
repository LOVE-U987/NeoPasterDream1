package com.pasterdream.pasterdreammod.mixin;

import com.pasterdream.pasterdreammod.api.client.effect.cutscene.CutsceneCameraHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * LocalPlayer 混合注入 —— 过场时声明为受控相机
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code LocalPlayerMixin} 设计思路
 * （独立实现，非复制）。过场激活时让 {@code isControlledCamera} 返回
 * {@code true}，使相机实体接管旋转控制。
 */
@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    /**
     * 过场时 isControlledCamera 返回 true
     *
     * @param cir 返回值回调
     */
    @Inject(method = "isControlledCamera", at = @At("HEAD"), cancellable = true)
    private void pasterdream$isControlledCamera(CallbackInfoReturnable<Boolean> cir) {
        if (CutsceneCameraHandler.isActive()) {
            cir.setReturnValue(true);
        }
    }
}
