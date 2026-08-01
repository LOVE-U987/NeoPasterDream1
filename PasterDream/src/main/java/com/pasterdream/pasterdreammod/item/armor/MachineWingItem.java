package com.pasterdream.pasterdreammod.item.armor;

import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import com.pasterdream.pasterdreammod.registry.PDArmorMaterials;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

/**
 * 机械之翼胸甲 (machine_wing)
 * <p>
 * 每秒刷新 {@link PDEffects#MACHINE_WING_EFFECT} 维持飞行。
 * 融梦能量消耗已剥离至附属 mod，主模组不再扣能。
 */
public class MachineWingItem extends ArmorItem implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    /** 驱动 fly 动画（原版 geckoAnim 标签） */
    public String animationprocedure = "empty";

    public MachineWingItem() {
        super(PDArmorMaterials.MACHINE_WING, Type.CHESTPLATE, new Item.Properties().stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!(entity instanceof Player player)) {
            return;
        }
        if (player.getItemBySlot(EquipmentSlot.CHEST) != stack) {
            return;
        }
        if (Boolean.TRUE.equals(PDCommonConfig.BAN_ALL_THE_WINGS.get())) {
            if (level.isClientSide()) {
                player.displayClientMessage(Component.translatable("tooltip.pasterdream.machine_wing.disabled"), true);
            }
            return;
        }
        // 融梦能量已拆到附属 mod：主模组无条件刷新飞行效果
        if (!level.isClientSide() && player.tickCount % 20 == 0) {
            player.addEffect(new MobEffectInstance(
                    PDEffects.MACHINE_WING_EFFECT.holder(), 22, 0, false, false));
        }
        if (!player.onGround()) {
            this.animationprocedure = "fly";
        } else if ("fly".equals(this.animationprocedure)) {
            this.animationprocedure = "empty";
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tip, TooltipFlag flag) {
        super.appendHoverText(stack, ctx, tip, flag);
        tip.add(Component.translatable("tooltip.pasterdream.machine_wing.flight"));
        tip.add(Component.translatable("tooltip.pasterdream.machine_wing.energy"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, event -> {
            if (!"empty".equals(this.animationprocedure)
                    && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
                if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
                    this.animationprocedure = "empty";
                    event.getController().forceAnimationReset();
                }
                return PlayState.CONTINUE;
            }
            if ("empty".equals(this.animationprocedure)) {
                event.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        // 仅客户端物理环境才创建渲染器：经反射调用客户端专属提供者，避免 common 类硬链客户端类
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }
        try {
            Class<?> providers = Class.forName(
                    "com.pasterdream.pasterdreammod.client.renderer.armor.WingRenderProviders");
            consumer.accept((GeoRenderProvider) providers.getMethod("machineWing").invoke(null));
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("WingRenderProviders.machineWing 反射调用失败", e);
        }
    }
}
