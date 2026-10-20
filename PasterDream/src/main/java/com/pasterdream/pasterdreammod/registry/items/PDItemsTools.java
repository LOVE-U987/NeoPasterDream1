package com.pasterdream.pasterdreammod.registry.items;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.curio.CurioAPI;
import com.pasterdream.pasterdreammod.api.curio.model.CurioSlot;
import com.pasterdream.pasterdreammod.api.effect.MobEffectAPI;
import com.pasterdream.pasterdreammod.api.entity.EntityAPI;
import com.pasterdream.pasterdreammod.api.item.ItemAPI;
import com.pasterdream.pasterdreammod.api.item.model.MigrationCategory;
import com.pasterdream.pasterdreammod.api.item.model.ToolSpec.ToolType;
import com.pasterdream.pasterdreammod.item.*;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.food.FoodProperties;
import net.neoforged.neoforge.registries.DeferredItem;


/**
 * 工具与武器注册。
 *
 * @see PDItems
 */
public class PDItemsTools {


    // ==================== 剑类武器 ====================

    public static final DeferredItem<Item> BROKEN_HERO_SWORD =
            ItemAPI.toolItem("broken_hero_sword")
                    .type(ToolType.SWORD).durability(100)
                    .attackDamage(6.0f).attackSpeed(-2.4f)
                    .enchantment(0)
                    .build();
    public static final DeferredItem<Item> COPPER_SWORD =
            ItemAPI.toolItem("copper_sword")
                    .type(ToolType.SWORD).durability(225)
                    .attackDamage(4.5f).attackSpeed(-2.4f)
                    .enchantment(12)
                    .repairWith(new ItemStack(Items.COPPER_INGOT))
                    .build();
    /**
     * 创造调试剑 (creative_sword) — 强制击杀清实体
     * 改用自定义 {@link CreativeSwordItem}（属性与原 ItemAPI 注册逐项一致，按原版补 fireResistant），
     * 补上命中 1 亿虚空伤害 + 1 tick 后清除目标实体的调试行为
     */
    public static final DeferredItem<Item> CREATIVE_SWORD =
            PDItems.ITEMS.register("creative_sword",
                    () -> new CreativeSwordItem(new Item.Properties()));
    /**
     * 沙漠剑 (desert_sword) — 代价型被动
     * 改用自定义 {@link DesertSwordItem}（属性与原 ItemAPI 注册逐项一致），
     * 补上手持缓速 -15% / 抗性 +20% 的代价型被动（高攻速高伤的平衡代价）
     */
    public static final DeferredItem<Item> DESERT_SWORD =
            PDItems.ITEMS.register("desert_sword",
                    () -> new DesertSwordItem(new Item.Properties()));
    /**
     * 极锋染梦合金剑 (dyedream_sword_0) — W2-D 战技接入
     * 改用自定义 {@link DyedreamSword0Item}（属性与原 ItemAPI 注册逐项一致），
     * 补上战技"凌空斩"：右键强化下次攻击，命中额外伤害并上击飞，
     * 染梦守护效果可缩短战技冷却
     */
    public static final DeferredItem<Item> DYEDREAM_SWORD_0 =
            PDItems.ITEMS.register("dyedream_sword_0",
                    () -> new DyedreamSword0Item(new Item.Properties()));
    public static final DeferredItem<Item> DYEDREAM_SWORD =
            ItemAPI.toolItem("dyedream_sword")
                    .type(ToolType.SWORD).durability(1314)
                    .attackDamage(7.0f).attackSpeed(-2.4f)
                    .enchantment(22)
                    .build();
    /**
     * 草薙 (grass_sword) — 中毒被动 + 击杀成长
     * 改用自定义 {@link GrassSwordItem}（属性与原 ItemAPI 注册逐项一致），
     * 补上攻击施加中毒（3 秒）与击杀 200 只生物自动成长为『天丛云』草薙的机制
     */
    public static final DeferredItem<Item> GRASS_SWORD =
            PDItems.ITEMS.register("grass_sword",
                    () -> new GrassSwordItem(new Item.Properties()));
    public static final DeferredItem<Item> ICESHADOW_HAMMER =
            ItemAPI.toolItem("iceshadow_hammer")
                    .type(ToolType.SWORD).durability(835)
                    .attackDamage(12.0f).attackSpeed(-3.3f)
                    .enchantment(2)
                    .build();
    /**
     * 熔金剑 (moltengold_sword) — 引燃被动
     * 改用自定义 {@link MoltengoldSwordItem}（属性与原 ItemAPI 注册逐项一致，按原版补 fireResistant），
     * 补上攻击引燃 3 秒的被动
     */
    public static final DeferredItem<Item> MOLTENGOLD_SWORD =
            PDItems.ITEMS.register("moltengold_sword",
                    () -> new MoltengoldSwordItem(new Item.Properties()));
    public static final DeferredItem<Item> SHADOW_EROSION_SWORD =
            ItemAPI.toolItem("shadow_erosion_sword")
                    .type(ToolType.SWORD).durability(1725)
                    .attackDamage(5.5f).attackSpeed(-1.0f)
                    .enchantment(2)
                    .build();
    /**
     * 暗影剑 (shadow_sword) — W2-D 战技接入：精神值联动
     * 改用自定义 {@link ShadowSwordItem}（属性与原 ItemAPI 注册逐项一致，按原版补 fireResistant），
     * 补上精神值联动属性（精神越低攻速/攻击越高）、SAN_VARIABILITY -3.6、
     * 挥砍"暗影之才"成就门控（未达成反噬）与精神值耗尽魔法反噬
     */
    public static final DeferredItem<Item> SHADOW_SWORD =
            PDItems.ITEMS.register("shadow_sword",
                    () -> new ShadowSwordItem(new Item.Properties()));
    /**
     * 大地之刃 (terra_sword) — W2-D 战技接入
     * 改用自定义 {@link TerraSwordItem}（属性与原 ItemAPI 注册逐项一致），
     * 补上泰拉剑技：右键开启后 3 次挥剑各挥出一道大地之刃剑气
     */
    public static final DeferredItem<Item> TERRA_SWORD =
            PDItems.ITEMS.register("terra_sword",
                    () -> new TerraSwordItem(new Item.Properties()));
    /**
     * 温热匕首 (thermal_dagger) — 命中音效演出
     * 改用自定义 {@link ThermalDaggerItem}（属性与原 ItemAPI 注册逐项一致，按原版补 fireResistant），
     * 补上命中 thermal_dagger 音效 + 气泡粒子的演出
     */
    public static final DeferredItem<Item> THERMAL_DAGGER =
            PDItems.ITEMS.register("thermal_dagger",
                    () -> new ThermalDaggerItem(new Item.Properties()));
    /**
     * 潮汐剑 (tide_sword) — 水下呼吸被动
     * 改用自定义 {@link TideSwordItem}（属性与原 ItemAPI 注册逐项一致），
     * 补上手持水下呼吸的被动；修复材料还原为原版海晶晶粒
     */
    public static final DeferredItem<Item> TIDE_SWORD =
            PDItems.ITEMS.register("tide_sword",
                    () -> new TideSwordItem(new Item.Properties()));
    public static final DeferredItem<Item> TITANIUM_SWORD =
            ItemAPI.toolItem("titanium_sword")
                    .type(ToolType.SWORD).durability(1721)
                    .attackDamage(6.5f).attackSpeed(-2.4f)
                    .enchantment(17)
                    .build();
    /**
     * 真沙漠剑 (true_desert_sword) — W2-D 战技接入
     * 改用自定义 {@link TrueDesertSwordItem}（属性与原 ItemAPI 注册逐项一致），
     * 补上战技"绝地反击"：按已损失生命比例加伤+短暂吸收，被动与沙漠剑共用（缓速+抗性）
     */
    public static final DeferredItem<Item> TRUE_DESERT_SWORD =
            PDItems.ITEMS.register("true_desert_sword",
                    () -> new TrueDesertSwordItem(new Item.Properties()));
    /**
     * 『天丛云』草薙 (true_grass_sword) — 更长中毒被动 + 战技"极致锋利"
     * 改用自定义 {@link TrueGrassSwordItem}（属性与原 ItemAPI 注册逐项一致），
     * 补上攻击施加更长时间的中毒（5 秒）与右键战技"极致锋利"（首次命中造成额外伤害）
     */
    public static final DeferredItem<Item> TRUE_GRASS_SWORD =
            PDItems.ITEMS.register("true_grass_sword",
                    () -> new TrueGrassSwordItem(new Item.Properties()));
    /**
     * 真熔金剑 (true_moltengold_sword) — 可叠加引燃被动
     * 改用自定义 {@link TrueMoltengoldSwordItem}（属性与原 ItemAPI 注册逐项一致，按原版补 fireResistant），
     * 补上可无限叠加的引燃（剩余引燃时间上 +2 秒起叠）
     */
    public static final DeferredItem<Item> TRUE_MOLTENGOLD_SWORD =
            PDItems.ITEMS.register("true_moltengold_sword",
                    () -> new TrueMoltengoldSwordItem(new Item.Properties()));
    /**
     * 真潮汐剑 (true_tide_sword) — W2-D 战技接入
     * 改用自定义 {@link TrueTideSwordItem}（属性与原 ItemAPI 注册逐项一致），
     * 补上战技"涌流剑技"：水中冲刺+免疫 80% 伤害、水环境条件伤害，被动水下呼吸
     */
    public static final DeferredItem<Item> TRUE_TIDE_SWORD =
            PDItems.ITEMS.register("true_tide_sword",
                    () -> new TrueTideSwordItem(new Item.Properties()));
    /**
     * 至真熔金剑 (truest_moltengold_sword) — W2-D 战技接入
     * 改用自定义 {@link TruestMoltengoldSwordItem}（属性与原 ItemAPI 注册逐项一致，按原版补 fireResistant），
     * 补上战技"融骸斩击"：熔岩类型伤害（含引燃时长加成）+缓慢+清火，被动叠加引燃（+3 秒）
     */
    public static final DeferredItem<Item> TRUEST_MOLTENGOLD_SWORD =
            PDItems.ITEMS.register("truest_moltengold_sword",
                    () -> new TruestMoltengoldSwordItem(new Item.Properties()));
    /**
     * 白色灾厄 (white_sword) — W2-D 战技接入
     * 改用自定义 {@link WhiteSwordItem}（属性与原 ItemAPI 注册逐项一致，按原版补 fireResistant），
     * 补上白厄剑雨：右键在视点落点召唤 8 轮下落光剑
     */
    public static final DeferredItem<Item> WHITE_SWORD =
            PDItems.ITEMS.register("white_sword",
                    () -> new WhiteSwordItem(new Item.Properties()));


