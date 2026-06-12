package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.menu.DreamCauldronMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 梦境炼药锅 GUI 屏幕 (Dream Cauldron Screen)
 * 渲染炼药锅界面纹理 + 合成/清空按钮
 *
 * 纹理资源：
 * - dream_cauldron_gui.png：背景纹理（176×182）
 * - dream_cauldron_gui_button0.png：按钮默认纹理
 * - dream_cauldron_gui_button1.png：按钮悬停纹理
 *
 * 按钮区域（相对于 GUI 纹理左上角）：
 * - 合成按钮：位置 (115, 47)，尺寸 (16, 16)
 * - 清空按钮：位置 (133, 47)，尺寸 (16, 16)
 */
public class DreamCauldronScreen extends AbstractContainerScreen<DreamCauldronMenu> {

    /** GUI 背景纹理 */
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID,
                    "textures/screens/dream_cauldron_gui.png");

    /** 按钮默认纹理 */
    private static final ResourceLocation BUTTON_0_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID,
                    "textures/screens/dream_cauldron_gui_button0.png");

    /** 按钮悬停纹理 */
    private static final ResourceLocation BUTTON_1_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID,
                    "textures/screens/dream_cauldron_gui_button1.png");

    /** 合成按钮区域（相对于 GUI 纹理） */
    private static final int CRAFT_BUTTON_X = 115;
    private static final int CRAFT_BUTTON_Y = 47;
    private static final int BUTTON_SIZE = 16;

    /** 清空按钮区域 */
    private static final int CLEAR_BUTTON_X = 133;
    private static final int CLEAR_BUTTON_Y = 47;

    /**
     * 构造梦境炼药锅 GUI 屏幕
     *
     * @param menu  容器菜单
     * @param inv   玩家库存
     * @param title 标题文本
     */
    public DreamCauldronScreen(DreamCauldronMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 182;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // 绘制背景纹理，纹理尺寸 176×182
        guiGraphics.blit(GUI_TEXTURE,
                this.leftPos, this.topPos,
                0, 0,
                this.imageWidth, this.imageHeight,
                this.imageWidth, this.imageHeight);

        // 绘制合成按钮
        boolean hoverCraft = isHovering(CRAFT_BUTTON_X, CRAFT_BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE, mouseX, mouseY);
        guiGraphics.blit(hoverCraft ? BUTTON_1_TEXTURE : BUTTON_0_TEXTURE,
                this.leftPos + CRAFT_BUTTON_X, this.topPos + CRAFT_BUTTON_Y,
                0, 0,
                BUTTON_SIZE, BUTTON_SIZE,
                BUTTON_SIZE, BUTTON_SIZE);

        // 绘制清空按钮
        boolean hoverClear = isHovering(CLEAR_BUTTON_X, CLEAR_BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE, mouseX, mouseY);
        guiGraphics.blit(hoverClear ? BUTTON_1_TEXTURE : BUTTON_0_TEXTURE,
                this.leftPos + CLEAR_BUTTON_X, this.topPos + CLEAR_BUTTON_Y,
                0, 0,
                BUTTON_SIZE, BUTTON_SIZE,
                BUTTON_SIZE, BUTTON_SIZE);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // 绘制玩家背包标签，位置相对于 GUI 纹理左上角
        guiGraphics.drawString(this.font, this.playerInventoryTitle,
                this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            // 检查合成按钮点击
            if (isHovering(CRAFT_BUTTON_X, CRAFT_BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE, (int) mouseX, (int) mouseY)) {
                handleCraftButton();
                return true;
            }
            // 检查清空按钮点击
            if (isHovering(CLEAR_BUTTON_X, CLEAR_BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE, (int) mouseX, (int) mouseY)) {
                handleClearButton();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * 处理合成按钮点击
     * 发送自定义数据包到服务端（目前为占位实现，仅记录日志）
     */
    private void handleCraftButton() {
        PasterDreamMod.LOGGER.debug("[DreamCauldronScreen] 合成按钮点击 - 槽位: {}", 
                menu.getBlockEntity().getBlockPos());
    }

    /**
     * 处理清空按钮点击
     * 发送自定义数据包到服务端（目前为占位实现，仅记录日志）
     */
    private void handleClearButton() {
        PasterDreamMod.LOGGER.debug("[DreamCauldronScreen] 清空按钮点击 - 槽位: {}",
                menu.getBlockEntity().getBlockPos());
    }

    /**
     * 判断鼠标是否悬浮在指定区域内
     *
     * @param x      区域左上角 x（相对于 GUI 纹理）
     * @param y      区域左上角 y（相对于 GUI 纹理）
     * @param w      区域宽度
     * @param h      区域高度
     * @param mouseX 鼠标屏幕 x
     * @param mouseY 鼠标屏幕 y
     * @return 是否悬浮在区域内
     */
    private boolean isHovering(int x, int y, int w, int h, int mouseX, int mouseY) {
        int guiX = mouseX - this.leftPos;
        int guiY = mouseY - this.topPos;
        return guiX >= x && guiX < x + w && guiY >= y && guiY < y + h;
    }
}
