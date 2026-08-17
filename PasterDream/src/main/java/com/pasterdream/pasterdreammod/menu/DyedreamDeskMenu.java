package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.api.menu.SimpleContainerMenu;
import com.pasterdream.pasterdreammod.block.entity.DyedreamDeskBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

/**
 * 染梦书桌 GUI 容器菜单 (Dyedream Desk Menu)
 * 1 格物品槽（展示位）+ 玩家背包（27 格）+ 快捷栏（9 格）
 * 与原模组的 DyedreamDeskGuiMenu 功能一致
 *
 * 槽位分布：
 * - 索引 0：书桌展示槽（位置 79, 26）
 * - 索引 1-27：玩家背包（3×9，偏移 y=84）
 * - 索引 28-36：玩家快捷栏（1×9，偏移 y=142）
 */
public class DyedreamDeskMenu extends SimpleContainerMenu {
    private final DyedreamDeskBlockEntity blockEntity;

    /**
     * 构造染梦书桌菜单（从网络缓冲区接收）
     *
     * @param id        容器 ID
     * @param inv       玩家库存
     * @param extraData 包含 BlockPos 的网络缓冲区
     */
    public DyedreamDeskMenu(int id, Inventory inv, net.minecraft.network.FriendlyByteBuf extraData) {
        // Defense: spectator's vanilla single-arg openMenu sends an empty buffer (readableBytes == 0),
        // readBlockPos() would throw IndexOutOfBoundsException → connection lost.
        this(id, inv, extraData != null && extraData.readableBytes() >= 8
                ? inv.player.level().getBlockEntity(extraData.readBlockPos()) : null);
    }

    /**
     * 构造染梦书桌菜单
     *
     * @param id          容器 ID
     * @param inv         玩家库存
     * @param blockEntity 染梦书桌方块实体
     */
    public DyedreamDeskMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(PDMenus.DYEDREAM_DESK.get(), id, 1);
        // 防御：BE 缺失或类型不符时构造为空菜单，stillValid 返回 false 由服务端自动关闭
        this.blockEntity = blockEntity instanceof DyedreamDeskBlockEntity dbe ? dbe : null;
        bindBlockEntity(this.blockEntity);

        IItemHandler handler = this.blockEntity != null
                ? this.blockEntity.getItemHandler()
                : new net.neoforged.neoforge.items.ItemStackHandler(1);
        // Desk display slot: 1 slot at (79, 26)
        this.addSlot(new SlotItemHandler(handler, 0, 79, 26));
        addPlayerInventory(inv, 84);
    }

    /**
     * 获取方块实体
     *
     * @return 染梦书桌方块实体
     */
    public DyedreamDeskBlockEntity getBlockEntity() {
        return blockEntity;
    }

    /**
     * 校验玩家是否仍可操作该菜单
     *
     * @param player 玩家
     * @return BE 缺失/类型不符时菜单立即失效，由服务端关闭
     */
    @Override
    public boolean stillValid(Player player) {
        return this.blockEntity != null && super.stillValid(player);
    }
}
