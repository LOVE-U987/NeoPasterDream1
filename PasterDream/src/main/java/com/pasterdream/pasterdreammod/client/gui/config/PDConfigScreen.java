package com.pasterdream.pasterdreammod.client.gui.config;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.config.PDClientConfig;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * PasterDream 原生 Screen 配置界面
 * <p>
 * 左侧分类导航 + 右侧主配置区的紧凑双栏布局。BGM 设置整合为普通分类，
 * 不再常驻侧边栏，整体视觉更克制、去 AI 化。
 *
 * @author PasterDream
 */
public class PDConfigScreen extends Screen {

    private static final String LANG_PREFIX = "gui.pasterdream.config";

    private final Screen parent;
    private final List<ConfigEntry<?>> allEntries = new ArrayList<>();
    private final List<ConfigEntry<?>> visibleEntries = new ArrayList<>();
    private final List<AbstractWidget> categoryButtons = new ArrayList<>();
    private final List<AbstractWidget> footerButtons = new ArrayList<>();
    private final Map<ConfigCategory, Integer> categoryCounts = new EnumMap<>(ConfigCategory.class);

    private ConfigCategory selectedCategory = ConfigCategory.HUD;
    private int scrollOffset;
    private final long openTime;
    private long categorySwitchStart;
    private float categorySlideOffset;
    private int panelLeft;
    private int panelRight;
    private int panelTop;
    private int panelBottom;
    private int sidebarRight;
    private int contentX;
    private int contentWidth;
    private int listTop;
    private int listBottom;
    @Nullable
    private GuiEventListener focusedListener;

    /** 保存成功反馈动画 */
    private long saveFeedbackStart;
    /** 滚动条悬停过渡 */
    private float scrollbarHoverLerp;
    /** 是否正在滚动条上悬停 */
    private boolean scrollbarHovered;
    /** 屏幕淡入进度（0~1） */
    private float screenFadeIn;
    /** 屏幕淡入是否已完成 */
    private boolean screenFadeDone;

    /** 是否正在执行退出动画 */
    private boolean closing;
    /** 退出动画开始时间 */
    private long closeStartTime;
    /** 面板变换进度（0~1） */
    private float panelTransformProgress;

    /**
     * @param parent 父屏幕
     */
    public PDConfigScreen(Screen parent) {
        super(Component.translatable(LANG_PREFIX + ".title"));
        this.parent = parent;
        this.openTime = System.currentTimeMillis();
    }

    @Override
    protected void init() {
        super.init();

        panelLeft = ConfigStyles.PANEL_MARGIN_X;
        panelRight = this.width - ConfigStyles.PANEL_MARGIN_X;
        panelTop = ConfigStyles.PANEL_MARGIN_Y;
        panelBottom = this.height - ConfigStyles.PANEL_MARGIN_Y;
        sidebarRight = panelLeft + ConfigStyles.SIDEBAR_WIDTH;
        contentX = sidebarRight + ConfigStyles.CONTENT_LEFT_MARGIN;
        contentWidth = panelRight - ConfigStyles.CONTENT_LEFT_MARGIN - contentX;
        listTop = panelTop + ConfigStyles.HEADER_HEIGHT;
        listBottom = panelBottom - ConfigStyles.FOOTER_HEIGHT;

        visibleEntries.clear();
        categoryButtons.clear();
        footerButtons.clear();
        clearWidgets();

        if (allEntries.isEmpty()) {
            buildEntries();
        }
        rebuildVisibleEntries();

        buildCategoryButtons();
        buildFooterButtons();

        updateWidgetPositions();
    }

