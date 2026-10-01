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
 * 主世界竞技场遗迹由结构集正常随机生成且每世界仅生成一座（关门逻辑见
 * {@code AaroncosArenaPortalStructure}，真实放置确认见
 * {@code PDAroncosArenaWorldgen#confirmArenaPlacement}）；本类记录：
 * <ul>
 *   <li>{@code placed}：遗迹已真实放置（由传送门方块实际落入世界时置位，
 *       第三方结构查询/预览不会误置位），用于抑制后续结构候选点，
 *       保证主世界竞技场（结构+群系）只生成一次；</li>
 *   <li>{@code center}：遗迹中心坐标，供感染与群系恢复使用；</li>
 *   <li>{@code biomePainted}：竞技场群系是否已完成刷写（防止重复刷写）；</li>
 *   <li>{@code defeated}：竞技场 BOSS 是否已被击败（击败后感染永久退化，
 *       服务器重启也不再恢复感染）。</li>
 * </ul>
 */
public class PDAaroncosArenaSpawnData extends SavedData {

    private static final String DATA_ID = "pasterdream_aaroncos_arena_spawn";
    private static final String TAG_PLACED = "placed";
    private static final String TAG_CENTER_X = "center_x";
    private static final String TAG_CENTER_Y = "center_y";
    private static final String TAG_CENTER_Z = "center_z";
    private static final String TAG_BIOME_PAINTED = "biome_painted";
    private static final String TAG_DEFEATED = "defeated";

    private static final SavedData.Factory<PDAaroncosArenaSpawnData> FACTORY =
            new SavedData.Factory<>(PDAaroncosArenaSpawnData::new, PDAaroncosArenaSpawnData::new, null);

    private boolean placed = false;
    private BlockPos center = null;
    private boolean biomePainted = false;
    private boolean defeated = false;

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
        this.biomePainted = tag.getBoolean(TAG_BIOME_PAINTED);
        this.defeated = tag.getBoolean(TAG_DEFEATED);
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
     * <p>
     * 仅应由真实放置确认流程（传送门方块实际落入世界后的主线程确认）调用，
     * 结构查询/预览路径不得置位。
     */
    public void markPlaced() {
        if (!this.placed) {
            this.placed = true;
            setDirty();
        }
    }

    /**
     * 竞技场群系是否已完成刷写。
     *
     * @return true 若群系已刷写完成
     */
    public boolean isBiomePainted() {
        return biomePainted;
    }

    /**
     * 标记竞技场群系已刷写完成，并持久化（防止重复刷写）。
     */
    public void markBiomePainted() {
        if (!this.biomePainted) {
            this.biomePainted = true;
            setDirty();
        }
    }

    /**
     * 竞技场 BOSS 是否已被击败（感染已永久退化）。
     *
     * @return true 若 BOSS 已被击败
     */
    public boolean isDefeated() {
        return defeated;
    }

    /**
     * 标记竞技场 BOSS 已被击败，并持久化。
     * <p>
     * 击败后感染永久停止，服务器重启也不再恢复。
     */
    public void markDefeated() {
        if (!this.defeated) {
            this.defeated = true;
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
        tag.putBoolean(TAG_BIOME_PAINTED, biomePainted);
        tag.putBoolean(TAG_DEFEATED, defeated);
        return tag;
    }
}
