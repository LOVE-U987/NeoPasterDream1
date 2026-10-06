package com.pasterdream.pasterdreammod.client.gui.widget;

/**
 * 羊皮卷纸面风格共享配色（客户端）。
 * <p>
 * 供笔记界面的输入控件与文本渲染统一取色，避免多处硬编码漂移。
 */
public final class PaperStyle {

    /** 纸面正文文字色（深棕，ARGB）。 */
    public static final int TEXT_COLOR = 0xFF2B2118;

    /** 占位文字色（半透明中棕，ARGB）。 */
    public static final int PLACEHOLDER_COLOR = 0xCC8A7A66;

    /** 光标色（深棕，ARGB）。 */
    public static final int CURSOR_COLOR = 0xFF2B2118;

    /** 选区高亮色（半透明蓝，ARGB）。 */
    public static final int SELECTION_COLOR = 0x663B6EA5;

    /** 名称框深色底框上的文字色（浅灰，ARGB）。 */
    public static final int NAME_TEXT_COLOR = 0xFFE0E0E0;

    private PaperStyle() {
    }
}
