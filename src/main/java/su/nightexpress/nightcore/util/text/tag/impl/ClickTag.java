package su.nightexpress.nightcore.util.text.tag.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.md_5.bungee.api.chat.ClickEvent;
import su.nightexpress.nightcore.util.text.TextRoot;
import su.nightexpress.nightcore.util.text.tag.api.ComplexTag;
import su.nightexpress.nightcore.util.text.tag.api.ContentTag;
import su.nightexpress.nightcore.util.text.tag.decorator.ClickDecorator;

public class ClickTag extends ComplexTag implements ContentTag {

    public static final String NAME = "click";

    public ClickTag() { super(ClickTag.NAME); }

    @NotNull
    public String encloseRun(@NotNull final String text, @NotNull final String command) {
        return this.enclose(text, ClickEvent.Action.RUN_COMMAND, command);
    }

    @NotNull
    @Deprecated
    public String enclose(@NotNull final ClickEvent.Action action, @NotNull final String text, @NotNull final String content) {
        return this.enclose(text, action, content);
    }

    @NotNull
    public String enclose(@NotNull final String text, @NotNull final ClickEvent.Action action, @NotNull final String content) {
        // content = content.replace("'", "\\'");

        // String tagOpen = brackets(NAME + ":" + action.name().toLowerCase() + ":'" +
        // content + "'");
        // String tagClose = this.getClosingName();

        // return tagOpen + text + tagClose;

        final String data = action.name().toLowerCase() + ":\"" + this.escapeQuotes(content) + "\"";

        return this.encloseContent(text, data);
    }

    @Override
    @Nullable
    public ClickDecorator parse(@NotNull String tagContent) {
        ClickEvent.Action action = null;
        for (final ClickEvent.Action global : ClickEvent.Action.values()) {
            if (tagContent.startsWith(global.name().toLowerCase())) {
                action = global;
                break;
            }
        }
        if (action == null)
            return null;

        final int prefixSize = action.name().toLowerCase().length() + 1; // 1 for ':', like "run_command:"
        tagContent = tagContent.substring(prefixSize);

        final String value = TextRoot.stripQuotesSlash(tagContent);

        return new ClickDecorator(action, value);
    }
}
