package su.nightexpress.nightcore.command.experimental.flag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.command.experimental.builder.SimpleFlagBuilder;

public class SimpleFlag extends CommandFlag {

    public SimpleFlag(@NotNull final String name, @Nullable final String permission) { super(name, permission); }

    @NotNull
    public static SimpleFlagBuilder builder(@NotNull final String name) { return new SimpleFlagBuilder(name); }
}
