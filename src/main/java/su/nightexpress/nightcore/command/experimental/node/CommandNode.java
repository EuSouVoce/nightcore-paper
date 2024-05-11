package su.nightexpress.nightcore.command.experimental.node;

import java.util.List;
import java.util.stream.Stream;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.TabContext;

public abstract class CommandNode implements NodeExecutor {
    // @formatter:off
    protected final NightCorePlugin plugin;
    protected final String          name;
    protected final String[]        aliases;
    protected final String          description;
    protected final String          permission;
    protected final boolean         playerOnly;
    // @formatter:on
    protected CommandNode parent;

    public CommandNode(@NotNull final NightCorePlugin plugin, @NotNull final String name, @NotNull final String[] aliases,
            @NotNull final String description, @Nullable final String permission, final boolean playerOnly) {
        this.plugin = plugin;
        this.name = name.toLowerCase();
        this.aliases = Stream.of(aliases).map(String::toLowerCase).toArray(String[]::new);
        this.description = description;
        this.permission = permission;
        this.playerOnly = playerOnly;
    }

    @NotNull
    public abstract List<String> getTab(@NotNull TabContext context);

    @Override
    public boolean run(@NotNull final CommandContext context) {
        if (this.isPlayerOnly() && !(context.getSender() instanceof Player)) {
            context.errorPlayerOnly();
            return false;
        }
        if (!this.hasPermission(context.getSender())) {
            context.errorPermission();
            return false;
        }

        return this.onRun(context);
    }

    protected abstract boolean onRun(@NotNull CommandContext context);

    public boolean hasPermission(@NotNull final CommandSender sender) {
        return this.permission == null || sender.hasPermission(this.permission);
    }

    @NotNull
    public String getNameWithParents() {
        final StringBuilder builder = new StringBuilder();

        CommandNode parent = this.getParent();
        while (parent != null) {
            if (!builder.isEmpty()) {
                builder.insert(0, " ");
            }
            builder.insert(0, parent.getName());
            parent = parent.getParent();
        }

        builder.append(" ").append(this.getName());

        return builder.toString().strip();
    }

    @NotNull
    public String getName() { return this.name; }

    @NotNull
    public String[] getAliases() { return this.aliases; }

    @NotNull
    public String getUsage() { return ""; }

    @NotNull
    public String getDescription() { return this.description; }

    @Nullable
    public CommandNode getParent() { return this.parent; }

    protected void setParent(@Nullable final CommandNode parent) { this.parent = parent; }

    @Nullable
    public String getPermission() { return this.permission; }

    public boolean isPlayerOnly() { return this.playerOnly; }
}
