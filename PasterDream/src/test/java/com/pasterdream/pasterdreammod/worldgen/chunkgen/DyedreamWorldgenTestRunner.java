package com.pasterdream.pasterdreammod.worldgen.chunkgen;

import com.pasterdream.pasterdreammod.block.CalciteSpikeBlock;
import com.pasterdream.pasterdreammod.worldgen.feature.CaveCalciteSpikeFeature;
import com.mojang.serialization.Lifecycle;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.neoforged.fml.loading.LoadingModList;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.List;

/** 使用真实方块状态和生成器验证洞穴正反例，不接触存档。 */
public final class DyedreamWorldgenTestRunner {

    private static CalciteSpikeBlock[] spikes;

    private DyedreamWorldgenTestRunner() {
    }

    /**
     * 回归测试入口，任一断言失败立即以非零退出。
     *
     * @param args 未使用
     */
    public static void main(String[] args) {
        SharedConstants.tryDetectVersion();
        // 独立运行器没有 FML 扫描阶段，仅为空模组列表建立必要引导上下文。
        LoadingModList.of(List.of(), List.of(), List.of(), List.of(), Map.of());
        bootstrapTestBlocks();
        testCave();
        testRejectedSpaces();
        testHeightmaps();
        System.out.println("PASS: cave stalagmite and terrain heightmap regression cases");
    }

    /** 独立进程中在原版冻结注册表前注册两变体，再执行完整原版引导。 */
    private static void bootstrapTestBlocks() {
        try {
            var flag = Bootstrap.class.getDeclaredField("isBootstrapped");
            flag.setAccessible(true);
            // 原版引导在创建方块前设置此守卫；这里插入隔离测试方块后恢复并完整执行引导。
            flag.setBoolean(null, true);
            try {
                spikes = new CalciteSpikeBlock[]{createSpike(0), createSpike(1)};
                for (int variant = 0; variant < spikes.length; variant++) {
                    Registry.register(BuiltInRegistries.BLOCK,
                            ResourceLocation.fromNamespaceAndPath("pasterdream_worldgen_test", "spike_" + variant), spikes[variant]);
                }
            } finally {
                flag.setBoolean(null, false);
            }
            Bootstrap.bootStrap();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("无法在独立测试进程中初始化方块注册表", exception);
        }
    }

