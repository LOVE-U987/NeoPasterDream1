package com.pasterdream.pasterdreammod.world;

import com.pasterdream.pasterdreammod.attachment.PDAttachments;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import com.pasterdream.pasterdreammod.registry.PDAttributes;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.registry.PDGameRules;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import top.theillusivec4.curios.api.CuriosApi;

/**
 * 理智系统玩家 tick（移植原版 {@code SanHelper}）。
 * <p>
 * 服务端 {@link PlayerTickEvent.Post}：
 * <ul>
 *   <li>按 {@code pasterdreamSanVariabilityPerTick} 将 {@link PDAttributes#SAN_VARIABILITY}
 *       换算并写入 San 数值</li>
 *   <li>按配置 {@code player total tick update} 施加低理智 debuff / 高理智 cheerup，
 *       并刷新环境 SAN 修饰符；风维额外续 {@code cloudmist_buff} 200t</li>
 * </ul>
 */
public final class PDSanHelper {

    /** 与原版 UUID {@code 1217d41a-1591-455a-8821-3356aad56a00} 对应的稳定修饰符 ID */
    private static final ResourceLocation ENV_SAN_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath("pasterdream", "environment_san_variability");

    private PDSanHelper() {
    }

    /**
     * 玩家 tick 末：理智变化 + 环境修饰 / 云雾。
     *
     * @param event 玩家 tick 事件
     */
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide() || !(player instanceof ServerPlayer sp)) {
            return;
        }
        if (!sp.level().getGameRules().getBoolean(PDGameRules.SAN_CHECK_SYSTEM)) {
            return;
        }

        Level level = sp.level();
        int variabilityInterval = Math.max(1, level.getGameRules().getInt(PDGameRules.SAN_VARIABILITY_PER_TICK));
        if (sp.tickCount > 0 && sp.tickCount % variabilityInterval == 0) {
            double variability = sp.getAttributeValue(PDAttributes.SAN_VARIABILITY);
            if (variability != 0.0D) {
                // 原版：num * attr / 1200.0 → 每分钟约 attr 点
                PDAttachments.addPlayerSanWithCheck(sp, variabilityInterval * variability / 1200.0D);
            }
        }

        int totalInterval = Math.max(1, PDCommonConfig.PLAYER_TOTAL_TICK_UPDATE.get());
        if (sp.tickCount % totalInterval != 0) {
            return;
        }

        if (Boolean.TRUE.equals(PDCommonConfig.LOW_SAN_DEBUFF.get()) && !sp.isSpectator()) {
            double san = PDAttachments.getSan(sp).sanValue();
            if (san <= 1.0D) {
                sp.addEffect(new MobEffectInstance(PDEffects.INSAND_BUFF, 20, 2, false, false));
            } else if (san <= 10.0D) {
                sp.addEffect(new MobEffectInstance(PDEffects.INSAND_BUFF, 20, 1, false, false));
            } else if (san <= 20.0D) {
                sp.addEffect(new MobEffectInstance(PDEffects.INSAND_BUFF, 20, 0, false, false));
            } else if (san <= 40.0D) {
                sp.addEffect(new MobEffectInstance(PDEffects.TRANCE_BUFF.holder(), 20, 0, false, false));
            } else if (san <= 60.0D) {
                sp.addEffect(new MobEffectInstance(PDEffects.LETHARGY_BUFF.holder(), 20, 0, false, false));
            }
            double cheerupThreshold = PDCommonConfig.CHEERUP_BUFF_THRESHOLD_VALUE.get();
            if (san >= cheerupThreshold) {
                sp.addEffect(new MobEffectInstance(PDEffects.CHEERUP_BUFF, 20, 0, false, false));
            }
        }

        applyEnvironmentSan(sp, level);
    }

    /**
     * 刷新环境 SAN_VARIABILITY 瞬时修饰符；风维挂云雾以便 Y≤5 出维。
     *
     * @param player 服务端玩家
     * @param level  当前维度
     */
    private static void applyEnvironmentSan(ServerPlayer player, Level level) {
        AttributeInstance instance = player.getAttribute(PDAttributes.SAN_VARIABILITY);
        if (instance == null) {
            return;
        }
        instance.removeModifier(ENV_SAN_MODIFIER_ID);

        double environment = 0.0D;
        boolean noWhiteFlower = CuriosApi.getCuriosInventory(player)
                .map(handler -> handler.findFirstCurio(PDItems.WHITE_FLOWER_BODY.get()).isEmpty())
                .orElse(true);

        if (noWhiteFlower) {
            if (level.dimension() == Level.OVERWORLD && !level.isDay()
                    && Boolean.TRUE.equals(PDCommonConfig.OVERWORLD_NIGHT_LOWERS_SAN.get())) {
                environment -= 0.96D;
            }
            if (PDDimensions.isLampShadowWorld(level)) {
                environment -= 2.4D;
            }
            if (PDDimensions.isAaroncosArenaWorld(level)) {
                environment -= 9.6D;
            }
        }
        if (PDDimensions.isDyedreamWorld(level)) {
            environment += 4.8D;
        }
        if (PDDimensions.isWindJourneyWorld(level)) {
            environment += 1.2D;
            // 出维依赖 cloudmist_buff tick；原版 SanHelper 每 total-tick 续 200t
            player.addEffect(new MobEffectInstance(PDEffects.CLOUDMIST_BUFF.holder(), 200, 0, false, false));
        }

        instance.addTransientModifier(new AttributeModifier(
                ENV_SAN_MODIFIER_ID,
                environment,
                AttributeModifier.Operation.ADD_VALUE));
    }
}
