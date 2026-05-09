package net.pasterdream.client.gui;

import net.pasterdream.world.inventory.ResearchTableGuiMenu;
import net.pasterdream.network.ResearchTableGuiButtonMessage;
import net.pasterdream.PasterdreamMod;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class ResearchTableGuiScreen extends AbstractContainerScreen<ResearchTableGuiMenu> {
	private final static HashMap<String, Object> guistate = ResearchTableGuiMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	Button button_copy;
	Button button_study;

	public ResearchTableGuiScreen(ResearchTableGuiMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 206;
		this.imageHeight = 206;
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

		guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/research_table_gui.png"), this.leftPos + 0, this.topPos + 0, 0, 0, 206, 206, 206, 206);

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
	}

	@Override
	public void onClose() {
		super.onClose();
	}

	@Override
	public void init() {
		super.init();
		button_copy = Button.builder(Component.translatable("gui.pasterdream.research_table_gui.button_copy"), e -> {
			if (true) {
				PasterdreamMod.PACKET_HANDLER.sendToServer(new ResearchTableGuiButtonMessage(0, x, y, z));
				ResearchTableGuiButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		}).bounds(this.leftPos + 75, this.topPos + 35, 46, 20).build();
		guistate.put("button:button_copy", button_copy);
		this.addRenderableWidget(button_copy);
		button_study = Button.builder(Component.translatable("gui.pasterdream.research_table_gui.button_study"), e -> {
			if (true) {
				PasterdreamMod.PACKET_HANDLER.sendToServer(new ResearchTableGuiButtonMessage(1, x, y, z));
				ResearchTableGuiButtonMessage.handleButtonAction(entity, 1, x, y, z);
			}
		}).bounds(this.leftPos + 73, this.topPos + 80, 51, 20).build();
		guistate.put("button:button_study", button_study);
		this.addRenderableWidget(button_study);
	}
}
