
package net.pasterdream.world.dimension;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.pasterdream.procedures.LampShadowPr1Procedure;
import net.pasterdream.procedures.LampShadowPr0Procedure;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import org.joml.Matrix4f;

@Mod.EventBusSubscriber
public class LampShadowWorldDimension {
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class DimensionSpecialEffectsHandler3 {
        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        public static void registerDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
			DimensionSpecialEffects customEffect = new DimensionSpecialEffects(Float.NaN, true, DimensionSpecialEffects.SkyType.NONE, false, true) {
				@Override
				public Vec3 getBrightnessDependentFogColor(Vec3 color, float sunHeight) {
					return new Vec3(0.5215686275, 0.5215686275, 0.5215686275);
				}

				@Override
				public boolean isFoggyAt(int x, int y) {
					return false;
				}

                @Override
                public boolean renderSnowAndRain(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ) {
                    return true;
                }
                @Override
                public boolean tickRain(ClientLevel level, int ticks, Camera camera) {
                    return true;
                }
			};
			event.register(new ResourceLocation("pasterdream:lamp_shadow_world"), customEffect);
		}
	}
    @SubscribeEvent
    public static void onPlayerChangedDimensionEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
        Entity entity = event.getEntity();
        Level world = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        if (event.getFrom() == ResourceKey.create(Registries.DIMENSION, new ResourceLocation("pasterdream:lamp_shadow_world"))) {
            LampShadowPr1Procedure.execute(entity);
        }
        if (event.getTo() == ResourceKey.create(Registries.DIMENSION, new ResourceLocation("pasterdream:lamp_shadow_world"))) {
            LampShadowPr0Procedure.execute(world, x, y, z, entity);
        }
    }

}
