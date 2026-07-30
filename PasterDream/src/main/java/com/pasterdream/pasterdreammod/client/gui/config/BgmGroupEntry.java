package com.pasterdream.pasterdreammod.client.gui.config;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * BGM 曲目可折叠配置组。
 * <p>
 * 每个实例对应一个群系 BGM 曲目，折叠时显示曲目名和展开指示符，
 * 展开后显示启用开关 + 音量滑条，共占用 3 行高度。
 * 不继承 {@link ConfigEntry}，由 {@link PDConfigScreen} 中的渲染循环特殊处理。
 *
 * @author PasterDream
 */
public class BgmGroupEntry implements GuiEventListener {

    // ==================== 布局常量 ====================

    /** 折叠时高度（1 行） */
    public static final int HEIGHT_COLLAPSED = ConfigStyles.ROW_HEIGHT;
    /** 展开时高度（3 行：标题 + 开关行 + 音量行） */
    public static final int HEIGHT_EXPANDED = ConfigStyles.ROW_HEIGHT * 3;

    // ==================== 内部控件类 ====================

    /**
     * 自定义开关按钮（矩形 MC 风），与 {@code ConfigEntry.ToggleButton} 视觉一致。
     */
    private static class BgmToggle extends AbstractWidget {

        private boolean value;
        private final Runnable onChange;
        private float thumbOffset;
        private float pressAnim;
        private long pressStart;

        BgmToggle(int x, int y, int width, int height, boolean initial, Runnable onChange) {
            super(x, y, width, height, Component.empty());
            this.value = initial;
            this.onChange = onChange;
            this.thumbOffset = value ? width - height : 0;
        }

        void setValue(boolean v) { this.value = v; }

        boolean getValue() { return value; }

        @Override
        protected void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
            float target = value ? width - height : 0;
            thumbOffset += (target - thumbOffset) * ConfigStyles.TOGGLE_DAMPING;

            if (pressStart > 0) {
                long elapsed = System.currentTimeMillis() - pressStart;
                pressAnim = elapsed < 100 ? 1f - elapsed / 100f : 0;
                if (pressAnim <= 0) pressStart = 0;
            }

            boolean hovered = isMouseOver(mouseX, mouseY);

            // 轨道
            int trackY = getY() + 3;
            int trackH = height - 6;
            gui.fill(getX(), trackY, getX() + width, trackY + trackH,
                    value ? ConfigStyles.COLOR_TOGGLE_ON : ConfigStyles.COLOR_TOGGLE_OFF);
            if (hovered) {
                gui.fill(getX(), trackY, getX() + width, trackY + 1, 0x18FFFFFF);
                gui.fill(getX(), trackY + trackH - 1, getX() + width, trackY + trackH, 0x08000000);
            }

            // 滑块
            int thumbSize = height - 4;
            int thumbX = getX() + (int) thumbOffset + 1;
            int thumbY = getY() + 2;
            int pressOff = (int) (pressAnim * 1);
            int ts = thumbSize - pressOff * 2;
            int tx = thumbX + pressOff;
            int ty = thumbY + pressOff;
            gui.fill(tx + 1, ty + 1, tx + ts + 1, ty + ts + 1, 0x44000000);
            gui.fill(tx, ty, tx + ts, ty + ts, ConfigStyles.COLOR_TOGGLE_THUMB);
            gui.fill(tx, ty, tx + ts, ty + 1, 0x55FFFFFF);
            gui.fill(tx, ty, tx + 1, ty + ts, 0x55FFFFFF);
            gui.fill(tx, ty + ts - 1, tx + ts, ty + ts, 0x33000000);
            gui.fill(tx + ts - 1, ty, tx + ts, ty + ts, 0x33000000);
        }

        @Override
        public boolean mouseClicked(double mx, double my, int button) {
            if (visible && mx >= getX() && mx < getX() + width && my >= getY() && my < getY() + height) {
                value = !value;
                pressStart = System.currentTimeMillis();
                onChange.run();
                return true;
            }
            return false;
        }

