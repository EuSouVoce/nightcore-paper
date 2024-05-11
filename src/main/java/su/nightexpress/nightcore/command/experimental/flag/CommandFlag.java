package su.nightexpress.nightcore.command.experimental.flag;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.util.Placeholders;

public abstract class CommandFlag {

    public static final char PREFIX = '-';
    public static final char DELIMITER = '=';

    private final String name;
    private final String permission;

    public CommandFlag(@NotNull final String name, @Nullable final String permission) {
        this.name = name.toLowerCase();
        this.permission = permission;
    }

    public boolean hasPermission(@NotNull final CommandSender sender) { return this.permission == null || sender.hasPermission(this.permission); }

    @NotNull
    public String getName() { return this.name; }

    @NotNull
    public String getPrefixed() { return CommandFlag.PREFIX + this.getName(); }

    @NotNull
    public String getPrefixedFormatted() {
        return CoreLang.COMMAND_FLAG_FORMAT.getString().replace(Placeholders.GENERIC_NAME, this.getPrefixed());
    }

    @Nullable
    public String getPermission() { return this.permission; }

    @Override
    public String toString() { return "CommandFlag{" + "name='" + this.name + '\'' + '}'; }
}
