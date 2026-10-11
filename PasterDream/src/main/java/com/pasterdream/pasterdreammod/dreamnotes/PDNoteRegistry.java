package com.pasterdream.pasterdreammod.dreamnotes;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import org.jetbrains.annotations.Nullable;

/**
 * 寻梦者笔记同步动态注册表（{@code pasterdream:dreamnotes}）。
 * <p>
 * 经 {@link DataPackRegistryEvent.NewRegistry} 注册，提供网络 codec 使 datapack 定义
 * 自动同步至客户端；数据文件位于 {@code data/<ns>/pasterdream/dreamnotes/<entry>.json}。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID)
public final class PDNoteRegistry {

    /** 注册表根 key。 */
    public static final ResourceKey<Registry<NoteDefinition>> NOTE_REGISTRY =
            ResourceKey.createRegistryKey(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dreamnotes"));

    /** 研究梯度注册表根 key。 */
    public static final ResourceKey<Registry<ResearchChain>> RESEARCH_REGISTRY =
            ResourceKey.createRegistryKey(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "note_research"));

    /** 研究梯度的固定条目 ID。 */
    public static final ResourceLocation RESEARCH_TABLE_ID =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "research_table");

    private PDNoteRegistry() {
    }

    /**
     * 注册同步 datapack 注册表（MOD 总线事件）。
     *
     * @param event 新注册表事件
     */
    @SubscribeEvent
    public static void onNewRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(NOTE_REGISTRY, NoteDefinition.CODEC, NoteDefinition.CODEC);
        event.dataPackRegistry(RESEARCH_REGISTRY, ResearchChain.CODEC, ResearchChain.CODEC);
    }

    /**
     * 查询研究梯度链。
     *
     * @param level 世界
     * @return 研究梯度链；注册表或条目缺失返回 null
     */
    @Nullable
    public static ResearchChain research(Level level) {
        if (level == null) {
            return null;
        }
        return level.registryAccess().registry(RESEARCH_REGISTRY)
                .map(registry -> registry.get(RESEARCH_TABLE_ID)).orElse(null);
    }

    /** 显式触发类加载。 */
    public static void bootstrap() {
        Object unused = NOTE_REGISTRY;
    }

    /**
     * 构造某笔记序号对应的定义 ID（注册名与定义 ID 一致）。
     *
     * @param noteId 0..14
     * @return {@code pasterdream:dreamnotes_<noteId>}
     */
    public static ResourceLocation definitionId(int noteId) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dreamnotes_" + noteId);
    }

    /**
     * 从注册表访问器按 ID 查询定义。
     *
     * @param access 注册表访问器
     * @param id     定义 ID
     * @return 定义；注册表缺失或条目不存在返回 null
     */
    @Nullable
    public static NoteDefinition get(RegistryAccess access, ResourceLocation id) {
        if (access == null || id == null) {
            return null;
        }
        return access.registry(NOTE_REGISTRY).map(registry -> registry.get(id)).orElse(null);
    }

    /**
     * 从世界按 ID 查询定义。
     *
     * @param level 世界
     * @param id    定义 ID
     * @return 定义；不存在返回 null
     */
    @Nullable
    public static NoteDefinition get(Level level, ResourceLocation id) {
        return level == null ? null : get(level.registryAccess(), id);
    }

    /**
     * 按笔记序号查询定义。
     *
     * @param access 注册表访问器
     * @param noteId 0..14
     * @return 定义；不存在返回 null
     */
    @Nullable
    public static NoteDefinition get(RegistryAccess access, int noteId) {
        return get(access, definitionId(noteId));
    }
}
