package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.block.PDStructureBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

/**
 * 结构方块调试水晶 —— 右键在目标位置复现对应 {@link PDStructureBlock} 的生成逻辑。
 * <p>
 * 与 {@link DebugStructureWandItem} 不同，本物品会按照结构方块规格进行随机抽号、
 * 应用各自的偏移与旋转，从而保证转化后的功能与原结构方块一致。
 *
 * @see PDStructureBlock
 * @see PDStructureBlock.Spec
 */
public class DebugStructureBlockWandItem extends Item {

    /** 结构方块编号（0..23），对应 {@link PDStructureBlock#SPECS} */
    private final int index;

    /**
     * @param properties 物品属性
     * @param index      结构方块编号
     */
    public DebugStructureBlockWandItem(Properties properties, int index) {
        super(properties);
        this.index = index;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (level.isClientSide) {
            return InteractionResultHolder.success(itemStack);
        }

        PDStructureBlock.Spec spec = PDStructureBlock.SPECS.get(index);
        if (spec == null) {
            player.sendSystemMessage(Component.translatable("message.pasterdream.debug_wand.spec_not_found", index));
            return InteractionResultHolder.fail(itemStack);
        }

        HitResult hitResult = player.pick(100.0D, 1.0F, false);
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            player.sendSystemMessage(Component.translatable("message.pasterdream.debug_wand.no_target"));
            return InteractionResultHolder.fail(itemStack);
        }

        BlockHitResult blockHit = (BlockHitResult) hitResult;
        BlockPos targetPos = blockHit.getBlockPos().relative(blockHit.getDirection());
        ServerLevel serverLevel = (ServerLevel) level;

        int number = 0;
        if (spec.randomRange() > 0) {
            number = Mth.nextInt(RandomSource.create(), 1, spec.randomRange());
        }

        int placedCount = 0;
        for (PDStructureBlock.Placement placement : spec.placements()) {
            boolean matches = placement.min() == 0
                    || (number >= placement.min() && number <= placement.max());
            if (!matches) {
                continue;
            }

            ResourceLocation structureId = ResourceLocation.fromNamespaceAndPath("pasterdream", placement.template());
            StructureTemplate template = serverLevel.getStructureManager().getOrCreate(structureId);
            if (template == null) {
                player.sendSystemMessage(Component.translatable("message.pasterdream.debug_wand.structure_not_found", structureId));
                continue;
            }

            BlockPos placePos = targetPos.offset(placement.dx(), placement.dy(), placement.dz());
            StructurePlaceSettings settings = new StructurePlaceSettings()
                    .setRotation(placement.rotation())
                    .setMirror(Mirror.NONE)
                    .setIgnoreEntities(false);

            template.placeInWorld(serverLevel, placePos, placePos, settings, serverLevel.random, 3);
            placedCount++;
        }

        if (placedCount > 0) {
            player.sendSystemMessage(Component.translatable("message.pasterdream.debug_wand.structure_block_placed", index, targetPos.toShortString()));
        } else {
            player.sendSystemMessage(Component.translatable("message.pasterdream.debug_wand.structure_block_no_template", index));
        }
        return InteractionResultHolder.success(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        PDStructureBlock.Spec spec = PDStructureBlock.SPECS.get(index);
        if (spec != null) {
            tooltipComponents.add(Component.translatable("tooltip.pasterdream.debug_structure_block_wand.reproduce", index));
            for (String line : spec.tooltip()) {
                tooltipComponents.add(Component.translatable(line));
            }
        }
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.debug_structure_wand.right_click"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
