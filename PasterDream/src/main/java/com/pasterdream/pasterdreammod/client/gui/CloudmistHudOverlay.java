package com.pasterdream.pasterdreammod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.config.PDClientConfig;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/**
 * 云雾浓度全屏 HUD 叠加层
 * <p>
 * 移植自原版 {@code CloudmistHud.java}（IGuiOverlay → 1.21.1 {@link LayeredDraw.Layer}），
 * 层名沿用原版 {@code couldmist_hud}（含原版拼写）：以云雾浓度百分比为透明度，
 * 全屏渐显纹理 {@code textures/screens/cloudmist_hud.png}，浓度 ≤ 1% 时不绘制。
 * <p>
 * 数据来源差异说明：原版效果 tick 在双端执行、云雾百分比写入客户端本地 NBT
 * {@code cloudmist_percent} 供 HUD 读取；1.21.1 移植版的效果 tick
 * （{@code PDEffects#cloudmistTick / fondillusionTick}）仅在服务端执行，
 * 该 NBT 不会同步客户端。故此处按服务端同一公式在客户端本地重算（效果状态
 * 与坐标客户端均有权威副本），数值与原版逐项一致：
 * <ul>
 *   <li>cloudmist_buff（云雾）+ 风之旅途维度：0 &lt; y ≤ 50 时百分比 = (50 - y) × 2</li>
 *   <li>fondillusion_buff（迷梦）+ 主世界：260 &lt; y ≤ 310 时百分比 = (y - 260) × 2</li>
 *   <li>其余情况为 0</li>
 * </ul>
 * 原版渲染状态（关深度测试、关深度写入、默认混合、z=-90 全屏四边形）等价保留。
 */
public class CloudmistHudOverlay implements LayeredDraw.Layer {

    /** 云雾全屏纹理（自原版 assets 复制） */
    public static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(
            PasterDreamMod.MOD_ID, "textures/screens/cloudmist_hud.png");

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.options.hideGui) {
            return;
        }
        // 骑乘生物时不显示（与原版一致）
        if (player.getVehicle() instanceof LivingEntity) {
            return;
        }

        mc.getProfiler().push("couldmist_hud");
        double mind = computeCloudmistPercent(player) / 100.0D;

        if (mind > 0.01D) {
            int width = guiGraphics.guiWidth();
            int height = guiGraphics.guiHeight();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            // 关闭深度测试与深度写入（与原版一致）：全屏渐显必须覆盖已绘制的 HUD 元素
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, (float) mind);
            // 全屏拉伸绘制（z=-90，与原版顶点深度一致）
            guiGraphics.blit(ICON, 0, 0, -90, 0.0F, 0.0F, width, height, width, height);
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
        }
        mc.getProfiler().pop();
    }

    /**
     * 客户端重算云雾浓度百分比（公式与服务端 PDEffects 的
     * cloudmistTick / fondillusionTick 写入 {@code cloudmist_percent} 的值一致）
     *
     * @param player 本地玩家
     * @return 云雾浓度百分比（0 ~ 100）
     */
    private static double computeCloudmistPercent(LocalPlayer player) {
        double y = player.getY();
        // 云雾（风之旅途维度坠落浓度：越低越浓）
        if (player.hasEffect(PDEffects.CLOUDMIST_BUFF.holder())
                && player.level().dimension().equals(PDDimensions.WIND_JOURNEY_WORLD_LEVEL_KEY)) {
            return (y > 0 && y <= 50) ? (50 - y) * 2 : 0;
        }
        // 迷梦（主世界高空浓度：越高越浓）
        if (player.hasEffect(PDEffects.FONDILLUSION_BUFF.holder())
                && player.level().dimension() == Level.OVERWORLD) {
            return (y > 260 && y <= 310) ? (y - 260) * 2 : 0;
        }
        return 0;
    }
}