    /**
     * 构建全部配置项：Client HUD + San + Meltdream + Common + BGM 共 47 项。
     */
    @SuppressWarnings("unchecked")
    private void buildEntries() {
        int idx = 0;

        // ==================== Client HUD (4 items) ====================
        allEntries.add(new ConfigEntry.BooleanEntry(PDClientConfig.ENABLE_MOD_UI, ConfigCategory.HUD, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDClientConfig.STEALTH_DISPLAY_ATTRIBUTE_HUD, ConfigCategory.HUD, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDClientConfig.LOADING_GUI_TIPS, ConfigCategory.HUD, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDClientConfig.PASTER_HEALTH_HUD, ConfigCategory.HUD, idx++));

        // ==================== San 设置 (7 items) ====================
        allEntries.add(new ConfigEntry.BooleanEntry(PDCommonConfig.ENABLE_SAN_SYSTEM, ConfigCategory.SAN, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDClientConfig.SHOW_SAN_HUD, ConfigCategory.SAN, idx++));
        allEntries.add(new ConfigEntry.NumberEntry((ModConfigSpec.ConfigValue<Number>) (ModConfigSpec.ConfigValue<?>) PDClientConfig.SAN_TANK_XBASE,
                ConfigCategory.SAN, idx++, -5000, 5000));
        allEntries.add(new ConfigEntry.NumberEntry((ModConfigSpec.ConfigValue<Number>) (ModConfigSpec.ConfigValue<?>) PDClientConfig.SAN_TANK_YBASE,
                ConfigCategory.SAN, idx++, -5000, 5000));
        allEntries.add(new ConfigEntry.NumberEntry((ModConfigSpec.ConfigValue<Number>) (ModConfigSpec.ConfigValue<?>) PDClientConfig.SAN_HUD_SCALE,
                ConfigCategory.SAN, idx++, 0.5, 2.0));
        allEntries.add(new ConfigEntry.NumberEntry((ModConfigSpec.ConfigValue<Number>) (ModConfigSpec.ConfigValue<?>) PDClientConfig.SAN_HUD_ANCHOR,
                ConfigCategory.SAN, idx++, 0, 3));
        allEntries.add(new ConfigEntry.BooleanEntry(PDClientConfig.SAN_SHOW_VALUE_ALWAYS, ConfigCategory.SAN, idx++));

        // ==================== 融梦能量设置 (7 items) ====================
        allEntries.add(new ConfigEntry.BooleanEntry(PDCommonConfig.ENABLE_MELTDREAM_ENERGY_SYSTEM, ConfigCategory.MELTDREAM, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDClientConfig.SHOW_MELTDREAM_ENERGY_HUD, ConfigCategory.MELTDREAM, idx++));
        allEntries.add(new ConfigEntry.NumberEntry((ModConfigSpec.ConfigValue<Number>) (ModConfigSpec.ConfigValue<?>) PDClientConfig.MELTDREAMENERGY_TANK_XBASE,
                ConfigCategory.MELTDREAM, idx++, -5000, 5000));
        allEntries.add(new ConfigEntry.NumberEntry((ModConfigSpec.ConfigValue<Number>) (ModConfigSpec.ConfigValue<?>) PDClientConfig.MELTDREAMENERGY_TANK_YBASE,
                ConfigCategory.MELTDREAM, idx++, -5000, 5000));
        allEntries.add(new ConfigEntry.NumberEntry((ModConfigSpec.ConfigValue<Number>) (ModConfigSpec.ConfigValue<?>) PDClientConfig.MELTDREAM_ENERGY_HUD_SCALE,
                ConfigCategory.MELTDREAM, idx++, 0.5, 2.0));
        allEntries.add(new ConfigEntry.NumberEntry((ModConfigSpec.ConfigValue<Number>) (ModConfigSpec.ConfigValue<?>) PDClientConfig.MELTDREAM_ENERGY_HUD_ANCHOR,
                ConfigCategory.MELTDREAM, idx++, 0, 3));
        allEntries.add(new ConfigEntry.BooleanEntry(PDClientConfig.MELTDREAM_ENERGY_SHOW_VALUE_ALWAYS, ConfigCategory.MELTDREAM, idx++));

        // ==================== Common Basic (14 items) ====================
        allEntries.add(new ConfigEntry.BooleanEntry(PDCommonConfig.OVERWORLD_NIGHT_LOWERS_SAN, ConfigCategory.BASIC, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDCommonConfig.DYEDREAM_CRACK_GENERATE, ConfigCategory.BASIC, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDCommonConfig.LOW_SAN_DEBUFF, ConfigCategory.BASIC, idx++));
        allEntries.add(new ConfigEntry.NumberEntry((ModConfigSpec.ConfigValue<Number>) (ModConfigSpec.ConfigValue<?>) PDCommonConfig.CHEERUP_BUFF_THRESHOLD_VALUE,
                ConfigCategory.BASIC, idx++, 0, 200));
        allEntries.add(new ConfigEntry.NumberEntry((ModConfigSpec.ConfigValue<Number>) (ModConfigSpec.ConfigValue<?>) PDCommonConfig.MELTDREAM_CHEST_LEGEND_MULTIPLIER,
                ConfigCategory.BASIC, idx++, 0, 100));
        allEntries.add(new ConfigEntry.NumberEntry((ModConfigSpec.ConfigValue<Number>) (ModConfigSpec.ConfigValue<?>) PDCommonConfig.MELTDREAM_CHEST_RARE_MULTIPLIER,
                ConfigCategory.BASIC, idx++, 0, 100));
        allEntries.add(new ConfigEntry.NumberEntry((ModConfigSpec.ConfigValue<Number>) (ModConfigSpec.ConfigValue<?>) PDCommonConfig.SLEEP_SAN_RECOVERY_AMOUNT,
                ConfigCategory.BASIC, idx++, 0, 100));
        allEntries.add(new ConfigEntry.BooleanEntry(PDCommonConfig.LOW_SAN_PICTURE_JITTER, ConfigCategory.BASIC, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDCommonConfig.THE_ORIGIN_OF_THE_WORLD_INITIALLY_GENERATED_DYEDREAM_CRACK, ConfigCategory.BASIC, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDCommonConfig.MOD_ACCOUOCEMENT, ConfigCategory.BASIC, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDCommonConfig.IN_LAMP_SHADOW_GIVE_PALE_BONENEEDLE, ConfigCategory.BASIC, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDCommonConfig.NO_RETURN_DYEDREAM_CRACK, ConfigCategory.BASIC, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDCommonConfig.DYEDREAM_ORIGIN_SPAWNPOINT, ConfigCategory.BASIC, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDCommonConfig.SHADOW_NPC_THIRD_DIALOGUE_AFTER_TP_PLAYER_BACK_TO_OVERWORLD, ConfigCategory.BASIC, idx++));

        // ==================== Common Property (1 item) ====================
        allEntries.add(new ConfigEntry.NumberEntry((ModConfigSpec.ConfigValue<Number>) (ModConfigSpec.ConfigValue<?>) PDCommonConfig.PLAYER_TOTAL_TICK_UPDATE,
                ConfigCategory.PROPERTY, idx++, 2, 20));

        // ==================== Common Ban (4 items) ====================
        allEntries.add(new ConfigEntry.BooleanEntry(PDCommonConfig.BAN_ALL_THE_WINGS, ConfigCategory.BAN, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDCommonConfig.BAN_TERRA_SWORD, ConfigCategory.BAN, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDCommonConfig.BAN_FIRE_NECKLACE, ConfigCategory.BAN, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDCommonConfig.BAN_TIME_HOURGLASS, ConfigCategory.BAN, idx++));

        // ==================== BGM (8 items) ====================
        allEntries.add(new ConfigEntry.BooleanEntry(PDClientConfig.BGM_MASTER_ENABLED, ConfigCategory.BGM, idx++));
        allEntries.add(new ConfigEntry.NumberEntry((ModConfigSpec.ConfigValue<Number>) (ModConfigSpec.ConfigValue<?>) PDClientConfig.BGM_MASTER_VOLUME,
                ConfigCategory.BGM, idx++, 0.0, 1.0));
        allEntries.add(new ConfigEntry.BooleanEntry(PDClientConfig.BGM_DYEDREAM_WORLD, ConfigCategory.BGM, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDClientConfig.BGM_DREAM_HEATH, ConfigCategory.BGM, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDClientConfig.BGM_DREAM_DELTA, ConfigCategory.BGM, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDClientConfig.BGM_DREAM_TAIGA, ConfigCategory.BGM, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDClientConfig.BGM_SWEETDREAM, ConfigCategory.BGM, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDClientConfig.BGM_SNOWFALL_DREAM, ConfigCategory.BGM, idx++));

        // 统计每个分类的配置数量
        categoryCounts.clear();
        for (ConfigCategory c : ConfigCategory.values()) categoryCounts.put(c, 0);
        for (ConfigEntry<?> entry : allEntries) categoryCounts.merge(entry.getCategory(), 1, Integer::sum);
    }

