package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.api.menu.SimpleContainerMenu;
import com.pasterdream.pasterdreammod.block.entity.ShadowChestBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * 影之箱 GUI 容器菜单 (Shadow Chest Menu)
 * 15 格库存（5×3 网格）+ 玩家背包栏
 * 与原模组的 ShadowChestGuiMenu 功能一致
 */
public class ShadowChestMenu extends SimpleContainerMenu {
    private final ShadowChestBlockEntity blockEntity;

    /**
     * 构造影之箱菜单
     *
     * @param id        容器 ID
     * @param inv       玩家库存
     * @param extraData 包含 BlockPos 的网络缓冲区
     */
    public ShadowChestMenu(int id, Inventory inv, net.minecraft.network.FriendlyByteBuf extraData) {
        // 防御：extraData 可能为 null（旁观者经 vanilla 单参 openMenu 打开时无附加数据），
        // 兜底为 null BE → 空菜单，stillValid 返回 false 由服务端自动关闭
        this(id, inv, extraData != null ? inv.player.level().getBlockEntity(extraData.readBlockPos()) : null);
    }

    /**
     * 构造影之箱菜单
     *
     * @param id          容器 ID
     * @param inv         玩家库存
     * @param blockEntity 影之箱方块实体
     */
    public ShadowChestMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(PDMenus.SHADOW_CHEST.get(), id, 15);
        // 防御：BE 缺失或类型不符时构造为空菜单，stillValid 返回 false 由服务端自动关闭
        this.blockEntity = blockEntity instanceof ShadowChestBlockEntity sce ? sce : null;
        bindBlockEntity(this.blockEntity);

        if (this.blockEntity != null) {
            IItemHandler handler = this.blockEntity.getItemHandler();

            // 影之箱库存槽位: 5×3 网格 (索引 0-14)
            addContainerGrid(handler, 5, 3, 43, 15);
        }
        // 玩家背包 (索引 15-50)，快捷栏 y=142
        addPlayerInventory(inv, 84);
    }

    /**
     * 获取方块实体
     *
     * @return 影之箱方块实体
     */
    public ShadowChestBlockEntity getBlockEntity() {
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
