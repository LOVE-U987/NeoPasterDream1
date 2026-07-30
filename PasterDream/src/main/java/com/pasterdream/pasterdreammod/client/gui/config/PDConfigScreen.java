package com.pasterdream.pasterdreammod.client.gui.config;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.util.AddonDetector;
import com.pasterdream.pasterdreammod.client.PDPackHandler;
import com.pasterdream.pasterdreammod.config.PDClientConfig;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ConfirmScreen;
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
    /** 滚动目标位置（惯性滚动用） */
    private float targetScrollOffset;
    private final long openTime;

    /** 分类切换动画开始时间 */
    private long categoryTransitionStart;
    /** 是否正在分类切换动画中 */
    private boolean inCategoryTransition;
    /** 切换前的配置项列表，用于旧页面向左淡出 */
    private final List<ConfigEntry<?>> previousVisibleEntries = new ArrayList<>();

    /** 侧边栏选中反光条当前 Y 坐标（lerp） */
    private float categoryBarY;
    /** 侧边栏选中反光条目标 Y 坐标 */
    private float categoryBarTargetY;
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
    /** 当前内容区 X 轴切换偏移量，用于鼠标点击命中检测 */
    private float categorySlideOffset;

    /** 「启用模组 UI」配置项引用 */
    @Nullable
    private ConfigEntry<Boolean> enableModUiEntry;
    /** 「启用模组 UI」进入界面时的原始值 */
    private boolean enableModUiOriginalValue = true;
    /** 是否在退出时需要触发资源包重载 */
    private boolean pendingResourceReload;
    /** 是否已经弹出过资源包重载确认对话框 */
    private boolean resourceReloadDialogShown;

    /** 当前有配置项的可见分类列表（排除空分类） */
    private final List<ConfigCategory> activeCategories = new ArrayList<>();

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
     * 构建全部配置项：Client HUD + San HUD + Meltdream HUD + Common + BGM + Debug。
     * San/融梦系统开关与数值项已拆分至 PasterDreamSanity/PasterDreamMeltDream 模组配置。
     */
    @SuppressWarnings("unchecked")
    private void buildEntries() {
        int idx = 0;

        // ==================== Client HUD (4 items) ====================
        ConfigEntry<Boolean> enableModUi = new ConfigEntry.BooleanEntry(PDClientConfig.ENABLE_MOD_UI, ConfigCategory.HUD, idx++);
        this.enableModUiEntry = enableModUi;
        this.enableModUiOriginalValue = enableModUi.getPendingValue();
        allEntries.add(enableModUi);
        allEntries.add(new ConfigEntry.BooleanEntry(PDClientConfig.STEALTH_DISPLAY_ATTRIBUTE_HUD, ConfigCategory.HUD, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDClientConfig.LOADING_GUI_TIPS, ConfigCategory.HUD, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDClientConfig.PASTER_HEALTH_HUD, ConfigCategory.HUD, idx++));

        // ==================== San HUD 设置 (6 items，仅 PasterDreamSanity 安装时显示) ====================
        boolean sanityLoaded = AddonDetector.isSanityLoaded();
        if (sanityLoaded) {
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
        }

        // ==================== 融梦能量 HUD 设置 (6 items，仅 PasterDreamMeltDream 安装时显示) ====================
        boolean meltDreamLoaded = AddonDetector.isMeltDreamLoaded();
        if (meltDreamLoaded) {
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
        }

        // ==================== Common Basic (7 items) ====================
        allEntries.add(new ConfigEntry.BooleanEntry(PDCommonConfig.DYEDREAM_CRACK_GENERATE, ConfigCategory.BASIC, idx++));
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

        // ==================== BGM (11 items) ====================
        allEntries.add(new ConfigEntry.BooleanEntry(PDClientConfig.BGM_MASTER_ENABLED, ConfigCategory.BGM, idx++));
        allEntries.add(new ConfigEntry.SliderEntry(PDClientConfig.BGM_MASTER_VOLUME,
                ConfigCategory.BGM, idx++, 0.0d, 1.0d));
        allEntries.add(new ConfigEntry.BooleanEntry(PDClientConfig.BGM_DYEDREAM_WORLD, ConfigCategory.BGM, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDClientConfig.BGM_DREAM_HEATH, ConfigCategory.BGM, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDClientConfig.BGM_DREAM_DELTA, ConfigCategory.BGM, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDClientConfig.BGM_DREAM_TAIGA, ConfigCategory.BGM, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDClientConfig.BGM_SWEETDREAM, ConfigCategory.BGM, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDClientConfig.BGM_SNOWFALL_DREAM, ConfigCategory.BGM, idx++));
        allEntries.add(new ConfigEntry.SliderEntry(PDClientConfig.BGM_WIND_JOURNEY_DEPARTURE_VOLUME,
                ConfigCategory.BGM, idx++, 0.0d, 2.0d));
        allEntries.add(new ConfigEntry.SliderEntry(PDClientConfig.BGM_WIND_JOURNEY_MIDSUMMER_VOLUME,
                ConfigCategory.BGM, idx++, 0.0d, 2.0d));
        allEntries.add(new ConfigEntry.SliderEntry(PDClientConfig.BGM_DREAM_MEADOW_DAISY_VOLUME,
                ConfigCategory.BGM, idx++, 0.0d, 2.0d));

        // ==================== Debug (4 items) ====================
        allEntries.add(new ConfigEntry.BooleanEntry(PDCommonConfig.ENABLE_DEBUG_LOG, ConfigCategory.DEBUG, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDCommonConfig.ENABLE_API_DEBUG_LOG, ConfigCategory.DEBUG, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDCommonConfig.ENABLE_MAIN_DEBUG_LOG, ConfigCategory.DEBUG, idx++));
        allEntries.add(new ConfigEntry.BooleanEntry(PDCommonConfig.ENABLE_SMOKETEST_DEBUG_LOG, ConfigCategory.DEBUG, idx++));

        // 统计每个分类的配置数量
        categoryCounts.clear();
        activeCategories.clear();
        for (ConfigCategory c : ConfigCategory.values()) categoryCounts.put(c, 0);
        for (ConfigEntry<?> entry : allEntries) categoryCounts.merge(entry.getCategory(), 1, Integer::sum);
        for (ConfigCategory c : ConfigCategory.values()) {
            if (categoryCounts.getOrDefault(c, 0) > 0) activeCategories.add(c);
        }
    }

    private void rebuildVisibleEntries() {
        // 若当前选中分类已无配置项（对应附属模组未安装），回退到 HUD
        if (categoryCounts.getOrDefault(selectedCategory, 0) == 0) {
            selectedCategory = ConfigCategory.HUD;
        }
        visibleEntries.clear();
        for (ConfigEntry<?> entry : allEntries) {
            if (entry.getCategory() == selectedCategory) visibleEntries.add(entry);
        }
        scrollOffset = 0;
        targetScrollOffset = 0f;
    }

    private void buildCategoryButtons() {
        int y = listTop + 2;
        for (ConfigCategory category : activeCategories) {
            FlatButton btn = new FlatButton(panelLeft + 6, y, ConfigStyles.SIDEBAR_WIDTH - 12,
                    ConfigStyles.CATEGORY_BUTTON_HEIGHT,
                    category.getTitle(),
                    ConfigStyles.COLOR_SIDEBAR_BG,
                    ConfigStyles.COLOR_CARD_HOVER,
                    ConfigStyles.COLOR_ACCENT,
                    ConfigStyles.COLOR_LABEL,
                    true, 0, false,
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
                false, 3, true,
                b -> resetAll());

        FlatButton saveBtn = new FlatButton(startX + btnWidth + gap, y, btnWidth, btnHeight,
                Component.translatable(LANG_PREFIX + ".save"),
                ConfigStyles.COLOR_SAVE_BG,
                ConfigStyles.COLOR_ACCENT,
                ConfigStyles.COLOR_ACCENT_DARK,
                ConfigStyles.COLOR_SAVE_TEXT,
                false, 3, true,
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
        // 保存旧列表用于向左淡出
        previousVisibleEntries.clear();
        previousVisibleEntries.addAll(visibleEntries);
        inCategoryTransition = true;
        categoryTransitionStart = System.currentTimeMillis();

        selectedCategory = category;
        rebuildVisibleEntries();
        updateWidgetPositions();
        updateCategoryButtonStyles();
    }

    private void updateCategoryButtonStyles() {
        for (int i = 0; i < categoryButtons.size(); i++) {
            FlatButton btn = (FlatButton) categoryButtons.get(i);
            boolean selected = activeCategories.get(i) == selectedCategory;
            // 按钮背景与侧边栏融为一体，选中态通过左侧能量条体现
            int textColor = selected ? ConfigStyles.COLOR_ACCENT : ConfigStyles.COLOR_LABEL;
            btn.setColors(
                    ConfigStyles.COLOR_SIDEBAR_BG,
                    ConfigStyles.COLOR_SIDEBAR_BG,
                    ConfigStyles.COLOR_SIDEBAR_BG,
                    textColor
            );
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

        // 整体编排：取最晚区域（footer）完成时刻作为结束判定
        float masterRaw = getRegionRawProgress(ConfigStyles.REGION_FOOTER);
        panelTransformProgress = closing ? 1f - masterRaw : masterRaw;

        // 若退出动画完成，先切回父屏幕，再触发资源包重载；
        // 这样 reloadResourcePacks() 弹出的加载屏幕能正确覆盖父屏幕，玩家可见。
        if (closing && masterRaw >= 1f) {
            boolean needReload = pendingResourceReload;
            pendingResourceReload = false;
            minecraft.setScreen(parent);
            if (needReload) {
                // 此时配置已保存，ENABLE_MOD_UI 为最新值
                PDPackHandler.applyPackState(PDClientConfig.ENABLE_MOD_UI.get());
            }
            return;
        }

        // 1. 背景（不参与分区滑动）
        renderBackground(gui, mouseX, mouseY, partialTick);

        // 2. 面板底壳：轻微缩放 + 淡入
        float panelT = getRegionEased(ConfigStyles.REGION_PANEL);
        gui.pose().pushPose();
        applyRegionShellTransform(gui, panelT);
        gui.setColor(1f, 1f, 1f, Math.max(0f, Math.min(1f, panelT)));
        renderPanel(gui);
        gui.setColor(1f, 1f, 1f, 1f);
        gui.pose().popPose();

        // 3. 侧边栏：从左侧滑入 / 向左滑出
        gui.pose().pushPose();
        applyRegionSlide(gui, ConfigStyles.REGION_SIDEBAR, -1, 0);
        renderSidebar(gui, mouseX, mouseY);
        gui.setColor(1f, 1f, 1f, 1f);
        gui.pose().popPose();

        // 4. 标题区：从上方滑入 / 向上滑出
        gui.pose().pushPose();
        applyRegionSlide(gui, ConfigStyles.REGION_HEADER, 0, -1);
        float headerEased = getRegionEased(ConfigStyles.REGION_HEADER);
        int titleAlpha = (int) (0xFF * screenFadeIn * Math.max(0f, Math.min(1f, headerEased)));
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
        gui.setColor(1f, 1f, 1f, 1f);
        gui.pose().popPose();

        // 5. 惯性滚动
        if (Math.abs(targetScrollOffset - scrollOffset) > 0.1f) {
            scrollOffset += (int) Math.round((targetScrollOffset - scrollOffset) * 0.22f);
        } else {
            scrollOffset = (int) Math.round(targetScrollOffset);
        }

        // 6. 主内容区（底 + 列表 + 滚动条）：从右侧滑入 / 向右滑出
        gui.pose().pushPose();
        applyRegionSlide(gui, ConfigStyles.REGION_CONTENT, 1, 0);
        gui.fill(contentX - 4, listTop - 4, contentX + contentWidth + 4, listBottom + 4, ConfigStyles.COLOR_PANEL_BG);
        gui.fill(contentX - 4, listBottom + 4, contentX + contentWidth + 4, listBottom + 5, ConfigStyles.COLOR_DIVIDER);
        gui.enableScissor(contentX, listTop, contentX + contentWidth, listBottom);
        renderEntries(gui, mouseX, mouseY, partialTick);
        gui.disableScissor();
        renderScrollbar(gui, mouseX, mouseY);
        gui.setColor(1f, 1f, 1f, 1f);
        gui.pose().popPose();

        // 8. 保存反馈
        renderSaveFeedback(gui);

        // 9. 底部按钮：从下方滑入 / 向下滑出
        gui.pose().pushPose();
        applyRegionSlide(gui, ConfigStyles.REGION_FOOTER, 0, 1);
        for (AbstractWidget btn : footerButtons) {
            btn.render(gui, mouseX, mouseY, partialTick);
        }
        gui.setColor(1f, 1f, 1f, 1f);
        gui.pose().popPose();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 1. 径向渐变背景：从中心向边缘压暗，模拟水下光晕
        int cx = this.width / 2;
        int cy = this.height / 2;
        int maxRadius = (int) Math.sqrt(cx * cx + cy * cy);
        int layers = ConfigStyles.RADIAL_GRADIENT_LAYERS;
        for (int i = 0; i < layers; i++) {
            float t = (float) i / (layers - 1);
            int color = lerpColor(ConfigStyles.COLOR_BG_CENTER, ConfigStyles.COLOR_BG_EDGE, t);
            int radius = (int) (maxRadius * (1f - (1f - t) * (1f - t)));
            guiGraphics.fill(cx - radius, cy - radius, cx + radius, cy + radius, color);
        }

        // 2. 扫描线纹理（极淡，模拟 HUD 显示层）
        for (int y = 0; y < this.height; y += ConfigStyles.SCANLINE_SPACING) {
            guiGraphics.fill(0, y, this.width, y + 1, ConfigStyles.COLOR_SCANLINE);
        }
    }

    /**
     * 取某一 UI 区域的线性进度（0~1），含区域错开延迟。
     * 进入：delay = regionIndex * stagger；退出：倒序 delay = (max-region) * stagger。
     *
     * @param regionIndex 区域索引
     * @return 未缓动的 0~1 进度
     */
    private float getRegionRawProgress(int regionIndex) {
        long animStart = closing ? closeStartTime : openTime;
        float duration = closing ? ConfigStyles.SCREEN_EXIT_MS : ConfigStyles.SCREEN_ENTER_MS;
        float delay = closing
                ? (ConfigStyles.REGION_FOOTER - regionIndex) * ConfigStyles.REGION_STAGGER_MS
                : regionIndex * ConfigStyles.REGION_STAGGER_MS;
        float elapsed = System.currentTimeMillis() - animStart - delay;
        return Math.max(0f, Math.min(1f, elapsed / duration));
    }

    /**
     * 区域缓动后进度。进入用 easeOutBack（轻微回弹），退出用 easeInCubic（加速离场）。
     *
     * @param regionIndex 区域索引
     * @return 缓动后的视觉权重（进入可略>1；退出 1=完全在位）
     */
    private float getRegionEased(int regionIndex) {
        float raw = getRegionRawProgress(regionIndex);
        if (closing) {
            return 1f - easeInCubic(raw);
        }
        return easeOutBack(raw);
    }

    /**
     * 面板底壳：围中心轻微缩放。
     *
     * @param gui   绘制上下文
     * @param eased 缓动进度（1=完全展示）
     */
    private void applyRegionShellTransform(GuiGraphics gui, float eased) {
        float t = Math.max(0f, Math.min(1f, eased));
        float scale = ConfigStyles.SCREEN_ENTER_SCALE_FROM
                + (1f - ConfigStyles.SCREEN_ENTER_SCALE_FROM) * t;
        if (closing) {
            scale = 1f - (1f - ConfigStyles.SCREEN_EXIT_SCALE_TO) * (1f - t);
        }
        if (Math.abs(scale - 1f) < 0.001f) {
            return;
        }
        float centerX = (panelLeft + panelRight) / 2f;
        float centerY = (panelTop + panelBottom) / 2f;
        gui.pose().translate(centerX, centerY, 0);
        gui.pose().scale(scale, scale, 1f);
        gui.pose().translate(-centerX, -centerY, 0);
    }

    /**
     * 将区域从最近侧滑入 / 向最近侧滑出。
     * dirX/dirY 为进入方向单位符号：(-1,0)左 (1,0)右 (0,-1)上 (0,1)下。
     * 退出沿同轴向近侧离开；easeOutBack 允许轻微过冲回弹。
     *
     * @param gui         绘制上下文
     * @param regionIndex 区域索引
     * @param dirX        进入方向 X（-1/0/1）
     * @param dirY        进入方向 Y（-1/0/1）
     */
    private void applyRegionSlide(GuiGraphics gui, int regionIndex, int dirX, int dirY) {
        float eased = getRegionEased(regionIndex);
        float overshootRemain = 1f - eased;
        float dx = dirX * ConfigStyles.REGION_SLIDE_PX * overshootRemain;
        float dy = dirY * ConfigStyles.REGION_SLIDE_PX * overshootRemain;
        float alpha = Math.max(0f, Math.min(1f, eased));
        gui.setColor(1f, 1f, 1f, alpha);
        gui.pose().translate(dx, dy, 0);
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
        // 顶部微弱高光，增强面板浮起感
        gui.fill(panelLeft + 1, panelTop + 1, panelRight - 1, panelTop + 2, 0x12FFFFFF);
    }

    private void renderSidebar(GuiGraphics gui, int mouseX, int mouseY) {
        gui.fill(panelLeft, panelTop, sidebarRight, panelBottom, ConfigStyles.COLOR_SIDEBAR_BG);
        gui.fill(sidebarRight, panelTop, sidebarRight + 1, panelBottom, ConfigStyles.COLOR_DIVIDER);

        Component navTitle = Component.translatable(LANG_PREFIX + ".nav");
        gui.drawString(font, navTitle, panelLeft + 10, panelTop + 8, ConfigStyles.COLOR_TITLE);

        int titleLineY = panelTop + 8 + font.lineHeight + 3;
        gui.fill(panelLeft + 10, titleLineY, panelLeft + 10 + 36, titleLineY + 1, ConfigStyles.COLOR_ACCENT);

        // 更新反光条目标位置
        // 使用 activeCategories 索引而非 enum ordinal，避免跳过空分类时索引错位
        int selectedIndex = activeCategories.indexOf(selectedCategory);
        if (selectedIndex >= 0 && selectedIndex < categoryButtons.size()) {
            FlatButton selectedBtn = (FlatButton) categoryButtons.get(selectedIndex);
            categoryBarTargetY = selectedBtn.getY() + 2;
        }
        // 平滑移动反光条
        categoryBarY += (categoryBarTargetY - categoryBarY) * 0.35f;

        // 绘制滑动反光条
        int barX = panelLeft + 6 - 2;
        int barH = ConfigStyles.CATEGORY_BUTTON_HEIGHT - 4;
        gui.fill(barX - 1, (int) categoryBarY - 1, barX + 4, (int) categoryBarY + barH + 1, ConfigStyles.COLOR_CATEGORY_GLOW);
        gui.fill(barX, (int) categoryBarY, barX + 3, (int) categoryBarY + barH, ConfigStyles.COLOR_CATEGORY_BAR);

        for (int i = 0; i < categoryButtons.size(); i++) {
            FlatButton btn = (FlatButton) categoryButtons.get(i);
            btn.render(gui, mouseX, mouseY, 0);
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

        // 分类切换进度
        float transProgress = 0f;
        if (inCategoryTransition) {
            float elapsed = now - categoryTransitionStart;
            transProgress = Math.max(0f, Math.min(1f, elapsed / ConfigStyles.CATEGORY_TRANSITION_MS));
            if (transProgress >= 1f) {
                inCategoryTransition = false;
                previousVisibleEntries.clear();
            }
        }

        // 旧列表：向左淡出（0~0.5）
        if (inCategoryTransition && !previousVisibleEntries.isEmpty()) {
            float exitProgress = Math.max(0f, Math.min(1f, transProgress * 2f));
            int x = contentX - (int) (ConfigStyles.CATEGORY_EXIT_SHIFT_PX * easeOutCubic(exitProgress));
            categorySlideOffset = -ConfigStyles.CATEGORY_EXIT_SHIFT_PX * easeOutCubic(exitProgress);
            float alpha = 1f - easeOutCubic(exitProgress);
            gui.setColor(1f, 1f, 1f, alpha);
            renderEntryList(gui, previousVisibleEntries, baseY, x, rowWidth, mouseX, mouseY, partialTick, now, true);
            gui.setColor(1f, 1f, 1f, 1f);
        }

        // 新列表：从右淡入（0.5~1.0）
        float enterProgress = inCategoryTransition ? Math.max(0f, Math.min(1f, (transProgress - 0.5f) * 2f)) : 1f;
        if (enterProgress > 0f) {
            int x = contentX + (int) (ConfigStyles.CATEGORY_ENTER_SHIFT_PX * (1f - easeOutCubic(enterProgress)));
            categorySlideOffset = ConfigStyles.CATEGORY_ENTER_SHIFT_PX * (1f - easeOutCubic(enterProgress));
            float alpha = inCategoryTransition ? easeOutCubic(enterProgress) : 1f;
            gui.setColor(1f, 1f, 1f, alpha);
            renderEntryList(gui, visibleEntries, baseY, x, rowWidth, mouseX, mouseY, partialTick, now, false);
            gui.setColor(1f, 1f, 1f, 1f);
        }
    }

    /**
     * 渲染单个配置项列表
     */
    private void renderEntryList(GuiGraphics gui, List<ConfigEntry<?>> entries, int baseY, int x, int rowWidth,
                                 int mouseX, int mouseY, float partialTick, long now, boolean isExiting) {
        for (int i = 0; i < entries.size(); i++) {
            ConfigEntry<?> entry = entries.get(i);

            float entryProgress = isExiting ? 1f : getEntryProgress(entry.getIndex(), now);
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
     * easeInCubic：退出加速离场。
     *
     * @param t 线性进度 0~1
     * @return 缓动后进度
     */
    private float easeInCubic(float t) {
        return t * t * t;
    }

    /**
     * easeOutBack：轻微过冲回弹，模拟弹簧落位。
     *
     * @param t 线性进度 0~1
     * @return 可略大于 1 的缓动值
     */
    private float easeOutBack(float t) {
        float c1 = ConfigStyles.REGION_OVERSHOOT;
        float c3 = c1 + 1f;
        return 1f + c3 * (float) Math.pow(t - 1f, 3) + c1 * (float) Math.pow(t - 1f, 2);
    }

    /**
     * 线性插值两个 ARGB 颜色
     */
    private static int lerpColor(int a, int b, float t) {
        int aA = (a >> 24) & 0xFF, aR = (a >> 16) & 0xFF, aG = (a >> 8) & 0xFF, aB = a & 0xFF;
        int bA = (b >> 24) & 0xFF, bR = (b >> 16) & 0xFF, bG = (b >> 8) & 0xFF, bB = b & 0xFF;
        int al = (int) (aA + (bA - aA) * t);
        int r = (int) (aR + (bR - aR) * t);
        int g = (int) (aG + (bG - aG) * t);
        int bl = (int) (aB + (bB - aB) * t);
        return (al << 24) | (r << 16) | (g << 8) | bl;
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
            targetScrollOffset = (float) Math.max(0, Math.min(maxScroll, targetScrollOffset - scrollY * 28));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 进入编排未完成 / 正在退出时屏蔽交互
        if (closing || getRegionRawProgress(ConfigStyles.REGION_FOOTER) < 0.98f) return true;
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
                    checkEnableModUiChange();
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
        // ESC 始终优先于 focusedListener，确保可以随时退出
        if (keyCode == 256) {
            onClose();
            return true;
        }
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
     * 检查「启用模组 UI」是否被玩家变更（开→关 或 关→开），
     * 若是则弹出资源包重载确认对话框。无论变更方向，都需要重载资源包以应用/移除模组 UI 资源。
     */
    private void checkEnableModUiChange() {
        if (enableModUiEntry == null || resourceReloadDialogShown) {
            return;
        }
        boolean current = enableModUiEntry.getPendingValue();
        if (current == enableModUiOriginalValue) return; // 未变更
        resourceReloadDialogShown = true;
        minecraft.setScreen(new ConfirmScreen(
                confirmed -> {
                    if (confirmed) {
                        pendingResourceReload = true;
                    } else {
                        // 玩家取消：恢复原始值
                        enableModUiEntry.setPendingValue(enableModUiOriginalValue);
                        resourceReloadDialogShown = false;
                    }
                    minecraft.setScreen(this);
                },
                Component.translatable(LANG_PREFIX + ".reload_pack.title"),
                Component.translatable(LANG_PREFIX + ".reload_pack.message"),
                Component.translatable(LANG_PREFIX + ".reload_pack.confirm"),
                Component.translatable(LANG_PREFIX + ".reload_pack.cancel")
        ));
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
        // 主菜单打开配置时 player 可能为 null
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage(
                    Component.translatable(LANG_PREFIX + ".save.success"), true);
        }
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
        // 退出前自动保存所有配置（与原版 Mod 列表配置页行为一致）
        saveConfig();
        closing = true;
        closeStartTime = System.currentTimeMillis();
    }
}
