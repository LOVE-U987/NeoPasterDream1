package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * 塞西莉娅的加护（ceciliacare_charm）。
 * <p>
 * 还原自原版 CeciliacareCharmItem + CeciliaCarePr0Procedure：
 * <b>不依赖常驻药水标记</b>（避免清效果怪抹掉守护）；佩戴后由 curioTick
 * 直接检测生命 ≤ 最大生命 15% 时一次性触发：
 * 消耗本饰品 → 抗性 IV / 再生 III 5 秒、移速 II / 跳跃 I 10 秒、
 * 清除瞬身术冷却标记、图腾音效与粒子，并返还失色塞西莉娅的加护。
 * <p>
 * 触发时爆发药水 {@code visible/showIcon=true}，便于确认生效
 * （原版 MCreator 写成 false,false，HUD 上看不到图标）。
 */
public class CeciliacareCharmItem extends Item implements ICurioItem {

    public CeciliacareCharmItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
    }

    @Override
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.literal("品质：§5上古 ★★★★★"));
        list.add(Component.literal("§7■ §9当前生命值低于最大生命值的15%时可被触发"));
        list.add(Component.literal("§7■ §9获得5秒的无敌时间"));
        list.add(Component.literal("§7■ §9并在5秒内快速恢复生命 10秒内增加移速和跳跃高度"));
        list.add(Component.literal("§7■ §9立刻重置瞬身术的cd时间"));
        list.add(Component.literal("§7§o--我会守护你，直到永远..."));
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity == null) {
            return;
        }
        Level level = entity.level();
        if (level.isClientSide() || stack.isEmpty()) {
            return;
        }
        // 直接按生命判定，不挂常驻 MobEffect 标记
        if (entity.getHealth() > entity.getMaxHealth() * 0.15f) {
            return;
        }

        // 从 Curios 槽卸下（比单纯 shrink 更能保证槽位同步）
        CuriosApi.getCuriosInventory(entity).ifPresent(inv ->
                inv.setEquippedCurio(slotContext.identifier(), slotContext.index(), ItemStack.EMPTY));
        if (!stack.isEmpty()) {
            stack.setCount(0);
        }

        // ambient=false, visible=true, showIcon=true — 触发后 HUD 可见
        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 4, false, true, true));
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 3, false, true, true));
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 2, false, true, true));
        entity.addEffect(new MobEffectInstance(MobEffects.JUMP, 200, 1, false, true, true));
        entity.removeEffect(PDEffects.TELEPORTATION_BUFF.holder());

        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        BlockPos pos = BlockPos.containing(x, y, z);
        level.playSound(null, pos, SoundEvents.TOTEM_USE, SoundSource.NEUTRAL, 1.0f, 1.0f);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles((SimpleParticleType) PDParticles.DYEDREAM_0_PARTICLE.particleType(),
                    x, y, z, 64, 1, 1, 1, 0.2);
            serverLevel.sendParticles((SimpleParticleType) PDParticles.DUST_0_PARTICLE.particleType(),
                    x, y, z, 64, 1, 1, 1, 0.2);
            serverLevel.sendParticles((SimpleParticleType) PDParticles.MELTDREAM_CRYSTAL_BIG_PARTICLE.particleType(),
                    x, y, z, 32, 1, 1, 1, 0.2);
        }

        if (entity instanceof Player player) {
            // 原版客户端 displayItemActivation（塞西莉娅加护全屏展示）
            com.pasterdream.pasterdreammod.network.PDNetwork.sendItemActivation(
                    player, PDItems.CECILIACARE_CHARM.get());
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(PDItems.TURN_PALE_CECILIA.get()));
        }
    }
}
