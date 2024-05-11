package su.nightexpress.nightcore.database.sql.executor;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.database.DatabaseType;
import su.nightexpress.nightcore.database.AbstractConnector;
import su.nightexpress.nightcore.database.connection.SQLiteConnector;
import su.nightexpress.nightcore.database.sql.SQLColumn;
import su.nightexpress.nightcore.database.sql.SQLExecutor;
import su.nightexpress.nightcore.database.sql.SQLQueries;
import su.nightexpress.nightcore.database.sql.SQLValue;
import su.nightexpress.nightcore.database.sql.column.ColumnType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class AlterTableExecutor extends SQLExecutor<Void> {

    private final DatabaseType databaseType;
    private final List<SQLValue> columns;
    private Type type;

    private AlterTableExecutor(@NotNull final String table, @NotNull final DatabaseType databaseType) {
        super(table);
        this.databaseType = databaseType;
        this.columns = new ArrayList<>();
    }

    private enum Type {
        ADD_COLUMN, RENAME_COLUMN, DROP_COLUMN
    }

    @NotNull
    public static AlterTableExecutor builder(@NotNull final String table, @NotNull final DatabaseType databaseType) {
        return new AlterTableExecutor(table, databaseType);
    }

    @NotNull
    public AlterTableExecutor addColumn(@NotNull final SQLValue... columns) { return this.addColumn(Arrays.asList(columns)); }

    @NotNull
    public AlterTableExecutor addColumn(@NotNull final List<SQLValue> columns) { return this.columns(columns, Type.ADD_COLUMN); }

    @NotNull
    public AlterTableExecutor renameColumn(@NotNull final SQLValue... columns) { return this.addColumn(Arrays.asList(columns)); }

    @NotNull
    public AlterTableExecutor renameColumn(@NotNull final List<SQLValue> columns) { return this.columns(columns, Type.RENAME_COLUMN); }

    @NotNull
    public AlterTableExecutor dropColumn(@NotNull final SQLColumn... columns) { return this.dropColumn(Arrays.asList(columns)); }

    @NotNull
    public AlterTableExecutor dropColumn(@NotNull final List<SQLColumn> columns) {
        return this.columns(columns.stream().map(column -> column.toValue("dummy")).toList(), Type.DROP_COLUMN);
    }

    private AlterTableExecutor columns(@NotNull final List<SQLValue> values, @NotNull final Type type) {
        this.columns.clear();
        this.columns.addAll(values);
        this.type = type;
        return this;
    }

    @Override
    @NotNull
    public Void execute(@NotNull final AbstractConnector connector) {
        if (this.columns.isEmpty())
            return null;

        if (this.type == Type.ADD_COLUMN) {
            this.columns.forEach(value -> {
                if (SQLQueries.hasColumn(connector, this.getTable(), value.getColumn()))
                    return;

                String sql = "ALTER TABLE " + this.getTable() + " ADD " + value.getColumn().getName() + " "
                        + value.getColumn().formatType(this.databaseType);

                if (connector instanceof SQLiteConnector || value.getColumn().getType() != ColumnType.STRING) {
                    sql = sql + " DEFAULT '" + value.getValue() + "'";
                }

                SQLQueries.executeStatement(connector, sql);
            });
        } else if (this.type == Type.RENAME_COLUMN) {
            this.columns.forEach(value -> {
                if (!SQLQueries.hasColumn(connector, this.getTable(), value.getColumn()))
                    return;

                final String sql = "ALTER TABLE " + this.getTable() + " RENAME COLUMN " + value.getColumn().getName() + " TO " + value.getValue();
                SQLQueries.executeStatement(connector, sql);
            });
        } else if (this.type == Type.DROP_COLUMN) {
            this.columns.forEach(value -> {
                if (!SQLQueries.hasColumn(connector, this.getTable(), value.getColumn()))
                    return;

                final String sql = "ALTER TABLE " + this.getTable() + " DROP COLUMN " + value.getColumn().getName();
                SQLQueries.executeStatement(connector, sql);
            });
        }
        return null;
    }
}
