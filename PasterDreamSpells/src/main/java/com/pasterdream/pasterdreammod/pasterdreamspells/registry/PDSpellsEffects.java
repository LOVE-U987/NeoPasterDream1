package com.pasterdream.pasterdreammod.pasterdreamspells.registry;

import com.pasterdream.pasterdreammod.api.attribute.APIAttributes;
import com.pasterdream.pasterdreammod.api.effect.MobEffectAPI;
import com.pasterdream.pasterdreammod.pasterdreamspells.PasterDreamSpellsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 法术系统状态效果注册类。
 * <p>
 * 负责注册狂暴法术、冰冻法术等法术相关效果。
 * 所有效果均使用 {@link PasterDreamSpellsMod#MOD_ID}（{@code pasterdreamspells}）命名空间。
 *
 * @author PasterDream
 */
public class PDSpellsEffects {

    /** 状态效果注册器 */
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, PasterDreamSpellsMod.MOD_ID);

    /**
     * 狂暴法术增益 (fury_spell_buff)
     * 紫红色有益效果：攻击力 +4、攻击速度 +3、移动速度 +0.05、
     * 战技冷却 / 瞬身冷却 -0.3（与原版 FurySpellBuffPr0 一致）。
     */
    public static final DeferredHolder<MobEffect, MobEffect> FURY_SPELL_BUFF =
            MobEffectAPI.REGISTRY.register("fury_spell_buff",
                    () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xFFB655EC) {
                    }
                            .addAttributeModifier(Attributes.ATTACK_DAMAGE,
                                    modifierId("fury_spell_buff_0"),
                                    4, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                                    modifierId("fury_spell_buff_1"),
                                    0.05, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(Attributes.ATTACK_SPEED,
                                    modifierId("fury_spell_buff_2"),
                                    3, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(APIAttributes.SKILLCD, modifierId("fury_spell_buff_3"),
                                    -0.3, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(APIAttributes.TELEPORTATIONCD, modifierId("fury_spell_buff_4"),
                                    -0.3, AttributeModifier.Operation.ADD_VALUE));

    /**
     * 冰冻法术减益 (ice_spell_buff)
     * 冰蓝色有害效果：移动速度 -1（完全定身）、攻击力 -100（数值与原版一致）。
     */
    public static final DeferredHolder<MobEffect, MobEffect> ICE_SPELL_BUFF =
            MobEffectAPI.REGISTRY.register("ice_spell_buff",
                    () -> new MobEffect(MobEffectCategory.HARMFUL, 0xFFB8ECF6) {
                    }
                            .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                                    modifierId("ice_spell_buff_0"),
                                    -1, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(Attributes.ATTACK_DAMAGE,
                                    modifierId("ice_spell_buff_1"),
                                    -100, AttributeModifier.Operation.ADD_VALUE));

    private PDSpellsEffects() {
        throw new UnsupportedOperationException("PDSpellsEffects 是注册类，不可实例化");
    }

    /**
     * 生成法术系统专属的 attribute modifier ID。
     *
     * @param path 路径
     * @return 带 pasterdreamspells 命名空间的资源定位
     */
    private static ResourceLocation modifierId(String path) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamSpellsMod.MOD_ID, path);
    }
}
