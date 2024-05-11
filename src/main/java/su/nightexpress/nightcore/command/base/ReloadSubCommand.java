package su.nightexpress.nightcore.command.base;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.command.CommandResult;
import su.nightexpress.nightcore.command.impl.AbstractCommand;
import su.nightexpress.nightcore.core.CoreLang;

public class ReloadSubCommand extends AbstractCommand<NightCorePlugin> {

    public ReloadSubCommand(@NotNull final NightCorePlugin plugin, @NotNull final Permission permission) { this(plugin, permission.getName()); }

    public ReloadSubCommand(@NotNull final NightCorePlugin plugin, @NotNull final String permission) {
        super(plugin, new String[] { "reload" }, permission);
        this.setDescription(CoreLang.COMMAND_RELOAD_DESC);
    }

    @Override
    protected void onExecute(@NotNull final CommandSender sender, @NotNull final CommandResult result) {
        this.plugin.reload();
        CoreLang.COMMAND_RELOAD_DONE.getMessage(this.plugin).send(sender);
    }
}
