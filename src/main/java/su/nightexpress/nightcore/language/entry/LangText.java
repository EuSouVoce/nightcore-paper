package su.nightexpress.nightcore.language.entry;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.language.message.LangMessage;
import su.nightexpress.nightcore.util.Placeholders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LangText extends LangEntry<LangMessage> {

    private LangMessage message;

    public LangText(@NotNull final String path, @NotNull final String... defaultText) { super(path, String.join("\n", defaultText)); }

    @NotNull
    public static LangText of(@NotNull final String path, @NotNull final String defaultText) { return new LangText(path, defaultText); }

    @NotNull
    public static LangText of(@NotNull final String path, @NotNull final String... defaultText) { return new LangText(path, defaultText); }

    @Override
    public boolean write(@NotNull final FileConfig config) {
        if (!config.contains(this.getPath())) {
            final String textDefault = this.getDefaultText();
            final String[] textSplit = textDefault.split("\n");
            config.set(this.getPath(), textSplit.length > 1 ? Arrays.asList(textSplit) : textDefault);
            return true;
        }
        return false;
    }

    @Override
    @NotNull
    public LangMessage load(@NotNull final NightCorePlugin plugin) {
        final FileConfig config = plugin.getLang();

        this.write(config);

        final List<String> text = new ArrayList<>(config.getStringList(this.getPath()));
        if (text.isEmpty()) {
            text.add(config.getString(this.getPath(), this.getPath()));
        }

        this.setMessage(LangMessage.parse(plugin, String.join(Placeholders.TAG_LINE_BREAK, text)));

        return this.getMessage();
    }

    @NotNull
    public LangMessage getMessage() { return this.message; }

    @NotNull
    public LangMessage getMessage(@NotNull final NightCorePlugin plugin) { return this.message.setPrefix(plugin.getPrefix()); }

    public void setMessage(@NotNull final LangMessage message) { this.message = message; }
}
