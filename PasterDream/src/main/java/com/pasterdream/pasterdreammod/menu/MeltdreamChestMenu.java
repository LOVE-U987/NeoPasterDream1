package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.api.menu.SimpleContainerMenu;
import com.pasterdream.pasterdreammod.registry.PDMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * 融梦水晶箱 GUI 容器菜单 (Meltdream Chest Menu)
 *
 * 9 格库存（3×3 网格）+ 玩家背包栏
 * 右键打开的箱子时打开此菜单
 */
public class MeltdreamChestMenu extends SimpleContainerMenu {

    private final BlockEntity blockEntity;

    /**
     * 构造融梦水晶箱菜单（网络包）
     *
     * @param id        容器 ID
     * @param inv       玩家库存
     * @param extraData 包含 BlockPos 的网络缓冲区
     */
    public MeltdreamChestMenu(int id, Inventory inv, net.minecraft.network.FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    /**
     * 构造融梦水晶箱菜单
     *
     * @param id          容器 ID
     * @param inv         玩家库存
     * @param blockEntity 方块实体
     */
    public MeltdreamChestMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(PDMenus.MELTDREAM_CHEST.get(), id, 9);
        this.blockEntity = blockEntity;
        bindBlockEntity(blockEntity);

        IItemHandler handler;
        if (blockEntity instanceof com.pasterdream.pasterdreammod.block.entity.MeltdreamChestBlockEntity chest) {
            handler = chest.getItemHandler();
        } else if (blockEntity instanceof com.pasterdream.pasterdreammod.block.entity.MeltdreamChestOpenBlockEntity openChest) {
            handler = openChest.getItemHandler();
        } else {
            handler = new ItemStackHandler(9);
        }

        // 融梦水晶箱库存槽位: 3×3 网格
        addContainerGrid(handler, 3, 3, 62, 17);
        // 玩家背包 (3×9 网格, y=100-158)
        addPlayerInventory(inv, 100);
    }

    public BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
