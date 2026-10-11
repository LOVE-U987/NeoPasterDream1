package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.data.BluePrintLoader;
import com.pasterdream.pasterdreammod.menu.BlueprintGui0Menu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

/**
 * 蓝图阅览 GUI 屏幕（还原原版 {@code BlueprintGui0Screen}）。
 * <p>
 * - 背景纹理 196×216：{@code textures/screens/blueprint_gui_0.png}
 * - 页码标签：{@code screen.pasterdream.pagenum}
 * - 左右翻页按钮：通过 {@code handleInventoryButtonClick} → 菜单 {@code clickMenuButton}
 *   发送 1-based 目标页（等价原版 {@code BlueprintGui0ButtonMessage}）
 * <p>
 * 注意：原版并无“旋转”控件，结构“逐层”即分页（每页一层 5×5）。
 */
public class BlueprintGui0Screen extends AbstractContainerScreen<BlueprintGui0Menu> {

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/screens/blueprint_gui_0.png");

    private final ScreenVar screenVar = new ScreenVar();

    /**
     * 构造蓝图阅览屏幕
     *
     * @param container 菜单
     * @param inventory 玩家背包
     * @param title     标题
     */
    public BlueprintGui0Screen(BlueprintGui0Menu container, Inventory inventory, Component title) {
        super(container, inventory, title);
        this.imageWidth = 196;
        this.imageHeight = 216;

        BluePrintLoader.BluePrint bp = container.getBluePrint();
        this.screenVar.nowPage = 1;
        this.screenVar.maxPage = bp != null ? Math.max(0, bp.getMaxPage()) : 0;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        guiGraphics.blit(GUI_TEXTURE,
                this.leftPos, this.topPos,
                0, 0,
                196, 216,
                196, 216);
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.closeContainer();
            }
            return true;
        }
        return super.keyPressed(key, b, c);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // 原版颜色 -394759
        FormattedCharSequence pageLabel = Component.translatable(
                "screen.pasterdream.pagenum", this.screenVar.nowPage).getVisualOrderText();
        guiGraphics.drawString(this.font, pageLabel,
                97 - this.font.width(pageLabel) / 2, 7, -394759, false);
    }

    @Override
    protected void init() {
        super.init();
        // 原版左键：leftPos+11, topPos+58, 30×20
        Button buttonLeft = Button.builder(
                Component.translatable("gui.pasterdream.blueprint_gui_0.button_empty"),
                b -> {
                    this.screenVar.subPage();
                    sendPageToServer();
                }).bounds(this.leftPos + 11, this.topPos + 58, 30, 20).build();
        this.getMenu().guistate.put("button:button_empty", buttonLeft);
        this.addRenderableWidget(buttonLeft);

        // 原版右键：leftPos+153, topPos+58, 30×20
        Button buttonRight = Button.builder(
                Component.translatable("gui.pasterdream.blueprint_gui_0.button_empty1"),
                b -> {
                    this.screenVar.addPage();
                    sendPageToServer();
                }).bounds(this.leftPos + 153, this.topPos + 58, 30, 20).build();
        this.getMenu().guistate.put("button:button_empty1", buttonRight);
        this.addRenderableWidget(buttonRight);
    }

    private void sendPageToServer() {
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            // button id = 1-based page（原版网络包 now_page）
            this.minecraft.gameMode.handleInventoryButtonClick(
                    this.menu.containerId, this.screenVar.nowPage);
        }
    }

    /**
     * 客户端页码状态（原版 ScreenVar）
     */
    public static final class ScreenVar {
        int nowPage;
        int maxPage;

        void addPage() {
            if (maxPage <= 0) {
                return;
            }
            nowPage = Math.min(maxPage, nowPage + 1);
        }

        void subPage() {
            nowPage = Math.max(1, nowPage - 1);
        }
    }
}
