package su.nightexpress.nightcore.dialog;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nightexpress.nightcore.util.Colorizer;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.wrapper.UniDouble;
import su.nightexpress.nightcore.util.wrapper.UniInt;

@SuppressWarnings("deprecation")
public class WrappedInput {

    private final String text;
    private final String textRaw;
    private final String textColored;

    public WrappedInput(@NotNull final AsyncPlayerChatEvent event) { this(event.getMessage()); }

    public WrappedInput(@NotNull final String text) {
        this.text = text;
        this.textRaw = Colorizer.restrip(text); // TODO Modern formation, but keep colorizer to support legacy codes for other
                                                // chat format plugins
        this.textColored = Colorizer.apply(text);
    }

    public int asInt() { return this.asInt(0); }

    public int asInt(final int def) { return NumberUtil.getInteger(this.getTextRaw(), def); }

    public int asAnyInt(final int def) { return NumberUtil.getAnyInteger(this.getTextRaw(), def); }

    public double asDouble() { return this.asDouble(0D); }

    public double asDouble(final double def) { return NumberUtil.getDouble(this.getTextRaw(), def); }

    @NotNull
    public UniDouble asUniDouble() { return this.asUniDouble(0, 0); }

    @NotNull
    public UniDouble asUniDouble(final double min, final double max) {
        final String[] split = this.getTextRaw().split(" ");
        return UniDouble.of(NumberUtil.getDouble(split[0], min), NumberUtil.getDouble(split.length >= 2 ? split[1] : split[0], max));
    }

    @NotNull
    public UniInt asUniInt() { return this.asUniDouble().asInt(); }

    public double asAnyDouble(final double def) { return NumberUtil.getAnyDouble(this.getTextRaw(), def); }

    @Nullable
    public <E extends Enum<E>> E asEnum(@NotNull final Class<E> clazz) { return StringUtil.getEnum(this.getTextRaw(), clazz).orElse(null); }

    @NotNull
    public <E extends Enum<E>> E asEnum(@NotNull final Class<E> clazz, @NotNull final E def) {
        return StringUtil.getEnum(this.getTextRaw(), clazz).orElse(def);
    }

    @NotNull
    public String getText() { return this.text; }

    @NotNull
    public String getTextRaw() { return this.textRaw; }

    @NotNull
    public String getTextColored() { return this.textColored; }
}
