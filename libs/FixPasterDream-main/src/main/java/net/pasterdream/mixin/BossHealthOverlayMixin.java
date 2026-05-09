package net.pasterdream.mixin;

import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
@Mixin(BossHealthOverlay.class)
public interface BossHealthOverlayMixin {
    @Accessor
    Map<UUID, LerpingBossEvent> getEvents();

    /*
    @Inject(method = "update", at = @At("TAIL"))
    public void onUpdate(ClientboundBossEventPacket packet, CallbackInfo ci) {
        var player = Minecraft.getInstance().player;
        assert player != null;
        var nbt = player.serializeNBT();
        packet.dispatch(new ClientboundBossEventPacket.Handler() {
            public void add(UUID uuid, Component name, float progress, BossEvent.BossBarColor color, BossEvent.BossBarOverlay layout, boolean flagx, boolean flagy, boolean flagz) {
                String nameS = name.getString();
                if (nameS.equals(AmarokUtil.left_name)) nbt.putFloat("left_hand_hp", progress);
                else if (nameS.equals(AmarokUtil.right_name)) nbt.putFloat("right_hand_hp", progress);
                else return;

                nbt.putString(uuid.toString(), nameS);
            }

            public void remove(UUID uuid) {
                String name = nbt.contains(uuid.toString()) ? nbt.getString(uuid.toString()) : "";
                if (name.isEmpty()) return;

                if (name.equals(AmarokUtil.left_name)) nbt.remove("left_hand_hp");
                else if (name.equals(AmarokUtil.right_name)) nbt.remove("right_hand_hp");

                nbt.remove(uuid.toString());
            }

            public void updateProgress(UUID uuid , float progress) {
                String name = nbt.contains(uuid.toString()) ? nbt.getString(uuid.toString()) : "";
                if (name.isEmpty()) return;

                if (name.equals(AmarokUtil.left_name)) nbt.putFloat("left_hand_hp", progress);
                else if (name.equals(AmarokUtil.right_name)) nbt.putFloat("right_hand_hp", progress);
            }
        });
    }
    */
}
