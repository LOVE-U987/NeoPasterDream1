package com.pasterdream.pasterdreammod.smoketest;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.san.APISanGameRules;
import com.pasterdream.pasterdreammod.api.san.SanAPI;
import com.pasterdream.pasterdreammod.api.san.SanConfigRegistry;
import com.pasterdream.pasterdreammod.api.san.SanHelper;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import com.pasterdream.pasterdreammod.registry.PDAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

import java.util.function.Consumer;

/**
 * San 逐 tick 变化 VERIFY 套件 {@code san-tick}。
 * <p>
 * 回归验证「腰带/花环等 SAN_VARIABILITY 属性无效果（San 值不上升）」bug 的修复：
 * {@code PDSanityHelper.onPlayerTick}（San 逐 tick 变化循环）此前从未注册为监听器，
 * 导致 SAN_VARIABILITY 属性无人消费。本套件通过真实服务器 tick 验证：
 * <ul>
 *   <li>给玩家 SAN_VARIABILITY 附加 +100 瞬时修饰符；</li>
 *   <li>将 {@code pasterdreamSanVariabilityPerTick} 设为 1（每 tick 结算一次）；</li>
 *   <li>40 个真实 tick 后 San 值应明显上升（≈ 40 × 100 / 1200 ≈ +3.3）。</li>
 * </ul>
 * 依赖 PasterDreamSanity 模块已加载且 San 系统配置开启，否则跳过（pass + skip 说明）。
 * <p>
 * <b>不</b>并入默认 {@code all}；须 {@code PASTERDREAM_VERIFY_SUITES=san-tick}。
 */
public final class PDSanityTickVerifyHooks {

    public record Result(boolean pass, String name, String detail) {
    }

    private static final ResourceLocation TEST_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "verify_san_tick");

    /** 观察窗口（真实 tick 数） */
    private static final int WATCH_TICKS = 40;

    private PDSanityTickVerifyHooks() {
    }

    public static void verify(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
        if (server == null || player == null) {
            out.accept(new Result(false, "san-tick-skip", "server/player null"));
            return;
        }
        // 前提：PasterDreamSanity 未加载或 San 系统配置关闭时跳过（依赖 Tick 循环本体）
        if (!ModList.get().isLoaded("pasterdreamsanity")) {
            out.accept(new Result(true, "san-tick-skip", "SKIPPED_DEPENDENCY: pasterdreamsanity 未加载"));
            return;
        }
        if (!Boolean.TRUE.equals(SanConfigRegistry.get().enabled().get())) {
            out.accept(new Result(true, "san-tick-skip", "SKIPPED_DEPENDENCY: San 系统配置关闭"));
            return;
        }

        ServerLevel ow = server.overworld();
        ensureOverworld(player, ow);

        AttributeInstance attr = player.getAttribute(PDAttributes.SAN_VARIABILITY);
        if (attr == null) {
            out.accept(new Result(false, "san-tick-skip", "玩家缺少 SAN_VARIABILITY 属性实例"));
            return;
        }

        // 记录旧值以便收尾恢复
        boolean oldCheck = SanHelper.isSanCheckEnabled(player);
        int oldInterval = ow.getGameRules().getInt(APISanGameRules.SAN_VARIABILITY_PER_TICK);
        double originalSan = SanAPI.getSanValue(player);

        // 加速：每 tick 结算一次 + +100 变化率修饰符（同 tick 内环境修饰符稳定，+100 主导）
        ow.getGameRules().getRule(APISanGameRules.SAN_CHECK_SYSTEM).set(true, server);
        ow.getGameRules().getRule(APISanGameRules.SAN_VARIABILITY_PER_TICK).set(1, server);
        attr.removeModifier(TEST_MODIFIER_ID);
        attr.addTransientModifier(new AttributeModifier(TEST_MODIFIER_ID, 100.0D,
                AttributeModifier.Operation.ADD_VALUE));
        // 起始 San 降到中值：若保持 100（上限）则上升被钳制，无法观察 Δ
        SanAPI.setSanValue(player, 50.0D);
        double san0 = SanAPI.getSanValue(player);

        // 观察 40 个真实 tick（ServerScheduler 基于真实 ServerTickEvent.Post 派发）
        ServerScheduler.schedule(WATCH_TICKS, () -> {
            try {
                double san1 = SanAPI.getSanValue(player);
                double delta = san1 - san0;
                // 期望 ≈ WATCH_TICKS × (100 + 环境) / 1200 ≈ +3.3；给出宽松区间
                boolean rose = delta >= 1.0D && delta <= 10.0D;
                out.accept(ok(rose,
                        "San 随 SAN_VARIABILITY 上升（tick 循环生效）",
                        String.format("san0=%.2f san1=%.2f Δ=%.2f (%dt)", san0, san1, delta, WATCH_TICKS)));
                out.accept(ok(attr != null && attr.hasModifier(TEST_MODIFIER_ID),
                        "SAN_VARIABILITY 测试修饰符仍挂载", "id=" + TEST_MODIFIER_ID));
            } finally {
                // 收尾：移除修饰符、恢复 gamerule 与 San 值，避免污染其他套件
                attr.removeModifier(TEST_MODIFIER_ID);
                ow.getGameRules().getRule(APISanGameRules.SAN_CHECK_SYSTEM).set(oldCheck, server);
                ow.getGameRules().getRule(APISanGameRules.SAN_VARIABILITY_PER_TICK).set(oldInterval, server);
                SanAPI.setSanValue(player, originalSan);
            }
        });
    }

    private static void ensureOverworld(ServerPlayer player, ServerLevel overworld) {
        if (player.level().dimension() != Level.OVERWORLD) {
            BlockPos spawn = overworld.getSharedSpawnPos();
            player.teleportTo(overworld,
                    spawn.getX() + 0.5, spawn.getY() + 1.0, spawn.getZ() + 0.5,
                    player.getYRot(), player.getXRot());
        }
        player.setGameMode(GameType.SURVIVAL);
    }

    private static Result ok(boolean pass, String name, String detail) {
        return new Result(pass, name, detail);
    }
}
