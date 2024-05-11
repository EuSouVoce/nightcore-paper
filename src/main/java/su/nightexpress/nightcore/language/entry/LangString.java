package su.nightexpress.nightcore.language.entry;

import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.nightcore.util.text.WrappedMessage;

public class LangString extends LangEntry<String> {

    private String string;
    private WrappedMessage message;

    public LangString(@NotNull final String path, @NotNull final String defaultText) { super(path, defaultText); }

    @NotNull
    public static LangString of(@NotNull final String path, @NotNull final String defaultText) { return new LangString(path, defaultText); }

    @Override
    public boolean write(@NotNull final FileConfig config) {
        if (!config.contains(this.getPath())) {
            final String textDefault = this.getDefaultText();
            config.set(this.getPath(), textDefault);
            return true;
        }
        return false;
    }

    @Override
    @NotNull
    public String load(@NotNull final NightCorePlugin plugin) {
        final FileConfig config = plugin.getLang();

        this.write(config);
        final String text = config.getString(this.getPath(), this.getPath());
        this.setString(text);

        return this.getString();
    }

    @NotNull
    public String getString() { return this.string; }

    public void setString(@NotNull final String string) {
        this.string = string;
        this.message = NightMessage.from(string);
    }

    @NotNull
    public WrappedMessage getMessage() { return this.message; }

    @NotNull
    public String getLegacy() { return this.getMessage().toLegacy(); }
}
