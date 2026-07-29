package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.W4DataBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.W4GeoDataBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlockEntitiesFurniture;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.items.PDItemsFoods;
import com.pasterdream.pasterdreammod.registry.items.PDItemsTools;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * 荒漠英雄之墓（desert_hero_tomb）
 * <p>
 * 忠实还原原版 {@code DesertHeroTombBlock + DesertHeroTombPr0Procedure} 的任务链：
 * <ul>
 *   <li>阶段 0：亡魂苏醒的四句独白，赠予荒漠之剑（desert_sword）；</li>
 *   <li>阶段 1：清除 48 格内掠夺者/尸壳后领取三个心愿；</li>
 *   <li>阶段 2：任务一主手 10 块年糕、任务二 9 格内有马、任务三村庄英雄效果——
 *       全部完成进入阶段 3；</li>
 *   <li>阶段 3：携荒漠之剑再访，将其化为『沉荆门』朔漠（true_desert_sword）；</li>
 *   <li>阶段 4：不再回应。</li>
 * </ul>
 * 每次交互伴随 animation=1、灵魂粒子与骷髅脚步声。
 * 不可破坏、FACING、GeckoLib 渲染，形状随朝向 (0,0,-6,16,11,22)/(‑6,0,0,22,11,16)。
 */
public class DesertHeroTombBlock extends BaseEntityBlock {

