package su.nightexpress.nightcore.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.database.serialize.ItemStackSerializer;
import su.nightexpress.nightcore.database.sql.SQLColumn;
import su.nightexpress.nightcore.database.sql.SQLCondition;
import su.nightexpress.nightcore.database.sql.SQLQueries;
import su.nightexpress.nightcore.database.sql.SQLValue;
import su.nightexpress.nightcore.database.sql.executor.AlterTableExecutor;
import su.nightexpress.nightcore.database.sql.executor.CreateTableExecutor;
import su.nightexpress.nightcore.database.sql.executor.DeleteQueryExecutor;
import su.nightexpress.nightcore.database.sql.executor.InsertQueryExecutor;
import su.nightexpress.nightcore.database.sql.executor.RenameTableExecutor;
import su.nightexpress.nightcore.database.sql.executor.SelectQueryExecutor;
import su.nightexpress.nightcore.database.sql.executor.UpdateQueryExecutor;
import su.nightexpress.nightcore.manager.AbstractManager;

public abstract class AbstractDataHandler<P extends NightCorePlugin> extends AbstractManager<P> {

    protected final DatabaseConfig config;
    protected final AbstractConnector connector;
    protected final Gson gson;

    public AbstractDataHandler(@NotNull final P plugin) { this(plugin, AbstractDataHandler.getDataConfig(plugin)); }

    public AbstractDataHandler(@NotNull final P plugin, @NotNull final DatabaseConfig config) {
        super(plugin);
        this.config = config;
        this.connector = AbstractConnector.create(plugin, config);
        this.gson = this.registerAdapters(new GsonBuilder().setPrettyPrinting()).create();
    }

    @NotNull
    protected static DatabaseConfig getDataConfig(@NotNull final NightCorePlugin plugin) {
        DatabaseConfig dataConfig = plugin.getDetails().getDatabaseConfig();
        if (dataConfig == null) {
            plugin.warn("The plugin didn't have database configuration. Fixing it now...");
            dataConfig = DatabaseConfig.read(plugin);
        }
        return dataConfig;
    }

    @Override
    protected void onLoad() {
        if (this.config.getSaveInterval() > 0) {
            this.addTask(this.plugin.createAsyncTask(this::onSave).setSecondsInterval(this.config.getSaveInterval() * 60));
        }

        if (this.config.getSyncInterval() > 0 && this.getDatabaseType() != DatabaseType.SQLITE) {
            this.addTask(this.plugin.createAsyncTask(this::onSynchronize).setSecondsInterval(this.config.getSyncInterval()));
            this.plugin.info("Enabled data synchronization with " + this.config.getSyncInterval() + " seconds interval.");
        }

        if (this.config.isPurgeEnabled() && this.config.getPurgePeriod() > 0) {
            this.onPurge();
        }
    }

    @Override
    protected void onShutdown() {
        this.onSave();
        this.getConnector().close();
    }

    public abstract void onSynchronize();

    public abstract void onSave();

    public abstract void onPurge();

    @NotNull
    public DatabaseConfig getConfig() { return this.config; }

    @NotNull
    public DatabaseType getDatabaseType() { return this.config.getStorageType(); }

    @NotNull
    public String getTablePrefix() { return this.config.getTablePrefix(); }

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

    public boolean contains(@NotNull final String table, @NotNull final List<SQLColumn> columns,
            @NotNull final SQLCondition... conditions) {
        return this.load(table, (resultSet -> true), columns, Arrays.asList(conditions)).isPresent();
    }

    @NotNull
    public <T> Optional<T> load(@NotNull final String table, @NotNull final Function<ResultSet, T> function,
            @NotNull final List<SQLColumn> columns, @NotNull final List<SQLCondition> conditions) {
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
    public <T> List<T> load(@NotNull final String table, @NotNull final Function<ResultSet, T> dataFunction,
            @NotNull final List<SQLColumn> columns, final int amount) {
        return this.load(table, dataFunction, columns, Collections.emptyList(), amount);
    }

    @NotNull
    public <T> List<T> load(@NotNull final String table, @NotNull final Function<ResultSet, T> dataFunction,
            @NotNull final List<SQLColumn> columns, @NotNull final List<SQLCondition> conditions, final int amount) {
        return SelectQueryExecutor.builder(table, dataFunction).columns(columns).where(conditions).execute(this.getConnector());
    }
}
