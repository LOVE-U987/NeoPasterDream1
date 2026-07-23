package com.pasterdream.pasterdreammod.data;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.block.BlockAPI;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * PasterDream 方块标签数据生成器
 * 自动读取 {@link BlockAPI#getBlockConfigs()} 中的 mineable 配置生成工具标签
 */
public class PDBlockTagProvider extends BlockTagsProvider {

    public PDBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, PasterDreamMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // ==================== 自动注册（从 BlockAPI 配置读取） ====================
        var configs = BlockAPI.getBlockConfigs();
        for (var entry : configs.entrySet()) {
            String name = entry.getKey();
            var config = entry.getValue();
            String mineable = config.getMineable();
            if (mineable == null) continue;

            Block block = BuiltInRegistries.BLOCK.get(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, name));
            if (block == null) continue;

            switch (mineable) {
                case "axe" -> tag(BlockTags.MINEABLE_WITH_AXE).add(block);
                case "pickaxe" -> tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
                case "shovel" -> tag(BlockTags.MINEABLE_WITH_SHOVEL).add(block);
                case "hoe" -> tag(BlockTags.MINEABLE_WITH_HOE).add(block);
            }
        }

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
                PDBlocks.SALT_BLOCK.get()
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
    }

    @Override
    public String getName() {
        return "PasterDream Block Tags";
    }
}