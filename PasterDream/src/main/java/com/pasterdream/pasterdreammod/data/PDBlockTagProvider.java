package com.pasterdream.pasterdreammod.data;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.data.ApiBlockTagProvider;
import com.pasterdream.pasterdreammod.api.doll.DollAPI;
import com.pasterdream.pasterdreammod.registry.PDBlockTags;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * PasterDream 方块标签数据生成器。
 * <p>
 * BlockAPI {@code mineable} 自动写入已上收到 {@link ApiBlockTagProvider}；
 * 本类仅补充手写注册（非 BlockAPI）的 shadow / 波次C 等方块。
 */
public class PDBlockTagProvider extends ApiBlockTagProvider {

    public PDBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                              @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, PasterDreamMod.MOD_ID, existingFileHelper, "PasterDream Block Tags");
    }

    @Override
    protected void addExtraTags(HolderLookup.Provider provider) {
        // ==================== 手动补充（手写注册的 shadow 系列方块） ====================
        // 以下方块通过 BLOCKS.register() 手写注册（非 BlockAPI/SimpleBlockBuilder），
        // 需要显式添加到对应镐标签中。注意保留逗号缩进以方便对比维护。

        // ---- 镐挖掘 - shadow 石系列 ----
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                PDBlocks.SHADOW_STONE.get(),
                PDBlocks.SHADOW_STONE_BRICK.get(),
                PDBlocks.SHADOW_STONE_BRICKS.get(),
                PDBlocks.SHADOW_STONE_TILES.get(),
                PDBlocks.CHISELED_SHADOW_STONE_BRICK.get(),
                PDBlocks.CRACKED_SHADOW_STONE_BRICK.get(),
                PDBlocks.SHADOW_STONE_BRICK_STAIRS.get(),
                PDBlocks.SHADOW_STONE_BRICK_SLAB.get(),
                PDBlocks.SHADOW_STONE_BRICK_WALL.get(),
                PDBlocks.SHADOW_STONE_BRICKS_STAIRS.get(),
                PDBlocks.SHADOW_STONE_BRICKS_SLAB.get(),
                PDBlocks.SHADOW_STONE_BRICKS_WALL.get(),
                PDBlocks.SHADOW_STONE_TILES_STAIRS.get(),
                PDBlocks.SHADOW_STONE_TILES_SLAB.get(),
                PDBlocks.SHADOW_STONE_TILES_WALL.get(),
                PDBlocks.SHADOW_DUNGEON_BLOCK_0.get(),
                PDBlocks.SHADOW_DUNGEON_BLOCK_1.get(),
                PDBlocks.SHADOW_DUNGEON_BLOCK_2.get(),
                PDBlocks.SHADOW_DUNGEON_BLOCK_3.get(),
                PDBlocks.SHADOW_DUNGEON_BLOCK_4.get(),
                PDBlocks.SHADOW_DUNGEON_BLOCK_5.get(),
                PDBlocks.SHADOW_DUNGEON_BLOCK_6.get(),
                PDBlocks.LOOSE_SHADOW_DUNGEON_BLOCK.get(),
                PDBlocks.SHADOW_BLAST_FURNACE_CORE.get(),
                PDBlocks.SHADOW_CHEST.get(),
                PDBlocks.SHADOW_VORTEX.get(),
                PDBlocks.SHADOW_LIGHT_0.get(),
                PDBlocks.WINDRUNNER_CRYSTAL_BLOCK.get(),
                PDBlocks.CONGEAL_WIND_BLOCK.get(),
                PDBlocks.STARCALL_BLOCK.get(),
                PDBlocks.STARCALL_CRACK.get(),
                PDBlocks.CYAN_STONE.get(),
                PDBlocks.CYAN_STONE_BRICKS.get(),
                PDBlocks.MOSSY_CYAN_STONE_BRICKS.get(),
                PDBlocks.CHISELED_CYAN_STONE_BRICKS.get(),
                PDBlocks.CYAN_STONE_PILLAR.get(),
                PDBlocks.CYAN_STONE_BRICK_STAIRS.get(),
                PDBlocks.CYAN_STONE_BRICK_SLAB.get(),
                PDBlocks.CYAN_STONE_BRICK_WALL.get(),
                PDBlocks.CYAN_STONE_PRESSURE_PLATE.get(),
                PDBlocks.CYAN_STONE_BUTTON.get(),
                PDBlocks.SALT_BLOCK.get(),
                // 方解石笋（grass_5/grass_6）：石质装饰方块，用镐挖掘
                PDBlocks.GRASS_5.get(),
                PDBlocks.GRASS_6.get()
        );

        // ---- 锹挖掘 - shadow 泥土系列 ----
        tag(BlockTags.MINEABLE_WITH_SHOVEL).add(
                PDBlocks.SHADOW_NYLIUM.get(),
                PDBlocks.SHADOW_SHROOMLIGHT.get(),
                PDBlocks.SHADOW_BLOCK.get(),
                PDBlocks.THICK_SHADOW_BLOCK.get(),
                PDBlocks.WHITE_SAND.get()
        );
        // shadow_fissure_0~5 为裂隙装饰方块，不应被工具挖掘（徒手破坏）

        // ---- 波次C：镐挖掘 - 锈黑金属 / 甲胄残骸 / 弹射装置 / 陶盘等（均 requiresCorrectToolForDrops） ----
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                PDBlocks.RUST_BLACK_METAL_BLOCK.get(),
                PDBlocks.RUST_BLACK_METAL_BLOCK_WALL.get(),
                PDBlocks.RUST_BLACK_METAL_BLOCK_BARS.get(),
                PDBlocks.ARMOR_WRECK_BLOCK_0.get(),
                PDBlocks.ARMOR_WRECK_BLOCK_1.get(),
                PDBlocks.ARMOR_WRECK_BLOCK_2.get(),
                PDBlocks.ARMOR_WRECK_BLOCK_3.get(),
                PDBlocks.ARMOR_WRECK_BLOCK_4.get(),
                PDBlocks.EJECTION_PRESSURE_BLOCK.get(),
                PDBlocks.EJECTION_PRESSURE_PLATE.get(),
                PDBlocks.CLAYPAN_0.get(),
                PDBlocks.CLAYPAN_2.get(),
                PDBlocks.SMALL_STONE_SPIRIT_BLOCK.get(),
                PDBlocks.DREAM_SPAWNER_1.get()
        );

        // ---- 波次C：斧挖掘 - 风泊木族 ----
        tag(BlockTags.MINEABLE_WITH_AXE).add(
                PDBlocks.WINDMOOR_LOG.get(),
                PDBlocks.WINDMOOR_WOOD.get(),
                PDBlocks.STRIPPED_WINDMOOR_LOG.get(),
                PDBlocks.STRIPPED_WINDMOOR_WOOD.get(),
                PDBlocks.WINDMOOR_PLANKS.get(),
                PDBlocks.WINDMOOR_STAIRS.get(),
                PDBlocks.WINDMOOR_SLAB.get(),
                PDBlocks.WINDMOOR_FENCE.get(),
                PDBlocks.WINDMOOR_FENCE_GATE.get(),
                PDBlocks.WINDMOOR_DOOR.get(),
                PDBlocks.WINDMOOR_TRAPDOOR.get()
        );

        // ---- DollAPI 玩偶方块（木质玩偶，使用斧挖掘） ----
        var mineableAxe = tag(BlockTags.MINEABLE_WITH_AXE);
        for (var reg : DollAPI.getRegistrations()) {
            mineableAxe.add(reg.block().get());
        }

        // ==================== 灯笼标签（c:lanterns 社区约定） ====================
        // 染梦灯笼 / 染梦水晶灯 加入 c:lanterns，供其他模组识别为灯笼类方块；
        // 同时收录原版灯笼与灵魂灯笼，保证标签引用完整性。
        tag(PDBlockTags.LANTERNS).add(
                PDBlocks.DYEDREAM_LANTERN.get(),
                PDBlocks.DYEDREAM_LARTERN.get(),
                Blocks.LANTERN,
                Blocks.SOUL_LANTERN
        );
    }
}
