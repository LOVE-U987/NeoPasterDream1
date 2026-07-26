package com.pasterdream.pasterdreammod.registry.items;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.curio.CurioAPI;
import com.pasterdream.pasterdreammod.api.curio.model.CurioSlot;
import com.pasterdream.pasterdreammod.api.effect.MobEffectAPI;
import com.pasterdream.pasterdreammod.api.entity.EntityAPI;
import com.pasterdream.pasterdreammod.api.item.ItemAPI;
import com.pasterdream.pasterdreammod.api.item.model.MigrationCategory;
import com.pasterdream.pasterdreammod.api.item.model.ToolSpec.ToolType;
import com.pasterdream.pasterdreammod.attachment.PDAttachments;
import com.pasterdream.pasterdreammod.item.*;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
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
 * Curio 饰品与特殊物品注册。
 *
 * @see PDItems
 */
public class PDItemsCurios {


    // ==================== Curio饰品/特殊物品 ====================
    // 使用 CurioAPI 统一注册，slot() 对应 data/curios/tags/item/ 下的槽位标签文件

    // 非 Curio 的普通物品
    public static final DeferredItem<Item> ANGEL_WING = PDItems.ITEMS.registerSimpleItem("angel_wing");
    public static final DeferredItem<Item> FORSAKENS_WING = PDItems.ITEMS.registerSimpleItem("forsakens_wing");
    public static final DeferredItem<Item> GROUND_WING = PDItems.ITEMS.registerSimpleItem("ground_wing");
    public static final DeferredItem<Item> MACHINE_WING = PDItems.ITEMS.registerSimpleItem("machine_wing");
    public static final DeferredItem<Item> WINGS_OF_FANG = PDItems.ITEMS.registerSimpleItem("wings_of_fang");

    // === Curio 戒指 (RING) ===
    public static final DeferredItem<Item> EMBRYO_RING = CurioAPI.create("embryo_ring").slot(CurioSlot.RING).register();
    public static final DeferredItem<Item> HITHARD_0_RING = CurioAPI.create("hithard_0_ring").slot(CurioSlot.RING).withItemClass(Hithard0RingItem::new).register();
    public static final DeferredItem<Item> HITHARD_1_RING = CurioAPI.create("hithard_1_ring").slot(CurioSlot.RING).withItemClass(Hithard1RingItem::new).register();
    public static final DeferredItem<Item> RED_DEW_0_RING = CurioAPI.create("red_dew_0_ring").slot(CurioSlot.RING).withItemClass(RedDew0RingItem::new).register();
    public static final DeferredItem<Item> RED_DEW_1_RING = CurioAPI.create("red_dew_1_ring").slot(CurioSlot.RING).withItemClass(RedDew1RingItem::new).register();
    public static final DeferredItem<Item> RED_DEW_2_RING = CurioAPI.create("red_dew_2_ring").slot(CurioSlot.RING).withItemClass(RedDew2RingItem::new).register();
    public static final DeferredItem<Item> RED_DEW_3_RING = CurioAPI.create("red_dew_3_ring").slot(CurioSlot.RING).withItemClass(RedDew3RingItem::new).register();
    public static final DeferredItem<Item> ALLKINDS_RING = CurioAPI.create("allkinds_ring").slot(CurioSlot.RING).withItemClass(AllkindsRingItem::new).register();
    public static final DeferredItem<Item> COUNTER_RING = CurioAPI.create("counter_ring").slot(CurioSlot.RING).withItemClass(CounterRingItem::new).register();
    public static final DeferredItem<Item> MELTDREAM_ENERGY_0_RING = CurioAPI.create("meltdream_energy_0_ring").slot(CurioSlot.RING).withItemClass(MeltdreamEnergy0RingItem::new).register();

    // === Curio 项链 (NECKLACE) ===
    public static final DeferredItem<Item> EMBRYO_NECKLACE = CurioAPI.create("embryo_necklace").slot(CurioSlot.NECKLACE).withItemClass(EmbryoNecklaceItem::new).register();
    public static final DeferredItem<Item> CROSS_NECKLACE = CurioAPI.create("cross_necklace").slot(CurioSlot.NECKLACE).withItemClass(CrossNecklaceItem::new).register();
    public static final DeferredItem<Item> FEATHER_NECKLACE = CurioAPI.create("feather_necklace").slot(CurioSlot.NECKLACE).withItemClass(FeatherNecklaceItem::new).register();
    public static final DeferredItem<Item> FIRE_0_NECKLACE = CurioAPI.create("fire_0_necklace").slot(CurioSlot.NECKLACE).withItemClass(Fire0NecklaceItem::new).register();
    public static final DeferredItem<Item> HEALTH_0_NECKLACE = CurioAPI.create("health_0_necklace").slot(CurioSlot.NECKLACE).withItemClass(Health0NecklaceItem::new).register();
    public static final DeferredItem<Item> RABBIT_0_NECKLACE = CurioAPI.create("rabbit_0_necklace").slot(CurioSlot.NECKLACE).withItemClass(Rabbit0NecklaceItem::new).register();

