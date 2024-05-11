package su.nightexpress.nightcore.command;

import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.util.Colorizer;
import su.nightexpress.nightcore.util.NumberUtil;

public class CommandFlag<T> {

    public static final char PREFIX = '-';

    private final String name;
    private final Function<String, T> parser;

    public CommandFlag(@NotNull final String name, @NotNull final Function<String, T> parser) {
        this.name = name;
        this.parser = parser;
    }

    @NotNull
    public static CommandFlag<World> worldFlag(@NotNull final String name) { return new CommandFlag<>(name, Bukkit::getWorld); }

    @NotNull
    public static CommandFlag<String> stringFlag(@NotNull final String name) { return new CommandFlag<>(name, Function.identity()); }

    @NotNull
    public static CommandFlag<String> textFlag(@NotNull final String name) { return new CommandFlag<>(name, Colorizer::apply); }

    @NotNull
    public static CommandFlag<Integer> intFlag(@NotNull final String name) {
        return new CommandFlag<>(name, str -> NumberUtil.getAnyInteger(str, 0));
    }

    @NotNull
    public static CommandFlag<Double> doubleFlag(@NotNull final String name) {
        return new CommandFlag<>(name, str -> NumberUtil.getAnyDouble(str, 0D));
    }

    @NotNull
    public static CommandFlag<Boolean> booleanFlag(@NotNull final String name) { return new CommandFlag<>(name, str -> true); }

    @NotNull
    public String getName() { return this.name; }

    @NotNull
    public String getNamePrefixed() { return CommandFlag.PREFIX + this.getName(); }

    @NotNull
    public Function<String, T> getParser() { return this.parser; }

    @Override
    public String toString() { return "CommandFlag{" + "name='" + this.name + '\'' + '}'; }
}
