package com.pasterdream.pasterdreammod;

import com.pasterdream.pasterdreammod.attachment.PDAttachments;
import com.pasterdream.pasterdreammod.attachment.PlayerDataEvents;
import com.pasterdream.pasterdreammod.command.PDCommands;
import com.pasterdream.pasterdreammod.config.PDClientConfig;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import com.pasterdream.pasterdreammod.data.PDBlockModelProvider;
import com.pasterdream.pasterdreammod.data.PDBlockTagProvider;
import com.pasterdream.pasterdreammod.network.PDNetwork;
import com.pasterdream.pasterdreammod.registry.PDAttributes;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.registry.PDBlockEntitiesFurniture;
import com.pasterdream.pasterdreammod.registry.PDGameRules;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDArenaEvents;
import com.pasterdream.pasterdreammod.registry.LampShadowEvents;
import com.pasterdream.pasterdreammod.registry.PDCreativeTabs;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDRecipeTypes;
import com.pasterdream.pasterdreammod.registry.PDEntityEvents;
import com.pasterdream.pasterdreammod.registry.PDFeatures;
import com.pasterdream.pasterdreammod.registry.PDFluids;
import com.pasterdream.pasterdreammod.registry.PDFluidsType;
import com.pasterdream.pasterdreammod.api.entity.EntityAPI;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDArmorMaterials;
import com.pasterdream.pasterdreammod.registry.PDMenus;
import com.pasterdream.pasterdreammod.registry.PDMenusFurniture;
import com.pasterdream.pasterdreammod.registry.ModDecorations;
import com.pasterdream.pasterdreammod.registry.PDRuinsRegistration;
import com.pasterdream.pasterdreammod.api.ApiCodeGenConfig;
import com.pasterdream.pasterdreammod.entity.damage.EntityImmunitySetup;

