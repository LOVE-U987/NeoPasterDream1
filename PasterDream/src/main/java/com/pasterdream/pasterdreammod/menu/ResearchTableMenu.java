package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.block.entity.ResearchTableBlockEntity;
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
 * 研究台 GUI 容器菜单 (Research Table Menu)
 * 槽位布局完全还原原版 ResearchTableGuiMenu（206×206 背景）：
 * <ul>
 *   <li>索引 0：笔与墨槽（175, 19）——仅可放笔与墨；</li>
 *   <li>索引 1：寻梦者笔记槽（40, 28）——仅可放 pasterdream:dreamnotes 标签物品；</li>
 *   <li>索引 2：羊皮纸槽（40, 46）——仅可放羊皮纸；</li>
 *   <li>索引 3：复制产物槽（139, 37）——仅可取出；</li>
 *   <li>索引 4：未知笔记槽（40, 82）——仅可放未知笔记；</li>
 *   <li>索引 5：研究产物槽（139, 82）——仅可取出；</li>
 *   <li>索引 6-32：玩家背包（3×9，起始 (23, 124)）；</li>
 *   <li>索引 33-41：玩家快捷栏（1×9，起始 (23, 182)）。</li>
 * </ul>
 * "复制"/"研究"按钮经 vanilla {@code clickMenuButton} 通道触发
 * {@link ResearchTableBlockEntity#copyNotes} / {@link ResearchTableBlockEntity#studyNotes}
 * （等价原版 ResearchTableGuiButtonMessage 按钮 0/1 语义）。
 */
public class ResearchTableMenu extends AbstractContainerMenu {

    /** "复制"按钮的菜单按钮 ID（原版按钮 0 → ResearchTablePr1） */
    public static final int BUTTON_COPY = 0;
    /** "研究"按钮的菜单按钮 ID（原版按钮 1 → ResearchTablePr0） */
    public static final int BUTTON_STUDY = 1;

    private final ResearchTableBlockEntity blockEntity;
    private final Level level;

    /**
     * 构造研究台菜单（从网络缓冲区接收）
     *
     * @param id        容器 ID
     * @param inv       玩家库存
     * @param extraData 包含 BlockPos 的网络缓冲区
     */
    public ResearchTableMenu(int id, Inventory inv, net.minecraft.network.FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    /**
     * 构造研究台菜单
     *
     * @param id          容器 ID
     * @param inv         玩家库存
     * @param blockEntity 研究台方块实体
     */
    public ResearchTableMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(PDMenus.RESEARCH_TABLE.get(), id);
        // 防御：BE 缺失或类型不符时构造为空菜单，stillValid 返回 false 由服务端自动关闭
        this.blockEntity = blockEntity instanceof ResearchTableBlockEntity rte ? rte : null;
        this.level = inv.player.level();

        if (this.blockEntity != null) {
            IItemHandler handler = this.blockEntity.getItemHandler();

            // 槽位 0：笔与墨（原版坐标 175, 19）
            this.addSlot(new SlotItemHandler(handler, ResearchTableBlockEntity.SLOT_PEN, 175, 19) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.is(PDItems.PEN_AND_INK.get().asItem());
                }
            });
            // 槽位 1：寻梦者笔记（原版坐标 40, 28，按 dreamnotes 标签过滤）
            this.addSlot(new SlotItemHandler(handler, ResearchTableBlockEntity.SLOT_NOTES, 40, 28) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.is(ResearchTableBlockEntity.DREAMNOTES_TAG);
                }
            });
            // 槽位 2：羊皮纸（原版坐标 40, 46）
            this.addSlot(new SlotItemHandler(handler, ResearchTableBlockEntity.SLOT_PERGAMYN, 40, 46) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.is(PDItems.PERGAMYN.get().asItem());
                }
            });
            // 槽位 3：复制产物，仅可取出（原版坐标 139, 37）
            this.addSlot(new SlotItemHandler(handler, ResearchTableBlockEntity.SLOT_COPY_RESULT, 139, 37) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
            // 槽位 4：未知笔记（原版坐标 40, 82）
            this.addSlot(new SlotItemHandler(handler, ResearchTableBlockEntity.SLOT_UNKNOWN, 40, 82) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.is(PDItems.UNKNOWNNOTES_0.get().asItem());
                }
            });
            // 槽位 5：研究产物，仅可取出（原版坐标 139, 82）
            this.addSlot(new SlotItemHandler(handler, ResearchTableBlockEntity.SLOT_STUDY_RESULT, 139, 82) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
        }

        // 玩家背包：3×9 网格，起始 (23, 124)（原版偏移 15+8 / 40+84）
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, 23 + col * 18, 124 + row * 18));
            }
        }
        // 玩家快捷栏：1×9，起始 (23, 182)（原版偏移 40+142）
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, 23 + col * 18, 182));
        }
    }

    /**
     * 处理 GUI 按钮点击（服务端调用）
     *
     * @param player 点击按钮的玩家
     * @param id     按钮 ID（{@link #BUTTON_COPY} / {@link #BUTTON_STUDY}）
     * @return 是否处理了该按钮
     */
    @Override
    public boolean clickMenuButton(Player player, int id) {
        // 防御：BE 已失效（方块被拆除等）或距离过远时拒绝按钮操作
        if (this.blockEntity == null || !this.stillValid(player)) {
            return false;
        }
        if (id == BUTTON_COPY) {
            this.blockEntity.copyNotes(player);
            return true;
        }
        if (id == BUTTON_STUDY) {
            this.blockEntity.studyNotes(player);
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

            if (index < 6) {
                // 从研究台槽位移到玩家背包（索引 6-41）
                if (!this.moveItemStackTo(stackInSlot, 6, 42, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从玩家背包移到研究台可放入槽位（3/5 由 mayPlace 拦截）
                if (!this.moveItemStackTo(stackInSlot, 0, 6, false)) {
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
