package com.pasterdream.pasterdreammod.client.renderer;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.client.renderer.block.*;
import com.pasterdream.pasterdreammod.client.renderer.entity.*;
import com.pasterdream.pasterdreammod.entity.client.AaroncosLefthand0Renderer;
import com.pasterdream.pasterdreammod.entity.client.AaroncosRighthand0Renderer;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 渲染器注册器 —— 统一管理渲染器注册逻辑
 * <p>
 * 将原本散布在 ClientSetup 中的大量重复注册代码集中管理，
 * 通过方法分组方式简化注册逻辑，提高可维护性。
 * <p>
 * 使用方式：在 EntityRenderersEvent.RegisterRenderers 中调用 {@link #registerAll(EntityRenderersEvent.RegisterRenderers)}
 */
public final class RendererRegistry {

    private RendererRegistry() {
        throw new UnsupportedOperationException("RendererRegistry 是不可实例化的注册类");
    }

    /**
     * 注册所有方块实体渲染器和实体渲染器
     *
     * @param event EntityRenderersEvent.RegisterRenderers 事件
     */
    public static void registerAll(EntityRenderersEvent.RegisterRenderers event) {
        registerBlockEntityRenderers(event);
        registerEntityRenderers(event);
        registerProjectileRenderers(event);
    }

    /**
     * 注册所有方块实体渲染器
     */
    private static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        int count = 0;

        event.registerBlockEntityRenderer(PDBlockEntities.AARONCOS_HAND_SPAWN_BLOCK.get(), AaroncosHandSpawnBlockRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册方块实体渲染器: aaroncos_hand_spawn_block");
        count++;

        event.registerBlockEntityRenderer(PDBlockEntities.AARONCOS_HAND_CHEST.get(), AaroncosHandChestBlockRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册方块实体渲染器: aaroncos_hand_chest");
        count++;

        event.registerBlockEntityRenderer(PDBlockEntities.SHADOW_VORTEX.get(), ShadowVortexBlockRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册方块实体渲染器: shadow_vortex");
        count++;

        event.registerBlockEntityRenderer(PDBlockEntities.DREAM_ACCUMULATOR.get(), context -> new DreamAccumulatorBlockRenderer());
        PDDebugLogger.mainDebug("[RendererRegistry] 注册方块实体渲染器: dream_accumulator");
        count++;

        event.registerBlockEntityRenderer(PDBlockEntities.LIFE_CRYSTAL.get(), LifeCrystalBlockRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册方块实体渲染器: life_crystal");
        count++;

        event.registerBlockEntityRenderer(PDBlockEntities.QIN_DOLL_0.get(), QymDoll0BlockRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册方块实体渲染器: qin_doll_0");
        count++;

        event.registerBlockEntityRenderer(PDBlockEntities.LITTLE_PURPLE_DOLL_0.get(), UuzDoll0BlockRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册方块实体渲染器: little_purple_doll_0");
        count++;

        event.registerBlockEntityRenderer(PDBlockEntities.GOLDEN_FOX_SCULPTURE.get(), GoldenFoxSculptureBlockRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册方块实体渲染器: golden_fox_sculpture");
        count++;

        event.registerBlockEntityRenderer(PDBlockEntities.LOVE_U_DOLL.get(), LoveUDollBlockRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册方块实体渲染器: love_u_doll");
        count++;

        event.registerBlockEntityRenderer(PDBlockEntities.EOUL_DOLL.get(), EoulDollBlockRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册方块实体渲染器: eoul_doll");
        count++;

        event.registerBlockEntityRenderer(PDBlockEntities.SHADOW_CHEST.get(), ShadowChestBlockRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册方块实体渲染器: shadow_chest");
        count++;

        event.registerBlockEntityRenderer(PDBlockEntities.DREAM_CAULDRON.get(), DreamCauldronBlockRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册方块实体渲染器: dream_cauldron");
        count++;

        event.registerBlockEntityRenderer(PDBlockEntities.MELTDREAM_CHEST.get(), MeltdreamChestBlockRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册方块实体渲染器: meltdream_chest");
        count++;

        event.registerBlockEntityRenderer(PDBlockEntities.THE_ENDLESS_BOOK_OF_DREAM_SEEKERS.get(), TheEndlessBookOfDreamSeekersBlockRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册方块实体渲染器: the_endless_book_of_dream_seekers");
        count++;

        // ==================== [分区W] 武器工坊群 ====================

        event.registerBlockEntityRenderer(PDBlockEntities.WEAPON_TABLE.get(), WeaponTableBlockRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册方块实体渲染器: weapon_table");
        count++;

        event.registerBlockEntityRenderer(PDBlockEntities.WEAPON_WORKSHOP.get(), WeaponWorkshopBlockRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册方块实体渲染器: weapon_workshop");
        count++;

        // ==================== [分区R] 研究台组 ====================

        event.registerBlockEntityRenderer(PDBlockEntities.RESEARCH_TABLE.get(), ResearchTableBlockRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册方块实体渲染器: research_table");
        count++;

        event.registerBlockEntityRenderer(PDBlockEntities.SHADOW_BLAST_FURNACE.get(), ShadowBlastFurnaceBlockRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册方块实体渲染器: shadow_blast_furnace");
        count++;

        event.registerBlockEntityRenderer(PDBlockEntities.FORCED_TOWER.get(), ForcedTowerBlockRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册方块实体渲染器: forced_tower");
        count++;

        // ==================== DollAPI 注册的玩偶 ====================
        for (var reg : com.pasterdream.pasterdreammod.api.doll.DollAPI.getRegistrations()) {
            if (reg.config().modelType() == com.pasterdream.pasterdreammod.api.doll.DollModelType.LEGACY) {
                event.registerBlockEntityRenderer(reg.blockEntityType().get(), context -> new LegacyDollBlockRenderer(reg.name()));
            } else {
                event.registerBlockEntityRenderer(reg.blockEntityType().get(), DollBlockRenderer::new);
            }
            PDDebugLogger.mainDebug("[RendererRegistry] 注册玩偶方块实体渲染器: {}", reg.name());
            count++;
        }

        PDDebugLogger.mainDebug("[RendererRegistry] 方块实体渲染器注册完成，共 {} 个", count);
    }

    /**
     * 注册所有实体渲染器
     */
    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        int count = 0;

        // 基础生物
        event.registerEntityRenderer(PDEntities.SHADOW_GOLEM.get(), ShadowGolemRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: shadow_golem （GeckoLib）");
        event.registerEntityRenderer(PDEntities.PINK_SLIME.get(), PinkSlimeRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: pink_slime （原生模型）");
        count += 2;

        // 染梦世界生物
        event.registerEntityRenderer(PDEntities.PINK_CHICKEN.get(), PinkChickenRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: pink_chicken");
        event.registerEntityRenderer(PDEntities.JELLYFISH.get(), JellyfishRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: jellyfish （GeckoLib）");
        event.registerEntityRenderer(PDEntities.FRIENDLY_GHOST.get(), FriendlyGhostRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: friendly_ghost （GeckoLib）");
        event.registerEntityRenderer(PDEntities.FIREFLY.get(), FireflyRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: firefly （GeckoLib）");
        event.registerEntityRenderer(PDEntities.GOLDEN_FOX.get(), GoldenFoxRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: golden_fox （GeckoLib）");
        event.registerEntityRenderer(PDEntities.MELTDREAM_CRYSTAL.get(), MeltdreamCrystalRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: meltdream_crystal （GeckoLib）");
        event.registerEntityRenderer(PDEntities.TERRORBEAK.get(), TerrorbeakRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: terrorbeak （GeckoLib）");
        event.registerEntityRenderer(PDEntities.CRAZY_TERRORBEAK.get(), CrazyTerrorbeakRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: crazy_terrorbeak （GeckoLib）");
        event.registerEntityRenderer(PDEntities.WEAKENESS_TERRORBEAK.get(), WeakenessTerrorbeakRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: weakeness_terrorbeak （GeckoLib）");
        event.registerEntityRenderer(PDEntities.BONE_WING.get(), BoneWingRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: bone_wing （GeckoLib）");
        event.registerEntityRenderer(PDEntities.ASH_BONE_WING.get(), AshBoneWingRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: ash_bone_wing （GeckoLib）");
        count += 11;

        // 阴影系列
        event.registerEntityRenderer(PDEntities.SHADOW_GHOST.get(), ShadowGhostRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: shadow_ghost （GeckoLib）");
        event.registerEntityRenderer(PDEntities.SHADOW_SQUEAL_GHOST.get(), ShadowSquealGhostRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: shadow_squeal_ghost （GeckoLib）");
        event.registerEntityRenderer(PDEntities.SHADOW_SQUEAL_GHOST_0.get(), ShadowSquealGhost0Renderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: shadow_squeal_ghost_0 （GeckoLib）");
        event.registerEntityRenderer(PDEntities.SHADOW_HAND.get(), ShadowHandRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: shadow_hand （GeckoLib）");
        count += 4;

        // 雷云系列
        event.registerEntityRenderer(PDEntities.THUNDERCLOUD.get(), ThundercloudRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: thundercloud （GeckoLib）");
        event.registerEntityRenderer(PDEntities.HIGHVOLTAGE.get(), HighvoltageThundercloudRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: highvoltage （GeckoLib）");
        count += 2;

        // 其他敌对生物
        event.registerEntityRenderer(PDEntities.WIND_KNIGHT.get(), WindKnightRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: wind_knight （GeckoLib）");
        event.registerEntityRenderer(PDEntities.SHAKING_CRYSTAL.get(), ShakingCrystalRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: shaking_crystal （GeckoLib）");
        event.registerEntityRenderer(PDEntities.SHADOW_TUNE_TOTEM.get(), ShadowTuneTotemRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: shadow_tune_totem （GeckoLib）");
        event.registerEntityRenderer(PDEntities.SMALL_STONE_SPIRIT.get(), SmallStoneSpiritRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: small_stone_spirit （GeckoLib）");
        event.registerEntityRenderer(PDEntities.BLACK_BEETLE.get(), BlackBeetleRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: black_beetle （GeckoLib）");
        event.registerEntityRenderer(PDEntities.BLACK_BEETLE_MOTHER.get(), BlackBeetleMotherRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: black_beetle_mother （GeckoLib）");
        count += 6;

        // 染梦新生物
        event.registerEntityRenderer(PDEntities.BASALT_SNAIL.get(), BasaltSnailRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: basalt_snail （GeckoLib）");
        event.registerEntityRenderer(PDEntities.FOX_FIRE.get(), FoxFireRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: fox_fire （GeckoLib）");
        event.registerEntityRenderer(PDEntities.SHADOW_NPC_0.get(), ShadowNpc0Renderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: shadow_npc_0 （GeckoLib）");
        event.registerEntityRenderer(PDEntities.SPORE_ENTITY.get(), SporeEntityRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: spore_entity （自定义立方体模型 + 存活时不可见）");
        count += 4;

        // BOSS 系列
        event.registerEntityRenderer(PDEntities.AARONCOS_LEFTHAND_0.get(), AaroncosLefthand0Renderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: aaroncos_lefthand_0 （GeckoLib BOSS）");
        event.registerEntityRenderer(PDEntities.AARONCOS_RIGHTHAND_0.get(), AaroncosRighthand0Renderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: aaroncos_righthand_0 （GeckoLib BOSS）");
        count += 2;

        // 投射物
        event.registerEntityRenderer(PDEntities.SHADOW_MAGICBALL.get(), ShadowMagicballRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: shadow_magicball （GeckoLib 投射物）");
        count++;

        PDDebugLogger.mainDebug("[RendererRegistry] 实体渲染器注册完成，共 {} 个", count);
    }

    /**
     * 注册投射物渲染器（特殊处理）
     */
    private static void registerProjectileRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
                PDEntities.BONE_WING_FIRE_BALL_PROJECTILE.get(),
                context -> new ThrownItemRenderer<>(context, 1.0f, false)
        );
        PDDebugLogger.mainDebug("[RendererRegistry] 注册弹射物渲染器: bone_wing_fire_ball_projectile → ThrownItemRenderer");

        event.registerEntityRenderer(
                PDEntities.SQUEAL_WAVE_PROJECTILE.get(),
                context -> new com.pasterdream.pasterdreammod.client.renderer.entity.SquealWaveRenderer(context)
        );
        PDDebugLogger.mainDebug("[RendererRegistry] 注册弹射物渲染器: squeal_wave_projectile → SquealWaveRenderer");

        // ==================== 法杖武器投射物（W2-D） ====================
        // 全部实现 ItemSupplier，以飞行中的物品贴图渲染（与原版 ThrownItemRenderer 方案一致）
        event.registerEntityRenderer(PDEntities.LIGHTNING_PROJECTILE.get(),
                context -> new ThrownItemRenderer<>(context, 1.0f, false));
        event.registerEntityRenderer(PDEntities.MOLTENGOLD_WAND_PROJECTILE.get(),
                context -> new ThrownItemRenderer<>(context, 1.0f, false));
        event.registerEntityRenderer(PDEntities.TRUE_MOLTENGOLD_WAND_PROJECTILE.get(),
                context -> new ThrownItemRenderer<>(context, 1.0f, false));
        event.registerEntityRenderer(PDEntities.TRUEST_MOLTENGOLD_WAND_PROJECTILE.get(),
                context -> new ThrownItemRenderer<>(context, 1.0f, false));
        event.registerEntityRenderer(PDEntities.SQUEAL_WAVE_WAND_PROJECTILE.get(),
                context -> new ThrownItemRenderer<>(context, 1.0f, false));
        event.registerEntityRenderer(PDEntities.SHADOW_VORTEX_BOOK_PROJECTILE.get(),
                context -> new ThrownItemRenderer<>(context, 1.0f, false));
        event.registerEntityRenderer(PDEntities.PINKEGG_PROJECTILE.get(),
                context -> new ThrownItemRenderer<>(context, 1.0f, false));
        event.registerEntityRenderer(PDEntities.STRAWBERRY_HEART_PROJECTILE.get(),
                context -> new ThrownItemRenderer<>(context, 1.0f, false));
        event.registerEntityRenderer(PDEntities.WHITE_SWORD_RAIN_PROJECTILE.get(),
                context -> new ThrownItemRenderer<>(context, 1.0f, false));
        PDDebugLogger.mainDebug("[RendererRegistry] 注册弹射物渲染器: 9 个法杖投射物 → ThrownItemRenderer");

        // 大地之刃剑气（GeckoLib 半透明剑气实体）
        event.registerEntityRenderer(PDEntities.TERRASWORD_WAVE.get(),
                com.pasterdream.pasterdreammod.client.renderer.entity.TerraswordWaveRenderer::new);
        PDDebugLogger.mainDebug("[RendererRegistry] 注册实体渲染器: terrasword_wave （GeckoLib 剑气）");
    }
}