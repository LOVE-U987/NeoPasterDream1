package com.pasterdream.pasterdreammod.client.sky.content;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import com.pasterdream.pasterdreammod.api.client.sky.SkyContent;
import com.pasterdream.pasterdreammod.api.client.sky.SkyboxRenderContext;
import com.pasterdream.pasterdreammod.client.sky.math.SkyColor;
import com.pasterdream.pasterdreammod.client.sky.math.SkyPoint;
import com.pasterdream.pasterdreammod.client.sky.render.SkyGeometry;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * 流星内容 —— 周期性划过天空的流星，带渐变拖尾
 * <p>
 * 每个 spawner 有独立的周期/相位/方向/亮度，按游戏时间取模
 * 自动"发射"。渲染时头部为亮核，尾部 12 段渐隐渐变，使用
 * 加法混合（SRC_ALPHA, ONE）呈现发光效果。
 */
public class ShootingStarSkyContent implements SkyContent {

    /** 头部核心颜色（亮白青） */
    private static final SkyColor CORE_COLOR = new SkyColor(0.92F, 1.0F, 1.0F);
    /** 拖尾分段数 */
    private static final int TRAIL_SEGMENTS = 12;
    /** 拖尾本地长度（沿方向的比例） */
    private static final float TRAIL_LOCAL_LENGTH = 0.28F;

    private final ResourceLocation id;
    private final int priority;
    private final SkyColor color;
    private final int intervalTicks;
    private final int durationTicks;
    private final long seed;
    private final List<ShootingStarSpawner> spawners;

    /**
     * @param id            内容标识
     * @param priority      绘制优先级
     * @param count         流星 spawner 数量
     * @param color         流星颜色
     * @param seed          随机种子
     * @param intervalTicks 发射间隔（tick）
     * @param durationTicks 单颗流星存在时长（tick）
     */
    public ShootingStarSkyContent(
            ResourceLocation id, int priority, int count, SkyColor color, long seed, int intervalTicks, int durationTicks
    ) {
        this.id = id;
        this.priority = priority;
        this.color = color;
        this.intervalTicks = Math.max(1, intervalTicks);
        this.durationTicks = Math.max(1, durationTicks);
        this.seed = seed;
        this.spawners = makeSpawners(count, seed, this.intervalTicks, this.durationTicks);
    }

    @Override
    public ResourceLocation id() {
        return this.id;
    }

    @Override
    public int priority() {
        return this.priority;
    }

    @Override
    public void render(SkyboxRenderContext context, float alpha) {
        List<VisibleShootingStar> visible = visibleShootingStars(context);
        if (visible.isEmpty()) {
            return;
        }
        RenderSystem.disableCull();
        // 与 Stellara 一致：不设置混合模式，依赖外层 SkyboxRenderer 统一的标准混合
        context.poseStack().pushPose();
        // 反旋天空角，使流星固定在世界方向（不随昼夜旋转）
        context.poseStack().mulPose(Axis.YP.rotationDegrees(-context.skyAngle()));
        Matrix4f matrix = context.poseStack().last().pose();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for (VisibleShootingStar star : visible) {
            addTrail(buffer, matrix, star, this.color, alpha);
        }
        // 安全提交：避免空缓冲崩溃
        SkyGeometry.drawIfNotEmpty(buffer);
        context.poseStack().popPose();
        RenderSystem.enableCull();
    }

    /**
     * 计算当前可见的流星集合
     *
     * @param context 渲染上下文
     * @return 可见流星列表
     */
    private List<VisibleShootingStar> visibleShootingStars(SkyboxRenderContext context) {
        if (this.spawners.isEmpty()) {
            return List.of();
        }
        List<VisibleShootingStar> visible = new ArrayList<>();
        for (int index = 0; index < this.spawners.size(); index++) {
            ShootingStarSpawner spawner = this.spawners.get(index);
            float cycleTime = context.renderTime() + spawner.offset();
            int cycleIndex = Mth.floor(cycleTime / spawner.period());
            float localTicks = cycleTime - cycleIndex * spawner.period();
            if (localTicks > spawner.lifetime()) {
                continue;
            }
            ShootingStar shootingStar = makeShootingStar(
                    spawner, this.seed + index * 104729L + cycleIndex * 130363L
            );
            float local = localTicks / spawner.lifetime();
            float fade = Mth.sin(local * 3.1415927F);
            SkyPoint head = point(shootingStar, local);
            visible.add(new VisibleShootingStar(
                    shootingStar, local, head, shootingStar.angle(),
                    shootingStar.size(), shootingStar.brightness(), fade
            ));
        }
        return visible;
    }

