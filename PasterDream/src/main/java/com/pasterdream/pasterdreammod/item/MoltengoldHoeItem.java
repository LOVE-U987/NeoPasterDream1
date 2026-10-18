package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.List;

/**
 * 熔金锄 (moltengold_hoe) — 右键急迫
 * <p>
 * 对应原版 {@code MoltengoldHoeItem}（属性与新模组既有 ItemAPI 注册逐项一致：
 * 耐久 251 / 挖速 14 / 伤害+0 / 攻速 -0.5 / 石级不适用标签 / 附魔 23）。
 * 右键行为与熔金镐共用（见 {@link MoltengoldToolHelper}）；按原版补 fireResistant。
 */
public class MoltengoldHoeItem extends HoeItem {

    /** 右键急迫时长（tick），原版 400 tick = 20 秒 */
    private static final int HASTE_TICKS = 400;

    /** 物品冷却时长（tick），原版 200 tick = 10 秒 */
    private static final int COOLDOWN_TICKS = 200;

    /** 与既有 ItemAPI 注册等值的 tier（修复材料为熔金锭） */
    private static final SimpleTier TIER = new SimpleTier(
            BlockTags.INCORRECT_FOR_STONE_TOOL, 251, 14.0f, 0.0f, 23,
            () -> Ingredient.of(PDItems.MOLTENGOLD_INGOT.get()));

    public MoltengoldHoeItem(Properties properties) {
        // 原版 fireResistant；攻击伤害由 tier 承载（伤害参数 0，与 ItemAPI 方案一致）
        super(TIER, properties.fireResistant().attributes(HoeItem.createAttributes(TIER, 0, -0.5f)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = super.use(level, player, hand);
        // 原版 MoltengoldPickaxePr0Procedure：饱食换急迫（四件套共用）
        MoltengoldToolHelper.applyHaste(player, result.getObject(), HASTE_TICKS, COOLDOWN_TICKS);
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        // 原版 appendHoverText 逐行还原（与熔金镐/斧/锹共用语言键）
        tooltip.add(Component.translatable("tooltip.pasterdream.moltengold_tool.inlay"));
        tooltip.add(Component.translatable("tooltip.pasterdream.moltengold_tool.use_hint"));
        tooltip.add(Component.translatable("tooltip.pasterdream.moltengold_tool.cooldown"));
    }
}
