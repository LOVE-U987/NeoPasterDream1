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
 * 调试结构法杖 —— 右键在目标位置放置指定的遗迹结构
 * <p>
 * 用于开发阶段快速验证结构 NBT 的放置效果。
 * 右键点击一个方块，在点击面的外侧放置结构。
 * 仅服务器端执行，客户端仅触发动画。
 *
 * @param structurePath 结构 NBT 文件的路径（不含命名空间和扩展名）
 */
public class DebugStructureWandItem extends Item {

    private final String structurePath;

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
            player.sendSystemMessage(Component.literal("§c没有瞄准任何方块"));
            return InteractionResultHolder.fail(itemStack);
        }

        BlockHitResult blockHit = (BlockHitResult) hitResult;
        BlockPos targetPos = blockHit.getBlockPos().relative(blockHit.getDirection());

        // 加载并放置结构
        ServerLevel serverLevel = (ServerLevel) level;

        // 通过资源管理器直接加载 NBT 文件（绕开 StructureTemplateManager.get 可能的路径解析问题）
        ResourceLocation nbtLocation = ResourceLocation.parse("pasterdream:structures/" + structurePath + ".nbt");
        Optional<StructureTemplate> templateOpt = loadStructure(serverLevel, nbtLocation);
        if (templateOpt.isEmpty()) {
            player.sendSystemMessage(Component.literal("§c未找到结构: " + ResourceLocation.parse("pasterdream:" + structurePath)));
            return InteractionResultHolder.fail(itemStack);
        }

        StructureTemplate template = templateOpt.get();
        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(Rotation.NONE)
                .setMirror(Mirror.NONE)
                .setIgnoreEntities(false);

        Vec3i size = template.getSize();
        boolean placed = template.placeInWorld(serverLevel, targetPos, targetPos.offset(
                size.getX() - 1, size.getY() - 1, size.getZ() - 1
        ), settings, serverLevel.random, 2);

        ResourceLocation structureId = ResourceLocation.parse("pasterdream:" + structurePath);
        if (placed) {
            player.sendSystemMessage(Component.literal("§a已放置结构: §f" + structureId + " §a于 " + targetPos.toShortString()));
        } else {
            player.sendSystemMessage(Component.literal("§e结构放置返回空: §f" + structureId + " §e(可能没有方块数据)"));
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
        tooltipComponents.add(Component.literal("§7放置结构: §f" + structurePath));
        tooltipComponents.add(Component.literal("§7右键点击方块在§e外侧§7生成"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
