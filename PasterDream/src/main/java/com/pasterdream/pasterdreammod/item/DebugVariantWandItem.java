package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * 调试变体水晶 —— 一个水晶承载多个变体，潜行 + 滚轮切换。
 * <p>
 * 支持两种目标模式：
 * <ul>
 *   <li><b>结构模式</b>（{@code decor=false}）：目标为结构 NBT 路径，放置逻辑对齐
 *       {@link DebugStructureWandItem}，可选以点击点为中心对齐</li>
 *   <li><b>装饰模式</b>（{@code decor=true}）：目标为已注册的 {@code ConfiguredFeature} 名，
 *       直接生成程序化地物，逻辑对齐 {@link DebugDecorWandItem}</li>
 * </ul>
 * 当前选中变体存放在物品的 {@code CUSTOM_DATA} 中；客户端滚轮切换后经
 * {@code DebugWandVariantPayload} 同步到服务端，保证放置时两端一致。
 *
 * @author PasterDream Team
 */
public class DebugVariantWandItem extends Item {

    /** 变体序号在物品 CUSTOM_DATA 中的键 */
    private static final String VARIANT_KEY = "pasterdream:debug_variant";

    /** 变体目标列表（结构 NBT 路径 或 ConfiguredFeature 名，顺序即变体顺序） */
    private final List<String> targets;

    /** 是否为装饰模式（true=按 ConfiguredFeature 名生成） */
    private final boolean decor;

    /** 结构模式下是否以点击点为中心放置 */
    private final boolean centered;

    /**
     * 构造调试变体水晶
     *
     * @param properties 物品属性
     * @param targets    变体目标列表（结构 NBT 路径 或 ConfiguredFeature 名）
     * @param decor      是否为装饰模式
     * @param centered   结构模式下是否以点击点为中心放置
     */
    public DebugVariantWandItem(Properties properties, List<String> targets, boolean decor, boolean centered) {
        super(properties);
        if (targets == null || targets.isEmpty()) {
            throw new IllegalArgumentException("[DebugVariantWandItem] 变体列表不能为空");
        }
        this.targets = List.copyOf(targets);
        this.decor = decor;
        this.centered = centered;
    }

    /**
     * 获取变体总数
     *
     * @return 变体数量
     */
    public int targetCount() {
        return targets.size();
    }

    /**
     * 按序号取变体目标（自动取模，越界不抛异常）
     *
     * @param index 变体序号
     * @return 目标名
     */
    public String targetAt(int index) {
        return targets.get(Math.floorMod(index, targets.size()));
    }

    /**
     * 是否为装饰模式
     *
     * @return 装饰模式返回 true
     */
    public boolean isDecor() {
        return decor;
    }

