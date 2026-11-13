> [!CAUTION]
> **Experimental Fuji 26.4 Snapshot 8 port — not build- or runtime-validated.** This branch targets [Fain's Frontier Paper fork](https://github.com/FainNeito/Frontier-Paper-26.4/tree/dev/26.4). The retained Leaf/Sakura/Fuji 26.3 patches still need porting. See [FRONTIER-26.4-PORT.md](FRONTIER-26.4-PORT.md); the main README below describes the 26.3 development line.

<p align="center">
  <img src="assets/fuji-logo.png" width="200" alt="Fuji logo — Mount Fuji at golden hour with hanging wisteria">
</p>

<h1 align="center">Fuji&nbsp;&nbsp;藤</h1>

<p align="center"><em>the peak of cannoning</em></p>

<p align="center">
  <a href="https://github.com/BadgersMC/Fuji/tree/26.3"><img src="https://img.shields.io/badge/Minecraft-26.3-3E4C7E" alt="Minecraft 26.3"></a>
  <img src="https://img.shields.io/badge/Java-25-7C6BC4" alt="Java 25">
  <img src="https://img.shields.io/badge/status-experimental-F4BC88" alt="Experimental">
  <a href="LICENSE.md"><img src="https://img.shields.io/badge/license-upstream%20licenses-2B2540" alt="Upstream licenses"></a>
</p>

---

**Fuji** is a Minecraft server fork combining **[Leaf](https://github.com/Winds-Studio/Leaf)** performance work with **[Sakura](https://github.com/Samsuik/Sakura)**-derived cannoning mechanics. It is aimed at factions, raiding, technical Minecraft, and networks that need predictable TNT behavior without giving up Paper's plugin ecosystem.

> [!WARNING]
> **The Minecraft 26.3 branch is experimental.** Features and compatibility are still being validated. Back up your worlds and test plugins, cannon designs, and gameplay before considering a production migration.

## Why Fuji?

Most general-purpose performance forks are not designed around the unusual demands of faction cannoning. Fuji brings together:

- **Cannoning mechanics:** Sakura-derived explosion, TNT, falling-block, and configurable cannon behavior, maintained as part of Fuji.
- **Leaf's performance foundation:** Paper compatibility plus Moonrise, asynchronous optimizations, and other Leaf-integrated improvements.
- **Multi-world options:** Leaf's optional parallel-world ticking can help separate independent worlds' workloads; it requires careful compatibility testing.
- **PvP controls:** Fuji-specific, opt-in knockback and packet-flush settings intended for testing responsiveness without changing vanilla defaults.

The goal is **cannoning-focused mechanics on a high-performance Paper fork**, not an automatic guarantee of cannon parity or lag-free raids.

## Heritage

```text
Paper 26.3 → Leaf 26.3 ──────────────┐
                                     ├── Fuji 26.3
Sakura-derived / Fuji-maintained ────┘
             cannoning patches
```

Fuji is based on Leaf's 26.3 source and carries selected Sakura-derived and Fuji-maintained patches. The 26.3 port is not a claim that every Sakura patch from earlier Minecraft versions has been revalidated.

## Features

### 🎯 Cannoning

- Sakura-derived configurable cannon mechanics and explosions
- TNT and falling-block behavior relevant to wall-breaking, sand, and concrete cannon designs
- World-specific cannon settings through `sakura-world.yml`
- Specialized options for explosions, durable materials, and technical gameplay

### ⚡ Performance

- Leaf's Paper-based optimization stack, including the Moonrise chunk system
- Optional asynchronous facilities, such as entity tracking and pathfinding
- Configurable performance features; experimental options should be benchmarked before deployment

### 🌏 Worlds and gameplay

- Per-world configurations alongside Paper, Leaf, and other inherited configuration systems
- Optional parallel-world ticking for workloads that are safe to separate
- Fuji's opt-in melee knockback synchronization and immediate packet flush controls

> **Cannoning note:** A faster entity-tracking or client-position update rate does not change server-side TNT physics. Validate cannon timings and movement separately from packet smoothness.

## Building Fuji 26.3

**Requirements:** JDK 25, Git, and the included Gradle wrapper.

On Linux, macOS, or Git Bash:

```bash
git clone --branch 26.3 https://github.com/BadgersMC/Fuji.git
cd Fuji
./gradlew applyAllPatches
./gradlew createLeafclipJar
```

On Windows PowerShell, use `.\gradlew.bat` in place of `./gradlew`. Enabling Git/Windows long-path support may be necessary for Paperweight's deep source paths.

The generated server JAR is expected under `leaf-server/build/libs/`. Run it with **Java 25**, accept the Minecraft EULA, and start with a disposable test world. Build task names are inherited from Leaf and may still use Leaf naming.

### Contributing patches

Fuji uses the Paperweight patch workflow. Edit the applied sources, commit the change in the appropriate applied-source repository, then regenerate its patch channel with the matching Gradle rebuild task. See [CONTRIBUTING.md](CONTRIBUTING.md) and [AGENTS.md](AGENTS.md) before modifying the tracker or other hot paths.

## Configuration

Fuji includes configurations from its upstream projects. For cannon and PvP behavior, start with the generated **`sakura-world.yml`** for the relevant world.

The following **Fuji 26.3** knockback options are opt-in (shown as their configuration keys within the `players.knockback` section):

| Setting | Default | Purpose |
| --- | --- | --- |
| `sync` | `false` | Attempt to compensate certain melee knockback timing cases |
| `flush` | `false` | Immediately flush the final melee knockback packet |

Other Sakura, Leaf, and Paper settings remain available according to the features in this branch. Do not copy an older version's full config into 26.3 without reviewing renamed or removed settings.

## Project status

**Fuji 26.3 is under active testing.** Booting a server and loading plugins are useful checks, but they do not by themselves prove combat parity, TNT timing accuracy, plugin compatibility, or stability under cannon loads.

Before relying on a build, test repeated TNT shots, falling-block stacking, explosions in water, player knockback, entity visibility, saves/restarts, and your actual plugins on a separate server.

## Credits

Fuji builds on the work of:

- [Leaf](https://github.com/Winds-Studio/Leaf) — performance-focused Paper fork
- [Sakura](https://github.com/Samsuik/Sakura) — cannoning mechanics and inspiration
- [Paper](https://github.com/PaperMC/Paper) — server and plugin-API foundation
- [Gale](https://github.com/GaleMC/Gale), [Purpur](https://github.com/PurpurMC/Purpur), [Pufferfish](https://github.com/pufferfish-gg/Pufferfish), [Moonrise](https://github.com/Spottedleaf/Moonrise), and the many contributors whose work Leaf incorporates

Please credit Leaf, Sakura, and the applicable original patch authors when distributing modified builds.

## License

Fuji inherits multiple open-source licenses from its upstream dependencies and individual patches. See [LICENSE.md](LICENSE.md) and the individual patch headers for the applicable terms. Server binaries are GPL-3.0-only as described in the current license file.

---

<p align="center"><sub>藤 — Leaf × Sakura · Minecraft 26.3</sub></p>
\n