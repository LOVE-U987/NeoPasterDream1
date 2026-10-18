package com.pasterdream.pasterdreammod.thirdparty.jei.debugcrystal;

import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 调试水晶虚拟条目的 JEI 渲染器：绘制代表物品（染梦列车）的物品模型，
 * tooltip 显示名称「调试水晶」与获取提示行。
 */
public class DebugCrystalJeiRenderer implements IIngredientRenderer<DebugCrystalJeiIngredient> {

    /**
     * 在 16x16 空间内绘制虚拟条目图标（沿用代表物品的物品模型）。
     *
     * @param guiGraphics 图形上下文
     * @param ingredient 虚拟条目（携带代表物品堆）
     */
    @Override
    public void render(GuiGraphics guiGraphics, DebugCrystalJeiIngredient ingredient) {
        guiGraphics.renderItem(ingredient.displayStack(), 0, 0);
    }

    /**
     * 获取虚拟条目 tooltip：首行名称「调试水晶」，次行灰色获取提示。
     * <p>
     * JEI 会自动在末尾追加蓝色的模组名行，此处无需重复。
     *
     * @param ingredient 虚拟条目（未使用，tooltip 为家族统一内容）
     * @param tooltipFlag 高级信息开关（F3+H，未使用）
     * @return tooltip 行列表
     */
    @Override
    public List<Component> getTooltip(DebugCrystalJeiIngredient ingredient, TooltipFlag tooltipFlag) {
        return List.of(
                Component.translatable(DebugCrystalJeiHelper.NAME_KEY),
                Component.translatable(DebugCrystalJeiHelper.DESC_KEY).withStyle(ChatFormatting.GRAY)
        );
    }
}
