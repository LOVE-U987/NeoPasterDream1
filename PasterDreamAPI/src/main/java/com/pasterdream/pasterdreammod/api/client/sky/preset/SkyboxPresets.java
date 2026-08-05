package com.pasterdream.pasterdreammod.api.client.sky.preset;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 天空盒预设库 —— 预定义的天空内容配置模板（参考仿照模组 Stellara 排版）
 * <p>
 * 每个预设方法返回 {@link JsonObject}（单个内容层）或 {@link JsonArray}（组合层），
 * 与数据驱动格式完全一致，可被数据层解析、可自由组合、可序列化。
 * <p>
 * <b>纹理路径约定</b>：{@code starField(theme)} 引用
 * {@code pasterdream:textures/sky/<theme>/<theme>_star_1..7.png} 与
 * {@code <theme>_star2_1..7.png}（群系主题彩色星星，由
 * {@code tools/gen_sky_textures.py} 生成）；行星引用 {@code <theme>_planet_1/2.png}。
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 组合预设：极光之夜（参考 Stellara frozen/cherry 排版）
 * JsonArray layers = SkyboxPresets.auroraNight("frozen", new float[]{0.35f,0.6f,1f});
 *
 * // 组装为完整 skybox（含 biomes/dimensions）
 * JsonObject skybox = SkyboxPresets.asSkybox(
 *         List.of("pasterdream:biome_dyedream_2"),
 *         List.of("pasterdream:dyedream_world"),
 *         layers, 0.12f, 100);
 * }</pre>
 *
 * @see com.pasterdream.pasterdreammod.api.client.sky.SkyboxAPI
 */
public final class SkyboxPresets {

    private SkyboxPresets() {
    }

    // ==================== 基础内容预设（单个 JsonObject 层） ====================

    /**
     * 天空色调（球面渐变穹顶，天底暗天顶亮）
     *
     * @param color   RGB 颜色（分量 0~1）
     * @param opacity 最大着色强度（0~1，建议 0.03~0.08）
     * @return sky_tint 层
     */
    public static JsonObject tint(float[] color, float opacity) {
        JsonObject layer = new JsonObject();
        layer.addProperty("type", "pasterdream:sky_tint");
        layer.addProperty("priority", -100);
        layer.add("color", color(color));
        layer.addProperty("opacity", opacity);
        return layer;
    }

    /**
     * 群系主题星域（2 色 × 7 帧 = 14 帧彩色星星，聚团分布）
     *
     * @param theme 群系主题名（warm/forest/frozen/ocean/mushroom/dense）
     * @return star_field 层
     */
    public static JsonObject starField(String theme) {
        JsonObject layer = new JsonObject();
        layer.addProperty("type", "pasterdream:star_field");
        layer.addProperty("priority", -5);
        layer.add("textures", starTextures(theme));
        layer.addProperty("count", 1400);
        layer.addProperty("min_size", 0.6F);
        layer.addProperty("max_size", 2.0F);
        layer.add("color", color(new float[]{1.0F, 1.0F, 1.0F}));
        layer.addProperty("seed", theme.hashCode());
        return layer;
    }

    /**
     * 极光（多层幕帘网格）
     *
     * @param colors   极光颜色列表（纵向渐变）
     * @param opacity  不透明度（0~1，如 frozen 0.75、desert 0.15、微弱 0.05）
     * @param bands    幕帘层数（1~4）
     * @return aurora 层
     */
    public static JsonObject aurora(List<float[]> colors, float opacity, int bands) {
        JsonObject layer = new JsonObject();
        layer.addProperty("type", "pasterdream:aurora");
        layer.addProperty("priority", 20);
        layer.addProperty("bands", bands);
        layer.addProperty("segments", 200);
        layer.addProperty("gradient_steps", 16);
        layer.addProperty("center_yaw", 3.14F);
        layer.addProperty("sphere_radius", 2.0F);
        layer.addProperty("width", 20.0F);
        layer.addProperty("min_pitch", -0.5F);
        layer.addProperty("max_pitch", 0.0F);
        layer.addProperty("wave_amplitude", 0.05F);
        layer.addProperty("wave_frequency", 5.0F);
        layer.addProperty("depth_amplitude", 10.0F);
        layer.addProperty("ray_strength", 0.5F);
        layer.addProperty("edge_softness", 0.5F);
        layer.addProperty("speed", 0.005F);
        layer.addProperty("opacity", opacity);
        JsonArray colorArr = new JsonArray();
        for (float[] c : colors) {
            colorArr.add(color(c));
        }
        layer.add("colors", colorArr);
        return layer;
    }

