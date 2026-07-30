package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.WorkshopGrindBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.util.PasterItemData;
import com.pasterdream.pasterdreammod.util.WeaponWorkshopVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/**
 * 工坊磨石方块 (Workshop Grind)
 * <p>
 * 武器工坊群卫星工位之一（最终工序）：外观由精铸工坊核心统一渲染。
 * 打磨工序（原版 WorkshopGrindPr0Procedure）：手持工序 3（待打磨）的原胚右键——
 * <ul>
 *   <li>打磨层数 &lt;9：每次消耗 1 级经验，层数 +1，提示"打磨进度 X0%"
 *       （磨石音效 + 粉尘/灰烬/烟雾粒子）；经验不足则提示；</li>
 *   <li>层数 ≥9：磨石 BE 随机强化（{@code applyGrindInlay}）→ 提示"打磨进度 100%"、
 *       升级音效、粉尘×20 + 融梦水晶×5 粒子 → 按原胚种类换成成品
 *       （原版 WorkshopGrindRecipe0）→ 把暂存的 paster_* 强化属性拷贝到成品
 *       （原版 WorkshopNbtcopy0）并清空全局暂存。</li>
 * </ul>
 * 手持不符时提示用途与经验开销。
 */
public class WorkshopGrindBlock extends BaseEntityBlock {

    public static final MapCodec<WorkshopGrindBlock> CODEC = simpleCodec(WorkshopGrindBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    /** 原胚物品标签（pasterdream:embryo_items） */
    private static final TagKey<Item> EMBRYO_ITEMS =
            ItemTags.create(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "embryo_items"));

    /** 出品所需打磨层数 */
    private static final int MAX_GRIND_LEVEL = 9;
    /** 打磨总经验等级开销（门槛按 10-level 判定） */
    private static final int TOTAL_XP_LEVELS = 10;

    /**
     * 原胚 → 成品映射（与原版 WorkshopGrindRecipe0Procedure 一一对应）。
     * 通过注册表按名查找实现「落地即自动生效」的松耦合。
     */
    private record GrindResult(Supplier<Item> embryo, Supplier<Item> result) {
    }

    /** 全部 11 项原版打磨出品映射 */
    private static final List<GrindResult> RESULTS = List.of(
            new GrindResult(() -> PDItems.DREAM_WAND_EMBRYO.get().asItem(), () -> lookupItem("dream_wand")),
            new GrindResult(() -> PDItems.SHADOW_EROSION_SWORD_EMBRYO.get().asItem(), () -> PDItems.SHADOW_EROSION_SWORD.get().asItem()),
            new GrindResult(() -> PDItems.SHADOW_EROSION_PICKAXE_EMBRYO.get().asItem(), () -> PDItems.SHADOW_EROSION_PICKAXE.get().asItem()),
            new GrindResult(() -> PDItems.SHADOW_EROSION_AXE_EMBRYO.get().asItem(), () -> PDItems.SHADOW_EROSION_AXE.get().asItem()),
            new GrindResult(() -> PDItems.SHADOW_EROSION_SHOVEL_EMBRYO.get().asItem(), () -> PDItems.SHADOW_EROSION_SHOVEL.get().asItem()),
            new GrindResult(() -> PDItems.SHADOW_EROSION_HOE_EMBRYO.get().asItem(), () -> PDItems.SHADOW_EROSION_HOE.get().asItem()),
            new GrindResult(() -> PDItems.TERRASWORD_EMBRYO.get().asItem(), () -> PDItems.TERRA_SWORD.get().asItem()),
            new GrindResult(() -> PDItems.WHITE_SWORD_EMBRYO.get().asItem(), () -> PDItems.WHITE_SWORD.get().asItem()),
            new GrindResult(() -> PDItems.SHADOW_SWORD_EMBRYO.get().asItem(), () -> PDItems.SHADOW_SWORD.get().asItem()),
            new GrindResult(() -> PDItems.STAR_WISH_ROD_EMBRYO.get().asItem(), () -> lookupItem("star_wish_rod")),
            new GrindResult(() -> PDItems.ICESHADOW_HAMMER_EMBRYO.get().asItem(), () -> PDItems.ICESHADOW_HAMMER.get().asItem()));

