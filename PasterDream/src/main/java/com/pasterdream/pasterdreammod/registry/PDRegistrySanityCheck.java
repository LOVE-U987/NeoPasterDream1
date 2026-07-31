package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 注册表人口断言（审查项 M8 落地）。
 * <p>
 * 主模构造器依靠「显式引用触发 static 填充 DeferredRegister」的模式注册分区条目，
 * 新分区（如新增 {@code PDItemsXxx}/{@code PDBlocksXxx} 聚合类）若忘记在
 * {@link PasterDreamMod} 构造器中显式引用，其条目会静默缺失且无任何报错。
 * 本类在 {@code FMLCommonSetupEvent}（注册阶段结束后）执行一次性断言：
 * <ul>
 *   <li>统计各注册类别下 {@code pasterdream:} 命名空间条目数；</li>
 *   <li>校验每个分区挑选的「锚点」条目确实存在于实际注册表；</li>
 *   <li>缺失 / 类别人口为零时输出 ERROR 日志并汇总，便于定位漏引用的分区类。</li>
 * </ul>
 * 仅输出日志断言，不中断启动（避免把开发期遗漏升级为玩家崩溃）。
 */
public final class PDRegistrySanityCheck {

    /** 锚点：类别注册表 + 分区代表条目名（对应各 registry 聚合分区类） */
    private record Anchor(Registry<?> registry, String name) {
    }

    private static final List<Anchor> ANCHORS = List.of(
            // 物品分区（PDItems.* 各聚合类）
            new Anchor(BuiltInRegistries.ITEM, "blackmetal_ingot"),        // PDItemsMaterials
            new Anchor(BuiltInRegistries.ITEM, "turnback_cloak"),          // PDItemsCurios
            new Anchor(BuiltInRegistries.ITEM, "qin_armor_helmet"),        // PDItemsArmor
            new Anchor(BuiltInRegistries.ITEM, "shadow_dungeon_portal"),   // PDItemsFurniture
            new Anchor(BuiltInRegistries.ITEM, "angel_block_item"),        // PDItemsFunctional
            new Anchor(BuiltInRegistries.ITEM, "shadow_dungeon_block_0"),  // PDItemsBlocks
            new Anchor(BuiltInRegistries.ITEM, "wind_knight_spawn_egg"),   // PDItemsSpawnEggs
            // 方块分区（PDBlocks.* 各聚合类）
            new Anchor(BuiltInRegistries.BLOCK, "shadow_light_0"),         // PDBlocksVegetation
            new Anchor(BuiltInRegistries.BLOCK, "shadow_dungeon_block_0"), // PDBlocksDungeon
            new Anchor(BuiltInRegistries.BLOCK, "shadow_dungeon_portal"),  // PDBlocksFurniture
            new Anchor(BuiltInRegistries.BLOCK, "angel_block"),            // PDBlocksWindJourney
            // 实体 / 效果 / 粒子 / 声音
            new Anchor(BuiltInRegistries.ENTITY_TYPE, "wind_knight"),
            new Anchor(BuiltInRegistries.ENTITY_TYPE, "shadow_npc_0"),
            new Anchor(BuiltInRegistries.MOB_EFFECT, "machine_wing_effect"),
            new Anchor(BuiltInRegistries.PARTICLE_TYPE, "feather_white_particle"),
            new Anchor(BuiltInRegistries.SOUND_EVENT, "cloak"));

    /** 主模必然填充的注册类别（人口为 0 说明该类别聚合类整体未触发加载） */
    private static final List<Registry<?>> EXPECTED_NON_EMPTY = List.of(
            BuiltInRegistries.ITEM,
            BuiltInRegistries.BLOCK,
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            BuiltInRegistries.ENTITY_TYPE,
            BuiltInRegistries.MOB_EFFECT,
            BuiltInRegistries.PARTICLE_TYPE,
            BuiltInRegistries.SOUND_EVENT,
            BuiltInRegistries.MENU,
            BuiltInRegistries.POTION,
            BuiltInRegistries.ATTRIBUTE);

    private PDRegistrySanityCheck() {
        throw new UnsupportedOperationException("PDRegistrySanityCheck 是工具类，不可实例化");
    }

    /**
     * 执行注册表人口断言（在 {@code FMLCommonSetupEvent} 中调用，此时注册已全部提交）。
     */
    public static void verify() {
        List<String> failures = new ArrayList<>();
        Map<String, Long> population = new LinkedHashMap<>();

        // 1) 分类别统计 pasterdream: 条目人口
        for (Registry<?> registry : EXPECTED_NON_EMPTY) {
            long count = registry.keySet().stream()
                    .filter(rl -> PasterDreamMod.MOD_ID.equals(rl.getNamespace()))
                    .count();
            population.put(registry.key().location().toString(), count);
            if (count == 0) {
                failures.add("类别 " + registry.key().location() + " 的 pasterdream: 条目为 0 ——"
                        + "对应注册聚合类可能未被 PasterDreamMod 构造器显式引用触发初始化");
            }
        }

        // 2) 校验各分区锚点条目
        for (Anchor anchor : ANCHORS) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, anchor.name());
            if (!anchor.registry().containsKey(id)) {
                failures.add("锚点条目缺失 " + anchor.registry().key().location() + ":" + anchor.name()
                        + " —— 对应分区类未注册（新分区漏在 PasterDreamMod 构造器显式引用？）");
            }
        }

        // 3) 输出结果
        StringBuilder sb = new StringBuilder("[PDRegistrySanityCheck] 注册表人口统计:\n");
        population.forEach((k, v) -> sb.append("  - ").append(k).append(" = ").append(v).append("\n"));
        if (failures.isEmpty()) {
            PDDebugLogger.mainInfo(sb + "[PDRegistrySanityCheck] RESULT: 全部锚点存在，人口正常");
        } else {
            sb.append("[PDRegistrySanityCheck] FAILURES (").append(failures.size()).append("):\n");
            for (String f : failures) {
                sb.append("  - ").append(f).append("\n");
            }
            PasterDreamMod.LOGGER.error(sb.toString());
        }
    }
}
