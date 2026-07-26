package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.menu.ResearchTableMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 研究台 GUI 屏幕 (Research Table Screen)
 * 布局完全还原原版 ResearchTableGuiScreen：206×206 背景纹理 +
 * "复制"按钮（75, 35, 46×20）与"研究"按钮（73, 80, 51×20）。
 * <p>
 * 点击按钮通过 vanilla 的 {@code handleInventoryButtonClick} 通道发送到服务端，
 * 由 {@link ResearchTableMenu#clickMenuButton} 执行复制/研究逻辑
 * （等价原版 ResearchTableGuiButtonMessage 按钮 0/1）。
 */
public class ResearchTableScreen extends AbstractContainerScreen<ResearchTableMenu> {

    /** GUI 背景纹理（原版 206×206） */
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID,
                    "textures/screens/research_table_gui.png");

    /**
     * 构造研究台 GUI 屏幕
     *
     * @param menu  容器菜单
     * @param inv   玩家库存
     * @param title 标题文本
     */
    public ResearchTableScreen(ResearchTableMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 206;
        this.imageHeight = 206;
    }

    @Override
    public void init() {
        super.init();
        // "复制"按钮（原版 bounds(leftPos+75, topPos+35, 46, 20)）
        this.addRenderableWidget(Button.builder(
                        Component.translatable("gui.pasterdream.research_table_gui.button_copy"),
                        button -> {
                            if (this.minecraft != null && this.minecraft.gameMode != null) {
                                this.minecraft.gameMode.handleInventoryButtonClick(
                                        this.menu.containerId, ResearchTableMenu.BUTTON_COPY);
                            }
                        })
                .bounds(this.leftPos + 75, this.topPos + 35, 46, 20)
                .build());
        // "研究"按钮（原版 bounds(leftPos+73, topPos+80, 51, 20)）
        this.addRenderableWidget(Button.builder(
                        Component.translatable("gui.pasterdream.research_table_gui.button_study"),
                        button -> {
                            if (this.minecraft != null && this.minecraft.gameMode != null) {
                                this.minecraft.gameMode.handleInventoryButtonClick(
                                        this.menu.containerId, ResearchTableMenu.BUTTON_STUDY);
                            }
                        })
                .bounds(this.leftPos + 73, this.topPos + 80, 51, 20)
                .build());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(GUI_TEXTURE, this.leftPos, this.topPos, 0, 0,
                this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // 与原版一致：不绘制标题/背包标签
    }
}