    /**
     * 构造工坊磨石方块
     *
     * @param properties 方块属性
     */
    public WorkshopGrindBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    /**
     * 按注册名查找本模组物品（跨模块松耦合；未注册时返回 AIR）
     *
     * @param path 注册路径
     * @return 物品（未注册返回 {@link Items#AIR}）
     */
    private static Item lookupItem(String path) {
        return BuiltInRegistries.ITEM
                .getOptional(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, path))
                .orElse(Items.AIR);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        // 占位 JSON 模型（air 纹理），实际外观由精铸工坊核心渲染
        return RenderShape.MODEL;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        // 与原版一致：完全阻挡光线
        return 15;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        // 原版逐朝向碰撞箱
        return switch (state.getValue(FACING)) {
            case NORTH -> box(-2, 3, 3, 16, 12, 16);
            case EAST -> box(0, 3, -2, 13, 12, 16);
            case WEST -> box(3, 3, 0, 16, 12, 18);
            default -> box(0, 3, 0, 18, 12, 13);
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WorkshopGrindBlockEntity(pos, state);
    }

    // ==================== 打磨交互（原版 WorkshopGrindPr0Procedure） ====================

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        ItemStack hand = player.getMainHandItem();
        if (!hand.is(EMBRYO_ITEMS) || PasterItemData.getDouble(hand, "process") != 3) {
            player.displayClientMessage(Component.literal("用于打磨原胚 过程总计需要消耗10经验等级"), true);
            return InteractionResult.SUCCESS;
        }
        if (PasterItemData.getDouble(hand, "level") >= MAX_GRIND_LEVEL) {
            finishGrind(level, pos, player, hand);
        } else {
            advanceGrind(level, pos, player, hand);
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * 打磨推进（层数 <9）：经验足够则扣 1 级、层数 +1、进度提示与音效粒子
     *
     * @param level  世界
     * @param pos    磨石位置
     * @param player 玩家
     * @param hand   主手原胚
     */
    private void advanceGrind(Level level, BlockPos pos, Player player, ItemStack hand) {
        if (player.experienceLevel < TOTAL_XP_LEVELS - PasterItemData.getDouble(hand, "level")) {
            player.displayClientMessage(Component.literal("经验值不足"), true);
            return;
        }
        player.giveExperienceLevels(-1);
        double newLevel = PasterItemData.getDouble(hand, "level") + 1;
        PasterItemData.putDouble(hand, "level", newLevel);
        player.displayClientMessage(Component.literal(
                "打磨进度 " + new java.text.DecimalFormat("#").format(newLevel) + "0%"), true);
        level.playSound(null, pos, SoundEvents.GRINDSTONE_USE, SoundSource.NEUTRAL, 1.0f, 1.0f);
        if (level instanceof ServerLevel serverLevel) {
            double x = pos.getX();
            double y = pos.getY();
            double z = pos.getZ();
            serverLevel.sendParticles((SimpleParticleType) PDParticles.DUST_0_PARTICLE.particleType(),
                    x + 0.5, y + 1, z + 0.5, 16, 0.3, 0.5, 0.3, 0.05);
            serverLevel.sendParticles(ParticleTypes.ASH, x + 0.5, y + 1, z + 0.5, 6, 0.3, 0.5, 0.3, 0.05);
            serverLevel.sendParticles(ParticleTypes.SMOKE, x + 0.5, y + 1, z + 0.5, 3, 0.3, 0.5, 0.3, 0.05);
        }
    }

    /**
     * 打磨完工（层数 ≥9）：随机强化 → 成品替换主手 → 属性拷贝（原版
     * WorkshopGrindInlay0 + Recipe0 + Nbtcopy0 链）
     *
     * @param level  世界
     * @param pos    磨石位置
     * @param player 玩家
     * @param hand   主手原胚
     */
    private void finishGrind(Level level, BlockPos pos, Player player, ItemStack hand) {
        // 防御性：成品解析为 AIR 时不消耗原胚，避免异常路径刷属性
        Item resultItem = resolveResult(hand);
        if (resultItem == Items.AIR) {
            PasterDreamMod.LOGGER.debug("[WorkshopGrind] 原胚 {} 的成品未解析到注册物，跳过出品",
                    BuiltInRegistries.ITEM.getKey(hand.getItem()));
            player.displayClientMessage(Component.literal("无法识别该原胚的成品"), true);
            return;
        }
        ItemStack stash = hand.copy();
        WeaponWorkshopVariables.weaponWorkshopItem = stash;
        if (level.getBlockEntity(pos) instanceof WorkshopGrindBlockEntity grind) {
            grind.applyGrindInlay(stash);
        }
        player.displayClientMessage(Component.literal("打磨进度 100%"), true);
        level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 1.0f, 1.0f);
        if (level instanceof ServerLevel serverLevel) {
            double x = pos.getX();
            double y = pos.getY();
            double z = pos.getZ();
            serverLevel.sendParticles((SimpleParticleType) PDParticles.DUST_0_PARTICLE.particleType(),
                    x + 0.5, y + 1, z + 0.5, 20, 0.3, 0.5, 0.3, 0.05);
            serverLevel.sendParticles((SimpleParticleType) PDParticles.MELTDREAM_CRYSTAL_PARTICLE.particleType(),
                    x + 0.5, y + 1, z + 0.5, 5, 0.3, 0.5, 0.3, 0.05);
        }
        // Recipe0：主手替换为成品
        ItemStack result = new ItemStack(resultItem);
        player.setItemInHand(InteractionHand.MAIN_HAND, result);
        player.getInventory().setChanged();
        // Nbtcopy0：把暂存原胚上的工坊强化属性拷贝到成品，并清空全局暂存
        copyPasterAttributes(stash, result);
        WeaponWorkshopVariables.weaponWorkshopItem = ItemStack.EMPTY;
    }

