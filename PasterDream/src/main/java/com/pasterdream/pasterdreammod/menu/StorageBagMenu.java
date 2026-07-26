package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.item.StorageBagItem;
import com.pasterdream.pasterdreammod.registry.PDMenus;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * 储物袋 GUI 菜单（9 格小袋 / 25 格高级袋共用）。
 * <p>
 * 打开时从物品 {@code DataComponents.CONTAINER} 载入；关闭时写回。
 * 禁止把储物袋自身放进槽位。
 */
public class StorageBagMenu extends AbstractContainerMenu {

    private final ItemStack bag;
    private final ItemStackHandler handler;
    private final int bagSlots;
    private final boolean advanced;
    private final boolean mainHand;

    /**
     * 网络构造：顺序与 {@link StorageBagItem#use} 写入一致
     * （slotCount / advanced / mainHand）。
     */
    public StorageBagMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, buf.readVarInt(), buf.readBoolean(), buf.readBoolean());
    }

    private StorageBagMenu(int id, Inventory inv, int bagSlots, boolean advanced, boolean mainHand) {
        this(id, inv, resolveBag(inv.player, mainHand), bagSlots, advanced, mainHand);
    }

    /**
     * @param bag      打开时的储物袋物品（引用同一 stack）
     * @param bagSlots 槽位数
     * @param advanced 是否高级袋
     * @param mainHand 是否主手打开
     */
    public StorageBagMenu(int id, Inventory inv, ItemStack bag, int bagSlots, boolean advanced, boolean mainHand) {
        super(advanced ? PDMenus.STORAGE_BAG_0.get() : PDMenus.STORAGE_BAG.get(), id);
        this.bag = bag;
        this.bagSlots = bagSlots;
        this.advanced = advanced;
        this.mainHand = mainHand;
        this.handler = new ItemStackHandler(bagSlots) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return !(stack.getItem() instanceof StorageBagItem);
            }

            @Override
            protected void onContentsChanged(int slot) {
                StorageBagItem.writeContents(StorageBagMenu.this.bag, snapshot());
            }
        };
        List<ItemStack> existing = StorageBagItem.readContents(bag, bagSlots);
        for (int i = 0; i < bagSlots; i++) {
            handler.setStackInSlot(i, existing.get(i));
        }

        if (advanced) {
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 5; col++) {
                    int index = row * 5 + col;
                    this.addSlot(new SlotItemHandler(handler, index, 43 + col * 18, 16 + row * 18));
                }
            }
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 9; col++) {
                    this.addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 118 + row * 18));
                }
            }
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col, 8 + col * 18, 176));
            }
        } else {
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    int index = row * 3 + col;
                    this.addSlot(new SlotItemHandler(handler, index, 61 + col * 18, 17 + row * 18));
                }
            }
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 9; col++) {
                    this.addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
                }
            }
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col, 8 + col * 18, 142));
            }
        }

        if (!inv.player.level().isClientSide()) {
            inv.player.level().playSound(null, inv.player.blockPosition(),
                    PDSounds.ZIPPER.get(), SoundSource.NEUTRAL, 0.2f, 1.0f);
        }
    }

    private static ItemStack resolveBag(Player player, boolean mainHand) {
        return mainHand ? player.getMainHandItem() : player.getOffhandItem();
    }

    private List<ItemStack> snapshot() {
        List<ItemStack> list = new ArrayList<>(bagSlots);
        for (int i = 0; i < bagSlots; i++) {
            list.add(handler.getStackInSlot(i).copy());
        }
        return list;
    }

    public boolean isAdvanced() {
        return advanced;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide()) {
            StorageBagItem.writeContents(bag, snapshot());
        }
    }

    @Override
    public boolean stillValid(Player player) {
        ItemStack held = mainHand ? player.getMainHandItem() : player.getOffhandItem();
        return held == bag && held.getItem() instanceof StorageBagItem;
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < slots.size()) {
            Slot slot = slots.get(slotId);
            if (slot != null && slot.getItem() == bag) {
                return;
            }
        }
        super.clicked(slotId, button, clickType, player);
        if (!player.level().isClientSide()) {
            StorageBagItem.writeContents(bag, snapshot());
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index < bagSlots) {
                if (!this.moveItemStackTo(stack, bagSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, bagSlots, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }
}
