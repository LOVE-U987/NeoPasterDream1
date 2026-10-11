package com.pasterdream.pasterdreammod.dreamnotes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 寻梦者笔记的数据定义（同步动态注册表 {@code pasterdream:dreamnotes} 条目）。
 * <p>
 * 正文/标题按语言取自 {@link #title}/{@link #body}；成就解锁、坐标行为、附赠物品由数据驱动，
 * 获取链路与研究台梯度复用同一份定义。
 *
 * @param title            语言 -&gt; 标题
 * @param body             语言 -&gt; Markdown 正文
 * @param unlock           解锁成就
 * @param required         前置成就
 * @param locateShadowBase 阅读时是否写入最近暮影据点坐标（笔记 8/9）
 * @param give             成功解锁时附赠的物品
 * @param extraMessages    成功解锁后附加显示的聊天文案语言键
 */
public record NoteDefinition(
        Map<String, String> title,
        Map<String, String> body,
        Optional<ResourceLocation> unlock,
        Optional<ResourceLocation> required,
        boolean locateShadowBase,
        List<ItemStack> give,
        List<String> extraMessages
) {

    /** 数据包 JSON 编解码器（同步网络亦复用此 codec；{@code give} 依赖 RegistryOps）。 */
    public static final Codec<NoteDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("title").forGetter(NoteDefinition::title),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("body").forGetter(NoteDefinition::body),
            ResourceLocation.CODEC.optionalFieldOf("unlock").forGetter(NoteDefinition::unlock),
            ResourceLocation.CODEC.optionalFieldOf("required").forGetter(NoteDefinition::required),
            Codec.BOOL.optionalFieldOf("locate_shadow_base", false).forGetter(NoteDefinition::locateShadowBase),
            ItemStack.CODEC.listOf().optionalFieldOf("give", List.of()).forGetter(NoteDefinition::give),
            Codec.STRING.listOf().optionalFieldOf("extra_messages", List.of()).forGetter(NoteDefinition::extraMessages)
    ).apply(instance, NoteDefinition::new));

    /**
     * 按语言取标题。
     *
     * @param language 语言代码（如 {@code zh_cn}）
     * @return 标题；缺失回退 {@code en_us} -&gt; 首个非空 -&gt; 空串
     */
    public String titleOr(String language) {
        return pick(title, language);
    }

    /**
     * 按语言取正文。
     *
     * @param language 语言代码（如 {@code zh_cn}）
     * @return 正文；缺失回退 {@code en_us} -&gt; 首个非空 -&gt; 空串
     */
    public String bodyOr(String language) {
        return pick(body, language);
    }

    private static String pick(Map<String, String> map, String language) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        String value = map.get(language);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        value = map.get("en_us");
        if (value != null && !value.isEmpty()) {
            return value;
        }
        return map.values().iterator().next();
    }
}
