package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.registry.PDMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

/**
 * 融梦水晶箱 GUI 容器菜单 (Meltdream Chest Menu)
 *
 * 9 格库存（3×3 网格）+ 玩家背包栏
 * 右键打开的箱子时打开此菜单
 */
public class MeltdreamChestMenu extends AbstractContainerMenu {

    private final BlockEntity blockEntity;
    private final Level level;

    /**
     * 构造融梦水晶箱菜单（网络包）
     *
     * @param id        容器 ID
     * @param inv       玩家库存
     * @param extraData 包含 BlockPos 的网络缓冲区
     */
    public MeltdreamChestMenu(int id, Inventory inv, net.minecraft.network.FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    /**
     * 构造融梦水晶箱菜单
     *
     * @param id          容器 ID
     * @param inv         玩家库存
     * @param blockEntity 方块实体
     */
    public MeltdreamChestMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(PDMenus.MELTDREAM_CHEST.get(), id);
        this.blockEntity = blockEntity;
        this.level = inv.player.level();

        IItemHandler handler;
        if (blockEntity instanceof com.pasterdream.pasterdreammod.block.entity.MeltdreamChestBlockEntity chest) {
            handler = chest.getItemHandler();
        } else if (blockEntity instanceof com.pasterdream.pasterdreammod.block.entity.MeltdreamChestOpenBlockEntity openChest) {
            handler = openChest.getItemHandler();
        } else {
            handler = new net.neoforged.neoforge.items.ItemStackHandler(9);
        }

        // 融梦水晶箱库存槽位: 3×3 网格
        // 中心位置: (-1行偏移使网格居中)
        // 行 0 (y=17): 偏移量计算 = (176 - 9*18)/2 = 7, 但这里用 62 使其水平居中(62+18*3=116, 左右边距30)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new SlotItemHandler(handler, col + row * 3,
                        62 + col * 18, 17 + row * 18));
            }
        }

        // 玩家背包 (3×9 网格, y=100-158)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9,
                        8 + col * 18, 100 + row * 18));
            }
        }

        // 玩家快捷栏 (1×9, y=158)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, 158));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index < 9) {
                // 从融梦水晶箱移到玩家背包
                if (!this.moveItemStackTo(stackInSlot, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从玩家背包移到融梦水晶箱
                if (!this.moveItemStackTo(stackInSlot, 0, 9, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return AbstractContainerMenu.stillValid(
                net.minecraft.world.inventory.ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, blockEntity.getBlockState().getBlock());
    }
}