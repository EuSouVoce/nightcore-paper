package su.nightexpress.nightcore.command.experimental.node;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.TabContext;
import su.nightexpress.nightcore.command.experimental.builder.ChainedNodeBuilder;
import su.nightexpress.nightcore.command.experimental.builder.NodeBuilder;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.util.Placeholders;
import su.nightexpress.nightcore.util.StringUtil;

import java.util.*;

public class ChainedNode extends CommandNode {

    private final String localized;
    private final Map<String, CommandNode> commandMap;
    private final NodeExecutor fallback;

    public ChainedNode(@NotNull final NightCorePlugin plugin, @NotNull final String name, @NotNull final String[] aliases, @NotNull final String description,
            @Nullable final String localized, @Nullable final String permission, final boolean playerOnly, @Nullable final NodeExecutor fallback,
            @NotNull final Map<String, CommandNode> commandMap) {
        super(plugin, name, aliases, description, permission, playerOnly);
        this.localized = localized == null ? StringUtil.capitalizeUnderscored(name) : localized;
        this.commandMap = new HashMap<>();
        this.fallback = fallback;

        this.addChildren(DirectNode.builder(plugin, "help").description(CoreLang.COMMAND_HELP_DESC).permission(permission)
                .executes((context, arguments) -> this.sendCommandList(context)));

        commandMap.values().forEach(this::addChildren);
    }

    @NotNull
    public static ChainedNodeBuilder builder(@NotNull final NightCorePlugin plugin, @NotNull final String... aliases) {
        return new ChainedNodeBuilder(plugin, aliases);
    }

    @Override
    protected boolean onRun(@NotNull final CommandContext context) {
        // System.out.println("context.getArgs() = " +
        // Arrays.toString(context.getArgs()));
        if (context.getArgs().length == 0 || context.getArgumentIndex() >= context.getArgs().length) {
            return this.onFallback(context);
        }

        final String node = context.getArgs()[context.getArgumentIndex()];
        // System.out.println("node = " + node);
        final CommandNode children = this.getCommandMap().get(node);
        // System.out.println("children = " + children);
        // System.out.println("this.commandMap = " + this.commandMap);
        if (children == null) {
            return this.onFallback(context);
        }

        // System.out.println("children run");
        context.setArgumentIndex(context.getArgumentIndex() + 1);
        return children.run(context);
    }

    private boolean onFallback(@NotNull final CommandContext context) {
        if (this.fallback != null) {
            return this.fallback.run(context);
        }
        return this.sendCommandList(context);
    }

    private boolean sendCommandList(@NotNull final CommandContext context) {
        final CommandSender sender = context.getSender();

        return context.sendSuccess(CoreLang.COMMAND_HELP_LIST.getMessage().replace(Placeholders.GENERIC_NAME, this.getLocalized())
                .replace(Placeholders.GENERIC_ENTRY, list -> {
                    this.getChildrens().stream().sorted(Comparator.comparing(CommandNode::getName)).forEach(children -> {
                        if (!children.hasPermission(sender))
                            return;

                        list.add(CoreLang.COMMAND_HELP_ENTRY.getString().replace(Placeholders.COMMAND_LABEL, children.getNameWithParents())
                                .replace(Placeholders.COMMAND_USAGE, children.getUsage())
                                .replace(Placeholders.COMMAND_DESCRIPTION, children.getDescription()));
                    });
                }));
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull final TabContext context) {
        // System.out.println("context.getArgs() = " +
        // Arrays.toString(context.getArgs()));
        // System.out.println("context.getIndex() = " + context.getIndex());
        if (context.getIndex() == context.getArgs().length - 1) {
            return new ArrayList<>(this.commandMap.keySet());
        }
        if (context.getIndex() >= context.getArgs().length) {
            return Collections.emptyList();
        }

        final String node = context.getAtIndex();
        final CommandNode children = this.getCommandMap().get(node);
        if (children == null) {
            return Collections.emptyList();
        }

        context.setIndex(context.getIndex() + 1);

        return children.getTab(context);
    }

    public void addChildren(@NotNull final NodeBuilder<?, ?> builder) { this.addChildren(builder.build()); }

    public void addChildren(@NotNull final CommandNode children) {
        if (children.getParent() != null)
            return;

        this.commandMap.put(children.getName(), children);
        for (final String alias : children.getAliases()) {
            this.commandMap.put(alias, children);
        }
        children.setParent(this);
    }

    public void removeChildren(@NotNull final String alias) { this.commandMap.keySet().removeIf(key -> key.equalsIgnoreCase(alias)); }

    @Nullable
    public CommandNode getChildren(@NotNull final String alias) { return this.commandMap.get(alias); }

    @NotNull
    public Set<CommandNode> getChildrens() { return new HashSet<>(this.commandMap.values()); }

    @NotNull
    public String getLocalized() { return this.localized; }

    @NotNull
    public Map<String, CommandNode> getCommandMap() { return this.commandMap; }
}
