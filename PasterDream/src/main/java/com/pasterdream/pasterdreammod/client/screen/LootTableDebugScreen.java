package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.item.LootTableDebugCatalog;
import com.pasterdream.pasterdreammod.menu.LootTableDebugMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 战利品库选择屏幕（280×168，原版灰面板，无 GUI 纹理）。
 * <p>
 * 面板为原版灰 {@code #C6C6C6}，2px 黑色外框、左上白色高光、右下深灰阴影。
 * 分类与战利品表各用 40×20 文字按钮“上一项/下一项”，类型按钮 180×20 居中。
 * 不使用关闭按钮与键盘快捷键，{@code Esc} 走原版关闭逻辑。
 */
public class LootTableDebugScreen extends AbstractContainerScreen<LootTableDebugMenu> {

    /** 面板宽度 */
    private static final int PANEL_WIDTH = 280;
    /** 面板高度 */
    private static final int PANEL_HEIGHT = 168;
    /** 文本最大宽度（左右按钮之间的可用区域） */
    private static final int TEXT_MAX_WIDTH = 182;
    /** 原版 GUI 文本颜色 */
    private static final int TEXT_COLOR = 0x404040;

    /**
     * 构造战利品库选择屏幕。
     *
     * @param menu      菜单
     * @param inventory 玩家背包
     * @param title     标题
     */
    public LootTableDebugScreen(LootTableDebugMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = PANEL_WIDTH;
        this.imageHeight = PANEL_HEIGHT;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        ResourceLocation table = this.getMenu().getCurrentTable();
        if (table != null && isHoveringPath(mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font, Component.literal(table.toString()), mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        guiGraphics.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xFFC6C6C6);
        guiGraphics.fill(x, y, x + this.imageWidth, y + 2, 0xFF000000);
        guiGraphics.fill(x, y + this.imageHeight - 2, x + this.imageWidth, y + this.imageHeight, 0xFF000000);
        guiGraphics.fill(x, y, x + 2, y + this.imageHeight, 0xFF000000);
        guiGraphics.fill(x + this.imageWidth - 2, y, x + this.imageWidth, y + this.imageHeight, 0xFF000000);
        guiGraphics.fill(x + 2, y + 2, x + this.imageWidth - 2, y + 4, 0xFFFFFFFF);
        guiGraphics.fill(x + 2, y + 2, x + 4, y + this.imageHeight - 2, 0xFFFFFFFF);
        guiGraphics.fill(x + 2, y + this.imageHeight - 4, x + this.imageWidth - 2, y + this.imageHeight - 2, 0xFF555555);
        guiGraphics.fill(x + this.imageWidth - 4, y + 2, x + this.imageWidth - 2, y + this.imageHeight - 2, 0xFF555555);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int center = this.imageWidth / 2;
        drawCentered(guiGraphics, this.title, 9, center);
        LootTableDebugMenu menu = this.getMenu();
        if (menu.getTables().isEmpty()) {
            drawCentered(guiGraphics, Component.translatable("gui.pasterdream.loot_table_debug.empty"), 64, center);
            drawCentered(guiGraphics, Component.translatable("gui.pasterdream.loot_table_debug.index", 0, 0), 85, center);
        } else {
            renderSelection(guiGraphics, menu, center);
        }
        drawCentered(guiGraphics, Component.translatable("gui.pasterdream.loot_table_debug.type", typeName(menu)), 100, center);
        drawCentered(guiGraphics, Component.translatable("gui.pasterdream.loot_table_debug.hint"), 144, center);
    }

    /**
     * 绘制分类、路径与序号。
     *
     * @param guiGraphics 绘制上下文
     * @param menu        菜单
     * @param center      面板水平中心
     */
    private void renderSelection(GuiGraphics guiGraphics, LootTableDebugMenu menu, int center) {
        ResourceLocation table = menu.getCurrentTable();
        if (table != null) {
            Component category = Component.translatable(
                    LootTableDebugCatalog.translationKey(LootTableDebugCatalog.categoryOf(table)));
            Component categoryLine = Component.translatable("gui.pasterdream.loot_table_debug.category", category);
            drawCentered(guiGraphics,
                    Component.literal(truncate(categoryLine.getString(), TEXT_MAX_WIDTH)), 35, center);
            drawCentered(guiGraphics, Component.literal(truncate(table.getPath(), TEXT_MAX_WIDTH)), 64, center);
        }
        drawCentered(guiGraphics, Component.translatable("gui.pasterdream.loot_table_debug.index",
                menu.getSyncedIndex() + 1, menu.getTables().size()), 85, center);
    }

    @Override
    protected void init() {
        super.init();
        boolean active = !this.getMenu().getTables().isEmpty();
        addDebugButton(9, 29, 40, 20, "button.prev", LootTableDebugMenu.BUTTON_CATEGORY_PREV, active);
        addDebugButton(231, 29, 40, 20, "button.next", LootTableDebugMenu.BUTTON_CATEGORY_NEXT, active);
        addDebugButton(9, 59, 40, 20, "button.prev", LootTableDebugMenu.BUTTON_UP, active);
        addDebugButton(231, 59, 40, 20, "button.next", LootTableDebugMenu.BUTTON_DOWN, active);
        addDebugButton(50, 114, 180, 20, "type_button", LootTableDebugMenu.BUTTON_TYPE, active);
    }

    /**
     * 添加一个文字按钮。
     *
     * @param x      面板内 X
     * @param y      面板内 Y
     * @param width  宽度
     * @param height 高度
     * @param key    语言键后缀
     * @param id     按钮 ID
     * @param active 是否可用
     */
    private void addDebugButton(int x, int y, int width, int height, String key, int id, boolean active) {
        Button button = Button.builder(
                        Component.translatable("gui.pasterdream.loot_table_debug." + key),
                        b -> sendButton(id))
                .bounds(this.leftPos + x, this.topPos + y, width, height)
                .build();
        button.active = active;
        this.addRenderableWidget(button);
    }

    /**
     * 向服务端发送按钮点击。
     *
     * @param id 按钮 ID
     */
    private void sendButton(int id) {
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        }
    }

    /**
     * 绘制水平居中的无阴影文本。
     *
     * @param guiGraphics 绘制上下文
     * @param text        文本
     * @param y           面板内 Y
     * @param center      面板水平中心
     */
    private void drawCentered(GuiGraphics guiGraphics, Component text, int y, int center) {
        guiGraphics.drawString(this.font, text, center - this.font.width(text) / 2, y, TEXT_COLOR, false);
    }

    /**
     * 按像素宽度截断文本，超出时追加 ASCII 省略号。
     *
     * @param text     原始文本
     * @param maxWidth 最大宽度
     * @return 截断后的文本
     */
    private String truncate(String text, int maxWidth) {
        if (this.font.width(text) <= maxWidth) {
            return text;
        }
        String suffix = "...";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (this.font.width(builder.toString() + text.charAt(i) + suffix) > maxWidth) {
                break;
            }
            builder.append(text.charAt(i));
        }
        return builder + suffix;
    }

    /**
     * 判断鼠标是否位于战利品表路径行。
     *
     * @param mouseX 鼠标 X
     * @param mouseY 鼠标 Y
     * @return true 位于路径行
     */
    private boolean isHoveringPath(int mouseX, int mouseY) {
        return mouseX >= this.leftPos + 49 && mouseX < this.leftPos + 231
                && mouseY >= this.topPos + 60 && mouseY < this.topPos + 76;
    }

    /**
     * 当前箱体类型名称。
     *
     * @param menu 菜单
     * @return 类型名称组件
     */
    private static Component typeName(LootTableDebugMenu menu) {
        return Component.translatable(menu.isMeltdreamSelected()
                ? "gui.pasterdream.loot_table_debug.type.meltdream"
                : "gui.pasterdream.loot_table_debug.type.vanilla");
    }
}
