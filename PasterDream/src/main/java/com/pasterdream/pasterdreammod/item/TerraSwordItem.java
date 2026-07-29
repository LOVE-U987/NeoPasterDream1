package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import com.pasterdream.pasterdreammod.entity.mob.TerraswordWaveEntity;
import com.pasterdream.pasterdreammod.registry.PDAttributes;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.util.PasterItemData;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 大地之刃 (terra_sword)
 * <p>
 * 还原自原版 TerraSwordItem + TerraSwordPr0/Pr1Procedure（战技：泰拉剑技）：
 * <ul>
 *   <li>剑属性与既有 ItemAPI 注册逐项一致（耐久 1561、攻击 8、攻速 -2.4）</li>
 *   <li>右键消耗 0.3 融梦能量开启战技（附魔台音效）：写入 paster_atk =
 *       当前攻击力 + 锋利等级×0.5；佩戴大地护符时 paster_atk ×1.3、返还 0.2 能量、
 *       战技冷却 40×冷却属性，否则冷却 100×冷却属性（原版均取 getBaseValue）</li>
 *   <li>开启后接下来 3 次挥剑各挥出一道大地之刃剑气（初速 = 视线×2，音调逐段升高，
 *       段位动画 "1"/"2"/"3"），第三段剑气的属性攻击力加成翻倍</li>
 *   <li>通用配置 ban terra sword 开启时禁用战技（与原版一致）</li>
 * </ul>
 */
public class TerraSwordItem extends SwordItem {

    /** 与既有 ItemAPI 注册等值的 tier：耐久 1561 / 挖速 2 / 伤害加成 8 / 附魔 5 */
    private static final net.neoforged.neoforge.common.SimpleTier TIER =
            new net.neoforged.neoforge.common.SimpleTier(
                    BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1561, 2.0f, 8.0f, 5, () -> Ingredient.EMPTY);

