package com.pasterdream.pasterdreammod.client.gui.config;

/**
 * PasterDream 配置界面视觉与布局常量
 * <p>
 * 紧凑、去 AI 化的深色现代主题：以深海梦境为基调，使用低饱和青绿作为强调色，
 * 避免紫色渐变与过度光晕。所有颜色以 ARGB 整数形式提供，便于
 * {@link net.minecraft.client.gui.GuiGraphics} 直接填充。
 *
 * @author PasterDream
 */
public final class ConfigStyles {

    private ConfigStyles() {
    }

    // ==================== 布局常量 ====================

    /** 屏幕左右外边距（居中面板） */
    public static final int PANEL_MARGIN_X = 28;
    /** 屏幕上下外边距（居中面板） */
    public static final int PANEL_MARGIN_Y = 18;
    /** 左侧分类导航栏宽度 */
    public static final int SIDEBAR_WIDTH = 88;
    /** 内容区与侧边栏之间的内边距 */
    public static final int CONTENT_LEFT_MARGIN = 10;
    /** 行高 */
    public static final int ROW_HEIGHT = 30;
    /** 配置项横向内边距 */
    public static final int ROW_PADDING_X = 10;
    /** 配置项纵向内边距 */
    public static final int ROW_PADDING_Y = 4;
    /** 顶部标题区高度 */
    public static final int HEADER_HEIGHT = 26;
    /** 底部按钮区高度 */
    public static final int FOOTER_HEIGHT = 28;
    /** 分类按钮高度 */
    public static final int CATEGORY_BUTTON_HEIGHT = 20;
    /** 分类按钮间距 */
    public static final int CATEGORY_BUTTON_GAP = 1;
    /** 开关按钮宽度 */
    public static final int TOGGLE_WIDTH = 32;
    /** 开关按钮高度 */
    public static final int TOGGLE_HEIGHT = 12;
    /** 数字输入框宽度 */
    public static final int NUMBER_FIELD_WIDTH = 56;
    /** 数字输入框高度 */
    public static final int NUMBER_FIELD_HEIGHT = 16;
    /** 滑条宽度 */
    public static final int SLIDER_WIDTH = 80;
    /** 滑条高度 */
    public static final int SLIDER_HEIGHT = 12;
    /** 按钮高度 */
    public static final int BUTTON_HEIGHT = 18;
    /** 分类按钮圆角半径（模拟） */
    public static final int CATEGORY_BUTTON_RADIUS = 2;
    /** 卡片圆角半径（模拟） */
    public static final int CARD_RADIUS = 2;

    // ==================== 动画常量 ====================

