package com.pasterdream.pasterdreammod.fluid;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDFluids;
import com.pasterdream.pasterdreammod.registry.PDFluidsType;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.block.MeltdreamLiquidBlock;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.state.StateDefinition;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

/**
 * 融梦涌泉流体
 * 使用 NeoForge BaseFlowingFluid 实现 Source（源）和 Flowing（流动）两种状态
 * 属性：爆炸抗性100、tickRate 3
 */
public abstract class MeltdreamLiquidFluid extends BaseFlowingFluid {

    /**
     * 流体属性
     * 关联：流体类型、源流体、流动流体、桶物品、流体方块
     */
    public static final Properties PROPERTIES = new Properties(
            PDFluidsType.MELTDREAM_LIQUID_TYPE,
            PDFluids.MELTDREAM_LIQUID,
            PDFluids.FLOWING_MELTDREAM_LIQUID
    )
            .explosionResistance(100f).tickRate(3)
            .bucket(() -> PDItems.MELTDREAM_LIQUID_BUCKET.get())
            .block(() -> (MeltdreamLiquidBlock) PDBlocks.MELTDREAM_LIQUID.get());

    /**
     * 私有构造函数，仅允许内部 Source/Flowing 子类调用
     */
    private MeltdreamLiquidFluid() {
        super(PROPERTIES);
    }

    /**
     * 融梦涌泉流体源
     * 静止态，amount 始终为 8（满格）
     */
    public static class Source extends MeltdreamLiquidFluid {
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
     * 融梦涌泉流体流动
     * 流动态，amount 随 LEVEL 属性变化
     */
    public static class Flowing extends MeltdreamLiquidFluid {
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
