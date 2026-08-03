# PasterDream 装备、武器、配方、套装效果与方块移植 - 实现计划

## [ ] Task 1: 创建盔甲材料定义类 (PDArmorMaterials.java)
- **Priority**: high
- **Depends On**: None
- **Description**: 
  - 创建 PDArmorMaterials 类，定义 Copper、Titanium、Sculk、Dyedream、Qym 五种盔甲材料属性
  - 每个材料包含耐久、防御、韧性、击退抗性、附魔值、修复材料、声音等属性
- **Acceptance Criteria Addressed**: AC-1
- **Test Requirements**:
  - `programmatic` TR-1.1: 编译通过，无语法错误
  - `human-judgment` TR-1.2: 盔甲材料属性与原模组一致
- **Notes**: 参考原模组 DyedreamArmorItem 的属性定义

## [ ] Task 2: 注册盔甲物品 (PDItems.java 添加盔甲注册)
- **Priority**: high
- **Depends On**: Task 1
- **Description**: 
  - 在 PDItems.java 中注册 5 套盔甲（头盔、胸甲、护腿、靴子）
  - 使用 DeferredRegister 模式和 ArmorItem 类
- **Acceptance Criteria Addressed**: AC-1
- **Test Requirements**:
  - `programmatic` TR-2.1: 编译通过，20个盔甲物品成功注册
  - `human-judgment` TR-2.2: 盔甲名称符合项目命名规范
- **Notes**: 需要为每个盔甲创建自定义类以支持套装效果

## [ ] Task 3: 实现盔甲自定义类和套装效果
- **Priority**: high
- **Depends On**: Task 2
- **Description**: 
  - 创建 DyedreamArmorItem、SculkArmorItem 等自定义盔甲类
  - 实现 inventoryTick 检测全套穿戴并应用套装效果
  - 添加盔甲纹理路径
- **Acceptance Criteria Addressed**: AC-2
- **Test Requirements**:
  - `programmatic` TR-3.1: 编译通过
  - `human-judgment` TR-3.2: 穿戴全套盔甲时效果生效
- **Notes**: 参考原模组的 ArmorBuffPr0Procedure 和 DyedreamArmorPr0Procedure

## [ ] Task 4: 补全缺失的武器和工具
- **Priority**: medium
- **Depends On**: None
- **Description**: 
  - 注册 Meltdream 工具系列（axe, shovel, hoe）
  - 注册 Shadow Erosion 工具系列（axe, hoe, shovel）
  - 注册 Moltengold 工具系列（axe, shovel, hoe）
  - 注册法杖系列
- **Acceptance Criteria Addressed**: AC-3
- **Test Requirements**:
  - `programmatic` TR-4.1: 编译通过，所有工具成功注册
- **Notes**: 使用 ItemAPI.toolItem() 注册

## [ ] Task 5: 生成配方 JSON
- **Priority**: medium
- **Depends On**: Task 2, Task 4
- **Description**: 
  - 使用 ItemMigrationAPI.recipeGen() 生成盔甲合成配方
  - 生成工具合成配方
  - 生成熔炉/高炉配方
- **Acceptance Criteria Addressed**: AC-4
- **Test Requirements**:
  - `programmatic` TR-5.1: 配方文件生成到正确目录 (data/pasterdream/recipe/)
  - `human-judgment` TR-5.2: 游戏中配方可正常合成
- **Notes**: 使用 1.21 新格式（"id" 而非 "item"）

## [ ] Task 6: 移植染梦维度剩余方块
- **Priority**: medium
- **Depends On**: None
- **Description**: 
  - 对比原模组和当前项目的方块列表，识别缺失方块
  - 在 PDBlocks.java 中注册缺失方块
  - 在 PDItems.java 中注册对应的 BlockItem
- **Acceptance Criteria Addressed**: AC-5
- **Test Requirements**:
  - `programmatic` TR-6.1: 编译通过，方块成功注册
- **Notes**: 使用 BlockAPI.registerSimpleBlocks() 批量注册

## [ ] Task 7: 生成方块战利品表和挖掘标签
- **Priority**: medium
- **Depends On**: Task 6
- **Description**: 
  - 使用 ItemMigrationAPI.lootTableGen() 生成方块战利品表
  - 使用 ItemMigrationAPI.blockDataGen() 生成挖掘标签
- **Acceptance Criteria Addressed**: AC-5
- **Test Requirements**:
  - `programmatic` TR-7.1: 战利品表文件生成到 data/pasterdream/loot_table/blocks/
  - `programmatic` TR-7.2: 挖掘标签文件生成到 data/minecraft/tags/block/
- **Notes**: 使用 1.21 新目录名（loot_table/ 而非 loot_tables/）

## [ ] Task 8: 添加遗迹战利品列表
- **Priority**: low
- **Depends On**: None
- **Description**: 
  - 为各遗迹结构创建战利品表配置
  - 战利品包含材料、装备、稀有物品
- **Acceptance Criteria Addressed**: AC-6
- **Test Requirements**:
  - `human-judgment` TR-8.1: 游戏中遗迹宝箱包含配置的战利品
- **Notes**: 使用 PasterDreamRuinAPI 或直接创建 JSON 文件

## [ ] Task 9: 更新语言文件
- **Priority**: high
- **Depends On**: Task 2, Task 4, Task 6
- **Description**: 
  - 在 zh_cn.json 中添加所有新注册物品和方块的中文翻译
- **Acceptance Criteria Addressed**: NFR-4
- **Test Requirements**:
  - `human-judgment` TR-9.1: 游戏中所有物品显示中文名称
- **Notes**: 运行 check_lang.py 脚本检查完整性

## [ ] Task 10: 编译验证
- **Priority**: high
- **Depends On**: 所有任务
- **Description**: 
  - 运行 `.\gradlew compileJava` 验证编译
  - 修复编译错误
- **Acceptance Criteria Addressed**: NFR-3
- **Test Requirements**:
  - `programmatic` TR-10.1: 编译成功，无错误
