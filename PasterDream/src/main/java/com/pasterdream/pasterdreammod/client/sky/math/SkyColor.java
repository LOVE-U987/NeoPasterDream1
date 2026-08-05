package com.pasterdream.pasterdreammod.client.sky.math;

/**
 * 线性 RGB 颜色（分量 0~1）
 *
 * @param red   红色分量
 * @param green 绿色分量
 * @param blue  蓝色分量
 */
public record SkyColor(float red, float green, float blue) {

    /** 纯白色（用于无顶点着色、纯纹理渲染） */
    public static final SkyColor WHITE = new SkyColor(1.0F, 1.0F, 1.0F);

    /** 纯黑色 */
    public static final SkyColor BLACK = new SkyColor(0.0F, 0.0F, 0.0F);

    /**
     * 与另一颜色按比例插值
     *
     * @param other 另一颜色
     * @param t     插值比例（0=this，1=other）
     * @return 插值结果
     */
    public SkyColor lerp(SkyColor other, float t) {
        return new SkyColor(
                this.red + (other.red - this.red) * t,
                this.green + (other.green - this.green) * t,
                this.blue + (other.blue - this.blue) * t
        );
    }
}