    /**
     * 银河光带（环形闭合光带）
     *
     * @param colors  光带颜色（单色或多色渐变）
     * @param opacity 不透明度（0~1，建议 0.2~0.35）
     * @return ribbons 层
     */
    public static JsonObject ribbons(List<float[]> colors, float opacity) {
        JsonObject layer = new JsonObject();
        layer.addProperty("type", "pasterdream:ribbons");
        layer.addProperty("priority", -12);
        layer.addProperty("segments", 220);
        layer.addProperty("gradient_steps", 14);
        layer.addProperty("center_yaw", 2.95F);
        layer.addProperty("base_pitch", 0.18F);
        layer.addProperty("spacing", 0.0F);
        layer.addProperty("thickness", 0.12F);
        layer.addProperty("arc", 6.28318F);
        layer.addProperty("tilt", 0.72F);
        layer.addProperty("wave_amplitude", 0.04F);
        layer.addProperty("wave_frequency", 2.8F);
        layer.addProperty("wobble_amplitude", 0.05F);
        layer.addProperty("speed", 0.006F);
        layer.addProperty("edge_softness", 0.28F);
        layer.addProperty("blur", 0.45F);
        layer.addProperty("opacity", opacity);
        layer.addProperty("seed", 114902);
        JsonArray colorArr = new JsonArray();
        for (float[] c : colors) {
            colorArr.add(color(c));
        }
        layer.add("colors", colorArr);
        return layer;
    }

    /**
     * 行星系统（多行星布局，参考 Stellara 不同方位排版）
     *
     * @param theme   群系主题名
     * @param planets 行星数量（2~7）
     * @return planet_system 层
     */
    public static JsonObject planetSystem(String theme, int planets) {
        JsonObject layer = new JsonObject();
        layer.addProperty("type", "pasterdream:planet_system");
        layer.addProperty("priority", 8);
        JsonArray planetArr = new JsonArray();
        int count = Math.max(1, Math.min(7, planets));
        for (int i = 0; i < count; i++) {
            planetArr.add(planet(theme, i));
        }
        layer.add("planets", planetArr);
        return layer;
    }

    /**
     * 流星（周期性自动发射）
     *
     * @param count 流星数量（2~4）
     * @return shooting_stars 层
     */
    public static JsonObject shootingStars(int count) {
        JsonObject layer = new JsonObject();
        layer.addProperty("type", "pasterdream:shooting_stars");
        layer.addProperty("priority", 24);
        layer.addProperty("count", Math.max(1, Math.min(4, count)));
        layer.addProperty("interval_ticks", 110);
        layer.addProperty("duration_ticks", 16);
        layer.add("color", color(new float[]{0.66F, 0.92F, 1.0F}));
        layer.addProperty("seed", 114904);
        return layer;
    }

    /**
     * 星座（节点连线 + 双层光晕星点）
     *
     * @param stars 星座图案（{u, v, size} 相对坐标数组）
     * @param lines 连线（节点索引对）
     * @param color 星座颜色
     * @param yaw   中心偏航角（弧度）
     * @param pitch 中心俯仰角（弧度）
     * @return constellation 层
     */
    public static JsonObject constellation(float[][] stars, int[][] lines, float[] color, float yaw, float pitch) {
        JsonObject layer = new JsonObject();
        layer.addProperty("type", "pasterdream:constellation");
        layer.addProperty("priority", 15);
        layer.addProperty("yaw", yaw);
        layer.addProperty("pitch", pitch);
        layer.addProperty("scale", 0.56F);
        layer.addProperty("twinkle_speed", 0.024F);
        layer.addProperty("line_width", 0.15F);
        layer.add("color", color(color));
        JsonArray starArr = new JsonArray();
        for (float[] s : stars) {
            JsonObject star = new JsonObject();
            star.addProperty("u", s[0]);
            star.addProperty("v", s[1]);
            star.addProperty("size", s.length > 2 ? s[2] : 1.0F);
            starArr.add(star);
        }
        layer.add("stars", starArr);
        JsonArray lineArr = new JsonArray();
        for (int[] l : lines) {
            JsonArray pair = new JsonArray();
            pair.add(l[0]);
            pair.add(l[1]);
            lineArr.add(pair);
        }
        layer.add("lines", lineArr);
        return layer;
    }

    /**
     * 彩虹（白天显示）
     *
     * @return rainbow 层
     */
    public static JsonObject rainbow() {
        JsonObject layer = new JsonObject();
        layer.addProperty("type", "pasterdream:rainbow");
        layer.addProperty("priority", 6);
        layer.addProperty("yaw", 3.14F);
        layer.addProperty("base_pitch", -0.16F);
        layer.addProperty("radius", 1.25F);
        layer.addProperty("thickness", 0.35F);
        layer.addProperty("segments", 96);
        layer.addProperty("arc", 2.8F);
        layer.addProperty("opacity", 0.45F);
        layer.addProperty("blur", 1.0F);
        JsonArray colorArr = new JsonArray();
        for (float[] c : RAINBOW_COLORS) {
            colorArr.add(color(c));
        }
        layer.add("colors", colorArr);
        return layer;
    }

