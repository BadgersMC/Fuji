package org.dreeam.leaf.config.modules.gameplay;

import org.dreeam.leaf.config.ConfigCategory;
import org.dreeam.leaf.config.ConfigModule;

/**
 * Experimental block-stack shortcut for cannon ammunition only.
 * Never enabled for production or without an isolated mechanics regression test.
 */
public final class InstantFallingBlocks extends ConfigModule {
    public static boolean enabled = false;

    @Override
    public String basePath() {
        return ConfigCategory.GAMEPLAY.basePath() + ".instant-falling-blocks";
    }

    @Override
    public void onLoaded() {
        enabled = globalConfig.getBoolean(basePath() + ".enabled", false);
        if (enabled) {
            org.dreeam.leaf.config.LeafConfig.LOGGER.warn(
                "Fuji Frontier 26.4: experimental instant-falling-blocks is enabled. " +
                "Use only in disposable cannon-test worlds; falling-block behavior has not passed runtime parity tests."
            );
        }
    }
}