package net.pasterdream.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.pasterdream.PasterdreamMod;
import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamTags;

import java.util.concurrent.CompletableFuture;


public class PDItemTagProvider extends ItemTagsProvider {
    public PDItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagProvider, PasterdreamMod.MODID, existingFileHelper);
    }
    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        /*tag(PasterdreamTags.ItemsTag.COOK_BUFF)
                .add(PasterdreamModItems.BACONE_EGG.get())
                .add(PasterdreamModItems.SANDWICH.get())
                .add(PasterdreamModItems.SWISS_ROLL.get())
                .add(PasterdreamModItems.STUFFED_WAFER_COOKIES.get())
                .add(PasterdreamModItems.CHOCOLATE_MATCHA_CAKE.get())
                .add(PasterdreamModItems.GINGERBREAD_MAN.get());*/
    }
    @Override
    public String getName() {
        return "Paster dream's Item Tags";
    }
}
