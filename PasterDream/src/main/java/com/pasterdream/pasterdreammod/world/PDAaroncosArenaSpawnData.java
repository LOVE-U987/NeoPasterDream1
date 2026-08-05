package com.pasterdream.pasterdreammod.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import org.jetbrains.annotations.NotNull;

/**
 * 亚伦柯斯竞技场遗迹生成记录。
 * <p>
 * 主世界竞技场遗迹由结构集正常随机生成且每世界仅生成一座
 * （关门逻辑见 {@code AaroncosArenaPortalStructure}）；本类记录遗迹中心坐标，
 * 供服务器重启后恢复遗迹感染（{@link ArenaRuinInfection}）使用。
 */
public class PDAaroncosArenaSpawnData extends SavedData {

    private static final String DATA_ID = "pasterdream_aaroncos_arena_spawn";
    private static final String TAG_PLACED = "placed";
    private static final String TAG_CENTER_X = "center_x";
    private static final String TAG_CENTER_Y = "center_y";
    private static final String TAG_CENTER_Z = "center_z";

    private static final SavedData.Factory<PDAaroncosArenaSpawnData> FACTORY =
            new SavedData.Factory<>(PDAaroncosArenaSpawnData::new, PDAaroncosArenaSpawnData::new, null);

    private boolean placed = false;
    private BlockPos center = null;

    private PDAaroncosArenaSpawnData() {
    }

    private PDAaroncosArenaSpawnData(CompoundTag tag, HolderLookup.Provider provider) {
        this.placed = tag.getBoolean(TAG_PLACED);
        if (tag.contains(TAG_CENTER_X)) {
            this.center = new BlockPos(
                    tag.getInt(TAG_CENTER_X),
                    tag.getInt(TAG_CENTER_Y),
                    tag.getInt(TAG_CENTER_Z));
        }
    }

    /**
     * 获取主世界的竞技场生成记录。
     *
     * @param level 服务端世界（通常取主世界）
     * @return 该维度的 PDAaroncosArenaSpawnData 实例
     */
    public static PDAaroncosArenaSpawnData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, DATA_ID);
    }

    /**
     * 是否已在当前世界放置过竞技场遗迹。
     *
     * @return true 若已放置
     */
    public boolean isPlaced() {
        return placed;
    }

    /**
     * 标记竞技场遗迹已放置，并持久化。
     */
    public void markPlaced() {
        if (!this.placed) {
            this.placed = true;
            setDirty();
        }
    }

    /**
     * 回滚已放置标记。
     * <p>
     * 结构候选点生成失败（如落在海洋）时调用，允许后续候选点重新尝试生成。
     */
    public void rollback() {
        if (this.placed) {
            this.placed = false;
            setDirty();
        }
    }

    /**
     * 获取遗迹中心坐标。
     *
     * @return 遗迹中心；未放置时返回 null
     */
    public BlockPos getCenter() {
        return center;
    }

    /**
     * 记录遗迹中心坐标并持久化。
     *
     * @param center 遗迹中心坐标
     */
    public void setCenter(BlockPos center) {
        this.center = center.immutable();
        setDirty();
    }

    @Override
    @NotNull
    public CompoundTag save(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        tag.putBoolean(TAG_PLACED, placed);
        if (center != null) {
            tag.putInt(TAG_CENTER_X, center.getX());
            tag.putInt(TAG_CENTER_Y, center.getY());
            tag.putInt(TAG_CENTER_Z, center.getZ());
        }
        return tag;
    }
}
