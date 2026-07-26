package com.pasterdream.pasterdreammod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * W4 波次通用数据方块实体
 * <p>
 * 对应原版 MCreator 各方块实体中通过 {@code getPersistentData()} 存取的
 * 自由键值（number/time/switch/range/key/exit/cd/layerN/task1-3 等）。
 * 原版把数据放在 ForgeData 附加标签里；移植后收敛为显式的 {@code PDData}
 * 子标签，随 {@link #saveAdditional}/{@link #loadAdditional} 持久化，
 * 并通过 update packet 同步到客户端。
 * <p>
 * 一个类服务多个注册名（structure_block_0..23、shadow_bed、claypan_1、
 * guard_block、restrainmove_block、lost_sword_block 等），
 * 由构造时传入的 {@link BlockEntityType} 区分。
 */
public class W4DataBlockEntity extends BlockEntity {

    /** 自由键值数据（等价原版 persistent data 用法） */
    private CompoundTag data = new CompoundTag();

    /**
     * 构造通用数据方块实体
     *
     * @param type  方块实体类型
     * @param pos   方块位置
     * @param state 方块状态
     */
    public W4DataBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ==================== 键值访问 ====================

    /**
     * 读取 double 值（缺省 -1，与原版 getValue 匿名类一致）
     *
     * @param key 键名
     * @return 值；键不存在时为 -1 之外的 0？原版匿名类在 BE 缺失时返回 -1，
     *         键缺失时 CompoundTag.getDouble 返回 0——两处语义均已按原版调用点核对
     */
    public double getDoubleData(String key) {
        return data.getDouble(key);
    }

    /**
     * 写入 double 值并同步
     *
     * @param key   键名
     * @param value 值
     */
    public void putDoubleData(String key, double value) {
        data.putDouble(key, value);
        setChanged();
        syncToClient();
    }

    /**
     * 读取 boolean 值
     *
     * @param key 键名
     * @return 值（缺省 false）
     */
    public boolean getBooleanData(String key) {
        return data.getBoolean(key);
    }

    /**
     * 写入 boolean 值并同步
     *
     * @param key   键名
     * @param value 值
     */
    public void putBooleanData(String key, boolean value) {
        data.putBoolean(key, value);
        setChanged();
        syncToClient();
    }

    /** 发送方块更新（等价原版 sendBlockUpdated） */
    private void syncToClient() {
        if (level != null && !level.isClientSide()) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    // ==================== 静态便捷访问（跨方块读写） ====================

    /**
     * 读取指定位置 BE 的 double 数据（BE 不存在或类型不符时返回 -1，
     * 与原版 procedures 的匿名 getValue 类语义一致）
     *
     * @param world 世界
     * @param pos   位置
     * @param key   键名
     * @return 值
     */
    public static double getDoubleAt(LevelAccessor world, BlockPos pos, String key) {
        if (world.getBlockEntity(pos) instanceof W4DataBlockEntity be) {
            return be.getDoubleData(key);
        }
        return -1;
    }

    /**
     * 读取指定位置 BE 的 boolean 数据（BE 不存在时返回 false）
     *
     * @param world 世界
     * @param pos   位置
     * @param key   键名
     * @return 值
     */
    public static boolean getBooleanAt(LevelAccessor world, BlockPos pos, String key) {
        if (world.getBlockEntity(pos) instanceof W4DataBlockEntity be) {
            return be.getBooleanData(key);
        }
        return false;
    }

    /**
     * 写入指定位置 BE 的 double 数据（BE 不存在时忽略）
     *
     * @param world 世界
     * @param pos   位置
     * @param key   键名
     * @param value 值
     */
    public static void putDoubleAt(LevelAccessor world, BlockPos pos, String key, double value) {
        if (world.getBlockEntity(pos) instanceof W4DataBlockEntity be) {
            be.putDoubleData(key, value);
        }
    }

    /**
     * 写入指定位置 BE 的 boolean 数据（BE 不存在时忽略）
     *
     * @param world 世界
     * @param pos   位置
     * @param key   键名
     * @param value 值
     */
    public static void putBooleanAt(LevelAccessor world, BlockPos pos, String key, boolean value) {
        if (world.getBlockEntity(pos) instanceof W4DataBlockEntity be) {
            be.putBooleanData(key, value);
        }
    }

    // ==================== NBT 持久化 ====================

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("PDData", data.copy());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("PDData")) {
            data = tag.getCompound("PDData").copy();
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
}