import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.PDPotions;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.registry.PDTreeDecorators;
import com.pasterdream.pasterdreammod.registry.PDWorldgenRegistries;
import com.pasterdream.pasterdreammod.worldgen.tree.DyedreamTreePlacers;
import com.pasterdream.pasterdreammod.api.worldgen.decor.DecorationRegistry;
import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import net.minecraft.data.DataGenerator;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
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
        ApiCodeGenConfig.setDefaultBasePath(ApiCodeGenConfig.DEFAULT_BASE_PATH);
    }

    /**
     * 构造函数
     *
     * @param modEventBus NeoForge 事件总线
     * @param modContainer NeoForge 模组容器
     */
    public PasterDreamMod(IEventBus modEventBus, ModContainer modContainer) {
        // 统一注册 PasterDreamAPI 模块下所有 DeferredRegister
        PasterDreamAPI.registerAll(modEventBus);

        // 注册 ServerScheduler（已上收至 PasterDreamAPI）
        ServerScheduler.register(NeoForge.EVENT_BUS);

        // 显式引用 DecorationRegistry 以触发类初始化，确保 generic_decor 特征在注册事件前填充到 FEATURES
        // DecorationRegistry.FEATURES 已由 PasterDreamAPI.registerAll() 统一注册，此处避免重复注册
        Object unusedDecorationRegistry = DecorationRegistry.FEATURES;

        // 显式引用 PDBlocks 的静态字段以触发类初始化，确保方块静态字段填充到 BlockAPI.REGISTRY
        // BlockAPI.REGISTRY 已由 PasterDreamAPI.registerAll() 统一注册，此处避免重复注册
        Object unusedBlocks = PDBlocks.BLOCKS;

        // 初始化 PDItems 门面时会同步加载笔记分区；禁止先 bootstrap 分区，
        // 否则 PDItems 的 DREAMNOTES_* re-export 会在分区初始化途中读到 null。
        PDItems.ITEMS.register(modEventBus);
        com.pasterdream.pasterdreammod.registry.PDMenusDreamnotes.bootstrap();
        com.pasterdream.pasterdreammod.data.BluePrintLoader.bootstrap();

        // 注册盔甲材料
        PDArmorMaterials.ARMOR_MATERIALS.register(modEventBus);

        // 显式引用 PDBlockEntities 的静态字段以触发类初始化，确保方块实体静态字段填充到 BlockEntityAPI.REGISTRY
        // BlockEntityAPI.REGISTRY 已由 PasterDreamAPI.registerAll() 统一注册，此处避免重复注册
        Object unusedBlockEntity = PDBlockEntities.AARONCOS_HAND_SPAWN_BLOCK;
        // [分区F] 触发 W4 家具/结构方块实体类型在注册事件前完成填充
        Object unusedFurnitureBe = PDBlockEntitiesFurniture.PICNIC_BASKET;

        // 显式引用 PDEntities 的静态字段以触发类初始化，确保实体静态字段填充到 EntityAPI.REGISTRY
        // EntityAPI.REGISTRY 已由 PasterDreamAPI.registerAll() 统一注册，此处避免重复注册
        Object unusedEntityTypes = PDEntities.ENTITY_TYPES;

        // 注册创造模式物品栏
        PDCreativeTabs.TABS.register(modEventBus);

        // 显式引用 PDEffects 的静态字段以触发类初始化，确保所有效果在 RegisterEvent 触发前完成注册
        // PDPotions 的静态字段会引用 PDEffects 的效果，需要提前初始化
        Object unusedDreamwishBuff = PDEffects.DREAMWISH_BUFF;

        // 注册药水（可酿造）
        PDPotions.POTIONS.register(modEventBus);

        // 注册自定义声音事件（包括维度背景音乐）
        PDSounds.SOUND_EVENTS.register(modEventBus);

        // 染梦维度的注册由 data/pasterdream/dimension/dyedream_world.json 数据驱动

        // 注册染梦遗迹结构（染梦列车、巨型染梦树、粉红菇屋等）
        // 必须在构造器中注册，因为 RuinBuilder.build() 会向 DeferredRegister 添加新条目
        PDRuinsRegistration.register();

        // 显式引用 PDMenus 的静态字段以触发类初始化，确保菜单静态字段填充到 MenuAPI.REGISTRY
        // MenuAPI.REGISTRY 已由 PasterDreamAPI.registerAll() 统一注册，此处避免重复注册
        Object unusedShadowChest = PDMenus.SHADOW_CHEST;
        // [分区F] 触发野餐篮/阴影书桌/风泊板条筐菜单注册
        Object unusedFurnitureMenu = PDMenusFurniture.PICNIC_BASKET;

        // 触发 PDParticles 类加载，确保粒子类型静态字段填充到 ParticleAPI.REGISTRY
        // ParticleAPI.REGISTRY 已由 PasterDreamAPI.registerAll() 统一注册，此处避免重复注册
        PDParticles.register();

        // 注册自定义特征（如云朵团块生成器）
        PDFeatures.FEATURES.register(modEventBus);

        // 注册树装饰器类型（染梦树 0/1/2 的树干/树叶装饰器，供 biome_dyedream_*_tree.json 引用）
        // 缺失会导致数据包 registry 加载失败、无法创建世界（P0）
        PDTreeDecorators.TREE_DECORATOR_TYPES.register(modEventBus);

        // 注册上游染梦自定义 Trunk/Foliage/Decorator Placer（供 dyedream_tree*.json 引用）
        com.pasterdream.pasterdreammod.api.worldgen.decor.TreePlacerAPI.registerAll(
                modEventBus,
                DyedreamTreePlacers.TRUNK_PLACERS,
                DyedreamTreePlacers.FOLIAGE_PLACERS,
                DyedreamTreePlacers.TREE_DECORATORS);

        // 注册自定义 ChunkGenerator 和 BiomeSource 类型（供维度 JSON 引用）
        PDWorldgenRegistries.CHUNK_GENERATORS.register(modEventBus);
        PDWorldgenRegistries.BIOME_SOURCES.register(modEventBus);

        // BiomeModifier 序列化器（dyedream_features + 历史 wind_lake_verify codec 兼容）
        com.pasterdream.pasterdreammod.worldgen.PDBiomeModifiers.BIOME_MODIFIER_SERIALIZERS.register(modEventBus);

        // 显式引用流体 Type / Fluid 静态字段以触发类初始化，填充 FluidTypeAPI / FluidAPI
        // 二者 DeferredRegister 已由 PasterDreamAPI.registerAll() 统一挂总线
        Object unusedMeltdreamType = PDFluidsType.MELTDREAM_LIQUID_TYPE;
        Object unusedMeltdreamLiquid = PDFluids.MELTDREAM_LIQUID;

        // 配置刷怪蛋模型自动生成输出目录
        // 所有通过 EntityAPI 注册了 .spawnEgg() 的实体，在 build() 时自动生成模型 JSON
        EntityAPI.setSpawnEggModelsOutputDir(
                Path.of("PasterDream", "src", "main", "resources", "assets",
                        PasterDreamMod.MOD_ID, "models", "item"));

        // ==================== 玩家数据层（属性 / 变量 / 网络 / 规则 / 配置） ====================

        // 注册自定义玩家属性（战技冷却、瞬身术系列、理智光环等 10 项，还原自原版 PasterdreamModAttributes）
        PDAttributes.ATTRIBUTES.register(modEventBus);
        // 将玩家属性挂接到 EntityType.PLAYER（MOD 总线 EntityAttributeModificationEvent）
        modEventBus.addListener(PDAttributes::addPlayerAttributes);

        // 注册玩家数据附件（San 理智值 / 融梦能量），以 AttachmentType 替代原版 Forge Capability
        PDAttachments.ATTACHMENT_TYPES.register(modEventBus);

        // 注册网络包（玩家变量 S2C 同步 + 瞬身术/斗篷按键 C2S 消息）
        modEventBus.addListener(PDNetwork::registerPayloads);

        // 注册自定义游戏规则（randomCoordX/Z、调试模式、风向、San 系列，共 7 项）
        PDGameRules.register();

        // [分区R] 注册自定义配方类型与序列化器（暗影高炉 shadow_blasting 数据包配方）
        PDRecipeTypes.register(modEventBus);

        // 注册配置文件（文件名与原版一致：PasterDream-Client.toml / PasterDream-Common.toml）
        modContainer.registerConfig(ModConfig.Type.CLIENT, PDClientConfig.SPEC, "PasterDream-Client.toml");
        modContainer.registerConfig(ModConfig.Type.COMMON, PDCommonConfig.SPEC, "PasterDream-Common.toml");

        // 游戏总线：玩家登录/重生/跨维度/克隆时维护并全量同步玩家数据（对照原版 Capability 生命周期）
        NeoForge.EVENT_BUS.addListener(PlayerDataEvents::onPlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(PlayerDataEvents::onPlayerRespawn);
        NeoForge.EVENT_BUS.addListener(PlayerDataEvents::onPlayerChangedDimension);
        NeoForge.EVENT_BUS.addListener(PlayerDataEvents::onPlayerClone);

        // 理智环境 tick（SanHelper：含风维 cloudmist 续效）
        NeoForge.EVENT_BUS.addListener(com.pasterdream.pasterdreammod.world.PDSanHelper::onPlayerTick);
        // 风之旅途：日更风向 / 顺逆风 / 进维文案与主题曲
        NeoForge.EVENT_BUS.addListener(com.pasterdream.pasterdreammod.world.WindJourneyEvents::onLevelTick);
        NeoForge.EVENT_BUS.addListener(com.pasterdream.pasterdreammod.world.WindJourneyEvents::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(com.pasterdream.pasterdreammod.world.WindJourneyEvents::onPlayerChangedDimension);

        // 监听通用设置事件
        modEventBus.addListener(this::commonSetup);

        // 注册数据生成器（用于自动生成方块标签等资源文件）
        modEventBus.addListener(this::gatherData);

        // 在游戏总线上注册指令
        NeoForge.EVENT_BUS.addListener(PDCommands::register);

        // 在游戏总线上注册竞技场维度事件（玩家进入竞技场时的初始化逻辑）
        NeoForge.EVENT_BUS.addListener(PDArenaEvents::onPlayerChangedDimension);

        // 灯影进出：title / 配置赠针 / 离维窥视 buff（LampShadowPr0/Pr1）
        NeoForge.EVENT_BUS.addListener(LampShadowEvents::onPlayerChangedDimension);

        // 灯影出生结构（shadow_world_spawn）；Warden→hide_7 / 远古守卫者鳞
        NeoForge.EVENT_BUS.addListener(com.pasterdream.pasterdreammod.world.PDLampShadowWorldgen::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(com.pasterdream.pasterdreammod.world.PDEntityDeathEvents::onLivingDeath);

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
        LOGGER.debug("===== PasterDreamMod 地形生成系统初始化 =====");
        LOGGER.debug("BiomeModifier 序列化器: pasterdream:dyedream_features, pasterdream:wind_lake_verify(compat no-op)");

        // 注册 API 装饰物（冰刺、冰之门等）
        ModDecorations.register();

        // 配置所有实体的伤害免疫规则（替代原先散布在 27 个实体类中的重复 hurt() 逻辑）
        EntityImmunitySetup.setupAllImmunities();

        // 注意：commonSetup 阶段调用 ModDecorations.generateJson() 可能无法正确编码 BlockPredicate，
        // 如需同步 JSON 文件请在 data 生成阶段或独立任务中执行。

        // 输出预期的 BiomeModifier JSON 配置文件列表（用于测试时确认文件是否被正确加载）
        LOGGER.debug("预期的 BiomeModifier JSON 文件列表:");
        LOGGER.debug("  - neoforge/biome_modifier/dyedream_ores.json -> 注入矿石 (UNDERGROUND_ORES)");
        LOGGER.debug("    ├ pasterdream:ore_amber_candy");
        LOGGER.debug("    ├ pasterdream:ore_dyedreamdust");
        LOGGER.debug("    └ pasterdream:ore_dyedreamquartz");
        LOGGER.debug("  - neoforge/biome_modifier/dyedream_vegetation.json -> 注入树木与植被 (TOP_LAYER_MODIFICATION)");
        LOGGER.debug("    ├ pasterdream:dyedream_trees");
        LOGGER.debug("    ├ pasterdream:patch_dyedream_buds");
        LOGGER.debug("    ├ pasterdream:patch_pinkagaric");
        LOGGER.debug("    └ pasterdream:patch_dyedream_seagrass");
        LOGGER.debug("目标生物群系标签: #pasterdream:is_dyedream");
        LOGGER.debug("===== 地形生成系统初始化完成 =====");
    }
}
