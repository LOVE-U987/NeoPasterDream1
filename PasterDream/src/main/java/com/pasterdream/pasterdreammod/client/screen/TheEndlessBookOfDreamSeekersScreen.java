package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.menu.TheEndlessBookOfDreamSeekersMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 寻梦者的永恒书卷 GUI 屏幕 (The Endless Book of Dream Seekers Screen)
 * 布局对照原版 TheEndlessBookOfDreamSeekersGuiScreen：176×166 背景 +
 * "导入"按钮（leftPos+69, topPos+25, 35×20）。
 * <p>
 * 点击按钮通过 vanilla {@code handleInventoryButtonClick} 发送到服务端，
 * 由 {@link TheEndlessBookOfDreamSeekersMenu#clickMenuButton} 执行导入
 * （等价原版 GuiButtonMessage 按钮 0 → Pr5）。
 */
public class TheEndlessBookOfDreamSeekersScreen extends AbstractContainerScreen<TheEndlessBookOfDreamSeekersMenu> {

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID,
                    "textures/screens/the_endless_book_of_dream_seekers_gui.png");

    /**
     * 构造寻梦者的永恒书卷 GUI 屏幕
     *
     * @param menu  容器菜单
     * @param inv   玩家库存
     * @param title 标题文本
     */
    public TheEndlessBookOfDreamSeekersScreen(TheEndlessBookOfDreamSeekersMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void init() {
        super.init();
        // 原版 bounds(leftPos+69, topPos+25, 35, 20)
        this.addRenderableWidget(Button.builder(
                        Component.translatable("gui.pasterdream.the_endless_book_of_dream_seekers_gui.button_dao_ru"),
                        button -> {
                            if (this.minecraft != null && this.minecraft.gameMode != null) {
                                this.minecraft.gameMode.handleInventoryButtonClick(
                                        this.menu.containerId, TheEndlessBookOfDreamSeekersMenu.BUTTON_IMPORT);
                            }
                        })
                .bounds(this.leftPos + 69, this.topPos + 25, 35, 20)
                .build());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(GUI_TEXTURE,
                this.leftPos, this.topPos,
                0, 0,
                this.imageWidth, this.imageHeight,
                this.imageWidth, this.imageHeight);
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
