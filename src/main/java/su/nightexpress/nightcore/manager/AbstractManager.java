package su.nightexpress.nightcore.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.util.wrapper.UniTask;

public abstract class AbstractManager<P extends NightCorePlugin> extends SimpleManager<P> {

    protected final Set<SimpeListener> listeners;
    protected final List<UniTask> tasks;

    public AbstractManager(@NotNull final P plugin) {
        super(plugin);
        this.listeners = new HashSet<>();
        this.tasks = new ArrayList<>();
    }

    @Override
    public void shutdown() {
        this.tasks.forEach(UniTask::stop);
        this.tasks.clear();
        this.listeners.forEach(SimpeListener::unregisterListeners);
        this.listeners.clear();
        super.shutdown();
    }

    protected void addListener(@NotNull final SimpeListener listener) {
        if (this.listeners.add(listener)) {
            listener.registerListeners();
        }
    }

    protected void addTask(@NotNull final UniTask task) {
        this.tasks.add(task);
        task.start();
    }
}
