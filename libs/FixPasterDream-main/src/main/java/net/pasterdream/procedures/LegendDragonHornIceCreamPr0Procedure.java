package net.pasterdream.procedures;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.ParticleTypes;

import java.util.UUID;

public class LegendDragonHornIceCreamPr0Procedure {
    private static final UUID uuid = UUID.fromString("31f3e03b-4aea-45dd-8702-49d7000e170c");
    public static void execute(Level world, double x, double y, double z, LivingEntity entity) {
		if (entity == null)
			return;
        AttributeInstance instance = entity.getAttribute(PasterdreamModAttributes.LUCK.get());
        if(instance != null)
        {
            AttributeModifier modifier = instance.getModifier(uuid);
            if(modifier == null)
            {
                if(!world.isClientSide)
                {
                    instance.addPermanentModifier(new AttributeModifier(uuid,"legend_dragon_horn_ice_crea",10, AttributeModifier.Operation.ADDITION));
                    ((ServerLevel)world).sendParticles(ParticleTypes.SNOWFLAKE, x, (y + 3), z, 128, 2, 0.5, 2, 1);
                    if(entity instanceof Player pl)
                    {
                        pl.displayClientMessage(Component.translatable("item.pasterdream.legend_dragon_horn_ice_cream.client.success"), false);
                    }
                }
            }
            else {
                if (entity instanceof Player pl && world.isClientSide())
                    pl.displayClientMessage(Component.translatable("item.pasterdream.legend_dragon_horn_ice_cream.client.fail"), false);
            }
        }
	}
}
