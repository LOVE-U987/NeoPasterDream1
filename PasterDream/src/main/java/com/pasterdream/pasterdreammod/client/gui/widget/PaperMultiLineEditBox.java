package com.pasterdream.pasterdreammod.client.gui.widget;

import com.pasterdream.pasterdreammod.mixin.MultiLineEditBoxAccessor;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;

/**
 * 纸面风格多行输入框：透明叠于羊皮卷上，自绘正文（**无投影阴影**、深棕正文、深棕光标）。
 * <p>
 * 基类仍为 vanilla {@link MultiLineEditBox}：输入/焦点/滚动交由原版处理，使 IMBlocker 等
 * 「按原版文本框类型识别焦点」的输入法方案能正常输入中文（自绘 {@code AbstractScrollWidget}
 * 子类不会被识别，IME 会被保持关闭）。
 * <p>
 * 原版 {@code renderContents} 硬编码 5 参数 {@code drawString}（默认 {@code dropShadow = true}）
 * 与浅色正文/光标（光标 {@code 0xFFD0D0D0} 近白），子类无法改写，故本类覆写
 * {@link #renderContents} 完全自绘：正文用 {@link PaperStyle#TEXT_COLOR} 去阴影；光标用
 * {@link PaperStyle#CURSOR_COLOR}；选区用 {@link PaperStyle#SELECTION_COLOR}。光标后文本画在
 * 「正常位置」而非原版的「光标左移 1px」位置，避免光标闪烁时的 1px 抖动。
 * <p>
 * 通过 {@link MultiLineEditBoxAccessor} 取得原版 private {@code textField}，并经
 * {@link PDMultilineTextView} 读取行/光标/选区索引（{@code MultilineTextField.StringView} 跨包不可见）。
 * <p>
 * 本类覆写 {@link #renderWidget} 以跳过 {@code AbstractScrollWidget} 的 {@code enableScissor}：
 * 其裁剪矩形不随本控件所在的缩放 {@code pose} 变换，会裁到错误区域（见设计文档 D-notes-5）。
 */
public class PaperMultiLineEditBox extends MultiLineEditBox {

    /** 行高（逻辑像素），与分页口径一致。 */
    private static final int LINE_HEIGHT = 9;
    /** 光标竖线宽（逻辑像素）。 */
    private static final int CURSOR_WIDTH = 1;
    /** 行尾光标占位字符。 */
    private static final String CURSOR_APPEND_CHARACTER = "_";
    /** 光标闪烁周期（毫秒）。 */
    private static final long CURSOR_BLINK_INTERVAL_MS = 300L;

    private final Font pdFont;
    private final Component pdPlaceholder;
    private long pdFocusedTime = Util.getMillis();

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
        this.pdFont = font;
        this.pdPlaceholder = placeholder;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (focused) {
            this.pdFocusedTime = Util.getMillis();
        }
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

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        PDMultilineTextView text = this.pdText();
        String value = text.pdValue();
        if (value.isEmpty() && !this.isFocused()) {
            guiGraphics.drawWordWrap(this.pdFont, this.pdPlaceholder,
                    this.getX() + this.innerPadding(), this.getY() + this.innerPadding(),
                    this.width - this.totalInnerPadding(), PaperStyle.PLACEHOLDER_COLOR);
            return;
        }
        this.renderLines(guiGraphics, text, value);
        this.renderSelection(guiGraphics, text, value);
    }

    /**
     * 逐行绘制正文，并在光标处绘制竖线（行尾用占位字符）。
     *
     * @param guiGraphics 绘制上下文
     * @param text        文本状态视图
     * @param value       当前文本
     */
    private void renderLines(GuiGraphics guiGraphics, PDMultilineTextView text, String value) {
        int cursor = text.pdCursor();
        boolean blinkOn = this.isFocused()
                && (Util.getMillis() - this.pdFocusedTime) / CURSOR_BLINK_INTERVAL_MS % 2L == 0L;
        boolean cursorInLine = cursor < value.length();
        int textLeft = this.getX() + this.innerPadding();
        int lineTop = this.getY() + this.innerPadding();
        int cursorLeft = 0;
        int lastLineTop = 0;
        for (int i = 0; i < text.pdLineCount(); i++) {
            int begin = text.pdLineBegin(i);
            int end = text.pdLineEnd(i);
            if (this.withinContentAreaTopBottom(lineTop, lineTop + LINE_HEIGHT)) {
                if (blinkOn && cursorInLine && cursor >= begin && cursor <= end) {
                    cursorLeft = guiGraphics.drawString(this.pdFont, value.substring(begin, cursor),
                            textLeft, lineTop, PaperStyle.TEXT_COLOR, false) - 1;
                    guiGraphics.fill(cursorLeft, lineTop - 1, cursorLeft + CURSOR_WIDTH,
                            lineTop + 1 + LINE_HEIGHT, PaperStyle.CURSOR_COLOR);
                    // 光标后文本画在正常位置（cursorLeft + 1），避免闪烁时的 1px 抖动。
                    guiGraphics.drawString(this.pdFont, value.substring(cursor, end),
                            cursorLeft + 1, lineTop, PaperStyle.TEXT_COLOR, false);
                } else {
                    cursorLeft = guiGraphics.drawString(this.pdFont, value.substring(begin, end),
                            textLeft, lineTop, PaperStyle.TEXT_COLOR, false) - 1;
                }
                lastLineTop = lineTop;
            }
            lineTop += LINE_HEIGHT;
        }
        if (blinkOn && !cursorInLine
                && this.withinContentAreaTopBottom(lastLineTop, lastLineTop + LINE_HEIGHT)) {
            guiGraphics.drawString(this.pdFont, CURSOR_APPEND_CHARACTER, cursorLeft, lastLineTop,
                    PaperStyle.CURSOR_COLOR, false);
        }
    }

    /**
     * 绘制选区高亮（跨行分段）。
     *
     * @param guiGraphics 绘制上下文
     * @param text        文本状态视图
     * @param value       当前文本
     */
    private void renderSelection(GuiGraphics guiGraphics, PDMultilineTextView text, String value) {
        if (!text.pdHasSelection()) {
            return;
        }
        int selectBegin = text.pdSelectionBegin();
        int selectEnd = text.pdSelectionEnd();
        int textLeft = this.getX() + this.innerPadding();
        int lineTop = this.getY() + this.innerPadding();
        for (int i = 0; i < text.pdLineCount(); i++) {
            int begin = text.pdLineBegin(i);
            int end = text.pdLineEnd(i);
            if (selectBegin > end) {
                lineTop += LINE_HEIGHT;
                continue;
            }
            if (begin > selectEnd) {
                break;
            }
            if (this.withinContentAreaTopBottom(lineTop, lineTop + LINE_HEIGHT)) {
                int from = this.pdFont.width(value.substring(begin, Math.max(selectBegin, begin)));
                int to = selectEnd > end
                        ? this.width - this.innerPadding()
                        : this.pdFont.width(value.substring(begin, selectEnd));
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

    /** 取得原版内部文本状态视图。 */
    private PDMultilineTextView pdText() {
        return (PDMultilineTextView) (Object) ((MultiLineEditBoxAccessor) (Object) this).pasterdream$getTextField();
    }
}
