package com.pasterdream.pasterdreammod.api.data;

import com.pasterdream.pasterdreammod.api.block.BlockAPI;
import com.pasterdream.pasterdreammod.api.block.BlockConfig;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * 基于 {@link BlockAPI#getBlockConfigs()} 的方块模型/状态生成基类。
 * <p>
 * 读取 config 的 {@code model} / {@code textures} / {@code renderType}，生成常见立方体形态。
 * 主模可继承后覆盖 {@link #registerExtraStatesAndModels()} 补充非 API 注册方块。
 * <p>
 * 支持的 model id：{@code cube_all}、{@code cube_column}、{@code cube_top_bottom}、{@code cube_6}。
 */
public abstract class ApiBlockModelProvider extends BlockStateProvider {

    private final String providerName;

    /**
     * @param output             数据包输出
     * @param modId              模组 ID（与 BlockAPI 注册命名空间一致）
     * @param existingFileHelper 已有资源校验
     * @param providerName       {@link #getName()} 显示名
     */
    protected ApiBlockModelProvider(PackOutput output, String modId,
                                    ExistingFileHelper existingFileHelper, String providerName) {
        super(output, modId, existingFileHelper);
        this.providerName = providerName;
    }

    @Override
    protected final void registerStatesAndModels() {
        registerFromBlockConfigs();
        registerExtraStatesAndModels();
    }

    /**
     * 遍历 {@link BlockAPI#getBlockConfigs()} 并按 model 类型生成。
     */
    protected void registerFromBlockConfigs() {
        for (var entry : BlockAPI.getBlockConfigs().entrySet()) {
            String name = entry.getKey();
            BlockConfig config = entry.getValue();
            String model = config.getModel();
            if (model == null) {
                continue;
            }

            Block block = BlockAPI.getBlock(name).orElse(null);
            if (block == null) {
                continue;
            }

            applyModel(name, block, config, model);
        }
    }

    /**
     * 按 model id 生成单个方块；子类可覆盖以扩展 model 类型。
     */
    protected void applyModel(String name, Block block, BlockConfig config, String model) {
        switch (model) {
            case "cube_all" -> {
                ResourceLocation tex = resolveLoc(config, "all", "block/" + name);
                var modelBuilder = models().cubeAll(name, tex);
                if (config.getRenderType() != null) {
                    modelBuilder.renderType(config.getRenderType());
                }
                simpleBlock(block, modelBuilder);
            }
            case "cube_column" -> {
                ResourceLocation end = resolveLoc(config, "end", "block/" + name + "_top");
                ResourceLocation side = resolveLoc(config, "side", "block/" + name + "_side");
                if (block instanceof RotatedPillarBlock pillarBlock) {
                    axisBlock(pillarBlock, side, end);
                } else {
                    var modelBuilder = models().cubeColumn(name, side, end);
                    if (config.getRenderType() != null) {
                        modelBuilder.renderType(config.getRenderType());
                    }
                    simpleBlock(block, modelBuilder);
                }
            }
            case "cube_top_bottom" -> {
                ResourceLocation top = resolveLoc(config, "top", "block/" + name + "_top");
                ResourceLocation side = resolveLoc(config, "side", "block/" + name + "_side");
                ResourceLocation bottom = resolveLoc(config, "bottom", "block/" + name + "_bottom");
                simpleBlock(block, models().cubeBottomTop(name, side, bottom, top));
            }
            case "cube_6" -> {
                ResourceLocation north = resolveLoc(config, "north", "block/" + name + "_north");
                ResourceLocation south = resolveLoc(config, "south", "block/" + name + "_south");
                ResourceLocation east = resolveLoc(config, "east", "block/" + name + "_east");
                ResourceLocation west = resolveLoc(config, "west", "block/" + name + "_west");
                ResourceLocation up = resolveLoc(config, "up", "block/" + name + "_up");
                ResourceLocation down = resolveLoc(config, "down", "block/" + name + "_down");
                simpleBlock(block, models().cube(name, down, up, north, south, east, west));
            }
            default -> onUnknownModel(name, block, config, model);
        }
    }

    /**
     * 未知 model id 时的钩子（默认忽略）。
     */
    protected void onUnknownModel(String name, Block block, BlockConfig config, String model) {
        // no-op
    }

    /**
     * 主模补充：手写注册方块、特殊模型等。默认空。
     */
    protected void registerExtraStatesAndModels() {
        // no-op
    }

    /**
     * 从 BlockConfig 解析纹理；未配置时用 mod 内默认路径。
     */
    protected ResourceLocation resolveLoc(BlockConfig config, String layer, String defaultPath) {
        var textures = config.getTextures();
        return textures != null && textures.containsKey(layer)
                ? ResourceLocation.parse(textures.get(layer))
                : modLoc(defaultPath);
    }

    @Override
    public String getName() {
        return providerName;
    }
}
