## What does this PR do?

<!-- A clear description of the change and why it's needed. Link any related issue. -->

## How was it tested?

<!-- Especially important for cannon and combat changes — describe the in-game test (cannon fired, walls cleared,
     PvP felt, etc.), not just "it compiles". -->

## Checklist

- [ ] Built locally (`./gradlew createMojmapPaperclipJar`)
- [ ] New gameplay behavior is config-gated and **default-off** (no behavior change for existing servers)
- [ ] If Minecraft source changed: patches regenerated (per-channel) and committed; `build-data/leaf.at` not churned
- [ ] Upstream authorship / license headers preserved on anything derived from Leaf, Sakura, or another project
