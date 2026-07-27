package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.List;

/**
 * 失色塞西莉娅的加护（turn_pale_cecilia）。
 * <p>
 * 还原自原版 TurnPaleCeciliaItem + TurnPaleCeciliaPr0Procedure：
 * 手持右键并瞄准 5 格内的融梦涌泉源方块使用，
 * 将吸干该处液体源、消耗本物品（创造模式不消耗），
 * 并给予玩家塞西莉娅的加护（ceciliacare_charm），伴随 dream0 音效。
 */
public class TurnPaleCeciliaItem extends Item {

    /** 浸泡音效 pasterdream:dream0 */
    private static final SoundEvent DREAM0_SOUND =
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("pasterdream", "dream0"));

    public TurnPaleCeciliaItem(Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.COMMON));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.literal("§7或许我们应该亲手将它浸泡于融梦涌泉"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hit = clipMeltdreamSource(level, player);
        if (hit.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(stack);
        }
        if (tryRestore(level, player, stack, hit.getBlockPos(), hand)) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        return InteractionResultHolder.pass(stack);
    }

    /**
     * 直接对着融梦涌泉方块右键时也走同一恢复逻辑（避免仅 use 射线在部分视角下未命中）。
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        ItemStack stack = context.getItemInHand();
        if (tryRestore(level, player, stack, context.getClickedPos(), context.getHand())) {
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    /**
     * 从眼睛向视线 5 格探测液体源（与原版 ClipContext 一致）。
     */
    private static BlockHitResult clipMeltdreamSource(Level level, Player player) {
        Vec3 eye = player.getEyePosition(1.0f);
        Vec3 reach = eye.add(player.getViewVector(1.0f).scale(5));
        return level.clip(new ClipContext(eye, reach,
                ClipContext.Block.OUTLINE, ClipContext.Fluid.SOURCE_ONLY, player));
    }

    /**
     * @return true 若成功浸泡恢复
     */
    private static boolean tryRestore(Level level, Player player, ItemStack stack,
                                      BlockPos hitPos, InteractionHand hand) {
        BlockState state = level.getBlockState(hitPos);
        // 与原版一致：按融梦涌泉方块判定，而非仅 Fluid 引用（避免 Source/Flowing 判定偏差）
        if (!state.is(PDBlocks.MELTDREAM_LIQUID.get())) {
            return false;
        }
        // 只要是源级液体才吸干（流动层不消耗整格）
        if (!level.getFluidState(hitPos).isSource()) {
            return false;
        }
        if (level.isClientSide()) {
            return true;
        }

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
            if (stack.isEmpty()) {
                player.setItemInHand(hand, ItemStack.EMPTY);
            }
        }
        level.setBlock(hitPos, Blocks.AIR.defaultBlockState(), 3);
        player.swing(hand, true);

        ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(PDItems.CECILIACARE_CHARM.get()));
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, BlockPos.containing(player.getX(), player.getY(), player.getZ()),
                    DREAM0_SOUND, SoundSource.NEUTRAL, 1.0f, 1.0f);
        }
        return true;
    }
}
