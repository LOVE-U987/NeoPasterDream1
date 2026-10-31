# final 红框提交提取计划

会话：01a1916f-417c-73e2-92ce-ca8e7041547a；版本 v2；审核轮次 2。
用户已授权提取两张截图红框提交，并批准本次计划/审核文件创建、修订及审核通过后清理。仅本地 final，不推送。

## 范围与步骤

1. 基线 final=ea9912dd，工作区干净；保持 README 停更预告、0.10.1 版本、战利品调试器、粒子网络安全修复及已有掉落和 DataGen 修复。
2. 先提取 bd1f3e1b 地牢底高度修复、fc68b1ec 暮影长床修复、27b1f549 武器行为、4c4e7bdb 暗影沉默、7c3bad94 水晶箱 JEI、96acad09 工坊及炼药锅强化、31e2e225 粉尘配方、b42dd716 调试水晶 JEI。
3. 红框旧版本 7603ed7d/650d08d8/ff72d660/7ad9aea4 与上列新版逐文件核对后去重；3ef1855b 炼药锅/宝藏大提交拆分内容已在 7c3bad94 和 96acad09 及长床修复中覆盖，逐文件 diff 验证其独有有效内容不遗漏；不带 scratchpad 临时脚本。
4. 再提取 ecbdad51 玩偶 API 上收、0a74dddf 矿物生成、484a629a 裂隙概率减半、4c93f02f 删除旧 API、b99046d0 天空修复。保持主模/API 模块边界，仅做选中提交必要适配。
5. 0a74dddf 钛矿两项与 final 已有 JSON 内容相同，保留现有 DataGen；仅补熔融金矿 modifier，避免重复生成。b99046d0 如依赖框外 Static Sky 类，只移植该提交能够独立使用的渲染修复，不引入整个已撤回批次；遇到无法独立适配的实质扩围先报告。
6. 用 cherry-pick -x 保留来源，冲突按目标功能与 final 既有修复合并，禁止整文件选择覆盖现有修复；重复/空提交核验后跳过。冲突解决及必要集成修复在授权范围内。不得修改 libs、不带入 ID 重命名、笔记分页或其他框外功能。不强推、不重置、不切换其他分支。

## 验证与审计

- JDK 21，全模块 compileJava，涉及数据运行 runData，核对生成差异仅相关文件，避免无关产物提交。
- 运行现有相关脚本（工具属性、资源闭包、天空、水晶箱奖励）及适用 VERIFY（核对 Suite 名称），启动客户端检查；环境阻碍或手测未覆盖项如实记录，不能将编译/启动或 SKIP 当成功验收。
- JSON 格式、重复配方、旧 API 引用闭包；核对原红框每个提交去向与遗漏；保留 final 特有内容，git diff --check；仅本次改动文本 UTF-8 无 BOM/LF（bat 按规则），不批量格式化无关文件。
- 二次审查对照本计划。将实际集成决策/验证限制落入 docs/参考/2026-10-31-final提交提取记录.md，完成审计后按用户授权删除本计划。
- 审核交接与证据仅删除本会话确切路径：.opencode/handoff/2026-10-31-final-redbox-plan-review.md 及同名 -evidence.md；PASS 核验后先将审核必要约束并入计划、重读核验再删除；REJECT 保留。

## v2 审核修订

- PR-001：ecbdad51 的具体默认玩偶 geo 与自动生成 blockstate/model/display 资源路径不放 API。扩展 DollImplementations 注入默认模型、抱物模型 ResourceLocation 及 BiConsumer<String, String> 资源生成回调；具体值和现有生成逻辑移至主模 DollResourceGenerator，由主模 ensureDollInstalled 安装。保留注册门面、工厂和配置在 API，注册行为与兜底不变。这是选中 API 上收提交的必要边界适配，不引入新功能。
- 天空新增 renderStatic 保留；提取共享绘制核心，动态路径保留原 rotation/setup/cleanup，静态路径不二次旋转、不调用 cleanup。
- 旧/新版重复项逐文件验证；3ef1855b 独有有效行为未覆盖时补齐后才视作已提取。
- 编译基线受默认文件权限阻止：Gradle 用户缓存锁无法写入，将通过工具审批申请正常构建权限；任何失败或游戏未验收均记录，不宣称完成验收，未满足审计条件保留计划。

