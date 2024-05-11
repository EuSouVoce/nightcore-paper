package su.nightexpress.nightcore.language;

import java.io.File;

import org.jetbrains.annotations.NotNull;

import su.nightexpress.nightcore.NightCorePlugin;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.language.entry.LangEntry;
import su.nightexpress.nightcore.language.entry.LangEnum;
import su.nightexpress.nightcore.manager.SimpleManager;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.Reflex;
import su.nightexpress.nightcore.util.StringUtil;

public class LangManager extends SimpleManager<NightCorePlugin> {

    public static final String DEFAULT_LANGUAGE = "en";
    public static final String DIR_LANG = "/lang/";

    private FileConfig config;

    public LangManager(@NotNull final NightCorePlugin plugin) { super(plugin); }

    @Override
    protected void onLoad() {
        this.plugin.extractResources(LangManager.DIR_LANG);
        final String langCode = this.plugin.getLanguage();
        final String realCode = this.validateConfig(langCode);
        this.config = FileConfig.loadOrExtract(this.plugin, LangManager.DIR_LANG, this.getFileName(realCode));
    }

    @Override
    protected void onShutdown() {

    }

    private boolean isPacked(@NotNull String filePath) {
        if (!filePath.startsWith("/")) {
            filePath = "/" + filePath;
        }

        return this.plugin.getClass().getResourceAsStream(filePath) != null;
    }

    @NotNull
    private String validateConfig(@NotNull final String langCode) {
        final String fileName = this.getFileName(langCode);

        final File file = new File(this.plugin.getDataFolder() + LangManager.DIR_LANG, fileName);
        if (!file.exists() && !LangManager.isDefault(langCode)) {
            if (this.isPacked(LangManager.DIR_LANG + fileName)) {
                return langCode;
            }

            this.plugin.warn(
                    "Locale file for '" + langCode + "' language not found. Using default '" + LangManager.DEFAULT_LANGUAGE + "' locale.");
            return LangManager.DEFAULT_LANGUAGE;
        }
        return langCode;
    }

    public void loadEntries(@NotNull final Class<?> clazz) {
        Reflex.getFields(clazz, LangEntry.class).forEach(entry -> {
            entry.load(this.plugin);
        });
        Reflex.getFields(clazz, LangEnum.class).forEach(langEnum -> {
            langEnum.load(this.plugin);
        });
    }

    @Deprecated
    public void loadEnum(@NotNull final Class<? extends Enum<?>> clazz) {
        for (final Object eName : clazz.getEnumConstants()) {
            final String name = eName.toString();
            final String path = clazz.getSimpleName() + "." + name;
            final String val = StringUtil.capitalizeUnderscored(name);
            this.getConfig().addMissing(path, val);
        }
    }

    @NotNull
    @Deprecated
    public String getEnum(@NotNull final Enum<?> entry) {
        final String path = entry.getDeclaringClass().getSimpleName() + "." + entry.name();
        final String locEnum = this.config.getString(path);
        if (locEnum == null && !this.plugin.isEngine()) {
            return Plugins.CORE.getLangManager().getEnum(entry);
        }
        return locEnum == null ? StringUtil.capitalizeFully(entry.name()) : locEnum;
    }

    public static boolean isDefault(@NotNull final String langCode) { return langCode.equalsIgnoreCase(LangManager.DEFAULT_LANGUAGE); }

    @NotNull
    public String getFileName(@NotNull final String langCode) { return "messages_" + langCode + ".yml"; }

    @NotNull
    public FileConfig getConfig() { return this.config; }
}
