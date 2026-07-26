package com.pasterdream.pasterdreammod.thirdparty.jei.dreamcauldron;

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
 * 梦之坩埚（法术工厂）JEI 配方分类。
 *
 * <p>移植自原版 {@code net.pasterdream.jei.dreamcauldron.DreamCauldron}
 * （libs/FixPasterDream-main/src/main/java/net/pasterdream/jei/dreamcauldron/DreamCauldron.java）。</p>
 *
 * <p>JEI 19.x（1.21.1）API 迁移要点：</p>
 * <ul>
 *   <li>{@code getBackground()} 已被标记 @Deprecated(forRemoval)：改为覆写
 *       {@link #getWidth()}/{@link #getHeight()} 并在 {@link #draw} 中手动绘制背景贴图；</li>
 *   <li>{@code new ResourceLocation(...)} 在 1.21 中构造器私有化，改用
 *       {@link ResourceLocation#fromNamespaceAndPath(String, String)}；</li>
 *   <li>槽位布局 API（IRecipeLayoutBuilder#addSlot）与 15.x 相同，坐标沿用原版。</li>
 * </ul>
 */
public final class DreamCauldronJeiCategory implements IRecipeCategory<DreamCauldronJeiRecipe> {

    /** RecipeType 唯一标识沿用原版路径 pasterdream:dream_cauldron，保证 JEI 书签/配置兼容 */
    public static final RecipeType<DreamCauldronJeiRecipe> DREAM_CAULDRON_JEI_RECIPE_TYPE =
            RecipeType.create(PasterDreamMod.MOD_ID, "dream_cauldron", DreamCauldronJeiRecipe.class);

    /** 背景贴图（256x256 图集，实际绘制区域 192x112，与原版一致） */
    private static final ResourceLocation BACKGROUND_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/screens/dream_cauldron_gui_jei.png");
    private static final int WIDTH = 192;
    private static final int HEIGHT = 112;

    private final IDrawableStatic background;
    private final IDrawable icon;

    public DreamCauldronJeiCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(BACKGROUND_TEXTURE, 0, 0, WIDTH, HEIGHT);
        this.icon = helper.createDrawableItemStack(PDItems.DREAM_CAULDRON.get().getDefaultInstance());
    }

    @Override
    public RecipeType<DreamCauldronJeiRecipe> getRecipeType() {
        return DREAM_CAULDRON_JEI_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        // 原版为 Component.literal("法术工厂")；此处改用可翻译键 + 中文回退，
        // 语言键暂存于 lang_staging_JEI.json，合并语言文件后即支持多语言
        return Component.translatableWithFallback("jei.pasterdream.category.dream_cauldron", "法术工厂");
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
    public void draw(DreamCauldronJeiRecipe recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // JEI 19.x：背景改在 draw 中绘制（getBackground 已弃用）
        background.draw(guiGraphics);
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, DreamCauldronJeiRecipe recipe, IFocusGroup focuses) {
        // 槽位坐标与原版 JEI 完全一致：
        // 上排 3 材料（对应坩埚槽 1-3），左下引导药剂（槽 0），右上融梦液桶（催化剂），
        // 中下法术成品（槽 6），右下空桶返还（槽 5）
        builder.addSlot(RecipeIngredientRole.INPUT, 61, 19).addItemStack(recipe.input2);
        builder.addSlot(RecipeIngredientRole.INPUT, 89, 19).addItemStack(recipe.input3);
        builder.addSlot(RecipeIngredientRole.INPUT, 117, 19).addItemStack(recipe.input4);
        builder.addSlot(RecipeIngredientRole.INPUT, 17, 50).addItemStack(recipe.input1);
        builder.addSlot(RecipeIngredientRole.CATALYST, 170, 23).addItemStack(recipe.input5);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 89, 50).addItemStack(recipe.output2);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 170, 77).addItemStack(recipe.output1);
    }
}
