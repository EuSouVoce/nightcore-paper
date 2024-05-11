package su.nightexpress.nightcore.command.experimental.argument;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.flag.CommandFlag;

import java.util.HashMap;
import java.util.Map;

public class ParsedArguments {

    private final Map<String, ParsedArgument<?>> argumentMap;
    private final Map<String, ParsedArgument<?>> flags;

    public ParsedArguments() {
        this.argumentMap = new HashMap<>();
        this.flags = new HashMap<>();
    }

    public void add(@NotNull final CommandArgument<?> argument, @NotNull final ParsedArgument<?> parsedArgument) {
        this.argumentMap.put(argument.getName(), parsedArgument);
    }

    public void addFlag(@NotNull final CommandFlag flag, @NotNull final ParsedArgument<?> content) { this.flags.put(flag.getName(), content); }

    @NotNull
    public Map<String, ParsedArgument<?>> getArgumentMap() { return this.argumentMap; }

    @NotNull
    public Map<String, ParsedArgument<?>> getFlags() { return this.flags; }

    public int getIntArgument(@NotNull final String name, final int defaultValue) { return this.getArgument(name, Integer.class, defaultValue); }

    public int getIntArgument(@NotNull final String name) { return this.getArgument(name, Integer.class); }

    public double getDoubleArgument(@NotNull final String name, final double defaultValue) {
        return this.getArgument(name, Double.class, defaultValue);
    }

    public double getDoubleArgument(@NotNull final String name) { return this.getArgument(name, Double.class); }

    public boolean getBooleanArgument(@NotNull final String name, final boolean defaultValue) {
        return this.getArgument(name, Boolean.class, defaultValue);
    }

    public boolean getBooleanArgument(@NotNull final String name) { return this.getArgument(name, Boolean.class); }

    @NotNull
    public String getStringArgument(@NotNull final String name, @NotNull final String defaultValue) {
        return this.getArgument(name, String.class, defaultValue);
    }

    @NotNull
    public String getStringArgument(@NotNull final String name) { return this.getArgument(name, String.class); }

    @NotNull
    public Material getMaterialArgument(@NotNull final String name, @NotNull final Material defaultValue) {
        return this.getArgument(name, Material.class, defaultValue);
    }

    @NotNull
    public Material getMaterialArgument(@NotNull final String name) { return this.getArgument(name, Material.class); }

    @NotNull
    public World getWorldArgument(@NotNull final String name, @NotNull final World defaultValue) {
        return this.getArgument(name, World.class, defaultValue);
    }

    @NotNull
    public World getWorldArgument(@NotNull final String name) { return this.getArgument(name, World.class); }

    @NotNull
    public Enchantment getEnchantmentArgument(@NotNull final String name, @NotNull final Enchantment defaultValue) {
        return this.getArgument(name, Enchantment.class, defaultValue);
    }

    @NotNull
    public Enchantment getEnchantmentArgument(@NotNull final String name) { return this.getArgument(name, Enchantment.class); }

    @NotNull
    public Player getPlayerArgument(@NotNull final String name) { return this.getArgument(name, Player.class); }

    public boolean hasArgument(@NotNull final String name) { return this.argumentMap.containsKey(name); }

    @NotNull
    public <T> T getArgument(@NotNull final String name, @NotNull final Class<T> clazz, @NotNull final T defaultValue) {
        if (!this.hasArgument(name))
            return defaultValue;

        return this.getArgument(name, clazz);
    }

    @NotNull
    public <T> T getArgument(@NotNull final String name, @NotNull final Class<T> clazz) {
        final ParsedArgument<?> argument = this.argumentMap.get(name);
        if (argument == null) {
            throw new IllegalArgumentException("No such argument '" + name + "' exists on this command");
        }

        final Object result = argument.getResult();
        if (clazz.isAssignableFrom(result.getClass())) {
            return clazz.cast(result);
        } else {
            throw new IllegalArgumentException(
                    "Argument '" + name + "' is defined as " + result.getClass().getSimpleName() + ", not " + clazz);
        }
    }

    public boolean hasFlag(@NotNull final CommandFlag flag) { return this.hasFlag(flag.getName()); }

    public boolean hasFlag(@NotNull final String name) { return this.flags.containsKey(name); }

    public int getIntFlag(@NotNull final String name, final int defaultValue) { return this.getFlag(name, Integer.class, defaultValue); }

    public double getDoubleFlag(@NotNull final String name, final double defaultValue) { return this.getFlag(name, Double.class, defaultValue); }

    public boolean getBooleanFlag(@NotNull final String name, final boolean defaultValue) { return this.getFlag(name, Boolean.class, defaultValue); }

    @NotNull
    public String getStringFlag(@NotNull final String name, @NotNull final String defaultValue) {
        return this.getFlag(name, String.class, defaultValue);
    }

    @NotNull
    public <T> T getFlag(@NotNull final String name, @NotNull final Class<T> clazz, @NotNull final T defaultValue) {
        if (!this.hasFlag(name))
            return defaultValue;

        return this.getFlag(name, clazz);
    }

    @NotNull
    public <T> T getFlag(@NotNull final String name, @NotNull final Class<T> clazz) {
        final ParsedArgument<?> parsed = this.flags.get(name);
        if (parsed == null) {
            throw new IllegalArgumentException("No such flag '" + name + "' exists on this command");
        }

        final Object result = parsed.getResult();
        if (clazz.isAssignableFrom(result.getClass())) {
            return clazz.cast(result);
        } else {
            throw new IllegalArgumentException("Flag '" + name + "' is defined as " + result.getClass().getSimpleName() + ", not " + clazz);
        }
    }
}
