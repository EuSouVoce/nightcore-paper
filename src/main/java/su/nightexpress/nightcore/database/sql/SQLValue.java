package su.nightexpress.nightcore.database.sql;

import org.jetbrains.annotations.NotNull;

public class SQLValue {

    private final SQLColumn column;
    private final String value;

    public SQLValue(@NotNull final SQLColumn column, @NotNull final String value) {
        this.column = column;
        this.value = value;
    }

    @NotNull
    public static SQLValue of(@NotNull final SQLColumn column, @NotNull final String value) { return new SQLValue(column, value); }

    @NotNull
    public SQLColumn getColumn() { return this.column; }

    @NotNull
    public String getValue() { return this.value; }
}
