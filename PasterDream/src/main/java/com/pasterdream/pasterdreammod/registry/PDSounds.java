package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.ApiSoundRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

/**
 * 声音事件注册类 —— 管理模组中所有自定义声音、背景音乐的注册
 * <p>
 * 通过 {@link DeferredRegister} 注册所有 {@link SoundEvent}。
 * 维度背景音乐统一通过 {@link ApiSoundRegistry} 注册，复用其 {@code .ogg} 文件存在性校验。
 * <p>
 * 注意：注册 SoundEvent 后还需要在 {@code sounds.json} 中声明对应的声音条目，
 * 详见 {@link com.pasterdream.pasterdreammod.api.dimension.gen.SoundsJsonGenerator}。
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 在 PasterDreamMod 构造函数中：
 * PDSounds.SOUND_EVENTS.register(modEventBus);
 *
 * // 获取已注册的音乐事件：
 * Optional<Supplier<SoundEvent>> music = PDSounds.getDimensionMusic("dyedream_world");
 * }</pre>
 */
public class PDSounds {

    private PDSounds() {
        throw new UnsupportedOperationException("PDSounds 是不可实例化的注册类");
    }

    /**
     * 声音事件延迟注册器
     */
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, PasterDreamMod.MOD_ID);

    static {
        registerDimensionMusic("dyedream_world");
        registerDimensionMusic("dream_meadow");
        registerDimensionMusic("dream_heath");
        registerDimensionMusic("dream_taiga");
        registerDimensionMusic("dream_delta");
        registerDimensionMusic("sweetdream_music");
        registerDimensionMusic("snowfall_dream_music");
    }

    // ==================== 融梦水晶箱 SoundEvent ====================

    /**
     * 融梦水晶箱 —— 普通/稀有品质音效
     */
    public static final Supplier<SoundEvent> MELTDREAM_CHEST_0 = SOUND_EVENTS.register("meltdream_chest_0",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "meltdream_chest_0")));

    /**
     * 融梦水晶箱 —— 传说品质音效
     */
    public static final Supplier<SoundEvent> MELTDREAM_CHEST = SOUND_EVENTS.register("meltdream_chest",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "meltdream_chest")));

    // ==================== 唱片音乐 SoundEvent ====================

    /**
     * 甜蜜的梦 唱片音乐 SoundEvent
     */
    public static final Supplier<SoundEvent> SWEETDREAM_MUSIC = SOUND_EVENTS.register("sweetdream",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "sweetdream_music")));

    /**
     * 落雪之梦 唱片音乐 SoundEvent
     */
    public static final Supplier<SoundEvent> SNOWFALL_DREAM_MUSIC = SOUND_EVENTS.register("snowfall_dream_music",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "snowfall_dream_music")));

    /**
     * 亚伦柯斯之触 唱片音乐 SoundEvent
     */
    public static final Supplier<SoundEvent> AARONCOS_MUSIC = SOUND_EVENTS.register("aaroncos_music",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "aaroncos_music")));

    /**
     * 风之旅途 唱片音乐 SoundEvent
     */
    public static final Supplier<SoundEvent> WIND_JOURNEY_MUSIC = SOUND_EVENTS.register("wind_journey",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "wind_journey")));

    // ==================== 染梦群系背景音乐 唱片 SoundEvent ====================

    /**
     * 梦幻草原 唱片音乐 SoundEvent
     */
    public static final Supplier<SoundEvent> DREAM_MEADOW_MUSIC = SOUND_EVENTS.register("dream_meadow_music",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "music/dream_meadow")));

    /**
     * 梦幻荒原 唱片音乐 SoundEvent
     */
    public static final Supplier<SoundEvent> DREAM_HEATH_MUSIC = SOUND_EVENTS.register("dream_heath_music",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "music/dream_heath")));

    /**
     * 梦幻雪林 唱片音乐 SoundEvent
     */
    public static final Supplier<SoundEvent> DREAM_TAIGA_MUSIC = SOUND_EVENTS.register("dream_taiga_music",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "music/dream_taiga")));

    /**
     * 梦幻三角洲 唱片音乐 SoundEvent
     */
    public static final Supplier<SoundEvent> DREAM_DELTA_MUSIC = SOUND_EVENTS.register("dream_delta_music",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "music/dream_delta")));

    // ==================== 维度背景音乐（统一注册入口） ====================

    /**
     * 注册一个维度背景音乐 SoundEvent。
     * <p>
     * 委托给 {@link ApiSoundRegistry} 处理，复用其 .ogg 文件存在性校验和缓存机制。
     * ID 格式为 {@code music.{musicName}}，声音文件对应
     * {@code assets/pasterdream/sounds/music/{musicName}.ogg}。
     *
     * @param musicName 音乐名称（如 "dyedream_world"）
     * @return 已注册的 SoundEvent Supplier；若文件缺失则返回 {@code null}
     */
    @Nullable
    public static Supplier<SoundEvent> registerDimensionMusic(String musicName) {
        return ApiSoundRegistry.registerDimensionMusic(musicName);
    }

    /**
     * 获取已注册的维度背景音乐。
     * <p>
     * 实际注册与缓存由 {@link ApiSoundRegistry} 处理，此处提供统一的访问入口。
     *
     * @param musicName 音乐名称（与注册时一致）
     * @return 包含 SoundEvent Supplier 的 {@link Optional}，如果对应 {@code .ogg} 文件缺失或未注册则返回空 Optional
     */
    public static Optional<Supplier<SoundEvent>> getDimensionMusic(String musicName) {
        return ApiSoundRegistry.getDimensionMusic(musicName);
    }

    // ==================== 实体技能音效 ====================

    /**
     * 暗影尖啸幽灵召唤音效 (ghost0)
     */
    public static final Supplier<SoundEvent> GHOST_0 = SOUND_EVENTS.register("ghost0",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "ghost0")));

    /**
     * 黑甲虫母体技能音效 (beetle_skill)
     */
    public static final Supplier<SoundEvent> BEETLE_SKILL = SOUND_EVENTS.register("beetle_skill",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "beetle_skill")));

    /**
     * 骨翼火球发射音效 (bone_wing_fire_ball)
     */
    public static final Supplier<SoundEvent> BONE_WING_FIRE_BALL = SOUND_EVENTS.register("bone_wing_fire_ball",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "bone_wing_fire_ball")));

    /**
     * 雷云攻击音效 (thundercloud_attack)
     */
    public static final Supplier<SoundEvent> THUNDERCLOUD_ATTACK = SOUND_EVENTS.register("thundercloud_attack",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "thundercloud_attack")));

    /**
     * 恐怖尖喙咆哮音效 (terrorbeak_roar)
     */
    public static final Supplier<SoundEvent> TERRORBEAK_ROAR = SOUND_EVENTS.register("terrorbeak_roar",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "terrorbeak_roar")));

    /**
     * 风之骑士技能音效 (wind_knight_skill_0)
     */
    public static final Supplier<SoundEvent> WIND_KNIGHT_SKILL_0 = SOUND_EVENTS.register("wind_knight_skill_0",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "wind_knight_skill_0")));

    /**
     * 暗影音效 (shadow0)
     * 暗影幽灵、暗影之手等暗影系实体使用的通用音效。
     */
    public static final Supplier<SoundEvent> SHADOW_0 = SOUND_EVENTS.register("shadow0",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "shadow0")));

    /**
     * 暗影尖啸波音效 (squeal_wave)
     * 暗影尖啸幽灵发射的音波弹射物音效。
     */
    public static final Supplier<SoundEvent> SQUEAL_WAVE = SOUND_EVENTS.register("squeal_wave",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "squeal_wave")));

    /**
     * 黑甲虫攻击音效 (beetle_attack)
     */
    public static final Supplier<SoundEvent> BEETLE_ATTACK = SOUND_EVENTS.register("beetle_attack",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "beetle_attack")));

    /**
     * 石头碎裂音效 (stone_break)
     * 小石灵技能使用的音效。
     */
    public static final Supplier<SoundEvent> STONE_BREAK = SOUND_EVENTS.register("stone_break",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "stone_break")));

    /**
     * 石头碎裂音效变体 (stone_break_0)
     * 小石灵技能使用的音效变体。
     */
    public static final Supplier<SoundEvent> STONE_BREAK_0 = SOUND_EVENTS.register("stone_break_0",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "stone_break_0")));

    /**
     * 狐火生成音效 (fox_fire)
     * 狐火实体生成时播放的特殊环境音效。
     */
    public static final Supplier<SoundEvent> FOX_FIRE = SOUND_EVENTS.register("fox_fire",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "fox_fire")));

    // ==================== BOSS 实体音效 ====================

    /**
     * 亚伦柯斯生成/鲜血锁链触发音效 (aaroncos_spawn)
     * BOSS 亚伦柯斯生成时或使用鲜血锁链技能时触发的音效。
     */
    public static final Supplier<SoundEvent> AARONCOS_SPAWN = SOUND_EVENTS.register("aaroncos_spawn",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "aaroncos_spawn")));

    // ==================== 暗影系技能音效 ====================

    /**
     * 暗影门开启音效 (shadow_door)
     * 暗影地牢门开启、战利品箱打开等场景使用。
     */
    public static final Supplier<SoundEvent> SHADOW_DOOR = SOUND_EVENTS.register("shadow_door",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "shadow_door")));

    /**
     * 暗影漩涡音效 (shadow_vortex)
     * 亚伦柯斯右手 BOSS 使用漩涡技能时触发。
     */
    public static final Supplier<SoundEvent> SHADOW_VORTEX = SOUND_EVENTS.register("shadow_vortex",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "shadow_vortex")));

    /**
     * 暗影漩涡之书音效 (shadow_vortex_book)
     * 暗影漩涡书投射物飞行时的音效。
     */
    public static final Supplier<SoundEvent> SHADOW_VORTEX_BOOK = SOUND_EVENTS.register("shadow_vortex_book",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "shadow_vortex_book")));

    /**
     * 暗影剑音效 (shadow_sword)
     * 暗影剑攻击时的音效。
     */
    public static final Supplier<SoundEvent> SHADOW_SWORD = SOUND_EVENTS.register("shadow_sword",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "shadow_sword")));

    /**
     * 暗影之手灯笼音效 (shadow_hand_lantern)
     * 暗影之手使用灯笼技能时触发。
     */
    public static final Supplier<SoundEvent> SHADOW_HAND_LANTERN = SOUND_EVENTS.register("shadow_hand_lantern",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "shadow_hand_lantern")));

    /**
     * 暗影背景音乐 (shadow_music_0)
     * 暗影群系或暗影地牢内播放的环境音乐。
     */
    public static final Supplier<SoundEvent> SHADOW_MUSIC_0 = SOUND_EVENTS.register("shadow_music_0",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "shadow_music_0")));

    /**
     * 暗影陷阱音效 (shadow_trap_0)
     * 暗影陷阱触发时的音效。
     */
    public static final Supplier<SoundEvent> SHADOW_TRAP_0 = SOUND_EVENTS.register("shadow_trap_0",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "shadow_trap_0")));

    // ==================== 剑系技能音效 ====================

    /**
     * 剑波音效 (sword_wave)
     * 亚伦柯斯左手 BOSS 使用剑波技能时触发。
     */
    public static final Supplier<SoundEvent> SWORD_WAVE = SOUND_EVENTS.register("sword_wave",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "sword_wave")));

    /**
     * 剑击音效 (sword1)
     * 各种剑类武器攻击时的通用音效。
     */
    public static final Supplier<SoundEvent> SWORD1 = SOUND_EVENTS.register("sword1",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "sword1")));

    /**
     * 白色剑雨音效 (white_sword_rain)
     * 白色剑雨技能触发时的音效。
     */
    public static final Supplier<SoundEvent> WHITE_SWORD_RAIN = SOUND_EVENTS.register("white_sword_rain",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "white_sword_rain")));

    // ==================== 通用技能音效 ====================

    /**
     * 技能音效 0 (skill0)
     * 通用技能释放音效。
     */
    public static final Supplier<SoundEvent> SKILL0 = SOUND_EVENTS.register("skill0",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "skill0")));

    /**
     * 技能音效 1 (skill1)
     * 通用技能释放音效变体。
     */
    public static final Supplier<SoundEvent> SKILL1 = SOUND_EVENTS.register("skill1",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "skill1")));

    /**
     * 技能音效 2 (skill2)
     * 通用技能释放音效变体。
     */
    public static final Supplier<SoundEvent> SKILL2 = SOUND_EVENTS.register("skill2",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "skill2")));
}
