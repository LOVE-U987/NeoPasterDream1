package com.pasterdream.pasterdreammod.pasterdreamsanity;

import com.pasterdream.pasterdreammod.api.attribute.APIAttributes;
import com.pasterdream.pasterdreammod.api.dimension.APIDimensions;
import com.pasterdream.pasterdreammod.api.san.APISanGameRules;
import com.pasterdream.pasterdreammod.api.san.SanConfigRegistry;
import com.pasterdream.pasterdreammod.api.san.SanHelper;
import com.pasterdream.pasterdreammod.pasterdreamsanity.registry.PDSanityEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;

/**
 * PasterDreamSanity 理智系统玩家 tick 处理器。
 * <p>
 * 从 PasterDream 主模组迁移而来，负责服务端玩家每 tick 的 San 值变化、
 * 低/高 San 效果施加以及环境 SAN_VARIABILITY 修饰符刷新。
 */
public final class PDSanityHelper {

    /** 环境 SAN_VARIABILITY 修饰符 ID */
    private static final ResourceLocation ENV_SAN_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath("pasterdream", "environment_san_variability");

    private PDSanityHelper() {
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
        if (!sp.level().getGameRules().getBoolean(APISanGameRules.SAN_CHECK_SYSTEM)
                || !Boolean.TRUE.equals(SanConfigRegistry.get().enabled().get())) {
            return;
        }

        Level level = sp.level();
        int variabilityInterval = Math.max(1, level.getGameRules().getInt(APISanGameRules.SAN_VARIABILITY_PER_TICK));
        if (sp.tickCount > 0 && sp.tickCount % variabilityInterval == 0) {
            double variability = sp.getAttributeValue(APIAttributes.SAN_VARIABILITY);
            if (variability != 0.0D) {
                SanHelper.addPlayerSanWithCheck(sp, variabilityInterval * variability / 1200.0D);
            }
        }

        int totalInterval = Math.max(1, SanConfigRegistry.get().tickUpdateInterval().get());
        if (sp.tickCount % totalInterval != 0) {
            return;
        }

        if (Boolean.TRUE.equals(SanConfigRegistry.get().enableLowSanDebuff().get()) && !sp.isSpectator()) {
            double san = SanHelper.getSan(sp).sanValue();
            if (san <= 1.0D) {
                sp.addEffect(new MobEffectInstance(PDSanityEffects.INSAND_BUFF, 20, 2, false, false));
            } else if (san <= 10.0D) {
                sp.addEffect(new MobEffectInstance(PDSanityEffects.INSAND_BUFF, 20, 1, false, false));
            } else if (san <= 20.0D) {
                sp.addEffect(new MobEffectInstance(PDSanityEffects.INSAND_BUFF, 20, 0, false, false));
            } else if (san <= 40.0D) {
                sp.addEffect(new MobEffectInstance(PDSanityEffects.TRANCE_BUFF.holder(), 20, 0, false, false));
            } else if (san <= 60.0D) {
                sp.addEffect(new MobEffectInstance(PDSanityEffects.LETHARGY_BUFF.holder(), 20, 0, false, false));
            }
            double cheerupThreshold = SanConfigRegistry.get().cheerupThreshold().get();
            if (san >= cheerupThreshold) {
                sp.addEffect(new MobEffectInstance(PDSanityEffects.CHEERUP_BUFF, 20, 0, false, false));
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
        AttributeInstance instance = player.getAttribute(APIAttributes.SAN_VARIABILITY);
        if (instance == null) {
            return;
        }
        instance.removeModifier(ENV_SAN_MODIFIER_ID);

        double environment = 0.0D;
        boolean noWhiteFlower = getWhiteFlowerBody()
                .map(item -> CuriosApi.getCuriosInventory(player)
                        .map(handler -> handler.findFirstCurio(item).isEmpty())
                        .orElse(true))
                .orElse(true);

        if (noWhiteFlower) {
            if (level.dimension() == Level.OVERWORLD && !level.isDay()
                    && Boolean.TRUE.equals(SanConfigRegistry.get().overworldNightLowersSan().get())) {
                environment -= 0.96D;
            }
            if (APIDimensions.isLampShadowWorld(level)) {
                environment -= 2.4D;
            }
            if (APIDimensions.isAaroncosArenaWorld(level)) {
                environment -= 9.6D;
            }
        }
        if (APIDimensions.isDyedreamWorld(level)) {
            environment += 4.8D;
        }
        if (APIDimensions.isWindJourneyWorld(level)) {
            environment += 1.2D;
            getCloudmistBuff().ifPresent(effect -> player.addEffect(new MobEffectInstance(effect, 200, 0, false, false)));
        }

        instance.addTransientModifier(new AttributeModifier(
                ENV_SAN_MODIFIER_ID,
                environment,
                AttributeModifier.Operation.ADD_VALUE));
    }

    /**
     * 运行时获取白花胸针物品（主模组可能未安装，返回 empty 时不影响逻辑）。
     *
     * @return 白花胸针 Optional
     */
    private static Optional<Item> getWhiteFlowerBody() {
        return BuiltInRegistries.ITEM.getOptional(
                ResourceLocation.fromNamespaceAndPath("pasterdream", "white_flower_body"));
    }

    /**
     * 运行时获取云雾效果（cloudmist_buff）Holder（主模组可能未安装）。
     *
     * @return 云雾效果 Optional
     */
    @SuppressWarnings("unchecked")
    private static Optional<Holder<MobEffect>> getCloudmistBuff() {
        return BuiltInRegistries.MOB_EFFECT.getHolder(
                        ResourceLocation.fromNamespaceAndPath("pasterdream", "cloudmist_buff"))
                .map(holder -> (Holder<MobEffect>) holder);
    }
}
