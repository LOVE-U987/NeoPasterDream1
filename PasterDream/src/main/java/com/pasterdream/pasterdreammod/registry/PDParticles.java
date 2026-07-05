package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.particle.ParticleAPI;
import com.pasterdream.pasterdreammod.api.particle.ParticleResult;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 粒子类型注册类
 * <p>
 * 使用 DeferredRegister 模式注册所有自定义粒子类型。
 * 内部集成 {@link ParticleAPI}，所有注册通过 API 统一管理。
 * <p>
 * 所有粒子均使用 {@link ParticleAPI#createParticle(String)} 的 Builder 方式注册。
 * <p>
 * 使用示例（新粒子）：
 * <pre>{@code
 * // 在任意注册类中：
 * ParticleResult sparkle = ParticleAPI.createParticle("sparkle")
 *     .alwaysShow()
 *     .texture("pasterdream:sparkle")
 *     .build();
 * }</pre>
 */
public class PDParticles {

    /**
     * 粒子类型注册器（与 ParticleAPI 共享同一个注册器）
     * <p>
     * 注意：需要在 {@code PasterDreamMod} 构造函数中注册到事件总线：
     * <pre>{@code
     * PDParticles.PARTICLE_TYPES.register(modEventBus);
     * }</pre>
     */
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = ParticleAPI.REGISTRY;

    /**
     * 融梦水晶粒子（meltdream_crystal_particle）
     * 用于生命水晶的发光粒子效果
     */
    public static final ParticleResult MELTDREAM_CRYSTAL_PARTICLE = ParticleAPI.createParticle("meltdream_crystal_particle")
            .alwaysShow(false)
            .texture("pasterdream:meltdream_crystal_particle")
            .generateJson(false)
            .build();

    /**
     * 暗影石粒子（shadow_stone_particle）
     * 用于暗影魔像技能爆炸的碎石粒子效果
     */
    public static final ParticleResult SHADOW_STONE_PARTICLE = ParticleAPI.createParticle("shadow_stone_particle")
            .alwaysShow(false)
            .texture("pasterdream:shadow_stone_particle")
            .generateJson(false)
            .build();

    /**
     * 衍梦肥泥粒子（dreamfertiliter_particle）
     * 用于衍梦肥泥使用时的绿色魔法粒子效果
     */
    public static final ParticleResult DREAMFERTILITER_PARTICLE = ParticleAPI.createParticle("dreamfertiliter_particle")
            .alwaysShow(false)
            .texture("pasterdream:dreamfertiliter_particle")
            .generateJson(false)
            .build();

    /**
     * 梦境环境粒子（dream_ambient_particle）
     * 使用 ParticleAPI Builder 注册，用于染梦维度的漂浮梦境粉尘效果
     */
    public static final ParticleResult DREAM_AMBIENT_PARTICLE = ParticleAPI.createParticle("dream_ambient_particle")
            .alwaysShow(true)
            .texture("pasterdream:dream_ambient_particle_1")
            .withGravity(-0.005f)
            .generateJson(false)
            .build();

    /**
     * 落叶粒子（leaves_particle）
     * 使用 ParticleAPI Builder 注册，用于染梦树叶/樱花的飘落落叶效果。
     */
    public static final ParticleResult LEAVES_PARTICLE = ParticleAPI.createParticle("leaves_particle")
            .alwaysShow(true)
            .texture("pasterdream:leaves_particle")
            .withGravity(0.02f)
            .generateJson(false)
            .build();

    /**
     * 召唤粒子（calle_particle）
     * 3帧金色火焰粒子，用于炎热森林生物群系的环境效果。
     */
    public static final ParticleResult CALLE_PARTICLE = ParticleAPI.createParticle("calle_particle")
            .alwaysShow(true)
            .texture("pasterdream:calle_particle_1")
            .withGravity(0f)
            .generateJson(false)
            .build();

    /**
     * 银色粒子（silver_particle）
     * 3帧冰晶银色旋转粒子，用于寒冷冰雪生物群系的环境效果。
     */
    public static final ParticleResult SILVER_PARTICLE = ParticleAPI.createParticle("silver_particle")
            .alwaysShow(true)
            .texture("pasterdream:silver_particle_1")
            .withGravity(-0.003f)
            .generateJson(false)
            .build();

    /**
     * 裂纹粒子（crack_0_particle）
     * 1帧紫色半透明粒子，用于温暖海洋生物群系的环境效果。
     */
    public static final ParticleResult CRACK_0_PARTICLE = ParticleAPI.createParticle("crack_0_particle")
            .alwaysShow(true)
            .texture("pasterdream:crack_0_particle")
            .withGravity(-0.005f)
            .generateJson(false)
            .build();

    /**
     * 白星粒子（white_star_particle）
     * 4帧白色星光粒子，用于炎热森林生物群系的环境效果。
     */
    public static final ParticleResult WHITE_STAR_PARTICLE = ParticleAPI.createParticle("white_star_particle")
            .alwaysShow(true)
            .texture("pasterdream:white_star_particle_1")
            .withGravity(0f)
            .generateJson(false)
            .build();

    /**
     * 雪花粒子（snowflake_0_particle）
     * 1帧蓝色雪花粒子，用于温暖海洋生物群系的地面环境效果。
     */
    public static final ParticleResult SNOWFLAKE_0_PARTICLE = ParticleAPI.createParticle("snowflake_0_particle")
            .alwaysShow(true)
            .texture("pasterdream:snowflake_0_particle")
            .withGravity(0.01f)
            .generateJson(false)
            .build();

    /**
     * 荧光羽毛粒子（feather_white_particle）
     * 12帧白色羽毛粒子，用于染梦深海生物群系的发光浮游生物效果。
     */
    public static final ParticleResult FEATHER_WHITE_PARTICLE = ParticleAPI.createParticle("feather_white_particle")
            .alwaysShow(true)
            .texture("pasterdream:feather_white_particle")
            .withGravity(-0.004f)
            .generateJson(false)
            .build();

    /**
     * 染梦孢子粒子（dyedream_0_particle）
     * 1帧暖金色孢子粒子，用于蘑菇平原生物群系的发光孢子粉尘效果。
     */
    public static final ParticleResult DYEDREAM_0_PARTICLE = ParticleAPI.createParticle("dyedream_0_particle")
            .alwaysShow(true)
            .texture("pasterdream:dyedream_0_particle")
            .withGravity(0.003f)
            .generateJson(false)
            .build();

    /**
     * 孢子粒子（spore_particle）
     * 4帧绿色孢子粒子，用于孢子实体飞行的孢子粉尘效果。
     */
    public static final ParticleResult SPORE_PARTICLE = ParticleAPI.createParticle("spore_particle")
            .alwaysShow(false)
            .texture("pasterdream:spore_particle")
            .generateJson(false)
            .build();

    /**
     * 狐火粒子0（fox_fire_0_particle）
     * 用于狐火实体每 tick 散发的橙色火焰粒子效果。
     */
    public static final ParticleResult FOX_FIRE_0_PARTICLE = ParticleAPI.createParticle("fox_fire_0_particle")
            .alwaysShow(false)
            .texture("pasterdream:fox_fire_0_particle")
            .generateJson(false)
            .build();

    /**
     * 狐火粒子1（fox_fire_1_particle）
     * 用于狐火实体每 tick 散发的红色火焰粒子效果。
     */
    public static final ParticleResult FOX_FIRE_1_PARTICLE = ParticleAPI.createParticle("fox_fire_1_particle")
            .alwaysShow(false)
            .texture("pasterdream:fox_fire_1_particle")
            .generateJson(false)
            .build();

    // ======================== 染梦世界动态环境粒子 ========================

    /**
     * 梦幻孢子粒子（dream_spore）
     * <p>
     * 紫色/粉色渐变发光孢子，用于梦幻平原生物群系的环境效果。
     * 缓慢旋转飘浮，带有柔和呼吸发光效果，营造梦幻氛围。
     */
    public static final ParticleResult DREAM_SPORE = ParticleAPI.createParticle("dream_spore")
            .alwaysShow(true)
            .texture("pasterdream:dream_spore")
            .withGravity(-0.003f)
            .generateJson(false)
            .build();

    /**
     * 水晶雪花粒子（crystal_snowflake）
     * <p>
     * 白色半透明冰晶雪花，用于寒冷染梦生物群系的环境效果。
     * 缓慢旋转飘落，带有微弱的冷光闪烁，营造冰雪仙境氛围。
     */
    public static final ParticleResult CRYSTAL_SNOWFLAKE = ParticleAPI.createParticle("crystal_snowflake")
            .alwaysShow(true)
            .texture("pasterdream:crystal_snowflake")
            .withGravity(0.008f)
            .generateJson(false)
            .build();

    /**
     * 极光粒子（aurora_glow）
     * <p>
     * 半透明带状极光粒子，用于深海和极地生物群系的环境效果。
     * 水平缓慢飘动，颜色在青/蓝/紫之间渐变，模拟微缩极光效果。
     */
    public static final ParticleResult AURORA_GLOW = ParticleAPI.createParticle("aurora_glow")
            .alwaysShow(true)
            .texture("pasterdream:aurora_glow")
            .withGravity(-0.002f)
            .generateJson(false)
            .build();

    /**
     * 星尘粒子（stardust）
     * <p>
     * 微小白点星尘，用于多种染梦生物群系的环境点缀效果。
     * 旋转飘落，带有随机闪烁，模拟夜空中的星尘飘散。
     */
    public static final ParticleResult STARDUST = ParticleAPI.createParticle("stardust")
            .alwaysShow(true)
            .texture("pasterdream:stardust")
            .withGravity(-0.001f)
            .generateJson(false)
            .build();

    /**
     * 执行粒子注册（静态调用入口）
     * <p>
     * 此方法主要为提供显式的注册入口而存在。
     * 实际的粒子注册已在静态字段初始化时完成。
     */
    public static void register() {
        PasterDreamMod.LOGGER.debug("[PDParticles] 粒子注册已通过 ParticleAPI 统一管理，共 {} 个粒子",
                ParticleAPI.getRegisteredParticles().size());
    }
}