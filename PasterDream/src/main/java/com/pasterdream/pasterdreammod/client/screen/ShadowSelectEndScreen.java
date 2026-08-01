package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.menu.ShadowSelectEndMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 影之抉择 GUI 屏幕 (Shadow Select End Screen)
 * 布局完全还原原版 ShadowSelectEndScreen：320×200 背景（绘制于 topPos+3）+
 * 顶部两行提示文字 + 两个 82×87 图片按钮（黑暗 / 光明，
 * 纹理为 82×174 双帧图，悬停时切换下半帧）。
 * <p>
 * 点击按钮通过 vanilla 的 {@code handleInventoryButtonClick} 通道发送到服务端，
 * 由 {@link ShadowSelectEndMenu#clickMenuButton} 执行结局选择
 * （等价原版 ShadowSelectEndButtonMessage 按钮 0/1）。
 */
public class ShadowSelectEndScreen extends AbstractContainerScreen<ShadowSelectEndMenu> {

    /** GUI 背景纹理（原版 320×200） */
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID,
                    "textures/screens/shadow_select_end_gui.png");

    /** "黑暗"按钮纹理（82×174 双帧） */
    private static final ResourceLocation DARK_BUTTON_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID,
                    "textures/screens/atlas/imagebutton_dark_button.png");

    /** "光明"按钮纹理（82×174 双帧） */
    private static final ResourceLocation LIGHT_BUTTON_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID,
                    "textures/screens/atlas/imagebutton_light_button.png");

    /**
     * 构造影之抉择 GUI 屏幕
     *
     * @param menu  容器菜单
     * @param inv   玩家库存
     * @param title 标题文本
     */
    public ShadowSelectEndScreen(ShadowSelectEndMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 320;
        this.imageHeight = 200;
    }

    @Override
    public void init() {
        super.init();
        // "黑暗"按钮（原版 leftPos+48, topPos+54, 82×87）
        this.addRenderableWidget(new TwoFrameImageButton(this.leftPos + 48, this.topPos + 54,
                DARK_BUTTON_TEXTURE, Component.translatable("message.pasterdream.shadow_select.dark"),
                () -> sendButtonClick(ShadowSelectEndMenu.BUTTON_DARK)));
        // "光明"按钮（原版 leftPos+183, topPos+52, 82×87）
        this.addRenderableWidget(new TwoFrameImageButton(this.leftPos + 183, this.topPos + 52,
                LIGHT_BUTTON_TEXTURE, Component.translatable("message.pasterdream.shadow_select.light"),
                () -> sendButtonClick(ShadowSelectEndMenu.BUTTON_LIGHT)));
    }

    /** 通过 vanilla 容器按钮通道发送按钮点击 */
    private void sendButtonClick(int buttonId) {
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // 原版绘制在 topPos+3
        guiGraphics.blit(GUI_TEXTURE, this.leftPos, this.topPos + 3, 0, 0, 320, 200, 320, 200);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // 原版两行提示文字（浅灰 0xFFCCCCCC 对应 -3355444）
        guiGraphics.drawString(this.font,
                Component.translatable("gui.pasterdream.shadow_select_end.label_qing_gen_ju_zi_ji_de_di_zhi_jue_zuo_chu_xuan_ze"),
                98, 9, -3355444, false);
        guiGraphics.drawString(this.font,
                Component.translatable("gui.pasterdream.shadow_select_end.label_zhe_hui_shi_tan_suo_zai_wei_lai_zou_xiang_bu_tong_de_jie_ju"),
                95, 19, -3355444, false);
    }

    /**
     * 双帧图片按钮：82×87 显示区，纹理 82×174（上帧常态 / 下帧悬停），
     * 等价原版 MCreator ImageButton 的 yDiffTex=87 语义
     */
    private static final class TwoFrameImageButton extends AbstractButton {

        /** 按钮宽度 */
        private static final int BUTTON_WIDTH = 82;
        /** 按钮高度（单帧） */
        private static final int BUTTON_HEIGHT = 87;

        private final ResourceLocation texture;
        private final Runnable onPress;

        /**
         * 构造双帧图片按钮
         *
         * @param x       屏幕 X
         * @param y       屏幕 Y
         * @param texture 双帧纹理
         * @param message 无障碍文本
         * @param onPress 点击回调
         */
        private TwoFrameImageButton(int x, int y, ResourceLocation texture, Component message, Runnable onPress) {
            super(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, message);
            this.texture = texture;
            this.onPress = onPress;
        }

        @Override
        public void onPress() {
            this.onPress.run();
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int vOffset = this.isHoveredOrFocused() ? BUTTON_HEIGHT : 0;
            guiGraphics.blit(this.texture, this.getX(), this.getY(), 0, vOffset,
                    BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT * 2);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationOutput) {
            this.defaultButtonNarrationText(narrationOutput);
        }
    }
}
