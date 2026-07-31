package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.block.entity.DreamAccumulatorBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDItems;
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
 * 蓄梦池 GUI 容器菜单 (Dream Accumulator Menu)
 * 槽位布局完全还原原版 DreamAccumulatorGuiMenu（177×206 背景，
 * 原版按"先吸附剂后产物"的顺序添加槽位，这里保持一致）：
 * <ul>
 *   <li>索引 0：吸附剂槽（78, 82）——仅可放吸附剂（BE 槽 1）；</li>
 *   <li>索引 1：产物槽（78, 28）——取走时重置蓄梦计时（BE 槽 0，
 *       等价原版 DreamAccumulatorGuiSlotMessage 槽位消息语义）；</li>
 *   <li>索引 2-28：玩家背包（3×9，起始 (8, 124)）；</li>
 *   <li>索引 29-37：玩家快捷栏（1×9，起始 (8, 182)）。</li>
 * </ul>
 */
public class DreamAccumulatorMenu extends AbstractContainerMenu {

    private final DreamAccumulatorBlockEntity blockEntity;
    private final Level level;

    /**
     * 构造蓄梦池菜单（从网络缓冲区接收）
     *
     * @param id        容器 ID
     * @param inv       玩家库存
     * @param extraData 包含 BlockPos 的网络缓冲区
     */
    public DreamAccumulatorMenu(int id, Inventory inv, net.minecraft.network.FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    /**
     * 构造蓄梦池菜单
     *
     * @param id          容器 ID
     * @param inv         玩家库存
     * @param blockEntity 蓄梦池方块实体
     */
    public DreamAccumulatorMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(PDMenus.DREAM_ACCUMULATOR.get(), id);
        // 防御：BE 缺失或类型不符时构造为空菜单，stillValid 返回 false 由服务端自动关闭
        this.blockEntity = blockEntity instanceof DreamAccumulatorBlockEntity dae ? dae : null;
        this.level = inv.player.level();

        if (this.blockEntity != null) {
            IItemHandler handler = this.blockEntity.getItemHandler();

            // 菜单索引 0：吸附剂槽（BE 槽 1，原版坐标 78, 82；原版先添加此槽）
            this.addSlot(new SlotItemHandler(handler, DreamAccumulatorBlockEntity.SLOT_SORBENT, 78, 82) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.is(PDItems.SORBENT.get().asItem());
                }
            });
            // 菜单索引 1：产物槽（BE 槽 0，原版坐标 78, 28）；取走时重置蓄梦计时
            this.addSlot(new SlotItemHandler(handler, DreamAccumulatorBlockEntity.SLOT_OUTPUT, 78, 28) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }

                @Override
                public void onTake(Player player, ItemStack stack) {
                    super.onTake(player, stack);
                    // 原版槽位消息（changeType 1/2）均触发 DreamAccumulatorPr1：计时归零 + dream1 音效
                    if (!player.level().isClientSide()) {
                        DreamAccumulatorMenu.this.blockEntity.resetTime();
                    }
                }
            });
        }

        // 玩家背包：3×9 网格，起始 (8, 124)（原版偏移 0+8 / 40+84）
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 124 + row * 18));
            }
        }
        // 玩家快捷栏：1×9，起始 (8, 182)（原版偏移 40+142）
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, 182));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index < 2) {
                // 从蓄梦池槽位移到玩家背包（索引 2-37）
                if (!this.moveItemStackTo(stackInSlot, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stackInSlot, itemstack);
                // 补发 onTake 语义：Shift 取走产物同样重置计时
                if (index == 1 && !player.level().isClientSide() && this.blockEntity != null) {
                    this.blockEntity.resetTime();
                }
            } else {
                // 从玩家背包移到蓄梦池可放入槽位（产物槽由 mayPlace 拦截）
                if (!this.moveItemStackTo(stackInSlot, 0, 2, false)) {
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
        // 防御：BE 缺失/类型不符时菜单立即失效，由服务端关闭
        if (this.blockEntity == null) {
            return false;
        }
        return AbstractContainerMenu.stillValid(
                ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, blockEntity.getBlockState().getBlock());
    }
}