    // ==================== 组合预设（返回 JsonArray layers） ====================

    /**
     * 银河之夜 —— 参考 Stellara 末地/沼泽/海洋排版：
     * 天空色调 + 银河光带 + 星域 + 流星 + 行星系统 + 3 星座
     *
     * @param theme   群系主题名
     * @param bandColor 银河光带颜色
     * @return layers
     */
    public static JsonArray galaxyNight(String theme, float[] bandColor) {
        List<JsonObject> layers = new ArrayList<>();
        layers.add(tint(new float[]{0.10F, 0.12F, 0.30F}, 0.05F));
        layers.add(ribbons(List.of(bandColor), 0.30F));
        layers.add(starField(theme));
        layers.add(shootingStars(3));
        layers.add(planetSystem(theme, 5));
        layers.add(constellation(SHAPE_W, new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 4}}, new float[]{0.68F, 0.92F, 1.0F}, -1.28F, -0.46F));
        layers.add(constellation(SHAPE_UMBRELLA, new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 4}, {1, 5}, {5, 6}, {6, 2}}, new float[]{0.58F, 0.84F, 1.0F}, 2.06F, -0.34F));
        layers.add(constellation(SHAPE_CROSS, new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 4}, {1, 5}, {3, 6}}, new float[]{0.86F, 0.92F, 1.0F}, 0.18F, 0.82F));
        return toArray(layers);
    }

    /**
     * 极光之夜 —— 参考 Stellara 冰雪/沙漠/樱花排版：
     * 天空色调 + 极光 + 星域 + 流星 + 行星系统 + 2 星座
     *
     * @param theme    群系主题名
     * @param auroraColors 极光颜色（纵向渐变）
     * @param auroraOpacity 极光强度（frozen 0.75 / desert 0.15）
     * @return layers
     */
    public static JsonArray auroraNight(String theme, List<float[]> auroraColors, float auroraOpacity) {
        List<JsonObject> layers = new ArrayList<>();
        layers.add(tint(new float[]{0.10F, 0.12F, 0.30F}, 0.06F));
        layers.add(aurora(auroraColors, auroraOpacity, 3));
        layers.add(starField(theme));
        layers.add(shootingStars(2));
        layers.add(planetSystem(theme, 5));
        layers.add(constellation(SHAPE_W, new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 4}}, new float[]{0.72F, 0.78F, 1.0F}, -0.3F, 0.1F));
        layers.add(constellation(SHAPE_UMBRELLA, new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 4}, {1, 5}, {5, 6}, {6, 2}}, new float[]{0.74F, 1.0F, 0.96F}, 2.4F, 0.5F));
        return toArray(layers);
    }

    /**
     * 璀璨星空 —— 简洁版：天空色调 + 星域 + 流星 + 行星系统
     *
     * @param theme 群系主题名
     * @return layers
     */
    public static JsonArray starryNight(String theme) {
        List<JsonObject> layers = new ArrayList<>();
        layers.add(tint(new float[]{0.10F, 0.12F, 0.30F}, 0.05F));
        layers.add(starField(theme));
        layers.add(shootingStars(2));
        layers.add(planetSystem(theme, 4));
        return toArray(layers);
    }

    /**
     * 白昼彩虹 —— 参考 Stellara 平原排版
     *
     * @return layers
     */
    public static JsonArray plainDay() {
        List<JsonObject> layers = new ArrayList<>();
        layers.add(rainbow());
        return toArray(layers);
    }

    // ==================== 组装辅助 ====================

    /**
     * 将 layers 组装为完整 skybox JSON（含 biomes/dimensions/fade_speed/weight）
     *
     * @param biomes     群系列表（含命名空间，如 pasterdream:biome_dyedream_0）
     * @param dimensions 维度列表（含命名空间）
     * @param layers     内容层
     * @param fadeSpeed  淡入速度（建议 0.12）
     * @param weight     权重
     * @return 完整 skybox JsonObject
     */
    public static JsonObject asSkybox(List<String> biomes, List<String> dimensions, JsonArray layers, float fadeSpeed, int weight) {
        JsonObject skybox = new JsonObject();
        skybox.addProperty("fade_speed", fadeSpeed);
        skybox.addProperty("weight", weight);
        JsonArray biomeArr = new JsonArray();
        for (String b : biomes) {
            biomeArr.add(b);
        }
        skybox.add("biomes", biomeArr);
        JsonArray dimArr = new JsonArray();
        for (String d : dimensions) {
            dimArr.add(d);
        }
        skybox.add("dimensions", dimArr);
        skybox.add("layers", layers);
        return skybox;
    }

    // ==================== 内部工具 ====================

    /** 彩虹七色 */
    private static final float[][] RAINBOW_COLORS = {
            {1.0F, 0.12F, 0.10F},
            {1.0F, 0.48F, 0.08F},
            {1.0F, 0.88F, 0.12F},
            {0.18F, 0.78F, 0.22F},
            {0.18F, 0.86F, 1.0F},
            {0.10F, 0.48F, 1.0F},
            {0.42F, 0.18F, 0.88F}
    };

    /** 星座图案 W（5 节点） */
    private static final float[][] SHAPE_W = {
            {-0.48F, -0.24F, 0.9F}, {-0.24F, -0.02F, 1.02F},
            {0.02F, 0.18F, 1.28F}, {0.34F, 0.32F, 0.95F}, {0.5F, 0.06F, 0.86F}
    };

    /** 星座图案 伞形（7 节点） */
    private static final float[][] SHAPE_UMBRELLA = {
            {-0.56F, -0.06F, 0.92F}, {-0.34F, 0.18F, 1.08F}, {-0.08F, 0.04F, 1.22F},
            {0.18F, 0.2F, 0.98F}, {0.46F, 0.08F, 0.92F}, {0.58F, -0.18F, 0.86F},
            {-0.2F, -0.28F, 0.9F}, {0.1F, -0.18F, 0.86F}
    };

    /** 星座图案 十字星（7 节点） */
    private static final float[][] SHAPE_CROSS = {
            {-0.38F, -0.34F, 0.86F}, {-0.18F, -0.06F, 0.98F}, {0.02F, 0.24F, 1.28F},
            {0.26F, 0.02F, 0.94F}, {0.5F, -0.22F, 0.86F}, {-0.42F, 0.18F, 0.88F},
            {0.22F, -0.42F, 0.9F}
    };

    /**
     * 生成群系主题星星纹理列表（主色 7 帧 + 次色 7 帧）
     *
     * @param theme 群系主题名
     * @return 纹理路径数组
     */
    private static JsonArray starTextures(String theme) {
        JsonArray arr = new JsonArray();
        for (int i = 1; i <= 7; i++) {
            arr.add("pasterdream:textures/sky/" + theme + "/" + theme + "_star_" + i + ".png");
        }
        for (int i = 1; i <= 7; i++) {
            arr.add("pasterdream:textures/sky/" + theme + "/" + theme + "_star2_" + i + ".png");
        }
        return arr;
    }

    /**
     * 生成单颗行星配置（不同方位/大小交替，参考 Stellara 布局）
     *
     * @param theme 群系主题名
     * @param index 行星序号
     * @return 行星 JsonObject
     */
    private static JsonObject planet(String theme, int index) {
        JsonObject p = new JsonObject();
        // 纹理 planet_1/planet_2 交替
        int tex = index % 2 + 1;
        p.addProperty("texture", "pasterdream:textures/sky/" + theme + "/" + theme + "_planet_" + tex + ".png");
        // 方位表（偏航角/俯仰角/尺寸），参考 Stellara the_end 排版
        float[][] layout = {
                {-2.76F, -0.18F, 8.2F}, {-1.92F, 0.18F, 11.0F}, {-0.9F, 0.48F, 6.8F},
                {0.28F, -0.34F, 8.8F}, {1.34F, 0.26F, 6.4F}, {2.18F, 0.72F, 5.6F}, {2.9F, -0.5F, 5.2F}
        };
        float[] l = layout[Math.min(index, layout.length - 1)];
        p.addProperty("yaw", l[0]);
        p.addProperty("pitch", l[1]);
        p.addProperty("size", l[2]);
        p.addProperty("roll", 0.1F + index * 0.25F);
        p.addProperty("roll_speed", (index % 2 == 0 ? 1 : -1) * (0.0008F + index * 0.0001F));
        p.add("color", color(new float[]{1.0F, 1.0F, 1.0F}));
        p.addProperty("opacity", 0.86F - index * 0.03F);
        return p;
    }

    /**
     * 构造 RGB 颜色数组
     *
     * @param rgb 分量（0~1）
     * @return JsonArray
     */
    private static JsonArray color(float[] rgb) {
        JsonArray arr = new JsonArray();
        arr.add(rgb[0]);
        arr.add(rgb[1]);
        arr.add(rgb[2]);
        return arr;
    }

    /**
     * 列表转 JsonArray
     *
     * @param layers 层列表
     * @return JsonArray
     */
    private static JsonArray toArray(List<JsonObject> layers) {
        JsonArray arr = new JsonArray();
        for (JsonObject l : layers) {
            arr.add(l);
        }
        return arr;
    }
}
