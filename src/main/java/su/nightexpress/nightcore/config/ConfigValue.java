package su.nightexpress.nightcore.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nightexpress.nightcore.util.TriFunction;
import su.nightexpress.nightcore.util.wrapper.UniFormatter;
import su.nightexpress.nightcore.util.wrapper.UniParticle;
import su.nightexpress.nightcore.util.wrapper.UniSound;

public class ConfigValue<T> {

    private final String path;
    private final T defaultValue;
    private final String[] description;
    private final Reader<T> reader;
    private final Writer<T> writer;

    private T value;

    public ConfigValue(@NotNull final String path, @NotNull final Reader<T> reader, @NotNull final Writer<T> writer,
            @NotNull final T defaultValue, @Nullable final String... description) {
        this.path = path;
        this.description = description == null ? new String[0] : description;
        this.reader = reader;
        this.writer = writer;
        this.defaultValue = defaultValue;
    }

    @NotNull
    private static <T> ConfigValue<T> create(@NotNull final String path, @NotNull final Reader<T> reader, @NotNull final Writer<T> writer,
            @NotNull final T defaultValue, @Nullable final String... description) {
        return new ConfigValue<>(path, reader, writer, defaultValue, description);
    }

    @NotNull
    public static <T> ConfigValue<T> create(@NotNull final String path, @NotNull final Reader<T> reader, @NotNull final Writer<T> writer,

            @NotNull final Supplier<T> defaultValue, @Nullable final String... description) {
        return ConfigValue.create(path, reader, writer, defaultValue.get(), description);
    }

    @NotNull
    public static <T> ConfigValue<T> create(@NotNull final String path, @NotNull final Reader<T> reader, @NotNull final T defaultValue,
            @Nullable final String... description) {
        return ConfigValue.create(path, reader, FileConfig::set, defaultValue, description);
    }

    @NotNull
    public static <T> ConfigValue<T> create(@NotNull final String path, @NotNull final Reader<T> reader,
            @NotNull final Supplier<T> defaultValue, @Nullable final String... description) {
        return ConfigValue.create(path, reader, FileConfig::set, defaultValue, description);
    }

    @NotNull
    public static ConfigValue<Boolean> create(@NotNull final String path, final boolean defaultValue,
            @Nullable final String... description) {
        return ConfigValue.create(path, FileConfig::getBoolean, defaultValue, description);
    }

    @NotNull
    public static ConfigValue<Integer> create(@NotNull final String path, final int defaultValue, @Nullable final String... description) {
        return ConfigValue.create(path, FileConfig::getInt, defaultValue, description);
    }

    @NotNull
    public static ConfigValue<int[]> create(@NotNull final String path, final int[] defaultValue, @Nullable final String... description) {
        return ConfigValue.create(path, FileConfig::getIntArray, FileConfig::setIntArray, defaultValue, description);
    }

    @NotNull
    public static ConfigValue<Double> create(@NotNull final String path, final double defaultValue, @Nullable final String... description) {
        return ConfigValue.create(path, FileConfig::getDouble, defaultValue, description);
    }

    @NotNull
    public static ConfigValue<Long> create(@NotNull final String path, final long defaultValue, @Nullable final String... description) {
        return ConfigValue.create(path, FileConfig::getLong, defaultValue, description);
    }

    @NotNull
    public static ConfigValue<String> create(@NotNull final String path, @NotNull final String defaultValue,
            @Nullable final String... description) {
        return ConfigValue.create(path, FileConfig::getString, defaultValue, description);
    }

    @NotNull
    public static ConfigValue<String[]> create(@NotNull final String path, @NotNull final String[] defaultValue,
            @Nullable final String... description) {
        return ConfigValue.create(path, FileConfig::getStringArray, FileConfig::setStringArray, defaultValue, description);
    }

    @NotNull
    public static ConfigValue<List<String>> create(@NotNull final String path, @NotNull final List<String> defaultValue,
            @Nullable final String... description) {
        return ConfigValue.create(path, (cfg, path1, def) -> cfg.getStringList(path1), defaultValue, description);
    }

