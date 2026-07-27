package com.pasterdream.pasterdreammod.registry.creativetabs;

import com.pasterdream.pasterdreammod.registry.PDCreativeTabs;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;


/**
 * 生物实体相关创造模式标签页注册。
 *
 * @see PDCreativeTabs
 */
public class PDCreativeTabsEntity {


    // ==================== 1. 生物实体 ====================

    /**
     * 生物实体标签页
     * 仅包含刷怪蛋（掉落物归材料/阴影/食物/纪念品等对应页）
     */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ENTITY_TAB = PDCreativeTabs.TABS.register("entity_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.pasterdream.entity_tab"))
                    .icon(() -> new ItemStack(PDItems.SHADOW_GOLEM_SPAWN_EGG.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(PDItems.SHADOW_GOLEM_SPAWN_EGG.get());
                        output.accept(PDItems.PINK_SLIME_SPAWN_EGG.get());
                        output.accept(PDItems.PINK_CHICKEN_SPAWN_EGG.get());
                        output.accept(PDItems.JELLYFISH_SPAWN_EGG.get());
                        output.accept(PDItems.FRIENDLY_GHOST_SPAWN_EGG.get());
                        output.accept(PDItems.FIREFLY_SPAWN_EGG.get());
                        output.accept(PDItems.GOLDEN_FOX_SPAWN_EGG.get());
                        output.accept(PDItems.MELTDREAM_CRYSTAL_SPAWN_EGG.get());
                        output.accept(PDItems.SHADOW_GHOST_SPAWN_EGG.get());
                        output.accept(PDItems.SHADOW_SQUEAL_GHOST_SPAWN_EGG.get());
                        output.accept(PDItems.SHADOW_SQUEAL_GHOST_0_SPAWN_EGG.get());
                        output.accept(PDItems.SHADOW_HAND_SPAWN_EGG.get());
                        output.accept(PDItems.THUNDERCLOUD_SPAWN_EGG.get());
                        output.accept(PDItems.HIGHVOLTAGE_SPAWN_EGG.get());
                        output.accept(PDItems.WIND_KNIGHT_SPAWN_EGG.get());
                        output.accept(PDItems.SHAKING_CRYSTAL_SPAWN_EGG.get());
                        output.accept(PDItems.SHADOW_TUNE_TOTEM_SPAWN_EGG.get());
                        output.accept(PDItems.SMALL_STONE_SPIRIT_SPAWN_EGG.get());
                        output.accept(PDItems.BLACK_BEETLE_SPAWN_EGG.get());
                        output.accept(PDItems.BLACK_BEETLE_MOTHER_SPAWN_EGG.get());
                        output.accept(PDItems.TERRORBEAK_SPAWN_EGG.get());
                        output.accept(PDItems.CRAZY_TERRORBEAK_SPAWN_EGG.get());
                        output.accept(PDItems.WEAKENESS_TERRORBEAK_SPAWN_EGG.get());
                        output.accept(PDItems.BONE_WING_SPAWN_EGG.get());
                        output.accept(PDItems.ASH_BONE_WING_SPAWN_EGG.get());
                        output.accept(PDItems.BASALT_SNAIL_SPAWN_EGG.get());
                        output.accept(PDItems.FOX_FIRE_SPAWN_EGG.get());
                        output.accept(PDItems.SHADOW_NPC_0_SPAWN_EGG.get());
                        output.accept(PDItems.SPORE_ENTITY_SPAWN_EGG.get());
                        output.accept(PDItems.AARONCOS_LEFTHAND_0_SPAWN_EGG.get());
                        output.accept(PDItems.AARONCOS_RIGHTHAND_0_SPAWN_EGG.get());
                        // 注：TERRASWORD_WAVE / HEALING_SPELL_ENTITY 为特效实体刷怪蛋，原版亦不入创造刷怪蛋栏
                    })
                    .build());
}