    // === Curio 腰带 (BELT) ===
    public static final DeferredItem<Item> EMBRYO_BELT = CurioAPI.create("embryo_belt").slot(CurioSlot.BELT).withItemClass(EmbryoBeltItem::new).register();
    public static final DeferredItem<Item> DREAM_TRAVELER_BELT = CurioAPI.create("dream_traveler_belt").slot(CurioSlot.BELT).withItemClass(DreamTravelerBeltItem::new).register();
    public static final DeferredItem<Item> NATURE_BELT = CurioAPI.create("nature_belt").slot(CurioSlot.BELT).withItemClass(NatureBeltItem::new).register();
    public static final DeferredItem<Item> TRAVELER_BELT = CurioAPI.create("traveler_belt").slot(CurioSlot.BELT).withItemClass(TravelerBeltItem::new).register();

    // === Curio 护符 (CHARM) ===
    public static final DeferredItem<Item> FOURLEAF_CLOVER_CURIO = CurioAPI.create("fourleaf_clover_curio").slot(CurioSlot.CHARM)
            .attribute("minecraft:generic.max_health", "055dac74-49cf-474c-9078-f658a61f7047", 1.0, AttributeModifier.Operation.ADD_VALUE)
            .attribute("pasterdream:luck", "f723cde2-ecbf-45d1-b985-8670b2f00fd2", 6.0, AttributeModifier.Operation.ADD_VALUE)
            .tooltip("§a品质：优秀 ★★", "§7§o哪片叶子代表着幸运？")
            .register();
    public static final DeferredItem<Item> EMBRYO_CHARM = CurioAPI.create("embryo_charm").slot(CurioSlot.CHARM).withItemClass(EmbryoCharmItem::new).register();
    public static final DeferredItem<Item> CARAPAX_CHARM = CurioAPI.create("carapax_charm").slot(CurioSlot.CHARM).withItemClass(CarapaxCharmItem::new).register();
    public static final DeferredItem<Item> CECILIACARE_CHARM = CurioAPI.create("ceciliacare_charm").slot(CurioSlot.CHARM).withItemClass(CeciliacareCharmItem::new).register();
    public static final DeferredItem<Item> ENDEYE_CHARM = CurioAPI.create("endeye_charm").slot(CurioSlot.CHARM).withItemClass(EndeyeCharmItem::new).register();
    public static final DeferredItem<Item> GOLD_CHARM = CurioAPI.create("gold_charm").slot(CurioSlot.CHARM).withItemClass(GoldCharmItem::new).register();
    public static final DeferredItem<Item> SEA_CHARM = CurioAPI.create("sea_charm").slot(CurioSlot.CHARM).withItemClass(SeaCharmItem::new).register();
    public static final DeferredItem<Item> TERRA_CHARM = CurioAPI.create("terra_charm").slot(CurioSlot.CHARM).withItemClass(TerraCharmItem::new).register();
    public static final DeferredItem<Item> BOBO_PLUME = CurioAPI.create("boboji_plume").slot(CurioSlot.CHARM).withItemClass(BobojiCurioItem::new).register();
    public static final DeferredItem<Item> BRIGHT_BUTTERFLY_CURIO = CurioAPI.create("bright_butterfly_curio").slot(CurioSlot.CHARM).withItemClass(BrightButterflyCurioItem::new).register();
    public static final DeferredItem<Item> CALAIS_SPICE_BOTTLE_CURIO = CurioAPI.create("calais_spice_bottle_curio").slot(CurioSlot.CHARM).withItemClass(CalaisSpiceBottleCurioItem::new).register();
    public static final DeferredItem<Item> DARK_ALLLEGORY_CURIO = CurioAPI.create("dark_alllegory_curio").slot(CurioSlot.CHARM).withItemClass(DarkAlllegoryCurioItem::new).register();
    public static final DeferredItem<Item> DUKE_COIN_CURIO = CurioAPI.create("duke_coin_curio").slot(CurioSlot.CHARM).withItemClass(DukeCoinCurioItem::new).register();
    public static final DeferredItem<Item> ICESHADOW_CURIO = CurioAPI.create("iceshadow_curio").slot(CurioSlot.CHARM).withItemClass(IceshadowCurioItem::new).register();
    public static final DeferredItem<Item> LIGHT_BUTTERFLY_CURIO = CurioAPI.create("light_butterfly_curio").slot(CurioSlot.CHARM).withItemClass(LightButterflyCurioItem::new).register();
    public static final DeferredItem<Item> WORLDTREE_SEEDPOD = CurioAPI.create("worldtree_seedpod").slot(CurioSlot.CHARM).withItemClass(WorldtreeSeedpodItem::new).register();
    public static final DeferredItem<Item> PAPER_PLANE = CurioAPI.create("paper_plane").slot(CurioSlot.CHARM).withItemClass(PaperPlaneItem::new).register();

