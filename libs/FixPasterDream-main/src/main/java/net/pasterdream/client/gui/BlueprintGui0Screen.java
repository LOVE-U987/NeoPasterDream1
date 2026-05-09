package net.pasterdream.client.gui;

import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.pasterdream.world.inventory.BlueprintGui0Menu;
import net.pasterdream.network.BlueprintGui0ButtonMessage;
import net.pasterdream.PasterdreamMod;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;

import com.mojang.blaze3d.systems.RenderSystem;

public class BlueprintGui0Screen extends AbstractContainerScreen<BlueprintGui0Menu> {
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	Button button_left;
	Button button_right;
    public static class ScreenVar{
        int now_page;
        int max_page;
        public void add_page()
        {
            now_page = Math.min(max_page,now_page + 1);
        }
        public void sub_page()
        {
            now_page = Math.max(1,now_page - 1);
        }
    }
    ScreenVar screen_var;
	public BlueprintGui0Screen(BlueprintGui0Menu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 196;
		this.imageHeight = 216;
        this.screen_var = new ScreenVar();
        this.screen_var.now_page = 1;
        this.screen_var.max_page = container.getBluePrint().getMaxPage();
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/blueprint_gui_0.png"), this.leftPos, this.topPos, 0, 0, 196, 216, 196, 216);
		RenderSystem.disableBlend();
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		return super.keyPressed(key, b, c);
	}

	@Override
	public void containerTick() {
		super.containerTick();
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        FormattedCharSequence formattedcharsequence = Component.translatable("screen.pasterdream.pagenum",this.screen_var.now_page).getVisualOrderText();
        guiGraphics.drawString(this.font, formattedcharsequence, 97 - this.font.width(formattedcharsequence)/2, 7, -394759, false);
	}

	@Override
	public void onClose() {
		super.onClose();
	}

	@Override
	public void init() {
		super.init();
		button_left = Button.builder(Component.translatable("gui.pasterdream.blueprint_gui_0.button_empty"), e -> {
            this.screen_var.sub_page();
            PasterdreamMod.PACKET_HANDLER.sendToServer(new BlueprintGui0ButtonMessage(this.screen_var.now_page, x, y, z));
        }).bounds(this.leftPos + 11, this.topPos + 58, 30, 20).build();
        this.getMenu().guistate.put("button:button_empty", button_left);
		this.addRenderableWidget(button_left);
		button_right = Button.builder(Component.translatable("gui.pasterdream.blueprint_gui_0.button_empty1"), e -> {
            this.screen_var.add_page();
            PasterdreamMod.PACKET_HANDLER.sendToServer(new BlueprintGui0ButtonMessage(this.screen_var.now_page, x, y, z));
        }).bounds(this.leftPos + 153, this.topPos + 58, 30, 20).build();
        this.getMenu().guistate.put("button:button_empty1", button_right);
		this.addRenderableWidget(button_right);
	}
}