- PR-001 补全：默认名称派生的模型/纹理/抱物资源与默认强度/音效亦留主模；DollImplementations 用 Function<String,DollConfig> 提供名称默认配置、Supplier<BlockBehaviour.Properties> 提供方块属性、Supplier<DollConfig> 提供皮肤辅助默认配置；API 不保留 eoul 名称或具体默认属性。保留 API 内通用自掉落格式与命名空间注册规则。

## 审核归档

# 审核交接

会话 01a1916f-417c-73e2-92ce-ca8e7041547a
计划 D:\Project\NeoPasterDream1\docs\计划\2026-10-31-final-redbox.md
版本 v1；轮次 1；状态 待审核
更新时间 2026-10-31T18:12:45.120142
授权：本次交接创建与逐轮更新；PASS 归档核验后由主 Agent 清理。

## 第 1 轮完整审核记录

会话：01a1916f-417c-73e2-92ce-ca8e7041547a
计划：D:/Project/NeoPasterDream1/docs/计划/2026-10-31-final-redbox.md
版本：v1；轮次：1；更新时间：2026-10-31（Asia/Shanghai）
结论：REJECT
授权：仅更新本会话审核交接。保留原始基础记录；不修改计划或非 Markdown 文件。

### 已核实事实与来源

- 直接读取 Markdown：计划 v1、本会话证据文件、AGENTS.local.md、全局个人规则与 CODE.md、plan-reviewer.md、pasterdream-dev 技能、模块边界/API边界、测试指南、资源规范。
- 代码仅依据主 Agent 证据，未直接访问非 Markdown 文件。初次取证时间 2026-10-31T18:12:45.122637；后附本轮补证同日采集，补充时间未单独标秒。源以 Git revision/path/SHA-256 定位。
- final 的 titanium_ore.json 1–6 行：染梦群系标签、titanium_ore 特性、underground_ores 阶段；SHA-256 34d2c5feeb2a00b12d2e5f7977834cd6521727b54875e102a6fc613e11314ff7。deepslate_titanium_ore.json 1–6 行：主世界标签、deepslate_titanium_ore 特性；SHA-256 2bcc45d541865e5ee7cb25699821b1e1a32312c2dd4743a2b0a6ec3ffa206d67。来源路径均为 PasterDream/src/main/resources/data/pasterdream/neoforge/biome_modifier/ 下相应文件。
- final: PasterDream/src/main/java/com/pasterdream/pasterdreammod/client/sky/SkyboxRenderer.java 的 290–335 行 render 自行旋转及清理状态；SHA-256 95a7ab6642cfaddc3f278c249c9be3a0da6f4ed0fa7e0e51301600d4a6865ff8。b99046d0 同路径 347–359 行 renderStatic 注释要求调用者已旋转、不做 cleanup，却调用 render；SHA-256 9ef9e2ce50c24a2085889856e9f4119057f12e8f2ba02d17a52fb63f41cdcfca。共享不旋转/不清理的绘制核心是必要适配，不应引入框外整个 StaticSkyRenderer 批次。
- ecbdad51: PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/doll/DollAPI.java SHA-256 a90d9af3aefd55ddd2822469ccc89b5ff1b8711060d8878c0253800d7e21b096，77–89 行通过注入命名空间建立 DeferredRegister，零主模实现类 import；198–207 行却仍内置 eoul_doll 模型资产。
- ecbdad51: 同目录 DollBuilder.java SHA-256 6abaef81f1e4de8dacc1d7f4c08e47e01480b75472f975baa81066b249f900e6，174–208 行使用注入工厂注册；362–396 行在 API 内写出具体 pasterdream:custom/doll_generic_particle / pasterdream:displaysettings/doll_generic.item 资产 JSON；458–474 行还有具体默认路径/强度/音效。DollImplementations.java SHA-256 a9a0f14ac048a27470d01c92153dec9810b74b9b7e567293e216f0e6439962ae，25–29 行现仅注入命名空间和三工厂，尚未分离这些内容默认值。
- 主 Agent 消息补证（非独立重验）：mob 两版本 7 个 Java blob 相同；武器 Java patch-id a7d666db039c1e64aba6224c76cf63d2b465fdff，粉尘 resources patch-id 20d70896ed5fdcc75a15a3f705981cf66012a40e；调试水晶新增三个类相同，PDJeiPlugin 和语言差异含背景，不能整文件覆盖。JDK21 存在于本机两个路径。去重结论最终仍以主 Agent 逐文件记录为验收门槛。

