package net.pasterdream.client.gui;

import net.pasterdream.world.inventory.WorkshopAnvilGuiMenu;
import net.pasterdream.procedures.WorkshopAnvilPr2Procedure;
import net.pasterdream.procedures.NbtNumber5Procedure;
import net.pasterdream.procedures.NbtNumber4Procedure;
import net.pasterdream.procedures.NbtNumber3Procedure;
import net.pasterdream.procedures.NbtNumber2Procedure;
import net.pasterdream.procedures.NbtNumber1Procedure;
import net.pasterdream.network.WorkshopAnvilGuiButtonMessage;
import net.pasterdream.PasterdreamMod;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class WorkshopAnvilGuiScreen extends AbstractContainerScreen<WorkshopAnvilGuiMenu> {
	private final static HashMap<String, Object> guistate = WorkshopAnvilGuiMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	Button button_start;
	ImageButton imagebutton_anvil_button_0;
	ImageButton imagebutton_anvil_button_01;
	ImageButton imagebutton_anvil_button_02;
	ImageButton imagebutton_anvil_button_03;
	ImageButton imagebutton_anvil_button_04;

	public WorkshopAnvilGuiScreen(WorkshopAnvilGuiMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 216;
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

		guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/workshop_anvil_gui.png"), this.leftPos + 0, this.topPos + 0, 0, 0, 176, 216, 176, 216);

		if (NbtNumber1Procedure.execute(world, x, y, z)) {
			guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/anvil_button_1.png"), this.leftPos + 24, this.topPos + 14, 0, 0, 18, 18, 18, 18);
		}
		if (NbtNumber2Procedure.execute(world, x, y, z)) {
			guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/anvil_button_1.png"), this.leftPos + 51, this.topPos + 14, 0, 0, 18, 18, 18, 18);
		}
		if (NbtNumber3Procedure.execute(world, x, y, z)) {
			guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/anvil_button_1.png"), this.leftPos + 78, this.topPos + 14, 0, 0, 18, 18, 18, 18);
		}
		if (NbtNumber4Procedure.execute(world, x, y, z)) {
			guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/anvil_button_1.png"), this.leftPos + 105, this.topPos + 14, 0, 0, 18, 18, 18, 18);
		}
		if (NbtNumber5Procedure.execute(world, x, y, z)) {
			guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/anvil_button_1.png"), this.leftPos + 132, this.topPos + 14, 0, 0, 18, 18, 18, 18);
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
		guiGraphics.drawString(this.font,

				WorkshopAnvilPr2Procedure.execute(world, x, y, z), 78, 68, -12829636, false);
	}

	@Override
	public void onClose() {
		super.onClose();
	}

	@Override
	public void init() {
		super.init();
		button_start = Button.builder(Component.translatable("gui.pasterdream.workshop_anvil_gui.button_start"), e -> {
			if (true) {
				PasterdreamMod.PACKET_HANDLER.sendToServer(new WorkshopAnvilGuiButtonMessage(0, x, y, z));
				WorkshopAnvilGuiButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		}).bounds(this.leftPos + 62, this.topPos + 83, 51, 20).build();
		guistate.put("button:button_start", button_start);
		this.addRenderableWidget(button_start);
		imagebutton_anvil_button_0 = new ImageButton(this.leftPos + 24, this.topPos + 32, 18, 18, 0, 0, 18, new ResourceLocation("pasterdream:textures/screens/atlas/imagebutton_anvil_button_0.png"), 18, 36, e -> {
			if (true) {
				PasterdreamMod.PACKET_HANDLER.sendToServer(new WorkshopAnvilGuiButtonMessage(1, x, y, z));
				WorkshopAnvilGuiButtonMessage.handleButtonAction(entity, 1, x, y, z);
			}
		});
		guistate.put("button:imagebutton_anvil_button_0", imagebutton_anvil_button_0);
		this.addRenderableWidget(imagebutton_anvil_button_0);
		imagebutton_anvil_button_01 = new ImageButton(this.leftPos + 51, this.topPos + 32, 18, 18, 0, 0, 18, new ResourceLocation("pasterdream:textures/screens/atlas/imagebutton_anvil_button_01.png"), 18, 36, e -> {
			if (true) {
				PasterdreamMod.PACKET_HANDLER.sendToServer(new WorkshopAnvilGuiButtonMessage(2, x, y, z));
				WorkshopAnvilGuiButtonMessage.handleButtonAction(entity, 2, x, y, z);
			}
		});
		guistate.put("button:imagebutton_anvil_button_01", imagebutton_anvil_button_01);
		this.addRenderableWidget(imagebutton_anvil_button_01);
		imagebutton_anvil_button_02 = new ImageButton(this.leftPos + 78, this.topPos + 32, 18, 18, 0, 0, 18, new ResourceLocation("pasterdream:textures/screens/atlas/imagebutton_anvil_button_02.png"), 18, 36, e -> {
			if (true) {
				PasterdreamMod.PACKET_HANDLER.sendToServer(new WorkshopAnvilGuiButtonMessage(3, x, y, z));
				WorkshopAnvilGuiButtonMessage.handleButtonAction(entity, 3, x, y, z);
			}
		});
		guistate.put("button:imagebutton_anvil_button_02", imagebutton_anvil_button_02);
		this.addRenderableWidget(imagebutton_anvil_button_02);
		imagebutton_anvil_button_03 = new ImageButton(this.leftPos + 105, this.topPos + 32, 18, 18, 0, 0, 18, new ResourceLocation("pasterdream:textures/screens/atlas/imagebutton_anvil_button_03.png"), 18, 36, e -> {
			if (true) {
				PasterdreamMod.PACKET_HANDLER.sendToServer(new WorkshopAnvilGuiButtonMessage(4, x, y, z));
				WorkshopAnvilGuiButtonMessage.handleButtonAction(entity, 4, x, y, z);
			}
		});
		guistate.put("button:imagebutton_anvil_button_03", imagebutton_anvil_button_03);
		this.addRenderableWidget(imagebutton_anvil_button_03);
		imagebutton_anvil_button_04 = new ImageButton(this.leftPos + 132, this.topPos + 32, 18, 18, 0, 0, 18, new ResourceLocation("pasterdream:textures/screens/atlas/imagebutton_anvil_button_04.png"), 18, 36, e -> {
			if (true) {
				PasterdreamMod.PACKET_HANDLER.sendToServer(new WorkshopAnvilGuiButtonMessage(5, x, y, z));
				WorkshopAnvilGuiButtonMessage.handleButtonAction(entity, 5, x, y, z);
			}
		});
		guistate.put("button:imagebutton_anvil_button_04", imagebutton_anvil_button_04);
		this.addRenderableWidget(imagebutton_anvil_button_04);
	}
}
