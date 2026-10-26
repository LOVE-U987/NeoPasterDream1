package com.pasterdream.pasterdreammod.test;

import com.pasterdream.pasterdreammod.api.effect.particle.ParticleEmitterData;
import com.pasterdream.pasterdreammod.api.effect.particle.ParticleLimits;
import com.pasterdream.pasterdreammod.api.effect.particle.processors.BuiltinEmitterProcessors;
import com.pasterdream.pasterdreammod.api.effect.particle.processors.EmptyEmitterProcessor;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import net.minecraft.SharedConstants;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Field;

/**
 * 粒子发射器网络安全回归测试运行器
 * <p>
 * 覆盖 ParticleLimits 的校验与预算纯函数、ParticleEmitterData 的构造校验，
 * 以及完整报文的解码路径。运行方式：
 * {@code .\gradlew :PasterDream:runParticleEmitterSecurityTest}
 * <p>
 * 输出使用 {@code [PASS]} / {@code [FAIL]} / {@code [SKIP]} 文本标记；任一非跳过
 * 失败以退出码 1 结束。整包解码优先走标准 {@code Bootstrap.bootStrap()}；独立
 * JavaExec 缺少 FML 加载上下文时回退到粒子注册表最小初始化。两种引导都失败时
 * 相关用例标记 {@code [SKIP]}，不得计为通过。
 */
public final class ParticleEmitterSecurityTestRunner {

    private static final String PASS = "[PASS] ";
    private static final String FAIL = "[FAIL] ";
    private static final String SKIP = "[SKIP] ";

    private static int passed;
    private static int failed;
    private static int skipped;

    private ParticleEmitterSecurityTestRunner() {
    }

    /**
     * 测试入口。
     *
     * @param args 未使用
     */
    public static void main(String[] args) {
        printHeader("粒子发射器网络安全回归测试");

        printHeader("1. 解码校验函数");
        testS2();
        testS3();
        testS4();
        testS5();
        testS6();
        testS7();

        printHeader("2. 预算与槽位");
        testS17();
        testS18();
        testS19();
        testS20();
        testS21();
        testS22();
        testS23();

        printHeader("3. 构造校验");
        testS9();
        testS10();
        testS11();
        testS13();

        printHeader("4. 整包解码");
        boolean ready = bootstrap();
        if (ready) {
            group4();
        } else {
            skip("S1", "注册表引导失败，整包用例跳过");
            skip("S8", "注册表引导失败，整包用例跳过");
            skip("S12", "注册表引导失败，整包用例跳过");
            skip("S14", "注册表引导失败，整包用例跳过");
            skip("S15", "注册表引导失败，整包用例跳过");
            skip("S16", "注册表引导失败，整包用例跳过");
        }

        printSummary();
        if (failed > 0) {
            System.exit(1);
        }
    }

    // ==================== 1. 解码校验函数 ====================

    private static void testS2() {
        expectDecoder("S2", "类型数超过上限", () ->
                ParticleLimits.requireTypeCount(ParticleLimits.MAX_PARTICLE_TYPES + 1, 1024));
    }

    private static void testS3() {
        expectDecoder("S3", "类型数为零", () -> ParticleLimits.requireTypeCount(0, 1024));
    }

    private static void testS4() {
        expectDecoder("S4", "类型数超过剩余字节可容纳量", () -> ParticleLimits.requireTypeCount(3, 5));
    }

    private static void testS5() {
        expectDecoder("S5", "数据长度为负", () -> ParticleLimits.requireDataLength(-1, 1024));
    }

    private static void testS6() {
        expectDecoder("S6", "数据长度超过单条上限", () ->
                ParticleLimits.requireDataLength(ParticleLimits.MAX_PARTICLE_DATA_BYTES + 1,
                        ParticleLimits.MAX_PARTICLE_DATA_BYTES + 1));
    }

    private static void testS7() {
        expectDecoder("S7", "数据长度超过剩余字节", () -> ParticleLimits.requireDataLength(10, 9));
    }

    // ==================== 2. 预算与槽位 ====================

    private static void testS17() {
        expectInt("S17", "零 tick 预算返回 0",
                ParticleLimits.clampSpawnCount(Integer.MAX_VALUE, 0, 1024), 0);
    }

    private static void testS18() {
        expectInt("S18a", "预算 5 夹取到 5",
                ParticleLimits.clampSpawnCount(8, 5, 1024), 5);
        expectInt("S18b", "容量 3 夹取到 3",
                ParticleLimits.clampSpawnCount(8, 1024, 3), 3);
    }

    private static void testS19() {
        expectInt("S19", "超大输入夹取到单发射器上限",
                ParticleLimits.clampSpawnCount(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE),
                ParticleLimits.MAX_PARTICLES_PER_TICK);
    }

