package org.dreeam.leaf.config.modules.gameplay;

import org.dreeam.leaf.config.ConfigModules;
import org.dreeam.leaf.config.EnumConfigCategory;

/**
 * Fuji - instant falling-block stacking.
 * <p>
 * When a falling block (sand/gravel/etc.) would spawn as a gravity entity directly above an existing
 * pile of the same kind, place it instantly on top of the pile as a block instead of dropping it,
 * and convert any falling-block entities already standing in that column into blocks. This removes
 * the fall animation and the entities for stacked cannon sand.
 * <p>
 * Defaults off (vanilla behavior: falling blocks spawn as gravity entities).
 */
public class InstantFallingBlocks extends ConfigModules {

    public String getBasePath() {
        return EnumConfigCategory.GAMEPLAY.getBaseKeyName() + ".instant-falling-blocks";
    }

    public static boolean enabled = false;

    @Override
    public void onLoaded() {
        enabled = config.getBoolean(getBasePath(), enabled, config.pickStringRegionBased(
            """
            Instantly stack falling blocks onto an existing pile of the same kind below them (within 20
            blocks) instead of spawning a gravity entity, converting any falling-block entities already
            in that column to blocks. Removes the fall animation/entities for stacked cannon sand.""",
            """
            将下落方块直接堆叠到其下方（20 格内）已存在的同类方块堆上，而不是生成重力实体，
            并将该列中已有的下落方块实体转换为方块。消除堆叠加农炮沙子的下落动画与实体。"""
        ));
    }
}
