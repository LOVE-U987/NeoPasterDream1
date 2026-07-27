package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.entity.projectile.WhiteSwordRainProjectileEntity;
import com.pasterdream.pasterdreammod.registry.PDAttributes;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.util.ServerScheduler;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.List;

/**
 * 白色灾厄 (white_sword)
 * <p>
 * 还原自原版 WhiteSwordItem + WhiteSwordPr0Procedure（战技：白厄剑雨）：
 * <ul>
 *   <li>剑属性与既有 ItemAPI 注册逐项一致（耐久 1771、攻击 8、攻速 -2.4、附魔 10），
 *       并按原版补上 fireResistant</li>
 *   <li>右键（创造模式或已达成光明天赋成就）消耗 0.1 融梦能量发动白厄剑雨：
 *       以视点方向 8~9 格外的落点为中心，8 轮共 46 发光剑自天而降，
 *       每发造成 3 + 攻击力×0.4 点伤害、穿透 1（命中效果见剑雨投射物）</li>
 *   <li>发动后全部 pasterdream:skill 物品统一冷却 84 × 战技冷却属性 tick（4.2 秒）</li>
 * </ul>
 */
public class WhiteSwordItem extends SwordItem {

    /** 光明天赋成就 ID（白厄剑雨的施放门槛） */
    private static final ResourceLocation TALENT_LIGHT_ADVANCEMENT =
            ResourceLocation.fromNamespaceAndPath("pasterdream", "achievement_talent_light");

    /** 与既有 ItemAPI 注册等值的 tier：耐久 1771 / 挖速 2 / 伤害加成 8 / 附魔 10 */
    private static final SimpleTier TIER = new SimpleTier(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1771, 2.0f, 8.0f, 10, () -> Ingredient.EMPTY);

