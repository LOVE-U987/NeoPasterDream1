package com.pasterdream.pasterdreammod.client.renderer.armor;

import com.pasterdream.pasterdreammod.client.model.armor.MachineWingModel;
import com.pasterdream.pasterdreammod.item.armor.MachineWingItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

/** 机械之翼胸甲渲染器 */
public class MachineWingArmorRenderer extends GeoArmorRenderer<MachineWingItem> {

    public MachineWingArmorRenderer() {
        super(new MachineWingModel());
        this.head = new GeoBone(null, "armorHead", false, 0.0, false, false);
        this.body = new GeoBone(null, "armorBody", false, 0.0, false, false);
        this.rightArm = new GeoBone(null, "armorRightArm", false, 0.0, false, false);
        this.leftArm = new GeoBone(null, "armorLeftArm", false, 0.0, false, false);
        this.rightLeg = new GeoBone(null, "armorRightLeg", false, 0.0, false, false);
        this.leftLeg = new GeoBone(null, "armorLeftLeg", false, 0.0, false, false);
        this.rightBoot = new GeoBone(null, "armorRightBoot", false, 0.0, false, false);
        this.leftBoot = new GeoBone(null, "armorLeftBoot", false, 0.0, false, false);
    }

    @Override
    public RenderType getRenderType(MachineWingItem animatable, ResourceLocation texture,
                                    MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
