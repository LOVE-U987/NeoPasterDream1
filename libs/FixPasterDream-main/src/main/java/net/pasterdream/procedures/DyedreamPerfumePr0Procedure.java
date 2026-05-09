package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModMobEffects;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;

import java.lang.reflect.Field;

public class DyedreamPerfumePr0Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
			_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.DYEDREAM_PERFUME_BUFF.get(), 1200, 0));
		if (entity instanceof Player player) {
            try {
                Class<? extends Player> clazz = player.getClass();
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    String name = field.getName();
                    if (name.equals("sleepCounter")) {
                        field.setAccessible(true);
                        field.set(clazz, 0);
                    }
                }
            } catch (IllegalAccessException exception) {
                exception.printStackTrace();
            }
        }
	}
}