    public WhiteSwordItem(Properties properties) {
        // 原版 fireResistant；攻击伤害由 tier 承载（伤害参数 0，与 ItemAPI 方案一致）
        super(TIER, properties.fireResistant()
                .attributes(SwordItem.createAttributes(TIER, 0, -2.4f)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = super.use(level, player, hand);
        // 原版 WhiteSwordPr0Procedure：右键发动白厄剑雨
        castSwordRain(level, player);
        return result;
    }

    /**
     * 原版 WhiteSwordPr0Procedure：白厄剑雨战技
     * <p>
     * 门槛：创造模式或已达成 achievement_talent_light；消耗 0.1 融梦能量。
     * 波次时序（外层 1 tick 后并列调度，与原版 queueServerWork 嵌套一致）：
     * +8/+11 各 6宽+4窄；+14 6+4 并奏紫水晶音；+17 5+4；+20 5+3；+23 4+3；
     * +26/+29 仅奏紫水晶音收尾。宽散布 ±3.5、窄散布 ±2.5。
     *
     * @param level  世界
     * @param player 施放者
     */
    private static void castSwordRain(Level level, Player player) {
        if (level.isClientSide() || !(player instanceof ServerPlayer serverPlayer)
                || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!serverPlayer.getAbilities().instabuild && !hasLightTalent(serverPlayer)) {
            return;
        }
        // 融梦能量消耗已剥离至附属 mod
        // 施放点坐标（波次音效与剑雨基准高度均以此为锚，与原版捕获时机一致）
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        serverLevel.playSound(null, BlockPos.containing(x, y, z),
                PDSounds.WHITE_SWORD_RAIN.get(), SoundSource.PLAYERS, 0.7f, 1f);
        player.swing(InteractionHand.MAIN_HAND, true);
        // 全部 skill 标签物品统一冷却 84 × 战技冷却属性（原版取 getValue）
        AttributeInstance skillCd = player.getAttribute(PDAttributes.SKILLCD);
        WandSupport.applyTaggedCooldown(player, WandSupport.SKILL_TAG,
                (int) (84 * (skillCd != null ? skillCd.getValue() : 1)));
        // 原版：先延迟 1 tick，再并列调度 8 个波次
        ServerScheduler.schedule(1, () -> {
            scheduleWave(serverLevel, serverPlayer, x, y, z, 8, 6, 4, false);
            scheduleWave(serverLevel, serverPlayer, x, y, z, 11, 6, 4, false);
            scheduleWave(serverLevel, serverPlayer, x, y, z, 14, 6, 4, true);
            scheduleWave(serverLevel, serverPlayer, x, y, z, 17, 5, 4, true);
            scheduleWave(serverLevel, serverPlayer, x, y, z, 20, 5, 3, true);
            scheduleWave(serverLevel, serverPlayer, x, y, z, 23, 4, 3, true);
            scheduleWave(serverLevel, serverPlayer, x, y, z, 26, 0, 0, true);
            scheduleWave(serverLevel, serverPlayer, x, y, z, 29, 0, 0, true);
        });
    }

    /**
     * 调度一轮剑雨（宽散布 ±3.5 若干发 + 窄散布 ±2.5 若干发，可选紫水晶收束音）
     *
     * @param level       服务端世界
     * @param player      施放者
     * @param x           施放点 X（音效锚点）
     * @param y           施放点 Y（剑雨基准高度）
     * @param z           施放点 Z（音效锚点）
     * @param delay       相对外层 1 tick 的追加延迟
     * @param wideCount   宽散布光剑数
     * @param narrowCount 窄散布光剑数
     * @param chime       是否播放紫水晶簇放置音（音量 2）
     */
    private static void scheduleWave(ServerLevel level, ServerPlayer player,
                                     double x, double y, double z,
                                     int delay, int wideCount, int narrowCount, boolean chime) {
        ServerScheduler.schedule(delay, () -> {
            // 与原版一致：伤害按生成时刻的攻击力属性实时计算
            float damage = (float) (3 + 0.4 * player.getAttributeValue(Attributes.ATTACK_DAMAGE));
            for (int i = 0; i < wideCount; i++) {
                spawnRainSword(level, player, y, damage, 3.5);
            }
            for (int i = 0; i < narrowCount; i++) {
                spawnRainSword(level, player, y, damage, 2.5);
            }
            if (chime) {
                level.playSound(null, BlockPos.containing(x, y, z),
                        SoundEvents.AMETHYST_CLUSTER_PLACE, SoundSource.PLAYERS, 2f, 1f);
            }
        });
    }

    /**
     * 生成一发下落光剑：落点以施放者当前视线 9 格射线命中方块为中心随机偏移，
     * 高度为施放点 y + [13,15)（与原版逐参数一致）。
     *
     * @param level  服务端世界
     * @param player 施放者
     * @param baseY  施放点基准高度
     * @param damage 基础伤害
     * @param spread 水平随机散布半径
     */
    private static void spawnRainSword(ServerLevel level, ServerPlayer player,
                                       double baseY, float damage, double spread) {
        BlockPos target = level.clip(new ClipContext(
                player.getEyePosition(1f),
                player.getEyePosition(1f).add(player.getViewVector(1f).scale(9)),
                ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player)).getBlockPos();
        WhiteSwordRainProjectileEntity.summonRainSword(level, player,
                target.getX() + Mth.nextDouble(level.random, -spread, spread),
                baseY + Mth.nextDouble(level.random, 13, 15),
                target.getZ() + Mth.nextDouble(level.random, -spread, spread),
                damage);
    }

    /**
     * 判断玩家是否已达成光明天赋成就（缺失成就时按未达成处理）
     *
     * @param player 服务端玩家
     * @return true 表示已达成
     */
    private static boolean hasLightTalent(ServerPlayer player) {
        AdvancementHolder advancement = player.server.getAdvancements().get(TALENT_LIGHT_ADVANCEMENT);
        return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        // 原版 appendHoverText 逐行还原
        tooltip.add(Component.literal("战技：§f白厄剑雨"));
        tooltip.add(Component.literal("§7▪ §9技能开启时标记在视点方向8格外的位置"));
        tooltip.add(Component.literal("§7▪ §9在直径为7的区域释放多轮剑雨"));
        tooltip.add(Component.literal("§7▪ §9每发剑雨造成3+攻击力属性值*0.4点伤害并将其束缚"));
        tooltip.add(Component.literal("§7▪ §9且每发有12%的概率使暗影生物沉默10秒"));
        tooltip.add(Component.literal("§7▪ §9对BOSS类暗影生物概率减半"));
        tooltip.add(Component.literal("§7▪ §9冷却时间：4.2秒"));
        tooltip.add(Component.literal("§7▪ §4融梦能量消耗：0.1"));
    }
}
