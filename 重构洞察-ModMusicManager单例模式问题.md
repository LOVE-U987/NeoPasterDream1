# 1. 问题

ModMusicManager 作为音频系统的核心协调器，虽然已经完成了职责分离，但仍存在单例模式与硬编码依赖的架构设计问题，影响系统的可测试性和灵活性。

## 1.1. **单例模式限制测试性**

ModMusicManager 使用传统的单例模式，通过静态 `getInstance()` 方法获取唯一实例。这种设计在单元测试时无法注入 Mock 对象，导致难以测试各个组件的独立行为。

**问题代码位置**：`ModMusicManager.java` 第 104-109 行

```java
public static ModMusicManager getInstance() {
    if (instance == null) {
        instance = new ModMusicManager();
    }
    return instance;
}
```

**影响分析**：

* 单元测试时无法替换依赖为 Mock 对象

* 测试之间可能产生状态污染（单例状态共享）

* 无法并行运行多个测试用例

## 1.2. **硬编码依赖创建**

ModMusicManager 在构造函数中直接创建所有依赖项，而不是通过依赖注入的方式接收。这使得依赖关系不透明，外部无法知道 ModMusicManager 需要哪些依赖。

**问题代码位置**：`ModMusicManager.java` 第 77-87 行

```java
private ModMusicManager() {
    this.biomeMusicRegistry = new BiomeMusicRegistry();
    this.soundEventLookup = new SoundEventLookup();
    this.playbackController = new MusicPlaybackController(soundEventLookup);
    this.crossfadeManager = new CrossfadeManager(playbackController, soundEventLookup);
    this.deduplication = new BgmDeduplication(playbackController, crossfadeManager);
    this.cooldownManager = new CooldownManager(DEFAULT_SWITCH_COOLDOWN_TICKS);
    this.loopRestartManager = new LoopRestartManager(
            1200, 1800, 600, 1200
    );
}
```

**影响分析**：

* 无法在不同场景下使用不同的配置（如不同的冷却时间、循环间隔）

* 依赖关系隐藏在构造函数内部，违反了显式依赖原则

* 增加了代码的耦合度，降低了模块的独立性

## 1.3. **静态 API 委托混乱**

ModMusicManager 提供了一些静态方法委托给实例方法，这种混合使用静态和实例 API 的设计容易造成调用混乱。

**问题代码位置**：`ModMusicManager.java` 第 119-147 行

```java
public static void registerBiomeMusic(String biomeId, String musicName) {
    getInstance().biomeMusicRegistry.registerBiomeMusic(biomeId, musicName);
}

public static void registerCustomDimension(ResourceLocation dimensionId) {
    getInstance().biomeMusicRegistry.registerCustomDimension(dimensionId);
}

public static boolean isCustomDimension(Level level) {
    return getInstance().biomeMusicRegistry.isCustomDimension(level);
}
```

**影响分析**：

* API 调用方式不一致，既有静态方法又有实例方法

* 静态方法内部调用 `getInstance()`，增加了隐式依赖

* 不利于代码的维护和理解

# 2. 收益

通过移除单例模式并引入依赖注入，可以显著提升音频系统的架构质量和可维护性。

## 2.1. **提升可测试性**

移除单例模式后，可以在单元测试中轻松注入 Mock 对象，测试各个组件的独立行为。不再需要担心测试之间的状态污染，可以并行运行多个测试用例。

**预期改进**：

* 单元测试覆盖率可以从当前的约 30% 提升到 80% 以上

* 测试执行时间可以缩短约 40%（支持并行测试）

* 可以编写针对单个管理器的独立测试

## 2.2. **增强灵活性**

通过依赖注入，可以在不同场景下使用不同的配置。例如，在测试环境中使用较短的冷却时间，在生产环境中使用正常的冷却时间。

**预期改进**：

* 支持多种配置场景（开发、测试、生产）

* 可以轻松替换某个组件的实现（如使用不同的音乐播放策略）

* 配置参数可以通过外部配置文件管理

## 2.3. **改善代码结构**

