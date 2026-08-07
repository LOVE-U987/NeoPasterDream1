package com.pasterdream.pasterdreammod.api.client.effect.screen;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 屏幕特效叠加层 —— 渲染所有活跃屏幕特效并管理其生命周期
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code ScreenEffectOverlay} 设计思路
 * （独立实现，非复制）：
 * <ul>
 *   <li>实现 {@link LayeredDraw.Layer}，由主模 {@code PDHudLayers} 注册为 GUI 层；</li>
 *   <li>静态活跃列表持有全部 {@link ScreenEffectInstance}；</li>
 *   <li>{@link #tick()} 由主模 {@code PDEffectClientEvents} 在客户端 tick 驱动；</li>
 *   <li>网络包落地经 {@link #add(ScreenEffect)} 加入新特效。</li>
 * </ul>
 * 本类为客户端专用（引用 {@link Minecraft}、{@link GuiGraphics}），仅由
 * {@code api/client/**} 路径持有。
 */
@OnlyIn(Dist.CLIENT)
public class ScreenEffectOverlay implements LayeredDraw.Layer {

    private static final List<ScreenEffectInstance> ACTIVE = new ArrayList<>();

    /**
     * 加入一个新屏幕特效
     *
     * @param effect 特效
     */
    public static void add(ScreenEffect<?> effect) {
        ACTIVE.add(new ScreenEffectInstance(effect));
    }

    /**
     * 客户端每 tick 推进全部活跃特效（由 PDEffectClientEvents 调用）
     */
    public static void tick() {
        if (Minecraft.getInstance().isPaused()) {
            return;
        }
        Iterator<ScreenEffectInstance> iterator = ACTIVE.iterator();
        while (iterator.hasNext()) {
            ScreenEffectInstance instance = iterator.next();
            if (instance.isFinished()) {
                iterator.remove();
            } else {
                instance.tick();
            }
        }
    }

    /**
     * 玩家登出/世界卸载时清空
     */
    public static void clearAll() {
        ACTIVE.clear();
    }

    /**
     * 是否有活跃屏幕特效
     *
     * @return 有返回 {@code true}
     */
    public static boolean isActive() {
        return !ACTIVE.isEmpty();
    }

    /**
     * 测试辅助：清空活跃列表
     */
    public static void resetForTesting() {
        ACTIVE.clear();
    }

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Window window = Minecraft.getInstance().getWindow();
        float width = window.getGuiScaledWidth();
        float height = window.getGuiScaledHeight();
        for (ScreenEffectInstance instance : ACTIVE) {
            instance.getEffect().render(graphics, deltaTracker, instance.getCurrentTime(), width, height);
        }
    }
}
