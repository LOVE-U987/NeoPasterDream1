package com.pasterdream.pasterdreammod.util;

import net.minecraft.world.item.ItemStack;

/**
 * 武器工坊群共享暂存变量 (Weapon Workshop Variables)
 * <p>
 * 迁移自原版 {@code network/PasterdreamModVariables.java} 中被武器工坊
 * procedure 使用的静态暂存字段 {@code weapon_workshop_item}。
 * <p>
 * 原版语义：五座工坊（精铸工坊/锻炉/铁砧/冷却盆/磨石）之间通过这一
 * <b>全局静态 ItemStack</b> 传递"正在加工的原胚"副本；
 * 其中唯一的跨方块读取点是工坊锻炉的入炉门槛
 * {@code weapon_workshop_item.process < 1}（原版耦合怪癖，保真保留）。
 * <p>
 * 新版增强：各机器另将"在制品"持久化到自身方块实体 NBT，
 * 服务器重启不会再丢失在制品（原版静态字段重启即失）；
 * 本静态字段仅按原版时机同步更新，供门槛判断等跨块读取。
 */
public final class WeaponWorkshopVariables {

    /** 当前工序中的原胚暂存（等价原版 PasterdreamModVariables.weapon_workshop_item） */
    public static ItemStack weaponWorkshopItem = ItemStack.EMPTY;

    private WeaponWorkshopVariables() {
    }
}
