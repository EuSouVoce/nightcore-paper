package su.nightexpress.nightcore.command.experimental.argument;

public class ParsedArgument<T> {

    private final T result;

    public ParsedArgument(final T result) { this.result = result; }

    public T getResult() { return this.result; }
}
