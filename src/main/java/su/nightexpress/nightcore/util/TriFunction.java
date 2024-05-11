package su.nightexpress.nightcore.util;

import java.util.Objects;
import java.util.function.Function;

public interface TriFunction<T, U, V, R> {

    R apply(T t, U u, V v);

    default <K> TriFunction<T, U, V, K> andThen(final Function<? super R, ? extends K> after) {
        Objects.requireNonNull(after);
        return (final T t, final U u, final V v) -> after.apply(this.apply(t, u, v));
    }
}
