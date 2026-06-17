package me.fuji.maintenance;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Fuji - lightweight maintenance mode.
 * <p>
 * Toggled with {@code /maintenance} (op-level), persisted to {@code fuji-maintenance.yml}.
 * While enabled, any player lacking the {@code fuji.maintenance.bypass} permission is
 * kicked at login with the configured message. Intentionally self-contained so it does
 * not depend on any of the fork's larger config frameworks.
 */
public final class Maintenance {

    private static final File FILE = new File("fuji-maintenance.yml");
    private static final String DEFAULT_KICK =
        "<gradient:#7C6BC4:#E0968E>The server is under maintenance.</gradient><newline><gray>Please check back soon.";

    private static volatile boolean enabled;
    private static Component kickMessage = MiniMessage.miniMessage().deserialize(DEFAULT_KICK);

    private Maintenance() {}

    public static void load() {
        if (!FILE.exists()) {
            save();
            return;
        }
        final FileConfiguration cfg = YamlConfiguration.loadConfiguration(FILE);
        enabled = cfg.getBoolean("enabled", false);
        final String msg = cfg.getString("kick-message");
        if (msg != null && !msg.isEmpty()) {
            kickMessage = MiniMessage.miniMessage().deserialize(msg);
        }
    }

    public static void save() {
        final FileConfiguration cfg = new YamlConfiguration();
        cfg.set("enabled", enabled);
        cfg.set("kick-message", MiniMessage.miniMessage().serialize(kickMessage));
        try {
            cfg.save(FILE);
        } catch (final IOException ignored) {
            // best-effort persistence; maintenance state still applies in-memory
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static Component kickMessage() {
        return kickMessage;
    }

    public static boolean toggle() {
        enabled = !enabled;
        save();
        return enabled;
    }

    public static void set(final boolean value) {
        enabled = value;
        save();
    }
}
