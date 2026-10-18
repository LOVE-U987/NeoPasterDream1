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
 * 熔金镐 (moltengold_pickaxe) — 右键急迫
 * <p>
 * 对应原版 {@code MoltengoldPickaxeItem}（属性与新模组既有 ItemAPI 注册逐项一致：
 * 耐久 251 / 挖速 14 / 伤害+3 / 攻速 -2.7 / 石级不适用标签 / 附魔 23）。
 * 右键使用消耗 4 点饱食度换取 20 秒急迫（见 {@link MoltengoldToolHelper}），
 * 物品冷却 10 秒；按原版补 fireResistant（不会熔毁）。
 */
public class MoltengoldPickaxeItem extends PickaxeItem {

    /** 右键急迫时长（tick），原版 400 tick = 20 秒 */
    private static final int HASTE_TICKS = 400;

    /** 物品冷却时长（tick），原版 200 tick = 10 秒 */
    private static final int COOLDOWN_TICKS = 200;

    /** 与既有 ItemAPI 注册等值的 tier（修复材料为熔金锭） */
    private static final SimpleTier TIER = new SimpleTier(
            BlockTags.INCORRECT_FOR_STONE_TOOL, 251, 14.0f, 3.0f, 23,
            () -> Ingredient.of(PDItems.MOLTENGOLD_INGOT.get()));

    public MoltengoldPickaxeItem(Properties properties) {
        // 原版 fireResistant；攻击伤害由 tier 承载（伤害参数 0，与 ItemAPI 方案一致）
        super(TIER, properties.fireResistant().attributes(PickaxeItem.createAttributes(TIER, 0, -2.7f)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = super.use(level, player, hand);
        // 原版 MoltengoldPickaxePr0Procedure：饱食换急迫
        MoltengoldToolHelper.applyHaste(player, result.getObject(), HASTE_TICKS, COOLDOWN_TICKS);
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        // 原版 appendHoverText 逐行还原（与熔金斧/锹/锄共用语言键）
        tooltip.add(Component.translatable("tooltip.pasterdream.moltengold_tool.inlay"));
        tooltip.add(Component.translatable("tooltip.pasterdream.moltengold_tool.use_hint"));
        tooltip.add(Component.translatable("tooltip.pasterdream.moltengold_tool.cooldown"));
    }
}
