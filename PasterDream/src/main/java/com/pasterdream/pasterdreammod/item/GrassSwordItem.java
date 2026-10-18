package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.util.PasterItemData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.List;

/**
 * 草薙 (grass_sword)
 * <p>
 * 还原自原版 GrassSwordItem + GrassSwordPr0/Pr1Procedure：
 * <ul>
 *   <li>剑属性与既有 ItemAPI 注册逐项一致（耐久 874、攻击加成 6、攻速 -2.5、附魔能力 16），
 *       并还原原版修复材料丛林孢子</li>
 *   <li>攻击命中给目标附加中毒 3 秒（原版 GrassSwordPr0），并在 1 tick 后判定目标死亡，
 *       死亡则物品击杀计数 {@code killed} +1</li>
 *   <li>手持时若 {@code killed >= 200}（原版 GrassSwordPr1），消耗草薙并为玩家发放
 *       『天丛云』草薙，提示"武器成长到了新的阶段"并播放 skill0 音效、喷发融梦水晶粒子
 *       （成长机制不走武器工作台）</li>
 * </ul>
 */
public class GrassSwordItem extends SwordItem {

    /** 成长所需击杀数（原版 killed >= 200） */
    private static final double GROWTH_KILL_REQUIREMENT = 200;

    /** 命中附加的中毒时长（tick），原版 60 tick = 3 秒 */
    private static final int POISON_DURATION_TICKS = 60;

    /** 物品击杀计数键（原版 NBT killed） */
    private static final String KEY_KILLED = "killed";

    /** 与既有 ItemAPI 注册等值的 tier：耐久 874 / 挖速 2 / 伤害加成 6 / 附魔 16，修复材料丛林孢子 */
    private static final SimpleTier TIER = new SimpleTier(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL, 874, 2.0f, 6.0f, 16,
            () -> Ingredient.of(new ItemStack(PDItems.JUNGLE_SPORE.get())));

    public GrassSwordItem(Properties properties) {
        // 攻击伤害由 tier 承载（伤害参数 0，与 ItemAPI 方案一致）
        super(TIER, properties.attributes(SwordItem.createAttributes(TIER, 0, -2.5f)));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        // 原版 GrassSwordPr0Procedure：命中附加中毒；1 tick 后目标已死亡则击杀计数 +1
        if (!target.level().isClientSide()) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON, POISON_DURATION_TICKS, 0));
            // 原版 queueServerWork(1)：延迟 1 tick 判定该击是否致死
            ServerScheduler.schedule(1, () -> {
                if (!target.isAlive()) {
                    PasterItemData.putDouble(stack, KEY_KILLED,
                            PasterItemData.getDouble(stack, KEY_KILLED) + 1);
                }
            });
        }
        return result;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (isSelected) {
            checkGrowth(level, entity, stack);
        }
    }

    /**
     * 原版 GrassSwordPr1Procedure：击杀数达标时成长为『天丛云』草薙
     * <p>
     * 在服务端执行：消耗手中草薙、为玩家发放 TRUE_GRASS_SWORD（继承旧剑全部
     * 物品组件——附魔/耐久损耗/自定义数据，属对原版的体验改进）、在快捷栏提示
     * "武器成长到了新的阶段"，播放 skill0 音效并喷发融梦水晶粒子。
     *
     * @param level  世界
     * @param entity 手持者
     * @param stack  草薙物品栈
     */
    private static void checkGrowth(Level level, Entity entity, ItemStack stack) {
        if (level.isClientSide()) {
            return;
        }
        if (PasterItemData.getDouble(stack, KEY_KILLED) < GROWTH_KILL_REQUIREMENT) {
            return;
        }
        if (entity instanceof Player player) {
            // 构造『天丛云』草薙并继承旧剑全部组件（附魔/耐久损耗/killed 计数等）；
            // 必须先取组件再 shrink——旧剑计数归零后组件即被清空
            ItemStack grown = new ItemStack(PDItems.TRUE_GRASS_SWORD.get());
            grown.applyComponents(stack.getComponentsPatch());
            ItemHandlerHelper.giveItemToPlayer(player, grown);
            player.displayClientMessage(Component.translatable("message.pasterdream.grass_sword.growth"), true);
        }
        stack.shrink(1);
        BlockPos pos = BlockPos.containing(entity.getX(), entity.getY(), entity.getZ());
        level.playSound(null, pos, PDSounds.SKILL0.get(), SoundSource.NEUTRAL, 1f, 1f);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles((SimpleParticleType) PDParticles.MELTDREAM_CRYSTAL_PARTICLE.particleType(),
                    entity.getX(), entity.getY(), entity.getZ(), 5, 1, 1, 1, 0.5);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        // 原版 appendHoverText 逐行还原
        tooltip.add(Component.translatable("tooltip.pasterdream.grass_sword.inlay"));
        tooltip.add(Component.translatable("tooltip.pasterdream.grass_sword.poison"));
    }
}