    private static void testS20() {
        expectBoolean("S20a", "未达发射器上限可接受",
                ParticleLimits.canAcceptEmitter(ParticleLimits.MAX_ACTIVE_EMITTERS - 1), true);
        expectBoolean("S20b", "达到发射器上限拒绝",
                ParticleLimits.canAcceptEmitter(ParticleLimits.MAX_ACTIVE_EMITTERS), false);
    }

    private static void testS21() {
        expectInt("S21", "存量容量耗尽返回 0",
                ParticleLimits.clampSpawnCount(8, 1024, 0), 0);
    }

    private static void testS22() {
        expectInt("S22", "负容量夹取为 0",
                ParticleLimits.clampSpawnCount(8, 1024, -1), 0);
    }

    private static void testS23() {
        expectInt("S23", "负预算夹取为 0",
                ParticleLimits.clampSpawnCount(8, -1, 1024), 0);
    }

    // ==================== 3. 构造校验 ====================

    private static void testS9() {
        SimpleParticleType particle = new SimpleParticleType(false);
        expectIllegalArgument("S9a", "生命周期为负", () ->
                ParticleEmitterData.builder(particle).lifetime(-1).build());
        expectIllegalArgument("S9b", "生命周期超过上限", () ->
                ParticleEmitterData.builder(particle)
                        .lifetime(ParticleLimits.MAX_LIFETIME_TICKS + 1)
                        .build());
    }

    private static void testS10() {
        SimpleParticleType particle = new SimpleParticleType(false);
        expectIllegalArgument("S10a", "每 tick 粒子数为负", () ->
                ParticleEmitterData.builder(particle).particlesPerTick(-1).build());
        expectIllegalArgument("S10b", "每 tick 粒子数超过上限", () ->
                ParticleEmitterData.builder(particle)
                        .particlesPerTick(ParticleLimits.MAX_PARTICLES_PER_TICK + 1)
                        .build());
    }

    private static void testS11() {
        SimpleParticleType particle = new SimpleParticleType(false);
        expectIllegalArgument("S11a", "坐标为 NaN", () ->
                ParticleEmitterData.builder(particle).position(new Vec3(Double.NaN, 0, 0)).build());
        expectIllegalArgument("S11b", "坐标超过绝对值上限", () ->
                ParticleEmitterData.builder(particle)
                        .position(new Vec3(ParticleLimits.MAX_ABSOLUTE_COORDINATE + 1, 0, 0))
                        .build());
    }

    private static void testS13() {
        try {
            SimpleParticleType first = new SimpleParticleType(false);
            SimpleParticleType second = new SimpleParticleType(false);
            ParticleEmitterData data = ParticleEmitterData.builder(first)
                    .position(Vec3.ZERO)
                    .lifetime(20)
                    .particlesPerTick(1)
                    .addParticle(second)
                    .build();
            if (data.particleTypes().size() != 2) {
                fail("S13", "合法构建后类型数量不符: " + data.particleTypes().size());
                return;
            }
            try {
                data.particleTypes().add(first);
                fail("S13", "类型列表仍可修改");
                return;
            } catch (UnsupportedOperationException expected) {
                pass("S13", "合法构建成功且类型列表不可变");
            }
        } catch (Throwable t) {
            fail("S13", "合法构建异常: " + t);
        }
    }

    // ==================== 4. 整包解码 ====================

    private static void group4() {
        testS1();
        testS8();
        if (!particleTypesReady()) {
            skip("S12", "注册表缺少 minecraft:soul");
            skip("S14", "注册表缺少可用的 unit 粒子类型");
            skip("S15", "注册表缺少 minecraft:dust");
            skip("S16", "注册表缺少可用的 unit 粒子类型");
            return;
        }
        testS12();
        testS14();
        testS15();
        testS16();
    }

    private static void testS1() {
        RegistryFriendlyByteBuf buf = newBuffer();
        try {
            writePrefix(buf);
            buf.writeVarInt(Integer.MAX_VALUE);
            expectDecoder("S1", "巨量类型数在分配前被拒绝", () -> decode(buf));
        } finally {
            buf.release();
        }
    }

    private static void testS8() {
        RegistryFriendlyByteBuf buf = newBuffer();
        try {
            writePrefix(buf);
            buf.writeVarInt(1);
            buf.writeVarInt(Integer.MAX_VALUE);
            buf.writeVarInt(0);
            expectDecoder("S8", "全部类型未知导致空列表被拒绝", () -> decode(buf));
        } finally {
            buf.release();
        }
    }

