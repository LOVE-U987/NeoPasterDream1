package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.dreamnotes.DreamnotesLogic;
import com.pasterdream.pasterdreammod.menu.DreamnotesGui0Menu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import java.util.Locale;

/**
 * 寻梦者笔记阅读屏幕：按持有/打开的 noteId blit 对应 gui0..gui14 页纹理。
 * <p>
 * 布局还原原版 DreamnotesGui0Screen：image 176×166，纹理 196×256，偏移 (-6, -44)。
 * <p>
 * 语言适配：当游戏语言非中文时，自动使用 {@code dreamnotes_guiN_en.png} 英文版贴图。
 */
public class DreamnotesGui0Screen extends AbstractContainerScreen<DreamnotesGui0Menu> {

    /** 中文版页面贴图 */
    private static final ResourceLocation[] PAGE_TEXTURES_ZH = new ResourceLocation[15];
    /** 英文版页面贴图 */
    private static final ResourceLocation[] PAGE_TEXTURES_EN = new ResourceLocation[15];

    static {
        for (int i = 0; i < 15; i++) {
            PAGE_TEXTURES_ZH[i] = ResourceLocation.fromNamespaceAndPath(
                    PasterDreamMod.MOD_ID, "textures/screens/dreamnotes_gui" + i + ".png");
            PAGE_TEXTURES_EN[i] = ResourceLocation.fromNamespaceAndPath(
                    PasterDreamMod.MOD_ID, "textures/screens/dreamnotes_gui" + i + "_en.png");
        }
    }

    private final Player entity;

    public DreamnotesGui0Screen(DreamnotesGui0Menu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.entity = menu.entity;
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int page = resolvePage();
        if (page >= 0 && page < PAGE_TEXTURES_ZH.length) {
            ResourceLocation[] textures = isEnglish() ? PAGE_TEXTURES_EN : PAGE_TEXTURES_ZH;
            guiGraphics.blit(textures[page],
                    this.leftPos - 6, this.topPos - 44,
                    0, 0, 196, 256, 196, 256);
        }

        RenderSystem.disableBlend();
    }

    /**
     * 判断当前游戏语言是否为英文版（非中文语言一律使用英文贴图）。
     *
     * @return true 表示使用英文版贴图
     */
    private static boolean isEnglish() {
        String code = Minecraft.getInstance().options.languageCode;
        return code == null || !code.toLowerCase(Locale.ROOT).startsWith("zh");
    }

    /**
     * 优先使用菜单打开时写入的 noteId；否则回退原版主/副手 If 判定。
     */
    private int resolvePage() {
        if (this.menu.noteId >= 0) {
            return this.menu.noteId;
        }
        for (int i = 0; i < 15; i++) {
            if (DreamnotesLogic.isHoldingNote(entity, i)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.minecraft.player.closeContainer();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // 原版无标签
    }
}