    private void rebuildVisibleEntries() {
        visibleEntries.clear();
        for (ConfigEntry<?> entry : allEntries) {
            if (entry.getCategory() == selectedCategory) visibleEntries.add(entry);
        }
        scrollOffset = 0;
    }

    private void buildCategoryButtons() {
        int y = listTop + 2;
        for (ConfigCategory category : ConfigCategory.values()) {
            FlatButton btn = new FlatButton(panelLeft + 6, y, ConfigStyles.SIDEBAR_WIDTH - 12,
                    ConfigStyles.CATEGORY_BUTTON_HEIGHT,
                    category.getTitle(),
                    ConfigStyles.COLOR_SIDEBAR_BG,
                    ConfigStyles.COLOR_CARD_HOVER,
                    ConfigStyles.COLOR_ACCENT,
                    ConfigStyles.COLOR_LABEL,
                    b -> switchCategory(category));
            categoryButtons.add(btn);
            addRenderableWidget(btn);
            y += ConfigStyles.CATEGORY_BUTTON_HEIGHT + ConfigStyles.CATEGORY_BUTTON_GAP;
        }
        updateCategoryButtonStyles();
    }

    private void buildFooterButtons() {
        int btnWidth = 72;
        int btnHeight = ConfigStyles.BUTTON_HEIGHT;
        int gap = 6;
        int startX = panelRight - btnWidth * 2 - gap;
        int y = panelBottom - ConfigStyles.FOOTER_HEIGHT + 6;

        FlatButton resetBtn = new FlatButton(startX, y, btnWidth, btnHeight,
                Component.translatable(LANG_PREFIX + ".reset"),
                ConfigStyles.COLOR_RESET_BG,
                ConfigStyles.COLOR_CARD_HOVER,
                ConfigStyles.COLOR_DIVIDER,
                ConfigStyles.COLOR_RESET_TEXT,
                b -> resetAll());

        FlatButton saveBtn = new FlatButton(startX + btnWidth + gap, y, btnWidth, btnHeight,
                Component.translatable(LANG_PREFIX + ".save"),
                ConfigStyles.COLOR_SAVE_BG,
                ConfigStyles.COLOR_ACCENT,
                ConfigStyles.COLOR_ACCENT_DARK,
                ConfigStyles.COLOR_SAVE_TEXT,
                b -> saveConfig());

        footerButtons.add(resetBtn);
        footerButtons.add(saveBtn);
        addRenderableWidget(resetBtn);
        addRenderableWidget(saveBtn);
    }

