package su.nightexpress.nightcore.command.experimental;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.command.experimental.builder.ChainedNodeBuilder;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.builder.NodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.ChainedNode;
import su.nightexpress.nightcore.command.experimental.node.CommandNode;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.command.impl.WrappedCommand;
import su.nightexpress.nightcore.util.CommandUtil;
import su.nightexpress.nightcore.util.Lists;

public class RootCommand<P extends NightCorePlugin, S extends CommandNode> implements ServerCommand {

    private final P plugin;
    private final S node;

    private WrappedCommand backend;

    public RootCommand(@NotNull final P plugin, @NotNull final S node) {
        this.plugin = plugin;
        this.node = node;
    }

    @NotNull
    public static <T extends NightCorePlugin> RootCommand<T, DirectNode> direct(@NotNull final T plugin, @NotNull final String name,
            @NotNull final Consumer<DirectNodeBuilder> consumer) {
        return RootCommand.direct(plugin, new String[] { name }, consumer);
    }

    @NotNull
    public static <T extends NightCorePlugin> RootCommand<T, ChainedNode> chained(@NotNull final T plugin, @NotNull final String name,
            @NotNull final Consumer<ChainedNodeBuilder> consumer) {
        return RootCommand.chained(plugin, new String[] { name }, consumer);
    }

    @NotNull
    public static <T extends NightCorePlugin> RootCommand<T, DirectNode> direct(@NotNull final T plugin, @NotNull final String[] aliases,
            @NotNull final Consumer<DirectNodeBuilder> consumer) {
        final DirectNodeBuilder builder = DirectNode.builder(plugin, aliases);
        consumer.accept(builder);
        return RootCommand.build(plugin, builder);
    }

    @NotNull
    public static <T extends NightCorePlugin> RootCommand<T, ChainedNode> chained(@NotNull final T plugin, @NotNull final String[] aliases,
            @NotNull final Consumer<ChainedNodeBuilder> consumer) {
        final ChainedNodeBuilder builder = ChainedNode.builder(plugin, aliases);
        consumer.accept(builder);
        return RootCommand.build(plugin, builder);
    }

    @NotNull
    public static <T extends NightCorePlugin, S extends CommandNode, B extends NodeBuilder<S, B>> RootCommand<T, S> build(
            @NotNull final T plugin, @NotNull final B builder) {
        return new RootCommand<>(plugin, builder.build());
    }

    @Override
    public boolean register() {
        this.backend = new WrappedCommand(this.plugin, this, this, this.node.getName(), this.node.getAliases(), this.node.getDescription(),
                this.node.getUsage());
        return CommandUtil.register(this.plugin, this.backend);
    }

    @Override
    public boolean unregister() {
        if (CommandUtil.unregister(this.getNode().getName())) {
            this.backend = null;
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command cmd, @NotNull final String label,
            final String[] args) {
        if (args.length == 0)
            return Collections.emptyList();

        // int index = 0;//args.length - 1;
        // String input = args[index];
        final TabContext context = new TabContext(sender, label, args, 0);
        final List<String> samples = this.node.getTab(context);

        return Lists.getSequentialMatches(samples, context.getInput());
    }

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label,
            @NotNull final String[] args) {
        final CommandContext commandContext = new CommandContext(this.plugin, sender, label, args);

        return this.node.run(commandContext);
    }

    @Override
    @NotNull
    public S getNode() { return this.node; }

    @Override
    @NotNull
    public WrappedCommand getBackend() { return this.backend; }
}
