package com.pasterdream.pasterdreammod.entity.mob;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;
import com.pasterdream.pasterdreammod.api.entity.anim.ProcedureAnimationHandler;

/**
 * 金色狐狸 (Golden Fox) — 实现愿望的神秘金狐
 * <p>
 * 行为：
 * - 静止不动的环境生物，无 AI
 * - 接受特定祭品（金苹果/桶/梦币/融梦水晶等）并给予回报
 * - 几乎所有伤害类型免疫
 * - 右键交互后会消耗物品、播放粒子、延迟掉落回报物品后消失
 * <p>
 * 渲染：GeckoLib 动画实体，含 idle/movement/procedure 动画
 */
public class GoldenFoxEntity extends PathfinderMob implements GeoEntity {

    private static final EntityDataAccessor<Boolean> SHOOT =
            SynchedEntityData.defineId(GoldenFoxEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> ANIMATION =
            SynchedEntityData.defineId(GoldenFoxEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> TEXTURE =
            SynchedEntityData.defineId(GoldenFoxEntity.class, EntityDataSerializers.STRING);

    /** GeckoLib 动画实例缓存 */
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** 当前动画标识（用于 procedure 控制器） */
    public String animationprocedure = "empty";

    /** 客户端 procedure 动画处理器 */
    private final ProcedureAnimationHandler procAnim = new ProcedureAnimationHandler();

    /**
     * 构造金色狐狸实体
     *
     * @param entityType 实体类型
     * @param level      世界实例
     */
    public GoldenFoxEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.setNoAi(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHOOT, false);
        builder.define(ANIMATION, "undefined");
        builder.define(TEXTURE, "gloden_fox_light");
    }

    /**
     * 设置纹理
     *
     * @param texture 纹理名称
     */
    public void setTexture(String texture) {
        this.entityData.set(TEXTURE, texture);
    }

    /**
     * 获取纹理名称
     *
     * @return 纹理名称
     */
    public String getTexture() {
        return this.entityData.get(TEXTURE);
    }

    /**
     * 获取同步的动画名称
     *
     * @return 动画名称
     */
    public String getSyncedAnimation() {
        return this.entityData.get(ANIMATION);
    }

    /**
     * 设置同步动画，同时赋值 animationprocedure 以触发 procedure 控制器
     *
     * @param animation 动画名称
     */
    public void setAnimation(String animation) {
        this.entityData.set(ANIMATION, animation);
        this.animationprocedure = animation;
    }

    // ==================== NBT 持久化 ====================

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Texture", this.getTexture());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Texture")) {
            this.setTexture(compound.getString("Texture"));
        }
    }

    // ==================== 属性 ====================

    /**
     * 创建金色狐狸的属性
     * 移速0, 生命8, 护甲0, 攻击伤害3, 跟随范围16
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8)
                .add(Attributes.MOVEMENT_SPEED, 0)
                .add(Attributes.ARMOR, 0)
                .add(Attributes.ATTACK_DAMAGE, 3)
                .add(Attributes.FOLLOW_RANGE, 16);
    }

    // ==================== 受伤/免疫 ====================

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.IN_FIRE)) return false;
        if (source.is(DamageTypes.ON_FIRE)) return false;
        if (source.is(DamageTypes.LAVA)) return false;
        if (source.is(DamageTypes.ARROW)) return false;
        if (source.is(DamageTypes.INDIRECT_MAGIC)) return false;
        if (source.is(DamageTypes.FALL)) return false;
        if (source.is(DamageTypes.CACTUS)) return false;
        if (source.is(DamageTypes.DROWN)) return false;
        if (source.is(DamageTypes.LIGHTNING_BOLT)) return false;
        if (source.is(DamageTypes.EXPLOSION)) return false;
        if (source.is(DamageTypes.PLAYER_EXPLOSION)) return false;
        if (source.is(DamageTypes.TRIDENT)) return false;
        if (source.is(DamageTypes.FALLING_ANVIL)) return false;
        if (source.is(DamageTypes.DRAGON_BREATH)) return false;
        if (source.is(DamageTypes.WITHER)) return false;
        if (source.is(DamageTypes.WITHER_SKULL)) return false;

        return super.hurt(source, amount);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove(RemovalReason.KILLED);
        }
    }

    // ==================== 音效 ====================

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.FOX_AMBIENT, 0.15f, 1.0f);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.FOX_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.FOX_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.FOX_DEATH;
    }

    // ==================== 交互 ====================

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ServerLevel serverLevel = (ServerLevel) level();
        ItemStack heldStack = player.getItemInHand(hand);
        Item heldItem = heldStack.getItem();

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(heldItem);
        String itemNamespace = itemId.getNamespace();
        String itemPath = itemId.getPath();

        SacrificeType sacrifice = matchSacrifice(itemNamespace, itemPath);
        if (sacrifice == null) {
            return InteractionResult.PASS;
        }

        if (!player.hasInfiniteMaterials()) {
            heldStack.shrink(1);
        }

        Vec3 pos = this.position();

        // 播放黄色烟雾+云粒子
        serverLevel.sendParticles(ParticleTypes.CLOUD,
                pos.x, pos.y + 0.5, pos.z,
                30, 0.5, 0.5, 0.5, 0.1);
        serverLevel.sendParticles(ParticleTypes.END_ROD,
                pos.x, pos.y + 0.5, pos.z,
                20, 0.5, 0.5, 0.5, 0.1);

        // 播放狐狸叫声
        this.playSound(SoundEvents.FOX_AMBIENT, 1.0f, 1.0f);

        BlockPos entityPos = this.blockPosition();
        this.remove(RemovalReason.DISCARDED);

        scheduleDelayedReward(serverLevel, sacrifice, entityPos, pos);

        return InteractionResult.SUCCESS;
    }

    /**
     * 祭品类型枚举 — 定义金色狐狸接受的祭品及对应回报
     */
    private enum SacrificeType {
        GOLDEN_APPLE,
        BUCKET,
        DREAM_COIN,
        MELTDREAM_CRYSTAL,
        MEMENTO
    }

    /**
     * 匹配物品是否为有效祭品
     *
     * @param namespace 物品命名空间
     * @param path      物品路径
     * @return 匹配的祭品类型，无效返回 null
     */
    @Nullable
    private SacrificeType matchSacrifice(String namespace, String path) {
        if (namespace.equals("minecraft") && path.equals("golden_apple")) {
            return SacrificeType.GOLDEN_APPLE;
        }
        if (namespace.equals("minecraft") && path.equals("bucket")) {
            return SacrificeType.BUCKET;
        }
        if (namespace.equals("pasterdream") && path.equals("dream_coin_1")) {
            return SacrificeType.DREAM_COIN;
        }
        if (namespace.equals("pasterdream") && path.equals("meltdream_crystal_0")) {
            return SacrificeType.MELTDREAM_CRYSTAL;
        }
        if (namespace.equals("pasterdream") && path.equals("memento_item_11")) {
            return SacrificeType.MEMENTO;
        }
        return null;
    }

    /**
     * 延迟 15 tick 生成回报物品
     *
     * @param serverLevel 服务端世界实例
     * @param sacrifice   祭品类型
     * @param pos         实体位置（用于放置方块）
     * @param dropPos     掉落物生成位置
     */
    private void scheduleDelayedReward(ServerLevel serverLevel, SacrificeType sacrifice, BlockPos pos, Vec3 dropPos) {
        serverLevel.getServer().tell(new TickTask(
                serverLevel.getServer().getTickCount() + 15,
                () -> {
                    switch (sacrifice) {
                        case GOLDEN_APPLE -> {
                            ItemStack reward = new ItemStack(Items.ENCHANTED_GOLDEN_APPLE);
                            spawnItemDrop(serverLevel, dropPos, reward);
                        }
                        case BUCKET -> {
                            Item meltdreamLiquidBucket = BuiltInRegistries.ITEM.get(
                                    ResourceLocation.parse("pasterdream:meltdream_liquid_bucket"));
                            if (meltdreamLiquidBucket != Items.AIR) {
                                spawnItemDrop(serverLevel, dropPos, new ItemStack(meltdreamLiquidBucket));
                            }
                        }
                        case DREAM_COIN -> {
                            Block meltdreamChest = BuiltInRegistries.BLOCK.get(
                                    ResourceLocation.parse("pasterdream:meltdream_chest"));
                            if (meltdreamChest != null) {
                                serverLevel.setBlock(pos, meltdreamChest.defaultBlockState(), 3);
                            }
                        }
                        case MELTDREAM_CRYSTAL, MEMENTO -> {
                            Item cradle = BuiltInRegistries.ITEM.get(
                                    ResourceLocation.parse("pasterdream:cradle_in_ones_arms"));
                            if (cradle != Items.AIR) {
                                spawnItemDrop(serverLevel, dropPos, new ItemStack(cradle));
                            }
                        }
                    }

                    serverLevel.players().forEach(p ->
                            p.sendSystemMessage(Component.literal(
                                    "金色狐狸在完成了你许下的愿望之后消失了...")));
                }
        ));
    }

    /**
     * 在世界中生成掉落物
     *
     * @param level 世界实例
     * @param pos   生成位置
     * @param stack 物品堆
     */
    private void spawnItemDrop(ServerLevel level, Vec3 pos, ItemStack stack) {
        ItemEntity item = new ItemEntity(level, pos.x, pos.y, pos.z, stack);
        level.addFreshEntity(item);
    }

    // ==================== GeckoLib 动画 ====================

    private PlayState movementPredicate(AnimationState<GoldenFoxEntity> state) {
        if (state.isMoving()) {
            return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
        }
        return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
    }

    private PlayState procedurePredicate(AnimationState<GoldenFoxEntity> state) {
        return procAnim.predicate(state,
                level().isClientSide(),
                this::getSyncedAnimation,
                () -> setAnimation("empty"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
        controllers.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}