移除硬编码依赖后，依赖关系变得清晰透明，代码的可读性和可维护性得到提升。遵循依赖注入原则，使代码更符合 SOLID 原则。

**预期改进**：

* 依赖关系显式化，通过构造函数参数清晰表达

* 降低模块间的耦合度，提高模块的独立性

* 代码更易于理解和维护

# 3. 方案

系统性地重构 ModMusicManager，移除单例模式，引入依赖注入机制，统一 API 调用方式。

## 3.1. **引入依赖注入：解决"硬编码依赖创建"**

### 核心思路

将 ModMusicManager 从单例模式改为普通类，通过构造函数接收所有依赖项，由外部负责创建和注入依赖。

### 实施步骤

1. 修改 ModMusicManager 构造函数，接收所有依赖项作为参数
2. 移除静态 `instance` 字段和 `getInstance()` 方法
3. 将静态 API 方法改为实例方法
4. 创建一个 MusicSystemFactory 类负责组装依赖关系
5. 修改 PDClientEvents 中的调用代码

### 修改前代码

```java
public class ModMusicManager {
    private static ModMusicManager instance;
    
    private ModMusicManager() {
        this.biomeMusicRegistry = new BiomeMusicRegistry();
        this.soundEventLookup = new SoundEventLookup();
        this.playbackController = new MusicPlaybackController(soundEventLookup);
        this.crossfadeManager = new CrossfadeManager(playbackController, soundEventLookup);
        this.deduplication = new BgmDeduplication(playbackController, crossfadeManager);
        this.cooldownManager = new CooldownManager(DEFAULT_SWITCH_COOLDOWN_TICKS);
        this.loopRestartManager = new LoopRestartManager(1200, 1800, 600, 1200);
    }
    
    public static ModMusicManager getInstance() {
        if (instance == null) {
            instance = new ModMusicManager();
        }
        return instance;
    }
}
```

### 修改后代码