    @NotNull
    public static ConfigValue<Set<String>> create(@NotNull final String path, @NotNull final Set<String> defaultValue,
            @Nullable final String... description) {
        return ConfigValue.create(path, (cfg, path1, def) -> cfg.getStringSet(path1), defaultValue, description);
    }

    @NotNull
    public static ConfigValue<ItemStack> create(@NotNull final String path, @NotNull final ItemStack defaultValue,
            @Nullable final String... description) {
        return ConfigValue.create(path, FileConfig::getItem, FileConfig::setItem, defaultValue, description);
    }

    @NotNull
    public static ConfigValue<UniSound> create(@NotNull final String path, @NotNull final UniSound defaultValue,
            @Nullable final String... description) {
        final Reader<UniSound> reader = (cfg, path1, def) -> UniSound.read(cfg, path1);
        final Writer<UniSound> writer = (cfg, path1, obj) -> obj.write(cfg, path1);

        return ConfigValue.create(path, reader, writer, defaultValue, description);
    }

    @NotNull
    public static ConfigValue<UniParticle> create(@NotNull final String path, @NotNull final UniParticle defaultValue,
            @Nullable final String... description) {
        final Reader<UniParticle> reader = (cfg, path1, def) -> UniParticle.read(cfg, path1);
        final Writer<UniParticle> writer = (cfg, path1, obj) -> obj.write(cfg, path1);

        return ConfigValue.create(path, reader, writer, defaultValue, description);
    }

    @NotNull
    public static ConfigValue<UniFormatter> create(@NotNull final String path, @NotNull final UniFormatter defaultValue,
            @Nullable final String... description) {
        final Reader<UniFormatter> reader = (cfg, path1, def) -> UniFormatter.read(cfg, path1);
        final Writer<UniFormatter> writer = (cfg, path1, obj) -> obj.write(cfg, path1);

        return ConfigValue.create(path, reader, writer, defaultValue, description);
    }

    @NotNull
    public static <E extends Enum<E>> ConfigValue<E> create(@NotNull final String path, @NotNull final Class<E> clazz,
            @NotNull final E defaultValue, @Nullable final String... description) {
        final Reader<E> reader = (cfg, path1, def) -> cfg.getEnum(path1, clazz, def);
        final Writer<E> writer = (cfg, path1, obj) -> cfg.set(path1, obj.name());

        return ConfigValue.create(path, reader, writer, defaultValue, description);
    }

    @NotNull
    public static <V> ConfigValue<Set<V>> forSet(@NotNull final String path, @NotNull final Function<String, V> reader,
            @NotNull final Writer<Set<V>> writer, @NotNull final Supplier<Set<V>> defaultValue, @Nullable final String... description) {
        return ConfigValue.forSet(path, reader, writer, defaultValue.get(), description);
    }

    @NotNull
    public static <V> ConfigValue<Set<V>> forSet(@NotNull final String path, @NotNull final Function<String, V> valFun,
            @NotNull final Writer<Set<V>> writer, @NotNull final Set<V> defaultValue, @Nullable final String... description) {

        final Reader<Set<V>> reader = (cfg, path1, def) -> cfg.getStringSet(path1).stream().map(valFun).filter(Objects::nonNull)
                .collect(Collectors.toCollection(HashSet::new));

        return ConfigValue.create(path, reader, writer, defaultValue, description);
    }

    @NotNull
    public static <K, V> ConfigValue<Map<K, V>> forMap(@NotNull final String path, @NotNull final Function<String, K> keyFun,
            @NotNull final TriFunction<FileConfig, String, String, V> valFun, @NotNull final Writer<Map<K, V>> writer,
            @NotNull final Supplier<Map<K, V>> defaultValue, @Nullable final String... description) {
        return ConfigValue.forMap(path, keyFun, valFun, writer, defaultValue.get(), description);
    }

    @NotNull
    public static <K, V> ConfigValue<Map<K, V>> forMap(@NotNull final String path, @NotNull final Function<String, K> keyFun,
            @NotNull final TriFunction<FileConfig, String, String, V> valFun, @NotNull final Writer<Map<K, V>> writer,
            @NotNull final Map<K, V> defaultValue, @Nullable final String... description) {
        return ConfigValue.forMap(path, keyFun, valFun, HashMap::new, writer, defaultValue, description);
    }

