package com.pasterdream.pasterdreammod.client.sky.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.pasterdream.pasterdreammod.api.client.sky.SkyCondition;
import com.pasterdream.pasterdreammod.api.client.sky.SkyContent;
import com.pasterdream.pasterdreammod.api.client.sky.SkyboxEntry;
import com.pasterdream.pasterdreammod.api.client.sky.SkyboxRegistry;
import com.pasterdream.pasterdreammod.client.sky.content.AuroraSkyContent;
import com.pasterdream.pasterdreammod.client.sky.content.ConstellationSkyContent;
import com.pasterdream.pasterdreammod.client.sky.content.RainbowSkyContent;
import com.pasterdream.pasterdreammod.client.sky.content.ShootingStarSkyContent;
import com.pasterdream.pasterdreammod.client.sky.content.SkyRibbonContent;
import com.pasterdream.pasterdreammod.client.sky.content.SkyTintContent;
import com.pasterdream.pasterdreammod.client.sky.content.StarFieldSkyContent;
import com.pasterdream.pasterdreammod.client.sky.content.TexturedPlanetSystemSkyContent;
import com.pasterdream.pasterdreammod.client.sky.math.SkyColor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 天空盒数据重载监听器 —— 加载 {@code data/<namespace>/skyboxes/*.json}
 * <p>
 * JSON 结构（顶层可被各 layer 继承）：
 * <pre>{@code
 * {
 *   "fade_speed": 0.08, "weight": 100,
 *   "biomes": ["pasterdream:biome_dyedream_0"],
 *   "dimensions": ["pasterdream:dyedream_world"],
 *   "time": { "from": 13000, "to": 23000 },
 *   "layers": [ { "type": "pasterdream:star_field", ... }, ... ]
 * }
 * }</pre>
 * 每个 layer 是一个独立 {@link SkyboxEntry}，共享同一候选键（文件 id），
 * 整套天空盒作为一个整体切换。
 */
public class SkyboxDataReloadListener extends SimpleJsonResourceReloadListener {

    /** 监听器资源目录 */
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("pasterdream", "skyboxes");

    private static final Gson GSON = new Gson();

    public SkyboxDataReloadListener() {
        super(GSON, ID.getPath());
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> elements, ResourceManager resourceManager, ProfilerFiller profiler) {
        List<SkyboxEntry> entries = new ArrayList<>();
        for (Entry<ResourceLocation, JsonElement> entry : elements.entrySet()) {
            try {
                entries.addAll(parseEntries(entry.getKey(), entry.getValue().getAsJsonObject()));
            } catch (RuntimeException e) {
                throw new JsonParseException("Failed to parse skybox '" + entry.getKey() + "'", e);
            }
        }
        SkyboxRegistry.replaceDataEntries(entries);
    }

    /**
     * 解析单个天空盒 JSON（含 layers 继承）
     * <p>
     * 公开静态方法，供代码注册的预设天空盒（{@code SkyboxPresets}）复用解析逻辑。
     *
     * @param id   条目基资源路径（layers 会派生 /layer_N 子路径）
     * @param json 天空盒 JSON（顶层可含 biomes/dimensions/layers 等）
     * @return 条目列表
     */
    public static List<SkyboxEntry> parseEntries(ResourceLocation id, JsonObject json) {
        if (!json.has("layers")) {
            return List.of(parse(id, json));
        }
        List<SkyboxEntry> entries = new ArrayList<>();
        JsonArray layers = json.getAsJsonArray("layers");
        for (int index = 0; index < layers.size(); index++) {
            JsonObject layer = layers.get(index).getAsJsonObject().deepCopy();
            inherit(json, layer, "biomes");
            inherit(json, layer, "biome_tags");
            inherit(json, layer, "dimensions");
            inherit(json, layer, "time");
            inherit(json, layer, "fade_speed");
            inherit(json, layer, "weight");
            entries.add(parse(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath() + "/layer_" + index), layer));
        }
        return entries;
    }

    /**
     * 父级字段继承到 layer（layer 未声明时）
     *
     * @param parent 父 JSON
     * @param layer  子 JSON
     * @param key    字段名
     */
    private static void inherit(JsonObject parent, JsonObject layer, String key) {
        if (parent.has(key) && !layer.has(key)) {
            layer.add(key, parent.get(key));
        }
    }

    /**
     * 解析单条目（switch 分派到对应内容类型）
     *
     * @param id   条目资源路径
     * @param json JSON 对象
     * @return 天空盒条目
     */
    private static SkyboxEntry parse(ResourceLocation id, JsonObject json) {
        ResourceLocation type = SkyboxJson.resourceLocation(SkyboxJson.requiredString(json, "type"));
        int priority = SkyboxJson.getInt(json, "priority", 0);
        int weight = SkyboxJson.getInt(json, "weight", 0);
        float fadeSpeed = SkyboxJson.getFloat(json, "fade_speed", 0.08F);
        SkyCondition condition = parseCondition(json);
        long seed = SkyboxJson.getLong(json, "seed", id.hashCode());

        SkyContent content = switch (type.toString()) {
            case "pasterdream:sky_tint" -> new SkyTintContent(
                    id, priority,
                    SkyboxJson.color(json, "color", new SkyColor(0.45F, 0.78F, 1.0F)),
                    SkyboxJson.getFloat(json, "opacity", 0.075F)
            );
            case "pasterdream:star_field" -> new StarFieldSkyContent(
                    id, priority,
                    SkyboxJson.requiredResourceLocationList(json, "textures"),
                    SkyboxJson.getInt(json, "count", 500),
                    SkyboxJson.getFloat(json, "min_size", 0.5F),
                    SkyboxJson.getFloat(json, "max_size", 1.5F),
                    SkyboxJson.color(json, "color", new SkyColor(1.0F, 1.0F, 1.0F)),
                    seed
            );
            case "pasterdream:planet_system" -> parsePlanetSystem(id, priority, json);
            case "pasterdream:constellation" -> parseConstellation(id, priority, json);
            case "pasterdream:shooting_stars" -> new ShootingStarSkyContent(
                    id, priority,
                    SkyboxJson.getInt(json, "count", 4),
                    SkyboxJson.color(json, "color", new SkyColor(0.7F, 0.9F, 1.0F)),
                    seed,
                    SkyboxJson.getInt(json, "interval_ticks", 100),
                    SkyboxJson.getInt(json, "duration_ticks", 24)
            );
            case "pasterdream:aurora" -> new AuroraSkyContent(
                    id, priority,
                    SkyboxJson.colors(json, List.of(
                            SkyboxJson.color(json, "lower_color", new SkyColor(0.08F, 0.82F, 0.92F)),
                            SkyboxJson.color(json, "upper_color", new SkyColor(0.62F, 0.96F, 1.0F))
                    )),
                    SkyboxJson.getInt(json, "bands", 4),
                    SkyboxJson.getInt(json, "segments", 48),
                    SkyboxJson.getInt(json, "gradient_steps", 5),
                    SkyboxJson.getFloat(json, "wave_amplitude", 0.06F),
                    SkyboxJson.getFloat(json, "wave_frequency", 2.4F),
                    SkyboxJson.getFloat(json, "speed", 0.012F),
                    SkyboxJson.getFloat(json, "opacity", 0.32F),
                    SkyboxJson.getFloat(json, "center_yaw", 3.14F),
                    SkyboxJson.getFloat(json, "width", 1.9F),
                    SkyboxJson.getFloat(json, "min_pitch", 0.18F),
                    SkyboxJson.getFloat(json, "max_pitch", 1.12F),
                    SkyboxJson.getFloat(json, "ray_strength", 0.45F),
                    SkyboxJson.getFloat(json, "edge_softness", 0.35F),
                    SkyboxJson.getFloat(json, "depth_amplitude", 0.28F),
                    SkyboxJson.getFloat(json, "depth_offset", 0.0F),
                    SkyboxJson.getFloat(json, "sphere_radius", 100.0F)
            );
            case "pasterdream:ribbons" -> new SkyRibbonContent(
                    id, priority,
                    SkyboxJson.colors(json, List.of(
                            new SkyColor(0.92F, 0.38F, 0.72F),
                            new SkyColor(0.72F, 0.46F, 1.0F),
                            new SkyColor(0.38F, 0.82F, 1.0F)
                    )),
                    SkyboxJson.getInt(json, "segments", 128),
                    SkyboxJson.getInt(json, "gradient_steps", 8),
                    SkyboxJson.getFloat(json, "center_yaw", 3.14F),
                    SkyboxJson.getFloat(json, "base_pitch", 0.28F),
                    SkyboxJson.getFloat(json, "spacing", 0.08F),
                    SkyboxJson.getFloat(json, "thickness", 0.12F),
                    SkyboxJson.getFloat(json, "arc", 6.2831855F),
                    SkyboxJson.getFloat(json, "tilt", 0.0F),
                    SkyboxJson.getFloat(json, "wave_amplitude", 0.035F),
                    SkyboxJson.getFloat(json, "wave_frequency", 2.2F),
                    SkyboxJson.getFloat(json, "wobble_amplitude", 0.025F),
                    SkyboxJson.getFloat(json, "opacity", 0.28F),
                    SkyboxJson.getFloat(json, "speed", 0.012F),
                    SkyboxJson.getFloat(json, "edge_softness", 0.2F),
                    SkyboxJson.getFloat(json, "blur", 1.0F),
                    seed
            );
            case "pasterdream:rainbow" -> new RainbowSkyContent(
                    id, priority,
                    SkyboxJson.colors(json, List.of(
                            new SkyColor(1.0F, 0.16F, 0.12F),
                            new SkyColor(1.0F, 0.55F, 0.08F),
                            new SkyColor(1.0F, 0.92F, 0.12F),
                            new SkyColor(0.16F, 0.82F, 0.24F),
                            new SkyColor(0.12F, 0.48F, 1.0F),
                            new SkyColor(0.44F, 0.22F, 0.92F)
                    )),
                    SkyboxJson.getFloat(json, "yaw", 3.14F),
                    SkyboxJson.getFloat(json, "base_pitch", -0.22F),
                    SkyboxJson.getFloat(json, "radius", 0.86F),
                    SkyboxJson.getFloat(json, "thickness", 0.08F),
                    SkyboxJson.getInt(json, "segments", 64),
                    SkyboxJson.getFloat(json, "arc", 3.14F),
                    SkyboxJson.getFloat(json, "opacity", 0.34F),
                    SkyboxJson.getFloat(json, "blur", 1.0F)
            );
            default -> throw new JsonParseException("Unknown skybox type: " + type);
        };
        return new SkyboxEntry(content, condition, fadeSpeed, weight);
    }

    /**
     * 解析行星系统
     *
     * @param id       条目资源路径
     * @param priority 优先级
     * @param json     JSON 对象
     * @return 行星系统内容
     */
    private static TexturedPlanetSystemSkyContent parsePlanetSystem(ResourceLocation id, int priority, JsonObject json) {
        if (!json.has("planets")) {
            throw new JsonParseException("Missing required property: planets");
        }
        List<TexturedPlanetSystemSkyContent.Planet> planets = new ArrayList<>();
        for (JsonElement element : json.getAsJsonArray("planets")) {
            JsonObject planet = element.getAsJsonObject();
            ResourceLocation texture = SkyboxJson.resourceLocation(SkyboxJson.requiredString(planet, "texture"));
            planets.add(new TexturedPlanetSystemSkyContent.Planet(
                    texture,
                    SkyboxJson.getFloat(planet, "yaw", 0.0F),
                    SkyboxJson.getFloat(planet, "pitch", 0.0F),
                    SkyboxJson.getFloat(planet, "size", 8.0F),
                    SkyboxJson.getFloat(planet, "roll", 0.0F),
                    SkyboxJson.getFloat(planet, "roll_speed", 0.0F),
                    SkyboxJson.color(planet, "color", new SkyColor(1.0F, 1.0F, 1.0F)),
                    SkyboxJson.getFloat(planet, "opacity", 1.0F),
                    parseSatellites(planet)
            ));
        }
        return new TexturedPlanetSystemSkyContent(id, priority, planets);
    }

    /**
     * 解析行星的卫星列表
     *
     * @param planet 行星 JSON
     * @return 卫星列表
     */
    private static List<TexturedPlanetSystemSkyContent.Satellite> parseSatellites(JsonObject planet) {
        if (!planet.has("satellites")) {
            return List.of();
        }
        List<TexturedPlanetSystemSkyContent.Satellite> satellites = new ArrayList<>();
        for (JsonElement element : planet.getAsJsonArray("satellites")) {
            JsonObject satellite = element.getAsJsonObject();
            ResourceLocation texture = SkyboxJson.resourceLocation(SkyboxJson.requiredString(satellite, "texture"));
            satellites.add(new TexturedPlanetSystemSkyContent.Satellite(
                    texture,
                    SkyboxJson.getFloat(satellite, "size", 2.5F),
                    SkyboxJson.getFloat(satellite, "orbit_radius", 5.0F),
                    SkyboxJson.getFloat(satellite, "orbit_speed", 0.01F),
                    SkyboxJson.getFloat(satellite, "orbit_offset", 0.0F),
                    SkyboxJson.getFloat(satellite, "roll", 0.0F),
                    SkyboxJson.color(satellite, "color", new SkyColor(1.0F, 1.0F, 1.0F)),
                    SkyboxJson.getFloat(satellite, "opacity", 1.0F)
            ));
        }
        return satellites;
    }

    /**
     * 解析星座（节点 + 连线）
     *
     * @param id       条目资源路径
     * @param priority 优先级
     * @param json     JSON 对象
     * @return 星座内容
     */
    private static ConstellationSkyContent parseConstellation(ResourceLocation id, int priority, JsonObject json) {
        long seed = SkyboxJson.getLong(json, "seed", id.hashCode());
        SkyColor color = SkyboxJson.color(json, "color", new SkyColor(0.62F, 1.0F, 0.74F));
        float centerYaw = SkyboxJson.getFloat(json, "yaw", (float) Math.floorMod(seed, 6283L) / 1000.0F);
        float centerPitch = SkyboxJson.getFloat(json, "pitch", -0.05F + (float) Math.floorMod(seed / 7L, 700L) / 1000.0F);
        float scale = SkyboxJson.getFloat(json, "scale", 1.0F);
        float twinkleSpeed = SkyboxJson.getFloat(json, "twinkle_speed", 0.025F);
        float lineWidth = SkyboxJson.getFloat(json, "line_width", 0.4F);
        List<ResourceLocation> textures;
        if (json.has("textures")) {
            textures = SkyboxJson.requiredResourceLocationList(json, "textures");
        } else if (json.has("texture")) {
            textures = List.of(SkyboxJson.resourceLocation(SkyboxJson.requiredString(json, "texture")));
        } else {
            textures = List.of();
        }
        return new ConstellationSkyContent(
                id, priority, color, centerYaw, centerPitch, scale, twinkleSpeed, lineWidth, textures, parseShape(json), seed
        );
    }

    /**
     * 解析星座形状（stars + lines）
     *
     * @param json JSON 对象
     * @return 形状
     */
    private static ConstellationSkyContent.Shape parseShape(JsonObject json) {
        if (!json.has("stars")) {
            throw new JsonParseException("Missing required property: stars");
        }
        List<ConstellationSkyContent.Node> nodes = new ArrayList<>();
        for (JsonElement element : json.getAsJsonArray("stars")) {
            JsonObject star = element.getAsJsonObject();
            nodes.add(new ConstellationSkyContent.Node(
                    SkyboxJson.getFloat(star, "u", 0.0F),
                    SkyboxJson.getFloat(star, "v", 0.0F),
                    SkyboxJson.getFloat(star, "size", 1.0F)
            ));
        }
        List<ConstellationSkyContent.Link> links = new ArrayList<>();
        if (json.has("lines")) {
            for (JsonElement element : json.getAsJsonArray("lines")) {
                JsonArray pair = element.getAsJsonArray();
                links.add(new ConstellationSkyContent.Link(pair.get(0).getAsInt(), pair.get(1).getAsInt()));
            }
        }
        return new ConstellationSkyContent.Shape(nodes, links);
    }

    /**
     * 解析显示条件（biomes / biome_tags / dimensions / time 四轴 AND）
     *
     * @param json JSON 对象
     * @return 条件
     */
    private static SkyCondition parseCondition(JsonObject json) {
        List<ResourceKey<Biome>> biomeKeys = SkyboxJson.resourceLocationList(json, "biomes")
                .stream()
                .map(id -> ResourceKey.create(Registries.BIOME, id))
                .toList();
        List<TagKey<Biome>> biomeTags = SkyboxJson.resourceLocationList(json, "biome_tags")
                .stream()
                .map(id -> TagKey.create(Registries.BIOME, id))
                .toList();
        List<ResourceKey<Level>> dimensions = SkyboxJson.resourceLocationList(json, "dimensions")
                .stream()
                .map(id -> ResourceKey.create(Registries.DIMENSION, id))
                .toList();
        TimeWindow timeWindow = timeWindow(json);
        return context -> {
            boolean hasBiomeFilter = !biomeKeys.isEmpty() || !biomeTags.isEmpty();
            boolean biomeMatches = !hasBiomeFilter
                    || biomeKeys.contains(context.biomeKey())
                    || biomeTags.stream().anyMatch(context.biome()::is);
            boolean dimensionMatches = dimensions.isEmpty() || dimensions.contains(context.level().dimension());
            boolean timeMatches = timeWindow == null || timeWindow.matches(context.dayTime());
            return biomeMatches && dimensionMatches && timeMatches;
        };
    }

    /**
     * 解析时间窗（0~24000，支持跨午夜）
     *
     * @param json JSON 对象
     * @return 时间窗，无则 null
     */
    private static TimeWindow timeWindow(JsonObject json) {
        if (!json.has("time")) {
            return null;
        }
        JsonObject time = json.getAsJsonObject("time");
        return new TimeWindow(
                normalizeDayTime(SkyboxJson.getFloat(time, "from", 0.0F)),
                normalizeDayTime(SkyboxJson.getFloat(time, "to", 24000.0F))
        );
    }

    /**
     * 归一化白天时间到 0~24000
     *
     * @param time 原始时间
     * @return 归一化时间
     */
    private static float normalizeDayTime(float time) {
        return Math.floorMod((int) time, 24000);
    }

    /**
     * 时间窗记录（支持跨午夜回卷）
     *
     * @param from 起始时间
     * @param to   结束时间
     */
    private record TimeWindow(float from, float to) {

        /**
         * 判断时间是否在窗内
         *
         * @param time 当前时间
         * @return 是否在窗内
         */
        private boolean matches(float time) {
            if (this.from == this.to) {
                return true;
            }
            return this.from < this.to
                    ? time >= this.from && time <= this.to
                    : time >= this.from || time <= this.to;
        }
    }
}
