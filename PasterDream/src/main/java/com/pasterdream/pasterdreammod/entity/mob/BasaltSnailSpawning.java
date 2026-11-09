package com.pasterdream.pasterdreammod.entity.mob;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;

/**
 * 玄武岩蜗牛的专用生成类别与配额，具体参数由枚举扩展元数据声明。
 * 保留原版持久化过滤，并逐只检查，避免整组生成越过类别上限。
 */
public final class BasaltSnailSpawning {
    /** 加载器扩展的独立类别，不与炽足兽共用 CREATURE 计数。 */
    public static final MobCategory CATEGORY = MobCategory.valueOf("PASTERDREAM_BASALT_SNAIL");

    private BasaltSnailSpawning() {
    }

    /**
     * 当前维度已加载、参与自然配额的蜗牛是否还有空位。
     * 不删除存量个体，不限制刷怪蛋等非自然生成途径。
     *
     * @param level 生成所在世界
     * @return 尚未达到类别上限时为 true
     */
    public static boolean hasCapacity(ServerLevel level) {
        int count = 0;
        int cap = CATEGORY.getMaxInstancesPerChunk();
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof BasaltSnailEntity snail
                    && !snail.isPersistenceRequired() && !snail.requiresCustomPersistence()) {
                if (++count >= cap) {
                    return false;
                }
            }
        }
        return true;
    }
}
