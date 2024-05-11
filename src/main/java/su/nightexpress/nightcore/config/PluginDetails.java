package su.nightexpress.nightcore.config;

import java.util.Locale;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.language.LangManager;
import su.nightexpress.nightcore.util.text.tag.Tags;

public class PluginDetails {

    private final String name;
    private final String prefix;
    private final String[] commandAliases;
    private final String language;

    private Class<?> configClass;
    private Class<?> langClass;
    private Class<?> permissionsClass;

    public PluginDetails(@NotNull final String name, @NotNull final String prefix, @NotNull final String[] commandAliases,
            @NotNull final String language) {
        this.name = name;
        this.prefix = prefix;
        this.commandAliases = commandAliases;
        this.language = language.toLowerCase();
    }

    @NotNull
    public static PluginDetails create(@NotNull final String name, @NotNull final String[] commandAliases) {
        final String prefix = Tags.LIGHT_YELLOW.enclose(Tags.BOLD.enclose(name)) + Tags.DARK_GRAY.enclose(" » ") + Tags.GRAY.getFullName();
        final String language = Locale.getDefault().getLanguage();

        return new PluginDetails(name, prefix, commandAliases, language);
    }

    @NotNull
    public static PluginDetails read(@NotNull final NightCorePlugin plugin) {
        final FileConfig config = plugin.getConfig();
        final PluginDetails defaults = plugin.getDetails();

        final String pluginName = ConfigValue
                .create("Plugin.Name", defaults.getName(), "Localized plugin name. It's used in messages and with internal placeholders.")
                .read(config);

        final String pluginPrefix = ConfigValue.create("Plugin.Prefix", defaults.getPrefix(), "Plugin prefix. Used in messages.")
                .read(config);

        final String[] commandAliases = ConfigValue.create("Plugin.Command_Aliases", defaults.getCommandAliases(),
                "Command names that will be registered as main plugin commands.",
                "Do not leave this empty. Split multiple names with a comma.").read(config);

        final String languageCode = ConfigValue
                .create("Plugin.Language", defaults.getLanguage(), "Sets the plugin language.",
                        "Basically it tells the plugin to use certain messages config from the '" + LangManager.DIR_LANG + "' sub-folder.",
                        "If specified language is not available, default one (English) will be used instead.", "[Default is System Locale]")
                .read(config);

        return new PluginDetails(pluginName, pluginPrefix, commandAliases, languageCode).setConfigClass(defaults.getConfigClass())
                .setLangClass(defaults.getLangClass()).setPermissionsClass(defaults.getPermissionsClass());
    }

    @NotNull
    public String getName() { return this.name; }

    @NotNull
    public String getPrefix() { return this.prefix; }

    @NotNull
    public String[] getCommandAliases() { return this.commandAliases; }

    @NotNull
    public String getLanguage() { return this.language; }

    @Nullable
    public Class<?> getConfigClass() { return this.configClass; }

    public PluginDetails setConfigClass(@Nullable final Class<?> configClass) {
        this.configClass = configClass;
        return this;
    }

    @Nullable
    public Class<?> getLangClass() { return this.langClass; }

    public PluginDetails setLangClass(@Nullable final Class<?> langClass) {
        this.langClass = langClass;
        return this;
    }

    @Nullable
    public Class<?> getPermissionsClass() { return this.permissionsClass; }

    public PluginDetails setPermissionsClass(@Nullable final Class<?> permissionsClass) {
        this.permissionsClass = permissionsClass;
        return this;
    }
}
