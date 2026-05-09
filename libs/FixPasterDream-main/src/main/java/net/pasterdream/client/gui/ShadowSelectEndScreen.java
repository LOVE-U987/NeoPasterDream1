package net.pasterdream.client.gui;

import net.pasterdream.world.inventory.ShadowSelectEndMenu;
import net.pasterdream.network.ShadowSelectEndButtonMessage;
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

public class ShadowSelectEndScreen extends AbstractContainerScreen<ShadowSelectEndMenu> {
	private final static HashMap<String, Object> guistate = ShadowSelectEndMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	ImageButton imagebutton_hei_an_an_niu;
	ImageButton imagebutton_guang_ming_an_niu;

	public ShadowSelectEndScreen(ShadowSelectEndMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 320;
		this.imageHeight = 200;
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

		guiGraphics.blit(new ResourceLocation("pasterdream:textures/screens/shadow_select_end_gui.png"), this.leftPos + 0, this.topPos + 3, 0, 0, 320, 200, 320, 200);

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
		guiGraphics.drawString(this.font, Component.translatable("gui.pasterdream.shadow_select_end.label_qing_gen_ju_zi_ji_de_di_zhi_jue_zuo_chu_xuan_ze"), 98, 9, -3355444, false);
		guiGraphics.drawString(this.font, Component.translatable("gui.pasterdream.shadow_select_end.label_zhe_hui_shi_tan_suo_zai_wei_lai_zou_xiang_bu_tong_de_jie_ju"), 95, 19, -3355444, false);
	}

	@Override
	public void onClose() {
		super.onClose();
	}

	@Override
	public void init() {
		super.init();
		imagebutton_hei_an_an_niu = new ImageButton(this.leftPos + 48, this.topPos + 54, 82, 87, 0, 0, 87, new ResourceLocation("pasterdream:textures/screens/atlas/imagebutton_hei_an_an_niu.png"), 82, 174, e -> {
			if (true) {
				PasterdreamMod.PACKET_HANDLER.sendToServer(new ShadowSelectEndButtonMessage(0, x, y, z));
				ShadowSelectEndButtonMessage.handleButtonAction(entity, 0, x, y, z);
			}
		});
		guistate.put("button:imagebutton_hei_an_an_niu", imagebutton_hei_an_an_niu);
		this.addRenderableWidget(imagebutton_hei_an_an_niu);
		imagebutton_guang_ming_an_niu = new ImageButton(this.leftPos + 183, this.topPos + 52, 82, 87, 0, 0, 87, new ResourceLocation("pasterdream:textures/screens/atlas/imagebutton_guang_ming_an_niu.png"), 82, 174, e -> {
			if (true) {
				PasterdreamMod.PACKET_HANDLER.sendToServer(new ShadowSelectEndButtonMessage(1, x, y, z));
				ShadowSelectEndButtonMessage.handleButtonAction(entity, 1, x, y, z);
			}
		});
		guistate.put("button:imagebutton_guang_ming_an_niu", imagebutton_guang_ming_an_niu);
		this.addRenderableWidget(imagebutton_guang_ming_an_niu);
	}
}
