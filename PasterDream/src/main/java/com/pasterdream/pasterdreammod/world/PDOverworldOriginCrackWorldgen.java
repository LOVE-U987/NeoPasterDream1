package com.pasterdream.pasterdreammod.world;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
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
 * 染梦裂隙出生点放置：对齐原版 {@code GenerateWorldPr0Procedure} 的裂隙分支。
 * <p>
 * 自然随机生成已恢复由结构（structure_set）承担；本类仅负责原版出生点裂隙：
 * <ul>
 *   <li>主世界：{@code THE_ORIGIN_OF_THE_WORLD_INITIALLY_GENERATED_DYEDREAM_CRACK}
 *       开启时在原点附近放置（0,0 额外裂隙）；{@code (-9,110,-10)} 可望见天空放该处，否则 {@code (-9,160,-10)}。</li>
 *   <li>染梦维度：{@code DYEDREAM_ORIGIN_SPAWNPOINT} 开启时在出生点放置（回主世界入口）。</li>
 * </ul>
 * 每个维度仅自动放置一次（SavedData），避免 Level.Load 重复堆叠。
 */
public final class PDOverworldOriginCrackWorldgen {

    private static final ResourceLocation CRACK_TEMPLATE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dyedreamcrack0");

    private PDOverworldOriginCrackWorldgen() {
    }

    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (level.dimension().equals(Level.OVERWORLD)) {
            tryPlaceOverworldCrack(level);
        } else if (level.dimension().equals(PDDimensions.DYEDREAM_WORLD_LEVEL_KEY)) {
            tryPlaceDyedreamCrack(level);
        }
    }

    /**
     * 主世界 0,0 原点裂隙（额外，默认关闭）：配置开启且未放置时放置模板。可供 VERIFY 直接调用。
     *
     * @param level 主世界
     * @return true 若本次执行了 placeInWorld
     */
    public static boolean tryPlaceOverworldCrack(ServerLevel level) {
        if (!Boolean.TRUE.equals(PDCommonConfig.THE_ORIGIN_OF_THE_WORLD_INITIALLY_GENERATED_DYEDREAM_CRACK.get())) {
            return false;
        }
        OriginData data = OriginData.get(level);
        if (data.overworldPlaced) {
            return false;
        }
        BlockPos origin = level.canSeeSkyFromBelowWater(new BlockPos(-9, 110, -10))
                ? new BlockPos(-9, 110, -10)
                : new BlockPos(-9, 160, -10);
        if (placeTemplate(level, origin)) {
            data.overworldPlaced = true;
            data.setDirty();
            PDDebugLogger.mainInfo("[OriginCrack] placed dyedreamcrack0 (overworld) at {}", origin.toShortString());
            return true;
        }
        return false;
    }

    /**
     * 染梦维度裂隙（回主世界入口）：配置开启且未放置时放置模板。可供 VERIFY 直接调用。
     *
     * @param level 染梦维度
     * @return true 若本次执行了 placeInWorld
     */
    public static boolean tryPlaceDyedreamCrack(ServerLevel level) {
        if (!Boolean.TRUE.equals(PDCommonConfig.DYEDREAM_ORIGIN_SPAWNPOINT.get())) {
            return false;
        }
        OriginData data = OriginData.get(level);
        if (data.dyedreamPlaced) {
            return false;
        }
        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, -9, -9);
        BlockPos origin = surfaceY <= 100
                ? new BlockPos(-9, 110, -10)
                : new BlockPos(-9, 160, -10);
        if (placeTemplate(level, origin)) {
            data.dyedreamPlaced = true;
            data.setDirty();
            PDDebugLogger.mainInfo("[OriginCrack] placed dyedreamcrack0 (dyedream) at {}", origin.toShortString());
            return true;
        }
        return false;
    }

    /**
     * 放置裂隙模板。
     *
     * @param level  目标维度
     * @param origin 放置原点
     * @return true 若模板可用并完成 placeInWorld
     */
    private static boolean placeTemplate(ServerLevel level, BlockPos origin) {
        StructureTemplate template = level.getStructureManager().getOrCreate(CRACK_TEMPLATE);
        if (template == null || template.getSize().getX() == 0) {
            PasterDreamMod.LOGGER.warn("[OriginCrack] dyedreamcrack0 模板不可用，跳过放置");
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
        return true;
    }

    public static final class OriginData extends SavedData {
        private static final String ID = "pasterdream_worldgen_crack";
        private static final Factory<OriginData> FACTORY =
                new Factory<>(OriginData::new, OriginData::load, null);

        boolean overworldPlaced;
        boolean dyedreamPlaced;

        private OriginData() {
        }

        private static OriginData load(CompoundTag tag, HolderLookup.Provider provider) {
            OriginData d = new OriginData();
            d.overworldPlaced = tag.getBoolean("overworldPlaced");
            d.dyedreamPlaced = tag.getBoolean("dyedreamPlaced");
            return d;
        }

        static OriginData get(ServerLevel level) {
            return level.getDataStorage().computeIfAbsent(FACTORY, ID);
        }

        @Override
        public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
            tag.putBoolean("overworldPlaced", overworldPlaced);
            tag.putBoolean("dyedreamPlaced", dyedreamPlaced);
            return tag;
        }
    }
}
