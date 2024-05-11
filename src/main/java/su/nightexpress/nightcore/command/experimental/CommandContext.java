package su.nightexpress.nightcore.command.experimental;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.language.message.LangMessage;

public class CommandContext {

    private final NightCorePlugin plugin;
    private final CommandSender sender;
    private final Player executor;
    private final String label;
    private final String[] args;

    private int argumentIndex;

    public CommandContext(@NotNull final NightCorePlugin plugin, @NotNull final CommandSender sender, @NotNull final String label,
            final String[] args) {
        this.plugin = plugin;
        this.sender = sender;
        this.executor = sender instanceof final Player player ? player : null;
        this.label = label;
        this.args = args;
        this.argumentIndex = 0;
    }

    public void send(@NotNull final String string) { this.sender.sendMessage(string); }

    public boolean sendSuccess(@NotNull final String string) {
        this.send(string);
        return true;
    }

    public boolean sendFailure(@NotNull final String string) {
        this.send(string);
        return false;
    }

    public void send(@NotNull final LangMessage message) { message.send(this.sender); }

    public boolean sendSuccess(@NotNull final LangMessage message) {
        this.send(message);
        return true;
    }

    public boolean sendFailure(@NotNull final LangMessage message) {
        this.send(message);
        return false;
    }

    public boolean checkPermission(@NotNull final Permission permission) { return this.sender.hasPermission(permission); }

    public boolean checkPermission(@NotNull final String permission) { return this.sender.hasPermission(permission); }

    public boolean isPlayer() { return this.executor != null; }

    public int getArgumentIndex() { return this.argumentIndex; }

    public void setArgumentIndex(final int argumentIndex) { this.argumentIndex = argumentIndex; }

    @NotNull
    public CommandSender getSender() { return this.sender; }

    @Nullable
    public Player getExecutor() { return this.executor; }

    public int length() { return this.args.length; }

    @NotNull
    public String getLabel() { return this.label; }

    public String[] getArgs() { return this.args; }

    public void errorPermission() { this.send(CoreLang.ERROR_NO_PERMISSION.getMessage(this.plugin)); }

    public void errorBadPlayer() { this.send(CoreLang.ERROR_INVALID_PLAYER.getMessage(this.plugin)); }

    public void errorPlayerOnly() { this.send(CoreLang.ERROR_COMMAND_PLAYER_ONLY.getMessage(this.plugin)); }
}
