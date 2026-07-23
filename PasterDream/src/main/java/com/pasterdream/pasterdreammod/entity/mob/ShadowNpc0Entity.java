package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.api.entity.base.GeckoLibMobEntity;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animation.*;

/**
 * ???（无名NPC）—— 徘徊在暗影地牢中的神秘 NPC
 * <p>
 * 行为特点：
 * <ul>
 *   <li>被动 NPC，继承 {@link GeckoLibMobEntity}</li>
 *   <li>只有随机张望 AI（{@link RandomLookAroundGoal}）</li>
 *   <li>免疫火焰伤害和玩家直接伤害</li>
 *   <li>右键交互：播放 "say" 动画、显示对话、给予金块、提示进度解锁</li>
 * </ul>
 * <p>
 * 渲染：GeckoLib 动画实体，默认纹理 "shadow_npc_0"
 */
public class ShadowNpc0Entity extends GeckoLibMobEntity {

    /** 是否已经交互过的标记（防止重复触发对话） */
    private boolean hasInteracted = false;

    /**
     * 构造无名 NPC 实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public ShadowNpc0Entity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.xpReward = 0;
    }

    /**
     * 返回默认纹理名称
     *
     * @return 默认纹理名
     */
    @Override
    protected String getDefaultTexture() {
        return "shadow_npc_0";
    }

    // ======================== NBT 持久化 ========================

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("HasInteracted", this.hasInteracted);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("HasInteracted")) {
            this.hasInteracted = compound.getBoolean("HasInteracted");
        }
    }

    // ======================== 属性 ========================

    /**
     * 创建无名 NPC 的属性
     * <ul>
     *   <li>生命值: 20</li>
     *   <li>护甲: 0</li>
     *   <li>移动速度: 0（静止）</li>
     *   <li>攻击伤害: 3</li>
     *   <li>追踪范围: 16</li>
     * </ul>
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.ARMOR, 0)
                .add(Attributes.MOVEMENT_SPEED, 0)
                .add(Attributes.ATTACK_DAMAGE, 3)
                .add(Attributes.FOLLOW_RANGE, 16);
    }

    // ======================== AI ========================

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new RandomLookAroundGoal(this));
    }

    // ======================== 受伤/免疫 ========================

    /**
     * 无名 NPC 免疫以下伤害类型：
     * <ul>
     *   <li>火焰（站火、掉岩浆）</li>
     *   <li>玩家直接攻击（免疫来自 Player 的直接伤害）</li>
     * </ul>
     *
     * @param source 伤害来源
     * @param amount 伤害值
     * @return 是否受到伤害
     */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.IN_FIRE)) {
            return false;
        }
        if (source.getDirectEntity() instanceof Player) {
            return false;
        }
        return super.hurt(source, amount);
    }

    // ======================== 交互 ========================

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        // 防止重复触发
        if (this.hasInteracted) {
            return InteractionResult.PASS;
        }
        this.hasInteracted = true;

        // 播放 "say" 动画 + 暗影说话音效
        this.setAnimation("say");
        this.level().playSound(null, this.blockPosition(),
                PDSounds.SHADOW_0.get(), SoundSource.NEUTRAL, 0.6f, 0.8f);

        ServerLevel serverLevel = (ServerLevel) level();

        // 显示角色对话（简化版，仅播放第一次见面的对话）
        player.sendSystemMessage(Component.literal("???：..."));
        serverLevel.getServer().tell(new net.minecraft.server.TickTask(
                serverLevel.getServer().getTickCount() + 40,
                () -> player.sendSystemMessage(Component.literal("???：你好...已经..很久没人来到这里了"))
        ));
        serverLevel.getServer().tell(new net.minecraft.server.TickTask(
                serverLevel.getServer().getTickCount() + 80,
                () -> player.sendSystemMessage(Component.literal("???：请不要感到害怕，我不像外面的暗影生物一样。"))
        ));
        serverLevel.getServer().tell(new net.minecraft.server.TickTask(
                serverLevel.getServer().getTickCount() + 120,
                () -> player.sendSystemMessage(Component.literal("???：你很奇怪我和我为什么在这里？"))
        ));
        serverLevel.getServer().tell(new net.minecraft.server.TickTask(
                serverLevel.getServer().getTickCount() + 200,
                () -> player.sendSystemMessage(Component.literal("???：很抱歉，这也是我们第一次见面...希望你能理解。"))
        ));
        serverLevel.getServer().tell(new net.minecraft.server.TickTask(
                serverLevel.getServer().getTickCount() + 240,
                () -> player.sendSystemMessage(Component.literal("???：..."))
        ));
        serverLevel.getServer().tell(new net.minecraft.server.TickTask(
                serverLevel.getServer().getTickCount() + 360,
                () -> {
                    // 给予玩家金块作为见面礼
                    ItemStack gift = new ItemStack(Items.GOLD_BLOCK);
                    ItemEntity itemEntity = new ItemEntity(serverLevel,
                            this.getX(), this.getY(), this.getZ(), gift);
                    itemEntity.setPickUpDelay(20);
                    serverLevel.addFreshEntity(itemEntity);

                    player.sendSystemMessage(Component.literal("无名：这些给你，作为初次的见面礼。"));
                }
        ));
        serverLevel.getServer().tell(new net.minecraft.server.TickTask(
                serverLevel.getServer().getTickCount() + 520,
                () -> player.sendSystemMessage(Component.literal("§a新的主线进度已解锁"))
        ));

        return InteractionResult.SUCCESS;
    }

    // ======================== 尺寸刷新 ========================

    @Override
    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();
    }

    // ======================== 死亡处理 ========================

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience(this.getLastHurtByMob());
        }
    }

    // ======================== GeckoLib 动画 ========================

    /**
     * 移动状态动画控制器
     * <p>
     * 无名 NPC 静止不动，始终只播放 "idle" 循环动画。
     *
     * @param state 动画状态
     * @return 播放状态
     */
    private PlayState movementPredicate(AnimationState<ShadowNpc0Entity> state) {
        if (this.getSyncedAnimation().equals("empty")) {
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
    }
}
