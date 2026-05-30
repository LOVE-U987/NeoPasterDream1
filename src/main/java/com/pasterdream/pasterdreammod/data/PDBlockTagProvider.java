package com.pasterdream.pasterdreammod.data;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.block.BlockAPI;
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
    }

    @Override
    public String getName() {
        return "PasterDream Block Tags";
    }
}