package com.pasterdream.pasterdreammod.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 亚伦柯斯传送门感染记录存储。
 * <p>
 * 以维度 SavedData 形式保存在主世界，记录每个 {@code aaroncos_arena_portals}
 * 方块转化过的位置及其原始 {@link BlockState}，用于 BOSS 击败后完整回滚地形。
 * <p>
 * 数据结构：每个传送门位置（portal pos）对应一组转化记录（converted pos → original state）。
 */
public class PortalInfectionData extends SavedData {

    private static final String DATA_ID = "pasterdream_portal_infection";
    private static final String TAG_PORTALS = "portals";
    private static final String TAG_PORTAL_POS = "portal_pos";
    private static final String TAG_RECORDS = "records";
    private static final String TAG_CONVERTED_POS = "converted_pos";
    private static final String TAG_ORIGINAL_STATE = "original_state";

    private static final SavedData.Factory<PortalInfectionData> FACTORY =
            new SavedData.Factory<>(PortalInfectionData::new, PortalInfectionData::new, null);

    /**
     * 单个转化记录：被转化坐标与其原始方块状态。
     *
     * @param pos           被转化的世界坐标
     * @param originalState 转化前的方块状态
     */
    public record ConversionRecord(BlockPos pos, BlockState originalState) {
    }

    /** portal pos → 该传送门产生的转化记录列表 */
    private final Map<BlockPos, List<ConversionRecord>> recordsByPortal = new HashMap<>();

    private PortalInfectionData() {
    }

    private PortalInfectionData(CompoundTag tag, HolderLookup.Provider provider) {
        load(tag, provider);
    }

    /**
     * 获取主世界的感染数据存储。
     *
     * @param level 服务端世界（通常取主世界）
     * @return 该维度的 PortalInfectionData 实例
     */
    public static PortalInfectionData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, DATA_ID);
    }

    /**
     * 记录一次方块转化。
     * <p>
     * 若同一坐标已被同一传送门记录过，则更新为更早的原始状态（保留最初状态）。
     *
     * @param portalPos     产生转化的传送门坐标
     * @param convertedPos  被转化的方块坐标
     * @param originalState 转化前的原始方块状态
     */
    public void recordConversion(BlockPos portalPos, BlockPos convertedPos, BlockState originalState) {
        List<ConversionRecord> records = recordsByPortal.computeIfAbsent(portalPos.immutable(), k -> new ArrayList<>());
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).pos.equals(convertedPos)) {
                return;
            }
        }
        records.add(new ConversionRecord(convertedPos.immutable(), originalState));
        setDirty();
    }

    /**
     * 获取所有存在感染记录的传送门位置。
     *
     * @return 不可修改的传送门位置列表
     */
    public List<BlockPos> getPortalPositions() {
        return List.copyOf(recordsByPortal.keySet());
    }

    /**
     * 获取指定传送门的所有转化记录。
     *
     * @param portalPos 传送门坐标
     * @return 不可修改的转化记录列表
     */
    public List<ConversionRecord> getRecords(BlockPos portalPos) {
        return Collections.unmodifiableList(recordsByPortal.getOrDefault(portalPos.immutable(), List.of()));
    }

    /**
     * 移除指定传送门的全部记录（回滚完成后调用）。
     *
     * @param portalPos 传送门坐标
     */
    public void clearPortal(BlockPos portalPos) {
        if (recordsByPortal.remove(portalPos.immutable()) != null) {
            setDirty();
        }
    }

    /**
     * 移除指定传送门下的一条记录。
     *
     * @param portalPos    传送门坐标
     * @param convertedPos 已恢复的方块坐标
     */
    public void removeRecord(BlockPos portalPos, BlockPos convertedPos) {
        List<ConversionRecord> records = recordsByPortal.get(portalPos.immutable());
        if (records == null) {
            return;
        }
        boolean removed = records.removeIf(r -> r.pos.equals(convertedPos));
        if (removed) {
            if (records.isEmpty()) {
                recordsByPortal.remove(portalPos.immutable());
            }
            setDirty();
        }
    }

    /**
     * 判断是否还存在未恢复的感染记录。
     *
     * @return true 若没有任何记录
     */
    public boolean isEmpty() {
        return recordsByPortal.isEmpty();
    }

    private void load(CompoundTag tag, HolderLookup.Provider provider) {
        recordsByPortal.clear();
        HolderGetter<Block> blockLookup = provider.lookupOrThrow(Registries.BLOCK);

        ListTag portalsTag = tag.getList(TAG_PORTALS, CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < portalsTag.size(); i++) {
            CompoundTag portalTag = portalsTag.getCompound(i);
            BlockPos portalPos = BlockPos.of(portalTag.getLong(TAG_PORTAL_POS));
            ListTag recordsTag = portalTag.getList(TAG_RECORDS, CompoundTag.TAG_COMPOUND);

            List<ConversionRecord> records = new ArrayList<>(recordsTag.size());
            for (int j = 0; j < recordsTag.size(); j++) {
                CompoundTag recordTag = recordsTag.getCompound(j);
                BlockPos convertedPos = BlockPos.of(recordTag.getLong(TAG_CONVERTED_POS));
                BlockState originalState = NbtUtils.readBlockState(blockLookup, recordTag.getCompound(TAG_ORIGINAL_STATE));
                records.add(new ConversionRecord(convertedPos, originalState));
            }
            recordsByPortal.put(portalPos, records);
        }
    }

    @Override
    @NotNull
    public CompoundTag save(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        ListTag portalsTag = new ListTag();
        for (Map.Entry<BlockPos, List<ConversionRecord>> entry : recordsByPortal.entrySet()) {
            CompoundTag portalTag = new CompoundTag();
            portalTag.putLong(TAG_PORTAL_POS, entry.getKey().asLong());

            ListTag recordsTag = new ListTag();
            for (ConversionRecord record : entry.getValue()) {
                CompoundTag recordTag = new CompoundTag();
                recordTag.putLong(TAG_CONVERTED_POS, record.pos.asLong());
                recordTag.put(TAG_ORIGINAL_STATE, NbtUtils.writeBlockState(record.originalState));
                recordsTag.add(recordTag);
            }
            portalTag.put(TAG_RECORDS, recordsTag);
            portalsTag.add(portalTag);
        }
        tag.put(TAG_PORTALS, portalsTag);
        return tag;
    }
}
