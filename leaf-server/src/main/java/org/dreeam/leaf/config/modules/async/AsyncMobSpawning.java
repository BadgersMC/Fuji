package org.dreeam.leaf.config.modules.async;

import org.dreeam.leaf.config.ConfigModule;
import org.dreeam.leaf.config.ConfigCategory;

public class AsyncMobSpawning extends ConfigModule {

    public String basePath() {
        return ConfigCategory.ASYNC.basePath() + ".async-mob-spawning";
    }

    public static boolean enabled = false; // Fuji Frontier 26.4: opt-in until runtime and parity tests pass
    private static boolean asyncMobSpawningInitialized;

    @Override
    public void onLoaded() {
        globalConfig.addCommentRegionBased(basePath(), """
                Experimental (opt-in): aggregate mob-spawning counts off-thread from an immutable
                entity and spawn-cost snapshot captured on the server thread. Entity ticking,
                chunk and world access, mob-cap application, and actual spawning remain synchronous.
                This Frontier 26.4 port requires further gameplay and performance validation.""",
            """
                是否异步化生物生成.
                在实体较多的服务器上, 异步生成可最高带来 15% 的性能提升.
                须在Paper配置文件中打开 per-player-mob-spawns 才能生效.""");

        // This prevents us from changing the value during a reload.
        if (asyncMobSpawningInitialized) {
            globalConfig.getConfigSection(basePath());
            return;
        }
        asyncMobSpawningInitialized = true;

        enabled = globalConfig.getBoolean(basePath() + ".enabled", enabled);
    }
}
