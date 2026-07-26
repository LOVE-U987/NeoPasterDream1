package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.block.entity.ShadowDeskBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDMenusFurniture;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

/**
 * 影之桌 GUI 容器菜单（shadow_desk）
 * <p>
 * 与原版 ShadowDeskGuiMenu 布局一致：
 * 1 格展示槽 (79,26) + 玩家背包（y=84 起）+ 快捷栏（y=142）。
 */
public class ShadowDeskMenu extends AbstractContainerMenu {

    private final BlockEntity blockEntity;
    private final Level level;

    /**
     * 网络工厂构造（客户端）
     *
     * @param id        容器 ID
     * @param inv       玩家背包
     * @param extraData 附加数据（方块坐标）
     */
    public ShadowDeskMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    /**
     * 服务端构造
     *
     * @param id          容器 ID
     * @param inv         玩家背包
     * @param blockEntity 影之桌方块实体
     */
    public ShadowDeskMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(PDMenusFurniture.SHADOW_DESK.get(), id);
        this.blockEntity = blockEntity;
        this.level = inv.player.level();

        IItemHandler handler = blockEntity instanceof ShadowDeskBlockEntity desk
                ? desk.getItemHandler() : new ItemStackHandler(1);

        this.addSlot(new SlotItemHandler(handler, 0, 79, 26));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + (row + 1) * 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index < 1) {
                if (!this.moveItemStackTo(stack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, 1, false)) {
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

    @Override
    public boolean stillValid(Player player) {
        if (blockEntity == null) {
            return false;
        }
        return AbstractContainerMenu.stillValid(
                ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, blockEntity.getBlockState().getBlock());
    }
}
