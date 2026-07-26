package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * 强征传送塔方块实体 (Forced Tower Block Entity)
 * <p>
 * 承担 GeckoLib 动画渲染，并持久化传送链接数据
 * （原版经 BE PersistentData 存储，NBT 键保持一致以兼容旧存档）：
 * <ul>
 *   <li>{@code coord_x / coord_y / coord_z} —— 链接目标塔坐标；</li>
 *   <li>{@code switch} —— 链接是否已建立。</li>
 * </ul>
 * 原版 TileEntity 还带有 9 格箱子库存与 ChestMenu 菜单，但方块交互从不打开它，
 * 属 MCreator 生成的死代码，本版不移植（见移植说明）。
 */
public class ForcedTowerBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** 链接目标 X 坐标（原版 PersistentData "coord_x"，未链接时为 -1） */
    private double coordX = -1;
    /** 链接目标 Y 坐标 */
    private double coordY = -1;
    /** 链接目标 Z 坐标 */
    private double coordZ = -1;
    /** 链接是否已建立（原版 PersistentData "switch"） */
    private boolean linked;

    /**
     * 构造强征传送塔方块实体
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public ForcedTowerBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntities.FORCED_TOWER.get(), pos, state);
    }

    /**
     * 获取链接目标 X 坐标
     *
     * @return X 坐标（未链接为 -1）
     */
    public double getCoordX() {
        return coordX;
    }

    /**
     * 获取链接目标 Y 坐标
     *
     * @return Y 坐标（未链接为 -1）
     */
    public double getCoordY() {
        return coordY;
    }

    /**
     * 获取链接目标 Z 坐标
     *
     * @return Z 坐标（未链接为 -1）
     */
    public double getCoordZ() {
        return coordZ;
    }

    /**
     * 链接是否已建立
     *
     * @return 是否已链接
     */
    public boolean isLinked() {
        return linked;
    }

    /**
     * 设置链接目标坐标
     *
     * @param x 目标 X
     * @param y 目标 Y
     * @param z 目标 Z
     */
    public void setCoords(double x, double y, double z) {
        this.coordX = x;
        this.coordY = y;
        this.coordZ = z;
        setChanged();
        syncToClient();
    }

    /**
     * 设置链接标记
     *
     * @param linked 是否已链接
     */
    public void setLinked(boolean linked) {
        this.linked = linked;
        setChanged();
        syncToClient();
    }

    /** 同步方块实体数据到客户端（原版写 PersistentData 后 sendBlockUpdated 的语义） */
    private void syncToClient() {
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    // ==================== GeckoLib 动画 ====================

    /**
     * 动画谓词（还原原版双控制器语义）：
     * ANIMATION 方块状态为 0 时循环播放空闲动画 "0"，非 0 播放对应一次性动画
     *
     * @param state 动画状态
     * @return 播放状态
     */
    private PlayState predicate(AnimationState<ForcedTowerBlockEntity> state) {
        int anim = 0;
        if (getBlockState().getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty prop) {
            anim = getBlockState().getValue(prop);
        }
        if (anim == 0) {
            return state.setAndContinue(RawAnimation.begin().thenLoop("0"));
        }
        return state.setAndContinue(RawAnimation.begin().thenPlay(String.valueOf(anim)));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // ==================== 持久化（NBT 键与原版 PersistentData 一致） ====================

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putDouble("coord_x", coordX);
        tag.putDouble("coord_y", coordY);
        tag.putDouble("coord_z", coordZ);
        tag.putBoolean("switch", linked);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        coordX = tag.contains("coord_x") ? tag.getDouble("coord_x") : -1;
        coordY = tag.contains("coord_y") ? tag.getDouble("coord_y") : -1;
        coordZ = tag.contains("coord_z") ? tag.getDouble("coord_z") : -1;
        linked = tag.getBoolean("switch");
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
