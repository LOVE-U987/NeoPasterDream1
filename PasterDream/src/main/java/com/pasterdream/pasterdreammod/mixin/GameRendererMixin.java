package com.pasterdream.pasterdreammod.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.pasterdream.pasterdreammod.api.client.effect.post.PostShaderEvent;
import com.pasterdream.pasterdreammod.api.client.effect.shake.ScreenShakeHandler;
import com.pasterdream.pasterdreammod.client.effect.PDEffectClientEvents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * GameRenderer 混合注入 —— 分发自定义后处理事件
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code GameRendererMixin} 设计思路
 * （独立实现，非复制），在 {@code GameRenderer#render} 的两个时机发布
 * {@link PostShaderEvent}：
 * <ul>
 *   <li><b>Level</b>：首次 {@code RenderTarget#bindWrite} 之前 —— 世界帧已渲染、
 *       即将写入主目标，适合世界级后处理（如打击帧灰闪）；</li>
 *   <li><b>Screen</b>：{@code render} 尾部 —— 整帧（含 GUI）合成后，
 *       适合全屏屏幕特效后处理（如色差）。</li>
 * </ul>
 * <p>
 * 事件经 {@link NeoForge#EVENT_BUS} 发布，由主模
 * {@link PDEffectClientEvents} 订阅并转发到 API 的
 * {@code PostShaderManager}；附属模组亦可订阅追加自己的后处理链。
 *
 * @see PostShaderEvent
 * @see PDEffectClientEvents
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    /**
     * 世界帧级后处理事件注入点
     * <p>
     * 注入在 {@code render} 方法首次调用 {@code RenderTarget#bindWrite(Z)V}
     * 之前。1.21.1 的 {@code GameRenderer.render} 会在世界帧渲染后绑定主
     * 渲染目标进行合成，此处即为世界帧后处理的挂载时机。
     *
     * @param deltaTracker 帧时间跟踪器
     * @param renderLevel  是否渲染世界层
     * @param ci           回调信息
     */
    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;bindWrite(Z)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void pasterdream$postShaderLevel(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new PostShaderEvent.Level(deltaTracker));
    }

    /**
     * 整帧级后处理事件注入点
     *
     * @param deltaTracker 帧时间跟踪器
     * @param renderLevel  是否渲染世界层
     * @param ci           回调信息
     */
    @Inject(method = "render", at = @At("TAIL"))
    private void pasterdream$postShaderScreen(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new PostShaderEvent.Screen(deltaTracker));
    }

    /**
     * 屏幕晃动注入点
     * <p>
     * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code GameRendererMixin.bobHurt}
     * 设计思路（独立实现，非复制）：原版 {@code GameRenderer.bobHurt} 是受击
     * 视角晃动，注入其 HEAD 把 {@link ScreenShakeHandler} 的随机偏移 translate
     * 进同一 PoseStack，实现屏幕晃动。
     *
     * @param matrices 相机震动矩阵
     * @param pticks   部分 tick
     * @param ci       回调信息
     */
    @Inject(method = "bobHurt", at = @At("HEAD"))
    private void pasterdream$screenShake(PoseStack matrices, float pticks, CallbackInfo ci) {
        ScreenShakeHandler.processShakes(matrices, pticks);
    }
}