    /**
     * 读取物品当前选中的变体序号
     *
     * @param stack 物品栈
     * @return 变体序号（无数据时为 0）
     */
    public static int getVariant(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return 0;
        }
        CompoundTag tag = data.copyTag();
        return tag.getInt(VARIANT_KEY);
    }

    /**
     * 写入物品当前选中的变体序号
     *
     * @param stack 物品栈
     * @param index 变体序号
     */
    public static void setVariant(ItemStack stack, int index) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt(VARIANT_KEY, index));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(itemStack);
        }

        ServerLevel serverLevel = (ServerLevel) level;
        // 装饰模式需点击到地面（0.0F 命中原方块本身），结构模式沿用可放置面外扩
        HitResult hitResult = player.pick(decor ? 200.0D : 100.0D, decor ? 0.0F : 1.0F, false);
        if (!(hitResult instanceof BlockHitResult blockHit)) {
            player.sendSystemMessage(Component.translatable(decor
                    ? "message.pasterdream.debug_decor_wand.no_target"
                    : "message.pasterdream.debug_wand.no_target"));
            return InteractionResultHolder.fail(itemStack);
        }

        BlockPos targetPos = blockHit.getBlockPos().relative(blockHit.getDirection());
        String target = targetAt(getVariant(itemStack));

        boolean success = decor
                ? placeFeature(serverLevel, player, target, targetPos)
                : placeStructure(serverLevel, player, target, targetPos);

        if (success) {
            player.getCooldowns().addCooldown(this, 5);
            return InteractionResultHolder.success(itemStack);
        }
        return InteractionResultHolder.fail(itemStack);
    }

    /**
     * 按 ConfiguredFeature 名生成程序化地物（对齐 {@link DebugDecorWandItem}）
     *
     * @param serverLevel 服务端维度
     * @param player      玩家（用于回执消息）
     * @param featureName ConfiguredFeature 名（不含命名空间）
     * @param targetPos   生成原点
     * @return 生成成功返回 true
     */
    private boolean placeFeature(ServerLevel serverLevel, Player player, String featureName, BlockPos targetPos) {
        ResourceKey<ConfiguredFeature<?, ?>> featureKey = ResourceKey.create(
                Registries.CONFIGURED_FEATURE,
                ResourceLocation.parse(PasterDreamMod.MOD_ID + ":" + featureName));

        var configuredFeature = serverLevel.registryAccess()
                .registryOrThrow(Registries.CONFIGURED_FEATURE)
                .getHolder(featureKey)
                .orElse(null);

        if (configuredFeature == null) {
            player.sendSystemMessage(Component.translatable(
                    "message.pasterdream.debug_decor_wand.feature_not_found", featureName));
            return false;
        }

        boolean success = configuredFeature.value().place(
                serverLevel,
                serverLevel.getChunkSource().getGenerator(),
                serverLevel.getRandom(),
                targetPos);

        player.sendSystemMessage(Component.translatable(success
                        ? "message.pasterdream.debug_decor_wand.placed"
                        : "message.pasterdream.debug_decor_wand.place_failed",
                featureName, targetPos.toShortString()));
        return success;
    }

    /**
     * 按结构 NBT 路径放置（对齐 {@link DebugStructureWandItem}）
     *
     * @param serverLevel 服务端维度
     * @param player      玩家（用于回执消息）
     * @param structurePath 结构路径（不含命名空间与扩展名）
     * @param targetPos   放置起点
     * @return 放置成功返回 true
     */
    private boolean placeStructure(ServerLevel serverLevel, Player player, String structurePath, BlockPos targetPos) {
        ResourceLocation structureId = ResourceLocation.parse(PasterDreamMod.MOD_ID + ":" + structurePath);
        Optional<StructureTemplate> templateOpt = serverLevel.getStructureManager().get(structureId);
        if (templateOpt.isEmpty() || templateOpt.get().getSize().getX() <= 0) {
            ResourceLocation nbtLocation = ResourceLocation.parse(
                    PasterDreamMod.MOD_ID + ":structure/" + structurePath + ".nbt");
            templateOpt = loadStructure(serverLevel, nbtLocation);
        }
        if (templateOpt.isEmpty()) {
            // ResourceLocation 不能直接作为 translatable 参数（网络编码会抛异常），必须先转字符串
            player.sendSystemMessage(Component.translatable(
                    "message.pasterdream.debug_wand.structure_not_found", structureId.toString()));
            return false;
        }

        StructureTemplate template = templateOpt.get();
        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(Rotation.NONE)
                .setMirror(Mirror.NONE)
                .setIgnoreEntities(false);

        Vec3i size = template.getSize();
        BlockPos startPos = targetPos;
        if (centered) {
            startPos = targetPos.offset(-(size.getX() - 1) / 2, 0, -(size.getZ() - 1) / 2);
        }

        // flags=3：NOTIFY_NEIGHBORS|NOTIFY_CLIENTS，保证方块实体与客户端同步（与 PDStructureBlock 一致）
        boolean placed = template.placeInWorld(serverLevel, startPos,
                startPos.offset(size.getX() - 1, size.getY() - 1, size.getZ() - 1),
                settings, serverLevel.random, 3);

        player.sendSystemMessage(Component.translatable(placed
                        ? "message.pasterdream.debug_wand.structure_placed"
                        : "message.pasterdream.debug_wand.structure_empty",
                structureId.toString(), targetPos.toShortString()));
        return placed;
    }

    /**
     * 从数据包资源直读结构 NBT 并解析（与旁观者/数据包结构一致的 DFU 路径）
     *
     * @param level       服务端维度
     * @param nbtLocation NBT 资源路径
     * @return 解析出的结构模板；缺失或损坏时返回空
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
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
                                TooltipFlag tooltipFlag) {
        int index = Math.floorMod(getVariant(stack), targets.size());
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.debug_variant_wand.target",
                targets.get(index)));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.debug_variant_wand.variant",
                index + 1, targets.size()));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.debug_variant_wand.scroll"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