        @Override
        protected void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput output) {
            defaultButtonNarrationText(output);
        }
    }

    /**
     * 音量滑条，与 {@code ConfigEntry.SliderEntry.Slider} 视觉一致。
     */
    private static class VolumeSlider extends AbstractSliderButton {

        private final Runnable onApply;

        VolumeSlider(int x, int y, int width, int height, double ratio, Runnable onApply, Font font) {
            super(x, y, width, height, Component.empty(), ratio);
            this.onApply = onApply;
            this.font = font;
        }

        private final Font font;

        void setRatio(double r) { this.value = r; }

        /** 获取滑条当前比率（0.0 ~ 1.0），供外部计算音量值 */
        double getRatio() { return this.value; }

        @Override
        protected void updateMessage() {}

        @Override
        protected void applyValue() { onApply.run(); }

        @Override
        public void renderWidget(GuiGraphics gui, int mx, int my, float pt) {
            int trackH = 4;
            int trackY = getY() + (getHeight() - trackH) / 2;
            gui.fill(getX(), trackY, getX() + width, trackY + trackH, ConfigStyles.COLOR_FIELD_BORDER);
            int fillW = (int) (width * value);
            gui.fill(getX(), trackY, getX() + fillW, trackY + trackH, ConfigStyles.COLOR_ACCENT);

            int thumbSize = getHeight();
            int thumbX = getX() + fillW - thumbSize / 2;
            int thumbY = getY();
            gui.fill(thumbX + 1, thumbY + 1, thumbX + thumbSize + 1, thumbY + thumbSize + 1, 0x22000000);
            gui.fill(thumbX, thumbY, thumbX + thumbSize, thumbY + thumbSize, ConfigStyles.COLOR_TOGGLE_THUMB);

            String text = String.format("%.0f%%", value * 100);
            int textX = getX() + width + 5;
            int textY = getY() + (getHeight() - font.lineHeight) / 2 + 1;
            gui.drawString(font, text, textX, textY, ConfigStyles.COLOR_VALUE);
        }
    }

    // ==================== BgmGroupEntry 主体 ====================

    private final Minecraft mc;
    private final Font font;
    private final ConfigCategory category;
    private final int index;
    private final String musicName;
    private final Component label;

    /** 启用开关配置值 */
    private final ModConfigSpec.ConfigValue<Boolean> switchConfig;
    /** 音量配置值 */
    private final ModConfigSpec.ConfigValue<Double> volumeConfig;

    /** 当前待保存的开关值 */
    private boolean pendingSwitch;
    /** 当前待保存的音量值 */
    private double pendingVolume;

    /** 默认开关值（用于恢复） */
    private final boolean defaultSwitch;
    /** 默认音量值（用于恢复） */
    private final double defaultVolume;

    /** 启用开关控件 */
    private final BgmToggle toggleWidget;
    /** 音量滑条控件 */
    private final VolumeSlider sliderWidget;

    /** 是否处于展开状态 */
    private boolean expanded;

    /** 实时保存回调：每次调整后立即调用以持久化配置 */
    private final @Nullable Runnable saveCallback;

    /** 悬停动画插值（用于标题行） */
    private float hoverLerp;
    /** 是否正在标题行上悬停 */
    private boolean headerHovered;

    /** 高度变化动画进度 */
    private float heightAnimProgress = 0f;
    private boolean animatingExpansion = false;
    private long animStartTime;

    public BgmGroupEntry(ConfigCategory category, int index,
                         ModConfigSpec.ConfigValue<Boolean> switchConfig,
                         ModConfigSpec.ConfigValue<Double> volumeConfig,
                         String musicName,
                         @Nullable Runnable saveCallback) {
        this.mc = Minecraft.getInstance();
        this.font = mc.font;
        this.category = category;
        this.index = index;
        this.musicName = musicName;
        this.switchConfig = switchConfig;
        this.volumeConfig = volumeConfig;
        this.saveCallback = saveCallback;

        this.pendingSwitch = switchConfig.get();
        this.pendingVolume = volumeConfig.get();

        // 保存默认值用于 reset
        this.defaultSwitch = pendingSwitch;
        this.defaultVolume = pendingVolume;

        this.label = Component.translatable("gui.pasterdream.config.bgm_" + musicName);

        // 创建控件（坐标和可见性由 PDConfigScreen 的渲染循环控制）
        this.toggleWidget = new BgmToggle(0, 0, ConfigStyles.TOGGLE_WIDTH, ConfigStyles.TOGGLE_HEIGHT,
                pendingSwitch, this::onToggleChanged);
        double ratio = (pendingVolume - 0.0) / (2.0 - 0.0); // min=0, max=2
        this.sliderWidget = new VolumeSlider(0, 0, ConfigStyles.SLIDER_WIDTH, ConfigStyles.SLIDER_HEIGHT,
                ratio, this::onSliderChanged, font);

        this.expanded = false;
        updateWidgetVisibility();
    }

    // ==================== 属性 ====================

    public ConfigCategory getCategory() { return category; }
    public int getIndex() { return index; }
    public String getMusicName() { return musicName; }
    public boolean isExpanded() { return expanded; }

    /**
     * 获取当前视觉高度（含展开动画过渡）。
     *
     * @return 当前帧的视觉高度（px）
     */
    public int getVisualHeight() {
        if (expanded && heightAnimProgress >= 1f) return HEIGHT_EXPANDED;
        if (!expanded && heightAnimProgress <= 0f) return HEIGHT_COLLAPSED;
        // 动画过渡中
        int diff = HEIGHT_EXPANDED - HEIGHT_COLLAPSED;
        return HEIGHT_COLLAPSED + (int) (diff * heightAnimProgress);
    }

    /**
     * 获取展开动画进度（0=折叠, 1=完全展开）。
     */
    public float getExpandProgress() {
        return heightAnimProgress;
    }

    /**
     * 展开或折叠此分组。
     *
     * @param expand true=展开, false=折叠
     */
    public void setExpanded(boolean expand) {
        if (this.expanded == expand) return;
        this.expanded = expand;
        this.animStartTime = System.currentTimeMillis();
        this.animatingExpansion = true;
        updateWidgetVisibility();
    }

    /** 切换展开/折叠状态 */
    public void toggleExpanded() { setExpanded(!expanded); }

    public boolean hasPendingChanges() {
        return !switchConfig.get().equals(pendingSwitch)
                || !volumeConfig.get().equals(pendingVolume);
    }

    /** 保存待提交值到底层配置 */
    public void save() {
        if (!switchConfig.get().equals(pendingSwitch)) {
            switchConfig.set(pendingSwitch);
        }
        if (!volumeConfig.get().equals(pendingVolume)) {
            volumeConfig.set(pendingVolume);
        }
    }

    /** 恢复为默认值 */
    public void resetToDefault() {
        pendingSwitch = defaultSwitch;
        toggleWidget.setValue(pendingSwitch);
        pendingVolume = defaultVolume;
        double ratio = (pendingVolume - 0.0) / (2.0 - 0.0);
        sliderWidget.setRatio(ratio);
    }

    // ==================== 内部回调 ====================

    private void onToggleChanged() {
        pendingSwitch = toggleWidget.getValue();
        // 实时应用到底层配置值并持久化
        switchConfig.set(pendingSwitch);
        if (saveCallback != null) saveCallback.run();
        PasterDreamMod.LOGGER.debug("[BgmGroupEntry] 实时保存开关: {}={}", musicName, pendingSwitch);
    }

    private void onSliderChanged() {
        // 从滑条比率映射到实际音量值（0.0 ~ 2.0）
        pendingVolume = sliderWidget.getRatio() * 2.0;
        // 实时应用到底层配置值并持久化
        volumeConfig.set(pendingVolume);
        if (saveCallback != null) saveCallback.run();
        PasterDreamMod.LOGGER.debug("[BgmGroupEntry] 实时保存音量: {}={}", musicName, pendingVolume);
    }

    private void updateWidgetVisibility() {
        toggleWidget.visible = expanded;
        sliderWidget.visible = expanded;
    }

    // ==================== 动画更新 ====================

    /** 每帧由渲染循环调用，更新展开动画进度 */
    public void tickAnimation() {
        if (!animatingExpansion) return;
        long elapsed = System.currentTimeMillis() - animStartTime;
        float duration = 200f; // 展开/折叠动画 200ms
        float raw = Math.min(1f, elapsed / duration);
        // easeOutCubic
        float eased = 1f - (float) Math.pow(1f - raw, 3);
        heightAnimProgress = expanded ? eased : 1f - eased;
        if (raw >= 1f) {
            animatingExpansion = false;
            heightAnimProgress = expanded ? 1f : 0f;
        }
    }

    // ==================== 渲染 ====================

    /**
     * 渲染 BGM 配置组。
     *
     * @param gui      绘制上下文
     * @param x        内容区 X（不含滑入偏移）
     * @param y        顶部 Y 坐标
     * @param rowWidth 行宽
     * @param mouseX   鼠标 X
     * @param mouseY   鼠标 Y
     * @param pt       部分 tick
     */
    public void render(GuiGraphics gui, int x, int y, int rowWidth, int mouseX, int mouseY, float pt) {
        int baseY = y;
        tickAnimation();

        // ===== 标题行 =====
        renderHeader(gui, x, baseY, rowWidth, mouseX, mouseY, pt);

        if (getExpandProgress() <= 0f) return;

        // ===== 展开区（裁剪至当前动画高度） =====
        int subY0 = baseY + ConfigStyles.ROW_HEIGHT; // 子行 1：开关
        int subY1 = baseY + ConfigStyles.ROW_HEIGHT * 2; // 子行 2：音量

        // 计算展开区的裁剪高度：总视觉高 - 标题行高
        int expandedVisualH = getVisualHeight() - ConfigStyles.ROW_HEIGHT;
        // 使用 enableScissor 对展开区进行垂直裁剪，使折叠/展开动画中内容随高度收缩平滑消失/出现
        gui.enableScissor(x, subY0, x + rowWidth, subY0 + expandedVisualH);

        // 子行背景（深色区分）
        int subBgColor = ConfigStyles.COLOR_CARD_BG & 0x00FFFFFF | 0x10000000;
        gui.fill(x, subY0, x + rowWidth, subY1 + ConfigStyles.ROW_HEIGHT, subBgColor);

        // --- 开关行 ---
        // 标签
        Component toggleLabel = Component.translatable("gui.pasterdream.config.bgm_enable");
        gui.drawString(font, toggleLabel, x + ConfigStyles.ROW_PADDING_X + 16, subY0 + 5,
                ConfigStyles.COLOR_HINT);

        // 开关控件
        int controlX = x + rowWidth - ConfigStyles.ROW_PADDING_X - ConfigStyles.TOGGLE_WIDTH;
        toggleWidget.setX(controlX);
        toggleWidget.setY(subY0 + ConfigStyles.ROW_HEIGHT / 2 - toggleWidget.getHeight() / 2);
        toggleWidget.render(gui, mouseX, mouseY, pt);

        // --- 音量行 ---
        Component volLabel = Component.translatable("gui.pasterdream.config.bgm_volume");
        gui.drawString(font, volLabel, x + ConfigStyles.ROW_PADDING_X + 16, subY1 + 5,
                ConfigStyles.COLOR_HINT);

        // 音量百分比前置标签
        String pct = String.format("%.0f%%", sliderWidget.getRatio() * 100);

        // 滑条控件（含右侧百分比文字）
        int sliderX = x + ConfigStyles.ROW_PADDING_X + 16 + font.width(volLabel) + 12;
        int maxSliderW = controlX - sliderX + ConfigStyles.TOGGLE_WIDTH;
        int sliderW = Math.min(ConfigStyles.SLIDER_WIDTH, Math.max(80, maxSliderW));
        sliderWidget.setX(sliderX);
        sliderWidget.setY(subY1 + ConfigStyles.ROW_HEIGHT / 2 - sliderWidget.getHeight() / 2);
        sliderWidget.setWidth(sliderW);
        sliderWidget.render(gui, mouseX, mouseY, pt);

        // 分割线（子行之间）
        gui.fill(x, subY0 + ConfigStyles.ROW_HEIGHT - 1, x + rowWidth, subY0 + ConfigStyles.ROW_HEIGHT,
                ConfigStyles.COLOR_DIVIDER);

        // 关闭展开区的裁剪
        gui.disableScissor();
    }

    /**
     * 渲染标题行（曲目名 + 展开指示符）。
     */
    private void renderHeader(GuiGraphics gui, int x, int y, int rowWidth, int mouseX, int mouseY, float pt) {
        boolean hovered = mouseX >= x && mouseX < x + rowWidth && mouseY >= y && mouseY < y + ConfigStyles.ROW_HEIGHT;

        // 悬停动画
        if (hovered && hoverLerp < 1f) {
            hoverLerp = Math.min(1f, hoverLerp + ConfigStyles.HOVER_TRANSITION_RATE);
        } else if (!hovered && hoverLerp > 0f) {
            hoverLerp = Math.max(0f, hoverLerp - ConfigStyles.HOVER_TRANSITION_RATE);
        }
        headerHovered = hovered;

        // 背景
        int bgColor = ConfigStyles.lerpColor(ConfigStyles.COLOR_CARD_BG, ConfigStyles.COLOR_CARD_HOVER, hoverLerp);
        gui.fill(x, y, x + rowWidth, y + ConfigStyles.ROW_HEIGHT, bgColor);

        // 底部分割线
        gui.fill(x, y + ConfigStyles.ROW_HEIGHT - 1, x + rowWidth, y + ConfigStyles.ROW_HEIGHT,
                ConfigStyles.COLOR_DIVIDER);

        // 悬停时顶部光晕
        if (hoverLerp > 0.01f) {
            int glow = ((int) (0x33 * hoverLerp) << 24) | (ConfigStyles.COLOR_ROW_TOP_GLOW & 0x00FFFFFF);
            gui.fill(x, y, x + rowWidth, y + 1, glow);
        }

        // 左侧能量条
        int barW = (int) (2 + 2 * hoverLerp);
        if (barW > 0) {
            int glowAlpha = (int) (0x22 * hoverLerp);
            int glowColor = (glowAlpha << 24) | (ConfigStyles.COLOR_ROW_ACCENT_BAR & 0x00FFFFFF);
            gui.fill(x - 1, y + 4, x + barW + 1, y + ConfigStyles.ROW_HEIGHT - 4, glowColor);
            gui.fill(x, y + 5, x + barW - 1, y + ConfigStyles.ROW_HEIGHT - 5, ConfigStyles.COLOR_ROW_ACCENT_BAR);
        }

        // 曲目名称
        int labelX = x + ConfigStyles.ROW_PADDING_X;
        gui.drawString(font, label, labelX, y + 5, ConfigStyles.COLOR_LABEL);

        // 展开指示符（三角箭头）
        String arrow = expanded ? "▼" : "▶";
        int arrowX = x + rowWidth - ConfigStyles.ROW_PADDING_X - font.width(arrow);
        gui.drawString(font, arrow, arrowX, y + 5, ConfigStyles.COLOR_HINT);

        // 当前状态摘要
        String summary = expanded
                ? (pendingSwitch ? "ON" : "OFF")
                : String.format("%s | %d%%", pendingSwitch ? "ON" : "OFF", (int) (pendingVolume / 2.0 * 100));
        int summaryX = arrowX - font.width(summary) - 10;
        gui.drawString(font, summary, summaryX, y + 5, ConfigStyles.COLOR_VALUE);
    }

    // ==================== 事件处理 ====================

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 子控件
        if (expanded) {
            if (toggleWidget.mouseClicked(mouseX, mouseY, button)) return true;
            if (sliderWidget.mouseClicked(mouseX, mouseY, button)) return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (expanded && sliderWidget.mouseReleased(mouseX, mouseY, button)) return true;
        return false;
    }

    @Override
    public boolean mouseDragged(double mx, double my, int btn, double dx, double dy) {
        if (expanded && sliderWidget.mouseDragged(mx, my, btn, dx, dy)) return true;
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) { return false; }
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) { return false; }
    @Override
    public boolean charTyped(char codePoint, int modifiers) { return false; }
    @Override
    public void setFocused(boolean focused) {}
    @Override
    public boolean isFocused() { return false; }

    /** 点击了标题行（由 PDConfigScreen 检测，用于展开/折叠） */
    public boolean isHeaderClicked(double mouseX, double mouseY) {
        // 由外部判断，此处返回是否在标题区域的布尔值
        return headerHovered;
    }

    /** 获取当前是否是展开状态（不含动画） */
    public boolean isLogicallyExpanded() {
        return expanded;
    }
}
