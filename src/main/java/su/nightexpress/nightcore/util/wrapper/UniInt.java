package su.nightexpress.nightcore.util.wrapper;

import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.random.Rnd;

public final class UniInt {

    private final int minInclusive;
    private final int maxInclusive;

    private UniInt(final int min, final int max) {
        this.minInclusive = min;
        this.maxInclusive = max;
    }

    @NotNull
    public static UniInt of(final int var0, final int var1) { return new UniInt(var0, var1); }

    @NotNull
    public static UniInt read(@NotNull final FileConfig config, @NotNull final String path) {
        final int min = config.getInt(path + ".Min");
        final int max = config.getInt(path + ".Max");
        return UniInt.of(min, max);
    }

    public void write(@NotNull final FileConfig config, @NotNull final String path) {
        config.set(path + ".Min", this.getMinValue());
        config.set(path + ".Max", this.getMaxValue());
    }

    public int roll() { return Rnd.get(this.minInclusive, this.maxInclusive); }

    public int getMinValue() { return this.minInclusive; }

    public int getMaxValue() { return this.maxInclusive; }

    @Override
    public String toString() { return "[" + this.minInclusive + "-" + this.maxInclusive + "]"; }
}
