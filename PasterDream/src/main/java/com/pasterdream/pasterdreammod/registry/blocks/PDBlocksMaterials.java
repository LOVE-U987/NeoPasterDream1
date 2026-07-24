package com.pasterdream.pasterdreammod.registry.blocks;

import com.pasterdream.pasterdreammod.api.block.BlockAPI;
import com.pasterdream.pasterdreammod.api.block.BlockConfig;
import com.pasterdream.pasterdreammod.api.block.builder.VariantSetResult;
import com.pasterdream.pasterdreammod.block.*;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Map;


/**
 * 流体方块注册。
 *
 * @see PDBlocks
 */
public class PDBlocksMaterials {


    // ==================== 流体方块 ====================

    /**
     * 融梦涌泉流体方块 (meltdream_liquid)
     * 使用 MeltdreamLiquidBlock 自定义实现，含粒子效果和发光渲染
     */
    public static final DeferredBlock<MeltdreamLiquidBlock> MELTDREAM_LIQUID = PDBlocks.BLOCKS.registerBlock("meltdream_liquid",
            p -> new MeltdreamLiquidBlock());
}
