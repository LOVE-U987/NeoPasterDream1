package com.pasterdream.pasterdreammod.client.sky.math;

/**
 * 天空球面上的三维点（世界坐标，位于半径 100 的单位球上）
 *
 * @param x X 坐标
 * @param y Y 坐标
 * @param z Z 坐标
 */
public record SkyPoint(float x, float y, float z) {

    /**
     * 向量模长
     *
     * @return 长度
     */
    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }
}
