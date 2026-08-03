package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.api.dimension.APIDimensions;
import com.pasterdream.pasterdreammod.api.meltdream.MeltDreamEnergyAPI;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 融梦光环戒指（对应原版 item/MeltdreamEnergy0RingItem.java，行为逐项一致）。
 * <p>
 * 注册在主模组 {@code pasterdream} 命名空间，保证手册条目 / 配方 / 创造栏
 * 引用的 {@code pasterdream:meltdream_energy_0_ring} 在未安装 PasterDreamMeltDream
 * 附属模块时依然可解析（原注册仅挂在可选附属模块上，缺失会导致 Patchouli 手册加载失败）。
 * <p>
 * 佩戴期间每 20 tick（1 秒）判定一次：玩家身处梦境维度
 * （染梦世界或灯影世界）时融梦能量 +0.0025/秒（即原版工具提示所述 +0.15/分钟）。
 * 能量增减经 {@link MeltDreamEnergyAPI#addEnergy(Player, double)} 操作（仅服务端生效并自动同步）。
 * <p>
 * 原版无 canEquip 去重限制（可同时佩戴两枚），此处保持一致。
 *
 * @author PasterDream
 */
public class MeltdreamEnergy0RingItem extends Item implements ICurioItem {

    /**
     * 构造融梦光环戒指。
     */
    public MeltdreamEnergy0RingItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
    }

    @Override
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context,
                                List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.literal("品质：§b精良 ★★★"));
        list.add(Component.literal("§7▪ §9身处梦境时 融梦能量+0.15/min"));
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player pl)) return;
        if (pl.level().isClientSide) return;

        boolean dyedream = APIDimensions.isDyedreamWorld(pl.level());
        boolean lampShadow = APIDimensions.isLampShadowWorld(pl.level());

        if (pl.tickCount % 20 == 0) {
            if (dyedream || lampShadow) {
                MeltDreamEnergyAPI.addEnergy(pl, 0.0025);
            }
        }
    }
}
