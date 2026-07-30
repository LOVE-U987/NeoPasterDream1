package com.pasterdream.pasterdreammod.data;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.data.ApiBlockModelProvider;
import com.pasterdream.pasterdreammod.api.doll.DollAPI;
import com.pasterdream.pasterdreammod.block.MemorialDollBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * PasterDream 方块模型/状态数据生成器。
 * <p>
 * BlockAPI 驱动的通用生成已上收到 {@link ApiBlockModelProvider}；
 * 主模仅保留命名与未来 {@link #registerExtraStatesAndModels()} 扩展点。
 */
public class PDBlockModelProvider extends ApiBlockModelProvider {

    public PDBlockModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, PasterDreamMod.MOD_ID, existingFileHelper, "PasterDream Block Models");
    }

    @Override
    protected void registerExtraStatesAndModels() {
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
}
