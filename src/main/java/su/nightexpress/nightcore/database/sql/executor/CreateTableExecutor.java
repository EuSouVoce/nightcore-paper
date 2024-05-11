package su.nightexpress.nightcore.database.sql.executor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.database.AbstractConnector;
import su.nightexpress.nightcore.database.DatabaseType;
import su.nightexpress.nightcore.database.sql.SQLColumn;
import su.nightexpress.nightcore.database.sql.SQLExecutor;
import su.nightexpress.nightcore.database.sql.SQLQueries;
import su.nightexpress.nightcore.database.sql.column.ColumnFormer;

public final class CreateTableExecutor extends SQLExecutor<Void> {

    private final DatabaseType databaseType;
    private final List<SQLColumn> columns;

    private CreateTableExecutor(@NotNull final String table, @NotNull final DatabaseType databaseType) {
        super(table);
        this.databaseType = databaseType;
        this.columns = new ArrayList<>();
    }

    @NotNull
    public static CreateTableExecutor builder(@NotNull final String table, @NotNull final DatabaseType databaseType) {
        return new CreateTableExecutor(table, databaseType);
    }

    @NotNull
    public CreateTableExecutor columns(@NotNull final SQLColumn... columns) { return this.columns(Arrays.asList(columns)); }

    @NotNull
    public CreateTableExecutor columns(@NotNull final List<SQLColumn> columns) {
        this.columns.clear();
        this.columns.addAll(columns);
        return this;
    }

    @Override
    @NotNull
    public Void execute(@NotNull final AbstractConnector connector) {
        if (this.columns.isEmpty())
            return null;

        String id = "`id` " + ColumnFormer.INTEGER.build(this.databaseType, 11);
        if (this.databaseType == DatabaseType.SQLITE) {
            id += " PRIMARY KEY AUTOINCREMENT";
        } else {
            id += " PRIMARY KEY AUTO_INCREMENT";
        }

        final String columns = id + "," + this.columns.stream()
                .map(column -> column.getNameEscaped() + " " + column.formatType(this.databaseType)).collect(Collectors.joining(", "));

        final String sql = "CREATE TABLE IF NOT EXISTS " + this.getTable() + "(" + columns + ");";

        SQLQueries.executeStatement(connector, sql);
        return null;
    }
}
