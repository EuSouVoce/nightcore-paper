package su.nightexpress.nightcore.database.sql.executor;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.database.AbstractConnector;
import su.nightexpress.nightcore.database.sql.SQLColumn;
import su.nightexpress.nightcore.database.sql.SQLCondition;
import su.nightexpress.nightcore.database.sql.SQLExecutor;
import su.nightexpress.nightcore.database.sql.SQLQueries;
import su.nightexpress.nightcore.database.sql.SQLValue;

public final class SelectQueryExecutor<T> extends SQLExecutor<List<T>> {

    private final List<SQLColumn> columns;
    private final List<SQLCondition> wheres;
    private final Function<ResultSet, T> dataFunction;
    private int amount;

    private SelectQueryExecutor(@NotNull final String table, @NotNull final Function<ResultSet, T> dataFunction) {
        super(table);
        this.columns = new ArrayList<>();
        this.wheres = new ArrayList<>();
        this.dataFunction = dataFunction;
        this.amount = -1;
    }

    @NotNull
    public static <T> SelectQueryExecutor<T> builder(@NotNull final String table, @NotNull final Function<ResultSet, T> dataFunction) {
        return new SelectQueryExecutor<>(table, dataFunction);
    }

    /*
     * @NotNull public SelectQueryExecutor<T> all() { return this.columns(new
     * SQLColumn("*", ColumnType.STRING, -1)); }
     */

    @NotNull
    public SelectQueryExecutor<T> columns(@NotNull final SQLColumn... columns) { return this.columns(Arrays.asList(columns)); }

    @NotNull
    public SelectQueryExecutor<T> columns(@NotNull final List<SQLColumn> columns) {
        // if (columns.isEmpty()) return this.all();
        this.columns.clear();
        this.columns.addAll(columns);
        return this;
    }

    @NotNull
    public SelectQueryExecutor<T> where(@NotNull final SQLCondition... wheres) { return this.where(Arrays.asList(wheres)); }

    @NotNull
    public SelectQueryExecutor<T> where(@NotNull final List<SQLCondition> wheres) {
        this.wheres.clear();
        this.wheres.addAll(wheres);
        return this;
    }

    @NotNull
    public SelectQueryExecutor<T> amount(final int amount) {
        this.amount = amount;
        return this;
    }

    @Override
    @NotNull
    public List<T> execute(@NotNull final AbstractConnector connector) {
        String columns = this.columns.stream().map(SQLColumn::getNameEscaped).collect(Collectors.joining(","));
        if (columns.isEmpty())
            columns = "*";

        final String wheres = this.wheres.stream()
                .map(where -> where.getColumn().getNameEscaped() + " " + where.getType().getOperator() + " ?")
                .collect(Collectors.joining(" AND "));

        final String sql = "SELECT " + columns + " FROM " + this.getTable() + (wheres.isEmpty() ? "" : " WHERE " + wheres);

        final List<String> values = this.wheres.stream().map(SQLCondition::getValue).map(SQLValue::getValue).toList();

        return SQLQueries.executeQuery(connector, sql, values, this.dataFunction, this.amount);
    }
}