    private void switchCategory(ConfigCategory category) {
        if (category == selectedCategory) return;
        if (focusedListener != null) {
            focusedListener.setFocused(false);
            focusedListener = null;
        }
        selectedCategory = category;
        categorySwitchStart = System.currentTimeMillis();
        categorySlideOffset = 28f;
        rebuildVisibleEntries();
        updateWidgetPositions();
        updateCategoryButtonStyles();
    }

    private void updateCategoryButtonStyles() {
        for (int i = 0; i < categoryButtons.size(); i++) {
            FlatButton btn = (FlatButton) categoryButtons.get(i);
            boolean selected = ConfigCategory.values()[i] == selectedCategory;
            if (selected) {
                btn.setColors(
                        ConfigStyles.COLOR_CATEGORY_SELECTED_BG,
                        ConfigStyles.COLOR_CATEGORY_SELECTED_BG,
                        ConfigStyles.COLOR_ACCENT,
                        ConfigStyles.COLOR_ACCENT
                );
            } else {
                btn.setColors(
                        ConfigStyles.COLOR_SIDEBAR_BG,
                        ConfigStyles.COLOR_CARD_HOVER,
                        ConfigStyles.COLOR_ACCENT,
                        ConfigStyles.COLOR_LABEL
                );
            }
        }
    }

