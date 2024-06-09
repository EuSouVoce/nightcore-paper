package su.nightexpress.nightcore.database;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nightexpress.nightcore.NightDataPlugin;
import su.nightexpress.nightcore.core.CoreConfig;
import su.nightexpress.nightcore.database.listener.UserListener;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.Players;

public abstract class AbstractUserManager<P extends NightDataPlugin<U>, U extends DataUser> extends AbstractManager<P> {

    private final Map<UUID, U> loadedById;
    private final Map<String, U> loadedByName;
    private final Set<U> scheduledSaves;

    public AbstractUserManager(@NotNull final P plugin) {
        super(plugin);
        this.loadedById = new ConcurrentHashMap<>();
        this.loadedByName = new ConcurrentHashMap<>();
        this.scheduledSaves = ConcurrentHashMap.newKeySet();
    }

    @Override
    protected void onLoad() {
        this.addListener(new UserListener<>(this.plugin));
        this.addTask(this.plugin.createAsyncTask(this::saveScheduled).setTicksInterval(1));
    }

    @Override
    protected void onShutdown() {
        this.getLoaded().forEach(this::save);
        this.getLoadedByIdMap().clear();
        this.getLoadedByNameMap().clear();
        this.scheduledSaves.clear();
    }

    @NotNull
    public abstract U createUserData(@NotNull UUID uuid, @NotNull String name);

    public void loadOnlineUsers() {
        this.plugin.getServer().getOnlinePlayers().stream().map(Player::getUniqueId).forEach(id -> {
            final U user = this.getUserData(id);
            if (user != null)
                this.cachePermanent(user);
        });
    }

    public void saveScheduled() {
        this.scheduledSaves.forEach(this::save);
        this.scheduledSaves.clear();
    }

    @NotNull
    public final U getUserData(@NotNull final Player player) {
        final UUID uuid = player.getUniqueId();

        U user = this.getLoaded(uuid);
        if (user != null)
            return user;

        if (Players.isReal(player)) {
            user = this.getUserData(uuid);
            if (user != null) {
                if (CoreConfig.USER_DEBUG_ENABLED.get()) {
                    new Throwable().printStackTrace();
                    this.plugin.warn("Main thread user data load for '" + uuid + "' aka '" + player.getName() + "'.");
                }
                return user;
            }
        }

        return this.createUserData(uuid, player.getName());
    }

    @Nullable
    public final U getUserData(@NotNull final String name) {
        final Player player = Players.getPlayer(name);
        if (player != null)
            return this.getUserData(player);

        U user = this.getLoaded(name);
        if (user != null)
            return user;

        user = this.plugin.getData().getUser(name);
        if (user != null) {
            user.onLoad();
            // this.plugin.debug("Loaded by name from DB: " + user.getName());
            this.cacheTemporary(user);
        }

        return user;
    }

    @Nullable
    public final U getUserData(@NotNull final UUID uuid) {
        U user = this.getLoaded(uuid);
        if (user != null)
            return user;

        user = this.plugin.getData().getUser(uuid);
        if (user != null) {
            user.onLoad();
            // this.plugin.debug("Loaded by UUID from DB: " + user.getName());
            this.cacheTemporary(user);
        }

        return user;
    }

    public final CompletableFuture<U> getUserDataAsync(@NotNull final String name) {
        return CompletableFuture.supplyAsync(() -> this.getUserData(name));
    }

    public final CompletableFuture<U> getUserDataAsync(@NotNull final UUID uuid) {
        return CompletableFuture.supplyAsync(() -> this.getUserData(uuid));
    }

    /**
     * Performs an operation on the given user.<br>
     * Runs immediately in the current thread if player is online or data is already
     * loaded.<br>
     * Otherwise fetches player data asynchronously and performs an operation in
     * async CompletableFuture thread.
     * 
     * @param name Name of a player.
     */
    public void manageUser(@NotNull final String name, final Consumer<U> consumer) {
        this.manageUser(() -> this.getLoaded(name), () -> this.getUserDataAsync(name), consumer);
    }

    /**
     * Performs an operation on the given user.<br>
     * Runs immediately in the current thread if player is online or data is already
     * loaded.<br>
     * Otherwise fetches player data asynchronously and performs an operation in
     * async CompletableFuture thread.
     * 
     * @param playerId UUID of a player.
     */
    public void manageUser(@NotNull final UUID playerId, final Consumer<U> consumer) {
        this.manageUser(() -> this.getLoaded(playerId), () -> this.getUserDataAsync(playerId), consumer);
    }

    /**
     * Performs an operation on the given user.<br>
     * Runs immediately in the current thread if player is online or data is already
     * loaded.<br>
     * Otherwise fetches player data asynchronously and performs an operation next
     * tick in the main thread.
     * 
     * @param name Name of a player.
     */
    public void manageUserSynchronized(@NotNull final String name, final Consumer<U> consumer) {
        this.manageUserSynchronized(() -> this.getLoaded(name), () -> this.getUserDataAsync(name), consumer);
    }

    /**
     * Performs an operation on the given user.<br>
     * Runs immediately in the current thread if player is online or data is already
     * loaded.<br>
     * Otherwise fetches player data asynchronously and performs an operation next
     * tick in the main thread.
     * 
     * @param playerId UUID of a player.
     */
    public void manageUserSynchronized(@NotNull final UUID playerId, final Consumer<U> consumer) {
        this.manageUserSynchronized(() -> this.getLoaded(playerId), () -> this.getUserDataAsync(playerId), consumer);
    }

