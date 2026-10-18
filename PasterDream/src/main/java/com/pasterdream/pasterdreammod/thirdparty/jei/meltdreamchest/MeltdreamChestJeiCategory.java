package com.pasterdream.pasterdreammod.thirdparty.jei.meltdreamchest;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 融梦水晶箱 JEI 战利品分类。
 * <p>
 * 每个战利品池渲染为一页（材料/宝石/饰品/装备/水晶/纪念品/宝藏），物品槽位
 * 下方标注单次抽取概率，悬停提示含概率/数量范围/品质（数据来源见
 * {@link MeltdreamChestJeiRecipe}，全部实时解析战利品表 JSON，不硬编码）。
 * 背景与槽位均程序化绘制（原版槽位贴图），无需新增 png 资产。
 *
 * @author PasterDream
 */
public final class MeltdreamChestJeiCategory implements IRecipeCategory<MeltdreamChestJeiRecipe> {

    /** JEI 配方类型标识 */
    public static final RecipeType<MeltdreamChestJeiRecipe> MELTDREAM_CHEST_JEI_RECIPE_TYPE =
            RecipeType.create(PasterDreamMod.MOD_ID, "meltdream_chest_loot", MeltdreamChestJeiRecipe.class);

    /** 画布尺寸（JEI 标准宽 176；高 = 池标题 18 + 两行槽位 46 + 底部留白 8） */
    private static final int WIDTH = 176;
    private static final int HEIGHT = 72;
    /** 槽位规格：9 列 × 2 行 = 18 格（现有各池条目数 ≤ 12） */
    private static final int COLS = 9;
    private static final int SLOT_SIZE = 18;
    private static final int SLOT_ROW_PITCH = 28;
    private static final int GRID_TOP = 18;
    /** 槽位区起始 X（水平居中） */
    private static final int GRID_LEFT = (WIDTH - COLS * SLOT_SIZE) / 2;

    /** 程序化空白背景 */
    private final IDrawable background;
    /** 原版槽位贴图 */
    private final IDrawable slot;
    /** 分类图标（融梦水晶箱） */
    private final IDrawable icon;

    /**
     * 构造分类
     *
     * @param helper JEI GUI 绘制助手
     */
    public MeltdreamChestJeiCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(WIDTH, HEIGHT);
        this.slot = helper.getSlotDrawable();
        this.icon = helper.createDrawableItemStack(new ItemStack(PDItems.MELTDREAM_CHEST.get()));
    }

    @Override
    public RecipeType<MeltdreamChestJeiRecipe> getRecipeType() {
        return MELTDREAM_CHEST_JEI_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatableWithFallback("jei.pasterdream.category.meltdream_chest", "融梦水晶箱战利品");
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(MeltdreamChestJeiRecipe recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics);
        Font font = net.minecraft.client.Minecraft.getInstance().font;
        // 池标题（条目数由 JEI 原生计数角标展示，不再重复标注）
        guiGraphics.drawString(font, recipe.poolTitle(), 8, 6, 0xFF404040, false);
        // 槽位背景 + 概率角标（槽内右上角 0.6 倍小字，整数百分比；
        // 整行平铺文字会因槽间距 18px 不足而重叠，改为角标后互不干扰）
        List<MeltdreamChestJeiRecipe.Entry> entries = recipe.entries();
        for (int i = 0; i < entries.size(); i++) {
            int x = GRID_LEFT + (i % COLS) * SLOT_SIZE;
            int y = GRID_TOP + (i / COLS) * SLOT_ROW_PITCH;
            slot.draw(guiGraphics, x, y);
            String percent = formatChanceShort(entries.get(i).chance());
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(x + SLOT_SIZE - 1, y + 2, 0);
            guiGraphics.pose().scale(0.6f, 0.6f, 1.0f);
            guiGraphics.drawString(font, percent, -font.width(percent), 0, 0xFFFFFFFF, true);
            guiGraphics.pose().popPose();
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MeltdreamChestJeiRecipe recipe, IFocusGroup focuses) {
        List<MeltdreamChestJeiRecipe.Entry> entries = recipe.entries();
        for (int i = 0; i < entries.size(); i++) {
            MeltdreamChestJeiRecipe.Entry entry = entries.get(i);
            int x = GRID_LEFT + (i % COLS) * SLOT_SIZE;
            int y = GRID_TOP + (i / COLS) * SLOT_ROW_PITCH;
            builder.addSlot(RecipeIngredientRole.OUTPUT, x, y)
                    .addItemStack(entry.stack())
                    // 悬停提示：精确概率 / 数量范围（品质由物品本体 tooltip 提供，不再重复）
                    .addRichTooltipCallback((view, tooltip) -> {
                        tooltip.add(Component.translatable("jei.pasterdream.meltdream_chest.chance",
                                formatChance(entry.chance())));
                        if (!"×1".equals(entry.countText())) {
                            tooltip.add(Component.translatable("jei.pasterdream.meltdream_chest.count",
                                    entry.countText()));
                        }
                    });
        }
    }

    /**
     * 格式化概率文本（悬停用）：100% 不带小数，其余保留 1 位小数。
     *
     * @param chance 概率（0..1）
     * @return 如 "100%"、"9.1%"
     */
    private static String formatChance(double chance) {
        double percent = chance * 100.0;
        if (percent >= 99.95) {
            return "100%";
        }
        return String.format("%.1f%%", percent);
    }

    /**
     * 格式化概率角标文本（槽内小字用）：整数百分比，控制宽度避免相邻槽位视觉干扰。
     *
     * @param chance 概率（0..1）
     * @return 如 "100%"、"9%"
     */
    private static String formatChanceShort(double chance) {
        return Math.round(chance * 100.0) + "%";
    }
}
