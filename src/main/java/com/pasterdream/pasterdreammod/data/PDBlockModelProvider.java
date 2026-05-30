package com.pasterdream.pasterdreammod.data;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.block.BlockAPI;
import com.pasterdream.pasterdreammod.api.block.BlockConfig;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * PasterDream 方块模型/状态数据生成器
 * 自动读取 {@link BlockAPI#getBlockConfigs()} 中的 model/textures 配置，
 * 生成 blockstate JSON 和 model JSON 到 src/generated/resources
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

            Block block = BlockAPI.getBlock(name);
            if (block == null) continue;

            switch (model) {
                case "cube_all" -> {
                    ResourceLocation tex = resolveLoc(config, "all", "block/" + name);
                    simpleBlock(block, models().cubeAll(name, tex));
                }
                case "cube_column" -> {
                    ResourceLocation end = resolveLoc(config, "end", "block/" + name + "_top");
                    ResourceLocation side = resolveLoc(config, "side", "block/" + name + "_side");
                    if (block instanceof RotatedPillarBlock pillarBlock) {
                        axisBlock(pillarBlock, side, end);
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