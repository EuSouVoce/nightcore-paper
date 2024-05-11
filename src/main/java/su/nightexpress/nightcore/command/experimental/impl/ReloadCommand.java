package su.nightexpress.nightcore.command.experimental.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.node.ChainedNode;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.util.wrapper.UniPermission;

public class ReloadCommand {

    public static void inject(@NotNull final NightCorePlugin plugin, @NotNull final ChainedNode node, @NotNull final UniPermission permission) {
        node.addChildren(DirectNode.builder(plugin, "reload").permission(permission).description(CoreLang.COMMAND_RELOAD_DESC)
                .executes((context, arguments) -> ReloadCommand.execute(plugin, context, arguments)));
    }

    public static boolean execute(@NotNull final NightCorePlugin plugin, @NotNull final CommandContext context, @NotNull final ParsedArguments arguments) {
        plugin.reload();
        return context.sendSuccess(CoreLang.COMMAND_RELOAD_DONE.getMessage(plugin));
    }
}
