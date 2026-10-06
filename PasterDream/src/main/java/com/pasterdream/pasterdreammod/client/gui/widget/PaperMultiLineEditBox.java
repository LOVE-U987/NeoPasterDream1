package com.pasterdream.pasterdreammod.client.gui.widget;

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;

/**
 * 纸面风格多行输入框：透明叠于羊皮卷上，自绘文本（**无投影阴影**）。
 * <p>
 * 之所以不复用 vanilla {@code MultiLineEditBox}：其 {@code renderContents} 用 5 参数
 * {@code drawString}（默认 {@code dropShadow = true}），深色文字在浅色羊皮卷上叠出深色重影，
 * 观感"加粗"；且其 {@code textField} 为 private，子类无法改字色/阴影。故本类直接继承
 * {@link AbstractScrollWidget}，自持 {@link PaperTextField} 并以 6 参数
 * {@code drawString(..., false)} 自绘正文、光标与选区。
 * <p>
 * 本类**不启用 scissor**：{@link GuiGraphics#enableScissor} 的裁剪矩形不随 {@code PoseStack}
 * 变换，而本控件在缩放 pose 下绘制，使用 scissor 会裁到错误区域；纵向溢出由
 * {@link #withinContentAreaTopBottom} 按可视行裁剪。滚动条沿用基类
 * {@link AbstractScrollWidget#renderDecorations} 的精灵绘制。
 */
public class PaperMultiLineEditBox extends AbstractScrollWidget {

    /** 行高（逻辑像素），与分页口径一致。 */
    private static final int LINE_HEIGHT = 9;
    /** 光标竖线宽（逻辑像素）。 */
    private static final int CURSOR_WIDTH = 1;
    /** 行尾光标占位字符。 */
    private static final String CURSOR_APPEND_CHARACTER = "_";
    /** 光标闪烁周期（毫秒）。 */
    private static final long CURSOR_BLINK_INTERVAL_MS = 300L;