    // ==================== 镐类/锤类工具 ====================

    public static final DeferredItem<Item> COPPER_PICKAXE =
            ItemAPI.toolItem("copper_pickaxe")
                    .type(ToolType.PICKAXE).durability(225).miningSpeed(4.0f)
                    .attackDamage(2.5f).attackSpeed(-2.8f)
                    .enchantment(12)
                    .incorrectTag("minecraft:incorrect_for_stone_tool")
                    .repairWith(new ItemStack(Items.COPPER_INGOT))
                    .build();
    public static final DeferredItem<Item> DYEDREAM_HAMMER =
            ItemAPI.toolItem("dyedream_hammer")
                    .type(ToolType.HAMMER).durability(6570).miningSpeed(3.0f)
                    .attackDamage(10.0f).attackSpeed(-3.3f)
                    .enchantment(22)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(() -> new ItemStack(PDItems.DYEDREAM_INGOT.get()))
                    .build();
    public static final DeferredItem<Item> DYEDREAM_PICKAXE =
            ItemAPI.toolItem("dyedream_pickaxe")
                    .type(ToolType.PICKAXE).durability(1314).miningSpeed(11.0f)
                    .attackDamage(5.0f).attackSpeed(-2.8f)
                    .enchantment(22)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(() -> new ItemStack(PDItems.DYEDREAM_INGOT.get()))
                    .build();
    /**
     * 融梦水晶镐 (meltdream_pickaxe) — 融梦修补
     * 改用自定义 {@link MeltdreamPickaxeItem}（属性与原 ItemAPI 注册逐项一致），
     * 补上原版融梦修补：手持时每 10 tick 消耗 0.01 融梦能量修复 1 耐久；
     * 铁砧修复材料为融梦水晶碎片（原版行为，取代铁锭）
     */
    public static final DeferredItem<Item> MELTDREAM_PICKAXE =
            PDItems.ITEMS.register("meltdream_pickaxe",
                    () -> new MeltdreamPickaxeItem(new Item.Properties()));
    /**
     * 熔金镐 (moltengold_pickaxe) — 右键急迫
     * 改用自定义 {@link MoltengoldPickaxeItem}（属性与原 ItemAPI 注册逐项一致，按原版补 fireResistant），
     * 补上右键消耗饱食度换取 20 秒急迫（冷却 10 秒）
     */
    public static final DeferredItem<Item> MOLTENGOLD_PICKAXE =
            PDItems.ITEMS.register("moltengold_pickaxe",
                    () -> new MoltengoldPickaxeItem(new Item.Properties()));
    public static final DeferredItem<Item> SHADOW_EROSION_PICKAXE =
            ItemAPI.toolItem("shadow_erosion_pickaxe")
                    .type(ToolType.PICKAXE).durability(1725).miningSpeed(13.0f)
                    .attackDamage(5.0f).attackSpeed(-2.8f)
                    .enchantment(16)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(() -> new ItemStack(PDItems.BLACKMETAL_INGOT.get()))
                    .build();
    public static final DeferredItem<Item> TITANIUM_PICKAXE =
            ItemAPI.toolItem("titanium_pickaxe")
                    .type(ToolType.PICKAXE).durability(1721).miningSpeed(9.0f)
                    .attackDamage(4.5f).attackSpeed(-2.8f)
                    .enchantment(17)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(() -> new ItemStack(PDItems.TITANIUM_INGOT.get()))
                    .build();

