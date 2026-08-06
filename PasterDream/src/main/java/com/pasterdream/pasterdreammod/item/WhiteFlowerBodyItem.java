package com.pasterdream.pasterdreammod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 白花胸针 (White Flower Body)
 * <p>
 * San 值系统专属 Curio 身体饰品。装备后不再受到环境造成的降 San 影响。
 * <p>
 * 由 PasterDreamSanity 附属模组合并注册至主模组（使用 {@code pasterdream} 命名空间，
 * 原版 ID、主模 lang、curios:body 数据包、剧情掉落均写死该 ID）。主模组创造栏/
 * 配方/掉落引用不再依赖 PasterDreamSanity 是否加载；附属模组中的重复注册已移除。
 * 槽位绑定由 {@code data/curios/tags/item/body.json} 完成。
 *
 * @author PasterDream
 */
public class WhiteFlowerBodyItem extends Item implements ICurioItem {

    /**
     * 构造白花胸针。
     */
    public WhiteFlowerBodyItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
    }

    @Override
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context,
                                List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.literal("品质：§d史诗 ★★★★★★"));
        list.add(Component.literal("§7▪ §f条件:信仰光明"));
        list.add(Component.literal("§7▪ §9不再会受到环境造成的降san影响"));
        list.add(Component.literal("§7§o-- 我消逝于无形 此刻享受你应得的荣耀"));
    }
}
