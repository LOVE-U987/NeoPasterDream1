package com.pasterdream.pasterdreammod.api.client.effect.cutscene;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 客户端相机实体 —— 过场动画期间接管玩家镜头的虚拟实体
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code ClientCameraEntity} 设计思路
 * （独立实现，非复制）。过场期间由 {@link CutsceneCameraHandler} 把它设为
 * {@code Minecraft.cameraEntity}，使相机跟随过场路径。
 * <p>
 * <b>继承 {@link Entity} 而非 {@link net.minecraft.world.entity.LivingEntity}</b>：
 * 相机实体只需提供位置/旋转，不需要 LivingEntity 的属性系统（{@code AttributeMap}）、
 * 装备、药水效果等复杂逻辑。若继承 LivingEntity 而实体类型未注册属性，会在
 * {@code tick()} 访问属性时因 {@code AttributeMap.supplier == null} 抛 NPE
 * （此前过场启动失败的根因）。
 * <p>
 * 关键点（防物理/碰撞干扰）：
 * <ul>
 *   <li>{@code push} 系列空实现 —— 相机实体不可被推动/推挤；</li>
 *   <li>{@code tick} 调 super 保持实体数据同步（Entity 的 tick 为空实现，安全）；</li>
 *   <li>不参与世界逻辑（不 spawn，仅作为 {@code cameraEntity}）。</li>
 * </ul>
 * <p>
 * 本类为客户端专用，仅由 {@code api/client/**} 路径持有；服务端只注册其类型、
 * 从不 spawn。
 */
@OnlyIn(Dist.CLIENT)
public class ClientCameraEntity extends Entity {

    /**
     * 构造客户端相机实体
     *
     * @param type  实体类型
     * @param level 客户端世界
     */
    public ClientCameraEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
    }

    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
    }

    @Override
    public void push(Vec3 vec) {
    }

    @Override
    public void push(Entity entity) {
    }
}
