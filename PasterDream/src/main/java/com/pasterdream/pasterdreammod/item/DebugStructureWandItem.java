package com.pasterdream.pasterdreammod.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * 调试结构法杖 —— 右键在目标位置放置指定的遗迹结构。
 * <p>
 * 用于开发阶段快速验证结构 NBT 的放置效果。
 * 右键点击一个方块，在点击面的外侧放置结构。
 * 仅服务器端执行，客户端仅触发动画。
 */
public class DebugStructureWandItem extends Item {

    /** 结构 NBT 路径（不含命名空间和扩展名），如 {@code dream_train} */
    private final String structurePath;

    /**
     * @param properties    物品属性
     * @param structurePath 结构 NBT 文件路径（不含命名空间和扩展名）
     */
    public DebugStructureWandItem(Properties properties, String structurePath) {
        super(properties);
        this.structurePath = structurePath;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (level.isClientSide) {
            return InteractionResultHolder.success(itemStack);
        }

        // 射线检测目标方块
        HitResult hitResult = player.pick(100.0D, 1.0F, false);
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            player.sendSystemMessage(Component.translatable("message.pasterdream.debug_wand.no_target"));
            return InteractionResultHolder.fail(itemStack);
        }

        BlockHitResult blockHit = (BlockHitResult) hitResult;
        BlockPos targetPos = blockHit.getBlockPos().relative(blockHit.getDirection());

        // 加载并放置结构
        ServerLevel serverLevel = (ServerLevel) level;

        // 优先走 StructureTemplateManager（与 PDStructureBlock / 自然生成一致，含 DFU）；
        // 失败时再从 datapack 资源直读并 readStructure（同样走 DFU）。
        ResourceLocation structureId = ResourceLocation.parse("pasterdream:" + structurePath);
        Optional<StructureTemplate> templateOpt = serverLevel.getStructureManager().get(structureId);
        if (templateOpt.isEmpty() || templateOpt.get().getSize().getX() <= 0) {
            ResourceLocation nbtLocation = ResourceLocation.parse("pasterdream:structure/" + structurePath + ".nbt");
            templateOpt = loadStructure(serverLevel, nbtLocation);
        }
        if (templateOpt.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.pasterdream.debug_wand.structure_not_found", structureId));
            return InteractionResultHolder.fail(itemStack);
        }

        StructureTemplate template = templateOpt.get();
        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(Rotation.NONE)
                .setMirror(Mirror.NONE)
                .setIgnoreEntities(false);

        Vec3i size = template.getSize();
        // flags=3 与原版/PDStructureBlock 一致（NOTIFY_NEIGHBORS|NOTIFY_CLIENTS），保证 BE 与客户端同步
        boolean placed = template.placeInWorld(serverLevel, targetPos, targetPos.offset(
                size.getX() - 1, size.getY() - 1, size.getZ() - 1
        ), settings, serverLevel.random, 3);

        if (placed) {
            player.sendSystemMessage(Component.translatable("message.pasterdream.debug_wand.structure_placed", structureId, targetPos.toShortString()));
        } else {
            player.sendSystemMessage(Component.translatable("message.pasterdream.debug_wand.structure_empty", structureId));
        }
        return InteractionResultHolder.success(itemStack);
    }

    /**
     * 通过资源管理器直接加载结构 NBT 文件
     */
    private Optional<StructureTemplate> loadStructure(ServerLevel level, ResourceLocation nbtLocation) {
        try {
            var resourceOpt = level.getServer().getResourceManager().getResource(nbtLocation);
            if (resourceOpt.isEmpty()) {
                return Optional.empty();
            }
            try (InputStream is = resourceOpt.get().open()) {
                CompoundTag tag = NbtIo.readCompressed(is, new NbtAccounter(0x20000000L, 512));
                return Optional.of(level.getStructureManager().readStructure(tag));
            }
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.debug_structure_wand.place", structurePath));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.debug_structure_wand.right_click"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
