package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.FarmlandWaterManager;

/**
 * 染梦耕地方块
 * <p>
 * 继承原版 {@link FarmBlock}，具备原版耕地的完整行为，并针对染梦体系增强：
 * <ul>
 *   <li>{@code moisture} 属性：0~7，被水/雨水湿润（水边瞬间湿润到满，干燥逐级下降，与原版一致）</li>
 *   <li>方块状态 {@code moisture} 0~6 显示干燥纹理，7 显示湿润纹理（由 blockstate JSON 控制）</li>
 *   <li>实体踩踏时变为染梦泥土（原版变为原版泥土，这里替换为目标方块）</li>
 *   <li>上方被不透明方块遮挡/支撑不足时退化为染梦泥土（而非原版泥土）</li>
 *   <li>干燥且上方无作物维持时退化为染梦泥土（而非原版泥土）</li>
 *   <li>放置时上方不合法直接给染梦泥土（而非原版泥土）</li>
 *   <li>上方作物随机刻加速：额外触发一次作物随机刻，使生长速度约为原版的 2 倍</li>
 * </ul>
 * <p>
 * <b>关键</b>：原版 {@link FarmBlock} 内部所有「退化为泥土」路径都硬编码 {@code Blocks.DIRT}，
 * 本类必须逐条覆写，将其替换为 {@code dyedream_dirt}；否则耕地会在踩踏/干涸/遮挡时
 * 悄悄变成原版泥土。
 * <p>
 * 打有 {@code c:farmlands} 标签，供 Neoforge 生态中的作物与工具识别。
 */
public class DyedreamFarmlandBlock extends FarmBlock {

    /**
     * @param properties 方块属性（复制自原版耕地）
     */
    public DyedreamFarmlandBlock(Properties properties) {
        super(properties);
    }

    /**
     * 注意：不覆写 {@link FarmBlock#codec()} —— 原版该方法返回具体类型
     * {@code MapCodec<FarmBlock>}，受 Java 泛型不变性限制无法以
     * {@code MapCodec<DyedreamFarmlandBlock>} 覆写；而世界存档的方块状态
     * 反序列化走注册表 stateId 路径（不经过 codec），因此继承父类 codec 即可。
     */

    // ==================== 放置 ====================

