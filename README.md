<p align="center">
  <img src="assets/fuji-logo.png" width="200" alt="Fuji logo — Mount Fuji at golden hour with hanging wisteria">
</p>

<h1 align="center">Fuji&nbsp;&nbsp;藤</h1>

<p align="center"><em>the peak of cannoning</em></p>

<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-1.21.11-3E4C7E" alt="Minecraft 1.21.11">
  <img src="https://img.shields.io/badge/Java-21-7C6BC4" alt="Java 21">
  <img src="https://img.shields.io/badge/based%20on-Leaf%20%C3%97%20Sakura-E0968E" alt="Leaf × Sakura">
  <img src="https://img.shields.io/badge/status-early%20access-F4BC88" alt="Early access">
  <img src="https://img.shields.io/badge/license-GPL--3.0-2B2540" alt="GPL-3.0">
</p>

---

**Fuji** is a Minecraft server fork that fuses **[Leaf](https://github.com/Winds-Studio/Leaf)'s** optimization stack with **[Sakura](https://github.com/Samsuik/Sakura)'s** cannon mechanics — purpose-built for faction networks that run many worlds and demand frame-perfect cannons.

It is, as far as we know, the first fork to merge Sakura's cannoning engine into Leaf — a notoriously difficult patch-on-patch integration. The result is a single server jar that cannons like Sakura and scales like Leaf.

## Why Fuji

Most forks make you choose. Fuji refuses to.

- 🎯 **World-class cannoning.** Sakura's deterministic explosions, cannon-entity merging, and version-accurate physics — the most advanced open cannon engine there is — merged in whole.
- 🌏 **Per-world parallel ticking.** Each world ticks on its own thread with its own TPS budget. A massive cannon war on one world *cannot* lag the others.
- ⚡ **Leaf-class performance.** The full Leaf stack underneath — Moonrise chunk system, Lithium, async, and the optimizations pulled from Gale, Purpur, Pufferfish, and more.

The combination is the point: **deterministic single-world cannons _and_ multi-world parallelism** — without the cross-region desync that makes region-threaded forks hostile to cannoning.

## Heritage

```
Paper  →  Leaf 1.21.11  ──┐
                          ├──►  Fuji
Paper  →  Sakura 1.21.11 ─┘
```

Fuji is **Leaf `ver/1.21.11`** with **every Sakura `1.21.11` patch** folded in as native Leaf patches. Both are direct Paper forks on the same Minecraft version, so Sakura's cannon mechanics live alongside Leaf's optimizations rather than fighting them. Where the two genuinely overlapped (Sakura's state-watchers vs. Leaf's Lithium change-tracking), the systems were carefully grafted so both survive.

## Features

### 🎯 Cannoning — from Sakura
- Deterministic, optimised explosions and a replacement explosion-density cache
- Cannon-entity merging and optimised cannon-entity movement
- Configurable cannon physics, durable blocks, and specialised explosions
- Version-accurate mechanics targeting (`MinecraftMechanicsTarget`) for cross-version cannon parity
- Client visibility settings (`/tnttoggle`, `/sandtoggle`), `/tps`, `/fps`
- **Per-world cannon configuration** — every world gets its own `sakura-world.yml`

### ⚡ Performance — from Leaf
- Moonrise chunk system, Lithium, SparklyPaper parallel world ticking
- Async pathfinding, entity tracking, and the broader Leaf optimization suite
- Optimizations inherited from Gale, Purpur, Pufferfish, and Paper

### 🌏 Multi-world & factions
- **Per-world TPS isolation** — lag is contained to the world that causes it
- Coexisting config systems: `paper-`, `gale-`, `leaf-`, and `sakura-` configs side by side
- Designed for networks of many independent cannoning worlds

## The architecture it's built for

Fuji is happiest on a server designed as **many independent worlds** rather than one giant one:

> raid world · koth/pvp world · 3× overworld · 3× nether · 3× end

That's ~10 worlds, each on its own thread, each with its own TPS. By splitting gameplay across worlds, you sidestep the single-world scaling problem entirely — no sharding, no cross-region cannon desync. Cannons stay deterministic (one world = one thread, ticked in order), while the network as a whole scales across cores.

> **Tip:** keep cannons/factions on one world for full determinism, and let parallel ticking win you the cores on the others. Cannoning is intra-world, so per-world threading is safe — Sakura's cannon state is per-level/per-instance with no shared mutable globals.

## Status

> ⚠️ **Early access.** Fuji **compiles, builds, and boots** cleanly — Sakura's config system initializes with per-world configs and zero startup errors. What is *not* yet verified is **runtime cannon fidelity**: firing real cannons and confirming they behave identically to upstream Sakura, especially under parallel ticking. Treat this as an experimental fork in active testing, not a drop-in production server.

A short list of Sakura features is currently **deferred** where they collided with a stronger Leaf equivalent (hopper #0025, inside-block iteration #0026, the entity-collision limit #0014, lava-tick timing). See [`DEFERRED.md`](DEFERRED.md).

## Building from source

**Requirements:** JDK 21, Git. On Windows, run Gradle from **Git Bash** and enable long paths (`git config --global core.longpaths true` + the `LongPathsEnabled` registry key) — paperweight hits the 260-char limit otherwise.

```bash
git clone <your-fork-url> Fuji && cd Fuji
./gradlew applyAllPatches          # materialize Leaf + Sakura source
./gradlew createMojmapPaperclipJar # build the runnable jar
```

The jar lands in `leaf-server/build/libs/`. Spin up a test server with `./gradlew runPaperclip`.

> **Contributors:** the umbrella `rebuildPatches` trips a Gradle 9.4 / paperweight validation bug. Rebuild the channels individually instead, with `--no-configuration-cache`:
> `rebuildMinecraftFeaturePatches`, `rebuildServerFeaturePatches`, `rebuildPaperApiFeaturePatches`.

## Running

```bash
java -Xms4G -Xmx4G -jar leaf-paperclip-1.21.11-R0.1-SNAPSHOT-mojmap.jar --nogui
```

Accept the EULA (`eula=true`), and Fuji will generate `sakura-global.yml` plus a per-world `sakura-world.yml` for each world alongside the usual Paper/Leaf/Gale configs.

## Credits

Fuji stands entirely on the shoulders of others. Enormous thanks to:

- **[Leaf](https://github.com/Winds-Studio/Leaf)** (Winds-Studio / Dreeam) — the performance base
- **[Sakura](https://github.com/Samsuik/Sakura)** (Samsuik) — the cannon engine, and the project this fork exists to celebrate
- **[Paper](https://github.com/PaperMC/Paper)** — the foundation everything is built on
- **[Gale](https://github.com/GaleMC/Gale)**, **[Purpur](https://github.com/PurpurMC/Purpur)**, **[Pufferfish](https://github.com/pufferfish-gg/Pufferfish)**, **Moonrise** (Spottedleaf), **Lithium** (CaffeineMC), and the many forks Leaf draws from

If you run a server on Fuji, please credit Leaf and Sakura — they did the hard parts.

## License

Fuji is licensed under **GPL-3.0**, inherited from Paper, Leaf, and Sakura. See [`LICENSE.md`](LICENSE.md). All upstream patches remain under their original licenses and attributions.

---

<p align="center"><sub>藤 — Leaf × Sakura · Minecraft 1.21.11</sub></p>