    private void updateWidgetPositions() {
        int controlX = contentX + contentWidth - ConfigStyles.ROW_PADDING_X - ConfigStyles.TOGGLE_WIDTH;
        for (ConfigEntry<?> entry : visibleEntries) {
            for (AbstractWidget widget : entry.widgets) {
                int widgetX = controlX - (widget.getWidth() - ConfigStyles.TOGGLE_WIDTH);
                widget.setX(widgetX);
            }
        }
    }

    // ========================================================================
    // 渲染
    // ========================================================================

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        // 屏幕淡入
        if (!screenFadeDone) {
            float elapsed = System.currentTimeMillis() - openTime;
            screenFadeIn = Math.min(1f, elapsed / ConfigStyles.SCREEN_FADE_IN_MS);
            if (screenFadeIn >= 1f) screenFadeDone = true;
        }

        // 进入/退出面板变换进度
        long animStart = closing ? closeStartTime : openTime;
        float animDuration = closing ? ConfigStyles.SCREEN_EXIT_MS : ConfigStyles.SCREEN_ENTER_MS;
        float raw = Math.max(0f, Math.min(1f, (System.currentTimeMillis() - animStart) / animDuration));
        panelTransformProgress = closing ? 1f - easeOutCubic(raw) : easeOutCubic(raw);

        // 若退出动画完成，切回父屏幕
        if (closing && raw >= 1f) {
            minecraft.setScreen(parent);
            return;
        }

        // 1. 背景
        renderBackground(gui, mouseX, mouseY, partialTick);

        // 2. 居中面板（带进入/退出变换）
        gui.pose().pushPose();
        applyPanelTransform(gui);
        renderPanel(gui);

        // 3. 侧边栏
        renderSidebar(gui, mouseX, mouseY);

        // 4. 内容区面板背景
        gui.fill(contentX - 4, listTop - 4, contentX + contentWidth + 4, listBottom + 4, ConfigStyles.COLOR_PANEL_BG);
        gui.fill(contentX - 4, listBottom + 4, contentX + contentWidth + 4, listBottom + 5, ConfigStyles.COLOR_DIVIDER);

        // 5. 顶部标题、分类信息与下划线
        int titleAlpha = (int) (0xFF * screenFadeIn);
        int titleColor = (titleAlpha << 24) | (ConfigStyles.COLOR_TITLE & 0x00FFFFFF);
        gui.drawString(font, title, contentX, panelTop + 7, titleColor);

        int currentCount = categoryCounts.getOrDefault(selectedCategory, 0);
        Component subtitle = Component.translatable(LANG_PREFIX + ".current_category",
                selectedCategory.getTitle(),
                Component.translatable(LANG_PREFIX + ".item_count", currentCount));
        int valueColor = (titleAlpha << 24) | (ConfigStyles.COLOR_VALUE & 0x00FFFFFF);
        gui.drawString(font, subtitle, contentX, panelTop + 7 + font.lineHeight + 1, valueColor);

        int titleLineY = panelTop + 7 + font.lineHeight * 2 + 3;
        gui.fill(contentX, titleLineY, contentX + Math.min(contentWidth, 120), titleLineY + 1, ConfigStyles.COLOR_ACCENT);

        // 6. 分类切换动画偏移衰减
        categorySlideOffset *= 0.72f;
        if (categorySlideOffset < 0.5f) categorySlideOffset = 0f;

        // 7. 内容区裁剪 + 滚动条
        gui.enableScissor(contentX, listTop, contentX + contentWidth, listBottom);
        renderEntries(gui, mouseX, mouseY, partialTick);
        gui.disableScissor();
        renderScrollbar(gui, mouseX, mouseY);

        // 8. 保存反馈闪烁
        renderSaveFeedback(gui);

