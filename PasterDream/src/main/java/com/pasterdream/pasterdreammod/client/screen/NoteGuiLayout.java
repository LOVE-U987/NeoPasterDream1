package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.api.text.NoteLimits;

/**
 * 寻梦者笔记界面的统一布局与缩放计算（客户端）。
 * <p>
 * 坐标体系为「字体逻辑像素」：正文折行宽恒为 {@link NoteLimits#WIDTH}，与分页口径一致。
 * 底图基准尺寸由 {@link #BASE_SCALE} 一个旋钮控制，所有元素位置均由底图纹理锚点按
 * {@code design/TEX} 比例换算，随屏幕等比缩放（仅缩小以适配，见 {@link #compute}）。
 * <p>
 * {@link #compute} 返回不可变的 {@link Frame}，其中所有坐标均为逻辑坐标；实际屏幕绘制
 * 由 {@link AbstractPaperScreen} 统一施加 {@code translate + scale}。
 */
public final class NoteGuiLayout {

    /** 底图纹理原始宽（{@code textures/screens/dreamnote_paper.png}）。 */
    public static final int TEX_W = 775;
    /** 底图纹理原始高。 */
    public static final int TEX_H = 1024;
    /** 基准宽（固定参考基准，不随需求调整）。 */
    public static final int BASE_W = 200;
    /** 基础缩放比例（唯一可调旋钮）：调整后底图与全部元素等比缩放。 */
    public static final float BASE_SCALE = 0.8f;
    /** 底图与屏幕边缘的最小留白（屏幕像素）。 */
    public static final int MARGIN = 8;

    /** 标题基线纹理锚点 Y。 */
    private static final int ANCHOR_TITLE_Y = 155;
    /** 阅读正文顶部纹理锚点 Y。 */
    private static final int ANCHOR_BODY_Y = 217;
    /** 名称框顶部纹理锚点 Y。 */
    private static final int ANCHOR_NAME_Y = 147;
    /** 正文编辑框顶部纹理锚点 Y。 */
    private static final int ANCHOR_BODYBOX_Y = 202;
    /** 页码顶部纹理锚点 Y。 */
    private static final int ANCHOR_FOOTER_Y = 946;
    /** 翻页按钮顶部纹理锚点 Y。 */
    private static final int ANCHOR_BUTTON_Y = 892;
    /** 前一页按钮左缘纹理锚点 X。 */
    private static final int ANCHOR_PREV_X = 93;
    /** 后一页按钮左缘纹理锚点 X。 */
    private static final int ANCHOR_NEXT_X = 604;

    /** 正文与名称框的控件高（逻辑像素）。 */
    private static final int NAME_H = 14;
    /** 正文编辑框相对正文左移量，抵消 {@code PaperMultiLineEditBox} 的内边距，使内文左对齐。 */
    private static final int BODYBOX_LEFT_INSET = 4;
    /** 正文编辑框内边距总量（左右各 4）。 */
    private static final int BODYBOX_PADDING = 8;
    /** 翻页按钮尺寸（逻辑像素）。 */
    private static final int BUTTON_W = 20;
    private static final int BUTTON_H = 18;

    private NoteGuiLayout() {
    }

