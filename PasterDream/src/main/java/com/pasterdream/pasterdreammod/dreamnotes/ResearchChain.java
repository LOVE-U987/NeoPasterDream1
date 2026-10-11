package com.pasterdream.pasterdreammod.dreamnotes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 研究台研究梯度（同步动态注册表 {@code pasterdream:note_research} 条目）。
 * <p>
 * {@link #steps} 按「已完成的前置成就」从高到低排列，研究时取首个满足前置的步骤产出其产物；
 * {@link #completion} 完成后只给经验并关闭界面。
 *
 * @param steps          研究步骤（高梯度在前）
 * @param completion     全部研究完成的前置成就
 * @param completionExp  全部完成时的经验补偿
 */
public record ResearchChain(
        List<Step> steps,
        ResourceLocation completion,
        int completionExp
) {

    /**
     * 单个研究步骤。
     *
     * @param required 需已完成的前置成就
     * @param product  产出物品
     */
    public record Step(ResourceLocation required, ItemStack product) {
    }

    private static final Codec<Step> STEP_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("required").forGetter(Step::required),
            ItemStack.CODEC.fieldOf("product").forGetter(Step::product)
    ).apply(instance, Step::new));

    /** 数据包 JSON 编解码器（同步网络复用）。 */
    public static final Codec<ResearchChain> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            STEP_CODEC.listOf().fieldOf("steps").forGetter(ResearchChain::steps),
            ResourceLocation.CODEC.fieldOf("completion").forGetter(ResearchChain::completion),
            Codec.INT.optionalFieldOf("completion_exp", 50).forGetter(ResearchChain::completionExp)
    ).apply(instance, ResearchChain::new));
}
