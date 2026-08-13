package com.pasterdream.pasterdreammod.world;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.event.level.LevelEvent;

/**
 * 染梦裂隙出生结构 —— 对齐原版 {@code GenerateWorldPr0Procedure} 的裂隙相关分支。
 * <p>
 * 两个配置控制：
 * <ul>
 *   <li>{@code the origin of the world initially generated dyedream crack}（默认 false）：
 *       主世界初始生成时在 0,0 原点附近放置染梦裂隙 {@code dyedreamcrack0}。</li>
 *   <li>{@code dyedream origin spawnpoint}（默认 true）：
 *       染梦世界初始生成时在出生点附近放置裂隙/出生点岛屿 {@code dyedreamcrack0}。</li>
 * </ul>
 * 放置高度逻辑与原版一致：heightmap(-9,-9) ≤100 → (-9,110,-10)；否则 (-9,160,-10)。
 * 每个维度仅自动放置一次（SavedData），避免 Load 重复堆叠。
 */
public final class PDOriginCrackWorldgen {

    private static final ResourceLocation CRACK_TEMPLATE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dyedreamcrack0");

    private PDOriginCrackWorldgen() {
    }

    /**
     * 世界加载时尝试放置染梦裂隙出生结构。
     *
     * @param event 世界加载事件
     */
    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        // 主世界分支：初始生成裂隙（默认关闭）
        if (level.dimension() == Level.OVERWORLD) {
            if (Boolean.TRUE.equals(PDCommonConfig.THE_ORIGIN_OF_THE_WORLD_INITIALLY_GENERATED_DYEDREAM_CRACK.get())) {
                tryPlaceCrack(level, "overworld");
            }
            return;
        }
        // 染梦世界分支：出生点岛屿（默认开启）
        if (level.dimension() == PDDimensions.DYEDREAM_WORLD_LEVEL_KEY) {
            if (Boolean.TRUE.equals(PDCommonConfig.DYEDREAM_ORIGIN_SPAWNPOINT.get())) {
                tryPlaceCrack(level, "dyedream");
            }
        }
    }

    /**
     * 若该维度尚未放置裂隙结构，则按原版高度逻辑放置。
     * 可供 VERIFY 直接调用。
     *
     * @param level  服务端世界
     * @param dimKey SavedData 键（区分主世界/染梦世界）
     * @return true 若本次执行了 placeInWorld
     */
    public static boolean tryPlaceCrack(ServerLevel level, String dimKey) {
        CrackData data = CrackData.get(level);
        if (data.placed) {
            return false;
        }

        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, -9, -9);
        BlockPos origin = surfaceY <= 100
                ? new BlockPos(-9, 110, -10)
                : new BlockPos(-9, 160, -10);

        StructureTemplate template = level.getStructureManager().getOrCreate(CRACK_TEMPLATE);
        if (template == null || template.getSize().getX() == 0) {
            PasterDreamMod.LOGGER.warn("[OriginCrack] dyedreamcrack0 模板不可用，跳过放置 (dim={})", dimKey);
            return false;
        }
        template.placeInWorld(
                level,
                origin,
                origin,
                new StructurePlaceSettings()
                        .setRotation(Rotation.NONE)
                        .setMirror(Mirror.NONE)
                        .setIgnoreEntities(false),
                level.random,
                3);
        data.placed = true;
        data.setDirty();
        PasterDreamMod.LOGGER.info("[OriginCrack] 已在 {} 放置染梦裂隙出生结构 @ {}", dimKey, origin);
        return true;
    }

    /**
     * 出生结构放置标记（SavedData），跨世界加载持久化。
     */
    private static final class CrackData extends SavedData {

        private static final String DATA_NAME = "pd_origin_crack_placed";
        private static final String TAG_PLACED = "placed";

        private boolean placed;

        /**
         * 获取当前世界的放置标记数据。
         *
         * @param level 服务端世界
         * @return 放置标记
         */
        private static CrackData get(ServerLevel level) {
            return level.getDataStorage().computeIfAbsent(
                    new SavedData.Factory<>(CrackData::new, CrackData::load, null),
                    DATA_NAME);
        }

        private CrackData() {
        }

        /**
         * 从 NBT 反序列化放置标记。
         *
         * @param tag 存档 NBT
         * @param lookup Holder 查询上下文
         * @return 放置标记实例
         */
        private static CrackData load(CompoundTag tag, HolderLookup.Provider lookup) {
            CrackData data = new CrackData();
            data.placed = tag.getBoolean(TAG_PLACED);
            return data;
        }

        @Override
        public CompoundTag save(CompoundTag tag, HolderLookup.Provider lookup) {
            tag.putBoolean(TAG_PLACED, placed);
            return tag;
        }
    }
}
