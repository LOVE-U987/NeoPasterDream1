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
 * 玩偶/雕像类装饰方块注册。
 *
 * @see PDBlocks
 */
public class PDBlocksDolls {


    // ==================== 玩偶/雕像方块 ====================

    /**
     * 娇小琴雨梦玩偶方块 (qin_doll_0)
     * GeckoLib 3D 静态装饰玩偶
     */
    public static final DeferredBlock<QymDoll0Block> QIN_DOLL_0 = PDBlocks.BLOCKS.register("qin_doll_0",
            () -> new QymDoll0Block(BlockBehaviour.Properties.of()
                    .sound(SoundType.DECORATED_POT)
                    .strength(1.0f)
                    .noOcclusion()));

    /**
     * 娇小幼幼紫玩偶方块 (little_purple_doll_0)
     * GeckoLib 3D 静态装饰玩偶
     */
    public static final DeferredBlock<UuzDoll0Block> LITTLE_PURPLE_DOLL_0 = PDBlocks.BLOCKS.register("little_purple_doll_0",
            () -> new UuzDoll0Block(BlockBehaviour.Properties.of()
                    .sound(SoundType.DECORATED_POT)
                    .strength(1.0f)
                    .noOcclusion()));

    /**
     * 狐狸雕像方块 (golden_fox_sculpture)
     * GeckoLib 3D 静态装饰雕像
     */
    public static final DeferredBlock<GoldenFoxSculptureBlock> GOLDEN_FOX_SCULPTURE = PDBlocks.BLOCKS.register("golden_fox_sculpture",
            () -> new GoldenFoxSculptureBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.DECORATED_POT)
                    .strength(1.0f)
                    .noOcclusion()));

    /**
     * LOVE_U拉乌酱玩偶方块 (love_u_doll)
     * 可放置方块玩偶，绑定 LoveUDollBlockEntity
     */
    public static final DeferredBlock<LoveUDollBlock> LOVE_U_DOLL = PDBlocks.BLOCKS.register("love_u_doll",
            () -> new LoveUDollBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.DECORATED_POT)
                    .strength(1.0f)
                    .noOcclusion()));

    /**
     * EOUL小幽灵玩偶方块 (eoul_doll)
     * 可放置方块玩偶，绑定 EoulDollBlockEntity
     */
    public static final DeferredBlock<EoulDollBlock> EOUL_DOLL = PDBlocks.BLOCKS.register("eoul_doll",
            () -> new EoulDollBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.DECORATED_POT)
                    .strength(1.0f)
                    .noOcclusion()));

    public static final DeferredBlock<ShadowChestBlock> SHADOW_CHEST = PDBlocks.BLOCKS.register("shadow_chest",
            () -> new ShadowChestBlock(BlockBehaviour.Properties.of()
                    .sound(SoundType.DEEPSLATE_TILES)
                    .strength(1.0f, 0.5f)
                    .noOcclusion()));
}
