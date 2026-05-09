package net.pasterdream.init;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.pasterdream.PasterdreamMod;
import net.pasterdream.recipes.dark_smithing.ShadowBlastFurnaceRecipe;

public class PasterdreamModRecipeType {
    private static final DeferredRegister<RecipeType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, PasterdreamMod.MODID);
    public static final RegistryObject<RecipeType<ShadowBlastFurnaceRecipe>> SHADOW_BLAST_FURNACE = REGISTRY.register("shadow_blasting", ()->registerRecipeType("shadow_blasting"));
    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
    public static <T extends Recipe<?>> RecipeType<T> registerRecipeType(final String identifier) {
        return new RecipeType<>()
        {
            public String toString() {
                return PasterdreamMod.MODID + ":" + identifier;
            }
        };
    }
}
