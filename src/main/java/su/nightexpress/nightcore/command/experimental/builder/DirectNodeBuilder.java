package su.nightexpress.nightcore.command.experimental.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.command.experimental.argument.CommandArgument;
import su.nightexpress.nightcore.command.experimental.flag.CommandFlag;
import su.nightexpress.nightcore.command.experimental.node.DirectExecutor;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;

public class DirectNodeBuilder extends NodeBuilder<DirectNode, DirectNodeBuilder> {

    private final List<CommandArgument<?>> arguments;
    private final Map<String, CommandFlag> flags;

    private DirectExecutor executor;

    public DirectNodeBuilder(@NotNull final NightCorePlugin plugin, @NotNull final String... aliases) {
        super(plugin, aliases);
        this.arguments = new ArrayList<>();
        this.flags = new HashMap<>();
    }

    @Override
    @NotNull
    protected DirectNodeBuilder getThis() { return this; }

    @NotNull
    public DirectNodeBuilder withArgument(@NotNull final ArgumentBuilder<?> builder) { return this.withArgument(builder.build()); }

    @NotNull
    public DirectNodeBuilder withArgument(@NotNull final CommandArgument<?> argument) {
        this.arguments.add(argument);
        return this;
    }

    @NotNull
    public DirectNodeBuilder withFlag(@NotNull final FlagBuilder<?, ?> builder) { return this.withFlag(builder.build()); }

    @NotNull
    public DirectNodeBuilder withFlag(@NotNull final CommandFlag flag) {
        this.flags.put(flag.getName(), flag);
        return this;
    }

    @NotNull
    public DirectNodeBuilder executes(final DirectExecutor executor) {
        this.executor = executor;
        return this;
    }

    @Override
    @NotNull
    public DirectNode build() {
        return new DirectNode(this.plugin, this.name, this.aliases, this.description, this.permission, this.playerOnly, this.arguments,
                this.flags, this.executor);
    }
}
