
package net.pasterdream.world.inventory;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.pasterdream.PasterdreamMod;
import net.pasterdream.event.BluePrintLoader;
import net.pasterdream.init.PasterdreamModMenus;

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
import net.pasterdream.item.BlueprintItem;

import java.util.List;
import java.util.function.Supplier;
import java.util.Map;
import java.util.HashMap;

public class BlueprintGui0Menu extends AbstractContainerMenu {
	public final HashMap<String, Object> guistate = new HashMap<>();
	public final Level world;
	public final Player entity;
	public int x, y, z;
	private ContainerLevelAccess access = ContainerLevelAccess.NULL;
	private IItemHandler internal;
	private final Map<Integer, Slot> customSlots = new HashMap<>();
    private BluePrintLoader.BluePrint bluePrint = null;
    private InteractionHand hand;
	public BlueprintGui0Menu(int id, Inventory inv, FriendlyByteBuf extraData) {
		super(PasterdreamModMenus.BLUEPRINT_GUI_0.get(), id);
		this.entity = inv.player;
		this.world = inv.player.level();
		this.internal = new ItemStackHandler(25);
        if (extraData != null) {
            bluePrint = BluePrintLoader.get(extraData.readResourceLocation());
        }
		BlockPos pos;
        if (extraData != null) {
			pos = extraData.readBlockPos();
			this.x = pos.getX();
			this.y = pos.getY();
			this.z = pos.getZ();
			access = ContainerLevelAccess.create(world, pos);
		}
        for(int i = 0; i < 25; ++i)
        {
            int x = i % 5;
            int y = i / 5;
            this.customSlots.put(i, this.addSlot(new SlotItemHandler(internal, i, 53 + 18 * x, 24 + 18 * y) {
                @Override
                public boolean mayPickup(Player entity) {
                    return false;
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            }));
        }
        if (extraData != null) {
            boolean ismainhand = extraData.readBoolean();
            hand = ismainhand?InteractionHand.MAIN_HAND:InteractionHand.OFF_HAND;
        }
        refreshSlot(entity,0);
		for (int si = 0; si < 3; ++si)
			for (int sj = 0; sj < 9; ++sj)
				this.addSlot(new Slot(inv, sj + (si + 1) * 9, 10 + 8 + sj * 18, 50 + 84 + si * 18){
                    @Override
                    public boolean mayPickup(Player entity) {
                        return hand == InteractionHand.MAIN_HAND?this.getContainerSlot() != inv.selected:super.mayPickup(entity);
                    }

                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return hand == InteractionHand.MAIN_HAND?this.getContainerSlot() != inv.selected:super.mayPlace(stack);
                    }
                });
		for (int si = 0; si < 9; ++si)
			this.addSlot(new Slot(inv, si, 10 + 8 + si * 18, 50 + 142){
                @Override
                public boolean mayPickup(Player entity) {
                    return hand == InteractionHand.MAIN_HAND?this.getContainerSlot() != inv.selected:super.mayPickup(entity);
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return hand == InteractionHand.MAIN_HAND?this.getContainerSlot() != inv.selected:super.mayPlace(stack);
                }
            });
	}
    public BluePrintLoader.BluePrint getBluePrint()
    {
        return this.bluePrint;
    }
    public void refreshSlot(Player pl, int index)
    {
        List<Item> itemList = bluePrint.get(index);
        for (int i = 0 ;i < 25; ++i)
        {
            ItemStack setstack = new ItemStack(itemList.get(i));
            setstack.setCount(1);
            this.customSlots.get(i).set(setstack);
            pl.containerMenu.broadcastChanges();
        }
    }
	@Override
	public boolean stillValid(Player player) {
		return entity.isAlive();
	}
    @Override
    public void clicked(int slot, int button, ClickType clickType, Player player) {
        if(!(hand == InteractionHand.OFF_HAND && clickType == ClickType.SWAP))
        {
            super.clicked(slot,button,clickType,player);
        }
    }
	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = (Slot) this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index < 25) {
				if (!this.moveItemStackTo(itemstack1, 25, this.slots.size(), true))
					return ItemStack.EMPTY;
				slot.onQuickCraft(itemstack1, itemstack);
			} else if (!this.moveItemStackTo(itemstack1, 0, 25, false)) {
				if (index < 25 + 27) {
					if (!this.moveItemStackTo(itemstack1, 25 + 27, this.slots.size(), true))
						return ItemStack.EMPTY;
				} else {
					if (!this.moveItemStackTo(itemstack1, 25, 25 + 27, false))
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

}
