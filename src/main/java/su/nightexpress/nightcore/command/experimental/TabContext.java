package su.nightexpress.nightcore.command.experimental;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TabContext {

    private final CommandSender sender;
    private final Player player;
    private final String label;
    private final String[] args;

    private int index;

    public TabContext(@NotNull final CommandSender sender, @NotNull final String label, final String[] args, final int index) {
        this.sender = sender;
        this.player = sender instanceof final Player user ? user : null;
        this.label = label;
        this.args = args;
        this.index = index;
    }

    @NotNull
    public CommandSender getSender() { return this.sender; }

    @Nullable
    public Player getPlayer() { return this.player; }

    @NotNull
    public String getLabel() { return this.label; }

    @NotNull
    public String[] getArgs() { return this.args; }

    public int getIndex() { return this.index; }

    public void setIndex(final int index) { this.index = index; }

    public String getAtIndex() { return this.args[this.index]; }

    public String getInput() { return this.args[this.args.length - 1]; }
}
