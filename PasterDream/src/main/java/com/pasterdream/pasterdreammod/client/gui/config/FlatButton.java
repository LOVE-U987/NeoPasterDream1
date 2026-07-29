package com.pasterdream.pasterdreammod.client.gui.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/**
 * 扁平风格按钮，用于配置界面保存/重置等底部操作及分类导航按钮
 * <p>
 * 支持常态、悬停、按下三种视觉状态，添加按下动画与顶部高光，符合深色现代梦境主题。
 *
 * @author PasterDream
 */
public class FlatButton extends AbstractButton {

    private int normalBg;
    private int hoverBg;
    private int pressedBg;
    private int textColor;
    private final Consumer<FlatButton> onPress;
    private long pressStart;
    /** 悬停平滑过渡（0~1） */
    private float hoverAnim;

    /**
     * @param x          按钮 x 坐标
     * @param y          按钮 y 坐标
     * @param width      按钮宽度
     * @param height     按钮高度
     * @param message    按钮文本
     * @param normalBg   常态背景色
     * @param hoverBg    悬停背景色
     * @param pressedBg  按下背景色
     * @param textColor  文字颜色
     * @param onPress    按下回调
     */
    public FlatButton(int x, int y, int width, int height, Component message,
                      int normalBg, int hoverBg, int pressedBg, int textColor,
                      Consumer<FlatButton> onPress) {
        super(x, y, width, height, message);
        this.normalBg = normalBg;
        this.hoverBg = hoverBg;
        this.pressedBg = pressedBg;
        this.textColor = textColor;
        this.onPress = onPress;
    }

    @Override
    protected void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        boolean hovered = isMouseOver(mouseX, mouseY);

        // 悬停平滑过渡
        if (hovered && hoverAnim < 1f) {
            hoverAnim = Math.min(1f, hoverAnim + 0.18f);
        } else if (!hovered && hoverAnim > 0f) {
            hoverAnim = Math.max(0f, hoverAnim - 0.12f);
        }

        // 按下动画
        boolean pressing = pressStart > 0 && System.currentTimeMillis() - pressStart < 150;
        float pressAnim = pressing ? 1f - (System.currentTimeMillis() - pressStart) / 150f : 0f;

        // 背景色：根据悬停/按下状态选择
        int bg;
        if (pressing) {
            bg = pressedBg;
        } else if (hoverAnim > 0.01f) {
            bg = lerpColor(normalBg, hoverBg, hoverAnim);
        } else {
            bg = normalBg;
        }

        // 按下时轻微内缩效果
        int inset = (int) (pressAnim * 1);
        gui.fill(getX() + inset, getY() + inset, getX() + width - inset, getY() + height - inset, bg);

        // 顶部细微高光，增强层次感
        int highlightAlpha = hovered ? 0x22 : 0x11;
        int highlightColor = (highlightAlpha << 24) | 0xFFFFFF;
        gui.fill(getX() + inset, getY() + inset, getX() + width - inset, getY() + 1 + inset, highlightColor);

        // 文字居中
        Font font = Minecraft.getInstance().font;
        int textX = getX() + (width - font.width(getMessage())) / 2;
        int textY = getY() + (height - font.lineHeight) / 2 + 1 + inset;
        gui.drawString(font, getMessage(), textX, textY, textColor);
    }

    @Override
    public void onPress() {
        pressStart = System.currentTimeMillis();
        if (onPress != null) {
            onPress.accept(this);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }

    /**
     * 更新按钮颜色配置，用于分类按钮选中态切换。
     *
     * @param normalBg  常态背景色
     * @param hoverBg   悬停背景色
     * @param pressedBg 按下背景色
     * @param textColor 文字颜色
     */
    public void setColors(int normalBg, int hoverBg, int pressedBg, int textColor) {
        this.normalBg = normalBg;
        this.hoverBg = hoverBg;
        this.pressedBg = pressedBg;
        this.textColor = textColor;
    }

    /**
     * 线性插值两个 ARGB 颜色
     */
    private static int lerpColor(int a, int b, float t) {
        int aR = (a >> 16) & 0xFF, aG = (a >> 8) & 0xFF, aB = a & 0xFF;
        int bR = (b >> 16) & 0xFF, bG = (b >> 8) & 0xFF, bB = b & 0xFF;
        int r = (int) (aR + (bR - aR) * t);
        int g = (int) (aG + (bG - aG) * t);
        int bl = (int) (aB + (bB - aB) * t);
        return (0xFF << 24) | (r << 16) | (g << 8) | bl;
    }
}