    /**
     * 钛斧 (titanium_axe)
     * 使用 API ToolItemBuilder 注册
     * 原模组：耐久 1721，速度 9.0，伤害+9，攻速 -3.0
     */
    public static final DeferredItem<Item> TITANIUM_AXE =
            ItemAPI.toolItem("titanium_axe")
                    .type(ToolType.AXE)
                    .durability(1721).miningSpeed(9.0f)
                    .attackDamage(9.0f).attackSpeed(-3.0f)
                    .enchantment(17)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(() -> new ItemStack(PDItems.TITANIUM_INGOT.get()))
                    .build();

    /**
     * 钛锹 (titanium_shovel)
     * 使用 API ToolItemBuilder 注册
     * 原模组：耐久 1721，速度 9.0，伤害+5.5，攻速 -3.0
     */
    public static final DeferredItem<Item> TITANIUM_SHOVEL =
            ItemAPI.toolItem("titanium_shovel")
                    .type(ToolType.SHOVEL)
                    .durability(1721).miningSpeed(9.0f)
                    .attackDamage(5.5f).attackSpeed(-3.0f)
                    .enchantment(17)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(() -> new ItemStack(PDItems.TITANIUM_INGOT.get()))
                    .build();

    /**
     * 钛锄 (titanium_hoe)
     * 使用 API ToolItemBuilder 注册
     * 原模组：耐久 1721，速度 9.0，伤害+0.5，攻速 0.0
     */
    public static final DeferredItem<Item> TITANIUM_HOE =
            ItemAPI.toolItem("titanium_hoe")
                    .type(ToolType.HOE)
                    .durability(1721).miningSpeed(9.0f)
                    .attackDamage(0.5f).attackSpeed(0.0f)
                    .enchantment(17)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(() -> new ItemStack(PDItems.TITANIUM_INGOT.get()))
                    .build();