    private static void testS12() {
        RegistryFriendlyByteBuf buf = newBuffer();
        try {
            writePrefix(buf, 100, 8);
            buf.writeVarInt(1);
            buf.writeVarInt(BuiltInRegistries.PARTICLE_TYPE.getId(ParticleTypes.SOUL));
            buf.writeVarInt(0);
            ParticleEmitterData data = decode(buf);
            if (data.position().equals(Vec3.ZERO)
                    && data.lifetime() == 100
                    && data.particlesPerTick() == 8
                    && data.particleTypes().size() == 1
                    && data.particleTypes().get(0) == ParticleTypes.SOUL) {
                pass("S12", "合法报文解码成功且字段一致");
            } else {
                fail("S12", "合法报文解码结果不一致");
            }
        } catch (Throwable t) {
            fail("S12", "合法报文解码异常: " + t);
        } finally {
            buf.release();
        }
    }

    private static void testS14() {
        int unitId = firstUnitParticleId();
        if (unitId < 0) {
            skip("S14", "注册表缺少 id 小于 128 的 unit 粒子类型");
            return;
        }
        RegistryFriendlyByteBuf buf = newBuffer();
        try {
            writePrefix(buf);
            int size = 4;
            buf.writeVarInt(size);
            int entriesStart = buf.writerIndex();
            for (int i = 0; i < size; i++) {
                buf.writeVarInt(unitId);
                buf.writeVarInt(0);
            }
            int entriesBytes = buf.writerIndex() - entriesStart;
            ParticleEmitterData data = decode(buf);
            if (data.particleTypes().size() == size && size == entriesBytes / 2) {
                pass("S14", "类型数等于剩余字节一半的正向边界解码成功");
            } else {
                fail("S14", "正向边界解码结果不一致: size=" + size + ", entriesBytes=" + entriesBytes);
            }
        } catch (Throwable t) {
            fail("S14", "正向边界解码异常: " + t);
        } finally {
            buf.release();
        }
    }

    private static void testS15() {
        int dustId = BuiltInRegistries.PARTICLE_TYPE.getId(ParticleTypes.DUST);
        RegistryFriendlyByteBuf buf = newBuffer();
        try {
            writePrefix(buf);
            buf.writeVarInt(1);
            buf.writeVarInt(dustId);
            buf.writeVarInt(16);
            buf.writeFloat(1.0f);
            buf.writeFloat(0.0f);
            buf.writeFloat(0.0f);
            buf.writeFloat(1.0f);
            ParticleEmitterData data = decode(buf);
            if (data.particleTypes().size() == 1
                    && data.particleTypes().get(0) instanceof DustParticleOptions) {
                pass("S15", "数据长度等于剩余字节的正向边界解码成功");
            } else {
                fail("S15", "带数据粒子解码结果不一致");
            }
        } catch (Throwable t) {
            fail("S15", "带数据粒子解码异常: " + t);
        } finally {
            buf.release();
        }
    }

    private static void testS16() {
        int first = firstUnitParticleId();
        int second = secondUnitParticleId(first);
        if (first < 0 || second < 0) {
            skip("S16", "注册表缺少两个不同 unit 粒子类型");
            return;
        }
        RegistryFriendlyByteBuf buf = newBuffer();
        try {
            writePrefix(buf);
            buf.writeVarInt(2);
            buf.writeVarInt(first);
            buf.writeVarInt(0);
            buf.writeVarInt(second);
            buf.writeVarInt(0);
            ParticleEmitterData data = decode(buf);
            if (data.particleTypes().size() == 2) {
                pass("S16", "多类型合法报文解码成功");
            } else {
                fail("S16", "多类型解码结果不一致");
            }
        } catch (Throwable t) {
            fail("S16", "多类型解码异常: " + t);
        } finally {
            buf.release();
        }
    }

    // ==================== 辅助方法 ====================

    private static boolean bootstrap() {
        try {
            SharedConstants.tryDetectVersion();
            if (!tryFullBootstrap()) {
                initParticleRegistryOnly();
            }
            BuiltinEmitterProcessors.registerAll();
            return particleTypesReady();
        } catch (Throwable t) {
            System.out.println("  注册表引导失败: " + t);
            t.printStackTrace(System.out);
            return false;
        }
    }

    /**
     * 优先走标准引导；独立 JavaExec 缺少 FML 加载上下文时该方法会失败。
     *
     * @return 成功返回 true
     */
    private static boolean tryFullBootstrap() {
        try {
            Bootstrap.bootStrap();
            return true;
        } catch (Throwable t) {
            System.out.println("  标准 Bootstrap 不可用，改用粒子注册表最小初始化: " + t);
            return false;
        }
    }