    // === Curio 头部 (HEAD) ===
    public static final DeferredItem<Item> GHOST_FACE_HEAD = CurioAPI.create("ghost_face_head").slot(CurioSlot.HEAD).withItemClass(GhostFaceHeadItem::new).register();
    public static final DeferredItem<Item> HIYORI_HEAD = CurioAPI.create("hiyori_head").slot(CurioSlot.HEAD).withItemClass(HiyoriHeadItem::new).register();
    public static final DeferredItem<Item> QYM_HEAD = CurioAPI.create("qym_head").slot(CurioSlot.HEAD).withItemClass(QymHeadItem::new).register();
    public static final DeferredItem<Item> SNOW_VOW_HEAD = CurioAPI.create("snow_vow_head").slot(CurioSlot.HEAD).withItemClass(SnowVowHeadItem::new).register();
    public static final DeferredItem<Item> GARLAND = CurioAPI.create("garland").slot(CurioSlot.HEAD).withItemClass(GarlandItem::new).register();

    // === Curio 背部 (BACK) ===
    public static final DeferredItem<Item> EVASION_CLOAK = CurioAPI.create("evasion_cloak").slot(CurioSlot.BACK).withItemClass(EvasionCloakItem::new).register();
    public static final DeferredItem<Item> TURNBACK_CLOAK = CurioAPI.create("turnback_cloak").slot(CurioSlot.BACK).withItemClass(TurnbackCloakItem::new).register();

    // === Curio 身体 (BODY) ===
    public static final DeferredItem<Item> DEGENERATE_BODYS = CurioAPI.create("degenerate_bodys").slot(CurioSlot.BODY).withItemClass(DegenerateBodysItem::new).register();
    public static final DeferredItem<Item> WHITE_FLOWER_BODY = CurioAPI.create("white_flower_body").slot(CurioSlot.BODY).withItemClass(WhiteFlowerBodyItem::new).register();

    // === Curio 通用 (CURIO) ===
    public static final DeferredItem<Item> WIND_KNIGHT_FLAG = CurioAPI.create("wind_knight_flag").slot(CurioSlot.CURIO).withItemClass(WindKnightFlagItem::new).register();
    public static final DeferredItem<Item> TEST_CURIO = CurioAPI.create("test_curio").slot(CurioSlot.CURIO).register();

    // 非 Curio 实用物品
    public static final DeferredItem<PaleBoneneedleItem> PALE_BONENEEDLE = PDItems.ITEMS.register("pale_boneneedle",
            () -> new PaleBoneneedleItem(new Item.Properties()));

    // ==================== 内部饰品物品类 ====================

    /**
     * 融梦光环戒指（对应原版 item/MeltdreamEnergy0RingItem.java，行为逐项一致）
     * <p>
     * 佩戴期间每 20 tick（1 秒）判定一次：玩家身处梦境维度
     * （染梦世界 dyedream_world 或 灯影世界 lamp_shadow_world）时
     * 融梦能量 +0.0025/秒（即原版工具提示所述 +0.15/分钟）。
     * 能量增减经 {@link PDAttachments#addPlayerMeltDreamEnergy}（仅服务端生效并自动同步）。
     * <p>
     * 原版无 canEquip 去重限制（可同时佩戴两枚），此处保持一致；
     * D 波因能量系统未就绪跳过本物品，能量 Attachment API 落地后在此补录。
     */
    public static class MeltdreamEnergy0RingItem extends Item implements ICurioItem {

        public MeltdreamEnergy0RingItem() {
            super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
        }

        @Override
        public void appendHoverText(ItemStack itemstack, Item.TooltipContext context,
                                    List<Component> list, TooltipFlag flag) {
            super.appendHoverText(itemstack, context, list, flag);
            list.add(Component.literal("品质：§b精良 ★★★"));
            list.add(Component.literal("§7▪ §9身处梦境时 融梦能量+0.15/min"));
        }

        @Override
        public void curioTick(SlotContext slotContext, ItemStack stack) {
            if (slotContext.entity() instanceof Player pl && pl.tickCount % 20 == 0) {
                ResourceKey<Level> dimension = pl.level().dimension();
                if (dimension.equals(PDDimensions.DYEDREAM_WORLD_LEVEL_KEY)
                        || dimension.equals(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY)) {
                    PDAttachments.addPlayerMeltDreamEnergy(pl, 0.0025);
                }
            }
        }
    }

}