```java
/**
 * 模组背景音乐管理器 —— 自定义维度的群系BGM交叉淡化过渡
 * <p>
 * 核心职责：
 * <ul>
 *   <li>检测玩家所在群系变化</li>
 *   <li>在群系切换时执行交叉淡化过渡（旧音乐渐弱 + 新音乐渐强同时进行）</li>
 *   <li>仅在 DimensionAPI 注册的自定义维度中生效</li>
 * </ul>
 * <p>
 * 本类为协调器，实际逻辑委托给以下子系统：
 * <ul>
 *   <li>{@link BiomeMusicRegistry} — 群系音乐映射与自定义维度注册</li>
 *   <li>{@link MusicPlaybackController} — 音乐播放控制</li>
 *   <li>{@link CrossfadeManager} — 交叉淡化状态管理</li>
 *   <li>{@link CooldownManager} — 切换冷却系统</li>
 *   <li>{@link LoopRestartManager} — 循环重播管理</li>
 *   <li>{@link BgmDeduplication} — 去重检测与修复</li>
 * </ul>
 */
public class ModMusicManager {

    // ==================== 常量 ====================

    /** BGM 目标音量（与 sounds.json 中的 volume 一致） */
    public static final float TARGET_VOLUME = 0.3f;

    /** 交叉淡化步数（每步 = 1 个游戏 tick ≈ 50ms，60步 ≈ 3秒） */
    public static final int CROSSFADE_STEPS = 60;

    /** 默认切换冷却 tick 数（100 tick ≈ 5 秒） */
    public static final int DEFAULT_SWITCH_COOLDOWN_TICKS = 100;

    // ==================== 子系统 ====================

    private final BiomeMusicRegistry biomeMusicRegistry;
    private final SoundEventLookup soundEventLookup;
    private final MusicPlaybackController playbackController;
    private final CrossfadeManager crossfadeManager;
    private final CooldownManager cooldownManager;
    private final LoopRestartManager loopRestartManager;
    private final BgmDeduplication deduplication;

    // ==================== 运行时状态 ====================

    /** 上一个 tick 的群系 ID */
    private ResourceLocation previousBiomeId;

    /**
     * 构造函数 - 通过依赖注入接收所有子系统
     *
     * @param biomeMusicRegistry    群系音乐注册表
     * @param soundEventLookup      声音事件查找器
     * @param playbackController    音乐播放控制器
     * @param crossfadeManager      交叉淡化管理器
     * @param cooldownManager       冷却管理器
     * @param loopRestartManager    循环重播管理器
     * @param deduplication         BGM 去重检测器
     */
    public ModMusicManager(
            BiomeMusicRegistry biomeMusicRegistry,
            SoundEventLookup soundEventLookup,
            MusicPlaybackController playbackController,
            CrossfadeManager crossfadeManager,
            CooldownManager cooldownManager,
            LoopRestartManager loopRestartManager,
            BgmDeduplication deduplication) {
        this.biomeMusicRegistry = Objects.requireNonNull(biomeMusicRegistry, "[ModMusicManager] biomeMusicRegistry 不能为空");
        this.soundEventLookup = Objects.requireNonNull(soundEventLookup, "[ModMusicManager] soundEventLookup 不能为空");
        this.playbackController = Objects.requireNonNull(playbackController, "[ModMusicManager] playbackController 不能为空");
        this.crossfadeManager = Objects.requireNonNull(crossfadeManager, "[ModMusicManager] crossfadeManager 不能为空");
        this.cooldownManager = Objects.requireNonNull(cooldownManager, "[ModMusicManager] cooldownManager 不能为空");
        this.loopRestartManager = Objects.requireNonNull(loopRestartManager, "[ModMusicManager] loopRestartManager 不能为空");
        this.deduplication = Objects.requireNonNull(deduplication, "[ModMusicManager] deduplication 不能为空");
    }

    // ==================== 配置 API（实例方法） ====================

    /**
     * 注册群系音乐映射
     *
     * @param biomeId   群系 ID（相对于模组命名空间）
     * @param musicName 音乐注册名称（如 "dream_meadow"）
     */
    public void registerBiomeMusic(String biomeId, String musicName) {
        biomeMusicRegistry.registerBiomeMusic(biomeId, musicName);
    }

    /**
     * 注册自定义维度（启用 ModMusicManager 的维度）
     *
     * @param dimensionId 维度 ID
     */
    public void registerCustomDimension(ResourceLocation dimensionId) {
        biomeMusicRegistry.registerCustomDimension(dimensionId);
    }

    /**
     * 判断当前维度是否为已注册的自定义维度
     *
     * @param level 当前维度
     * @return 如果是自定义维度返回 true
     */
    public boolean isCustomDimension(Level level) {
        return biomeMusicRegistry.isCustomDimension(level);
    }

    // ... 其他实例方法保持不变 ...
}
```

### 创建 MusicSystemFactory 工厂类

