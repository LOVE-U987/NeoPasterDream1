package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import java.util.List;

/**
 * 失色塞西莉娅的加护（turn_pale_cecilia）。
 * <p>
 * 预期玩法：作为掉落物进入融梦涌泉<strong>源</strong>时，转化为塞西莉娅的加护
 * （ceciliacare_charm），并播放 dream0。不通过手持右键消耗/发放。
 */
public class TurnPaleCeciliaItem extends Item {

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

    /**
     * 掉落物每 tick：若处于融梦涌泉源格内，则转化为塞西莉娅的加护。
     *
     * @return true 表示已处理本 tick 的物品实体逻辑
     */
    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.level().isClientSide() || entity.isRemoved()) {
            return false;
        }
        if (tryRestoreItemEntity(entity)) {
            return true;
        }
        return false;
    }

    /**
     * 供融梦涌泉方块 entityInside 调用的同一套转化逻辑。
     *
     * @return true 若成功转化
     */
    public static boolean tryRestoreItemEntity(ItemEntity entity) {
        if (entity.level().isClientSide() || entity.isRemoved()) {
            return false;
        }
        ItemStack stack = entity.getItem();
        if (stack.isEmpty() || !stack.is(PDItems.TURN_PALE_CECILIA.get())) {
            return false;
        }

        Level level = entity.level();
        BlockPos pos = entity.blockPosition();
        // 自身格或脚下半格（掉落物常沉在流体表面附近）
        if (!isMeltdreamSource(level, pos) && !isMeltdreamSource(level, pos.below())) {
            return false;
        }
        BlockPos sourcePos = isMeltdreamSource(level, pos) ? pos : pos.below();

        int count = stack.getCount();
        entity.setItem(new ItemStack(PDItems.CECILIACARE_CHARM.get(), count));
        entity.setDefaultPickUpDelay();

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, sourcePos, DREAM0_SOUND, SoundSource.NEUTRAL, 1.0f, 1.0f);
        }
        return true;
    }

    private static boolean isMeltdreamSource(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(PDBlocks.MELTDREAM_LIQUID.get())) {
            return false;
        }
        FluidState fluid = level.getFluidState(pos);
        return fluid.isSource();
    }
}