    private void manageUser(@NotNull final Supplier<U> loadedSupplier, @NotNull final Supplier<CompletableFuture<U>> fetchSupplier,
            @NotNull final Consumer<U> consumer) {
        final U user = loadedSupplier.get();
        if (user != null) {
            consumer.accept(user);
        } else
            fetchSupplier.get().thenAccept(consumer);
    }

    private void manageUserSynchronized(@NotNull final Supplier<U> loadedSupplier, @NotNull final Supplier<CompletableFuture<U>> fetchSupplier,
            @NotNull final Consumer<U> consumer) {
        this.manageUser(loadedSupplier, fetchSupplier, user -> this.plugin.runTask(task -> consumer.accept(user)));
    }

    @Deprecated
    public void getUserDataAndPerform(@NotNull final String name, final Consumer<U> consumer) {
        this.manageUserSynchronized(name, consumer);
        /*
         * U user = this.getLoaded(name); if (user != null) { consumer.accept(user); }
         * else this.getUserDataAsync(name).thenAccept(user2 -> this.plugin.runTask(task
         * -> consumer.accept(user2)));
         */
    }

    @Deprecated
    public void getUserDataAndPerform(@NotNull final UUID uuid, final Consumer<U> consumer) {
        this.manageUserSynchronized(uuid, consumer);
        /*
         * U user = this.getLoaded(uuid); if (user != null) { consumer.accept(user); }
         * else this.getUserDataAsync(uuid).thenAccept(user2 -> this.plugin.runTask(task
         * -> consumer.accept(user2)));
         */
    }

    @Deprecated
    public void getUserDataAndPerformAsync(@NotNull final String name, final Consumer<U> consumer) {
        this.manageUser(name, consumer);
        /*
         * U user = this.getLoaded(name); if (user != null) { consumer.accept(user); }
         * else this.getUserDataAsync(name).thenAccept(consumer);
         */
    }

    @Deprecated
    public void getUserDataAndPerformAsync(@NotNull final UUID uuid, final Consumer<U> consumer) {
        this.manageUser(uuid, consumer);
        /*
         * U user = this.getLoaded(uuid); if (user != null) { consumer.accept(user); }
         * else this.getUserDataAsync(uuid).thenAccept(consumer);
         */
    }

    public final void unload(@NotNull final Player player) { this.unload(player.getUniqueId()); }

    public final void unload(@NotNull final UUID uuid) {
        final U user = this.getLoadedByIdMap().get(uuid);
        if (user == null)
            return;

        this.unload(user);
    }

    public void unload(@NotNull final U user) {
        final Player player = user.getPlayer();
        if (player != null) {
            user.setName(player.getName());
            user.setLastOnline(System.currentTimeMillis());
        }
        this.scheduleSave(user);
        this.cacheTemporary(user);
    }

    public void save(@NotNull final U user) { this.plugin.getData().saveUser(user); }

    public void scheduleSave(@NotNull final U user) { this.scheduledSaves.add(user); }

    @Deprecated
    public void saveAsync(@NotNull final U user) { this.scheduleSave(user); }

    @NotNull
    public Set<U> getAllUsers() {
        final Map<UUID, U> users = new HashMap<>();
        this.getLoaded().forEach(user -> users.put(user.getId(), user));
        this.plugin.getData().getUsers().forEach(user -> {
            users.putIfAbsent(user.getId(), user);
        });
        return new HashSet<>(users.values());
    }

    @NotNull
    public Map<UUID, U> getLoadedByIdMap() {
        this.removeExpired(this.loadedById.values());

        return this.loadedById;
    }

    @NotNull
    public Map<String, U> getLoadedByNameMap() {
        this.removeExpired(this.loadedByName.values());

        return this.loadedByName;
    }

    private void removeExpired(@NotNull final Collection<U> collection) {
        collection.removeIf(user -> {
            if (user.isCacheExpired()) {
                user.onUnload();
                // this.plugin.debug("Cache expired: " + user.getName());
                return true;
            }
            return false;
        });
    }

    @NotNull
    public Set<U> getLoaded() { return new HashSet<>(this.getLoadedByIdMap().values()); }

    @Nullable
    public U getLoaded(@NotNull final UUID uuid) { return this.getLoadedByIdMap().get(uuid); }

    @Nullable
    public U getLoaded(@NotNull final String name) { return this.getLoadedByNameMap().get(name.toLowerCase()); }

    public boolean isLoaded(@NotNull final Player player) { return this.isLoaded(player.getUniqueId()); }

    public boolean isLoaded(@NotNull final UUID id) { return this.getLoadedByIdMap().containsKey(id); }

    public boolean isLoaded(@NotNull final String name) { return this.getLoadedByNameMap().containsKey(name.toLowerCase()); }

    public boolean isCreated(@NotNull final String name) { return this.plugin.getData().isUserExists(name); }

    public boolean isCreated(@NotNull final UUID uuid) { return this.plugin.getData().isUserExists(uuid); }

    public void cacheTemporary(@NotNull final U user) {
        user.setCachedUntil(System.currentTimeMillis() + CoreConfig.USER_CACHE_LIFETIME.get() * 1000L);
        this.cache(user);
        // this.plugin.debug("Temp user cache: " + user.getName());
    }

    public void cachePermanent(@NotNull final U user) {
        user.setCachedUntil(-1);
        this.cache(user);
        // this.plugin.debug("Permanent user cache: " + user.getName());
    }

    private void cache(@NotNull final U user) {
        this.getLoadedByIdMap().putIfAbsent(user.getId(), user);
        this.getLoadedByNameMap().putIfAbsent(user.getName().toLowerCase(), user);
    }
}