### 已通过项

- 红框范围、13 项主要提取提交与旧版重复提交均列明；原炼药锅/宝藏大提交安排独有有效改动检查，没有依据同名直接丢弃。
- 保留 final 独有功能和 DataGen 修复；禁止修改 libs、框外 ID 迁移/笔记分页、推送/强推/重置。冲突不整文件覆盖，来源用 -x 追踪。
- compileJava、runData、相关现有脚本、适用 VERIFY 和客户端验证均安排，不能用编译或 SKIP 代替行为验收；计划与审核生命周期路径明确。

### 问题表

| 编号 | 严重度 | 状态 | 对应位置 | 问题与修订建议 | 验收条件 |
|---|---|---|---|---|---|
| PR-001 | 高 | 未解决 | 范围步骤 4、5 | 原玩偶上收补丁把具体资产路径及内容默认值搬入 API，违反模块边界/API边界。计划必须具体写明最小适配：具体默认模型/纹理/抱物模型、默认方块属性、eoul皮肤辅助所用资源与 KubeJS 通用模型 JSON 留主模，通过实现注入配置/函数/回调传入 API；API 仅保留通用 Builder/注册/战利品兜底格式与桥接类型。不得顺带扩做其他系统。 | 新 API 零主模实现类反向依赖、零具体资产路径/玩法默认值；保留原便利入口行为；DeferredRegister 路径编译与相关注册验证通过，KubeJS 生成兜底不丢失。 |

### 执行验收约束（应并入后续计划）

1. 天空保留选中提交的静态入口；用共享纯绘制核心避免重复旋转及状态清理，不以缺少框外调用者为由静默删除该提交有效入口。动态路径继续正常 setup/cleanup。
2. 3ef1855b 仅在逐文件核对独有有效改动均已覆盖后跳过；语言/JEI 必须合并有效增量，不整文件替换 final。
3. DataGen/VERIFY/客户端受阻项必须在报告中区分未运行、FAIL、SKIP；未满足计划验收不可宣称完整验收或清理仍需保留的计划。
4. 框内来源提交自身存在的 API 上收边界违例需做必要适配；其余既有技术债不扩大重构。

### 未知项及后续

- 尚未验证实现后的行为、编译、DataGen、实际游戏；本轮为计划审核。
- 具体 KubeJS 注册时序、客户端运行结果及 3ef1855b 最终去重覆盖尚待执行取证，不能当作已通过。
- 下一轮仅审核 PR-001 修订、天空契约约束已写入情况和受影响证据哈希；其余事实可在主 Agent 复核哈希无变后复用。
- 首审结论历史：v1 / 第 1 轮 / REJECT / PR-001 未解决；补证充实了 API/天空事实，无需全项目重新扫描。
## 第 2 轮预建记录
会话 01a1916f-417c-73e2-92ce-ca8e7041547a
计划 D:\Project\NeoPasterDream1\docs\计划\2026-10-31-final-redbox.md
版本 v2；轮次 2；状态 待审核
时间 2026-10-31T18:15:24.850245
修订：PR-001 具体资产/属性/JSON生成改由主模注入；静态入口共享绘制核心明确。

## 第 2 轮完整审核记录

会话：01a1916f-417c-73e2-92ce-ca8e7041547a
计划：D:/Project/NeoPasterDream1/docs/计划/2026-10-31-final-redbox.md
版本：v2；轮次：2；更新时间：2026-10-31（Asia/Shanghai）
结论：PASS
授权：同会话交接追加更新；仅主 Agent 可在 PASS 必要信息并入计划并核验后清理本交接及独立证据。

### 增量复审与有效证据

