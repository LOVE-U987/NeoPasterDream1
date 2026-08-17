package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.block.entity.WeaponWorkshopBlockEntity;
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
 * 精铸工坊 GUI 容器菜单 (Weapon Workshop Menu)
 * 槽位布局完全还原原版 WeaponWorkshopGuiMenu（186×186 背景）：
 * <ul>
 *   <li>索引 0-4：锻造材料槽（12/30/48/66/84, 18）</li>
 *   <li>索引 5：强化石槽（138, 18)</li>
 *   <li>索引 6：产物槽（138, 63)——仅可取出</li>
 *   <li>索引 7-33：玩家背包（3×9，起始 (13, 104)）</li>
 *   <li>索引 34-42：玩家快捷栏（1×9，起始 (13, 162)）</li>
 * </ul>
 * "锻造"按钮经 vanilla {@code clickMenuButton} 通道触发
 * {@link WeaponWorkshopBlockEntity#tryForge}（配方匹配 + 1 tick 后镶嵌结算，
 * 等价原版 WeaponWorkshopGuiButtonMessage 按钮 0 语义；
 * 原版产物槽变更触发的 Inlay0 已由 BE 内部调度覆盖，无需槽位消息）。
 */
public class WeaponWorkshopMenu extends AbstractContainerMenu {

    /** "锻造"按钮的菜单按钮 ID（原版按钮 0） */
    public static final int BUTTON_FORGE = 0;

    private final WeaponWorkshopBlockEntity blockEntity;
    private final Level level;

    /**
     * 构造精铸工坊菜单（从网络缓冲区接收）
     *
     * @param id        容器 ID
     * @param inv       玩家库存
     * @param extraData 包含 BlockPos 的网络缓冲区
     */
    public WeaponWorkshopMenu(int id, Inventory inv, net.minecraft.network.FriendlyByteBuf extraData) {
        // Defense: spectator's vanilla single-arg openMenu sends an empty buffer (readableBytes == 0),
        // readBlockPos() would throw IndexOutOfBoundsException → connection lost.
        this(id, inv, extraData != null && extraData.readableBytes() >= 8
                ? inv.player.level().getBlockEntity(extraData.readBlockPos()) : null);
    }

    /**
     * 构造精铸工坊菜单
     *
     * @param id          容器 ID
     * @param inv         玩家库存
     * @param blockEntity 精铸工坊方块实体
     */
    public WeaponWorkshopMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(PDMenus.WEAPON_WORKSHOP.get(), id);
        // 防御：BE 缺失或类型不符时构造为空菜单，stillValid 返回 false 由服务端自动关闭
        this.blockEntity = blockEntity instanceof WeaponWorkshopBlockEntity wwe ? wwe : null;
        this.level = inv.player.level();

        IItemHandler handler = this.blockEntity != null
                ? this.blockEntity.getItemHandler()
                : new net.neoforged.neoforge.items.ItemStackHandler(7);
        // Slots 0-4: forging materials (original coords 12/30/48/66/84, 18)
        for (int slot = 0; slot < 5; slot++) {
            this.addSlot(new SlotItemHandler(handler, slot, 12 + slot * 18, 18));
        }
        // Slot 5: enhancement stone (original coords 138, 18)
        this.addSlot(new SlotItemHandler(handler, 5, 138, 18));
        // Slot 6: output, take only (original coords 138, 63)
        this.addSlot(new SlotItemHandler(handler, 6, 138, 63) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        // 玩家背包：3×9 网格，起始 (13, 104)（原版偏移 5+8 / 20+84）
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9,
                        13 + col * 18, 104 + row * 18));
            }
        }
        // 玩家快捷栏：1×9，起始 (13, 162)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, 13 + col * 18, 162));
        }
    }

    /**
     * 处理 GUI 按钮点击（服务端调用）
     *
     * @param player 点击按钮的玩家
     * @param id     按钮 ID（{@link #BUTTON_FORGE}）
     * @return 是否处理了该按钮
     */
    @Override
    public boolean clickMenuButton(Player player, int id) {
        // 防御：BE 已失效（方块被拆除等）或距离过远时拒绝按钮操作
        if (this.blockEntity == null || !this.stillValid(player)) {
            return false;
        }
        if (id == BUTTON_FORGE) {
            this.blockEntity.tryForge(player);
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
                // 从工坊槽位移到玩家背包（索引 7-42）
                if (!this.moveItemStackTo(stackInSlot, 7, 43, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 从玩家背包移到工坊可放入槽位（索引 0-5，6 由 mayPlace 拦截）
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
