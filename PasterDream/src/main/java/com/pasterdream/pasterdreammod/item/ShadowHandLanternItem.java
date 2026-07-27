package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

/**
 * 暗影提灯 (shadow_hand_lantern)
 * GeckoLib 3D 手持模型；客户端渲染器由 {@code PDClientItemExtensions} 注册。
 */
public class ShadowHandLanternItem extends Item implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public String animationprocedure = "empty";
    public static ItemDisplayContext transformType;

    public ShadowHandLanternItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
    }

    public void getTransformType(ItemDisplayContext type) {
        transformType = type;
    }

    private PlayState idlePredicate(software.bernie.geckolib.animation.AnimationState<ShadowHandLanternItem> event) {
        if (transformType != null) {
            if (this.animationprocedure.equals("empty")) {
                event.getController().setAnimation(RawAnimation.begin().thenLoop("0"));
                return PlayState.CONTINUE;
            }
        }
        return PlayState.STOP;
    }

    private PlayState procedurePredicate(software.bernie.geckolib.animation.AnimationState<ShadowHandLanternItem> event) {
        if (transformType != null) {
            if (!this.animationprocedure.equals("empty")
                    && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
                if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
                    this.animationprocedure = "empty";
                    event.getController().forceAnimationReset();
                }
            } else if (this.animationprocedure.equals("empty")) {
                return PlayState.STOP;
            }
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "procedureController", 0, this::procedurePredicate));
        data.add(new AnimationController<>(this, "idleController", 0, this::idlePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tip, TooltipFlag flag) {
        super.appendHoverText(stack, ctx, tip, flag);
        tip.add(Component.literal("§7召唤暗影之手的灯笼"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            player.getCooldowns().addCooldown(this, 160);
            level.playSound(null, player.blockPosition(), PDSounds.SHADOW_HAND_LANTERN.get(),
                    SoundSource.PLAYERS, 1f, 1f);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
