package com.pasterdream.pasterdreammod.api.effect.cutscene;

import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;

/**
 * 过场动画数据 —— 描述一次完整的相机过场
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code CutsceneData} 设计思路
 * （独立实现，非复制）。包含：
 * <ul>
 *   <li><b>cameraPositions</b>：相机路径关键帧；</li>
 *   <li><b>cutsceneTime</b>：总时长（tick）；</li>
 *   <li><b>moveCurveType</b>：路径曲线类型；</li>
 *   <li><b>timeEasing / lookEasing</b>：时间/朝向缓动；</li>
 *   <li><b>stopMode</b>：停止方式（自动/玩家/不可停）；</li>
 *   <li><b>nextCutscene</b>：链式下一段过场；</li>
 *   <li><b>screenEffects</b>：内嵌屏幕特效时间轴。</li>
 * </ul>
 * 使用 {@link #create()} + 链式 Builder 构建，经 {@link #toTag()}/{@link #fromTag(CompoundTag)}
 * 随网络包传输。
 */
public final class CutsceneData {

    private final List<CameraPos> cameraPositions;
    private int cutsceneTime;
    private CurveType moveCurveType;
    private EasingType timeEasing;
    private EasingType lookEasing;
    private StopMode stopMode;
    private CutsceneData nextCutscene;
    private final CutsceneScreenEffectData screenEffects;

    private CutsceneData() {
        this.cameraPositions = new ArrayList<>();
        this.cutsceneTime = 100;
        this.moveCurveType = CurveType.CATMULLROM;
        this.timeEasing = EasingType.SMOOTHSTEP;
        this.lookEasing = EasingType.LINEAR;
        this.stopMode = StopMode.AUTOMATIC;
        this.screenEffects = new CutsceneScreenEffectData();
    }

    /**
     * 创建过场动画数据构建器
     *
     * @return 空数据对象（可链式配置）
     */
    public static CutsceneData create() {
        return new CutsceneData();
    }

    /**
     * 设置总时长
     *
     * @param ticks 时长（tick）
     * @return 当前对象
     */
    public CutsceneData time(int ticks) {
        this.cutsceneTime = ticks;
        return this;
    }

    /**
     * 设置路径曲线类型
     *
     * @param type 曲线类型
     * @return 当前对象
     */
    public CutsceneData moveCurveType(CurveType type) {
        this.moveCurveType = type;
        return this;
    }

    /**
     * 设置时间缓动
     *
     * @param type 缓动类型
     * @return 当前对象
     */
    public CutsceneData timeEasing(EasingType type) {
        this.timeEasing = type;
        return this;
    }

    /**
     * 设置朝向缓动
     *
     * @param type 缓动类型
     * @return 当前对象
     */
    public CutsceneData lookEasing(EasingType type) {
        this.lookEasing = type;
        return this;
    }

    /**
     * 设置停止方式
     *
     * @param mode 停止方式
     * @return 当前对象
     */
    public CutsceneData stopMode(StopMode mode) {
        this.stopMode = mode;
        return this;
    }

    /**
     * 追加一个相机关键帧
     *
     * @param pos 相机点
     * @return 当前对象
     */
    public CutsceneData addCameraPos(CameraPos pos) {
        this.cameraPositions.add(pos);
        return this;
    }

    /**
     * 设置链式下一段过场（当前段播完后自动衔接）
     *
     * @param next 下一段过场
     * @return 当前对象
     */
    public CutsceneData nextCutscene(CutsceneData next) {
        this.nextCutscene = next;
        return this;
    }

    /**
     * 在指定 tick 内嵌一个屏幕特效
     *
     * @param tick     触发 tick
     * @param typeId   特效类型注册 id
     * @param dataNbt  特效数据序列化字节
     * @param inTime   渐入 tick 数
     * @param stayTime 持续 tick 数
     * @param outTime  渐出 tick 数
     * @return 当前对象
     */
    public CutsceneData addScreenEffect(int tick, String typeId, byte[] dataNbt,
                                        int inTime, int stayTime, int outTime) {
        this.screenEffects.put(tick, typeId, dataNbt, inTime, stayTime, outTime);
        return this;
    }

