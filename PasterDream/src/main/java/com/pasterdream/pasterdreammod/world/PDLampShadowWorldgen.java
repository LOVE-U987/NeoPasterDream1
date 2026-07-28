package com.pasterdream.pasterdreammod.world;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.blocks.PDBlocksFurniture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.neoforge.event.level.LevelEvent;

/**
 * 灯影世界出生结构：对齐原版 {@code GenerateWorldPr0} 的 lamp_shadow 分支。
 * <p>
 * heightmap(-9,-9) ≤100 → 放在 (-11,100,-9)；否则 (-11,150,-9)。
 * 模板 {@code pasterdream:shadow_world_spawn}（含返程暮影之笼）。
 * 每维度仅自动放置一次（SavedData），避免 Load 重复堆叠。
 */
public final class PDLampShadowWorldgen {

    private static final ResourceLocation SPAWN_TEMPLATE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "shadow_world_spawn");

    private PDLampShadowWorldgen() {
    }

    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (level.dimension() != PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY) {
            return;
        }
        tryPlaceSpawn(level);
    }

    /**
     * 若尚未放置则 place 模板。可供 VERIFY 直接调用。
     *
     * @return true 若本次执行了 placeInWorld
     */
    public static boolean tryPlaceSpawn(ServerLevel level) {
        SpawnData data = SpawnData.get(level);
        if (data.placed) {
            return false;
        }
        // 已有笼则只打标，避免覆盖玩家改动
        if (hasNearbyLantern(level, new BlockPos(0, 100, 0), 48)
                || hasNearbyLantern(level, new BlockPos(0, 150, 0), 48)) {
            data.placed = true;
            data.setDirty();
            return false;
        }

        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, -9, -9);
        BlockPos origin = surfaceY <= 100
                ? new BlockPos(-11, 100, -9)
                : new BlockPos(-11, 150, -9);

        StructureTemplate template = level.getStructureManager().getOrCreate(SPAWN_TEMPLATE);
        if (template == null || template.getSize().getX() == 0) {
            PasterDreamMod.LOGGER.warn("[LampShadow] shadow_world_spawn 模板不可用，跳过放置");
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
        PasterDreamMod.LOGGER.info("[LampShadow] placed shadow_world_spawn at {}", origin.toShortString());
        return true;
    }

    private static boolean hasNearbyLantern(ServerLevel level, BlockPos center, int radius) {
        int r = Math.max(8, radius);
        BlockPos min = center.offset(-r, -32, -r);
        BlockPos max = center.offset(r, 48, r);
        for (BlockPos p : BlockPos.betweenClosed(min, max)) {
            if (level.getBlockState(p).is(PDBlocksFurniture.TWILIGHT_LANTERN.get())) {
                return true;
            }
        }
        return false;
    }

    public static final class SpawnData extends SavedData {
        private static final String ID = "pasterdream_lamp_shadow_spawn";
        private static final Factory<SpawnData> FACTORY =
                new Factory<>(SpawnData::new, SpawnData::load, null);

        boolean placed;

        private SpawnData() {
        }

        private static SpawnData load(CompoundTag tag, HolderLookup.Provider provider) {
            SpawnData d = new SpawnData();
            d.placed = tag.getBoolean("placed");
            return d;
        }

        static SpawnData get(ServerLevel level) {
            return level.getDataStorage().computeIfAbsent(FACTORY, ID);
        }

        @Override
        public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
            tag.putBoolean("placed", placed);
            return tag;
        }
    }
}
