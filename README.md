# PasterDream — API 架构修复进度

> 最后更新：2026-06-18（内容基于 [`API_REVIEW_REPORT.md`](API_REVIEW_REPORT.md) 全面重构审查结果合并整理）

## 目前已知问题

|   优先级  | 问题                                                              | 建议                                                                           |      修复状态       |
| :----: | --------------------------------------------------------------- | ---------------------------------------------------------------------------- |:---------------:|
| **P0** | 粒子 Provider 缺失                                                  | 为 SHADOW\_STONE、SPORE、FOX\_FIRE\_0/1 新建粒子类并在 `ClientSetup` 注册 Provider       |       已修复       |
| **P0** | `DimensionApiDemo` 引用客户端类                                       | 将 `dimension/example/` 整体迁移到 `src/test/java` 或独立工具模块，避免 dedicated server 加载  |       已修复       |
| **P0** | CurioAPI 引用客户端类                                                 | 用 `Supplier<ICurioRenderer>` 延迟加载，或拆分 client/common                          |       已修复       |
| **P0** | 双重注册残留                                                          | 移除旧 `MOB_EFFECTS` 和 `STRUCTURE_TYPES` 注册器                                    |       已修复       |
| **P1** | `CurioBuilder` 使用已弃用 `ICurioItem` API                           | 迁移到 `getAttributeModifiers(SlotContext, ResourceLocation, ItemStack)`，消除编译警告 |       已修复       |
| **P1** | `CurioAPI.registerClientRenderers()` 无 side 校验                  | 方法入口增加 `FMLEnvironment.dist == Dist.CLIENT` 保护                               |       已修复       |
| **P1** | `BatchBlockBuilder` / `VariantSetBuilder` 未写入 `BLOCK_SUPPLIERS` | 注册完成后调用 `BlockAPI.putBlock()`，确保 `BlockAPI.getBlock()` 可查询                   |       修复中       |
| **P1** | `BlockLootAPI` INFO 级别日志泛滥                                      | 注册流程日志统一降为 `debug`，仅异常/摘要使用 `info`                                           | 计划中 MomoNyako负责 |
| **P1** | 缺少统一注册入口                                                        | 新增 `PasterDreamAPI.registerAll(modEventBus)`                                 |       已修复       |
| **P1** | 日志过多                                                            | 全部降为 `debug`，仅保留启动 banner 为 `info`                                           |       计划中       |
| **P1** | 粒子新旧混用                                                          | 迁移剩余旧式粒子到 ParticleAPI，统一 15 个粒子注册方式                                    |       已修复       |
| **P2** | `ApiSoundRegistry` 硬编码音乐文件                                      | 启动期校验 `.ogg` 文件存在性，缺失时跳过注册并输出 WARN                                       |       已修复       |
| **P2** | `ApiCodeGenConfig.setDefaultBasePath(null)` 无校验                 | 添加 `Objects.requireNonNull` 前置校验                                             |       已修复       |
| **P2** | `CompatLayer` 仍保留在源码                                            | 删除已标记 `@Deprecated(forRemoval=true)` 的兼容层                                    |       未修复       |
| **P2** | 静态缓存无清理                                                         | 新增 `resetForTesting()` 方法                                                    |       未进行       |
| **P2** | 查询返回 null                                                       | 改为 `Optional` 或加 `@Nullable`                                                 |       未进行       |
| **P2** | ItemMigrationAPI 命名                                             | 重命名为 `ItemAPI`                                                               |       未进行       |
| **P3** | `example` / `gen` 包打包                                           | 将示例代码与开发工具类移到 test sourceSet 或独立工具模块                                         |       未进行       |
| **P3** | API 覆盖不完整                                                       | 按需为 BlockEntity/Menu/Fluid 等添加 API                                           |       未进行       |

<br />

<br />

