package com.pasterdream.pasterdreammod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.attachment.PDAttachments;
import com.pasterdream.pasterdreammod.config.PDClientConfig;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

/**
 * 融梦能量条 HUD 叠加层
 * <p>
 * 移植自原版 {@code MeltdreamenergyTank.java}（IGuiOverlay → 1.21.1 {@link LayeredDraw.Layer}），
 * 纹理、坐标与 UV 参数与原版一致：
 * <ul>
 *   <li>锚点：由 {@link PDClientConfig#MELTDREAM_ENERGY_HUD_ANCHOR} 控制（左上/右上/左下/右下）</li>
 *   <li>偏移：xBase / yBase 为相对锚点的偏移量</li>
 *   <li>缩放：由 {@link PDClientConfig#MELTDREAM_ENERGY_HUD_SCALE} 控制（0.5 ~ 2.0）</li>
 *   <li>背景 80x15（UV 0,0），填充条按能量比例裁剪宽度（UV 0,16，宽 = 11 + 0.66*能量）</li>
 * </ul>
 * 显示条件：配置开启、未骑乘生物、未隐藏 GUI、且（若开启潜行显示配置）处于潜行。
 */
public class MeltdreamEnergyTankOverlay implements LayeredDraw.Layer {

    /** HUD 纹理（与 San 条共用图集） */
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
        if (!Boolean.TRUE.equals(PDCommonConfig.ENABLE_MELTDREAM_ENERGY_SYSTEM.get())
                || !PDClientConfig.SHOW_MELTDREAM_ENERGY_HUD.get()) {
            return;
        }
        // 骑乘生物时不显示（与原版一致）
        if (player.getVehicle() instanceof LivingEntity) {
            return;
        }
        // 配置为"仅潜行显示"且未潜行时不显示
        if (PDClientConfig.STEALTH_DISPLAY_ATTRIBUTE_HUD.get() && !player.isShiftKeyDown()) {
            return;
        }

        mc.getProfiler().push("meltdreamenergy_bar");
        RenderSystem.enableBlend();

        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();
        int anchor = PDClientConfig.MELTDREAM_ENERGY_HUD_ANCHOR.get();
        int xBase = PDClientConfig.MELTDREAMENERGY_TANK_XBASE.get().intValue();
        int yBase = PDClientConfig.MELTDREAMENERGY_TANK_YBASE.get().intValue();
        float scale = PDClientConfig.MELTDREAM_ENERGY_HUD_SCALE.get().floatValue();

        int screenX = computeX(anchor, xBase, width);
        int screenY = computeY(anchor, yBase, height);

        int amount = (int) Math.round(player.getData(PDAttachments.PLAYER_MELTDREAM_ENERGY).meltDreamEnergy());
        float smallAmount = 66.0F / 100.0F;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(screenX, screenY, 0);
        guiGraphics.pose().scale(scale, scale, 1.0f);

        // 能量条背景（UV 0,0 尺寸 80x15）
        guiGraphics.blit(ICON, 0, 0, 0, 0, 80, 15);
        // 能量填充（UV 0,16，宽度按能量比例）
        guiGraphics.blit(ICON, 0, 0, 0, 16, 11 + Math.round(smallAmount * amount), 15);

        guiGraphics.pose().popPose();

        // 数值文本：始终显示配置开启，或潜行时显示
        boolean showValue = PDClientConfig.MELTDREAM_ENERGY_SHOW_VALUE_ALWAYS.get() || player.isShiftKeyDown();
        if (showValue) {
            String text = amount + "/100";
            int textX = screenX + Math.round(33 * scale);
            int textY = screenY + Math.round(-5 * scale);
            guiGraphics.drawString(mc.font, text, textX, textY, -1);
        }

        RenderSystem.disableBlend();
        mc.getProfiler().pop();
    }

    /**
     * 根据锚点计算屏幕 X 坐标。
     *
     * @param anchor     锚点索引（0=左上 1=右上 2=左下 3=右下）
     * @param offset     相对锚点的 X 偏移
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
     * @param anchor      锚点索引（0=左上 1=右上 2=左下 3=右下）
     * @param offset      相对锚点的 Y 偏移
     * @param screenHeight 屏幕高度
     * @return 实际屏幕 Y 坐标
     */
    private static int computeY(int anchor, int offset, int screenHeight) {
        return (anchor == PDClientConfig.ANCHOR_TOP_LEFT || anchor == PDClientConfig.ANCHOR_TOP_RIGHT)
                ? offset : screenHeight + offset;
    }
}
