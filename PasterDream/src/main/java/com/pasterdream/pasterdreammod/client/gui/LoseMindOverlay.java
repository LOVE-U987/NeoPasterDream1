package com.pasterdream.pasterdreammod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.attachment.PDAttachments;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

/**
 * 失智全屏渐显 HUD 叠加层
 * <p>
 * 移植自原版 {@code LoseMind.java}：San ≤ 20 时全屏纹理
 * {@code textures/screens/lose_mind_gui.png} 以每帧 +0.005 的速度渐显（上限 1.0），
 * San 回升后以每帧 -0.01 渐隐。渐变进度沿用原版做法存于玩家持久化 NBT 的
 * {@code mind} 键（客户端本地副本）。
 * <p>
 * 原版使用 Tesselator 手绘全屏四边形（z=-90），此处等价改用
 * {@code GuiGraphics#setColor + blit} 全屏拉伸绘制。
 */
public class LoseMindOverlay implements LayeredDraw.Layer {

    /** 失智全屏纹理（自原版 assets 复制） */
    public static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(
            PasterDreamMod.MOD_ID, "textures/screens/lose_mind_gui.png");

    /** 渐变进度 NBT 键（与原版一致） */
    private static final String NBT_KEY_MIND = "mind";

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
        // 理智系统关闭时不显示
        if (!player.getData(PDAttachments.PLAYER_SAN).sanCheck()) {
            return;
        }

        mc.getProfiler().push("lose_mind_gui");
        double amount = player.getData(PDAttachments.PLAYER_SAN).sanValue();

        // 渐变进度累积（与原版逐帧速率一致）
        CompoundTag nbt = player.getPersistentData();
        float mind = nbt.contains(NBT_KEY_MIND) ? nbt.getFloat(NBT_KEY_MIND) : 0.0F;
        if (amount <= 20.0D) {
            if (mind < 1.0F) {
                mind += 0.005F;
            }
        } else if (mind > 0.0F) {
            mind -= 0.01F;
        }
        nbt.putFloat(NBT_KEY_MIND, mind);

        if (mind > 0.01F) {
            int width = guiGraphics.guiWidth();
            int height = guiGraphics.guiHeight();
            RenderSystem.enableBlend();
            // 关闭深度测试（与原版一致）：全屏渐显必须覆盖已绘制的 HUD 元素，
            // 否则 z=-90 的四边形会被先前写入深度的 HUD 像素遮挡
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, mind);
            // 全屏拉伸绘制（z=-90，与原版顶点深度一致）
            guiGraphics.blit(ICON, 0, 0, -90, 0.0F, 0.0F, width, height, width, height);
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
        }
        mc.getProfiler().pop();
    }
}
