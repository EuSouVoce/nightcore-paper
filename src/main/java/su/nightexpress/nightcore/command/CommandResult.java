package su.nightexpress.nightcore.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.NumberUtil;

import java.util.HashMap;
import java.util.Map;

public class CommandResult {

    private final String label;
    private final String[] args;
    private final Map<CommandFlag<?>, String> flags;

    public CommandResult(@NotNull final String label, final String[] args, @NotNull final Map<CommandFlag<?>, StringBuilder> flags) {
        this.label = label;
        this.flags = new HashMap<>();
        this.args = args;

        flags.forEach((flag, content) -> this.flags.put(flag, content.toString()));
    }

    public int length() { return this.args.length; }

    @NotNull
    public String getArg(final int index) { return this.getArgs()[index]; }

    @NotNull
    public String getArg(final int index, @NotNull final String def) {
        if (index >= this.length())
            return def;

        return this.getArgs()[index];
    }

    public int getInt(final int index, final int def) { return NumberUtil.getAnyInteger(this.getArg(index, ""), def); }

    public double getDouble(final int index, final double def) { return NumberUtil.getAnyDouble(this.getArg(index, ""), def); }

    public boolean hasFlag(@NotNull final CommandFlag<?> flag) { return this.getFlags().containsKey(flag); }

    @Nullable
    public <T> T getFlag(@NotNull final CommandFlag<T> flag) {
        final String value = this.getFlags().get(flag);
        if (value == null)
            return null;

        return flag.getParser().apply(value);
    }

    @NotNull
    public <T> T getFlag(@NotNull final CommandFlag<T> flag, @NotNull final T def) {
        final T value = this.getFlag(flag);
        return value == null ? def : value;
    }

    @NotNull
    public String getLabel() { return this.label; }

    public String[] getArgs() { return this.args; }

    @NotNull
    public Map<CommandFlag<?>, String> getFlags() { return this.flags; }
}
