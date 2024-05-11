package su.nightexpress.nightcore.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.menu.api.Menu;

public class PlayerOpenMenuEvent extends Event implements Cancellable {

    public static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final Menu menu;

    private boolean cancelled;

    public PlayerOpenMenuEvent(@NotNull final Player player, @NotNull final Menu menu) {
        this.player = player;
        this.menu = menu;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() { return PlayerOpenMenuEvent.HANDLER_LIST; }

    public static HandlerList getHandlerList() { return PlayerOpenMenuEvent.HANDLER_LIST; }

    @NotNull
    public Player getPlayer() { return this.player; }

    @NotNull
    public Menu getMenu() { return this.menu; }

    @Override
    public boolean isCancelled() { return this.cancelled; }

    @Override
    public void setCancelled(final boolean cancelled) { this.cancelled = cancelled; }
}
