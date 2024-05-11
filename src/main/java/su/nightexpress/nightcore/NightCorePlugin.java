package su.nightexpress.nightcore;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.CommandManager;
import su.nightexpress.nightcore.command.api.NightPluginCommand;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.PluginDetails;
import su.nightexpress.nightcore.language.LangManager;
import su.nightexpress.nightcore.util.wrapper.UniTask;

import java.util.function.Consumer;

public interface NightCorePlugin extends Plugin {

    boolean isEngine();

    void enable();

    void disable();

    void reload();

    NightPluginCommand getBaseCommand();

    @Override
    @NotNull
    FileConfig getConfig();

    @NotNull
    FileConfig getLang();

    @NotNull
    PluginDetails getDetails();

    void extractResources(@NotNull String jarPath);

    void extractResources(@NotNull String jarParh, @NotNull String toPath);

    @NotNull
    default String getNameLocalized() { return this.getDetails().getName(); }

    @NotNull
    default String getPrefix() { return this.getDetails().getPrefix(); }

    @NotNull
    default String[] getCommandAliases() { return this.getDetails().getCommandAliases(); }

    @NotNull
    default String getLanguage() { return this.getDetails().getLanguage(); }

    default void info(@NotNull final String msg) { this.getLogger().info(msg); }

    default void warn(@NotNull final String msg) { this.getLogger().warning(msg); }

    default void error(@NotNull final String msg) { this.getLogger().severe(msg); }

    default void debug(@NotNull final String msg) { this.info("[DEBUG] " + msg); }

    @NotNull
    LangManager getLangManager();

    @NotNull
    CommandManager getCommandManager();

    @NotNull
    default BukkitScheduler getScheduler() { return this.getServer().getScheduler(); }

    @NotNull
    default PluginManager getPluginManager() { return this.getServer().getPluginManager(); }

    default void runTask(@NotNull final Consumer<BukkitTask> consumer) { this.getScheduler().runTask(this, consumer); }

    default void runTaskAsync(@NotNull final Consumer<BukkitTask> consumer) { this.getScheduler().runTaskAsynchronously(this, consumer); }

    default void runTaskLater(@NotNull final Consumer<BukkitTask> consumer, final long delay) {
        this.getScheduler().runTaskLater(this, consumer, delay);
    }

    default void runTaskLaterAsync(@NotNull final Consumer<BukkitTask> consumer, final long delay) {
        this.getScheduler().runTaskLaterAsynchronously(this, consumer, delay);
    }

    default void runTaskTimer(@NotNull final Consumer<BukkitTask> consumer, final long delay, final long interval) {
        this.getScheduler().runTaskTimer(this, consumer, delay, interval);
    }

    default void runTaskTimerAsync(@NotNull final Consumer<BukkitTask> consumer, final long delay, final long interval) {
        this.getScheduler().runTaskTimerAsynchronously(this, consumer, delay, interval);
    }

    @NotNull
    default UniTask createTask(@NotNull final Runnable runnable) { return new UniTask(this, runnable); }

    @NotNull
    default UniTask createAsyncTask(@NotNull final Runnable runnable) { return this.createTask(runnable).setAsync(); }
}