        // 9. 底部按钮
        for (AbstractWidget btn : footerButtons) {
            btn.render(gui, mouseX, mouseY, partialTick);
        }

        gui.pose().popPose();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, this.width, this.height, ConfigStyles.COLOR_BG);
    }

    /**
     * 将面板变换应用到当前 PoseStack，实现进入/退出时的缩放与位移。
     *
     * @param gui 绘制上下文
     */
    private void applyPanelTransform(GuiGraphics gui) {
        float progress = panelTransformProgress;
        if (progress >= 1f) return;

        float scale = closing
                ? 1f - (1f - ConfigStyles.SCREEN_EXIT_SCALE_TO) * (1f - progress)
                : ConfigStyles.SCREEN_ENTER_SCALE_FROM + (1f - ConfigStyles.SCREEN_ENTER_SCALE_FROM) * progress;

        float offsetY = closing
                ? ConfigStyles.SCREEN_EXIT_SINK_PX * (1f - progress)
                : -ConfigStyles.SCREEN_ENTER_RISE_PX * (1f - progress);

        float centerX = (panelLeft + panelRight) / 2f;
        float centerY = (panelTop + panelBottom) / 2f;

        gui.pose().translate(centerX, centerY, 0);
        gui.pose().scale(scale, scale, 1f);
        gui.pose().translate(-centerX, -centerY + offsetY, 0);
    }

    /**
     * 渲染居中面板背景（深色卡片 + 极淡边框）
     */
    private void renderPanel(GuiGraphics gui) {
        gui.fill(panelLeft, panelTop, panelRight, panelBottom, ConfigStyles.COLOR_PANEL_BG);
        // 面板 1px 边框
        gui.fill(panelLeft, panelTop, panelRight, panelTop + 1, ConfigStyles.COLOR_BORDER);
        gui.fill(panelLeft, panelBottom - 1, panelRight, panelBottom, ConfigStyles.COLOR_BORDER);
        gui.fill(panelLeft, panelTop, panelLeft + 1, panelBottom, ConfigStyles.COLOR_BORDER);
        gui.fill(panelRight - 1, panelTop, panelRight, panelBottom, ConfigStyles.COLOR_BORDER);
    }

    private void renderSidebar(GuiGraphics gui, int mouseX, int mouseY) {
        gui.fill(panelLeft, panelTop, sidebarRight, panelBottom, ConfigStyles.COLOR_SIDEBAR_BG);
        gui.fill(sidebarRight, panelTop, sidebarRight + 1, panelBottom, ConfigStyles.COLOR_DIVIDER);

        Component navTitle = Component.translatable(LANG_PREFIX + ".nav");
        gui.drawString(font, navTitle, panelLeft + 10, panelTop + 8, ConfigStyles.COLOR_TITLE);

        int titleLineY = panelTop + 8 + font.lineHeight + 3;
        gui.fill(panelLeft + 10, titleLineY, panelLeft + 10 + 36, titleLineY + 1, ConfigStyles.COLOR_ACCENT);

        for (int i = 0; i < categoryButtons.size(); i++) {
            FlatButton btn = (FlatButton) categoryButtons.get(i);
            btn.render(gui, mouseX, mouseY, 0);

            ConfigCategory category = ConfigCategory.values()[i];
            int count = categoryCounts.getOrDefault(category, 0);
            String countText = String.valueOf(count);
            int countWidth = font.width(countText);
            int badgeX = btn.getX() + btn.getWidth() - countWidth - 4;
            int badgeY = btn.getY() + (btn.getHeight() - font.lineHeight) / 2 + 1;
            int badgeColor = category == selectedCategory ? ConfigStyles.COLOR_ACCENT : ConfigStyles.COLOR_HINT;
            gui.drawString(font, countText, badgeX, badgeY, badgeColor);

            if (category == selectedCategory) {
                gui.fill(btn.getX() - 2, btn.getY(), btn.getX(), btn.getY() + btn.getHeight(), ConfigStyles.COLOR_ACCENT);
            }
        }

        // 底部提示
        int total = allEntries.size();
        Component totalHint = Component.translatable(LANG_PREFIX + ".total", total);
        int hintY = panelBottom - 12;
        gui.drawString(font, totalHint, panelLeft + 10, hintY, ConfigStyles.COLOR_HINT);
    }

    /**
     * 渲染主内容区滚动条
     */
    private void renderScrollbar(GuiGraphics gui, int mouseX, int mouseY) {
        int totalH = visibleEntries.size() * ConfigStyles.ROW_HEIGHT;
        int visibleH = listBottom - listTop;
        if (totalH <= visibleH) return;

        int scrollbarX = contentX + contentWidth - ConfigStyles.SCROLLBAR_WIDTH - 2;
        int scrollbarTrackH = visibleH;
        float ratio = (float) visibleH / totalH;
        int scrollbarThumbH = Math.max(ConfigStyles.SCROLLBAR_MIN_HEIGHT, (int) (scrollbarTrackH * ratio));
        float scrollRatio = (float) scrollOffset / (totalH - visibleH);
        int scrollbarThumbY = listTop + (int) (scrollRatio * (scrollbarTrackH - scrollbarThumbH));

        // 轨道
        gui.fill(scrollbarX, listTop, scrollbarX + ConfigStyles.SCROLLBAR_WIDTH, listTop + scrollbarTrackH,
                ConfigStyles.COLOR_SCROLLBAR_TRACK);

        // 滑块悬停检测
        boolean hovered = mouseX >= scrollbarX && mouseX < scrollbarX + ConfigStyles.SCROLLBAR_WIDTH + 4
                && mouseY >= scrollbarThumbY && mouseY < scrollbarThumbY + scrollbarThumbH;
        if (hovered && scrollbarHoverLerp < 1f) {
            scrollbarHoverLerp = Math.min(1f, scrollbarHoverLerp + 0.15f);
        } else if (!hovered && scrollbarHoverLerp > 0f) {
            scrollbarHoverLerp = Math.max(0f, scrollbarHoverLerp - 0.08f);
        }
        scrollbarHovered = hovered;

        int thumbColor = scrollbarHovered
                ? ConfigStyles.COLOR_SCROLLBAR_THUMB_HOVER
                : ConfigStyles.COLOR_SCROLLBAR_THUMB;
        gui.fill(scrollbarX, scrollbarThumbY, scrollbarX + ConfigStyles.SCROLLBAR_WIDTH, scrollbarThumbY + scrollbarThumbH,
                thumbColor);
    }

    private void renderEntries(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        long now = System.currentTimeMillis();
        int baseY = listTop - scrollOffset;
        int rowWidth = contentWidth;
        int x = contentX + (int) categorySlideOffset;

        for (int i = 0; i < visibleEntries.size(); i++) {
            ConfigEntry<?> entry = visibleEntries.get(i);

            float entryProgress = getEntryProgress(entry.getIndex(), now);
            if (entryProgress <= 0f) continue;

            int yOffset = (int) ((1f - entryProgress) * 14f);
            int y = baseY + i * ConfigStyles.ROW_HEIGHT + yOffset;

            if (y + ConfigStyles.ROW_HEIGHT < listTop || y > listBottom) {
                entry.widgets.forEach(w -> w.visible = false);
                continue;
            }
            entry.widgets.forEach(w -> w.visible = true);

            entry.render(gui, x, y, rowWidth, mouseX, mouseY, partialTick);
        }
    }

    private float getEntryProgress(int index, long now) {
        long elapsed = now - openTime;
        float delay = index * ConfigStyles.ENTRY_STAGGER_MS;
        float raw = (elapsed - delay) / ConfigStyles.ENTRY_DURATION_MS;
        raw = Math.max(0f, Math.min(1f, raw));
        return easeOutQuart(raw);
    }

    private float easeOutCubic(float t) {
        return 1f - (float) Math.pow(1f - t, 3);
    }

    private float easeOutQuart(float t) {
        return 1f - (float) Math.pow(1f - t, 4);
    }

    /**
     * 渲染保存成功后的绿色闪烁反馈
     */
    private void renderSaveFeedback(GuiGraphics gui) {
        if (saveFeedbackStart == 0) return;
        long elapsed = System.currentTimeMillis() - saveFeedbackStart;
        if (elapsed > ConfigStyles.SAVE_FEEDBACK_MS) {
            saveFeedbackStart = 0;
            return;
        }
        float progress = 1f - (elapsed / ConfigStyles.SAVE_FEEDBACK_MS);
        int alpha = (int) (progress * 0x44);
        gui.fill(panelLeft, panelBottom - ConfigStyles.FOOTER_HEIGHT, panelRight, panelBottom,
                (alpha << 24) | (ConfigStyles.COLOR_SUCCESS & 0x00FFFFFF));
    }

    // ========================================================================
    // 事件处理
    // ========================================================================

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (mouseX >= contentX && mouseX < contentX + contentWidth && mouseY >= listTop && mouseY < listBottom) {
            int totalH = visibleEntries.size() * ConfigStyles.ROW_HEIGHT;
            int visibleH = listBottom - listTop;
            int maxScroll = Math.max(0, totalH - visibleH);
            scrollOffset = (int) Math.max(0, Math.min(maxScroll, scrollOffset - scrollY * 14));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (closing || panelTransformProgress < 1f) return true;
        for (AbstractWidget widget : categoryButtons) {
            if (widget.mouseClicked(mouseX, mouseY, button)) return true;
        }
        for (AbstractWidget widget : footerButtons) {
            if (widget.mouseClicked(mouseX, mouseY, button)) return true;
        }

        long now = System.currentTimeMillis();

        // 主内容区
        int baseY = listTop - scrollOffset;
        int x = contentX + (int) categorySlideOffset;
        for (int i = 0; i < visibleEntries.size(); i++) {
            ConfigEntry<?> entry = visibleEntries.get(i);
            float entryProgress = getEntryProgress(entry.getIndex(), now);
            int yOffset = (int) ((1f - entryProgress) * 14f);
            int y = baseY + i * ConfigStyles.ROW_HEIGHT + yOffset;
            if (mouseX >= x && mouseX < x + contentWidth && mouseY >= y && mouseY < y + ConfigStyles.ROW_HEIGHT) {
                if (entry.mouseClicked(mouseX, mouseY, button)) {
                    focusedListener = entry;
                    return true;
                }
            }
        }

        if (focusedListener != null) {
            focusedListener.setFocused(false);
        }
        focusedListener = null;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (focusedListener != null) return focusedListener.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (focusedListener != null) return focusedListener.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (focusedListener != null && focusedListener.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (focusedListener != null && focusedListener.keyReleased(keyCode, scanCode, modifiers)) return true;
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (focusedListener != null && focusedListener.charTyped(codePoint, modifiers)) return true;
        return super.charTyped(codePoint, modifiers);
    }

    /**
     * 保存所有待保存的配置项
     */
    private void saveConfig() {
        boolean anyInvalid = false;
        for (ConfigEntry<?> entry : allEntries) {
            if (entry instanceof ConfigEntry.NumberEntry numberEntry && !numberEntry.isValid()) {
                anyInvalid = true;
                break;
            }
        }
        if (anyInvalid) {
            PasterDreamMod.LOGGER.warn("[PDConfigScreen] 存在无效输入，保存被阻止");
            return;
        }

        for (ConfigEntry<?> entry : allEntries) {
            entry.save();
        }
        saveFeedbackStart = System.currentTimeMillis();
        Minecraft.getInstance().player.displayClientMessage(
                Component.translatable(LANG_PREFIX + ".save.success"), true);
    }

    /**
     * 将所有配置项恢复为默认值
     */
    private void resetAll() {
        for (ConfigEntry<?> entry : allEntries) {
            entry.resetToDefault();
        }
        Minecraft.getInstance().player.displayClientMessage(
                Component.translatable(LANG_PREFIX + ".reset.success"), true);
    }

    @Override
    public void onClose() {
        if (closing) return;
        closing = true;
        closeStartTime = System.currentTimeMillis();
    }
}
