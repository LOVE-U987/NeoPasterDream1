package com.pasterdream.pasterdreammod.smoketest;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.curio.CurioAPI;
import com.pasterdream.pasterdreammod.attachment.PDAttachments;
import com.pasterdream.pasterdreammod.item.CeciliacareCharmItem;
import com.pasterdream.pasterdreammod.item.LightButterflyCurioItem;
import com.pasterdream.pasterdreammod.registry.PDAttributes;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForgeMod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotAttribute;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 饰品（Curio）专项行为验证 —— 严格按物品描述 / 原版数值断言。
 * <p>
 * 原则：
 * <ul>
 *   <li>描述或原版写明的数值必须<strong>全等</strong>（amount + operation），不得用近似或“升高即可”</li>
 *   <li>有运行时效果的饰品测可观测行为（效果 amp/duration、能量增量、条件门控）</li>
 *   <li>声明唯一装备的饰品测 canEquip</li>
 *   <li>实现缺失或数值偏移 → FAIL（不 skip 放过）</li>
 * </ul>
 * 挂在 {@code PASTERDREAM_VERIFY=1} 的 {@code core} 套件。
 */
public final class PDCurioVerifyHooks {

    public record Result(boolean pass, String name, String detail) {}

    /** 期望的单条属性修饰（与描述/原版一一对应）。 */
    private record ExpectMod(Holder<Attribute> attribute, double amount, AttributeModifier.Operation operation, String label) {}

