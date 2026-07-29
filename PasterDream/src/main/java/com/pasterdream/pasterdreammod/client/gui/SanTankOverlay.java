package com.pasterdream.pasterdreammod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.attachment.PDAttachments;
import com.pasterdream.pasterdreammod.config.PDClientConfig;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import com.pasterdream.pasterdreammod.registry.PDAttributes;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

/**
 * San 精神值 HUD 叠加层
 * <p>
 * 移植自原版 {@code SanTank.java}（IGuiOverlay → 1.21.1 {@link LayeredDraw.Layer}），
 * 纹理 {@code textures/screens/pasterdream_hud.png}、绘制坐标与 UV 参数与原版一致：
 * <ul>
 *   <li>锚点：由 {@link PDClientConfig#SAN_HUD_ANCHOR} 控制（左上/右上/左下/右下）</li>
 *   <li>偏移：xBase / yBase 为相对锚点的偏移量</li>
 *   <li>缩放：由 {@link PDClientConfig#SAN_HUD_SCALE} 控制（0.5 ~ 2.0）</li>
 *   <li>图标 32x32（UV 0,32），其上以 UV(0,70) 的遮罩自顶向下覆盖表示消耗量</li>
 *   <li>按属性 {@code san_variability} 的正负绘制升/降箭头（|值|&lt;5 时绘制小号箭头）</li>
 * </ul>
 * 显示条件：配置开启、未骑乘生物、未隐藏 GUI、理智系统开启、且（若开启潜行显示配置）处于潜行。
 */
public class SanTankOverlay implements LayeredDraw.Layer {

    /** HUD 纹理（与原版同名，已随资源迁移） */
    public static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(
            PasterDreamMod.MOD_ID, "textures/screens/pasterdream_hud.png");

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.options.hideGui) {
            return;
        }
        // 系统关闭或 HUD 关闭时不显示
        if (!Boolean.TRUE.equals(PDCommonConfig.ENABLE_SAN_SYSTEM.get())
                || !PDClientConfig.SHOW_SAN_HUD.get()) {
            return;
        }
        // 骑乘生物时不显示（与原版一致）
        if (player.getVehicle() instanceof LivingEntity) {
            return;
        }
        // 理智系统关闭时不显示（客户端读附件镜像开关，对应原版 IsSanCheckSystem 客户端分支）
        if (!player.getData(PDAttachments.PLAYER_SAN).sanCheck()) {
            return;
        }
        // 配置为"仅潜行显示"且未潜行时不显示
        if (PDClientConfig.STEALTH_DISPLAY_ATTRIBUTE_HUD.get() && !player.isShiftKeyDown()) {
            return;
        }

        mc.getProfiler().push("san_bar");
        RenderSystem.enableBlend();

        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();
        int anchor = PDClientConfig.SAN_HUD_ANCHOR.get();
        int xBase = PDClientConfig.SAN_TANK_XBASE.get().intValue();
        int yBase = PDClientConfig.SAN_TANK_YBASE.get().intValue();
        float scale = PDClientConfig.SAN_HUD_SCALE.get().floatValue();

        int screenX = computeX(anchor, xBase, width);
        int screenY = computeY(anchor, yBase, height);

        int amount = (int) Math.round(player.getData(PDAttachments.PLAYER_SAN).sanValue());
        float smallAmount = 20.0F / 100.0F;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(screenX, screenY, 0);
        guiGraphics.pose().scale(scale, scale, 1.0f);

        // 理智图标（UV 0,32 尺寸 32x32）
        guiGraphics.blit(ICON, 0, 0, 0, 32, 32, 32);
        // 消耗遮罩：自顶向下覆盖（UV 0,70，高度 = 20 - 当前值*0.2）
        guiGraphics.blit(ICON, 0, 6, 0, 70, 32, 20 - Math.round(amount * smallAmount));

        // 理智光环箭头：正值升箭头（UV 32,32）、负值降箭头（UV 32,48）；|值|<5 时缩小为 7x8
        AttributeInstance instance = player.getAttribute(PDAttributes.SAN_VARIABILITY);
        if (instance != null) {
            boolean small = Math.abs(instance.getValue()) < 5;
            if (instance.getValue() > 0) {
                guiGraphics.blit(ICON, 18 + (small ? 3 : 0), 16 + (small ? 4 : 0),
                        small ? 7 : 14, small ? 8 : 16, 32, 32, 14, 16, 256, 256);
            } else if (instance.getValue() < 0) {
                guiGraphics.blit(ICON, 18 + (small ? 3 : 0), 16 + (small ? 4 : 0),
                        small ? 7 : 14, small ? 8 : 16, 32, 48, 14, 16, 256, 256);
            }
        }

        guiGraphics.pose().popPose();

        // 数值文本：始终显示配置开启，或潜行时显示
        boolean showValue = PDClientConfig.SAN_SHOW_VALUE_ALWAYS.get() || player.isShiftKeyDown();
        if (showValue) {
            String text = amount + "/100";
            int textX = screenX + Math.round(4 * scale);
            int textY = screenY + Math.round(-4 * scale);
            guiGraphics.drawString(mc.font, text, textX, textY, -1);
        }

        RenderSystem.disableBlend();
        mc.getProfiler().pop();
    }

    /**
     * 根据锚点计算屏幕 X 坐标。
     *
     * @param anchor      锚点索引（0=左上 1=右上 2=左下 3=右下）
     * @param offset      相对锚点的 X 偏移
     * @param screenWidth 屏幕宽度
     * @return 实际屏幕 X 坐标
     */
    private static int computeX(int anchor, int offset, int screenWidth) {
        return (anchor == PDClientConfig.ANCHOR_TOP_RIGHT || anchor == PDClientConfig.ANCHOR_BOTTOM_RIGHT)
                ? screenWidth + offset : offset;
    }

    /**
     * 根据锚点计算屏幕 Y 坐标。
     *
     * @param anchor       锚点索引（0=左上 1=右上 2=左下 3=右下）
     * @param offset       相对锚点的 Y 偏移
     * @param screenHeight 屏幕高度
     * @return 实际屏幕 Y 坐标
     */
    private static int computeY(int anchor, int offset, int screenHeight) {
        return (anchor == PDClientConfig.ANCHOR_TOP_LEFT || anchor == PDClientConfig.ANCHOR_TOP_RIGHT)
                ? offset : screenHeight + offset;
    }
}
