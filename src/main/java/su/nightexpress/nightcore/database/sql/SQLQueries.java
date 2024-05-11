package su.nightexpress.nightcore.database.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.database.AbstractConnector;

public class SQLQueries {

    public static boolean hasTable(@NotNull final AbstractConnector connector, @NotNull final String table) {
        try (Connection connection = connector.getConnection()) {

            boolean has;
            final DatabaseMetaData metaData = connection.getMetaData();
            final ResultSet tables = metaData.getTables(null, null, table, null);
            has = tables.next();
            tables.close();
            return has;
        } catch (final SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public static boolean hasColumn(@NotNull final AbstractConnector connector, @NotNull final String table,
            @NotNull final SQLColumn column) {
        final String sql = "SELECT * FROM " + table;
        final String columnName = column.getName();
        try (Connection connection = connector.getConnection(); Statement statement = connection.createStatement()) {

            final ResultSet resultSet = statement.executeQuery(sql);
            final ResultSetMetaData metaData = resultSet.getMetaData();
            final int columns = metaData.getColumnCount();
            for (int index = 1; index <= columns; index++) {
                if (columnName.equals(metaData.getColumnName(index))) {
                    return true;
                }
            }
            return false;
        } catch (final SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public static void executeStatement(@NotNull final AbstractConnector connector, @NotNull final String sql) {
        SQLQueries.executeStatement(connector, sql, Collections.emptySet());
    }

    public static void executeStatement(@NotNull final AbstractConnector connector, @NotNull final String sql,
            @NotNull final Collection<String> values1) {
        SQLQueries.executeStatement(connector, sql, values1, Collections.emptySet());
    }

    public static void executeStatement(@NotNull final AbstractConnector connector, @NotNull final String sql,
            @NotNull final Collection<String> values1, @NotNull final Collection<String> values2) {

        try (Connection connection = connector.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            int count = 1;
            for (final String columnName : values1) {
                statement.setString(count++, columnName);
            }
            for (final String columnValue : values2) {
                statement.setString(count++, columnValue);
            }

            statement.executeUpdate();
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
    }

    @NotNull
    public static <T> List<@NotNull T> executeQuery(@NotNull final AbstractConnector connector, @NotNull final String sql,
            @NotNull final Collection<String> values, @NotNull final Function<ResultSet, T> dataFunction, final int amount) {

        final List<T> list = new ArrayList<>();
        try (Connection connection = connector.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            int count = 1;
            for (final String wValue : values) {
                statement.setString(count++, wValue);
            }

            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next() && (amount < 0 || list.size() < amount)) {
                list.add(dataFunction.apply(resultSet));
            }
            resultSet.close();
        } catch (final SQLException exception) {
            exception.printStackTrace();
        }
        list.removeIf(Objects::isNull);

        return list;
    }
}
