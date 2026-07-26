package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.block.entity.ShadowBlastFurnaceBlockEntity;
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
 * 暗影高炉 GUI 容器菜单 (Shadow Blast Furnace Menu)
 * 槽位布局完全还原原版 ShadowBlastFurnaceGuiMenu（176×216 背景）：
 * <ul>
 *   <li>索引 0：冶炼输入槽（25, 24）；</li>
 *   <li>索引 1：梦魇燃料槽（25, 69）；</li>
 *   <li>索引 2：主产物槽（61, 105）——仅可取出；</li>
 *   <li>索引 3：副产物槽（97, 105）——仅可取出；</li>
 *   <li>索引 4：暗影液体桶输入槽（133, 24）；</li>
 *   <li>索引 5：空桶回收槽（133, 69）——仅可取出；</li>
 *   <li>索引 6-32：玩家背包（3×9，起始 (8, 134)）；</li>
 *   <li>索引 33-41：玩家快捷栏（1×9，起始 (8, 192)）。</li>
 * </ul>
 * 无按钮交互；冶炼进度/所需时长/储罐液量经三个 {@link DataSlot} 同步，
 * 供屏幕绘制进度条与液体柱（原版屏幕直接读客户端 BE，这里用菜单同步更稳）。
 */
public class ShadowBlastFurnaceMenu extends AbstractContainerMenu {

    private final ShadowBlastFurnaceBlockEntity blockEntity;
    private final Level level;

    /** 客户端同步的冶炼进度（tick） */
    private final DataSlot blastingTime = DataSlot.standalone();
    /** 客户端同步的当前配方所需时长（tick，未冶炼为 0） */
    private final DataSlot needBlastingTime = DataSlot.standalone();
    /** 客户端同步的储罐液量（mB） */
    private final DataSlot fluidAmount = DataSlot.standalone();

    /**
     * 构造暗影高炉菜单（从网络缓冲区接收）
     *
     * @param id        容器 ID
     * @param inv       玩家库存
     * @param extraData 包含 BlockPos 的网络缓冲区
     */
    public ShadowBlastFurnaceMenu(int id, Inventory inv, net.minecraft.network.FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    /**
     * 构造暗影高炉菜单
     *
     * @param id          容器 ID
     * @param inv         玩家库存
     * @param blockEntity 暗影高炉方块实体
     */
    public ShadowBlastFurnaceMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(PDMenus.SHADOW_BLAST_FURNACE.get(), id);
        this.blockEntity = (ShadowBlastFurnaceBlockEntity) blockEntity;
        this.level = inv.player.level();

        IItemHandler handler = this.blockEntity.getItemHandler();

        // 槽位 0：冶炼输入（原版坐标 25, 24）
        this.addSlot(new SlotItemHandler(handler, ShadowBlastFurnaceBlockEntity.SLOT_INPUT, 25, 24));
        // 槽位 1：梦魇燃料（原版坐标 25, 69）
        this.addSlot(new SlotItemHandler(handler, ShadowBlastFurnaceBlockEntity.SLOT_FUEL, 25, 69));
        // 槽位 2：主产物，仅可取出（原版坐标 61, 105）
        this.addSlot(new SlotItemHandler(handler, ShadowBlastFurnaceBlockEntity.SLOT_RESULT, 61, 105) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        // 槽位 3：副产物，仅可取出（原版坐标 97, 105）
        this.addSlot(new SlotItemHandler(handler, ShadowBlastFurnaceBlockEntity.SLOT_BY_RESULT, 97, 105) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        // 槽位 4：暗影液体桶输入（原版坐标 133, 24）
        this.addSlot(new SlotItemHandler(handler, ShadowBlastFurnaceBlockEntity.SLOT_BUCKET_IN, 133, 24));
        // 槽位 5：空桶回收，仅可取出（原版坐标 133, 69）
        this.addSlot(new SlotItemHandler(handler, ShadowBlastFurnaceBlockEntity.SLOT_BUCKET_OUT, 133, 69) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        // 玩家背包：3×9 网格，起始 (8, 134)（原版偏移 0+8 / 50+84）
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 134 + row * 18));
            }
        }
        // 玩家快捷栏：1×9，起始 (8, 192)（原版偏移 50+142）
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, 192));
        }

        this.addDataSlot(this.blastingTime);
        this.addDataSlot(this.needBlastingTime);
        this.addDataSlot(this.fluidAmount);
    }

    @Override
    public void broadcastChanges() {
        // 每次广播前刷新进度与液量（服务端侧）
        if (!this.level.isClientSide()) {
            this.blastingTime.set(this.blockEntity.getBlastingTime());
            this.needBlastingTime.set(this.blockEntity.getNeedBlastingTime());
            this.fluidAmount.set(this.blockEntity.getFluidAmount());
        }
        super.broadcastChanges();
    }

    /**
     * 获取冶炼进度（客户端为同步值）
     *
     * @return 已冶炼 tick 数
     */
    public int getBlastingTime() {
        return this.level.isClientSide() ? this.blastingTime.get() : this.blockEntity.getBlastingTime();
    }

    /**
     * 获取当前配方所需冶炼时长（客户端为同步值）
     *
     * @return 总时长（tick），未在冶炼为 0
     */
    public int getNeedBlastingTime() {
        return this.level.isClientSide() ? this.needBlastingTime.get() : this.blockEntity.getNeedBlastingTime();
    }

    /**
     * 获取储罐液量（客户端为同步值）
     *
     * @return 液量（mB）
     */
    public int getFluidAmount() {
        return this.level.isClientSide() ? this.fluidAmount.get() : this.blockEntity.getFluidAmount();
    }

    /**
     * 获取储罐容量
     *
     * @return 容量（mB）
     */
    public int getFluidCapacity() {
        return ShadowBlastFurnaceBlockEntity.TANK_CAPACITY;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index < 6) {
                // 从高炉槽位移到玩家背包（索引 6-41）
                if (!this.moveItemStackTo(stackInSlot, 6, 42, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从玩家背包移到高炉可放入槽位（2/3/5 由 mayPlace 拦截）
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
        return AbstractContainerMenu.stillValid(
                ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, blockEntity.getBlockState().getBlock());
    }
}
