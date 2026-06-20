package org.dreeam.leaf.config.modules.gameplay;

import org.dreeam.leaf.config.ConfigModules;
import org.dreeam.leaf.config.EnumConfigCategory;

/**
 * Fuji - legacy (1.8-style) friction/air knockback model.
 * <p>
 * When enabled, melee knockback uses a friction-divided + directional velocity computation with
 * separate ground/air horizontal and vertical modifiers and sprint multipliers — the model that
 * gives old-PvP its combo and air-knockback feel — instead of the default add-based knockback.
 * <p>
 * Defaults off. The values below are a vanilla-equivalent baseline (friction = 2.0 halving, unit
 * air/sprint modifiers); the only non-vanilla default is that vertical knockback is also applied in
 * the air, which is the 1.8-ish part. Tune the modifiers for a specific era's feel.
 */
public class LegacyKnockback extends ConfigModules {

    public String getBasePath() {
        return EnumConfigCategory.GAMEPLAY.getBaseKeyName() + ".legacy-knockback";
    }

    public static boolean enabled = false;
    public static double frictionHorizontal = 2.0;
    public static double frictionVertical = 2.0;
    public static double horizontalModifier = 0.4;
    public static double verticalModifier = 0.36;
    public static double horizontalAirModifier = 1.0;
    public static double verticalAirModifier = 1.0;
    public static double sprintingHorizontalModifier = 1.0;
    public static double sprintingVerticalModifier = 1.0;
    public static double verticalMax = 0.4;
    public static boolean probabilisticResistance = false;

    @Override
    public void onLoaded() {
        enabled = config.getBoolean(getBasePath() + ".enabled", enabled, config.pickStringRegionBased(
            """
            Use a 1.8-style friction/air knockback model on melee hits instead of the default add-based
            model. Replaces the per-hit knockback velocity with a friction-divided + directional
            computation that exposes separate ground/air and sprint modifiers. Default off.""",
            """
            在近战命中时使用 1.8 风格的摩擦/空中击退模型，而非默认的叠加式模型。默认关闭。"""));
        frictionHorizontal = config.getDouble(getBasePath() + ".friction-horizontal", frictionHorizontal);
        frictionVertical = config.getDouble(getBasePath() + ".friction-vertical", frictionVertical);
        horizontalModifier = config.getDouble(getBasePath() + ".horizontal-modifier", horizontalModifier);
        verticalModifier = config.getDouble(getBasePath() + ".vertical-modifier", verticalModifier);
        horizontalAirModifier = config.getDouble(getBasePath() + ".horizontal-air-modifier", horizontalAirModifier);
        verticalAirModifier = config.getDouble(getBasePath() + ".vertical-air-modifier", verticalAirModifier);
        sprintingHorizontalModifier = config.getDouble(getBasePath() + ".sprinting-horizontal-modifier", sprintingHorizontalModifier);
        sprintingVerticalModifier = config.getDouble(getBasePath() + ".sprinting-vertical-modifier", sprintingVerticalModifier);
        verticalMax = config.getDouble(getBasePath() + ".vertical-max", verticalMax);
        probabilisticResistance = config.getBoolean(getBasePath() + ".probabilistic-resistance", probabilisticResistance,
            config.pickStringRegionBased(
                "Use 1.8-style knockback resistance (a chance to fully resist) instead of a linear factor.",
                "使用 1.8 风格的击退抗性（按概率完全抵抗），而非线性系数。"));
    }
}
