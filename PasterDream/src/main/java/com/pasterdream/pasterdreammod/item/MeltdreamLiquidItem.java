package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDFluids;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/**
 * 融梦涌泉桶装物品
 * 继承 BucketItem，使用 PDFluids.MELTDREAM_LIQUID 作为流体源
 * 属性：最大堆叠1、合成残留为桶、COMMON 稀有度
 */
public class MeltdreamLiquidItem extends BucketItem {

    /**
     * 构造融梦涌泉桶
     *
     * @param properties 物品属性
     */
    public MeltdreamLiquidItem(Item.Properties properties) {
        super(PDFluids.MELTDREAM_LIQUID.get(), properties);
    }

    /**
     * 创建默认物品属性
     *
     * @return 默认物品属性（堆叠1、合成残留桶）
     */
    public static Item.Properties createProperties() {
        return new Item.Properties()
                .stacksTo(1)
                .craftRemainder(Items.BUCKET);
    }
}
