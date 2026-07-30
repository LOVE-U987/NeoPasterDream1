package com.pasterdream.pasterdreammod.thirdparty.jei.dreamcauldron;

import com.pasterdream.pasterdreammod.api.util.AddonDetector;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 梦之坩埚（法术工厂）JEI 展示配方数据。
 *
 * <p>移植自原版 {@code net.pasterdream.jei.dreamcauldron.DreamCauldronRecipe}
 * （libs/FixPasterDream-main/src/main/java/net/pasterdream/jei/dreamcauldron/DreamCauldronRecipe.java）。</p>
 *
 * <p>字段语义（与坩埚 GUI 槽位一一对应，见 {@code DreamCauldronBlockEntity}）：</p>
 * <ul>
 *   <li>input1 —— 槽 0 引导药剂（所有配方的公共前提）</li>
 *   <li>input2/input3/input4 —— 槽 1-3 三种按序匹配的材料</li>
 *   <li>input5 —— 融梦液桶（催化剂展示位：每桶向储罐注入 1000mB，每次炼制消耗 100mB）</li>
 *   <li>output1 —— 空桶（倒入液体后从桶回收槽返还）</li>
 *   <li>output2 —— 法术成品（每次炼制产出 1 个）</li>
 * </ul>
 *
 * <p>5 组配方与 {@code DreamCauldronBlockEntity.RECIPES}（也即原版
 * DreamCauldronRecipePr0Procedure）完全一致。当 PasterDreamSpells 未加载时返回空列表，
 * 避免 JEI 展示不存在的法术物品。</p>
 */
public class DreamCauldronJeiRecipe {

    /** PasterDreamSpells 法术物品的命名空间 */
    private static final String SPELLS_MOD_ID = "pasterdreamspells";

    final ItemStack input1;
    final ItemStack input2;
    final ItemStack input3;
    final ItemStack input4;
    final ItemStack input5;
    final ItemStack output1;
    final ItemStack output2;

    public DreamCauldronJeiRecipe(Item input1, Item input2, Item input3, Item input4,
                                  Item input5, Item output1, Item output2) {
        this.input1 = input1.getDefaultInstance();
        this.input2 = input2.getDefaultInstance();
        this.input3 = input3.getDefaultInstance();
        this.input4 = input4.getDefaultInstance();
        this.input5 = input5.getDefaultInstance();
        this.output1 = output1.getDefaultInstance();
        this.output2 = output2.getDefaultInstance();
    }

    /**
     * 通过 {@link BuiltInRegistries#ITEM} 动态查找 PasterDreamSpells 的法术物品。
     *
     * @param path 法术物品注册名（如 "lightning_spell"）
     * @return 对应物品的 Optional
     */
    private static Optional<Item> lookupSpellItem(String path) {
        return BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath(SPELLS_MOD_ID, path));
    }

    /**
     * 构建全部 5 组坩埚炼药配方（顺序与原版 JEI 及 DreamCauldronBlockEntity.RECIPES 一致）。
     * 当 PasterDreamSpells 未加载时返回空列表。
     */
    public static List<DreamCauldronJeiRecipe> build() {
        if (!AddonDetector.isSpellsLoaded()) {
            return List.of();
        }

        Optional<Item> lightning = lookupSpellItem("lightning_spell");
        Optional<Item> poison = lookupSpellItem("poison_spell");
        Optional<Item> healing = lookupSpellItem("healing_spell");
        Optional<Item> fury = lookupSpellItem("fury_spell");
        Optional<Item> ice = lookupSpellItem("ice_spell");

        List<DreamCauldronJeiRecipe> recipes = new ArrayList<>();

        // 引导药剂 + 矢车菊 + 红石 + 阴暗云 → 闪电法术
        lightning.ifPresent(item -> recipes.add(new DreamCauldronJeiRecipe(
                PDItems.GUIDING_DRUG.get(), Items.CORNFLOWER, Items.REDSTONE,
                PDItems.DARK_CLOUD.get(), PDItems.MELTDREAM_LIQUID_BUCKET.get(),
                Items.BUCKET, item)));

        // 引导药剂 + 花卉2 + 蜘蛛眼 + 毒马铃薯 → 剧毒法术
        poison.ifPresent(item -> recipes.add(new DreamCauldronJeiRecipe(
                PDItems.GUIDING_DRUG.get(), PDItems.FLOWER_2.get(), Items.SPIDER_EYE,
                Items.POISONOUS_POTATO, PDItems.MELTDREAM_LIQUID_BUCKET.get(),
                Items.BUCKET, item)));

        // 引导药剂 + 金苹果 + 闪烁的西瓜片 + 向日葵 → 治疗法术
        healing.ifPresent(item -> recipes.add(new DreamCauldronJeiRecipe(
                PDItems.GUIDING_DRUG.get(), Items.GOLDEN_APPLE, Items.GLISTERING_MELON_SLICE,
                Items.SUNFLOWER, PDItems.MELTDREAM_LIQUID_BUCKET.get(),
                Items.BUCKET, item)));

        // 引导药剂 + 绒球葱 + 龙息 + 紫水晶碎片 → 狂暴法术
        fury.ifPresent(item -> recipes.add(new DreamCauldronJeiRecipe(
                PDItems.GUIDING_DRUG.get(), Items.ALLIUM, Items.DRAGON_BREATH,
                Items.AMETHYST_SHARD, PDItems.MELTDREAM_LIQUID_BUCKET.get(),
                Items.BUCKET, item)));

        // 引导药剂 + 兰花 + 雪球 + 冰芽 → 冰冻法术
        ice.ifPresent(item -> recipes.add(new DreamCauldronJeiRecipe(
                PDItems.GUIDING_DRUG.get(), Items.BLUE_ORCHID, Items.SNOWBALL,
                PDItems.ICE_BUD_0.get(), PDItems.MELTDREAM_LIQUID_BUCKET.get(),
                Items.BUCKET, item)));

        return recipes;
    }
}
