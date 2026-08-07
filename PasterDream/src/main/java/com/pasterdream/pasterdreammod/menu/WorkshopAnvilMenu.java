package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.block.entity.WorkshopAnvilBlockEntity;
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
 * 工坊铁砧 GUI 容器菜单 (Workshop Anvil Menu)
 * 槽位布局完全还原原版 WorkshopAnvilGuiMenu（176×216 背景）：
 * <ul>
 *   <li>索引 0：原胚输入槽（34, 78）</li>
 *   <li>索引 1：产物槽（124, 78)——仅可取出</li>
 *   <li>索引 2-28：玩家背包（3×9，起始 (8, 134)）</li>
 *   <li>索引 29-37：玩家快捷栏（1×9，起始 (8, 192)）</li>
 * </ul>
 * 按钮语义（vanilla {@code clickMenuButton} 通道，等价原版
 * WorkshopAnvilGuiButtonMessage 0-5）：0 = 开始锻造，1-5 = 数字按钮。
 * 目标数字与积分经 {@link DataSlot} 同步供指示灯/积分标签绘制
 * （等价原版 BE 持久数据 number/score 的 sendBlockUpdated 同步）。
 */
public class WorkshopAnvilMenu extends AbstractContainerMenu {

    /** "开始锻造"按钮的菜单按钮 ID（原版按钮 0） */
    public static final int BUTTON_START = 0;

    private final WorkshopAnvilBlockEntity blockEntity;
    private final Level level;

    /** 客户端同步的目标数字（1..5，未开始为 0） */
    private final DataSlot number = DataSlot.standalone();
    /** 客户端同步的当前积分（原版可为负） */
    private final DataSlot score = DataSlot.standalone();

    /**
     * 构造工坊铁砧菜单（从网络缓冲区接收）
     *
     * @param id        容器 ID
     * @param inv       玩家库存
     * @param extraData 包含 BlockPos 的网络缓冲区
     */
    public WorkshopAnvilMenu(int id, Inventory inv, net.minecraft.network.FriendlyByteBuf extraData) {
        // 防御：extraData 可能为 null（旁观者经 vanilla 单参 openMenu 打开时无附加数据），
        // 兜底为 null BE → 空菜单，stillValid 返回 false 由服务端自动关闭
        this(id, inv, extraData != null ? inv.player.level().getBlockEntity(extraData.readBlockPos()) : null);
    }

    /**
     * 构造工坊铁砧菜单
     *
     * @param id          容器 ID
     * @param inv         玩家库存
     * @param blockEntity 工坊铁砧方块实体
     */
    public WorkshopAnvilMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(PDMenus.WORKSHOP_ANVIL.get(), id);
        // 防御：BE 缺失或类型不符时构造为空菜单，stillValid 返回 false 由服务端自动关闭
        this.blockEntity = blockEntity instanceof WorkshopAnvilBlockEntity wbe ? wbe : null;
        this.level = inv.player.level();

        if (this.blockEntity != null) {
            IItemHandler handler = this.blockEntity.getItemHandler();

            // 槽位 0：原胚输入（原版坐标 34, 78）
            this.addSlot(new SlotItemHandler(handler, 0, 34, 78));
            // 槽位 1：产物输出，仅可取出（原版坐标 124, 78）
            this.addSlot(new SlotItemHandler(handler, 1, 124, 78) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
        }

        // 玩家背包：3×9 网格，起始 (8, 134)（原版偏移 0+8 / 50+84）
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9,
                        8 + col * 18, 134 + row * 18));
            }
        }
        // 玩家快捷栏：1×9，起始 (8, 192)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, 192));
        }

        this.addDataSlot(this.number);
        this.addDataSlot(this.score);
    }

    @Override
    public void broadcastChanges() {
        // 每次广播前刷新小游戏状态（服务端侧）；BE 失效时跳过
        if (!this.level.isClientSide() && this.blockEntity != null) {
            this.number.set(this.blockEntity.getNumber());
            this.score.set(this.blockEntity.getScore());
        }
        super.broadcastChanges();
    }

    /**
     * 获取当前目标数字（客户端为同步值）
     *
     * @return 目标数字 1..5，未开始为 0
     */
    public int getNumber() {
        if (this.blockEntity == null) {
            return 0;
        }
        return this.level.isClientSide() ? this.number.get() : this.blockEntity.getNumber();
    }

    /**
     * 获取当前积分（客户端为同步值）
     *
     * @return 积分（可为负）
     */
    public int getScore() {
        if (this.blockEntity == null) {
            return 0;
        }
        return this.level.isClientSide() ? this.score.get() : this.blockEntity.getScore();
    }

    /**
     * 处理 GUI 按钮点击（服务端调用）
     *
     * @param player 点击按钮的玩家
     * @param id     按钮 ID（0 开始锻造 / 1-5 数字按钮）
     * @return 是否处理了该按钮
     */
    @Override
    public boolean clickMenuButton(Player player, int id) {
        // 防御：BE 已失效（方块被拆除等）或距离过远时拒绝按钮操作
        if (this.blockEntity == null || !this.stillValid(player)) {
            return false;
        }
        if (id == BUTTON_START) {
            this.blockEntity.startGame();
            return true;
        }
        if (id >= 1 && id <= 5) {
            this.blockEntity.pressNumber(id);
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

            if (index < 2) {
                // 从铁砧槽位移到玩家背包（索引 2-37）
                if (!this.moveItemStackTo(stackInSlot, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从玩家背包移到原胚输入槽（索引 0，1 由 mayPlace 拦截）
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
        // 防御：BE 缺失/类型不符时菜单立即失效，由服务端关闭
        if (this.blockEntity == null) {
            return false;
        }
        return AbstractContainerMenu.stillValid(
                ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, blockEntity.getBlockState().getBlock());
    }
}
