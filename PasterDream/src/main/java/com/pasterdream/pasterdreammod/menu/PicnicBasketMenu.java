package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.api.menu.SimpleContainerMenu;
import com.pasterdream.pasterdreammod.block.entity.PicnicBasketBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDMenusFurniture;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * 野餐篮 GUI 容器菜单（picnic_basket）
 * <p>
 * 与原版 PicnicBasketGuiMenu 布局一致：
 * 15 格（5×3，x=43..115 步进 18，y=23/41/59）+
 * 玩家背包（y=114 起）+ 快捷栏（y=172）——玩家区整体下移 30 像素。
 */
public class PicnicBasketMenu extends SimpleContainerMenu {

    private final BlockEntity blockEntity;

    /**
     * 网络工厂构造（客户端）
     *
     * @param id        容器 ID
     * @param inv       玩家背包
     * @param extraData 附加数据（方块坐标）
     */
    public PicnicBasketMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    /**
     * 服务端构造
     *
     * @param id          容器 ID
     * @param inv         玩家背包
     * @param blockEntity 野餐篮方块实体
     */
    public PicnicBasketMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(PDMenusFurniture.PICNIC_BASKET.get(), id, 15);
        this.blockEntity = blockEntity;
        bindBlockEntity(blockEntity);

        IItemHandler handler = blockEntity instanceof PicnicBasketBlockEntity basket
                ? basket.getItemHandler() : new ItemStackHandler(15);

        // 15 格容器：5×3
        addContainerGrid(handler, 5, 3, 43, 23);
        // 玩家背包（下移 30）：快捷栏 y=172 = 114+58
        addPlayerInventory(inv, 114);
    }

    public BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
