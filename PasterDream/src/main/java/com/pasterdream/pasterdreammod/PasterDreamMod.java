package com.pasterdream.pasterdreammod;

import com.pasterdream.pasterdreammod.command.PDCommands;
import com.pasterdream.pasterdreammod.data.PDBlockModelProvider;
import com.pasterdream.pasterdreammod.data.PDBlockTagProvider;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDCreativeTabs;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDEntityEvents;
import com.pasterdream.pasterdreammod.registry.PDFeatures;
import com.pasterdream.pasterdreammod.registry.PDFluids;
import com.pasterdream.pasterdreammod.registry.PDFluidsType;
import com.pasterdream.pasterdreammod.api.entity.EntityAPI;
import com.pasterdream.pasterdreammod.api.itemmigration.ItemMigrationAPI;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDMenus;
import com.pasterdream.pasterdreammod.registry.ModDecorations;
import com.pasterdream.pasterdreammod.registry.PDRuinsRegistration;
import com.pasterdream.pasterdreammod.api.ApiCodeGenConfig;
import com.pasterdream.pasterdreammod.api.ApiSoundRegistry;
import com.pasterdream.pasterdreammod.api.block.BlockAPI;
import com.pasterdream.pasterdreammod.api.curio.CurioAPI;
import com.pasterdream.pasterdreammod.api.effect.MobEffectAPI;
import com.pasterdream.pasterdreammod.api.ruin.RuinAPI;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.PDPotions;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.registry.PDStructures;
import com.pasterdream.pasterdreammod.worldgen.decor.DecorationRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Path;

/**
 * PasterDream 模组主类
 * 负责模组的初始化和事件总线管理
 */
@Mod(PasterDreamMod.MOD_ID)
public class PasterDreamMod {

    /**
     * 模组 ID 常量
     */
    public static final String MOD_ID = "pasterdream";

    /**
     * 模组日志记录器
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(PasterDreamMod.class);

    static {
        ApiCodeGenConfig.setDefaultBasePath("src/main/resources");
    }

    /**
     * 构造函数
     *
     * @param modEventBus NeoForge 事件总线
     * @param modContainer NeoForge 模组容器
     */
    public PasterDreamMod(IEventBus modEventBus, ModContainer modContainer) {
        // 注册方块
        PDBlocks.BLOCKS.register(modEventBus);

        // 注册物品
        PDItems.ITEMS.register(modEventBus);

        // 注册 API 物品移植注册器（由 ItemMigrationAPI 管理）
        ItemMigrationAPI.REGISTRY.register(modEventBus);

        // 注册方块实体
        PDBlockEntities.BLOCK_ENTITIES.register(modEventBus);

        // 注册实体类型
        PDEntities.ENTITY_TYPES.register(modEventBus);

        // 注册创造模式物品栏
        PDCreativeTabs.TABS.register(modEventBus);

        // 注册状态效果（BUFF/DEBUFF）
        // TODO: 过渡期双重注册 —— 下个主版本移除 PDEffects.MOB_EFFECTS，仅保留 MobEffectAPI.REGISTRY
        @SuppressWarnings({"deprecation", "removal"})
        DeferredRegister<MobEffect> _legacyEffects = PDEffects.MOB_EFFECTS;
        _legacyEffects.register(modEventBus);
        MobEffectAPI.REGISTRY.register(modEventBus);

        // 注册药水（可酿造）
        PDPotions.POTIONS.register(modEventBus);

        // 注册自定义声音事件（包括维度背景音乐）
        PDSounds.SOUND_EVENTS.register(modEventBus);

        // 注册 API 层面的维度背景音乐 SoundEvent
        ApiSoundRegistry.DIMENSION_SOUNDS.register(modEventBus);

        // 染梦维度的注册由 data/pasterdream/dimension/dyedream_world.json 数据驱动

        // 注册结构类型
        // TODO: 过渡期双重注册 —— 下个主版本移除 PDStructures.STRUCTURE_TYPES，仅保留 RuinAPI.REGISTRY
        @SuppressWarnings({"deprecation", "removal"})
        DeferredRegister<StructureType<?>> _legacyStructures = PDStructures.STRUCTURE_TYPES;
        _legacyStructures.register(modEventBus);
        RuinAPI.REGISTRY.register(modEventBus);

        // 注册染梦遗迹结构（染梦列车、巨型染梦树、粉红菇屋等）
        // 必须在构造器中注册，因为 RuinBuilder.build() 会向 DeferredRegister 添加新条目
        PDRuinsRegistration.register();

        // 注册菜单类型
        PDMenus.MENUS.register(modEventBus);

        // 注册粒子类型
        PDParticles.PARTICLE_TYPES.register(modEventBus);

        // 注册饰品（CurioAPI）
        CurioAPI.REGISTRY.register(modEventBus);

        // 注册自定义特征（如云朵团块生成器）
        PDFeatures.FEATURES.register(modEventBus);

        // 注册通用装饰物特征（WorldDecorationAPI）
        DecorationRegistry.FEATURES.register(modEventBus);

        // 注册流体类型
        PDFluidsType.FLUID_TYPES.register(modEventBus);

        // 注册流体
        PDFluids.FLUIDS.register(modEventBus);

        // 配置刷怪蛋模型自动生成输出目录
        // 所有通过 EntityAPI 注册了 .spawnEgg() 的实体，在 build() 时自动生成模型 JSON
        EntityAPI.setSpawnEggModelsOutputDir(
                Path.of("PasterDream", "src", "main", "resources", "assets",
                        PasterDreamMod.MOD_ID, "models", "item"));

        // 监听通用设置事件
        modEventBus.addListener(this::commonSetup);

        // 注册数据生成器（用于自动生成方块标签等资源文件）
        modEventBus.addListener(this::gatherData);

        // 在游戏总线上注册指令
        NeoForge.EVENT_BUS.addListener(PDCommands::register);

        // 客户端 Tick 事件和极光天幕渲染器通过 @EventBusSubscriber(Dist.CLIENT)
        // 在 PDClientEvents 和 DyeDreamSkyRenderer 中自动注册，避免服务端类加载
    }

