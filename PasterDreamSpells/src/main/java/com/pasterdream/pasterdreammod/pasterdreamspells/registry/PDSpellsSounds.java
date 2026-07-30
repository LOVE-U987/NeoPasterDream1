package com.pasterdream.pasterdreammod.pasterdreamspells.registry;

import com.pasterdream.pasterdreammod.pasterdreamspells.PasterDreamSpellsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * 法术系统声音事件注册类。
 * <p>
 * 负责注册五种法术的命中/持续音效，所有注册均使用
 * {@link PasterDreamSpellsMod#MOD_ID}（{@code pasterdreamspells}）命名空间。
 *
 * @author PasterDream
 */
public class PDSpellsSounds {

    /** 声音事件延迟注册器 */
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, PasterDreamSpellsMod.MOD_ID);

    /**
     * 闪电法术命中音效 (lightning_spell)
     */
    public static final Supplier<SoundEvent> LIGHTNING_SPELL = SOUND_EVENTS.register("lightning_spell",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamSpellsMod.MOD_ID, "lightning_spell")));

    /**
     * 狂暴法术命中音效 (fury_spell_0)
     */
    public static final Supplier<SoundEvent> FURY_SPELL_0 = SOUND_EVENTS.register("fury_spell_0",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamSpellsMod.MOD_ID, "fury_spell_0")));

    /**
     * 治疗法术命中音效 (healing_spell)
     */
    public static final Supplier<SoundEvent> HEALING_SPELL = SOUND_EVENTS.register("healing_spell",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamSpellsMod.MOD_ID, "healing_spell")));

    /**
     * 冰冻法术命中音效 (ice_spell)
     */
    public static final Supplier<SoundEvent> ICE_SPELL = SOUND_EVENTS.register("ice_spell",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamSpellsMod.MOD_ID, "ice_spell")));

    /**
     * 狂暴法术持续音效 (fury_spell)
     */
    public static final Supplier<SoundEvent> FURY_SPELL = SOUND_EVENTS.register("fury_spell",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamSpellsMod.MOD_ID, "fury_spell")));

    /**
     * 剧毒法术命中音效 (poison_spell)
     */
    public static final Supplier<SoundEvent> POISON_SPELL = SOUND_EVENTS.register("poison_spell",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamSpellsMod.MOD_ID, "poison_spell")));

    private PDSpellsSounds() {
        throw new UnsupportedOperationException("PDSpellsSounds 是注册类，不可实例化");
    }
}
