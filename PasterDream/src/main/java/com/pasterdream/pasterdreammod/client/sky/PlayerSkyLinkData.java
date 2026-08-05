package com.pasterdream.pasterdreammod.client.sky;

import com.pasterdream.pasterdreammod.client.sky.math.SkyPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 玩家连线星体数据 —— 存储玩家用「星空枕」(memento_item_08) 在天空上
 * 创建的连线星体节点
 * <p>
 * 每个玩家最多 {@link #MAX_STARS} 个节点，按创建顺序依次连接；
 * 再次右键已存在的节点可取消该节点。数据仅存客户端（视觉功能），
 * 不跨存档持久化。上限由配置
 * {@code PasterDream-Common.toml → Sky.skylink max stars} 同步（默认 8）。
 */
public final class PlayerSkyLinkData {

    /** 每个玩家最多创建的星体节点数（默认 8，由配置同步到本字段） */
    public static int MAX_STARS = 8;

    /** 玩家 UUID → 星体节点列表（顺序即连接顺序） */
    private static final Map<UUID, List<SkyPoint>> PLAYER_STARS = new HashMap<>();

    private PlayerSkyLinkData() {
    }

    /**
     * 获取玩家的星体节点列表（不存在则创建）
     *
     * @param playerUuid 玩家 UUID
     * @return 节点列表（只读视图）
     */
    public static List<SkyPoint> getStars(UUID playerUuid) {
        return PLAYER_STARS.computeIfAbsent(playerUuid, k -> new ArrayList<>());
    }

    /**
     * 添加一个星体节点（超过上限则忽略）
     *
     * @param playerUuid 玩家 UUID
     * @param point      天空球面位置
     * @return 是否成功添加
     */
    public static boolean addStar(UUID playerUuid, SkyPoint point) {
        List<SkyPoint> stars = getStars(playerUuid);
        if (stars.size() >= MAX_STARS) {
            return false;
        }
        stars.add(point);
        return true;
    }

    /**
     * 移除与指定位置接近的星体节点（再次右键取消）
     *
     * @param playerUuid 玩家 UUID
     * @param point      查询位置（天空球面）
     * @param threshold  距离阈值
     * @return 是否移除了节点
     */
    public static boolean removeStarNear(UUID playerUuid, SkyPoint point, float threshold) {
        List<SkyPoint> stars = getStars(playerUuid);
        for (int i = 0; i < stars.size(); i++) {
            SkyPoint s = stars.get(i);
            float dx = s.x() - point.x();
            float dy = s.y() - point.y();
            float dz = s.z() - point.z();
            if (dx * dx + dy * dy + dz * dz < threshold * threshold) {
                stars.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * 清空玩家的星体节点
     *
     * @param playerUuid 玩家 UUID
     */
    public static void clear(UUID playerUuid) {
        PLAYER_STARS.remove(playerUuid);
    }
}
