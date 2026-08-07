package com.pasterdream.pasterdreammod.worldgen.tree;

import com.mojang.serialization.Codec;
import com.pasterdream.pasterdreammod.api.worldgen.WorldGenUtils;
import com.pasterdream.pasterdreammod.worldgen.tree.trunk.DyedreamColossalTrunkPlacer;
import com.pasterdream.pasterdreammod.worldgen.tree.trunk.DyedreamWorldTreeTrunkPlacer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;

/**
 * 染梦混合树 Feature
 * <p>
 * 委托给原版 TreeFeature 完成树干、树冠与装饰器生成，作为自定义类型入口供 configured_feature JSON 引用。
 * 未来可在此扩展 NBT 结构模板补充（根系/垂枝）。
 * <p>
 * <b>区块中心对齐：</b>巨型染梦树（超巨型/世界树）的树干与侧枝横向跨度可达 18+ 格，
 * 若以随机 origin 为基准生成，方块很容易落在 features 阶段 ±1 区块写半径之外，
 * 触发 "Detected setBlock in a far chunk" 刷屏并连锁导致光照 DataLayer NPE。
 * 因此对这两类巨型树将 X/Z 对齐到所在区块中心（Y 仍由 heightmap 决定），
 * 保证整棵树落在可安全写入的 3×3 区块内。普通尺寸的树保持原分布不受影响。
 */
public class DyedreamTreeFeature extends Feature<TreeConfiguration> {

    /**
     * 构造混合树特征
     *
     * @param codec TreeConfiguration 编解码器
     */
    public DyedreamTreeFeature(Codec<TreeConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<TreeConfiguration> context) {
        // 巨型树区块中心对齐：仅在横向跨度大的树干类型上启用
        FeaturePlaceContext<TreeConfiguration> effective = alignGiantTree(context);
        return new TreeFeature(TreeConfiguration.CODEC).place(effective);
    }

    /**
     * 巨型染梦树区块中心对齐
     * <p>
     * 对 {@link DyedreamColossalTrunkPlacer}（超巨型树）与 {@link DyedreamWorldTreeTrunkPlacer}
     * （世界树）两类横向跨度超过一个区块的巨木，将 origin 的 X/Z 对齐到所在区块中心，
     * 使树干与侧枝全程落在 features 阶段 ±1 区块写半径（中心 ±1，共 3×3 区块）内。
     * Y 坐标保持不变（由 placed_feature 的 heightmap 定位，直接复用原值）。
     * 其他普通染梦树直接透传原 context，分布不变。
     *
     * @param context 原始生成上下文
     * @return 对齐后的上下文（普通树为原对象）
     */
    private static FeaturePlaceContext<TreeConfiguration> alignGiantTree(FeaturePlaceContext<TreeConfiguration> context) {
        // mojmap 下 TreeConfiguration.trunkPlacer 为 public final 字段，直接访问
        TrunkPlacer trunkPlacer = context.config().trunkPlacer;
        boolean isGiant = trunkPlacer instanceof DyedreamColossalTrunkPlacer
                || trunkPlacer instanceof DyedreamWorldTreeTrunkPlacer;
        if (!isGiant) {
            return context;
        }

        // X/Z 对齐到区块中心，Y 保留 heightmap 定位结果
        BlockPos alignedOrigin = WorldGenUtils.alignToChunkCenter(context.origin());
        return new FeaturePlaceContext<>(
                context.topFeature(),
                context.level(),
                context.chunkGenerator(),
                context.random(),
                alignedOrigin,
                context.config()
        );
    }
}
