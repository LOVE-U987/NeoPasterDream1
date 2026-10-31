package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.doll.DollConfig;
import com.pasterdream.pasterdreammod.api.doll.DollModelType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.pasterdream.pasterdreammod.api.PasterDreamAPI.LOGGER;

/** 玩偶内容默认值与资源生成，供通用玩偶 API 注入使用。 */
public final class DollResourceGenerator {
    private DollResourceGenerator() {
    }

    /**
     * 提供按名称派生的玩偶默认资源配置。
     * @param name 玩偶名称
     * @return 默认资源配置
     */
    public static DollConfig defaultConfig(String name) {
        return new DollConfig(name,
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "geo/block/" + name + ".geo.json"),
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/block/" + name + ".png"),
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "geo/block/" + name + "_holding.geo.json"),
                false, DollModelType.NEW);
    }

    /** @return 皮肤快捷入口默认配置 */
    public static DollConfig skinConfig() {
        return defaultConfig("eoul_doll");
    }

    /** @return 每次新建的玩偶默认方块属性 */
    public static BlockBehaviour.Properties defaultBlockProperties() {
        return BlockBehaviour.Properties.of().strength(1.0f).sound(SoundType.DECORATED_POT).noOcclusion();
    }

    /**
     * 为外部命名空间生成引用主模通用模型的资源文件。
     * @param namespace 命名空间
     * @param name 玩偶名称
     */
    public static void generateAssets(String namespace, String name) {
        Path assetsDir = FMLPaths.GAMEDIR.get().resolve("kubejs/assets").resolve(namespace);
        Path blockstatesDir = assetsDir.resolve("blockstates");
        Path modelsItemDir = assetsDir.resolve("models/item");

        try {
            Files.createDirectories(blockstatesDir);
            Files.createDirectories(modelsItemDir);

            String blockstateJson = """
                    {
                      "variants": {
                        "facing=north": { "model": "pasterdream:custom/doll_generic_particle" },
                        "facing=east": { "model": "pasterdream:custom/doll_generic_particle", "y": 90 },
                        "facing=south": { "model": "pasterdream:custom/doll_generic_particle", "y": 180 },
                        "facing=west": { "model": "pasterdream:custom/doll_generic_particle", "y": 270 }
                      }
                    }
                    """;
            Files.writeString(blockstatesDir.resolve(name + ".json"), blockstateJson);

            String itemModelJson = """
                    {
                      "parent": "pasterdream:displaysettings/doll_generic.item"
                    }
                    """;
            Files.writeString(modelsItemDir.resolve(name + ".json"), itemModelJson);

            LOGGER.info("[DollBuilder] 为 {}:{} 生成 KubeJS 资源文件", namespace, name);
        } catch (IOException e) {
            LOGGER.error("[DollBuilder] 为 {}:{} 生成 KubeJS 资源文件失败", namespace, name, e);
        }
    }
}