    private final Font font;
    private final Component placeholder;
    private final PaperTextField textField;
    private long focusedTime = Util.getMillis();

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
        super(x, y, width, height, message);
        this.font = font;
        this.placeholder = placeholder;
        this.textField = new PaperTextField(font, width - this.totalInnerPadding());
        this.textField.setCursorListener(this::scrollToCursor);
    }

    /**
     * 设置文本内容。
     *
     * @param value 文本
     */
    public void setValue(String value) {
        this.textField.setValue(value);
    }

    /**
     * 取当前文本内容。
     *
     * @return 文本
     */
    public String getValue() {
        return this.textField.value();
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE,
                Component.translatable("gui.narrate.editBox", this.getMessage(), this.getValue()));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.withinContentAreaPoint(mouseX, mouseY) && button == 0) {
            this.textField.setSelecting(Screen.hasShiftDown());
            this.seekCursorScreen(mouseX, mouseY);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (super.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        if (this.withinContentAreaPoint(mouseX, mouseY) && button == 0) {
            this.textField.setSelecting(true);
            this.seekCursorScreen(mouseX, mouseY);
            this.textField.setSelecting(Screen.hasShiftDown());
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.textField.keyPressed(keyCode);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.visible && this.isFocused() && StringUtil.isAllowedChatCharacter(codePoint)) {
            this.textField.insertText(Character.toString(codePoint));
            return true;
        }
        return false;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (focused) {
            this.focusedTime = Util.getMillis();
        }
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!this.visible) {
            return;
        }
        // 背景保持透明；不使用 scissor（原因见类注释）。
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0, -this.scrollAmount(), 0.0);
        this.renderContents(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.pose().popPose();
        this.renderDecorations(guiGraphics);
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        String value = this.textField.value();
        if (value.isEmpty() && !this.isFocused()) {
            guiGraphics.drawWordWrap(this.font, this.placeholder,
                    this.getX() + this.innerPadding(), this.getY() + this.innerPadding(),
                    this.width - this.totalInnerPadding(), PaperStyle.PLACEHOLDER_COLOR);
            return;
        }
        this.renderLines(guiGraphics, value);
        this.renderSelection(guiGraphics, value);
    }

    /**
     * 逐行绘制正文，并在光标处绘制竖线（行尾用占位字符）。
     *
     * @param guiGraphics 绘制上下文
     * @param value       当前文本
     */
    private void renderLines(GuiGraphics guiGraphics, String value) {
        int cursor = this.textField.cursor();
        boolean blinkOn = this.isFocused()
                && (Util.getMillis() - this.focusedTime) / CURSOR_BLINK_INTERVAL_MS % 2L == 0L;
        boolean cursorInLine = cursor < value.length();
        int textLeft = this.getX() + this.innerPadding();
        int lineTop = this.getY() + this.innerPadding();
        int cursorLeft = 0;
        int lastLineTop = 0;
        for (int i = 0; i < this.textField.lineCount(); i++) {
            int begin = this.textField.lineBegin(i);
            int end = this.textField.lineEnd(i);
            if (this.withinContentAreaTopBottom(lineTop, lineTop + LINE_HEIGHT)) {
                if (blinkOn && cursorInLine && cursor >= begin && cursor <= end) {
                    cursorLeft = guiGraphics.drawString(this.font, value.substring(begin, cursor),
                            textLeft, lineTop, PaperStyle.TEXT_COLOR, false) - 1;
                    guiGraphics.fill(cursorLeft, lineTop - 1, cursorLeft + CURSOR_WIDTH,
                            lineTop + 1 + LINE_HEIGHT, PaperStyle.CURSOR_COLOR);
                    guiGraphics.drawString(this.font, value.substring(cursor, end),
                            cursorLeft, lineTop, PaperStyle.TEXT_COLOR, false);
                } else {
                    cursorLeft = guiGraphics.drawString(this.font, value.substring(begin, end),
                            textLeft, lineTop, PaperStyle.TEXT_COLOR, false) - 1;
                }
                lastLineTop = lineTop;
            }
            lineTop += LINE_HEIGHT;
        }
        if (blinkOn && !cursorInLine
                && this.withinContentAreaTopBottom(lastLineTop, lastLineTop + LINE_HEIGHT)) {
            guiGraphics.drawString(this.font, CURSOR_APPEND_CHARACTER, cursorLeft, lastLineTop,
                    PaperStyle.CURSOR_COLOR, false);
        }
    }

    /**
     * 绘制选区高亮（跨行分段）。
     *
     * @param guiGraphics 绘制上下文
     * @param value       当前文本
     */
    private void renderSelection(GuiGraphics guiGraphics, String value) {
        if (!this.textField.hasSelection()) {
            return;
        }
        int selectBegin = this.textField.selectionBegin();
        int selectEnd = this.textField.selectionEnd();
        int textLeft = this.getX() + this.innerPadding();
        int lineTop = this.getY() + this.innerPadding();
        for (int i = 0; i < this.textField.lineCount(); i++) {
            int begin = this.textField.lineBegin(i);
            int end = this.textField.lineEnd(i);
            if (selectBegin > end) {
                lineTop += LINE_HEIGHT;
                continue;
            }
            if (begin > selectEnd) {
                break;
            }
            if (this.withinContentAreaTopBottom(lineTop, lineTop + LINE_HEIGHT)) {
                int from = this.font.width(value.substring(begin, Math.max(selectBegin, begin)));
                int to = selectEnd > end
                        ? this.width - this.innerPadding()
                        : this.font.width(value.substring(begin, selectEnd));
                this.renderHighlight(guiGraphics, textLeft + from, lineTop, textLeft + to, lineTop + LINE_HEIGHT);
            }
            lineTop += LINE_HEIGHT;
        }
    }

    /**
     * 绘制高亮矩形。
     *
     * @param guiGraphics 绘制上下文
     * @param x1          左缘
     * @param y1          顶部
     * @param x2          右缘
     * @param y2          底部
     */
    private void renderHighlight(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2) {
        guiGraphics.fill(RenderType.guiTextHighlight(), x1, y1, x2, y2, PaperStyle.SELECTION_COLOR);
    }

    /** 使光标所在行保持在可视区内。 */
    private void scrollToCursor() {
        double scroll = this.scrollAmount();
        int firstLineBegin = this.textField.lineBegin((int) (scroll / LINE_HEIGHT));
        if (this.textField.cursor() <= firstLineBegin) {
            scroll = this.textField.getLineAtCursor() * LINE_HEIGHT;
        } else {
            int lastLineEnd = this.textField.lineEnd((int) ((scroll + this.height) / LINE_HEIGHT) - 1);
            if (this.textField.cursor() > lastLineEnd) {
                scroll = this.textField.getLineAtCursor() * LINE_HEIGHT
                        - this.height + LINE_HEIGHT + this.totalInnerPadding();
            }
        }
        this.setScrollAmount(scroll);
    }

    /** 可视行数（含内边距扣减）。 */
    private double getDisplayableLineCount() {
        return (double) (this.height - this.totalInnerPadding()) / LINE_HEIGHT;
    }

    /**
     * 将屏幕坐标换算为文本坐标并定位光标。
     *
     * @param mouseX 屏幕 X（逻辑坐标）
     * @param mouseY 屏幕 Y（逻辑坐标）
     */
    private void seekCursorScreen(double mouseX, double mouseY) {
        double localX = mouseX - this.getX() - this.innerPadding();
        double localY = mouseY - this.getY() - this.innerPadding() + this.scrollAmount();
        this.textField.seekCursorToPoint(localX, localY);
    }

    @Override
    protected int getInnerHeight() {
        return LINE_HEIGHT * this.textField.lineCount();
    }

    @Override
    protected boolean scrollbarVisible() {
        return (double) this.textField.lineCount() > this.getDisplayableLineCount();
    }

    @Override
    protected double scrollRate() {
        return LINE_HEIGHT / 2.0;
    }
}
