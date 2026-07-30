package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.List;
import java.util.function.Supplier;

/**
 * 卡莱的黄金预言 —— 卡勒占卜卡牌（calle_card_0~9）。
 * <p>
 * 还原自原版 CalleCard0~9Item + CalleCardXPr0Procedure：
 * <ul>
 *   <li>0 号：占卜牌，使用后随机抽取一张预言卡牌并给予玩家；</li>
 *   <li>1 号『墓园』：对周围 5*5 范围内的非玩家生物造成 100 点魔法伤害；</li>
 *   <li>3 号『疾行』：速度 III + 跳跃提升 I，持续 120 秒；</li>
 *   <li>4 号『守护』：伤害吸收 V（20 点）+ 抗性提升 I，持续 120 秒；</li>
 *   <li>5 号『对立』：当前生命值与已损失生命值互换（满血使用会直接死亡并授予隐藏成就）；</li>
 *   <li>6 号『罪恶』：引燃 19*19 范围内亡灵生物 15 秒并造成 20 点火焰伤害，僵尸村民直接转化为村民；</li>
 *   <li>7 号『平衡』：部分带等级的药水效果等级翻倍、持续时间减半；</li>
 *   <li>9 号『混乱』：7*7 范围内非玩家生物获得暗影混乱与缓慢 IV，持续 10 秒。</li>
 * </ul>
 * 移植说明：
 * <ul>
 *   <li>2 号『执剑』→ flareup_buff；8 号『圣杯』→ grail_buff；</li>
 *   <li>3 号附带 rapid_reaction（高速反射，瞬身术 CD -20%）；</li>
 *   <li>使用后全屏物品展示：S2C {@link com.pasterdream.pasterdreammod.network.ItemActivationPayload}
 *       → 客户端 {@code GameRenderer#displayItemActivation}（卡 1–9，与原版一致；0 号无）；</li>
 *   <li>1 号原版对范围内所有实体（含掉落物）调用 hurt，此处等价限定为非玩家生物实体；</li>
 *   <li>6 号熔金杖投射物自上而下坠落为纯视觉（伤害仍由延迟 hurt 结算）；</li>
 *   <li>原版 3/4/5/7 号仅调用服务端 addParticle（实际无可见效果），故不补发粒子。</li>
 * </ul>
 */
public class CalleCardItem extends Item {

