package com.pasterdream.pasterdreammod.compat;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;

/**
 * 存档兼容数据（按世界持久化）。
 * <p>
 * 记录各模组的 ID schema 版本、写入时的模组版本与最近一次升级时间。
 * 存放于主世界的 {@code DataStorage}，即整份存档一份。
 */
public class PDSaveCompatData extends SavedData {

    private static final String TAG_SCHEMA_VERSIONS = "SchemaVersions";
    private static final String TAG_MOD_VERSIONS = "ModVersions";
    private static final String TAG_LAST_UPGRADED_AT = "LastUpgradedAt";

    private static final SavedData.Factory<PDSaveCompatData> FACTORY =
            new SavedData.Factory<>(PDSaveCompatData::new, PDSaveCompatData::load, null);

    /** 模组 ID → schema 版本 */
    private final Map<String, Integer> schemaVersions = new HashMap<>();

    /** 模组 ID → 模组版本字符串 */
    private final Map<String, String> modVersions = new HashMap<>();

    /** 最近一次升级时间戳 */
    private String lastUpgradedAt = "";

    /**
     * 空构造器（新建数据）。
     */
    public PDSaveCompatData() {
    }

    /**
     * 反序列化。
     *
     * @param tag        NBT
     * @param registries 注册表查询
     * @return 数据实例
     */
    public static PDSaveCompatData load(CompoundTag tag, HolderLookup.Provider registries) {
        PDSaveCompatData data = new PDSaveCompatData();
        CompoundTag schema = tag.getCompound(TAG_SCHEMA_VERSIONS);
        for (String key : schema.getAllKeys()) {
            data.schemaVersions.put(key, schema.getInt(key));
        }
        CompoundTag mods = tag.getCompound(TAG_MOD_VERSIONS);
        for (String key : mods.getAllKeys()) {
            data.modVersions.put(key, mods.getString(key));
        }
        data.lastUpgradedAt = tag.getString(TAG_LAST_UPGRADED_AT);
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag schema = new CompoundTag();
        this.schemaVersions.forEach(schema::putInt);
        tag.put(TAG_SCHEMA_VERSIONS, schema);

        CompoundTag mods = new CompoundTag();
        this.modVersions.forEach(mods::putString);
        tag.put(TAG_MOD_VERSIONS, mods);

        tag.putString(TAG_LAST_UPGRADED_AT, this.lastUpgradedAt);
        return tag;
    }

    /**
     * 读取指定模组的 schema 版本。
     *
     * @param modId 模组 ID
     * @return 版本；未记录返回 0
     */
    public int getVersion(String modId) {
        return this.schemaVersions.getOrDefault(modId, 0);
    }

    /**
     * 读取指定模组最近写入的模组版本。
     *
     * @param modId 模组 ID
     * @return 模组版本字符串；未记录返回空串
     */
    public String getModVersion(String modId) {
        return this.modVersions.getOrDefault(modId, "");
    }

    /**
     * @return 最近一次升级时间戳
     */
    public String getLastUpgradedAt() {
        return this.lastUpgradedAt;
    }

    /**
     * 写入指定模组的 schema 版本与模组版本。
     *
     * @param modId      模组 ID
     * @param version    schema 版本
     * @param modVersion 模组版本字符串
     */
    public void setVersion(String modId, int version, String modVersion) {
        this.schemaVersions.put(modId, version);
        this.modVersions.put(modId, modVersion == null ? "" : modVersion);
        setDirty();
    }

    /**
     * 记录最近一次升级时间。
     *
     * @param stamp 时间戳
     */
    public void markUpgradedAt(String stamp) {
        this.lastUpgradedAt = stamp == null ? "" : stamp;
        setDirty();
    }

    /**
     * 获取（或创建）主世界的兼容数据。
     *
     * @param overworld 主世界
     * @return 兼容数据
     */
    public static PDSaveCompatData get(ServerLevel overworld) {
        return overworld.getDataStorage().computeIfAbsent(FACTORY, PDSaveSchema.SAVE_COMPAT_DATA_ID);
    }
}
