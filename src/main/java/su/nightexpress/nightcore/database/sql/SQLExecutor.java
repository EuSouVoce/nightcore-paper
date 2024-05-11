package su.nightexpress.nightcore.database.sql;

import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.database.AbstractConnector;

public abstract class SQLExecutor<T> {

    protected final String table;

    protected SQLExecutor(@NotNull final String table) { this.table = table; }

    @NotNull
    public String getTable() { return "`" + this.table + "`"; }

    @NotNull
    public abstract T execute(@NotNull AbstractConnector connector);
}
