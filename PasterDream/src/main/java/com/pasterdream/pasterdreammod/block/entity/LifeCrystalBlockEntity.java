package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

/**
 * 生命水晶方块实体 (Life Crystal Block Entity)
 * 集成 GeckoLib 动画 + 吸收计时器系统
 *
 * 功能流程：
 * 1. 玩家右键 → animation = 1 → 播放旋转动画
 * 2. 服务器端 tick 计时 40 ticks（2 秒）
 * 3. 每隔 5 ticks 生成爱心粒子
 * 4. 第 35 tick：最终粒子爆发（爱心+水晶粒子）
 * 5. 第 40 tick：摧毁方块 → 玩家最大生命值 +2 → 显示消息
 */
public class LifeCrystalBlockEntity extends BlockEntity implements GeoBlockEntity {

    private static final ResourceLocation MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "life_crystal");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final String ANIM_PROPERTY = "animation";

    /** 缓存动画属性值，避免每帧查询 block state */
    private int cachedAnimation = 0;

    /** 吸收计时 tick 数（从 0 开始累加） */
    private int useTicks = 0;
    /** 是否正在执行吸收流程 */
    private boolean useActive = false;
    /** 激活水晶的玩家 UUID，用于后续加成 */
    private UUID activatingPlayerUUID = null;

    /**
     * 构造生命水晶方块实体
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public LifeCrystalBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntities.LIFE_CRYSTAL.get(), pos, state);
        updateCachedAnimation(state);
    }

    /**
     * 玩家右键激活水晶，启动吸收计时
     *
     * @param player 激活的玩家
     */
    public void startUse(Player player) {
        this.useActive = true;
        this.useTicks = 0;
        this.activatingPlayerUUID = player.getUUID();
        setChanged();
    }

    // ==================== GeckoLib 动画 ====================

    /**
     * 动画控制器 1：空闲循环
     * animation = 0 时循环播放漂浮 + 脉动缩放效果
     */
    private PlayState idlePredicate(software.bernie.geckolib.animation.AnimationState<LifeCrystalBlockEntity> state) {
        int anim = getAnimationProperty();
        if (anim == 0) {
            return state.setAndContinue(RawAnimation.begin().thenLoop("0"));
        }
        return PlayState.STOP;
    }

    /**
     * 动画控制器 2：触发式旋转
     * animation = 1 时播放旋转 + 升空动画，播放完毕后自动重置为 0
     */
    private PlayState activatePredicate(software.bernie.geckolib.animation.AnimationState<LifeCrystalBlockEntity> state) {
        int anim = getAnimationProperty();
        if (anim != 0 && state.getController().getAnimationState() == AnimationController.State.STOPPED) {
            state.getController().setAnimation(RawAnimation.begin().thenPlay(String.valueOf(anim)));
            if (state.getController().getAnimationState() == AnimationController.State.STOPPED) {
                resetAnimationProperty();
                state.getController().forceAnimationReset();
            }
        } else if (anim == 0) {
            return PlayState.STOP;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::idlePredicate));
        controllers.add(new AnimationController<>(this, "procedurecontroller", 0, this::activatePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null) {
            updateCachedAnimation(getBlockState());
        }
    }

    @Override
    public void setBlockState(BlockState state) {
        super.setBlockState(state);
        updateCachedAnimation(state);
    }

    /**
     * 更新缓存的动画属性值
     * 从方块状态中提取 animation 属性并缓存
     */
    private void updateCachedAnimation(BlockState state) {
        if (state.getBlock().getStateDefinition().getProperty(ANIM_PROPERTY) instanceof IntegerProperty prop) {
            cachedAnimation = state.getValue(prop);
        } else {
            cachedAnimation = 0;
        }
    }

    /**
     * 获取方块状态的 animation 属性值（使用缓存，避免每帧查询 block state）
     *
     * @return 0（空闲）或 1（激活）
     */
    private int getAnimationProperty() {
        return cachedAnimation;
    }

    /**
     * 将动画属性重置为 0（空闲状态）
     */
    private void resetAnimationProperty() {
        if (level == null) return;
        BlockState state = level.getBlockState(worldPosition);
        if (state.getBlock().getStateDefinition().getProperty(ANIM_PROPERTY) instanceof IntegerProperty prop) {
            level.setBlock(worldPosition, state.setValue(prop, 0), 3);
        }
    }

    // ==================== 服务器端吸收计时器 ====================

    /**
     * 服务器端 tick 处理 - 管理吸收流程的时序
     *
     * 时间线：
     * - tick 2, 7, 12, 17, 22, 27, 32：每波 3 个爱心粒子
     * - tick 35：4 个爱心 + 8 个水晶粒子（最终爆发）
     * - tick 40：摧毁方块 → 玩家最大生命值 +2
     *
     * @param level 世界实例
     * @param pos   方块位置
     * @param state 方块状态
     */
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (!useActive || !(level instanceof ServerLevel serverLevel)) return;

        useTicks++;

        // 每 5 tick 生成爱心粒子（从第 2 tick 开始）
        if (useTicks >= 2 && (useTicks - 2) % 5 == 0) {
            double yOffset = 0.6 + level.random.nextDouble() * 0.4;
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + 0.5, pos.getY() + yOffset, pos.getZ() + 0.5,
                    3, 0, 0, 0, 1);
        }

        // 第 35 tick：最终粒子爆发
        if (useTicks == 35) {
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5,
                    4, 0.3, 0.3, 0.3, 1);
            for (int i = 0; i < 8; i++) {
                serverLevel.sendParticles((SimpleParticleType) PDParticles.MELTDREAM_CRYSTAL_PARTICLE.particleType(),
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        1, 0, 0, 0, 1);
            }
        }

        // 第 40 tick：摧毁方块 + 应用属性修改器
        if (useTicks >= 40) {
            BlockState currentState = level.getBlockState(pos);
            if (currentState.getBlock() == state.getBlock()) {
                // 应用最大生命值永久 +2
                if (activatingPlayerUUID != null) {
                    Player player = level.getPlayerByUUID(activatingPlayerUUID);
                    if (player instanceof LivingEntity livingEntity) {
                        AttributeInstance attr = livingEntity.getAttribute(Attributes.MAX_HEALTH);
                        if (attr != null && !attr.hasModifier(MODIFIER_ID)) {
                            attr.addPermanentModifier(
                                    new AttributeModifier(MODIFIER_ID, 2,
                                            AttributeModifier.Operation.ADD_VALUE));
                        }
                        // 播放破碎音效
                        level.playSound(null, pos, SoundEvent.createVariableRangeEvent(
                                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "life_crystal")),
                                SoundSource.BLOCKS, 1.0f, 0.5f);
                        // 发送消息
                        player.displayClientMessage(
                                net.minecraft.network.chat.Component.literal(
                                        "生命水晶破碎并涌入你的体内 §a最大生命值+2"),
                                false);
                    }
                }
                // 清除方块
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
            useActive = false;
            useTicks = 0;
            activatingPlayerUUID = null;
        }
    }
}