    /**
     * 真熔金镐 (true_moltengold_pickaxe) — 右键急迫（强化版）
     * 改用自定义 {@link TrueMoltengoldPickaxeItem}（属性与原 ItemAPI 注册逐项一致，按原版补 fireResistant），
     * 补上右键消耗饱食度换取 30 秒急迫（冷却仅 8 秒）
     */
    public static final DeferredItem<Item> TRUE_MOLTENGOLD_PICKAXE =
            PDItems.ITEMS.register("true_moltengold_pickaxe",
                    () -> new TrueMoltengoldPickaxeItem(new Item.Properties()));

    // === Dyedream 工具 ===
    public static final DeferredItem<Item> DYEDREAM_AXE =
            ItemAPI.toolItem("dyedream_axe")
                    .type(ToolType.AXE).durability(1314).miningSpeed(11.0f)
                    .attackDamage(9.5f).attackSpeed(-3.0f)
                    .enchantment(22)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(() -> new ItemStack(PDItems.DYEDREAM_INGOT.get()))
                    .build();
    public static final DeferredItem<Item> DYEDREAM_SHOVEL =
            ItemAPI.toolItem("dyedream_shovel")
                    .type(ToolType.SHOVEL).durability(1314).miningSpeed(11.0f)
                    .attackDamage(5.5f).attackSpeed(-3.0f)
                    .enchantment(22)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(() -> new ItemStack(PDItems.DYEDREAM_INGOT.get()))
                    .build();
    public static final DeferredItem<Item> DYEDREAM_HOE =
            ItemAPI.toolItem("dyedream_hoe")
                    .type(ToolType.HOE).durability(1314).miningSpeed(11.0f)
                    .attackDamage(1.0f).attackSpeed(0.0f)
                    .enchantment(22)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(() -> new ItemStack(PDItems.DYEDREAM_INGOT.get()))
                    .build();

