package com.pasterdream.pasterdreammod.client.sky.content;

import com.pasterdream.pasterdreammod.client.sky.math.SkyPoint;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;

import java.util.ArrayList;
import java.util.List;

/**
 * 星星聚类分布工具 —— 让星空星星呈聚团分布而非均匀撒点
 * <p>
 * 模拟真实星空中的星群/银河感：随机生成若干聚类中心，每颗星
 * 在最近聚类中心附近按高斯偏移分布。
 */
final class StarClusters {

    /** 聚类中心列表 */
    private final List<SkyPoint> centers;

    /**
     * @param centers 聚类中心
     */
    private StarClusters(List<SkyPoint> centers) {
        this.centers = centers;
    }

    /**
     * 生成星群分布
     *
     * @param count  星星数量
     * @param random 随机源
     * @return 聚类分布器
     */
    static StarClusters make(int count, LegacyRandomSource random) {
        // 聚类数量随星数增长（约 1/15），最少 2 个
        int clusterCount = Math.max(2, count / 15);
        List<SkyPoint> centers = new ArrayList<>(clusterCount);
        for (int i = 0; i < clusterCount; i++) {
            // 聚类中心偏向中高纬度（银道面效果）
            float yaw = random.nextFloat() * 6.2831855F;
            float pitch = -0.9F + random.nextFloat() * 1.8F;
            centers.add(skyPoint(yaw, pitch));
        }
        return new StarClusters(centers);
    }

    /**
     * 在最近聚类中心附近采样一个点（高斯偏移）
     *
     * @param random 随机源
     * @return 采样点，可能为空（偏移超出可视范围时）
     */
    SkyPoint sample(RandomSource random) {
        SkyPoint center = this.centers.get(random.nextInt(this.centers.size()));
        // 高斯偏移（3 次均匀采样近似）
        float spread = 0.12F;
        float dx = gaussian(random) * spread;
        float dy = gaussian(random) * spread;
        float dz = gaussian(random) * spread;
        float nx = center.x() / 100.0F + dx;
        float ny = center.y() / 100.0F + dy;
        float nz = center.z() / 100.0F + dz;
        float length = Mth.sqrt(nx * nx + ny * ny + nz * nz);
        if (length < 0.001F) {
            return null;
        }
        return new SkyPoint(nx / length * 100.0F, ny / length * 100.0F, nz / length * 100.0F);
    }

    /**
     * 近似高斯随机数（3 次均匀采样 - 1.5）
     *
     * @param random 随机源
     * @return 近似正态分布值（均值 0，标准差约 0.5）
     */
    private static float gaussian(RandomSource random) {
        return (random.nextFloat() + random.nextFloat() + random.nextFloat()) / 3.0F * 2.0F - 1.0F;
    }

    /**
     * 偏航/俯仰换算为球面点
     *
     * @param yaw   偏航角（弧度）
     * @param pitch 俯仰角（弧度）
     * @return 球面坐标
     */
    private static SkyPoint skyPoint(float yaw, float pitch) {
        float horizontal = Mth.cos(pitch);
        return new SkyPoint(
                Mth.sin(yaw) * horizontal * 100.0F,
                Mth.sin(pitch) * 100.0F,
                Mth.cos(yaw) * horizontal * 100.0F
        );
    }
}
