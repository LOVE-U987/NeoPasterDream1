package com.pasterdream.pasterdreammod.client.model;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.doll.DollAPI;
import com.pasterdream.pasterdreammod.api.doll.DollConfig;
import com.pasterdream.pasterdreammod.block.MemorialDollBlock;
import com.pasterdream.pasterdreammod.block.entity.DollBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.model.GeoModel;

/**
 * 通用玩偶方块模型
 * <p>
 * 根据 {@link DollConfig} 动态返回模型与纹理，并根据 {@link MemorialDollBlock#HOLDING}
 * 切换基础/抱物模型。
 */
public class DollBlockModel extends GeoModel<DollBlockEntity> {

    private static final ResourceLocation EMPTY_ANIMATION =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "animations/block/empty.animation.json");

    @Override
    public ResourceLocation getModelResource(DollBlockEntity animatable) {
        DollConfig config = getConfig(animatable);
        if (config == null) {
            return missingModel();
        }
        boolean holding = isHolding(animatable);
        // 如果当前处于抱物状态且提供了独立的抱物模型，则使用抱物模型；
        // 当抱物模型与基础模型相同时（表示该玩偶没有抱物模型），避免尝试加载不存在的 _holding 文件。
        if (holding) {
            ResourceLocation holdingModel = config.holdingModel();
            if (holdingModel != null && !holdingModel.equals(config.model())) {
                return holdingModel;
            }
        }
        return config.model();
    }

    @Override
    public ResourceLocation getTextureResource(DollBlockEntity animatable) {
        DollConfig config = getConfig(animatable);
        return config != null ? config.texture() : missingTexture();
    }

    @Override
    public ResourceLocation getAnimationResource(DollBlockEntity animatable) {
        return EMPTY_ANIMATION;
    }

    private static DollConfig getConfig(DollBlockEntity animatable) {
        return DollAPI.getConfig(animatable.getBlockState().getBlock()).orElse(null);
    }

    private static boolean isHolding(DollBlockEntity animatable) {
        BlockState state = animatable.getBlockState();
        if (state.hasProperty(MemorialDollBlock.HOLDING)) {
            return state.getValue(MemorialDollBlock.HOLDING);
        }
        return animatable.isHolding();
    }

    private static ResourceLocation missingModel() {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "geo/block/missing_doll.geo.json");
    }

    private static ResourceLocation missingTexture() {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/block/missing_doll.png");
    }
}
