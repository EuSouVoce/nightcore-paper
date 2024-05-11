package su.nightexpress.nightcore.util;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

public class Pair<F, S> {

    private final F first;
    private final S second;

    public Pair(@NotNull final F first, @NotNull final S second) {
        this.first = first;
        this.second = second;
    }

    @NotNull
    public F getFirst() { return this.first; }

    @NotNull
    public S getSecond() { return this.second; }

    @NotNull
    public Pair<S, F> swap() { return Pair.of(this.second, this.first); }

    @Override
    public String toString() { return "Pair{" + "first=" + this.first + ", second=" + this.second + '}'; }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof final Pair<?, ?> other) {
            return Objects.equals(this.first, other.first) && Objects.equals(this.second, other.second);
        }
        return false;
    }

    @NotNull
    public static <F, S> Pair<F, S> of(@NotNull final F first, @NotNull final S second) { return new Pair<>(first, second); }
}