    /**
     * 生成 spawner 列表
     *
     * @param count         数量
     * @param seed          种子
     * @param intervalTicks 间隔
     * @param durationTicks 时长
     * @return spawner 列表
     */
    private static List<ShootingStarSpawner> makeSpawners(int count, long seed, int intervalTicks, int durationTicks) {
        List<ShootingStarSpawner> spawners = new ArrayList<>();
        LegacyRandomSource random = new LegacyRandomSource(seed);
        for (int i = 0; i < count; i++) {
            float period = intervalTicks * (0.6F + random.nextFloat() * 0.8F);
            float lifetime = durationTicks * (0.6F + random.nextFloat() * 0.8F);
            float offset = random.nextFloat() * period;
            float yaw = (float) (Math.PI * (0.15 + random.nextFloat() * 0.7));
            float pitch = 0.35F + random.nextFloat() * 0.55F;
            float angle = random.nextFloat() * 6.2831855F;
            float speed = 0.55F + random.nextFloat() * 0.45F;
            float size = 2.2F + random.nextFloat() * 2.2F;
            float brightness = 0.55F + random.nextFloat() * 0.45F;
            spawners.add(new ShootingStarSpawner(period, lifetime, offset, yaw, pitch, angle, speed, size, brightness));
        }
        return spawners;
    }

    /**
     * 由 spawner + 周期序号派生具体流星（每次发射参数随机微变）
     *
     * @param spawner spawner
     * @param seed    周期种子
     * @return 流星参数
     */
    private static ShootingStar makeShootingStar(ShootingStarSpawner spawner, long seed) {
        LegacyRandomSource random = new LegacyRandomSource(seed);
        float yaw = spawner.yaw() + (random.nextFloat() - 0.5F) * 0.12F;
        float pitch = spawner.pitch() + (random.nextFloat() - 0.5F) * 0.1F;
        float angle = spawner.angle() + (random.nextFloat() - 0.5F) * 0.3F;
        float speed = spawner.speed() * (0.9F + random.nextFloat() * 0.2F);
        float size = spawner.size() * (0.85F + random.nextFloat() * 0.3F);
        float brightness = spawner.brightness() * (0.9F + random.nextFloat() * 0.2F);
        return new ShootingStar(yaw, pitch, angle, speed, size, brightness);
    }

    /**
     * 流星头位置（沿方向线性推进）
     *
     * @param star  流星参数
     * @param local 生命周期进度（0~1）
     * @return 头部球面位置
     */
    private static SkyPoint point(ShootingStar star, float local) {
        return SkyGeometry.point(
                star.yaw() + star.speed() * local * 3.0F,
                star.pitch() - star.speed() * local * 1.2F
        );
    }

    /**
     * 添加流星拖尾（头部亮核 + 渐隐尾迹）
     *
     * @param buffer 顶点缓冲
     * @param matrix 变换矩阵
     * @param star   可见流星
     * @param color  拖尾颜色（来自配置）
     * @param alpha  整体透明度
     */
    private static void addTrail(BufferBuilder buffer, Matrix4f matrix, VisibleShootingStar star, SkyColor color, float alpha) {
        SkyPoint head = star.head();
        float headSize = star.size() * (0.85F + star.fade() * 0.3F);
        // 头部亮核
        SkyGeometry.addBillboard(buffer, matrix, head, headSize * 0.22F, star.angle(), CORE_COLOR, alpha * star.fade() * star.brightness());
        // 拖尾
        for (int i = 0; i < TRAIL_SEGMENTS; i++) {
            float t = (float) (i + 1) / TRAIL_SEGMENTS;
            float trailFade = star.fade() * (1.0F - t) * 0.55F;
            if (trailFade <= 0.003F) {
                continue;
            }
            SkyPoint segment = SkyGeometry.point(
                    star.star().yaw() + star.star().speed() * t * TRAIL_LOCAL_LENGTH * 10.0F + star.star().speed() * star.local() * 3.0F,
                    star.star().pitch() - star.star().speed() * t * TRAIL_LOCAL_LENGTH * 4.0F - star.star().speed() * star.local() * 1.2F
            );
            SkyGeometry.addBillboard(
                    buffer, matrix, segment,
                    headSize * (1.0F - t * 0.7F) * 0.8F,
                    star.angle(),
                    color,
                    alpha * trailFade * star.brightness()
            );
        }
    }

    /**
     * 流星 spawner 静态属性
     *
     * @param period    周期（tick）
     * @param lifetime  存活时长（tick）
     * @param offset    相位偏移
     * @param yaw       偏航角
     * @param pitch     俯仰角
     * @param angle     自旋角
     * @param speed     推进速度
     * @param size      尺寸
     * @param brightness 亮度
     */
    private record ShootingStarSpawner(
            float period, float lifetime, float offset,
            float yaw, float pitch, float angle, float speed, float size, float brightness
    ) {
    }

    /**
     * 单次发射的流星参数（随周期变化）
     *
     * @param yaw        偏航角
     * @param pitch      俯仰角
     * @param angle      自旋角
     * @param speed      推进速度
     * @param size       尺寸
     * @param brightness 亮度
     */
    private record ShootingStar(float yaw, float pitch, float angle, float speed, float size, float brightness) {
    }

    /**
     * 渲染中的可见流星
     *
     * @param star       流星参数
     * @param local      生命周期进度
     * @param head       头部位置
     * @param angle      自旋角
     * @param size       尺寸
     * @param brightness 亮度
     * @param fade       淡入淡出系数（正弦）
     */
    private record VisibleShootingStar(
            ShootingStar star, float local, SkyPoint head,
            float angle, float size, float brightness, float fade
    ) {
    }
}
