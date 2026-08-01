package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/** 漂泊旅者的染梦竖琴 (dreamharp_of_wanderer) */
public class DreamharpOfWandererItem extends Item {
    public DreamharpOfWandererItem() {
        super(new Item.Properties().stacksTo(1));
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tip, TooltipFlag flag) {
        super.appendHoverText(stack, ctx, tip, flag);
        tip.add(Component.translatable("tooltip.pasterdream.dreamharp_of_wanderer.desc"));
        tip.add(Component.translatable("tooltip.pasterdream.dreamharp_of_wanderer.buff"));
        tip.add(Component.translatable("tooltip.pasterdream.dreamharp_of_wanderer.san_regen"));
        tip.add(Component.translatable("tooltip.pasterdream.dreamharp_of_wanderer.cooldown"));
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) return InteractionResultHolder.success(stack);
        // 融梦能量消耗已剥离至附属 mod
        ServerLevel server = (ServerLevel) level;
        double x=player.getX(), y=player.getY(), z=player.getZ();
        server.sendParticles(ParticleTypes.HEART, x, y, z, 7, 0.45, 0.8, 0.45, 0.5);
        ServerScheduler.schedule(5, () -> server.sendParticles(ParticleTypes.HEART, x, y, z, 7, 0.45, 0.8, 0.45, 0.5));
        ServerScheduler.schedule(10, () -> server.sendParticles(ParticleTypes.HEART, x, y, z, 7, 0.45, 0.8, 0.45, 0.5));
        server.playSound(null, player.blockPosition(), PDSounds.DREAMHARP_OF_WANDERER.get(), SoundSource.PLAYERS, 0.7f, 1f);
        player.getCooldowns().addCooldown(this, 200);
        for (Player p : level.getEntitiesOfClass(Player.class, new AABB(x,y,z,x,y,z).inflate(5))) {
            p.addEffect(new MobEffectInstance(PDEffects.DREAMHARP_OF_WANDERER_BUFF, 1200, 0));
            p.heal(4f);
        }
        return InteractionResultHolder.sidedSuccess(stack, false);
    }
}
