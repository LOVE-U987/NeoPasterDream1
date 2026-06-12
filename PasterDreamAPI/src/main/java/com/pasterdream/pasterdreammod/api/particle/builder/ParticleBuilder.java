package com.pasterdream.pasterdreammod.api.particle.builder;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.particle.ParticleAPI;
import com.pasterdream.pasterdreammod.api.particle.ParticleResult;
import com.pasterdream.pasterdreammod.api.particle.gen.ParticleGenerator;
import com.pasterdream.pasterdreammod.api.particle.gen.ParticleTextureGenerator;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.io.IOException;

/**
 * 粒子类型构建器 —— 采用 Builder 模式链式配置和注册粒子类型
 * <p>
 * 解决在 {@code PDParticles.java} 中手动编写 DeferredRegister 注册代码的繁琐问题。
 * 通过链式调用即可完成粒子类型的注册和资源文件生成。
 * <p>
 * 使用示例：
 * <pre>{@code
 * ParticleResult sparkle = ParticleAPI.createParticle("sparkle")
 *     .alwaysShow()
 *     .texture("pasterdream:sparkle")
 *     .withGravity(0.05f)
 *     .generateJson(true)
 *     .build();
 * }</pre>
 *
 * @see com.pasterdream.pasterdreammod.api.particle.ParticleAPI
 */
public class ParticleBuilder {

    private final String modId;
    private final String name;

    /** SimpleParticleType 的 alwaysUpdate 参数，默认为 false */
    private boolean alwaysShow = false;
    /** 粒子纹理路径（如 "pasterdream:sparkle"），用于生成粒子 JSON */
    private String texturePath;
    /** 粒子重力值（仅文档/提示用途，实际重力需在 Particle 类中实现） */
    private Float gravity;
    /** 是否自动生成粒子资源 JSON 文件，默认为 true */
    private boolean generateJsonFiles = true;
    /** 资源文件基础路径，默认为 src/main/resources */
    private String basePath = "src/main/resources";

    /**
     * 构造粒子构建器
     *
     * @param modId 模组 ID
     * @param name  粒子注册名称（如 "sparkle"）
     */
    public ParticleBuilder(String modId, String name) {
        this.modId = modId;
        this.name = name;
        this.texturePath = modId + ":" + name;
        PasterDreamAPI.LOGGER.debug("[ParticleBuilder] 创建粒子构建器: modId={}, name={}, 默认纹理={}", modId, name, texturePath);
    }

    /**
     * 创建一个新的粒子构建器（静态工厂方法）
     * <p>
     * 使用此方法无需通过 {@link ParticleAPI}，
     * 可直接在注册类中独立使用。
     *
     * @param name 粒子注册名称
     * @return 粒子构建器实例
     */
    public static ParticleBuilder builder(String name) {
        return new ParticleBuilder(PasterDreamAPI.MOD_ID, name);
    }

    /**
     * 设置粒子始终更新（alwaysShow / alwaysUpdate）
     * <p>
     * 对应 {@link SimpleParticleType} 构造函数的 alwaysUpdate 参数。
     * 设置为 true 时，粒子即使在距离玩家较远时也会更新渲染。
     *
     * @return 当前构建器实例
     */
    public ParticleBuilder alwaysShow() {
        this.alwaysShow = true;
        PasterDreamAPI.LOGGER.debug("[ParticleBuilder] {} → alwaysShow=true", name);
        return this;
    }

    /**
     * 设置粒子始终更新状态
     *
     * @param alwaysShow 是否始终更新
     * @return 当前构建器实例
     */
    public ParticleBuilder alwaysShow(boolean alwaysShow) {
        this.alwaysShow = alwaysShow;
        PasterDreamAPI.LOGGER.debug("[ParticleBuilder] {} → alwaysShow={}", name, alwaysShow);
        return this;
    }

    /**
     * 设置粒子纹理路径
     * <p>
     * 用于生成 {@code particles/{name}.json} 中引用的纹理路径。
     * 默认为 {@code {modId}:{name}}。
     *
     * @param texturePath 纹理路径（如 "pasterdream:sparkle"）
     * @return 当前构建器实例
     */
    public ParticleBuilder texture(String texturePath) {
        this.texturePath = texturePath;
        PasterDreamAPI.LOGGER.debug("[ParticleBuilder] {} → texture={}", name, texturePath);
        return this;
    }

    /**
     * 设置粒子重力值
     * <p>
     * <b>注意：</b>此值仅用于文档记录和配置文件提示。
     * {@link SimpleParticleType} 本身不包含重力参数，
     * 实际重力效果需要在自定义 {@link net.minecraft.client.particle.Particle} 类中
     * 通过 {@code this.gravity} 字段实现。
     *
     * @param gravity 重力值（正数向下加速，如 0.05f）
     * @return 当前构建器实例
     */
    public ParticleBuilder withGravity(float gravity) {
        this.gravity = gravity;
        PasterDreamAPI.LOGGER.debug("[ParticleBuilder] {} → gravity={}", name, gravity);
        return this;
    }

