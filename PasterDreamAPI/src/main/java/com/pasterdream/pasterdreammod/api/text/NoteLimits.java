package com.pasterdream.pasterdreammod.api.text;

/**
 * 笔记正文的布局与容量上限（双端共用）。
 * <p>
 * 折行宽度/每页行数为布局口径；{@link #MAX_PAGES}/{@link #MAX_BODY_BYTES} 为容量口径。
 * 服务端无法重算分页，仅以字节上限作为权威约束。
 */
public final class NoteLimits {

    /** 折行宽度（像素，与编辑框文本宽一致）。 */
    public static final int WIDTH = 120;
    /** 每页可视行数（PD2 为 19，此处有意少 1 行）。 */
    public static final int LINES = 18;
    /** 保存时的最大页数。 */
    public static final int MAX_PAGES = 128;
    /** 正文最大字节数（JSON 字符串含引号与转义）。 */
    public static final int MAX_BODY_BYTES = 28000;

    private NoteLimits() {
    }
}
