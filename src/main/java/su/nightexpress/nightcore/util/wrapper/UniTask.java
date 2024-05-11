package su.nightexpress.nightcore.util.wrapper;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.NightCorePlugin;

public class UniTask {

    private final NightCorePlugin plugin;
    private final Runnable runnable;

    private long interval;
    private boolean async;
    private int taskId;

    public UniTask(@NotNull final NightCorePlugin plugin, @NotNull final Runnable runnable) { this(plugin, runnable, 0L); }

    public UniTask(@NotNull final NightCorePlugin plugin, @NotNull final Runnable runnable, final long interval) { this(plugin, runnable, interval, false); }

    public UniTask(@NotNull final NightCorePlugin plugin, @NotNull final Runnable runnable, final long interval, final boolean async) {
        this.plugin = plugin;
        this.runnable = runnable;
        this.setTicksInterval(interval);
        this.setAsync(async);

        this.taskId = -1;
    }

    public UniTask setSecondsInterval(final int interval) { return this.setTicksInterval(interval * 20L); }

    public UniTask setTicksInterval(final long interval) {
        this.interval = interval;
        return this;
    }

    public UniTask setAsync() { return this.setAsync(true); }

    public UniTask setAsync(final boolean async) {
        this.async = async;
        return this;
    }

    public boolean isRunning() { return this.taskId >= 0 && this.plugin.getScheduler().isCurrentlyRunning(this.taskId); }

    public final void restart() {
        this.stop();
        this.start();
    }

    public UniTask start() {
        if (this.taskId >= 0 || this.interval <= 0L)
            return this;

        if (this.async) {
            this.taskId = this.plugin.getScheduler().runTaskTimerAsynchronously(this.plugin, this.runnable, 0L, this.interval).getTaskId();
        } else {
            this.taskId = this.plugin.getScheduler().runTaskTimer(this.plugin, this.runnable, 0L, this.interval).getTaskId();
        }
        return this;
    }

    public boolean stop() {
        if (this.taskId < 0)
            return false;

        this.plugin.getServer().getScheduler().cancelTask(this.taskId);
        this.taskId = -1;
        return true;
    }
}
