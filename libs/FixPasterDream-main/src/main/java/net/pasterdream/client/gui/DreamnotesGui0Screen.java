package net.pasterdream.client.gui;

import net.pasterdream.world.inventory.DreamnotesGui0Menu;
import net.pasterdream.procedures.DreamnotesIf0Procedure;
import net.pasterdream.procedures.DreamnotesIf1Procedure;
import net.pasterdream.procedures.DreamnotesIf2Procedure;
import net.pasterdream.procedures.DreamnotesIf3Procedure;
import net.pasterdream.procedures.DreamnotesIf4Procedure;
import net.pasterdream.procedures.DreamnotesIf5Procedure;
import net.pasterdream.procedures.DreamnotesIf6Procedure;
import net.pasterdream.procedures.DreamnotesIf7Procedure;
import net.pasterdream.procedures.DreamnotesIf8Procedure;
import net.pasterdream.procedures.DreamnotesIf9Procedure;
import net.pasterdream.procedures.DreamnotesIf10Procedure;
import net.pasterdream.procedures.DreamnotesIf11Procedure;
import net.pasterdream.procedures.DreamnotesIf12Procedure;
import net.pasterdream.procedures.DreamnotesIf13Procedure;
import net.pasterdream.procedures.DreamnotesIf14Procedure;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiGraphics;

import java.util.HashMap;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

public class DreamnotesGui0Screen extends AbstractContainerScreen<DreamnotesGui0Menu> {
	private final static HashMap<String, Object> guistate = DreamnotesGui0Menu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;

	public DreamnotesGui0Screen(DreamnotesGui0Menu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
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
		if (DreamnotesIf0Procedure.execute(entity)) {
			guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/xun_meng_zhe_bi_ji__gui0.png"), 
			this.leftPos + -6, this.topPos + -44, 0, 0, 196, 256, 196, 256);
		}
		if (DreamnotesIf1Procedure.execute(entity)) {
			guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/xun_meng_zhe_bi_ji__gui1.png"), 
			this.leftPos + -6, this.topPos + -44, 0, 0, 196, 256, 196, 256);
		}
		if (DreamnotesIf2Procedure.execute(entity)) {
			guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/xun_meng_zhe_bi_ji__gui2.png"), 
			this.leftPos + -6, this.topPos + -44, 0, 0, 196, 256, 196, 256);
		}
		if (DreamnotesIf3Procedure.execute(entity)) {
			guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/xun_meng_zhe_bi_ji__gui3.png"), 
			this.leftPos + -6, this.topPos + -44, 0, 0, 196, 256, 196, 256);
		}
		if (DreamnotesIf4Procedure.execute(entity)) {
			guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/xun_meng_zhe_bi_ji__gui4.png"), 
			this.leftPos + -6, this.topPos + -44, 0, 0, 196, 256, 196, 256);
		}
		if (DreamnotesIf5Procedure.execute(entity)) {
			guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/xun_meng_zhe_bi_ji__gui5.png"), 
			this.leftPos + -6, this.topPos + -44, 0, 0, 196, 256, 196, 256);
		}
		if (DreamnotesIf6Procedure.execute(entity)) {
			guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/xun_meng_zhe_bi_ji__gui6.png"), 
			this.leftPos + -6, this.topPos + -44, 0, 0, 196, 256, 196, 256);
		}
		if (DreamnotesIf7Procedure.execute(entity)) {
			guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/xun_meng_zhe_bi_ji__gui7.png"), 
			this.leftPos + -6, this.topPos + -44, 0, 0, 196, 256, 196, 256);
		}
		if (DreamnotesIf8Procedure.execute(entity)) {
			guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/xun_meng_zhe_bi_ji__gui8.png"), 
			this.leftPos + -6, this.topPos + -44, 0, 0, 196, 256, 196, 256);
		}
		if (DreamnotesIf9Procedure.execute(entity)) {
			guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/xun_meng_zhe_bi_ji__gui9.png"), 
			this.leftPos + -6, this.topPos + -44, 0, 0, 196, 256, 196, 256);
		}
		if (DreamnotesIf10Procedure.execute(entity)) {
			guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/xun_meng_zhe_bi_ji__gui10.png"), 
			this.leftPos + -6, this.topPos + -44, 0, 0, 196, 256, 196, 256);
		}
		if (DreamnotesIf11Procedure.execute(entity)) {
			guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/xun_meng_zhe_bi_ji__gui11.png"), 
			this.leftPos + -6, this.topPos + -44, 0, 0, 196, 256, 196, 256);
		}
		if (DreamnotesIf12Procedure.execute(entity)) {
			guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/xun_meng_zhe_bi_ji__gui12.png"), 
			this.leftPos + -6, this.topPos + -44, 0, 0, 196, 256, 196, 256);
		}
		if (DreamnotesIf13Procedure.execute(entity)) {
			guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/xun_meng_zhe_bi_ji__gui13.png"), 
			this.leftPos + -6, this.topPos + -44, 0, 0, 196, 256, 196, 256);
		}
		if (DreamnotesIf14Procedure.execute(entity)) {
			guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/xun_meng_zhe_bi_ji__gui14.png"), 
			this.leftPos + -6, this.topPos + -44, 0, 0, 196, 256, 196, 256);
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
