package com.pasterdream.pasterdreammod.client.model;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.MemorialDollBlock;
import com.pasterdream.pasterdreammod.block.entity.MemorialDollBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.model.GeoModel;

/**
 * 纪念玩偶方块模型基类 (Memorial Doll Block Model)
 * <p>
 * 根据方块实体是否抱物，动态切换空模型与抱物模型。
 *
 * @param <T> 纪念玩偶方块实体类型
 */
public abstract class MemorialDollBlockModel<T extends MemorialDollBlockEntity> extends GeoModel<T> {

    private final String name;

    /**
     * 构造纪念玩偶方块模型
     *
     * @param name 模型基础注册名，如 {@code love_u_doll}
     */
    protected MemorialDollBlockModel(String name) {
        this.name = name;
    }

    @Override
    public ResourceLocation getModelResource(T animatable) {
        // 客户端直接从世界中读取方块状态 HOLDING，避免依赖 BlockEntity 缓存的方块状态，
        // 确保空/抱物模型切换与物品拿起/取出动作同步。
        boolean holding = false;
        if (animatable != null && animatable.getLevel() != null && animatable.getLevel().isClientSide) {
            BlockState state = animatable.getLevel().getBlockState(animatable.getBlockPos());
            if (state.hasProperty(MemorialDollBlock.HOLDING)) {
                holding = state.getValue(MemorialDollBlock.HOLDING);
            }
        } else if (animatable != null) {
            holding = animatable.isHolding();
        }
        String suffix = holding ? "_holding" : "";
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "geo/block/" + name + suffix + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/block/" + name + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "animations/block/empty.animation.json");
    }
}
