package com.pasterdream.pasterdreammod.thirdparty.jei.claypot;

import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

/**
 * 陶盆（claypan）晒盐 JEI 展示配方数据。
 *
 * <p>移植自原版 {@code net.pasterdream.jei.claypot.ClaypotDataRecipe}
 * （libs/FixPasterDream-main/src/main/java/net/pasterdream/jei/claypot/ClaypotDataRecipe.java）。</p>
 *
 * <p>字段语义：item1 = 水源物品（输入），item2 = 陶盆本体（仅展示），item3 = 粗盐（产出）。</p>
 *
 * <p>说明：本分类引用的三个模组物品（claypan_0 / coarse_salt / water_glassjar）
 * 均已在新项目注册（PDItemsBlocks / PDItemsMaterials / PDItemsFoods），因此配方数据完整落地；
 * 陶盆的实际蒸发产盐方块逻辑属 claypan_1 排队模块（见 PDBlocksMisc 中
 * “claypan_1 带方块实体逻辑，移交 GUI 波次” 注释），逻辑落地后本展示数据无需改动。</p>
 */
public class ClaypotJeiRecipe {

    final ItemStack item1;
    final ItemStack item2;
    final ItemStack item3;

    public ClaypotJeiRecipe(Item item1, Item item2, Item item3) {
        this.item1 = item1.getDefaultInstance();
        this.item2 = item2.getDefaultInstance();
        this.item3 = item3.getDefaultInstance();
    }

    /**
     * 构建全部 3 组晒盐配方（与原版一致）：
     * 水桶 / 药水（水瓶）/ 玻璃罐装水 + 陶盆 → 粗盐。
     *
     * <p>注：Items.POTION 使用默认实例（无药水成分组件），显示为“不可合成的药水”，
     * 与原版 JEI 行为一致（原版同样直接 getDefaultInstance）。</p>
     */
    public static List<ClaypotJeiRecipe> build() {
        return List.of(
                new ClaypotJeiRecipe(Items.WATER_BUCKET, PDItems.CLAYPAN_0.get(), PDItems.COARSE_SALT.get()),
                new ClaypotJeiRecipe(Items.POTION, PDItems.CLAYPAN_0.get(), PDItems.COARSE_SALT.get()),
                new ClaypotJeiRecipe(PDItems.WATER_GLASSJAR.get(), PDItems.CLAYPAN_0.get(), PDItems.COARSE_SALT.get())
        );
    }
}
