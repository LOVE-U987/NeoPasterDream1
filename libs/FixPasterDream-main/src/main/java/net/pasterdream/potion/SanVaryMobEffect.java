
package net.pasterdream.potion;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.pasterdream.capability.SanCapability;

public class SanVaryMobEffect extends InstantenousMobEffect {
    private final boolean isIncrease;
	public SanVaryMobEffect(boolean isIncrease) {
        super(isIncrease?MobEffectCategory.BENEFICIAL:MobEffectCategory.HARMFUL, isIncrease?0xADFF2F:0x9b4400);
        this.isIncrease = isIncrease;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if(entity instanceof ServerPlayer pl)
        {
            if(isIncrease)
            {
                SanCapability.addPlayerSanWithCheck(pl,(amplifier & 0xff) + 1);
            }
            else
            {
                SanCapability.addPlayerSanWithCheck(pl,- (amplifier & 0xff) - 1);
            }
        }
    }
}
