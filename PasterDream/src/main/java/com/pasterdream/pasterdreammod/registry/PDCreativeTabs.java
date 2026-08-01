package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.pasterdream.pasterdreammod.registry.creativetabs.*;

/**
 * 创造模式物品栏注册类
 * 按维度/功能划分为10个标签页，便于玩家分类查找物品
 */
public class PDCreativeTabs {

    /**
     * 创造模式物品栏注册器
     */
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(
            BuiltInRegistries.CREATIVE_MODE_TAB, PasterDreamMod.MOD_ID);

    // ==================== 子文件聚合引用 ====================

    // --- PDCreativeTabsEntity ---
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ENTITY_TAB = PDCreativeTabsEntity.ENTITY_TAB;

    // --- PDCreativeTabsDyedream ---
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DYEDREAM_TAB = PDCreativeTabsDyedream.DYEDREAM_TAB;

    // --- PDCreativeTabsShadow ---
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SHADOW_TAB = PDCreativeTabsShadow.SHADOW_TAB;

    // --- PDCreativeTabsWind ---
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WIND_TAB = PDCreativeTabsWind.WIND_TAB;

    // --- PDCreativeTabsFunctional ---
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FUNCTIONAL_TAB = PDCreativeTabsFunctional.FUNCTIONAL_TAB;

    // --- PDCreativeTabsSouvenir ---
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SOUVENIR_TAB = PDCreativeTabsSouvenir.SOUVENIR_TAB;

    // --- PDCreativeTabsWeapon ---
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WEAPON_TAB = PDCreativeTabsWeapon.WEAPON_TAB;

    // --- PDCreativeTabsFood ---
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FOOD_TAB = PDCreativeTabsFood.FOOD_TAB;

    // --- PDCreativeTabsCurio ---
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CURIO_TAB = PDCreativeTabsCurio.CURIO_TAB;

    // --- PDCreativeTabsDisc ---
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DISC_TAB = PDCreativeTabsDisc.DISC_TAB;

    // --- PDCreativeTabsDebug ---
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DEBUG_TAB = PDCreativeTabsDebug.DEBUG_TAB;

}