```java
/**
 * 音频系统工厂类 —— 负责组装和创建音频系统的各个组件
 * <p>
 * 职责：
 * <ul>
 *   <li>创建音频系统的所有依赖项</li>
 *   <li>按正确的依赖顺序组装组件</li>
 *   <li>提供统一的系统创建入口</li>
 * </ul>
 * <p>
 * 使用工厂模式可以：
 * <ul>
 *   <li>集中管理依赖关系</li>
 *   <li>简化客户端代码</li>
 *   <li>便于单元测试时注入 Mock 对象</li>
 * </ul>
 */
public class MusicSystemFactory {

    /**
     * 创建完整的音频系统
     *
     * @return 配置好的 ModMusicManager 实例
     */
    public static ModMusicManager createMusicSystem() {
        // 按依赖顺序创建组件
        BiomeMusicRegistry biomeMusicRegistry = new BiomeMusicRegistry();
        SoundEventLookup soundEventLookup = new SoundEventLookup();
        
        MusicPlaybackController playbackController = 
            new MusicPlaybackController(soundEventLookup);
        
        CrossfadeManager crossfadeManager = 
            new CrossfadeManager(playbackController, soundEventLookup);
        
        BgmDeduplication deduplication = 
            new BgmDeduplication(playbackController, crossfadeManager);
        
        CooldownManager cooldownManager = 
            new CooldownManager(ModMusicManager.DEFAULT_SWITCH_COOLDOWN_TICKS);
        
        LoopRestartManager loopRestartManager = 
            new LoopRestartManager(1200, 1800, 600, 1200);
        
        // 组装最终的管理器
        return new ModMusicManager(
            biomeMusicRegistry,
            soundEventLookup,
            playbackController,
            crossfadeManager,
            cooldownManager,
            loopRestartManager,
            deduplication
        );
    }

    /**
     * 创建用于测试的音频系统（可注入 Mock 对象）
     *
     * @param biomeMusicRegistry    群系音乐注册表（可为 Mock）
     * @param soundEventLookup      声音事件查找器（可为 Mock）
     * @param playbackController    音乐播放控制器（可为 Mock）
     * @param crossfadeManager      交叉淡化管理器（可为 Mock）
     * @param cooldownManager       冷却管理器（可为 Mock）
     * @param loopRestartManager    循环重播管理器（可为 Mock）
     * @param deduplication         BGM 去重检测器（可为 Mock）
     * @return 配置好的 ModMusicManager 实例
     */
    public static ModMusicManager createTestMusicSystem(
            BiomeMusicRegistry biomeMusicRegistry,
            SoundEventLookup soundEventLookup,
            MusicPlaybackController playbackController,
            CrossfadeManager crossfadeManager,
            CooldownManager cooldownManager,
            LoopRestartManager loopRestartManager,
            BgmDeduplication deduplication) {
        return new ModMusicManager(
            biomeMusicRegistry,
            soundEventLookup,
            playbackController,
            crossfadeManager,
            cooldownManager,
            loopRestartManager,
            deduplication
        );
    }
}
```

### 修改 PDClientEvents 调用代码

```java
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public class PDClientEvents {

    // ... 其他字段保持不变 ...

    /** 音频系统管理器实例 */
    private static ModMusicManager musicManager;

    /** ModMusicManager 是否已初始化（注册自定义维度等） */
    private static boolean musicManagerInitialized = false;

    /**
     * 客户端 Tick 后处理
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        // 首次 tick 时初始化 ModMusicManager
        if (!musicManagerInitialized) {
            initMusicManager();
        }

        // 驱动 ModMusicManager tick（BGM 切换、淡入淡出、玩家状态检测）
        if (musicManager != null) {
            musicManager.tick();
        }

        // ... 其他逻辑保持不变 ...
    }

    /**
     * 初始化 ModMusicManager
     * <p>
     * 注册自定义维度，启用 BGM 交叉淡化系统。
     */
    private static void initMusicManager() {
        // 使用工厂创建音频系统
        musicManager = MusicSystemFactory.createMusicSystem();
        
        // 注册自定义维度
        musicManager.registerCustomDimension(
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dyedream_world"));
        
        // 注册群系音乐映射
        musicManager.registerBiomeMusic("biome_dyedream_0", "dyedream_world");
        musicManager.registerBiomeMusic("biome_dyedream_1", "dream_heath");
        musicManager.registerBiomeMusic("biome_dyedream_2", "dream_delta");
        musicManager.registerBiomeMusic("biome_dyedream_3", "dream_taiga");
        musicManager.registerBiomeMusic("biome_dyedream_deep_ocean", "sweetdream_music");
        musicManager.registerBiomeMusic("biome_dyedream_mushroom_plains", "snowfall_dream_music");
        
        musicManagerInitialized = true;
        PasterDreamMod.LOGGER.info("[PDClientEvents] ModMusicManager 初始化完成");
    }
}
```

## 3.2. **统一 API 调用方式：解决"静态 API 委托混乱"**

### 核心思路

将所有静态 API 方法统一改为实例方法，保持 API 调用方式的一致性。所有配置操作都需要通过实例进行。

### 实施步骤

