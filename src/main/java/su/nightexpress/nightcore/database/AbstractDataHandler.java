package su.nightexpress.nightcore.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.database.connection.MySQLConnector;
import su.nightexpress.nightcore.database.connection.SQLiteConnector;
import su.nightexpress.nightcore.database.serialize.ItemStackSerializer;
import su.nightexpress.nightcore.database.sql.SQLColumn;
import su.nightexpress.nightcore.database.sql.SQLCondition;
import su.nightexpress.nightcore.database.sql.SQLQueries;
import su.nightexpress.nightcore.database.sql.SQLValue;
import su.nightexpress.nightcore.database.sql.executor.*;
import su.nightexpress.nightcore.manager.SimpleManager;
import su.nightexpress.nightcore.util.wrapper.UniTask;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractDataHandler<P extends NightCorePlugin> extends SimpleManager<P> {

    protected final DatabaseConfig config;
    protected final AbstractConnector connector;
    protected final Gson gson;

    private UniTask syncTask;
    private UniTask saveTask;

    public AbstractDataHandler(@NotNull final P plugin) { this(plugin, DatabaseConfig.read(plugin)); }

    public AbstractDataHandler(@NotNull final P plugin, @NotNull final DatabaseConfig config) {
        super(plugin);

        this.config = config;
        if (this.getDatabaseType() == DatabaseType.MYSQL) {
            this.connector = new MySQLConnector(plugin, config);
        } else {
            this.connector = new SQLiteConnector(plugin, config);
        }
        this.gson = this.registerAdapters(new GsonBuilder().setPrettyPrinting()).create();
    }

    @Override
    protected void onLoad() {
        if (this.getConfig().getSaveInterval() > 0) {
            this.saveTask = this.plugin.createAsyncTask(this::onSave).setSecondsInterval(this.getConfig().getSaveInterval() * 60).start();
        }

        if (this.getConfig().getSyncInterval() > 0 && this.getDatabaseType() != DatabaseType.SQLITE) {
            this.syncTask = this.plugin.createAsyncTask(this::onSynchronize).setSecondsInterval(this.getConfig().getSyncInterval()).start();
            this.plugin.info("Enabled data synchronization with " + this.config.getSyncInterval() + " seconds interval.");
        }

        if (this.getConfig().isPurgeEnabled() && this.getConfig().getPurgePeriod() > 0) {
            this.onPurge();
        }
    }

    @Override
    protected void onShutdown() {
        if (this.syncTask != null)
            this.syncTask.stop();
        if (this.saveTask != null)
            this.saveTask.stop();
        // this.onSynchronize();
        this.onSave();
        this.getConnector().close();
    }

    public abstract void onSynchronize();

    public abstract void onSave();

    public abstract void onPurge();

    @NotNull
    public DatabaseConfig getConfig() { return this.config; }

    @NotNull
    public DatabaseType getDatabaseType() { return this.getConfig().getStorageType(); }

    @NotNull
    public String getTablePrefix() { return this.getConfig().getTablePrefix(); }

    @NotNull
    public AbstractConnector getConnector() { return this.connector; }

    @NotNull
    protected GsonBuilder registerAdapters(@NotNull final GsonBuilder builder) {
        return builder.registerTypeAdapter(ItemStack.class, new ItemStackSerializer());
    }

    @NotNull
    protected final Connection getConnection() throws SQLException { return this.getConnector().getConnection(); }

    public void createTable(@NotNull final String table, @NotNull final List<SQLColumn> columns) {
        CreateTableExecutor.builder(table, this.getDatabaseType()).columns(columns).execute(this.getConnector());
    }

    public void renameTable(@NotNull final String from, @NotNull final String to) {
        RenameTableExecutor.builder(from, this.getDatabaseType()).renameTo(to).execute(this.getConnector());
    }

    public void addColumn(@NotNull final String table, @NotNull final SQLValue... columns) {
        AlterTableExecutor.builder(table, this.getDatabaseType()).addColumn(columns).execute(this.getConnector());
    }

    public void renameColumn(@NotNull final String table, @NotNull final SQLValue... columns) {
        AlterTableExecutor.builder(table, this.getDatabaseType()).renameColumn(columns).execute(this.getConnector());
    }

    public void dropColumn(@NotNull final String table, @NotNull final SQLColumn... columns) {
        AlterTableExecutor.builder(table, this.getDatabaseType()).dropColumn(columns).execute(this.getConnector());
    }

    public boolean hasColumn(@NotNull final String table, @NotNull final SQLColumn column) {
        return SQLQueries.hasColumn(this.getConnector(), table, column);
    }

    public void insert(@NotNull final String table, @NotNull final List<SQLValue> values) {
        InsertQueryExecutor.builder(table).values(values).execute(this.getConnector());
    }

    public void update(@NotNull final String table, @NotNull final List<SQLValue> values, @NotNull final SQLCondition... conditions) {
        UpdateQueryExecutor.builder(table).values(values).where(conditions).execute(this.getConnector());
    }

    public void delete(@NotNull final String table, @NotNull final SQLCondition... conditions) {
        DeleteQueryExecutor.builder(table).where(conditions).execute(this.getConnector());
    }

    public boolean contains(@NotNull final String table, @NotNull final SQLCondition... conditions) {
        return this.load(table, (resultSet -> true), Collections.emptyList(), Arrays.asList(conditions)).isPresent();
    }

    public boolean contains(@NotNull final String table, @NotNull final List<SQLColumn> columns, @NotNull final SQLCondition... conditions) {
        return this.load(table, (resultSet -> true), columns, Arrays.asList(conditions)).isPresent();
    }

    @NotNull
    public <T> Optional<T> load(@NotNull final String table, @NotNull final Function<ResultSet, T> function, @NotNull final List<SQLColumn> columns,
            @NotNull final List<SQLCondition> conditions) {
        final List<T> list = this.load(table, function, columns, conditions, 1);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @NotNull
    public <T> List<T> load(@NotNull final String table, @NotNull final Function<ResultSet, T> dataFunction) {
        return this.load(table, dataFunction, -1);
    }

    @NotNull
    public <T> List<T> load(@NotNull final String table, @NotNull final Function<ResultSet, T> dataFunction, final int amount) {
        return this.load(table, dataFunction, Collections.emptyList(), amount);
    }

    @NotNull
    public <T> List<T> load(@NotNull final String table, @NotNull final Function<ResultSet, T> dataFunction, @NotNull final List<SQLColumn> columns,
            final int amount) {
        return this.load(table, dataFunction, columns, Collections.emptyList(), amount);
    }

    @NotNull
    public <T> List<T> load(@NotNull final String table, @NotNull final Function<ResultSet, T> dataFunction, @NotNull final List<SQLColumn> columns,
            @NotNull final List<SQLCondition> conditions, final int amount) {
        return SelectQueryExecutor.builder(table, dataFunction).columns(columns).where(conditions).execute(this.getConnector());
    }
}
