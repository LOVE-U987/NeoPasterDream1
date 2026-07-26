package com.pasterdream.pasterdreammod.thirdparty.jei.dreamcauldron;

import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

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
 * <p>5 组硬编码配方与 {@code DreamCauldronBlockEntity.RECIPES}（也即原版
 * DreamCauldronRecipePr0Procedure）完全一致。</p>
 */
public class DreamCauldronJeiRecipe {

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
     * 构建全部 5 组坩埚炼药配方（顺序与原版 JEI 及 DreamCauldronBlockEntity.RECIPES 一致）。
     */
    public static List<DreamCauldronJeiRecipe> build() {
        return List.of(
                // 引导药剂 + 矢车菊 + 红石 + 阴暗云 → 闪电法术
                new DreamCauldronJeiRecipe(PDItems.GUIDING_DRUG.get(), Items.CORNFLOWER, Items.REDSTONE,
                        PDItems.DARK_CLOUD.get(), PDItems.MELTDREAM_LIQUID_BUCKET.get(),
                        Items.BUCKET, PDItems.LIGHTNING_SPELL.get()),
                // 引导药剂 + 花卉2 + 蜘蛛眼 + 毒马铃薯 → 剧毒法术
                new DreamCauldronJeiRecipe(PDItems.GUIDING_DRUG.get(), PDItems.FLOWER_2.get(), Items.SPIDER_EYE,
                        Items.POISONOUS_POTATO, PDItems.MELTDREAM_LIQUID_BUCKET.get(),
                        Items.BUCKET, PDItems.POISON_SPELL.get()),
                // 引导药剂 + 金苹果 + 闪烁的西瓜片 + 向日葵 → 治疗法术
                new DreamCauldronJeiRecipe(PDItems.GUIDING_DRUG.get(), Items.GOLDEN_APPLE, Items.GLISTERING_MELON_SLICE,
                        Items.SUNFLOWER, PDItems.MELTDREAM_LIQUID_BUCKET.get(),
                        Items.BUCKET, PDItems.HEALING_SPELL.get()),
                // 引导药剂 + 绒球葱 + 龙息 + 紫水晶碎片 → 狂暴法术
                new DreamCauldronJeiRecipe(PDItems.GUIDING_DRUG.get(), Items.ALLIUM, Items.DRAGON_BREATH,
                        Items.AMETHYST_SHARD, PDItems.MELTDREAM_LIQUID_BUCKET.get(),
                        Items.BUCKET, PDItems.FURY_SPELL.get()),
                // 引导药剂 + 兰花 + 雪球 + 冰芽 → 冰冻法术
                new DreamCauldronJeiRecipe(PDItems.GUIDING_DRUG.get(), Items.BLUE_ORCHID, Items.SNOWBALL,
                        PDItems.ICE_BUD_0.get(), PDItems.MELTDREAM_LIQUID_BUCKET.get(),
                        Items.BUCKET, PDItems.ICE_SPELL.get())
        );
    }
}
