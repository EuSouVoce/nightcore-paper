package su.nightexpress.nightcore.util.wrapper;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.random.Rnd;

public final class UniInt {

    private final int minInclusive;
    private final int maxInclusive;

    private UniInt(final int var0, final int var1) {
        this.minInclusive = var0;
        this.maxInclusive = var1;
    }

    @NotNull
    public static UniInt of(final int var0, final int var1) { return new UniInt(var0, var1); }

    @NotNull
    public static UniInt read(@NotNull final FileConfig cfg, @NotNull final String path) {
        final int min = cfg.getInt(path + ".Min");
        final int max = cfg.getInt(path + ".Max");
        return UniInt.of(min, max);
    }

    public void write(@NotNull final FileConfig cfg, @NotNull final String path) {
        cfg.set(path + ".Min", this.getMinValue());
        cfg.set(path + ".Max", this.getMaxValue());
    }

    public int roll() { return Rnd.get(this.minInclusive, this.maxInclusive); }

    public int getMinValue() { return this.minInclusive; }

    public int getMaxValue() { return this.maxInclusive; }

    @Override
    public String toString() { return "[" + this.minInclusive + "-" + this.maxInclusive + "]"; }
}