    /**
     * 设置是否自动生成粒子资源 JSON 文件
     * <p>
     * 默认为 true，会在 {@link #build()} 时自动生成：
     * <ul>
     *   <li>{@code particles/{name}.json} — 粒子定义（纹理列表）</li>
     *   <li>{@code textures/particle/{name}.json} — 粒子纹理元数据</li>
     * </ul>
     *
     * @param generate 是否生成 JSON 文件
     * @return 当前构建器实例
     */
    public ParticleBuilder generateJson(boolean generate) {
        this.generateJsonFiles = generate;
        PasterDreamAPI.LOGGER.debug("[ParticleBuilder] {} → generateJson={}", name, generate);
        return this;
    }

    /**
     * 设置资源文件基础路径
     * <p>
     * 默认为 {@code "src/main/resources"}。
     *
     * @param basePath 资源根目录
     * @return 当前构建器实例
     */
    public ParticleBuilder basePath(String basePath) {
        this.basePath = basePath;
        PasterDreamAPI.LOGGER.debug("[ParticleBuilder] {} → basePath={}", name, basePath);
        return this;
    }

    /**
     * 执行构建，完成粒子类型注册和资源文件生成
     * <p>
     * 完成以下工作：
     * <ol>
     *   <li>通过 ParticleAPI 的注册器注册 {@link SimpleParticleType}</li>
     *   <li>可选：生成 {@code particles/{name}.json} 粒子定义文件</li>
     *   <li>可选：生成 {@code textures/particle/{name}.json} 纹理元数据文件</li>
     *   <li>返回 {@link ParticleResult} 包含粒子类型引用</li>
     * </ol>
     *
     * @return {@link ParticleResult} 包含粒子类型的所有引用信息
     * @throws RuntimeException 如果 JSON 文件写入失败
     */
    public ParticleResult build() {
        PasterDreamAPI.LOGGER.info("[ParticleBuilder] ===== 开始构建粒子: {} =====", name);
        PasterDreamAPI.LOGGER.info("[ParticleBuilder]   配置: alwaysShow={}, texture={}, gravity={}, generateJson={}",
                alwaysShow, texturePath, gravity, generateJsonFiles);

        // 注册 SimpleParticleType
        PasterDreamAPI.LOGGER.debug("[ParticleBuilder] 注册 SimpleParticleType: {} (alwaysShow={})", name, alwaysShow);
        DeferredHolder<net.minecraft.core.particles.ParticleType<?>, SimpleParticleType> holder =
                ParticleAPI.REGISTRY.register(name, () -> new SimpleParticleType(alwaysShow));
        PasterDreamAPI.LOGGER.info("[ParticleBuilder] ✅ SimpleParticleType 已注册: {} | holder={}", name, holder);

        // 生成资源文件
        if (generateJsonFiles) {
            try {
                PasterDreamAPI.LOGGER.info("[ParticleBuilder] ===== 开始生成粒子资源文件: {} =====", name);

                // 生成 particles/{name}.json（粒子定义）
                PasterDreamAPI.LOGGER.debug("[ParticleBuilder] 生成粒子定义 JSON: {}/{}.json", basePath, name);
                new ParticleGenerator(modId, name)
                        .addTexture(texturePath)
                        .saveToFile(basePath);

                // 生成 textures/particle/{name}.json（纹理元数据）
                PasterDreamAPI.LOGGER.debug("[ParticleBuilder] 生成粒子纹理元数据: {} (gravity={})", name, gravity);
                new ParticleTextureGenerator(modId, name)
                        .withGravity(gravity)
                        .saveToFile(basePath);

                PasterDreamAPI.LOGGER.info("[ParticleBuilder] ✅ 粒子资源文件生成完成: {}", name);
            } catch (IOException e) {
                PasterDreamAPI.LOGGER.error("[ParticleBuilder] ❌ 无法生成粒子资源文件 [{}]: {}", name, e.getMessage(), e);
                throw new RuntimeException("ParticleBuilder: 无法生成粒子资源文件 [" + name + "]", e);
            }
        } else {
            PasterDreamAPI.LOGGER.info("[ParticleBuilder] ⏭️ 跳过 JSON 文件生成: {} (generateJson=false)", name);
        }

        ParticleResult result = new ParticleResult(name, holder);
        PasterDreamAPI.LOGGER.debug("[ParticleBuilder] 创建 ParticleResult: name={}, holder={}", name, holder);

        // 缓存到 ParticleAPI 中，方便后续查询
        ParticleAPI.cacheParticle(result);

        PasterDreamAPI.LOGGER.info("[ParticleBuilder] ✅ 粒子构建完成: {} | result={}", name, result);
        return result;
    }
}