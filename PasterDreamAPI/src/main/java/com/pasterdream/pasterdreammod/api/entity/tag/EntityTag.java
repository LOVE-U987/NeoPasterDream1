package com.pasterdream.pasterdreammod.api.entity.tag;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import net.minecraft.resources.ResourceLocation;

/**
 * 内置实体标签枚举 —— 为模组实体提供统一的分类与行为标记
 * <p>
 * 每个标签对应一个 {@link ResourceLocation}，并在 {@link EntityTagRegistry} 中绑定到具体实体类型。
 * 主代码可通过 {@link com.pasterdream.pasterdreammod.api.entity.builder.EntityBuilder#tag(EntityTag...)} 或
 * {@link EntityTagRegistry} 的批量方法快速为实体添加标签。
 * <p>
 * 当前内置标签：
 * <ul>
 *   <li>{@link #LAMP_SHADOW_MONSTER} — 灯影怪物，同标签实体之间不会产生友伤</li>
 *   <li>{@link #SPELL_INVINCIBLE} — 法术无敌实体，加入世界后自动设置为无敌状态</li>
 * </ul>
 *
 * @see EntityTagRegistry
 * @see com.pasterdream.pasterdreammod.api.entity.builder.EntityBuilder
 */
public enum EntityTag {

    /**
     * 灯影怪物标签
     * <p>
     * 用于标记灯影维度/暗影主题的敌对生物，使其不会互相造成伤害（避免友伤）。
     * 标签 ID：{@code pasterdream:lamp_shadow_monster}
     */
    LAMP_SHADOW_MONSTER("lamp_shadow_monster"),

    /**
     * 法术无敌标签
     * <p>
     * 用于标记治疗立场、狂暴立场等展示性法术实体，使其加入世界后自动无敌。
     * 标签 ID：{@code pasterdream:spell_invincible}
     */
    SPELL_INVINCIBLE("spell_invincible");

    /** 标签唯一资源标识 */
    private final ResourceLocation id;

    /**
     * 构造实体标签
     *
     * @param path 标签路径（不含命名空间）
     */
    EntityTag(String path) {
        this.id = ResourceLocation.fromNamespaceAndPath(PasterDreamAPI.DATA_NAMESPACE, path);
    }

    /**
     * 获取标签的资源标识
     *
     * @return 标签 {@link ResourceLocation}
     */
    public ResourceLocation getId() {
        return id;
    }

    /**
     * 通过资源路径查找标签
     *
     * @param path 标签路径（不含命名空间）
     * @return 对应的 {@link EntityTag}，未找到时返回 {@code null}
     */
    public static EntityTag byPath(String path) {
        for (EntityTag tag : values()) {
            if (tag.id.getPath().equals(path)) {
                return tag;
            }
        }
        return null;
    }
}

