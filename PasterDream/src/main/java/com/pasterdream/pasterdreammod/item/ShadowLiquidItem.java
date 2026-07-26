package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDFluids;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/**
 * 熔融阴影桶装物品 (shadow_liquid_bucket)
 * <p>
 * 继承 BucketItem，使用 PDFluids.SHADOW_LIQUID 作为流体源，
 * 结构完全对照 {@link MeltdreamLiquidItem}。
 * 属性：最大堆叠 1、合成残留为空桶。
 */
public class ShadowLiquidItem extends BucketItem {

    /**
     * 构造熔融阴影桶
     *
     * @param properties 物品属性
     */
    public ShadowLiquidItem(Item.Properties properties) {
        super(PDFluids.SHADOW_LIQUID.get(), properties);
    }

    /**
     * 创建默认物品属性
     *
     * @return 默认物品属性（堆叠 1、合成残留桶）
     */
    public static Item.Properties createProperties() {
        return new Item.Properties()
                .stacksTo(1)
                .craftRemainder(Items.BUCKET);
    }
}
