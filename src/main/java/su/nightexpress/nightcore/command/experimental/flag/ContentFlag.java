package su.nightexpress.nightcore.command.experimental.flag;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nightexpress.nightcore.command.experimental.argument.ParsedArgument;
import su.nightexpress.nightcore.command.experimental.builder.ContentFlagBuilder;

public class ContentFlag<T> extends CommandFlag {

    private final Function<String, T> parser;
    private final String sample;

    public ContentFlag(@NotNull final String name, @NotNull final Function<String, T> parser, @Nullable final String sample,
            @Nullable final String permission) {
        super(name, permission);
        this.parser = parser;
        this.sample = sample == null ? "" : sample;
    }

    @NotNull
    public static <T> ContentFlagBuilder<T> builder(@NotNull final String name, @NotNull final Function<String, T> parser) {
        return new ContentFlagBuilder<>(name, parser);
    }

    @Nullable
    public ParsedArgument<T> parse(@NotNull final String str) {
        final T result = this.parser.apply(str);
        return result == null ? null : new ParsedArgument<>(result);
    }

    @NotNull
    public String getSampled() { return this.getPrefixed() + CommandFlag.DELIMITER + this.getSample(); }

    @NotNull
    public Function<String, T> getParser() { return this.parser; }

    @NotNull
    public String getSample() { return this.sample; }
}
