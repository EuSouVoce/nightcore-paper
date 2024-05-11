package su.nightexpress.nightcore.language.entry;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class LangEnum<E extends Enum<E>> {

    private final String path;
    private final Class<E> clazz;
    private final Map<E, String> localeMap;

    public LangEnum(@NotNull final String path, @NotNull final Class<E> clazz) {
        this.path = path;
        this.clazz = clazz;
        this.localeMap = new HashMap<>();
    }

    @NotNull
    public static <E extends Enum<E>> LangEnum<E> of(@NotNull final String path, @NotNull final Class<E> clazz) { return new LangEnum<>(path, clazz); }

    public void load(@NotNull final NightCorePlugin plugin) {
        final FileConfig config = plugin.getLang();

        Stream.of(this.clazz.getEnumConstants()).forEach(con -> {
            final String text = ConfigValue.create(this.path + "." + con.name(), StringUtil.capitalizeUnderscored(con.name())).read(config);
            this.localeMap.put(con, text);
        });
    }

    @NotNull
    public String getLocalized(@NotNull final E con) { return this.localeMap.getOrDefault(con, con.name()); }
}
