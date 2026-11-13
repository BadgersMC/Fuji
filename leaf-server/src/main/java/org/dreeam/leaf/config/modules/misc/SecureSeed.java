package org.dreeam.leaf.config.modules.misc;

import org.dreeam.leaf.config.ConfigModule;
import org.dreeam.leaf.config.ConfigCategory;

public class SecureSeed extends ConfigModule {

    public String basePath() {
        return ConfigCategory.MISC.basePath() + ".secure-seed";
    }

    public static boolean enabled = false;

    @Override
    public void onLoaded() {
        globalConfig.addCommentRegionBased(basePath(), """
                Fuji Frontier 26.4 experimental: secure seed is NOT available yet.
                Setting enabled=true emits an error and is ignored to preserve vanilla world generation.
                Do not rely on this option for 1024-bit seed protection.""",
            """
                安全种子开启后, 所有矿物与结构都将使用1024位的种子进行生成, 无法被破解.""");

        final boolean requested = globalConfig.getBoolean(basePath() + ".enabled", false);
        // The old Matter patch has unported 26.4 worldgen hooks and shared global seed state.
        // Never claim 1024-bit protection or change terrain generation with a partial implementation.
        if (requested) {
            org.dreeam.leaf.config.LeafConfig.LOGGER.error(
                "Fuji Frontier 26.4: secure-seed.enabled=true is unsupported. " +
                "Secure Seed is DISABLED; world generation remains vanilla. Do not use this as seed protection."
            );
        }
        enabled = false;
    }
}
