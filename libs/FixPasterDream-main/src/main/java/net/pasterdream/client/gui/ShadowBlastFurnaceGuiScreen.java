package net.pasterdream.client.gui;

import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.pasterdream.block.entity.ShadowBlastFurnaceTileEntity;
import net.pasterdream.world.inventory.ShadowBlastFurnaceGuiMenu;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiGraphics;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class ShadowBlastFurnaceGuiScreen extends AbstractContainerScreen<ShadowBlastFurnaceGuiMenu> {
	private final static HashMap<String, Object> guistate = ShadowBlastFurnaceGuiMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;

	public ShadowBlastFurnaceGuiScreen(ShadowBlastFurnaceGuiMenu container, Inventory inventory, Component text) {
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
    private static final ResourceLocation gui = new ResourceLocation("pasterdream:textures/screens/shadow_blast_furnace_gui.png");
	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		guiGraphics.blit(gui, this.leftPos , this.topPos, 0, 0, 176, 216, 236, 216);
        if(this.getMenu().getBoundBlockEntity() instanceof ShadowBlastFurnaceTileEntity SBFTEntity){
            if(SBFTEntity.getWorkRecipeID() != null){
                int cap = SBFTEntity.getNeedBlastingTime();
                int now = SBFTEntity.getBlastingTime();
                if(cap > 0)
                {
                    int percent_height = (int) Math.nextUp(34.0 * now / cap);
                    guiGraphics.blit(gui, this.leftPos + 68, this.topPos + 71, 176, 0, 38, percent_height, 236, 216);
                }
            }
            SBFTEntity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(iFluidHandler->{
                int cap = iFluidHandler.getTankCapacity(0);
                int now = iFluidHandler.getFluidInTank(0).getAmount();
                if(cap > 0)
                {
                    int percent_height = (int) Math.nextUp(35.0 * now / cap);
                    guiGraphics.blit(gui, this.leftPos + 99, this.topPos + 46 + 35 - percent_height, 176, 34 + 35 - percent_height, 10, percent_height, 236, 216);
                }
            });
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
