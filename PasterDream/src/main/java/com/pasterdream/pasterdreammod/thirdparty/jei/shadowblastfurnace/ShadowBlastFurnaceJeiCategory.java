package com.pasterdream.pasterdreammod.thirdparty.jei.shadowblastfurnace;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.recipe.ShadowBlastFurnaceRecipe;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDRecipeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Arrays;

/**
 * 暗影高炉 JEI 配方分类。
 *
 * <p>移植自原版 {@code net.pasterdream.jei.shadowblastfurnace.BlastCategory}
 * （libs/FixPasterDream-main/src/main/java/net/pasterdream/jei/shadowblastfurnace/BlastCategory.java）。
 * 配方数据不再经原版 DataRecipe 中转，改由 PDJeiPlugin#registerRecipes 里
 * 客户端 RecipeManager 动态读取数据包配方，直接注入
 * {@code List<RecipeHolder<ShadowBlastFurnaceRecipe>>}。</p>
 *
 * <p>JEI 19.x（1.21.1）API 迁移要点：</p>
 * <ul>
 *   <li>1.21.1 的 {@code RecipeManager#getAllRecipesFor} 返回
 *       {@code List<RecipeHolder<T>>}，分类泛型随之改为 {@code RecipeHolder}，
 *       RecipeType 用 {@link RecipeType#createFromVanilla} 构造
 *       （UID 自动取注册名 pasterdream:shadow_blasting，与原版一致）；</li>
 *   <li>{@code getBackground()} 已弃用：改覆写 getWidth/getHeight 并在 draw 中绘制；</li>
 *   <li>{@code addTooltipCallback} 已弃用：改用 addRichTooltipCallback
 *       （回调签名 (IRecipeSlotView, ITooltipBuilder)）。</li>
 * </ul>
 */
public final class ShadowBlastFurnaceJeiCategory implements IRecipeCategory<RecipeHolder<ShadowBlastFurnaceRecipe>> {

    /** RecipeType 唯一标识沿用原版路径 pasterdream:shadow_blasting，保证 JEI 书签/配置兼容 */
    public static final RecipeType<RecipeHolder<ShadowBlastFurnaceRecipe>> SHADOW_BLAST_FURNACE_JEI_RECIPE_TYPE =
            RecipeType.createFromVanilla(PDRecipeTypes.SHADOW_BLASTING.get());

    /** 背景贴图（256x256 图集，实际绘制区域 128x103，与原版一致） */
    private static final ResourceLocation BACKGROUND_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/screens/shadow_blast_furnace_jei.png");
    private static final int WIDTH = 128;
    private static final int HEIGHT = 103;

    private final IDrawableStatic background;
    private final IDrawable icon;

    public ShadowBlastFurnaceJeiCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(BACKGROUND_TEXTURE, 0, 0, WIDTH, HEIGHT);
        this.icon = helper.createDrawableItemStack(PDItems.SHADOW_BLAST_FURNACE.get().getDefaultInstance());
    }

    @Override
    public RecipeType<RecipeHolder<ShadowBlastFurnaceRecipe>> getRecipeType() {
        return SHADOW_BLAST_FURNACE_JEI_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        // 原版为 Component.literal("暗影高炉")；语言键已在正式语言文件中
        return Component.translatableWithFallback("jei.pasterdream.category.shadow_blast_furnace", "暗影高炉");
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
    public void draw(RecipeHolder<ShadowBlastFurnaceRecipe> recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // JEI 19.x：背景改在 draw 中绘制（getBackground 已弃用）
        background.draw(guiGraphics);
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ShadowBlastFurnaceRecipe> holder, IFocusGroup focuses) {
        ShadowBlastFurnaceRecipe recipe = holder.value();
        // 槽位坐标照抄原版 BlastCategory#setRecipe：
        // INPUT(1,5) 原料 / CATALYST(1,50) 噩梦燃料 / OUTPUT(37,86) 主产物
        // OUTPUT(73,86) 副产物 + 概率提示 / CATALYST(109,5) 暗影液体桶 + 液体消耗提示
        ItemStack spendFuel = new ItemStack(PDItems.NIGHTMARE_FUEL.get(), recipe.getSpendFuel());
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 5)
                .addItemStacks(Arrays.asList(recipe.getIngredients().get(0).getItems()));
        builder.addSlot(RecipeIngredientRole.CATALYST, 1, 50).addItemStack(spendFuel);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 37, 86).addItemStack(getResultItem(recipe));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 73, 86)
                .addItemStack(recipe.getByResultItem().copy())
                .addRichTooltipCallback((slotView, tooltip) -> {
                    // 与原版一致：0 < 概率 < 1 时追加金色概率提示
                    if (recipe.getByOutputProbability() > 0 && recipe.getByOutputProbability() < 1 && !slotView.isEmpty()) {
                        tooltip.add(Component.translatable("jei.pasterdream.shadow_blast_furnace.probability",
                                Math.floor(recipe.getByOutputProbability() * 100)).withStyle(ChatFormatting.GOLD));
                    }
                });
        // 液体燃料槽：spendFluidFuel > 0 时展示暗影液体桶（原版空桶位形同隐藏）
        builder.addSlot(RecipeIngredientRole.CATALYST, 109, 5)
                .addItemStack(recipe.getSpendFluidFuel() > 0
                        ? new ItemStack(PDItems.SHADOW_LIQUID_BUCKET.get())
                        : ItemStack.EMPTY)
                .addRichTooltipCallback((slotView, tooltip) -> {
                    if (!slotView.isEmpty()) {
                        tooltip.add(Component.translatable("jei.pasterdream.shadow_blast_furnace.consumefluid",
                                recipe.getSpendFluidFuel()).withStyle(ChatFormatting.GOLD));
                    }
                });
    }

    /**
     * 取主产物展示栈（原版 RecipeUtils.getResultItem 的客户端语义）
     *
     * @param recipe 暗影高炉配方
     * @return 主产物拷贝（客户端世界未就绪时退化为直接拷贝）
     */
    private static ItemStack getResultItem(ShadowBlastFurnaceRecipe recipe) {
        // 本配方的 getResultItem 不依赖注册表快照；JEI 分类构建总在进入世界后发生，
        // level 为空仅是防御分支
        ClientLevel level = Minecraft.getInstance().level;
        return recipe.getResultItem(level == null ? null : level.registryAccess()).copy();
    }
}
