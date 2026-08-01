package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.block.entity.W4DataBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlockEntitiesFurniture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 结构生成方块（structure_block_0..23）
 * <p>
 * 忠实还原原版 {@code StructureBlockNBlock + StructureBlockNPr0Procedure} 语义：
 * 放置后（以及 10 tick 循环兜底）在服务端随机 1..N 抽取一个结构模板
 * placeInWorld（按各自旋转与偏移），随后把自身替换为空气。
 * 无随机数的编号（1/9/11/16/17）直接放置全部模板。
 * 抽取结果写入 BE 数据键 {@code number}（与原版持久化数据一致）。
 * <p>
 * 方块属性：不可破坏（-1/3600000）、石质音效、noOcclusion、光阻 15、
 * 视觉形状为空；掉落回退为自身。
 */
public class PDStructureBlock extends Block implements EntityBlock {

    /**
     * 单条结构放置规则
     *
     * @param min      随机数下界（0 表示无条件放置）
     * @param max      随机数上界
     * @param template 结构模板名（pasterdream 命名空间）
     * @param dx       X 偏移
     * @param dy       Y 偏移
     * @param dz       Z 偏移
     * @param rotation 放置旋转
     */
    public record Placement(int min, int max, String template, int dx, int dy, int dz, Rotation rotation) {
    }

    /**
     * 单个结构方块的完整规格
     *
     * @param randomRange 随机数上界（0 = 无随机）
     * @param tickLoop    是否有 10 tick 调度循环（原版仅 structure_block_1 没有）
     * @param placements  放置规则表
     * @param tooltip     悬浮提示行（原版 Component.literal 文案）
     */
    public record Spec(int randomRange, boolean tickLoop, List<Placement> placements, List<String> tooltip) {
    }

    /** 24 个结构方块的规格表（数据自原版 procedures 逐一提取） */
    public static final Map<Integer, Spec> SPECS = new HashMap<>();

