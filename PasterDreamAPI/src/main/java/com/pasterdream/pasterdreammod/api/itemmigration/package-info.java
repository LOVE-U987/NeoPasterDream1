/**
 * 物品移植 API (Item Migration API)
 * <p>
 * == 设计理念 ==
 * 本 API 专门用于将原 FixPasterDream 模组（MCreator 生成）中的物品
 * 系统化地移植到 NeoForge 1.21.1 新模组中。
 * 通过 Builder 模式 + Facade 模式，提供简洁、类型安全的物品注册体验，
 * 让"物品移植"变成"填写参数"而非"手写类"。
 * <p>
 * 原模组有 200+ 物品需要移植，手动写 DeferredRegister 重复代码量大、容易出错。
 * 本 API 将常见物品类型（材料、食物、工具、饰品）的注册流程标准化，
 * 并集成迁移状态管理，帮助开发者清晰追踪移植进度。
 * <p>
 * == 核心架构 ==
 * <pre>
 * ┌──────────────────────────────────────────────────────────────┐
 * │                     ItemMigrationAPI                          │
 * │                     (Facade 门面)                              │
 * │  ┌──────────┬───────────┬───────────┬────────────────────┐   │
 * │  │simpleItem│ foodItem  │ toolItem  │    curioItem       │   │
 * │  │ Builder  │ Builder   │ Builder   │    Builder         │   │
 * │  └────┬─────┴────┬──────┴─────┬─────┴──────┬────────────┘   │
 * │       │          │            │            │                 │
 * │       ▼          ▼            ▼            ▼                 │
 * │  SimpleItem  FoodItem     ToolItem     CurioItem             │
 * │  (材料/普通)  (食物类)    (工具/武器)   (饰品/Curio)         │
 * │       │          │            │            │                 │
 * │       └──────────┴────────────┴────────────┘                 │
 * │                        │                                     │
 * │                        ▼                                     │
 * │            PDItems.ITEMS (NeoForge Registry)                  │
 * └──────────────────────────────────────────────────────────────┘
 * </pre>
 * <p>
 * == 设计模式 ==
 * <ul>
 *   <li><b>Builder 模式</b>：通过流式 API（Fluent Interface）定义物品属性，
 *       将复杂对象的构建与表示分离</li>
 *   <li><b>Facade 模式</b>：{@link com.pasterdream.pasterdreammod.api.itemmigration.ItemMigrationAPI}
 *       提供统一入口，屏蔽内部实现细节</li>
 *   <li><b>规范驱动</b>：{@code ItemSpec}、{@code FoodSpec} 等记录类型
 *       定义标准属性规范，减少参数错误</li>
 * </ul>
 * <p>
 * == 快速开始 ==
 * <pre>{@code
 * // 注册一个稀有材料
 * import static com.pasterdream.pasterdreammod.api.itemmigration.ItemMigrationAPI.*;
 * 
 * simpleItem("titanium_ingot")
 *     .rarity(Rarity.UNCOMMON)
 *     .build();
 * 
 * // 注册一个食物
 * foodItem("apple_juice")
 *     .nutrition(4).saturationModifier(0.2f)
 *     .alwaysEdible()
 *     .build();
 * 
 * // 批量注册
 * batchSimpleItems(
 *     new ItemSpec("soul_dust", Rarity.COMMON),
 *     new ItemSpec("soul_essence", Rarity.UNCOMMON)
 * );
 * 
 * // 标记迁移进度
 * markMigrated(MigrationCategory.MATERIAL, "titanium_ingot", "soul_dust");
 * System.out.println(generateReport());
 * }</pre>
 * <p>
 * == 包结构 ==
 * <ul>
 *   <li><b>{@code model/}</b> — 数据模型记录（{@code ItemSpec}、{@code FoodSpec}、{@code MigrationCategory}）</li>
 *   <li><b>{@code builder/}</b> — Builder 实现（{@code SimpleItemBuilder}、{@code FoodItemBuilder}、{@code ToolItemBuilder}、{@code CurioItemBuilder}）</li>
 *   <li><b>{@code manager/}</b> — 迁移状态管理（{@code MigrationManager}）</li>
 *   <li><b>{@code gen/}</b> — 自动生成器（可选，用于批量生成注册代码）</li>
 * </ul>
 * <p>
 * == 工作流示例 ==
 * <ol>
 *   <li>在 {@code PDItems.java} 中通过 API 注册物品</li>
 *   <li>在 {@code PDCreativeTabs.java} 中添加到创造模式物品栏</li>
 *   <li>在语言文件中添加本地化文本</li>
 *   <li>通过 {@code markMigrated()} 标记完成迁移</li>
 *   <li>使用 {@code generateReport()} 查看整体进度</li>
 * </ol>
 * <p>
 * == 最佳实践 ==
 * <ul>
 *   <li>优先使用 Builder 方法注册简单物品，避免创建额外 Item 子类</li>
 *   <li>复杂物品（需交互、Tooltip、特殊渲染等）使用 {@code registerCustom()}</li>
 *   <li>使用 {@code markMigrated()} / {@code markPending()} 持续追踪进度</li>
 *   <li>每个移植批次完成后调用 {@code generateReport()} 记录日志</li>
 * </ul>
 *
 * @see com.pasterdream.pasterdreammod.api.itemmigration.ItemMigrationAPI
 * @see com.pasterdream.pasterdreammod.api.itemmigration.builder.SimpleItemBuilder
 * @see com.pasterdream.pasterdreammod.api.itemmigration.builder.FoodItemBuilder
 * @see com.pasterdream.pasterdreammod.api.itemmigration.builder.ToolItemBuilder
 * @see com.pasterdream.pasterdreammod.api.itemmigration.builder.CurioItemBuilder
 * @see com.pasterdream.pasterdreammod.api.itemmigration.manager.MigrationManager
 * @see com.pasterdream.pasterdreammod.api.itemmigration.model.ItemSpec
 * @see com.pasterdream.pasterdreammod.api.itemmigration.model.FoodSpec
 * @see com.pasterdream.pasterdreammod.api.itemmigration.model.MigrationCategory
 * @see com.pasterdream.pasterdreammod.registry.PDItems
 */
package com.pasterdream.pasterdreammod.api.itemmigration;
