package su.nightexpress.nightcore.command.experimental.builder;

import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.command.experimental.node.CommandNode;
import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.util.wrapper.UniPermission;

public abstract class NodeBuilder<S extends CommandNode, B extends NodeBuilder<S, B>> {

    protected final NightCorePlugin plugin;
    protected final String name;

    protected String[] aliases;
    protected String description;
    protected String permission;
    protected boolean playerOnly;

    public NodeBuilder(@NotNull final NightCorePlugin plugin, @NotNull final String... aliases) {
        this.plugin = plugin;
        this.name = aliases[0];
        this.aliases = Stream.of(aliases).skip(1).toArray(String[]::new);
        this.description = "";
        this.permission = null;
        this.playerOnly = false;
    }

    @NotNull
    protected abstract B getThis();

    @NotNull
    public abstract S build();

    @NotNull
    public B aliases(@NotNull final String... aliases) {
        this.aliases = aliases;
        return this.getThis();
    }

    @NotNull
    public B description(@NotNull final LangString description) { return this.description(description.getString()); }

    @NotNull
    public B description(@NotNull final String description) {
        this.description = description;
        return this.getThis();
    }

    @NotNull
    public B permission(@NotNull final UniPermission permission) { return this.permission(permission.getName()); }

    @NotNull
    public B permission(@Nullable final String permission) {
        this.permission = permission;
        return this.getThis();
    }

    @NotNull
    public B playerOnly() {
        this.playerOnly = true;
        return this.getThis();
    }
}