    /**
     * 笔记界面的一次布局结果（全部为字体逻辑坐标）。
     * <p>
     * 屏幕坐标 = {@code origin + logical * scale}；反向由 {@link #toLogicalX}/{@link #toLogicalY} 完成。
     *
     * @param scale        逻辑到屏幕的缩放系数（0~1）
     * @param originX      缩放坐标系原点 X（屏幕像素）
     * @param originY      缩放坐标系原点 Y（屏幕像素）
     * @param designW      底图逻辑宽
     * @param designH      底图逻辑高
     * @param titleY       标题顶部 Y
     * @param bodyX        正文左缘 X
     * @param bodyY        正文顶部 Y
     * @param bodyW        正文折行宽
     * @param nameX        名称框左缘 X
     * @param nameY        名称框顶部 Y
     * @param nameW        名称框宽
     * @param nameH        名称框高
     * @param bodyBoxX     正文编辑框左缘 X
     * @param bodyBoxY     正文编辑框顶部 Y
     * @param bodyBoxW     正文编辑框宽
     * @param bodyBoxH     正文编辑框高
     * @param footerY      页码顶部 Y
     * @param prevButtonX  前一页按钮左缘 X
     * @param nextButtonX  后一页按钮左缘 X
     * @param buttonY      翻页按钮顶部 Y
     * @param buttonW      翻页按钮宽
     * @param buttonH      翻页按钮高
     */
    public record Frame(float scale, int originX, int originY,
                        int designW, int designH,
                        int titleY,
                        int bodyX, int bodyY, int bodyW,
                        int nameX, int nameY, int nameW, int nameH,
                        int bodyBoxX, int bodyBoxY, int bodyBoxW, int bodyBoxH,
                        int footerY,
                        int prevButtonX, int nextButtonX, int buttonY, int buttonW, int buttonH) {

        /** 屏幕 X -> 逻辑 X。 */
        public int toLogicalX(double screenX) {
            return Math.round((float) ((screenX - originX) / scale));
        }

        /** 屏幕 Y -> 逻辑 Y。 */
        public int toLogicalY(double screenY) {
            return Math.round((float) ((screenY - originY) / scale));
        }

        /** 逻辑 X -> 屏幕 X。 */
        public double toScreenX(double logicalX) {
            return originX + logicalX * scale;
        }

        /** 逻辑 Y -> 屏幕 Y。 */
        public double toScreenY(double logicalY) {
            return originY + logicalY * scale;
        }
    }

    /**
     * 根据屏幕尺寸计算布局。
     * <p>
     * 缩放策略：底图基准尺寸 {@code BASE_W * BASE_SCALE}，逐轴取屏幕可用空间能容纳的最小系数，
     * 并封顶 1.0（仅缩小以适配，不放大）。底图在屏幕内居中。
     *
     * @param screenW 屏幕可用宽（GUI 缩放后的逻辑像素）
     * @param screenH 屏幕可用高
     * @return 布局结果
     */
    public static Frame compute(int screenW, int screenH) {
        // 下限保护：正文字宽恒为 NoteLimits.WIDTH，designW 过小会使编辑框越出纸面
        int designW = Math.max(Math.round(BASE_W * BASE_SCALE), NoteLimits.WIDTH + BODYBOX_PADDING);
        int designH = Math.round(designW * (float) TEX_H / TEX_W);

        float fitW = (screenW - 2f * MARGIN) / designW;
        float fitH = (screenH - 2f * MARGIN) / designH;
        float scale = Math.max(0.05f, Math.min(1f, Math.min(fitW, fitH)));

        int paperW = Math.round(designW * scale);
        int paperH = Math.round(designH * scale);
        int originX = (screenW - paperW) / 2;
        int originY = (screenH - paperH) / 2;

        float ratioX = 1f * designW / TEX_W;
        float ratioY = 1f * designH / TEX_H;

        int bodyX = (designW - NoteLimits.WIDTH) / 2;
        int bodyBoxX = bodyX - BODYBOX_LEFT_INSET;

        return new Frame(
                scale, originX, originY, designW, designH,
                Math.round(ANCHOR_TITLE_Y * ratioY),
                bodyX, Math.round(ANCHOR_BODY_Y * ratioY), NoteLimits.WIDTH,
                bodyX, Math.round(ANCHOR_NAME_Y * ratioY), NoteLimits.WIDTH, NAME_H,
                bodyBoxX, Math.round(ANCHOR_BODYBOX_Y * ratioY),
                NoteLimits.WIDTH + BODYBOX_PADDING, NoteLimits.LINES * 9 + BODYBOX_PADDING,
                Math.round(ANCHOR_FOOTER_Y * ratioY),
                Math.round(ANCHOR_PREV_X * ratioX), Math.round(ANCHOR_NEXT_X * ratioX),
                Math.round(ANCHOR_BUTTON_Y * ratioY), BUTTON_W, BUTTON_H);
    }
}
