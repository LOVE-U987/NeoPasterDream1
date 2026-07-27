package com.pasterdream.pasterdreammod.worldgen.tree;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

/**
 * 染梦混合树 Feature
 * <p>
 * 委托给原版 TreeFeature 完成树干、树冠与装饰器生成，作为自定义类型入口供 configured_feature JSON 引用。
 * 未来可在此扩展 NBT 结构模板补充（根系/垂枝）。
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
        return new TreeFeature(TreeConfiguration.CODEC).place(context);
    }
}
