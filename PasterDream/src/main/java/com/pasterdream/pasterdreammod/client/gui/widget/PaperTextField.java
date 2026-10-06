package com.pasterdream.pasterdreammod.client.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.MultilineTextField;

/**
 * 纸面多行文本状态：在 {@link MultilineTextField} 之上暴露自绘所需的行与选区索引。
 * <p>
 * {@link MultilineTextField.StringView} 为 {@code protected} 嵌套类型，跨包无法直接引用；本子类在类内
 * 访问后再以 {@code int} 形式对外提供，供 {@link PaperMultiLineEditBox} 自绘文本/光标/选区使用。
 */
public final class PaperTextField extends MultilineTextField {

    /**
     * 构造纸面多行文本状态。
     *
     * @param font  字体
     * @param width 折行宽（逻辑像素）
     */
    public PaperTextField(Font font, int width) {
        super(font, width);
    }

    /** 显示行数。 */
    public int lineCount() {
        return this.getLineCount();
    }

    /**
     * 第 {@code index} 行的起始字符下标（含）。
     *
     * @param index 行号（越界自动收敛）
     * @return 起始下标
     */
    public int lineBegin(int index) {
        return this.getLineView(index).beginIndex();
    }

    /**
     * 第 {@code index} 行的结束字符下标（不含）。
     *
     * @param index 行号（越界自动收敛）
     * @return 结束下标
     */
    public int lineEnd(int index) {
        return this.getLineView(index).endIndex();
    }

    /** 选区起始下标（含）。 */
    public int selectionBegin() {
        return this.getSelected().beginIndex();
    }

    /** 选区结束下标（不含）。 */
    public int selectionEnd() {
        return this.getSelected().endIndex();
    }
}