    public static final MapCodec<DesertHeroTombBlock> CODEC = simpleCodec(DesertHeroTombBlock::new);

    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 2);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    /**
     * 构造荒漠英雄之墓方块
     *
     * @param properties 方块属性
     */
    public DesertHeroTombBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST, WEST -> box(-6, 0, 0, 22, 11, 16);
            default -> box(0, 0, -6, 16, 11, 22);
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ANIMATION, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
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
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        // 演出：animation=1 + 灵魂粒子 + 骷髅脚步声
        BlockState current = level.getBlockState(pos);
        if (current.getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty prop
                && prop.getPossibleValues().contains(1)) {
            level.setBlock(pos, current.setValue(prop, 1), 3);
        }
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(PDParticles.SOUL_PARTICLE.holder().get(),
                    x + 0.5, y + 0.5, z + 0.5, 7, 0.2, 3, 0.2, 0.1);
        }
        if (!level.isClientSide()) {
            level.playSound(null, pos, SoundEvents.SKELETON_STEP, SoundSource.NEUTRAL, 1, 1);
        }

        if (W4DataBlockEntity.getDoubleAt(level, pos, "number") < 1) {
            W4DataBlockEntity.putDoubleAt(level, pos, "number", 0);
        }

        // 注意：与原版一致，各阶段判断均为“新鲜读取”，同一次点击可级联进入下一阶段
        if (W4DataBlockEntity.getDoubleAt(level, pos, "number") == 0) {
            W4DataBlockEntity.putDoubleAt(level, pos, "number", 1);
            ServerScheduler.schedule(20, () -> chat(player, "良久，竟有来者。"));
            ServerScheduler.schedule(40, () -> chat(player, "无苦心，有孚，吾有一事相求。"));
            ServerScheduler.schedule(60, () -> chat(player, "如卿所见，吾今也。"));
            ServerScheduler.schedule(80, () -> chat(player, "以剑授汝，可否助我逐外寇也？"));
            ServerScheduler.schedule(90, () -> {
                ItemStack sword = new ItemStack(PDItemsTools.DESERT_SWORD.get());
                sword.setCount(1);
                ItemHandlerHelper.giveItemToPlayer(player, sword);
                chat(player, "[清除附近的劫掠者与尸壳]");
            });
        }

        if (W4DataBlockEntity.getDoubleAt(level, pos, "number") == 1) {
            Vec3 above = new Vec3(x, y + 16, z);
            boolean threats = !level.getEntitiesOfClass(Pillager.class,
                    AABB.ofSize(above, 48, 48, 48), e -> true).isEmpty()
                    || !level.getEntitiesOfClass(Husk.class,
                    AABB.ofSize(above, 48, 48, 48), e -> true).isEmpty();
            if (threats) {
                bar(player, "尚未清除附近的威胁，他不予回应");
            } else {
                chat(player, "为得好，子曰谢酬。");
                ServerScheduler.schedule(20, () -> chat(player, "汝又能终吾三请，吾以真剑付汝。"));
                ServerScheduler.schedule(40, () -> chat(player, "其名为：§e『沉荆门』朔漠"));
                ServerScheduler.schedule(60, () -> {
                    chat(player, "1.为我带来10块年糕");
                    chat(player, "2.我想要一匹马在旁边陪着我");
                    chat(player, "3.获得一个全村庄村民的英雄认可时来见我");
                    W4DataBlockEntity.putDoubleAt(level, pos, "number", 2);
                });
            }
        }

        if (W4DataBlockEntity.getDoubleAt(level, pos, "number") == 2) {
            if (!W4DataBlockEntity.getBooleanAt(level, pos, "task1")) {
                ItemStack main = player.getMainHandItem();
                if (main.getItem() == PDItemsFoods.RICECAKE.get() && main.getCount() >= 10) {
                    main.shrink(10);
                    W4DataBlockEntity.putBooleanAt(level, pos, "task1", true);
                    bar(player, "任务1完成");
                }
            }
            if (!W4DataBlockEntity.getBooleanAt(level, pos, "task2")) {
                if (!level.getEntitiesOfClass(Horse.class,
                        AABB.ofSize(new Vec3(x, y, z), 9, 9, 9), e -> true).isEmpty()) {
                    W4DataBlockEntity.putBooleanAt(level, pos, "task2", true);
                    bar(player, "任务2完成");
                }
            }
            if (!W4DataBlockEntity.getBooleanAt(level, pos, "task3")) {
                if (player.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)) {
                    W4DataBlockEntity.putBooleanAt(level, pos, "task3", true);
                    bar(player, "任务3完成");
                }
            }
            if (W4DataBlockEntity.getBooleanAt(level, pos, "task1")
                    && W4DataBlockEntity.getBooleanAt(level, pos, "task2")
                    && W4DataBlockEntity.getBooleanAt(level, pos, "task3")) {
                W4DataBlockEntity.putDoubleAt(level, pos, "number", 3);
                ServerScheduler.schedule(20, () -> chat(player, "吾心愿已成，请携朔漠大剑见我"));
            }
        }

        if (W4DataBlockEntity.getDoubleAt(level, pos, "number") == 3) {
            if (player.getMainHandItem().getItem() == PDItemsTools.DESERT_SWORD.get()) {
                W4DataBlockEntity.putDoubleAt(level, pos, "number", 4);
                chat(player, "请君收下吧：此名为『沉荆门』朔漠");
                ItemStack trueSword = new ItemStack(PDItemsTools.TRUE_DESERT_SWORD.get());
                trueSword.setCount(1);
                player.setItemInHand(InteractionHand.MAIN_HAND, trueSword);
                player.getInventory().setChanged();
            } else {
                chat(player, "吾心愿已成，请携朔漠大剑见我");
            }
        }

        if (W4DataBlockEntity.getDoubleAt(level, pos, "number") == 4) {
            bar(player, "已有人完成他的全部愿望，他便不再回应");
        }
        return InteractionResult.SUCCESS;
    }

    /** 聊天栏消息（仅服务端） */
    private static void chat(Player player, String text) {
        if (!player.level().isClientSide()) {
            player.displayClientMessage(Component.literal(text), false);
        }
    }

    /** 快捷栏消息（仅服务端） */
    private static void bar(Player player, String text) {
        if (!player.level().isClientSide()) {
            player.displayClientMessage(Component.literal(text), true);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new W4GeoDataBlockEntity(PDBlockEntitiesFurniture.DESERT_HERO_TOMB.get(), pos, state);
    }
}