    static {
        SPECS.put(0, new Spec(4, true,
                List.of(new Placement(1, 1, "dream_church_0", 0, 0, 0, Rotation.NONE),
                        new Placement(2, 2, "dream_church_1", 0, 0, 0, Rotation.CLOCKWISE_90),
                        new Placement(3, 3, "dream_church_2", 0, 0, 0, Rotation.CLOCKWISE_180),
                        new Placement(4, 4, "dream_church_3", 0, 0, 0, Rotation.COUNTERCLOCKWISE_90)),
                List.of("tooltip.pasterdream.structure_block.place", "tooltip.pasterdream.structure_block.name.dream_church_first", "tooltip.pasterdream.structure_block.random_count.4")));
        SPECS.put(1, new Spec(0, false,
                List.of(new Placement(0, 0, "dyedream_laboratory_0", 0, 0, 0, Rotation.NONE)),
                List.of("tooltip.pasterdream.structure_block.place", "tooltip.pasterdream.structure_block.name.dyedream_laboratory", "tooltip.pasterdream.structure_block.random_count.1")));
        SPECS.put(2, new Spec(4, true,
                List.of(new Placement(1, 1, "pinkagaric_house_0", -11, -4, -11, Rotation.NONE),
                        new Placement(2, 2, "pinkagaric_house_1", -11, -4, -11, Rotation.NONE),
                        new Placement(3, 3, "pinkagaric_house_2", -11, -4, -11, Rotation.NONE),
                        new Placement(4, 4, "pinkagaric_house_3", -11, -4, -11, Rotation.NONE)),
                List.of("tooltip.pasterdream.structure_block.place", "tooltip.pasterdream.structure_block.name.pinkagaric_house", "tooltip.pasterdream.structure_block.random_count.4")));
        SPECS.put(3, new Spec(4, true,
                List.of(new Placement(1, 1, "dream_wishingtree_0", 0, 0, 0, Rotation.NONE),
                        new Placement(2, 4, "dream_wishingtree_1", 0, 0, 0, Rotation.CLOCKWISE_90)),
                List.of("tooltip.pasterdream.structure_block.place", "tooltip.pasterdream.structure_block.name.dream_wishingtree", "tooltip.pasterdream.structure_block.random_count.2")));
        SPECS.put(4, new Spec(4, true,
                List.of(new Placement(1, 1, "dream_church_4", 0, 0, 0, Rotation.NONE),
                        new Placement(2, 2, "dream_church_5", 0, 0, 0, Rotation.CLOCKWISE_90),
                        new Placement(3, 3, "dream_church_6", 0, 0, 0, Rotation.CLOCKWISE_180),
                        new Placement(4, 4, "dream_church_7", 0, 0, 0, Rotation.COUNTERCLOCKWISE_90)),
                List.of("tooltip.pasterdream.structure_block.place", "tooltip.pasterdream.structure_block.name.dream_church_second", "tooltip.pasterdream.structure_block.random_count.4")));
        SPECS.put(5, new Spec(9, true,
                List.of(new Placement(1, 4, "traveler_house_1", -7, -13, -9, Rotation.NONE),
                        new Placement(5, 5, "traveler_house_0", -7, -13, -9, Rotation.CLOCKWISE_90),
                        new Placement(6, 9, "traveler_house_2", -7, -13, -9, Rotation.CLOCKWISE_180)),
                List.of("tooltip.pasterdream.structure_block.place", "tooltip.pasterdream.structure_block.name.traveler_house", "tooltip.pasterdream.structure_block.random_count.3")));
        SPECS.put(6, new Spec(6, true,
                List.of(new Placement(1, 1, "crystal_ball_0", 0, 0, 0, Rotation.NONE),
                        new Placement(2, 6, "crystal_ball_1", 0, 0, 0, Rotation.CLOCKWISE_90)),
                List.of("tooltip.pasterdream.structure_block.place", "tooltip.pasterdream.structure_block.name.dyedream_crystal_ball", "tooltip.pasterdream.structure_block.random_count.2")));
        SPECS.put(7, new Spec(2, true,
                List.of(new Placement(1, 1, "meltdream_liquid_well", 0, 0, 0, Rotation.NONE),
                        new Placement(2, 2, "meltdream_liquid_well_0", 0, 0, 0, Rotation.NONE)),
                List.of("tooltip.pasterdream.structure_block.place", "tooltip.pasterdream.structure_block.name.meltdream_liquid_well", "tooltip.pasterdream.structure_block.random_count.2")));
        SPECS.put(8, new Spec(5, true,
                List.of(new Placement(1, 1, "stone_pillar_sky0", 0, 0, 0, Rotation.NONE),
                        new Placement(2, 3, "stone_pillar_sky1", 0, 0, 0, Rotation.NONE),
                        new Placement(4, 5, "stone_pillar_sky2", 0, 0, 0, Rotation.NONE)),
                List.of("tooltip.pasterdream.structure_block.place", "tooltip.pasterdream.structure_block.name.dyedream_floating_island", "tooltip.pasterdream.structure_block.random_count.3")));
        SPECS.put(9, new Spec(0, true,
                List.of(new Placement(0, 0, "shadow_world_door", 0, 0, 0, Rotation.NONE)),
                List.of("tooltip.pasterdream.structure_block.place", "tooltip.pasterdream.structure_block.name.shadow_cage", "tooltip.pasterdream.structure_block.random_count.1")));
        SPECS.put(10, new Spec(6, true,
                List.of(new Placement(1, 1, "shadow_tomb_0", 0, 0, 0, Rotation.NONE),
                        new Placement(2, 2, "shadow_tomb_1", 0, 0, 0, Rotation.CLOCKWISE_90),
                        new Placement(3, 3, "shadow_tomb_2", 0, 0, 0, Rotation.CLOCKWISE_90),
                        new Placement(4, 4, "shadow_tomb_3", 0, 0, 0, Rotation.CLOCKWISE_90),
                        new Placement(5, 5, "shadow_tomb_4", 0, 0, 0, Rotation.CLOCKWISE_90),
                        new Placement(6, 6, "shadow_tomb_5", 0, 0, 0, Rotation.CLOCKWISE_90)),
                List.of("tooltip.pasterdream.structure_block.place", "tooltip.pasterdream.structure_block.name.shadow_tomb", "tooltip.pasterdream.structure_block.random_count.6", "tooltip.pasterdream.structure_block.incomplete")));
        SPECS.put(11, new Spec(0, true,
                List.of(new Placement(0, 0, "shadow_chain_0", 0, 0, 0, Rotation.NONE),
                        new Placement(0, 0, "shadow_chain_1", -1, 45, -1, Rotation.NONE)),
                List.of("tooltip.pasterdream.structure_block.place", "tooltip.pasterdream.structure_block.name.shadow_lantern", "tooltip.pasterdream.structure_block.random_count.1")));
        SPECS.put(12, new Spec(6, true,
                List.of(new Placement(1, 5, "dream_church_9", 0, 0, 0, Rotation.NONE),
                        new Placement(6, 6, "dream_church_8", 0, 0, 0, Rotation.CLOCKWISE_90)),
                List.of("tooltip.pasterdream.structure_block.place", "tooltip.pasterdream.structure_block.name.dream_church_third", "tooltip.pasterdream.structure_block.random_count.2")));
        SPECS.put(13, new Spec(2, true,
                List.of(new Placement(1, 1, "shadow_shelter_0", 0, 0, 0, Rotation.NONE),
                        new Placement(2, 2, "shadow_shelter_1", 0, 0, 0, Rotation.CLOCKWISE_90)),
                List.of("tooltip.pasterdream.structure_block.place", "tooltip.pasterdream.structure_block.name.shadow_shelter", "tooltip.pasterdream.structure_block.random_count.2")));
        SPECS.put(14, new Spec(3, true,
                List.of(new Placement(1, 1, "shadow_fungus_nest_0", 0, 0, 0, Rotation.NONE),
                        new Placement(2, 2, "shadow_fungus_nest_1", 0, 0, 0, Rotation.CLOCKWISE_90),
                        new Placement(3, 3, "shadow_fungus_nest_2", 0, 0, 0, Rotation.CLOCKWISE_90)),
                List.of("tooltip.pasterdream.structure_block.place", "tooltip.pasterdream.structure_block.name.shadow_fungus_nest", "tooltip.pasterdream.structure_block.random_count.3")));
        SPECS.put(15, new Spec(3, true,
                List.of(new Placement(1, 1, "shadow_foundry_0", 0, 0, 0, Rotation.NONE),
                        new Placement(2, 2, "shadow_foundry_1", 0, 0, 0, Rotation.NONE),
                        new Placement(3, 3, "shadow_foundry_2", 0, 0, 0, Rotation.NONE)),
                List.of("tooltip.pasterdream.structure_block.place", "tooltip.pasterdream.structure_block.name.shadow_foundry", "tooltip.pasterdream.structure_block.random_count.3")));
        SPECS.put(16, new Spec(0, true,
                List.of(new Placement(0, 0, "desert_fortress", -20, 0, -20, Rotation.NONE)),
                List.of("tooltip.pasterdream.structure_block.place", "tooltip.pasterdream.structure_block.name.desert_hero_tomb", "tooltip.pasterdream.structure_block.random_count.2")));
        SPECS.put(17, new Spec(0, true,
                List.of(new Placement(0, 0, "shadow_dungeon_door", 0, -3, 0, Rotation.NONE),
                        new Placement(0, 0, "shadow_dungeon_wall_1", 0, -17, 0, Rotation.NONE),
                        new Placement(0, 0, "shadow_dungeon_wall_0", 0, -63, 0, Rotation.NONE),
                        new Placement(0, 0, "shadow_dungeon_wall_2", 0, -79, 0, Rotation.NONE)),
                List.of("tooltip.pasterdream.structure_block.place", "tooltip.pasterdream.structure_block.name.shadow_dungeon")));
        SPECS.put(18, new Spec(2, true,
                List.of(new Placement(1, 1, "shadow_fungus_house_0", -8, -2, -7, Rotation.NONE),
                        new Placement(2, 2, "shadow_fungus_house_1", -8, -2, -7, Rotation.NONE)),
                List.of("tooltip.pasterdream.structure_block.place", "tooltip.pasterdream.structure_block.name.shadow_fungus_house", "tooltip.pasterdream.structure_block.random_count.2")));
        SPECS.put(19, new Spec(4, true,
                List.of(new Placement(1, 1, "shadow_underground_workroom", -2, -26, -11, Rotation.NONE),
                        new Placement(2, 4, "shadow_underground_workroom_1", -2, -26, -11, Rotation.NONE)),
                List.of("tooltip.pasterdream.structure_block.place", "tooltip.pasterdream.structure_block.name.shadow_underground_workroom", "tooltip.pasterdream.structure_block.random_count.1")));
        SPECS.put(20, new Spec(7, true,
                List.of(new Placement(1, 1, "dyedream_worldtree_true", -40, -25, -45, Rotation.NONE),
                        new Placement(2, 7, "dyedream_worldtree", -40, -25, -45, Rotation.NONE)),
                List.of("tooltip.pasterdream.structure_block.place", "tooltip.pasterdream.structure_block.name.dyedream_worldtree", "tooltip.pasterdream.structure_block.random_count.2")));
        SPECS.put(21, new Spec(4, true,
                List.of(new Placement(1, 1, "windmoor_tree_0", -23, -2, -21, Rotation.NONE),
                        new Placement(2, 2, "windmoor_tree_0", 21, -2, -23, Rotation.CLOCKWISE_90),
                        new Placement(3, 3, "windmoor_tree_0", 23, -2, 21, Rotation.CLOCKWISE_180),
                        new Placement(4, 4, "windmoor_tree_0", -23, -2, 21, Rotation.COUNTERCLOCKWISE_90)),
                List.of("tooltip.pasterdream.structure_block.place", "tooltip.pasterdream.structure_block.name.windmoor_tree", "tooltip.pasterdream.structure_block.random_count.1")));
        SPECS.put(22, new Spec(3, true,
                List.of(new Placement(1, 1, "hot_air_balloon_0", 0, 0, 0, Rotation.NONE),
                        new Placement(2, 2, "hot_air_balloon_1", 0, 0, 0, Rotation.NONE),
                        new Placement(3, 3, "hot_air_balloon_2", 0, 0, 0, Rotation.NONE)),
                List.of("tooltip.pasterdream.structure_block.place", "tooltip.pasterdream.structure_block.name.hot_air_balloon", "tooltip.pasterdream.structure_block.random_count.1")));
        SPECS.put(23, new Spec(1, true,
                List.of(new Placement(1, 1, "christmas_tree_0", -30, 0, -30, Rotation.NONE)),
                List.of("tooltip.pasterdream.structure_block.place", "tooltip.pasterdream.structure_block.name.christmas_tree", "tooltip.pasterdream.structure_block.random_count.1")));
    }

