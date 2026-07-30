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
 * 玩偶/雕像物品注册。
 *
 * @see PDItems
 */
public class PDItemsDolls {


    // ==================== 玩偶/雕像物品 ====================

    /**
     * 娇小琴雨梦玩偶物品 (qin_doll_0)
     * 使用 QymDoll0DisplayItem 实现手持 3D 渲染
     */
    public static final DeferredItem<QymDoll0DisplayItem> QIN_DOLL_0 = PDItems.ITEMS.register("qin_doll_0",
            () -> new QymDoll0DisplayItem(new Item.Properties()));

    /**
     * 娇小幼幼紫玩偶物品 (little_purple_doll_0)
     * 使用 UuzDoll0DisplayItem 实现手持 3D 渲染
     */
    public static final DeferredItem<UuzDoll0DisplayItem> LITTLE_PURPLE_DOLL_0 = PDItems.ITEMS.register("little_purple_doll_0",
            () -> new UuzDoll0DisplayItem(new Item.Properties()));

    /**
     * LOVE_U拉乌酱玩偶 (love_u_doll)
     * 可放置方块玩偶，右键放置后再次右键可让玩偶抱住手中的物品
     */
    public static final DeferredItem<LoveUDollDisplayItem> LOVE_U_DOLL = PDItems.ITEMS.register("love_u_doll",
            () -> new LoveUDollDisplayItem());

    /**
     * EOUL小幽灵玩偶 (eoul_doll)
     * 可放置方块玩偶，右键放置后再次右键可让玩偶抱住手中的物品
     */
    public static final DeferredItem<EoulDollDisplayItem> EOUL_DOLL = PDItems.ITEMS.register("eoul_doll",
            () -> new EoulDollDisplayItem());

    /**
     * 狐狸雕像物品 (golden_fox_sculpture)
     * 使用 GoldenFoxSculptureDisplayItem 实现手持 3D 渲染
     */
    public static final DeferredItem<GoldenFoxSculptureDisplayItem> GOLDEN_FOX_SCULPTURE = PDItems.ITEMS.register("golden_fox_sculpture",
            () -> new GoldenFoxSculptureDisplayItem(new Item.Properties()));

    /**
     * 影之箱子物品 (shadow_chest)
     * 对应 PDBlocks.SHADOW_CHEST 方块
     * 使用 ShadowChestDisplayItem 实现手持 3D 渲染
     * 装饰性方块，无存储功能
     */
    public static final DeferredItem<ShadowChestDisplayItem> SHADOW_CHEST = PDItems.ITEMS.register("shadow_chest",
            () -> new ShadowChestDisplayItem(new Item.Properties()));

}
