package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDFluids;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.List;

/**
 * 失色塞西莉娅的加护（turn_pale_cecilia）。
 * <p>
 * 还原自原版 TurnPaleCeciliaItem + TurnPaleCeciliaPr0Procedure：
 * 手持右键并瞄准 5 格内的融梦涌泉（融梦液体源方块）使用，
 * 将吸干该处液体源、消耗本物品（创造模式不消耗），
 * 并给予玩家塞西莉娅的关怀（ceciliacare_charm），伴随 dream0 音效。
 */
public class TurnPaleCeciliaItem extends Item {

    /** 浸泡音效 pasterdream:dream0（音效键由并行任务统一并入 sounds.json，ogg 已随本任务复制） */
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
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.success(stack);
        }

        // 从眼睛位置向视线方向探测 5 格，仅命中液体源方块（与原版 ClipContext 一致）
        Vec3 eye = player.getEyePosition(1.0f);
        Vec3 reach = eye.add(player.getViewVector(1.0f).scale(5));
        BlockHitResult hit = level.clip(new ClipContext(eye, reach,
                ClipContext.Block.OUTLINE, ClipContext.Fluid.SOURCE_ONLY, player));
        if (hit.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.success(stack);
        }

        BlockPos hitPos = hit.getBlockPos();
        FluidState fluidState = serverLevel.getFluidState(hitPos);
        // 命中处必须为融梦液体源
        if (!fluidState.isSource() || !fluidState.is(PDFluids.MELTDREAM_LIQUID.get())) {
            return InteractionResultHolder.success(stack);
        }

        // 消耗本物品（创造模式不消耗），吸干液体源
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        serverLevel.setBlock(hitPos, Blocks.AIR.defaultBlockState(), 3);
        player.swing(InteractionHand.MAIN_HAND, true);

        // 给予塞西莉娅的关怀
        ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(PDItems.CECILIACARE_CHARM.get()));
        serverLevel.playSound(null, BlockPos.containing(player.getX(), player.getY(), player.getZ()),
                DREAM0_SOUND, SoundSource.NEUTRAL, 1.0f, 1.0f);
        return InteractionResultHolder.success(stack);
    }
}
