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
 * 布局完全还原原版 PasterDream：196×196 背景纹理 + 合成按钮 + 液体量文本
 *
 * 纹理资源：
 * - dream_cauldron_gui.png：背景纹理（196×196，原版素材）
 * - atlas/imagebutton_dream_cauldron_gui_button0.png：合成按钮图集（38×30，上半默认/下半悬停）
 *
 * 界面元素（相对于 GUI 纹理左上角，与原版一致）：
 * - 合成按钮：位置 (78, 71)，尺寸 (38, 15)
 * - 液体量文本：位置 (170, 43)，显示储罐中融梦液体毫桶数
 *
 * 点击合成按钮通过 vanilla 的 {@code handleInventoryButtonClick} 通道发送到服务端，
 * 由 {@link DreamCauldronMenu#clickMenuButton} 校验配方并启动炼制。
 */
public class DreamCauldronScreen extends AbstractContainerScreen<DreamCauldronMenu> {

    /** GUI 背景纹理（原版 196×196） */
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID,
                    "textures/screens/dream_cauldron_gui.png");

    /** 合成按钮图集纹理（38×30：v=0 默认帧，v=15 悬停帧） */
    private static final ResourceLocation CRAFT_BUTTON_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID,
                    "textures/screens/atlas/imagebutton_dream_cauldron_gui_button0.png");

    /** 合成按钮区域（相对于 GUI 纹理，原版坐标） */
    private static final int CRAFT_BUTTON_X = 78;
    private static final int CRAFT_BUTTON_Y = 71;
    private static final int CRAFT_BUTTON_WIDTH = 38;
    private static final int CRAFT_BUTTON_HEIGHT = 15;

    /** 液体量文本位置与颜色（原版值） */
    private static final int FLUID_TEXT_X = 170;
    private static final int FLUID_TEXT_Y = 43;
    private static final int FLUID_TEXT_COLOR = -26887;

    /**
     * 构造梦境炼药锅 GUI 屏幕
     *
     * @param menu  容器菜单
     * @param inv   玩家库存
     * @param title 标题文本
     */
    public DreamCauldronScreen(DreamCauldronMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 196;
        this.imageHeight = 196;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // 绘制背景纹理，纹理尺寸 196×196
        guiGraphics.blit(GUI_TEXTURE,
                this.leftPos, this.topPos,
                0, 0,
                this.imageWidth, this.imageHeight,
                this.imageWidth, this.imageHeight);

        // 绘制合成按钮：悬停时使用图集下半帧（v=15）
        boolean hover = isHovering(CRAFT_BUTTON_X, CRAFT_BUTTON_Y,
                CRAFT_BUTTON_WIDTH, CRAFT_BUTTON_HEIGHT, mouseX, mouseY);
        guiGraphics.blit(CRAFT_BUTTON_TEXTURE,
                this.leftPos + CRAFT_BUTTON_X, this.topPos + CRAFT_BUTTON_Y,
                0, hover ? CRAFT_BUTTON_HEIGHT : 0,
                CRAFT_BUTTON_WIDTH, CRAFT_BUTTON_HEIGHT,
                CRAFT_BUTTON_WIDTH, CRAFT_BUTTON_HEIGHT * 2);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // 与原版一致：不绘制标题/背包标签，仅绘制储罐液体量文本
        guiGraphics.drawString(this.font,
                this.menu.getFluidAmount() + "mb",
                FLUID_TEXT_X, FLUID_TEXT_Y, FLUID_TEXT_COLOR, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isHovering(CRAFT_BUTTON_X, CRAFT_BUTTON_Y,
                CRAFT_BUTTON_WIDTH, CRAFT_BUTTON_HEIGHT, (int) mouseX, (int) mouseY)) {
            // 通过 vanilla 按钮包通知服务端启动炼制（服务端二次校验配方与液体量）
            if (this.minecraft != null && this.minecraft.gameMode != null) {
                this.minecraft.gameMode.handleInventoryButtonClick(
                        this.menu.containerId, DreamCauldronMenu.BUTTON_CRAFT);
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
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
