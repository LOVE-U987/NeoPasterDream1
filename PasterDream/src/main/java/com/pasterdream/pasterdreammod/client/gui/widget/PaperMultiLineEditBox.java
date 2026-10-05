package com.pasterdream.pasterdreammod.client.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.network.chat.Component;

/**
 * 纸面风格多行输入框：隐藏 vanilla 深色底框与边框，文字改为深棕，叠于羊皮卷上。
 * <p>
 * {@link MultiLineEditBox} 的正文色硬编码为浅色且 {@code textField} 为私有，无法直接改色，
 * 故在 {@link #renderWidget} 期间用 {@link GuiGraphics#setColor} 做整体 tint。副作用：光标、
 * 选择高亮与滚动条会被同色 tint（观感可接受；若后续需要更精细控制，可改为自写编辑控件）。
 * <p>
 * 本类不启用 scissor：{@link GuiGraphics#enableScissor} 的裁剪矩形不随 {@code PoseStack}
 * 变换，而本控件在缩放 pose 下绘制，使用 scissor 会裁到错误区域（显示异常）。
 */
public class PaperMultiLineEditBox extends MultiLineEditBox {

    /** 默认文字色 0xFFE0E0E0 的单通道值，用于换算 tint 系数。 */
    private static final float BASE_TEXT_CHANNEL = 0xE0;
    /** 目标文字色（{@link PaperStyle#TEXT_COLOR}）相对默认文字色的 tint 系数。 */
    private static final float TINT_R = ((PaperStyle.TEXT_COLOR >> 16) & 0xFF) / BASE_TEXT_CHANNEL;
    private static final float TINT_G = ((PaperStyle.TEXT_COLOR >> 8) & 0xFF) / BASE_TEXT_CHANNEL;
    private static final float TINT_B = (PaperStyle.TEXT_COLOR & 0xFF) / BASE_TEXT_CHANNEL;

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
    protected void renderBackground(GuiGraphics guiGraphics) {
        // 纸面风格：不绘制 vanilla 深色底框
    }

    @Override
    protected void renderBorder(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        // 纸面风格：不绘制边框
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!this.visible) {
            return;
        }
        // 不使用 scissor（原因见类注释）；背景保持透明，仅绘制文本与滚动条。
        guiGraphics.setColor(TINT_R, TINT_G, TINT_B, 1.0f);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0, -this.scrollAmount(), 0.0);
        this.renderContents(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.pose().popPose();
        this.renderDecorations(guiGraphics);
        guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
