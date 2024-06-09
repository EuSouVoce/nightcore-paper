package su.nightexpress.nightcore.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;

public class RankMap<T extends Number> {

    private final Mode mode;
    private final String permissionPrefix;
    private final T defaultValue;
    private final Map<String, T> values;

    public enum Mode {
        RANK, PERMISSION
    }

    public RankMap(@NotNull final Mode mode, @NotNull final String permissionPrefix, @NotNull final T defaultValue,
            @NotNull final Map<String, T> values) {
        this.mode = mode;
        this.permissionPrefix = permissionPrefix;
        this.defaultValue = defaultValue;
        this.values = new HashMap<>(values);
    }

    @NotNull
    public static RankMap<Integer> readInt(@NotNull final FileConfig cfg, @NotNull final String path, final int defaultValue) {
        return RankMap.read(cfg, path, Integer.class, defaultValue);
    }

    @NotNull
    public static RankMap<Double> readDouble(@NotNull final FileConfig cfg, @NotNull final String path, final double defaultValue) {
        return RankMap.read(cfg, path, Double.class, defaultValue);
    }

    @NotNull
    public static RankMap<Long> readLong(@NotNull final FileConfig cfg, @NotNull final String path, final long defaultValue) {
        return RankMap.read(cfg, path, Long.class, defaultValue);
    }

    @NotNull
    public static <T extends Number> RankMap<T> read(@NotNull final FileConfig cfg, @NotNull final String path,
            @NotNull final Class<T> clazz, @NotNull final T defaultValue) {
        final Map<String, T> oldMap = new HashMap<>();

        if (!cfg.contains(path + ".Mode")) {
            for (final String rank : cfg.getSection(path)) {
                T number;
                if (clazz == Double.class) {
                    number = clazz.cast(cfg.getDouble(path + "." + rank));
                } else
                    number = clazz.cast(cfg.getInt(path + "." + rank));

                oldMap.put(rank.toLowerCase(), number);
            }
            cfg.remove(path);
        }

        oldMap.forEach((rank, number) -> {
            if (rank.equalsIgnoreCase(Placeholders.DEFAULT)) {
                cfg.set(path + ".Default_Value", number);
            } else {
                cfg.set(path + ".Values." + rank, number);
            }
        });

        final Mode mode = ConfigValue.create(path + ".Mode", Mode.class, Mode.RANK,
                "Available values: " + StringUtil.inlineEnum(Mode.class, ", "),
                "=".repeat(20) + " " + Mode.RANK.name() + " MODE " + "=".repeat(20),
                "Get value by player's permission group. All keys in 'Values' list will represent permission group names.",
                "If player has none of specified groups, the 'Default_Value' setting will be used then", "  Values:",
                "    vip: 1 # -> Player must be in 'vip' permission group.", "    gold: 2 # -> Player must be in 'gold' permission group.",
                "    emerald: 3 # -> Player must be in 'emerald' permission group.", "",
                "=".repeat(20) + " " + Mode.PERMISSION.name() + " MODE " + "=".repeat(20),
                "Get value by player's permissions. All keys in 'Values' list will represent postfixes for the 'Permission_Prefix' setting (see below).",
                "If player has none of specified permissions, the 'Default_Value' setting will be used then",
                "  Permission_Prefix: 'example.prefix.'", "  Values:", "    vip: 1 # -> Player must have 'example.prefix.vip' permission.",
                "    gold: 2 # -> Player must have 'example.prefix.gold' permission.",
                "    emerald: 3 # -> Player must have 'example.prefix.emerald' permission.").read(cfg);

        final String permissionPrefix = ConfigValue.create(path + ".Permission_Prefix", "example.prefix.",
                "Sets permission prefix for the '" + Mode.PERMISSION.name() + "' mode.").read(cfg);

        T fallback;
        if (clazz == Double.class) {
            fallback = clazz.cast(ConfigValue.create(path + ".Default_Value", defaultValue.doubleValue()).read(cfg));
        } else
            fallback = clazz.cast(ConfigValue.create(path + ".Default_Value", defaultValue.intValue()).read(cfg));

        final Map<String, T> values = new HashMap<>();
        for (final String rank : cfg.getSection(path + ".Values")) {
            T number;
            if (clazz == Double.class) {
                number = clazz.cast(cfg.getDouble(path + ".Values." + rank));
            } else
                number = clazz.cast(cfg.getInt(path + ".Values." + rank));

            values.put(rank.toLowerCase(), number);
        }

        return new RankMap<>(mode, permissionPrefix, fallback, values);
    }

    public void write(@NotNull final FileConfig cfg, @NotNull final String path) {
        cfg.set(path + ".Mode", this.getMode().name());
        cfg.set(path + ".Permission_Prefix", this.getPermissionPrefix());
        cfg.set(path + ".Default_Value", this.getDefaultValue());
        this.values.forEach((rank, number) -> {
            cfg.set(path + ".Values." + rank, number);
        });
    }

    @NotNull
    public T getRankValue(@NotNull final Player player) {
        final String group = Players.getPermissionGroup(player);
        return this.values.getOrDefault(group, this.values.getOrDefault(Placeholders.DEFAULT, this.getDefaultValue()));
    }

    @NotNull
    public T getGreatestOrNegative(@NotNull final Player player) {
        final T best = this.getGreatest(player);
        final T lowest = this.getSmallest(player);

        return lowest.doubleValue() < 0D ? lowest : best;
    }

    @NotNull
    public T getGreatest(@NotNull final Player player) {
        if (this.getMode() == Mode.RANK) {
            return this.getRankValue(player);
        }
        return this.values.entrySet().stream().filter(entry -> player.hasPermission(this.getPermissionPrefix() + entry.getKey()))
                .map(Map.Entry::getValue).max(Comparator.comparingDouble(Number::doubleValue)).orElse(this.getDefaultValue());
    }

    @NotNull
    public T getSmallest(@NotNull final Player player) {
        if (this.getMode() == Mode.RANK) {
            return this.getRankValue(player);
        }
        return this.values.entrySet().stream().filter(entry -> player.hasPermission(this.getPermissionPrefix() + entry.getKey()))
                .map(Map.Entry::getValue).min(Comparator.comparingDouble(Number::doubleValue)).orElse(this.getDefaultValue());
    }

    @NotNull
    public Mode getMode() { return this.mode; }

    @Nullable
    public String getPermissionPrefix() { return this.permissionPrefix; }

    @NotNull
    public T getDefaultValue() { return this.defaultValue; }
}
