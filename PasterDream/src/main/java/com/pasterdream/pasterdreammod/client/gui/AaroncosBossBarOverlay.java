package com.pasterdream.pasterdreammod.client.gui;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.mob.AaroncosLefthand0Entity;
import com.pasterdream.pasterdreammod.entity.mob.AaroncosRighthand0Entity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import java.util.List;

/**
 * 亚伦柯斯之触 — 自定义双体 BOSS 血条 HUD
 * <p>
 * 在屏幕顶部渲染左右手双体血条，使用纹理 {@code pasterdream:textures/gui/aaroncos_hand_hp.png}。
 * <ul>
 *   <li>通过 {@link CustomizeGuiOverlayEvent.BossEventProgress} 取消原始 BOSS 血条渲染</li>
 *   <li>通过 {@link RenderGuiEvent.Post} 渲染自定义双体血条</li>
 *   <li>搜索附近 {@link AaroncosLefthand0Entity} 和 {@link AaroncosRighthand0Entity} 实体并读取血量</li>
 * </ul>
 * 只有至少一只手存活且在搜索范围内时显示。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public class AaroncosBossBarOverlay {

    private static final Minecraft MC = Minecraft.getInstance();

    /** BOSS 血条纹理 */
    private static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(
            PasterDreamMod.MOD_ID, "textures/gui/aaroncos_hand_hp.png");

    /** BOSS 搜索范围（格） */
    private static final double SEARCH_RANGE = 80.0;

    // ===================== 左血条（左手）纹理常量 =====================
    private static final int LEFT_BG_X_OFF = -118;    // 左背景 X：屏幕中心 - 118
    private static final int LEFT_BG_Y = 13;           // 左背景 Y
    private static final int LEFT_BG_W = 116;          // 左背景宽
    private static final int LEFT_BG_H = 21;           // 左背景高
    private static final int LEFT_BG_U = 0;            // 左背景纹理 U
    private static final int LEFT_BG_V = 0;            // 左背景纹理 V

    private static final int LEFT_FILL_X_OFF = -111;   // 左填充 X：屏幕中心 - 111
    private static final int LEFT_FILL_Y = 19;          // 左填充 Y
    private static final int LEFT_FILL_U = 7;           // 左填充纹理 U
    private static final int LEFT_FILL_V = 38;          // 左填充纹理 V
    private static final int LEFT_FILL_MAX_W = 106;     // 左填充最大宽度
    private static final int LEFT_FILL_H = 5;           // 左填充高度

    // ===================== 右血条（右手）纹理常量 =====================
    private static final int RIGHT_BG_X = 0;            // 右背景 X：屏幕中心
    private static final int RIGHT_BG_Y = 13;           // 右背景 Y
    private static final int RIGHT_BG_W = 244;          // 右背景宽
    private static final int RIGHT_BG_H = 21;           // 右背景高
    private static final int RIGHT_BG_U = 0;            // 右背景纹理 U
    private static final int RIGHT_BG_V = 64;           // 右背景纹理 V

    private static final int RIGHT_FILL_X_OFF = 3;      // 右填充 X：屏幕中心 + 3
    private static final int RIGHT_FILL_Y = 19;         // 右填充 Y
    private static final int RIGHT_FILL_U = 3;           // 右填充纹理 U
    private static final int RIGHT_FILL_V = 102;         // 右填充纹理 V
    private static final int RIGHT_FILL_MAX_W = 106;     // 右填充最大宽度
    private static final int RIGHT_FILL_H = 5;           // 右填充高度

    // ===================== 标题常量 =====================
    private static final int TITLE_X_OFF = -30;          // 标题 X：屏幕中心 - 30
    private static final int TITLE_Y = 5;                // 标题 Y
    /** BOSS 显示名称 */
    private static final String BOSS_TITLE = "\u00a7l\u4e9a\u4f26\u67ef\u65af\u4e4b\u89e6";

    /**
     * 取消亚伦柯斯之手实体的原始 BOSS 血条渲染
     * <p>
     * 当 BOSS 血条事件中的名称匹配到亚伦柯斯左右手时，取消该血条渲染，
     * 由本类的 {@link #onRenderGuiPost(RenderGuiEvent.Post)} 方法渲染自定义双体血条。
     *
     * @param event BOSS 血条进度自定义事件
     */
    @SubscribeEvent
    public static void onBossEventProgress(CustomizeGuiOverlayEvent.BossEventProgress event) {
        String name = event.getBossEvent().getName().getString();
        // 匹配原始翻译键（语言文件未提供翻译时显示）或已翻译的名称
        if (name.contains("aaroncos_lefthand") || name.contains("aaroncos_righthand")) {
            event.setCanceled(true);
        }
    }

    /**
     * 在 GUI 渲染后绘制自定义双体 BOSS 血条
     * <p>
     * 搜索附近的 {@link AaroncosLefthand0Entity} 和 {@link AaroncosRighthand0Entity}，
     * 获取其血量比例后绘制双体血条 + 标题。
     *
     * @param event GUI 渲染后事件
     */
    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        if (MC.player == null || MC.options.hideGui || MC.level == null) return;

        // 搜索附近的左右手实体
        List<AaroncosLefthand0Entity> leftHands = MC.level.getEntitiesOfClass(
                AaroncosLefthand0Entity.class,
                MC.player.getBoundingBox().inflate(SEARCH_RANGE),
                Entity::isAlive);
        List<AaroncosRighthand0Entity> rightHands = MC.level.getEntitiesOfClass(
                AaroncosRighthand0Entity.class,
                MC.player.getBoundingBox().inflate(SEARCH_RANGE),
                Entity::isAlive);

        boolean hasLeft = !leftHands.isEmpty();
        boolean hasRight = !rightHands.isEmpty();

        // 至少一只手存活时才渲染
        if (!hasLeft && !hasRight) return;

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int centerX = MC.getWindow().getGuiScaledWidth() / 2;

        // 渲染血条
        if (hasLeft) {
            AaroncosLefthand0Entity leftHand = leftHands.get(0);
            float progress = leftHand.getHealth() / leftHand.getMaxHealth();
            renderLeftBar(guiGraphics, centerX, progress);
        }
        if (hasRight) {
            AaroncosRighthand0Entity rightHand = rightHands.get(0);
            float progress = rightHand.getHealth() / rightHand.getMaxHealth();
            renderRightBar(guiGraphics, centerX, progress);
        }

        // 渲染标题
        guiGraphics.drawString(MC.font, BOSS_TITLE, centerX + TITLE_X_OFF, TITLE_Y, 0xFFFFFFFF);
    }

    /**
     * 渲染左手血条
     * <p>
     * 使用纹理中的左血条背景和填充区域，根据血量比例动态裁剪填充宽度。
     *
     * @param gui      绘制上下文
     * @param centerX  屏幕中心 X
     * @param progress 血量比例（0.0 ~ 1.0）
     */
    private static void renderLeftBar(GuiGraphics gui, int centerX, float progress) {
        // 背景框
        gui.blit(ICON, centerX + LEFT_BG_X_OFF, LEFT_BG_Y, LEFT_BG_U, LEFT_BG_V, LEFT_BG_W, LEFT_BG_H);

        // 血量填充（根据比例动态裁剪宽度）
        int fillWidth = Mth.floor(LEFT_FILL_MAX_W * Math.min(progress, 1.0f));
        if (fillWidth > 0) {
            gui.blit(ICON, centerX + LEFT_FILL_X_OFF, LEFT_FILL_Y,
                    LEFT_FILL_U, LEFT_FILL_V, fillWidth, LEFT_FILL_H);
        }
    }

    /**
     * 渲染右手血条
     * <p>
     * 使用纹理中的右血条背景和填充区域，根据血量比例动态裁剪填充宽度。
     *
     * @param gui      绘制上下文
     * @param centerX  屏幕中心 X
     * @param progress 血量比例（0.0 ~ 1.0）
     */
    private static void renderRightBar(GuiGraphics gui, int centerX, float progress) {
        // 背景框
        gui.blit(ICON, centerX + RIGHT_BG_X, RIGHT_BG_Y, RIGHT_BG_U, RIGHT_BG_V, RIGHT_BG_W, RIGHT_BG_H);

        // 血量填充（根据比例动态裁剪宽度）
        int fillWidth = Mth.floor(RIGHT_FILL_MAX_W * Math.min(progress, 1.0f));
        if (fillWidth > 0) {
            gui.blit(ICON, centerX + RIGHT_FILL_X_OFF, RIGHT_FILL_Y,
                    RIGHT_FILL_U, RIGHT_FILL_V, fillWidth, RIGHT_FILL_H);
        }
    }
}
