package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.attachment.PDAttachments;
import com.pasterdream.pasterdreammod.registry.PDAttributes;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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
 * <p>
 * GeckoLib 3D 手持模型；右键对范围内 {@code pasterdream:shadow_mob} 施加易伤，
 * 播放粒子/音效并消耗 1 SAN。手持提供 SAN 变化率 +1.2。
 * 客户端渲染器由 {@code PDClientItemExtensions} 注册。
 */
public class ShadowHandLanternItem extends Item implements GeoItem {

    private static final int USE_COOLDOWN = 160;
    private static final int VULNERABILITY_DURATION = 300;
    /** 原版 AABB.inflate(15/2) */
    private static final double EFFECT_RADIUS = 15.0D;
    private static final ResourceLocation SAN_VARIABILITY_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "shadow_hand_lantern_san_variability");
    private static final TagKey<net.minecraft.world.entity.EntityType<?>> SHADOW_MOB =
            TagKey.create(Registries.ENTITY_TYPE,
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "shadow_mob"));

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
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        return super.getDefaultAttributeModifiers(stack)
                .withModifierAdded(
                        PDAttributes.SAN_VARIABILITY,
                        new AttributeModifier(SAN_VARIABILITY_MODIFIER_ID, 1.2D,
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HAND);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tip, TooltipFlag flag) {
        super.appendHoverText(stack, ctx, tip, flag);
        tip.add(Component.literal("§7▪ §9手持提灯时理智光环+1.2san/分钟"));
        tip.add(Component.literal("§7右键使用"));
        tip.add(Component.literal("§7▪ §9直径15格范围内的暗影生物受到20%的易伤效果持续15秒"));
        tip.add(Component.literal("§7▪ §9冷却时间：8秒"));
        tip.add(Component.literal("§7▪ §4精神值消耗：1"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }

        player.getCooldowns().addCooldown(this, USE_COOLDOWN);
        this.animationprocedure = "1";

        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        level.playSound(null, player.blockPosition(), PDSounds.SHADOW_HAND_LANTERN.get(),
                SoundSource.PLAYERS, 1f, 1f);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.END_ROD, x, y, z, 128, 4.0, 0.8, 4.0, 0.03);
            serverLevel.sendParticles(ParticleTypes.ASH, x, y, z, 256, 4.0, 0.8, 4.0, 0.03);
        }

        Vec3 center = player.position();
        AABB box = new AABB(center, center).inflate(EFFECT_RADIUS / 2.0D);
        for (Entity target : level.getEntitiesOfClass(Entity.class, box, e -> true)) {
            if (target instanceof LivingEntity living && target.getType().is(SHADOW_MOB)) {
                // amp=0 → PDEffectEvents 按 (amp+1)*20% 易伤
                living.addEffect(new MobEffectInstance(
                        PDEffects.VULNERABILITY_BUFF.holder(), VULNERABILITY_DURATION, 0, false, false));
            }
        }

        PDAttachments.addPlayerSanWithCheck(player, -1);
        return InteractionResultHolder.success(stack);
    }
}
