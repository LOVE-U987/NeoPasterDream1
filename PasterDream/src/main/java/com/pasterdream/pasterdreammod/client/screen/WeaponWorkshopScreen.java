package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.menu.WeaponWorkshopMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 精铸工坊 GUI 屏幕 (Weapon Workshop Screen)
 * 布局完全还原原版 WeaponWorkshopGuiScreen：186×186 背景纹理 +
 * "锻造"按钮（vanilla Button，位置 (16, 54)，尺寸 61×20）。
 * <p>
 * 点击按钮通过 vanilla 的 {@code handleInventoryButtonClick} 通道发送到
 * 服务端，由 {@link WeaponWorkshopMenu#clickMenuButton} 触发配方匹配
 * （等价原版 WeaponWorkshopGuiButtonMessage 按钮 0）。
 */
public class WeaponWorkshopScreen extends AbstractContainerScreen<WeaponWorkshopMenu> {

    /** GUI 背景纹理（原版 186×186） */
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID,
                    "textures/screens/weapon_workshop_gui.png");

    /**
     * 构造精铸工坊 GUI 屏幕
     *
     * @param menu  容器菜单
     * @param inv   玩家库存
     * @param title 标题文本
     */
    public WeaponWorkshopScreen(WeaponWorkshopMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 186;
        this.imageHeight = 186;
    }

    @Override
    public void init() {
        super.init();
        // "锻造"按钮（原版 bounds(leftPos+16, topPos+54, 61, 20)）
        this.addRenderableWidget(Button.builder(
                        Component.translatable("gui.pasterdream.weapon_workshop_gui.button_forging"),
                        button -> {
                            if (this.minecraft != null && this.minecraft.gameMode != null) {
                                this.minecraft.gameMode.handleInventoryButtonClick(
                                        this.menu.containerId, WeaponWorkshopMenu.BUTTON_FORGE);
                            }
                        })
                .bounds(this.leftPos + 16, this.topPos + 54, 61, 20)
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