    /**
     * 放置：替换原版 {@link FarmBlock#getStateForPlacement} 中「上方不合法时给原版泥土」
     * 的兜底，改为给染梦泥土。
     *
     * @param context 放置上下文
     * @return 放置的方块状态
     */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return !this.defaultBlockState().canSurvive(context.getLevel(), context.getClickedPos())
                ? PDBlocks.DYEDREAM_DIRT.get().defaultBlockState()
                : super.getStateForPlacement(context);
    }

    // ==================== 踩踏 ====================

    /**
     * 实体落地：按 {@link CommonHooks#onFarmlandTrample} 判定是否被踩回染梦泥土。
     * <p>
     * <b>不能调用 {@code super.fallOn}</b>：原版 {@link FarmBlock#fallOn} 会再次执行
     * {@code onFarmlandTrample(..., Blocks.DIRT, ...)} 判定并调用硬编码原版泥土的
     * {@code turnToDirt}，导致「有概率变成原版泥土」。这里手动调用
     * {@link Entity#causeFallDamage} 保留掉落伤害（即原版 {@code Block#fallOn} 的职责）。
     *
     * @param level        世界
     * @param state        耕地状态
     * @param pos          耕地位置
     * @param entity       落地的实体
     * @param fallDistance 下落距离
     */
    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (!level.isClientSide && CommonHooks.onFarmlandTrample(
                level, pos, PDBlocks.DYEDREAM_DIRT.get().defaultBlockState(), fallDistance, entity)) {
            turnToDyedreamDirt(entity, state, level, pos);
        }
        // 保留掉落伤害（原版 Block#fallOn 的职责），但不走 super.fallOn 避免二次踩踏判定
        entity.causeFallDamage(fallDistance, 1.0F, entity.damageSources().fall());
    }

    // ==================== 支撑退化 ====================

    /**
     * 计划刻：上方被不透明方块遮挡导致支撑不足时，退化为染梦泥土
     * （替换原版 {@link FarmBlock#tick} 中硬编码的原版泥土）。
     *
     * @param state  当前方块状态
     * @param level  服务端世界
     * @param pos    耕地位置
     * @param random 随机源
     */
    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            turnToDyedreamDirt(null, state, level, pos);
        }
    }

    // ==================== 随机刻：湿润/干涸 + 作物加速 ====================

    /**
     * 随机刻：复制原版 {@link FarmBlock#randomTick} 的湿润/干涸逻辑（水边瞬间湿润到满，
     * 干燥逐级下降，干燥且无作物时退化为泥土），但退化目标改为染梦泥土；
     * 之后额外触发一次上方作物的随机刻，使染梦耕地上的作物生长速度约为原版的 2 倍。
     *
     * @param state  当前方块状态
     * @param level  服务端世界
     * @param pos    耕地位置
     * @param random 随机源
     */
    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int moisture = state.getValue(MOISTURE);
        if (!isNearWater(level, pos) && !level.isRainingAt(pos.above())) {
            // 无水源且不下雨 → 水分逐级下降；降至 0 且上方无作物维持 → 退化染梦泥土
            if (moisture > 0) {
                level.setBlock(pos, state.setValue(MOISTURE, moisture - 1), 2);
            } else if (!shouldMaintainFarmland(level, pos)) {
                turnToDyedreamDirt(null, state, level, pos);
                return;
            }
        } else if (moisture < 7) {
            // 水源（4 格内）/下雨 → 立即湿润到满（原版语义：湿润速度与原版一致）
            level.setBlock(pos, state.setValue(MOISTURE, 7), 2);
        }

        // 作物 2 倍速：额外触发一次上方作物的随机刻（原版作物生长判定）
        BlockPos above = pos.above();
        BlockState aboveState = level.getBlockState(above);
        if (aboveState.getBlock() instanceof CropBlock) {
            aboveState.randomTick(level, above, random);
        }
    }

    /**
     * 判断 4 格范围内是否有可湿润耕地的水源（复制原版 {@code FarmBlock#isNearWater}，
     * 该方法为 private 无法直接调用）。
     *
     * @param level 世界读取器
     * @param pos   耕地位置
     * @return 有水源返回 true
     */
    private static boolean isNearWater(LevelReader level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
            if (state.canBeHydrated(level, pos, level.getFluidState(blockpos), blockpos)) {
                return true;
            }
        }
        return FarmlandWaterManager.hasBlockWaterTicket(level, pos);
    }

    /**
     * 判断上方方块是否维持耕地（原版 {@code MAINTAINS_FARMLAND} 标签，如作物）。
     * 复制自原版 {@code FarmBlock#shouldMaintainFarmland}（private 无法直接调用）。
     *
     * @param level 方块读取器
     * @param pos   耕地位置
     * @return 上方有维持耕地的方块返回 true
     */
    private static boolean shouldMaintainFarmland(BlockGetter level, BlockPos pos) {
        return level.getBlockState(pos.above()).is(BlockTags.MAINTAINS_FARMLAND);
    }

    // ==================== 转换 ====================

    /**
     * 将耕地转为染梦泥土（替换原版 {@link FarmBlock#turnToDirt} 的目标方块）。
     *
     * @param entity 踩踏实体（用于推动实体与游戏事件上下文，可为 null）
     * @param state  当前耕地状态
     * @param level  世界
     * @param pos    耕地位置
     */
    public static void turnToDyedreamDirt(Entity entity, BlockState state, Level level, BlockPos pos) {
        BlockState dirt = pushEntitiesUp(state, PDBlocks.DYEDREAM_DIRT.get().defaultBlockState(), level, pos);
        level.setBlockAndUpdate(pos, dirt);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, dirt));
    }
}
