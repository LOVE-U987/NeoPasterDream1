package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * 梦境果汁（dreamjuice）。
 * <p>
 * 还原自原版 DreamjuiceItem + DreamjuicePr0Procedure：
 * 可随时饮用（24 tick）；饮用完成后若玩家已完成前置成就
 * {@code pasterdream:achievement_b_0}，则获得梦愿（dreamwish）效果 90 秒，
 * 并伴随染梦粒子与 dream1 音效；未完成前置成就时仅提示"你尚未了解前置知识"。
 */
public class DreamjuiceItem extends Item {

    /** 饮用成功音效 pasterdream:dream1（音效键由并行任务统一并入 sounds.json，ogg 已随本任务复制） */
    private static final SoundEvent DREAM1_SOUND =
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("pasterdream", "dream1"));

    public DreamjuiceItem(Properties properties) {
        super(properties.stacksTo(8).rarity(Rarity.COMMON)
                .food(new FoodProperties.Builder().nutrition(0).saturationModifier(0f).alwaysEdible().build()));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 24;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.literal("§7甜美而梦幻的味道 让你交融与梦"));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (level instanceof ServerLevel serverLevel && entity instanceof ServerPlayer player) {
            if (hasPrerequisite(player)) {
                player.addEffect(new MobEffectInstance(PDEffects.DREAMWISH_BUFF.holder(), 1800, 0));
                serverLevel.sendParticles((SimpleParticleType) PDParticles.DYEDREAM_0_PARTICLE.particleType(),
                        player.getX(), player.getY() + 0.5, player.getZ(), 64, 1, 1, 1, 0.1);
                serverLevel.playSound(null, BlockPos.containing(player.getX(), player.getY(), player.getZ()),
                        DREAM1_SOUND, SoundSource.PLAYERS, 0.1f, 1.0f);
            } else {
                player.displayClientMessage(Component.literal("你尚未了解前置知识"), false);
            }
        }
        return result;
    }

    /** 判定玩家是否已完成前置成就 achievement_b_0（成就数据缺失时视为未完成，不抛异常） */
    private static boolean hasPrerequisite(ServerPlayer player) {
        AdvancementHolder advancement = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath("pasterdream", "achievement_b_0"));
        return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
    }
}
