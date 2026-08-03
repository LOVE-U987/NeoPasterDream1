package com.pasterdream.pasterdreammod.pasterdreammeltdream.registry;

import com.pasterdream.pasterdreammod.pasterdreammeltdream.PasterDreamMeltDreamMod;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 融梦能量系统物品注册类。
 * <p>
 * 物品注册已上收至主模组 {@code PDItemsCurios}（注册于 {@code pasterdream} 命名空间），
 * 保证手册条目 / 配方 / 创造栏引用的 {@code pasterdream:meltdream_energy_0_ring}
 * 在未安装本附属模块时依然可解析。本类仅保留注册器骨架，供后续融梦专属物品扩展。
 *
 * @author PasterDream
 */
public class PDMeltDreamItems {

    /** 物品注册器 */
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PasterDreamMeltDreamMod.MOD_ID);

    private PDMeltDreamItems() {
    }
}
