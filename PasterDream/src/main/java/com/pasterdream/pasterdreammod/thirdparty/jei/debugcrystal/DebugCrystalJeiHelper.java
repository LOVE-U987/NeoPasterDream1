package com.pasterdream.pasterdreammod.thirdparty.jei.debugcrystal;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;

/**
 * 调试水晶虚拟条目的 JEI 原料助手：提供搜索名「调试水晶」与固定 uid。
 * <p>
 * 显示名走语言键（双语），JEI 物品列表搜索与 tooltip 首行均使用该名称；
 * 虚拟条目非真实物品，uid 使用固定注册名 {@code pasterdream:debug_crystal}。
 */
public class DebugCrystalJeiHelper implements IIngredientHelper<DebugCrystalJeiIngredient> {

    /** JEI 显示名语言键 */
    public static final String NAME_KEY = "jei.pasterdream.debug_crystal.name";

    /** tooltip 获取提示语言键 */
    public static final String DESC_KEY = "jei.pasterdream.debug_crystal.desc";

    /** 虚拟条目的注册名（仅作 JEI 内部标识，非真实物品） */
    private static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "debug_crystal");

    /**
     * 获取本助手对应的 JEI 原料类型。
     *
     * @return 调试水晶虚拟条目类型
     */
    @Override
    public IIngredientType<DebugCrystalJeiIngredient> getIngredientType() {
        return DebugCrystalJeiIngredient.TYPE;
    }

    /**
     * 获取搜索用显示名（JEI 物品列表与 tooltip 首行）。
     *
     * @param ingredient 虚拟条目（未使用，名称为家族统一名称）
     * @return 语言键解析后的显示名「调试水晶」
     */
    @Override
    public String getDisplayName(DebugCrystalJeiIngredient ingredient) {
        return net.minecraft.network.chat.Component.translatable(NAME_KEY).getString();
    }

    /**
     * 获取物品 uid（弃用接口，JEI 19.19.4 起由 {@link #getUid(Object, UidContext)} 默认实现委托）。
     *
     * @param ingredient 虚拟条目（未使用）
     * @param context uid 使用场景（未使用，家族统一 uid）
     * @return 固定注册名字符串
     */
    @Deprecated(since = "19.9.0", forRemoval = true)
    @Override
    public String getUniqueId(DebugCrystalJeiIngredient ingredient, UidContext context) {
        return ID.toString();
    }

    /**
     * 获取虚拟条目的注册名。
     *
     * @param ingredient 虚拟条目（未使用）
     * @return 固定注册名 {@code pasterdream:debug_crystal}
     */
    @Override
    public ResourceLocation getResourceLocation(DebugCrystalJeiIngredient ingredient) {
        return ID;
    }

    /**
     * 复制虚拟条目（JEI 用于防止原料被外部修改）。
     *
     * @param ingredient 待复制的虚拟条目
     * @return 携带代表物品堆副本的新虚拟条目
     */
    @Override
    public DebugCrystalJeiIngredient copyIngredient(DebugCrystalJeiIngredient ingredient) {
        return new DebugCrystalJeiIngredient(ingredient.displayStack().copy());
    }

    /**
     * 获取错误诊断信息（JEI 日志用）。
     *
     * @param ingredient 虚拟条目（未使用）
     * @return 诊断描述字符串
     */
    @Override
    public String getErrorInfo(DebugCrystalJeiIngredient ingredient) {
        return "DebugCrystalJeiIngredient[" + ID + "]";
    }
}
