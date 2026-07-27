package com.pasterdream.pasterdreammod.item.armor;

import com.pasterdream.pasterdreammod.client.renderer.armor.ForsakensWingArmorRenderer;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import com.pasterdream.pasterdreammod.registry.PDArmorMaterials;
import com.pasterdream.pasterdreammod.util.ServerScheduler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
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
 * 遗落之翼胸甲 (forsakens_wing)
 * <p>
 * 飞行 + 摔落免疫，并清除缓慢/凋零。受 {@link PDCommonConfig#BAN_ALL_THE_WINGS} 约束。
 */
public class ForsakensWingItem extends ArmorItem implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ForsakensWingItem() {
        super(PDArmorMaterials.FORSAKENS_WING, Type.CHESTPLATE, new Item.Properties().stacksTo(1));
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
        player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        player.removeEffect(MobEffects.WITHER);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tip, TooltipFlag flag) {
        super.appendHoverText(stack, ctx, tip, flag);
        tip.add(Component.literal("§7▪ §9装备后获得飞行能力"));
        tip.add(Component.literal("§7▪ §9免疫摔落伤害"));
        tip.add(Component.literal("§7▪ §9清除缓慢与凋零效果"));
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
            private ForsakensWingArmorRenderer renderer;

            @Override
            public <E extends net.minecraft.world.entity.LivingEntity> net.minecraft.client.model.HumanoidModel<?> getGeoArmorRenderer(
                    E livingEntity, ItemStack itemStack,
                    net.minecraft.world.entity.EquipmentSlot equipmentSlot,
                    net.minecraft.client.model.HumanoidModel<E> original) {
                if (this.renderer == null) {
                    this.renderer = new ForsakensWingArmorRenderer();
                }
                return this.renderer;
            }
        });
    }
}
