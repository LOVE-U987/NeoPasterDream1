package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.menu.PlayerBookMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 玩家书籍管理界面（创造模式）
 * <p>
 * 与 {@link TheEndlessBookOfDreamSeekersScreen} 共用相同
 * 176×166 纹理和布局，数据存储于玩家 NBT。
 * <p>
 * "导入"按钮通过 vanilla {@code handleInventoryButtonClick} 发送
 * {@link PlayerBookMenu#BUTTON_IMPORT} 到服务端，
 * 将导入槽物品存入玩家书库。
 */
public class PlayerBookScreen extends AbstractContainerScreen<PlayerBookMenu> {

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID,
                    "textures/screens/the_endless_book_of_dream_seekers_gui.png");

    /**
     * 构造玩家书籍管理界面
     *
     * @param menu  容器菜单
     * @param inv   玩家库存
     * @param title 标题文本
     */
    public PlayerBookScreen(PlayerBookMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void init() {
        super.init();
        // 导入按钮（与 TheEndlessBookOfDreamSeekersScreen 位置一致）
        this.addRenderableWidget(Button.builder(
                        Component.translatable("gui.pasterdream.the_endless_book_of_dream_seekers_gui.button_dao_ru"),
                        button -> {
                            if (this.minecraft != null && this.minecraft.gameMode != null) {
                                this.minecraft.gameMode.handleInventoryButtonClick(
                                        this.menu.containerId, PlayerBookMenu.BUTTON_IMPORT);
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
