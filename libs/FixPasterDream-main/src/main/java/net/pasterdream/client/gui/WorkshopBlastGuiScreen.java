package net.pasterdream.client.gui;

import net.pasterdream.world.inventory.WorkshopBlastGuiMenu;
import net.pasterdream.procedures.WorkshopBlastGuiPr0Procedure;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiGraphics;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class WorkshopBlastGuiScreen extends AbstractContainerScreen<WorkshopBlastGuiMenu> {
	private final static HashMap<String, Object> guistate = WorkshopBlastGuiMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;

	public WorkshopBlastGuiScreen(WorkshopBlastGuiMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
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

		guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/workshop_blast_gui.png"), this.leftPos + 0, this.topPos + 0, 0, 0, 176, 196, 176, 196);

		if (WorkshopBlastGuiPr0Procedure.execute(world, x, y, z)) {
			guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/workshop_blast_gui_0.png"), this.leftPos + 0, this.topPos + 0, 0, 0, 176, 196, 176, 196);
		}
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
	}
}