    /** 验证旧高新低、全空气列、含水列及河流式后续修改的高度图。 */
    private static void testHeightmaps() {
        MappedRegistry<Biome> biomes = new MappedRegistry<>(Registries.BIOME, Lifecycle.stable());
        Biome biome = new Biome.BiomeBuilder().hasPrecipitation(false).temperature(0.5f).downfall(0.5f)
                .specialEffects(new BiomeSpecialEffects.Builder().fogColor(0).waterColor(0)
                        .waterFogColor(0).skyColor(0).build())
                .mobSpawnSettings(MobSpawnSettings.EMPTY).generationSettings(BiomeGenerationSettings.EMPTY).build();
        Registry.register(biomes, Biomes.PLAINS, biome);
        ProtoChunk chunk = new ProtoChunk(new ChunkPos(0, 0), UpgradeData.EMPTY,
                LevelHeightAccessor.create(-64, 384), biomes, null);
        chunk.setPersistedStatus(ChunkStatus.NOISE);
        directSet(chunk, new BlockPos(0, 100, 0), Blocks.STONE.defaultBlockState());
        directSet(chunk, new BlockPos(1, 100, 0), Blocks.STONE.defaultBlockState());
        Heightmap.primeHeightmaps(chunk, EnumSet.of(Heightmap.Types.WORLD_SURFACE_WG, Heightmap.Types.OCEAN_FLOOR_WG));
        directSet(chunk, new BlockPos(0, 100, 0), Blocks.AIR.defaultBlockState());
        directSet(chunk, new BlockPos(1, 100, 0), Blocks.AIR.defaultBlockState());
        directSet(chunk, new BlockPos(0, 0, 0), Blocks.STONE.defaultBlockState());
        for (int y = 1; y <= 3; y++) {
            directSet(chunk, new BlockPos(0, y, 0), Blocks.WATER.defaultBlockState());
        }
        DyedreamChunkGenerator.rebuildTerrainHeightmaps(chunk);
        require(chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, 0, 0) == 3, "新表面应为水面而非旧高地");
        require(chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, 0, 0) == 0, "海床高度应排除液体");
        require(chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, 1, 0) == -65, "全空气列应清除旧高度");
        chunk.setBlockState(new BlockPos(0, 0, 0), Blocks.WATER.defaultBlockState(), false);
        require(chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, 0, 0) == -65, "河流挖除洞底后高度图应继续更新");
    }

    /**
     * 模拟自定义地形绕过区块写入接口覆盖 section。
     *
     * @param chunk 测试区块
     * @param pos 位置
     * @param state 方块状态
     */
    private static void directSet(ProtoChunk chunk, BlockPos pos, BlockState state) {
        chunk.getSection(chunk.getSectionIndex(pos.getY())).setBlockState(
                pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state, false);
    }

    /** 验证两个小笋变体在真实落点的支撑、洞顶和支撑消失行为。 */
    private static void testCave() {
        for (int variant = 0; variant < 2; variant++) {
            CalciteSpikeBlock spike = spikes[variant];
            Fixture cave = new Fixture(Blocks.STONE.defaultBlockState(), 10);
            require(cave.generate(spike), "小笋变体 " + variant + " 应在石质洞穴生成");
            BlockPos pos = new BlockPos(8, 1, 8);
            require(cave.blocks.get(pos).is(spike), "实际落点必须贴住洞底");
            require(cave.blocks.get(pos.below()).is(Blocks.STONE), "不得覆盖洞底");
            require(cave.blocks.get(new BlockPos(8, 10, 8)).is(Blocks.STONE), "不得穿透洞顶");
            require(spike.defaultBlockState().canSurvive(cave.level(), pos), "有支撑时可以存活");
            cave.blocks.remove(pos.below());
            require(!spike.defaultBlockState().canSurvive(cave.level(), pos), "无支撑时不可存活");
            BlockState updated = spike.defaultBlockState().updateShape(Direction.DOWN,
                    Blocks.AIR.defaultBlockState(), cave.level(), pos, pos.below());
            require(updated.isAir(), "挖掉支撑后小笋必须移除");
        }
    }

    /** 验证两变体在露天、树冠、液体、完全悬空和无顶盖位置均拒绝。 */
    private static void testRejectedSpaces() {
        for (int variant = 0; variant < 2; variant++) {
            CalciteSpikeBlock spike = spikes[variant];
            reject(new Fixture(Blocks.AIR.defaultBlockState(), 10), spike, "露天地表");
            reject(new Fixture(Blocks.OAK_LEAVES.defaultBlockState(), 10), spike, "树冠");
            reject(new Fixture(Blocks.WATER.defaultBlockState(), 10), spike, "含水顶盖");
            reject(new Fixture(Blocks.STONE.defaultBlockState(), 1), spike, "洞穴无空气空间");
            Fixture support = new Fixture(Blocks.STONE.defaultBlockState(), 10);
            support.blocks.remove(new BlockPos(8, 0, 8));
            reject(support, spike, "实际落点完全悬空");
            Fixture roof = new Fixture(Blocks.STONE.defaultBlockState(), 10);
            roof.blocks.remove(new BlockPos(8, 10, 8));
            reject(roof, spike, "实际落点无顶盖");
            for (BlockState liquid : new BlockState[]{Blocks.WATER.defaultBlockState(), Blocks.LAVA.defaultBlockState()}) {
                Fixture submerged = new Fixture(Blocks.STONE.defaultBlockState(), 10);
                submerged.blocks.put(new BlockPos(8, 1, 8), liquid);
                reject(submerged, spike, "实际落点被液体占据");
            }
        }
    }

    /**
     * 按注册时的两种轮廓创建真实的小笋方块。
     *
     * @param variant 变体编号
     * @return 测试小笋
     */
    private static CalciteSpikeBlock createSpike(int variant) {
        return new CalciteSpikeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE).noCollission(),
                variant == 0 ? Block.box(1, 0, 1, 15, 1, 15) : Block.box(0, 0, 0, 16, 1, 16));
    }

    /**
     * 断言无效空间不产生任何放置副作用。
     *
     * @param fixture 测试空间
     * @param spike 小笋变体
     * @param name 场景名称
     */
    private static void reject(Fixture fixture, CalciteSpikeBlock spike, String name) {
        require(!fixture.generate(spike) && fixture.writes == 0, name + "必须完全拒绝");
    }

    /**
     * 断言指定结果。
     *
     * @param condition 预期成立的条件
     * @param message 失败说明
     */
    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
        System.out.println("PASS: " + message);
    }

    /** 仅模拟方块读写的隔离世界；未实现的世界操作直接失败，防止测试掩盖副作用。 */
    private static final class Fixture {
        private final Map<BlockPos, BlockState> blocks = new HashMap<>();
        private int writes;

        /**
         * 创建平坦石质洞底及指定洞顶。
         *
         * @param roof 洞顶状态
         * @param roofY 洞顶高度
         */
        private Fixture(BlockState roof, int roofY) {
            for (int x = -4; x <= 4; x++) {
                for (int z = -4; z <= 4; z++) {
                    blocks.put(new BlockPos(x + 8, 0, z + 8), Blocks.STONE.defaultBlockState());
                    blocks.put(new BlockPos(x + 8, roofY, z + 8), roof);
                }
            }
        }

        /**
         * 通过真实特征上下文验证 random_patch 偏移后的单个候选点。
         *
         * @param spike 小笋变体
         * @return 特征返回值
         */
        private boolean generate(CalciteSpikeBlock spike) {
            var config = new CaveCalciteSpikeFeature.Config(BlockStateProvider.simple(spike),
                    BlockPredicate.matchesBlocks(Blocks.STONE), 16, 32);
            return new CaveCalciteSpikeFeature().place(new FeaturePlaceContext<>(Optional.empty(),
                    level(), null, RandomSource.create(42), new BlockPos(8, 2, 8), config));
        }

        /**
         * 创建仅提供测试所需方块访问的代理。
         *
         * @return 隔离世界
         */
        private WorldGenLevel level() {
            return (WorldGenLevel) Proxy.newProxyInstance(WorldGenLevel.class.getClassLoader(),
                    new Class<?>[]{WorldGenLevel.class}, this::invoke);
        }

        /**
         * 转发世界接口中本测试允许的少量操作。
         *
         * @param proxy 世界代理
         * @param method 调用方法
         * @param args 方法参数
         * @return 方法结果
         * @throws Throwable 未实现方法或默认调用失败
         */
        private Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) throws Throwable {
            return switch (method.getName()) {
                case "getBlockState" -> blocks.getOrDefault(args[0], Blocks.AIR.defaultBlockState());
                case "getMinBuildHeight" -> -64;
                case "getMaxBuildHeight" -> 320;
                case "setBlock" -> {
                    blocks.put(((BlockPos) args[0]).immutable(), (BlockState) args[1]);
                    writes++;
                    yield true;
                }
                default -> {
                    if (method.isDefault()) {
                        yield InvocationHandler.invokeDefault(proxy, method, args);
                    }
                    throw new UnsupportedOperationException(method.getName());
                }
            };
        }
    }
}
