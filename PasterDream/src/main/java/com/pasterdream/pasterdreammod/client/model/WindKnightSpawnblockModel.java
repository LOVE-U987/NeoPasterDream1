package com.pasterdream.pasterdreammod.client.model;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.WindKnightSpawnblockBlock;
import com.pasterdream.pasterdreammod.block.entity.W4GeoDataBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.model.GeoModel;

/**
 * 风之骑士唤醒台模型
 * <p>
 * 按方块数据 {@link WindKnightSpawnblockBlock#STAGE} 动态选择
 * {@code geo/block/wind_knight_spawnblock_N.geo.json}，取代原版 5 个独立方块各自固定模型：
 * <ul>
 *   <li>模型：按 STAGE 0..4 切换对应 geo 文件；</li>
 *   <li>纹理：统一使用 {@code textures/block/wind_knight_spawnblock.png}（各阶段贴图内容相同）；</li>
 *   <li>动画：统一使用 {@code animations/block/wind_knight_spawnblock_0.animation.json}（各阶段动画一致）。</li>
 * </ul>
 */
public class WindKnightSpawnblockModel extends GeoModel<W4GeoDataBlockEntity> {

    @Override
    public ResourceLocation getModelResource(W4GeoDataBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID,
                "geo/block/wind_knight_spawnblock_" + stageOf(animatable) + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(W4GeoDataBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID,
                "textures/block/wind_knight_spawnblock.png");
    }

    @Override
    public ResourceLocation getAnimationResource(W4GeoDataBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID,
                "animations/block/wind_knight_spawnblock_0.animation.json");
    }

    /**
     * 读取方块实时阶段（客户端从世界读取方块状态，与服务端 setBlock 推进同步）。
     *
     * @param animatable 方块实体
     * @return 阶段编号 0..4
     */
    private static int stageOf(W4GeoDataBlockEntity animatable) {
        if (animatable != null && animatable.getLevel() != null && animatable.getLevel().isClientSide) {
            BlockState state = animatable.getLevel().getBlockState(animatable.getBlockPos());
            if (state.hasProperty(WindKnightSpawnblockBlock.STAGE)) {
                return state.getValue(WindKnightSpawnblockBlock.STAGE);
            }
        }
        return 0;
    }
}
