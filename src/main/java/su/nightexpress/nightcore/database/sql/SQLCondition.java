package su.nightexpress.nightcore.database.sql;

import org.jetbrains.annotations.NotNull;

public class SQLCondition {

    private final SQLValue value;
    private final Type type;

    public SQLCondition(@NotNull final SQLValue value, @NotNull final Type type) {
        this.value = value;
        this.type = type;
    }

    @NotNull
    public static SQLCondition of(@NotNull final SQLValue value, @NotNull final Type type) { return new SQLCondition(value, type); }

    @NotNull
    public static SQLCondition equal(@NotNull final SQLValue value) { return SQLCondition.of(value, Type.EQUAL); }

    @NotNull
    public static SQLCondition not(@NotNull final SQLValue value) { return SQLCondition.of(value, Type.NOT_EQUAL); }

    @NotNull
    public static SQLCondition smaller(@NotNull final SQLValue value) { return SQLCondition.of(value, Type.SMALLER); }

    @NotNull
    public static SQLCondition greater(@NotNull final SQLValue value) { return SQLCondition.of(value, Type.GREATER); }

    @NotNull
    public Type getType() { return this.type; }

    @NotNull
    public SQLValue getValue() { return this.value; }

    @NotNull
    public SQLColumn getColumn() { return this.getValue().getColumn(); }

    public enum Type {
        GREATER(">"), SMALLER("<"), EQUAL("="), NOT_EQUAL("!=");

        private final String operator;

        Type(@NotNull final String operator) { this.operator = operator; }

        @NotNull
        public String getOperator() { return this.operator; }
    }
}
