package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.block.entity.ShadowChestBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDMenus;
import net.minecraft.core.BlockPos;
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
 * 影之箱 GUI 容器菜单 (Shadow Chest Menu)
 * 15 格库存（5×3 网格）+ 玩家背包栏
 * 与原模组的 ShadowChestGuiMenu 功能一致
 */
public class ShadowChestMenu extends AbstractContainerMenu {
    private final ShadowChestBlockEntity blockEntity;
    private final Level level;

    /**
     * 构造影之箱菜单
     *
     * @param id        容器 ID
     * @param inv       玩家库存
     * @param extraData 包含 BlockPos 的网络缓冲区
     */
    public ShadowChestMenu(int id, Inventory inv, net.minecraft.network.FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    /**
     * 构造影之箱菜单
     *
     * @param id          容器 ID
     * @param inv         玩家库存
     * @param blockEntity 影之箱方块实体
     */
    public ShadowChestMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(PDMenus.SHADOW_CHEST.get(), id);
        this.blockEntity = (ShadowChestBlockEntity) blockEntity;
        this.level = inv.player.level();

        IItemHandler handler = this.blockEntity.getItemHandler();

        // 影之箱库存槽位: 5×3 网格 (索引 0-14)
        // 行 1 (y=15): 43, 61, 79, 97, 115
        // 行 2 (y=33): 43, 61, 79, 97, 115
        // 行 3 (y=51): 43, 61, 79, 97, 115
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 5; col++) {
                this.addSlot(new SlotItemHandler(handler, col + row * 5,
                        43 + col * 18, 15 + row * 18));
            }
        }

        // 玩家背包 (索引 15-50)
        // 玩家背包: 3×9 网格 (y=84-142)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9,
                        8 + col * 18, 84 + row * 18));
            }
        }
        // 玩家快捷栏: 1×9 (y=142)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }
    }

    /**
     * 获取方块实体
     *
     * @return 影之箱方块实体
     */
    public ShadowChestBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index < 15) {
                // 从影之箱移到玩家背包
                if (!this.moveItemStackTo(stackInSlot, 15, 51, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从玩家背包移到影之箱
                if (!this.moveItemStackTo(stackInSlot, 0, 15, false)) {
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