    public TerraSwordItem(Properties properties) {
        // 攻击伤害由 tier 承载（伤害参数 0，与 ItemAPI 方案一致）
        super(TIER, properties.attributes(SwordItem.createAttributes(TIER, 0, -2.4f)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = super.use(level, player, hand);
        // 原版 TerraSwordPr1Procedure：右键开启泰拉剑技
        startSkill(level, player, result.getObject());
        return result;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        boolean result = super.onEntitySwing(stack, entity, hand);
        // 原版 TerraSwordPr0Procedure：每次挥剑结算一段剑气
        performSkillWave(entity.level(), entity, stack);
        return result;
    }

    /**
     * 原版 TerraSwordPr1Procedure：开启战技
     * <p>
     * 消耗 0.3 融梦能量（不足则提示"融梦能量不足"），写入 paster_atk/skill_multiplier，
     * skill 置 1，播放附魔台音效；按大地护符佩戴与否施加 40/100 × 冷却属性的战技冷却。
     *
     * @param level  世界
     * @param player 玩家
     * @param stack  大地之刃物品栈
     */
    private static void startSkill(Level level, Player player, ItemStack stack) {
        if (level.isClientSide() || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        // 融梦能量消耗已剥离至附属 mod，主模组不再拦截
        // 原版 AtkPr0Procedure：paster_atk = 攻击力属性 + 锋利×0.5；skill_multiplier = 战技倍率基础值
        int sharpness = stack.getEnchantmentLevel(
                level.registryAccess().registryOrThrow(Registries.ENCHANTMENT)
                        .getHolderOrThrow(Enchantments.SHARPNESS));
        PasterItemData.putDouble(stack, "paster_atk",
                serverPlayer.getAttributeValue(Attributes.ATTACK_DAMAGE) + sharpness * 0.5);
        AttributeInstance skillMultiplier = serverPlayer.getAttribute(PDAttributes.SKILLMULTIPLIER);
        PasterItemData.putDouble(stack, "skill_multiplier",
                skillMultiplier != null ? skillMultiplier.getBaseValue() : 1);
        PasterItemData.putDouble(stack, "skill", 1);
        level.playSound(null, BlockPos.containing(player.getX(), player.getY(), player.getZ()),
                SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1f, 1f);
        AttributeInstance skillCd = serverPlayer.getAttribute(PDAttributes.SKILLCD);
        double cdFactor = skillCd != null ? skillCd.getBaseValue() : 1;
        if (WandSupport.hasCurioEquipped(serverPlayer, PDItems.TERRA_CHARM.get())) {
            // 大地护符：攻击加成 ×1.3、冷却减至 40×（融梦能量返还已随消耗一并剥离）
            PasterItemData.putDouble(stack, "paster_atk",
                    PasterItemData.getDouble(stack, "paster_atk") * 1.3);
            WandSupport.applyTaggedCooldown(serverPlayer, WandSupport.SKILL_TAG, (int) (40 * cdFactor));
        } else {
            WandSupport.applyTaggedCooldown(serverPlayer, WandSupport.SKILL_TAG, (int) (100 * cdFactor));
        }
    }

    /**
     * 原版 TerraSwordPr0Procedure：挥剑结算一段剑气
     * <p>
     * skill ≥1 时在面前 1 格、脚上 1.5 格处挥出剑气（初速 = 视线×2），并按段位 1/2/3：
     * 播放逐段升调的 sword_wave 音效、对刚挥出的剑气写入 paster_atk（第三段 ×2）并触发
     * 段位动画；1 tick 后 skill 推进（3 段后归零）。原版另写 "animation" 计分板供
     * MCreator 玩家动画系统使用——该系统未移植，与本项目既有移植一致跳过。
     *
     * @param level  世界
     * @param entity 挥剑者
     * @param stack  大地之刃物品栈
     */
    private static void performSkillWave(Level level, Entity entity, ItemStack stack) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        // 原版通用配置：ban terra sword
        if (PDCommonConfig.BAN_TERRA_SWORD.get()) {
            if (entity instanceof Player player) {
                player.displayClientMessage(Component.literal("§4此物品已被禁用"), true);
            }
            return;
        }
        int stage = (int) PasterItemData.getDouble(stack, "skill");
        if (stage < 1) {
            return;
        }
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        Vec3 look = entity.getLookAngle();
        // 挥出剑气：面前 1 格、脚上 1.5 格，朝向与挥剑者一致，初速 = 视线×2
        Entity wave = PDEntities.TERRASWORD_WAVE.get().spawn(serverLevel,
                BlockPos.containing(x + look.x, y + 1.5, z + look.z), MobSpawnType.MOB_SUMMONED);
        if (wave != null) {
            wave.setYRot(entity.getYRot());
            wave.setYBodyRot(entity.getYRot());
            wave.setYHeadRot(entity.getYRot());
            wave.setXRot(entity.getXRot());
            wave.setDeltaMovement(look.x * 2, look.y * 2, look.z * 2);
        }
        if (stage > 3) {
            return;
        }
        // 段位演出：音量/音调与搜索直径逐段对应（原版 1:0.8/1.0 d4、2:0.9/1.2 d3、3:1.0/1.4 d3）
        float volume = switch (stage) {
            case 1 -> 0.8f;
            case 2 -> 0.9f;
            default -> 1f;
        };
        float pitch = switch (stage) {
            case 1 -> 1f;
            case 2 -> 1.2f;
            default -> 1.4f;
        };
        double diameter = stage == 1 ? 4 : 3;
        serverLevel.playSound(null, BlockPos.containing(x, y, z),
                PDSounds.SWORD_WAVE.get(), SoundSource.PLAYERS, volume, pitch);
        // 对刚挥出的剑气写入属性攻击加成并触发段位动画（第三段加成翻倍）
        double pasterAtk = PasterItemData.getDouble(stack, "paster_atk") * (stage == 3 ? 2 : 1);
        Vec3 center = new Vec3(x + look.x, y + 1.5, z + look.z);
        for (Entity around : serverLevel.getEntitiesOfClass(Entity.class,
                new AABB(center, center).inflate(diameter / 2d), e -> true)) {
            if (around instanceof TerraswordWaveEntity waveEntity) {
                waveEntity.setAnimation(String.valueOf(stage));
                waveEntity.getPersistentData().putDouble("paster_atk", pasterAtk);
            }
        }
        // 1 tick 后推进段位（第三段后归零收束）
        int nextStage = stage == 3 ? 0 : stage + 1;
        ServerScheduler.schedule(1, () -> PasterItemData.putDouble(stack, "skill", nextStage));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        // 原版 appendHoverText 逐行还原
        tooltip.add(Component.literal("战技：§a泰拉剑技"));
        tooltip.add(Component.literal("§7▪ §9技能开启后共可向自身朝向挥出3次剑气"));
        tooltip.add(Component.literal("§7▪ §9对剑气途径范围的所有敌人造成伤害并小幅击飞"));
        tooltip.add(Component.literal("§7▪ §9剑气造成2+当前攻击力*0.7点伤害"));
        tooltip.add(Component.literal("§7▪ §9第三段剑气受到的的属性攻击力加成翻倍"));
        tooltip.add(Component.literal("§7▪ §9冷却时间：5秒"));
        tooltip.add(Component.literal("§7▪ §4融梦能量消耗：0.3"));
        tooltip.add(Component.literal("§7右键以强化下3次攻击为剑气攻击"));
    }
}