    // === Moltengold 工具 ===
    /**
     * 熔金斧 (moltengold_axe) — 右键急迫
     * 改用自定义 {@link MoltengoldAxeItem}（属性与原 ItemAPI 注册逐项一致，按原版补 fireResistant），
     * 右键行为与熔金镐共用（消耗饱食度换取 20 秒急迫）
     */
    public static final DeferredItem<Item> MOLTENGOLD_AXE =
            PDItems.ITEMS.register("moltengold_axe",
                    () -> new MoltengoldAxeItem(new Item.Properties()));
    /**
     * 熔金锹 (moltengold_shovel) — 右键急迫
     * 改用自定义 {@link MoltengoldShovelItem}（属性与原 ItemAPI 注册逐项一致，按原版补 fireResistant），
     * 右键行为与熔金镐共用（消耗饱食度换取 20 秒急迫）
     */
    public static final DeferredItem<Item> MOLTENGOLD_SHOVEL =
            PDItems.ITEMS.register("moltengold_shovel",
                    () -> new MoltengoldShovelItem(new Item.Properties()));
    /**
     * 熔金锄 (moltengold_hoe) — 右键急迫
     * 改用自定义 {@link MoltengoldHoeItem}（属性与原 ItemAPI 注册逐项一致，按原版补 fireResistant），
     * 右键行为与熔金镐共用（消耗饱食度换取 20 秒急迫）
     */
    public static final DeferredItem<Item> MOLTENGOLD_HOE =
            PDItems.ITEMS.register("moltengold_hoe",
                    () -> new MoltengoldHoeItem(new Item.Properties()));

    // === Meltdream 工具（融梦修补：手持消耗融梦能量自动修复）===
    /**
     * 融梦水晶斧 (meltdream_axe) — 融梦修补
     * 改用自定义 {@link MeltdreamAxeItem}（属性与原 ItemAPI 注册逐项一致），
     * 补上原版融梦修补：手持时每 10 tick 消耗 0.01 融梦能量修复 1 耐久；
     * 铁砧修复材料为融梦水晶碎片（原版行为，取代铁锭）
     */
    public static final DeferredItem<Item> MELTDREAM_AXE =
            PDItems.ITEMS.register("meltdream_axe",
                    () -> new MeltdreamAxeItem(new Item.Properties()));
    /**
     * 融梦水晶锹 (meltdream_shovel) — 融梦修补
     * 改用自定义 {@link MeltdreamShovelItem}（属性与原 ItemAPI 注册逐项一致），
     * 补上原版融梦修补：手持时每 10 tick 消耗 0.01 融梦能量修复 1 耐久；
     * 铁砧修复材料为融梦水晶碎片（原版行为，取代铁锭）
     */
    public static final DeferredItem<Item> MELTDREAM_SHOVEL =
            PDItems.ITEMS.register("meltdream_shovel",
                    () -> new MeltdreamShovelItem(new Item.Properties()));
    /**
     * 融梦水晶锄 (meltdream_hoe) — 融梦修补
     * 改用自定义 {@link MeltdreamHoeItem}（属性与原 ItemAPI 注册逐项一致），
     * 补上原版融梦修补：手持时每 10 tick 消耗 0.01 融梦能量修复 1 耐久；
     * 铁砧修复材料为融梦水晶碎片（原版行为，取代铁锭）
     */
    public static final DeferredItem<Item> MELTDREAM_HOE =
            PDItems.ITEMS.register("meltdream_hoe",
                    () -> new MeltdreamHoeItem(new Item.Properties()));

    // === Shadow Erosion 工具 ===
    public static final DeferredItem<Item> SHADOW_EROSION_AXE =
            ItemAPI.toolItem("shadow_erosion_axe")
                    .type(ToolType.AXE).durability(1725).miningSpeed(13.0f)
                    .attackDamage(10.0f).attackSpeed(-3.0f)
                    .enchantment(16)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(() -> new ItemStack(PDItems.BLACKMETAL_INGOT.get()))
                    .build();
    public static final DeferredItem<Item> SHADOW_EROSION_SHOVEL =
            ItemAPI.toolItem("shadow_erosion_shovel")
                    .type(ToolType.SHOVEL).durability(1725).miningSpeed(13.0f)
                    .attackDamage(6.0f).attackSpeed(-3.0f)
                    .enchantment(16)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(() -> new ItemStack(PDItems.BLACKMETAL_INGOT.get()))
                    .build();
    public static final DeferredItem<Item> SHADOW_EROSION_HOE =
            ItemAPI.toolItem("shadow_erosion_hoe")
                    .type(ToolType.HOE).durability(1725).miningSpeed(13.0f)
                    .attackDamage(1.5f).attackSpeed(0.0f)
                    .enchantment(16)
                    .incorrectTag("minecraft:incorrect_for_netherite_tool")
                    .repairWith(() -> new ItemStack(PDItems.BLACKMETAL_INGOT.get()))
                    .build();

}
