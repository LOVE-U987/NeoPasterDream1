package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.util.StructureInventoryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

/**
 * 构梦刷怪笼方块实体 (Dream Spawner 0 Block Entity)
 * <p>
 * 1 格库存存放刷怪蛋（决定生成的生物种类），并持久化刷怪状态
 * （NBT 键与原版 PersistentData 一致以兼容旧存档与后续波次的联动写入）：
 * <ul>
 *   <li>{@code first} —— 是否已完成首次生成；</li>
 *   <li>{@code player_range} —— 玩家侦测半径（放置时初始化为 16）；</li>
 *   <li>{@code number} —— 剩余批量生成次数（由地牢/事件波次写入）。</li>
 * </ul>
 */
public class DreamSpawner0BlockEntity extends BlockEntity {

    /** 刷怪蛋槽 */
    public static final int SLOT_EGG = 0;

    /** 1 格库存（刷怪蛋） */
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    /** 是否已完成首次生成（原版 PersistentData "first"） */
    private boolean firstSpawned;
    /** 玩家侦测半径（原版 PersistentData "player_range"） */
    private double playerRange = 16;
    /** 剩余批量生成次数（原版 PersistentData "number"） */
    private double spawnNumber;

    /**
     * 构造构梦刷怪笼方块实体
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public DreamSpawner0BlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntities.DREAM_SPAWNER_0.get(), pos, state);
    }

    /**
     * 获取库存处理器
     *
     * @return 1 格 ItemStackHandler
     */
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    /**
     * 获取刷怪蛋物品
     *
     * @return 槽 0 的物品栈
     */
    public ItemStack getEgg() {
        return itemHandler.getStackInSlot(SLOT_EGG);
    }

    /**
     * 写入刷怪蛋物品（原版 DreamSpawner0Pr2 的 setItem(0, item)）
     *
     * @param stack 刷怪蛋物品栈
     */
    public void setEgg(ItemStack stack) {
        itemHandler.setStackInSlot(SLOT_EGG, stack);
        syncToClient();
    }

    /**
     * 是否已完成首次生成
     *
     * @return 首次生成标记
     */
    public boolean isFirstSpawned() {
        return firstSpawned;
    }

    /**
     * 设置首次生成标记
     *
     * @param firstSpawned 标记值
     */
    public void setFirstSpawned(boolean firstSpawned) {
        this.firstSpawned = firstSpawned;
        setChanged();
        syncToClient();
    }

    /**
     * 获取玩家侦测半径
     *
     * @return 半径（格）
     */
    public double getPlayerRange() {
        return playerRange;
    }

    /**
     * 设置玩家侦测半径（原版 DreamSpawner0Pr1 初始化为 16）
     *
     * @param playerRange 半径（格）
     */
    public void setPlayerRange(double playerRange) {
        this.playerRange = playerRange;
        setChanged();
        syncToClient();
    }

    /**
     * 获取剩余批量生成次数
     *
     * @return 剩余次数
     */
    public double getSpawnNumber() {
        return spawnNumber;
    }

    /**
     * 设置剩余批量生成次数（供地牢/事件波次写入）
     *
     * @param spawnNumber 剩余次数
     */
    public void setSpawnNumber(double spawnNumber) {
        this.spawnNumber = spawnNumber;
        setChanged();
        syncToClient();
    }

    /** 同步方块实体数据到客户端 */
    private void syncToClient() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    // ==================== 持久化（NBT 键与原版 PersistentData 一致） ====================

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putBoolean("first", firstSpawned);
        tag.putDouble("player_range", playerRange);
        tag.putDouble("number", spawnNumber);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        StructureInventoryHelper.loadItemHandler(itemHandler, tag, registries);
        firstSpawned = tag.getBoolean("first");
        playerRange = tag.contains("player_range") ? tag.getDouble("player_range") : 16;
        spawnNumber = tag.getDouble("number");
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
}
