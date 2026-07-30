package com.pasterdream.pasterdreammod.data;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.block.BlockAPI;
import com.pasterdream.pasterdreammod.api.block.BlockConfig;
import com.pasterdream.pasterdreammod.api.doll.DollAPI;
import com.pasterdream.pasterdreammod.block.MemorialDollBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * PasterDream 方块模型/状态数据生成器
 * 自动读取 {@link BlockAPI#getBlockConfigs()} 中的 model/textures 配置，
 * 生成 blockstate JSON 和 model JSON 到 src/generated/resources
 * <p>
 * 如果方块配置了 {@link BlockConfig#getRenderType()}，会在模型 JSON 中输出
 * {@code "render_type": "xxx"} 字段，用于透明/半透明方块的渲染。
 */
public class PDBlockModelProvider extends BlockStateProvider {

    public PDBlockModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, PasterDreamMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (var entry : BlockAPI.getBlockConfigs().entrySet()) {
            String name = entry.getKey();
            BlockConfig config = entry.getValue();
            String model = config.getModel();
            if (model == null) continue;

            Block block = BlockAPI.getBlock(name).orElse(null);
            if (block == null) continue;

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
                        // 柱子类方块：生成 axis=x/y/z 三个变体
                        axisBlock(pillarBlock, side, end);
                    } else {
                        // 非柱子方块：生成简单 cube_column 模型
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
            }
        }

        // ==================== DollAPI 玩偶方块模型/状态自动生成 ====================
        // 玩偶使用方块实体 + GeoRenderer 进行 3D 渲染，因此 blockstate 只需要一个
        // 占位粒子模型（用于破坏粒子与碰撞粒子），物品模型则复用默认展示配置。
        for (var reg : DollAPI.getRegistrations()) {
            String name = reg.name();
            Block block = reg.block().get();
            var config = reg.config();

            ModelFile particleModel = models().getBuilder("custom/" + name + "_particle")
                    .parent(new ModelFile.UncheckedModelFile(mcLoc("block/cube_all")))
                    .texture("all", toParticleTexture(config.texture()));

            getVariantBuilder(block).forAllStatesExcept(
                    state -> ConfiguredModel.builder().modelFile(particleModel).build(),
                    MemorialDollBlock.WATERLOGGED,
                    MemorialDollBlock.HOLDING);

            itemModels().getBuilder(name)
                    .parent(new ModelFile.UncheckedModelFile(modLoc("displaysettings/doll_default.item")));
        }
    }

    /**
     * 将 DollConfig 中的纹理路径转换为模型可用的粒子纹理位置。
     * <p>
     * 配置中的纹理路径通常为 {@code textures/block/<name>.png}，需要去掉前缀
     * {@code textures/} 与后缀 {@code .png}，得到 {@code block/<name>}。
     *
     * @param texture 配置中的纹理 ResourceLocation
     * @return 粒子纹理 ResourceLocation
     */
    private ResourceLocation toParticleTexture(ResourceLocation texture) {
        String path = texture.getPath();
        if (path.startsWith("textures/")) {
            path = path.substring("textures/".length());
        }
        if (path.endsWith(".png")) {
            path = path.substring(0, path.length() - 4);
        }
        if (texture.getNamespace().equals(PasterDreamMod.MOD_ID)) {
            return modLoc(path);
        }
        return ResourceLocation.fromNamespaceAndPath(texture.getNamespace(), path);
    }

    /**
     * 从 BlockConfig 获取纹理 ResourceLocation，若未配置则使用默认路径
     */
    private ResourceLocation resolveLoc(BlockConfig config, String layer, String defaultPath) {
        var textures = config.getTextures();
        return textures != null && textures.containsKey(layer)
                ? ResourceLocation.parse(textures.get(layer))
                : modLoc(defaultPath);
    }

    @Override
    public String getName() {
        return "PasterDream Block Models";
    }
}