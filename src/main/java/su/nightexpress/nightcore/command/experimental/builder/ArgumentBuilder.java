package su.nightexpress.nightcore.command.experimental.builder;

import java.util.List;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nightexpress.nightcore.command.experimental.TabContext;
import su.nightexpress.nightcore.command.experimental.argument.CommandArgument;
import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;
import su.nightexpress.nightcore.language.message.LangMessage;
import su.nightexpress.nightcore.util.wrapper.UniPermission;

public class ArgumentBuilder<T> {

    private final String name;
    private final Function<String, T> parser;

    private boolean required;
    private String localized;
    private String permission;
    private Function<TabContext, List<String>> samples;
    private LangMessage failureMessage;

    public ArgumentBuilder(@NotNull final String name, @NotNull final Function<String, T> parser) {
        this.name = name;
        this.parser = parser;
    }

    @NotNull
    public ArgumentBuilder<T> required() { return this.required(true); }

    @NotNull
    public ArgumentBuilder<T> required(final boolean required) {
        this.required = required;
        return this;
    }

    @NotNull
    public ArgumentBuilder<T> localized(@NotNull final LangString localized) { return this.localized(localized.getString()); }

    @NotNull
    public ArgumentBuilder<T> localized(@Nullable final String localized) {
        this.localized = localized;
        return this;
    }

    @NotNull
    public ArgumentBuilder<T> permission(@NotNull final UniPermission permission) { return this.permission(permission.getName()); }

    @NotNull
    public ArgumentBuilder<T> permission(@Nullable final String permission) {
        this.permission = permission;
        return this;
    }

    @NotNull
    public ArgumentBuilder<T> customFailure(@NotNull final LangText text) { return this.customFailure(text.getMessage()); }

    @NotNull
    public ArgumentBuilder<T> customFailure(@Nullable final LangMessage message) {
        this.failureMessage = message;
        return this;
    }

    @NotNull
    public ArgumentBuilder<T> withSamples(@NotNull final Function<TabContext, List<String>> samples) {
        this.samples = samples;
        return this;
    }

    @NotNull
    public CommandArgument<T> build() {
        return new CommandArgument<>(this.name, this.parser, this.required, this.localized, this.permission, this.failureMessage,
                this.samples);
    }
}
