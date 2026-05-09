package net.pasterdream.client.gui;

import net.pasterdream.world.inventory.WeaponWorkshopGuiMenu;
import net.pasterdream.network.WeaponWorkshopGuiButtonMessage;
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

public class WeaponWorkshopGuiScreen extends AbstractContainerScreen<WeaponWorkshopGuiMenu> {
	private final static HashMap<String, Object> guistate = WeaponWorkshopGuiMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	Button button_forging;

	public WeaponWorkshopGuiScreen(WeaponWorkshopGuiMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 186;
		this.imageHeight = 186;
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

		guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/weapon_workshop_gui.png"), this.leftPos + 0, this.topPos + 0, 0, 0, 186, 186, 186, 186);

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
		button_forging = Button.builder(Component.translatable("gui.pasterdream.weapon_workshop_gui.button_forging"), e -> {
			if (true) {
				PasterdreamMod.PACKET_HANDLER.sendToServer(new WeaponWorkshopGuiButtonMessage(0, x, y, z));
				WeaponWorkshopGuiButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		}).bounds(this.leftPos + 16, this.topPos + 54, 61, 20).build();
		guistate.put("button:button_forging", button_forging);
		this.addRenderableWidget(button_forging);
	}
}