    /**
     * 获取相机关键帧列表
     *
     * @return 只读列表
     */
    public List<CameraPos> cameraPositions() {
        return List.copyOf(cameraPositions);
    }

    /**
     * 获取总时长
     *
     * @return tick 数
     */
    public int cutsceneTime() {
        return cutsceneTime;
    }

    /**
     * 获取曲线类型
     *
     * @return 曲线类型
     */
    public CurveType moveCurveType() {
        return moveCurveType;
    }

    /**
     * 获取时间缓动
     *
     * @return 缓动类型
     */
    public EasingType timeEasing() {
        return timeEasing;
    }

    /**
     * 获取朝向缓动
     *
     * @return 缓动类型
     */
    public EasingType lookEasing() {
        return lookEasing;
    }

    /**
     * 获取停止方式
     *
     * @return 停止方式
     */
    public StopMode stopMode() {
        return stopMode;
    }

    /**
     * 获取链式下一段过场
     *
     * @return 下一段或 {@code null}
     */
    public CutsceneData nextCutscene() {
        return nextCutscene;
    }

    /**
     * 获取内嵌屏幕特效时间轴
     *
     * @return 时间轴
     */
    public CutsceneScreenEffectData screenEffects() {
        return screenEffects;
    }

    /**
     * 序列化到 NBT
     *
     * @return CompoundTag
     */
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("cutsceneTime", cutsceneTime);
        tag.putString("moveCurveType", moveCurveType.name());
        tag.putString("timeEasing", timeEasing.name());
        tag.putString("lookEasing", lookEasing.name());
        tag.putString("stopMode", stopMode.name());
        tag.putInt("cameraCount", cameraPositions.size());
        for (int i = 0; i < cameraPositions.size(); i++) {
            CameraPos pos = cameraPositions.get(i);
            CompoundTag posTag = new CompoundTag();
            posTag.putDouble("x", pos.pos().x);
            posTag.putDouble("y", pos.pos().y);
            posTag.putDouble("z", pos.pos().z);
            posTag.putFloat("yaw", pos.yaw());
            posTag.putFloat("pitch", pos.pitch());
            posTag.putFloat("roll", pos.roll());
            tag.put("camera" + i, posTag);
        }
        tag.putBoolean("hasNext", nextCutscene != null);
        if (nextCutscene != null) {
            tag.put("next", nextCutscene.toTag());
        }
        tag.put("screenEffects", screenEffects.toTag());
        return tag;
    }

    /**
     * 从 NBT 反序列化
     *
     * @param tag CompoundTag
     * @return 过场动画数据
     */
    public static CutsceneData fromTag(CompoundTag tag) {
        CutsceneData data = new CutsceneData();
        data.cutsceneTime = tag.getInt("cutsceneTime");
        data.moveCurveType = CurveType.valueOf(tag.getString("moveCurveType"));
        data.timeEasing = EasingType.valueOf(tag.getString("timeEasing"));
        data.lookEasing = EasingType.valueOf(tag.getString("lookEasing"));
        data.stopMode = StopMode.valueOf(tag.getString("stopMode"));
        int cameraCount = tag.getInt("cameraCount");
        for (int i = 0; i < cameraCount; i++) {
            CompoundTag posTag = tag.getCompound("camera" + i);
            data.cameraPositions.add(new CameraPos(
                    new net.minecraft.world.phys.Vec3(
                            posTag.getDouble("x"), posTag.getDouble("y"), posTag.getDouble("z")),
                    posTag.getFloat("yaw"), posTag.getFloat("pitch"), posTag.getFloat("roll")));
        }
        if (tag.getBoolean("hasNext")) {
            data.nextCutscene = fromTag(tag.getCompound("next"));
        }
        CutsceneScreenEffectData loaded = CutsceneScreenEffectData.fromTag(tag.getCompound("screenEffects"));
        // 合并加载的时间轴到当前对象（通过复制 map 引用）
        data.screenEffects.replaceWith(loaded);
        return data;
    }

    /**
     * 过场停止方式
     */
    public enum StopMode {
        /** 播完自动停止 */
        AUTOMATIC,
        /** 玩家可按键停止 */
        PLAYER,
        /** 仅代码/命令可停 */
        UNSTOPPABLE
    }
}
