package su.nightexpress.nightcore.database.listener;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.NightDataPlugin;
import su.nightexpress.nightcore.core.CoreConfig;
import su.nightexpress.nightcore.database.AbstractUserManager;
import su.nightexpress.nightcore.database.DataUser;
import su.nightexpress.nightcore.manager.AbstractListener;

public class UserListener<P extends NightDataPlugin<U>, U extends DataUser> extends AbstractListener<P> {

    private final AbstractUserManager<? extends NightDataPlugin<U>, U> userManager;

    public UserListener(@NotNull final P plugin) {
        super(plugin);
        this.userManager = plugin.getUserManager();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onUserLogin(final AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED)
            return;

        final UUID uuid = event.getUniqueId();
        U user;
        if (!this.userManager.isCreated(uuid)) {
            user = this.userManager.createUserData(uuid, event.getName());
            this.plugin.getData().addUser(user);
            if (CoreConfig.USER_DEBUG_ENABLED.get()) {
                this.plugin.info("Created new data for: '" + uuid + "'");
            }
        } else
            user = this.userManager.getUserData(uuid);

        if (user != null) {
            this.userManager.cachePermanent(user);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onUserQuit(final PlayerQuitEvent event) { this.userManager.unload(event.getPlayer()); }
}
