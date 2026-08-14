package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import com.pasterdream.pasterdreammod.attachment.PDAttachments;
import com.pasterdream.pasterdreammod.entity.projectile.StrawberryHeartProjectileEntity;
import com.pasterdream.pasterdreammod.registry.PDAttributes;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * 草莓甜心 (strawberry_heart)
 * <p>
 * 还原自原版 StrawberryHeartItem（松开蓄力施法的法杖）：
 * <ul>
 *   <li>右键蓄力，<b>松开时</b>发射草莓甜心音符弹（消耗魔法石），法杖自体 hurtAndBreak(1)</li>
 *   <li>施法后执行 StrawberryHeartPr0Procedure 统一冷却：佩戴俏皮鬼头饰（qym_head）→ 本法杖冷却 0；
 *       否则全部 pasterdream:magic 物品冷却 12 + 法术冷却属性 tick</li>
 *   <li>潜行右击演奏：消耗 0.25 融梦能量，播放 4 段吉他琶音（延迟 4/7/10 tick），
 *       为 8 格半径内玩家回复 4 点生命并给予短暂生命恢复/力量/速度，随后全部法术物品长冷却
 *       100 + 法术冷却属性 tick；能量不足时提示</li>
 * </ul>
 */
public class StrawberryHeartItem extends Item {

    /** 演奏消耗的融梦能量 */
    private static final double PLAY_ENERGY_COST = 0.25;

    /** 演奏增益半径（格）——原版 AABB.inflate(8/2d) */
    private static final double PLAY_RANGE = 8d / 2d;

    /**
     * 构造方法
     *
     * @param properties 物品属性
     */
    public StrawberryHeartItem(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return 0f;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.strawberry_heart.spell_damage"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.strawberry_heart.projectile_kinetic"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.strawberry_heart.cooldown"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.strawberry_heart.cost"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.strawberry_heart.play"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.strawberry_heart.heal"));
        tooltipComponents.add(Component.literal("§o§7 -- Show by rock !"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = InteractionResultHolder.success(player.getItemInHand(hand));
        player.startUsingItem(hand);
        return result;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (level.isClientSide() || !(entity instanceof ServerPlayer player)) {
            return;
        }
        // 原版 StrawberryHeartPr0Procedure：统一冷却 + 潜行演奏（不依赖弹药）
        applyStrawberryHeartProcedure(level, player, stack);
        // 弹药查找：先看双手，再扫主背包（弹药 = 魔法石）
        ItemStack ammo = ProjectileWeaponItem.getHeldProjectile(entity,
                s -> s.getItem() == PDItems.MAGIC_STONE.get());
        if (ammo.isEmpty()) {
            for (int i = 0; i < player.getInventory().items.size(); i++) {
                ItemStack candidate = player.getInventory().items.get(i);
                if (candidate != null && candidate.getItem() == PDItems.MAGIC_STONE.get()) {
                    ammo = candidate;
                    break;
                }
            }
        }
        if (!player.getAbilities().instabuild && ammo.isEmpty()) {
            return;
        }
        StrawberryHeartProjectileEntity projectile =
                StrawberryHeartProjectileEntity.shoot(level, entity, level.getRandom());
        stack.hurtAndBreak(1, entity, LivingEntity.getSlotForHand(entity.getUsedItemHand()));
        if (player.getAbilities().instabuild) {
            projectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        } else {
            AbstractChargeWandItem.consumeAmmo(level, player, ammo);
        }
    }

    /**
     * 原版 StrawberryHeartPr0Procedure：
     * <ul>
     *   <li>冷却：佩戴俏皮鬼头饰（qym_head）→ 本法杖冷却 0（免冷却）；否则全部
     *       pasterdream:magic 物品冷却 12 + 法术冷却属性 tick</li>
     *   <li>潜行演奏：消耗 0.25 融梦能量 → 4 段吉他琶音 + 范围内玩家回血/增益，
     *       随后全部法术物品长冷却 100 + 法术冷却属性 tick；能量不足提示</li>
     * </ul>
     *
     * @param level  世界（服务端）
     * @param player 施法者
     * @param stack  本法杖物品栈
     */
    private static void applyStrawberryHeartProcedure(Level level, ServerPlayer player, ItemStack stack) {
        if (WandSupport.hasCurioEquipped(player, PDItems.QYM_HEAD.get())) {
            player.getCooldowns().addCooldown(stack.getItem(), 0);
        } else {
            AttributeInstance magicCd = player.getAttribute(PDAttributes.MAGICCD);
            int ticks = (int) (12 * (magicCd != null ? magicCd.getValue() : 1));
            WandSupport.applyTaggedCooldown(player, WandSupport.MAGIC_TAG, ticks);
        }
        if (!player.isShiftKeyDown()) {
            return;
        }
        if (!PDAttachments.consumePlayerMeltDreamEnergy(player, PLAY_ENERGY_COST)) {
            player.displayClientMessage(
                    Component.translatable("message.pasterdream.strawberry_heart.no_energy"), true);
            return;
        }
        // 4 段吉他琶音：立即 + 延迟 4/7/10 tick（原版 queueServerWork）
        playGuitarNote(level, player, 1.2f, 0.8f);
        ServerScheduler.schedule(4, () -> playGuitarNote(level, player, 1.2f, 1.0f));
        ServerScheduler.schedule(7, () -> playGuitarNote(level, player, 1.2f, 1.2f));
        ServerScheduler.schedule(10, () -> playGuitarNote(level, player, 1.4f, 1.7f));
        // 为 8 格半径内所有玩家回复 4 点生命并给予短暂生命恢复/力量/速度
        AABB range = new AABB(player.getX() - PLAY_RANGE, player.getY() - PLAY_RANGE, player.getZ() - PLAY_RANGE,
                player.getX() + PLAY_RANGE, player.getY() + PLAY_RANGE, player.getZ() + PLAY_RANGE);
        for (Player target : level.getEntitiesOfClass(Player.class, range)) {
            target.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 0));
            target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));
            target.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 0));
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 0));
        }
        // 演奏后全部法术物品长冷却
        AttributeInstance magicCd = player.getAttribute(PDAttributes.MAGICCD);
        int ticks = (int) (100 * (magicCd != null ? magicCd.getValue() : 1));
        WandSupport.applyTaggedCooldown(player, WandSupport.MAGIC_TAG, ticks);
    }

    /**
     * 在施法者位置播放一段吉他琶音（block.note_block.guitar）
     *
     * @param level  世界（服务端）
     * @param player 施法者
     * @param volume 音量
     * @param pitch  音调
     */
    private static void playGuitarNote(Level level, Player player, float volume, float pitch) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.NOTE_BLOCK_GUITAR.value(), SoundSource.PLAYERS, volume, pitch);
    }
}
