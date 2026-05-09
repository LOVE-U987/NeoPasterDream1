package net.pasterdream.procedures;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import java.util.UUID;

public class RageElixirProcedure {
    private static final UUID uuid = UUID.fromString("3a1020b9-f021-4666-9978-049631cef027");
	public static void execute(Level world, double x, double y, double z, LivingEntity entity, int level) {
		if (entity == null)
			return;
        AttributeInstance instance = entity.getAttribute(Attributes.ATTACK_SPEED);
        if(instance != null)
        {
            AttributeModifier modifier = instance.getModifier(uuid);
            if(modifier == null || modifier.getAmount() < getValue(level))
            {
                if (!world.isClientSide) {
                    instance.removeModifier(uuid);
                    instance.addPermanentModifier(new AttributeModifier(uuid,"rage_elixir",getValue(level), AttributeModifier.Operation.ADDITION));
                    world.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.beacon.activate")), SoundSource.NEUTRAL, 1, 1);
                } else {
                    if (entity instanceof Player pl )
                        pl.displayClientMessage(Component.translatable("item.pasterdream.rage_elixir.client.success"), false);
                    world.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.beacon.activate")), SoundSource.NEUTRAL, 1, 1, false);
                }
            }
            else {
                if (entity instanceof Player pl && world.isClientSide)
                    pl.displayClientMessage(Component.translatable("item.pasterdream.rage_elixir.client.fail"), false);
            }
        }
	}
    public static double getValue(int level)
    {
        return 0.1 + 0.2 * (level - 1);
    }
}
