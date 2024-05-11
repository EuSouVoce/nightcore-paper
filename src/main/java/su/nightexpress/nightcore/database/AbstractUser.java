package su.nightexpress.nightcore.database;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nightexpress.nightcore.NightCorePlugin;

public abstract class AbstractUser<P extends NightCorePlugin> implements DataUser {

    protected final P plugin;
    protected final UUID uuid;

    protected String name;
    protected long dateCreated;
    protected long lastOnline;
    protected long cachedUntil;

    public AbstractUser(@NotNull final P plugin, @NotNull final UUID uuid, @NotNull final String name, final long dateCreated,
            final long lastOnline) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.name = name;
        this.setDateCreated(dateCreated);
        this.setLastOnline(lastOnline);
        this.setCachedUntil(-1);
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onUnload() {

    }

    @Override
    public boolean isCacheExpired() { return this.getCachedUntil() > 0 && System.currentTimeMillis() > this.getCachedUntil(); }

    @Override
    public long getCachedUntil() { return this.cachedUntil; }

    @Override
    public void setCachedUntil(final long cachedUntil) { this.cachedUntil = cachedUntil; }

    @Override
    @NotNull
    public final UUID getId() { return this.uuid; }

    @Override
    @NotNull
    public final String getName() { return this.name; }

    @Override
    public void setName(final String name) { this.name = name; }

    @Override
    public final long getDateCreated() { return this.dateCreated; }

    @Override
    public final void setDateCreated(final long dateCreated) { this.dateCreated = dateCreated; }

    @Override
    public final long getLastOnline() { return this.lastOnline; }

    @Override
    public final void setLastOnline(final long lastOnline) { this.lastOnline = lastOnline; }

    @Override
    public final boolean isOnline() { return this.getPlayer() != null; }

    @Override
    @NotNull
    public final OfflinePlayer getOfflinePlayer() { return this.plugin.getServer().getOfflinePlayer(this.getId()); }

    @Override
    @Nullable
    public final Player getPlayer() { return this.plugin.getServer().getPlayer(this.getId()); }

    @Override
    public String toString() { return "AbstractUser [uuid=" + this.uuid + ", name=" + this.name + ", lastOnline=" + this.lastOnline + "]"; }
}
