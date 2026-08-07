package com.pasterdream.pasterdreammod.api.client.effect.screen.instances;

import com.mojang.blaze3d.systems.RenderSystem;
import com.pasterdream.pasterdreammod.api.client.effect.screen.ScreenEffect;
import com.pasterdream.pasterdreammod.api.effect.screen.instances.ScreenColorData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 纯色屏幕特效 —— 全屏填充单一颜色（带 in/stay/out 渐入渐出）
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code ScreenColorEffect} 设计思路
 * （独立实现，非复制）。alpha 随生命周期阶段变化：渐入 0→1、持续 1、渐出 1→0。
 * <p>
 * 本类为客户端专用，仅由 {@code api/client/**} 路径持有；服务端经
 * {@code ScreenColorData.TYPE}（通用元数据）发送，不引用本类。
 */
@OnlyIn(Dist.CLIENT)
public class ScreenColorEffect extends ScreenEffect<ScreenColorData> {

    /**
     * 构造纯色特效
     *
     * @param data     颜色数据
     * @param inTime   渐入 tick 数
     * @param stayTime 持续 tick 数
     * @param outTime  渐出 tick 数
     */
    public ScreenColorEffect(ScreenColorData data, int inTime, int stayTime, int outTime) {
        super(data, inTime, stayTime, outTime);
    }

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker,
                       int currentTick, float screenWidth, float screenHeight) {
        float pticks = deltaTracker.getGameTimeDeltaPartialTick(false);
        int argb = getData().argb();
        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;

        float alpha;
        if (isInTime(currentTick)) {
            alpha = getInTimePercent(currentTick, pticks);
        } else if (isStayTime(currentTick)) {
            alpha = 1.0f;
        } else if (isOutTime(currentTick)) {
            alpha = 1.0f - getOutTimePercent(currentTick, pticks);
        } else {
            alpha = 0.0f;
        }

        if (alpha <= 0.0001f) {
            return;
        }

        // GUI 空间全屏填充
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        graphics.fill(0, 0, (int) screenWidth, (int) screenHeight,
                (int) (a * alpha) << 24 | (r << 16) | (g << 8) | b);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }
}
