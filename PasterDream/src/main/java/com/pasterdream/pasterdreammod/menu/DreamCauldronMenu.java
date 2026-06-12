package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.block.entity.DreamCauldronBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDMenus;
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
 * 梦境炼药锅 GUI 容器菜单 (Dream Cauldron Menu)
 * 3 格输入槽 + 1 格输出槽 + 玩家背包（27 格）+ 快捷栏（9 格）
 *
 * 槽位分布：
 * - 索引 0-2：输入槽（位置 36, 17 / 54, 17 / 72, 17）
 * - 索引 3：输出槽（位置 134, 17）
 * - 索引 4-30：玩家背包（3×9，偏移 y=84）
 * - 索引 31-39：玩家快捷栏（1×9，偏移 y=142）
 */
public class DreamCauldronMenu extends AbstractContainerMenu {

    private final DreamCauldronBlockEntity blockEntity;
    private final Level level;

    /**
     * 构造梦境炼药锅菜单（从网络缓冲区接收）
     *
     * @param id        容器 ID
     * @param inv       玩家库存
     * @param extraData 包含 BlockPos 的网络缓冲区
     */
    public DreamCauldronMenu(int id, Inventory inv, net.minecraft.network.FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    /**
     * 构造梦境炼药锅菜单
     *
     * @param id          容器 ID
     * @param inv         玩家库存
     * @param blockEntity 梦境炼药锅方块实体
     */
    public DreamCauldronMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(PDMenus.DREAM_CAULDRON.get(), id);
        this.blockEntity = (DreamCauldronBlockEntity) blockEntity;
        this.level = inv.player.level();

        IItemHandler handler = this.blockEntity.getItemHandler();

        // 输入槽位 0-2：3 格，水平排列
        this.addSlot(new SlotItemHandler(handler, 0, 36, 17));
        this.addSlot(new SlotItemHandler(handler, 1, 54, 17));
        this.addSlot(new SlotItemHandler(handler, 2, 72, 17));

        // 输出槽位 3：1 格，不可手动放置（只有合成产出可放入）
        this.addSlot(new SlotItemHandler(handler, 3, 134, 17) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

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
     * @return 梦境炼药锅方块实体
     */
    public DreamCauldronBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index < 4) {
                // 从炼药锅槽位移到玩家背包（索引 4-39）
                if (!this.moveItemStackTo(stackInSlot, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从玩家背包移到炼药锅输入槽（索引 0-2）
                if (!this.moveItemStackTo(stackInSlot, 0, 3, false)) {
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