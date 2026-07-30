package com.pasterdream.pasterdreammod.item;

import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Curio 饰品工具类 —— 提供通用唯一装备检查、槽位查找等逻辑。
 * <p>
 * 通过遍历所有 Curio 槽位，检查相同类型的饰品是否已被装备，
 * 配合各饰品 Item 的 {@code canEquip} 方法实现「唯一装备」约束。
 */
public final class CurioHelper {

    private CurioHelper() {
        throw new UnsupportedOperationException("CurioHelper 是工具类，不可实例化");
    }

    /**
     * 检查饰品是否为「唯一装备」—— 若已在任意槽位中存在则禁止重复装备。
     * <p>
     * 遍历玩家所有 Curio 槽位，比较物品类型（按 Item 实例而非 stack tag）。
     *
     * @param slotContext 当前槽位上下文
     * @param stack       待装备的物品
     * @return true 表示可以装备（当前尚未装备同类型饰品）
     */
    public static boolean canEquipSingleton(SlotContext slotContext, ItemStack stack) {
        var player = slotContext.entity();
        if (player == null) return true;

        AtomicBoolean found = new AtomicBoolean(false);
        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            var stacks = handler.getEquippedCurios();
            for (int i = 0; i < stacks.getSlots(); i++) {
                ItemStack existing = stacks.getStackInSlot(i);
                if (!existing.isEmpty() && existing.is(stack.getItem())) {
                    found.set(true);
                    return;
                }
            }
        });
        return !found.get();
    }
}
