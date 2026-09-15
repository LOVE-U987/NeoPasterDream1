# NeoPasterDream

[简体中文](README.md) | English

**An unofficial port of PasterDream to Minecraft 1.21.1 / NeoForge.**

Explore Dyedream, Lamp Shadow, and Wind Journey, collect knowledge from the dream worlds, and progress through workshops, spells, and accessories. The project aims to preserve the original mod's core content and gameplay while adapting, fixing, and improving it for the newer platform.

[Download](https://www.curseforge.com/minecraft/mc-mods/neopasterdream) · [Report an issue](https://github.com/LOVE-U987/NeoPasterDream1/issues) · [Documentation](docs/README.md) · [Contributing](CONTRIBUTING.md)

## Project status

The project is in development.. See the [development status](docs/设计/开发进度.md) and [design direction](docs/设计/设计方向.md) for implementation coverage, intentional differences, and open work.

The project **has not received formal authorization from the original author**. The author is aware of the public porting effort and has offered encouragement, which the project interprets as tacit acceptance. See [project identity and authorization status](PERMISSIONS.md#english).

## Content overview

| Area                          | Content                                                                                                |
| ----------------------------- | ------------------------------------------------------------------------------------------------------ |
| Dream exploration             | Dyedream, Lamp Shadow, Wind Journey, and Aaroncos Arena, with entrances, ruins, and progression events |
| Combat and abilities          | Staffs, spell projectiles, evasion, cloak abilities, and Curios accessories                            |
| Crafting and research         | Weapon workshops, the shadow blast furnace, dream brewing, research tables, and blueprints             |
| Adventure records             | Dream notes, cards, mementos, and advancements; the Paster guide is available with Patchouli           |
| Separate addons               | Spells, sanity, and dream fusion energy are provided by addon mods                                     |
| Integrations and presentation | JEI recipe viewing, KubeJS integration, GeckoLib animations, dimension music, and visual effects       |

This is an overview of content under development. Refer to the development documentation for verified coverage.

## Installation

1. Prepare **Minecraft 1.21.1, Java 21, and NeoForge 21.1.219 or a later compatible 1.21.1 version**.
2. Obtain the matching release from the [download page](https://www.curseforge.com/minecraft/mc-mods/neopasterdream), then place the main mod and required dependencies in your instance's `mods/` directory.
3. For spells, sanity, and dream fusion energy, also install the three addon Jars from the same release. PasterDreamAPI is bundled inside the main mod.
4. Add any optional integrations listed below, then launch the game.

### Third-party dependencies

The versions below are the current development dependencies. Choose files compatible with Minecraft 1.21.1 / NeoForge when installing.

| Dependency      | Type     | Current development version / purpose                                                      |
| --------------- | -------- | ------------------------------------------------------------------------------------------ |
| GeckoLib        | Required | 4.8.4, models and animations                                                               |
| Curios          | Required | 9.5.1+1.21.1, accessories                                                                  |
| Player Animator | Optional | 2.0.4+1.21.1, player animations                                                            |
| JEI             | Optional | 19.39.0.372, recipe viewing                                                                |
| Patchouli       | Optional | 1.21.1-93-NEOFORGE, the Paster guide                                                       |
| KubeJS / Rhino  | Optional | Scripting integration and its runtime dependency; see the build configuration for versions |

See [gradle.properties](gradle.properties) for exact build versions and [neoforge.mods.toml](PasterDream/src/main/templates/META-INF/neoforge.mods.toml) for the main mod's declared loading requirements. Third-party dependencies are not bundled in the release Jars.

## Development from source

### Environment

- JDK 21.
- The repository's Gradle Wrapper, currently 9.2.1. The [Wrapper configuration](gradle/wrapper/gradle-wrapper.properties) is authoritative.
- Git; IntelliJ IDEA can be used as the IDE. Set the project encoding to UTF-8.
- Python 3, available through the `python` command, for tools and release archiving.

### Common commands

Run from the project root in Windows PowerShell:

```powershell
# Compile every module
.\gradlew compileJava

# Generate data
.\gradlew runData

# Launch the development client
.\gradlew runClient

# Package the main mod and all three addons
.\gradlew packageMod
```

On macOS / Linux, use `./gradlew`. The `packageMod` task collects four release Jars in `build/dist/`, then automatically moves them into the root `打包产物/` directory. See the [environment setup guide](docs/入门/环境搭建.md) for the complete setup and run configurations.

### Test-driven development

Establish a failing test for the intended behavior, then implement, verify, and refactor. Before submission, complete a full compilation, the relevant VERIFY suites, and in-game testing; run DataGen for data changes. The default VERIFY `all` selection excludes some specialist suites, so choose suites explicitly for the affected behavior.

See the [testing guide](docs/开发指南/测试指南.md) for commands, coverage, and acceptance requirements.

## Modules and documentation

| Module                 | Responsibility                                                                                 |
| ---------------------- | ---------------------------------------------------------------------------------------------- |
| `PasterDream`          | Main mod: game content, client presentation, and integrations; its release Jar bundles the API |
| `PasterDreamAPI`       | Reusable APIs, registration facades, and builders                                              |
| `PasterDreamSpells`    | Spells addon                                                                                   |
| `PasterDreamSanity`    | Sanity addon                                                                                   |
| `PasterDreamMeltDream` | Dream fusion energy addon                                                                      |

The three addons are distributed separately and require the main mod and its bundled API at runtime.

- [Documentation index](docs/README.md): setup, architecture, development guides, references, and tutorials.
- [Contributing](CONTRIBUTING.md): task discussion, development workflow, and PR acceptance.
- [Architecture overview](docs/架构/架构总览.md): design principles and module relationships.
- [Changelog](Changelog.md): version changes.
- [Agent rules](AGENTS.md): project requirements for AI-assisted development.

The development documentation is primarily in Simplified Chinese.

## License and credits

- **NPD original content**: Copyright in original code, artwork, and other content belongs to the NPD project, with specific ownership and contributor-retained rights determined by actual ownership and contribution agreements.
- **Referenced content**: Pre-existing code, artwork, and other material used or adapted from the original mod or PDR remains copyrighted by its original authors or respective rights holders, with credits retained and applicable licenses and permission scopes respected.
- **Usage licenses**: NPD's original code continues under the existing [MIT License](LICENSE). Non-code and third-party material follows its respective license or explicit permission.

The project plans to send the full [copyright statement and authorization status](PERMISSIONS.md#english) and project introductions to the original author to explain the current statement and ownership of relevant assets. The project remains an unofficial port; ownership and permissions are documented according to the actual records.

Thanks to **异星之尘 (Aerolite_Dust)** and the original contributors for PasterDream's content and resources, and to the author for encouraging the porting effort. Thanks to all [project contributors](https://github.com/LOVE-U987/NeoPasterDream1/graphs/contributors), [MomoNyako](https://www.mcmod.cn/author/40210.html), and [PhantomDaze](https://www.mcmod.cn/author/41210.html).

## Contact and feedback

Use [GitHub Issues](https://github.com/LOVE-U987/NeoPasterDream1/issues) to report problems. Include the mod version, loader version, reproduction steps, and relevant logs.

Project QQ group: **710290194**.
