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
 * BOSS 相关方块注册。
 *
 * @see PDBlocks
 */
public class PDBlocksBoss {


    // ==================== BOSS 相关方块 ====================

    /**
     * 亚伦柯斯竞技场传送门方块 (aaroncos_arena_portals)
     * 位于 BOSS 竞技场入口的不可破坏传送门方块，触碰时传送至竞技场维度
     * 继承 SlabBlock 实现半砖形状，无碰撞箱，发光等级 15
     */
    public static final DeferredBlock<SlabBlock> AARONCOS_ARENA_PORTALS = PDBlocks.BLOCKS.registerBlock("aaroncos_arena_portals",
            p -> new AaroncosArenaPortalsBlock(), BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .sound(SoundType.GLASS)
                    .strength(-1, 3600000)
                    .lightLevel(s -> 15)
                    .noCollission()
                    .noOcclusion()
                    .hasPostProcess((bs, br, bp) -> true)
                    .emissiveRendering((bs, br, bp) -> true)
                    .isRedstoneConductor((bs, br, bp) -> false)
                    .dynamicShape());

    /**
     * 亚伦柯斯之触战利品箱 (aaroncos_hand_chest)
     * BOSS 战后的战利品箱，含 GeckoLib 3D 模型和动画
     */
    public static final DeferredBlock<AaroncosHandChestBlock> AARONCOS_HAND_CHEST = PDBlocks.BLOCKS.registerBlock("aaroncos_hand_chest",
            AaroncosHandChestBlock::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.GLASS)
                    .strength(1.0f, 0.5f)
                    .noOcclusion());

    /**
     * 暗影漩涡 (shadow_vortex)
     * BOSS 右手涡流技能生成的临时方块，含 GeckoLib 3D 模型和动画
     * 无碰撞、无掉落、持续约 5 秒后自动消失
     */
    public static final DeferredBlock<ShadowVortexBlock> SHADOW_VORTEX = PDBlocks.BLOCKS.registerBlock("shadow_vortex",
            ShadowVortexBlock::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.GLASS)
                    .strength(-1, 3600000)
                    .noCollission()
                    .noOcclusion()
                    .isRedstoneConductor((bs, br, bp) -> false));

    /**
     * 亚伦柯斯之手生成激活方块 (aaroncoshandspawnblock)
     * 放置后周期性检测并激活 BOSS 战，含 GeckoLib 3D 模型和动画
     * 不可破坏，发光等级 12
     */
    public static final DeferredBlock<AaroncosHandSpawnBlock> AARONCOSHANDSPAWNBLOCK = PDBlocks.BLOCKS.registerBlock("aaroncoshandspawnblock",
            AaroncosHandSpawnBlock::new, BlockBehaviour.Properties.of()
                    .sound(SoundType.GLASS)
                    .strength(-1, 3600000)
                    .lightLevel(s -> 12)
                    .noOcclusion()
                    .hasPostProcess((bs, br, bp) -> true)
                    .emissiveRendering((bs, br, bp) -> true)
                    .isRedstoneConductor((bs, br, bp) -> false));
}
