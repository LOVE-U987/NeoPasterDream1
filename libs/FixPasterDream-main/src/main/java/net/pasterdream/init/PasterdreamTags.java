package net.pasterdream.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.pasterdream.PasterdreamMod;

public class PasterdreamTags {
    public static class ItemsTag{
        //public static final TagKey<Item> COOK_BUFF = local("cook_buff");
        /** Makes a tag in the touhoutinkermodifier domain **/
        private static TagKey<Item> local(String name) {
            return TagKey.create(Registries.ITEM, new ResourceLocation(PasterdreamMod.MODID, name));
        }

        /** Makes a tag in the forge domain **/
        private static TagKey<Item> common(String name) {
            return TagKey.create(Registries.ITEM, new ResourceLocation("forge", name));
        }
    }
}
