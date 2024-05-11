package su.nightexpress.nightcore.command.experimental.builder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.command.experimental.node.ChainedNode;
import su.nightexpress.nightcore.command.experimental.node.CommandNode;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.command.experimental.node.NodeExecutor;
import su.nightexpress.nightcore.language.entry.LangString;

public class ChainedNodeBuilder extends NodeBuilder<ChainedNode, ChainedNodeBuilder> {

    private final Map<String, CommandNode> childrenMap;

    private String localized;
    private NodeExecutor executor;

    public ChainedNodeBuilder(@NotNull final NightCorePlugin plugin, @NotNull final String... aliases) {
        super(plugin, aliases);
        this.childrenMap = new HashMap<>();
    }

    @Override
    @NotNull
    protected ChainedNodeBuilder getThis() { return this; }

    @NotNull
    public ChainedNodeBuilder localized(@NotNull final LangString localized) { return this.localized(localized.getString()); }

    @NotNull
    public ChainedNodeBuilder localized(@Nullable final String localized) {
        this.localized = localized;
        return this;
    }

    @NotNull
    public ChainedNodeBuilder addChained(@NotNull final String name, @NotNull final Consumer<ChainedNodeBuilder> consumer) {
        final ChainedNodeBuilder builder = ChainedNode.builder(this.plugin, name);
        consumer.accept(builder);
        return this.child(builder);
    }

    @NotNull
    public ChainedNodeBuilder addDirect(@NotNull final String name, @NotNull final Consumer<DirectNodeBuilder> consumer) {
        final DirectNodeBuilder builder = DirectNode.builder(this.plugin, name);
        consumer.accept(builder);
        return this.child(builder);
    }

    @NotNull
    public <S extends CommandNode, B extends NodeBuilder<S, B>> ChainedNodeBuilder child(@NotNull final B builder) {
        return this.child(builder.build());
    }

    @NotNull
    private ChainedNodeBuilder child(@NotNull final CommandNode name) {
        this.childrenMap.put(name.getName(), name);
        return this;
    }

    @NotNull
    public ChainedNodeBuilder fallback(@NotNull final NodeExecutor executor) {
        this.executor = executor;
        return this;
    }

    @Override
    @NotNull
    public ChainedNode build() {
        return new ChainedNode(this.plugin, this.name, this.aliases, this.description, this.localized, this.permission, this.playerOnly,
                this.executor, this.childrenMap);
    }
}
