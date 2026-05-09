package net.pasterdream.client.gui;

import net.pasterdream.world.inventory.DreamCauldronGuiMenu;
import net.pasterdream.procedures.DreamCauldronTxtPr0Procedure;
import net.pasterdream.network.DreamCauldronGuiButtonMessage;
import net.pasterdream.PasterdreamMod;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.GuiGraphics;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class DreamCauldronGuiScreen extends AbstractContainerScreen<DreamCauldronGuiMenu> {
	private final static HashMap<String, Object> guistate = DreamCauldronGuiMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	ImageButton imagebutton_dream_cauldron_gui_button0;

	public DreamCauldronGuiScreen(DreamCauldronGuiMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 196;
		this.imageHeight = 196;
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

		guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/dream_cauldron_gui.png"), this.leftPos + 0, this.topPos + 0, 0, 0, 196, 196, 196, 196);

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
		guiGraphics.drawString(this.font,

				DreamCauldronTxtPr0Procedure.execute(world, x, y, z), 170, 43, -26887, false);
	}

	@Override
	public void onClose() {
		super.onClose();
	}

	@Override
	public void init() {
		super.init();
		imagebutton_dream_cauldron_gui_button0 = new ImageButton(this.leftPos + 78, this.topPos + 71, 38, 15, 0, 0, 15, new ResourceLocation("pasterdream:textures/screens/atlas/imagebutton_dream_cauldron_gui_button0.png"), 38, 30, e -> {
			if (true) {
				PasterdreamMod.PACKET_HANDLER.sendToServer(new DreamCauldronGuiButtonMessage(0, x, y, z));
				DreamCauldronGuiButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		});
		guistate.put("button:imagebutton_dream_cauldron_gui_button0", imagebutton_dream_cauldron_gui_button0);
		this.addRenderableWidget(imagebutton_dream_cauldron_gui_button0);
	}
}