    @NotNull
    public static <K, V> ConfigValue<TreeMap<K, V>> forTreeMap(@NotNull final String path, @NotNull final Function<String, K> keyFun,
            @NotNull final TriFunction<FileConfig, String, String, V> valFun, @NotNull final Writer<TreeMap<K, V>> writer,
            @NotNull final Supplier<TreeMap<K, V>> defaultValue, @Nullable final String... description) {
        return ConfigValue.forTreeMap(path, keyFun, valFun, writer, defaultValue.get(), description);
    }

    @NotNull
    public static <K, V> ConfigValue<TreeMap<K, V>> forTreeMap(@NotNull final String path, @NotNull final Function<String, K> keyFun,
            @NotNull final TriFunction<FileConfig, String, String, V> valFun, @NotNull final Writer<TreeMap<K, V>> writer,
            @NotNull final TreeMap<K, V> defaultValue, @Nullable final String... description) {
        return ConfigValue.forMap(path, keyFun, valFun, TreeMap::new, writer, defaultValue, description);
    }

    @NotNull
    public static <K, V, M extends Map<K, V>> ConfigValue<M> forMap(@NotNull final String path, @NotNull final Function<String, K> keyFun,
            @NotNull final TriFunction<FileConfig, String, String, V> valFun, @NotNull final Supplier<M> mapSupplier,
            @NotNull final Writer<M> writer, @NotNull final M defaultValue, @Nullable final String... description) {
        final Reader<M> reader = (cfg, path1, def) -> {
            final M map = mapSupplier.get();
            for (final String id : cfg.getSection(path1)) {
                final K key = keyFun.apply(id);
                final V val = valFun.apply(cfg, path1, id);
                if (key == null || val == null)
                    continue;

                map.put(key, val);
            }
            return map;
        };

        return ConfigValue.create(path, reader, writer, defaultValue, description);
    }

    @NotNull
    public static <V> ConfigValue<Map<String, V>> forMap(@NotNull final String path,
            @NotNull final TriFunction<FileConfig, String, String, V> function, @NotNull final Writer<Map<String, V>> writer,
            @NotNull final Supplier<Map<String, V>> defaultValue, @Nullable final String... description) {
        return ConfigValue.forMap(path, String::toLowerCase, function, writer, defaultValue.get(), description);
    }

    @NotNull
    public static <V> ConfigValue<Map<String, V>> forMap(@NotNull final String path,
            @NotNull final TriFunction<FileConfig, String, String, V> function, @NotNull final Writer<Map<String, V>> writer,
            @NotNull final Map<String, V> defaultValue, @Nullable final String... description) {
        return ConfigValue.forMap(path, String::toLowerCase, function, writer, defaultValue, description);
    }

    @NotNull
    public T read(@NotNull final FileConfig cfg) {
        if (!cfg.contains(this.getPath())) {
            this.write(cfg);
        }
        if (this.getDescription().length > 0 && !this.getDescription()[0].isEmpty()) {
            cfg.setComments(this.getPath(), this.getDescription());
        }
        return (this.value = this.reader.read(cfg, this.getPath(), this.getDefaultValue()));
    }

    public void write(@NotNull final FileConfig cfg) { this.getWriter().write(cfg, this.getPath(), this.get()); }

    public boolean remove(@NotNull final FileConfig cfg) { return cfg.remove(this.getPath()); }

    @NotNull
    public T get() { return this.value == null ? this.getDefaultValue() : this.value; }

    public void set(@NotNull final T value) { this.value = value; }

    @NotNull
    public String getPath() { return this.path; }

    @NotNull
    public String[] getDescription() { return this.description; }

    @NotNull
    public T getDefaultValue() { return this.defaultValue; }

    @NotNull
    public Reader<T> getReader() { return this.reader; }

    @NotNull
    public Writer<T> getWriter() { return this.writer; }

    public interface Reader<T> {

        @NotNull
        T read(@NotNull FileConfig cfg, @NotNull String path, @NotNull T def);
    }

    public interface Writer<T> {

        void write(@NotNull FileConfig cfg, @NotNull String path, @NotNull T obj);
    }
}
