package net.pasterdream.client.gui;

import net.pasterdream.world.inventory.TheEndlessBookOfDreamSeekersGuiMenu;
import net.pasterdream.network.TheEndlessBookOfDreamSeekersGuiButtonMessage;
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

public class TheEndlessBookOfDreamSeekersGuiScreen extends AbstractContainerScreen<TheEndlessBookOfDreamSeekersGuiMenu> {
	private final static HashMap<String, Object> guistate = TheEndlessBookOfDreamSeekersGuiMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	Button button_dao_ru;

	public TheEndlessBookOfDreamSeekersGuiScreen(TheEndlessBookOfDreamSeekersGuiMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}

	private static final ResourceLocation texture = new ResourceLocation("pasterdream:textures/screens/the_endless_book_of_dream_seekers_gui.png");

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
		guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
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
		button_dao_ru = Button.builder(Component.translatable("gui.pasterdream.the_endless_book_of_dream_seekers_gui.button_dao_ru"), e -> {
			if (true) {
				PasterdreamMod.PACKET_HANDLER.sendToServer(new TheEndlessBookOfDreamSeekersGuiButtonMessage(0, x, y, z));
				TheEndlessBookOfDreamSeekersGuiButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		}).bounds(this.leftPos + 69, this.topPos + 25, 35, 20).build();
		guistate.put("button:button_dao_ru", button_dao_ru);
		this.addRenderableWidget(button_dao_ru);
	}
}