    /** 抽卡音效 pasterdream:card0 */
    private static final SoundEvent CARD0_SOUND =
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("pasterdream", "card0"));

    /** 0 号占卜牌可抽取 1..9 全卡 */
    private static final int[] DRAW_POOL = {1, 2, 3, 4, 5, 6, 7, 8, 9};

    /** 各编号预言卡牌的名称（下标 = 卡牌编号，0 号占位为空） */
    private static final String[] CARD_NAMES = {
            "", "墓园", "执剑", "疾行", "守护", "对立", "罪恶", "均衡", "圣杯", "混乱"
    };

    /** 抽取结果编号 → 对应卡牌物品（延迟解析，避免静态初始化顺序问题） */
    private static Supplier<Item> cardById(int id) {
        return switch (id) {
            case 1 -> () -> com.pasterdream.pasterdreammod.registry.PDItems.CALLE_CARD_1.get();
            case 2 -> () -> com.pasterdream.pasterdreammod.registry.PDItems.CALLE_CARD_2.get();
            case 3 -> () -> com.pasterdream.pasterdreammod.registry.PDItems.CALLE_CARD_3.get();
            case 4 -> () -> com.pasterdream.pasterdreammod.registry.PDItems.CALLE_CARD_4.get();
            case 5 -> () -> com.pasterdream.pasterdreammod.registry.PDItems.CALLE_CARD_5.get();
            case 6 -> () -> com.pasterdream.pasterdreammod.registry.PDItems.CALLE_CARD_6.get();
            case 7 -> () -> com.pasterdream.pasterdreammod.registry.PDItems.CALLE_CARD_7.get();
            case 8 -> () -> com.pasterdream.pasterdreammod.registry.PDItems.CALLE_CARD_8.get();
            case 9 -> () -> com.pasterdream.pasterdreammod.registry.PDItems.CALLE_CARD_9.get();
            default -> () -> Item.byBlock(net.minecraft.world.level.block.Blocks.AIR);
        };
    }

    /** 7 号『平衡』会翻倍处理的原版效果列表（与原版 CalleCard7Pr0Procedure 一致） */
    private static final List<Holder<MobEffect>> BALANCE_EFFECTS = List.of(
            MobEffects.MOVEMENT_SPEED, MobEffects.MOVEMENT_SLOWDOWN,
            MobEffects.DIG_SPEED, MobEffects.DIG_SLOWDOWN,
            MobEffects.DAMAGE_BOOST, MobEffects.JUMP,
            MobEffects.REGENERATION, MobEffects.HUNGER,
            MobEffects.POISON, MobEffects.WITHER,
            MobEffects.DAMAGE_RESISTANCE);

    /** 卡牌编号（0~9，对应 calle_card_N） */
    private final int cardId;

    /** 悬浮提示文案（原版硬编码文案） */
    private final String[] tooltipLines;

    /**
     * 构造卡莱卡牌
     *
     * @param cardId       卡牌编号（0~9）
     * @param tooltipLines 原版悬浮提示文案（可为空）
     */
    public CalleCardItem(int cardId, String... tooltipLines) {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
        this.cardId = cardId;
        this.tooltipLines = tooltipLines;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        for (String line : tooltipLines) {
            tooltip.add(Component.literal(line));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.success(stack);
        }
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        // 除 0 号外，原版所有卡牌使用时都会挥手 + 全屏展示
        if (cardId != 0) {
            player.swing(hand, true);
            com.pasterdream.pasterdreammod.network.PDNetwork.sendItemActivation(player, stack);
        }

        switch (cardId) {
            case 0 -> useDivination(serverLevel, player, stack, x, y, z);
            case 1 -> useGraveyard(serverLevel, player, stack, x, y, z);
            case 2 -> useFlareup(serverLevel, player, stack, x, y, z);
            case 3 -> useSwift(serverLevel, player, stack, x, y, z);
            case 4 -> useGuard(serverLevel, player, stack, x, y, z);
            case 5 -> useOpposition(serverLevel, player, stack, x, y, z);
            case 6 -> useSin(serverLevel, player, stack, x, y, z);
            case 7 -> useBalance(serverLevel, player, stack, x, y, z);
            case 8 -> useGrail(serverLevel, player, stack, x, y, z);
            case 9 -> useChaos(serverLevel, player, stack, x, y, z);
            default -> { }
        }
        return InteractionResultHolder.success(stack);
    }

    // ==================== 0 号：占卜抽卡 ====================

    /** 0 号占卜牌：随机抽取一张预言卡牌，播放卡牌音效并延迟 2 tick 发放 */
    private static void useDivination(ServerLevel level, Player player, ItemStack stack, double x, double y, double z) {
        int drawn = DRAW_POOL[level.getRandom().nextInt(DRAW_POOL.length)];
        level.sendParticles((SimpleParticleType) PDParticles.CALLE_PARTICLE.particleType(),
                x, y, z, 128, 1, 2, 1, 0.1);
        level.playSound(null, BlockPos.containing(x, y, z), CARD0_SOUND, SoundSource.PLAYERS, 1.5f, 1.0f);
        player.displayClientMessage(Component.literal("§6你手中的卡莱占卜牌已显示所预言的一面"), false);
        stack.shrink(1);
        String name = CARD_NAMES[drawn];
        player.displayClientMessage(Component.literal("§6预言卡牌为：『" + name + "』"), false);
        player.displayClientMessage(Component.literal("§6§l『" + name + "』"), true);
        Supplier<Item> reward = cardById(drawn);
        schedule(level, 2, () -> {
            if (player.isAlive()) {
                ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(reward.get()));
            }
        });
    }

    // ==================== 1 号『墓园』 ====================

    /** 1 号『墓园』：5*5 范围内非玩家生物受到坠落音波与 100 点魔法伤害 */
    private static void useGraveyard(ServerLevel level, Player player, ItemStack stack, double x, double y, double z) {
        stack.shrink(1);
        Vec3 center = new Vec3(x, y, z);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class,
                new AABB(center, center).inflate(6 / 2.0));
        for (LivingEntity target : targets) {
            if (target instanceof Player) {
                continue;
            }
            // 纯视觉：音波投射物自上而下坠落（伤害由下方延迟 hurt 结算）
            var projectile = new com.pasterdream.pasterdreammod.entity.projectile.SquealWaveProjectileEntity(
                    PDEntities.SQUEAL_WAVE_PROJECTILE.get(), level);
            projectile.setPos(target.getX(), target.getY() + 6, target.getZ());
            projectile.setDeltaMovement(0, -1, 0);
            projectile.setSilent(true);
            level.addFreshEntity(projectile);
            schedule(level, 3, () -> {
                if (target.isAlive()) {
                    target.hurt(level.damageSources().magic(), 100);
                    level.sendParticles(ParticleTypes.SOUL,
                            target.getX(), target.getY(), target.getZ(), 6, 0.5, 0.5, 0.5, 2);
                }
            });
        }
        level.sendParticles((SimpleParticleType) PDParticles.CALLE_PARTICLE.particleType(),
                x, y, z, 64, 2, 1.5, 2, 0.1);
        level.sendParticles(ParticleTypes.LARGE_SMOKE, x, y, z, 128, 3, 0, 3, 0.25);
        playTotemSound(level, x, y, z);
    }

    // ==================== 2 号『执剑』 ====================

    /** 2 号『执剑』：怒气爆发 flareup_buff 120 秒 */
    private static void useFlareup(ServerLevel level, Player player, ItemStack stack, double x, double y, double z) {
        stack.shrink(1);
        playTotemSound(level, x, y, z);
        level.sendParticles((SimpleParticleType) PDParticles.CALLE_PARTICLE.particleType(),
                x, y, z, 64, 2, 1.5, 2, 0.1);
        player.addEffect(new MobEffectInstance(PDEffects.FLAREUP_BUFF.holder(), 2400, 0));
    }

    // ==================== 3 号『疾行』 ====================

    /** 3 号『疾行』：速度 III + 跳跃提升 I + rapid_reaction，持续 120 秒 */
    private static void useSwift(ServerLevel level, Player player, ItemStack stack, double x, double y, double z) {
        stack.shrink(1);
        playTotemSound(level, x, y, z);
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 2400, 2));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, 2400, 0));
        player.addEffect(new MobEffectInstance(PDEffects.RAPID_REACTION.holder(), 2400, 0));
    }

    // ==================== 4 号『守护』 ====================

    /** 4 号『守护』：伤害吸收 V（20 点）+ 抗性提升 I，持续 120 秒 */
    private static void useGuard(ServerLevel level, Player player, ItemStack stack, double x, double y, double z) {
        stack.shrink(1);
        playTotemSound(level, x, y, z);
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 4));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2400, 0));
    }

    // ==================== 5 号『对立』 ====================

    /** 5 号『对立』：当前生命值与已损失生命值互换；满血使用会直接死亡并授予隐藏成就 achievement_special_0 */
    private static void useOpposition(ServerLevel level, Player player, ItemStack stack, double x, double y, double z) {
        stack.shrink(1);
        playTotemSound(level, x, y, z);
        float max = player.getMaxHealth();
        float current = player.getHealth();
        if (max == current) {
            // 满血互换 = 生命值归零：以虚空伤害直接结算，并授予隐藏成就
            player.hurt(level.damageSources().fellOutOfWorld(), max);
            if (player instanceof ServerPlayer serverPlayer) {
                grantAdvancement(serverPlayer, "achievement_special_0");
            }
        }
        player.setHealth(max - current);
    }

    // ==================== 6 号『罪恶』 ====================

    /** 6 号『罪恶』：引燃 19*19 范围内亡灵生物 15 秒并造成 20 点火焰伤害；僵尸村民直接转化为村民 */
    private static void useSin(ServerLevel level, Player player, ItemStack stack, double x, double y, double z) {
        stack.shrink(1);
        Vec3 center = new Vec3(x, y, z);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class,
                new AABB(center, center).inflate(19 / 2.0));
        for (LivingEntity target : targets) {
            if (target instanceof ZombieVillager) {
                // 僵尸村民：不受伤害，延迟 3 tick 转化为村民
                schedule(level, 3, () -> {
                    if (!target.isAlive()) {
                        return;
                    }
                    level.sendParticles(ParticleTypes.HEART,
                            target.getX(), target.getY(), target.getZ(), 6, 0.5, 0.5, 0.5, 2);
                    double tx = target.getX();
                    double ty = target.getY();
                    double tz = target.getZ();
                    target.discard();
                    var villager = EntityType.VILLAGER.spawn(level, BlockPos.containing(tx, ty, tz), MobSpawnType.MOB_SUMMONED);
                    if (villager != null) {
                        villager.setYRot(level.getRandom().nextFloat() * 360.0f);
                    }
                });
            } else if (target.getType().is(EntityTypeTags.UNDEAD)) {
                // 亡灵生物：熔金杖投射物坠落 VFX + 引燃 15 秒 + 延迟 3 tick 火焰伤害 20
                var projectile = new com.pasterdream.pasterdreammod.entity.projectile.MoltengoldWandProjectileEntity(
                        PDEntities.MOLTENGOLD_WAND_PROJECTILE.get(), level);
                projectile.setPos(target.getX(), target.getY() + 7, target.getZ());
                projectile.shoot(0, -1, 0, 1, 0);
                projectile.setSilent(true);
                projectile.setBaseDamage(5);
                // 原版 pierceLevel=1；1.21 基类 setPierceCount 为 protected，坠落单目标可省略
                projectile.setOwner(player);
                level.addFreshEntity(projectile);
                target.igniteForSeconds(15);
                schedule(level, 3, () -> {
                    if (target.isAlive()) {
                        target.hurt(level.damageSources().inFire(), 20);
                        level.sendParticles(ParticleTypes.LANDING_LAVA,
                                target.getX(), target.getY(), target.getZ(), 6, 0.5, 0.5, 0.5, 2);
                    }
                });
            }
        }
        level.sendParticles((SimpleParticleType) PDParticles.CALLE_PARTICLE.particleType(),
                x, y, z, 64, 2, 1.5, 2, 0.1);
        level.sendParticles(ParticleTypes.FIREWORK, x, y, z, 256, 9, 0.5, 9, 0.25);
        playTotemSound(level, x, y, z);
    }

    // ==================== 7 号『平衡』 ====================

    /** 7 号『平衡』：列表内已有效果等级翻倍（amp*2+1）、持续时间减半 */
    private static void useBalance(ServerLevel level, Player player, ItemStack stack, double x, double y, double z) {
        stack.shrink(1);
        playTotemSound(level, x, y, z);
        for (Holder<MobEffect> effect : BALANCE_EFFECTS) {
            MobEffectInstance current = player.getEffect(effect);
            if (current != null) {
                player.addEffect(new MobEffectInstance(effect,
                        current.getDuration() / 2, current.getAmplifier() * 2 + 1));
            }
        }
    }

    // ==================== 8 号『圣杯』 ====================

    /** 8 号『圣杯』：grail_buff 120 秒 */
    private static void useGrail(ServerLevel level, Player player, ItemStack stack, double x, double y, double z) {
        stack.shrink(1);
        playTotemSound(level, x, y, z);
        player.addEffect(new MobEffectInstance(PDEffects.GRAIL_BUFF.holder(), 2400, 0));
    }

    // ==================== 9 号『混乱』 ====================

    /** 9 号『混乱』：7*7 范围内非玩家生物获得暗影混乱与缓慢 IV，持续 10 秒 */
    private static void useChaos(ServerLevel level, Player player, ItemStack stack, double x, double y, double z) {
        stack.shrink(1);
        Vec3 center = new Vec3(x, y, z);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class,
                new AABB(center, center).inflate(7 / 2.0));
        for (LivingEntity target : targets) {
            if (target instanceof Player) {
                continue;
            }
            target.addEffect(new MobEffectInstance(PDEffects.CONFUSION_BUFF.holder(), 200, 0));
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 3));
            level.sendParticles(ParticleTypes.ASH,
                    target.getX(), target.getY(), target.getZ(), 6, 0.5, 0.5, 0.5, 2);
        }
        level.sendParticles((SimpleParticleType) PDParticles.CALLE_PARTICLE.particleType(),
                x, y, z, 64, 2, 1.5, 2, 0.1);
        level.sendParticles(ParticleTypes.ASH, x, y, z, 128, 3, 0.5, 3, 0.25);
        playTotemSound(level, x, y, z);
    }

    // ==================== 公共工具 ====================

    /** 播放不死图腾音效（原版所有卡牌共用） */
    private static void playTotemSound(ServerLevel level, double x, double y, double z) {
        level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.TOTEM_USE, SoundSource.NEUTRAL, 1.0f, 1.0f);
    }

    /** 延迟指定 tick 后在服务端主线程执行任务（等价原版 MCreator 的 queueServerWork） */
    private static void schedule(ServerLevel level, int delay, Runnable task) {
        ServerScheduler.schedule(delay, task);
    }

    /** 授予指定 pasterdream 命名空间成就（成就不存在时静默跳过，保证数据包缺失时不崩溃） */
    private static void grantAdvancement(ServerPlayer player, String path) {
        AdvancementHolder advancement = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath("pasterdream", path));
        if (advancement == null) {
            return;
        }
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        if (!progress.isDone()) {
            for (String criterion : progress.getRemainingCriteria()) {
                player.getAdvancements().award(advancement, criterion);
            }
        }
    }
}
