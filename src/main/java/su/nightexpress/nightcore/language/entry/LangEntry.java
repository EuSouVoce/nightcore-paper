package su.nightexpress.nightcore.language.entry;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.config.FileConfig;

public abstract class LangEntry<T> {

    protected final String path;
    protected final String defaultText;

    public LangEntry(@NotNull final String path, @NotNull final String defaultText) {
        this.path = path;
        this.defaultText = defaultText;
    }

    public abstract boolean write(@NotNull FileConfig config);

    @NotNull
    public abstract T load(@NotNull NightCorePlugin plugin);

    @NotNull
    public String getPath() { return this.path; }

    @NotNull
    public String getDefaultText() { return this.defaultText; }
}
