package com.pasterdream.pasterdreammod.api.data;

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

    /**
     * 遍历 BlockAPI configs 写入 mineable 标签。
     */
    protected void addTagsFromBlockConfigs() {
        for (var entry : BlockAPI.getBlockConfigs().entrySet()) {
            String name = entry.getKey();
            String mineable = entry.getValue().getMineable();
            if (mineable == null) {
                continue;
            }

            Block block = BuiltInRegistries.BLOCK.get(
                    ResourceLocation.fromNamespaceAndPath(modId, name));
            if (block == null) {
                continue;
            }

            switch (mineable) {
                case "axe" -> tag(BlockTags.MINEABLE_WITH_AXE).add(block);
                case "pickaxe" -> tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
                case "shovel" -> tag(BlockTags.MINEABLE_WITH_SHOVEL).add(block);
                case "hoe" -> tag(BlockTags.MINEABLE_WITH_HOE).add(block);
                default -> onUnknownMineable(name, block, mineable);
            }
        }
    }

    /**
     * 未知 mineable 字符串时的钩子（默认忽略）。
     */
    protected void onUnknownMineable(String name, Block block, String mineable) {
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
