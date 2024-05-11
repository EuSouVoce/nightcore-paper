package su.nightexpress.nightcore.command.impl;

import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.api.NightPluginCommand;
import su.nightexpress.nightcore.util.text.NightMessage;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WrappedCommand extends Command implements PluginIdentifiableCommand {

    protected final Plugin plugin;
    protected final CommandExecutor executor;
    protected final TabCompleter tabCompleter;

    public WrappedCommand(@NotNull final Plugin plugin, @NotNull final NightPluginCommand command) {
        this(plugin, command, command, command.getAliases(), command.getDescription(), command.getUsage());
        this.setPermission(command.getPermission());
    }

    public WrappedCommand(@NotNull final Plugin plugin, @NotNull final CommandExecutor executor, @NotNull final TabCompleter tabCompleter,
            @NotNull final String[] aliases, @NotNull final String description, @NotNull final String usage) {
        /*
         * super(aliases[0], description, usage, Arrays.asList(aliases)); this.plugin =
         * plugin; this.executor = executor; this.tabCompleter = tabCompleter;
         */
        this(plugin, executor, tabCompleter, aliases[0], aliases, description, usage);
    }

    public WrappedCommand(@NotNull final Plugin plugin, @NotNull final CommandExecutor executor, @NotNull final TabCompleter tabCompleter,
            @NotNull final String name, @NotNull final String[] aliases, @NotNull final String description, @NotNull final String usage) {
        super(name, NightMessage.clean(description), NightMessage.clean(usage), Arrays.asList(aliases));
        this.plugin = plugin;
        this.executor = executor;
        this.tabCompleter = tabCompleter;
    }

    @Override
    @NotNull
    public Plugin getPlugin() { return this.plugin; }

    @Override
    public boolean execute(@NotNull final CommandSender sender, @NotNull final String label, @NotNull final String[] args) {
        return this.executor.onCommand(sender, this, label, args);
    }

    @Override
    @NotNull
    public List<String> tabComplete(@NotNull final CommandSender sender, @NotNull final String alias, @NotNull final String[] args) {
        final List<String> list = this.tabCompleter.onTabComplete(sender, this, alias, args);
        return list == null ? Collections.emptyList() : list;
    }
}