    /**
     * 解析原胚对应的成品（未命中映射或成品未注册返回 AIR）
     *
     * @param embryo 原胚
     * @return 成品物品
     */
    private static Item resolveResult(ItemStack embryo) {
        for (GrindResult entry : RESULTS) {
            if (embryo.is(entry.embryo().get())) {
                return entry.result().get();
            }
        }
        return Items.AIR;
    }

    /**
     * 工坊强化属性拷贝（原版 WorkshopNbtcopy0Procedure）：
     * 逐项检查 paster_* 标记，把数值同步到成品
     *
     * @param source 暂存原胚
     * @param target 成品
     */
    private static void copyPasterAttributes(ItemStack source, ItemStack target) {
        if (PasterItemData.getBoolean(source, "paster_attack_damage")) {
            PasterItemData.putBoolean(target, "paster_attack_damage", true);
            PasterItemData.putDouble(target, "paster_attack_damage_number",
                    PasterItemData.getDouble(source, "paster_attack_damage_number"));
        }
        if (PasterItemData.getBoolean(source, "paster_attack_speed")) {
            PasterItemData.putBoolean(target, "paster_attack_speed", true);
            PasterItemData.putDouble(target, "paster_attack_speed_number",
                    PasterItemData.getDouble(source, "paster_attack_speed_number"));
        }
        if (PasterItemData.getBoolean(source, "paster_movement_speed")) {
            PasterItemData.putBoolean(target, "paster_movement_speed", true);
            PasterItemData.putDouble(target, "paster_movement_speed_number",
                    PasterItemData.getDouble(source, "paster_movement_speed_number"));
        }
        if (PasterItemData.getBoolean(source, "paster_luck")) {
            PasterItemData.putBoolean(target, "paster_luck", true);
            PasterItemData.putDouble(target, "paster_luck_number",
                    PasterItemData.getDouble(source, "paster_luck_number"));
        }
    }

    @Override
    public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
        super.triggerEvent(state, world, pos, eventID, eventParam);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(eventID, eventParam);
    }
}