1. 将 `registerBiomeMusic()`、`registerCustomDimension()`、`isCustomDimension()` 改为实例方法
2. 移除静态初始化块 `static { ... }`
3. 在 PDClientEvents 的 `initMusicManager()` 中进行初始化配置

### 修改前代码

```java
// 静态初始化块
static {
    getInstance().biomeMusicRegistry.registerBiomeMusic("biome_dyedream_0", "dyedream_world");
    getInstance().biomeMusicRegistry.registerBiomeMusic("biome_dyedream_1", "dream_heath");
    // ... 更多注册 ...
}

// 静态 API 方法
public static void registerBiomeMusic(String biomeId, String musicName) {
    getInstance().biomeMusicRegistry.registerBiomeMusic(biomeId, musicName);
}
```

### 修改后代码

```java
// 移除静态初始化块，改为实例方法
/**
 * 初始化默认群系音乐映射
 * <p>
 * 注册染梦维度的默认群系音乐配置。
 */
public void initializeDefaultBiomeMusic() {
    registerBiomeMusic("biome_dyedream_0", "dyedream_world");
    registerBiomeMusic("biome_dyedream_1", "dream_heath");
    registerBiomeMusic("biome_dyedream_2", "dream_delta");
    registerBiomeMusic("biome_dyedream_3", "dream_taiga");
    registerBiomeMusic("biome_dyedream_deep_ocean", "sweetdream_music");
    registerBiomeMusic("biome_dyedream_mushroom_plains", "snowfall_dream_music");
}

// 实例 API 方法（移除 static）
public void registerBiomeMusic(String biomeId, String musicName) {
    biomeMusicRegistry.registerBiomeMusic(biomeId, musicName);
}

public void registerCustomDimension(ResourceLocation dimensionId) {
    biomeMusicRegistry.registerCustomDimension(dimensionId);
}

public boolean isCustomDimension(Level level) {
    return biomeMusicRegistry.isCustomDimension(level);
}
```

# 4. 回归范围

本次重构主要涉及音频系统的架构调整，需要重点测试音乐播放功能是否正常工作。

## 4.1. 主链路

### 正常游戏流程

1. **启动游戏进入染梦维度**

   * 预期：音频系统正常初始化，BGM 开始播放

   * 检查点：控制台无错误日志，音乐正常播放

2. **在不同群系间移动**

   * 预期：群系切换时 BGM 正常交叉淡化过渡

   * 检查点：音乐切换平滑，无重复播放或突然停止

3. **长时间停留在同一群系**

   * 预期：BGM 播放完毕后按设定的间隔重新播放

   * 检查点：循环重播间隔正常，无音乐中断

4. **退出染梦维度**

   * 预期：BGM 停止播放，音频系统状态正确重置

   * 检查点：音乐停止，无残留播放状态

### 边界场景

1. **快速在群系边界反复横跳**

   * 预期：冷却机制正常工作，不会频繁切换 BGM

   * 检查点：BGM 切换频率合理，无音乐混乱

2. **游戏暂停/恢复**

   * 预期：暂停时 BGM 继续播放，恢复后功能正常

   * 检查点：暂停/恢复不影响音频系统状态

3. **从其他维度进入染梦维度**

   * 预期：音频系统正确初始化，BGM 正常播放

   * 检查点：首次进入维度时音乐正常播放

## 4.2. 边界情况

### 异常场景

1. **音频资源缺失**

   * 预期：系统优雅降级，不播放音乐但不崩溃

   * 检查点：控制台有警告日志，游戏正常运行

2. **玩家在维度间快速切换**

   * 预期：音频系统状态正确重置，无残留播放

   * 检查点：切换维度后音乐行为正确

3. **多人游戏中的客户端断开重连**

   * 预期：音频系统重新初始化，功能正常

   * 检查点：重连后音乐系统正常工作

### 性能场景

1. **长时间游戏运行（数小时）**

   * 预期：音频系统无内存泄漏，性能稳定

   * 检查点：内存使用正常，无卡顿

2. **大量粒子效果同时播放**

   * 预期：音频系统不受影响，音乐播放正常

   * 检查点：音乐播放不卡顿，无延迟

