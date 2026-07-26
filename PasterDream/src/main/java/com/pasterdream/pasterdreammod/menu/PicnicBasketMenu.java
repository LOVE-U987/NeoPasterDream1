package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.block.entity.PicnicBasketBlockEntity;
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
 * 野餐篮 GUI 容器菜单（picnic_basket）
 * <p>
 * 与原版 PicnicBasketGuiMenu 布局一致：
 * 15 格（5×3，x=43..115 步进 18，y=23/41/59）+
 * 玩家背包（y=114 起）+ 快捷栏（y=172）——玩家区整体下移 30 像素。
 */
public class PicnicBasketMenu extends AbstractContainerMenu {

    private final BlockEntity blockEntity;
    private final Level level;

    /**
     * 网络工厂构造（客户端）
     *
     * @param id        容器 ID
     * @param inv       玩家背包
     * @param extraData 附加数据（方块坐标）
     */
    public PicnicBasketMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    /**
     * 服务端构造
     *
     * @param id          容器 ID
     * @param inv         玩家背包
     * @param blockEntity 野餐篮方块实体
     */
    public PicnicBasketMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(PDMenusFurniture.PICNIC_BASKET.get(), id);
        this.blockEntity = blockEntity;
        this.level = inv.player.level();

        IItemHandler handler = blockEntity instanceof PicnicBasketBlockEntity basket
                ? basket.getItemHandler() : new ItemStackHandler(15);

        // 15 格容器：5×3
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 5; col++) {
                this.addSlot(new SlotItemHandler(handler, col + row * 5,
                        43 + col * 18, 23 + row * 18));
            }
        }
        // 玩家背包（下移 30）
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + (row + 1) * 9, 8 + col * 18, 114 + row * 18));
            }
        }
        // 快捷栏
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, 172));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index < 15) {
                if (!this.moveItemStackTo(stack, 15, 51, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, 15, false)) {
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
