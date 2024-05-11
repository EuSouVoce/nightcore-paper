package su.nightexpress.nightcore.command.base;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.command.CommandResult;
import su.nightexpress.nightcore.command.api.NightCommand;
import su.nightexpress.nightcore.command.impl.AbstractCommand;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.util.Placeholders;

public class HelpSubCommand extends AbstractCommand<NightCorePlugin> {

    public HelpSubCommand(@NotNull final NightCorePlugin plugin) {
        super(plugin, new String[] { "help" });
        this.setDescription(CoreLang.COMMAND_HELP_DESC);
    }

    @Override
    protected void onExecute(@NotNull final CommandSender sender, @NotNull final CommandResult result) {
        final NightCommand parent = this.getParent();
        if (parent == null)
            return;

        if (!parent.hasPermission(sender)) {
            this.errorPermission(sender);
            return;
        }

        CoreLang.COMMAND_HELP_LIST.getMessage().replace(Placeholders.GENERIC_NAME, this.plugin.getNameLocalized())
                .replace(Placeholders.GENERIC_ENTRY, list -> {
                    final Set<NightCommand> commands = new HashSet<>(parent.getChildrens());

                    commands.stream().sorted(Comparator.comparing(command -> command.getAliases()[0])).forEach(children -> {
                        if (!children.hasPermission(sender))
                            return;

                        list.add(children.replacePlaceholders().apply(CoreLang.COMMAND_HELP_ENTRY.getString()));
                    });
                }).send(sender);
    }
}
