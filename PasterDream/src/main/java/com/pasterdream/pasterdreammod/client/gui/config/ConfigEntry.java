package com.pasterdream.pasterdreammod.client.gui.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 配置项抽象基类
 * <p>
 * 每个配置项负责：维护待保存值（pending）与默认值，渲染标签、提示、控件与变更反馈动画。
 *
 * @param <T> 配置值类型，通常为 Boolean / Integer / Double
 * @author PasterDream
 */
public abstract class ConfigEntry<T> implements GuiEventListener {

    protected final Minecraft minecraft;
    protected final Font font;
    protected final ModConfigSpec.ConfigValue<T> configValue;
    protected final ConfigCategory category;
    protected final T defaultValue;
    protected final int index;
    protected final String translationKey;
    protected final Component label;
    protected final Component tooltip;

    /** 待保存值，可能尚未写入底层配置 */
    protected T pendingValue;
    /** 上次提交的值，用于触发变更反馈动画 */
    protected T lastCommittedValue;
    /** 变更反馈动画起始时间，0 表示未播放 */
    protected long changeFeedbackStart;
    /** 悬停状态平滑过渡值（0~1） */
    protected float hoverLerp;
    /** 内部交互控件，由子类填充 */
    protected final List<AbstractWidget> widgets = new ArrayList<>();

    /**
     * @param configValue 底层配置值引用
     * @param category    所属分类
     * @param index       全局序号，用于入场动画错峰
     */
    @SuppressWarnings("unchecked")
    protected ConfigEntry(ModConfigSpec.ConfigValue<T> configValue, ConfigCategory category, int index) {
        this.minecraft = Minecraft.getInstance();
        this.font = minecraft.font;
        this.configValue = configValue;
        this.category = category;
        this.index = index;
        this.translationKey = buildTranslationKey(configValue.getPath());
        this.label = Component.translatable("gui.pasterdream.config." + translationKey);
        this.tooltip = Component.translatable("gui.pasterdream.config." + translationKey + ".tooltip");
        this.defaultValue = configValue.get();
        this.pendingValue = defaultValue;
        this.lastCommittedValue = defaultValue;
    }

    /**
     * 将配置路径（可能含空格）转换为翻译键下划线形式，跳过 TOML 顶层分类段。
     */
    private static String buildTranslationKey(List<String> path) {
        StringBuilder sb = new StringBuilder();
        int start = Math.min(1, path.size());
        for (int i = start; i < path.size(); i++) {
            if (i > start) sb.append("_");
            sb.append(path.get(i).trim().replace(" ", "_"));
        }
        return sb.toString();
    }

    public ConfigCategory getCategory() { return category; }
    public int getIndex() { return index; }
    public T getPendingValue() { return pendingValue; }
    public boolean hasPendingChanges() { return !configValue.get().equals(pendingValue); }
    public boolean isDirtyFromDefault() { return !defaultValue.equals(pendingValue); }

    /**
     * 将待保存值提交到底层配置
     */
    @SuppressWarnings("unchecked")
    public void save() {
        if (hasPendingChanges()) {
            // 对于整型配置，避免将整数以浮点形式写入 TOML
            Object valueToSet = pendingValue;
            if (pendingValue instanceof Number number && configValue.get() instanceof Integer) {
                valueToSet = number.intValue();
            }
            configValue.set((T) valueToSet);
            lastCommittedValue = pendingValue;
            changeFeedbackStart = System.currentTimeMillis();
        }
    }

    /**
     * 将当前待保存值恢复为默认值
     */
    public void resetToDefault() { setPendingValue(defaultValue); }

    public abstract void setPendingValue(T value);

