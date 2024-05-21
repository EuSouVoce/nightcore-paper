package su.nightexpress.nightcore.util.text.tag.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.md_5.bungee.api.chat.ClickEvent;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.text.decoration.ClickEventDecorator;
import su.nightexpress.nightcore.util.text.decoration.Decorator;
import su.nightexpress.nightcore.util.text.decoration.ParsedDecorator;
import su.nightexpress.nightcore.util.text.tag.api.ContentTag;

public class ClickTag extends ContentTag {

    public static final String NAME = "click";

    public ClickTag() { super(ClickTag.NAME); }

    @Override
    public int getWeight() { return 50; }

    @NotNull
    public String enclose(@NotNull final ClickEvent.Action action, @NotNull final String text, @NotNull final String command) {
        final String actionName = action.name().toLowerCase();
        return this.enclose(actionName, command, text);
    }

    @Override
    @Nullable
    public ParsedDecorator onParse(@NotNull String sub) {
        ClickEvent.Action action = null;
        for (final ClickEvent.Action global : ClickEvent.Action.values()) {
            if (sub.startsWith(global.name().toLowerCase())) {
                action = global;
                break;
            }
        }
        if (action == null)
            return null;

        final int prefixSize = action.name().toLowerCase().length() + 1; // 1 for ':', like "run_command:"
        sub = sub.substring(prefixSize);

        final String content = StringUtil.parseQuotedContent(sub);
        if (content == null)
            return null;

        final int length = prefixSize + content.length();// + 2; // 2 for quotes

        final Decorator decorator = new ClickEventDecorator(action, content);

        return new ParsedDecorator(decorator, length);
    }
}
