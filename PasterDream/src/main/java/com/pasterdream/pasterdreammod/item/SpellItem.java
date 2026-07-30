package com.pasterdream.pasterdreammod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 法术物品 (Spell Item)
 * <p>
 * 还原自原版 PasterDream 的五种法术（闪电/剧毒/治疗/狂怒/冰冻），
 * 由梦境炼药锅炼制产出。与原版一致的使用体验：
 * <ul>
 *   <li>右键开始蓄力（最长 72000 tick，与弓类似）</li>
 *   <li>松开后施放法术，非创造模式消耗 1 个</li>
 *   <li>堆叠上限 8</li>
 * </ul>
 * 施法的具体效果由 {@link SpellCast} 回调注入（发射法术投射物）。
 */
public class SpellItem extends Item {

    /**
     * 施法回调：在服务端发射法术投射物 / 触发法术效果
     */
    @FunctionalInterface
    public interface SpellCast {
        /**
         * 执行施法
         *
         * @param level  服务端世界
         * @param caster 施法者
         */
        void cast(ServerLevel level, LivingEntity caster);
    }

    /** 施法效果回调；null 表示无施法逻辑的扩展位（不消耗物品）。现有五法术均非 null。 */
    @Nullable
    private final SpellCast spellCast;

    /** 悬浮提示行的翻译键 */
    private final String[] tooltipKeys;

    /**
     * 构造法术物品
     *
     * @param properties  物品属性（堆叠 8、普通稀有度）
     * @param spellCast   施法效果回调（null = 扩展位，施放时不做任何事、不消耗）
     * @param tooltipKeys 悬浮提示行翻译键（依序显示）
     */
    public SpellItem(Properties properties, @Nullable SpellCast spellCast, String... tooltipKeys) {
        super(properties);
        this.spellCast = spellCast;
        this.tooltipKeys = tooltipKeys;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        // 与原版一致：类似弓的长蓄力时间
        return 72000;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        for (String key : tooltipKeys) {
            tooltip.add(Component.translatable(key));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        // 开始蓄力，松开时经由 releaseUsing 施放
        player.startUsingItem(hand);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (level.isClientSide() || !(entity instanceof Player player)) {
            return;
        }
        if (spellCast == null) {
            // 扩展位：无回调时不消耗
            return;
        }
        spellCast.cast((ServerLevel) level, entity);
        // 与原版一致：非创造模式施放后消耗 1 个法术
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
    }
}
