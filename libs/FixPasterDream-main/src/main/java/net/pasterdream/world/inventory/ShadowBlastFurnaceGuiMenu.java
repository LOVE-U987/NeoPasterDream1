
package net.pasterdream.world.inventory;

import net.pasterdream.network.ShadowBlastFurnaceGuiSlotMessage;
import net.pasterdream.init.PasterdreamModMenus;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import java.util.function.Supplier;
import java.util.Map;
import java.util.HashMap;

public class ShadowBlastFurnaceGuiMenu extends AbstractContainerMenu {
	public final static HashMap<String, Object> guistate = new HashMap<>();
	public final Level world;
	public final Player entity;
	public int x, y, z;
	private ContainerLevelAccess access = ContainerLevelAccess.NULL;
	private IItemHandler internal;
	private final Map<Integer, Slot> customSlots = new HashMap<>();
	private boolean bound = false;
	private BlockEntity boundBlockEntity = null;
    public BlockEntity getBoundBlockEntity(){
        return boundBlockEntity;
    }
	public ShadowBlastFurnaceGuiMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
		super(PasterdreamModMenus.SHADOW_BLAST_FURNACE_GUI.get(), id);
		this.entity = inv.player;
		this.world = inv.player.level();
		this.internal = new ItemStackHandler(6);
		BlockPos pos = null;
		if (extraData != null) {
			pos = extraData.readBlockPos();
			this.x = pos.getX();
			this.y = pos.getY();
			this.z = pos.getZ();
			access = ContainerLevelAccess.create(world, pos);
            boundBlockEntity = this.world.getBlockEntity(pos);
            if (boundBlockEntity != null)
                boundBlockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(capability -> {
                    this.internal = capability;
                    this.bound = true;
                });
		}
		this.customSlots.put(0, this.addSlot(new SlotItemHandler(internal, 0, 25, 24) {
			private final int slot = 0;
		}));
		this.customSlots.put(1, this.addSlot(new SlotItemHandler(internal, 1, 25, 69) {
			private final int slot = 1;
		}));
        this.customSlots.put(2, this.addSlot(new SlotItemHandler(internal, 2, 61, 105) {
            private final int slot = 2;
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        }));
        this.customSlots.put(3, this.addSlot(new SlotItemHandler(internal, 3, 97, 105) {
            private final int slot = 3;
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        }));
		this.customSlots.put(4, this.addSlot(new SlotItemHandler(internal, 4, 133, 24) {
			private final int slot = 4;
		}));
		this.customSlots.put(5, this.addSlot(new SlotItemHandler(internal, 5, 133, 69) {
			private final int slot = 5;
			@Override
			public boolean mayPlace(ItemStack stack) {
				return false;
			}
		}));
		for (int si = 0; si < 3; ++si)
			for (int sj = 0; sj < 9; ++sj)
				this.addSlot(new Slot(inv, sj + (si + 1) * 9, 8 + sj * 18, 50 + 84 + si * 18));
		for (int si = 0; si < 9; ++si)
			this.addSlot(new Slot(inv, si, 8 + si * 18, 50 + 142));
	}

	@Override
	public boolean stillValid(Player player) {
		if (this.bound) {
            return AbstractContainerMenu.stillValid(this.access, player, this.boundBlockEntity.getBlockState().getBlock());
		}
		return true;
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = (Slot) this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index < 6) {
				if (!this.moveItemStackTo(itemstack1, 6, this.slots.size(), true))
					return ItemStack.EMPTY;
				slot.onQuickCraft(itemstack1, itemstack);
			} else if (!this.moveItemStackTo(itemstack1, 0, 6, false)) {
				if (index < 6 + 27) {
					if (!this.moveItemStackTo(itemstack1, 6 + 27, this.slots.size(), true))
						return ItemStack.EMPTY;
				} else {
					if (!this.moveItemStackTo(itemstack1, 6, 6 + 27, false))
						return ItemStack.EMPTY;
				}
				return ItemStack.EMPTY;
			}
			if (itemstack1.getCount() == 0)
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();
			if (itemstack1.getCount() == itemstack.getCount())
				return ItemStack.EMPTY;
			slot.onTake(playerIn, itemstack1);
		}
		return itemstack;
	}

	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);
		if (!bound && playerIn instanceof ServerPlayer serverPlayer) {
			if (!serverPlayer.isAlive() || serverPlayer.hasDisconnected()) {
				for (int j = 0; j < internal.getSlots(); ++j) {
					playerIn.drop(internal.extractItem(j, internal.getStackInSlot(j).getCount(), false), false);
				}
			} else {
				for (int i = 0; i < internal.getSlots(); ++i) {
					playerIn.getInventory().placeItemBackInInventory(internal.extractItem(i, internal.getStackInSlot(i).getCount(), false));
				}
			}
		}
	}
    @Deprecated
	private void slotChanged(int slotid, int ctype, int meta) {
		if (this.world != null && this.world.isClientSide()) {
			PasterdreamMod.PACKET_HANDLER.sendToServer(new ShadowBlastFurnaceGuiSlotMessage(slotid, x, y, z, ctype, meta));
			ShadowBlastFurnaceGuiSlotMessage.handleSlotAction(entity, slotid, ctype, meta, x, y, z);
		}
	}
}
