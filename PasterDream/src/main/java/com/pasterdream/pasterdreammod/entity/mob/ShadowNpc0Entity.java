package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.entity.base.GeckoLibMobEntity;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.util.ServerScheduler;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.*;

import java.util.Comparator;

/**
 * 无名（shadow_npc_0）—— 暗影地牢 NPC
 * <p>
 * 多阶段对话按玩家成就 {@code achievement_shadow_npc_0..5} 分支，
 * 完整还原原版 {@code ShadowNpc0Pr0Procedure}。
 * 对话进行中用同步字段 {@code switch} 锁，结束后解锁以便下阶段再交互；
 * 成就缺失时降级为 debug 日志（不抛异常）。
 */
public class ShadowNpc0Entity extends GeckoLibMobEntity {

    /** 对话进行中锁（原版 persistent "switch"） */
    private static final EntityDataAccessor<Boolean> DATA_SWITCH =
            SynchedEntityData.defineId(ShadowNpc0Entity.class, EntityDataSerializers.BOOLEAN);

    private static final String ADV_NPC_0 = "achievement_shadow_npc_0";
    private static final String ADV_NPC_1 = "achievement_shadow_npc_1";
    private static final String ADV_NPC_2 = "achievement_shadow_npc_2";
    private static final String ADV_NPC_3 = "achievement_shadow_npc_3";
    private static final String ADV_NPC_4 = "achievement_shadow_npc_4";
    private static final String ADV_NPC_5 = "achievement_shadow_npc_5";

