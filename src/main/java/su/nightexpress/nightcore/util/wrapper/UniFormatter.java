package su.nightexpress.nightcore.util.wrapper;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.config.FileConfig;

public class UniFormatter {

    private final DecimalFormat formatter;

    private final String format;
    private final RoundingMode rounding;

    private UniFormatter(final String format, final RoundingMode rounding) {
        this.format = format;
        this.rounding = rounding;
        this.formatter = new DecimalFormat(format, new DecimalFormatSymbols(Locale.US));
        this.formatter.setRoundingMode(rounding);
    }

    @NotNull
    public static UniFormatter of(final String format, final RoundingMode rounding) { return new UniFormatter(format, rounding); }

    @NotNull
    public static UniFormatter read(@NotNull final FileConfig cfg, @NotNull final String path) {
        final String format = cfg.getString(path + ".Format_Pattern", "#,###.##");
        final RoundingMode rounding = RoundingMode.valueOf(cfg.getString(path + ".Rounding_Mode", "half_even").toUpperCase());
        return UniFormatter.of(format, rounding);
    }

    public void write(@NotNull final FileConfig cfg, @NotNull final String path) {
        cfg.set(path + ".Format", this.getFormat());
        cfg.set(path + ".Rounding", this.getRounding().name().toLowerCase());
    }

    public String getFormat() { return this.format; }

    public RoundingMode getRounding() { return this.rounding; }

    public String format(final double value) { return this.formatter.format(value); }
}
