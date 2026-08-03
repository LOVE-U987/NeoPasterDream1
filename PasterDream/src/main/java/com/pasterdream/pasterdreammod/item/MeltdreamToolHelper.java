package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.api.meltdream.MeltDreamEnergyAPI;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 融梦工具共享逻辑 —— 融梦修补
 * <p>
 * 对应原版 {@code MeltdreamToolPr0Procedure}：手持受损的融梦水晶工具时，
 * 每 10 tick（0.5 秒）消耗 0.01 融梦能量修复 1 点耐久。
 * 仅在服务端执行（耐久变化经物品同步回客户端）。
 * 融梦能量模组未安装时消耗恒失败，自动退化为普通工具，不影响使用。
 * </p>
 */
public final class MeltdreamToolHelper {

    /** 每次修复消耗的融梦能量（对应原版 0.01E/1耐久） */
    private static final double REPAIR_ENERGY_COST = 0.01;

    /** 修复判定周期（tick），对应原版 tickCount % 10 */
    private static final int REPAIR_INTERVAL = 10;

    private MeltdreamToolHelper() {
    }

    /**
     * 融梦修补：手持时周期性消耗融梦能量修复 1 点耐久。
     *
     * @param stack    工具物品栈
     * @param entity   持有者实体
     * @param selected 是否为当前手持（主手选中）物品
     */
    public static void tickRepair(ItemStack stack, Entity entity, boolean selected) {
        if (!selected || !(entity instanceof Player player)) return;
        if (player.level().isClientSide) return;
        if (player.tickCount % REPAIR_INTERVAL != 0) return;
        if (stack.getDamageValue() < 1) return;
        if (MeltDreamEnergyAPI.consumeEnergy(player, REPAIR_ENERGY_COST)) {
            stack.setDamageValue(stack.getDamageValue() - 1);
        }
    }

    /**
     * 追加「融梦修补」tooltip（原版样式：手持工具时 / ▪ 融梦修补：0.01E/1耐久）。
     *
     * @param tooltip 待追加的提示行集合
     */
    public static void appendRepairTooltip(List<Component> tooltip) {
        tooltip.add(Component.translatable("tooltip.pasterdream.meltdream_tool.hold_hint"));
        tooltip.add(Component.translatable("tooltip.pasterdream.meltdream_tool.repair"));
    }
}
