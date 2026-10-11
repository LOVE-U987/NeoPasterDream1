package com.pasterdream.pasterdreammod.client.gui.widget;

/**
 * {@code MultilineTextField} 自绘视图桥：以 {@code int}/{@code String} 形式对外暴露行、光标与选区索引。
 * <p>
 * 原版 {@code MultilineTextField.StringView} 为 {@code protected} 嵌套类型，跨包不可引用；
 * {@code MultilineTextFieldViewMixin}（mixin 包）在类内访问后经本接口桥接，供
 * {@link PaperMultiLineEditBox} 自绘文本/光标/选区使用。
 * <p>
 * 注意：本接口刻意**不放在** {@code com.pasterdream.pasterdreammod.mixin} 包内——Mixin 会将其所属包
 * 视为「mixin 独占包」，包内非 Mixin 类被外部直接引用会在类加载期抛
 * {@code IllegalClassLoadError}。
 */
public interface PDMultilineTextView {

    /** 显示行数。 */
    int pdLineCount();

    /**
     * 第 {@code index} 行的起始字符下标（含）。
     *
     * @param index 行号
     * @return 起始下标
     */
    int pdLineBegin(int index);

    /**
     * 第 {@code index} 行的结束字符下标（不含）。
     *
     * @param index 行号
     * @return 结束下标
     */
    int pdLineEnd(int index);

    /** 光标下标。 */
    int pdCursor();

    /** 当前文本。 */
    String pdValue();

    /** 是否存在选区。 */
    boolean pdHasSelection();

    /** 选区起始下标（含）。 */
    int pdSelectionBegin();

    /** 选区结束下标（不含）。 */
    int pdSelectionEnd();
}
