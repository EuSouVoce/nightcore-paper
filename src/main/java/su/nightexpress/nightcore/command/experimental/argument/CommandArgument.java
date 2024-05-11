package su.nightexpress.nightcore.command.experimental.argument;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nightexpress.nightcore.command.experimental.TabContext;
import su.nightexpress.nightcore.command.experimental.builder.ArgumentBuilder;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.language.message.LangMessage;
import su.nightexpress.nightcore.util.Placeholders;

public class CommandArgument<T> {
// @formatter:off
    private final String                             name;
    private final Function<String, T>                parser;
    private final boolean                            required;
    private final String                             localized;
    private final String                             permission;
    private final Function<TabContext, List<String>> samples;
    private final LangMessage                        failureMessage;
// @formatter:on
    public CommandArgument(@NotNull final String name, @NotNull final Function<String, T> parser, final boolean required,
            @Nullable final String localized, @Nullable final String permission, @Nullable final LangMessage failureMessage,
            @Nullable final Function<TabContext, List<String>> samples) {
        this.name = name.toLowerCase();
        this.parser = parser;
        this.required = required;
        this.samples = samples;
        this.localized = this.getLocalized(localized);
        this.permission = permission;
        this.failureMessage = failureMessage;
    }

    @NotNull
    public static <T> ArgumentBuilder<T> builder(@NotNull final String name, @NotNull final Function<String, T> parser) {
        return new ArgumentBuilder<>(name, parser);
    }

    @Nullable
    public ParsedArgument<T> parse(@NotNull final String str) {
        final T result = this.parser.apply(str);
        return result == null ? null : new ParsedArgument<>(result);
    }

    public boolean hasPermission(@NotNull final CommandSender sender) {
        return this.permission == null || sender.hasPermission(this.permission);
    }

    @NotNull
    public List<String> getSamples(@NotNull final TabContext context) {
        return this.samples == null ? Collections.emptyList() : this.samples.apply(context);
    }

    @NotNull
    private String getLocalized(@Nullable String localized) {
        if (localized == null) {
            localized = this.name;
        }

        final String format = (this.isRequired() ? CoreLang.COMMAND_ARGUMENT_FORMAT_REQUIRED : CoreLang.COMMAND_ARGUMENT_FORMAT_OPTIONAL)
                .getString();

        return format.replace(Placeholders.GENERIC_NAME, localized);
    }

    @NotNull
    public LangMessage getFailureMessage() {
        return this.failureMessage == null ? CoreLang.ERROR_COMMAND_PARSE_ARGUMENT.getMessage() : this.failureMessage;
    }

    @NotNull
    public String getName() { return this.name; }

    @NotNull
    public Function<String, T> getParser() { return this.parser; }

    public boolean isRequired() { return this.required; }

    @NotNull
    public String getLocalized() { return this.localized == null ? this.name : this.localized; }

    @Nullable
    public String getPermission() { return this.permission; }

    @NotNull
    public Function<TabContext, List<String>> getSamples() { return this.samples; }
}