    /** 入场动画单项延迟（毫秒） */
    public static final float ENTRY_STAGGER_MS = 18f;
    /** 入场动画持续时间（毫秒） */
    public static final float ENTRY_DURATION_MS = 160f;
    /** 分类切换动画持续时间（毫秒） */
    public static final float CATEGORY_SWITCH_DURATION_MS = 120f;
    /** 数值变更反馈闪烁持续时间（毫秒） */
    public static final float CHANGE_FEEDBACK_MS = 160f;
    /** 保存成功反馈持续时间（毫秒） */
    public static final float SAVE_FEEDBACK_MS = 300f;
    /** 屏幕入场淡入持续时间（毫秒） */
    public static final float SCREEN_FADE_IN_MS = 100f;
    /** 面板级进入动画时长（毫秒，单区域主体） */
    public static final float SCREEN_ENTER_MS = 340f;
    /** 面板级退出动画时长（毫秒，单区域主体） */
    public static final float SCREEN_EXIT_MS = 240f;
    /** 分区从近侧滑入/滑出的距离（像素） */
    public static final float REGION_SLIDE_PX = 52f;
    /** 分区入场错开延迟（毫秒） */
    public static final float REGION_STAGGER_MS = 48f;
    /** 回弹过冲系数（easeOutBack 的 c1，约 1.15 为轻微回弹） */
    public static final float REGION_OVERSHOOT = 1.15f;
    /** 面板底壳轻微缩放起点 */
    public static final float SCREEN_ENTER_SCALE_FROM = 0.97f;
    /** 面板底壳退出缩放终点 */
    public static final float SCREEN_EXIT_SCALE_TO = 0.96f;
    /** 面板退出时下沉像素 */
    public static final float SCREEN_EXIT_SINK_PX = 12f;
    /** 面板进入时上升像素 */
    public static final float SCREEN_ENTER_RISE_PX = 12f;
    /** 进入起始透明度 */
    public static final float SCREEN_ENTER_ALPHA_FROM = 0.0f;
    // 区域索引（决定错开顺序与默认近侧方向）
    /** 区域：面板底壳 */
    public static final int REGION_PANEL = 0;
    /** 区域：左侧导航 */
    public static final int REGION_SIDEBAR = 1;
    /** 区域：顶部标题 */
    public static final int REGION_HEADER = 2;
    /** 区域：主内容列表 */
    public static final int REGION_CONTENT = 3;
    /** 区域：底部按钮 */
    public static final int REGION_FOOTER = 4;
    /** 分类切换动画时长（毫秒） */
    public static final float CATEGORY_TRANSITION_MS = 260f;
    /** 分类切换时旧页面向左位移（像素） */
    public static final float CATEGORY_EXIT_SHIFT_PX = 40f;
    /** 分类切换时新页面从右位移（像素） */
    public static final float CATEGORY_ENTER_SHIFT_PX = 40f;
    /** 悬停渐变过渡速率（0~1，越大越快） */
    public static final float HOVER_TRANSITION_RATE = 0.55f;
    /** 开关滑块动画阻尼（0~1，越小越滑） */
    public static final float TOGGLE_DAMPING = 0.42f;
    /** 滚动条最小高度 */
    public static final int SCROLLBAR_MIN_HEIGHT = 18;
    /** 滚动条宽度 */
    public static final int SCROLLBAR_WIDTH = 3;
    /** 扫描线间隔（像素） */
    public static final int SCANLINE_SPACING = 4;
    /** 径向渐变模拟层数 */
    public static final int RADIAL_GRADIENT_LAYERS = 8;

    // ==================== 颜色主题：深海梦境，去 AI 化 ====================

    // --- 背景层（深海 HUD 氛围） ---
    /** 背景主色：极深蓝灰 */
    public static final int COLOR_BG = 0xFF0A0D12;
    /** 背景径向渐变中心（略亮，模拟水下光晕） */
    public static final int COLOR_BG_CENTER = 0xFF0E141C;
    /** 背景径向渐变边缘（压暗） */
    public static final int COLOR_BG_EDGE = 0xFF06090D;
    /** 扫描线颜色（极淡） */
    public static final int COLOR_SCANLINE = 0x06000000;
    /** 侧边栏背景 */
    public static final int COLOR_SIDEBAR_BG = 0xFF0E1218;
    /** 内容区面板背景 */
    public static final int COLOR_PANEL_BG = 0xFF10141A;
    /** 外层背景叠加（轻微压暗四角） */
    public static final int COLOR_BG_OVERLAY = 0x22000000;

    // --- 卡片层 ---
    /** 卡片背景 */
    public static final int COLOR_CARD_BG = 0xFF161B22;
    /** 卡片悬停背景 */
    public static final int COLOR_CARD_HOVER = 0xFF1E242D;
    /** 卡片选中背景 */
    public static final int COLOR_CATEGORY_SELECTED_BG = 0xFF1F2731;

    // --- 边框与分割线 ---
    /** 分割线 */
    public static final int COLOR_DIVIDER = 0xFF1F252D;
    /** 轻微边框 */
    public static final int COLOR_BORDER = 0xFF252C36;

