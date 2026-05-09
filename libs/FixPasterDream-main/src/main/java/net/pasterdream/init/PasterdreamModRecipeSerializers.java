package net.pasterdream.init;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.pasterdream.PasterdreamMod;
import net.pasterdream.recipes.dark_smithing.ShadowBlastFurnaceRecipe;

public class PasterdreamModRecipeSerializers {
    private static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, PasterdreamMod.MODID);
    public static final RegistryObject<RecipeSerializer<?>> SHADOW_BLAST_FURNACE = REGISTRY.register("shadow_blasting", ShadowBlastFurnaceRecipe.Serializer::new);
    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
