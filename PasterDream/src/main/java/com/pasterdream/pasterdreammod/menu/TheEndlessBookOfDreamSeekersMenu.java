package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.block.entity.TheEndlessBookOfDreamSeekersBlockEntity;
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
 * 寻梦者的永恒书卷 GUI 容器菜单 (The Endless Book of Dream Seekers Menu)
 * 布局对照原版 TheEndlessBookOfDreamSeekersGuiMenu：
 * <ul>
 *   <li>索引 0：展示槽（115, 26）——仅可取出；</li>
 *   <li>索引 1：导入槽（43, 26）；</li>
 *   <li>索引 2-28：玩家背包（3×9，起始 (8, 84)）；</li>
 *   <li>索引 29-37：玩家快捷栏（1×9，起始 (8, 142)）。</li>
 * </ul>
 * "导入"按钮经 vanilla {@code clickMenuButton} 触发
 * {@link TheEndlessBookOfDreamSeekersBlockEntity#importFromSlot}
 * （等价原版 GuiButtonMessage → Pr5）。
 */
public class TheEndlessBookOfDreamSeekersMenu extends AbstractContainerMenu {

    /** "导入"按钮 ID（原版 buttonID == 0 → Pr5） */
    public static final int BUTTON_IMPORT = 0;

    private final TheEndlessBookOfDreamSeekersBlockEntity blockEntity;
    private final Level level;

    /**
     * 构造寻梦者的永恒书卷菜单（从网络缓冲区接收）
     *
     * @param id        容器 ID
     * @param inv       玩家库存
     * @param extraData 包含 BlockPos 的网络缓冲区
     */
    public TheEndlessBookOfDreamSeekersMenu(int id, Inventory inv, net.minecraft.network.FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    /**
     * 构造寻梦者的永恒书卷菜单
     *
     * @param id          容器 ID
     * @param inv         玩家库存
     * @param blockEntity 书卷方块实体
     */
    public TheEndlessBookOfDreamSeekersMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(PDMenus.THE_ENDLESS_BOOK_OF_DREAM_SEEKERS.get(), id);
        this.blockEntity = (TheEndlessBookOfDreamSeekersBlockEntity) blockEntity;
        this.level = inv.player.level();

        IItemHandler handler = this.blockEntity.getItemHandler();

        // 展示槽：原版 (115, 26)，仅可取出
        this.addSlot(new SlotItemHandler(handler, TheEndlessBookOfDreamSeekersBlockEntity.SLOT_DISPLAY, 115, 26) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        // 导入槽：原版 (43, 26)
        this.addSlot(new SlotItemHandler(handler, TheEndlessBookOfDreamSeekersBlockEntity.SLOT_IMPORT, 43, 26));

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
     * @return 书卷方块实体
     */
    public TheEndlessBookOfDreamSeekersBlockEntity getBlockEntity() {
        return blockEntity;
    }

    /**
     * 处理 GUI 按钮点击（服务端调用）
     *
     * @param player 点击按钮的玩家
     * @param id     按钮 ID（{@link #BUTTON_IMPORT}）
     * @return 是否处理了该按钮
     */
    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == BUTTON_IMPORT) {
            if (!player.level().isClientSide()) {
                this.blockEntity.importFromSlot();
            }
            return true;
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            // 0-1 书卷槽 → 玩家；玩家 → 书卷（展示槽 mayPlace=false，只会进导入槽）
            if (index < 2) {
                if (!this.moveItemStackTo(stackInSlot, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
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
        return AbstractContainerMenu.stillValid(
                ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, blockEntity.getBlockState().getBlock());
    }
}