    /**
     * 渲染配置项
     */
    public void render(GuiGraphics gui, int x, int y, int rowWidth, int mouseX, int mouseY, float partialTick) {
        boolean hovered = mouseX >= x && mouseX < x + rowWidth && mouseY >= y && mouseY < y + ConfigStyles.ROW_HEIGHT;

        // 平滑悬停过渡
        if (hovered && hoverLerp < 1f) {
            hoverLerp = Math.min(1f, hoverLerp + ConfigStyles.HOVER_TRANSITION_RATE);
        } else if (!hovered && hoverLerp > 0f) {
            hoverLerp = Math.max(0f, hoverLerp - ConfigStyles.HOVER_TRANSITION_RATE);
        }

        // 卡片背景：纯色 + 悬停过渡
        int bgColor = lerpColor(ConfigStyles.COLOR_CARD_BG, ConfigStyles.COLOR_CARD_HOVER, hoverLerp);
        gui.fill(x, y, x + rowWidth, y + ConfigStyles.ROW_HEIGHT, bgColor);

        // 卡片 1px 边框（仅在悬停时渐显）
        if (hoverLerp > 0.01f) {
            int borderColor = ((int) (0x22 * hoverLerp) << 24) | (ConfigStyles.COLOR_BORDER & 0x00FFFFFF);
            gui.fill(x, y, x + rowWidth, y + 1, borderColor);
            gui.fill(x, y + ConfigStyles.ROW_HEIGHT - 1, x + rowWidth, y + ConfigStyles.ROW_HEIGHT, borderColor);
        }

        // 底部分割线
        gui.fill(x, y + ConfigStyles.ROW_HEIGHT - 1, x + rowWidth, y + ConfigStyles.ROW_HEIGHT,
                ConfigStyles.COLOR_DIVIDER);

        // 悬停时顶部能量光：模拟能量从行顶升起
        if (hoverLerp > 0.01f) {
            int glowAlpha = (int) (0x33 * hoverLerp);
            int glowColor = (glowAlpha << 24) | (ConfigStyles.COLOR_ROW_TOP_GLOW & 0x00FFFFFF);
            gui.fill(x, y, x + rowWidth, y + 1, glowColor);
        }

        // 悬停时左侧能量条：平滑宽度过渡 + 发光外扩
        int barWidth = (int) (2 + 2 * hoverLerp);
        if (barWidth > 0) {
            int glowAlpha = (int) (0x22 * hoverLerp);
            int glowColor = (glowAlpha << 24) | (ConfigStyles.COLOR_ROW_ACCENT_BAR & 0x00FFFFFF);
            gui.fill(x - 1, y + 4, x + barWidth + 1, y + ConfigStyles.ROW_HEIGHT - 4, glowColor);
            gui.fill(x, y + 5, x + barWidth - 1, y + ConfigStyles.ROW_HEIGHT - 5, ConfigStyles.COLOR_ROW_ACCENT_BAR);
        }

        // 保存变更反馈闪烁
        renderChangeFeedback(gui, x, y, rowWidth);

        // 标签文字（左对齐，靠上）
        int labelX = x + ConfigStyles.ROW_PADDING_X;
        int labelY = y + 5;
        gui.drawString(font, label, labelX, labelY, ConfigStyles.COLOR_LABEL);

        // 提示文字（标签下方，紧凑）
        int tooltipY = labelY + font.lineHeight + 1;
        gui.drawString(font, tooltip, labelX, tooltipY, ConfigStyles.COLOR_HINT);

        // 控件垂直居中
        int controlCenterY = y + ConfigStyles.ROW_HEIGHT / 2;
        for (AbstractWidget widget : widgets) {
            widget.setY(controlCenterY - widget.getHeight() / 2);
            widget.render(gui, mouseX, mouseY, partialTick);
        }
    }

    /**
     * 渲染变更反馈闪烁（柔和单色）
     */
    protected void renderChangeFeedback(GuiGraphics gui, int x, int y, int rowWidth) {
        if (changeFeedbackStart == 0) return;
        long elapsed = System.currentTimeMillis() - changeFeedbackStart;

        if (elapsed < ConfigStyles.CHANGE_FEEDBACK_MS) {
            float progress = 1f - (elapsed / ConfigStyles.CHANGE_FEEDBACK_MS);
            int alpha = (int) (progress * 0x1A);
            int color = (alpha << 24) | (ConfigStyles.COLOR_SUCCESS & 0x00FFFFFF);
            gui.fill(x, y, x + rowWidth, y + ConfigStyles.ROW_HEIGHT, color);
        } else {
            changeFeedbackStart = 0;
        }
    }

    /**
     * 触发变更反馈动画
     */
    public void triggerChangeFeedback() { changeFeedbackStart = System.currentTimeMillis(); }

