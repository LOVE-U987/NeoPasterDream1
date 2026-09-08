package com.pasterdream.pasterdreammod.worldgen.tree;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pasterdream.pasterdreammod.api.worldgen.WorldGenUtils;
import com.pasterdream.pasterdreammod.mixin.StructureTemplateAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

        // 底部支撑检测：最低占用层每列正下方必须为实体方块，防止斜坡放置产生悬空树木
        if (!hasGroundSupport(level, template, startPos, settings)) {
            return false;
        }

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
     * 检测结构底部支撑
     * <p>
     * 取模板中全部方块（与 {@link StructureTemplate#placeInWorld} 相同的坐标变换），
     * 过滤空气与结构空位后找出最低占用层，要求该层每一个方块列的正下方
     * 均为实体方块（{@link WorldGenUtils#isSolidSurface}，排除空气/树叶/植被类支撑）。
     * 任一列悬空即判定不可放置，避免斜坡地形上出现悬空的树干裙边或灌木底座。
     * <p>
     * 不使用 {@code filterBlocks}：其第三参数会被 NeoForge 线程安全补丁用作
     * ConcurrentHashMap 缓存键，且仅支持单一 Block 过滤，无法表达「取全部方块」。
     * 此处通过 {@link StructureTemplateAccessor} 直接取得 palettes，再按
     * placeInWorld 相同的方式选取调色板并做坐标变换。
     *
     * @param level     世界生成层
     * @param template  结构模板
     * @param startPos  结构放置起点
     * @param settings  放置设置（坐标变换需与 placeInWorld 一致）
     * @return true 表示底部支撑完整，可以放置
     */
    private static boolean hasGroundSupport(WorldGenLevel level, StructureTemplate template,
                                            BlockPos startPos, StructurePlaceSettings settings) {
        List<StructureTemplate.Palette> palettes =
                ((StructureTemplateAccessor) (Object) template).getPalettes();
        if (palettes.isEmpty()) {
            return true;
        }
        int minY = Integer.MAX_VALUE;
        Set<BlockPos> bottomColumns = new HashSet<>();
        for (StructureTemplate.StructureBlockInfo info : settings.getRandomPalette(palettes, startPos).blocks()) {
            if (info.state().isAir() || info.state().is(Blocks.STRUCTURE_VOID)) {
                continue;
            }
            BlockPos pos = StructureTemplate.calculateRelativePosition(settings, info.pos()).offset(startPos);
            if (pos.getY() < minY) {
                minY = pos.getY();
                bottomColumns.clear();
            }
            if (pos.getY() == minY) {
                bottomColumns.add(pos);
            }
        }
        for (BlockPos column : bottomColumns) {
            if (!WorldGenUtils.isSolidSurface(level, column.below())) {
                return false;
            }
        }
        return true;
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
