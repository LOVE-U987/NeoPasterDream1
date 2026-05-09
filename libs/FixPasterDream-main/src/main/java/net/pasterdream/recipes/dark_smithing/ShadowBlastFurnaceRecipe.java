package net.pasterdream.recipes.dark_smithing;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.pasterdream.init.PasterdreamModBlocks;
import net.pasterdream.init.PasterdreamModRecipeSerializers;
import net.pasterdream.init.PasterdreamModRecipeType;
import org.jetbrains.annotations.NotNull;
import net.pasterdream.block.entity.ShadowBlastFurnaceTileEntity;
import org.jetbrains.annotations.Nullable;

public class ShadowBlastFurnaceRecipe implements Recipe<ShadowBlastFurnaceTileEntity> {
    private final ResourceLocation id;
    private final String group;
    private final Ingredient inputItem;
    private final ItemStack output;
    private final ItemStack byoutput;
    private final double byoutput_probability;
    private final int blastingTick;
    private final int spendFluidFuel;
    private final int spendFuel;
    public ShadowBlastFurnaceRecipe(ResourceLocation id, String group,Ingredient inputItem,ItemStack output,ItemStack byoutput,double byoutput_probability,int blastingTick,int spendFluidFuel,int spendFuel)
    {
        this.id = id;
        this.group = group;
        this.inputItem = inputItem;
        this.output = output;
        this.byoutput = byoutput;
        this.byoutput_probability = Mth.clamp(byoutput_probability,0,1);
        this.blastingTick = blastingTick;
        this.spendFluidFuel = spendFluidFuel;
        this.spendFuel = spendFuel;
    }

    @Override
    public boolean matches(@NotNull ShadowBlastFurnaceTileEntity shadowBlastFurnaceTileEntity, @NotNull Level level) {
        return inputItem.test(shadowBlastFurnaceTileEntity.getItem(0));
    }

    @Override
    @NotNull
    public ItemStack assemble(@NotNull ShadowBlastFurnaceTileEntity shadowBlastFurnaceTileEntity, @NotNull RegistryAccess registryAccess) {
        return getResultItem(registryAccess).copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    @NotNull
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.withSize(1,inputItem);
    }

    @Override
    @NotNull
    public ItemStack getResultItem(RegistryAccess access) {
        return this.output;
    }

    @NotNull
    public ItemStack getByResultItem(RegistryAccess access) {
        return this.byoutput;
    }

    @Override
    @NotNull
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    @NotNull
    public String getGroup() {
        return this.group;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PasterdreamModRecipeSerializers.SHADOW_BLAST_FURNACE.get();
    }

    @Override
    public RecipeType<?> getType() {
        return PasterdreamModRecipeType.SHADOW_BLAST_FURNACE.get();
    }
    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(PasterdreamModBlocks.SHADOW_BLAST_FURNACE.get());
    }
    public double getByoutput_probability() {
        return byoutput_probability;
    }

    public int getBlastingTick() {
        return blastingTick;
    }

    public int getSpendFuel() {
        return spendFuel;
    }

    public int getSpendFluidFuel() {
        return spendFluidFuel;
    }

    public static class Serializer implements RecipeSerializer<ShadowBlastFurnaceRecipe>{

        @Override
        public @NotNull ShadowBlastFurnaceRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            String groupIn = GsonHelper.getAsString(json, "group", "");
            Ingredient ingredient = Ingredient.fromJson(json.get("input_ingredient"));
            if (ingredient.isEmpty()) {
                throw new JsonParseException("No ingredient for Shadow Blast Furnace recipe");
            } else {
                ItemStack output = ItemStack.EMPTY;
                if(json.has("result"))
                    output = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true,true);
                ItemStack byoutput = ItemStack.EMPTY;
                if(json.has("by_result"))
                    byoutput = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "by_result"), true,true);
                double byoutput_probability = 0;
                if(!byoutput.isEmpty())
                {
                    byoutput_probability = GsonHelper.getAsDouble(json, "by_result_probability", 1.0);
                }
                int blastingTick = GsonHelper.getAsInt(json, "blasting_tick", 100);
                int spendFluidFuel = GsonHelper.getAsInt(json, "spend_fluid_fuel", 500);
                int spendFuel = GsonHelper.getAsInt(json, "spend_fuel", 1);
                return new ShadowBlastFurnaceRecipe(recipeId,groupIn,ingredient,output,byoutput,byoutput_probability,blastingTick,spendFluidFuel,spendFuel);
            }
        }

        @Override
        public @Nullable ShadowBlastFurnaceRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            Ingredient input = Ingredient.fromNetwork(buffer);
            ItemStack output = buffer.readItem();
            ItemStack byoutput = buffer.readItem();
            double byoutput_probability = buffer.readDouble();
            int blastingTick = buffer.readVarInt();
            int spendFluidFuel = buffer.readVarInt();
            int spendFuel = buffer.readVarInt();
            return new ShadowBlastFurnaceRecipe(recipeId,group,input,output,byoutput,byoutput_probability,blastingTick,spendFluidFuel,spendFuel);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ShadowBlastFurnaceRecipe recipe) {
            buffer.writeUtf(recipe.group);
            recipe.inputItem.toNetwork(buffer);
            buffer.writeItem(recipe.output);
            buffer.writeItem(recipe.byoutput);
            buffer.writeDouble(recipe.byoutput_probability);
            buffer.writeVarInt(recipe.blastingTick);
            buffer.writeVarInt(recipe.spendFluidFuel);
            buffer.writeVarInt(recipe.spendFuel);
        }
    }
}
