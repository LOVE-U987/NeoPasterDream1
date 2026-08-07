package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.block.entity.WorkshopBlastBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

/**
 * 工坊锻炉 GUI 容器菜单 (Workshop Blast Menu)
 * 槽位布局完全还原原版 WorkshopBlastGuiMenu（176×196 背景）：
 * <ul>
 *   <li>索引 0：原胚输入槽（79, 23）</li>
 *   <li>索引 1：镶嵌材料槽（25, 50）——暗影碎片</li>
 *   <li>索引 2：岩浆桶输入槽（133, 32）</li>
 *   <li>索引 3：空桶回收槽（133, 68)——仅可取出</li>
 *   <li>索引 4：产物槽（79, 77)——仅可取出</li>
 *   <li>索引 5-31：玩家背包（3×9，起始 (8, 114)）</li>
 *   <li>索引 32-40：玩家快捷栏（1×9，起始 (8, 172)）</li>
 * </ul>
 * 无按钮：原版岩浆桶注入由槽位变更消息触发，
 * 新版由 BE 的 10 tick 周期轮询完成（语义超集）；
 * 储罐岩浆量经 {@link DataSlot} 同步，供满罐贴图（≥1000mB）判断。
 */
public class WorkshopBlastMenu extends AbstractContainerMenu {

    private final WorkshopBlastBlockEntity blockEntity;
    private final Level level;

    /** 客户端同步的储罐岩浆量（mB） */
    private final DataSlot fluidAmount = DataSlot.standalone();

    /**
     * 构造工坊锻炉菜单（从网络缓冲区接收）
     *
     * @param id        容器 ID
     * @param inv       玩家库存
     * @param extraData 包含 BlockPos 的网络缓冲区
     */
    public WorkshopBlastMenu(int id, Inventory inv, net.minecraft.network.FriendlyByteBuf extraData) {
        // 防御：extraData 可能为 null（旁观者经 vanilla 单参 openMenu 打开时无附加数据），
        // 兜底为 null BE → 空菜单，stillValid 返回 false 由服务端自动关闭
        this(id, inv, extraData != null ? inv.player.level().getBlockEntity(extraData.readBlockPos()) : null);
    }

    /**
     * 构造工坊锻炉菜单
     *
     * @param id          容器 ID
     * @param inv         玩家库存
     * @param blockEntity 工坊锻炉方块实体
     */
    public WorkshopBlastMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(PDMenus.WORKSHOP_BLAST.get(), id);
        // 防御：BE 缺失或类型不符时构造为空菜单，stillValid 返回 false 由服务端自动关闭
        this.blockEntity = blockEntity instanceof WorkshopBlastBlockEntity wbe ? wbe : null;
        this.level = inv.player.level();

        if (this.blockEntity != null) {
            IItemHandler handler = this.blockEntity.getItemHandler();

            // 槽位 0：原胚输入（原版坐标 79, 23）
            this.addSlot(new SlotItemHandler(handler, 0, 79, 23));
            // 槽位 1：镶嵌材料（原版坐标 25, 50）
            this.addSlot(new SlotItemHandler(handler, 1, 25, 50));
            // 槽位 2：岩浆桶输入（原版坐标 133, 32；注入由 BE 周期轮询处理）
            this.addSlot(new SlotItemHandler(handler, 2, 133, 32));
            // 槽位 3：空桶回收，仅可取出（原版坐标 133, 68）
            this.addSlot(new SlotItemHandler(handler, 3, 133, 68) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
            // 槽位 4：产物输出，仅可取出（原版坐标 79, 77）
            this.addSlot(new SlotItemHandler(handler, 4, 79, 77) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
        }

        // 玩家背包：3×9 网格，起始 (8, 114)（原版偏移 0+8 / 30+84）
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9,
                        8 + col * 18, 114 + row * 18));
            }
        }
        // 玩家快捷栏：1×9，起始 (8, 172)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, 172));
        }

        this.addDataSlot(this.fluidAmount);
    }

    @Override
    public void broadcastChanges() {
        // 每次广播前刷新岩浆量（服务端侧）
        if (!this.level.isClientSide()) {
            this.fluidAmount.set(this.blockEntity.getFluidAmount());
        }
        super.broadcastChanges();
    }

    /**
     * 获取当前储罐岩浆量（客户端为同步值）
     *
     * @return 岩浆量（mB）
     */
    public int getFluidAmount() {
        if (this.blockEntity == null) {
            return 0;
        }
        return this.level.isClientSide() ? this.fluidAmount.get() : this.blockEntity.getFluidAmount();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index < 5) {
                // 从锻炉槽位移到玩家背包（索引 5-40）
                if (!this.moveItemStackTo(stackInSlot, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从玩家背包移到锻炉可放入槽位（索引 0-2，3/4 由 mayPlace 拦截）
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
        // 防御：BE 缺失/类型不符时菜单立即失效，由服务端关闭
        if (this.blockEntity == null) {
            return false;
        }
        return AbstractContainerMenu.stillValid(
                ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, blockEntity.getBlockState().getBlock());
    }
}
