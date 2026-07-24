package com.pasterdream.pasterdreammod.registry.items;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.curio.CurioAPI;
import com.pasterdream.pasterdreammod.api.curio.model.CurioSlot;
import com.pasterdream.pasterdreammod.api.effect.MobEffectAPI;
import com.pasterdream.pasterdreammod.api.entity.EntityAPI;
import com.pasterdream.pasterdreammod.api.item.ItemAPI;
import com.pasterdream.pasterdreammod.api.item.model.MigrationCategory;
import com.pasterdream.pasterdreammod.api.item.model.ToolSpec.ToolType;
import com.pasterdream.pasterdreammod.item.*;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.food.FoodProperties;
import net.neoforged.neoforge.registries.DeferredItem;


/**
 * 音乐唱片注册。
 *
 * @see PDItems
 */
public class PDItemsMusic {


    // ==================== 音乐唱片（使用 API registerCustom 注册） ====================

    /**
     * 甜蜜的梦唱片 (sweetdream_disc)
     * "double scoop" by A L E X
     */
    public static final DeferredItem<PastedreamMusicDiscItem> SWEETDREAM_DISC =
            ItemAPI.registerCustom("sweetdream_disc",
                    () -> new PastedreamMusicDiscItem(PasterDreamMod.MOD_ID, "sweetdream_disc", "sweetdream",
                            "double scoop", "A L E X", "double scoop"));

    /**
     * 落雪之梦唱片 (snowfalldream_disc)
     * PasterDream - 落雪之梦，时长 2520 tick（约 126 秒）
     */
    public static final DeferredItem<PastedreamMusicDiscItem> SNOWFALLDREAM_DISC =
            ItemAPI.registerCustom("snowfalldream_disc",
                    () -> new PastedreamMusicDiscItem(PasterDreamMod.MOD_ID, "snowfalldream_disc", "snowfalldream"));

    /**
     * 亚伦柯斯之触唱片 (aaroncos_disc)
     * PasterDream - 亚伦柯斯之触，时长 2980 tick（约 149 秒）
     */
    public static final DeferredItem<PastedreamMusicDiscItem> AARONCOS_DISC =
            ItemAPI.registerCustom("aaroncos_disc",
                    () -> new PastedreamMusicDiscItem(PasterDreamMod.MOD_ID, "aaroncos_disc", "aaroncos"));

    /**
     * 染梦世界唱片 (dyedream_world_disc)
     * PasterDream - DyeDream World，使用 dyedream_world.ogg，时长 120 秒，纹理 music_disc_sweetdream
     */
    public static final DeferredItem<PastedreamMusicDiscItem> DYEDREAM_WORLD_DISC =
            ItemAPI.registerCustom("dyedream_world_disc",
                    () -> new PastedreamMusicDiscItem(PasterDreamMod.MOD_ID, "dyedream_world_disc", "dyedream_world"));

    /**
     * 风之旅途唱片 (wind_journey_disc)
     * PasterDream - 风之旅途，时长 4240 tick（约 212 秒）
     */
    public static final DeferredItem<PastedreamMusicDiscItem> WIND_JOURNEY_DISC =
            ItemAPI.registerCustom("wind_journey_disc",
                    () -> new PastedreamMusicDiscItem(PasterDreamMod.MOD_ID, "wind_journey_disc", "wind_journey"));

    /**
     * 风之旅途·其二唱片 (wind_journey_1_disc)
     * PasterDream - 风之旅途·其二，使用 wind_journey1.ogg，时长 130 秒
     */
    public static final DeferredItem<PastedreamMusicDiscItem> WIND_JOURNEY_1_DISC =
            ItemAPI.registerCustom("wind_journey_1_disc",
                    () -> new PastedreamMusicDiscItem(PasterDreamMod.MOD_ID, "wind_journey_1_disc", "wind_journey1"));


    // ==================== 染梦群系背景音乐唱片（使用 API registerCustom 注册） ====================

    /**
     * 梦幻草原唱片 (dream_meadow_disc)
     * "Nocturne in Paris" by Tony Anderson
     */
    public static final DeferredItem<PastedreamMusicDiscItem> DREAM_MEADOW_DISC =
            ItemAPI.registerCustom("dream_meadow_disc",
                    () -> new PastedreamMusicDiscItem(PasterDreamMod.MOD_ID, "dream_meadow_disc", "dream_meadow",
                            "Nocturne in Paris", "Tony Anderson", "Immanuel"));

    /**
     * 梦幻荒原唱片 (dream_heath_disc)
     * "Pop In" by [.que]
     */
    public static final DeferredItem<PastedreamMusicDiscItem> DREAM_HEATH_DISC =
            ItemAPI.registerCustom("dream_heath_disc",
                    () -> new PastedreamMusicDiscItem(PasterDreamMod.MOD_ID, "dream_heath_disc", "dream_heath",
                            "Pop In", "[.que]", "Another Sky"));

    /**
     * 梦幻雪林唱片 (dream_taiga_disc)
     * "Forest" by [.que]
     */
    public static final DeferredItem<PastedreamMusicDiscItem> DREAM_TAIGA_DISC =
            ItemAPI.registerCustom("dream_taiga_disc",
                    () -> new PastedreamMusicDiscItem(PasterDreamMod.MOD_ID, "dream_taiga_disc", "dream_taiga",
                            "Forest", "[.que]", "Wonderland"));

    /**
     * 梦幻三角洲唱片 (dream_delta_disc)
     * "The Shore" by Mango
     */
    public static final DeferredItem<PastedreamMusicDiscItem> DREAM_DELTA_DISC =
            ItemAPI.registerCustom("dream_delta_disc",
                    () -> new PastedreamMusicDiscItem(PasterDreamMod.MOD_ID, "dream_delta_disc", "dream_delta",
                            "The Shore", "Mango", "Citylanes Airplanes"));

    // 标记唱片迁移状态
    static {
        ItemAPI.markMigrated(MigrationCategory.MUSIC_DISC,
                "sweetdream_disc", "snowfalldream_disc", "aaroncos_disc", "dyedream_world_disc",
                "wind_journey_disc", "wind_journey_1_disc",
                "dream_meadow_disc", "dream_heath_disc", "dream_taiga_disc", "dream_delta_disc");
    }

}
