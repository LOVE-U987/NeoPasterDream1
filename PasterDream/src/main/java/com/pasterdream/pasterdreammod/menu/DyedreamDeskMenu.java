package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.block.entity.DyedreamDeskBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

/**
 * 染梦书桌 GUI 容器菜单 (Dyedream Desk Menu)
 * 1 格物品槽（展示位）+ 玩家背包（27 格）+ 快捷栏（9 格）
 * 与原模组的 DyedreamDeskGuiMenu 功能一致
 *
 * 槽位分布：
 * - 索引 0：书桌展示槽（位置 79, 26）
 * - 索引 1-27：玩家背包（3×9，偏移 y=84）
 * - 索引 28-36：玩家快捷栏（1×9，偏移 y=142）
 */
public class DyedreamDeskMenu extends AbstractContainerMenu {
    private final DyedreamDeskBlockEntity blockEntity;
    private final Level level;

    /**
     * 构造染梦书桌菜单（从网络缓冲区接收）
     *
     * @param id        容器 ID
     * @param inv       玩家库存
     * @param extraData 包含 BlockPos 的网络缓冲区
     */
    public DyedreamDeskMenu(int id, Inventory inv, net.minecraft.network.FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    /**
     * 构造染梦书桌菜单
     *
     * @param id          容器 ID
     * @param inv         玩家库存
     * @param blockEntity 染梦书桌方块实体
     */
    public DyedreamDeskMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(PDMenus.DYEDREAM_DESK.get(), id);
        this.blockEntity = (DyedreamDeskBlockEntity) blockEntity;
        this.level = inv.player.level();

        IItemHandler handler = this.blockEntity.getItemHandler();

        // 书桌展示槽位：1 格，位置 (79, 26)
        this.addSlot(new SlotItemHandler(handler, 0, 79, 26));

        // 玩家背包：3×9 网格，起始 (8, 84)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9,
                        8 + col * 18, 84 + row * 18));
            }
        }

        // 玩家快捷栏：1×9，起始 (8, 142)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }
    }

    /**
     * 获取方块实体
     *
     * @return 染梦书桌方块实体
     */
    public DyedreamDeskBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index < 1) {
                // 从书桌展示槽移到玩家背包（索引 1-36）
                if (!this.moveItemStackTo(stackInSlot, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从玩家背包移到书桌展示槽（索引 0）
                if (!this.moveItemStackTo(stackInSlot, 0, 1, false)) {
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
                ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, blockEntity.getBlockState().getBlock());
    }
}