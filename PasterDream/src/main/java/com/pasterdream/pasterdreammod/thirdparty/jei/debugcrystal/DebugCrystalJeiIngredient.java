package com.pasterdream.pasterdreammod.thirdparty.jei.debugcrystal;

import com.pasterdream.pasterdreammod.registry.PDItems;
import com.mojang.serialization.Codec;
import mezz.jei.api.ingredients.IIngredientType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * JEI 虚拟原料：调试水晶家族（{@code debug_wand_*}）在 JEI 物品列表中的唯一展示条目。
 * <p>
 * 调试水晶共 130+ 个独立注册物品，全部展示会挤占玩家 JEI 界面；本类型将其收拢为
 * 单一虚拟条目「调试水晶」，渲染沿用代表物品（染梦列车，调试创造栏图标）的贴图。
 * JEI 19.x 无法为单个 ItemStack 覆盖显示名，故以自定义原料类型实现家族统一命名。
 * <p>
 * 非真实物品：创造模式（cheat）下 JEI 不提供发放（helper {@code getCheatItemStack}
 * 默认返回空堆），获取途径见 tooltip 提示（创造模式「调试」栏）。
 *
 * @param displayStack 用于渲染贴图的代表物品堆（染梦列车）
 */
public record DebugCrystalJeiIngredient(ItemStack displayStack) {

    /** JEI 原料类型单例（uid 默认取类全名，序列化稳定） */
    public static final IIngredientType<DebugCrystalJeiIngredient> TYPE =
            () -> DebugCrystalJeiIngredient.class;

    /** 序列化 codec（书签保存用）：仅编码代表物品的注册名 */
    public static final Codec<DebugCrystalJeiIngredient> CODEC = ResourceLocation.CODEC.xmap(
            id -> new DebugCrystalJeiIngredient(new ItemStack(BuiltInRegistries.ITEM.get(id))),
            ingredient -> BuiltInRegistries.ITEM.getKey(ingredient.displayStack().getItem()));

    /**
     * 创建家族代表条目。
     *
     * @return 渲染为染梦列车贴图的调试水晶虚拟条目
     */
    public static DebugCrystalJeiIngredient createRepresentative() {
        return new DebugCrystalJeiIngredient(new ItemStack(PDItems.DEBUG_WAND_DREAM_TRAIN.get()));
    }
}