    /**
     * 只初始化粒子解码所需的注册表：置位 vanilla 引导标志（使
     * {@code BuiltInRegistries} 的静态初始化通过检查），再触发
     * {@code ParticleTypes} 静态注册。不触碰 Blocks，因而不会触发
     * NeoForge {@code FeatureFlagLoader} 对 FML 加载上下文的依赖。
     *
     * @throws ReflectiveOperationException 无法置位引导标志
     */
    private static void initParticleRegistryOnly() throws ReflectiveOperationException {
        Field bootstrapped = Bootstrap.class.getDeclaredField("isBootstrapped");
        bootstrapped.setAccessible(true);
        bootstrapped.setBoolean(null, true);
        Class.forName(BuiltInRegistries.class.getName(), true,
                ParticleEmitterSecurityTestRunner.class.getClassLoader());
        // 触发 ParticleTypes 静态初始化，向 PARTICLE_TYPE 注册全部原版粒子
        Object ignored = ParticleTypes.SOUL;
    }

    private static boolean particleTypesReady() {
        return BuiltInRegistries.PARTICLE_TYPE
                .getOptional(ResourceLocation.withDefaultNamespace("soul")).isPresent()
                && BuiltInRegistries.PARTICLE_TYPE
                .getOptional(ResourceLocation.withDefaultNamespace("dust")).isPresent();
    }

    private static int firstUnitParticleId() {
        for (ParticleType<?> type : BuiltInRegistries.PARTICLE_TYPE) {
            if (!(type instanceof SimpleParticleType)) {
                continue;
            }
            int id = BuiltInRegistries.PARTICLE_TYPE.getId(type);
            if (id >= 0 && id < 128) {
                return id;
            }
        }
        return -1;
    }

    private static int secondUnitParticleId(int excluded) {
        for (ParticleType<?> type : BuiltInRegistries.PARTICLE_TYPE) {
            if (!(type instanceof SimpleParticleType)) {
                continue;
            }
            int id = BuiltInRegistries.PARTICLE_TYPE.getId(type);
            if (id >= 0 && id < 128 && id != excluded) {
                return id;
            }
        }
        return -1;
    }

    private static RegistryFriendlyByteBuf newBuffer() {
        return new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY);
    }

    private static void writePrefix(RegistryFriendlyByteBuf buf) {
        writePrefix(buf, 20, 1);
    }

    private static void writePrefix(RegistryFriendlyByteBuf buf, int lifetime, int particlesPerTick) {
        buf.writeDouble(0.0);
        buf.writeDouble(0.0);
        buf.writeDouble(0.0);
        buf.writeInt(lifetime);
        buf.writeInt(particlesPerTick);
        buf.writeResourceLocation(EmptyEmitterProcessor.Type.ID);
    }

    private static ParticleEmitterData decode(RegistryFriendlyByteBuf buf) {
        return ParticleEmitterData.STREAM_CODEC.decode(buf);
    }

    private static void expectDecoder(String id, String desc, Runnable action) {
        try {
            action.run();
            fail(id, desc + ": 未抛出 DecoderException");
        } catch (DecoderException expected) {
            pass(id, desc);
        } catch (Throwable t) {
            fail(id, desc + ": 抛出 " + t.getClass().getName());
        }
    }

    private static void expectIllegalArgument(String id, String desc, Runnable action) {
        try {
            action.run();
            fail(id, desc + ": 未抛出 IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            pass(id, desc);
        } catch (Throwable t) {
            fail(id, desc + ": 抛出 " + t.getClass().getName());
        }
    }

    private static void expectInt(String id, String desc, int actual, int expected) {
        if (actual == expected) {
            pass(id, desc + " = " + actual);
        } else {
            fail(id, desc + ": 期望 " + expected + "，实际 " + actual);
        }
    }

    private static void expectBoolean(String id, String desc, boolean actual, boolean expected) {
        if (actual == expected) {
            pass(id, desc);
        } else {
            fail(id, desc + ": 期望 " + expected + "，实际 " + actual);
        }
    }

    private static void pass(String id, String message) {
        passed++;
        System.out.println("  " + PASS + id + " " + message);
    }

    private static void fail(String id, String message) {
        failed++;
        System.out.println("  " + FAIL + id + " " + message);
    }

    private static void skip(String id, String message) {
        skipped++;
        System.out.println("  " + SKIP + id + " " + message);
    }

    private static void printHeader(String title) {
        System.out.println();
        System.out.println("== " + title + " ==");
    }

    private static void printSummary() {
        System.out.println();
        System.out.println("== 结果汇总 ==");
        System.out.println("  总计: " + (passed + failed + skipped)
                + " | 通过: " + passed + " | 失败: " + failed + " | 跳过: " + skipped);
    }
}
