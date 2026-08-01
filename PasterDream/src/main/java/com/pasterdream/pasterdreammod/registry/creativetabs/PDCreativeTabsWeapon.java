package com.pasterdream.pasterdreammod.registry.creativetabs;

import com.pasterdream.pasterdreammod.registry.PDCreativeTabs;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;


/**
 * 盔甲装备和武器工具创造模式标签页注册。
 * 由原「盔甲装备」(armor_tab) 与「武器工具」(weapon_tab) 两栏合并而成。
 *
 * @see PDCreativeTabs
 */
public class PDCreativeTabsWeapon {


    // ==================== 7. 盔甲装备和武器工具 ====================

    /**
     * 盔甲装备和武器工具标签页
     * 包含所有盔甲套装、武器、工具、升级套件及基础材料
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WEAPON_TAB = PDCreativeTabs.TABS.register("weapon_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pasterdream.weapon_tab"))
                    .icon(() -> new ItemStack(PDItems.MOLTENGOLD_SWORD.get()))
                    .withTabsBefore(PDCreativeTabs.SOUVENIR_TAB.getKey())
                    .displayItems((parameters, output) -> {
                        // ===== 盔甲套装（原 armor_tab 并入）=====
                        // 铜盔甲套装
                        output.accept(PDItems.COPPER_ARMOR_HELMET.get());
                        output.accept(PDItems.COPPER_ARMOR_CHESTPLATE.get());
                        output.accept(PDItems.COPPER_ARMOR_LEGGINGS.get());
                        output.accept(PDItems.COPPER_ARMOR_BOOTS.get());
                        // 钛盔甲套装
                        output.accept(PDItems.TITANIUM_ARMOR_HELMET.get());
                        output.accept(PDItems.TITANIUM_ARMOR_CHESTPLATE.get());
                        output.accept(PDItems.TITANIUM_ARMOR_LEGGINGS.get());
                        output.accept(PDItems.TITANIUM_ARMOR_BOOTS.get());
                        // 潜声盔甲套装
                        output.accept(PDItems.SCULK_ARMOR_HELMET.get());
                        output.accept(PDItems.SCULK_ARMOR_CHESTPLATE.get());
                        output.accept(PDItems.SCULK_ARMOR_LEGGINGS.get());
                        output.accept(PDItems.SCULK_ARMOR_BOOTS.get());
                        // 染梦盔甲套装
                        output.accept(PDItems.DYEDREAM_ARMOR_HELMET.get());
                        output.accept(PDItems.DYEDREAM_ARMOR_CHESTPLATE.get());
                        output.accept(PDItems.DYEDREAM_ARMOR_LEGGINGS.get());
                        output.accept(PDItems.DYEDREAM_ARMOR_BOOTS.get());
                        // QYM盔甲套装
                        output.accept(PDItems.QIN_ARMOR_HELMET.get());
                        output.accept(PDItems.QIN_ARMOR_CHESTPLATE.get());
                        output.accept(PDItems.QIN_ARMOR_LEGGINGS.get());
                        output.accept(PDItems.QIN_ARMOR_BOOTS.get());

                        // 基础材料
                        output.accept(PDItems.TITANIUM_INGOT.get());
                        output.accept(PDItems.TITANIUM_NUGGET.get());
                        output.accept(PDItems.RAW_TITANIUM.get());
                        output.accept(PDItems.MOLTENGOLD_INGOT.get());
                        output.accept(PDItems.MOLTENGOLD_NUGGET.get());
                        output.accept(PDItems.MOLTENGOLD_DUST.get());
                        output.accept(PDItems.RAW_MOLTENGOLD.get());
                        output.accept(PDItems.BLACKMETAL_INGOT.get());
                        output.accept(PDItems.BLACKMETAL_GRAIN.get());
                        output.accept(PDItems.RUST_BLACK_METAL_GRAIN.get());
                        output.accept(PDItems.BLACKSTICK.get());
                        output.accept(PDItems.ENHANCE_STONE_0.get());
                        output.accept(PDItems.ENHANCE_STONE_1.get());
                        output.accept(PDItems.PROTECT_DECK.get());
                        output.accept(PDItems.TITANIUM_UPGRADE.get());
                        output.accept(PDItems.WHITE_CRYSTAL.get());
                        output.accept(PDItems.CHARGED_AMETHYST.get());
                        output.accept(PDItems.MAGIC_STONE.get());
                        output.accept(PDItems.MANADUST.get());
                        output.accept(PDItems.BLUE_HEART_OF_THE_SEA.get());
                        output.accept(PDItems.MORTAR.get());
                        output.accept(PDItems.SILVER_BELL.get());
                        output.accept(PDItems.SORBENT.get());
                        output.accept(PDItems.COTTON.get());
                        output.accept(PDItems.SPOOL.get());
                        output.accept(PDItems.FABRIC.get());
                        output.accept(PDItems.PERGAMYN.get());
                        output.accept(PDItems.PEN_AND_INK.get());
                        output.accept(PDItems.REEDROD.get());
                        // 生物掉落材料（原版 paster_tab_0 物品栏；曾误挂 entity_tab）
                        output.accept(PDItems.SOUL_DUST.get());
                        output.accept(PDItems.SOUL_ESSENCE.get());
                        output.accept(PDItems.PINK_SLIMEBALL.get());
                        output.accept(PDItems.ELDER_GUARDIAN_SCALE.get());
                        output.accept(PDItems.BLACK_BEETLE_CARAPACE.get());
                        output.accept(PDItems.BLACK_BEETLE_VOCALCORD.get());
                        output.accept(PDItems.BASALT_SNAIL_SHELL.get());

                        // 武器原胚
                        output.accept(PDItems.SWORD_EMBRYO_0.get());
                        output.accept(PDItems.SHADOW_SWORD_EMBRYO.get());
                        output.accept(PDItems.SHADOW_EROSION_SWORD_EMBRYO.get());
                        output.accept(PDItems.SHADOW_EROSION_AXE_EMBRYO.get());
                        output.accept(PDItems.SHADOW_EROSION_PICKAXE_EMBRYO.get());
                        output.accept(PDItems.SHADOW_EROSION_HOE_EMBRYO.get());
                        output.accept(PDItems.SHADOW_EROSION_SHOVEL_EMBRYO.get());
                        output.accept(PDItems.ICESHADOW_HAMMER_EMBRYO.get());
                        output.accept(PDItems.WHITE_SWORD_EMBRYO.get());
                        output.accept(PDItems.TERRASWORD_EMBRYO.get());
                        output.accept(PDItems.STAR_WISH_ROD_EMBRYO.get());
                        // 杂项补全：聚梦法杖原胚（原版物品栏 tab_0）
                        output.accept(PDItems.DREAM_WAND_EMBRYO.get());

                        // 剑类武器
                        output.accept(PDItems.BROKEN_HERO_SWORD.get());
                        output.accept(PDItems.COPPER_SWORD.get());
                        output.accept(PDItems.CREATIVE_SWORD.get());
                        output.accept(PDItems.DESERT_SWORD.get());
                        output.accept(PDItems.GRASS_SWORD.get());
                        output.accept(PDItems.ICESHADOW_HAMMER.get());
                        output.accept(PDItems.MOLTENGOLD_SWORD.get());
                        output.accept(PDItems.SHADOW_EROSION_SWORD.get());
                        output.accept(PDItems.SHADOW_SWORD.get());
                        output.accept(PDItems.TERRA_SWORD.get());
                        output.accept(PDItems.THERMAL_DAGGER.get());
                        output.accept(PDItems.TIDE_SWORD.get());
                        output.accept(PDItems.TITANIUM_SWORD.get());
                        output.accept(PDItems.TRUE_DESERT_SWORD.get());
                        output.accept(PDItems.TRUE_GRASS_SWORD.get());
                        output.accept(PDItems.TRUE_MOLTENGOLD_SWORD.get());
                        output.accept(PDItems.TRUE_TIDE_SWORD.get());
                        output.accept(PDItems.TRUEST_MOLTENGOLD_SWORD.get());
                        output.accept(PDItems.WHITE_SWORD.get());
                        // 染梦剑类（原染梦维度栏移入）
                        output.accept(PDItems.DYEDREAM_SWORD_0.get());
                        output.accept(PDItems.DYEDREAM_SWORD.get());

                        // 镐类/锤类工具
                        output.accept(PDItems.COPPER_PICKAXE.get());
                        output.accept(PDItems.MOLTENGOLD_PICKAXE.get());
                        output.accept(PDItems.SHADOW_EROSION_PICKAXE.get());
                        output.accept(PDItems.TITANIUM_PICKAXE.get());
                        output.accept(PDItems.TRUE_MOLTENGOLD_PICKAXE.get());
                        // 染梦/融梦镐锤（原染梦维度栏移入）
                        output.accept(PDItems.DYEDREAM_PICKAXE.get());
                        output.accept(PDItems.DYEDREAM_HAMMER.get());
                        output.accept(PDItems.MELTDREAM_PICKAXE.get());

                        // 其他工具
                        output.accept(PDItems.COPPER_AXE.get());
                        output.accept(PDItems.COPPER_SHOVEL.get());
                        output.accept(PDItems.COPPER_HOE.get());
                        output.accept(PDItems.TITANIUM_AXE.get());
                        output.accept(PDItems.TITANIUM_SHOVEL.get());
                        output.accept(PDItems.TITANIUM_HOE.get());
                        // 染梦工具
                        output.accept(PDItems.DYEDREAM_AXE.get());
                        output.accept(PDItems.DYEDREAM_SHOVEL.get());
                        output.accept(PDItems.DYEDREAM_HOE.get());
                        // 熔金工具
                        output.accept(PDItems.MOLTENGOLD_AXE.get());
                        output.accept(PDItems.MOLTENGOLD_SHOVEL.get());
                        output.accept(PDItems.MOLTENGOLD_HOE.get());
                        // 融梦工具
                        output.accept(PDItems.MELTDREAM_AXE.get());
                        output.accept(PDItems.MELTDREAM_SHOVEL.get());
                        output.accept(PDItems.MELTDREAM_HOE.get());
                        // 蚀影工具
                        output.accept(PDItems.SHADOW_EROSION_AXE.get());
                        output.accept(PDItems.SHADOW_EROSION_SHOVEL.get());
                        output.accept(PDItems.SHADOW_EROSION_HOE.get());
                        // 杂项补全：挖掘机3000!（调试用挖掘工具，原版调试栏 tab_8）
                        output.accept(PDItems.EXCAVATOR.get());
                        output.accept(PDItems.GLASS_CUP.get());
                        output.accept(PDItems.DOUGH.get());
                        output.accept(PDItems.RYESEED.get());

                        // Phase 1: 移植方块
                        output.accept(PDBlocks.TITANIUM_BLOCK.get());
                        output.accept(PDBlocks.RAW_TITANIUM_BLOCK.get());
                        output.accept(PDBlocks.MOLTENGOLD_BLOCK.get());
                        output.accept(PDBlocks.BLACKMETAL_BLOCK.get());
                        output.accept(PDBlocks.CHARGED_AMETHYST_BLOCK.get());
                        output.accept(PDBlocks.WIND_IRON_BLOCK.get());
                        output.accept(PDBlocks.DEEPSLATE_TITANIUM_ORE.get());
                        output.accept(PDBlocks.TITANIUM_ORE.get());
                        output.accept(PDBlocks.MOLTENGOLD_ORE.get());
                        output.accept(PDBlocks.SOUL_ORE.get());

                        // Phase 1: 移植特殊物品（熔梦液桶归染梦页；蓝图归纪念品/工坊相关页）
                        output.accept(PDItems.JUNGLE_SPORE.get());
                        output.accept(PDItems.PINKEGG.get());
                        output.accept(PDItems.PLIERS.get());

                        // ==================== [分区W] 武器工坊群 ====================
                        // 核心与卫星工位（原版 tab_5 / tab_8）
                        output.accept(PDItems.WEAPON_TABLE.get());
                        output.accept(PDItems.WEAPON_WORKSHOP.get());
                        output.accept(PDItems.WORKSHOP_CAULDEON.get());
                        output.accept(PDItems.WORKSHOP_BLAST.get());
                        output.accept(PDItems.WORKSHOP_ANVIL.get());
                        output.accept(PDItems.WORKSHOP_GRIND.get());
                        // 蓝图（工坊合成指引，与纪念品页双挂便于查找）
                        output.accept(PDItems.BLUEPRINT_0.get());
                        output.accept(PDItems.BLUEPRINT_1.get());

                        // 法杖武器（W2-D）
                        output.accept(PDItems.DREAM_WAND.get());
                        output.accept(PDItems.MANA_WAND.get());
                        output.accept(PDItems.MOLTENGOLD_WAND.get());
                        output.accept(PDItems.TRUE_MOLTENGOLD_WAND.get());
                        output.accept(PDItems.TRUEST_MOLTENGOLD_WAND.get());
                        output.accept(PDItems.SQUEAL_WAVE_WAND.get());
                        output.accept(PDItems.STAR_WISH_ROD.get());
                        output.accept(PDItems.SHADOW_VORTEX_BOOK.get());
                        output.accept(PDItems.WHITE_SWORD_RAIN.get());
                    })
                    .build());
}