    private final int index;
    private final Spec spec;

    /**
     * 构造结构生成方块
     *
     * @param index      编号（0..23）
     * @param properties 方块属性
     */
    public PDStructureBlock(int index, Properties properties) {
        super(properties);
        this.index = index;
        this.spec = SPECS.get(index);
    }

    /**
     * 获取编号
     *
     * @return 结构方块编号
     */
    public int getIndex() {
        return index;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, context, list, flag);
        for (String line : spec.tooltip()) {
            list.add(Component.translatable(line));
        }
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 15;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        if (!drops.isEmpty()) {
            return drops;
        }
        return Collections.singletonList(new ItemStack(this));
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(state, level, pos, oldState, moving);
        if (spec.tickLoop()) {
            level.scheduleTick(pos, this, 10);
        }
        generate(level, pos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        generate(level, pos);
        level.scheduleTick(pos, this, 10);
    }

    /**
     * 执行结构生成（服务端）：随机抽号 → 写入 BE → 放置模板 → 替换为空气
     *
     * @param level 世界
     * @param pos   方块位置
     */
    private void generate(Level level, BlockPos pos) {
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        int number = 0;
        if (spec.randomRange() > 0) {
            number = Mth.nextInt(RandomSource.create(), 1, spec.randomRange());
            W4DataBlockEntity.putDoubleAt(level, pos, "number", number);
        }
        for (Placement placement : spec.placements()) {
            boolean matches = placement.min() == 0
                    || (number >= placement.min() && number <= placement.max());
            if (!matches) {
                continue;
            }
            StructureTemplate template = serverLevel.getStructureManager().getOrCreate(
                    ResourceLocation.fromNamespaceAndPath("pasterdream", placement.template()));
            if (template != null) {
                BlockPos target = pos.offset(placement.dx(), placement.dy(), placement.dz());
                template.placeInWorld(serverLevel, target, target,
                        new StructurePlaceSettings()
                                .setRotation(placement.rotation())
                                .setMirror(Mirror.NONE)
                                .setIgnoreEntities(false),
                        serverLevel.random, 3);
            }
        }
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
    }

    // ==================== 方块实体 ====================

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new W4DataBlockEntity(PDBlockEntitiesFurniture.STRUCTURE_BLOCKS.get(index).get(), pos, state);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int eventId, int eventParam) {
        super.triggerEvent(state, level, pos, eventId, eventParam);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(eventId, eventParam);
    }
}