    // --- 文字 ---
    /** 主标题文字：冷白 */
    public static final int COLOR_TITLE = 0xFFE8ECEF;
    /** 标签文字 */
    public static final int COLOR_LABEL = 0xFFC8CDD4;
    /** 提示文字 */
    public static final int COLOR_HINT = 0xFF6E7682;
    /** 数值/次要说明文字 */
    public static final int COLOR_VALUE = 0xFF95A0AC;

    // --- 强调色（低饱和青绿，替代高饱和紫罗兰） ---
    /** 主强调色 */
    public static final int COLOR_ACCENT = 0xFF4ECDC4;
    /** 强调色暗色 */
    public static final int COLOR_ACCENT_DARK = 0xFF3A9A94;
    /** 强调色柔和 */
    public static final int COLOR_ACCENT_SOFT = 0x334ECDC4;
    /** 强调色发光（更淡，用于悬停光晕） */
    public static final int COLOR_ACCENT_GLOW = 0x184ECDC4;
    /** 分类选中态左侧能量条 */
    public static final int COLOR_CATEGORY_BAR = 0xFF4ECDC4;
    /** 分类选中态文字发光 */
    public static final int COLOR_CATEGORY_GLOW = 0x554ECDC4;
    /** 正面反馈：柔和青绿 */
    public static final int COLOR_SUCCESS = 0xFF5ECF9A;

    // --- 开关控件 ---
    /** 开关开启背景 */
    public static final int COLOR_TOGGLE_ON = 0xFF4ECDC4;
    /** 开关关闭背景 */
    public static final int COLOR_TOGGLE_OFF = 0xFF2C333D;
    /** 开关滑块 */
    public static final int COLOR_TOGGLE_THUMB = 0xFFE8ECEF;

    // --- 输入框 ---
    /** 输入框背景 */
    public static final int COLOR_FIELD_BG = 0xFF0B0E13;
    /** 输入框边框 */
    public static final int COLOR_FIELD_BORDER = 0xFF2C333D;
    /** 输入框聚焦边框 */
    public static final int COLOR_FIELD_FOCUS = 0xFF4ECDC4;
    /** 输入框错误边框 */
    public static final int COLOR_FIELD_ERROR = 0xFFFF6B6B;
    /** 输入框聚焦光晕 */
    public static final int COLOR_FIELD_FOCUS_GLOW = 0xFF4ECDC4;
    /** 输入框错误光晕 */
    public static final int COLOR_FIELD_ERROR_GLOW = 0xFFFF6B6B;

    // --- 按钮 ---
    /** 保存按钮背景 */
    public static final int COLOR_SAVE_BG = 0xFF4ECDC4;
    /** 保存按钮文字 */
    public static final int COLOR_SAVE_TEXT = 0xFF0A0D12;
    /** 重置按钮背景 */
    public static final int COLOR_RESET_BG = 0xFF1A2028;
    /** 重置按钮文字 */
    public static final int COLOR_RESET_TEXT = 0xFFC8CDD4;

    // --- 反馈 ---
    /** 变更反馈高亮 */
    public static final int COLOR_CHANGE_FLASH = 0x1A4ECDC4;
    /** 错误提示文字 */
    public static final int COLOR_ERROR_TEXT = 0xFFFF8A8A;
    /** 行左侧能量条 */
    public static final int COLOR_ROW_ACCENT_BAR = 0xFF4ECDC4;
    /** 行悬停顶部能量光 */
    public static final int COLOR_ROW_TOP_GLOW = 0x334ECDC4;
    /** 保存成功绿色闪烁 */
    public static final int COLOR_SAVE_FLASH = 0x225ECF9A;

    // --- 滚动条 ---
    /** 滚动条轨道 */
    public static final int COLOR_SCROLLBAR_TRACK = 0x10FFFFFF;
    /** 滚动条滑块 */
    public static final int COLOR_SCROLLBAR_THUMB = 0x554ECDC4;
    /** 滚动条滑块悬停 */
    public static final int COLOR_SCROLLBAR_THUMB_HOVER = 0x884ECDC4;
}
