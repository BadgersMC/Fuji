package org.dreeam.leaf.config.modules.gameplay;

import org.dreeam.leaf.config.ConfigModules;
import org.dreeam.leaf.config.EnumConfigCategory;

/**
 * Fuji - roof-cannon height nerf.
 * <p>
 * Paper's {@code fixes.tnt-entity-height-nerf} / {@code fixes.falling-block-height-nerf} discard
 * cannon entities above a configured Y <em>regardless of movement</em>, which breaks cannons that
 * fire over a wall and arc back down below the limit. When the matching option here is enabled, the
 * nerf only discards an entity that is above the height limit <em>and</em> still moving horizontally,
 * so a projectile descending straight back into bounds survives.
 * <p>
 * Defaults off (stock Paper behavior); only takes effect when the corresponding Paper height-nerf is
 * also set.
 */
public class CannonHeightNerf extends ConfigModules {

    public String getBasePath() {
        return EnumConfigCategory.GAMEPLAY.getBaseKeyName() + ".cannon-height-nerf";
    }

    public static boolean tntOnlyWithHorizontalMovement = false;
    public static boolean fallingBlockOnlyWithHorizontalMovement = false;

    @Override
    public void onLoaded() {
        tntOnlyWithHorizontalMovement = config.getBoolean(getBasePath() + ".tnt-only-with-horizontal-movement", tntOnlyWithHorizontalMovement, config.pickStringRegionBased(
            """
            Only apply the TNT height nerf (fixes.tnt-entity-height-nerf) to TNT that is also moving
            horizontally. Keeps roof cannons working when projectiles arc back below the limit.""",
            """
            仅对同时存在水平移动的 TNT 应用高度削减（fixes.tnt-entity-height-nerf），
            使越墙后再次落回限制以下的炮弹得以保留。"""
        ));
        fallingBlockOnlyWithHorizontalMovement = config.getBoolean(getBasePath() + ".falling-block-only-with-horizontal-movement", fallingBlockOnlyWithHorizontalMovement, config.pickStringRegionBased(
            "Same as tnt-only-with-horizontal-movement, but for the falling-block height nerf.",
            "与 tnt-only-with-horizontal-movement 相同，但作用于下落方块的高度削减。"
        ));
    }
}
