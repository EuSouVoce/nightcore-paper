package su.nightexpress.nightcore.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nightexpress.nightcore.command.api.NightPluginCommand;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.impl.WrappedCommand;

public class CommandUtil {

    private static final String FIELD_COMMAND_MAP = "commandMap";
    private static final String FIELD_KNOWN_COMMANDS = "knownCommands";

    private static final SimpleCommandMap COMMAND_MAP = CommandUtil.getCommandMap();

    private static SimpleCommandMap getCommandMap() {
        return (SimpleCommandMap) Reflex.getFieldValue(Bukkit.getServer(), CommandUtil.FIELD_COMMAND_MAP);
    }

    public static void register(@NotNull final Plugin plugin, @NotNull final NightPluginCommand command) {
        final WrappedCommand wrappedCommand = new WrappedCommand(plugin, command);
        if (CommandUtil.COMMAND_MAP.register(plugin.getName(), wrappedCommand)) {
            command.setBackend(wrappedCommand);
        }
    }

    public static boolean register(@NotNull final Plugin plugin, @NotNull final WrappedCommand wrappedCommand) {
        return CommandUtil.COMMAND_MAP.register(plugin.getName(), wrappedCommand);
    }

    /*
     * public static void syncCommands() { // Fix tab completer when registerd on
     * runtime Server server = Bukkit.getServer(); Method method =
     * Reflex.getMethod(server.getClass(), "syncCommands"); if (method == null)
     * return; Reflex.invokeMethod(method, server); }
     */

    @SuppressWarnings("unchecked")
    public static boolean unregister(@NotNull final String name) {
        final Command command = CommandUtil.getCommand(name).orElse(null);
        if (command == null)
            return false;

        final Map<String, Command> knownCommands = (HashMap<String, Command>) Reflex.getFieldValue(CommandUtil.COMMAND_MAP,
                CommandUtil.FIELD_KNOWN_COMMANDS);
        if (knownCommands == null)
            return false;
        if (!command.unregister(CommandUtil.COMMAND_MAP))
            return false;

        return knownCommands.keySet().removeIf(key -> key.equalsIgnoreCase(command.getName()) || command.getAliases().contains(key));
    }

    @NotNull
    public static Set<String> getAliases(@NotNull final String name) { return CommandUtil.getAliases(name, false); }

    @NotNull
    public static Set<String> getAliases(@NotNull final String name, final boolean inclusive) {
        final Command command = CommandUtil.getCommand(name).orElse(null);
        if (command == null)
            return Collections.emptySet();

        final Set<String> aliases = new HashSet<>(command.getAliases());
        if (inclusive)
            aliases.add(command.getName());
        return aliases;
    }

    @NotNull
    public static Optional<Command> getCommand(@NotNull final String name) {
        return CommandUtil.COMMAND_MAP.getCommands().stream()
                .filter(command -> command.getLabel().equalsIgnoreCase(name) || command.getAliases().contains(name)).findFirst();
    }

    @NotNull
    public static String getCommandName(@NotNull final String str) {
        String name = Colorizer.strip(str).split(" ")[0].substring(1);

        final String[] pluginPrefix = name.split(":");
        if (pluginPrefix.length == 2) {
            name = pluginPrefix[1];
        }

        return name;
    }

    @Nullable
    public static Player getPlayerOrSender(@NotNull final CommandContext context, @NotNull final ParsedArguments arguments,
            @NotNull final String name) {
        Player player;
        if (arguments.hasArgument(name)) {
            player = arguments.getPlayerArgument(name);
        } else {
            if (context.getExecutor() == null) {
                context.errorPlayerOnly();
                return null;
            }
            player = context.getExecutor();
        }
        return player;
    }
}
