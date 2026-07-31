package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.block.entity.DreamCauldronBlockEntity;
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
 * 梦境炼药锅 GUI 容器菜单 (Dream Cauldron Menu)
 * 槽位布局完全还原原版 PasterDream（7 格炼药槽 + 玩家背包），
 * GUI 背景纹理为原版 196×196 素材，槽位坐标与纹理绘制位置一一对应。
 *
 * 槽位分布（相对于 GUI 纹理左上角）：
 * - 索引 0：引导药剂槽（17, 50）——合成必须放入引导药剂
 * - 索引 1-3：材料槽（61, 19）(89, 19)(117, 19)
 * - 索引 4：融梦液体桶输入槽（170, 23）——放入后自动注入 1000mB 并退还空桶
 * - 索引 5：空桶回收槽（170, 77）——仅可取出
 * - 索引 6：成品槽（89, 50)——仅可取出，合成完成后由炼药锅自动弹出
 * - 索引 7-33：玩家背包（3×9，起始 (18, 114)）
 * - 索引 34-42：玩家快捷栏（1×9，起始 (18, 172)）
 *
 * 液体量通过 {@link DataSlot} 自动同步到客户端供 GUI 显示。
 */
public class DreamCauldronMenu extends AbstractContainerMenu {

    /** 合成按钮的菜单按钮 ID（经由 vanilla clickMenuButton 通道触发） */
    public static final int BUTTON_CRAFT = 0;

    private final DreamCauldronBlockEntity blockEntity;
    private final Level level;

    /** 客户端同步的液体量（mB），服务端每 broadcast 周期自动推送 */
    private final DataSlot fluidAmount = DataSlot.standalone();

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
        // 防御：BE 缺失或类型不符时构造为空菜单，stillValid 返回 false 由服务端自动关闭
        this.blockEntity = blockEntity instanceof DreamCauldronBlockEntity dce ? dce : null;
        this.level = inv.player.level();

        if (this.blockEntity != null) {
            IItemHandler handler = this.blockEntity.getItemHandler();

            // 槽位 0：引导药剂（原版坐标 17, 50）
            this.addSlot(new SlotItemHandler(handler, 0, 17, 50));
            // 槽位 1-3：炼药材料（原版坐标 61/89/117, 19）
            this.addSlot(new SlotItemHandler(handler, 1, 61, 19));
            this.addSlot(new SlotItemHandler(handler, 2, 89, 19));
            this.addSlot(new SlotItemHandler(handler, 3, 117, 19));
            // 槽位 4：液体桶输入（原版坐标 170, 23）
            this.addSlot(new SlotItemHandler(handler, 4, 170, 23));
            // 槽位 5：空桶回收，仅可取出（原版坐标 170, 77）
            this.addSlot(new SlotItemHandler(handler, 5, 170, 77) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
            // 槽位 6：成品输出，仅可取出（原版坐标 89, 50）
            this.addSlot(new SlotItemHandler(handler, 6, 89, 50) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
        }

        // 玩家背包：3×9 网格，起始 (18, 114)（原版偏移）
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9,
                        18 + col * 18, 114 + row * 18));
            }
        }

        // 玩家快捷栏：1×9，起始 (18, 172)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, 18 + col * 18, 172));
        }

        // 液体量同步槽：服务端读方块实体储罐，客户端读同步值
        this.addDataSlot(this.fluidAmount);
    }

    @Override
    public void broadcastChanges() {
        // 每次广播前刷新液体量（服务端侧）；BE 失效时跳过
        if (!this.level.isClientSide() && this.blockEntity != null) {
            this.fluidAmount.set(this.blockEntity.getFluidAmount());
        }
        super.broadcastChanges();
    }

    /**
     * 获取当前储罐液体量（客户端为同步值）
     *
     * @return 液体量（mB）
     */
    public int getFluidAmount() {
        if (this.blockEntity == null) {
            return 0;
        }
        return this.level.isClientSide() ? this.fluidAmount.get() : this.blockEntity.getFluidAmount();
    }

    /**
     * 获取方块实体
     *
     * @return 梦境炼药锅方块实体
     */
    public DreamCauldronBlockEntity getBlockEntity() {
        return blockEntity;
    }

    /**
     * 处理 GUI 按钮点击（服务端调用）
     * 客户端通过 {@code gameMode.handleInventoryButtonClick} 发送 vanilla 按钮包到达此处
     *
     * @param player 点击按钮的玩家
     * @param id     按钮 ID（{@link #BUTTON_CRAFT}）
     * @return 是否处理了该按钮
     */
    @Override
    public boolean clickMenuButton(Player player, int id) {
        // 防御：BE 已失效（方块被拆除等）或距离过远时拒绝按钮操作
        if (this.blockEntity == null || !this.stillValid(player)) {
            return false;
        }
        if (id == BUTTON_CRAFT) {
            this.blockEntity.tryStartCraft(player);
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

            if (index < 7) {
                // 从炼药锅槽位移到玩家背包（索引 7-42）
                if (!this.moveItemStackTo(stackInSlot, 7, 43, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从玩家背包移到炼药锅可放入槽位（索引 0-4，5/6 由 mayPlace 拦截）
                if (!this.moveItemStackTo(stackInSlot, 0, 5, false)) {
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
