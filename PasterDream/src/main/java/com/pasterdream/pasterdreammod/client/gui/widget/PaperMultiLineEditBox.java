package com.pasterdream.pasterdreammod.client.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.network.chat.Component;

/**
 * 纸面风格多行输入框：透明叠于羊皮卷上，正文去投影阴影。
 * <p>
 * 直接继承 vanilla {@link MultiLineEditBox}，使输入/焦点/滚动交由原版处理；这一点同时是
 * IMBlocker 等「按原版文本框类型识别焦点」的输入法方案能正常输入中文的前提（自绘
 * {@code AbstractScrollWidget} 子类不会被识别，IME 会被保持关闭）。
 * <p>
 * 原版 {@code renderContents} 用 5 参数 {@code drawString}（默认 {@code dropShadow = true}），
 * 深色正文在浅色羊皮卷上会叠出重影；该硬编码色/阴影无法在子类改写，统一由
 * {@code PaperMultiLineEditBoxMixin} 的 {@code @Redirect} 仅对本类去除投影并套用纸面正文色。
 * <p>
 * 本类覆写 {@link #renderWidget} 以跳过 {@code AbstractScrollWidget} 的 {@code enableScissor}：
 * 其裁剪矩形不随本控件所在的缩放 {@code pose} 变换，会裁到错误区域（见设计文档 D-notes-5）。
 * 纵向溢出由 {@code renderContents} 内部按可视行裁剪，滚动条沿用基类
 * {@code renderDecorations}。
 */
public class PaperMultiLineEditBox extends MultiLineEditBox {

    /**
     * 构造纸面风格多行输入框。
     *
     * @param font        字体
     * @param x           左缘 X（逻辑坐标）
     * @param y           顶部 Y（逻辑坐标）
     * @param width       宽（逻辑坐标）
     * @param height      高（逻辑坐标）
     * @param placeholder 空内容占位文本
     * @param message     提示文本
     */
    public PaperMultiLineEditBox(Font font, int x, int y, int width, int height,
                                 Component placeholder, Component message) {
        super(font, x, y, width, height, placeholder, message);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!this.visible) {
            return;
        }
        // 不使用 scissor（原因见类注释）；背景保持透明，不绘制原版深色底框/边框。
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0, -this.scrollAmount(), 0.0);
        this.renderContents(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.pose().popPose();
        this.renderDecorations(guiGraphics);
    }
}
