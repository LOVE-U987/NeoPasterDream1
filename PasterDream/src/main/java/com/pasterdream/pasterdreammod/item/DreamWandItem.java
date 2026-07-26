package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.util.PasterItemData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.List;

/**
 * 聚梦法杖 (dream_wand)
 * <p>
 * 还原自原版 DreamWandItem（TieredItem 万用工具型法杖）：
 * <ul>
 *   <li>等级 1 工具（可挖需石制工具的方块），挖掘速度 4，攻击 +2 / 攻速 -2，附魔能力 2，
 *       耐久 0（不可损耗），防火</li>
 *   <li>兼具 斧/锄/锹/镐/剑 五类工具行为（canPerformAction）</li>
 *   <li>站在染梦书桌上右键：清空法杖数据（switch=false）并播放 dream1 音效
 *       （DreamWandPr0Procedure）</li>
 * </ul>
 */
public class DreamWandItem extends TieredItem {

    /** 原版 tier：uses 0 / speed 4 / bonus 2 / level 1（等价石级不适挖标签）/ 附魔 2 */
    private static final SimpleTier TIER = new SimpleTier(
            BlockTags.INCORRECT_FOR_STONE_TOOL, 0, 4f, 2f, 2, () -> Ingredient.EMPTY);

    public DreamWandItem(Properties properties) {
        super(TIER, properties.fireResistant()
                .attributes(ItemAttributeModifiers.builder()
                        .add(Attributes.ATTACK_DAMAGE,
                                new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 2f,
                                        AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND)
                        .add(Attributes.ATTACK_SPEED,
                                new AttributeModifier(BASE_ATTACK_SPEED_ID, -2,
                                        AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND)
                        .build()));
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        // 原版逐字还原：等级 1，不可采钻石/铁级方块，可采四类可挖掘方块
        int tier = 1;
        if (tier < 3 && state.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
            return false;
        } else if (tier < 2 && state.is(BlockTags.NEEDS_IRON_TOOL)) {
            return false;
        } else {
            return tier < 1 && state.is(BlockTags.NEEDS_STONE_TOOL)
                    ? false
                    : (state.is(BlockTags.MINEABLE_WITH_AXE) || state.is(BlockTags.MINEABLE_WITH_HOE)
                    || state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL));
        }
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
        return ItemAbilities.DEFAULT_AXE_ACTIONS.contains(ability)
                || ItemAbilities.DEFAULT_HOE_ACTIONS.contains(ability)
                || ItemAbilities.DEFAULT_SHOVEL_ACTIONS.contains(ability)
                || ItemAbilities.DEFAULT_PICKAXE_ACTIONS.contains(ability)
                || ItemAbilities.DEFAULT_SWORD_ACTIONS.contains(ability);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return 4f;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        stack.hurtAndBreak(1, entity, EquipmentSlot.MAINHAND);
        return true;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(2, attacker, EquipmentSlot.MAINHAND);
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = super.use(level, player, hand);
        clearWandDataOnDesk(level, player, result.getObject());
        return result;
    }

    /**
     * 原版 DreamWandPr0Procedure：玩家所在方块为染梦书桌时清空法杖数据
     *
     * @param level  世界
     * @param player 玩家
     * @param stack  法杖物品栈
     */
    static void clearWandDataOnDesk(Level level, Player player, ItemStack stack) {
        BlockPos pos = BlockPos.containing(player.getX(), player.getY(), player.getZ());
        if (level.getBlockState(pos).getBlock() != PDBlocks.DYEDREAM_DESK.get()) {
            return;
        }
        PasterItemData.putBoolean(stack, "switch", false);
        if (!level.isClientSide()) {
            level.playSound(null, pos, PDSounds.DREAM1.get(), SoundSource.NEUTRAL, 1, 1);
            player.displayClientMessage(Component.literal("法杖数据已清空"), true);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.literal("§7▪§d 帕斯特之梦风格"));
        tooltip.add(Component.literal("§7按住[§fctrl§7]右键空气以清空法杖数据"));
    }
}
