package su.nightexpress.nightcore.core.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.NightCore;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.node.ChainedNode;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.core.CorePerms;
import su.nightexpress.nightcore.integration.VaultHook;
import su.nightexpress.nightcore.util.Colorizer;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.nightcore.util.text.tag.Tags;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class CheckPermCommand {

    private static final String ARG_PLAYER = "player";

    public static void inject(@NotNull final NightCore plugin, @NotNull final ChainedNode node) {
        node.addChildren(DirectNode.builder(plugin, "checkperm").permission(CorePerms.COMMAND_CHECK_PERM)
                .description(CoreLang.COMMAND_CHECKPERM_DESC).withArgument(ArgumentTypes.player(CheckPermCommand.ARG_PLAYER).required())
                .executes(CheckPermCommand::execute));
    }

    public static boolean execute(@NotNull final CommandContext context, @NotNull final ParsedArguments arguments) {
        final Player player = arguments.getPlayerArgument(CheckPermCommand.ARG_PLAYER);
        final String builder = Tags.BOLD.enclose(Tags.LIGHT_YELLOW.enclose("Permissions report for ") + Tags.LIGHT_ORANGE.enclose(player.getName() + ":"))
                + Tags.LIGHT_ORANGE
                        .enclose("▪ " + Tags.LIGHT_YELLOW.enclose("Primary Group: ") + Colorizer.plain(VaultHook.getPermissionGroup(player)))
                + Tags.LIGHT_ORANGE.enclose("▪ " + Tags.LIGHT_YELLOW.enclose("All Groups: ")
                        + Colorizer.plain(String.join(", ", VaultHook.getPermissionGroups(player))))
                + Tags.LIGHT_ORANGE.enclose("▪ " + Tags.LIGHT_YELLOW.enclose("Prefix: ") + VaultHook.getPrefix(player))
                + Tags.LIGHT_ORANGE.enclose("▪ " + Tags.LIGHT_YELLOW.enclose("Suffix: ") + VaultHook.getSuffix(player));
        NightMessage.create(builder).send(context.getSender());
        return true;
    }
}
