package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.menu.MeltdreamChestMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

/**
 * 打开的融梦水晶箱方块实体 (Meltdream Chest Open Block Entity)
 * <p>
 * 由 MeltdreamChestBlock 在动画播放完毕后替换生成，
 * 包含 9 格库存，右键打开 GUI 菜单查看剩余物品。
 * 实现 {@link MenuProvider} 以支持 GUI 交互。
 */
public class MeltdreamChestOpenBlockEntity extends BlockEntity implements MenuProvider {

    /** 9 格库存处理器 */
    private final ItemStackHandler itemHandler = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    /**
     * 构造打开的融梦水晶箱方块实体
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public MeltdreamChestOpenBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntities.MELTDREAM_CHEST_OPEN.get(), pos, state);
    }

    /**
     * 获取库存处理器
     *
     * @return ItemStackHandler 实例（9 格）
     */
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    // ==================== 库存持久化 ====================

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) {
            itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        }
    }

    // ==================== 客户端同步 ====================

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // ==================== MenuProvider ====================

    /**
     * 获取菜单标题
     *
     * @return 菜单标题文本组件
     */
    @Override
    public Component getDisplayName() {
        return Component.translatable("container.pasterdream.meltdream_chest");
    }

    /**
     * 创建容器菜单
     *
     * @param id        容器 ID
     * @param inventory 玩家库存
     * @param player    玩家
     * @return 融梦水晶箱菜单实例
     */
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new MeltdreamChestMenu(id, inventory, this);
    }
}
