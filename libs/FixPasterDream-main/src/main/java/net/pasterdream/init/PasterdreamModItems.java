
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.pasterdream.init;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.pasterdream.capability.MeltDreamEnergyCapability;
import net.pasterdream.item.*;
import net.pasterdream.block.display.WindKnightSpawnblock4DisplayItem;
import net.pasterdream.block.display.WindKnightSpawnblock3DisplayItem;
import net.pasterdream.block.display.WindKnightSpawnblock2DisplayItem;
import net.pasterdream.block.display.WindKnightSpawnblock1DisplayItem;
import net.pasterdream.block.display.WindKnightSpawnblock0DisplayItem;
import net.pasterdream.block.display.WeaponWorkshopDisplayItem;
import net.pasterdream.block.display.WeaponTableDisplayItem;
import net.pasterdream.block.display.UuzDoll0DisplayItem;
import net.pasterdream.block.display.TwilightLanternDisplayItem;
import net.pasterdream.block.display.TheEndlessBookOfDreamSeekersDisplayItem;
import net.pasterdream.block.display.ShadowVortexDisplayItem;
import net.pasterdream.block.display.ShadowTrap0DisplayItem;
import net.pasterdream.block.display.ShadowDungeonPortalDisplayItem;
import net.pasterdream.block.display.ShadowChestDisplayItem;
import net.pasterdream.block.display.ShadowBrazierDisplayItem;
import net.pasterdream.block.display.ShadowBlastFurnaceDisplayItem;
import net.pasterdream.block.display.ResearchTableDisplayItem;
import net.pasterdream.block.display.QymDoll0DisplayItem;
import net.pasterdream.block.display.PicnicBasketDisplayItem;
import net.pasterdream.block.display.MeltdreamChestOpenDisplayItem;
import net.pasterdream.block.display.MeltdreamChestDisplayItem;
import net.pasterdream.block.display.LightFireflyGlassJarDisplayItem;
import net.pasterdream.block.display.LifeCrystalDisplayItem;
import net.pasterdream.block.display.GuardCrystalDisplayItem;
import net.pasterdream.block.display.GoldenFoxSculptureDisplayItem;
import net.pasterdream.block.display.ForcedTowerDisplayItem;
import net.pasterdream.block.display.FireflyGlassJarDisplayItem;
import net.pasterdream.block.display.EcologyGlassJarDisplayItem;
import net.pasterdream.block.display.DreamCauldronDisplayItem;
import net.pasterdream.block.display.DreamAccumulatorDisplayItem;
import net.pasterdream.block.display.DesertHeroTombDisplayItem;
import net.pasterdream.block.display.BrokenShadowDungeonProtalDisplayItem;
import net.pasterdream.block.display.BirdsNestDisplayItem;
import net.pasterdream.block.display.AaroncoshandspawnblockDisplayItem;
import net.pasterdream.block.display.AaroncosHandChestDisplayItem;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.ForgeSpawnEggItem;

import net.minecraft.world.level.block.Block;
import net.pasterdream.procedures.LegendDragonHornIceCreamPr0Procedure;
import net.pasterdream.procedures.RageElixirProcedure;
import org.jetbrains.annotations.NotNull;

