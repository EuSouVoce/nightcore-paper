package su.nightexpress.nightcore.util.text.tag.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.md_5.bungee.api.chat.HoverEvent;
import su.nightexpress.nightcore.util.text.TextRoot;
import su.nightexpress.nightcore.util.text.tag.api.ComplexTag;
import su.nightexpress.nightcore.util.text.tag.api.ContentTag;
import su.nightexpress.nightcore.util.text.tag.decorator.Decorator;
import su.nightexpress.nightcore.util.text.tag.decorator.ShowItemDecorator;
import su.nightexpress.nightcore.util.text.tag.decorator.ShowTextDecorator;

public class HoverTag extends ComplexTag implements ContentTag {

    public static final String NAME = "hover";

    public HoverTag() { super(HoverTag.NAME); }

    @NotNull
    @Deprecated
    public String enclose(@NotNull final String text, @NotNull final String hint) { return this.encloseHint(text, hint); }

    @NotNull
    @Deprecated
    public String enclose(@NotNull final HoverEvent.Action action, @NotNull final String text, @NotNull final String content) {
        return this.enclose(text, action, content);
    }

    @NotNull
    public String encloseHint(@NotNull final String text, @NotNull final String hint) { return this.enclose(text, HoverEvent.Action.SHOW_TEXT, hint); }

    @NotNull
    public String enclose(@NotNull final String text, @NotNull final HoverEvent.Action action, @NotNull final String content) {
        // content = content.replace("'", "\\'");

        // String tagOpen = brackets(NAME + ":" + action.name().toLowerCase() + ":'" +
        // content + "'");
        // String tagClose = this.getClosingName();

        final String data = action.name().toLowerCase() + ":'" + this.escapeQuotes(content) + "'";
        return this.encloseContent(text, data);

        // return tagOpen + text + tagClose;
    }

    @Override
    @Nullable
    public Decorator parse(@NotNull String tagContent) {
        HoverEvent.Action action = null;
        for (final HoverEvent.Action global : HoverEvent.Action.values()) {
            if (tagContent.startsWith(global.name().toLowerCase())) {
                action = global;
                break;
            }
        }
        if (action == null)
            return null;

        final int prefixSize = action.name().toLowerCase().length() + 1; // 1 for ':', like "show_text:"
        tagContent = tagContent.substring(prefixSize);

        final String value = TextRoot.stripQuotesSlash(tagContent);

        if (action == HoverEvent.Action.SHOW_TEXT) {
            return new ShowTextDecorator(value);
        } else if (action == HoverEvent.Action.SHOW_ITEM) {
            return new ShowItemDecorator(value);
        }
        return null;
    }
}
