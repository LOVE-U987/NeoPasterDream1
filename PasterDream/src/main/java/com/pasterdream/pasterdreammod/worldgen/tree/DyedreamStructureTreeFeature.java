package com.pasterdream.pasterdreammod.worldgen.tree;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pasterdream.pasterdreammod.api.worldgen.WorldGenUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Optional;

/**
 * 结构树 Feature —— 在染梦世界自然生成 Better Biomes 移植树（结构 NBT）
 * <p>
 * 数据包的树是静态结构 NBT（如 {@code bb_tallbirch}），无法用 TrunkPlacer/FoliagePlacer
 * 程序化复刻。本 Feature 在生成阶段从 {@link StructureTemplateManager} 加载对应结构并放置，
 * 保证树形与数据包完全一致（方块布局由结构数据决定）。
 * <p>
 * 放置逻辑：结构以 origin（placed_feature heightmap 定位的树根点）为水平中心展开；
 * 超大树（22×22 conifer）origin 对齐区块中心，保证落在 features 阶段可写 3×3 区块内。
 */
public class DyedreamStructureTreeFeature extends Feature<DyedreamStructureTreeFeature.Config> {

    /**
     * 结构树配置
     *
     * @param structurePath 结构 NBT 路径（不含命名空间和扩展名），如 {@code bb_tallbirch}
     */
    public record Config(String structurePath) implements FeatureConfiguration {
        public static final MapCodec<Config> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.STRING.fieldOf("structure").forGetter(Config::structurePath)
                ).apply(instance, Config::new));
    }

    /** 超大树判定阈值：横向尺寸超过该值则对齐区块中心（22×22 conifer 半径 11 > 16-8 需对齐） */
    private static final int GIANT_SIZE_THRESHOLD = 18;

    /**
     * 构造结构树特征
     *
     * @param codec 配置编解码器
     */
    public DyedreamStructureTreeFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        WorldGenLevel level = context.level();
        Config config = context.config();

        StructureTemplateManager manager = resolveTemplateManager(level);
        if (manager == null) {
            return false;
        }

        ResourceLocation structureId = ResourceLocation.parse("pasterdream:" + config.structurePath());
        Optional<StructureTemplate> templateOpt = manager.get(structureId);
        if (templateOpt.isEmpty()) {
            return false;
        }

        StructureTemplate template = templateOpt.get();
        Vec3i size = template.getSize();
        if (size.getX() <= 0 || size.getY() <= 0 || size.getZ() <= 0) {
            return false;
        }

        // 水平中心对齐：origin 为树根点，结构向 -X/-Z 偏移半尺寸，树冠以原点为中心
        BlockPos startPos = context.origin().offset(
                -(size.getX() - 1) / 2, 0, -(size.getZ() - 1) / 2
        );

        // 超大树（22×22）对齐区块中心，保证横跨 22 格的树落在 features 阶段可写 3×3 区块内
        if (size.getX() > GIANT_SIZE_THRESHOLD || size.getZ() > GIANT_SIZE_THRESHOLD) {
            startPos = WorldGenUtils.alignToChunkCenter(startPos);
        }

        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(Rotation.NONE)
                .setMirror(Mirror.NONE)
                .setIgnoreEntities(false);

        boolean placed = template.placeInWorld(
                level,
                startPos,
                startPos.offset(size.getX() - 1, size.getY() - 1, size.getZ() - 1),
                settings,
                context.random(),
                2
        );
        return placed;
    }

    /**
     * 从 worldgen 阶段或运行时 level 解析结构模板管理器
     *
     * @param level 世界生成层
     * @return 结构模板管理器，不可用返回 null
     */
    private static StructureTemplateManager resolveTemplateManager(WorldGenLevel level) {
        if (level instanceof WorldGenRegion region) {
            MinecraftServer server = region.getServer();
            return server != null ? server.getStructureManager() : null;
        }
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.getStructureManager();
        }
        return null;
    }
}
