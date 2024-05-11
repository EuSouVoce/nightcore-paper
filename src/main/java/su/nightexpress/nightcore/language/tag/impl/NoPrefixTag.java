package su.nightexpress.nightcore.language.tag.impl;

import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.language.message.MessageOptions;
import su.nightexpress.nightcore.language.tag.MessageDecorator;
import su.nightexpress.nightcore.util.text.tag.api.Tag;

public class NoPrefixTag extends Tag implements MessageDecorator {

    public NoPrefixTag() { super("noprefix"); }

    @Override
    public int getWeight() { return 0; }

    @Override
    public void apply(@NotNull final MessageOptions options, @NotNull final String value) { options.setHasPrefix(false); }
}
