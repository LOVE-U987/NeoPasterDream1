package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.menu.WindmoorCrateMenu;
import com.pasterdream.pasterdreammod.registry.PDBlockEntitiesFurniture;
import com.pasterdream.pasterdreammod.api.util.StructureInventoryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

/**
 * 风泊木箱方块实体（windmoor_crate）
 * <p>
 * 15 格库存（5×3），实现 MenuProvider 供右键打开 GUI；
 * 另通过 {@link W4DataBlockEntity} 的数据键 new_loots 支持调试模式下的
 * 战利品刷新粒子提示（原 WindmoorCratePr0/Pr1）。
 * 结构放置兼容原版 {@code Items} / {@code LootTable}。
 */
public class WindmoorCrateBlockEntity extends W4DataBlockEntity implements MenuProvider {

    /** 15 格库存 */
    private final ItemStackHandler itemHandler = new ItemStackHandler(15) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    @Nullable
    private ResourceKey<LootTable> lootTable;
    private long lootTableSeed;

    /**
     * 构造风泊木箱方块实体
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public WindmoorCrateBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntitiesFurniture.WINDMOOR_CRATE.get(), pos, state);
    }

    /**
     * 获取库存处理器
     *
     * @return 15 格 ItemStackHandler
     */
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    /** 打开 GUI 前解包结构 LootTable（若有）。 */
    public void unpackLootIfNeeded(@Nullable Player player) {
        if (lootTable == null || level == null || level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        StructureInventoryHelper.unpackLootTable(
                itemHandler, lootTable, lootTableSeed, serverLevel, worldPosition, player,
                () -> {
                    lootTable = null;
                    lootTableSeed = 0L;
                    setChanged();
                });
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (lootTable != null) {
            StructureInventoryHelper.writeLootTable(tag, lootTable, lootTableSeed);
        } else {
            tag.put("inventory", itemHandler.serializeNBT(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        StructureInventoryHelper.loadItemHandler(itemHandler, tag, registries);
        this.lootTable = StructureInventoryHelper.readLootTable(tag);
        this.lootTableSeed = StructureInventoryHelper.readLootTableSeed(tag);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        unpackLootIfNeeded(player);
        return new WindmoorCrateMenu(id, inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.pasterdream.windmoor_crate");
    }
}
