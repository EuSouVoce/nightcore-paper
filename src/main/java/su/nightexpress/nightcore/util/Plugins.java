package su.nightexpress.nightcore.util;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.NightCore;

public class Plugins {

    public static final NightCore CORE = NightCore.getPlugin(NightCore.class);
    // @formatter:off
    public static final String VAULT           = "Vault";
    public static final String PLACEHOLDER_API = "PlaceholderAPI";
    public static final String FLOODGATE       = "floodgate";

    private static final boolean HAS_PLACEHOLDER_API = Plugins.isInstalled(Plugins.PLACEHOLDER_API);
    private static final boolean HAS_VAULT           = Plugins.isInstalled(Plugins.VAULT);
    private static final boolean HAS_FLOODGATE       = Plugins.isInstalled(Plugins.FLOODGATE);
    // @formatter:on

    public static boolean isInstalled(@NotNull final String pluginName) {
        final Plugin plugin = Plugins.CORE.getPluginManager().getPlugin(pluginName);
        return plugin != null;
    }

    public static boolean isLoaded(@NotNull final String pluginName) {
        final Plugin plugin = Plugins.CORE.getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }

    public static boolean isSpigot() { return Plugins.CORE.getServer().getVersion().contains("Spigot"); }

    public static boolean hasPlaceholderAPI() { return Plugins.HAS_PLACEHOLDER_API; }

    public static boolean hasVault() { return Plugins.HAS_VAULT; }

    public static boolean hasFloodgate() { return Plugins.HAS_FLOODGATE; }
}
