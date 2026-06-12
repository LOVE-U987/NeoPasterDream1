/**
 * PasterDream API 兼容层 —— 过渡期向后兼容接口
 * <p>
 * 此包中的类和方法仅在 API 拆分过渡期内提供向后兼容，
 * <b>将在下个主版本中移除</b>。
 * <p>
 * 迁移指引：
 * <ul>
 *   <li>实体注册 → {@link com.pasterdream.pasterdreammod.api.entity.EntityAPI}</li>
 *   <li>粒子注册 → {@link com.pasterdream.pasterdreammod.api.particle.ParticleAPI}</li>
 *   <li>效果注册 → {@link com.pasterdream.pasterdreammod.api.effect.MobEffectAPI}</li>
 *   <li>方块注册 → {@link com.pasterdream.pasterdreammod.api.block.BlockAPI}</li>
 *   <li>维度注册 → {@link com.pasterdream.pasterdreammod.api.dimension.DimensionAPI}</li>
 *   <li>遗迹注册 → {@link com.pasterdream.pasterdreammod.api.ruin.RuinAPI}</li>
 *   <li>物品注册 → {@link com.pasterdream.pasterdreammod.api.itemmigration.ItemMigrationAPI}</li>
 * </ul>
 *
 * @deprecated 此包为过渡期兼容层，将在下个主版本移除。请迁移至对应的 Facade API 类。
 */
@Deprecated(forRemoval = true, since = "0.0.3.2")
package com.pasterdream.pasterdreammod.api.compat;
