# Tech Stack

## Runtime
- **Java 21** (Temurin JDK 21, `/opt/data/jdk21`)
- **Minecraft** 1.21.11 (Vanilla server + decompiled source)

## Build
- **Gradle 9.4.1** (`GRADLE_OPTS=-Xmx6g`)
- **Paperweight Patcher** (papermill v2) — applies patches to decompiled MC source
- **Vineflower** decompiler (macheDecompileJar step)

## Fork
- **Base:** [Leaf](https://github.com/Winds-Studio/Leaf) (Paper fork)
- **Fuji:** [BadgersMC/Fuji](https://github.com/BadgersMC/Fuji) (Leaf fork)
- **Patch system:** `.patch` files under `leaf-server/minecraft-patches/features/`
- **Additional source:** `leaf-server/src/main/java/` compiles alongside patched MC source

## Architecture (non-standard)
Fuji is a Minecraft server fork, not a traditional layered application. Code falls into two categories:
1. **MC source patches** — diff files applied to decompiled Minecraft server classes
2. **Fork Java source** — additional classes compiled alongside MC server code

There is no domain/application/infrastructure separation. The "domain" is Minecraft server internals.
