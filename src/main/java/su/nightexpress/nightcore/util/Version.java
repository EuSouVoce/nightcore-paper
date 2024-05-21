package su.nightexpress.nightcore.util;

import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public enum Version {

    V1_18_R2("1.18.2"), V1_19_R3("1.19.4"), V1_20_R1("1.20.1", true), V1_20_R2("1.20.2", true), V1_20_R3("1.20.4"), MC_1_20_6("1.20.6"),
    UNKNOWN("Unknown"),;

    public static final String CRAFTBUKKIT_PACKAGE = Bukkit.getServer().getClass().getPackage().getName();

    private static Version current;

    private final boolean deprecated;
    private final String localized;

    Version(@NotNull final String localized) { this(localized, false); }

    Version(@NotNull final String localized, final boolean deprecated) {
        this.localized = localized;
        this.deprecated = deprecated;
    }

    @NotNull
    @Deprecated
    public static String getProtocol() { return Bukkit.getServer().getBukkitVersion(); }

    @NotNull
    public static Version getCurrent() {
        if (Version.current == null) {
            final String protocol = Bukkit.getServer().getBukkitVersion();
            Version.current = Stream.of(Version.values()).filter(version -> protocol.startsWith(version.getLocalized())).findFirst()
                    .orElse(UNKNOWN);
        }
        return Version.current;
    }

    public boolean isDeprecated() { return this.deprecated; }

    @NotNull
    public String getLocalized() { return this.localized; }

    public boolean isLower(@NotNull final Version version) { return this.ordinal() < version.ordinal(); }

    public boolean isHigher(@NotNull final Version version) { return this.ordinal() > version.ordinal(); }

    public static boolean isAtLeast(@NotNull final Version version) {
        return version.isCurrent() || Version.getCurrent().isHigher(version);
    }

    public static boolean isAbove(@NotNull final Version version) { return Version.getCurrent().isHigher(version); }

    public static boolean isBehind(@NotNull final Version version) { return Version.getCurrent().isLower(version); }

    public boolean isCurrent() { return this == Version.getCurrent(); }
}