    private static final double EPS = 1e-9;
    private static final ResourceLocation VERIFY_MOD_ID =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "verify_curio_probe");

    private PDCurioVerifyHooks() {}

    public static void verify(ServerPlayer player, Consumer<Result> out) {
        if (player == null) {
            out.accept(new Result(false, "curio-skip", "player == null"));
            return;
        }

        // ----- 属性类（数值全等，对照原版 getAttributeModifiers） -----
        assertAttrs(player, out, "fourleaf_clover_curio", "charm", List.of(
                exp(Attributes.MAX_HEALTH, 1.0, AttributeModifier.Operation.ADD_VALUE, "max_health+1"),
                exp(PDAttributes.LUCK, 6.0, AttributeModifier.Operation.ADD_VALUE, "luck+6")
        ));

        assertAttrs(player, out, "hithard_0_ring", "ring", List.of(
                exp(Attributes.ATTACK_DAMAGE, 0.5, AttributeModifier.Operation.ADD_VALUE, "attack+0.5")
        ));
        assertAttrs(player, out, "hithard_1_ring", "ring", List.of(
                exp(Attributes.ATTACK_DAMAGE, 1.0, AttributeModifier.Operation.ADD_VALUE, "attack+1")
        ));

        // 红露滴：原版 0/1/2/3 → 生命 +1/+2/+3/+4
        assertAttrs(player, out, "red_dew_0_ring", "ring", List.of(
                exp(Attributes.MAX_HEALTH, 1.0, AttributeModifier.Operation.ADD_VALUE, "max_health+1")
        ));
        assertAttrs(player, out, "red_dew_1_ring", "ring", List.of(
                exp(Attributes.MAX_HEALTH, 2.0, AttributeModifier.Operation.ADD_VALUE, "max_health+2")
        ));
        assertAttrs(player, out, "red_dew_2_ring", "ring", List.of(
                exp(Attributes.MAX_HEALTH, 3.0, AttributeModifier.Operation.ADD_VALUE, "max_health+3")
        ));
        assertAttrs(player, out, "red_dew_3_ring", "ring", List.of(
                exp(Attributes.MAX_HEALTH, 4.0, AttributeModifier.Operation.ADD_VALUE, "max_health+4")
        ));

        assertAttrs(player, out, "health_0_necklace", "necklace", List.of(
                exp(Attributes.MAX_HEALTH, 2.0, AttributeModifier.Operation.ADD_VALUE, "max_health+2")
        ));

        assertAttrs(player, out, "carapax_charm", "charm", List.of(
                exp(Attributes.MOVEMENT_SPEED, -0.08, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, "speed*-0.08"),
                exp(Attributes.ARMOR, 2.0, AttributeModifier.Operation.ADD_VALUE, "armor+2"),
                exp(Attributes.ARMOR_TOUGHNESS, 1.0, AttributeModifier.Operation.ADD_VALUE, "toughness+1")
        ));

        assertAttrs(player, out, "allkinds_ring", "ring", List.of(
                exp(Attributes.MAX_HEALTH, 4.0, AttributeModifier.Operation.ADD_VALUE, "max_health+4"),
                exp(Attributes.ATTACK_DAMAGE, 2.0, AttributeModifier.Operation.ADD_VALUE, "attack+2"),
                exp(Attributes.ATTACK_SPEED, 0.1, AttributeModifier.Operation.ADD_VALUE, "attack_speed+0.1"),
                exp(Attributes.ENTITY_INTERACTION_RANGE, 0.2, AttributeModifier.Operation.ADD_VALUE, "entity_reach+0.2"),
                exp(Attributes.BLOCK_INTERACTION_RANGE, 0.5, AttributeModifier.Operation.ADD_VALUE, "block_reach+0.5"),
                exp(Attributes.MOVEMENT_SPEED, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, "speed*+0.05"),
                exp(PDAttributes.SKILLCD, -0.05, AttributeModifier.Operation.ADD_VALUE, "skillcd-0.05"),
                exp(PDAttributes.SKILLMULTIPLIER, 0.05, AttributeModifier.Operation.ADD_VALUE, "skillmult+0.05"),
                exp(PDAttributes.TELEPORTATIONCD, -0.05, AttributeModifier.Operation.ADD_VALUE, "tpcd-0.05")
        ));

        // 啵啵鸡：原版 map 最终为 consume 上 -0.2 与 -0.4、range +0.1、speed *0.05
        assertAttrs(player, out, "boboji_plume", "charm", List.of(
                exp(PDAttributes.TELEPORTATIONCONSUME, -0.6, AttributeModifier.Operation.ADD_VALUE, "tp_consume-0.6"),
                exp(PDAttributes.TELEPORTATIONRANGE, 0.1, AttributeModifier.Operation.ADD_VALUE, "tp_range+0.1"),
                exp(Attributes.MOVEMENT_SPEED, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, "speed*+0.05")
        ));

        assertAttrs(player, out, "feather_necklace", "necklace", List.of(
                exp(PDAttributes.TELEPORTATIONCONSUME, -0.05, AttributeModifier.Operation.ADD_VALUE, "tp_consume-0.05"),
                exp(PDAttributes.TELEPORTATIONRANGE, 0.2, AttributeModifier.Operation.ADD_VALUE, "tp_range+0.2")
        ));

        assertAttrs(player, out, "dream_traveler_belt", "belt", List.of(
                exp(Attributes.MOVEMENT_SPEED, 0.03, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, "speed*+0.03"),
                exp(PDAttributes.TELEPORTATIONCD, -0.1, AttributeModifier.Operation.ADD_VALUE, "tpcd-0.1")
        ));

        assertAttrs(player, out, "traveler_belt", "belt", List.of(
                exp(PDAttributes.TELEPORTATIONCONSUME, -0.5, AttributeModifier.Operation.ADD_VALUE, "tp_consume-0.5")
        ));

        assertAttrs(player, out, "dark_alllegory_curio", "charm", List.of(
                exp(PDAttributes.MAGICCD, -0.1, AttributeModifier.Operation.ADD_VALUE, "magiccd-0.1"),
                exp(PDAttributes.MAGICPOWER, 1.0, AttributeModifier.Operation.ADD_VALUE, "magicpower+1")
        ));

        assertAttrs(player, out, "degenerate_bodys", "body", List.of(
                exp(Attributes.MAX_HEALTH, -4.0, AttributeModifier.Operation.ADD_VALUE, "max_health-4"),
                exp(Attributes.ENTITY_INTERACTION_RANGE, 0.2, AttributeModifier.Operation.ADD_VALUE, "entity_reach+0.2"),
                exp(Attributes.BLOCK_INTERACTION_RANGE, 1.0, AttributeModifier.Operation.ADD_VALUE, "block_reach+1"),
                exp(Attributes.ATTACK_DAMAGE, 2.0, AttributeModifier.Operation.ADD_VALUE, "attack+2")
        ));

        assertAttrs(player, out, "duke_coin_curio", "charm", List.of(
                exp(PDAttributes.LUCK, 7.0, AttributeModifier.Operation.ADD_VALUE, "luck+7"),
                exp(PDAttributes.TELEPORTATIONCD, 7.0, AttributeModifier.Operation.ADD_VALUE, "tpcd+7")
        ));

        assertAttrs(player, out, "garland", "head", List.of(
                exp(PDAttributes.SAN_VARIABILITY, 0.48, AttributeModifier.Operation.ADD_VALUE, "san_var+0.48")
        ));
        assertAttrs(player, out, "nature_belt", "belt", List.of(
                exp(PDAttributes.SAN_VARIABILITY, 0.48, AttributeModifier.Operation.ADD_VALUE, "san_var+0.48")
        ));

        assertAttrs(player, out, "sea_charm", "charm", List.of(
                exp(NeoForgeMod.SWIM_SPEED, 0.3, AttributeModifier.Operation.ADD_VALUE, "swim+0.3"),
                exp(PDAttributes.SAN_VARIABILITY, 0.96, AttributeModifier.Operation.ADD_VALUE, "san_var+0.96")
        ));

        assertAttrs(player, out, "hiyori_head", "head", List.of(
                exp(Attributes.MAX_HEALTH, 2.0, AttributeModifier.Operation.ADD_VALUE, "max_health+2"),
                exp(PDAttributes.LUCK, 15.0, AttributeModifier.Operation.ADD_VALUE, "luck+15"),
                exp(PDAttributes.SAN_VARIABILITY, 0.96, AttributeModifier.Operation.ADD_VALUE, "san_var+0.96")
        ));

        // ----- 唯一装备 -----
        for (String[] u : new String[][]{
                {"hithard_0_ring", "ring"}, {"hithard_1_ring", "ring"},
                {"red_dew_0_ring", "ring"}, {"red_dew_1_ring", "ring"},
                {"red_dew_2_ring", "ring"}, {"red_dew_3_ring", "ring"},
                {"allkinds_ring", "ring"},
                {"fourleaf_clover_curio", "charm"}, {"carapax_charm", "charm"},
                {"degenerate_bodys", "body"}, {"boboji_plume", "charm"},
                {"duke_coin_curio", "charm"}, {"dark_alllegory_curio", "charm"},
                {"hiyori_head", "head"}, {"traveler_belt", "belt"},
                {"dream_traveler_belt", "belt"}, {"feather_necklace", "necklace"},
                {"health_0_necklace", "necklace"}, {"sea_charm", "charm"}
        }) {
            assertSingleton(player, out, u[0], u[1]);
        }

        // ----- 运行时行为 -----
        testLightButterflyNightVision(player, out);
        testBrightButterflyNightVisionAndDarkness(player, out);
        testRabbitJump(player, out);
        testFireNecklaceHasteWhenBurning(player, out);
        testMeltdreamEnergyRing(player, out);
        testWorldtreeSeedpod(player, out);
        testCeciliaCare(player, out);
        testCounterAttackBuffDefinition(out);
        testDescriptionNumbers(out);

        // ----- 全量基础装备 -----
        testBasicEquipAllRegistered(player, out);
    }

    // =====================================================================
    // 属性全等
    // =====================================================================

    private static ExpectMod exp(Holder<Attribute> attr, double amount, AttributeModifier.Operation op, String label) {
        return new ExpectMod(attr, amount, op, label);
    }

    private static void assertAttrs(ServerPlayer player, Consumer<Result> out,
                                    String regName, String slot, List<ExpectMod> expected) {
        String caseName = "attr-" + regName;
        Item it = item(regName);
        if (it == null) {
            out.accept(new Result(false, caseName, "item not registered"));
            return;
        }

        clearSlot(player, slot, 0);
        // 真实应用基线：装备前记录各属性当前值（同 tick 内读取，环境修饰符稳定）
        Map<Holder<Attribute>, Double> baseValues = new java.util.LinkedHashMap<>();
        for (ExpectMod exp : expected) {
            if (!baseValues.containsKey(exp.attribute())) {
                AttributeInstance inst = player.getAttribute(exp.attribute());
                baseValues.put(exp.attribute(), inst == null ? Double.NaN : inst.getValue());
            }
        }

        ItemStack stack = new ItemStack(it);
        equip(player, slot, 0, stack);

        SlotContext ctx = new SlotContext(slot, player, 0, false, true);
        var mods = CuriosApi.getAttributeModifiers(ctx, VERIFY_MOD_ID, stack);

        List<String> fails = new ArrayList<>();
        List<String> found = new ArrayList<>();
        // 实际条目列表，便于「每条期望匹配一条实际，且无多余」
        record Actual(Holder<Attribute> attr, double amount, AttributeModifier.Operation op) {}
        List<Actual> actuals = new ArrayList<>();
        for (var entry : mods.entries()) {
            AttributeModifier mod = entry.getValue();
            actuals.add(new Actual(entry.getKey(), mod.amount(), mod.operation()));
            found.add(fmtMod(entry.getKey(), mod));
        }

        boolean[] actualUsed = new boolean[actuals.size()];
        for (int i = 0; i < expected.size(); i++) {
            ExpectMod exp = expected.get(i);
            int hit = -1;
            for (int j = 0; j < actuals.size(); j++) {
                if (actualUsed[j]) continue;
                Actual a = actuals.get(j);
                if (a.attr().equals(exp.attribute())
                        && a.op() == exp.operation()
                        && eq(a.amount(), exp.amount())) {
                    hit = j;
                    break;
                }
            }
            if (hit >= 0) {
                actualUsed[hit] = true;
            } else {
                fails.add("missing " + exp.label()
                        + " (want " + exp.amount() + " " + exp.operation()
                        + " on " + attrId(exp.attribute()) + ")");
            }
        }
        for (int j = 0; j < actuals.size(); j++) {
            if (!actualUsed[j]) {
                Actual a = actuals.get(j);
                fails.add("unexpected " + attrId(a.attr()) + ":" + a.amount() + "/" + a.op());
            }
        }

        // 真实应用断言：装备前后玩家实际属性值增量应与期望修饰符贡献一致。
        // Curios 的 setEquippedCurio 仅把物品放入槽位，属性修饰符要到下一个实体 tick 由
        // CuriosEventHandler.tick 检测「当前栈 != 上一 tick 栈」后统一应用
        // （addOrUpdateTransientModifier，同 id 覆盖式）。因此同一 tick 内 equip 后
        // 直接读属性必然读到旧值；这里手动执行与 Curios 完全相同的 apply 逻辑
        // （同一 map、同一方法），读 after 结算增量后再还原，等价于等待 1 个真实 tick。
        List<Map.Entry<Holder<Attribute>, AttributeModifier>> applied = new ArrayList<>();
        for (var entry : mods.entries()) {
            if (entry.getKey().value() instanceof SlotAttribute) {
                continue; // 槽位数量修饰符由 Curios 经 addTransientSlotModifiers 单独处理
            }
            AttributeInstance inst = player.getAttribute(entry.getKey());
            if (inst != null) {
                inst.addOrUpdateTransientModifier(entry.getValue());
                applied.add(entry);
            }
        }
        try {
            for (ExpectMod exp : expected) {
                AttributeInstance inst = player.getAttribute(exp.attribute());
                if (inst == null) {
                    fails.add("player has no attribute instance " + attrId(exp.attribute())
                            + " —— 修饰符无法应用");
                    continue;
                }
                double before = baseValues.getOrDefault(exp.attribute(), Double.NaN);
                double after = inst.getValue();
                // 仅当该属性在期望列表中首次出现时结算一次增量（多个同属性条目合并计算）
                if (before != before || before != baseValues.get(exp.attribute())) {
                    continue;
                }
                baseValues.put(exp.attribute(), after); // 标记已结算
                double add = expected.stream()
                        .filter(e -> e.attribute().equals(exp.attribute())
                                && e.operation() == AttributeModifier.Operation.ADD_VALUE)
                        .mapToDouble(ExpectMod::amount).sum();
                double baseMult = expected.stream()
                        .filter(e -> e.attribute().equals(exp.attribute())
                                && e.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                        .mapToDouble(ExpectMod::amount).sum();
                double totalMult = expected.stream()
                        .filter(e -> e.attribute().equals(exp.attribute())
                                && e.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                        .mapToDouble(ExpectMod::amount).sum();
                double expectDelta = (before + add) * (1 + baseMult) * (1 + totalMult) - before;
                double actualDelta = after - before;
                if (!eq(actualDelta, expectDelta)) {
                    fails.add("real-delta " + attrId(exp.attribute()) + ": want " + expectDelta
                            + " got " + actualDelta + " (before " + before + " → after " + after + ")");
                }
            }
        } finally {
            // 还原手动应用的修饰符，避免污染后续断言
            for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : applied) {
                AttributeInstance inst = player.getAttribute(entry.getKey());
                if (inst != null) {
                    inst.removeModifier(entry.getValue());
                }
            }
        }

        clearSlot(player, slot, 0);

        boolean pass = fails.isEmpty();
        String detail = pass
                ? "ok " + expected.size() + " mods exact + real-delta(simulated Curios apply): "
                + String.join(", ", expected.stream().map(ExpectMod::label).toList())
                : "FAIL " + String.join(" | ", fails) + " || actual=[" + String.join("; ", found) + "]";
        out.accept(new Result(pass, caseName, detail));
    }

    // =====================================================================
    // 唯一装备
    // =====================================================================

    private static void assertSingleton(ServerPlayer player, Consumer<Result> out, String regName, String slot) {
        String caseName = "unique-" + regName;
        Item it = item(regName);
        if (it == null) {
            out.accept(new Result(false, caseName, "item not registered"));
            return;
        }
        if (!(it instanceof ICurioItem curio)) {
            out.accept(new Result(false, caseName, "not ICurioItem"));
            return;
        }

        clearSlot(player, slot, 0);
        ItemStack s1 = new ItemStack(it);
        ItemStack s2 = new ItemStack(it);
        SlotContext ctx = new SlotContext(slot, player, 0, false, true);

        boolean first = curio.canEquip(ctx, s1);
        equip(player, slot, 0, s1);
        boolean second = curio.canEquip(ctx, s2);
        clearSlot(player, slot, 0);

        boolean ok = first && !second;
        out.accept(new Result(ok, caseName,
                String.format("firstCan=%s secondCan=%s (expected true/false)", first, second)));
    }

    // =====================================================================
    // 夜明蝶：低亮度夜视 amp0 / duration 240
    // =====================================================================

    private static void testLightButterflyNightVision(ServerPlayer player, Consumer<Result> out) {
        String caseName = "light-butterfly-nv";
        Item it = item("light_butterfly_curio");
        if (it == null) {
            out.accept(new Result(false, caseName, "not registered"));
            return;
        }

        ServerLevel level = player.serverLevel();
        clearSlot(player, "charm", 0);
        player.removeEffect(MobEffects.NIGHT_VISION);

        long prevTime = level.getDayTime();
        level.setDayTime(18000L);

        // 改用 y=-64 深地底房间（天空光=0，无需依赖光引擎重算暗室）
        BlockPos base = new BlockPos(player.blockPosition().getX(), -64, player.blockPosition().getZ());
        List<BlockPos> placed = digBedrockRoom(level, base);

        player.teleportTo(level, base.getX() + 0.5, base.getY() + 2.5, base.getZ() + 0.5,
                player.getYRot(), player.getXRot());

        ItemStack stack = new ItemStack(it);
        equip(player, "charm", 0, stack);

        int oldTick = player.tickCount;
        player.tickCount = 120;
        if (stack.getItem() instanceof LightButterflyCurioItem lb) {
            lb.curioTick(new SlotContext("charm", player, 0, false, true), stack);
        } else if (stack.getItem() instanceof ICurioItem curio) {
            curio.curioTick(new SlotContext("charm", player, 0, false, true), stack);
        }
        player.tickCount = oldTick;

        int brightness = level.getMaxLocalRawBrightness(player.blockPosition());
        MobEffectInstance nv = player.getEffect(MobEffects.NIGHT_VISION);

        for (BlockPos p : placed) {
            level.setBlock(p, Blocks.STONE.defaultBlockState(), 3); // 回填为石头
        }
        clearSlot(player, "charm", 0);
        player.removeEffect(MobEffects.NIGHT_VISION);
        level.setDayTime(prevTime);

        boolean nvExact = nv != null && nv.getAmplifier() == 0 && nv.getDuration() == 240;
        boolean pass = nvExact;

        out.accept(new Result(pass, caseName,
                String.format("brightness=%d nv=%s (want amp0/d240 exact)",
                        brightness, fmtEffect(nv))));
    }

    // =====================================================================
    // 光明飞蝶：低亮度夜视 amp0/d240 + 清除黑暗
    // =====================================================================

    private static void testBrightButterflyNightVisionAndDarkness(ServerPlayer player, Consumer<Result> out) {
        String caseName = "bright-butterfly-nv-darkness";
        Item it = item("bright_butterfly_curio");
        if (it == null) {
            out.accept(new Result(false, caseName, "not registered"));
            return;
        }

        ServerLevel level = player.serverLevel();
        clearSlot(player, "charm", 0);
        player.removeEffect(MobEffects.NIGHT_VISION);
        player.removeEffect(MobEffects.DARKNESS);

        long prevTime = level.getDayTime();
        level.setDayTime(18000L);

        // 改用 y=-64 深地底房间
        BlockPos base = new BlockPos(player.blockPosition().getX(), -64, player.blockPosition().getZ());
        List<BlockPos> placed = digBedrockRoom(level, base);

        player.teleportTo(level, base.getX() + 0.5, base.getY() + 2.5, base.getZ() + 0.5,
                player.getYRot(), player.getXRot());

        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 200, 0, false, false));

        ItemStack stack = new ItemStack(it);
        equip(player, "charm", 0, stack);
        if (stack.getItem() instanceof ICurioItem curio) {
            curio.curioTick(new SlotContext("charm", player, 0, false, true), stack);
        }

        int brightness = level.getMaxLocalRawBrightness(player.blockPosition());
        MobEffectInstance nv = player.getEffect(MobEffects.NIGHT_VISION);
        boolean darkGone = !player.hasEffect(MobEffects.DARKNESS);
        boolean nvExact = nv != null && nv.getAmplifier() == 0 && nv.getDuration() == 240;

        for (BlockPos p : placed) {
            level.setBlock(p, Blocks.STONE.defaultBlockState(), 3); // 回填
        }
        clearSlot(player, "charm", 0);
        player.removeEffect(MobEffects.NIGHT_VISION);
        player.removeEffect(MobEffects.DARKNESS);
        level.setDayTime(prevTime);

        boolean pass = nvExact && darkGone;
        out.accept(new Result(pass, caseName,
                String.format("brightness=%d nv=%s darkCleared=%s (want nv amp0/d240 + darkness cleared)",
                        brightness, fmtEffect(nv), darkGone)));
    }

    // =====================================================================
    // 雪兔脚：跳跃提升 I（amp0）
    // =====================================================================

    private static void testRabbitJump(ServerPlayer player, Consumer<Result> out) {
        String caseName = "rabbit-jump-i";
        Item it = item("rabbit_0_necklace");
        if (it == null) {
            out.accept(new Result(false, caseName, "not registered"));
            return;
        }

        clearSlot(player, "necklace", 0);
        player.removeEffect(MobEffects.JUMP);

        ItemStack stack = new ItemStack(it);
        equip(player, "necklace", 0, stack);
        if (stack.getItem() instanceof ICurioItem curio) {
            curio.curioTick(new SlotContext("necklace", player, 0, false, true), stack);
        }

        MobEffectInstance jump = player.getEffect(MobEffects.JUMP);
        // 描述「跳跃提升I」→ amp0；原版每 tick 续 duration=2
        boolean pass = jump != null && jump.getAmplifier() == 0 && jump.getDuration() == 2;

        clearSlot(player, "necklace", 0);
        player.removeEffect(MobEffects.JUMP);

        out.accept(new Result(pass, caseName,
                String.format("jump=%s (want JUMP amp0/d2 exact)", fmtEffect(jump))));
    }

    // =====================================================================
    // 业火项链：燃烧时急迫 I（amp0）；原版 duration=2
    // =====================================================================

    private static void testFireNecklaceHasteWhenBurning(ServerPlayer player, Consumer<Result> out) {
        String caseName = "fire-necklace-haste";
        Item it = item("fire_0_necklace");
        if (it == null) {
            out.accept(new Result(false, caseName, "not registered"));
            return;
        }

        clearSlot(player, "necklace", 0);
        player.removeEffect(MobEffects.DIG_SPEED);
        player.clearFire();

        ItemStack stack = new ItemStack(it);
        equip(player, "necklace", 0, stack);
        player.setRemainingFireTicks(40);

        if (stack.getItem() instanceof ICurioItem curio) {
            curio.curioTick(new SlotContext("necklace", player, 0, false, true), stack);
        }

        MobEffectInstance haste = player.getEffect(MobEffects.DIG_SPEED);
        boolean pass = haste != null && haste.getAmplifier() == 0 && haste.getDuration() == 2;

        clearSlot(player, "necklace", 0);
        player.removeEffect(MobEffects.DIG_SPEED);
        player.clearFire();

        out.accept(new Result(pass, caseName,
                String.format("haste=%s (want DIG_SPEED amp0/d2 exact while on fire)", fmtEffect(haste))));
    }

    // =====================================================================
    // 融梦光环戒指：描述 +0.15/min ⇒ 每 20t +0.0025；染梦/灯影
    // =====================================================================

    private static void testMeltdreamEnergyRing(ServerPlayer player, Consumer<Result> out) {
        String caseName = "meltdream-ring-energy";
        Item ringItem = item("meltdream_energy_0_ring");
        if (ringItem == null) {
            out.accept(new Result(false, caseName, "meltdream_energy_0_ring not registered"));
            return;
        }

        clearSlot(player, "ring", 0);
        ItemStack stack = new ItemStack(ringItem);
        equip(player, "ring", 0, stack);

        ServerLevel dyed = findLevel(player, "dyedream_world");
        ServerLevel origin = player.serverLevel();
        if (dyed == null) {
            out.accept(new Result(false, caseName, "dyedream_world missing"));
            clearSlot(player, "ring", 0);
            return;
        }
        player.teleportTo(dyed, player.getX(), Math.max(60, player.getY()), player.getZ(),
                player.getYRot(), player.getXRot());

        double e0 = player.getData(PDAttachments.PLAYER_MELTDREAM_ENERGY).meltDreamEnergy();

        int old = player.tickCount;
        player.tickCount = 20;
        if (stack.getItem() instanceof ICurioItem curio) {
            curio.curioTick(new SlotContext("ring", player, 0, false, true), stack);
        }
        player.tickCount = old;

        double e1 = player.getData(PDAttachments.PLAYER_MELTDREAM_ENERGY).meltDreamEnergy();
        double delta = e1 - e0;
        boolean pass = eq(delta, 0.0025);

        player.teleportTo(origin, player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
        clearSlot(player, "ring", 0);

        out.accept(new Result(pass, caseName,
                String.format(Locale.ROOT, "energyDelta=%.6f (want exactly +0.0025 / +0.15 per min)", delta)));
    }

    // =====================================================================
    // 世界树种荚：描述 y>160 等条件；能量 +360/h = +0.1/s；exhaustion +0.4/s
    // =====================================================================

    private static void testWorldtreeSeedpod(ServerPlayer player, Consumer<Result> out) {
        String caseName = "worldtree-seedpod";
        Item it = item("worldtree_seedpod");
        if (it == null) {
            out.accept(new Result(false, caseName, "not registered"));
            return;
        }

        ServerLevel dyed = findLevel(player, "dyedream_world");
        ServerLevel origin = player.serverLevel();
        if (dyed == null) {
            out.accept(new Result(false, caseName, "dyedream_world missing"));
            return;
        }

        var leavesBlock = BuiltInRegistries.BLOCK.getOptional(
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dyedream_worldtree_leaves")).orElse(null);
        if (leavesBlock == null) {
            out.accept(new Result(false, caseName, "dyedream_worldtree_leaves block missing"));
            return;
        }

        clearSlot(player, "charm", 0);
        ItemStack stack = new ItemStack(it);
        equip(player, "charm", 0, stack);

        double tx = 8.5;
        double ty = 170.0;
        double tz = 8.5;

        // 设时间和方块（注意：setDayTime 在 test-audit 世界中运行时可能不生效，
        // 该测试项需手动验证）
        long prevTime = dyed.getDayTime();
        dyed.setDayTime(1000L);

        BlockPos feet = BlockPos.containing(tx, ty - 1, tz);
        var prev = dyed.getBlockState(feet);
        dyed.setBlock(feet, leavesBlock.defaultBlockState(), 3);
        for (int y = feet.getY() + 1; y < feet.getY() + 8; y++) {
            BlockPos p = new BlockPos(feet.getX(), y, feet.getZ());
            if (!dyed.getBlockState(p).isAir()) {
                dyed.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
            }
        }

        player.teleportTo(dyed, tx, ty, tz, player.getYRot(), player.getXRot());

        float foodEx0 = player.getFoodData().getExhaustionLevel();
        double e0 = player.getData(PDAttachments.PLAYER_MELTDREAM_ENERGY).meltDreamEnergy();

        int old = player.tickCount;
        player.tickCount = 20;
        if (stack.getItem() instanceof ICurioItem curio) {
            curio.curioTick(new SlotContext("charm", player, 0, false, true), stack);
        }
        player.tickCount = old;

        float foodEx1 = player.getFoodData().getExhaustionLevel();
        double e1 = player.getData(PDAttachments.PLAYER_MELTDREAM_ENERGY).meltDreamEnergy();
        double dEnergy = e1 - e0;
        float dEx = foodEx1 - foodEx0;

        boolean energyOk = eq(dEnergy, 0.1);
        boolean exOk = Math.abs(dEx - 0.4f) < 1e-4f;

        dyed.setBlock(feet, prev, 3);
        dyed.setDayTime(prevTime);
        player.teleportTo(origin, origin.getSharedSpawnPos().getX() + 0.5,
                origin.getSharedSpawnPos().getY(), origin.getSharedSpawnPos().getZ() + 0.5,
                player.getYRot(), player.getXRot());
        clearSlot(player, "charm", 0);

        boolean pass = energyOk && exOk;
        out.accept(new Result(pass, caseName,
                String.format(Locale.ROOT,
                        "energyDelta=%.4f (want +0.1) exhaustionDelta=%.4f (want +0.4) y=%.1f",
                        dEnergy, dEx, ty)));
    }

    // =====================================================================
    // 塞西莉亚：描述 15% / 5s 无敌与回复 / 10s 移速跳跃 / 重置瞬身 CD
    // 原版精确：抗性 amp4 d100、再生 amp3 d100、移速 amp2 d200、跳跃 amp1 d200
    // =====================================================================

    private static void testCeciliaCare(ServerPlayer player, Consumer<Result> out) {
        String caseName = "cecilia-care";
        Item it = item("ceciliacare_charm");
        Item paleItem = item("turn_pale_cecilia");
        if (it == null) {
            out.accept(new Result(false, caseName, "ceciliacare_charm not registered"));
            return;
        }
        if (paleItem == null) {
            out.accept(new Result(false, caseName, "turn_pale_cecilia not registered"));
            return;
        }
        if (!(it instanceof CeciliacareCharmItem charm)) {
            out.accept(new Result(false, caseName, "not CeciliacareCharmItem"));
            return;
        }

        float savedHealth = player.getHealth();
        float maxHp = player.getMaxHealth();
        if (maxHp <= 0.0f) {
            out.accept(new Result(false, caseName, "maxHealth <= 0"));
            return;
        }

        clearCeciliaState(player, it, paleItem);
        SlotContext ctx = new SlotContext("charm", player, 0, false, true);

        // A. 高血不得触发
        ItemStack highStack = new ItemStack(it);
        equip(player, "charm", 0, highStack);
        player.setHealth(maxHp);
        int paleBeforeHigh = countItem(player, paleItem);
        try {
            charm.curioTick(ctx, highStack);
        } catch (Throwable t) {
            restoreCeciliaState(player, it, paleItem, savedHealth);
            out.accept(new Result(false, caseName, "high-hp threw " + t.getClass().getSimpleName()));
            return;
        }
        boolean stillHigh = isCurioInSlot(player, "charm", 0, it);
        int paleAfterHigh = countItem(player, paleItem);
        boolean highFired = !stillHigh || paleAfterHigh > paleBeforeHigh
                || player.hasEffect(MobEffects.DAMAGE_RESISTANCE)
                || player.hasEffect(MobEffects.REGENERATION);
        if (highFired) {
            restoreCeciliaState(player, it, paleItem, savedHealth);
            out.accept(new Result(false, caseName,
                    String.format("FAIL high-hp fired equipped=%s paleDelta=%d hp=%.1f/%.1f",
                            stillHigh, paleAfterHigh - paleBeforeHigh, player.getHealth(), maxHp)));
            return;
        }

        // B. 低血 ≤15%
        clearCeciliaState(player, it, paleItem);
        player.addEffect(new MobEffectInstance(PDEffects.TELEPORTATION_BUFF.holder(), 200, 0, false, false, true));

        ItemStack lowStack = new ItemStack(it);
        equip(player, "charm", 0, lowStack);
        float lowHp = Math.max(1.0f, maxHp * 0.10f);
        player.setHealth(lowHp);
        int paleBeforeLow = countItem(player, paleItem);

        try {
            charm.curioTick(ctx, lowStack);
        } catch (Throwable t) {
            restoreCeciliaState(player, it, paleItem, savedHealth);
            out.accept(new Result(false, caseName, "low-hp threw " + t.getClass().getSimpleName()));
            return;
        }

        boolean stillLow = isCurioInSlot(player, "charm", 0, it);
        int paleAfterLow = countItem(player, paleItem);
        boolean gotPale = paleAfterLow == paleBeforeLow + 1;

        MobEffectInstance resist = player.getEffect(MobEffects.DAMAGE_RESISTANCE);
        MobEffectInstance regen = player.getEffect(MobEffects.REGENERATION);
        MobEffectInstance speed = player.getEffect(MobEffects.MOVEMENT_SPEED);
        MobEffectInstance jump = player.getEffect(MobEffects.JUMP);

        // duration 必须全等 100 / 200（描述 5s / 10s）
        boolean resistOk = resist != null && resist.getAmplifier() == 4 && resist.getDuration() == 100;
        boolean regenOk = regen != null && regen.getAmplifier() == 3 && regen.getDuration() == 100;
        boolean speedOk = speed != null && speed.getAmplifier() == 2 && speed.getDuration() == 200;
        boolean jumpOk = jump != null && jump.getAmplifier() == 1 && jump.getDuration() == 200;
        boolean tpCleared = !player.hasEffect(PDEffects.TELEPORTATION_BUFF.holder());
        boolean consumed = !stillLow;

        boolean lowOk = consumed && gotPale && resistOk && regenOk && speedOk && jumpOk && tpCleared;

        String detail = String.format(Locale.ROOT,
                "highOk=true lowHp=%.1f/%.1f consumed=%s pale+%d resist=%s regen=%s speed=%s jump=%s tpCleared=%s "
                        + "(want resist amp4/d100, regen amp3/d100, speed amp2/d200, jump amp1/d200, pale+1 exact)",
                lowHp, maxHp, consumed, paleAfterLow - paleBeforeLow,
                fmtEffect(resist), fmtEffect(regen), fmtEffect(speed), fmtEffect(jump), tpCleared);

        restoreCeciliaState(player, it, paleItem, savedHealth);
        out.accept(new Result(lowOk, caseName, detail));
    }

    // =====================================================================
    // 反击 buff：描述 攻击力+3、战技倍率+50%；回避成功持续 0:02=40t（事件侧）
    // =====================================================================

    private static void testCounterAttackBuffDefinition(Consumer<Result> out) {
        String caseName = "counterattack-buff-def";
        var effect = PDEffects.COUNTERATTACK_BUFF.get();
        if (effect == null) {
            out.accept(new Result(false, caseName, "counterattack_buff missing"));
            return;
        }

        boolean atkOk = false;
        boolean skillOk = false;
        List<String> seen = new ArrayList<>();
        effect.createModifiers(0, (attr, mod) -> {
            seen.add(attrId(attr) + ":" + mod.amount() + "/" + mod.operation());
        });
        // re-scan with assignment
        final boolean[] flags = {false, false};
        effect.createModifiers(0, (attr, mod) -> {
            if (attr.is(Attributes.ATTACK_DAMAGE)
                    && eq(mod.amount(), 3.0)
                    && mod.operation() == AttributeModifier.Operation.ADD_VALUE) {
                flags[0] = true;
            }
            if (attr.equals(PDAttributes.SKILLMULTIPLIER)
                    && eq(mod.amount(), 0.5)
                    && mod.operation() == AttributeModifier.Operation.ADD_VALUE) {
                flags[1] = true;
            }
        });
        atkOk = flags[0];
        skillOk = flags[1];

        boolean pass = atkOk && skillOk;
        out.accept(new Result(pass, caseName,
                "want ATK+3 ADD, skillmult+0.5 ADD (desc +3 / +50%); actual=["
                        + String.join("; ", seen) + "]; evadeApplyDuration=40t in PDEffectEvents"));
    }

    // =====================================================================
    // 描述文案中的关键数字必须出现在 tooltip
    // =====================================================================

    private static void testDescriptionNumbers(Consumer<Result> out) {
        checkItemTooltipNumbers(out, "evasion_cloak",
                List.of("90", "0.6", "300", "10"), "desc-evasion_cloak");
        checkItemTooltipNumbers(out, "turnback_cloak",
                List.of("90", "6", "300", "10"), "desc-turnback_cloak");
        checkItemTooltipNumbers(out, "counter_ring",
                List.of("0:02", "+3", "50%"), "desc-counter_ring");
        checkItemTooltipNumbers(out, "cross_necklace",
                List.of("0.4"), "desc-cross_necklace");
        checkItemTooltipNumbers(out, "terra_charm",
                List.of("60%", "30%", "0.2"), "desc-terra_charm");
        checkItemTooltipNumbers(out, "worldtree_seedpod",
                List.of("160", "360"), "desc-worldtree_seedpod");
        checkItemTooltipNumbers(out, "meltdream_energy_0_ring",
                List.of("0.15"), "desc-meltdream_energy_0_ring");
        checkItemTooltipNumbers(out, "snow_vow_head",
                List.of("7", "+3"), "desc-snow_vow_head");
        checkItemTooltipNumbers(out, "ghost_face_head",
                List.of("10%"), "desc-ghost_face_head");
        checkItemTooltipNumbers(out, "boboji_plume",
                List.of("0.25"), "desc-boboji_plume");
        checkItemTooltipNumbers(out, "ceciliacare_charm",
                List.of("15%", "5"), "desc-ceciliacare_charm");
        checkItemTooltipNumbers(out, "light_butterfly_curio",
                List.of("夜视"), "desc-light_butterfly_curio");
        checkItemTooltipNumbers(out, "bright_butterfly_curio",
                List.of("夜视", "黑暗"), "desc-bright_butterfly_curio");
        checkItemTooltipNumbers(out, "rabbit_0_necklace",
                List.of("跳跃提升I"), "desc-rabbit_0_necklace");
        checkItemTooltipNumbers(out, "fire_0_necklace",
                List.of("火焰", "急迫I"), "desc-fire_0_necklace");
    }

    private static void checkItemTooltipNumbers(Consumer<Result> out, String regName,
                                                List<String> mustContain, String caseName) {
        Item it = item(regName);
        if (it == null) {
            out.accept(new Result(false, caseName, regName + " not registered"));
            return;
        }
        ItemStack stack = new ItemStack(it);
        List<net.minecraft.network.chat.Component> lines = new ArrayList<>();
        try {
            it.appendHoverText(stack, Item.TooltipContext.EMPTY, lines, TooltipFlag.Default.NORMAL);
        } catch (Throwable t) {
            out.accept(new Result(false, caseName, "appendHoverText failed: " + t.getClass().getSimpleName()));
            return;
        }
        String joined = lines.stream()
                .map(net.minecraft.network.chat.Component::getString)
                .reduce("", (a, b) -> a + "\n" + b);
        List<String> missing = new ArrayList<>();
        for (String n : mustContain) {
            if (!joined.contains(n)) {
                missing.add(n);
            }
        }
        boolean pass = missing.isEmpty();
        out.accept(new Result(pass, caseName,
                pass ? "tooltip contains " + mustContain
                        : "tooltip missing " + missing + " || text=" + joined.replace("\n", " | ")));
    }

    // =====================================================================
    // 全量基础装备
    // =====================================================================

    private static void testBasicEquipAllRegistered(ServerPlayer player, Consumer<Result> out) {
        int checked = 0;
        int ok = 0;

        for (CurioAPI.CurioRegistration reg : CurioAPI.getRegisteredCurios()) {
            if ("test_curio".equals(reg.name())) continue;
            Item it = reg.item();
            if (it == null) {
                out.accept(new Result(false, "equip-" + reg.name(), "item() null"));
                checked++;
                continue;
            }

            String slot = reg.slotId();
            clearSlot(player, slot, 0);
            ItemStack st = new ItemStack(it);
            equip(player, slot, 0, st);

            boolean present = isCurioInSlot(player, slot, 0, it);
            checked++;
            if (present) ok++;
            out.accept(new Result(present, "equip-" + reg.name(),
                    present ? "equipped in " + slot : "not detected after equip into " + slot));
            clearSlot(player, slot, 0);
        }

        Item melt = item("meltdream_energy_0_ring");
        if (melt != null) {
            clearSlot(player, "ring", 0);
            equip(player, "ring", 0, new ItemStack(melt));
            boolean present = isCurioInSlot(player, "ring", 0, melt);
            checked++;
            if (present) ok++;
            out.accept(new Result(present, "equip-meltdream_energy_0_ring",
                    present ? "equipped in ring" : "not detected"));
            clearSlot(player, "ring", 0);
        } else {
            checked++;
            out.accept(new Result(false, "equip-meltdream_energy_0_ring", "not registered"));
        }

        out.accept(new Result(ok == checked && checked > 0, "curio-basic-equip-all",
                String.format("%d/%d curios detected after equip", ok, checked)));
    }

    // =====================================================================
    // helpers
    // =====================================================================

    /**
     * 在 Y=-64 挖一个 3x3x4 的地底房间（天空光=0，无需光引擎重算）。
     * 回填时 caller 用 {@code level.setBlock(p, Blocks.STONE.defaultBlockState(), 3)}。
     */
    private static List<BlockPos> digBedrockRoom(ServerLevel level, BlockPos base) {
        List<BlockPos> placed = new ArrayList<>();
        for (int dy = 0; dy <= 3; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos p = base.offset(dx, dy, dz);
                    BlockState st = level.getBlockState(p);
                    if (!st.isAir()) {
                        level.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
                        placed.add(p);
                    }
                }
            }
        }
        return placed;
    }

    private static void clearCeciliaState(ServerPlayer player, Item charmItem, Item paleItem) {
        clearSlot(player, "charm", 0);
        player.removeEffect(MobEffects.DAMAGE_RESISTANCE);
        player.removeEffect(MobEffects.REGENERATION);
        player.removeEffect(MobEffects.MOVEMENT_SPEED);
        player.removeEffect(MobEffects.JUMP);
        player.removeEffect(PDEffects.TELEPORTATION_BUFF.holder());
        removeAllOf(player, paleItem);
        removeAllOf(player, charmItem);
    }

    private static void restoreCeciliaState(ServerPlayer player, Item charmItem, Item paleItem, float savedHealth) {
        clearCeciliaState(player, charmItem, paleItem);
        float max = player.getMaxHealth();
        player.setHealth(Math.min(Math.max(1.0f, savedHealth), max > 0 ? max : savedHealth));
    }

    private static boolean isCurioInSlot(ServerPlayer player, String slotId, int index, Item item) {
        return CuriosApi.getCuriosInventory(player)
                .flatMap(inv -> inv.getStacksHandler(slotId))
                .map(handler -> {
                    var stacks = handler.getStacks();
                    if (index < 0 || index >= stacks.getSlots()) {
                        return false;
                    }
                    ItemStack s = stacks.getStackInSlot(index);
                    return !s.isEmpty() && s.is(item);
                })
                .orElse(false);
    }

    private static void equip(ServerPlayer player, String slot, int index, ItemStack stack) {
        CuriosApi.getCuriosInventory(player).ifPresent(inv -> inv.setEquippedCurio(slot, index, stack));
    }

    private static void clearSlot(ServerPlayer player, String slot, int index) {
        CuriosApi.getCuriosInventory(player).ifPresent(inv -> inv.setEquippedCurio(slot, index, ItemStack.EMPTY));
    }

    private static int countItem(ServerPlayer player, Item item) {
        int n = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (!s.isEmpty() && s.is(item)) {
                n += s.getCount();
            }
        }
        return n;
    }

    private static void removeAllOf(ServerPlayer player, Item item) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (!s.isEmpty() && s.is(item)) {
                player.getInventory().setItem(i, ItemStack.EMPTY);
            }
        }
    }

    private static String fmtEffect(MobEffectInstance e) {
        if (e == null) {
            return "none";
        }
        return "amp" + e.getAmplifier() + "/d" + e.getDuration();
    }

    private static String fmtMod(Holder<Attribute> key, AttributeModifier mod) {
        return attrId(key) + ":" + mod.amount() + "/" + mod.operation();
    }

    private static String attrId(Holder<Attribute> key) {
        return key.unwrapKey().map(k -> k.location().toString()).orElse(key.value().toString());
    }

    private static boolean eq(double a, double b) {
        return Math.abs(a - b) < EPS;
    }

    private static Item item(String name) {
        return BuiltInRegistries.ITEM
                .getOptional(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, name))
                .orElse(null);
    }

    private static ServerLevel findLevel(ServerPlayer player, String path) {
        for (ResourceKey<Level> key : player.server.levelKeys()) {
            if (key.location().getNamespace().equals(PasterDreamMod.MOD_ID)
                    && key.location().getPath().equals(path)) {
                return player.server.getLevel(key);
            }
        }
        return null;
    }
}