- 已读取计划 v2、第 2 轮预建记录、证据末尾 2026-10-31T18:15:24.850647 来源哈希复核。版本/会话/路径与委派一致。
- 主 Agent 复核 final SkyboxRenderer 与 ecbdad51 DollAPI/DollBuilder/DollImplementations 源 SHA-256 与第 1 轮完全一致；规则未修改。第 1 轮本交接保留的摘录、路径、行号、SHA-256 和通过项继续有效；本轮没有访问任何非 Markdown 源文件。
- 本轮仅审核 PR-001 对应的 v2 具体注入方案和天空契约约束；未扩为全仓库审查。
- 当前读取的是计划，尚未实现代码，因此本结论不代表编译、DataGen、运行时或游戏行为验收通过。

### 已解决问题

| 编号 | 严重度 | 状态 | 修订与验收 |
|---|---|---|---|
| PR-001 | 高 | 已解决（计划层） | v2 将具体玩偶资源、KubeJS blockstate/model JSON、名称派生模型/纹理/抱物路径及默认强度/音效留主模；Function<String,DollConfig>、Supplier<BlockBehaviour.Properties>、Supplier<DollConfig>、资源生成回调通过 DollImplementations 注入。此方案消除已取证的 API 内容默认值与资产硬编码，不扩展新功能。实现后仍须核对 API 无主模反依赖、无具体资产/玩法默认值，保留注册与皮肤便利入口、兜底生成及命名空间行为。 |

### 已通过项与必要约束

1. 范围仍限用户红框及必要集成适配；保留 final 既有 README/版本/调试器/网络防护/DataGen 修复。不修改 libs，不带入框外 ID 迁移/笔记分页，不推送、不重置。
2. 新旧重复提交逐文件核对，3ef1855b 独有有效改动补齐后才跳过；语言/JEI 背景差异合并，不整文件覆盖。
3. 天空静态入口保留，共享不重复旋转、不 cleanup 的纯绘制核心；动态路径保留 setup/rotation/cleanup，不引入框外整套 Static Sky 功能。
4. API 只保留通用注册/Builder/配置/工厂桥接及通用自掉落格式；具体内容默认值和资源生成落主模。保持原 DeferredRegister 注册路径，不新增框外直接注册能力。
5. 按计划执行 JDK21 compileJava、runData、相关脚本、适用 VERIFY 与客户端检查；不能以编译/启动/SKIP 代替行为验收。任何环境阻碍须具体记录，未满足审计条件保留计划。
6. 本次文本 UTF-8 无 BOM/LF，仅处理本次文件；审核归档与清理按确切会话路径执行。

### 未知项与执行门槛

- 代码尚未提取；冲突解决、去重覆盖、API 注入正确性、KubeJS 注册时序、DataGen 差异、VERIFY 和游戏实际行为均待主 Agent 实现和审计取证。
- 默认构建缓存写锁受权限阻挡是已报告环境问题，不是功能测试失败基线；正常构建权限获准并运行后再按真实结果判断。
- 实现如发现需要扩大红框功能范围或改变以上契约，应修订计划并增量复审，不沿用本轮 PASS。

### 逐轮结论

- 第 1 轮：v1 / REJECT / PR-001 未解决（API 上收内容默认值）。
- 第 2 轮：v2 / PASS / PR-001 已在计划层解决；实现验收仍待完成。
- 当前无未决计划审核问题，不需要下一轮；主 Agent 应将本轮结论、约束与后续所需证据并入计划并重新读取核验，随后才按已获授权清理本会话审核临时文件。

本计划沿用用户“批准执行”的红框提取范围；v2仅收紧必要适配与验收约束，未扩大功能范围。

## 实现与审计状态

红框来源提取完成：13 个 cherry-pick 加必要集成适配；3ef1855b 独有提示已补回。编译、DataGen、工坊/KubeJS 41 项专项及玩偶注册通过；来源覆盖/边界/受保护文件/格式审计通过。详见 docs/参考/2026-10-31-final提交提取记录.md。
扩展 registry/core 首轮仍有未消除的失败，部分手测未覆盖，因此发布验收不通过；本计划按规则保留，不提前清理。审核交接/独立证据已在 v2 PASS 归档核验后按授权删除。
