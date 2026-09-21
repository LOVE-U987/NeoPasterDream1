/**
 * 注册名兼容 API（ID Compatibility API）
 * <p>
 * == 设计目标 ==
 * 统一处理模组迭代中「旧注册名 → 新注册名」的兼容问题，避免旧存档/第三方数据包
 * 因改名而丢失方块、物品、实体等内容。
 * <p>
 * 底层直接复用 NeoForge 1.21.1 原生机制：
 * {@code IRegistryExtension#addAlias} 与 {@code resolve}。注册表查找（
 * {@code get}/{@code getHolder}/{@code getId}）在 NeoForge 补丁中已统一走
 * {@code resolve}，因此别名对真实反序列化路径生效。
 * <p>
 * == 模块边界 ==
 * 本包只提供通用框架（零具体 ID、零事件处理器、零命令体），
 * 具体旧→新映射表由调用方（主模块）在 {@code compat} 包内维护。
 * <p>
 * == 核心类型 ==
 * <ul>
 *   <li>{@link com.pasterdream.pasterdreammod.api.compat.IdAlias} —— 单条别名（from/to）</li>
 *   <li>{@link com.pasterdream.pasterdreammod.api.compat.IdAliasTable} —— 按注册表分类的别名表</li>
 *   <li>{@link com.pasterdream.pasterdreammod.api.compat.IdAliasRegistry} —— 登记与静态应用</li>
 *   <li>{@link com.pasterdream.pasterdreammod.api.compat.IdCompatAPI} —— 对外门面与自检</li>
 * </ul>
 * <p>
 * == 用法 ==
 * <pre>
 * // 主模块构造器内（RegisterEvent 之前）：
 * IdCompatAPI.registerAll(PDIdAliases.build());
 * IdCompatAPI.applyStatic(BlockAPI.REGISTRY, ItemAPI.REGISTRY, PDItems.ITEMS, EntityAPI.REGISTRY);
 *
 * // 注册完成后（FMLCommonSetupEvent）：
 * IdCompatAPI.selfCheck();
 * </pre>
 */
package com.pasterdream.pasterdreammod.api.compat;
