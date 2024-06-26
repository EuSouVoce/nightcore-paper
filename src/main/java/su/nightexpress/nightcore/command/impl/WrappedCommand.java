package su.nightexpress.nightcore.command.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nightexpress.nightcore.command.api.NightPluginCommand;
import su.nightexpress.nightcore.util.text.NightMessage;

public class WrappedCommand extends Command implements PluginIdentifiableCommand {

    protected final Plugin plugin;
    protected final CommandExecutor executor;
    protected final TabCompleter tabCompleter;

    public WrappedCommand(@NotNull final Plugin plugin, @NotNull final NightPluginCommand command) {
        this(plugin, command, command, command.getAliases(), command.getDescription(), command.getUsage(), command.getPermission());
        // this.setPermission(command.getPermission());
    }

    public WrappedCommand(@NotNull final Plugin plugin, @NotNull final CommandExecutor executor, @NotNull final TabCompleter tabCompleter,
            @NotNull final String[] aliases, @NotNull final String description, @NotNull final String usage, @Nullable final String permission) {
        /*
         * super(aliases[0], description, usage, Arrays.asList(aliases)); this.plugin =
         * plugin; this.executor = executor; this.tabCompleter = tabCompleter;
         */
        this(plugin, executor, tabCompleter, aliases[0], aliases, description, usage, permission);
    }

    public WrappedCommand(@NotNull final Plugin plugin, @NotNull final CommandExecutor executor, @NotNull final TabCompleter tabCompleter,
            @NotNull final String name, @NotNull final String[] aliases, @NotNull final String description, @NotNull final String usage,
            @Nullable final String permission) {
        super(name, NightMessage.clean(description), NightMessage.clean(usage), Arrays.asList(aliases));
        this.plugin = plugin;
        this.executor = executor;
        this.tabCompleter = tabCompleter;
        this.setPermission(permission);
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
