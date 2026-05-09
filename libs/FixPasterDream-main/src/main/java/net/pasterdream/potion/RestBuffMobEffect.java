
package net.pasterdream.potion;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.LevelAccessor;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.pasterdream.init.PasterdreamModMobEffects;

public class RestBuffMobEffect extends MobEffect {
	public RestBuffMobEffect(int fix) {
		super(MobEffectCategory.BENEFICIAL, -3873025);
        this.addAttributeModifier(PasterdreamModAttributes.SAN_VARIABILITY.get(), uuid, 0.6 * fix, AttributeModifier.Operation.ADDITION);
    }
    static String uuid = "71614fba-528a-4ba8-a0f3-26c47629cc24";
	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide) {
            LevelAccessor world = entity.level();
            double x = entity.getX();
            double y = entity.getY();
            double z = entity.getZ();
            MobEffectInstance oldInstance;
            MobEffectInstance newInstance = null;
            if (world.canSeeSkyFromBelowWater(BlockPos.containing(x, y, z)) || world.getMaxLocalRawBrightness(BlockPos.containing(x, y, z)) < 7) {
                oldInstance = entity.getEffect(PasterdreamModMobEffects.REST_BUFF_IN_DARK.get());
                if(oldInstance != null)
                {
                    newInstance = new MobEffectInstance(PasterdreamModMobEffects.REST_BUFF.get(),
                            oldInstance.getDuration(),
                            oldInstance.getAmplifier(),
                            oldInstance.isAmbient(),
                            oldInstance.isVisible(),
                            oldInstance.showIcon());
                }
            } else {
                oldInstance = entity.getEffect(PasterdreamModMobEffects.REST_BUFF.get());
                if(oldInstance != null)
                {
                    newInstance = new MobEffectInstance(PasterdreamModMobEffects.REST_BUFF_IN_DARK.get(),
                        oldInstance.getDuration(),
                        oldInstance.getAmplifier(),
                        oldInstance.isAmbient(),
                        oldInstance.isVisible(),
                        oldInstance.showIcon());
                }
            }
            if(oldInstance != null)
            {
                entity.removeEffect(oldInstance.getEffect());
                entity.addEffect(newInstance);
            }
        }
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
