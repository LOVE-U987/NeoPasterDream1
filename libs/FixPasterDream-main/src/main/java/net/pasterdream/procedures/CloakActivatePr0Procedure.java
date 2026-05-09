package net.pasterdream.procedures;

import net.minecraft.world.entity.player.Player;
import net.pasterdream.capability.MeltDreamEnergyCapability;
import top.theillusivec4.curios.api.CuriosApi;

import net.pasterdream.init.PasterdreamModParticleTypes;
import net.pasterdream.init.PasterdreamModMobEffects;
import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.BlockPos;

public class CloakActivatePr0Procedure {
	public static void execute(Level world, double x, double y, double z, Player pl) {
		if (pl == null)
			return;
		CuriosApi.getCuriosInventory(pl)
                .ifPresent(handler -> {
                    if(!pl.getCooldowns().isOnCooldown(PasterdreamModItems.TURNBACK_CLOAK.get()))
                    {
                        handler.findFirstCurio(PasterdreamModItems.TURNBACK_CLOAK.get()).ifPresent(item ->{
                            if (MeltDreamEnergyCapability.consumePlayerMeltDreamEnergy(pl,10)) {
                                if(!world.isClientSide) {
                                    pl.getCooldowns().addCooldown(PasterdreamModItems.TURNBACK_CLOAK.get(),7800);
                                    world.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:cloak")), SoundSource.NEUTRAL, 1, 1);
                                    world.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:evasion")), SoundSource.NEUTRAL, 1, 1);
                                    ((ServerLevel)world).sendParticles(PasterdreamModParticleTypes.GOLDEN_PARTICLE.get(), x, y + 1, z, 32, 0.4, 0.5, 0.4, 0.01);
                                    pl.addEffect(new MobEffectInstance(PasterdreamModMobEffects.TURNBACK_CLOAK_BUFF.get(), 2400, 0, false, false));
                                    pl.addEffect(new MobEffectInstance(PasterdreamModMobEffects.EVASION_BUFF.get(), 2400, 5, false, false));
                                }
                                else {
                                    world.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:cloak")), SoundSource.NEUTRAL, 1, 1, false);
                                    world.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:evasion")), SoundSource.NEUTRAL, 1, 1, false);
                                }
                            }
                        });
                    }
                    if(!pl.getCooldowns().isOnCooldown(PasterdreamModItems.EVASION_CLOAK.get()))
                    {
                        handler.findFirstCurio(PasterdreamModItems.EVASION_CLOAK.get()).ifPresent(item ->{
                            if (MeltDreamEnergyCapability.consumePlayerMeltDreamEnergy(pl,10)) {
                                if(!world.isClientSide) {
                                    pl.getCooldowns().addCooldown(PasterdreamModItems.EVASION_CLOAK.get(),7800);
                                    world.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:cloak")), SoundSource.NEUTRAL, 1, 1);
                                    world.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:evasion")), SoundSource.NEUTRAL, 1, 1);
                                    ((ServerLevel)world).sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.SILVER_PARTICLE.get()), x, y + 1, z, 32, 0.4, 0.5, 0.4, 0.01);
                                    pl.addEffect(new MobEffectInstance(PasterdreamModMobEffects.EVASION_CLOAK_BUFF.get(), 1800, 0, false, false));
                                }
                                else {
                                    world.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:cloak")), SoundSource.NEUTRAL, 1, 1, false);
                                    world.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:evasion")), SoundSource.NEUTRAL, 1, 1, false);
                                }
                            }
                        });
                    }
                });
    }
}
