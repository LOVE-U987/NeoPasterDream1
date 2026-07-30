package com.pasterdream.pasterdreammod.api.data;

import com.pasterdream.pasterdreammod.api.block.BlockAPI;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * 基于 {@link BlockAPI#getBlockConfigs()} 的方块工具标签生成基类。
 * <p>
 * 读取 config {@code mineable}（axe/pickaxe/shovel/hoe）写入对应 {@link BlockTags}。
 * 主模继承后在 {@link #addExtraTags(HolderLookup.Provider)} 补充手写注册方块。
 */
public abstract class ApiBlockTagProvider extends BlockTagsProvider {

    private final String providerName;
    private final String modId;

    /**
     * @param output             数据包输出
     * @param lookupProvider     注册表查找
     * @param modId              模组 ID
     * @param existingFileHelper 可选已有资源
     * @param providerName       {@link #getName()} 显示名
     */
    protected ApiBlockTagProvider(PackOutput output,
                                  CompletableFuture<HolderLookup.Provider> lookupProvider,
                                  String modId,
                                  @Nullable ExistingFileHelper existingFileHelper,
                                  String providerName) {
        super(output, lookupProvider, modId, existingFileHelper);
        this.modId = modId;
        this.providerName = providerName;
    }

    @Override
    protected final void addTags(HolderLookup.Provider provider) {
        addTagsFromBlockConfigs();
        addExtraTags(provider);
    }

    /** {@code pasterdream:plantable_on} 标签键 —— 标记可种植地面 */
    private TagKey<Block> plantableOnTag;

    /**
     * 获取或创建 {@code plantable_on} 标签键
     */
    private TagKey<Block> getPlantableOnTag() {
        if (plantableOnTag == null) {
            plantableOnTag = TagKey.create(Registries.BLOCK,
                    ResourceLocation.fromNamespaceAndPath(modId, "plantable_on"));
        }
        return plantableOnTag;
    }

    /**
     * 遍历 BlockAPI configs 写入 mineable 标签 + plantable_on 标签。
     */
    protected void addTagsFromBlockConfigs() {
        boolean plantableInitialized = false;

        for (var entry : BlockAPI.getBlockConfigs().entrySet()) {
            String name = entry.getKey();
            var config = entry.getValue();
            var loc = ResourceLocation.fromNamespaceAndPath(modId, name);

            // 用 ResourceKey 替代 Block 实例，兼容 TagAppender 的 add(ResourceKey) 签名
            ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, loc);

            // ---- 写入 mineable 标签 ----
            String mineable = config.getMineable();
            if (mineable != null) {
                switch (mineable) {
                    case "axe" -> tag(BlockTags.MINEABLE_WITH_AXE).add(blockKey);
                    case "pickaxe" -> tag(BlockTags.MINEABLE_WITH_PICKAXE).add(blockKey);
                    case "shovel" -> tag(BlockTags.MINEABLE_WITH_SHOVEL).add(blockKey);
                    case "hoe" -> tag(BlockTags.MINEABLE_WITH_HOE).add(blockKey);
                    default -> onUnknownMineable(name, blockKey, mineable);
                }
            }

            // ---- 写入 plantable_on 标签 ----
            if (config.isPlantable()) {
                if (!plantableInitialized) {
                    tag(getPlantableOnTag()).addTag(BlockTags.DIRT);
                    plantableInitialized = true;
                }
                tag(getPlantableOnTag()).add(blockKey);
            }
        }
    }

    /**
     * 未知 mineable 字符串时的钩子（默认忽略）。
     *
     * @param name     方块注册名
     * @param blockKey 方块的 ResourceKey
     * @param mineable 未知的挖掘工具标识
     */
    protected void onUnknownMineable(String name, ResourceKey<Block> blockKey, String mineable) {
        // no-op
    }

    /**
     * 主模补充：非 BlockAPI 注册的方块标签。默认空。
     */
    protected void addExtraTags(HolderLookup.Provider provider) {
        // no-op
    }

    @Override
    public String getName() {
        return providerName;
    }
}