public class PasterdreamModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, PasterdreamMod.MODID);
	public static final RegistryObject<Item> MELTDREAM_CRYSTAL_0 = REGISTRY.register("meltdream_crystal_0", () -> new MeltdreamCrystal0Item());
	public static final RegistryObject<Item> DYEDREAM_DUST = REGISTRY.register("dyedream_dust", () -> new DyedreamDustItem());
	public static final RegistryObject<Item> DYEDREAM_DUST_PIECE = REGISTRY.register("dyedream_dust_piece", () -> new DyedreamDustPieceItem());
	public static final RegistryObject<Item> DYEDREAM_DYE = REGISTRY.register("dyedream_dye", () -> new DyedreamDyeItem());
	public static final RegistryObject<Item> DYEDREAM_BUD_NUGGET = REGISTRY.register("dyedream_bud_nugget", () -> new DyedreamBudNuggetItem());
	public static final RegistryObject<Item> DYEDREAMQUARTZ = REGISTRY.register("dyedreamquartz", () -> new DyedreamquartzItem());
	public static final RegistryObject<Item> DREAM_COIN_0 = REGISTRY.register("dream_coin_0", () -> new DreamCoin0Item());
	public static final RegistryObject<Item> DREAM_COIN_1 = REGISTRY.register("dream_coin_1", () -> new DreamCoin1Item());
	public static final RegistryObject<Item> RAW_TITANIUM = REGISTRY.register("raw_titanium", () -> new RawTitaniumItem());
	public static final RegistryObject<Item> RAW_MOLTENGOLD = REGISTRY.register("raw_moltengold", () -> new RawMoltengoldItem());
	public static final RegistryObject<Item> TITANIUM_INGOT = REGISTRY.register("titanium_ingot", () -> new TitaniumIngotItem());
	public static final RegistryObject<Item> MOLTENGOLD_INGOT = REGISTRY.register("moltengold_ingot", () -> new MoltengoldIngotItem());
	public static final RegistryObject<Item> DYEDREAM_BASE = REGISTRY.register("dyedream_base", () -> new DyedreamBaseItem());
	public static final RegistryObject<Item> DYEDREAM_INGOT = REGISTRY.register("dyedream_ingot", () -> new DyedreamIngotItem());
	public static final RegistryObject<Item> WIND_IRON_INGOT = REGISTRY.register("wind_iron_ingot", () -> new WindIronIngotItem());
	public static final RegistryObject<Item> TITANIUM_NUGGET = REGISTRY.register("titanium_nugget", () -> new TitaniumNuggetItem());
	public static final RegistryObject<Item> MOLTENGOLD_NUGGET = REGISTRY.register("moltengold_nugget", () -> new MoltengoldNuggetItem());
	public static final RegistryObject<Item> DYEDREAM_NUGGET = REGISTRY.register("dyedream_nugget", () -> new DyedreamNuggetItem());
	public static final RegistryObject<Item> MOLTENGOLD_DUST = REGISTRY.register("moltengold_dust", () -> new MoltengoldDustItem());
	public static final RegistryObject<Item> SOUL_DUST = REGISTRY.register("soul_dust", () -> new SoulDustItem());
	public static final RegistryObject<Item> SOUL_ESSENCE = REGISTRY.register("soul_essence", () -> new SoulEssenceItem());
	public static final RegistryObject<Item> MAGIC_STONE = REGISTRY.register("magic_stone", () -> new MagicStoneItem());
	public static final RegistryObject<Item> WINDRUNNER_CRYSTAL = REGISTRY.register("windrunner_crystal", () -> new WindrunnerCrystalItem());
	public static final RegistryObject<Item> PULSE_WINDRUNNER_CRYSTAL = REGISTRY.register("pulse_windrunner_crystal", () -> new PulseWindrunnerCrystalItem());
	public static final RegistryObject<Item> CONGEAL_WIND = REGISTRY.register("congeal_wind", () -> new CongealWindItem());
	public static final RegistryObject<Item> PINK_SLIMEBALL = REGISTRY.register("pink_slimeball", () -> new PinkSlimeballItem());
	public static final RegistryObject<Item> DREAM_FERTILIZER = REGISTRY.register("dream_fertilizer", () -> new DreamFertilizerItem());
	public static final RegistryObject<Item> PINKEGG = REGISTRY.register("pinkegg", () -> new PinkeggItem());
	public static final RegistryObject<Item> LIGHTBALL = block(PasterdreamModBlocks.LIGHTBALL);
	public static final RegistryObject<Item> DYEDREAM_COROLLA = REGISTRY.register("dyedream_corolla", () -> new DyedreamCorollaItem());
	public static final RegistryObject<Item> WHITE_COROLLA = REGISTRY.register("white_corolla", () -> new WhiteCorollaItem());
	public static final RegistryObject<Item> RED_DEW_0 = REGISTRY.register("red_dew_0", () -> new RedDew0Item());
	public static final RegistryObject<Item> BLUE_DEW = REGISTRY.register("blue_dew", () -> new BlueDewItem());
	public static final RegistryObject<Item> BLUE_HEART_OF_THE_SEA = REGISTRY.register("blue_heart_of_the_sea", () -> new BlueHeartOfTheSeaItem());
	public static final RegistryObject<Item> ELDER_GUARDIAN_SCALE = REGISTRY.register("elder_guardian_scale", () -> new ElderGuardianScaleItem());
	public static final RegistryObject<Item> BLACK_BEETLE_CARAPACE = REGISTRY.register("black_beetle_carapace", () -> new BlackBeetleCarapaceItem());
	public static final RegistryObject<Item> BLACK_BEETLE_VOCALCORD = REGISTRY.register("black_beetle_vocalcord", () -> new BlackBeetleVocalcordItem());
	public static final RegistryObject<Item> PEBBLE_0 = block(PasterdreamModBlocks.PEBBLE_0);
	public static final RegistryObject<Item> CHARGED_AMETHYST = REGISTRY.register("charged_amethyst", () -> new ChargedAmethystItem());
	public static final RegistryObject<Item> JUNGLE_SPORE = REGISTRY.register("jungle_spore", () -> new JungleSporeItem());
	public static final RegistryObject<Item> PROTECT_DECK = REGISTRY.register("protect_deck", () -> new ProtectDeckItem());
	public static final RegistryObject<Item> COTTON = REGISTRY.register("cotton", () -> new CottonItem());
	public static final RegistryObject<Item> SPOOL = REGISTRY.register("spool", () -> new SpoolItem());
	public static final RegistryObject<Item> FABRIC = REGISTRY.register("fabric", () -> new FabricItem());
	public static final RegistryObject<Item> PERGAMYN = REGISTRY.register("pergamyn", () -> new PergamynItem());
	public static final RegistryObject<Item> PEN_AND_INK = REGISTRY.register("pen_and_ink", () -> new PenAndInkItem());
	public static final RegistryObject<Item> ANGEL_BLOCK_ITEM = REGISTRY.register("angel_block_item", () -> new AngelBlockItemItem());
	public static final RegistryObject<Item> REEDROD = REGISTRY.register("reedrod", () -> new ReedrodItem());
	public static final RegistryObject<Item> RYESEED = REGISTRY.register("ryeseed", () -> new RyeseedItem());
	public static final RegistryObject<Item> FLOUR = REGISTRY.register("flour", () -> new FlourItem());
	public static final RegistryObject<Item> SALT = REGISTRY.register("salt", () -> new SaltItem());
	public static final RegistryObject<Item> COARSE_SALT = REGISTRY.register("coarse_salt", () -> new CoarseSaltItem());
	public static final RegistryObject<Item> SORBENT = REGISTRY.register("sorbent", () -> new SorbentItem());
	public static final RegistryObject<Item> WIND_PLANT_EXTRACT = REGISTRY.register("wind_plant_extract", () -> new WindPlantExtractItem());
	public static final RegistryObject<Item> MELTDREAM_LIQUID_BUCKET = REGISTRY.register("meltdream_liquid_bucket", () -> new MeltdreamLiquidItem());
	public static final RegistryObject<Item> SHADOW_LIQUID_BUCKET = REGISTRY.register("shadow_liquid_bucket", () -> new ShadowLiquidItem());
	public static final RegistryObject<Item> PALE_BONENEEDLE = REGISTRY.register("pale_boneneedle", () -> new PaleBoneneedleItem());
	public static final RegistryObject<Item> ROOTS_PALE_BONENEEDLE = REGISTRY.register("roots_pale_boneneedle", () -> new RootsPaleBoneneedleItem());
	public static final RegistryObject<Item> MORTAR = REGISTRY.register("mortar", () -> new MortarItem());
	public static final RegistryObject<Item> BASALT_SNAIL_SHELL = REGISTRY.register("basalt_snail_shell", () -> new BasaltSnailShellItem());
	public static final RegistryObject<Item> SCULK_HEART = REGISTRY.register("sculk_heart", () -> new SculkHeartItem());
	public static final RegistryObject<Item> BLACKSTICK = REGISTRY.register("blackstick", () -> new BlackstickItem());
	public static final RegistryObject<Item> SWORD_EMBRYO_0 = REGISTRY.register("sword_embryo_0", () -> new SwordEmbryo0Item());
	public static final RegistryObject<Item> SHADOWCANDLE = block(PasterdreamModBlocks.SHADOWCANDLE);
	public static final RegistryObject<Item> CLAY_POT_0 = block(PasterdreamModBlocks.CLAY_POT_0);
	public static final RegistryObject<Item> NIGHTMARE_FUEL = REGISTRY.register("nightmare_fuel", () -> new NightmareFuelItem());
	public static final RegistryObject<Item> RUST_BLACK_METAL_GRAIN = REGISTRY.register("rust_black_metal_grain", () -> new RustBlackMetalGrainItem());
	public static final RegistryObject<Item> BLACKMETAL_GRAIN = REGISTRY.register("blackmetal_grain", () -> new BlackmetalGrainItem());
	public static final RegistryObject<Item> BLACKMETAL_INGOT = REGISTRY.register("blackmetal_ingot", () -> new BlackmetalIngotItem());
	public static final RegistryObject<Item> BROKENNOTES_0 = REGISTRY.register("brokennotes_0", () -> new Brokennotes0Item());
	public static final RegistryObject<Item> UNKNOWNNOTES_0 = REGISTRY.register("unknownnotes_0", () -> new Unknownnotes0Item());
	public static final RegistryObject<Item> SHADOW_DUNGEON_KEY = REGISTRY.register("shadow_dungeon_key", () -> new ShadowDungeonKeyItem());
	public static final RegistryObject<Item> PURE_HORROR = REGISTRY.register("pure_horror", () -> new PureHorrorItem());
	public static final RegistryObject<Item> WHITE_CRYSTAL = REGISTRY.register("white_crystal", () -> new WhiteCrystalItem());
	public static final RegistryObject<Item> SHADOW_HILT = REGISTRY.register("shadow_hilt", () -> new ShadowHiltItem());
	public static final RegistryObject<Item> TIME_HOURGLASS = REGISTRY.register("time_hourglass", () -> new TimeHourglassItem());
	public static final RegistryObject<Item> WIND_VANE = REGISTRY.register("wind_vane", () -> new WindVaneItem());
	public static final RegistryObject<Item> DREAM_WAND_EMBRYO = REGISTRY.register("dream_wand_embryo", () -> new DreamWandEmbryoItem());
	public static final RegistryObject<Item> TERRASWORD_EMBRYO = REGISTRY.register("terrasword_embryo", () -> new TerraswordEmbryoItem());
	public static final RegistryObject<Item> SHADOW_EROSION_SWORD_EMBRYO = REGISTRY.register("shadow_erosion_sword_embryo", () -> new ShadowErosionSwordEmbryoItem());
	public static final RegistryObject<Item> SHADOW_EROSION_PICKAXE_EMBRYO = REGISTRY.register("shadow_erosion_pickaxe_embryo", () -> new ShadowErosionPickaxeEmbryoItem());
	public static final RegistryObject<Item> SHADOW_EROSION_AXE_EMBRYO = REGISTRY.register("shadow_erosion_axe_embryo", () -> new ShadowErosionAxeEmbryoItem());
	public static final RegistryObject<Item> SHADOW_EROSION_HOE_EMBRYO = REGISTRY.register("shadow_erosion_hoe_embryo", () -> new ShadowErosionHoeEmbryoItem());
	public static final RegistryObject<Item> SHADOW_EROSION_SHOVEL_EMBRYO = REGISTRY.register("shadow_erosion_shovel_embryo", () -> new ShadowErosionShovelEmbryoItem());
	public static final RegistryObject<Item> ICESHADOW_HAMMER_EMBRYO = REGISTRY.register("iceshadow_hammer_embryo", () -> new IceshadowHammerEmbryoItem());
	public static final RegistryObject<Item> WHITE_SWORD_EMBRYO = REGISTRY.register("white_sword_embryo", () -> new WhiteSwordEmbryoItem());
	public static final RegistryObject<Item> SHADOW_SWORD_EMBRYO = REGISTRY.register("shadow_sword_embryo", () -> new ShadowSwordEmbryoItem());
	public static final RegistryObject<Item> STAR_WISH_ROD_EMBRYO = REGISTRY.register("star_wish_rod_embryo", () -> new StarWishRodEmbryoItem());
	public static final RegistryObject<Item> DEEP_TREASURE_0 = REGISTRY.register("deep_treasure_0", () -> new DeepTreasure0Item());
	public static final RegistryObject<Item> DEEP_TREASURE_1 = REGISTRY.register("deep_treasure_1", () -> new DeepTreasure1Item());
	public static final RegistryObject<Item> ENHANCE_STONE_0 = REGISTRY.register("enhance_stone_0", () -> new EnhanceStone0Item());
	public static final RegistryObject<Item> ENHANCE_STONE_1 = REGISTRY.register("enhance_stone_1", () -> new EnhanceStone1Item());
	public static final RegistryObject<Item> TURN_PALE_CECILIA = REGISTRY.register("turn_pale_cecilia", () -> new TurnPaleCeciliaItem());
	public static final RegistryObject<Item> CALLE_CARD_0 = REGISTRY.register("calle_card_0", () -> new CalleCard0Item());
	public static final RegistryObject<Item> CALLE_CARD_1 = REGISTRY.register("calle_card_1", () -> new CalleCard1Item());
	public static final RegistryObject<Item> CALLE_CARD_2 = REGISTRY.register("calle_card_2", () -> new CalleCard2Item());
	public static final RegistryObject<Item> CALLE_CARD_3 = REGISTRY.register("calle_card_3", () -> new CalleCard3Item());
	public static final RegistryObject<Item> CALLE_CARD_4 = REGISTRY.register("calle_card_4", () -> new CalleCard4Item());
	public static final RegistryObject<Item> CALLE_CARD_5 = REGISTRY.register("calle_card_5", () -> new CalleCard5Item());
	public static final RegistryObject<Item> CALLE_CARD_6 = REGISTRY.register("calle_card_6", () -> new CalleCard6Item());
	public static final RegistryObject<Item> CALLE_CARD_7 = REGISTRY.register("calle_card_7", () -> new CalleCard7Item());
	public static final RegistryObject<Item> CALLE_CARD_8 = REGISTRY.register("calle_card_8", () -> new CalleCard8Item());
	public static final RegistryObject<Item> CALLE_CARD_9 = REGISTRY.register("calle_card_9", () -> new CalleCard9Item());
	public static final RegistryObject<Item> SWEETDREAM_DISC = REGISTRY.register("sweetdream_disc", () -> new SweetdreamDiscItem());
	public static final RegistryObject<Item> SNOWFALLDREAM_DISC = REGISTRY.register("snowfalldream_disc", () -> new SnowfalldreamDiscItem());
	public static final RegistryObject<Item> AARONCOS_DISC = REGISTRY.register("aaroncos_disc", () -> new AaroncosDiscItem());
	public static final RegistryObject<Item> WIND_JOURNEY_DISC = REGISTRY.register("wind_journey_disc", () -> new WindJourneyDiscItem());
	public static final RegistryObject<Item> LIGHTNING_SPELL = REGISTRY.register("lightning_spell", () -> new LightningSpellItem());
	public static final RegistryObject<Item> POISON_SPELL = REGISTRY.register("poison_spell", () -> new PoisonSpellItem());
	public static final RegistryObject<Item> HEALING_SPELL = REGISTRY.register("healing_spell", () -> new HealingSpellItem());
	public static final RegistryObject<Item> FURY_SPELL = REGISTRY.register("fury_spell", () -> new FurySpellItem());
	public static final RegistryObject<Item> ICE_SPELL = REGISTRY.register("ice_spell", () -> new IceSpellItem());
	public static final RegistryObject<Item> GLASSJAR = REGISTRY.register("glassjar", () -> new GlassjarItem());
	public static final RegistryObject<Item> WATER_GLASSJAR = REGISTRY.register("water_glassjar", () -> new PasterDreamGlassDrinkItem(new Item
            .Properties()
            .craftRemainder(PasterdreamModItems.GLASSJAR.get())
            .stacksTo(64)
            .rarity(Rarity.COMMON)
            .food((new FoodProperties.Builder())
                    .nutrition(0)
                    .saturationMod(0)
                    .alwaysEat()
                    .build()),PasterdreamModItems.GLASSJAR));
	public static final RegistryObject<Item> MILK_GLASSJAR = REGISTRY.register("milk_glassjar", () -> new PasterDreamGlassDrinkItem(new Item
            .Properties()
            .craftRemainder(PasterdreamModItems.GLASSJAR.get())
            .stacksTo(64)
            .rarity(Rarity.COMMON)
            .food((new FoodProperties.Builder())
                    .nutrition(0)
                    .saturationMod(0)
                    .alwaysEat()
                    .build()),PasterdreamModItems.GLASSJAR));
	public static final RegistryObject<Item> YEAST = REGISTRY.register("yeast", () -> new YeastItem());
	public static final RegistryObject<Item> GUIDING_DRUG = REGISTRY.register("guiding_drug", () -> new GuidingDrugItem());
	public static final RegistryObject<Item> DOUGH = REGISTRY.register("dough", () -> new DoughItem());
	public static final RegistryObject<Item> EGGDOUGH = REGISTRY.register("eggdough", () -> new EggdoughItem());
	public static final RegistryObject<Item> BREAD_SLICE = REGISTRY.register("bread_slice", () -> new BreadSliceItem());
	public static final RegistryObject<Item> CAKE_BASE = REGISTRY.register("cake_base", () -> new CakeBaseItem());
	public static final RegistryObject<Item> WAFER_BISCUIT = REGISTRY.register("wafer_biscuit", () -> new WaferBiscuitItem());
	public static final RegistryObject<Item> DYEDREAM_FRUIT = REGISTRY.register("dyedream_fruit", () -> new DyedreamFruitItem());
	public static final RegistryObject<Item> FIG = REGISTRY.register("fig", () -> new FigItem());
	public static final RegistryObject<Item> DREAMJUICE = REGISTRY.register("dreamjuice", () -> new DreamjuiceItem());
	public static final RegistryObject<Item> AMBER_CANDY = REGISTRY.register("amber_candy", () -> new AmberCandyItem());
    public static final RegistryObject<Item> GLASS_CUP = REGISTRY.register("glass_cup", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> QUEER_SOUP = REGISTRY.register("queer_soup",  () -> new PasterDreamGlassDrinkItem(new Item
            .Properties()
            .craftRemainder(Items.BOWL)
            .stacksTo(64)
            .rarity(Rarity.COMMON)
            .food((new FoodProperties.Builder())
                    .nutrition(0)
                    .saturationMod(0)
                    .alwaysEat()
                    .effect(()->new MobEffectInstance(PasterdreamModMobEffects.MELT_DREAM_ENERGY_INCREASE.get(), 1),1)
                    .effect(()->new MobEffectInstance(PasterdreamModMobEffects.FONDILLUSION_BUFF.get(), 6000),1)
                    .build()),1,() -> Items.BOWL){
        @Override
        public int getUseDuration(ItemStack itemstack) {
            return 24;
        }
    });
	public static final RegistryObject<Item> UNCOOKED_DYEDREAM_FLOWER_TEA = REGISTRY.register("uncooked_dyedream_flower_tea", () -> new PasterDreamGlassDrinkItem(new Item
            .Properties()
            .craftRemainder(PasterdreamModItems.GLASS_CUP.get())
            .stacksTo(64)
            .rarity(Rarity.COMMON)
            .food((new FoodProperties.Builder())
                    .nutrition(0)
                    .saturationMod(0)
                    .alwaysEat()
                    .build())){
        @Override
        public int getUseDuration(ItemStack itemstack) {
            return 30;
        }
    });
	public static final RegistryObject<Item> DYEDREAM_FLOWER_TEA = REGISTRY.register("dyedream_flower_tea", () -> new PasterDreamGlassDrinkItem(new Item
            .Properties()
            .craftRemainder(PasterdreamModItems.GLASS_CUP.get())
            .stacksTo(64)
            .rarity(Rarity.COMMON)
            .food((new FoodProperties.Builder())
                    .nutrition(0)
                    .saturationMod(0)
                    .alwaysEat()
                    .effect(()->new MobEffectInstance(MobEffects.HEAL, 1),1)
                    .build())){
        @Override
        @OnlyIn(Dist.CLIENT)
        public boolean isFoil(ItemStack itemstack) {
            return true;
        }
        @Override
        public int getUseDuration(ItemStack itemstack) {
            return 30;
        }
    });
	public static final RegistryObject<Item> DYEDREAM_JUICE = REGISTRY.register("dyedream_juice", () -> new PasterDreamGlassDrinkItem(new Item
            .Properties()
            .craftRemainder(PasterdreamModItems.GLASS_CUP.get())
            .stacksTo(64)
            .rarity(Rarity.COMMON)
            .food((new FoodProperties.Builder())
                    .nutrition(1)
                    .saturationMod(0.2f)
                    .alwaysEat()
                    .build()),1,true){
        @Override
        public int getUseDuration(ItemStack itemstack) {
            return 24;
        }
        @Override
        public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
            if(!world.isClientSide && entity instanceof Player pl)
            {
                MeltDreamEnergyCapability.addPlayerMeltDreamEnergy(pl,0.2);
            }
            return super.finishUsingItem(itemstack, world, entity);
        }
    });
	public static final RegistryObject<Item> APPLE_JUICE = REGISTRY.register("apple_juice", () -> new PasterDreamGlassDrinkItem(new Item
            .Properties()
            .craftRemainder(PasterdreamModItems.GLASS_CUP.get())
            .stacksTo(64)
            .rarity(Rarity.COMMON)
            .food((new FoodProperties.Builder())
                    .nutrition(4)
                    .saturationMod(0.2f)
                    .alwaysEat()
                    .build())){
        @Override
        public int getUseDuration(ItemStack itemstack) {
            return 24;
        }
    });
	public static final RegistryObject<Item> HONEY_JUICE = REGISTRY.register("honey_juice", () -> new PasterDreamGlassDrinkItem(new Item
            .Properties()
            .craftRemainder(PasterdreamModItems.GLASS_CUP.get())
            .stacksTo(64)
            .rarity(Rarity.COMMON)
            .food((new FoodProperties.Builder())
                    .nutrition(6)
                    .saturationMod(0.1f)
                    .alwaysEat()
                    .build()),1,true){
        @Override
        public int getUseDuration(ItemStack itemstack) {
            return 24;
        }
        @Override
        public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
            if(!world.isClientSide)
            {
                entity.removeEffect(MobEffects.POISON);
            }
            return super.finishUsingItem(itemstack, world, entity);
        }
    });
	public static final RegistryObject<Item> WATERMELON_JUICE = REGISTRY.register("watermelon_juice", () -> new PasterDreamGlassDrinkItem(new Item
            .Properties()
            .craftRemainder(PasterdreamModItems.GLASS_CUP.get())
            .stacksTo(64)
            .rarity(Rarity.COMMON)
            .food((new FoodProperties.Builder())
                    .nutrition(2)
                    .saturationMod(0.1f)
                    .alwaysEat()
                    .build())){
        @Override
        public int getUseDuration(ItemStack itemstack) {
            return 24;
        }
    });
	public static final RegistryObject<Item> DYEDREAM_POPSICLE = REGISTRY.register("dyedream_popsicle", () -> new PasterDreamGlassDrinkItem(new Item
            .Properties()
            .craftRemainder(Items.STICK)
            .stacksTo(64)
            .rarity(Rarity.COMMON)
            .food((new FoodProperties.Builder())
                    .nutrition(1)
                    .saturationMod(0.4f)
                    .build()),2,true,()->Items.STICK){
        @Override
        public UseAnim getUseAnimation(ItemStack itemstack) {
            return UseAnim.EAT;
        }
        @Override
        public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
            if(!world.isClientSide)
            {
                if(entity instanceof Player pl)
                {
                    MeltDreamEnergyCapability.addPlayerMeltDreamEnergy(pl,0.18);
                }
                if(entity.isOnFire())
                {
                    entity.setRemainingFireTicks(Math.max(0,entity.getRemainingFireTicks() - 100));
                }
            }
            return super.finishUsingItem(itemstack, world, entity);
        }
    });
	public static final RegistryObject<Item> FRIED_EGG = REGISTRY.register("fried_egg", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(2).saturationMod(0.2f).build())));
	public static final RegistryObject<Item> BACONE_EGG = REGISTRY.register("bacone_egg", () -> new PasterDreamFoodItem(
            new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
                    .food((new FoodProperties.Builder())
                            .nutrition(10)
                            .saturationMod(1.2f)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.SAN_INCREASE.get(), 1),1)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.COOK_BUFF.get(), 1200, 0, false, false),1)
                            .build())){
        @Override
        public int getUseDuration(ItemStack itemstack) {
            return 48;
        }
    });
	public static final RegistryObject<Item> RICECAKE = REGISTRY.register("ricecake", () -> new Item(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(6).saturationMod(0.4f).build())));
	public static final RegistryObject<Item> SANDWICH = REGISTRY.register("sandwich", () -> new PasterDreamFoodItem(
            new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
                    .food((new FoodProperties.Builder())
                            .nutrition(10)
                            .saturationMod(0.9f)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.SAN_INCREASE.get(), 1),1)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.COOK_BUFF.get(), 1200, 0, false, false),1)
                            .build())));
	public static final RegistryObject<Item> CHOCOLATE = REGISTRY.register("chocolate", () -> new PasterDreamFoodItem(
            new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
                    .food((new FoodProperties.Builder())
                            .nutrition(4)
                            .saturationMod(0.375f)
                            .build())));
	public static final RegistryObject<Item> POPPING_CANDY = REGISTRY.register("popping_candy", () -> new PoppingCandyItem());
	public static final RegistryObject<Item> CHOCOLATE_MATCHA_CAKE = REGISTRY.register("chocolate_matcha_cake", () -> new PasterDreamFoodItem(
            new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
                    .food((new FoodProperties.Builder())
                            .nutrition(8)
                            .saturationMod(1f)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.SAN_INCREASE.get(),1),1)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.COOK_BUFF.get(), 1200, 0),1)
                            .build())));
	public static final RegistryObject<Item> SWISS_ROLL = REGISTRY.register("swiss_roll", () -> new PasterDreamFoodItem(
            new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
                    .food((new FoodProperties.Builder())
                            .nutrition(6)
                            .saturationMod(0.8f)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.SAN_INCREASE.get(), 1),1)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.COOK_BUFF.get(), 1200, 0, false, false),1)
                            .build())));
	public static final RegistryObject<Item> STUFFED_WAFER_COOKIES = REGISTRY.register("stuffed_wafer_cookies", () -> new PasterDreamFoodItem(
            new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
                    .food((new FoodProperties.Builder())
                            .nutrition(7)
                            .saturationMod(1f)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.SAN_INCREASE.get(), 1),1)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.COOK_BUFF.get(), 1200, 0, false, false),1)
                            .build())));
	public static final RegistryObject<Item> CREAM_BUNCAKE = REGISTRY.register("cream_buncake", () -> new PasterDreamFoodItem(
            new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
                    .food((new FoodProperties.Builder())
                            .nutrition(4)
                            .saturationMod(0.5f)
                            .alwaysEat()
                            .fast()
                            .build())));
	public static final RegistryObject<Item> BERRY_BUNCAKE = REGISTRY.register("berry_buncake", () -> new PasterDreamFoodItem(
            new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
                    .food((new FoodProperties.Builder())
                            .nutrition(4)
                            .saturationMod(0.5f)
                            .alwaysEat()
                            .fast()
                            .effect(()->new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 0),1)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.COOK_BUFF.get(), 1200, 0),1)
                            .build())));
	public static final RegistryObject<Item> POTATO_BUNCAKE = REGISTRY.register("potato_buncake", () -> new PasterDreamFoodItem(
            new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
                    .food((new FoodProperties.Builder())
                            .nutrition(4)
                            .saturationMod(0.5f)
                            .alwaysEat()
                            .fast()
                            .effect(()->new MobEffectInstance(MobEffects.DIG_SPEED, 1200, 0),1)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.COOK_BUFF.get(), 1200, 0),1)
                            .build())));
	public static final RegistryObject<Item> MELON_BUNCAKE = REGISTRY.register("melon_buncake", () -> new PasterDreamFoodItem(
            new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
                    .food((new FoodProperties.Builder())
                            .nutrition(4)
                            .saturationMod(0.5f)
                            .alwaysEat()
                            .fast()
                            .effect(()->new MobEffectInstance(MobEffects.REGENERATION, 400, 0),1)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.COOK_BUFF.get(), 1200, 0),1)
                            .build())));
	public static final RegistryObject<Item> PUMPKIN_BUNCAKE = REGISTRY.register("pumpkin_buncake", () -> new PasterDreamFoodItem(
            new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
                    .food((new FoodProperties.Builder())
                            .nutrition(4)
                            .saturationMod(0.5f)
                            .alwaysEat()
                            .fast()
                            .effect(()->new MobEffectInstance(MobEffects.JUMP, 1200, 0),1)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.COOK_BUFF.get(), 1200, 0),1)
                            .build())));
	public static final RegistryObject<Item> GLOW_BERRY_BUNCAKE = REGISTRY.register("glow_berry_buncake", () -> new PasterDreamFoodItem(
            new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
                    .food((new FoodProperties.Builder())
                            .nutrition(4)
                            .saturationMod(0.5f)
                            .alwaysEat()
                            .fast()
                            .effect(()->new MobEffectInstance(MobEffects.NIGHT_VISION, 1200, 0),1)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.COOK_BUFF.get(), 1200, 0),1)
                            .build())));
	public static final RegistryObject<Item> DYEDREAM_FRUIT_BUNCAKE = REGISTRY.register("dyedream_fruit_buncake", () -> new PasterDreamFoodItem(
            new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
                    .food((new FoodProperties.Builder())
                            .nutrition(4)
                            .saturationMod(0.5f)
                            .alwaysEat()
                            .fast()
                            .effect(()->new MobEffectInstance(MobEffects.REGENERATION, 200, 0),1)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.COOK_BUFF.get(), 1200, 0),1)
                            .build())));
	public static final RegistryObject<Item> HEART_CHOCOLATE_0 = REGISTRY.register("heart_chocolate_0", () -> new HeartChocolate0Item());
	public static final RegistryObject<Item> HEART_CHOCOLATE_1 = REGISTRY.register("heart_chocolate_1", () -> new HeartChocolate1Item());
	public static final RegistryObject<Item> HEART_CHOCOLATE_2 = REGISTRY.register("heart_chocolate_2", () -> new HeartChocolate2Item());
	public static final RegistryObject<Item> GINGERBREAD_MAN = REGISTRY.register("gingerbread_man", () -> new PasterDreamFoodItem(
            new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
                    .food((new FoodProperties.Builder())
                            .nutrition(5)
                            .saturationMod(0.6f)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.SAN_INCREASE.get(), 1),1)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.COOK_BUFF.get(), 1200, 0, false, false),1)
                            .build())));
	public static final RegistryObject<Item> CANDY_CANE = REGISTRY.register("candy_cane", () -> new PasterDreamFoodItem(
            new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
                    .food((new FoodProperties.Builder())
                            .nutrition(3)
                            .saturationMod(0.5f)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.SAN_INCREASE.get(), 1),1)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.COOK_BUFF.get(), 1200, 0, false, false),1)
                            .build())));
	public static final RegistryObject<Item> BUBBLE_GUM = REGISTRY.register("bubble_gum", () -> new PasterDreamFoodItem(
            new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
                    .food((new FoodProperties.Builder())
                            .nutrition(1)
                            .saturationMod(0)
                            .alwaysEat()
                            .fast()
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.SAN_INCREASE.get(), 1, 2),1)
                            .build())){
        @Override
        public int getUseDuration(@NotNull ItemStack itemstack) {
            return 64;
        }
    });
	public static final RegistryObject<Item> DREAM_COTTON_CANDY = REGISTRY.register("dream_cotton_candy", () -> new PasterDreamFoodItem(
            new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
                    .food((new FoodProperties.Builder())
                            .nutrition(4)
                            .saturationMod(0.625f)
                            .alwaysEat()
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.MELT_DREAM_ENERGY_INCREASE.get(), 1, 2),1)
                            .build())));
	public static final RegistryObject<Item> YINHUL_COTTON_CANDY = REGISTRY.register("yinhul_cotton_candy", () -> new PasterDreamFoodItem(
            new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
                    .food((new FoodProperties.Builder())
                            .nutrition(6)
                            .saturationMod(0.75f)
                            .alwaysEat()
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.SAN_INCREASE.get(), 1, 9),1)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.MELT_DREAM_ENERGY_INCREASE.get(), 1, 19),1)
                            .build())));
	public static final RegistryObject<Item> JELLYFISH_MUD = REGISTRY.register("jellyfish_mud", () -> new PasterDreamFoodItem(
            new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
                    .food((new FoodProperties.Builder())
                            .nutrition(1)
                            .saturationMod(0f)
                            .effect(() -> new MobEffectInstance(PasterdreamModMobEffects.WINDPROOF_BUFF.get(), 1200, 0),1)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.SAN_DECREASE.get(), 1, 2),1)
                            .build()),1));
	public static final RegistryObject<Item> JELLYFISH_JELLO = REGISTRY.register("jellyfish_jello", () -> new PasterDreamFoodItem(
            new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
                    .food((new FoodProperties.Builder())
                            .nutrition(4)
                            .saturationMod(0.5f)
                            .alwaysEat()
                            .effect(()-> new MobEffectInstance(PasterdreamModMobEffects.WINDPROOF_BUFF.get(), 12000, 0),1)
                            .build()),1));
	public static final RegistryObject<Item> ELIXIR_BOTTLE = REGISTRY.register("elixir_bottle", () -> new ElixirBottleItem());
	public static final RegistryObject<Item> MELTDREAM_ELIXIR_BOTTLE = REGISTRY.register("meltdream_elixir_bottle", () -> new PasterDreamGlassDrinkItem(new Item
            .Properties()
            .craftRemainder(PasterdreamModItems.ELIXIR_BOTTLE.get())
            .stacksTo(1)
            .rarity(Rarity.COMMON)
            .food((new FoodProperties.Builder())
                    .nutrition(4)
                    .saturationMod(0.25f)
                    .alwaysEat()
                    .effect(()->new MobEffectInstance(PasterdreamModMobEffects.MELT_DREAM_ENERGY_INCREASE.get(),1,24),1)
                    .build()),PasterdreamModItems.ELIXIR_BOTTLE));
	public static final RegistryObject<Item> RAGE_ELIXIR_0 = REGISTRY.register("rage_elixir_0", () -> new PasterDreamGlassDrinkItem(new Item
            .Properties()
            .craftRemainder(PasterdreamModItems.ELIXIR_BOTTLE.get())
            .stacksTo(1)
            .rarity(Rarity.UNCOMMON)
            .food((new FoodProperties.Builder())
                    .nutrition(0)
                    .saturationMod(0)
                    .alwaysEat()
                    .effect(()->new MobEffectInstance(PasterdreamModMobEffects.MELT_DREAM_ENERGY_INCREASE.get(),1,24),1)
                    .build()),3,true,PasterdreamModItems.ELIXIR_BOTTLE){
        @Override
        public int getUseDuration(ItemStack itemstack) {
            return 40;
        }
        @Override
        @OnlyIn(Dist.CLIENT)
        public boolean isFoil(ItemStack itemstack) {
            return true;
        }
        @Override
        public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
            double x = entity.getX();
            double y = entity.getY();
            double z = entity.getZ();
            RageElixirProcedure.execute(world, x, y, z, entity,1);
            return super.finishUsingItem(itemstack, world, entity);
        }
    });
	public static final RegistryObject<Item> PINEAPPLE_LOVE_SEA = REGISTRY.register("pineapple_love_sea", () -> new PasterDreamFoodItem(
            new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.UNCOMMON)
                    .food((new FoodProperties.Builder())
                            .nutrition(3)
                            .saturationMod(0.5f)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.SAN_INCREASE.get(), 1, 19),1)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.COOK_BUFF.get(), 6000, 0, false, false),1)
                            .build()),1){
        @Override
        public @NotNull UseAnim getUseAnimation(@NotNull ItemStack itemstack) {
            return UseAnim.DRINK;
        }
    });
	public static final RegistryObject<Item> GOLDENROD_TEA = REGISTRY.register("goldenrod_tea", () -> new PasterDreamGlassDrinkItem(new Item
            .Properties()
            .craftRemainder(PasterdreamModItems.GLASSJAR.get())
            .stacksTo(64)
            .rarity(Rarity.COMMON)
            .food((new FoodProperties.Builder())
                    .nutrition(1)
                    .saturationMod(0)
                    .alwaysEat()
                    .effect(()-> new MobEffectInstance(PasterdreamModMobEffects.GOLDENROD_TEA_BUFF.get(), 3600, 0),1)
                    .build()),1,PasterdreamModItems.GLASSJAR));
	public static final RegistryObject<Item> LEGEND_DRAGON_HORN_ICE_CREAM = REGISTRY.register("legend_dragon_horn_ice_cream", () -> new PasterDreamGlassDrinkItem(new Item
            .Properties()
            .craftRemainder(Items.BOWL)
            .stacksTo(64)
            .rarity(Rarity.EPIC)
            .food((new FoodProperties.Builder())
                    .nutrition(10)
                    .saturationMod(1.2f)
                    .alwaysEat()
                    .build()),2,() ->Items.BOWL){
        @Override
        public UseAnim getUseAnimation(ItemStack itemstack) {
            return UseAnim.EAT;
        }
        @Override
        public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
            double x = entity.getX();
            double y = entity.getY();
            double z = entity.getZ();
            LegendDragonHornIceCreamPr0Procedure.execute(world, x, y, z, entity);
            return super.finishUsingItem(itemstack,world,entity);
        }
    });
	public static final RegistryObject<Item> DYEDREAM_PERFUME = REGISTRY.register("dyedream_perfume", () -> new DyedreamPerfumeItem());
	public static final RegistryObject<Item> MELTDREAM_CHEST = REGISTRY.register(PasterdreamModBlocks.MELTDREAM_CHEST.getId().getPath(), () -> new MeltdreamChestDisplayItem(PasterdreamModBlocks.MELTDREAM_CHEST.get(), new Item.Properties()));
	public static final RegistryObject<Item> MELTDREAM_CHEST_OPEN = REGISTRY.register(PasterdreamModBlocks.MELTDREAM_CHEST_OPEN.getId().getPath(),
			() -> new MeltdreamChestOpenDisplayItem(PasterdreamModBlocks.MELTDREAM_CHEST_OPEN.get(), new Item.Properties()));
	public static final RegistryObject<Item> DREAM_ACCUMULATOR = REGISTRY.register(PasterdreamModBlocks.DREAM_ACCUMULATOR.getId().getPath(), () -> new DreamAccumulatorDisplayItem(PasterdreamModBlocks.DREAM_ACCUMULATOR.get(), new Item.Properties()));
	public static final RegistryObject<Item> CLAYPAN_0 = block(PasterdreamModBlocks.CLAYPAN_0);
	public static final RegistryObject<Item> RESEARCH_TABLE = REGISTRY.register(PasterdreamModBlocks.RESEARCH_TABLE.getId().getPath(), () -> new ResearchTableDisplayItem(PasterdreamModBlocks.RESEARCH_TABLE.get(), new Item.Properties()));
	public static final RegistryObject<Item> FORCED_TOWER = REGISTRY.register(PasterdreamModBlocks.FORCED_TOWER.getId().getPath(), () -> new ForcedTowerDisplayItem(PasterdreamModBlocks.FORCED_TOWER.get(), new Item.Properties()));
	public static final RegistryObject<Item> WEAPON_TABLE = REGISTRY.register(PasterdreamModBlocks.WEAPON_TABLE.getId().getPath(), () -> new WeaponTableDisplayItem(PasterdreamModBlocks.WEAPON_TABLE.get(), new Item.Properties()));
	public static final RegistryObject<Item> WEAPON_WORKSHOP = REGISTRY.register(PasterdreamModBlocks.WEAPON_WORKSHOP.getId().getPath(), () -> new WeaponWorkshopDisplayItem(PasterdreamModBlocks.WEAPON_WORKSHOP.get(), new Item.Properties()));
	public static final RegistryObject<Item> DYEDREAM_DESK = block(PasterdreamModBlocks.DYEDREAM_DESK);
	public static final RegistryObject<Item> LOST_SWORD_BLOCK = block(PasterdreamModBlocks.LOST_SWORD_BLOCK);
	public static final RegistryObject<Item> DESERT_HERO_TOMB = REGISTRY.register(PasterdreamModBlocks.DESERT_HERO_TOMB.getId().getPath(), () -> new DesertHeroTombDisplayItem(PasterdreamModBlocks.DESERT_HERO_TOMB.get(), new Item.Properties()));
	public static final RegistryObject<Item> QYM_DOLL_0 = REGISTRY.register(PasterdreamModBlocks.QYM_DOLL_0.getId().getPath(), () -> new QymDoll0DisplayItem(PasterdreamModBlocks.QYM_DOLL_0.get(), new Item.Properties()));
	public static final RegistryObject<Item> UUZ_DOLL_0 = REGISTRY.register(PasterdreamModBlocks.UUZ_DOLL_0.getId().getPath(), () -> new UuzDoll0DisplayItem(PasterdreamModBlocks.UUZ_DOLL_0.get(), new Item.Properties()));
	public static final RegistryObject<Item> TWILIGHT_LANTERN = REGISTRY.register(PasterdreamModBlocks.TWILIGHT_LANTERN.getId().getPath(), () -> new TwilightLanternDisplayItem(PasterdreamModBlocks.TWILIGHT_LANTERN.get(), new Item.Properties()));
	public static final RegistryObject<Item> SHADOW_BED = block(PasterdreamModBlocks.SHADOW_BED);
	public static final RegistryObject<Item> SHADOW_DESK = block(PasterdreamModBlocks.SHADOW_DESK);
	public static final RegistryObject<Item> SHADOW_CHEST = REGISTRY.register(PasterdreamModBlocks.SHADOW_CHEST.getId().getPath(), () -> new ShadowChestDisplayItem(PasterdreamModBlocks.SHADOW_CHEST.get(), new Item.Properties()));
	public static final RegistryObject<Item> SHADOW_BLAST_FURNACE_CORE = block(PasterdreamModBlocks.SHADOW_BLAST_FURNACE_CORE);
	public static final RegistryObject<Item> SHADOW_BLAST_FURNACE = REGISTRY.register(PasterdreamModBlocks.SHADOW_BLAST_FURNACE.getId().getPath(),
			() -> new ShadowBlastFurnaceDisplayItem(PasterdreamModBlocks.SHADOW_BLAST_FURNACE.get(), new Item.Properties()));
	public static final RegistryObject<Item> CALLE_CARD_BLOCK = block(PasterdreamModBlocks.CALLE_CARD_BLOCK);
	public static final RegistryObject<Item> GUARD_CRYSTAL = REGISTRY.register(PasterdreamModBlocks.GUARD_CRYSTAL.getId().getPath(), () -> new GuardCrystalDisplayItem(PasterdreamModBlocks.GUARD_CRYSTAL.get(), new Item.Properties()));
	public static final RegistryObject<Item> BROKEN_SHADOW_DUNGEON_PROTAL = REGISTRY.register(PasterdreamModBlocks.BROKEN_SHADOW_DUNGEON_PROTAL.getId().getPath(),
			() -> new BrokenShadowDungeonProtalDisplayItem(PasterdreamModBlocks.BROKEN_SHADOW_DUNGEON_PROTAL.get(), new Item.Properties()));
	public static final RegistryObject<Item> SHADOW_DUNGEON_PORTAL = REGISTRY.register(PasterdreamModBlocks.SHADOW_DUNGEON_PORTAL.getId().getPath(),
			() -> new ShadowDungeonPortalDisplayItem(PasterdreamModBlocks.SHADOW_DUNGEON_PORTAL.get(), new Item.Properties()));
	public static final RegistryObject<Item> SHADOW_DUNGEON_DOOR_0 = block(PasterdreamModBlocks.SHADOW_DUNGEON_DOOR_0);
	public static final RegistryObject<Item> SHADOWDUNGEONDOOR_2 = block(PasterdreamModBlocks.SHADOWDUNGEONDOOR_2);
	public static final RegistryObject<Item> SHADOW_DUNGEON_KEY_0 = block(PasterdreamModBlocks.SHADOW_DUNGEON_KEY_0);
	public static final RegistryObject<Item> SHADOW_DUNGEON_KEY_1 = block(PasterdreamModBlocks.SHADOW_DUNGEON_KEY_1);
	public static final RegistryObject<Item> SHADOW_TRAP_0 = REGISTRY.register(PasterdreamModBlocks.SHADOW_TRAP_0.getId().getPath(), () -> new ShadowTrap0DisplayItem(PasterdreamModBlocks.SHADOW_TRAP_0.get(), new Item.Properties()));
	public static final RegistryObject<Item> SHADOW_BRAZIER = REGISTRY.register(PasterdreamModBlocks.SHADOW_BRAZIER.getId().getPath(), () -> new ShadowBrazierDisplayItem(PasterdreamModBlocks.SHADOW_BRAZIER.get(), new Item.Properties()));
	public static final RegistryObject<Item> AARONCOS_HAND_CHEST = REGISTRY.register(PasterdreamModBlocks.AARONCOS_HAND_CHEST.getId().getPath(),
			() -> new AaroncosHandChestDisplayItem(PasterdreamModBlocks.AARONCOS_HAND_CHEST.get(), new Item.Properties()));
	public static final RegistryObject<Item> DREAM_CAULDRON = REGISTRY.register(PasterdreamModBlocks.DREAM_CAULDRON.getId().getPath(), () -> new DreamCauldronDisplayItem(PasterdreamModBlocks.DREAM_CAULDRON.get(), new Item.Properties()));
	public static final RegistryObject<Item> BIRDS_NEST = REGISTRY.register(PasterdreamModBlocks.BIRDS_NEST.getId().getPath(), () -> new BirdsNestDisplayItem(PasterdreamModBlocks.BIRDS_NEST.get(), new Item.Properties()));
	public static final RegistryObject<Item> WINDMOOR_CRATE = block(PasterdreamModBlocks.WINDMOOR_CRATE);
	public static final RegistryObject<Item> THE_ENDLESS_BOOK_OF_DREAM_SEEKERS = REGISTRY.register(PasterdreamModBlocks.THE_ENDLESS_BOOK_OF_DREAM_SEEKERS.getId().getPath(),
			() -> new TheEndlessBookOfDreamSeekersDisplayItem(PasterdreamModBlocks.THE_ENDLESS_BOOK_OF_DREAM_SEEKERS.get(), new Item.Properties()));
	public static final RegistryObject<Item> CHRISTMAS_LIGHTS = block(PasterdreamModBlocks.CHRISTMAS_LIGHTS);
	public static final RegistryObject<Item> FIREFLY_NEST = block(PasterdreamModBlocks.FIREFLY_NEST);
	public static final RegistryObject<Item> ECOLOGY_GLASS_JAR = REGISTRY.register(PasterdreamModBlocks.ECOLOGY_GLASS_JAR.getId().getPath(), () -> new EcologyGlassJarDisplayItem(PasterdreamModBlocks.ECOLOGY_GLASS_JAR.get(), new Item.Properties()));
	public static final RegistryObject<Item> FIREFLY_GLASS_JAR = REGISTRY.register(PasterdreamModBlocks.FIREFLY_GLASS_JAR.getId().getPath(), () -> new FireflyGlassJarDisplayItem(PasterdreamModBlocks.FIREFLY_GLASS_JAR.get(), new Item.Properties()));
	public static final RegistryObject<Item> LIFE_CRYSTAL = REGISTRY.register(PasterdreamModBlocks.LIFE_CRYSTAL.getId().getPath(), () -> new LifeCrystalDisplayItem(PasterdreamModBlocks.LIFE_CRYSTAL.get(), new Item.Properties()));
	public static final RegistryObject<Item> PICNIC_BASKET = REGISTRY.register(PasterdreamModBlocks.PICNIC_BASKET.getId().getPath(), () -> new PicnicBasketDisplayItem(PasterdreamModBlocks.PICNIC_BASKET.get(), new Item.Properties()));
	public static final RegistryObject<Item> WIND_KNIGHT_SPAWNBLOCK_0 = REGISTRY.register(PasterdreamModBlocks.WIND_KNIGHT_SPAWNBLOCK_0.getId().getPath(),
			() -> new WindKnightSpawnblock0DisplayItem(PasterdreamModBlocks.WIND_KNIGHT_SPAWNBLOCK_0.get(), new Item.Properties()));
	public static final RegistryObject<Item> GOLDEN_FOX_SCULPTURE = REGISTRY.register(PasterdreamModBlocks.GOLDEN_FOX_SCULPTURE.getId().getPath(),
			() -> new GoldenFoxSculptureDisplayItem(PasterdreamModBlocks.GOLDEN_FOX_SCULPTURE.get(), new Item.Properties()));
	public static final RegistryObject<Item> DYEDREAM_GRASS = block(PasterdreamModBlocks.DYEDREAM_GRASS);
	public static final RegistryObject<Item> DYEDREAM_DIRT = block(PasterdreamModBlocks.DYEDREAM_DIRT);
	public static final RegistryObject<Item> DYEDREAM_SAND = block(PasterdreamModBlocks.DYEDREAM_SAND);
	public static final RegistryObject<Item> DYEDREAM_LOG = block(PasterdreamModBlocks.DYEDREAM_LOG);
	public static final RegistryObject<Item> DYEDREAM_WOOD = block(PasterdreamModBlocks.DYEDREAM_WOOD);
	public static final RegistryObject<Item> DYEDREAM_LEAVES = block(PasterdreamModBlocks.DYEDREAM_LEAVES);
	public static final RegistryObject<Item> DYEDREAM_WORLDTREE_LEAVES = block(PasterdreamModBlocks.DYEDREAM_WORLDTREE_LEAVES);
	public static final RegistryObject<Item> DYEDREAM_PLANKS = block(PasterdreamModBlocks.DYEDREAM_PLANKS);
	public static final RegistryObject<Item> DYEDREAM_PLANKS_STAIRS = block(PasterdreamModBlocks.DYEDREAM_PLANKS_STAIRS);
	public static final RegistryObject<Item> DYEDREAM_PLANKS_SLAB = block(PasterdreamModBlocks.DYEDREAM_PLANKS_SLAB);
	public static final RegistryObject<Item> DYEDREAM_PLANKS_FENCE = block(PasterdreamModBlocks.DYEDREAM_PLANKS_FENCE);
	public static final RegistryObject<Item> DYEDREAM_PLANKS_FENCEGATE = block(PasterdreamModBlocks.DYEDREAM_PLANKS_FENCEGATE);
	public static final RegistryObject<Item> DYEDREAM_PLANKS_PANE = block(PasterdreamModBlocks.DYEDREAM_PLANKS_PANE);
	public static final RegistryObject<Item> DYEDREAM_PLANKS_DOOR = doubleBlock(PasterdreamModBlocks.DYEDREAM_PLANKS_DOOR);
	public static final RegistryObject<Item> DYEDREAM_PLANKS_TRAPDOOR = block(PasterdreamModBlocks.DYEDREAM_PLANKS_TRAPDOOR);
	public static final RegistryObject<Item> DYEDREAM_PLANKS_PRESSURE_PLATE = block(PasterdreamModBlocks.DYEDREAM_PLANKS_PRESSURE_PLATE);
	public static final RegistryObject<Item> DYEDREAM_PLANKS_BUTTON = block(PasterdreamModBlocks.DYEDREAM_PLANKS_BUTTON);
	public static final RegistryObject<Item> DYEDREAMQUARTZ_ORE = block(PasterdreamModBlocks.DYEDREAMQUARTZ_ORE);
	public static final RegistryObject<Item> DYEDREAMDUST_ORE = block(PasterdreamModBlocks.DYEDREAMDUST_ORE);
	public static final RegistryObject<Item> AMBER_CANDY_ORE = block(PasterdreamModBlocks.AMBER_CANDY_ORE);
	public static final RegistryObject<Item> DYEDREAM_BLOCK = block(PasterdreamModBlocks.DYEDREAM_BLOCK);
	public static final RegistryObject<Item> PINKSLIME_BLOCK = block(PasterdreamModBlocks.PINKSLIME_BLOCK);
	public static final RegistryObject<Item> PINKAGARIC_0 = block(PasterdreamModBlocks.PINKAGARIC_0);
	public static final RegistryObject<Item> PINKAGARIC_1 = block(PasterdreamModBlocks.PINKAGARIC_1);
	public static final RegistryObject<Item> PINKAGARIC_2 = block(PasterdreamModBlocks.PINKAGARIC_2);
	public static final RegistryObject<Item> PINKAGARIC_3 = block(PasterdreamModBlocks.PINKAGARIC_3);
	public static final RegistryObject<Item> DYEDREAM_ICE = block(PasterdreamModBlocks.DYEDREAM_ICE);
	public static final RegistryObject<Item> DYEDREAM_PACKED_ICE = block(PasterdreamModBlocks.DYEDREAM_PACKED_ICE);
	public static final RegistryObject<Item> BIG_BUBBLE = block(PasterdreamModBlocks.BIG_BUBBLE);
	public static final RegistryObject<Item> DYEDREAMQUARTZ_BLOCK = block(PasterdreamModBlocks.DYEDREAMQUARTZ_BLOCK);
	public static final RegistryObject<Item> SMOOTH_DYEDREAMQUARTZ_BLOCK = block(PasterdreamModBlocks.SMOOTH_DYEDREAMQUARTZ_BLOCK);
	public static final RegistryObject<Item> BRICKS_DYEDREAMQUARTZ_BLOCK = block(PasterdreamModBlocks.BRICKS_DYEDREAMQUARTZ_BLOCK);
	public static final RegistryObject<Item> PILLAR_DYEDREAMQUARTZ_BLOCK = block(PasterdreamModBlocks.PILLAR_DYEDREAMQUARTZ_BLOCK);
	public static final RegistryObject<Item> CHISELED_DYEDREAMQUARTZ_BLOCK = block(PasterdreamModBlocks.CHISELED_DYEDREAMQUARTZ_BLOCK);
	public static final RegistryObject<Item> DYEDREAMQUARTZ_BLOCK_STAIRS = block(PasterdreamModBlocks.DYEDREAMQUARTZ_BLOCK_STAIRS);
	public static final RegistryObject<Item> DYEDREAMQUARTZ_BLOCK_SLAB = block(PasterdreamModBlocks.DYEDREAMQUARTZ_BLOCK_SLAB);
	public static final RegistryObject<Item> DYEDREAMQUARTZ_BLOCK_WALL = block(PasterdreamModBlocks.DYEDREAMQUARTZ_BLOCK_WALL);
	public static final RegistryObject<Item> DYEDREAM_LARTERN = block(PasterdreamModBlocks.DYEDREAM_LARTERN);
	public static final RegistryObject<Item> DYEDREAM_GLASS = block(PasterdreamModBlocks.DYEDREAM_GLASS);
	public static final RegistryObject<Item> DYEDREAM_GLASSPANE = block(PasterdreamModBlocks.DYEDREAM_GLASSPANE);
	public static final RegistryObject<Item> CARVE_DYEDREAM_GLASS = block(PasterdreamModBlocks.CARVE_DYEDREAM_GLASS);
	public static final RegistryObject<Item> CARVE_DYEDREAM_GLASSPANE = block(PasterdreamModBlocks.CARVE_DYEDREAM_GLASSPANE);
	public static final RegistryObject<Item> GOLD_CARVE_DYEDREAM_GLASS = block(PasterdreamModBlocks.GOLD_CARVE_DYEDREAM_GLASS);
	public static final RegistryObject<Item> GOLD_CARVE_DYEDREAM_GLASSPANE = block(PasterdreamModBlocks.GOLD_CARVE_DYEDREAM_GLASSPANE);
	public static final RegistryObject<Item> DYEDREAM_BUD_0 = block(PasterdreamModBlocks.DYEDREAM_BUD_0);
	public static final RegistryObject<Item> DYEDREAM_BUD_1 = block(PasterdreamModBlocks.DYEDREAM_BUD_1);
	public static final RegistryObject<Item> DYEDREAM_BUD_2 = block(PasterdreamModBlocks.DYEDREAM_BUD_2);
	public static final RegistryObject<Item> DYEDREAM_BUD_BLOCK = block(PasterdreamModBlocks.DYEDREAM_BUD_BLOCK);
	public static final RegistryObject<Item> DYEDREAM_BUD_STAIRS = block(PasterdreamModBlocks.DYEDREAM_BUD_STAIRS);
	public static final RegistryObject<Item> DYEDREAM_BUD_SLAB = block(PasterdreamModBlocks.DYEDREAM_BUD_SLAB);
	public static final RegistryObject<Item> DYEDREAM_BUD_WALL = block(PasterdreamModBlocks.DYEDREAM_BUD_WALL);
	public static final RegistryObject<Item> ICESTONE = block(PasterdreamModBlocks.ICESTONE);
	public static final RegistryObject<Item> ICE_BUD_0 = block(PasterdreamModBlocks.ICE_BUD_0);
	public static final RegistryObject<Item> CLOUD = block(PasterdreamModBlocks.CLOUD);
	public static final RegistryObject<Item> DARK_CLOUD = block(PasterdreamModBlocks.DARK_CLOUD);
	public static final RegistryObject<Item> POLISHED_CALCITE = block(PasterdreamModBlocks.POLISHED_CALCITE);
	public static final RegistryObject<Item> POLISHED_CALCITE_STAIRS = block(PasterdreamModBlocks.POLISHED_CALCITE_STAIRS);
	public static final RegistryObject<Item> POLISHED_CALCITE_SLAB = block(PasterdreamModBlocks.POLISHED_CALCITE_SLAB);
	public static final RegistryObject<Item> POLISHED_CALCITE_WALL = block(PasterdreamModBlocks.POLISHED_CALCITE_WALL);
	public static final RegistryObject<Item> CALCITE_TILES = block(PasterdreamModBlocks.CALCITE_TILES);
	public static final RegistryObject<Item> CALCITE_TILES_STAIRS = block(PasterdreamModBlocks.CALCITE_TILES_STAIRS);
	public static final RegistryObject<Item> CALCITE_TILES_SLAB = block(PasterdreamModBlocks.CALCITE_TILES_SLAB);
	public static final RegistryObject<Item> CALCITE_TILES_WALL = block(PasterdreamModBlocks.CALCITE_TILES_WALL);
	public static final RegistryObject<Item> TITANIUM_ORE = block(PasterdreamModBlocks.TITANIUM_ORE);
	public static final RegistryObject<Item> DEEPSLATE_TITANIUM_ORE = block(PasterdreamModBlocks.DEEPSLATE_TITANIUM_ORE);
	public static final RegistryObject<Item> MOLTENGOLD_ORE = block(PasterdreamModBlocks.MOLTENGOLD_ORE);
	public static final RegistryObject<Item> SOUL_ORE = block(PasterdreamModBlocks.SOUL_ORE);
	public static final RegistryObject<Item> RAW_TITANIUM_BLOCK = block(PasterdreamModBlocks.RAW_TITANIUM_BLOCK);
	public static final RegistryObject<Item> SALT_BLOCK = block(PasterdreamModBlocks.SALT_BLOCK);
	public static final RegistryObject<Item> TITANIUM_BLOCK = block(PasterdreamModBlocks.TITANIUM_BLOCK);
	public static final RegistryObject<Item> MOLTENGOLD_BLOCK = block(PasterdreamModBlocks.MOLTENGOLD_BLOCK);
	public static final RegistryObject<Item> CHARGED_AMETHYST_BLOCK = block(PasterdreamModBlocks.CHARGED_AMETHYST_BLOCK);
	public static final RegistryObject<Item> SHADOW_BLOCK = block(PasterdreamModBlocks.SHADOW_BLOCK);
	public static final RegistryObject<Item> THICK_SHADOW_BLOCK = block(PasterdreamModBlocks.THICK_SHADOW_BLOCK);
	public static final RegistryObject<Item> SHADOW_STONE = block(PasterdreamModBlocks.SHADOW_STONE);
	public static final RegistryObject<Item> SHADOW_STONE_BRICK = block(PasterdreamModBlocks.SHADOW_STONE_BRICK);
	public static final RegistryObject<Item> SHADOW_STONE_BRICK_STAIRS = block(PasterdreamModBlocks.SHADOW_STONE_BRICK_STAIRS);
	public static final RegistryObject<Item> SHADOW_STONE_BRICK_WALL = block(PasterdreamModBlocks.SHADOW_STONE_BRICK_WALL);
	public static final RegistryObject<Item> SHADOW_STONE_BRICK_SLAB = block(PasterdreamModBlocks.SHADOW_STONE_BRICK_SLAB);
	public static final RegistryObject<Item> SHADOW_STONE_BRICKS = block(PasterdreamModBlocks.SHADOW_STONE_BRICKS);
	public static final RegistryObject<Item> SHADOW_STONE_BRICKS_STAIRS = block(PasterdreamModBlocks.SHADOW_STONE_BRICKS_STAIRS);
	public static final RegistryObject<Item> SHADOW_STONE_BRICKS_SLAB = block(PasterdreamModBlocks.SHADOW_STONE_BRICKS_SLAB);
	public static final RegistryObject<Item> SHADOW_STONE_BRICKS_WALL = block(PasterdreamModBlocks.SHADOW_STONE_BRICKS_WALL);
	public static final RegistryObject<Item> SHADOW_STONE_TILES = block(PasterdreamModBlocks.SHADOW_STONE_TILES);
	public static final RegistryObject<Item> SHADOW_STONE_TILES_STAIRS = block(PasterdreamModBlocks.SHADOW_STONE_TILES_STAIRS);
	public static final RegistryObject<Item> SHADOW_STONE_TILES_SLAB = block(PasterdreamModBlocks.SHADOW_STONE_TILES_SLAB);
	public static final RegistryObject<Item> SHADOW_STONE_TILES_WALL = block(PasterdreamModBlocks.SHADOW_STONE_TILES_WALL);
	public static final RegistryObject<Item> CRACKED_SHADOW_STONE_BRICK = block(PasterdreamModBlocks.CRACKED_SHADOW_STONE_BRICK);
	public static final RegistryObject<Item> CHISELED_SHADOW_STONE_BRICK = block(PasterdreamModBlocks.CHISELED_SHADOW_STONE_BRICK);
	public static final RegistryObject<Item> SHADOW_LIGHT_0 = block(PasterdreamModBlocks.SHADOW_LIGHT_0);
	public static final RegistryObject<Item> SHADOW_SHROOMLIGHT = block(PasterdreamModBlocks.SHADOW_SHROOMLIGHT);
	public static final RegistryObject<Item> SHADOW_NYLIUM = block(PasterdreamModBlocks.SHADOW_NYLIUM);
	public static final RegistryObject<Item> SHADOW_WART_BLOCK = block(PasterdreamModBlocks.SHADOW_WART_BLOCK);
	public static final RegistryObject<Item> SHADOW_STEM = block(PasterdreamModBlocks.SHADOW_STEM);
	public static final RegistryObject<Item> SHADOW_HYPHAE = block(PasterdreamModBlocks.SHADOW_HYPHAE);
	public static final RegistryObject<Item> STRIPPED_SHADOW_STEM = block(PasterdreamModBlocks.STRIPPED_SHADOW_STEM);
	public static final RegistryObject<Item> STRIPPED_SHADOW_HYPHAE = block(PasterdreamModBlocks.STRIPPED_SHADOW_HYPHAE);
	public static final RegistryObject<Item> SHADOW_PLANKS = block(PasterdreamModBlocks.SHADOW_PLANKS);
	public static final RegistryObject<Item> SHADOW_PLANKS_STAIRS = block(PasterdreamModBlocks.SHADOW_PLANKS_STAIRS);
	public static final RegistryObject<Item> SHADOW_PLANKS_SLAB = block(PasterdreamModBlocks.SHADOW_PLANKS_SLAB);
	public static final RegistryObject<Item> SHADOW_PLANKS_FENCE = block(PasterdreamModBlocks.SHADOW_PLANKS_FENCE);
	public static final RegistryObject<Item> SHADOW_PLANKS_FENCEGATE = block(PasterdreamModBlocks.SHADOW_PLANKS_FENCEGATE);
	public static final RegistryObject<Item> SHADOW_PLANKS_PANE = block(PasterdreamModBlocks.SHADOW_PLANKS_PANE);
	public static final RegistryObject<Item> SHADOW_PLANKS_DOOR = doubleBlock(PasterdreamModBlocks.SHADOW_PLANKS_DOOR);
	public static final RegistryObject<Item> SHADOW_PLANKS_TRAPDOOR = block(PasterdreamModBlocks.SHADOW_PLANKS_TRAPDOOR);
	public static final RegistryObject<Item> SHADOW_PLANKS_PRESSURE_PLATE = block(PasterdreamModBlocks.SHADOW_PLANKS_PRESSURE_PLATE);
	public static final RegistryObject<Item> SHADOW_PLANKS_BUTTON = block(PasterdreamModBlocks.SHADOW_PLANKS_BUTTON);
	public static final RegistryObject<Item> RUST_BLACK_METAL_BLOCK = block(PasterdreamModBlocks.RUST_BLACK_METAL_BLOCK);
	public static final RegistryObject<Item> RUST_BLACK_METAL_BLOCK_WALL = block(PasterdreamModBlocks.RUST_BLACK_METAL_BLOCK_WALL);
	public static final RegistryObject<Item> RUST_BLACK_METAL_BLOCK_BARS = block(PasterdreamModBlocks.RUST_BLACK_METAL_BLOCK_BARS);
	public static final RegistryObject<Item> BLACKMETAL_BLOCK = block(PasterdreamModBlocks.BLACKMETAL_BLOCK);
	public static final RegistryObject<Item> ARMOR_WRECK_BLOCK_0 = block(PasterdreamModBlocks.ARMOR_WRECK_BLOCK_0);
	public static final RegistryObject<Item> ARMOR_WRECK_BLOCK_1 = block(PasterdreamModBlocks.ARMOR_WRECK_BLOCK_1);
	public static final RegistryObject<Item> ARMOR_WRECK_BLOCK_2 = block(PasterdreamModBlocks.ARMOR_WRECK_BLOCK_2);
	public static final RegistryObject<Item> ARMOR_WRECK_BLOCK_3 = block(PasterdreamModBlocks.ARMOR_WRECK_BLOCK_3);
	public static final RegistryObject<Item> ARMOR_WRECK_BLOCK_4 = block(PasterdreamModBlocks.ARMOR_WRECK_BLOCK_4);
	public static final RegistryObject<Item> SHADOW_DUNGEON_BLOCK_0 = block(PasterdreamModBlocks.SHADOW_DUNGEON_BLOCK_0);
	public static final RegistryObject<Item> SHADOW_DUNGEON_BLOCK_1 = block(PasterdreamModBlocks.SHADOW_DUNGEON_BLOCK_1);
	public static final RegistryObject<Item> SHADOW_DUNGEON_BLOCK_2 = block(PasterdreamModBlocks.SHADOW_DUNGEON_BLOCK_2);
	public static final RegistryObject<Item> SHADOW_DUNGEON_BLOCK_3 = block(PasterdreamModBlocks.SHADOW_DUNGEON_BLOCK_3);
	public static final RegistryObject<Item> SHADOW_DUNGEON_BLOCK_4 = block(PasterdreamModBlocks.SHADOW_DUNGEON_BLOCK_4);
	public static final RegistryObject<Item> SHADOW_DUNGEON_BLOCK_5 = block(PasterdreamModBlocks.SHADOW_DUNGEON_BLOCK_5);
	public static final RegistryObject<Item> SHADOW_DUNGEON_BLOCK_6 = block(PasterdreamModBlocks.SHADOW_DUNGEON_BLOCK_6);
	public static final RegistryObject<Item> LOOSE_SHADOW_DUNGEON_BLOCK = block(PasterdreamModBlocks.LOOSE_SHADOW_DUNGEON_BLOCK);
	public static final RegistryObject<Item> SHADOWSHELF_0 = block(PasterdreamModBlocks.SHADOWSHELF_0);
	public static final RegistryObject<Item> SHADOWSHELF_1 = block(PasterdreamModBlocks.SHADOWSHELF_1);
	public static final RegistryObject<Item> SHADOWSHELF_2 = block(PasterdreamModBlocks.SHADOWSHELF_2);
	public static final RegistryObject<Item> SHADOWSHELF_3 = block(PasterdreamModBlocks.SHADOWSHELF_3);
	public static final RegistryObject<Item> SHADOW_ARENA_BLOCK_0 = block(PasterdreamModBlocks.SHADOW_ARENA_BLOCK_0);
	public static final RegistryObject<Item> SHADOW_FISSURE_0 = block(PasterdreamModBlocks.SHADOW_FISSURE_0);
	public static final RegistryObject<Item> SHADOW_FISSURE_1 = block(PasterdreamModBlocks.SHADOW_FISSURE_1);
	public static final RegistryObject<Item> SHADOW_FISSURE_2 = block(PasterdreamModBlocks.SHADOW_FISSURE_2);
	public static final RegistryObject<Item> SHADOW_FISSURE_3 = block(PasterdreamModBlocks.SHADOW_FISSURE_3);
	public static final RegistryObject<Item> SHADOW_FISSURE_4 = block(PasterdreamModBlocks.SHADOW_FISSURE_4);
	public static final RegistryObject<Item> SHADOW_FISSURE_5 = block(PasterdreamModBlocks.SHADOW_FISSURE_5);
	public static final RegistryObject<Item> CYAN_MOSS_STONE = block(PasterdreamModBlocks.CYAN_MOSS_STONE);
	public static final RegistryObject<Item> CYAN_STONE = block(PasterdreamModBlocks.CYAN_STONE);
	public static final RegistryObject<Item> WHITE_SAND = block(PasterdreamModBlocks.WHITE_SAND);
	public static final RegistryObject<Item> THICK_CLOUD = block(PasterdreamModBlocks.THICK_CLOUD);
	public static final RegistryObject<Item> WINDMOOR_LOG = block(PasterdreamModBlocks.WINDMOOR_LOG);
	public static final RegistryObject<Item> WINDMOOR_WOOD = block(PasterdreamModBlocks.WINDMOOR_WOOD);
	public static final RegistryObject<Item> WINDMOOR_LEAVES_0 = block(PasterdreamModBlocks.WINDMOOR_LEAVES_0);
	public static final RegistryObject<Item> WINDMOOR_LEAVES_1 = block(PasterdreamModBlocks.WINDMOOR_LEAVES_1);
	public static final RegistryObject<Item> WINDMOOR_LEAVES_2 = block(PasterdreamModBlocks.WINDMOOR_LEAVES_2);
	public static final RegistryObject<Item> CYAN_STONE_BRICKS = block(PasterdreamModBlocks.CYAN_STONE_BRICKS);
	public static final RegistryObject<Item> CYAN_STONE_BRICK_STAIRS = block(PasterdreamModBlocks.CYAN_STONE_BRICK_STAIRS);
	public static final RegistryObject<Item> CYAN_STONE_BRICK_SLAB = block(PasterdreamModBlocks.CYAN_STONE_BRICK_SLAB);
	public static final RegistryObject<Item> CYAN_STONE_BRICK_WALL = block(PasterdreamModBlocks.CYAN_STONE_BRICK_WALL);
	public static final RegistryObject<Item> MOSSY_CYAN_STONE_BRICKS = block(PasterdreamModBlocks.MOSSY_CYAN_STONE_BRICKS);
	public static final RegistryObject<Item> MOSSY_CYAN_STONE_BRICK_STAIRS = block(PasterdreamModBlocks.MOSSY_CYAN_STONE_BRICK_STAIRS);
	public static final RegistryObject<Item> MOSSY_CYAN_STONE_BRICK_SLAB = block(PasterdreamModBlocks.MOSSY_CYAN_STONE_BRICK_SLAB);
	public static final RegistryObject<Item> MOSSY_CYAN_STONE_BRICK_WALL = block(PasterdreamModBlocks.MOSSY_CYAN_STONE_BRICK_WALL);
	public static final RegistryObject<Item> CHISELED_CYAN_STONE_BRICKS = block(PasterdreamModBlocks.CHISELED_CYAN_STONE_BRICKS);
	public static final RegistryObject<Item> CYAN_STONE_PILLAR = block(PasterdreamModBlocks.CYAN_STONE_PILLAR);
	public static final RegistryObject<Item> WINDMOOR_PLANKS = block(PasterdreamModBlocks.WINDMOOR_PLANKS);
	public static final RegistryObject<Item> WINDMOOR_STAIRS = block(PasterdreamModBlocks.WINDMOOR_STAIRS);
	public static final RegistryObject<Item> WINDMOOR_SLAB = block(PasterdreamModBlocks.WINDMOOR_SLAB);
	public static final RegistryObject<Item> WINDMOOR_FENCE = block(PasterdreamModBlocks.WINDMOOR_FENCE);
	public static final RegistryObject<Item> WINDMOOR_FENCE_GATE = block(PasterdreamModBlocks.WINDMOOR_FENCE_GATE);
	public static final RegistryObject<Item> WINDMOOR_DOOR = doubleBlock(PasterdreamModBlocks.WINDMOOR_DOOR);
	public static final RegistryObject<Item> WINDMOOR_TRAPDOOR = block(PasterdreamModBlocks.WINDMOOR_TRAPDOOR);
	public static final RegistryObject<Item> WINDMOOR_PRESSURE_PLATE = block(PasterdreamModBlocks.WINDMOOR_PRESSURE_PLATE);
	public static final RegistryObject<Item> WINDMOOR_BUTTON = block(PasterdreamModBlocks.WINDMOOR_BUTTON);
	public static final RegistryObject<Item> CYAN_STONE_PRESSURE_PLATE = block(PasterdreamModBlocks.CYAN_STONE_PRESSURE_PLATE);
	public static final RegistryObject<Item> CYAN_STONE_BUTTON = block(PasterdreamModBlocks.CYAN_STONE_BUTTON);
	public static final RegistryObject<Item> CLARITY_GLASS = block(PasterdreamModBlocks.CLARITY_GLASS);
	public static final RegistryObject<Item> CARVE_CLARITY_GLASS = block(PasterdreamModBlocks.CARVE_CLARITY_GLASS);
	public static final RegistryObject<Item> FRAME_CLARITY_GLASS = block(PasterdreamModBlocks.FRAME_CLARITY_GLASS);
	public static final RegistryObject<Item> CLARITY_GLASSPANE = block(PasterdreamModBlocks.CLARITY_GLASSPANE);
	public static final RegistryObject<Item> CARVE_CLARITY_GLASSPANE = block(PasterdreamModBlocks.CARVE_CLARITY_GLASSPANE);
	public static final RegistryObject<Item> FRAME_CLARITY_GLASSPANE = block(PasterdreamModBlocks.FRAME_CLARITY_GLASSPANE);
	public static final RegistryObject<Item> BREAKWIND_CURTAIN = block(PasterdreamModBlocks.BREAKWIND_CURTAIN);
	public static final RegistryObject<Item> STRIPPED_WINDMOOR_LOG = block(PasterdreamModBlocks.STRIPPED_WINDMOOR_LOG);
	public static final RegistryObject<Item> STRIPPED_WINDMOOR_WOOD = block(PasterdreamModBlocks.STRIPPED_WINDMOOR_WOOD);
	public static final RegistryObject<Item> WINDRUNNER_CRYSTAL_ORE = block(PasterdreamModBlocks.WINDRUNNER_CRYSTAL_ORE);
	public static final RegistryObject<Item> WINDRUNNER_CRYSTAL_BLOCK = block(PasterdreamModBlocks.WINDRUNNER_CRYSTAL_BLOCK);
	public static final RegistryObject<Item> CONGEAL_WIND_BLOCK = block(PasterdreamModBlocks.CONGEAL_WIND_BLOCK);
	public static final RegistryObject<Item> CONGEAL_WIND_ORE = block(PasterdreamModBlocks.CONGEAL_WIND_ORE);
	public static final RegistryObject<Item> WIND_IRON_BLOCK = block(PasterdreamModBlocks.WIND_IRON_BLOCK);
	public static final RegistryObject<Item> EJECTION_PRESSURE_PLATE = block(PasterdreamModBlocks.EJECTION_PRESSURE_PLATE);
	public static final RegistryObject<Item> WINDIRON_BARS = block(PasterdreamModBlocks.WINDIRON_BARS);
	public static final RegistryObject<Item> EJECTION_PRESSURE_BLOCK = block(PasterdreamModBlocks.EJECTION_PRESSURE_BLOCK);
	public static final RegistryObject<Item> TABITEM_1 = REGISTRY.register("tabitem_1", () -> new Tabitem1Item());
	public static final RegistryObject<Item> DREAMWISH = REGISTRY.register("dreamwish", () -> new DreamwishItem());
	public static final RegistryObject<Item> CREATIVE_SWORD = REGISTRY.register("creative_sword", () -> new CreativeSwordItem());
	public static final RegistryObject<Item> STARCALL_BLOCK = block(PasterdreamModBlocks.STARCALL_BLOCK);
	public static final RegistryObject<Item> EXCAVATOR = REGISTRY.register("excavator", () -> new ExcavatorItem());
	public static final RegistryObject<Item> DYEDREAM_CRACK = block(PasterdreamModBlocks.DYEDREAM_CRACK);
	public static final RegistryObject<Item> STARCALL_CRACK = block(PasterdreamModBlocks.STARCALL_CRACK);
	public static final RegistryObject<Item> LOOTSTABLE_CREATE_0 = REGISTRY.register("lootstable_create_0", () -> new LootstableCreate0Item());
	public static final RegistryObject<Item> LOOTSTABLE_CREATE_1 = REGISTRY.register("lootstable_create_1", () -> new LootstableCreate1Item());
	public static final RegistryObject<Item> LOOTSTABLE_CREATE_2 = REGISTRY.register("lootstable_create_2", () -> new LootstableCreate2Item());
	public static final RegistryObject<Item> LOOTSTABLE_CREATE_3 = REGISTRY.register("lootstable_create_3", () -> new LootstableCreate3Item());
	public static final RegistryObject<Item> LOOTSTABLE_CREATE_4 = REGISTRY.register("lootstable_create_4", () -> new LootstableCreate4Item());
	public static final RegistryObject<Item> LOOTSTABLE_CREATE_5 = REGISTRY.register("lootstable_create_5", () -> new LootstableCreate5Item());
	public static final RegistryObject<Item> LOOTSTABLE_CREATE_6 = REGISTRY.register("lootstable_create_6", () -> new LootstableCreate6Item());
	public static final RegistryObject<Item> LOOTSTABLE_CREATE_7 = REGISTRY.register("lootstable_create_7", () -> new LootstableCreate7Item());
	public static final RegistryObject<Item> LOOTSTABLE_CREATE_8 = REGISTRY.register("lootstable_create_8", () -> new LootstableCreate8Item());
	public static final RegistryObject<Item> LOOTSTABLE_CREATE_9 = REGISTRY.register("lootstable_create_9", () -> new LootstableCreate9Item());
	public static final RegistryObject<Item> GUARD_BLOCK = block(PasterdreamModBlocks.GUARD_BLOCK);
	public static final RegistryObject<Item> RESTRAINMOVE_BLOCK = block(PasterdreamModBlocks.RESTRAINMOVE_BLOCK);
	public static final RegistryObject<Item> DREAM_SPAWNER_0 = block(PasterdreamModBlocks.DREAM_SPAWNER_0);
	public static final RegistryObject<Item> DREAM_SPAWNER_1 = block(PasterdreamModBlocks.DREAM_SPAWNER_1);
	public static final RegistryObject<Item> PINK_CHICKEN_SPAWN_EGG = REGISTRY.register("pink_chicken_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.PINK_CHICKEN, -4354, -850762, new Item.Properties()));
	public static final RegistryObject<Item> PINK_SLIME_SPAWN_EGG = REGISTRY.register("pink_slime_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.PINK_SLIME, -1068828, -885304, new Item.Properties()));
	public static final RegistryObject<Item> BASALT_SNAIL_SPAWN_EGG = REGISTRY.register("basalt_snail_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.BASALT_SNAIL, -10922393, -12700090, new Item.Properties()));
	public static final RegistryObject<Item> SPORE_ENTITY_SPAWN_EGG = REGISTRY.register("spore_entity_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.SPORE_ENTITY, -2883673, -8331416, new Item.Properties()));
	public static final RegistryObject<Item> SHADOW_GOLEM_SPAWN_EGG = REGISTRY.register("shadow_golem_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.SHADOW_GOLEM, -15132378, -5790287, new Item.Properties()));
	public static final RegistryObject<Item> TERRORBEAK_SPAWN_EGG = REGISTRY.register("terrorbeak_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.TERRORBEAK, -16777216, -13487565, new Item.Properties()));
	public static final RegistryObject<Item> SHADOW_HAND_SPAWN_EGG = REGISTRY.register("shadow_hand_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.SHADOW_HAND, -12763843, -16316665, new Item.Properties()));
	public static final RegistryObject<Item> CRAZY_TERRORBEAK_SPAWN_EGG = REGISTRY.register("crazy_terrorbeak_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.CRAZY_TERRORBEAK, -16777216, -11271421, new Item.Properties()));
	public static final RegistryObject<Item> SHADOW_GHOST_SPAWN_EGG = REGISTRY.register("shadow_ghost_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.SHADOW_GHOST, -591623, -9012613, new Item.Properties()));
	public static final RegistryObject<Item> SHADOW_SQUEAL_GHOST_SPAWN_EGG = REGISTRY.register("shadow_squeal_ghost_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.SHADOW_SQUEAL_GHOST, -722951, -5317392, new Item.Properties()));
	public static final RegistryObject<Item> SHADOW_SQUEAL_GHOST_0_SPAWN_EGG = REGISTRY.register("shadow_squeal_ghost_0_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.SHADOW_SQUEAL_GHOST_0, -985609, -9851188, new Item.Properties()));
	public static final RegistryObject<Item> FRIENDLY_GHOST_SPAWN_EGG = REGISTRY.register("friendly_ghost_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.FRIENDLY_GHOST, -985866, -4334102, new Item.Properties()));
	public static final RegistryObject<Item> BLACK_BEETLE_SPAWN_EGG = REGISTRY.register("black_beetle_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.BLACK_BEETLE, -2698583, -11316914, new Item.Properties()));
	public static final RegistryObject<Item> BLACK_BEETLE_MOTHER_SPAWN_EGG = REGISTRY.register("black_beetle_mother_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.BLACK_BEETLE_MOTHER, -2436181, -9876718, new Item.Properties()));
	public static final RegistryObject<Item> SHADOW_NPC_0_SPAWN_EGG = REGISTRY.register("shadow_npc_0_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.SHADOW_NPC_0, -15592942, -11776948, new Item.Properties()));
	public static final RegistryObject<Item> WEAKENESS_TERRORBEAK_SPAWN_EGG = REGISTRY.register("weakeness_terrorbeak_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.WEAKENESS_TERRORBEAK, -16777216, -13421773, new Item.Properties()));
	public static final RegistryObject<Item> AARONCOS_LEFTHAND_0_SPAWN_EGG = REGISTRY.register("aaroncos_lefthand_0_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.AARONCOS_LEFTHAND_0, -15987700, -11246213, new Item.Properties()));
	public static final RegistryObject<Item> AARONCOS_RIGHTHAND_0_SPAWN_EGG = REGISTRY.register("aaroncos_righthand_0_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.AARONCOS_RIGHTHAND_0, -15593198, -11189920, new Item.Properties()));
	public static final RegistryObject<Item> SHADOW_TUNE_TOTEM_SPAWN_EGG = REGISTRY.register("shadow_tune_totem_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.SHADOW_TUNE_TOTEM, -15658735, -4473925, new Item.Properties()));
	public static final RegistryObject<Item> SHAKING_CRYSTAL_SPAWN_EGG = REGISTRY.register("shaking_crystal_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.SHAKING_CRYSTAL, -13422280, -11774866, new Item.Properties()));
	public static final RegistryObject<Item> BONE_WING_SPAWN_EGG = REGISTRY.register("bone_wing_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.BONE_WING, -1186636, -5296106, new Item.Properties()));
	public static final RegistryObject<Item> ASH_BONE_WING_SPAWN_EGG = REGISTRY.register("ash_bone_wing_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.ASH_BONE_WING, -10527144, -5419498, new Item.Properties()));
	public static final RegistryObject<Item> FIREFLY_SPAWN_EGG = REGISTRY.register("firefly_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.FIREFLY, -12570340, -5382814, new Item.Properties()));
	public static final RegistryObject<Item> THUNDERCLOUD_SPAWN_EGG = REGISTRY.register("thundercloud_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.THUNDERCLOUD, -8155750, -14399334, new Item.Properties()));
	public static final RegistryObject<Item> HIGHVOLTAGE_THUNDERCLOUD_SPAWN_EGG = REGISTRY.register("highvoltage_thundercloud_spawn_egg",
			() -> new ForgeSpawnEggItem(PasterdreamModEntities.HIGHVOLTAGE_THUNDERCLOUD, -8155750, -14339416, new Item.Properties()));
	public static final RegistryObject<Item> JELLYFISH_SPAWN_EGG = REGISTRY.register("jellyfish_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.JELLYFISH, -4134166, -8542261, new Item.Properties()));
	public static final RegistryObject<Item> WIND_KNIGHT_SPAWN_EGG = REGISTRY.register("wind_knight_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.WIND_KNIGHT, -1907228, -9642058, new Item.Properties()));
	public static final RegistryObject<Item> GOLDEN_FOX_SPAWN_EGG = REGISTRY.register("golden_fox_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.GOLDEN_FOX, -134806, -1395432, new Item.Properties()));
	public static final RegistryObject<Item> SHADOW_VORTEX = REGISTRY.register(PasterdreamModBlocks.SHADOW_VORTEX.getId().getPath(), () -> new ShadowVortexDisplayItem(PasterdreamModBlocks.SHADOW_VORTEX.get(), new Item.Properties()));
	public static final RegistryObject<Item> AARONCOS_ARENA_CREATE = REGISTRY.register("aaroncos_arena_create", () -> new AaroncosArenaCreateItem());
	public static final RegistryObject<Item> AARONCOS_ARENA_PORTALS = block(PasterdreamModBlocks.AARONCOS_ARENA_PORTALS);
	public static final RegistryObject<Item> WORKSHOP_CAULDEON = block(PasterdreamModBlocks.WORKSHOP_CAULDEON);
	public static final RegistryObject<Item> WORKSHOP_BLAST = block(PasterdreamModBlocks.WORKSHOP_BLAST);
	public static final RegistryObject<Item> WORKSHOP_ANVIL = block(PasterdreamModBlocks.WORKSHOP_ANVIL);
	public static final RegistryObject<Item> WORKSHOP_GRIND = block(PasterdreamModBlocks.WORKSHOP_GRIND);
	public static final RegistryObject<Item> DREAM_METER = REGISTRY.register("dream_meter", () -> new DreamMeterItem());
	public static final RegistryObject<Item> DYEDREAM_SAPLING = block(PasterdreamModBlocks.DYEDREAM_SAPLING);
	public static final RegistryObject<Item> CROP_0A = block(PasterdreamModBlocks.CROP_0A);
	public static final RegistryObject<Item> CROP_0B = block(PasterdreamModBlocks.CROP_0B);
	public static final RegistryObject<Item> CROP_2A = block(PasterdreamModBlocks.CROP_2A);
	public static final RegistryObject<Item> CROP_2B = block(PasterdreamModBlocks.CROP_2B);
	public static final RegistryObject<Item> CROP_3A = block(PasterdreamModBlocks.CROP_3A);
	public static final RegistryObject<Item> CROP_3B = block(PasterdreamModBlocks.CROP_3B);
	public static final RegistryObject<Item> GRASS_3 = block(PasterdreamModBlocks.GRASS_3);
	public static final RegistryObject<Item> GRASS_4 = doubleBlock(PasterdreamModBlocks.GRASS_4);
	public static final RegistryObject<Item> FLOWER_8 = block(PasterdreamModBlocks.FLOWER_8);
	public static final RegistryObject<Item> FLOWER_10 = doubleBlock(PasterdreamModBlocks.FLOWER_10);
	public static final RegistryObject<Item> GRASS_5 = block(PasterdreamModBlocks.GRASS_5);
	public static final RegistryObject<Item> GRASS_6 = block(PasterdreamModBlocks.GRASS_6);
	public static final RegistryObject<Item> GRASS_7 = block(PasterdreamModBlocks.GRASS_7);
	public static final RegistryObject<Item> FLOWER_9 = block(PasterdreamModBlocks.FLOWER_9);
	public static final RegistryObject<Item> FLOWER_13 = block(PasterdreamModBlocks.FLOWER_13);
	public static final RegistryObject<Item> FLOWER_14 = block(PasterdreamModBlocks.FLOWER_14);
	public static final RegistryObject<Item> VINE_0 = block(PasterdreamModBlocks.VINE_0);
	public static final RegistryObject<Item> DYEDREAM_LILY_PAD = block(PasterdreamModBlocks.DYEDREAM_LILY_PAD);
	public static final RegistryObject<Item> DYEDREAM_SEAGRASS = block(PasterdreamModBlocks.DYEDREAM_SEAGRASS);
	public static final RegistryObject<Item> DYEDREAM_LOTUS = block(PasterdreamModBlocks.DYEDREAM_LOTUS);
	public static final RegistryObject<Item> FLOWER_11 = doubleBlock(PasterdreamModBlocks.FLOWER_11);
	public static final RegistryObject<Item> FLOWER_12 = doubleBlock(PasterdreamModBlocks.FLOWER_12);
	public static final RegistryObject<Item> FLOWER_16 = block(PasterdreamModBlocks.FLOWER_16);
	public static final RegistryObject<Item> FLOWER_17 = block(PasterdreamModBlocks.FLOWER_17);
	public static final RegistryObject<Item> CROP_1A = block(PasterdreamModBlocks.CROP_1A);
	public static final RegistryObject<Item> CROP_1B = block(PasterdreamModBlocks.CROP_1B);
	public static final RegistryObject<Item> CROP_4A = block(PasterdreamModBlocks.CROP_4A);
	public static final RegistryObject<Item> CROP_4B = block(PasterdreamModBlocks.CROP_4B);
	public static final RegistryObject<Item> FLOWER_1 = block(PasterdreamModBlocks.FLOWER_1);
	public static final RegistryObject<Item> FLOWER_2 = block(PasterdreamModBlocks.FLOWER_2);
	public static final RegistryObject<Item> JUNGLE_SPORE_PLANT = block(PasterdreamModBlocks.JUNGLE_SPORE_PLANT);
	public static final RegistryObject<Item> FLOWER_5 = block(PasterdreamModBlocks.FLOWER_5);
	public static final RegistryObject<Item> GRASS_1 = block(PasterdreamModBlocks.GRASS_1);
	public static final RegistryObject<Item> FLOWER_3 = block(PasterdreamModBlocks.FLOWER_3);
	public static final RegistryObject<Item> GRASS_2 = block(PasterdreamModBlocks.GRASS_2);
	public static final RegistryObject<Item> GOLDENROD = block(PasterdreamModBlocks.GOLDENROD);
	public static final RegistryObject<Item> FOURLEAF_CLOVER = block(PasterdreamModBlocks.FOURLEAF_CLOVER);
	public static final RegistryObject<Item> FLOWER_6 = block(PasterdreamModBlocks.FLOWER_6);
	public static final RegistryObject<Item> FLOWER_7 = doubleBlock(PasterdreamModBlocks.FLOWER_7);
	public static final RegistryObject<Item> SHADOW_FUNGUS = block(PasterdreamModBlocks.SHADOW_FUNGUS);
	public static final RegistryObject<Item> GRASS_8 = block(PasterdreamModBlocks.GRASS_8);
	public static final RegistryObject<Item> GRASS_9 = block(PasterdreamModBlocks.GRASS_9);
	public static final RegistryObject<Item> GRASS_10 = doubleBlock(PasterdreamModBlocks.GRASS_10);
	public static final RegistryObject<Item> GRASS_11 = block(PasterdreamModBlocks.GRASS_11);
	public static final RegistryObject<Item> GRASS_12 = block(PasterdreamModBlocks.GRASS_12);
	public static final RegistryObject<Item> FLOWER_15 = block(PasterdreamModBlocks.FLOWER_15);
	public static final RegistryObject<Item> GRASS_13 = block(PasterdreamModBlocks.GRASS_13);
	public static final RegistryObject<Item> GRASS_14 = block(PasterdreamModBlocks.GRASS_14);
	public static final RegistryObject<Item> GRASS_15 = doubleBlock(PasterdreamModBlocks.GRASS_15);
	public static final RegistryObject<Item> FLOWER_18 = doubleBlock(PasterdreamModBlocks.FLOWER_18);
	public static final RegistryObject<Item> FIG_VINE = block(PasterdreamModBlocks.FIG_VINE);
	public static final RegistryObject<Item> COPPER_SWORD = REGISTRY.register("copper_sword", () -> new CopperSwordItem());
	public static final RegistryObject<Item> COPPER_PICKAXE = REGISTRY.register("copper_pickaxe", () -> new CopperPickaxeItem());
	public static final RegistryObject<Item> COPPER_AXE = REGISTRY.register("copper_axe", () -> new CopperAxeItem());
	public static final RegistryObject<Item> COPPER_SHOVEL = REGISTRY.register("copper_shovel", () -> new CopperShovelItem());
	public static final RegistryObject<Item> COPPER_HOE = REGISTRY.register("copper_hoe", () -> new CopperHoeItem());
	public static final RegistryObject<Item> COPPER_ARMOR_HELMET = REGISTRY.register("copper_armor_helmet", () -> new CopperArmorItem.Helmet());
	public static final RegistryObject<Item> COPPER_ARMOR_CHESTPLATE = REGISTRY.register("copper_armor_chestplate", () -> new CopperArmorItem.Chestplate());
	public static final RegistryObject<Item> COPPER_ARMOR_LEGGINGS = REGISTRY.register("copper_armor_leggings", () -> new CopperArmorItem.Leggings());
	public static final RegistryObject<Item> COPPER_ARMOR_BOOTS = REGISTRY.register("copper_armor_boots", () -> new CopperArmorItem.Boots());
	public static final RegistryObject<Item> TITANIUM_SWORD = REGISTRY.register("titanium_sword", () -> new TitaniumSwordItem());
	public static final RegistryObject<Item> TITANIUM_PICKAXE = REGISTRY.register("titanium_pickaxe", () -> new TitaniumPickaxeItem());
	public static final RegistryObject<Item> TITANIUM_AXE = REGISTRY.register("titanium_axe", () -> new TitaniumAxeItem());
	public static final RegistryObject<Item> TITANIUM_SHOVEL = REGISTRY.register("titanium_shovel", () -> new TitaniumShovelItem());
	public static final RegistryObject<Item> TITANIUM_HOE = REGISTRY.register("titanium_hoe", () -> new TitaniumHoeItem());
	public static final RegistryObject<Item> TITANIUM_ARMOR_HELMET = REGISTRY.register("titanium_armor_helmet", () -> new TitaniumArmorItem.Helmet());
	public static final RegistryObject<Item> TITANIUM_ARMOR_CHESTPLATE = REGISTRY.register("titanium_armor_chestplate", () -> new TitaniumArmorItem.Chestplate());
	public static final RegistryObject<Item> TITANIUM_ARMOR_LEGGINGS = REGISTRY.register("titanium_armor_leggings", () -> new TitaniumArmorItem.Leggings());
	public static final RegistryObject<Item> TITANIUM_ARMOR_BOOTS = REGISTRY.register("titanium_armor_boots", () -> new TitaniumArmorItem.Boots());
	public static final RegistryObject<Item> TITANIUM_UPGRADE = REGISTRY.register("titanium_upgrade", () -> new TitaniumUpgradeItem());
	public static final RegistryObject<Item> SCULK_ARMOR_HELMET = REGISTRY.register("sculk_armor_helmet", () -> new SculkArmorItem.Helmet());
	public static final RegistryObject<Item> SCULK_ARMOR_CHESTPLATE = REGISTRY.register("sculk_armor_chestplate", () -> new SculkArmorItem.Chestplate());
	public static final RegistryObject<Item> SCULK_ARMOR_LEGGINGS = REGISTRY.register("sculk_armor_leggings", () -> new SculkArmorItem.Leggings());
	public static final RegistryObject<Item> SCULK_ARMOR_BOOTS = REGISTRY.register("sculk_armor_boots", () -> new SculkArmorItem.Boots());
	public static final RegistryObject<Item> SCULK_UPGRADE = REGISTRY.register("sculk_upgrade", () -> new SculkUpgradeItem());
	public static final RegistryObject<Item> DYEDREAM_SWORD = REGISTRY.register("dyedream_sword", () -> new DyedreamSwordItem());
	public static final RegistryObject<Item> DYEDREAM_SWORD_0 = REGISTRY.register("dyedream_sword_0", () -> new DyedreamSword0Item());
	public static final RegistryObject<Item> DYEDREAM_PICKAXE = REGISTRY.register("dyedream_pickaxe", () -> new DyedreamPickaxeItem());
	public static final RegistryObject<Item> DYEDREAM_AXE = REGISTRY.register("dyedream_axe", () -> new DyedreamAxeItem());
	public static final RegistryObject<Item> DYEDREAM_SHOVEL = REGISTRY.register("dyedream_shovel", () -> new DyedreamShovelItem());
	public static final RegistryObject<Item> DYEDREAM_HOE = REGISTRY.register("dyedream_hoe", () -> new DyedreamHoeItem());
	public static final RegistryObject<Item> DYEDREAM_HAMMER = REGISTRY.register("dyedream_hammer", () -> new DyedreamHammerItem());
	public static final RegistryObject<Item> DYEDREAM_ARMOR_HELMET = REGISTRY.register("dyedream_armor_helmet", () -> new DyedreamArmorItem.Helmet());
	public static final RegistryObject<Item> DYEDREAM_ARMOR_CHESTPLATE = REGISTRY.register("dyedream_armor_chestplate", () -> new DyedreamArmorItem.Chestplate());
	public static final RegistryObject<Item> DYEDREAM_ARMOR_LEGGINGS = REGISTRY.register("dyedream_armor_leggings", () -> new DyedreamArmorItem.Leggings());
	public static final RegistryObject<Item> DYEDREAM_ARMOR_BOOTS = REGISTRY.register("dyedream_armor_boots", () -> new DyedreamArmorItem.Boots());
	public static final RegistryObject<Item> DYEDREAM_UPGRADE = REGISTRY.register("dyedream_upgrade", () -> new DyedreamUpgradeItem());
	public static final RegistryObject<Item> MOLTENGOLD_SWORD = REGISTRY.register("moltengold_sword", () -> new MoltengoldSwordItem());
	public static final RegistryObject<Item> TRUE_MOLTENGOLD_SWORD = REGISTRY.register("true_moltengold_sword", () -> new TrueMoltengoldSwordItem());
	public static final RegistryObject<Item> TRUEST_MOLTENGOLD_SWORD = REGISTRY.register("truest_moltengold_sword", () -> new TruestMoltengoldSwordItem());
	public static final RegistryObject<Item> MOLTENGOLD_PICKAXE = REGISTRY.register("moltengold_pickaxe", () -> new MoltengoldPickaxeItem());
	public static final RegistryObject<Item> TRUE_MOLTENGOLD_PICKAXE = REGISTRY.register("true_moltengold_pickaxe", () -> new TrueMoltengoldPickaxeItem());
	public static final RegistryObject<Item> MOLTENGOLD_AXE = REGISTRY.register("moltengold_axe", () -> new MoltengoldAxeItem());
	public static final RegistryObject<Item> MOLTENGOLD_SHOVEL = REGISTRY.register("moltengold_shovel", () -> new MoltengoldShovelItem());
	public static final RegistryObject<Item> MOLTENGOLD_HOE = REGISTRY.register("moltengold_hoe", () -> new MoltengoldHoeItem());
	public static final RegistryObject<Item> MELTDREAM_PICKAXE = REGISTRY.register("meltdream_pickaxe", () -> new MeltdreamPickaxeItem());
	public static final RegistryObject<Item> MELTDREAM_AXE = REGISTRY.register("meltdream_axe", () -> new MeltdreamAxeItem());
	public static final RegistryObject<Item> MELTDREAM_SHOVEL = REGISTRY.register("meltdream_shovel", () -> new MeltdreamShovelItem());
	public static final RegistryObject<Item> MELTDREAM_HOE = REGISTRY.register("meltdream_hoe", () -> new MeltdreamHoeItem());
	public static final RegistryObject<Item> GRASS_SWORD = REGISTRY.register("grass_sword", () -> new GrassSwordItem());
	public static final RegistryObject<Item> TRUE_GRASS_SWORD = REGISTRY.register("true_grass_sword", () -> new TrueGrassSwordItem());
	public static final RegistryObject<Item> TIDE_SWORD = REGISTRY.register("tide_sword", () -> new TideSwordItem());
	public static final RegistryObject<Item> TRUE_TIDE_SWORD = REGISTRY.register("true_tide_sword", () -> new TrueTideSwordItem());
	public static final RegistryObject<Item> DESERT_SWORD = REGISTRY.register("desert_sword", () -> new DesertSwordItem());
	public static final RegistryObject<Item> TRUE_DESERT_SWORD = REGISTRY.register("true_desert_sword", () -> new TrueDesertSwordItem());
	public static final RegistryObject<Item> BROKEN_HERO_SWORD = REGISTRY.register("broken_hero_sword", () -> new BrokenHeroSwordItem());
	public static final RegistryObject<Item> SHADOW_EROSION_SWORD = REGISTRY.register("shadow_erosion_sword", () -> new ShadowErosionSwordItem());
	public static final RegistryObject<Item> SHADOW_EROSION_PICKAXE = REGISTRY.register("shadow_erosion_pickaxe", () -> new ShadowErosionPickaxeItem());
	public static final RegistryObject<Item> SHADOW_EROSION_AXE = REGISTRY.register("shadow_erosion_axe", () -> new ShadowErosionAxeItem());
	public static final RegistryObject<Item> SHADOW_EROSION_HOE = REGISTRY.register("shadow_erosion_hoe", () -> new ShadowErosionHoeItem());
	public static final RegistryObject<Item> SHADOW_EROSION_SHOVEL = REGISTRY.register("shadow_erosion_shovel", () -> new ShadowErosionShovelItem());
	public static final RegistryObject<Item> WHITE_SWORD = REGISTRY.register("white_sword", () -> new WhiteSwordItem());
	public static final RegistryObject<Item> SHADOW_SWORD = REGISTRY.register("shadow_sword", () -> new ShadowSwordItem());
	public static final RegistryObject<Item> ICESHADOW_HAMMER = REGISTRY.register("iceshadow_hammer", () -> new IceshadowHammerItem());
	public static final RegistryObject<Item> STAR_WISH_ROD = REGISTRY.register("star_wish_rod", () -> new StarWishRodItem());
	public static final RegistryObject<Item> MOLTENGOLD_WAND = REGISTRY.register("moltengold_wand", () -> new MoltengoldWandItem());
	public static final RegistryObject<Item> TRUE_MOLTENGOLD_WAND = REGISTRY.register("true_moltengold_wand", () -> new TrueMoltengoldWandItem());
	public static final RegistryObject<Item> TRUEST_MOLTENGOLD_WAND = REGISTRY.register("truest_moltengold_wand", () -> new TruestMoltengoldWandItem());
	public static final RegistryObject<Item> SQUEAL_WAVE_WAND = REGISTRY.register("squeal_wave_wand", () -> new SquealWaveWandItem());
	public static final RegistryObject<Item> SHADOW_VORTEX_BOOK = REGISTRY.register("shadow_vortex_book", () -> new ShadowVortexBookItem());
	public static final RegistryObject<Item> DREAMHARP_OF_WANDERER = REGISTRY.register("dreamharp_of_wanderer", () -> new DreamharpOfWandererItem());
	public static final RegistryObject<Item> PLIERS = REGISTRY.register("pliers", () -> new PliersItem());
	public static final RegistryObject<Item> STORAGE_BAG = REGISTRY.register("storage_bag", () -> new StorageBagItem());
	public static final RegistryObject<Item> STORAGE_BAG_0 = REGISTRY.register("storage_bag_0", () -> new StorageBag0Item());
	public static final RegistryObject<Item> EMBRYO_RING = REGISTRY.register("embryo_ring", () -> new EmbryoRingItem());
	public static final RegistryObject<Item> EMBRYO_NECKLACE = REGISTRY.register("embryo_necklace", () -> new EmbryoNecklaceItem());
	public static final RegistryObject<Item> EMBRYO_BELT = REGISTRY.register("embryo_belt", () -> new EmbryoBeltItem());
	public static final RegistryObject<Item> EMBRYO_CHARM = REGISTRY.register("embryo_charm", () -> new EmbryoCharmItem());
	public static final RegistryObject<Item> RED_DEW_0_RING = REGISTRY.register("red_dew_0_ring", () -> new RedDew0RingItem());
	public static final RegistryObject<Item> RED_DEW_1_RING = REGISTRY.register("red_dew_1_ring", () -> new RedDew1RingItem());
	public static final RegistryObject<Item> RED_DEW_2_RING = REGISTRY.register("red_dew_2_ring", () -> new RedDew2RingItem());
	public static final RegistryObject<Item> RED_DEW_3_RING = REGISTRY.register("red_dew_3_ring", () -> new RedDew3RingItem());
	public static final RegistryObject<Item> HITHARD_0_RING = REGISTRY.register("hithard_0_ring", () -> new Hithard0RingItem());
	public static final RegistryObject<Item> HITHARD_1_RING = REGISTRY.register("hithard_1_ring", () -> new Hithard1RingItem());
	public static final RegistryObject<Item> COUNTER_RING = REGISTRY.register("counter_ring", () -> new CounterRingItem());
	public static final RegistryObject<Item> MELTDREAM_ENERGY_0_RING = REGISTRY.register("meltdream_energy_0_ring", () -> new MeltdreamEnergy0RingItem());
	public static final RegistryObject<Item> RABBIT_0_NECKLACE = REGISTRY.register("rabbit_0_necklace", () -> new Rabbit0NecklaceItem());
	public static final RegistryObject<Item> FEATHER_NECKLACE = REGISTRY.register("feather_necklace", () -> new FeatherNecklaceItem());
	public static final RegistryObject<Item> HEALTH_0_NECKLACE = REGISTRY.register("health_0_necklace", () -> new Health0NecklaceItem());
	public static final RegistryObject<Item> FIRE_0_NECKLACE = REGISTRY.register("fire_0_necklace", () -> new Fire0NecklaceItem());
	public static final RegistryObject<Item> CROSS_NECKLACE = REGISTRY.register("cross_necklace", () -> new CrossNecklaceItem());
	public static final RegistryObject<Item> NATURE_BELT = REGISTRY.register("nature_belt", () -> new NatureBeltItem());
	public static final RegistryObject<Item> TRAVELER_BELT = REGISTRY.register("traveler_belt", () -> new TravelerBeltItem());
	public static final RegistryObject<Item> DREAM_TRAVELER_BELT = REGISTRY.register("dream_traveler_belt", () -> new DreamTravelerBeltItem());
	public static final RegistryObject<Item> GOLD_CHARM = REGISTRY.register("gold_charm", () -> new GoldCharmItem());
	public static final RegistryObject<Item> ENDEYE_CHARM = REGISTRY.register("endeye_charm", () -> new EndeyeCharmItem());
	public static final RegistryObject<Item> SEA_CHARM = REGISTRY.register("sea_charm", () -> new SeaCharmItem());
	public static final RegistryObject<Item> CARAPAX_CHARM = REGISTRY.register("carapax_charm", () -> new CarapaxCharmItem());
	public static final RegistryObject<Item> WORLDTREE_SEEDPOD = REGISTRY.register("worldtree_seedpod", () -> new WorldtreeSeedpodItem());
	public static final RegistryObject<Item> GARLAND = REGISTRY.register("garland", () -> new GarlandItem());
	public static final RegistryObject<Item> GHOST_FACE_HEAD = REGISTRY.register("ghost_face_head", () -> new GhostFaceHeadItem());
	public static final RegistryObject<Item> QYM_HEAD = REGISTRY.register("qym_head", () -> new QymHeadItem());
	public static final RegistryObject<Item> WHITE_FLOWER_BODY = REGISTRY.register("white_flower_body", () -> new WhiteFlowerBodyItem());
	public static final RegistryObject<Item> DEGENERATE_BODYS = REGISTRY.register("degenerate_bodys", () -> new DegenerateBodysItem());
	public static final RegistryObject<MachineWingItem> MACHINE_WING_CHESTPLATE = REGISTRY.register("machine_wing_chestplate", () -> new MachineWingItem(ArmorItem.Type.CHESTPLATE, new Item.Properties()));
	public static final RegistryObject<Item> DARK_ALLLEGORY_CURIO = REGISTRY.register("dark_alllegory_curio", () -> new DarkAlllegoryCurioItem());
	public static final RegistryObject<Item> CALAIS_SPICE_BOTTLE_CURIO = REGISTRY.register("calais_spice_bottle_curio", () -> new CalaisSpiceBottleCurioItem());
	public static final RegistryObject<Item> FOURLEAF_CLOVER_CURIO = REGISTRY.register("fourleaf_clover_curio", () -> new FourleafCloverCurioItem());
	public static final RegistryObject<Item> SNOW_VOW_HEAD = REGISTRY.register("snow_vow_head", () -> new SnowVowHeadItem());
	public static final RegistryObject<Item> BRIGHT_BUTTERFLY_CURIO = REGISTRY.register("bright_butterfly_curio", () -> new BrightButterflyCurioItem());
	public static final RegistryObject<Item> ICESHADOW_CURIO = REGISTRY.register("iceshadow_curio", () -> new IceshadowCurioItem());
	public static final RegistryObject<Item> BOBOJI_CURIO = REGISTRY.register("boboji_curio", () -> new BobojiCurioItem());
	public static final RegistryObject<Item> PAPER_PLANE = REGISTRY.register("paper_plane", () -> new PaperPlaneItem());
	public static final RegistryObject<Item> WIND_KNIGHT_FLAG = REGISTRY.register("wind_knight_flag", () -> new WindKnightFlagItem());
	public static final RegistryObject<Item> DREAM_WAND = REGISTRY.register("dream_wand", () -> new DreamWandItem());
	public static final RegistryObject<Item> MANA_WAND = REGISTRY.register("mana_wand", () -> new ManaWandItem());
	public static final RegistryObject<Item> QYM_ARMOR_HELMET = REGISTRY.register("qym_armor_helmet", () -> new QymArmorItem.Helmet());
	public static final RegistryObject<Item> QYM_ARMOR_CHESTPLATE = REGISTRY.register("qym_armor_chestplate", () -> new QymArmorItem.Chestplate());
	public static final RegistryObject<Item> QYM_ARMOR_LEGGINGS = REGISTRY.register("qym_armor_leggings", () -> new QymArmorItem.Leggings());
	public static final RegistryObject<Item> QYM_ARMOR_BOOTS = REGISTRY.register("qym_armor_boots", () -> new QymArmorItem.Boots());
	public static final RegistryObject<Item> TABITEM_2 = REGISTRY.register("tabitem_2", () -> new Tabitem2Item());
	public static final RegistryObject<Item> ODD_BACONE_EGG = REGISTRY.register("odd_bacone_egg", () -> new PasterDreamFoodItem(
            new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
                    .food((new FoodProperties.Builder())
                            .nutrition(11)
                            .saturationMod(1.5f)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.SAN_INCREASE.get(), 1, 1),1)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.COOK_BUFF.get(), 2400, 0),1)
                            .build()),
            1));
	public static final RegistryObject<Item> MANADUST = REGISTRY.register("manadust", () -> new ManadustItem());
	public static final RegistryObject<Item> BUBBLE_TEA = REGISTRY.register("bubble_tea", () -> new BubbleTeaItem());
	public static final RegistryObject<Item> ALLKINDS_RING = REGISTRY.register("allkinds_ring", () -> new AllkindsRingItem());
	public static final RegistryObject<Item> TERRA_SWORD = REGISTRY.register("terra_sword", () -> new TerraSwordItem());
	public static final RegistryObject<Item> TERRA_CHARM = REGISTRY.register("terra_charm", () -> new TerraCharmItem());
	public static final RegistryObject<Item> HIYORI_HEAD = REGISTRY.register("hiyori_head", () -> new HiyoriHeadItem());
	public static final RegistryObject<Item> CECILIACARE_CHARM = REGISTRY.register("ceciliacare_charm", () -> new CeciliacareCharmItem());
	public static final RegistryObject<Item> THERMAL_DAGGER = REGISTRY.register("thermal_dagger", () -> new ThermalDaggerItem());
	public static final RegistryObject<Item> DUKE_COIN_CURIO = REGISTRY.register("duke_coin_curio", () -> new DukeCoinCurioItem());
	public static final RegistryObject<Item> DREAM_AURORIAN_STEEL = REGISTRY.register("dream_aurorian_steel", () -> new DreamAurorianSteelItem());
	public static final RegistryObject<Item> TURNBACK_CLOAK = REGISTRY.register("turnback_cloak", () -> new TurnbackCloakItem());
	public static final RegistryObject<Item> EVASION_CLOAK = REGISTRY.register("evasion_cloak", () -> new EvasionCloakItem());
	public static final RegistryObject<Item> STRAWBERRY_HEART = REGISTRY.register("strawberry_heart", () -> new StrawberryHeartItem());
	public static final RegistryObject<Item> SILVER_BELL = REGISTRY.register("silver_bell", () -> new SilverBellItem());
	public static final RegistryObject<Item> CRADLE_IN_ONES_ARMS = REGISTRY.register("cradle_in_ones_arms", () -> new CradleInOnesArmsItem());
	public static final RegistryObject<ForsakensWingItem> FORSAKENS_WING_CHESTPLATE = REGISTRY.register("forsakens_wing_chestplate", () -> new ForsakensWingItem(ArmorItem.Type.CHESTPLATE, new Item.Properties().fireResistant()));
	public static final RegistryObject<AngelWingItem> ANGEL_WING_CHESTPLATE = REGISTRY.register("angel_wing_chestplate", () -> new AngelWingItem(ArmorItem.Type.CHESTPLATE, new Item.Properties()));
	public static final RegistryObject<Item> DREAMNOTES_0 = REGISTRY.register("dreamnotes_0", () -> new Dreamnotes0Item());
	public static final RegistryObject<Item> DREAMNOTES_1 = REGISTRY.register("dreamnotes_1", () -> new Dreamnotes1Item());
	public static final RegistryObject<Item> DREAMNOTES_2 = REGISTRY.register("dreamnotes_2", () -> new Dreamnotes2Item());
	public static final RegistryObject<Item> DREAMNOTES_3 = REGISTRY.register("dreamnotes_3", () -> new Dreamnotes3Item());
	public static final RegistryObject<Item> DREAMNOTES_4 = REGISTRY.register("dreamnotes_4", () -> new Dreamnotes4Item());
	public static final RegistryObject<Item> DREAMNOTES_5 = REGISTRY.register("dreamnotes_5", () -> new Dreamnotes5Item());
	public static final RegistryObject<Item> DREAMNOTES_6 = REGISTRY.register("dreamnotes_6", () -> new Dreamnotes6Item());
	public static final RegistryObject<Item> DREAMNOTES_7 = REGISTRY.register("dreamnotes_7", () -> new Dreamnotes7Item());
	public static final RegistryObject<Item> DREAMNOTES_8 = REGISTRY.register("dreamnotes_8", () -> new Dreamnotes8Item());
	public static final RegistryObject<Item> DREAMNOTES_9 = REGISTRY.register("dreamnotes_9", () -> new Dreamnotes9Item());
	public static final RegistryObject<Item> DREAMNOTES_10 = REGISTRY.register("dreamnotes_10", () -> new Dreamnotes10Item());
	public static final RegistryObject<Item> DREAMNOTES_11 = REGISTRY.register("dreamnotes_11", () -> new Dreamnotes11Item());
	public static final RegistryObject<Item> DREAMNOTES_12 = REGISTRY.register("dreamnotes_12", () -> new Dreamnotes12Item());
	public static final RegistryObject<Item> DREAMNOTES_13 = REGISTRY.register("dreamnotes_13", () -> new Dreamnotes13Item());
	public static final RegistryObject<Item> DREAMNOTES_14 = REGISTRY.register("dreamnotes_14", () -> new Dreamnotes14Item());
	public static final RegistryObject<Item> BLUEPRINT_0 = REGISTRY.register("blueprint_0", () -> new BlueprintItem("pasterdream:shadow_blast_furnace"));
	public static final RegistryObject<Item> BLUEPRINT_1 = REGISTRY.register("blueprint_1", () -> new BlueprintItem("pasterdream:weapon_workshop",1));
	public static final RegistryObject<Item> MEMORY_GEM_0 = REGISTRY.register("memory_gem_0", () -> new MemoryGem0Item());
	public static final RegistryObject<Item> MEMENTO_ITEM_01 = REGISTRY.register("memento_item_01", () -> new MementoItem01Item());
	public static final RegistryObject<Item> MEMENTO_ITEM_05 = REGISTRY.register("memento_item_05", () -> new MementoItem05Item());
	public static final RegistryObject<Item> MEMENTO_ITEM_06 = REGISTRY.register("memento_item_06", () -> new MementoItem06Item());
	public static final RegistryObject<Item> MEMENTO_ITEM_02 = REGISTRY.register("memento_item_02", () -> new MementoItem02Item());
	public static final RegistryObject<Item> MEMENTO_ITEM_03 = REGISTRY.register("memento_item_03", () -> new MementoItem03Item());
	public static final RegistryObject<Item> MEMENTO_ITEM_04 = REGISTRY.register("memento_item_04", () -> new MementoItem04Item());
	public static final RegistryObject<Item> MEMENTO_ITEM_07 = REGISTRY.register("memento_item_07", () -> new MementoItem07Item());
	public static final RegistryObject<Item> MEMENTO_ITEM_08 = REGISTRY.register("memento_item_08", () -> new MementoItem08Item());
	public static final RegistryObject<Item> MEMENTO_ITEM_09 = REGISTRY.register("memento_item_09", () -> new MementoItem09Item());
	public static final RegistryObject<Item> MEMENTO_ITEM_10 = REGISTRY.register("memento_item_10", () -> new MementoItem10Item());
	public static final RegistryObject<Item> MEMENTO_ITEM_11 = block(PasterdreamModBlocks.MEMENTO_ITEM_11);
	public static final RegistryObject<Item> STRUCTURE_BLOCK_0 = block(PasterdreamModBlocks.STRUCTURE_BLOCK_0);
	public static final RegistryObject<Item> STRUCTURE_BLOCK_1 = block(PasterdreamModBlocks.STRUCTURE_BLOCK_1);
	public static final RegistryObject<Item> STRUCTURE_BLOCK_2 = block(PasterdreamModBlocks.STRUCTURE_BLOCK_2);
	public static final RegistryObject<Item> STRUCTURE_BLOCK_3 = block(PasterdreamModBlocks.STRUCTURE_BLOCK_3);
	public static final RegistryObject<Item> STRUCTURE_BLOCK_4 = block(PasterdreamModBlocks.STRUCTURE_BLOCK_4);
	public static final RegistryObject<Item> STRUCTURE_BLOCK_5 = block(PasterdreamModBlocks.STRUCTURE_BLOCK_5);
	public static final RegistryObject<Item> STRUCTURE_BLOCK_6 = block(PasterdreamModBlocks.STRUCTURE_BLOCK_6);
	public static final RegistryObject<Item> STRUCTURE_BLOCK_7 = block(PasterdreamModBlocks.STRUCTURE_BLOCK_7);
	public static final RegistryObject<Item> MELTDREAM_CRYSTAL_ENTITY_SPAWN_EGG = REGISTRY.register("meltdream_crystal_entity_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.MELTDREAM_CRYSTAL_ENTITY, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> STRUCTURE_BLOCK_8 = block(PasterdreamModBlocks.STRUCTURE_BLOCK_8);
	public static final RegistryObject<Item> STRUCTURE_BLOCK_9 = block(PasterdreamModBlocks.STRUCTURE_BLOCK_9);
	public static final RegistryObject<Item> STRUCTURE_BLOCK_10 = block(PasterdreamModBlocks.STRUCTURE_BLOCK_10);
	public static final RegistryObject<Item> STRUCTURE_BLOCK_11 = block(PasterdreamModBlocks.STRUCTURE_BLOCK_11);
	public static final RegistryObject<Item> STRUCTURE_BLOCK_12 = block(PasterdreamModBlocks.STRUCTURE_BLOCK_12);
	public static final RegistryObject<Item> STRUCTURE_BLOCK_13 = block(PasterdreamModBlocks.STRUCTURE_BLOCK_13);
	public static final RegistryObject<Item> STRUCTURE_BLOCK_14 = block(PasterdreamModBlocks.STRUCTURE_BLOCK_14);
	public static final RegistryObject<Item> STRUCTURE_BLOCK_15 = block(PasterdreamModBlocks.STRUCTURE_BLOCK_15);
	public static final RegistryObject<Item> CLAYPAN_1 = block(PasterdreamModBlocks.CLAYPAN_1);
	public static final RegistryObject<Item> CLAYPAN_2 = block(PasterdreamModBlocks.CLAYPAN_2);
	public static final RegistryObject<Item> STRUCTURE_BLOCK_16 = block(PasterdreamModBlocks.STRUCTURE_BLOCK_16);
	public static final RegistryObject<Item> STRUCTURE_BLOCK_17 = block(PasterdreamModBlocks.STRUCTURE_BLOCK_17);
	public static final RegistryObject<Item> SHADOW_DUNGEON_DOOR_1 = block(PasterdreamModBlocks.SHADOW_DUNGEON_DOOR_1);
	public static final RegistryObject<Item> TERRASWORD_WAVE_SPAWN_EGG = REGISTRY.register("terrasword_wave_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.TERRASWORD_WAVE, -7157493, -15616972, new Item.Properties()));
	public static final RegistryObject<Item> TRUE_SHADOW_BED = block(PasterdreamModBlocks.TRUE_SHADOW_BED);
	public static final RegistryObject<Item> SHADOW_MAGICBALL_SPAWN_EGG = REGISTRY.register("shadow_magicball_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.SHADOW_MAGICBALL, -16777216, -15658735, new Item.Properties()));
	public static final RegistryObject<Item> SHADOWDUNGEONDOOR_3 = block(PasterdreamModBlocks.SHADOWDUNGEONDOOR_3);
	public static final RegistryObject<Item> TALENT_SHADOW = REGISTRY.register("talent_shadow", () -> new TalentShadowItem());
	public static final RegistryObject<Item> TALENT_LIGHT = REGISTRY.register("talent_light", () -> new TalentLightItem());
	public static final RegistryObject<Item> AARONCOSHANDSPAWNBLOCK = REGISTRY.register(PasterdreamModBlocks.AARONCOSHANDSPAWNBLOCK.getId().getPath(),
			() -> new AaroncoshandspawnblockDisplayItem(PasterdreamModBlocks.AARONCOSHANDSPAWNBLOCK.get(), new Item.Properties()));
	public static final RegistryObject<Item> SHADOW_VORTEX_ITEM = REGISTRY.register("shadow_vortex_item", () -> new ShadowVortexItemItem());
	public static final RegistryObject<Item> STRUCTURE_BLOCK_18 = block(PasterdreamModBlocks.STRUCTURE_BLOCK_18);
	public static final RegistryObject<Item> ANGEL_BLOCK = block(PasterdreamModBlocks.ANGEL_BLOCK);
	public static final RegistryObject<Item> STRUCTURE_BLOCK_19 = block(PasterdreamModBlocks.STRUCTURE_BLOCK_19);
	public static final RegistryObject<Item> STRUCTURE_BLOCK_20 = block(PasterdreamModBlocks.STRUCTURE_BLOCK_20);
	public static final RegistryObject<Item> HEALING_SPELL_ENTITY_SPAWN_EGG = REGISTRY.register("healing_spell_entity_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.HEALING_SPELL_ENTITY, -133520, -16323, new Item.Properties()));
	public static final RegistryObject<Item> STRUCTURE_BLOCK_21 = block(PasterdreamModBlocks.STRUCTURE_BLOCK_21);
	public static final RegistryObject<Item> STRUCTURE_BLOCK_22 = block(PasterdreamModBlocks.STRUCTURE_BLOCK_22);
	public static final RegistryObject<Item> STRUCTURE_BLOCK_23 = block(PasterdreamModBlocks.STRUCTURE_BLOCK_23);
	public static final RegistryObject<Item> SQUEAL_WAVE = REGISTRY.register("squeal_wave", () -> new SquealWaveItem());
	public static final RegistryObject<Item> WHITE_SWORD_RAIN = REGISTRY.register("white_sword_rain", () -> new WhiteSwordRainItem());
	public static final RegistryObject<Item> BONE_WING_FIRE_BALL = REGISTRY.register("bone_wing_fire_ball", () -> new BoneWingFireBallItem());
	public static final RegistryObject<Item> LIGHT_FIREFLY_GLASS_JAR = REGISTRY.register(PasterdreamModBlocks.LIGHT_FIREFLY_GLASS_JAR.getId().getPath(),
			() -> new LightFireflyGlassJarDisplayItem(PasterdreamModBlocks.LIGHT_FIREFLY_GLASS_JAR.get(), new Item.Properties()));
	public static final RegistryObject<Item> LIGHTNING_ITEM = REGISTRY.register("lightning_item", () -> new LightningItemItem());
	public static final RegistryObject<Item> DREAM_TRAIN_STRUCTURE = block(PasterdreamModBlocks.DREAM_TRAIN_STRUCTURE);
	public static final RegistryObject<Item> WIND_KNIGHT_SPAWNBLOCK_1 = REGISTRY.register(PasterdreamModBlocks.WIND_KNIGHT_SPAWNBLOCK_1.getId().getPath(),
			() -> new WindKnightSpawnblock1DisplayItem(PasterdreamModBlocks.WIND_KNIGHT_SPAWNBLOCK_1.get(), new Item.Properties()));
	public static final RegistryObject<Item> WIND_KNIGHT_SPAWNBLOCK_2 = REGISTRY.register(PasterdreamModBlocks.WIND_KNIGHT_SPAWNBLOCK_2.getId().getPath(),
			() -> new WindKnightSpawnblock2DisplayItem(PasterdreamModBlocks.WIND_KNIGHT_SPAWNBLOCK_2.get(), new Item.Properties()));
	public static final RegistryObject<Item> WIND_KNIGHT_SPAWNBLOCK_3 = REGISTRY.register(PasterdreamModBlocks.WIND_KNIGHT_SPAWNBLOCK_3.getId().getPath(),
			() -> new WindKnightSpawnblock3DisplayItem(PasterdreamModBlocks.WIND_KNIGHT_SPAWNBLOCK_3.get(), new Item.Properties()));
	public static final RegistryObject<Item> WIND_KNIGHT_SPAWNBLOCK_4 = REGISTRY.register(PasterdreamModBlocks.WIND_KNIGHT_SPAWNBLOCK_4.getId().getPath(),
			() -> new WindKnightSpawnblock4DisplayItem(PasterdreamModBlocks.WIND_KNIGHT_SPAWNBLOCK_4.get(), new Item.Properties()));
	public static final RegistryObject<Item> FOX_FIRE_SPAWN_EGG = REGISTRY.register("fox_fire_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.FOX_FIRE, -4530446, -14311978, new Item.Properties()));
	public static final RegistryObject<Item> MOSS_PHANTOM_MEMBRANE = REGISTRY.register("moss_phantom_membrane", () -> new MossPhantomMembraneItem());
	public static final RegistryObject<Item> LIGHT_MOSS_PHANTOM_MEMBRANE = REGISTRY.register("light_moss_phantom_membrane", () -> new LightMossPhantomMembraneItem());
	public static final RegistryObject<Item> LIGHT_ORGAN = REGISTRY.register("light_organ", () -> new PasterDreamFoodItem(
            new Item.Properties()
                    .stacksTo(64)
                    .rarity(Rarity.COMMON)
                    .food((new FoodProperties.Builder())
                            .nutrition(1)
                            .saturationMod(0f)
                            .effect(()->new MobEffectInstance(MobEffects.GLOWING, 100, 0),1)
                            .effect(()->new MobEffectInstance(PasterdreamModMobEffects.SAN_DECREASE.get(), 1),1)
                            .build()),1));
	public static final RegistryObject<Item> SHADOW_BREATH = REGISTRY.register("shadow_breath", () -> new ShadowBreathItem());
	public static final RegistryObject<Item> SMALL_STONE_SPIRIT_SPAWN_EGG = REGISTRY.register("small_stone_spirit_spawn_egg", () -> new ForgeSpawnEggItem(PasterdreamModEntities.SMALL_STONE_SPIRIT, -3937560, -9340294, new Item.Properties()));
	public static final RegistryObject<Item> SMALL_STONE_SPIRIT_BLOCK = block(PasterdreamModBlocks.SMALL_STONE_SPIRIT_BLOCK);
	public static final RegistryObject<Item> LIGHT_BUTTERFLY_CURIO = REGISTRY.register("light_butterfly_curio", () -> new LightButterflyCurioItem());
	public static final RegistryObject<Item> SHADOW_HAND_LANTERN = REGISTRY.register("shadow_hand_lantern", () -> new ShadowHandLanternItem());
	public static final RegistryObject<Item> PASTER_BLOCK_RESET_TOOL = REGISTRY.register("paster_block_reset_tool", () -> new PasterBlockResetToolItem());

	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}

	private static RegistryObject<Item> doubleBlock(RegistryObject<Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new DoubleHighBlockItem(block.get(), new Item.Properties()));
	}
}
