package com.pasterdream.pasterdreammod.api.blockentity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 自由 Compound 键值方块实体骨架。
 * <p>
 * 对应 MCreator {@code getPersistentData()} 用法：number/time/switch 等自由键
 * 写入子标签，随 save/load 持久化，并通过 update packet 同步客户端。
 * 子类可重写 {@link #persistentDataKey()} 以保留既有存档键名。
 */
public class FreeDataBlockEntity extends BlockEntity {

    private CompoundTag data = new CompoundTag();

    public FreeDataBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * NBT 子标签名。默认 {@code BlockData}；主模 W4 覆写为 {@code PDData} 以兼容存档。
     */
    protected String persistentDataKey() {
        return "BlockData";
    }

    public double getDoubleData(String key) {
        return data.getDouble(key);
    }

    public void putDoubleData(String key, double value) {
        data.putDouble(key, value);
        setChanged();
        syncToClient();
    }

    public boolean getBooleanData(String key) {
        return data.getBoolean(key);
    }

    public void putBooleanData(String key, boolean value) {
        data.putBoolean(key, value);
        setChanged();
        syncToClient();
    }

    public CompoundTag getDataCopy() {
        return data.copy();
    }

    /**
     * 清空所有自定义持久化数据，使方块实体回到初始状态。
     */
    public void clearData() {
        this.data = new CompoundTag();
        setChanged();
        syncToClient();
    }

    protected void syncToClient() {
        if (level != null && !level.isClientSide()) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    /** BE 不存在或类型不符时返回 -1（对齐 MCreator 匿名 getValue 缺省） */
    public static double getDoubleAt(LevelAccessor world, BlockPos pos, String key) {
        if (world.getBlockEntity(pos) instanceof FreeDataBlockEntity be) {
            return be.getDoubleData(key);
        }
        return -1;
    }

    public static boolean getBooleanAt(LevelAccessor world, BlockPos pos, String key) {
        if (world.getBlockEntity(pos) instanceof FreeDataBlockEntity be) {
            return be.getBooleanData(key);
        }
        return false;
    }

    public static void putDoubleAt(LevelAccessor world, BlockPos pos, String key, double value) {
        if (world.getBlockEntity(pos) instanceof FreeDataBlockEntity be) {
            be.putDoubleData(key, value);
        }
    }

    public static void putBooleanAt(LevelAccessor world, BlockPos pos, String key, boolean value) {
        if (world.getBlockEntity(pos) instanceof FreeDataBlockEntity be) {
            be.putBooleanData(key, value);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put(persistentDataKey(), data.copy());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        String key = persistentDataKey();
        if (tag.contains(key)) {
            data = tag.getCompound(key).copy();
        }
    }

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
