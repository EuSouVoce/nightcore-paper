package su.nightexpress.nightcore.dialog;

import java.util.HashSet;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.NightCore;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.nightcore.util.Colorizer;
import su.nightexpress.nightcore.util.NumberUtil;

public class DialogListener extends AbstractListener<NightCore> {

    // TODO Timeout

    public DialogListener(@NotNull final NightCore plugin) { super(plugin); }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(final PlayerQuitEvent event) { Dialog.stop(event.getPlayer()); }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChatText(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final Dialog dialog = Dialog.get(player);
        if (dialog == null)
            return;

        event.getRecipients().clear();
        event.setCancelled(true);

        final WrappedInput input = new WrappedInput(event);

        this.plugin.runTask(task -> {
            if (input.getTextRaw().equalsIgnoreCase(Dialog.EXIT) || dialog.getHandler().onInput(dialog, input)) {
                Dialog.stop(player);
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChatCommand(final PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        final Dialog dialog = Dialog.get(player);
        if (dialog == null)
            return;

        event.setCancelled(true);

        final String raw = event.getMessage();
        final String text = Colorizer.apply(raw.substring(1));
        if (text.startsWith(Dialog.VALUES)) {
            final String[] split = text.split(" ");
            final int page = split.length >= 2 ? NumberUtil.getInteger(split[1], 0) : 0;
            final boolean auto = split.length >= 3 && Boolean.parseBoolean(split[2]);
            dialog.displaySuggestions(auto, page);
            return;
        }

        final AsyncPlayerChatEvent chatEvent = new AsyncPlayerChatEvent(true, player, text, new HashSet<>());
        final WrappedInput input = new WrappedInput(chatEvent);

        this.plugin.runTask(task -> {
            if (input.getTextRaw().equalsIgnoreCase(Dialog.EXIT) || dialog.getHandler().onInput(dialog, input)) {
                Dialog.stop(player);
            }
        });
    }
}
