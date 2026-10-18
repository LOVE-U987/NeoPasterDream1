package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.List;

/**
 * 真熔金镐 (true_moltengold_pickaxe) — 右键急迫（强化版）
 * <p>
 * 对应原版 {@code TrueMoltengoldPickaxeItem}（属性与新模组既有 ItemAPI 注册逐项一致：
 * 耐久 1255 / 挖速 16 / 伤害+4 / 攻速 -2.6 / 石级不适用标签 / 附魔 23）。
 * 右键使用消耗 4 点饱食度换取 30 秒急迫（比普通熔金镐更长），物品冷却仅 8 秒；
 * 按原版补 fireResistant（不会熔毁）。
 */
public class TrueMoltengoldPickaxeItem extends PickaxeItem {

    /** 右键急迫时长（tick），原版 600 tick = 30 秒（比普通熔金镐更长） */
    private static final int HASTE_TICKS = 600;

    /** 物品冷却时长（tick），原版 160 tick = 8 秒（比普通熔金镐更短） */
    private static final int COOLDOWN_TICKS = 160;

    /** 与既有 ItemAPI 注册等值的 tier（修复材料为熔金锭） */
    private static final SimpleTier TIER = new SimpleTier(
            BlockTags.INCORRECT_FOR_STONE_TOOL, 1255, 16.0f, 4.0f, 23,
            () -> Ingredient.of(PDItems.MOLTENGOLD_INGOT.get()));

    public TrueMoltengoldPickaxeItem(Properties properties) {
        // 原版 fireResistant；攻击伤害由 tier 承载（伤害参数 0，与 ItemAPI 方案一致）
        super(TIER, properties.fireResistant().attributes(PickaxeItem.createAttributes(TIER, 0, -2.6f)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = super.use(level, player, hand);
        // 原版 TrueMoltengoldPickaxePr0Procedure：饱食换更长急迫、冷却更短
        MoltengoldToolHelper.applyHaste(player, result.getObject(), HASTE_TICKS, COOLDOWN_TICKS);
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        // 原版 appendHoverText 逐行还原
        tooltip.add(Component.translatable("tooltip.pasterdream.true_moltengold_pickaxe.inlay"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_moltengold_pickaxe.use_hint"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_moltengold_pickaxe.cooldown"));
    }
}
