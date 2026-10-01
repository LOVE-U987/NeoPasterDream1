package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDArenaBossManager;
import com.pasterdream.pasterdreammod.registry.PDAdvancements;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.world.ArenaInfectionUtils;
import com.pasterdream.pasterdreammod.worldgen.PDAaroncosArenaWorldgen;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 亚伦柯斯竞技场传送门方块
 * <p>
 * 触碰时传送至竞技场维度。继承 SlabBlock 实现半砖形状，具有发光效果。
 * <p>
 * 传送条件（原 {@code AaroncosArenaPortalsPr0}）：已完成 {@code achievement_shadow_d_0}
 * 或创造模式；否则提示尚未完成前置进度。
 * <p>
 * 感染效果：方块周围小范围的地面方块会被转化为灯影之下风格的方块，
 * 受统一门控约束（配置开关/仅主世界/竞技场群系内/BOSS 未击败，见
 * {@link ArenaInfectionUtils}）；群系级的大范围感染由竞技场遗迹本身承担
 * （见 {@link com.pasterdream.pasterdreammod.world.ArenaRuinInfection}）。
 * <p>
 * <b>放置确认</b>（{@code hasPostProcess = true} 可触发二层 onPlace）：
 * 结构方块经 {@code WorldGenRegion.setBlock} 进入 ProtoChunk（首层无 onPlace），
 * 但因 {@code hasPostProcess} 被标记进 PostProcessing 列表。区块可 tick 时
 * {@code LevelChunk.postProcessGeneration()} 在主线程重设本方块，触发二层 onPlace——
 * 此时 {@code oldState} 与世界生成时的状态属同一方块，证明已真实放置。
 * 经 {@link PDAaroncosArenaWorldgen#offerPendingPlacement} 入队，由主线程确认器
 * 落库竞技场放置记录（保证只生成一次）并按配置启动群系/感染。
 * 第三方结构查询/预览不会放置方块，因此永远不会误触发。
 */
public class AaroncosArenaPortalsBlock extends SlabBlock {

    /** 感染半径 */
    private static final int NORMAL_RADIUS = 6;
    /** 每 tick 最多处理候选数 */
    private static final int NORMAL_CANDIDATES = 10;
    /** 调度 tick 间隔 */
    private static final int NORMAL_INTERVAL = 12;

    public AaroncosArenaPortalsBlock() {
        super(BlockBehaviour.Properties.of()
                .instrument(NoteBlockInstrument.BASEDRUM)
                .sound(SoundType.GLASS)
                .strength(-1, 3600000)
                .lightLevel(s -> 15)
                .noCollission()
                .noOcclusion()
                .hasPostProcess((bs, br, bp) -> true)
                .emissiveRendering((bs, br, bp) -> true)
                .isRedstoneConductor((bs, br, bp) -> false)
                .dynamicShape());
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of(new ItemStack(this));
    }

    /**
     * 每 tick 更新时触发感染效果。
     * 以受控速率将周围方块转化为灯影之下风格，保持动态可见但不过度刷屏。
     * 感染受统一门控（配置开关/仅主世界/竞技场群系内/BOSS 未击败），
     * 门控不满足时本调用为空操作。
     *
     * @param state  当前方块状态
     * @param level  当前世界
     * @param pos    方块位置
     * @param random 随机数生成器
     */
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        ArenaInfectionUtils.infectSurroundingBlocks(level, pos, NORMAL_RADIUS, NORMAL_CANDIDATES, random);
        level.scheduleTick(pos, this, NORMAL_INTERVAL);
    }

    /**
     * 方块放置时按上下文分流，基于 vanilla 的 PostProcessing 机制放置确认。
     * <p>
     * <b>世界生成路径</b>（Class-LevelHosted放置确认链的核心）：
     * 结构方块经 {@code WorldGenRegion.setBlock} 落入 ProtoChunk（此时 {@code onPlace}
     * 不触发）。因本方块设置了 {@code hasPostProcess = true}，被标记进 chunk 的
     * PostProcessing 列表。区块可 tick 时 {@code LevelChunk.postProcessGeneration()}
     * 在主线程用 {@code ServerLevel.setBlock(flags=20)} 重设所有标记方块 →
     * {@code LevelChunk.setBlockState} 无条件调用 {@code onPlace}（已验证的原版机制）。
     * 此时 {@code oldState} 即世界生成时真实落地的方块本身（新旧状态属同一方块），
     * 转交主线程放置确认器落库 {@code placed}（保证只生成一次）；
     * 第三方结构查询/预览不放置方块，因此永远不会误触发。
     * <p>
     * <b>玩家/机器放置</b>：{@code oldState} 非本方块，不产生确认——仅调度小范围感染
     * tick，受统一门控约束（配置/仅主世界/竞技场群系内/BOSS 未击败，
     * 见 {@link ArenaInfectionUtils}）。竞技场群系外的手动放置不产生感染。
     */
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (level.isClientSide) {
            return;
        }
        if (level instanceof ServerLevel) {
            level.scheduleTick(pos, this, NORMAL_INTERVAL);
            // 世界生成阶段：因 hasPostProcess=true 被标记进 PostProcessing 列表，
            // 区块可 tick 时 postProcessGeneration 在主线程重设本方块触发本钩子——
            // oldState 与 state 属同一方块，证明是真实放置（区别于玩家/机器放置）。
            if (oldState.getBlock() == this) {
                PDAaroncosArenaWorldgen.offerPendingPlacement(pos);
            }
        }
    }

    /**
     * 当实体进入方块碰撞箱时触发 —— 实现竞技场传送。
     * <p>
     * 需 {@code achievement_shadow_d_0} 或创造；通过后传送并赋予缓降。
     */
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide || !(entity instanceof ServerPlayer player)) {
            return;
        }

        if (level.dimension().equals(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY)) {
            return;
        }

        // 刚从竞技场返回（胜利/离场传送）的玩家在冷却期内不响应传送：
        // 胜利传送会把人送到本传送门正上方，下落穿过无碰撞方块时不得立即再次进竞技场
        if (player.getPersistentData().getLong(PDArenaBossManager.ARENA_EXIT_COOLDOWN_KEY)
                > player.level().getGameTime()) {
            return;
        }

        if (!player.getAbilities().instabuild && !hasAdvancement(player, "achievement_shadow_d_0")) {
            player.displayClientMessage(
                    Component.translatable("message.pasterdream.aaroncos_arena_portals.locked"), true);
            return;
        }

        ServerLevel targetWorld = player.getServer().getLevel(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY);
        if (targetWorld == null) {
            return;
        }

        DimensionTransition transition = new DimensionTransition(
                targetWorld,
                new net.minecraft.world.phys.Vec3(0.5, 70.0, 0.5),
                entity.getDeltaMovement(),
                entity.getYRot(),
                entity.getXRot(),
                DimensionTransition.PLAY_PORTAL_SOUND
        );

        player.changeDimension(transition);

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 120, 0));
        }
    }

    private static boolean hasAdvancement(ServerPlayer player, String name) {
        if (!PDAdvancements.isAdvancementLocked(player, ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, name))) {
            return true;
        }
        AdvancementHolder holder = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, name));
        return holder != null && player.getAdvancements().getOrStartProgress(holder).isDone();
    }
}
