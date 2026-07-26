package com.pasterdream.pasterdreammod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.attachment.PDAttachments;
import com.pasterdream.pasterdreammod.config.PDClientConfig;
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
 *   <li>锚点：屏幕左下（x = meltdreamenergy tank xbase(1) 绝对值，y = 高 + ybase(-19)）</li>
 *   <li>背景 80x15（UV 0,0），填充条按能量比例裁剪宽度（UV 0,16，宽 = 11 + 0.66*能量）</li>
 *   <li>潜行时显示 "当前值/100" 文本</li>
 * </ul>
 * 显示条件：未骑乘生物、未隐藏 GUI、且（若开启潜行显示配置）处于潜行。
 * 原版的"能力存在"检查在 attachment 体系下恒成立，故不再需要。
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

        int height = guiGraphics.guiHeight();
        int xBase = PDClientConfig.MELTDREAMENERGY_TANK_XBASE.get().intValue();
        int yBase = height + PDClientConfig.MELTDREAMENERGY_TANK_YBASE.get().intValue();

        int amount = (int) Math.round(player.getData(PDAttachments.PLAYER_MELTDREAM_ENERGY).meltDreamEnergy());
        float smallAmount = 66.0F / 100.0F;

        // 能量条背景（UV 0,0 尺寸 80x15）
        guiGraphics.blit(ICON, xBase, yBase, 0, 0, 80, 15);
        // 能量填充（UV 0,16，宽度按能量比例）
        guiGraphics.blit(ICON, xBase, yBase, 0, 16, 11 + Math.round(smallAmount * amount), 15);

        // 潜行时显示数值文本
        if (player.isShiftKeyDown()) {
            guiGraphics.drawString(mc.font, amount + "/100", xBase + 33, yBase - 5, -1);
        }

        RenderSystem.disableBlend();
        mc.getProfiler().pop();
    }
}
