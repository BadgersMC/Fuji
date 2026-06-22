<p align="center">
  <img src="assets/fuji-logo.png" width="200" alt="Fuji logo — Mount Fuji at golden hour with hanging wisteria">
</p>

<h1 align="center">Fuji&nbsp;&nbsp;藤</h1>

<p align="center"><em>the peak of cannoning</em></p>

<p align="center">
  <a href="https://github.com/BadgersMC/Fuji/actions/workflows/build.yml"><img src="https://github.com/BadgersMC/Fuji/actions/workflows/build.yml/badge.svg?branch=fuji" alt="Build status"></a>
  <a href="https://github.com/BadgersMC/Fuji/releases/latest"><img src="https://img.shields.io/github/v/release/BadgersMC/Fuji?label=latest&color=7C6BC4" alt="Latest release"></a>
  <img src="https://img.shields.io/github/downloads/BadgersMC/Fuji/total?color=E0968E" alt="Downloads">
  <img src="https://img.shields.io/badge/Minecraft-1.21.11-3E4C7E" alt="Minecraft 1.21.11">
  <img src="https://img.shields.io/badge/Java-21-7C6BC4" alt="Java 21">
  <img src="https://img.shields.io/badge/status-early%20access-F4BC88" alt="Early access">
  <a href="LICENSE"><img src="https://img.shields.io/badge/license-GPL--3.0-2B2540" alt="GPL-3.0"></a>
</p>

---

**Fuji** is a Minecraft server fork that fuses **[Leaf](https://github.com/Winds-Studio/Leaf)'s** optimization stack with **[Sakura](https://github.com/Samsuik/Sakura)'s** cannon mechanics — purpose-built for faction networks that run many worlds and demand frame-perfect cannons.

## Why Fuji

Most forks make you choose. Fuji refuses to.

- 🎯 **World-class cannoning.** Sakura's deterministic explosions, cannon-entity merging, and version-accurate physics — the most advanced open cannon engine there is — merged in whole.
- 🌏 **Per-world parallel ticking.** Each world ticks on its own thread with its own TPS budget. A massive raid on one world *cannot* lag the others.
- ⚡ **Leaf-class performance.** The full Leaf stack underneath — Moonrise chunk system, Lithium, async, and the optimizations pulled from Gale, Purpur, Pufferfish, and Kore's TequilaSpigot.

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
- Client visibility settings (`/tnttoggle`, `/sandtoggle`), `/fps`
- **Per-world cannon configuration** — every world gets its own `sakura-world.yml`

### ⚡ Performance — from Leaf
- Moonrise chunk system, Lithium, SparklyPaper parallel world ticking
- Async pathfinding, entity tracking, and the broader Leaf optimization suite
- Optimizations inherited from Gale, Purpur, Pufferfish, and Paper

### 🌏 Multi-world & factions
- **Per-world TPS isolation** — lag is contained to the world that causes it
- Coexisting config systems: `paper-`, `gale-`, `leaf-`, and `sakura-` configs side by side
- Designed for networks of many independent cannoning worlds

### 🗻 Fuji's own
- **Instant falling-block stacking** — sand drops straight onto a matching pile as blocks instead of spawning gravity entities *(opt-in)*
- **Roof-cannon height nerf** — only clear above-limit entities that are still moving horizontally, so cannons firing over walls and back down survive *(opt-in)*

## The architecture it's built for

Fuji is happiest on a server designed as **many independent worlds** rather than one giant one:

> raid world · koth/pvp world · 3× overworld · 3× nether · 3× end

That's ~10 worlds, each on its own thread, each with its own TPS. By splitting gameplay across worlds, you sidestep the single-world scaling problem entirely — no sharding, no cross-region cannon desync. Cannons stay deterministic (one world = one thread, ticked in order), while the network as a whole scales across cores.

> **Tip:** keep cannons/factions on one world for full determinism, and let parallel ticking win you the cores on the others. Cannoning is intra-world, so per-world threading is safe — Sakura's cannon state is per-level/per-instance with no shared mutable globals.

## Status

> ⚠️ **Early access.** Fuji compiles, builds, and boots cleanly — and **fires real cannons**. Live testing has confirmed a 384-entity sand stacker, a sustained **160-wall stress test**, and cannon entities keeping their own chunks loaded across the shot — all matching upstream Sakura behavior. It's still early access: the wider range of cannon designs and heavy-load behavior under parallel ticking are in active testing, so treat it as a capable-but-maturing fork rather than a hands-off production drop-in.

A few Sakura patches remain **deferred** — kept on Leaf's stronger equivalent (e.g. hopper ticking #0025) or held pending further cannon testing (the entity-collision limit #0014). Others, including lava-tick timing and version-accurate inside-block traversal #0026, have since been ported. See [`DEFERRED.md`](DEFERRED.md).

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

Fuji is licensed under **GPL-3.0**, inherited from Paper, Leaf, and Sakura. See [`LICENSE`](LICENSE) for the full text and [`NOTICE.md`](NOTICE.md) for attribution. All upstream patches remain under their original licenses and attributions.

---

<p align="center"><sub>藤 — Leaf × Sakura · Minecraft 1.21.11</sub></p>
