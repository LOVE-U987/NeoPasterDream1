package com.pasterdream.pasterdreammod.thirdparty.jei.claypot;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * 陶盆（claypan）晒盐 JEI 配方分类。
 *
 * <p>移植自原版 {@code net.pasterdream.jei.claypot.Claypot}
 * （libs/FixPasterDream-main/src/main/java/net/pasterdream/jei/claypot/Claypot.java）。</p>
 *
 * <p>背景贴图 textures/screens/clay_pot_jei.png 已从原版资源复制到新项目
 * （256x256 图集，绘制区域 128x64）。</p>
 */
public final class ClaypotJeiCategory implements IRecipeCategory<ClaypotJeiRecipe> {

    /**
     * RecipeType 唯一标识沿用原版路径 pasterdream:slatpot（原版拼写如此，
     * 疑为 saltpot 笔误；为保证 JEI 书签/配置兼容不予更正）。
     */
    public static final RecipeType<ClaypotJeiRecipe> CLAYPOT_JEI_RECIPE_TYPE =
            RecipeType.create(PasterDreamMod.MOD_ID, "slatpot", ClaypotJeiRecipe.class);

    private static final ResourceLocation BACKGROUND_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/screens/clay_pot_jei.png");
    private static final int WIDTH = 128;
    private static final int HEIGHT = 64;

    private final IDrawableStatic background;
    private final IDrawable icon;

    public ClaypotJeiCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(BACKGROUND_TEXTURE, 0, 0, WIDTH, HEIGHT);
        this.icon = helper.createDrawableItemStack(PDItems.CLAYPAN_0.get().getDefaultInstance());
    }

    @Override
    public RecipeType<ClaypotJeiRecipe> getRecipeType() {
        return CLAYPOT_JEI_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        // 原版为 Component.literal("陶盆")；改用可翻译键 + 中文回退（键暂存 lang_staging_JEI.json）
        return Component.translatableWithFallback("jei.pasterdream.category.claypot", "陶盆");
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
    public void draw(ClaypotJeiRecipe recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // JEI 19.x：背景改在 draw 中绘制（getBackground 已弃用）
        background.draw(guiGraphics);
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ClaypotJeiRecipe recipe, IFocusGroup focuses) {
        // 坐标与原版一致：左输入（水源）→ 中间陶盆（仅展示，不参与配方查询）→ 右产出（粗盐）
        builder.addSlot(RecipeIngredientRole.INPUT, 16, 24).addItemStack(recipe.item1);
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 52, 24).addItemStack(recipe.item2);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 88, 24).addItemStack(recipe.item3);
    }
}
