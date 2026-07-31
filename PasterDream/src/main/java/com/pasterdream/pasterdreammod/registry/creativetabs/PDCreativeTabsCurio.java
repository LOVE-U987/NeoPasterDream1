package com.pasterdream.pasterdreammod.registry.creativetabs;

import com.pasterdream.pasterdreammod.registry.PDCreativeTabs;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDEntities;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;


/**
 * 饰品装备创造模式标签页注册。
 *
 * @see PDCreativeTabs
 */
public class PDCreativeTabsCurio {


    // ==================== 9. 饰品装备 ====================

    /**
     * 饰品装备标签页
     * 包含所有 Curio 饰品、戒指、项链、护符、腰带及翅膀
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CURIO_TAB = PDCreativeTabs.TABS.register("curio_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pasterdream.curio_tab"))
                    .icon(() -> new ItemStack(PDItems.FOURLEAF_CLOVER_CURIO.get()))
                    .withTabsBefore(PDCreativeTabs.FOOD_TAB.getKey())
                    .displayItems((parameters, output) -> {
                        output.accept(PDItems.EMBRYO_CHARM.get());
                        output.accept(PDItems.EMBRYO_RING.get());
                        output.accept(PDItems.EMBRYO_NECKLACE.get());
                        output.accept(PDItems.EMBRYO_BELT.get());
                        output.accept(PDItems.HITHARD_0_RING.get());
                        output.accept(PDItems.HITHARD_1_RING.get());
                        output.accept(PDItems.RED_DEW_0_RING.get());
                        output.accept(PDItems.RED_DEW_1_RING.get());
                        output.accept(PDItems.RED_DEW_2_RING.get());
                        output.accept(PDItems.RED_DEW_3_RING.get());
                        output.accept(PDItems.COUNTER_RING.get());
                        // 融梦光环戒指（注册在 pasterdream 命名空间；未安装时不在创造栏显示）
                        BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath("pasterdream", "meltdream_energy_0_ring"))
                                .ifPresent(item -> output.accept(new ItemStack(item)));
                        output.accept(PDItems.DARK_ALLLEGORY_CURIO.get());
                        output.accept(PDItems.CECILIACARE_CHARM.get());
                        output.accept(PDItems.CARAPAX_CHARM.get());
                        output.accept(PDItems.SEA_CHARM.get());
                        output.accept(PDItems.CALAIS_SPICE_BOTTLE_CURIO.get());
                        output.accept(PDItems.CROSS_NECKLACE.get());
                        output.accept(PDItems.FEATHER_NECKLACE.get());
                        output.accept(PDItems.FIRE_0_NECKLACE.get());
                        output.accept(PDItems.HEALTH_0_NECKLACE.get());
                        output.accept(PDItems.RABBIT_0_NECKLACE.get());
                        output.accept(PDItems.GOLD_CHARM.get());
                        output.accept(PDItems.ENDEYE_CHARM.get());
                        output.accept(PDItems.TERRA_CHARM.get());
                        output.accept(PDItems.DREAM_TRAVELER_BELT.get());
                        output.accept(PDItems.NATURE_BELT.get());
                        output.accept(PDItems.TRAVELER_BELT.get());
                        output.accept(PDItems.EVASION_CLOAK.get());
                        output.accept(PDItems.TURNBACK_CLOAK.get());
                        output.accept(PDItems.GARLAND.get());
                        output.accept(PDItems.PAPER_PLANE.get());
                        output.accept(PDItems.DUKE_COIN_CURIO.get());
                        output.accept(PDItems.BOBO_PLUME.get());
                        output.accept(PDItems.BRIGHT_BUTTERFLY_CURIO.get());
                        output.accept(PDItems.LIGHT_BUTTERFLY_CURIO.get());
                        output.accept(PDItems.ICESHADOW_CURIO.get());
                        output.accept(PDItems.DEGENERATE_BODYS.get());
                        // 白花胸针已拆分到 PasterDreamSanity（注册在 pasterdream 命名空间）；未安装时不在创造栏显示
                        BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath("pasterdream", "white_flower_body"))
                                .ifPresent(item -> output.accept(new ItemStack(item)));
                        output.accept(PDItems.WORLDTREE_SEEDPOD.get());
                        output.accept(PDItems.HIYORI_HEAD.get());
                        output.accept(PDItems.QYM_HEAD.get());
                        output.accept(PDItems.SNOW_VOW_HEAD.get());
                        output.accept(PDItems.GHOST_FACE_HEAD.get());
                        output.accept(PDItems.ALLKINDS_RING.get());
                        output.accept(PDItems.FOURLEAF_CLOVER_CURIO.get());
                    })
                    .build());
}