    /**
     * 数据生成事件
     * 用于自动生成方块标签（mineable/axe 等），替代手动编写的 JSON 文件
     *
     * @param event 数据生成事件
     */
    private void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        var packOutput = generator.getPackOutput();
        var lookupProvider = event.getLookupProvider();
        var existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeServer(),
                new PDBlockTagProvider(packOutput, lookupProvider, existingFileHelper));

        generator.addProvider(event.includeClient(),
                new PDBlockModelProvider(packOutput, existingFileHelper));
    }

    /**
     * 通用设置阶段初始化
     *
     * @param event FML 通用设置事件
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("===== PasterDreamMod 地形生成系统初始化 =====");
        LOGGER.info("BiomeModifier 序列化器已注册: pasterdream:dyedream_features");

        // 注册 API 装饰物（冰刺、冰之门等）
        ModDecorations.register();

        // 如需同步 JSON 文件，取消注释下行（注意 commonSetup 阶段可能无法编码 BlockPredicate）：
        // ModDecorations.generateJson();

        // 输出预期的 BiomeModifier JSON 配置文件列表（用于测试时确认文件是否被正确加载）
        LOGGER.info("预期的 BiomeModifier JSON 文件列表:");
        LOGGER.info("  - neoforge/biome_modifier/dyedream_ores.json -> 注入矿石 (UNDERGROUND_ORES)");
        LOGGER.info("    ├ pasterdream:ore_amber_candy");
        LOGGER.info("    ├ pasterdream:ore_dyedreamdust");
        LOGGER.info("    └ pasterdream:ore_dyedreamquartz");
        LOGGER.info("  - neoforge/biome_modifier/dyedream_vegetation.json -> 注入树木与植被 (TOP_LAYER_MODIFICATION)");
        LOGGER.info("    ├ pasterdream:dyedream_trees");
        LOGGER.info("    ├ pasterdream:patch_dyedream_buds");
        LOGGER.info("    ├ pasterdream:patch_pinkagaric");
        LOGGER.info("    └ pasterdream:patch_dyedream_seagrass");
        LOGGER.info("目标生物群系标签: #pasterdream:is_dyedream");
        LOGGER.info("===== 地形生成系统初始化完成 =====");
    }
}