    /**
     * 线性插值两个 ARGB 颜色
     */
    protected static int lerpColor(int a, int b, float t) {
        int aA = (a >> 24) & 0xFF, aR = (a >> 16) & 0xFF, aG = (a >> 8) & 0xFF, aB = a & 0xFF;
        int bA = (b >> 24) & 0xFF, bR = (b >> 16) & 0xFF, bG = (b >> 8) & 0xFF, bB = b & 0xFF;
        int al = (int) (aA + (bA - aA) * t);
        int r = (int) (aR + (bR - aR) * t);
        int g = (int) (aG + (bG - aG) * t);
        int bl = (int) (aB + (bB - aB) * t);
        return (al << 24) | (r << 16) | (g << 8) | bl;
    }

    // ==================== 事件转发 ====================

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (AbstractWidget w : widgets) { if (w.mouseClicked(mouseX, mouseY, button)) return true; }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (AbstractWidget w : widgets) { if (w.mouseReleased(mouseX, mouseY, button)) return true; }
        return false;
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        for (AbstractWidget w : widgets) { if (w.mouseDragged(mx, my, button, dx, dy)) return true; }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double sx, double sy) {
        for (AbstractWidget w : widgets) { if (w.mouseScrolled(mx, my, sx, sy)) return true; }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (AbstractWidget w : widgets) { if (w.keyPressed(keyCode, scanCode, modifiers)) return true; }
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (AbstractWidget w : widgets) { if (w.keyReleased(keyCode, scanCode, modifiers)) return true; }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (AbstractWidget w : widgets) { if (w.charTyped(codePoint, modifiers)) return true; }
        return false;
    }

    @Override
    public void setFocused(boolean focused) {
        for (AbstractWidget w : widgets) w.setFocused(focused);
    }

    @Override
    public boolean isFocused() {
        for (AbstractWidget w : widgets) { if (w.isFocused()) return true; }
        return false;
    }

    @Nullable
    public GuiEventListener getFocused() {
        for (AbstractWidget w : widgets) { if (w.isFocused()) return w; }
        return null;
    }

    public Component getTooltip() { return tooltip; }

    // ========================================================================
    // 布尔配置项
    // ========================================================================

    public static class BooleanEntry extends ConfigEntry<Boolean> {

        public BooleanEntry(ModConfigSpec.ConfigValue<Boolean> configValue, ConfigCategory category, int index) {
            super(configValue, category, index);
            widgets.add(new ToggleButton(0, 0, ConfigStyles.TOGGLE_WIDTH, ConfigStyles.TOGGLE_HEIGHT,
                    Component.empty(), this::onToggle, pendingValue));
        }

        private void onToggle(boolean value) { setPendingValue(value); }

        @Override
        public void setPendingValue(Boolean value) {
            pendingValue = value;
            ((ToggleButton) widgets.get(0)).setValue(value);
        }
    }

    // ========================================================================
    // 数值配置项
    // ========================================================================

    public static class NumberEntry extends ConfigEntry<Number> {

        private final double min;
        private final double max;
        private final boolean integer;
        private final StyledEditBox editBox;

        public NumberEntry(ModConfigSpec.ConfigValue<Number> configValue, ConfigCategory category,
                           int index, double min, double max) {
            super(configValue, category, index);
            this.min = min;
            this.max = max;
            this.integer = configValue.get() instanceof Integer;
            this.editBox = new StyledEditBox(font, 0, 0, ConfigStyles.NUMBER_FIELD_WIDTH,
                    ConfigStyles.NUMBER_FIELD_HEIGHT, Component.empty());
            this.editBox.setValue(formatValue(configValue.get()));
            this.editBox.setResponder(this::onTextChanged);
            this.editBox.setFilter(this::isValidInput);
            widgets.add(editBox);
        }

        private String formatValue(Number value) {
            if (integer) return String.valueOf(value.intValue());
            String s = String.format("%.2f", value.doubleValue());
            return s.replace(".00", "");
        }

        private boolean isValidInput(String text) {
            if (text.isEmpty() || text.equals("-") || text.equals(".")) return true;
            try {
                double v = Double.parseDouble(text);
                return !Double.isNaN(v) && !Double.isInfinite(v);
            } catch (NumberFormatException e) { return false; }
        }

        private void onTextChanged(String text) {
            boolean valid = false;
            if (!text.isEmpty() && !text.equals("-") && !text.equals(".")) {
                try {
                    double v = Double.parseDouble(text);
                    valid = v >= min && v <= max;
                } catch (NumberFormatException ignored) {}
            }
            editBox.setValid(valid);
            if (valid) {
                double v = Double.parseDouble(text);
                pendingValue = integer ? Integer.valueOf((int) Math.round(v)) : Double.valueOf(v);
            }
        }

        @Override
        public void setPendingValue(Number value) {
            pendingValue = integer ? value.intValue() : value.doubleValue();
            editBox.setValue(formatValue(pendingValue));
            editBox.setValid(true);
        }

        public boolean isValid() { return editBox.isValid(); }

        @Override
        public void save() {
            if (!isValid()) return;
            super.save();
        }
    }

    // ========================================================================
    // 滑条配置项
    // ========================================================================

    /**
     * 双精度浮点滑条配置项。
     * <p>
     * 用于 BGM 音量等需要在连续范围内调节的数值配置，右侧显示当前百分比。
     */
    public static class SliderEntry extends ConfigEntry<Double> {

        private final double min;
        private final double max;
        private final Slider slider;

        /**
         * @param configValue 底层 Double 配置值引用
         * @param category    所属分类
         * @param index       全局序号
         * @param min         滑条最小值
         * @param max         滑条最大值
         */
        public SliderEntry(ModConfigSpec.ConfigValue<Double> configValue, ConfigCategory category,
                           int index, double min, double max) {
            super(configValue, category, index);
            this.min = min;
            this.max = max;
            double initialRatio = (configValue.get() - min) / (max - min);
            this.slider = new Slider(0, 0, ConfigStyles.SLIDER_WIDTH, ConfigStyles.SLIDER_HEIGHT,
                    Component.empty(), initialRatio);
            widgets.add(slider);
        }

        @Override
        public void setPendingValue(Double value) {
            pendingValue = value;
            slider.setValue((value - min) / (max - min));
        }

        /**
         * 将滑条比率映射到实际配置值。
         *
         * @param ratio 滑条位置（0.0 ~ 1.0）
         * @return 对应的实际值
         */
        private double mapToValue(double ratio) {
            return min + ratio * (max - min);
        }

        /**
         * 自定义滑条控件：轨道 + 已填充段 + 滑块 + 百分比文字。
         */
        private class Slider extends AbstractSliderButton {

            Slider(int x, int y, int width, int height, Component message, double value) {
                super(x, y, width, height, message, value);
            }

            /**
             * 设置滑条当前比率位置。
             *
             * @param ratio 新的比率位置（0.0 ~ 1.0）
             */
            void setValue(double ratio) {
                this.value = ratio;
            }

            @Override
            protected void updateMessage() {
                // 数值直接绘制在滑条上，无需通过 message 显示
            }

            @Override
            protected void applyValue() {
                pendingValue = mapToValue(value);
            }

            @Override
            public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
                int trackHeight = 4;
                int trackY = getY() + (getHeight() - trackHeight) / 2;

                // 轨道背景
                gui.fill(getX(), trackY, getX() + width, trackY + trackHeight,
                        ConfigStyles.COLOR_FIELD_BORDER);

                // 已填充段
                int fillWidth = (int) (width * value);
                gui.fill(getX(), trackY, getX() + fillWidth, trackY + trackHeight,
                        ConfigStyles.COLOR_ACCENT);

                // 滑块
                int thumbSize = getHeight();
                int thumbX = getX() + fillWidth - thumbSize / 2;
                int thumbY = getY();
                // 滑块阴影
                gui.fill(thumbX + 1, thumbY + 1, thumbX + thumbSize + 1, thumbY + thumbSize + 1,
                        0x22000000);
                // 滑块主体
                gui.fill(thumbX, thumbY, thumbX + thumbSize, thumbY + thumbSize,
                        ConfigStyles.COLOR_TOGGLE_THUMB);

                // 百分比文字（滑条右侧）
                String text = String.format("%.0f%%", value * 100);
                int textX = getX() + width + 5;
                int textY = getY() + (getHeight() - font.lineHeight) / 2 + 1;
                gui.drawString(font, text, textX, textY, ConfigStyles.COLOR_VALUE);
            }
        }
    }

    // ========================================================================
    // 样式化输入框
    // ========================================================================

    private static class StyledEditBox extends AbstractWidget {

        private final Font font;
        private String value = "";
        private int cursorPos;
        private int highlightPos;
        private long focusedTime;
        private boolean valid = true;
        private Consumer<String> responder;
        private Predicate<String> filter = s -> true;
        /** 聚焦光晕动画进度 */
        private float focusGlow;

        StyledEditBox(Font font, int x, int y, int width, int height, Component msg) {
            super(x, y, width, height, msg);
            this.font = font;
        }

        void setResponder(Consumer<String> r) { this.responder = r; }
        void setFilter(Predicate<String> f) { this.filter = f; }

        void setValue(String value) {
            if (filter.test(value)) {
                this.value = value;
                this.cursorPos = value.length();
                this.highlightPos = cursorPos;
                if (responder != null) responder.accept(value);
            }
        }

        String getValue() { return value; }
        void setValid(boolean v) { this.valid = v; }
        boolean isValid() { return valid; }

        @Override
        protected void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
            // 聚焦光晕动画
            if (isFocused() && focusGlow < 1f) {
                focusGlow = Math.min(1f, focusGlow + 0.18f);
            } else if (!isFocused() && focusGlow > 0f) {
                focusGlow = Math.max(0f, focusGlow - 0.12f);
            }

            boolean errored = !valid;
            int borderColor = errored ? ConfigStyles.COLOR_FIELD_ERROR
                    : (isFocused() ? ConfigStyles.COLOR_FIELD_FOCUS : ConfigStyles.COLOR_FIELD_BORDER);
            int glowColor = errored ? ConfigStyles.COLOR_FIELD_ERROR_GLOW : ConfigStyles.COLOR_FIELD_FOCUS_GLOW;

            // 聚焦光晕（极淡外发光）
            if (focusGlow > 0.01f) {
                int glowSpread = (int) (2 * focusGlow);
                gui.fill(getX() - glowSpread, getY() - glowSpread,
                        getX() + width + glowSpread, getY() + height + glowSpread,
                        ((int) (0x22 * focusGlow) << 24) | (glowColor & 0x00FFFFFF));
            }

            // 外边框 + 背景
            gui.fill(getX() - 1, getY() - 1, getX() + width + 1, getY() + height + 1, borderColor);
            gui.fill(getX(), getY(), getX() + width, getY() + height, ConfigStyles.COLOR_FIELD_BG);

            // 文字右对齐
            int textColor = valid ? ConfigStyles.COLOR_LABEL : ConfigStyles.COLOR_ERROR_TEXT;
            String display = value;
            int textW = font.width(display);
            int textX = getX() + width - 6 - textW;
            int textY = getY() + (height - font.lineHeight) / 2 + 1;
            gui.drawString(font, display, Math.max(getX() + 4, textX), textY, textColor);

            // 光标闪烁
            if (isFocused()) {
                long elapsed = System.currentTimeMillis() - focusedTime;
                if ((elapsed / 530) % 2 == 0) {
                    int cursorAdvance = font.width(display.substring(0, Math.min(cursorPos, display.length())));
                    int cursorX = Math.max(getX() + 4, textX) + cursorAdvance;
                    gui.fill(cursorX, textY - 1, cursorX + 1, textY + font.lineHeight, ConfigStyles.COLOR_TITLE);
                }
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (visible && mouseX >= getX() && mouseX < getX() + width && mouseY >= getY() && mouseY < getY() + height) {
                setFocused(true);
                focusedTime = System.currentTimeMillis();
                return true;
            }
            setFocused(false);
            return false;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (!isFocused()) return false;
            if (keyCode == 259 && cursorPos > 0) {
                value = value.substring(0, cursorPos - 1) + value.substring(cursorPos);
                cursorPos--;
                highlightPos = cursorPos;
                if (responder != null) responder.accept(value);
                return true;
            }
            if (keyCode == 262 && cursorPos < value.length()) { cursorPos++; highlightPos = cursorPos; return true; }
            if (keyCode == 263 && cursorPos > 0) { cursorPos--; highlightPos = cursorPos; return true; }
            return false;
        }

        @Override
        public boolean charTyped(char codePoint, int modifiers) {
            if (!isFocused()) return false;
            String candidate = value.substring(0, cursorPos) + codePoint + value.substring(cursorPos);
            if (filter.test(candidate)) {
                value = candidate;
                cursorPos++;
                highlightPos = cursorPos;
                if (responder != null) responder.accept(value);
                return true;
            }
            return false;
        }

        @Override
        protected void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput output) {
            defaultButtonNarrationText(output);
        }
    }

    // ========================================================================
    // 自定义开关按钮：矩形 MC 风
    // ========================================================================

    private static class ToggleButton extends AbstractWidget {

        private boolean value;
        private final Consumer<Boolean> onChange;
        private float thumbOffset;
        /** 按下动画进度（0~1） */
        private float pressAnim;
        private long pressStart;

        ToggleButton(int x, int y, int width, int height, Component msg, Consumer<Boolean> onChange, boolean initial) {
            super(x, y, width, height, msg);
            this.onChange = onChange;
            this.value = initial;
            this.thumbOffset = value ? width - height : 0;
        }

        void setValue(boolean value) {
            if (this.value != value) {
                this.value = value;
            }
        }

        @Override
        protected void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
            // 滑块目标位置
            float target = value ? width - height : 0;
            thumbOffset += (target - thumbOffset) * ConfigStyles.TOGGLE_DAMPING;

            // 按下动画
            if (pressStart > 0) {
                long elapsed = System.currentTimeMillis() - pressStart;
                if (elapsed < 100) {
                    pressAnim = 1f - (elapsed / 100f);
                } else {
                    pressAnim = 0;
                    pressStart = 0;
                }
            }

            boolean hovered = isMouseOver(mouseX, mouseY);

            // --- 轨道：纯矩形背景 ---
            int trackY = getY() + 3;
            int trackH = height - 6;
            int trackColor = value ? ConfigStyles.COLOR_TOGGLE_ON : ConfigStyles.COLOR_TOGGLE_OFF;
            gui.fill(getX(), trackY, getX() + width, trackY + trackH, trackColor);

            // 悬停时轨道上浮泛光
            if (hovered) {
                gui.fill(getX(), trackY, getX() + width, trackY + 1, 0x18FFFFFF);
                gui.fill(getX(), trackY + trackH - 1, getX() + width, trackY + trackH, 0x08000000);
            }

            // --- 滑块：纯矩形，MC 按钮式斜角 ---
            int thumbSize = height - 4;
            int thumbX = getX() + (int) thumbOffset + 1;
            int thumbY = getY() + 2;

            // 按下时轻微内缩
            int pressOffset = (int) (pressAnim * 1);
            int ts = thumbSize - pressOffset * 2;
            int tx = thumbX + pressOffset;
            int ty = thumbY + pressOffset;

            // 滑块阴影（右下）
            gui.fill(tx + 1, ty + 1, tx + ts + 1, ty + ts + 1, 0x44000000);
            // 滑块主体
            gui.fill(tx, ty, tx + ts, ty + ts, ConfigStyles.COLOR_TOGGLE_THUMB);
            // MC 风格左上高光
            gui.fill(tx, ty, tx + ts, ty + 1, 0x55FFFFFF);
            gui.fill(tx, ty, tx + 1, ty + ts, 0x55FFFFFF);
            // MC 风格右下暗角
            gui.fill(tx, ty + ts - 1, tx + ts, ty + ts, 0x33000000);
            gui.fill(tx + ts - 1, ty, tx + ts, ty + ts, 0x33000000);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (visible && mouseX >= getX() && mouseX < getX() + width && mouseY >= getY() && mouseY < getY() + height) {
                value = !value;
                pressStart = System.currentTimeMillis();
                onChange.accept(value);
                return true;
            }
            return false;
        }

        @Override
        protected void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput output) {
            defaultButtonNarrationText(output);
        }
    }
}
