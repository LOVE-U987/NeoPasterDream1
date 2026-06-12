package com.pasterdream.pasterdreammod.fluid.types;

import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

/**
 * 融梦涌泉流体类型
 * 定义流体的物理属性（光照、粘度、温度、路径类型等）和客户端纹理
 */
public class MeltdreamLiquidFluidType extends FluidType {

    /**
     * 构造融梦涌泉流体类型
     * 属性：不可游泳、不可溺水、熔岩路径类型、光照12、粘度100、温度10
     */
    public MeltdreamLiquidFluidType() {
        super(FluidType.Properties.create()
                .canSwim(false).canDrown(false)
                .pathType(PathType.LAVA).adjacentPathType(null)
                .motionScale(0.007D)
                .lightLevel(12).viscosity(100).temperature(10)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH));
    }

    /**
     * 初始化客户端流体扩展
     * 设置流体 still/flowing 纹理
     *
     * @param consumer 客户端扩展消费者
     */
    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            private static final ResourceLocation STILL_TEXTURE =
                    ResourceLocation.fromNamespaceAndPath("pasterdream", "block/meltdream_liquid_still");
            private static final ResourceLocation FLOWING_TEXTURE =
                    ResourceLocation.fromNamespaceAndPath("pasterdream", "block/meltdream_liquid_flowing");

            @Override
            public ResourceLocation getStillTexture() {
                return STILL_TEXTURE;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return FLOWING_TEXTURE;
            }
        });
    }
}