    public ShadowNpc0Entity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.xpReward = 0;
    }

    @Override
    protected String getDefaultTexture() {
        return "shadow_npc_0";
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SWITCH, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("switch", this.entityData.get(DATA_SWITCH));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("switch")) {
            this.entityData.set(DATA_SWITCH, compound.getBoolean("switch"));
        } else if (compound.contains("HasInteracted")) {
            // 兼容旧存档字段
            this.entityData.set(DATA_SWITCH, compound.getBoolean("HasInteracted"));
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.ARMOR, 0)
                .add(Attributes.MOVEMENT_SPEED, 0)
                .add(Attributes.ATTACK_DAMAGE, 3)
                .add(Attributes.FOLLOW_RANGE, 16);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new RandomLookAroundGoal(this));
    }

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

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.entityData.get(DATA_SWITCH)) {
            return InteractionResult.PASS;
        }
        if (level().isClientSide()) {
            return InteractionResult.sidedSuccess(true);
        }
        if (!(player instanceof ServerPlayer sourcePlayer)) {
            return InteractionResult.PASS;
        }

        boolean done0 = isAdvDone(sourcePlayer, ADV_NPC_0);
        boolean done1 = isAdvDone(sourcePlayer, ADV_NPC_1);
        boolean done2 = isAdvDone(sourcePlayer, ADV_NPC_2);
        boolean done3 = isAdvDone(sourcePlayer, ADV_NPC_3);
        boolean done4 = isAdvDone(sourcePlayer, ADV_NPC_4);
        boolean done5 = isAdvDone(sourcePlayer, ADV_NPC_5);

        // 原版优先级：先匹配最早未完成分支
        if (!done0) {
            startStage0(sourcePlayer);
            return InteractionResult.sidedSuccess(false);
        }
        if (done0 && !done1) {
            startStage1(sourcePlayer);
            return InteractionResult.sidedSuccess(false);
        }
        // 原版： (npc1 && !npc2) || (!npc3 && npc2)
        if ((done1 && !done2) || (!done3 && done2)) {
            startStage2(sourcePlayer);
            return InteractionResult.sidedSuccess(false);
        }
        if (done3 && !done4) {
            startStage4(sourcePlayer);
            return InteractionResult.sidedSuccess(false);
        }
        if (done4 && !done5) {
            startStage5(sourcePlayer);
            return InteractionResult.sidedSuccess(false);
        }

        return InteractionResult.PASS;
    }

    private void beginDialogue() {
        this.entityData.set(DATA_SWITCH, true);
        this.setAnimation("say");
        this.level().playSound(null, this.blockPosition(),
                PDSounds.SHADOW_0.get(), SoundSource.NEUTRAL, 0.6f, 0.8f);
    }

    private void endDialogue() {
        this.entityData.set(DATA_SWITCH, false);
    }

    // ======================== 各阶段对话 ========================

    private void startStage0(ServerPlayer source) {
        beginDialogue();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        ServerLevel level = (ServerLevel) this.level();

        forEachNearbyPlayer(x, y, z, p -> {
            msg(p, "???：...");
            scheduleMsg(p, 40, "???：你好...已经..很久没人来到这里了");
            scheduleMsg(p, 80, "???：请不要感到害怕，我不像外面的暗影生物一样。");
            scheduleMsg(p, 120, "???：你很好奇我和我为什么在这里？");
            scheduleMsg(p, 160, "???：如果要谈起关于我的事...经历了太多，我也不想回忆。");
            scheduleMsg(p, 200, "???：很抱歉，这也是我们第一次见面...希望你能理解。");
            scheduleMsg(p, 240, "???：...");
            scheduleMsg(p, 280, "???：我的名字？...时间过去太久，我也失去了曾经的“自己”。");
            scheduleMsg(p, 320, "???：就叫我“无名”吧。");
            ServerScheduler.schedule(360, () -> {
                if (!p.isAlive()) return;
                msg(p, "无名：这些给你，作为初次的见面礼。");
                ItemEntity gift = new ItemEntity(level, x, y, z, new ItemStack(Blocks.GOLD_BLOCK));
                gift.setPickUpDelay(20);
                level.addFreshEntity(gift);
            });
            scheduleMsg(p, 400,
                    "无名：还有，这里很危险。 每次进入这个暗影地牢，它都会产生一些变化，是更多的威胁还是更多的宝藏...我也不得而知。");
            scheduleMsg(p, 440, "无名：从这里下去就是出口了");
            scheduleMsg(p, 480, "无名：...");
            ServerScheduler.schedule(520, () -> {
                if (!p.isAlive()) return;
                msg(p, "无名：最后，请不要冒着生命风险探索这里。 你所看到的鬼魂...是曾经的...");
                awardAdvancement(p, ADV_NPC_0);
            });
            ServerScheduler.schedule(560, () -> {
                if (!p.isAlive()) return;
                msg(p, "§a新的主线进度已解锁");
                endDialogue();
            });
        });
    }

    private void startStage1(ServerPlayer source) {
        beginDialogue();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();

        forEachNearbyPlayer(x, y, z, p -> {
            msg(p, "无名：...");
            scheduleMsg(p, 40, "无名：我们见过一面，对吧");
            scheduleMsg(p, 80, "无名：又一次来到这里...你是为了什么");
            scheduleMsg(p, 120, "无名：...");
            scheduleMsg(p, 160, "无名：我为什么在这里？ 因为我无法逃离，也不再想尝试逃离这里了。");
            scheduleMsg(p, 200, "无名：正如你所见，这一层，并非被阴影全部覆盖。");
            scheduleMsg(p, 240, "无名：因为这是我仅存的回忆，我曾看到过的世界。");
            scheduleMsg(p, 280, "无名：我脑海中的想法...让这里的部分成为了“现实”，这很奇妙 对吧。");
            scheduleMsg(p, 320, "无名：...");
            scheduleMsg(p, 360, "无名：想帮我出去？");
            scheduleMsg(p, 400, "无名：钥匙就在这里，出口也就在这一层的下方，你明白的。");
            scheduleMsg(p, 440, "无名：我在这里...也是因为我受到一个诅咒，守护承诺的诅咒。");
            scheduleMsg(p, 480, "无名：但我并不反感... 这事出有因。");
            scheduleMsg(p, 520, "无名：离开这里吧...一直待在这里只会侵蚀你的精神...");
            scheduleMsg(p, 560, "无名：在你还可以回家的时候...");
            ServerScheduler.schedule(600, () -> {
                if (!p.isAlive()) return;
                msg(p, "§a新的主线进度已解锁");
                awardAdvancement(p, ADV_NPC_1);
                endDialogue();
            });
        });
    }

    private void startStage2(ServerPlayer source) {
        beginDialogue();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        ServerLevel level = (ServerLevel) this.level();

        forEachNearbyPlayer(x, y, z, p -> {
            msg(p, "无名：你为何如此执着...");
            scheduleMsg(p, 40, "无名：请不要再尝试继续向下了，这会危及你的性命。");
            scheduleMsg(p, 80,
                    "无名：这种渴求力量的想法只会让暗影更容易控制你的精神...在你放松下来的瞬间。");
            scheduleMsg(p, 120, "无名：我不能让你继续留在这里了...请离开吧。");
            ServerScheduler.schedule(150, () -> {
                if (!p.isAlive()) return;
                msg(p, "§a新的主线进度已解锁");
                awardAdvancement(p, ADV_NPC_2);
                level.playSound(null, BlockPos.containing(x, y, z),
                        PDSounds.PORTAL.get(), SoundSource.NEUTRAL, 1, 1);
            });
            ServerScheduler.schedule(160, () -> {
                if (!p.isAlive()) return;
                if (Boolean.TRUE.equals(PDCommonConfig.SHADOW_NPC_THIRD_DIALOGUE_AFTER_TP_PLAYER_BACK_TO_OVERWORLD.get())) {
                    teleportPlayerToOverworldSpawn(p);
                }
                endDialogue();
            });
        });
    }

    private void startStage4(ServerPlayer source) {
        beginDialogue();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();

        forEachNearbyPlayer(x, y, z, p -> {
            msg(p, "无名：你已经...见到它们出现在了你的世界？");
            scheduleMsg(p, 40, "无名：蛰伏于黑暗，在没有光亮的地方，它们伺机而动...");
            scheduleMsg(p, 80, "无名：影子...黑暗...它们是这些的化身，还是灯影之下的另一种存在？");
            scheduleMsg(p, 120, "无名：它们是实际存在的物质？还只是仅存于人们思想的梦...");
            scheduleMsg(p, 160, "无名：即使身临此地的我也不明确这一切的答案...");
            scheduleMsg(p, 200, "无名：哈...抱歉，我唠叨了。问我曾阻止你的原因？");
            scheduleMsg(p, 240, "无名：可没几个人能见到这些怪物之后还活着的，但现在看来你与常人不同。");
            scheduleMsg(p, 280, "无名：我能感受到...你可以运用那份力量，来自你的精神。");
            scheduleMsg(p, 320, "无名：如果我没猜错，你是主动来到这里的吧，而不是被那些会动的影子拉进来...");
            scheduleMsg(p, 360, "无名：...");
            scheduleMsg(p, 400, "无名：其实...这些影子会前往你的世界也许和我有关，就在这层地牢之下...");
            scheduleMsg(p, 440, "无名：不过...我需要去准备一下，也请把你的想法告诉我。");
            scheduleMsg(p, 480, "无名：在我们下次见面...");
            ServerScheduler.schedule(500, () -> {
                if (!p.isAlive()) return;
                msg(p, "§a新的主线进度已解锁");
                awardAdvancement(p, ADV_NPC_4);
                endDialogue();
            });
        });
    }

    private void startStage5(ServerPlayer source) {
        beginDialogue();
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();

        forEachNearbyPlayer(x, y, z, p -> {
            msg(p, "无名：看来你准备好了...");
            scheduleMsg(p, 40,
                    "无名：在这层下面，其实是一位已故之人的坟墓，我的存在也是为了信守那份承诺。");
            scheduleMsg(p, 80,
                    "无名：“如果我失败了，就请把我埋葬在这里，永远” 这是他所留下的遗言。");
            scheduleMsg(p, 120,
                    "无名：他的尸体被放在暮影长床上，因为转移灵魂的实验，那具肉体化为了流淌的阴影，充斥满那个房间。");
            scheduleMsg(p, 160,
                    "无名：“只有真正的失去，才能体会到存在的意义” 在我的梦里，这是他的声音。");
            scheduleMsg(p, 200,
                    "无名：如果你执意要斗争下去...那么就再次触摸下方的门扉吧，如果你得到了认可，门自然会打开。");
            scheduleMsg(p, 240, "无名：去轻抚暮影长床，做出你内心的第一选择，不计结果。");
            ServerScheduler.schedule(260, () -> {
                if (!p.isAlive()) return;
                msg(p, "§a前往下层打开触摸大门的“眼睛”");
                awardAdvancement(p, ADV_NPC_5);
                endDialogue();
            });
        });
    }

    // ======================== 工具 ========================

    @FunctionalInterface
    private interface PlayerAction {
        void accept(ServerPlayer player);
    }

    /**
     * 原版 16 格内全体玩家同步收消息。
     * <p>
     * 用 {@link ServerLevel#players()} 而非 {@code getEntitiesOfClass}：
     * 后者依赖实体 section，同 tick 跨维/传送后可能暂查不到玩家，
     * 会导致对话 schedule 整段未挂上（VERIFY main-flow Stage0–5 复现）。
     */
    private void forEachNearbyPlayer(double x, double y, double z, PlayerAction action) {
        if (!(this.level() instanceof ServerLevel sl)) {
            return;
        }
        Vec3 center = new Vec3(x, y, z);
        double r2 = 16.0 * 16.0;
        sl.players().stream()
                .filter(p -> p.isAlive() && p.distanceToSqr(center) <= r2)
                .sorted(Comparator.comparingDouble(p -> p.distanceToSqr(center)))
                .forEach(action::accept);
    }

    private static void scheduleMsg(ServerPlayer player, int delay, String text) {
        ServerScheduler.schedule(delay, () -> {
            if (player.isAlive()) {
                msg(player, text);
            }
        });
    }

    private static void msg(Player player, String text) {
        if (!player.level().isClientSide()) {
            player.displayClientMessage(Component.literal(text), false);
        }
    }

    private static boolean isAdvDone(ServerPlayer player, String path) {
        AdvancementHolder holder = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, path));
        if (holder == null) {
            return false;
        }
        return player.getAdvancements().getOrStartProgress(holder).isDone();
    }

    private static void awardAdvancement(ServerPlayer player, String path) {
        AdvancementHolder holder = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, path));
        if (holder == null) {
            PasterDreamMod.LOGGER.debug("[ShadowNpc0] 成就 {} 未注册，跳过授予", path);
            return;
        }
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
        if (!progress.isDone()) {
            for (String criteria : progress.getRemainingCriteria()) {
                player.getAdvancements().award(holder, criteria);
            }
        }
    }

    /**
     * 第三段对话后的主世界回传（原版 changeDimension + 重生点/世界出生点定位）。
     */
    private static void teleportPlayerToOverworldSpawn(ServerPlayer player) {
        ResourceKey<Level> overworld = Level.OVERWORLD;
        if (player.level().dimension() == overworld) {
            return;
        }
        ServerLevel next = player.server.getLevel(overworld);
        if (next == null) {
            return;
        }
        player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 0));
        player.teleportTo(next, player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
        player.connection.send(new ClientboundPlayerAbilitiesPacket(player.getAbilities()));
        for (MobEffectInstance effect : player.getActiveEffects()) {
            player.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), effect, false));
        }
        player.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));

        BlockPos spawnPos;
        if (player.getRespawnDimension().equals(overworld) && player.getRespawnPosition() != null) {
            spawnPos = player.getRespawnPosition();
        } else {
            spawnPos = next.getSharedSpawnPos();
        }
        double sx = spawnPos.getX() + 0.5;
        double sy = spawnPos.getY();
        double sz = spawnPos.getZ() + 0.5;
        player.teleportTo(sx, sy, sz);
        player.connection.teleport(sx, sy, sz, player.getYRot(), player.getXRot());
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience(this.getLastHurtByMob());
        }
    }

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
