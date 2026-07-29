package com.pasterdream.pasterdreammod.item.armor;

import com.pasterdream.pasterdreammod.client.renderer.armor.AngelWingArmorRenderer;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import com.pasterdream.pasterdreammod.registry.PDArmorMaterials;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
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
 * 天使之翼胸甲 (angel_wing)
 * <p>
 * 装备胸部槽时提供飞行与摔落免疫；白羽粒子。受 {@link PDCommonConfig#BAN_ALL_THE_WINGS} 约束。
 */
public class AngelWingItem extends ArmorItem implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public AngelWingItem() {
        super(PDArmorMaterials.ANGEL_WING, Type.CHESTPLATE, new Item.Properties().stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!(entity instanceof Player player) || level.isClientSide()) {
            return;
        }
        if (player.getItemBySlot(EquipmentSlot.CHEST) != stack) {
            return;
        }
        if (Boolean.TRUE.equals(PDCommonConfig.BAN_ALL_THE_WINGS.get())) {
            player.displayClientMessage(Component.literal("§4此物品已被禁用"), true);
            return;
        }
        if (Math.random() >= 0.4 && level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    (SimpleParticleType) PDParticles.FEATHER_WHITE_PARTICLE.particleType(),
                    player.getX(), player.getY() + 1, player.getZ(),
                    1, 0.6, 0.3, 0.6, 0.05);
        }
        // 脱下检测：2+2 tick 后确认未再装备则关飞（对齐原版 queueServerWork）
        ServerScheduler.schedule(2, () -> {
            if (player.getItemBySlot(EquipmentSlot.CHEST).getItem() != this) {
                ServerScheduler.schedule(2, () -> {
                    if (player.getItemBySlot(EquipmentSlot.CHEST).getItem() != this
                            && !player.isCreative() && !player.isSpectator()) {
                        player.getAbilities().mayfly = false;
                        player.getAbilities().flying = false;
                        player.onUpdateAbilities();
                    }
                });
            }
        });
        player.getAbilities().mayfly = true;
        player.onUpdateAbilities();
        player.fallDistance = 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tip, TooltipFlag flag) {
        super.appendHoverText(stack, ctx, tip, flag);
        tip.add(Component.literal("§7▪ §9装备后获得飞行能力"));
        tip.add(Component.literal("§7▪ §9免疫摔落伤害"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, event -> {
            event.getController().setAnimation(RawAnimation.begin().thenLoop("idle"));
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private AngelWingArmorRenderer renderer;

            @Override
            public <E extends net.minecraft.world.entity.LivingEntity> net.minecraft.client.model.HumanoidModel<?> getGeoArmorRenderer(
                    E livingEntity, ItemStack itemStack,
                    net.minecraft.world.entity.EquipmentSlot equipmentSlot,
                    net.minecraft.client.model.HumanoidModel<E> original) {
                if (this.renderer == null) {
                    this.renderer = new AngelWingArmorRenderer();
                }
                return this.renderer;
            }
        });
    }
}
