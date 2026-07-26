package com.pasterdream.pasterdreammod.fluid;

import com.pasterdream.pasterdreammod.block.ShadowLiquidBlock;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDFluids;
import com.pasterdream.pasterdreammod.registry.PDFluidsType;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

/**
 * 熔融阴影流体 (shadow_liquid)
 * <p>
 * 阴影维度湖泊流体（世界生成硬依赖：ground_feature_shadow_0 湖泊特征引用）。
 * 使用 NeoForge BaseFlowingFluid 实现 Source（源）和 Flowing（流动）两种状态，
 * 结构完全对照 {@link MeltdreamLiquidFluid}。
 * 属性：爆炸抗性 100（与原版 ShadowLiquidFluid 一致）。
 */
public abstract class ShadowLiquidFluid extends BaseFlowingFluid {

    /**
     * 流体属性
     * 关联：流体类型、源流体、流动流体、桶物品、流体方块
     */
    public static final Properties PROPERTIES = new Properties(
            PDFluidsType.SHADOW_LIQUID_TYPE,
            PDFluids.SHADOW_LIQUID,
            PDFluids.FLOWING_SHADOW_LIQUID
    )
            .explosionResistance(100f)
            .bucket(() -> PDItems.SHADOW_LIQUID_BUCKET.get())
            .block(() -> (ShadowLiquidBlock) PDBlocks.SHADOW_LIQUID.get());

    /**
     * 私有构造函数，仅允许内部 Source/Flowing 子类调用
     */
    private ShadowLiquidFluid() {
        super(PROPERTIES);
    }

    /**
     * 熔融阴影流体源
     * 静止态，amount 始终为 8（满格）
     */
    public static class Source extends ShadowLiquidFluid {
        @Override
        public int getAmount(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }

    /**
     * 熔融阴影流体流动
     * 流动态，amount 随 LEVEL 属性变化
     */
    public static class Flowing extends ShadowLiquidFluid {
        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }
}
